package com.mauriciotogneri.bleacons;

import com.mauriciotogneri.bleacons.beacons.Beacon;

public class BeaconReading
{
    public final Beacon beacon;
    public final int rssi;
    public final long timestamp; // in milliseconds

    public BeaconReading(Beacon beacon, int rssi, long timestamp)
    {
        this.beacon = beacon;
        this.rssi = rssi;
        this.timestamp = timestamp;
    }
}