package com.mauriciotogneri.beacons;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BeaconService extends Service
{
	public class BeaconBinder extends Binder
	{
		public BeaconService getService()
		{
			return BeaconService.this;
		}
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		Log.e("TEST", "SERVICE CREATED");
	}
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return new BeaconBinder();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.e("TEST", "SERVICE STARTING");
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		Log.e("TEST", "SERVICE DESTROYED");
		
		super.onDestroy();
	}
}