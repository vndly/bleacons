package com.mauriciotogneri.bluetooth.connection.server;

import java.io.IOException;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.mauriciotogneri.bluetooth.connection.kernel.ConnectionThread;

public class ServerLink extends ConnectionThread
{
	private final ServerEvent serverEvent;
	
	public ServerLink(BluetoothSocket socket, ServerEvent serverEvent) throws IOException
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
		this.serverEvent.onDisconnect(device);
	}
}