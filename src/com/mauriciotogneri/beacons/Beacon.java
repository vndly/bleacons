package com.mauriciotogneri.beacons;

import java.util.Map;

public class Beacon
{
	private final Map<String, String> data;
	private final int rssi;
	private final String macAddress;
	
	public Beacon(String macAddress, Map<String, String> data, int rssi)
	{
		this.macAddress = macAddress;
		this.data = data;
		this.rssi = rssi;
	}
	
	public String getMacAddress()
	{
		return this.macAddress;
	}
	
	public Map<String, String> getData()
	{
		return this.data;
	}
	
	public int getRssi()
	{
		return this.rssi;
	}
}