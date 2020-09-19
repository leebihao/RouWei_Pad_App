/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.protocol;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by lijunjie on 15/12/13.
 */
public class UDPClientRead extends Thread {

    private Context mContext;
    private String mKeywords;
    private int mPort;
    private UDPReadCallback mCallback;
    private UDPReadCallback2 mCallback2;
    private boolean isOne2One = true;

    public UDPClientRead(Context context, String keywords, int port, UDPReadCallback callback) {
        this(context, keywords, port, callback, true);
    }

    public UDPClientRead(Context context, String keywords, int port, UDPReadCallback2 callback) {
        this(context, keywords, port, callback, true);
    }

    public UDPClientRead(Context context, String keywords, int port, UDPReadCallback callback, boolean isOne2One) {
        mContext = context;
        mKeywords = keywords;
        mPort = port;
        mCallback = callback;
        this.isOne2One = isOne2One;
    }

    public UDPClientRead(Context context, String keywords, int port, UDPReadCallback2 callback, boolean isOne2One) {
        mContext = context;
        mKeywords = keywords;
        mPort = port;
        mCallback2 = callback;
        this.isOne2One = isOne2One;
    }

    @Override
    public void run() {
        DatagramSocket ds = null;
        try {
            log("================" + mPort);
            log("udp UDPClientRead start");
            ds = new DatagramSocket(null);
            ds.setReuseAddress(true);
            ds.setBroadcast(true);
            ds.bind(new InetSocketAddress(mPort));
            //ds = new DatagramSocket(mPort);
            while (true) {
                log("receive response from udp listening");
                byte[] b = new byte[1024];
                DatagramPacket dp = new DatagramPacket(b, b.length);
                ds.receive(dp);
                String deviceIP = dp.getAddress().getHostAddress();
                String wifiIP = AndroidUtil.getWifiIP(mContext);
                log("this device's ip is  " + deviceIP);
                log("my phone's wifi ip is " + wifiIP);
                if (TextUtils.isEmpty(wifiIP)) {
                    sleep(5000);
                    wifiIP = AndroidUtil.getWifiIP(mContext);
                    log("my phone's wifi ip is " + wifiIP);
                }
                String message = new String(dp.getData(), 0, dp.getLength(), "utf-8").trim();
                log("========================================");
                log(message);
                log("========================================");
                if (TextUtils.equals(deviceIP.trim(), wifiIP.trim())) {
                    log("receive message from myself,and ds port is " + dp.getPort());
                    continue;
                } else {
                    log("receive useful response from udp device, but not sure deviceId whether we wanted");
                    log("we wanted keywords is " + mKeywords);
                    if (message.contains(mKeywords)) {
                        log("sure!! this is my wanted device, deviceIP is " + deviceIP);
                        if (mCallback != null) {
                            mCallback.onEnd(new UDPData(dp.getAddress().getHostAddress(), mPort, message));
                        } else if (mCallback2 != null) {
                            mCallback2.onEnd(new UDPData(dp.getAddress().getHostAddress(), mPort, message));
                        }
                        if (isOne2One) {
                            sleep(100000);
                            break;
                        } else {
                            sleep(10);
                        }
                    } else {
                        log("what a big pity!! this is not my wanted device, continue listening...");
                    }
                }
            }
            if (ds.isConnected()) {
                ds.close();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
            if (mCallback != null) {
                mCallback.onError();
            } else if (mCallback2 != null) {
                mCallback2.onError();
            }

        } finally {
            log("udp UDPClientRead die");
            if (ds != null && ds.isConnected()) {
                ds.disconnect();
                ds = null;
            }
        }
    }

    private void log(String log) {
        if (mCallback2 != null) {
            mCallback2.onProgress(log);
        } else {
            LogUtil.t(log);
        }
    }
}
