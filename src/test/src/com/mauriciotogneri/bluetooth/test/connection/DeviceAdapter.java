package com.mauriciotogneri.bluetooth.test.connection;

import java.util.List;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mauriciotogneri.bluetooth.test.R;

public class DeviceAdapter extends ArrayAdapter<BluetoothDevice>
{
	private final LayoutInflater inflater;
	
	public DeviceAdapter(Context context, List<BluetoothDevice> list)
	{
		super(context, R.layout.device_row, list);
		
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View originalView, ViewGroup parent)
	{
		View convertView = originalView;
		BluetoothDevice device = getItem(position);
		
		if (convertView == null)
		{
			convertView = this.inflater.inflate(R.layout.device_row, parent, false);
		}
		
		TextView address = (TextView)convertView.findViewById(R.id.device_address);
		address.setText(device.getName() + " - " + device.getAddress());
		
		return convertView;
	}
}