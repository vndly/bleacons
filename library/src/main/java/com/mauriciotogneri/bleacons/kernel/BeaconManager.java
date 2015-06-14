package com.mauriciotogneri.bleacons.kernel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.mauriciotogneri.bleacons.kernel.BeaconService.ServiceBinder;
import com.mauriciotogneri.bleacons.modes.ReadingMode;

/**
 * Entry point to which the application will communicate in order to perform different operations
 * related with the beacons. It uses internally an Android service to perform the reading of the
 * beacons' signals.
 */
public class BeaconManager
{
    private final Context context;
    private final BeaconManagerObserver beaconManagerObserver;
    private BeaconService beaconService;
    private volatile boolean isConnected = false;
    private final ServiceConnection serviceConnection;

    /**
     * Constructs a BeaconManager.
     *
     * @param context               the context
     * @param beaconManagerObserver the observer that will be notified of the events
     * @throws UnsupportedBluetoothLeException if the device doesn't support Bluetooth Low Energy
     */
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

    /**
     * Connects to the internal service.
     *
     * @return true if the operation was successful
     */
    public boolean connect()
    {
        Intent intent = new Intent(context, BeaconService.class);

        return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Disconnects from the internal service.
     */
    public void disconnect()
    {
        stop();

        context.unbindService(serviceConnection);
    }

    /**
     * Starts reading the signals from the beacons.
     *
     * @return true if the operation was successful
     */
    public boolean start()
    {
        if ((beaconService != null) && (isConnected()))
        {
            beaconService.start();

            return true;
        }

        return false;
    }

    /**
     * Pauses the reading of the beacon's signals.
     *
     * @return true if the operation was successful
     */
    public boolean pause()
    {
        if ((beaconService != null) && (isConnected()))
        {
            beaconService.pause();

            return true;
        }

        return false;
    }

    /**
     * Stops the readings of the signals and removes the current reading mode (if any).
     *
     * @return true if the operation was successful
     */
    public boolean stop()
    {
        if ((beaconService != null) && (isConnected()))
        {
            beaconService.stop();

            return true;
        }

        return false;
    }

    /**
     * Sets the current reading mode. It removes the previous one (if any). Notice that this method
     * doesn't start the reading of the beacons. For that you must call {@link #start()}.
     *
     * @param readingMode the reading mode
     * @return true if the operation was successful
     */
    public boolean setMode(ReadingMode readingMode)
    {
        if (isConnected())
        {
            beaconService.setMode(readingMode);

            return true;
        }

        return false;
    }

    /**
     * Returns true if connected to the internal service.
     *
     * @return true if connected to the internal service, false otherwise.
     */
    public boolean isConnected()
    {
        return isConnected;
    }

    /**
     * Returns true if it's currently scanning for beacons.
     *
     * @return true if it's currently scanning for beacons, false otherwise.
     */
    public boolean isScanning()
    {
        return beaconService.isScanning();
    }

    private void onConnected(IBinder service)
    {
        isConnected = true;

        ServiceBinder binder = (ServiceBinder) service;
        beaconService = binder.getService();

        beaconManagerObserver.onConnected();
    }

    private void onDisconnected()
    {
        isConnected = false;

        beaconManagerObserver.onDisconnected();
    }

    /**
     * Observer that will be notified when the manager connects and disconnects from the service.
     */
    public interface BeaconManagerObserver
    {
        /**
         * The manager has connected to the internal service.
         */
        void onConnected();

        /**
         * The manager has disconnected from the internal service.
         */
        void onDisconnected();
    }
}