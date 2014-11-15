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
import com.mauriciotogneri.bluetooth.connection.ConnectionInterface;
import com.mauriciotogneri.bluetooth.connection.ConnectionManager;
import com.mauriciotogneri.bluetooth.test.R;

public class ChatScreen extends Activity implements ConnectionInterface
{
	private ConnectionManager connectionManager;
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
		
		this.connectionManager = new ConnectionManager(this, this);
		this.connectionManager.startClient(device, TestConnection.UUID);
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
		this.connectionManager.close();
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
		
		this.connectionManager.send(message.getBytes());
		
		addHistory(">>> " + message);
		
		editText.setText("");
	}
	
	@Override
	public void onReceive(BluetoothDevice device, byte[] message)
	{
		addHistory("<<< " + new String(message));
	}
	
	@Override
	public void onConnect(BluetoothDevice device)
	{
		addHistory("Connected...");
		
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				EditText editText = (EditText)findViewById(R.id.message);
				editText.setEnabled(true);
				
				ImageButton send = (ImageButton)findViewById(R.id.send);
				send.setEnabled(true);
				
				Button disconnect = (Button)findViewById(R.id.disconnect);
				disconnect.setEnabled(true);
			}
		});
	}
	
	@Override
	public void onDisconnect(BluetoothDevice device)
	{
		addHistory("Disconnected...");
	}
}