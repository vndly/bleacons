package com.mauriciotogneri.bluetooth.connection.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import com.mauriciotogneri.bluetooth.connection.exceptions.ConnectionException;

public class ServerConnection
{
	private final ServerEvent serverEvent;
	private final Context context;
	private final Set<ServerThread> serverThreads = new HashSet<ServerThread>();
	private final Map<BluetoothDevice, ServerLink> connections = new HashMap<BluetoothDevice, ServerLink>();
	
	public ServerConnection(ServerEvent serverEvent, Context context)
	{
		this.serverEvent = serverEvent;
		this.context = context;
	}
	
	public void listen(String uuid, int numberOfConnections, int duration)
	{
		makeVisible(duration);
		
		for (int i = 0; i < numberOfConnections; i++)
		{
			ServerThread serverThread = new ServerThread(this, uuid);
			serverThread.start();
			
			this.serverThreads.add(serverThread);
		}
	}
	
	public void listen(String uuid, int duration)
	{
		listen(uuid, 1, duration);
	}
	
	public void makeVisible(int duration)
	{
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if ((bluetoothAdapter != null) && (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE))
		{
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
			this.context.startActivity(intent);
		}
	}
	
	void clientConnected(BluetoothSocket socket, ServerThread serverThread)
	{
		try
		{
			this.serverThreads.remove(serverThread);
			
			ServerLink serverLink = new ServerLink(socket, this.serverEvent);
			serverLink.start();
			
			BluetoothDevice device = socket.getRemoteDevice();
			
			this.connections.put(device, serverLink);
			
			this.serverEvent.onConnect(device);
		}
		catch (ConnectionException e)
		{
			this.serverEvent.onErrorOpeningConnection();
		}
	}
	
	void errorOpeningConnection(ServerThread serverThread)
	{
		this.serverThreads.remove(serverThread);
		
		this.serverEvent.onErrorOpeningConnection();
	}
	
	public boolean send(BluetoothDevice device, byte[] message)
	{
		boolean result = false;
		
		if (this.connections.containsKey(device))
		{
			ServerLink serverLink = this.connections.get(device);
			result = serverLink.send(message);
		}
		
		return result;
	}
	
	public void close()
	{
		for (ServerThread serverThread : this.serverThreads)
		{
			serverThread.close();
		}
		
		Collection<ServerLink> links = this.connections.values();
		
		for (ServerLink link : links)
		{
			link.close();
		}
		
		this.connections.clear();
	}
}