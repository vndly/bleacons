package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.BeaconFilter;
import com.mauriciotogneri.bleacons.Reading;
import com.mauriciotogneri.bleacons.beacons.Beacon;
import com.mauriciotogneri.bleacons.modes.ReadingModeContinuous.Listener;

import java.util.List;

public class ReadingModeContinuous extends ReadingMode<Listener>
{
    public ReadingModeContinuous(List<Listener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity)
    {
        super(beaconListeners, beaconFilters, maxCapacity);
    }

    public ReadingModeContinuous(Listener beaconListeners, BeaconFilter beaconFilters, int maxCapacity)
    {
        super(beaconListeners, beaconFilters, maxCapacity);
    }

    @Override
    public void processBeacon(Beacon beacon, int rssi, long timestamp)
    {
        List<Listener> beaconListeners = getBeaconListeners();

        for (Listener listener : beaconListeners)
        {
            listener.onReceive(new Reading(beacon, rssi, timestamp));
        }
    }

    public interface Listener
    {
        void onReceive(Reading readings);
    }
}