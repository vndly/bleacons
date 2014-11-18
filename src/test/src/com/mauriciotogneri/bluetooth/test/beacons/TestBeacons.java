package com.mauriciotogneri.bluetooth.test.beacons;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.mauriciotogneri.bluetooth.beacons.Beacon;
import com.mauriciotogneri.bluetooth.beacons.BeaconListener;
import com.mauriciotogneri.bluetooth.beacons.BeaconManager;
import com.mauriciotogneri.bluetooth.beacons.UnsupportedBluetoothLeException;
import com.mauriciotogneri.bluetooth.test.R;
import com.mauriciotogneri.bluetooth.test.beacons.custom.IBeacon;

public class TestBeacons extends Activity implements BeaconListener
{
	private BeaconManager beaconManager;
	private BeaconAdapter beaconAdapter;
	private final List<IBeacon> beacons = new ArrayList<IBeacon>();
	
	private static final int SCAN_FREQUENCY = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listen_beacons);
		
		this.beaconAdapter = new BeaconAdapter(this, this.beacons);
		
		ListView listView = (ListView)findViewById(R.id.beacon_list);
		listView.setAdapter(this.beaconAdapter);
		
		// TODO: ACTIVATE BLUETOOTH
		
		try
		{
			this.beaconManager = new BeaconManager(this, TestBeacons.SCAN_FREQUENCY);
			this.beaconManager.addFilter(new IBeacon.Filter());
			this.beaconManager.addListener(this);
			this.beaconManager.start();
		}
		catch (UnsupportedBluetoothLeException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onReceive(List<Beacon> beacons)
	{
		for (Beacon beacon : beacons)
		{
			IBeacon ibeacon = (IBeacon)beacon;
			
			if (!beacons.contains(ibeacon))
			{
				beacons.add(ibeacon);
			}
		}
		
		this.beaconAdapter.notifyDataSetChanged();
		
		Log.e("TEST", "<<< RECEIVED " + beacons.size());
	}
	
	@Override
	public void onDestroy()
	{
		this.beaconManager.stop();
		
		super.onDestroy();
	}
}