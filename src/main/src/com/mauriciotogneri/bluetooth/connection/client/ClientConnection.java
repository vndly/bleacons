package com.mauriciotogneri.bluetooth.connection.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.mauriciotogneri.bluetooth.connection.ConnectionEvent;
import com.mauriciotogneri.bluetooth.connection.ConnectionThread;

public class ClientConnection
{
	private final ConnectionEvent connectionEvent;
	private ClientThread clientThread;
	private ConnectionThread connectionThread;
	
	public ClientConnection(ConnectionEvent connectionEvent)
	{
		this.connectionEvent = connectionEvent;
	}
	
	public void start(BluetoothDevice device, String uuid)
	{
		if (this.clientThread == null)
		{
			this.clientThread = new ClientThread(this, device, uuid);
			this.clientThread.start();
		}
	}
	
	public void connected(BluetoothSocket socket)
	{
		this.connectionEvent.onConnect(socket.getRemoteDevice());
		
		this.connectionThread = new ConnectionThread(socket, this.connectionEvent);
		this.connectionThread.start();
	}
	
	public void close()
	{
		if (this.clientThread != null)
		{
			this.clientThread.close();
		}
		
		if (this.connectionThread != null)
		{
			this.connectionThread.close();
		}
	}
	
	public boolean send(byte[] message)
	{
		boolean result = false;
		
		if (this.connectionThread != null)
		{
			result = this.connectionThread.send(message);
		}
		
		return result;
	}
}