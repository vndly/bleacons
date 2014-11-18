package com.mauriciotogneri.bluetooth.test.beacons;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mauriciotogneri.bluetooth.test.R;
import com.mauriciotogneri.bluetooth.test.beacons.custom.IBeacon;

public class BeaconAdapter extends ArrayAdapter<IBeacon>
{
	private final LayoutInflater inflater;
	
	public BeaconAdapter(Context context, List<IBeacon> list)
	{
		super(context, R.layout.beacon_row, list);
		
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View originalView, ViewGroup parent)
	{
		View convertView = originalView;
		IBeacon beacon = getItem(position);
		
		if (convertView == null)
		{
			convertView = this.inflater.inflate(R.layout.beacon_row, parent, false);
		}
		
		TextView address = (TextView)convertView.findViewById(R.id.beacon_address);
		address.setText(beacon.getMacAddress());
		
		return convertView;
	}
}