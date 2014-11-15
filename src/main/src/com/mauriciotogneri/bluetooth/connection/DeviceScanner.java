package com.mauriciotogneri.bluetooth.connection;

import android.bluetooth.BluetoothDevice;

public interface DeviceScanner
{
	void onDeviceDiscovered(BluetoothDevice device);
}