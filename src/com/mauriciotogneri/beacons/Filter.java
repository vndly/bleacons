package com.mauriciotogneri.beacons;

public interface Filter
{
	Beacon getBeacon(byte[] data);
}