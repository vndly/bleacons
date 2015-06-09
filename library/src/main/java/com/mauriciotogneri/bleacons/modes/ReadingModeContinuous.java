package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.BeaconFilter;
import com.mauriciotogneri.bleacons.BeaconReading;
import com.mauriciotogneri.bleacons.beacons.Beacon;
import com.mauriciotogneri.bleacons.modes.ReadingModeContinuous.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadingModeContinuous extends ReadingMode<Listener>
{
    private ReadingModeContinuous(List<Listener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity)
    {
        super(beaconListeners, beaconFilters, maxCapacity);
    }

    @Override
    public void processBeacon(Beacon beacon, int rssi, long timestamp)
    {
        List<Listener> beaconListeners = getBeaconListeners();

        BeaconReading beaconReading = new BeaconReading(beacon, rssi, timestamp);

        for (Listener listener : beaconListeners)
        {
            listener.onReceive(beaconReading);
        }
    }

    public static class Builder
    {
        private final int maxCapacity;
        private final List<Listener> beaconListeners = new ArrayList<>();
        private final List<BeaconFilter> beaconFilters = new ArrayList<>();

        public Builder(int maxCapacity)
        {
            this.maxCapacity = maxCapacity;
        }

        public Builder addListeners(Listener... listeners)
        {
            beaconListeners.addAll(Arrays.asList(listeners));

            return this;
        }

        public Builder addFilters(BeaconFilter... filters)
        {
            beaconFilters.addAll(Arrays.asList(filters));

            return this;
        }

        public ReadingModeContinuous build()
        {
            return new ReadingModeContinuous(beaconListeners, beaconFilters, maxCapacity);
        }
    }

    public interface Listener
    {
        void onReceive(BeaconReading readings);
    }
}