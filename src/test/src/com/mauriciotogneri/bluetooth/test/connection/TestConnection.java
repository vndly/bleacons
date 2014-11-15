package com.mauriciotogneri.bluetooth.test.connection;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mauriciotogneri.bluetooth.connection.ConnectionInterface;
import com.mauriciotogneri.bluetooth.connection.ConnectionManager;
import com.mauriciotogneri.bluetooth.test.R;

public class TestConnection extends Activity implements ConnectionInterface
{
	private ConnectionManager connectionManager;
	private ArrayAdapter<BluetoothDevice> deviceAdapter = null;
	private final List<BluetoothDevice> devicesFound = new ArrayList<BluetoothDevice>();
	
	private static final String UUID = "a6989332-69a6-11e4-b116-123b93f75cba";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);
		
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Button search = (Button)findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				disableButtons();
				search();
			}
		});
		
		Button startServer = (Button)findViewById(R.id.start_server);
		startServer.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				disableButtons();
				startServer();
			}
		});
		
		Button sendMessage = (Button)findViewById(R.id.send);
		sendMessage.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				send();
			}
		});
		
		initializeAdapter();
	}
	
	private void disableButtons()
	{
		Button search = (Button)findViewById(R.id.search);
		search.setEnabled(false);
		
		Button startServer = (Button)findViewById(R.id.start_server);
		startServer.setEnabled(false);
	}
	
	private void initializeAdapter()
	{
		this.deviceAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1, android.R.id.text1, this.devicesFound);
		
		ListView listView = (ListView)findViewById(R.id.devices);
		listView.setAdapter(this.deviceAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				BluetoothDevice device = (BluetoothDevice)parent.getItemAtPosition(position);
				startClient(device);
			}
		});
		
		this.connectionManager = new ConnectionManager(this, this);
	}
	
	private void startClient(BluetoothDevice device)
	{
		this.connectionManager.startClient(device, TestConnection.UUID);
	}
	
	private void startServer()
	{
		this.connectionManager.startServer(TestConnection.UUID, 60);
	}
	
	private void search()
	{
		this.connectionManager.searchDevices(false);
	}
	
	private void addLog(String log)
	{
		TextView console = (TextView)findViewById(R.id.console);
		console.append(log + "\r\n");
		
		ScrollView scroll = (ScrollView)findViewById(R.id.scroll);
		scroll.fullScroll(View.FOCUS_DOWN);
	}
	
	// ==========================================================================
	
	private void send()
	{
		EditText messageInput = (EditText)findViewById(R.id.message);
		String message = messageInput.getText().toString();
		
		this.connectionManager.send(message.getBytes());
		addLog(">>> " + message);
		
		messageInput.setText("");
	}
	
	private void receive(BluetoothDevice device, byte[] message)
	{
		addLog("<<< " + new String(message));
	}
	
	private void connected(BluetoothDevice device)
	{
		addLog("Connection stablished...");
		
		Button sendMessage = (Button)findViewById(R.id.send);
		sendMessage.setEnabled(true);
	}
	
	private void disconnected(BluetoothDevice device)
	{
		addLog("Connection closed...");
		
		Button sendMessage = (Button)findViewById(R.id.send);
		sendMessage.setEnabled(false);
	}
	
	private void deviceDiscovered(BluetoothDevice device)
	{
		this.deviceAdapter.add(device);
		this.deviceAdapter.notifyDataSetChanged();
		
		addLog("NEW DEVICE: " + device.getName() + ": " + device.getAddress());
	}
	
	@Override
	public void onReceive(final BluetoothDevice device, final byte[] message)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				receive(device, message);
			}
		});
	}
	
	@Override
	public void onConnect(final BluetoothDevice device)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				connected(device);
			}
		});
	}
	
	@Override
	public void onDisconnect(final BluetoothDevice device)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				disconnected(device);
			}
		});
	}
	
	@Override
	public void onDeviceDiscovered(final BluetoothDevice device)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				deviceDiscovered(device);
			}
		});
	}
	
	@Override
	protected void onDestroy()
	{
		this.connectionManager.close();
		
		super.onDestroy();
	}
}