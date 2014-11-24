package com.mauriciotogneri.bluetooth.connection.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public abstract class ConnectionThread extends Thread
{
	private final BluetoothSocket socket;
	private final InputStream inputStream;
	private final OutputStream outputStream;
	private boolean connected = false;
	private volatile boolean lastMessageSent = true;
	
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
				
				try
				{
					byte[] message = Arrays.copyOfRange(buffer, 0, bytes);
					Log.e("TEST", "RECEIVED FROM: " + this.socket.getRemoteDevice().getAddress() + " = " + message[0]);
					onReceive(device, message);
				}
				catch (Exception e)
				{
					Log.e("TEST", "ERROR", e);
				}
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
	
	public boolean send(byte[] message, boolean force)
	{
		boolean result = false;
		
		if ((this.connected) && (force || this.lastMessageSent))
		{
			this.lastMessageSent = false;
			
			try
			{
				Log.e("TEST", "SENDING TO: " + this.socket.getRemoteDevice().getAddress() + " = " + message[0]);
				this.outputStream.write(message);
				this.outputStream.flush();
				Log.e("TEST", "SENT TO: " + this.socket.getRemoteDevice().getAddress() + " = " + message[0]);
				
				result = true;
			}
			catch (Exception e)
			{
			}
			
			this.lastMessageSent = true;
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