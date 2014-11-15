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
	private final List<BluetoothDevice> list;
	
	public DeviceAdapter(Context context, List<BluetoothDevice> list)
	{
		super(context, android.R.layout.simple_list_item_1, list);
		
		this.inflater = LayoutInflater.from(context);
		this.list = list;
	}
	
	public void addDevice(BluetoothDevice device)
	{
		this.list.add(device);
		notifyDataSetChanged();
	}
	
	public void removeDevice(BluetoothDevice device)
	{
		this.list.remove(device);
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View originalView, ViewGroup parent)
	{
		View convertView = originalView;
		BluetoothDevice device = getItem(position);
		
		if (convertView == null)
		{
			convertView = this.inflater.inflate(R.layout.device_row, null);
		}
		
		TextView address = (TextView)convertView.findViewById(R.id.device_address);
		address.setText(device.getAddress());
		
		return convertView;
	}
}