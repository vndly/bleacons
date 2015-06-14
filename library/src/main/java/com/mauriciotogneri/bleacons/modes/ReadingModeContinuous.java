package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.beacons.Beacon;
import com.mauriciotogneri.bleacons.beacons.BeaconFilter;
import com.mauriciotogneri.bleacons.kernel.BeaconReading;
import com.mauriciotogneri.bleacons.modes.ReadingModeContinuous.Listener;

import java.util.List;

/**
 * Implements a continuous reading mode. It informs the listeners immediately after receiving a
 * signal.
 */
public class ReadingModeContinuous extends ReadingMode<Listener>
{
    private ReadingModeContinuous(List<Listener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity)
    {
        super(beaconListeners, beaconFilters, maxCapacity);
    }

    @Override
    public void processReading(Beacon beacon, int rssi, long timestamp)
    {
        List<Listener> beaconListeners = getBeaconListeners();

        BeaconReading beaconReading = new BeaconReading(beacon, rssi, timestamp);

        for (Listener listener : beaconListeners)
        {
            listener.onReceive(beaconReading);
        }
    }

    /**
     * Builds a new instance of ReadingModeContinuous.
     */
    public static class Builder extends BaseBuilder<Listener>
    {
        /**
         * Constructs the builder.
         *
         * @param maxCapacity the maximum capacity of cached beacons (zero for infinite capacity)
         */
        public Builder(int maxCapacity)
        {
            super(maxCapacity);
        }

        /**
         * Returns the reading mode instantiated.
         *
         * @return the reading mode instantiated
         */
        public ReadingModeContinuous build()
        {
            return new ReadingModeContinuous(beaconListeners, beaconFilters, maxCapacity);
        }
    }

    /**
     * Listens for new beacons readings.
     */
    public interface Listener
    {
        /**
         * Called when a new beacon reading is ready.
         *
         * @param reading the beacon reading
         */
        void onReceive(BeaconReading reading);
    }
}