package com.mauriciotogneri.bluetooth.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.mauriciotogneri.bluetooth.test.beacons.TestBeacons;
import com.mauriciotogneri.bluetooth.test.connection.StartClient;
import com.mauriciotogneri.bluetooth.test.connection.StartServer;

public class TestActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button listenBeacons = (Button)findViewById(R.id.listen_beacons);
		listenBeacons.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				listenBeacons();
			}
		});
		
		Button startClient = (Button)findViewById(R.id.start_client);
		startClient.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				startClient();
			}
		});
		
		Button startServer = (Button)findViewById(R.id.start_server);
		startServer.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				startServer();
			}
		});
	}
	
	private void listenBeacons()
	{
		Intent intent = new Intent(this, TestBeacons.class);
		startActivity(intent);
	}
	
	private void startClient()
	{
		Intent intent = new Intent(this, StartClient.class);
		startActivity(intent);
	}
	
	private void startServer()
	{
		Intent intent = new Intent(this, StartServer.class);
		startActivity(intent);
	}
}