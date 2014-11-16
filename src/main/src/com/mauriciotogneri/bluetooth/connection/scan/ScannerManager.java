package com.mauriciotogneri.bluetooth.connection.scan;

import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ScannerManager
{
	private final Context context;
	private final DeviceScanner deviceScanner;
	private final BluetoothAdapter bluetoothAdapter;
	private final BroadcastReceiver receiver;
	
	public ScannerManager(Context context, DeviceScanner deviceScanner)
	{
		this.context = context;
		this.deviceScanner = deviceScanner;
		this.bluetoothAdapter = getBluetoothAdapter();
		
		this.receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
				{
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					onDeviceDiscovered(device);
				}
			}
		};
	}
	
	private BluetoothAdapter getBluetoothAdapter()
	{
		BluetoothAdapter result = BluetoothAdapter.getDefaultAdapter();
		
		if (!result.isEnabled())
		{
			result.enable();
		}
		
		return result;
	}
	
	public void scan(boolean includePaired)
	{
		if (includePaired)
		{
			Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();
			
			for (BluetoothDevice device : pairedDevices)
			{
				onDeviceDiscovered(device);
			}
		}
		
		if (!this.bluetoothAdapter.isDiscovering())
		{
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			this.context.registerReceiver(this.receiver, filter);
			
			this.bluetoothAdapter.startDiscovery();
		}
	}
	
	public void scan()
	{
		scan(false);
	}
	
	private void onDeviceDiscovered(BluetoothDevice device)
	{
		this.deviceScanner.onDeviceDiscovered(device);
	}
	
	public void stop()
	{
		if (this.bluetoothAdapter.isDiscovering())
		{
			this.bluetoothAdapter.cancelDiscovery();
		}
		
		try
		{
			this.context.unregisterReceiver(this.receiver);
		}
		catch (IllegalArgumentException e)
		{
		}
	}
}