package com.mauriciotogneri.bluetooth.test.connection;

import java.util.ArrayList;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.mauriciotogneri.bluetooth.test.R;

public class StartClient extends Activity
{
	private DeviceAdapter deviceAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_client);
		
		this.deviceAdapter = new DeviceAdapter(this, new ArrayList<BluetoothDevice>());
		
		ListView listView = (ListView)findViewById(R.id.device_list);
		listView.setAdapter(this.deviceAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				BluetoothDevice device = (BluetoothDevice)parent.getItemAtPosition(position);
				deviceSelected(device);
			}
		});
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				cancel();
			}
		});
	}
	
	private void deviceSelected(BluetoothDevice device)
	{
		
	}
	
	private void cancel()
	{
		finish();
	}
}