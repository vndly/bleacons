package com.mauriciotogneri.bluetooth.connection.server;

import android.bluetooth.BluetoothDevice;

public interface ServerEvent
{
	void onReceive(BluetoothDevice device, byte[] message);
	
	void onConnect(BluetoothDevice device);
	
	void onErrorOpeningConnection();
	
	void onDisconnect(BluetoothDevice device);
}