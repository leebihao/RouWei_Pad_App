/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.interfaces.LANConnectCallback;
import com.scinan.sdk.protocol.TCPClient;
import com.scinan.sdk.protocol.UDPClient;
import com.scinan.sdk.protocol.UDPData;
import com.scinan.sdk.util.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijunjie on 15/12/13.
 */
public class LANConnection implements TCPClient.TCPClientCallback, UDPClient.UDPClientCallback {

    protected Context mContext;
    protected String mDeviceId;
    protected volatile String mDeviceIP;

    protected volatile TCPClient mTCP;
    private volatile UDPClient mUDP;
    private LANConnectCallback mCallback;

    protected volatile boolean isConnected = false;
    private volatile Timer mLanTimer;
    private TimerTask mReadTimeoutTimerTask;
    private TimerTask mHeartbeatTimerTask;
    private TimerTask mConnectTimeoutTimerTask;

    public LANConnection(Context context, String deviceId, LANConnectCallback callback) {
        mContext = context;
        mDeviceId = deviceId;
        mCallback = callback;
        mUDP = new UDPClient(context, deviceId, null);
        mUDP.setCallback(this);
        mLanTimer = new Timer();
    }

    public void openConnection() {
        cancelAllTasks();
        mConnectTimeoutTimerTask = getConnectionTimeoutTask();
        mLanTimer.schedule(mConnectTimeoutTimerTask, 10000);
        mUDP.connect();
    }

    public void openConnection(String ip) {
        synchronized (LANConnection.class) {
            mDeviceIP = ip;
            if (mTCP != null) {
                mTCP.disconnect();
            }
            cancelAllTasks();
            mConnectTimeoutTimerTask = getConnectionTimeoutTask();
            mLanTimer.schedule(mConnectTimeoutTimerTask, 10000);
            mTCP = new TCPClient(mDeviceIP, Constants.DEVICE_TCP_PORT);
            mTCP.setCallback(this);
            mTCP.connect();
        }
    }

    public void close() {
        if (mUDP != null) {
            mUDP.disconnect();
        }
        if (mTCP != null) {
            mTCP.disconnect();
        }
        cancelAllTasks();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void cmd(String msg) {
        LogUtil.i("$$$$$$$$$$send cmd is " + msg + ",and isConnected is " + isConnected);
        if (isConnected) {
            cancelReadTimeoutTask();
            mReadTimeoutTimerTask = getTimeoutTask();
            mLanTimer.schedule(mReadTimeoutTimerTask, 5000);
            mTCP.sendMsg(msg);
        }
    }

    private TimerTask getConnectionTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                LogUtil.e("connection tcp time out");
                close();
                mCallback.onError();
            }
        };
    }

    private TimerTask getTimeoutTask() {
        return new TimerTask() {
            @Override
            public void run() {
                LogUtil.e("read tcp time out");
                close();
                mCallback.onError();
            }
        };
    }

    protected TimerTask getHeartbeatTask() {
        return new TimerTask() {
            @Override
            public void run() {
                if (isConnected) {
                    LogUtil.d("##################send tcp heartbeat " + mDeviceId + ",and device ip is " + mDeviceIP);
                    cmd("/" + mDeviceId + "/SPOLL/1/1");
                }
            }
        };
    }

    protected boolean ignoreHeartbeat(String data) {
        if (TextUtils.isEmpty(data)) {
            return false;
        }
        return data.contains("SPOLL/1/");
    }

    @Override
    public void onTCPError() {
        LogUtil.e("===========tcp error");
        isConnected = false;
        mCallback.onError();
        close();
    }

    private void cancelAllTasks() {
        cancelConnectTimeoutTask();
        cancelHeartbeatTask();
        cancelReadTimeoutTask();
    }

    private void cancelConnectTimeoutTask() {
        if (mConnectTimeoutTimerTask != null) {
            mConnectTimeoutTimerTask.cancel();
            mLanTimer.purge();
            mConnectTimeoutTimerTask = null;
        }
    }

    private void cancelHeartbeatTask() {
        if (mHeartbeatTimerTask != null) {
            mHeartbeatTimerTask.cancel();
            mLanTimer.purge();
            mHeartbeatTimerTask = null;
        }
    }

    private void cancelReadTimeoutTask() {
        if (mReadTimeoutTimerTask != null) {
            mReadTimeoutTimerTask.cancel();
            mLanTimer.purge();
            mReadTimeoutTimerTask = null;
        }
    }

    @Override
    public void onTCPConnected() {
        if (!isConnected) {
            isConnected = true;
            mCallback.onConnected();
            LogUtil.d("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            cancelAllTasks();
            mHeartbeatTimerTask = getHeartbeatTask();
            mLanTimer.schedule(mHeartbeatTimerTask, 10000, 10000);
        }
    }

    @Override
    public void onTCPReveived(byte[] result) {
        String data = new String(result).trim();
        LogUtil.d("received the response from TCP:" + data + " and result.length is " + result.length);
        cancelReadTimeoutTask();
        if (TextUtils.isEmpty(data)) {
            mCallback.onError();
            close();
            return;
        }
        if (!ignoreHeartbeat(data)) {
            mCallback.onResponse(data);
        } else {
            LogUtil.d("heartbeat ignore callback!");
        }
    }

    @Override
    public void onUDPError() {
        LogUtil.d("===========udp error");
        isConnected = false;
        mCallback.onError();
        close();
    }

    @Override
    public void onUDPEnd(UDPData udpData) {
        LogUtil.d("=========udp end " + udpData + "///" + (mTCP == null));
        synchronized (LANConnection.class) {
            if (mTCP != null) {
                mTCP.disconnect();
            }
            mDeviceIP = udpData.getIp();
            mTCP = new TCPClient(mDeviceIP, Constants.DEVICE_TCP_PORT);
            mTCP.setCallback(this);
            mTCP.connect();
        }
    }
}
