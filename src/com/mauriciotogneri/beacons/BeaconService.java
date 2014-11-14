package com.mauriciotogneri.beacons;

import java.util.ArrayList;
import java.util.List;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BeaconService extends Service implements LeScanCallback
{
	private int scanFrequency;
	private List<BeaconFilter> filters;
	private List<BeaconListener> listeners;
	private BluetoothAdapter bluetoothAdapter;
	
	private boolean scanningActive = false;
	private final Handler handler = new Handler();
	private final List<Beacon> currentBeacons = new ArrayList<Beacon>();
	
	public void startListening(int scanFrequency, List<BeaconFilter> filters, List<BeaconListener> listeners)
	{
		this.scanFrequency = scanFrequency;
		this.filters = filters;
		this.listeners = listeners;
		
		BluetoothManager bluetoothManager = (BluetoothManager)getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
		this.bluetoothAdapter = bluetoothManager.getAdapter();
		
		this.scanningActive = true;
		this.bluetoothAdapter.startLeScan(this);
		
		startScanningCycle();
	}
	
	private void startScanningCycle()
	{
		if (this.scanningActive)
		{
			this.handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					finishScanningCycle();
					startScanningCycle();
				}
			}, this.scanFrequency);
		}
	}
	
	@Override
	public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
	{
		for (BeaconFilter filter : this.filters)
		{
			BeaconData data = filter.getBeaconData(scanRecord);
			
			if (data != null)
			{
				Beacon beacon = new Beacon(device.getAddress(), data, rssi);
				this.currentBeacons.add(beacon);
			}
		}
	}
	
	private void finishScanningCycle()
	{
		if (this.scanningActive)
		{
			Log.e("TEST", "FINISHED SCANNING CYCLE");
			
			for (BeaconListener listener : this.listeners)
			{
				listener.onReceive(this.currentBeacons);
			}
			
			this.currentBeacons.clear();
		}
	}
	
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
		
		this.scanningActive = false;
		this.bluetoothAdapter.stopLeScan(this);
		
		super.onDestroy();
	}
}