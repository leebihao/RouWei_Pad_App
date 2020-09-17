package com.scinan.sdk.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * Created by lijunjie on 17/3/2.
 */

public class SPPBluzDevice extends BaseBluzDevice {

    boolean isA2dpNeed;
    SPPInputDeviceExtra[] mExtra;

    public SPPBluzDevice(Context context, boolean ad2p, InputDeviceExtra[] extra) {
        super(context);
        isA2dpNeed = ad2p;
        mExtra = (SPPInputDeviceExtra[]) extra;
    }

    @Override
    public boolean isReallyConnected(BluetoothDevice bluetoothDevice) {
        return isConnected(bluetoothDevice);
    }

    @Override
    public int getConnectDeviceType() {
        return isA2dpNeed ? ConnectDeviceFactory.BT_TYPE_SPP_MIX_A2DP : ConnectDeviceFactory.BT_TYPE_SPP;
    }
}
