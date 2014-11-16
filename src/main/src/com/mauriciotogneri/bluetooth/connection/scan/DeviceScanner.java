package com.mauriciotogneri.bluetooth.connection.scan;

import android.bluetooth.BluetoothDevice;

public interface DeviceScanner
{
	void onDeviceDiscovered(BluetoothDevice device);
}