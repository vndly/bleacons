package com.mauriciotogneri.bleacons;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.mauriciotogneri.bleacons.BeaconService.BeaconBinder;
import com.mauriciotogneri.bleacons.modes.ReadingMode;

public class BeaconManager
{
    private final Context context;
    private final BeaconManagerObserver beaconManagerObserver;
    private BeaconService beaconService;
    private volatile boolean isConnected = false;
    private final ServiceConnection serviceConnection;

    public BeaconManager(Context context, BeaconManagerObserver beaconManagerObserver) throws UnsupportedBluetoothLeException
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            throw new UnsupportedBluetoothLeException(Build.VERSION.SDK_INT);
        }

        this.context = context;
        this.beaconManagerObserver = beaconManagerObserver;

        this.serviceConnection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service)
            {
                onConnected(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name)
            {
                onDisconnected();
            }
        };
    }

    public boolean connect()
    {
        Intent intent = new Intent(context, BeaconService.class);

        return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnect()
    {
        pause();

        context.unbindService(serviceConnection);
    }

    public boolean resume()
    {
        if ((beaconService != null) && (isConnected()))
        {
            beaconService.resume();

            return true;
        }

        return false;
    }

    public boolean pause()
    {
        if ((beaconService != null) && (isConnected()))
        {
            beaconService.pause();

            return true;
        }

        return false;
    }

    public boolean setMode(ReadingMode readingMode)
    {
        if (isConnected())
        {
            beaconService.setMode(readingMode);

            return true;
        }

        return false;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    private void onConnected(IBinder service)
    {
        isConnected = true;

        BeaconBinder binder = (BeaconBinder) service;
        beaconService = binder.getService();

        beaconManagerObserver.onConnected();
    }

    private void onDisconnected()
    {
        isConnected = false;

        beaconManagerObserver.onDisconnected();
    }

    public interface BeaconManagerObserver
    {
        void onConnected();

        void onDisconnected();
    }
}