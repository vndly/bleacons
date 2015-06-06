package com.mauriciotogneri.bleacons.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.mauriciotogneri.bleacons.BeaconManager;
import com.mauriciotogneri.bleacons.Reading;
import com.mauriciotogneri.bleacons.UnsupportedBluetoothLeException;
import com.mauriciotogneri.bleacons.interfaces.BeaconListener;
import com.mauriciotogneri.bleacons.interfaces.BeaconManagerObserver;
import com.mauriciotogneri.bleacons.modes.ReadingModeContinuous;
import com.mauriciotogneri.bleacons.modes.ReadingMode;
import com.mauriciotogneri.bleacons.beacons.IBeacon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestBeacons extends Activity implements BeaconListener, BeaconManagerObserver
{
    private BeaconManager beaconManager;
    private BeaconAdapter beaconAdapter;
    private final List<Reading> readingList = new ArrayList<>();

    private static final int SCAN_FREQUENCY = 1000; // in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen_beacons);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        beaconAdapter = new BeaconAdapter(this, readingList);

        ListView listView = (ListView) findViewById(R.id.beacon_list);
        listView.setAdapter(beaconAdapter);

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

        ReadingMode mode = new ReadingModeContinuous(this, new IBeacon.Filter(), 100);

        beaconManager.setMode(mode);
    }

    @Override
    public void onDisconnected()
    {
        Toast.makeText(this, "Disconnected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceive(List<Reading> readings)
    {
        readingList.clear();
        readingList.addAll(readings);

        Collections.sort(readingList, new Comparator<Reading>()
        {
            @Override
            public int compare(Reading lhs, Reading rhs)
            {
                return lhs.rssi - rhs.rssi;
            }
        });

        beaconAdapter.notifyDataSetChanged();

        Log.e("TEST", "<<< RECEIVED " + readings.size());
    }
}