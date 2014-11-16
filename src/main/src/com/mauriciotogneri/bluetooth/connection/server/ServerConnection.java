package com.mauriciotogneri.bluetooth.connection.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import com.mauriciotogneri.bluetooth.connection.exceptions.ConnectionException;

public class ServerConnection
{
	private final ServerEvent serverEvent;
	private final Context context;
	private ServerThread serverThread;
	private ServerLink serverLink;
	
	public ServerConnection(ServerEvent serverEvent, Context context)
	{
		this.serverEvent = serverEvent;
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
	
	void clientConnected(BluetoothSocket socket)
	{
		try
		{
			this.serverLink = new ServerLink(socket, this.serverEvent);
			this.serverLink.start();
			
			this.serverEvent.onConnect(socket.getRemoteDevice());
		}
		catch (ConnectionException e)
		{
			this.serverEvent.onErrorOpeningConnection();
		}
	}
	
	void errorOpeningConnection()
	{
		this.serverEvent.onErrorOpeningConnection();
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
		
		if (this.serverLink != null)
		{
			result = this.serverLink.send(message);
		}
		
		return result;
	}
	
	public void close()
	{
		if (this.serverThread != null)
		{
			this.serverThread.close();
		}
		
		if (this.serverLink != null)
		{
			this.serverLink.close();
		}
	}
}