package com.mauriciotogneri.beacons;

public interface BeaconFilter
{
	BeaconData getBeaconData(byte[] data);
}