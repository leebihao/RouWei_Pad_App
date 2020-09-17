/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.mediatek.elian.ElianNative;
import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.interfaces.ConfigDeviceCallback;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;
import com.scinan.sdk.interfaces.WifiScanResultCallback;
import com.scinan.sdk.protocol.UDPClient;
import com.scinan.sdk.protocol.UDPData;

import java.util.List;

/**
 * Created by lijunjie on 15/12/11.
 */
public class SmartMediatek7681DeviceTask extends ScinanConfigDeviceTask implements UDPClient.UDPClientCallback2 {

    public static byte AuthModeOpen = 0x00;
    public static byte AuthModeShared = 0x01;
    public static byte AuthModeAutoSwitch = 0x02;
    public static byte AuthModeWPA = 0x03;
    public static byte AuthModeWPAPSK = 0x04;
    public static byte AuthModeWPANone = 0x05;
    public static byte AuthModeWPA2 = 0x06;
    public static byte AuthModeWPA2PSK = 0x07;
    public static byte AuthModeWPA1WPA2 = 0x08;
    public static byte AuthModeWPA1PSKWPA2PSK = 0x09;

    private String mDeviceSSID, mAPSSID, mAPPasswd, mAPNetworkId;
    private byte mAuthMode = -9;

    private UDPClient mUDP;

    private ElianNative mElianNative;

    private Object mLock = new Object();
    private Object mGettCurrentAPMetaLock = new Object();

    public SmartMediatek7681DeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
    }

    public SmartMediatek7681DeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
        super(context, scinanDevice, callback);
    }

    @Override
    public Void doInBackground(String... params) {
        ConnectWakeLock.acquireWakeLock(mContext);
        publishProgress(String.valueOf(STEP_START));
        mDeviceSSID = params[0];
        mAPSSID = params[1];
        mAPPasswd = params[2];
        mAPNetworkId = params[3];
        mElianNative = new ElianNative();
        logT("params is mDeviceSSID="+ mDeviceSSID + ",mAPSSID=" + mAPSSID + ",mAPPasswd=" + mAPPasswd + ",mAPNetworkId=" + mAPNetworkId + ",CompanyID=" + (mScinanConnectDevice == null ? "null" : mScinanConnectDevice.getCompanyId()));
        connect();
        if (!isCancelled()) {
            holdTask();
        }
        ConnectWakeLock.releaseWakeLock();
        return null;
    }

    private void holdTask() {
        synchronized (mLock) {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    private void connect() {
        while (!isCancelled()) {
            if (!getCurrentAPMeta()) {
                logT("get the ap meta fail, sleep 5s retry");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            } else {
                logT("get the ap meta ok");
                break;
            }
        }

        if (isCancelled()) {
            return;
        }
        logT("===begin to StartSmartConnection");
        mElianNative.InitSmartConnection(null, 1, 0);
        mElianNative.StartSmartConnection(mAPSSID, mAPPasswd, "", mAuthMode);

        mHandler.sendEmptyMessageDelayed(1, 5000);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mUDP = new UDPClient(mContext, getKeywords(), "SMNT");
                    mUDP.setCallback2(SmartMediatek7681DeviceTask.this);
                    mUDP.connect(5000);
                    break;
            }
        }
    };

    @Override
    public synchronized void finish() {
        logT("begin to finish the task================");
        if (mElianNative != null) {
            mElianNative.StopSmartConnection();
        }
        if (mUDP != null) {
            mUDP.disconnect();
        }
        cancel(true);
        getCurrentAPMetaFinish();
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }

    private void getCurrentAPMetaFinish() {
        synchronized (mGettCurrentAPMetaLock) {
            mGettCurrentAPMetaLock.notifyAll();
        }
    }

    private boolean getCurrentAPMeta() {
        WifiScanAgent.getInstance(mContext).startScan(new WifiScanResultCallback() {
            @Override
            public void onSuccess(List<ScanResult> all, List<ScanResult> filter) {
                logT("=====getCurrentAPMeta finish ok");
                for (ScanResult AccessPoint : all) {
                    if (AccessPoint.SSID.equals(mAPSSID)) {
                        boolean WpaPsk = AccessPoint.capabilities.contains("WPA-PSK");
                        boolean Wpa2Psk = AccessPoint.capabilities.contains("WPA2-PSK");
                        boolean Wpa = AccessPoint.capabilities.contains("WPA-EAP");
                        boolean Wpa2 = AccessPoint.capabilities.contains("WPA2-EAP");

                        if (AccessPoint.capabilities.contains("WEP")) {
                            mAuthMode = AuthModeOpen;
                            break;
                        }
                        if (WpaPsk && Wpa2Psk) {
                            mAuthMode = AuthModeWPA1PSKWPA2PSK;
                            break;
                        } else if (Wpa2Psk) {
                            mAuthMode = AuthModeWPA2PSK;
                            break;
                        } else if (WpaPsk) {
                            mAuthMode = AuthModeWPAPSK;
                            break;
                        }
                        if (Wpa && Wpa2) {
                            mAuthMode = AuthModeWPA1WPA2;
                            break;
                        } else if (Wpa2) {
                            mAuthMode = AuthModeWPA2;
                            break;
                        } else if (Wpa) {
                            mAuthMode = AuthModeWPA;
                            break;
                        }
                        mAuthMode = AuthModeOpen;
                    }
                }
                getCurrentAPMetaFinish();
            }

            @Override
            public void onFail(int errorCode) {
                logT("=====getCurrentAPMeta fail");
                mAuthMode = -9;
                getCurrentAPMetaFinish();
            }
        });

        synchronized (mGettCurrentAPMetaLock) {
            try {
                mGettCurrentAPMetaLock.wait();
            } catch (InterruptedException e) {
            }
        }
        return mAuthMode != -9;
    }

    @Override
    public void onUDPError() {
        publishProgress(String.valueOf(STEP_FAIL), "onUDPError");
    }

    @Override
    public void onUDPEnd(UDPData udpData) {
        try {
            String data = udpData.getData();
            logT("===onUDPEnd receive data=====" + data);
            mHardwareCmds.add(getHardwareCmd(data));
            publishProgress(String.valueOf(STEP_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUDPProgress(String progress) {
        logT(progress);
    }
}
