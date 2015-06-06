package com.mauriciotogneri.bleacons;

import com.mauriciotogneri.bleacons.beacons.Beacon;

import java.util.LinkedHashMap;

public class BeaconCache extends LinkedHashMap<String, Beacon>
{
    private final int maxCapacity;

    public BeaconCache(int maxCapacity)
    {
        super(maxCapacity * 10 / 7, 0.7f, true);

        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Entry<String, Beacon> eldest)
    {
        return size() > maxCapacity;
    }
}