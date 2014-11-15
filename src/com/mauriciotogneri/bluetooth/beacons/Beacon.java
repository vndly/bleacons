package com.mauriciotogneri.bluetooth.beacons;

public class Beacon
{
	private final BeaconData data;
	private final int rssi;
	private final String macAddress;
	
	public Beacon(String macAddress, BeaconData data, int rssi)
	{
		this.macAddress = macAddress;
		this.data = data;
		this.rssi = rssi;
	}
	
	public String getMacAddress()
	{
		return this.macAddress;
	}
	
	@SuppressWarnings("unchecked")
	public <Type> Type get(String key)
	{
		return (Type)this.data.get(key);
	}
	
	public boolean contains(String key)
	{
		return this.data.contains(key);
	}
	
	public int getRssi()
	{
		return this.rssi;
	}
}