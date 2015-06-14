package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.beacons.Beacon;
import com.mauriciotogneri.bleacons.beacons.BeaconFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Receives the signals read from the BeaconManager and process them. When ready, the class will
 * inform the listener (or listeners) about the calculated readings.
 *
 * @param <ListenerType> the type of listener to inform
 */
public abstract class ReadingMode<ListenerType>
{
    private final List<ListenerType> beaconListeners;
    private final List<BeaconFilter> beaconFilters;
    private final BeaconCache beaconCache;
    private final Object beaconCacheLock = new Object();
    private volatile boolean isScanning = false;

    /**
     * Constructs the reading mode.
     *
     * @param beaconListeners the listeners to inform about the readings
     * @param beaconFilters   the list of filters to apply
     * @param maxCapacity     the maximum capacity of cached beacons (zero for infinite capacity)
     */
    public ReadingMode(List<ListenerType> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity)
    {
        this.beaconListeners = beaconListeners;
        this.beaconFilters = beaconFilters;
        this.beaconCache = new BeaconCache(maxCapacity);
    }

    /**
     * Constructs the reading mode.
     *
     * @param beaconListener the listener to inform about the readings
     * @param beaconFilter   the filter to apply
     * @param maxCapacity    the maximum capacity of cached beacons (zero for infinite capacity)
     */
    public ReadingMode(ListenerType beaconListener, BeaconFilter beaconFilter, int maxCapacity)
    {
        this.beaconListeners = new ArrayList<>();
        this.beaconListeners.add(beaconListener);

        this.beaconFilters = new ArrayList<>();
        this.beaconFilters.add(beaconFilter);

        this.beaconCache = new BeaconCache(maxCapacity);
    }

    private Beacon getBeacon(String macAddress, byte[] data)
    {
        synchronized (beaconCacheLock)
        {
            Beacon cachedBeacon = beaconCache.get(macAddress);

            if (cachedBeacon != null)
            {
                return cachedBeacon;
            }
            else
            {
                for (BeaconFilter filter : beaconFilters)
                {
                    Beacon beacon = filter.getBeacon(macAddress, data);

                    if (beacon != null)
                    {
                        beaconCache.put(macAddress, beacon);

                        return beacon;
                    }
                }
            }
        }

        return null;
    }

    public void process(String macAddress, int rssi, byte[] data)
    {
        Beacon beacon = getBeacon(macAddress, data);

        if (beacon != null)
        {
            processReading(beacon, rssi, System.currentTimeMillis());
        }
    }

    public void setScanning(boolean scanning)
    {
        boolean startScanning = !isScanning && scanning;
        boolean stopScanning = isScanning && !scanning;

        isScanning = scanning;

        if (startScanning)
        {
            onStartScanning();
        }

        if (stopScanning)
        {
            onStopScanning();
        }
    }

    /**
     * Returns true if it's scanning for beacons.
     *
     * @return true if it's scanning for beacons, false otherwise
     */
    protected boolean isScanning()
    {
        return isScanning;
    }

    /**
     * Called when the status changes from not scanning to scanning.
     */
    protected void onStartScanning()
    {
    }

    /**
     * Called when the status changes from scanning to not scanning.
     */
    protected void onStopScanning()
    {
    }

    /**
     * Returns the list of listeners to inform.
     *
     * @return the list of listeners to inform
     */
    protected List<ListenerType> getBeaconListeners()
    {
        return beaconListeners;
    }

    /**
     * Process the reading received.
     *
     * @param beacon    the beacon read
     * @param rssi      the RSSI value (in db)
     * @param timestamp the timestamp when the signal was received
     */
    public abstract void processReading(Beacon beacon, int rssi, long timestamp);

    public static abstract class BaseBuilder<ListenerType>
    {
        protected final int maxCapacity;
        protected final List<ListenerType> beaconListeners = new ArrayList<>();
        protected final List<BeaconFilter> beaconFilters = new ArrayList<>();

        /**
         * Constructs the builder.
         *
         * @param maxCapacity the maximum capacity of cached beacons (zero for infinite capacity)
         */
        public BaseBuilder(int maxCapacity)
        {
            this.maxCapacity = maxCapacity;
        }

        /**
         * Adds a list of listeners to the reading mode.
         *
         * @param listeners the list of listeners
         * @return the builder updated
         */
        public BaseBuilder addListeners(ListenerType... listeners)
        {
            beaconListeners.addAll(Arrays.asList(listeners));

            return this;
        }

        /**
         * Adds a list of filters to the reading mode.
         *
         * @param filters the list of filters
         * @return the builder updated
         */
        public BaseBuilder addFilters(BeaconFilter... filters)
        {
            beaconFilters.addAll(Arrays.asList(filters));

            return this;
        }

        /**
         * Returns the reading mode instantiated.
         *
         * @return the reading mode instantiated
         */
        public abstract ReadingMode build();
    }
}