package com.mauriciotogneri.bluetooth.beacons;

public interface BeaconFilter
{
	Beacon getBeacon(String macAddress, int rssi, byte[] data);
}