package com.mauriciotogneri.bluetooth.test.connection;

import java.util.ArrayList;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.mauriciotogneri.bluetooth.connection.ConnectionInterface;
import com.mauriciotogneri.bluetooth.connection.ConnectionManager;
import com.mauriciotogneri.bluetooth.test.R;

public class StartServer extends Activity implements ConnectionInterface
{
	private ConnectionManager connectionManager;
	private DeviceAdapter deviceAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_server);
		
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
		
		Button disconnect = (Button)findViewById(R.id.disconnect);
		disconnect.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				disconnect();
			}
		});
		
		this.connectionManager = new ConnectionManager(this, this);
		this.connectionManager.startServer(TestConnection.UUID, 60);
	}
	
	private void deviceSelected(BluetoothDevice device)
	{
		// TODO
		Toast.makeText(this, device.getAddress(), Toast.LENGTH_SHORT).show();
	}
	
	private void disconnect()
	{
		this.connectionManager.close();
		finish();
	}
	
	@Override
	public void onBackPressed()
	{
		disconnect();
	}
	
	private void addHistory(final String text)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				TextView console = (TextView)findViewById(R.id.chat_history);
				console.append(text + "\r\n");
				
				ScrollView scroll = (ScrollView)findViewById(R.id.scroll);
				scroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}
	
	@Override
	public void onReceive(BluetoothDevice device, byte[] message)
	{
		addHistory("<<< " + device.getName() + " - " + device.getAddress() + ": " + new String(message));
	}
	
	private void addDevice(BluetoothDevice device)
	{
		this.deviceAdapter.add(device);
	}
	
	private void removeDevice(BluetoothDevice device)
	{
		this.deviceAdapter.remove(device);
	}
	
	@Override
	public void onConnect(final BluetoothDevice device)
	{
		addHistory("Device connected: " + device.getName() + " - " + device.getAddress());
		
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				addDevice(device);
			}
		});
	}
	
	@Override
	public void onDisconnect(final BluetoothDevice device)
	{
		addHistory("Device disconnected: " + device.getName() + " - " + device.getAddress());
		
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				removeDevice(device);
			}
		});
	}
}