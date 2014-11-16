package com.mauriciotogneri.bluetooth.connection.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.mauriciotogneri.bluetooth.connection.ConnectionThread;
import com.mauriciotogneri.bluetooth.connection.exceptions.ConnectionException;

public class ClientLink extends ConnectionThread
{
	private final ClientEvent clientEvent;
	
	public ClientLink(BluetoothSocket socket, ClientEvent clientEvent) throws ConnectionException
	{
		super(socket);
		
		this.clientEvent = clientEvent;
	}
	
	@Override
	protected void onReceive(BluetoothDevice device, byte[] message)
	{
		this.clientEvent.onReceive(message);
	}
	
	@Override
	protected void onDisconnect(BluetoothDevice device)
	{
		this.clientEvent.onDisconnect();
	}
}