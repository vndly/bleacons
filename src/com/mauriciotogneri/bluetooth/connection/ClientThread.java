package com.mauriciotogneri.bluetooth.connection;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ClientThread extends LinkThread
{
	private final ConnectionInterface connectionInterface;
	private final ConnectionThread connectionThread;
	private BluetoothSocket socket;
	
	public ClientThread(BluetoothDevice device, String uuid, ConnectionInterface connectionInterface, ConnectionThread connectionThread)
	{
		this.connectionInterface = connectionInterface;
		this.connectionThread = connectionThread;
		
		try
		{
			this.socket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			this.socket.connect();
			
			this.connectionInterface.onConnect();
			
			this.connectionThread.initialize(this.socket, this.connectionInterface);
		}
		catch (IOException connectException)
		{
			try
			{
				this.socket.close();
			}
			catch (IOException closeException)
			{
				closeException.printStackTrace();
			}
		}
	}
	
	@Override
	public void close()
	{
		try
		{
			this.socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}