package com.scinan.sdk.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lijunjie on 17/3/2.
 */

public abstract class BaseBluzDevice implements IBluzDevice {

    protected Context mContext;
    protected BluetoothAdapter mBluetoothAdapter;
    protected CopyOnWriteArrayList<OnDiscoveryListener> mOnDiscoveryListeners;
    protected CopyOnWriteArrayList<OnConnectionListener> mOnConnectionListeners;

    //已经连上的蓝牙列表
    protected CopyOnWriteArrayList<BluetoothDevice> mConnectedBluetoothDevice;
    //期望要连接的蓝牙列表
    protected CopyOnWriteArrayList<BluetoothDevice> mHopeConnectBluetoothDevice;

    protected BluetoothManager mBluetoothManager;

    public BaseBluzDevice(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mConnectedBluetoothDevice = new CopyOnWriteArrayList<BluetoothDevice>();
        mHopeConnectBluetoothDevice = new CopyOnWriteArrayList<BluetoothDevice>();
        mOnConnectionListeners = new CopyOnWriteArrayList<OnConnectionListener>();
        mOnDiscoveryListeners = new CopyOnWriteArrayList<OnDiscoveryListener>();
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    stopDiscovery();
                    mOnDiscoveryListener.onDiscoveryFinished();
                    break;
                default:
                    if (msg.obj instanceof BluetoothDevice) {
                        BluetoothDevice device = (BluetoothDevice) msg.obj;
                        if (!compareIntAndrDevice(msg.what, device)) {
                            LogUtil.e("rubbish message msg.what is " + msg.what + ", device mac is " + device.getAddress());
                            break;
                        }
                        if (!mConnectedBluetoothDevice.contains(device)) {
                            LogUtil.e("this device is not connected, timeout " + device.getAddress());
                            mConnectedBluetoothDevice.remove(device);
                            mOnConnectionListener.onError(device, ERROR_CONN_TIMEOUT);
                            mHopeConnectBluetoothDevice.remove(device);
                            clear(device);
                        } else if (!isReallyConnected(device)) {
                            LogUtil.e("this device is not connected really, timeout " + device.getAddress());
                            disconnect(device);
                            mOnConnectionListener.onError(device, ERROR_CONN_TIMEOUT);
                        }
                    }

            }
        }
    };

    protected void clear(BluetoothDevice bluetoothDevice) {
    }

    @Override
    public boolean checkEnable(Activity activity) {
        //检查是否需要发送打开蓝牙的dialog
        if (!isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public boolean isConnected(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            return false;
        }
        return mConnectedBluetoothDevice.contains(bluetoothDevice);
    }

    @Override
    public boolean enable() {
        return mBluetoothAdapter.enable();
    }

    @Override
    public boolean disable() {
        return mBluetoothAdapter.disable();
    }

    @Override
    public void startDiscovery(long timeoutTimeMillis) {
        //开始之前先停止扫描
        stopDiscovery();
        mHandler.sendEmptyMessageDelayed(0, timeoutTimeMillis);
    }

    @Override
    public void stopDiscovery() {
        mHandler.removeMessages(0);
    }

    public void addConnectedDevice(BluetoothDevice bluetoothDevice) {
        if (!mConnectedBluetoothDevice.contains(bluetoothDevice)) {
            mConnectedBluetoothDevice.add(bluetoothDevice);
        }
    }

    public void removeConnectedDevice(BluetoothDevice bluetoothDevice) {
        if (mConnectedBluetoothDevice.contains(bluetoothDevice)) {
            mConnectedBluetoothDevice.remove(bluetoothDevice);
        }
    }

    @Override
    public boolean isDiscoverying() {
        return mHandler.hasMessages(0);
    }

    @Override
    public void retry(BluetoothDevice bluetoothDevice) {
    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice) {
        connect(bluetoothDevice, 5000);
    }

    @Override
    public void connect(BluetoothDevice bluetoothDevice, long timeout) {
        LogUtil.d("begin to connect the device " + bluetoothDevice);
        //这里用了个讨巧的方法，将蓝牙mac地址后三个字节的十六进制转换为int，最为message的what
        mHandler.removeMessages(getIntfromDevice(bluetoothDevice));
        mHandler.sendMessageDelayed(mHandler.obtainMessage(getIntfromDevice(bluetoothDevice), bluetoothDevice), timeout);
        if (!mHopeConnectBluetoothDevice.contains(bluetoothDevice)) {
            mHopeConnectBluetoothDevice.add(bluetoothDevice);
        }
    }

    @Override
    public void connect(String address, long timeout) {
        connect(getRemoteDevice(address), timeout);
    }

    @Override
    public void connect(ArrayList<BluetoothDevice> bluetoothDevices) {
    }

    @Override
    public void disconnect(BluetoothDevice bluetoothDevice) {
        LogUtil.d("begin to disconnect the device " + bluetoothDevice);
        mHandler.removeMessages(getIntfromDevice(bluetoothDevice));
    }

    @Override
    public void disconnect() {
    }

    protected boolean checkInHopeList(BluetoothDevice bluetoothDevice) {
        boolean hope =  mHopeConnectBluetoothDevice.contains(bluetoothDevice);
        LogUtil.d("checkInHopeList bluetoothDevice is " + bluetoothDevice + ", hope is " + hope);
        return hope;
    }

    @Override
    public BluetoothDevice getConnectedDevice() {
        return null;
    }

    @Override
    public BluetoothDevice getConnectedA2dpDevice() {
        return null;
    }

    public OnConnectionListener mOnConnectionListener = new OnConnectionListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            if (!checkInHopeList(bluetoothDevice)) {
                return;
            }

            for (OnConnectionListener connectionListener : mOnConnectionListeners) {
                connectionListener.onConnected(bluetoothDevice);
            }
        }

        @Override
        public void onRetryConnecting(BluetoothDevice bluetoothDevice) {
            if (!checkInHopeList(bluetoothDevice)) {
                return;
            }

            for (OnConnectionListener connectionListener : mOnConnectionListeners) {
                connectionListener.onRetryConnecting(bluetoothDevice);
            }
        }

        @Override
        public void onDisconnected(BluetoothDevice bluetoothDevice) {
            if (!checkInHopeList(bluetoothDevice)) {
                return;
            }
            for (OnConnectionListener connectionListener : mOnConnectionListeners) {
                connectionListener.onDisconnected(bluetoothDevice);
            }
        }

        @Override
        public void onError(BluetoothDevice bluetoothDevice, int reason) {
            if (!checkInHopeList(bluetoothDevice)) {
                return;
            }
            for (OnConnectionListener connectionListener : mOnConnectionListeners) {
                connectionListener.onError(bluetoothDevice, reason);
            }
        }

        @Override
        public void onReceive(BluetoothDevice bluetoothDevice, int type, BluetoothGattCharacteristic characteristics, byte[] data) {
            if (!checkInHopeList(bluetoothDevice)) {
                return;
            }
            for (OnConnectionListener connectionListener : mOnConnectionListeners) {
                connectionListener.onReceive(bluetoothDevice, type, characteristics, data);
            }
        }
    };

    public OnDiscoveryListener mOnDiscoveryListener = new OnDiscoveryListener() {
        @Override
        public void onDiscoveryStarted() {
            for (OnDiscoveryListener discoveryListener: mOnDiscoveryListeners) {
                discoveryListener.onDiscoveryStarted();
            }
        }

        @Override
        public void onDiscoveryFinished() {
            for (OnDiscoveryListener discoveryListener: mOnDiscoveryListeners) {
                discoveryListener.onDiscoveryFinished();
            }
        }

        @Override
        public void onFound(ScanDeviceResult result) {
            for (OnDiscoveryListener discoveryListener: mOnDiscoveryListeners) {
                discoveryListener.onFound(result);
            }
        }
    };

    @Override
    public void registerOnConnectionListener(OnConnectionListener connectionListener) {
        if (!mOnConnectionListeners.contains(connectionListener)) {
            mOnConnectionListeners.add(connectionListener);
        }
    }

    @Override
    public void unRegisterOnConnectionListener(OnConnectionListener connectionListener) {
        if (mOnConnectionListeners.contains(connectionListener)) {
            mOnConnectionListeners.remove(connectionListener);
        }
    }

    @Override
    public void registerOnDiscoveryListener(OnDiscoveryListener discoveryListener) {
        if (!mOnDiscoveryListeners.contains(discoveryListener)) {
            mOnDiscoveryListeners.add(discoveryListener);
        }
    }

    @Override
    public void unRegisterOnDiscoveryListener(OnDiscoveryListener discoveryListener) {
        if (mOnDiscoveryListeners.contains(discoveryListener)) {
            mOnDiscoveryListeners.remove(discoveryListener);
        }
    }

    @Override
    public boolean write(BluetoothDevice bluetoothDevice, byte[] var1) throws Exception {
        return true;
    }

    @Override
    public BluetoothDevice getRemoteDevice(String address) {
        return mBluetoothAdapter.getRemoteDevice(address);
    }

    public abstract int getConnectDeviceType();

    @Override
    public void connect(String address) {
        connect(address, 5000);
    }

    @Override
    public void disconnect(String address) {
        disconnect(getRemoteDevice(address));
    }


    protected int getIntfromDevice(BluetoothDevice bluetoothDevice) {
        //mac地址的后三个字节的十六进制
        String hex = bluetoothDevice.getAddress().replace(":", "").substring(6).toUpperCase();
        return Integer.parseInt(hex, 16);
    }

    private boolean compareIntAndrDevice(int id, BluetoothDevice bluetoothDevice) {
        return id == getIntfromDevice(bluetoothDevice);
    }
}
