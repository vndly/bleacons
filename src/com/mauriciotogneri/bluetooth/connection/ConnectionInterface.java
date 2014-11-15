package com.mauriciotogneri.bluetooth.connection;

import android.bluetooth.BluetoothDevice;

public interface ConnectionInterface
{
	void onReceive(byte[] message);
	
	void onConnect();
	
	void onDisconnect();
	
	void onDeviceDiscovered(BluetoothDevice device);
}