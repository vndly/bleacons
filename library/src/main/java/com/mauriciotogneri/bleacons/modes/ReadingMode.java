package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.BeaconCache;
import com.mauriciotogneri.bleacons.BeaconFilter;
import com.mauriciotogneri.bleacons.beacons.Beacon;

import java.util.ArrayList;
import java.util.List;

public abstract class ReadingMode<T>
{
    private final List<T> beaconListeners;
    private final List<BeaconFilter> beaconFilters;
    private final BeaconCache beaconCache;
    private final Object beaconCacheLock = new Object();

    public ReadingMode(List<T> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity)
    {
        this.beaconListeners = beaconListeners;
        this.beaconFilters = beaconFilters;
        this.beaconCache = new BeaconCache(maxCapacity);
    }

    public ReadingMode(T beaconListener, BeaconFilter beaconFilter, int maxCapacity)
    {
        this.beaconListeners = new ArrayList<>();
        this.beaconListeners.add(beaconListener);

        this.beaconFilters = new ArrayList<>();
        this.beaconFilters.add(beaconFilter);

        this.beaconCache = new BeaconCache(maxCapacity);
    }

    protected Beacon getBeacon(String macAddress, byte[] data)
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

    protected List<T> getBeaconListeners()
    {
        return beaconListeners;
    }

    public void process(String macAddress, int rssi, byte[] data)
    {
        Beacon beacon = getBeacon(macAddress, data);

        if (beacon != null)
        {
            processBeacon(beacon, rssi, System.currentTimeMillis());
        }
    }

    public abstract void processBeacon(Beacon beacon, int rssi, long timestamp);
}