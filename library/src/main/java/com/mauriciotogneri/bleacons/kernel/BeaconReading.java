package com.mauriciotogneri.bleacons.kernel;

import com.mauriciotogneri.bleacons.beacons.Beacon;

/**
 * Contains all the information related to a signal received from the device.
 */
public class BeaconReading
{
    /**
     * The beacon from where the signal was received.
     */
    public final Beacon beacon;

    /**
     * The RSSI value of the signal (in db).
     */
    public final int rssi;

    /**
     * The timestamp when the signal was received (in milliseconds since January 1, 1970 00:00:00.0
     * UTC).
     */
    public final long timestamp;

    /**
     * Constructs a new beacon reading
     *
     * @param beacon    the beacon
     * @param rssi      the RSSI value
     * @param timestamp the timestamp
     */
    public BeaconReading(Beacon beacon, int rssi, long timestamp)
    {
        this.beacon = beacon;
        this.rssi = rssi;
        this.timestamp = timestamp;
    }
}