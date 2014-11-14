package com.mauriciotogneri.beacons;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

class BeaconService extends Service implements LeScanCallback
{
	private int scanFrequency;
	private List<BeaconFilter> filters;
	private List<BeaconListener> listeners;
	private BluetoothAdapter bluetoothAdapter;
	private boolean scanningActive = false;
	private final Handler handler = new Handler();
	private final Map<String, Beacon> currentBeacons = new HashMap<String, Beacon>();
	
	public void startListening(int scanFrequency, List<BeaconFilter> filters, List<BeaconListener> listeners)
	{
		this.scanFrequency = scanFrequency;
		this.filters = filters;
		this.listeners = listeners;
		
		this.scanningActive = true;
		
		BluetoothManager bluetoothManager = (BluetoothManager)getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
		this.bluetoothAdapter = bluetoothManager.getAdapter();
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
		String macAddress = device.getAddress();
		
		log("BEACON SCANNED: " + macAddress + ", RSSI: " + rssi + ", DATA LENGTH: " + scanRecord.length);
		
		for (BeaconFilter filter : this.filters)
		{
			BeaconData data = filter.getBeaconData(scanRecord);
			
			if (data != null)
			{
				Beacon beacon = new Beacon(macAddress, data, rssi);
				this.currentBeacons.put(macAddress, beacon);
			}
		}
	}
	
	private void finishScanningCycle()
	{
		if (this.scanningActive)
		{
			log("FINISHED SCANNING CYCLE");
			
			List<Beacon> list = new ArrayList<Beacon>();
			list.addAll(this.currentBeacons.values());
			
			for (BeaconListener listener : this.listeners)
			{
				listener.onReceive(list);
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
	public IBinder onBind(Intent intent)
	{
		return new BeaconBinder();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy()
	{
		this.scanningActive = false;
		
		if (this.bluetoothAdapter != null)
		{
			this.bluetoothAdapter.stopLeScan(this);
		}
		
		super.onDestroy();
	}
	
	private void log(String text)
	{
		Log.e("BEACONS_LOG", text);
	}
}