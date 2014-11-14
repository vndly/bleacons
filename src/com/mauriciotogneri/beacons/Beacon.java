package com.mauriciotogneri.beacons;

import java.util.HashMap;
import java.util.Map;

public class Beacon
{
	private Map<String, String> data = new HashMap<String, String>();
	private final int rssi;
	private final int txPower;
	private final String macAddress;
	
	public Beacon(Map<String, String> data, int rssi, int txPower, String macAddress)
	{
		this.data = data;
		this.rssi = rssi;
		this.txPower = txPower;
		this.macAddress = macAddress;
	}
}