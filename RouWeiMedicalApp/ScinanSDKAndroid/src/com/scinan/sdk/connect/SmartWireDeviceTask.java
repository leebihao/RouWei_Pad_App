/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.interfaces.ConfigDeviceCallback;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;
import com.scinan.sdk.protocol.UDPClient;
import com.scinan.sdk.protocol.UDPData;

/**
 * Created by lijunjie on 15/12/11.
 */
public class SmartWireDeviceTask extends ScinanConfigDeviceTask implements UDPClient.UDPClientCallback {

    private String mDeviceSSID, mAPSSID, mAPPasswd, mAPNetworkId;

    private UDPClient mUDP;

    private Object mLock = new Object();

    public SmartWireDeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
    }

    public SmartWireDeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
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
        logT("params is mDeviceSSID="+ mDeviceSSID + ",mAPSSID=" + mAPSSID + ",mAPPasswd=" + mAPPasswd + ",mAPNetworkId=" + mAPNetworkId + ",CompanyID=" + (mScinanConnectDevice == null ? "null" : mScinanConnectDevice.getCompanyId()));
        connect();
        holdTask();
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
        logT("===begin to StartSmartConnection");
        mHandler.sendEmptyMessageDelayed(1, 0);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mUDP = new UDPClient(mContext, getKeywords(), "SMNT");
                    mUDP.setCallback(SmartWireDeviceTask.this);
                    mUDP.connect();
                    break;
            }
        }
    };

    @Override
    public synchronized void finish() {
        logT("begin to finish the task================");
        if (mUDP != null) {
            mUDP.disconnect();
        }
        cancel(true);
        synchronized (mLock) {
            mLock.notifyAll();
        }
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
            mHardwareCmds.add(getHardwareCmd(udpData.getData()));
            publishProgress(String.valueOf(STEP_SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
