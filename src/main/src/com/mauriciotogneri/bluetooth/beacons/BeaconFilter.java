package com.mauriciotogneri.bluetooth.beacons;

public abstract class BeaconFilter
{
	private static final char[] HEX_CHARS =
		{
		    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
		};
	
	protected static byte toByte(String hex)
	{
		int result = Integer.parseInt(hex, 16);
		
		if (result > 127)
		{
			result -= 256;
		}
		
		return (byte)result;
	}
	
	protected int toInt(byte[] bytes)
	{
		return Integer.parseInt(toHex(bytes), 16);
	}
	
	protected String toHex(byte[] bytes)
	{
		char[] chars = new char[bytes.length * 2];
		
		for (int i = 0; i < bytes.length; i++)
		{
			int value = bytes[i] & 0xFF;
			chars[i * 2] = BeaconFilter.HEX_CHARS[value >>> 4];
			chars[i * 2 + 1] = BeaconFilter.HEX_CHARS[value & 0x0F];
		}
		
		return new String(chars);
	}
	
	public abstract Beacon getBeacon(String macAddress, int rssi, byte[] data);
}