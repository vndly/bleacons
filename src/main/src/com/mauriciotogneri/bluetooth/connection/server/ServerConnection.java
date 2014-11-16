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
	private final Context context;
	private ServerThread serverThread;
	private ConnectionThread connectionThread;
	
	public ServerConnection(ConnectionEvent connectionEvent, Context context)
	{
		this.connectionEvent = connectionEvent;
		this.context = context;
	}
	
	public void listen(String uuid, int duration)
	{
		if (this.serverThread == null)
		{
			makeVisible(duration);
			
			this.serverThread = new ServerThread(this, uuid);
			this.serverThread.start();
		}
	}
	
	void connected(BluetoothSocket socket)
	{
		this.connectionEvent.onConnect(socket.getRemoteDevice());
		
		this.connectionThread = new ConnectionThread(socket, this.connectionEvent);
		this.connectionThread.start();
	}
	
	public void makeVisible(int duration)
	{
		Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
		this.context.startActivity(intent);
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
}