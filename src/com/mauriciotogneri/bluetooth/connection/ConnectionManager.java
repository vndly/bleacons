package com.mauriciotogneri.bluetooth.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class ConnectionManager
{
	private final ClientThread clientThread;
	private final ServerThread serverThread;
	private final ConnectionThread connectionThread;
	
	public ConnectionManager(BluetoothDevice device, String uuid, ConnectionInterface connectionInterface)
	{
		this.connectionThread = new ConnectionThread();
		
		this.clientThread = new ClientThread(device, uuid, connectionInterface, this.connectionThread);
		this.serverThread = null;
	}
	
	public ConnectionManager(BluetoothAdapter bluetoothAdapter, String uuid, ConnectionInterface connectionInterface)
	{
		this.connectionThread = new ConnectionThread();
		
		this.serverThread = new ServerThread(bluetoothAdapter, uuid, connectionInterface, this.connectionThread);
		this.clientThread = null;
	}
	
	public void start()
	{
		if (this.clientThread != null)
		{
			this.clientThread.start();
		}
		else
		{
			this.serverThread.start();
		}
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
	}
	
	public void send(byte[] message)
	{
		this.connectionThread.send(message);
	}
}