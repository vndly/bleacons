package com.mauriciotogneri.beacons;

import java.util.List;

public interface BeaconListener
{
	void onReceive(List<Beacon> beacons);
}