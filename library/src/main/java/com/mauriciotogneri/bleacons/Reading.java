package com.mauriciotogneri.bleacons;

public class Reading
{
    public final Beacon beacon;
    public final int rssi;
    public final long timestamp; // in milliseconds

    public Reading(Beacon beacon, int rssi, long timestamp)
    {
        this.beacon = beacon;
        this.rssi = rssi;
        this.timestamp = timestamp;
    }
}