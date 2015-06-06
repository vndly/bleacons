package com.mauriciotogneri.bleacons.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mauriciotogneri.bleacons.BeaconReading;
import com.mauriciotogneri.bleacons.beacons.IBeacon;

import java.util.List;

public class BeaconReadingAdapter extends ArrayAdapter<BeaconReading>
{
    private final LayoutInflater inflater;

    public BeaconReadingAdapter(Context context, List<BeaconReading> list)
    {
        super(context, R.layout.beacon_row, list);

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View originalView, ViewGroup parent)
    {
        View convertView = originalView;
        BeaconReading beaconReading = getItem(position);
        IBeacon beacon = (IBeacon) beaconReading.beacon;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.beacon_row, parent, false);
        }

        TextView address = (TextView) convertView.findViewById(R.id.beacon_address);
        address.setText(beacon.macAddress);

        TextView uuid = (TextView) convertView.findViewById(R.id.beacon_uuid);
        uuid.setText(beacon.uuid.toLowerCase());

        TextView major = (TextView) convertView.findViewById(R.id.beacon_major);
        major.setText(String.valueOf(beacon.major));

        TextView minor = (TextView) convertView.findViewById(R.id.beacon_minor);
        minor.setText(String.valueOf(beacon.minor));

        TextView rssi = (TextView) convertView.findViewById(R.id.beacon_rssi);
        rssi.setText(String.valueOf(beaconReading.rssi));

        TextView txPower = (TextView) convertView.findViewById(R.id.beacon_txpower);
        txPower.setText(String.valueOf(beacon.txPower));

        return convertView;
    }
}