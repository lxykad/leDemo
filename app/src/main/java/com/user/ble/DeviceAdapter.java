package com.user.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by user on 2016/5/10.
 */
public class DeviceAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<BluetoothDevice> mList = new ArrayList<>();

    public DeviceAdapter(Context context) {
        mContext = context;
    }

    public void addItems(ArrayList<BluetoothDevice> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearItems() {
        mList.clear();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public BluetoothDevice getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_device, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.address = (TextView) convertView.findViewById(R.id.address);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        BluetoothDevice device = mList.get(position);
        holder.name.setText(device.getName());
        holder.address.setText(device.getAddress());
        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView address;
    }
}
