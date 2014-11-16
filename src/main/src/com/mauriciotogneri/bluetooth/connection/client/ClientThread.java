package com.mauriciotogneri.bluetooth.connection.client;

import java.util.UUID;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ClientThread extends Thread
{
	private final ClientConnection clientConnection;
	private final BluetoothSocket socket;
	
	public ClientThread(ClientConnection clientConnection, BluetoothDevice device, String uuid)
	{
		this.clientConnection = clientConnection;
		
		this.socket = createSocket(device, uuid);
	}
	
	private BluetoothSocket createSocket(BluetoothDevice device, String uuid)
	{
		BluetoothSocket result = null;
		
		try
		{
			result = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
		}
		catch (Exception e)
		{
			this.clientConnection.errorConnecting();
		}
		
		return result;
	}
	
	@Override
	public void run()
	{
		try
		{
			this.socket.connect();
			
			this.clientConnection.connected(this.socket);
		}
		catch (Exception connectException)
		{
			this.clientConnection.errorConnecting();
			
			try
			{
				this.socket.close();
			}
			catch (Exception closeException)
			{
			}
		}
	}
	
	public void close()
	{
		try
		{
			this.socket.close();
		}
		catch (Exception e)
		{
		}
	}
}