package com.scinan.sdk.bluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * Created by lijunjie on 17/4/26.
 */

public interface BLEOADCallback {
    void onProgress(BluetoothDevice bluetoothDevice, int progress);
    void onCancel(BluetoothDevice bluetoothDevice);
    void onSuccess(BluetoothDevice bluetoothDevice);
}
