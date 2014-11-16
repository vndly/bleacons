package com.mauriciotogneri.bluetooth.beacons;

public class Beacon
{
	private final String macAddress;
	private final int rssi;
	
	public Beacon(String macAddress, int rssi)
	{
		this.macAddress = macAddress;
		this.rssi = rssi;
	}
	
	public String getMacAddress()
	{
		return this.macAddress;
	}
	
	public int getRssi()
	{
		return this.rssi;
	}
}