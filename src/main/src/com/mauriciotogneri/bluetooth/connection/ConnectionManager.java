package com.mauriciotogneri.bluetooth.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

public class ConnectionManager implements ConnectionInterface
{
	private final BluetoothAdapter bluetoothAdapter;
	
	private LinkThread linkThread;
	
	private final ConnectionInterface connectionInterface;
	
	private final ConnectionThread connectionThread;
	
	private final Context context;
	
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
		makeVisible(duration);
		
		this.linkThread = new ServerThread(this.bluetoothAdapter, uuid, this, this.connectionThread);
		this.linkThread.start();
	}
	
	public void startClient(BluetoothDevice device, String uuid)
	{
		this.linkThread = new ClientThread(device, uuid, this, this.connectionThread);
		this.linkThread.start();
	}
	
	public void makeVisible(int duration)
	{
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
		this.context.startActivity(intent);
	}
	
	public void close()
	{
		if (this.linkThread != null)
		{
			this.linkThread.close();
		}
		
		this.connectionThread.close();
	}
	
	public void send(byte[] message)
	{
		this.connectionThread.send(message);
	}
	
	@Override
	public void onReceive(BluetoothDevice device, byte[] message)
	{
		this.connectionInterface.onReceive(device, message);
	}
	
	@Override
	public void onConnect(BluetoothDevice device)
	{
		this.connectionInterface.onConnect(device);
	}
	
	@Override
	public void onDisconnect(BluetoothDevice device)
	{
		this.connectionInterface.onDisconnect(device);
	}
}