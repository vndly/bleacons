package com.mauriciotogneri.bluetooth.connection.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public abstract class ConnectionThread extends Thread
{
	private final BluetoothSocket socket;
	private final InputStream inputStream;
	private final OutputStream outputStream;
	private boolean connected = false;
	
	private static final int BUFFER_SIZE = 1024;
	
	public ConnectionThread(BluetoothSocket socket) throws IOException
	{
		this.socket = socket;
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
		this.connected = true;
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
		
		this.connected = false;
		onDisconnect(device);
	}
	
	protected abstract void onReceive(BluetoothDevice device, byte[] message);
	
	protected abstract void onDisconnect(BluetoothDevice device);
	
	public boolean send(byte[] message)
	{
		boolean result = false;
		
		if (this.connected)
		{
			try
			{
				this.outputStream.write(message);
				
				result = true;
			}
			catch (Exception e)
			{
			}
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