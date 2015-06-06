package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.Beacon;
import com.mauriciotogneri.bleacons.BeaconFilter;
import com.mauriciotogneri.bleacons.interfaces.BeaconListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModeWindow extends ReadingMode
{
    private int scanFrequency;
    private final Object currentBeaconsLock = new Object();
    private final Map<String, Beacon> currentBeacons = new HashMap<>();

    public ModeWindow(List<BeaconListener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity, int scanFrequency)
    {
        super(beaconListeners, beaconFilters, maxCapacity);

        this.scanFrequency = scanFrequency;
    }

    @Override
    public void processBeacon(Beacon beacon, int rssi)
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
    }
}