package com.mauriciotogneri.bluetooth.beacons;

import java.util.HashMap;
import java.util.Map;

public class BeaconData
{
	private final Map<String, Object> data = new HashMap<String, Object>();
	
	public void add(String key, Object value)
	{
		this.data.put(key, value);
	}
	
	public Object get(String key)
	{
		return this.data.get(key);
	}
	
	public boolean contains(String key)
	{
		return this.data.containsKey(key);
	}
}