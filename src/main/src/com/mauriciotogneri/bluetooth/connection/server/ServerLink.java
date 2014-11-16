package com.mauriciotogneri.bluetooth.connection.server;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.mauriciotogneri.bluetooth.connection.ConnectionThread;
import com.mauriciotogneri.bluetooth.connection.exceptions.ConnectionException;

public class ServerLink extends ConnectionThread
{
	private final ServerEvent serverEvent;
	
	public ServerLink(BluetoothSocket socket, ServerEvent serverEvent) throws ConnectionException
	{
		super(socket);
		
		this.serverEvent = serverEvent;
	}
	
	@Override
	protected void onReceive(BluetoothDevice device, byte[] message)
	{
		this.serverEvent.onReceive(device, message);
	}
	
	@Override
	protected void onDisconnect(BluetoothDevice device)
	{
		this.serverEvent.onClientDisconnect(device);
	}
}