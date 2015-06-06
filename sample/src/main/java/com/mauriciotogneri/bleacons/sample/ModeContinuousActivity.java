package com.mauriciotogneri.bleacons.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mauriciotogneri.bleacons.BeaconManager;
import com.mauriciotogneri.bleacons.BeaconManager.BeaconManagerObserver;
import com.mauriciotogneri.bleacons.Reading;
import com.mauriciotogneri.bleacons.UnsupportedBluetoothLeException;
import com.mauriciotogneri.bleacons.beacons.IBeacon;
import com.mauriciotogneri.bleacons.modes.ReadingMode;
import com.mauriciotogneri.bleacons.modes.ReadingModeContinuous;

public class ModeContinuousActivity extends Activity implements ReadingModeContinuous.Listener, BeaconManagerObserver
{
    private BeaconManager beaconManager;
    private TextView readingList;
    private ScrollView scrollView;
    private boolean autoScroll;

    private static final int MAX_CACHED_BEACONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_cotinuous);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        readingList = (TextView) findViewById(R.id.reading_list);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        CheckBox autoScrollButton = (CheckBox) findViewById(R.id.button_auto_scroll);
        autoScroll = autoScrollButton.isChecked();
        autoScrollButton.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                autoScroll = isChecked;
            }
        });

        try
        {
            beaconManager = new BeaconManager(this, this);
            beaconManager.connect();
        }
        catch (UnsupportedBluetoothLeException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        beaconManager.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        beaconManager.resume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        beaconManager.disconnect();
    }

    @Override
    public void onConnected()
    {
        Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();

        ReadingMode mode = new ReadingModeContinuous(this, new IBeacon.Filter(), MAX_CACHED_BEACONS);

        beaconManager.setMode(mode);
    }

    @Override
    public void onDisconnected()
    {
        Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(Reading reading)
    {
        IBeacon iBeacon = (IBeacon) reading.beacon;

        StringBuilder builder = new StringBuilder();
        builder.append("\n").append("\n");
        builder.append("MAC:      ").append(iBeacon.macAddress).append("\n");
        builder.append("UUID:     ").append(iBeacon.uuid).append("\n");
        builder.append("Major:    ").append(iBeacon.major).append("\n");
        builder.append("Minor:    ").append(iBeacon.minor).append("\n");
        builder.append("RSSI:     ").append(reading.rssi).append("\n");
        builder.append("Tx power: ").append(iBeacon.txPower);

        readingList.append(builder.toString());

        if (autoScroll)
        {
            scrollView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
    }
}