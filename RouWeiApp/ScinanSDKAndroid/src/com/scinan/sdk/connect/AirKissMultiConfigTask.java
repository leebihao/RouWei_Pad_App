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
import com.scinan.sdk.hardware.HardwareCmd;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lijunjie on 16/1/20.
 */
public class AirKissMultiConfigTask extends ScinanConfigDeviceTask {

    private Object mLock = new Object();
    private String mAPSSID, mAPPasswd;

    Thread sendUdpThread;
    InetAddress address;
    Random rand = new Random();
    StringBuffer ipData;
    String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    AirKissEncoder airKissEncoder;
    char mRandomStr;

    public AirKissMultiConfigTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
    }

    public AirKissMultiConfigTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
        super(context, scinanDevice, callback);
    }

    @Override
    public Void doInBackground(String... params) {
        ConnectWakeLock.acquireWakeLock(mContext);
        publishProgress(String.valueOf(STEP_START));
        mAPSSID = params[1];
        mAPPasswd = params[2];
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
                    enableThread();
                    break;
            }
        }
    };

    @Override
    public void finish() {
        logT("begin to finish the task================");
        cancel(true);
        if (sendUdpThread != null) {
            sendUdpThread.interrupt();
            sendUdpThread = null;
        }
        mHandler.removeMessages(0);
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

    public class sendUdpThread extends Thread {

        public void run() {
            while (!isCancelled()) {
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
            if (isCancelled()) {
                break;
            }

            try {
                DatagramSocket clientSocket = new DatagramSocket();
                clientSocket.setBroadcast(true);
                address = InetAddress.getByName("255.255.255.255");
                DatagramPacket sendPacketSeqSocket = new DatagramPacket(sendPacketSeq.get().toString().getBytes(), sendPacketSeq.get().toString().length(), address, 8300);
                clientSocket.send(sendPacketSeqSocket);
                logD("" + sendPacketSeqSocket.getData().length);
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
