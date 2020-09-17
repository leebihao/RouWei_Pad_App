package com.scinan.sdk.bluetooth;

import android.content.Context;

/**
 * Created by lijunjie on 17/3/3.
 */

public class BluzDeviceManager {

    private static BluzDeviceManager sBLEInstance;
    private static BluzDeviceManager sSPPInstance;

    private BLEBluzDevice mBLEBluzDevice;
    private SPPBluzDevice mSPPBluzDevice;

    private BluzDeviceManager(Context context, InputDeviceExtra[] extra) {
        mBLEBluzDevice = (BLEBluzDevice) ConnectDeviceFactory.getDevice(context, ConnectDeviceFactory.BT_TYPE_BLE, extra);
    }

    private BluzDeviceManager(Context context, boolean a2dp, InputDeviceExtra[] extra) {
        mSPPBluzDevice = (SPPBluzDevice) ConnectDeviceFactory.getDevice(context, a2dp ? ConnectDeviceFactory.BT_TYPE_SPP_MIX_A2DP : ConnectDeviceFactory.BT_TYPE_SPP, extra);
    }

    public static BluzDeviceManager getBLEInstance(Context context, BLEInputDeviceExtra extra) {
        return getBLEInstance(context, new BLEInputDeviceExtra[] {extra});
    }

    public static BluzDeviceManager getBLEInstance(Context context, InputDeviceExtra[] extra) {
        if (sBLEInstance == null) {
            synchronized (BluzDeviceManager.class) {
                if (sBLEInstance == null) {
                    sBLEInstance = new BluzDeviceManager(context.getApplicationContext(), extra);
                }
            }
        }
        return sBLEInstance;
    }

    public static BluzDeviceManager getSPPInstance(Context context, boolean a2dp, SPPInputDeviceExtra extra) {
        return getSPPInstance(context, a2dp, new SPPInputDeviceExtra[] {extra});
    }

    public static BluzDeviceManager getSPPInstance(Context context, boolean a2dp, InputDeviceExtra[] extra) {
        if (sSPPInstance == null) {
            synchronized (BluzDeviceManager.class) {
                if (sSPPInstance == null) {
                    sSPPInstance = new BluzDeviceManager(context.getApplicationContext(), a2dp, extra);
                }
            }
        }
        return sSPPInstance;
    }

    public BLEBluzDevice getBLEDevice() {
        return mBLEBluzDevice;
    }

    public SPPBluzDevice getSPPDevice() {
        return mSPPBluzDevice;
    }
}
