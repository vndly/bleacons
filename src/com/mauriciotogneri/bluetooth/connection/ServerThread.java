package com.mauriciotogneri.bluetooth.connection;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class ServerThread extends LinkThread
{
	private final ConnectionInterface connectionInterface;
	private BluetoothServerSocket serverSocket;
	private final ConnectionThread connectionThread;
	
	public ServerThread(BluetoothAdapter bluetoothAdapter, String uuid, ConnectionInterface connectionInterface, ConnectionThread connectionThread)
	{
		this.connectionInterface = connectionInterface;
		this.connectionThread = connectionThread;
		
		try
		{
			this.serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("NAME", UUID.fromString(uuid));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			@SuppressWarnings("resource")
			BluetoothSocket socket = this.serverSocket.accept();
			
			if (socket != null)
			{
				BluetoothDevice device = socket.getRemoteDevice();
				
				this.connectionInterface.onConnect();
				
				this.connectionThread.initialize(socket, this.connectionInterface);
				
				this.serverSocket.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void close()
	{
		try
		{
			this.serverSocket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}