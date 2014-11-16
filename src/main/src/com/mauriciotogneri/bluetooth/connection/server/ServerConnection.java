package com.mauriciotogneri.bluetooth.connection.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import com.mauriciotogneri.bluetooth.connection.ConnectionEvent;
import com.mauriciotogneri.bluetooth.connection.ConnectionThread;

public class ServerConnection
{
	private final ConnectionEvent connectionEvent;
	private ServerThread serverThread;
	private ConnectionThread connectionThread;
	
	public ServerConnection(ConnectionEvent connectionEvent)
	{
		this.connectionEvent = connectionEvent;
	}
	
	public void start(Context context, String uuid, int duration)
	{
		if (this.serverThread == null)
		{
			makeVisible(context, duration);
			
			this.serverThread = new ServerThread(this, uuid);
			this.serverThread.start();
		}
	}
	
	public void connected(BluetoothSocket socket)
	{
		this.connectionEvent.onConnect(socket.getRemoteDevice());
		
		this.connectionThread = new ConnectionThread(socket, this.connectionEvent);
		this.connectionThread.start();
	}
	
	public void makeVisible(Context context, int duration)
	{
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
		context.startActivity(intent);
	}
	
	public void close()
	{
		if (this.serverThread != null)
		{
			this.serverThread.close();
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