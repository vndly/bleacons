package com.mauriciotogneri.bluetooth.test.connection;

import java.util.ArrayList;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.mauriciotogneri.bluetooth.connection.DeviceScanner;
import com.mauriciotogneri.bluetooth.connection.ScannerManager;
import com.mauriciotogneri.bluetooth.test.R;

public class StartClient extends Activity implements DeviceScanner
{
	private ScannerManager scannerManager;
	private DeviceAdapter deviceAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_client);
		
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
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
		
		Button scan = (Button)findViewById(R.id.scan);
		scan.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				scan();
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
		
		this.scannerManager = new ScannerManager(this, this);
		scan();
	}
	
	private void deviceSelected(BluetoothDevice device)
	{
		Intent intent = new Intent(this, ChatScreen.class);
		intent.putExtra(ChatScreen.PARAMETER_DEVICE, device);
		startActivity(intent);
		
		stopScanning();
	}
	
	private void scan()
	{
		this.scannerManager.scan();
		
		Button scan = (Button)findViewById(R.id.scan);
		scan.setEnabled(false);
		scan.setText(R.string.connection_button_scanning);
	}
	
	private void cancel()
	{
		stopScanning();
		finish();
	}
	
	private void stopScanning()
	{
		this.scannerManager.stop();
		
		Button scan = (Button)findViewById(R.id.scan);
		scan.setEnabled(true);
		scan.setText(R.string.connection_button_scan);
	}
	
	@Override
	public void onDeviceDiscovered(BluetoothDevice device)
	{
		this.deviceAdapter.add(device);
	}
}