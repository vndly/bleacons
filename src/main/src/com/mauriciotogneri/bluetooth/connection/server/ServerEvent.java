package com.mauriciotogneri.bluetooth.connection.server;

import android.bluetooth.BluetoothDevice;

public interface ServerEvent
{
	void onReceive(BluetoothDevice device, byte[] message);
	
	void onClientConnect(BluetoothDevice device);
	
	void onClientDisconnect(BluetoothDevice device);
}