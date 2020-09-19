/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk_ext.protocol;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk_ext.smartlink.UDPData;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Random;

/**
 * Created by lijunjie on 15/12/13.
 */
public class UDPClient implements UDPReadCallback {

    private Context mContext;
    private String mKeywords;
    private UDPClientRead mReadThread;
    private UDPClientCallback mCallback;
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
        mPortUDP = getRandomPort();
        mReadThread = new UDPClientRead(mContext, mKeywords, mPortUDP, this, isOne2One);
        mReadThread.start();
        mMultiBroadcastThread = new MultiBroadcastThread();
        mMultiBroadcastThread.start();
    }

    private int getRandomPort() {
        int port = 5000 + new Random().nextInt(60535);
        return port;
    }

    public void disconnect() {
        if (mReadThread != null) {
            mReadThread.interrupt();
            mReadThread = null;
        }

        if (mMultiBroadcastThread != null) {
            mMultiBroadcastThread.interrupt();
            mMultiBroadcastThread = null;
        }
    }

    private void sendMultiBroadcast() {
        String host = "255.255.255.255";
        String data = TextUtils.isEmpty(mBroadcastData) ? "S0000" : mBroadcastData;
        mCallback.onUDPLog("sendMultiBroadcast:" + data);
        try {
            InetAddress ip = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(data.getBytes(),
                    data.length(), ip, 4000);
            MulticastSocket ms = new MulticastSocket(mPortUDP);

            ms.setLoopbackMode(true);
            InetAddress group = InetAddress.getByName("224.5.0.7");
            ms.joinGroup(group);

            ms.send(packet);
            ms.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MultiBroadcastThread extends Thread {

        @Override
        public void run() {
            mCallback.onUDPLog("udp MultiBroadcastThread start");
            while (!isInterrupted()) {
                if (!isInited) {
                    isInited = true;
                    mFirstConfigTime = System.currentTimeMillis();
                }
                sendMultiBroadcast();
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    mCallback.onUDPLog("udp MultiBroadcastThread die");
                    break;
                }
            }
        }
    }

    public void setCallback(UDPClientCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onError() {
        mCallback.onUDPError();
        if (mMultiBroadcastThread != null)
            mMultiBroadcastThread.interrupt();
    }

    @Override
    public void onEnd(UDPData data) {
        mCallback.onUDPLog("send broadcast time is  = " + (System.currentTimeMillis() - mFirstConfigTime));
        mCallback.onUDPEnd(data);
        if (isOne2One) {
            if (mMultiBroadcastThread != null) {
                mMultiBroadcastThread.interrupt();
            }
        }
    }

    public interface UDPClientCallback {
        void onUDPError();
        void onUDPEnd(UDPData data);
        void onUDPLog(String log);
    }

    @Override
    public void onPortError(int port) {
        mReadThread.interrupt();
        mReadThread = new UDPClientRead(mContext, mKeywords, port, this);
        mReadThread.start();
    }
}
