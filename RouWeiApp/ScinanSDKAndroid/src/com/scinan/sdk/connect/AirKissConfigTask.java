/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.interfaces.ConfigDeviceCallback;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;
import com.scinan.sdk.protocol.UDPClient;
import com.scinan.sdk.protocol.UDPData;
import com.scinan.sdk.protocol.UDPServer;
import com.scinan.sdk.volley.FetchDataCallback;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lijunjie on 16/1/20.
 */
public class AirKissConfigTask extends ScinanConfigDeviceTask implements UDPClient.UDPClientCallback2 {

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
    String ip;
    StringBuffer sb;

    volatile boolean sbSended;

    public AirKissConfigTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
    }

    public AirKissConfigTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
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
        ip = null;
        sb = new StringBuffer("SMNT_0");
        mRandomStr = AB.charAt(rand.nextInt(AB.length()));
        logT("AirKissConfigTask params is mAPSSID=" + mAPSSID + ",mAPPasswd=" + mAPPasswd + ", mRandomStr is " + mRandomStr);
        mHandler.sendEmptyMessage(0);
        holdTask();
        ConnectWakeLock.releaseWakeLock();
        return null;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (checkWifiConnected()) {
                        enableThread();
                    } else {
                        publishProgress();
                    }
                    break;
                case 1:
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

        mHandler.removeMessages(0);
        mHandler.removeMessages(1);
        synchronized (mLock) {
            mLock.notifyAll();
        }
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
            if (TextUtils.isEmpty(ip)) {
                logT("===onUDPEnd receive rubbish data, reason ip is null=====" + udpData);
                return;
            }
            if (TextUtils.equals(udpData.getIp(), ip)) {
                logT("===onUDPEnd receive data=====" + udpData);
                mHardwareCmds.add(getHardwareCmd(udpData.getData()));
                publishProgress(String.valueOf(STEP_SUCCESS));
            } else {
                logT("===onUDPEnd receive rubbish data, reason ip not match=====" + udpData.getData());
            }
        } catch (Exception e) {
            logE(e);
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
                logT("receive the UDP : " + data.toString() + ",and random str is " + mRandomStr);

                String random = data.getData();
                if (!TextUtils.isEmpty(random) && random.length() > 0) {
                    random = random.substring(0, 1);
                    logT("we cut random str finish, new random is " + random);
                } else {
                    random = "";
                }
                if (TextUtils.equals(random, String.valueOf(mRandomStr)) && !sbSended) {
                    mHandler.sendEmptyMessage(1);
                    logT("==begin to send smnt========");
                    sbSended = true;
                    ip = data.getIp();
                    mUDP = new UDPClient(mContext, getKeywords(), sb.toString(), false);
                    mUDP.setCallback2(AirKissConfigTask.this);
                    mUDP.connect();
                }
            }
        });
        mUDPServer.connect();

        RequestHelper.getInstance(mContext).getPushAddress(new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                sb = new StringBuffer();
                sb.append("SMNT_0");
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
                        for (int i = 0; i < 100; i++) {
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

                } catch (Exception e) {
                    e.printStackTrace();
                    sb = new StringBuffer();
                    sb.append("SMNT_0");
                }
            }
        });
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
}
