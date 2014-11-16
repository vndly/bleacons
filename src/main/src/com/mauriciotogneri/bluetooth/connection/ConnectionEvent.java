package com.mauriciotogneri.bluetooth.connection;

import android.bluetooth.BluetoothDevice;

public interface ConnectionEvent
{
	void onReceive(BluetoothDevice device, byte[] message);
	
	void onConnect(BluetoothDevice device);
	
	void onDisconnect(BluetoothDevice device);
}