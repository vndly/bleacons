package com.mauriciotogneri.bleacons.sample;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.mauriciotogneri.bleacons.sample.beacons.TestBeacons;

public class TestActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button listenBeacons = (Button) findViewById(R.id.listen_beacons);
        listenBeacons.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listenBeacons();
            }
        });

        enableBluetooth();
    }

    private void enableBluetooth()
    {
        BluetoothAdapter result = BluetoothAdapter.getDefaultAdapter();

        if (!result.isEnabled())
        {
            result.enable();
        }
    }

    private void listenBeacons()
    {
        Intent intent = new Intent(this, TestBeacons.class);
        startActivity(intent);
    }
}