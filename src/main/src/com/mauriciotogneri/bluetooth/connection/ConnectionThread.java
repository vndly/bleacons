package com.mauriciotogneri.bluetooth.connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ConnectionThread extends Thread
{
	private final BluetoothSocket socket;
	private final InputStream inputStream;
	private final OutputStream outputStream;
	private final ConnectionEvent connectionEvent;
	
	private static final int BUFFER_SIZE = 1024;
	
	public ConnectionThread(BluetoothSocket socket, ConnectionEvent connectionEvent)
	{
		this.socket = socket;
		this.inputStream = getInputStream(socket);
		this.outputStream = getOutputStream(socket);
		this.connectionEvent = connectionEvent;
	}
	
	private InputStream getInputStream(BluetoothSocket socket)
	{
		InputStream result = null;
		
		try
		{
			result = socket.getInputStream();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	private OutputStream getOutputStream(BluetoothSocket socket)
	{
		OutputStream result = null;
		
		try
		{
			result = socket.getOutputStream();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public void run()
	{
		byte[] buffer = new byte[ConnectionThread.BUFFER_SIZE];
		
		BluetoothDevice device = this.socket.getRemoteDevice();
		
		while (true)
		{
			try
			{
				int bytes = this.inputStream.read(buffer);
				
				this.connectionEvent.onReceive(device, Arrays.copyOfRange(buffer, 0, bytes));
			}
			catch (Exception e)
			{
				break;
			}
		}
		
		this.connectionEvent.onDisconnect(device);
	}
	
	public boolean send(byte[] bytes)
	{
		boolean result = false;
		
		try
		{
			this.outputStream.write(bytes);
			
			result = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void close()
	{
		try
		{
			this.socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}