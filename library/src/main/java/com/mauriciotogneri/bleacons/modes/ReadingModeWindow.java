package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.BeaconFilter;
import com.mauriciotogneri.bleacons.Reading;
import com.mauriciotogneri.bleacons.beacons.Beacon;
import com.mauriciotogneri.bleacons.modes.ReadingModeWindow.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadingModeWindow extends ReadingMode<Listener>
{
    private int scanFrequency;
    private final Object currentBeaconsLock = new Object();
    private final Map<String, Beacon> currentBeacons = new HashMap<>();

    public ReadingModeWindow(List<Listener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity, int scanFrequency)
    {
        super(beaconListeners, beaconFilters, maxCapacity);

        this.scanFrequency = scanFrequency;
    }

    public ReadingModeWindow(Listener beaconListener, BeaconFilter beaconFilter, int maxCapacity, int scanFrequency)
    {
        super(beaconListener, beaconFilter, maxCapacity);

        this.scanFrequency = scanFrequency;
    }

    @Override
    public void processBeacon(Beacon beacon, int rssi, long timestamp)
    {
        //        List<Beacon> beaconsList = new ArrayList<>();
        //
        //        synchronized (currentBeaconsLock)
        //        {
        //            Collection<Beacon> beacons = currentBeacons.values();
        //
        //            for (Beacon beacon : beacons)
        //            {
        //                beaconsList.add(beacon);
        //            }
        //
        //            currentBeacons.clear();
        //        }
        //
        //        synchronized (listenersLock)
        //        {
        //            for (BeaconListener listener : listeners)
        //            {
        //                listener.onReceive(beaconsList);
        //            }
        //        }

        List<Reading> readings = new ArrayList<>();

        List<Listener> beaconListeners = getBeaconListeners();

        for (Listener listener : beaconListeners)
        {
            listener.onReceive(readings);
        }
    }

    public interface Listener
    {
        void onReceive(List<Reading> readings);
    }
}