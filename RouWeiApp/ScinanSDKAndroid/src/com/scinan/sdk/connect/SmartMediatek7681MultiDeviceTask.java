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
import com.scinan.sdk.contants.Constants;
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
 public class SmartMediatek7681MultiDeviceTask extends ScinanConfigDeviceTask implements UDPClient.UDPClientCallback2 {

    private byte AuthModeOpen = 0x00;
    private byte AuthModeShared = 0x01;
    private byte AuthModeAutoSwitch = 0x02;
    private byte AuthModeWPA = 0x03;
    private byte AuthModeWPAPSK = 0x04;
    private byte AuthModeWPANone = 0x05;
    private byte AuthModeWPA2 = 0x06;
    private byte AuthModeWPA2PSK = 0x07;
    private byte AuthModeWPA1WPA2 = 0x08;
    private byte AuthModeWPA1PSKWPA2PSK = 0x09;

    private String mDeviceSSID, mAPSSID, mAPPasswd, mAPNetworkId;
    private byte mAuthMode = -9;

    private volatile int mLeftRetryTimes = Constants.RETRY_TIMES_CONNECT;
    private volatile int mLastFail;
    private UDPClient mUDP;

    private ElianNative mElianNative;

    private Object mLock = new Object();
    private Object mGettCurrentAPMetaLock = new Object();

    public SmartMediatek7681MultiDeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
    }

    public SmartMediatek7681MultiDeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
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

    private synchronized void connect() {
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

        mHandler.sendEmptyMessageDelayed(1, 12000);
        mHandler.sendEmptyMessageDelayed(2, 32000);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (mUDP != null)
                        mUDP.disconnect();
                    mUDP = new UDPClient(mContext, getKeywords(), "SMNT", false);
                    mUDP.setCallback2(SmartMediatek7681MultiDeviceTask.this);
                    mUDP.connect(5000);
                    break;
                case 2:
                    if (mHardwareCmds.size() > 0) {
                        if (mUDP != null)
                            mUDP.disconnect();
                        publishProgress(String.valueOf(STEP_SUCCESS));
                    }
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
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
        cancel(true);
        getCurrentAPMetaFinish();
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }

    private void getCurrentAPMetaFinish() {
        logT("===========getCurrentAPMetaFinish");
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
                    logT("=========" + AccessPoint.SSID + "/" + AccessPoint.capabilities);
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

        logT("=========" + mAuthMode);
        return mAuthMode != -9;
    }

    @Override
    public void onUDPError() {
        publishProgress(String.valueOf(STEP_FAIL), "onUDPError");
    }

    @Override
    public void onUDPEnd(UDPData udpData) {
        logT("===onUDPEnd receive data=====" + udpData);
        String data = udpData.getData();
        if (TextUtils.isEmpty(data)) {
            return;
        }

        if (data.contains(getKeywords())) {
            logT("===onUDPEnd receive useful data=====" + data);
            try {
                mHardwareCmds.add(getHardwareCmd(data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUDPProgress(String progress) {
        logT(progress);
    }
}
