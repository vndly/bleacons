package com.mauriciotogneri.bluetooth.beacons;

import java.util.List;

public interface BeaconListener
{
	void onReceive(List<Beacon> beacons);
}