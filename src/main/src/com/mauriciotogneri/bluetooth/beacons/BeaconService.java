package com.mauriciotogneri.bluetooth.beacons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.TargetApi;
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

@TargetApi(18)
public class BeaconService extends Service implements LeScanCallback
{
	private int scanFrequency;
	private Object filtersLock;
	private List<BeaconFilter> filters;
	private Object listenersLock;
	private List<BeaconListener> listeners;
	private BluetoothAdapter bluetoothAdapter;
	private volatile boolean scanningActive = false;
	private final Handler handler = new Handler();
	private final Object currentBeaconsLock = new Object();
	private final Map<String, Beacon> currentBeacons = new HashMap<String, Beacon>();
	
	public void startListening(int scanFrequency, Object filtersLock, List<BeaconFilter> filters, Object listenersLock, List<BeaconListener> listeners)
	{
		this.scanFrequency = scanFrequency;
		
		this.filtersLock = filtersLock;
		this.filters = filters;
		
		this.listenersLock = listenersLock;
		this.listeners = listeners;
		
		this.scanningActive = true;
		
		BluetoothManager bluetoothManager = (BluetoothManager)getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
		this.bluetoothAdapter = bluetoothManager.getAdapter();
		this.bluetoothAdapter.startLeScan(this);
		
		startScanningCycle();
	}
	
	public void pause()
	{
		this.scanningActive = false;
	}
	
	public void resume()
	{
		this.scanningActive = true;
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
	public void onLeScan(BluetoothDevice device, int rssi, byte[] data)
	{
		String macAddress = device.getAddress();
		
		log("BEACON SCANNED: " + macAddress + ", RSSI: " + rssi + ", DATA LENGTH: " + Arrays.toString(data));
		
		synchronized (this.filtersLock)
		{
			for (BeaconFilter filter : this.filters)
			{
				Beacon beacon = filter.getBeacon(macAddress, rssi, data);
				
				if (beacon != null)
				{
					synchronized (this.currentBeaconsLock)
					{
						this.currentBeacons.put(macAddress, beacon);
					}
					
					break;
				}
			}
		}
	}
	
	private void finishScanningCycle()
	{
		if (this.scanningActive)
		{
			log("FINISHED SCANNING CYCLE");
			
			List<Beacon> beaconsList = new ArrayList<Beacon>();
			
			synchronized (this.currentBeaconsLock)
			{
				Collection<Beacon> beacons = this.currentBeacons.values();
				
				for (Beacon beacon : beacons)
				{
					beaconsList.add(beacon);
				}
				
				this.currentBeacons.clear();
			}
			
			synchronized (this.listenersLock)
			{
				for (BeaconListener listener : this.listeners)
				{
					listener.onReceive(beaconsList);
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
		log("SERVICE DESTROYED");
		
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