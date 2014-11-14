package com.mauriciotogneri.beacons;

import java.util.ArrayList;
import java.util.List;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.mauriciotogneri.beacons.BeaconService.BeaconBinder;

public class BeaconManager
{
	private final Context context;
	private final List<Filter> filters = new ArrayList<Filter>();
	private final List<BeaconListener> beaconListeners = new ArrayList<BeaconListener>();
	
	private BeaconService beaconService;
	private final ServiceConnection serviceConnection;
	
	public BeaconManager(Context context)
	{
		this.context = context;
		
		// Intent intent = new Intent(context, BeaconService.class);
		// context.startService(intent);
		
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
			}
		};
		
		Intent intent = new Intent(context, BeaconService.class);
		context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void onConnected(IBinder service)
	{
		BeaconBinder binder = (BeaconBinder)service;
		BeaconManager.this.beaconService = binder.getService();
	}
	
	public void addFilter(Filter filter)
	{
		this.filters.add(filter);
	}
	
	public void addListener(BeaconListener listener)
	{
		this.beaconListeners.add(listener);
	}
	
	public void stop()
	{
		// Intent intent = new Intent(this.context, BeaconService.class);
		// this.context.stopService(intent);
		
		this.context.unbindService(this.serviceConnection);
	}
}