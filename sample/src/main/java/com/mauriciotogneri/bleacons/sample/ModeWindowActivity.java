package com.mauriciotogneri.bleacons.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.mauriciotogneri.bleacons.beacons.IBeacon;
import com.mauriciotogneri.bleacons.kernel.BeaconManager;
import com.mauriciotogneri.bleacons.kernel.BeaconManager.BeaconManagerObserver;
import com.mauriciotogneri.bleacons.kernel.BeaconReading;
import com.mauriciotogneri.bleacons.kernel.UnsupportedBluetoothLeException;
import com.mauriciotogneri.bleacons.modes.ReadingModeWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Activity that starts a window based reading and displays the result of each window in a listview.
 */
public class ModeWindowActivity extends Activity implements ReadingModeWindow.Listener, BeaconManagerObserver
{
    private BeaconManager beaconManager;
    private BeaconReadingAdapter beaconReadingAdapter;
    private final List<BeaconReading> beaconReadingsList = new ArrayList<>();

    // maximum number of beacons cached by the reading mode
    private static final int MAX_CACHED_BEACONS = 100;

    // set to create windows of 1 second
    private static final int SCAN_FREQUENCY = 1000; // in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_window);

        // keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        beaconReadingAdapter = new BeaconReadingAdapter(this, beaconReadingsList);

        ListView listView = (ListView) findViewById(R.id.reading_list);
        listView.setAdapter(beaconReadingAdapter);

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

        // creates a window reading mode
        ReadingModeWindow.Builder builder = new ReadingModeWindow.Builder(MAX_CACHED_BEACONS, SCAN_FREQUENCY, ReadingModeWindow.READING_CALCULATOR_AVERAGE);

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
    public void onReceive(List<BeaconReading> beaconReadings)
    {
        // clears the list and add the new readings
        beaconReadingsList.clear();
        beaconReadingsList.addAll(beaconReadings);

        // sort the readings from closer to farthest
        Collections.sort(beaconReadingsList, new Comparator<BeaconReading>()
        {
            @Override
            public int compare(BeaconReading lhs, BeaconReading rhs)
            {
                return rhs.rssi - lhs.rssi;
            }
        });

        // notify that the list has changed
        beaconReadingAdapter.notifyDataSetChanged();
    }
}