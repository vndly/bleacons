package com.mauriciotogneri.bluetooth.beacons.custom;

import java.util.Arrays;
import com.mauriciotogneri.bluetooth.beacons.Beacon;
import com.mauriciotogneri.bluetooth.beacons.BeaconFilter;

public class AltBeacon extends Beacon
{
	public final String uuid;
	public final int identifier1;
	public final int identifier2;
	public final int referenceRSSI;
	
	public AltBeacon(String macAddress, int rssi, String uuid, int identifier1, int identifier2, int referenceRSSI)
	{
		super(macAddress, rssi);
		
		this.uuid = uuid;
		this.identifier1 = identifier1;
		this.identifier2 = identifier2;
		this.referenceRSSI = referenceRSSI;
	}
	
	public static class Filter extends BeaconFilter
	{
		private static final byte HEADER_0 = BeaconFilter.toByte("1B");
		private static final byte HEADER_1 = BeaconFilter.toByte("FF");
		private static final byte HEADER_4 = BeaconFilter.toByte("BE");
		private static final byte HEADER_5 = BeaconFilter.toByte("AC");
		
		@Override
		public Beacon getBeacon(String macAddres, int rssi, byte[] data)
		{
			AltBeacon result = null;
			
			if (data.length >= 30)
			{
				if (isValidHeader(data))
				{
					String uuid = getUUID(data);
					int identifier1 = getIdentifier1(data);
					int identifier2 = getIdentifier2(data);
					int referenceRSSI = getReferenceRSSI(data);
					
					result = new AltBeacon(macAddres, rssi, uuid, identifier1, identifier2, referenceRSSI);
				}
			}
			
			return result;
		}
		
		private boolean isValidHeader(byte[] data)
		{
			return ((data[0] == Filter.HEADER_0) && // 0x1B
			    (data[1] == Filter.HEADER_1) && // 0xFF
			    // (data[2] == ?) && // ??
			    // (data[3] == ?) && // ??
			    (data[4] == Filter.HEADER_4) && // 0xBE
			(data[5] == Filter.HEADER_5)); // 0xAC
		}
		
		private String getUUID(byte[] data)
		{
			String uuid = toHex(Arrays.copyOfRange(data, 6, 22)); // 6-21
			
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
		
		private int getIdentifier1(byte[] data)
		{
			byte[] array =
				{
				    data[22], data[23]
				};
			
			return toInt(array);
		}
		
		private int getIdentifier2(byte[] data)
		{
			byte[] array =
				{
				    data[24], data[25]
				};
			
			return toInt(array);
		}
		
		private int getReferenceRSSI(byte[] data)
		{
			return data[26] - 256;
		}
	}
}