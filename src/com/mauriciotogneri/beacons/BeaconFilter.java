package com.mauriciotogneri.beacons;

import java.util.Map;

public interface BeaconFilter
{
	Map<String, String> getBeaconData(byte[] data);
}