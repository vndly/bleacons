package com.mauriciotogneri.bluetooth.test.connection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.mauriciotogneri.bluetooth.test.R;

public class TestConnection extends Activity
{
	public static final String UUID = "a6989332-69a6-11e4-b116-123b93f75cba";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connection);
		
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