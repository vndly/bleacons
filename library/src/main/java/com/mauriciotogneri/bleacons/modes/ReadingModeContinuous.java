package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.beacons.Beacon;
import com.mauriciotogneri.bleacons.BeaconFilter;
import com.mauriciotogneri.bleacons.Reading;
import com.mauriciotogneri.bleacons.interfaces.BeaconListener;

import java.util.ArrayList;
import java.util.List;

public class ReadingModeContinuous extends ReadingMode
{
    private final List<Reading> readings = new ArrayList<>();

    public ReadingModeContinuous(List<BeaconListener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity)
    {
        super(beaconListeners, beaconFilters, maxCapacity);

        init();
    }

    public ReadingModeContinuous(BeaconListener beaconListeners, BeaconFilter beaconFilters, int maxCapacity)
    {
        super(beaconListeners, beaconFilters, maxCapacity);

        init();
    }

    private void init()
    {
        readings.add(0, null);
    }

    @Override
    public void processBeacon(Beacon beacon, int rssi, long timestamp)
    {
        readings.remove(0);
        readings.add(0, new Reading(beacon, rssi, timestamp));

        broadcastReadings(readings);
    }
}