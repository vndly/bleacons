package com.mauriciotogneri.bluetooth.beacons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
	private final List<BeaconFilter> filters = Collections.synchronizedList(new ArrayList<BeaconFilter>());
	private final List<BeaconListener> listeners = Collections.synchronizedList(new ArrayList<BeaconListener>());
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
	}
	
	public void start()
	{
		Intent intent = new Intent(this.context, BeaconService.class);
		this.context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	public boolean isConnected()
	{
		return this.isConnected;
	}
	
	public void addFilter(BeaconFilter filter)
	{
		this.filters.add(filter);
	}
	
	public void removeFilter(BeaconFilter filter)
	{
		this.filters.remove(filter);
	}
	
	public void addListener(BeaconListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeListener(BeaconListener listener)
	{
		this.listeners.remove(listener);
	}
	
	public void stop()
	{
		this.context.unbindService(this.serviceConnection);
	}
	
	private void onConnected(IBinder service)
	{
		log("SERVICE CONNECTED");
		
		this.isConnected = true;
		
		BeaconBinder binder = (BeaconBinder)service;
		this.beaconService = binder.getService();
		
		this.beaconService.startListening(this.scanFrequency, this.filters, this.listeners);
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