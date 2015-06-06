package com.mauriciotogneri.bleacons.modes;

import com.mauriciotogneri.bleacons.Beacon;
import com.mauriciotogneri.bleacons.BeaconCache;
import com.mauriciotogneri.bleacons.BeaconFilter;
import com.mauriciotogneri.bleacons.Reading;
import com.mauriciotogneri.bleacons.interfaces.BeaconListener;

import java.util.ArrayList;
import java.util.List;

public abstract class ReadingMode
{
    private final List<BeaconListener> beaconListeners;
    private final List<BeaconFilter> beaconFilters;
    private final BeaconCache beaconCache;

    public ReadingMode(List<BeaconListener> beaconListeners, List<BeaconFilter> beaconFilters, int maxCapacity)
    {
        this.beaconListeners = beaconListeners;
        this.beaconFilters = beaconFilters;
        this.beaconCache = new BeaconCache(maxCapacity);
    }

    public ReadingMode(BeaconListener beaconListener, BeaconFilter beaconFilter, int maxCapacity)
    {
        this.beaconListeners = new ArrayList<>();
        this.beaconListeners.add(beaconListener);

        this.beaconFilters = new ArrayList<>();
        this.beaconFilters.add(beaconFilter);

        this.beaconCache = new BeaconCache(maxCapacity);
    }

    protected Beacon getBeacon(String macAddress, byte[] data)
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

        return null;
    }

    protected void broadcastReading(List<Reading> readings)
    {
        for (BeaconListener listener : beaconListeners)
        {
            listener.onReceive(readings);
        }
    }

    public void process(String macAddress, int rssi, byte[] data)
    {
        Beacon beacon = getBeacon(macAddress, data);

        if (beacon != null)
        {
            processBeacon(beacon, rssi);
        }
    }

    public abstract void processBeacon(Beacon beacon, int rssi);
}