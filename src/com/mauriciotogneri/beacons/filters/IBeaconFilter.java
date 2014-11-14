package com.mauriciotogneri.beacons.filters;

import com.mauriciotogneri.beacons.BeaconData;
import com.mauriciotogneri.beacons.BeaconFilter;

public class IBeaconFilter implements BeaconFilter
{
	private final String uuid;
	private final Integer major;
	private final Integer minor;
	
	public IBeaconFilter(String uuid, Integer major, Integer minor)
	{
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
	}
	
	public IBeaconFilter()
	{
		this(null, null, null);
	}
	
	@Override
	public BeaconData getBeaconData(byte[] data)
	{
		String uuid = getUUID(data);
		int major = getMajor(data);
		int minor = getMinor(data);
		
		if ((this.uuid != null) && (!this.uuid.equals(uuid)))
		{
			return null;
		}
		
		if ((this.major != null) && (this.major != major))
		{
			return null;
		}
		
		if ((this.minor != null) && (this.minor != minor))
		{
			return null;
		}
		
		BeaconData result = new BeaconData();
		result.add("uuid", uuid);
		result.add("major", major);
		result.add("minor", minor);
		
		return result;
	}
	
	private String getUUID(byte[] data)
	{
		return ""; // TODO
	}
	
	private int getMajor(byte[] data)
	{
		return 0; // TODO
	}
	
	private int getMinor(byte[] data)
	{
		return 0; // TODO
	}
}