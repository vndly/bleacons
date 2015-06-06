package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.Beacon;
import com.mauriciotogneri.bleacons.BeaconFilter;
import com.mauriciotogneri.bleacons.Reading;
import com.mauriciotogneri.bleacons.interfaces.BeaconListener;

import java.util.ArrayList;
import java.util.List;

public class ModeContinuous extends ReadingMode
{
    public ModeContinuous(List<BeaconListener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity)
    {
        super(beaconListeners, beaconFilters, maxCapacity);
    }

    public ModeContinuous(BeaconListener beaconListeners, BeaconFilter beaconFilters, int maxCapacity)
    {
        super(beaconListeners, beaconFilters, maxCapacity);
    }

    @Override
    public void processBeacon(Beacon beacon, int rssi)
    {
        List<Reading> readings = new ArrayList<>();
        readings.add(new Reading(beacon, rssi, System.currentTimeMillis()));

        broadcastReading(readings);
    }
}