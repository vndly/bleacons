package com.mauriciotogneri.bleacons.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.mauriciotogneri.bleacons.BeaconManager;
import com.mauriciotogneri.bleacons.BeaconManager.BeaconManagerObserver;
import com.mauriciotogneri.bleacons.BeaconReading;
import com.mauriciotogneri.bleacons.UnsupportedBluetoothLeException;
import com.mauriciotogneri.bleacons.beacons.IBeacon;
import com.mauriciotogneri.bleacons.modes.ReadingModeWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModeWindowActivity extends Activity implements ReadingModeWindow.Listener, BeaconManagerObserver
{
    private BeaconManager beaconManager;
    private BeaconReadingAdapter beaconReadingAdapter;
    private final List<BeaconReading> beaconReadingsList = new ArrayList<>();

    private static final int MAX_CACHED_BEACONS = 100;
    private static final int SCAN_FREQUENCY = 1000; // in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_window);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        beaconReadingAdapter = new BeaconReadingAdapter(this, beaconReadingsList);

        ListView listView = (ListView) findViewById(R.id.reading_list);
        listView.setAdapter(beaconReadingAdapter);

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

        beaconManager.start();
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

        ReadingModeWindow.Builder builder = new ReadingModeWindow.Builder(MAX_CACHED_BEACONS, SCAN_FREQUENCY, ReadingModeWindow.READING_CALCULATOR_AVERAGE);
        builder.addListeners(this);
        builder.addFilters(new IBeacon.Filter());

        beaconManager.setMode(builder.build());
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
        beaconReadingsList.clear();
        beaconReadingsList.addAll(beaconReadings);

        Collections.sort(beaconReadingsList, new Comparator<BeaconReading>()
        {
            @Override
            public int compare(BeaconReading lhs, BeaconReading rhs)
            {
                return rhs.rssi - lhs.rssi;
            }
        });

        beaconReadingAdapter.notifyDataSetChanged();
    }
}