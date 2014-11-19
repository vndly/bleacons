package com.mauriciotogneri.bluetooth.beacons;

import java.util.ArrayList;
import java.util.List;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import com.mauriciotogneri.bluetooth.beacons.BeaconService.BeaconBinder;

public class BeaconManager
{
	private final Context context;
	private final Object filtersLock = new Object();
	private final List<BeaconFilter> filters = new ArrayList<BeaconFilter>();
	private final Object listenersLock = new Object();
	private final List<BeaconListener> listeners = new ArrayList<BeaconListener>();
	private final int scanFrequency;
	private boolean isConnected = false;
	private BeaconService beaconService;
	private final ServiceConnection serviceConnection;
	
	public BeaconManager(Context context, int scanFrequency) throws UnsupportedBluetoothLeException
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
		{
			throw new UnsupportedBluetoothLeException(Build.VERSION.SDK_INT);
		}
		
		this.context = context;
		this.scanFrequency = scanFrequency;
		
		this.serviceConnection = new ServiceConnection()
		{
			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				onConnected(service);
			}
			
			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				onDisconnected();
			}
		};
		
		enableBluetooth();
	}
	
	private void enableBluetooth()
	{
		BluetoothAdapter result = BluetoothAdapter.getDefaultAdapter();
		
		if (!result.isEnabled())
		{
			result.enable();
		}
	}
	
	public void start()
	{
		Intent intent = new Intent(this.context, BeaconService.class);
		this.context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	public void pause()
	{
		if (this.beaconService != null)
		{
			this.beaconService.pause();
		}
	}
	
	public void resume()
	{
		if (this.beaconService != null)
		{
			this.beaconService.resume();
		}
	}
	
	public boolean isConnected()
	{
		return this.isConnected;
	}
	
	public void stop()
	{
		try
		{
			this.context.unbindService(this.serviceConnection);
		}
		catch (Exception e)
		{
		}
	}
	
	public void addFilter(BeaconFilter filter)
	{
		synchronized (this.filtersLock)
		{
			this.filters.add(filter);
		}
	}
	
	public void removeFilter(BeaconFilter filter)
	{
		synchronized (this.filtersLock)
		{
			this.filters.remove(filter);
		}
	}
	
	public void addListener(BeaconListener listener)
	{
		synchronized (this.listenersLock)
		{
			this.listeners.add(listener);
		}
	}
	
	public void removeListener(BeaconListener listener)
	{
		synchronized (this.listenersLock)
		{
			this.listeners.remove(listener);
		}
	}
	
	private void onConnected(IBinder service)
	{
		log("SERVICE CONNECTED");
		
		this.isConnected = true;
		
		BeaconBinder binder = (BeaconBinder)service;
		this.beaconService = binder.getService();
		
		this.beaconService.startListening(this.scanFrequency, this.filtersLock, this.filters, this.listenersLock, this.listeners);
	}
	
	private void onDisconnected()
	{
		log("SERVICE DISCONNECTED");
		
		this.isConnected = false;
	}
	
	private void log(String text)
	{
		Log.e("BEACONS_LOG", text);
	}
}