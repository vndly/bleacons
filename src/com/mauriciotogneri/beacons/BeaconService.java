package com.mauriciotogneri.beacons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BeaconService extends Service
{
	private int scanFrequency;
	private List<BeaconFilter> filters;
	private List<BeaconListener> listeners;
	
	private BluetoothAdapter bluetoothAdapter;
	
	public void startListening(int scanFrequency, List<BeaconFilter> filters, List<BeaconListener> listeners)
	{
		this.scanFrequency = scanFrequency;
		this.filters = filters;
		this.listeners = listeners;
		
		BluetoothManager bluetoothManager = (BluetoothManager)getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
		this.bluetoothAdapter = bluetoothManager.getAdapter();
		
		this.bluetoothAdapter.startLeScan(new LeScanCallback()
		{
			@Override
			public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
			{
				processScan(device, rssi, scanRecord);
			}
		});
	}
	
	private void processScan(BluetoothDevice device, int rssi, byte[] scanRecord)
	{
		for (BeaconFilter filter : this.filters)
		{
			Map<String, String> data = filter.getBeaconData(scanRecord);
			
			if (data != null)
			{
				Beacon beacon = new Beacon(device.getAddress(), data, rssi);
				
				List<Beacon> list = new ArrayList<Beacon>();
				list.add(beacon);
				
				for (BeaconListener listener : this.listeners)
				{
					listener.onReceive(list);
				}
			}
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
		
		// TODO: STOP SCANNING
		
		super.onDestroy();
	}
}