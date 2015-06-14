package com.mauriciotogneri.bleacons.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mauriciotogneri.bleacons.beacons.IBeacon;
import com.mauriciotogneri.bleacons.kernel.BeaconManager;
import com.mauriciotogneri.bleacons.kernel.BeaconManager.BeaconManagerObserver;
import com.mauriciotogneri.bleacons.kernel.BeaconReading;
import com.mauriciotogneri.bleacons.kernel.UnsupportedBluetoothLeException;
import com.mauriciotogneri.bleacons.modes.ReadingModeContinuous;

/**
 * Activity that displays continuously the beacon readings received.
 */
public class ModeContinuousActivity extends Activity implements ReadingModeContinuous.Listener, BeaconManagerObserver
{
    private BeaconManager beaconManager;
    private TextView readingList;
    private ScrollView scrollView;

    // true to autoscroll down, false otherwise
    private boolean autoScroll;

    // maximum number of beacons cached by the reading mode
    private static final int MAX_CACHED_BEACONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_cotinuous);

        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        readingList = (TextView) findViewById(R.id.reading_list);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        CheckBox autoScrollButton = (CheckBox) findViewById(R.id.button_auto_scroll);
        autoScroll = autoScrollButton.isChecked();

        // invert the autoscroll flag when clicking the checkbox
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
            // instantiate the beacon manager and connect to internal service
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

        // pauses the readings
        beaconManager.pause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // resumes the readings
        beaconManager.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // disconnects from the service
        beaconManager.disconnect();
    }

    @Override
    public void onConnected()
    {
        Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();

        // creates a continuous reading mode
        ReadingModeContinuous.Builder builder = new ReadingModeContinuous.Builder(MAX_CACHED_BEACONS);

        // set this class as listener
        builder.addListeners(this);

        // filter only iBeacons
        builder.addFilters(new IBeacon.Filter());

        // sets the reading mode
        beaconManager.setMode(builder.build());

        // starts the readings
        beaconManager.start();
    }

    @Override
    public void onDisconnected()
    {
        Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(BeaconReading beaconReading)
    {
        // cast the generic beacon from the reading to an IBeacon
        IBeacon iBeacon = (IBeacon) beaconReading.beacon;

        // creates the text to be shown
        StringBuilder builder = new StringBuilder();
        builder.append("\n").append("\n");
        builder.append("MAC:      ").append(iBeacon.macAddress).append("\n");
        builder.append("UUID:     ").append(iBeacon.uuid).append("\n");
        builder.append("Major:    ").append(iBeacon.major).append("\n");
        builder.append("Minor:    ").append(iBeacon.minor).append("\n");
        builder.append("RSSI:     ").append(beaconReading.rssi).append("\n");
        builder.append("Tx power: ").append(iBeacon.txPower);

        // appends the text to the end of the view
        readingList.append(builder.toString());

        // if autoscroll enabled, scroll down the view
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