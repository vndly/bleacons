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
import android.util.Log;

public class ServerConnection implements ServerEvent
{
	private ServerEvent serverEvent;
	private final Context context;
	private final Object serverThreadLock = new Object();
	private final Set<ServerThread> serverThreads = new HashSet<ServerThread>();
	private final Object connectionsLock = new Object();
	private final Map<String, ServerLink> connections = new HashMap<String, ServerLink>();
	
	public ServerConnection(ServerEvent serverEvent, Context context)
	{
		this.serverEvent = serverEvent;
		this.context = context;
	}
	
	public void setListener(ServerEvent serverEvent)
	{
		this.serverEvent = serverEvent;
	}
	
	public void listen(String uuid, int numberOfConnections, int duration)
	{
		makeVisible(duration);
		
		for (int i = 0; i < numberOfConnections; i++)
		{
			ServerThread serverThread = new ServerThread(this, uuid);
			serverThread.start();
			
			addServerThread(serverThread);
		}
	}
	
	public void listen(String uuid, int duration)
	{
		listen(uuid, 1, duration);
	}
	
	public void makeVisible(int duration)
	{
		if (!isDiscoverable())
		{
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
			this.context.startActivity(intent);
		}
	}
	
	public boolean isDiscoverable()
	{
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		return ((bluetoothAdapter != null) && (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE));
	}
	
	@Override
	public void onReceive(BluetoothDevice device, byte[] message)
	{
		Log.e("TEST", "RECEIVING: " + device.getAddress() + ": " + message.length);
		
		this.serverEvent.onReceive(device, message);
	}
	
	@Override
	public void onConnect(BluetoothDevice device)
	{
		this.serverEvent.onConnect(device);
	}
	
	@Override
	public void onErrorOpeningConnection()
	{
		this.serverEvent.onErrorOpeningConnection();
	}
	
	@Override
	public void onDisconnect(BluetoothDevice device)
	{
		removeConnection(device);
		this.serverEvent.onDisconnect(device);
	}
	
	public String getDeviceName()
	{
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		return (bluetoothAdapter == null) ? "" : bluetoothAdapter.getName();
	}
	
	public String getDeviceAddress()
	{
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		return (bluetoothAdapter == null) ? "" : bluetoothAdapter.getAddress();
	}
	
	void clientConnected(BluetoothSocket socket, ServerThread serverThread)
	{
		try
		{
			removeServerThread(serverThread);
			
			ServerLink serverLink = new ServerLink(socket, this);
			serverLink.start();
			
			BluetoothDevice device = socket.getRemoteDevice();
			
			addConnection(device.getAddress(), serverLink);
			
			onConnect(device);
		}
		catch (Exception e)
		{
			onErrorOpeningConnection();
		}
	}
	
	void errorOpeningConnection(ServerThread serverThread)
	{
		removeServerThread(serverThread);
		
		onErrorOpeningConnection();
	}
	
	public boolean send(String macAddress, byte[] message, boolean force)
	{
		boolean result = false;
		
		ServerLink serverLink = getConnection(macAddress);
		
		if (serverLink != null)
		{
			result = serverLink.send(message, force);
		}
		
		return result;
	}
	
	public boolean sendAll(String macAddress, byte[] message, boolean force)
	{
		boolean result = false;
		
		synchronized (this.connectionsLock)
		{
			Set<String> addresses = this.connections.keySet();
			
			for (String address : addresses)
			{
				if (!address.equals(macAddress))
				{
					result |= this.connections.get(address).send(message, force);
				}
			}
		}
		
		return result;
	}
	
	public boolean sendAll(byte[] message, boolean force)
	{
		return sendAll(null, message, force);
	}
	
	private void addServerThread(ServerThread serverThread)
	{
		synchronized (this.serverThreadLock)
		{
			this.serverThreads.add(serverThread);
		}
	}
	
	private void removeServerThread(ServerThread serverThread)
	{
		synchronized (this.serverThreadLock)
		{
			this.serverThreads.remove(serverThread);
		}
	}
	
	private void addConnection(String macAddress, ServerLink serverLink)
	{
		synchronized (this.connectionsLock)
		{
			this.connections.put(macAddress, serverLink);
		}
	}
	
	private void removeConnection(BluetoothDevice device)
	{
		synchronized (this.connectionsLock)
		{
			this.connections.remove(device);
		}
	}
	
	private ServerLink getConnection(String macAddress)
	{
		ServerLink result = null;
		
		synchronized (this.connectionsLock)
		{
			result = this.connections.get(macAddress);
		}
		
		return result;
	}
	
	public int getNumberOfConnections()
	{
		int result = 0;
		
		synchronized (this.connectionsLock)
		{
			result = this.connections.size();
		}
		
		return result;
	}
	
	public void close()
	{
		synchronized (this.serverThreadLock)
		{
			for (ServerThread serverThread : this.serverThreads)
			{
				serverThread.close();
			}
		}
		
		synchronized (this.connectionsLock)
		{
			Collection<ServerLink> links = this.connections.values();
			
			for (ServerLink link : links)
			{
				link.close();
			}
			
			this.connections.clear();
		}
	}
}