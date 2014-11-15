package com.mauriciotogneri.bluetooth.beacons;

public interface BeaconFilter
{
	BeaconData getBeaconData(byte[] data);
}