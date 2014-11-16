package com.mauriciotogneri.bluetooth.connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.mauriciotogneri.bluetooth.connection.exceptions.ConnectionException;

public abstract class ConnectionThread extends Thread
{
	private final BluetoothSocket socket;
	private final InputStream inputStream;
	private final OutputStream outputStream;
	
	private static final int BUFFER_SIZE = 1024;
	
	public ConnectionThread(BluetoothSocket socket) throws ConnectionException
	{
		this.socket = socket;
		this.inputStream = getInputStream(socket);
		this.outputStream = getOutputStream(socket);
	}
	
	private InputStream getInputStream(BluetoothSocket socket) throws ConnectionException
	{
		InputStream result = null;
		
		try
		{
			result = socket.getInputStream();
		}
		catch (Exception e)
		{
			throw new ConnectionException(e);
		}
		
		return result;
	}
	
	private OutputStream getOutputStream(BluetoothSocket socket) throws ConnectionException
	{
		OutputStream result = null;
		
		try
		{
			result = socket.getOutputStream();
		}
		catch (Exception e)
		{
			throw new ConnectionException(e);
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
				
				onReceive(device, Arrays.copyOfRange(buffer, 0, bytes));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				break;
			}
		}
		
		onDisconnect(device);
	}
	
	protected abstract void onReceive(BluetoothDevice device, byte[] message);
	
	protected abstract void onDisconnect(BluetoothDevice device);
	
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
		}
	}
}