package com.mauriciotogneri.bluetooth.beacons.custom;

import com.mauriciotogneri.bluetooth.beacons.Beacon;
import com.mauriciotogneri.bluetooth.beacons.BeaconFilter;

public class IBeacon extends Beacon
{
	public final String uuid;
	public final int major;
	public final int minor;
	public final int txPower;
	
	public IBeacon(String macAddress, int rssi, String uuid, int major, int minor, int txPower)
	{
		super(macAddress, rssi);
		
		this.uuid = uuid;
		this.major = major;
		this.minor = minor;
		this.txPower = txPower;
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
		public Beacon getBeacon(String macAddres, int rssi, byte[] data)
		{
			IBeacon result = null;
			
			if (data.length >= 30)
			{
				if (isValidHeader(data))
				{
					String uuid = getUUID(data);
					int major = getMajor(data);
					int minor = getMinor(data);
					int txPower = getTxPower(data);
					
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
					
					result = new IBeacon(macAddres, rssi, uuid, major, minor, txPower);
				}
			}
			
			return result;
		}
		
		private boolean isValidHeader(byte[] data)
		{
			return ((data[0] == 2) && (data[1] == 1) && (data[2] == 6) && (data[3] == 26) && (data[4] == 255) && (data[5] == 76) && (data[6] == 0) && (data[7] == 2) && (data[8] == 21));
		}
		
		private String getUUID(byte[] data)
		{
			return ""; // TODO: 9-24
		}
		
		private int getMajor(byte[] data)
		{
			return 0; // TODO: 25-26
		}
		
		private int getMinor(byte[] data)
		{
			return 0; // TODO: 27-28
		}
		
		private int getTxPower(byte[] data)
		{
			return 255 - data[29];
		}
	}
}