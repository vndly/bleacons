package com.mauriciotogneri.bluetooth.test.beacons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
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
	private final List<IBeacon> beaconList = new ArrayList<IBeacon>();
	
	private static final int SCAN_FREQUENCY = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listen_beacons);
		
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		this.beaconAdapter = new BeaconAdapter(this, this.beaconList);
		
		ListView listView = (ListView)findViewById(R.id.beacon_list);
		listView.setAdapter(this.beaconAdapter);
		
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
		this.beaconList.clear();
		
		for (Beacon beacon : beacons)
		{
			IBeacon ibeacon = (IBeacon)beacon;
			this.beaconList.add(ibeacon);
		}
		
		Collections.sort(this.beaconList, new Comparator<IBeacon>()
		{
			@Override
			public int compare(IBeacon lhs, IBeacon rhs)
			{
				return lhs.getRssi() - rhs.getRssi();
			}
		});
		
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