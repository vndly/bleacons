package com.mauriciotogneri.bleacons;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.mauriciotogneri.bleacons.BeaconService.BeaconBinder;
import com.mauriciotogneri.bleacons.interfaces.BeaconManagerObserver;
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

    public void start()
    {
        Intent intent = new Intent(context, BeaconService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void pause()
    {
        if ((beaconService != null) && (isConnected()))
        {
            beaconService.pause();
        }
    }

    public void resume()
    {
        if ((beaconService != null) && (isConnected()))
        {
            beaconService.resume();
        }
    }

    public void stop()
    {
        context.unbindService(serviceConnection);
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void startListening(ReadingMode readingMode)
    {
        if (isConnected())
        {
            beaconService.startListening(readingMode);
        }
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
}