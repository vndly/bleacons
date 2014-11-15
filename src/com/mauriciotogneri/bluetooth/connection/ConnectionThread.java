package com.mauriciotogneri.bluetooth.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectionThread extends Thread
{
	private BluetoothSocket socket;
	private BluetoothDevice device;
	private InputStream inputStream;
	private OutputStream outputStream;
	private ConnectionInterface connectionInterface;
	
	private static final int BUFFER_SIZE = 1024;
	
	public void initialize(BluetoothSocket socket, ConnectionInterface connectionInterface)
	{
		this.socket = socket;
		this.device = socket.getRemoteDevice();
		this.connectionInterface = connectionInterface;
		
		try
		{
			this.inputStream = socket.getInputStream();
			this.outputStream = socket.getOutputStream();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		start();
	}
	
	@Override
	public void run()
	{
		byte[] buffer = new byte[ConnectionThread.BUFFER_SIZE];
		
		while (true)
		{
			try
			{
				int bytes = this.inputStream.read(buffer);
				
				this.connectionInterface.onReceive(this.device, Arrays.copyOfRange(buffer, 0, bytes));
			}
			catch (IOException e)
			{
				break;
			}
		}
		
		this.connectionInterface.onDisconnect(this.device);
	}
	
	public boolean send(byte[] bytes)
	{
		boolean result = false;
		
		if (this.outputStream != null)
		{
			try
			{
				this.outputStream.write(bytes);
				
				result = true;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public void close()
	{
		if (this.socket != null)
		{
			try
			{
				this.socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}