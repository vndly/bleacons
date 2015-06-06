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

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button modeContinuous = (Button) findViewById(R.id.mode_continuous);
        modeContinuous.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                modeContinuous();
            }
        });

        Button modeWindow = (Button) findViewById(R.id.mode_window);
        modeWindow.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                modeWindow();
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

    private void modeContinuous()
    {
        Intent intent = new Intent(this, ModeContinuousActivity.class);
        startActivity(intent);
    }

    private void modeWindow()
    {
        Intent intent = new Intent(this, ModeWindowActivity.class);
        startActivity(intent);
    }
}