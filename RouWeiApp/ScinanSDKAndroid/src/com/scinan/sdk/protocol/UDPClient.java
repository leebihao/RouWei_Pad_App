/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.protocol;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.util.LogUtil;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

/**
 * Created by lijunjie on 15/12/13.
 */
public class UDPClient implements UDPReadCallback2 {

    private Context mContext;
    private String mKeywords;
    private UDPClientRead mReadThread;
    private UDPClientCallback mCallback;
    private UDPClientCallback2 mCallback2;
    private MultiBroadcastThread mMultiBroadcastThread;
    private String mBroadcastData;
    private volatile long mFirstConfigTime;
    boolean isInited;
    private int mPortUDP;
    private boolean isOne2One = true;

    public UDPClient(Context context, String keywords, String broadcastData) {
        this(context, keywords, broadcastData, true);
    }

    public UDPClient(Context context, String keywords, String broadcastData, boolean isOne2One) {
        mContext = context;
        mKeywords = keywords;
        mBroadcastData = broadcastData;
        isInited = false;
        this.isOne2One = isOne2One;
    }

    public void connect() {
        connect(getRandomPort());
    }

    public void connect(int port){
        mPortUDP = port;
        mReadThread = new UDPClientRead(mContext, mKeywords, mPortUDP, this, isOne2One);
        mReadThread.start();
        mMultiBroadcastThread = new MultiBroadcastThread();
        mMultiBroadcastThread.start();
    }

    private int getRandomPort() {
        int port = 10001 + new Random().nextInt(25534);
        return port;
    }

    public boolean isConnect() {
        return mMultiBroadcastThread != null;
    }

    public void disconnect() {
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }

        if (mMultiBroadcastThread != null) {
            mMultiBroadcastThread.finish();
            mMultiBroadcastThread = null;
        }
    }

    private void sendMultiBroadcast() {
        String host = "255.255.255.255";
        String data = TextUtils.isEmpty(mBroadcastData) ? "S0000" : mBroadcastData;
        LogUtil.e("sendMultiBroadcast:" + data);
        if (mCallback2 != null) {
            mCallback2.onUDPProgress("sendMultiBroadcast:" + data);
        }
        try {
            InetAddress ip = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(data.getBytes(),
                    data.length(), ip, Constants.DEVICE_UDP_PORT);
            InetAddress group = InetAddress.getByName(Constants.DEVICE_UDP_GROUP_IP);
            //随机端口发送
            MulticastSocket ms1 = new MulticastSocket();
            ms1.setLoopbackMode(true);
            ms1.joinGroup(group);
            ms1.send(packet);
            ms1.close();
            //指定端口发送
            MulticastSocket ms2 = new MulticastSocket(mPortUDP);
            ms2.setLoopbackMode(true);
            ms2.joinGroup(group);
            ms2.send(packet);
            ms2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MultiBroadcastThread extends Thread {

        boolean isFinish = false;

        void finish() {
            isFinish = true;
        }
        @Override
        public void run() {
            log("udp MultiBroadcastThread start");
            while (!isFinish) {
                if (!isInited) {
                    isInited = true;
                    mFirstConfigTime = System.currentTimeMillis();
                }
                sendMultiBroadcast();
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    log("udp MultiBroadcastThread die");
                    break;
                }
            }
        }
    }

    public void setCallback(UDPClientCallback callback) {
        mCallback = callback;
    }

    public void setCallback2(UDPClientCallback2 callback) {
        mCallback2 = callback;
    }

    @Override
    public void onError() {
        if (mCallback != null) {
            mCallback.onUDPError();
        } else if (mCallback2 != null) {
            mCallback2.onUDPError();
        }
        if (mMultiBroadcastThread != null)
            mMultiBroadcastThread.interrupt();
    }

    @Override
    public void onEnd(UDPData data) {
        log("send broadcast time is  = " + (System.currentTimeMillis() - mFirstConfigTime));
        if (mCallback != null) {
            mCallback.onUDPEnd(data);
        } else if (mCallback2 != null) {
            mCallback2.onUDPEnd(data);
        }
        if (isOne2One) {
            if (mMultiBroadcastThread != null) {
                mMultiBroadcastThread.interrupt();
            }
        }
    }

    public interface UDPClientCallback {
        void onUDPError();
        void onUDPEnd(UDPData data);
    }

    public interface UDPClientCallback2 {
        void onUDPError();
        void onUDPEnd(UDPData data);
        void onUDPProgress(String progress);
    }

    @Override
    public void onPortError(int port) {
        mReadThread.interrupt();
        mReadThread = new UDPClientRead(mContext, mKeywords, port, this);
        mReadThread.start();
    }

    @Override
    public void onProgress(String progress) {
        if (mCallback2 != null) {
            mCallback2.onUDPProgress(progress);
        }
    }

    private void log(String log) {
        if (mCallback2 != null) {
            mCallback2.onUDPProgress(log);
        } else {
            LogUtil.t(log);
        }
    }
}
