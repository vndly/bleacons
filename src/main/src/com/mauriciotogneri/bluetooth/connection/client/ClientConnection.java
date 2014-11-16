package com.mauriciotogneri.bluetooth.connection.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.mauriciotogneri.bluetooth.connection.exceptions.ConnectionException;

public class ClientConnection
{
	private final ClientEvent clientEvent;
	private ClientThread clientThread;
	private ClientLink clientLink;
	
	public ClientConnection(ClientEvent clientEvent)
	{
		this.clientEvent = clientEvent;
	}
	
	public void connect(BluetoothDevice device, String uuid)
	{
		if (this.clientThread == null)
		{
			this.clientThread = new ClientThread(this, device, uuid);
			this.clientThread.start();
		}
	}
	
	void connected(BluetoothSocket socket)
	{
		try
		{
			this.clientLink = new ClientLink(socket, this.clientEvent);
			this.clientLink.start();
			
			this.clientEvent.onConnect();
		}
		catch (ConnectionException e)
		{
			this.clientEvent.onErrorConnecting();
		}
	}
	
	void errorConnecting()
	{
		this.clientEvent.onErrorConnecting();
	}
	
	public boolean send(byte[] message)
	{
		boolean result = false;
		
		if (this.clientLink != null)
		{
			result = this.clientLink.send(message);
		}
		
		return result;
	}
	
	public void close()
	{
		if (this.clientThread != null)
		{
			this.clientThread.close();
		}
		
		if (this.clientLink != null)
		{
			this.clientLink.close();
		}
	}
}