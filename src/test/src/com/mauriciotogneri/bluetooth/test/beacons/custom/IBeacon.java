package com.mauriciotogneri.bluetooth.test.beacons.custom;

import java.util.Arrays;
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
	
	public static class Filter extends BeaconFilter
	{
		private final String filterUUID;
		private final Integer filterMajor;
		private final Integer filterMinor;
		
		private static final byte HEADER_0 = BeaconFilter.toByte("02");
		private static final byte HEADER_1 = BeaconFilter.toByte("01");
		private static final byte HEADER_2 = BeaconFilter.toByte("06");
		private static final byte HEADER_3 = BeaconFilter.toByte("1A");
		private static final byte HEADER_4 = BeaconFilter.toByte("FF");
		private static final byte HEADER_5 = BeaconFilter.toByte("4C");
		private static final byte HEADER_6 = BeaconFilter.toByte("00");
		private static final byte HEADER_7 = BeaconFilter.toByte("02");
		private static final byte HEADER_8 = BeaconFilter.toByte("15");
		
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
			return ((data[0] == Filter.HEADER_0) && // 0x02
			    (data[1] == Filter.HEADER_1) && // 0x01
			    (data[2] == Filter.HEADER_2) && // 0x06
			    (data[3] == Filter.HEADER_3) && // 0x1A
			    (data[4] == Filter.HEADER_4) && // 0xFF
			    (data[5] == Filter.HEADER_5) && // 0x4C
			    (data[6] == Filter.HEADER_6) && // 0x00
			    (data[7] == Filter.HEADER_7) && // 0x02
			(data[8] == Filter.HEADER_8)); // 0x15
		}
		
		private String getUUID(byte[] data)
		{
			String uuid = toHex(Arrays.copyOfRange(data, 9, 25)); // 9-24
			
			StringBuilder builder = new StringBuilder();
			builder.append(uuid.substring(0, 8)); // 8 bytes
			builder.append("-");
			builder.append(uuid.substring(8, 12)); // 4 bytes
			builder.append("-");
			builder.append(uuid.substring(12, 16)); // 4 bytes
			builder.append("-");
			builder.append(uuid.substring(16, 20)); // 4 bytes
			builder.append("-");
			builder.append(uuid.substring(20, 32)); // 12 bytes
			
			return builder.toString();
		}
		
		private int getMajor(byte[] data)
		{
			byte[] array =
				{
				    data[25], data[26]
				};
			
			return toInt(array);
		}
		
		private int getMinor(byte[] data)
		{
			byte[] array =
				{
				    data[27], data[28]
				};
			
			return toInt(array);
		}
		
		private int getTxPower(byte[] data)
		{
			return data[29];
		}
	}
}