package com.mauriciotogneri.bluetooth.connection.client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.mauriciotogneri.bluetooth.connection.exceptions.ConnectionException;

public class ClientConnection implements ClientEvent
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
	
	void connected(BluetoothSocket socket)
	{
		try
		{
			this.clientLink = new ClientLink(socket, this);
			this.clientLink.start();
			
			onConnect();
		}
		catch (ConnectionException e)
		{
			onErrorConnecting();
		}
	}
	
	@Override
	public void onReceive(byte[] message)
	{
		this.clientEvent.onReceive(message);
	}
	
	@Override
	public void onConnect()
	{
		this.clientEvent.onConnect();
	}
	
	@Override
	public void onErrorConnecting()
	{
		this.clientEvent.onErrorConnecting();
	}
	
	@Override
	public void onDisconnect()
	{
		this.clientEvent.onDisconnect();
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