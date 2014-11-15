package com.mauriciotogneri.bluetooth.connection;

import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ConnectionManager
{
	private final BluetoothAdapter bluetoothAdapter;
	
	private final ClientThread clientThread;
	private final ServerThread serverThread;
	private final ConnectionThread connectionThread;
	
	private final Context context;
	private BroadcastReceiver receiver;
	
	public ConnectionManager(Context context, BluetoothDevice device, String uuid, ConnectionInterface connectionInterface)
	{
		this.context = context;
		this.bluetoothAdapter = getBluetoothAdapter();
		
		this.connectionThread = new ConnectionThread();
		
		this.clientThread = new ClientThread(device, uuid, connectionInterface, this.connectionThread);
		this.serverThread = null;
	}
	
	public ConnectionManager(Context context, String uuid, ConnectionInterface connectionInterface)
	{
		this.context = context;
		this.bluetoothAdapter = getBluetoothAdapter();
		
		this.connectionThread = new ConnectionThread();
		
		this.serverThread = new ServerThread(this.bluetoothAdapter, uuid, connectionInterface, this.connectionThread);
		this.clientThread = null;
	}
	
	private BluetoothAdapter getBluetoothAdapter()
	{
		BluetoothAdapter result = BluetoothAdapter.getDefaultAdapter();
		
		if (result != null)
		{
			if (!result.isEnabled())
			{
				result.enable();
			}
		}
		
		return result;
	}
	
	public void start()
	{
		stopDiscovery();
		
		if (this.clientThread != null)
		{
			this.clientThread.start();
		}
		else
		{
			this.serverThread.start();
		}
	}
	
	private void stopDiscovery()
	{
		if (this.bluetoothAdapter.isDiscovering())
		{
			this.bluetoothAdapter.cancelDiscovery();
		}
	}
	
	public void makeVisible(int duration)
	{
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
		this.context.startActivity(intent);
	}
	
	public void searchDevices(boolean includePaired)
	{
		if (includePaired)
		{
			Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();
			
			for (BluetoothDevice device : pairedDevices)
			{
				newDeviceDiscovered(device);
			}
		}
		
		this.receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
				{
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					newDeviceDiscovered(device);
				}
			}
		};
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.context.registerReceiver(this.receiver, filter);
		
		this.bluetoothAdapter.startDiscovery();
	}
	
	private void newDeviceDiscovered(BluetoothDevice device)
	{
		// TODO
	}
	
	public void close()
	{
		if (this.clientThread != null)
		{
			this.clientThread.close();
		}
		else
		{
			this.serverThread.close();
		}
		
		this.connectionThread.close();
		
		stopDiscovery();
		
		if (this.receiver != null)
		{
			this.context.unregisterReceiver(this.receiver);
		}
	}
	
	public void send(byte[] message)
	{
		this.connectionThread.send(message);
	}
}