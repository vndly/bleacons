package com.mauriciotogneri.bluetooth.beacons.custom;

import com.mauriciotogneri.bluetooth.beacons.Beacon;
import com.mauriciotogneri.bluetooth.beacons.BeaconFilter;

public class IBeacon extends Beacon
{
	public final String uuid;
	public final int major;
	public final int minor;
	
	public IBeacon(String macAddress, int rssi, String uuid, int major, int minor)
	{
		super(macAddress, rssi);
		
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
	}
	
	public static class Filter implements BeaconFilter
	{
		private final String filterUUID;
		private final Integer filterMajor;
		private final Integer filterMinor;
		
		public Filter(String filterUUID, Integer filterMajor, Integer filterMinor)
		{
			this.filterUUID = filterUUID;
			this.filterMajor = filterMajor;
			this.filterMinor = filterMinor;
		}
		
		public Filter()
		{
			this(null, null, null);
		}
		
		@Override
		public IBeacon getBeacon(String macAddres, int rssi, byte[] data)
		{
			String uuid = getUUID(data);
			int major = getMajor(data);
			int minor = getMinor(data);
			
			if ((this.filterUUID != null) && (!this.filterUUID.equals(uuid)))
			{
				return null;
			}
			
			if ((this.filterMajor != null) && (this.filterMajor != major))
			{
				return null;
			}
			
			if ((this.filterMinor != null) && (this.filterMinor != minor))
			{
				return null;
			}
			
			return new IBeacon(macAddres, rssi, uuid, major, minor);
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
}