package com.scinan.sdk.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;

/**
 * Created by lijunjie on 17/3/2.
 */

public interface IBluzDevice {

    boolean isEnabled();

    boolean isConnected(BluetoothDevice bluetoothDevice);

    boolean enable();

    boolean disable();

    void startDiscovery(long timeoutTimeMillis);

    void stopDiscovery();

    boolean isDiscoverying();

    void retry(BluetoothDevice bluetoothDevice);

    void connect(BluetoothDevice bluetoothDevice);

    void connect(String address);

    void connect(String address, long timeout);

    void connect(BluetoothDevice bluetoothDevice, long timeout);

    void connect(ArrayList<BluetoothDevice> bluetoothDevices);

    void disconnect(BluetoothDevice bluetoothDevice);

    void disconnect(String address);

    void disconnect();

    BluetoothDevice getConnectedDevice();

    BluetoothDevice getConnectedA2dpDevice();

    void registerOnConnectionListener(OnConnectionListener connectionListener);

    void unRegisterOnConnectionListener(OnConnectionListener connectionListener);

    void registerOnDiscoveryListener(OnDiscoveryListener discoveryListener);

    void unRegisterOnDiscoveryListener(OnDiscoveryListener discoveryListener);

    boolean checkEnable(Activity activity);

    public interface OnConnectionListener {
        void onConnected(BluetoothDevice bluetoothDevice);

        void onDisconnected(BluetoothDevice bluetoothDevice);

        void onRetryConnecting(BluetoothDevice bluetoothDevice);

        void onError(BluetoothDevice bluetoothDevice, int reason);

        void onReceive(BluetoothDevice bluetoothDevice, int type, BluetoothGattCharacteristic characteristics, byte[] data);
    }

    public interface OnDiscoveryListener {
        void onDiscoveryStarted();

        void onDiscoveryFinished();

        void onFound(ScanDeviceResult result);
    }

    boolean write(BluetoothDevice bluetoothDevice, byte[] var1) throws Exception;

    boolean isReallyConnected(BluetoothDevice bluetoothDevice);

    BluetoothDevice getRemoteDevice(String address);

    public static final int REQUEST_ENABLE_BT = 0x81;

    public static final int ERROR_CONN_UNKNOWN                   = 1;
    public static final int ERROR_CONN_SERVICE_NOT_SUPPORT       = 2;
    public static final int ERROR_CONN_TIMEOUT                   = 3;
    public static final int ERROR_CONN_DIABLE                    = 4;
}
