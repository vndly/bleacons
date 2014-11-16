package com.mauriciotogneri.bluetooth.connection.server;

import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class ServerThread extends Thread
{
	private final ServerConnection serverConnection;
	private final BluetoothServerSocket serverSocket;
	
	public ServerThread(ServerConnection serverConnection, String uuid)
	{
		this.serverConnection = serverConnection;
		
		this.serverSocket = createSocket(uuid);
	}
	
	private BluetoothServerSocket createSocket(String uuid)
	{
		BluetoothServerSocket result = null;
		
		try
		{
			BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			
			if (!bluetoothAdapter.isEnabled())
			{
				bluetoothAdapter.enable();
			}
			
			result = bluetoothAdapter.listenUsingRfcommWithServiceRecord(uuid, UUID.fromString(uuid));
		}
		catch (Exception e)
		{
			this.serverConnection.errorOpeningConnection(this);
		}
		
		return result;
	}
	
	@Override
	public void run()
	{
		try
		{
			@SuppressWarnings("resource")
			BluetoothSocket socket = this.serverSocket.accept();
			
			this.serverConnection.clientConnected(socket, this);
		}
		catch (Exception e)
		{
			this.serverConnection.errorOpeningConnection(this);
		}
		finally
		{
			close();
		}
	}
	
	public void close()
	{
		try
		{
			this.serverSocket.close();
		}
		catch (Exception e)
		{
		}
	}
}