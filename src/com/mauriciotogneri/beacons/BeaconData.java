package com.mauriciotogneri.beacons;

import java.util.HashMap;
import java.util.Map;

public class BeaconData
{
	private final Map<String, String> data = new HashMap<String, String>();
	
	public void add(String key, String value)
	{
		this.data.put(key, value);
	}
	
	public String get(String key)
	{
		return this.data.get(key);
	}
	
	public boolean contains(String key)
	{
		return this.data.containsKey(key);
	}
}