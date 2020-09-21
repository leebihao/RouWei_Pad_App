package com.lbh.rouwei.common.adapter;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lbh.rouwei.R;
import com.lbh.rouwei.common.bean.SocketDevice;
import com.scinan.sdk.api.v2.bean.Device;

import java.util.List;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/21
 *     desc   :
 * </pre>
 */
public class DeviceListAdapter extends BaseAdapter {

    Context mContext;
    List<SocketDevice> mDeviceList;
    DeviceListCallback mDeviceListCallback;

    Handler mPostDelayHandler;

    public DeviceListAdapter(Context context, List<SocketDevice> list) {
        mContext = context;
        mDeviceList = list;
        mPostDelayHandler = new Handler();
    }

    @Override
    public int getCount() {
        return mDeviceList.size();
    }

    @Override
    public Device getItem(int position) {
        if (position >= getCount()) {
            return null;
        }
        return mDeviceList.get(position);
    }

    public SocketDevice getData(int position) {
        if (position >= getCount()) {
            return null;
        }
        return (SocketDevice)getItem(position);
    }

    public void setCallback(DeviceListCallback callback) {
        mDeviceListCallback = callback;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position >= getCount()) {
            return convertView;
        }

        SocketDevice socketDevice = mDeviceList.get(position);

        Holder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.item_deivcelist, null);
            holder = new Holder();
            holder.deviceItemLabelTitle = (TextView) convertView.findViewById(R.id.deviceItemLabelTitle);
            holder.deviceItemStatus = (TextView) convertView.findViewById(R.id.deviceItemStatus);
            holder.tv_devices_type = (TextView) convertView.findViewById(R.id.tv_devices_type);

            holder.deviceItemRootView = convertView.findViewById(R.id.deviceItemRootView);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.deviceItemRootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mDeviceListCallback != null)
                    mDeviceListCallback.OnLongClickListener(position);
                return true;
            }
        });

        holder.deviceItemRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDeviceListCallback != null)
                    mDeviceListCallback.OnItemClickListener(position);
            }
        });

        holder.deviceItemLabelTitle.setText(socketDevice.getTitle(mContext));
        holder.deviceItemStatus.setText(socketDevice.isOnline() ? "在线": "离线");
        holder.deviceItemStatus.setEnabled(socketDevice.isOnline());

        if (!TextUtils.isEmpty(socketDevice.getType())) {

            if (position == 0) {
                holder.tv_devices_type.setText(socketDevice.getType());
                holder.tv_devices_type.setVisibility(View.VISIBLE);
            } else if (socketDevice.getType().equals(mDeviceList.get(position - 1).getType())) {
                holder.tv_devices_type.setText("");
                holder.tv_devices_type.setVisibility(View.GONE);
            } else {
                holder.tv_devices_type.setText(socketDevice.getType());
                holder.tv_devices_type.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tv_devices_type.setText("");
            holder.tv_devices_type.setVisibility(View.GONE);
        }

        return convertView;
    }

    class Holder {
        TextView deviceItemLabelTitle, deviceItemStatus, tv_devices_type, tv_corner;
        View deviceItemRootView;
    }

    public interface DeviceListCallback {
        void OnSwitchChanged(int position, boolean checked);
        void OnLongClickListener(int position);
        void OnItemClickListener(int position);
    }
}
