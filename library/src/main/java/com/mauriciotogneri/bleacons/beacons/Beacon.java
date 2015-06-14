package com.mauriciotogneri.bleacons.beacons;

/**
 * Represents a transmitter that uses Bluetooth Low Energy 4.0 to broadcast signals. It contains a
 * MAC address that uniquely identifies it.
 */
public class Beacon
{
    /**
     * The beacons MAC address.
     */
    public final String macAddress;

    /**
     * Constructs a new beacon.
     *
     * @param macAddress the mac address
     */
    public Beacon(String macAddress)
    {
        this.macAddress = macAddress;
    }
}