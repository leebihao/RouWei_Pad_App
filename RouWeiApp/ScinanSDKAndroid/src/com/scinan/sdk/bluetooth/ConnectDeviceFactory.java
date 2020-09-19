package com.scinan.sdk.bluetooth;

import android.content.Context;

/**
 * Created by lijunjie on 17/3/2.
 */

public class ConnectDeviceFactory {

    //BLE4.0蓝牙
    public static final int BT_TYPE_BLE                              = 1;
    //SPP传统传输蓝牙
    public static final int BT_TYPE_SPP                              = 2;
    //SPP传统传输蓝牙和A2DP传统立体声蓝牙混合
    public static final int BT_TYPE_SPP_MIX_A2DP                     = 3;


    public static IBluzDevice getDevice(Context context, int type, InputDeviceExtra[] extra) {
        IBluzDevice device = null;
        switch (type) {
            case BT_TYPE_BLE:
                device = new BLEBluzDevice(context, extra);
                break;
            case BT_TYPE_SPP:
                device = new SPPBluzDevice(context, false, extra);
                break;
            case BT_TYPE_SPP_MIX_A2DP:
                device = new SPPBluzDevice(context, true, extra);
                break;
        }
        return device;
    }

    public static class ConnectionState {
        public static final int A2DP_CONNECTED = 1;
        public static final int A2DP_CONNECTING = 2;
        public static final int A2DP_DISCONNECTED = 3;
        public static final int A2DP_FAILURE = 4;
        public static final int A2DP_PAIRING = 5;
        public static final int SPP_CONNECTED = 11;
        public static final int SPP_CONNECTING = 12;
        public static final int SPP_DISCONNECTED = 13;
        public static final int SPP_FAILURE = 14;

        public ConnectionState() {
        }
    }
}
