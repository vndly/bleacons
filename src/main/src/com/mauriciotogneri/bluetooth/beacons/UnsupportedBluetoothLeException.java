package com.mauriciotogneri.bluetooth.beacons;

public class UnsupportedBluetoothLeException extends Exception
{
	private static final long serialVersionUID = -2865765651529657978L;
	
	public UnsupportedBluetoothLeException(int currentVersion)
	{
		super("Minimum version required: 18 - Current version: " + currentVersion);
	}
}