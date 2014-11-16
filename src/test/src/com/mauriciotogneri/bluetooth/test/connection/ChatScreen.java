package com.mauriciotogneri.bluetooth.test.connection;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import com.mauriciotogneri.bluetooth.connection.client.ClientConnection;
import com.mauriciotogneri.bluetooth.connection.client.ClientEvent;
import com.mauriciotogneri.bluetooth.test.R;

public class ChatScreen extends Activity implements ClientEvent
{
	private ClientConnection clientConnection;
	public static final String PARAMETER_DEVICE = "device";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_screen);
		
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		ImageButton send = (ImageButton)findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				send();
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
		
		BluetoothDevice device = getIntent().getExtras().getParcelable(ChatScreen.PARAMETER_DEVICE);
		
		this.clientConnection = new ClientConnection(this);
		this.clientConnection.connect(device, TestConnection.UUID);
		
		TextView deviceName = (TextView)findViewById(R.id.device_name);
		deviceName.setText(this.clientConnection.getDeviceName() + " - " + this.clientConnection.getDeviceAddress());
		
		addHistory("Connecting...");
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
	
	private void disconnect()
	{
		this.clientConnection.close();
		finish();
	}
	
	@Override
	public void onBackPressed()
	{
		disconnect();
	}
	
	private void send()
	{
		EditText editText = (EditText)findViewById(R.id.message);
		String message = editText.getText().toString();
		
		this.clientConnection.send(message.getBytes());
		
		addHistory(">>> " + message);
		
		editText.setText("");
	}
	
	private void enableButtons(final boolean value)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				EditText editText = (EditText)findViewById(R.id.message);
				editText.setEnabled(value);
				
				ImageButton send = (ImageButton)findViewById(R.id.send);
				send.setEnabled(value);
				
				Button disconnect = (Button)findViewById(R.id.disconnect);
				disconnect.setText(R.string.connection_button_disconnect);
				disconnect.setEnabled(value);
			}
		});
	}
	
	@Override
	public void onReceive(byte[] message)
	{
		addHistory("<<< " + new String(message));
	}
	
	@Override
	public void onConnect()
	{
		addHistory("Connected...");
		
		enableButtons(true);
	}
	
	@Override
	public void onDisconnect()
	{
		addHistory("Disconnected...");
		
		enableButtons(false);
	}
	
	@Override
	public void onErrorConnecting()
	{
		addHistory("Error connecting...");
	}
}