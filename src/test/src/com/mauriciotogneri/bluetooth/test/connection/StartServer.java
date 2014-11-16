package com.mauriciotogneri.bluetooth.test.connection;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mauriciotogneri.bluetooth.connection.server.ServerConnection;
import com.mauriciotogneri.bluetooth.connection.server.ServerEvent;
import com.mauriciotogneri.bluetooth.test.R;

public class StartServer extends Activity implements ServerEvent
{
	private ServerConnection serverConnection;
	private DeviceAdapter deviceAdapter;
	
	private static final int VISIBILITY_DURATION = 60;
	
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
		
		this.serverConnection = new ServerConnection(this, this);
		this.serverConnection.listen(TestConnection.UUID, StartServer.VISIBILITY_DURATION);
	}
	
	private void deviceSelected(final BluetoothDevice device)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_menu_send);
		builder.setTitle(R.string.connection_main_server_send_message_title);
		builder.setCancelable(true);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		final View layout = inflater.inflate(R.layout.dialog_send_message, null);
		builder.setView(layout);
		
		builder.setPositiveButton(R.string.connection_button_send, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				EditText editText = (EditText)layout.findViewById(R.id.message);
				
				send(device, editText.getText().toString());
			}
		});
		
		builder.setNegativeButton(R.string.connection_button_cancel, null);
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void send(BluetoothDevice device, String message)
	{
		this.serverConnection.send(device, message.getBytes());
		
		addHistory(">>> " + message);
	}
	
	private void disconnect()
	{
		this.serverConnection.close();
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
	
	@Override
	public void onErrorOpeningConnection()
	{
		addHistory("Error opening connection...");
	}
}