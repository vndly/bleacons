package com.mauriciotogneri.bluetooth.connection;

import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class ConnectionManager implements ConnectionInterface
{
	private final BluetoothAdapter bluetoothAdapter;
	
	private LinkThread linkThread;
	
	private final ConnectionInterface connectionInterface;
	
	private final ConnectionThread connectionThread;
	
	private final Context context;
	private BroadcastReceiver receiver;
	
	public ConnectionManager(Context context, ConnectionInterface connectionInterface)
	{
		this.context = context;
		this.bluetoothAdapter = getBluetoothAdapter();
		this.connectionInterface = connectionInterface;
		this.connectionThread = new ConnectionThread();
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
	
	public void startServer(String uuid, int duration)
	{
		stopDiscovery();
		makeVisible(duration);
		
		this.linkThread = new ServerThread(this.bluetoothAdapter, uuid, this, this.connectionThread);
		this.linkThread.start();
	}
	
	public void startClient(BluetoothDevice device, String uuid)
	{
		stopDiscovery();
		
		this.linkThread = new ClientThread(device, uuid, this, this.connectionThread);
		this.linkThread.start();
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
				onDeviceDiscovered(device);
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
					onDeviceDiscovered(device);
				}
			}
		};
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.context.registerReceiver(this.receiver, filter);
		
		this.bluetoothAdapter.startDiscovery();
	}
	
	public void close()
	{
		if (this.linkThread != null)
		{
			this.linkThread.close();
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
	
	@Override
	public void onReceive(byte[] message)
	{
		this.connectionInterface.onReceive(message);
	}
	
	@Override
	public void onConnect()
	{
		this.connectionInterface.onConnect();
	}
	
	@Override
	public void onDisconnect()
	{
		this.connectionInterface.onDisconnect();
	}
	
	@Override
	public void onDeviceDiscovered(BluetoothDevice device)
	{
		this.connectionInterface.onDeviceDiscovered(device);
	}
}