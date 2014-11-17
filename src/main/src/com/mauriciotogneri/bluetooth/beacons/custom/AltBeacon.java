package com.mauriciotogneri.bluetooth.beacons.custom;

import com.mauriciotogneri.bluetooth.beacons.Beacon;
import com.mauriciotogneri.bluetooth.beacons.BeaconFilter;

public class AltBeacon extends Beacon
{
	public AltBeacon(String macAddress, int rssi)
	{
		super(macAddress, rssi);
	}
	
	public static class Filter extends BeaconFilter
	{
		@Override
		public Beacon getBeacon(String macAddres, int rssi, byte[] data)
		{
			return null;
		}
	}
}