/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mediatek.elian.ElianNative;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.interfaces.ConfigDeviceCallback;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;
import com.scinan.sdk.interfaces.WifiScanResultCallback;
import com.scinan.sdk.protocol.UDPClient;
import com.scinan.sdk.protocol.UDPData;
import com.scinan.sdk.protocol.UDPServer;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lijunjie on 16/1/20.
 */
public class SouthSV601Mix7681MultiConfigTask extends ScinanConfigDeviceTask implements UDPClient.UDPClientCallback2 {

    private Object mLock = new Object();
    private String mAPSSID, mAPPasswd;

    Thread sendUdpThread;
    InetAddress address;
    Random rand = new Random();
    StringBuffer ipData;
    String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private UDPClient mUDP;
    private UDPServer mUDPServer;

    private boolean stopSendPassword;

    AirKissEncoder airKissEncoder;
    char mRandomStr;
    StringBuffer sb;

    volatile boolean sbSended;

    private ElianNative mElianNative;
    private byte mAuthMode = -9;
    private Object mGettCurrentAPMetaLock = new Object();

    //for timeout and callback immediately
//    private long timeout = 60000L;
    private long timeout = 120000L;
//    private OnHardwareConfigListener onHardwareConfigListener;
//
//    public interface OnHardwareConfigListener {
//        void onHardwareConfig(int type, HardwareCmd hardwareCmd);
//    }

    public SouthSV601Mix7681MultiConfigTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
    }

    public SouthSV601Mix7681MultiConfigTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
        super(context, scinanDevice, callback);
    }

    @Override
    public Void doInBackground(String... params) {
        ConnectWakeLock.acquireWakeLock(mContext);
        publishProgress(String.valueOf(STEP_START));
        mAPSSID = params[1];
        mAPPasswd = params[2];
        stopSendPassword = false;
        sbSended = false;
        sb = new StringBuffer("SMNT_0");
        mRandomStr = AB.charAt(rand.nextInt(AB.length()));
        mElianNative = new ElianNative();
        logT("SouthSV601Mix7681ConfigTask,params is mAPSSID=" + mAPSSID + ",mAPPasswd=" + mAPPasswd);
        mHandler.sendEmptyMessage(0);
        if(timeout > 0L) {
            mHandler.sendEmptyMessageDelayed(1, timeout);
        }
        start7681Config();
        if (!isCancelled()) {
            holdTask();
        }
        ConnectWakeLock.releaseWakeLock();
        return null;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

//    public OnHardwareConfigListener getOnHardwareConfigListener() {
//        return onHardwareConfigListener;
//    }
//
//    public void setOnHardwareConfigListener(OnHardwareConfigListener onHardwareConfigListener) {
//        this.onHardwareConfigListener = onHardwareConfigListener;
//    }

    void start7681Config() {
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
                            mAuthMode = SmartMediatek7681DeviceTask.AuthModeOpen;
                            break;
                        }
                        if (WpaPsk && Wpa2Psk) {
                            mAuthMode = SmartMediatek7681DeviceTask.AuthModeWPA1PSKWPA2PSK;
                            break;
                        } else if (Wpa2Psk) {
                            mAuthMode = SmartMediatek7681DeviceTask.AuthModeWPA2PSK;
                            break;
                        } else if (WpaPsk) {
                            mAuthMode = SmartMediatek7681DeviceTask.AuthModeWPAPSK;
                            break;
                        }
                        if (Wpa && Wpa2) {
                            mAuthMode = SmartMediatek7681DeviceTask.AuthModeWPA1WPA2;
                            break;
                        } else if (Wpa2) {
                            mAuthMode = SmartMediatek7681DeviceTask.AuthModeWPA2;
                            break;
                        } else if (Wpa) {
                            mAuthMode = SmartMediatek7681DeviceTask.AuthModeWPA;
                            break;
                        }
                        mAuthMode = SmartMediatek7681DeviceTask.AuthModeOpen;
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

    private void getCurrentAPMetaFinish() {
        synchronized (mGettCurrentAPMetaLock) {
            mGettCurrentAPMetaLock.notifyAll();
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (checkWifiConnected()) {
                        enableThread();
                    } else {
                        onUDPError();
                    }
                    break;
                case 1:
                    LogUtil.d("#LBH_REV# 是否收到1");
                    if (mHardwareCmds.size() > 0) {
                        if (mUDP != null)
                            mUDP.disconnect();
                        publishProgress(String.valueOf(STEP_SUCCESS));
                    }
                    logT("stop send password and ssid");
                    stopSendPassword = true;
                    break;
            }
        }
    };

    boolean checkWifiConnected() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getIpAddress() == 0)
            return false;
        savePhoneIp(wifiInfo.getIpAddress());
        return true;
    }

    void savePhoneIp(int ipAddress) {
        ipData = new StringBuffer();
        ipData.append((char) (ipAddress & 0xff));
        ipData.append((char) (ipAddress >> 8 & 0xff));
        ipData.append((char) (ipAddress >> 16 & 0xff));
        ipData.append((char) (ipAddress >> 24 & 0xff));
    }


    private void holdTask() {
        synchronized (mLock) {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void onUDPError() {
        publishProgress(String.valueOf(STEP_FAIL), "onUDPError");
    }

    @Override
    public void onUDPEnd(UDPData udpData) {
        try {
            logT("===onUDPEnd receive data=====" + udpData);
            HardwareCmd hardwareCmd = getHardwareCmd(udpData.getData());
            mHardwareCmds.add(hardwareCmd);
            if(onHardwareConfigListener != null){
                onHardwareConfigListener.onHardwareConfig(getTaskType(), hardwareCmd);
            }
            //don't stop in multi-mode
//            publishProgress(String.valueOf(STEP_SUCCESS));
//            mHandler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUDPProgress(String progress) {
        logT(progress);
    }

    public class sendUdpThread extends Thread {

        public void run() {
            while (!isCancelled() && !stopSendPassword) {
                try {
                    SendbroadCast();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void enableThread() {
        if (sendUdpThread == null) {
            sendUdpThread = new sendUdpThread();
            sendUdpThread.start();
        }

        mUDPServer = new UDPServer(10000);
        mUDPServer.setCallback(new UDPServer.UDPServerCallback() {
            @Override
            public void onUDPError() {

            }

            @Override
            public void onUDPEnd(UDPData data) {
//                stopSendPassword = true;
                logT("receive the UDP : " + data + ",and random str is " + mRandomStr);
                if (TextUtils.equals(data.getData(), String.valueOf(mRandomStr)) && !sbSended) {
                    logT("==begin to send smnt========");
                    //Don't send because SMNT may be sent by RequestHelper below
//                    sendSMTBroadcast();
                }
            }
        });
        mUDPServer.connect();

        RequestHelper.getInstance(mContext).getPushAddress(new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                sb = new StringBuffer();
                sb.append("SMNT_0");
                sendSMTBroadcast();
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                try {
                    JSONArray hosts = JSON.parseArray(responseBody);
                    sb = new StringBuffer();
                    sb.append("SMNT_");
                    if (hosts.size() <= 3) {
                        for (int i = 0; i < hosts.size(); i++) {
                            sb.append(hosts.getString(i));
                            if (i != hosts.size() - 1)
                                sb.append(",");
                        }
                    } else {
                        ArrayList<Integer> indexs = new ArrayList<Integer>();
                        for (int i = 0; i < 100; i ++) {
                            int index = new Random().nextInt(hosts.size());
                            if (!indexs.contains(index)) {
                                sb.append(hosts.getString(i));
                                if (indexs.size() != 2)
                                    sb.append(",");
                                indexs.add(index);
                            }
                            if (indexs.size() == 3) {
                                break;
                            }
                        }
                    }
                    sendSMTBroadcast();

                } catch (Exception e) {
                    e.printStackTrace();
                    sb = new StringBuffer();
                    sb.append("SMNT_0");
                    sendSMTBroadcast();
                }
            }
        });
    }

    void sendSMTBroadcast() {
        sbSended = true;
        if (mUDP != null)
            mUDP.disconnect();
        mUDP = new UDPClient(mContext, getKeywords(), sb.toString(), false);
        mUDP.setCallback2(SouthSV601Mix7681MultiConfigTask.this);
        mUDP.connect(5000);
    }

    public void SendbroadCast() {
        if (airKissEncoder == null) {
            airKissEncoder = new AirKissEncoder(mRandomStr, mAPSSID, mAPPasswd);
        }

        for (int i = 0; i < airKissEncoder.getEncodedData().length; i++) {
            AtomicReference<StringBuffer> sendPacketSeq = new AtomicReference<StringBuffer>(new StringBuffer());
            for (int j = 0; j < airKissEncoder.getEncodedData()[i]; j++) {
                sendPacketSeq.get().append(AB.charAt(rand.nextInt(AB.length())));
            }

            //如果需要暂停发送密码就退出发送
            if (stopSendPassword) {
                break;
            }

            try {
                DatagramSocket clientSocket = new DatagramSocket();
                clientSocket.setBroadcast(true);
                address = InetAddress.getByName("255.255.255.255");
                DatagramPacket sendPacketSeqSocket = new DatagramPacket(sendPacketSeq.get().toString().getBytes(), sendPacketSeq.get().toString().length(), address, 8300);
                clientSocket.send(sendPacketSeqSocket);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                clientSocket.close();
                if (isCancelled())
                    return;
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (isCancelled()) {
                break;
            }
        }
    }

    @Override
    public void finish() {
        logT("begin to finish the task================");
        cancel(true);
        if (sendUdpThread != null) {
            sendUdpThread.interrupt();
            sendUdpThread = null;
        }

        if (mUDP != null) {
            mUDP.disconnect();
        }

        if (mUDPServer != null) {
            mUDPServer.disconnect();
        }

        if (mElianNative != null) {
            mElianNative.StopSmartConnection();
        }

        getCurrentAPMetaFinish();

        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }
}
