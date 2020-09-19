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
public class SmartSouthSV601ConfigTask extends ScinanConfigDeviceTask implements UDPClient.UDPClientCallback2 {

    private Object mLock = new Object();
    private String mAPSSID, mAPPasswd;

    Thread sendUdpThread;
    InetAddress address;
    Random rand = new Random();
    StringBuffer ipData;
    String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private UDPClient mUDP;
    private UDPServer mUDPServer;

    //暂停发送密码标志位
    private volatile boolean stopSendPassword;

    //Airkiss变量
    char mRandomStr;
    StringBuffer sb;
    volatile boolean sbSended;
    AirKissEncoder airKissEncoder;


    //601原生变量
    StringBuffer[] packetData = new StringBuffer[3];
    StringBuffer[] seqData = new StringBuffer[3];
    String retryNumber[] = {"10", "10", "5"};
    int[] tempPacket = new int[256];
    int[] tempSeq = new int[256];
    int[] sonkey;
    int[] stable = new int[256];
    String rc4Key = "Key";

    public SmartSouthSV601ConfigTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
    }

    public SmartSouthSV601ConfigTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
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
        logT("new601 -> SmartSouthSV601ConfigTask, params is mAPSSID=" + mAPSSID + ",mAPPasswd=" + mAPPasswd);
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
                        logT("checkWifiConnected is false");
                        onUDPError();
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
        logT("onUDPError that pushlish error");
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
                logT("onUDPError in receive airkiss random");
            }

            @Override
            public void onUDPEnd(UDPData data) {
                logT("UDPServerRead in task:" + data);
                mHandler.sendEmptyMessage(1);
                logT("receive the UDP : " + data + ",and random str is " + mRandomStr);
                if (TextUtils.equals(data.getData(), String.valueOf(mRandomStr)) && !sbSended) {
                    logT("==begin to send smnt========");
                    sendSMTBroadcast();
                }
            }
        });
        mUDPServer.connect();

        RequestHelper.getInstance(mContext).getPushAddress(new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                logT("OnFetchDataSuccess and " + responseBody);
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
        mUDP = new UDPClient(mContext, getKeywords(), sb.toString());
        mUDP.setCallback2(SmartSouthSV601ConfigTask.this);
        mUDP.connect();
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

        if (sonkey == null) {
            sonkey = new int[256];
            KSA();
            PRGA();
        }
        for (int z = 0; z < 3; z++) {
            packetData[z] = new StringBuffer();
            if (z == 0)
                packetData[0].append("iot");
            else if (z == 1)
                packetData[1].append((char) mAPSSID.length()).append((char) mAPPasswd.length()).append(ipData.charAt(0)).append(ipData.charAt(1)).append(ipData.charAt(2)).append(ipData.charAt(3));
            else
                packetData[2].append(mAPSSID).append(mAPPasswd);
            char crcDdata = crc8_msb((char) 0x1D, packetData[z].length(), z);
            packetData[z].append(crcDdata);
            addSeqPacket(z);
            if (isCancelled())
                return;
        }

        for (int i = 0; i < 3; i++) {
            cmdCryption(i);
            for (int j = 0; j < Integer.valueOf(retryNumber[i]); j++) {
                for (int k = 0; k < packetData[i].length(); k++) {
                    AtomicReference<StringBuffer> sendPacketData = new AtomicReference<StringBuffer>(new StringBuffer());
                    AtomicReference<StringBuffer> sendPacketSeq = new AtomicReference<StringBuffer>(new StringBuffer());

                    for (int v = 0; v < tempPacket[k] + 1; v++) {
                        sendPacketData.get().append(AB.charAt(rand.nextInt(AB.length())));
                    }
                    for (int g = 0; g < (tempSeq[k] + 1 + 256); g++) {
                        sendPacketSeq.get().append(AB.charAt(rand.nextInt(AB.length())));
                    }

                    try {
                        DatagramSocket clientSocket = new DatagramSocket();
                        clientSocket.setBroadcast(true);
                        address = InetAddress.getByName("255.255.255.255");
                        DatagramPacket sendPacketSeqSocket = new DatagramPacket(sendPacketSeq.get().toString().getBytes(), sendPacketSeq.get().length(), address, 8300);
                        clientSocket.send(sendPacketSeqSocket);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        DatagramPacket sendPacketDataSocket = new DatagramPacket(sendPacketData.get().toString().getBytes(), sendPacketData.get().length(), address, 8300);
                        clientSocket.send(sendPacketDataSocket);
                        try {
                            Thread.sleep(10);
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
                    if (isCancelled())
                        return;
                }
            }
        }
    }

    char crc8_msb(char poly, int size, int cmdNum) {
        char crc = 0x00, tmp;
        int bit;
        int i = 0;
        while (size > 0) {
            crc ^= packetData[cmdNum].charAt(i);
            for (bit = 0; bit < 8; bit++) {
                if ((0x0ff & (crc & 0x80)) != 0x00) {
                    tmp = (char) (0x0ff & (crc << 1));
                    crc = (char) (tmp ^ poly);
                } else {
                    crc <<= 1;
                }
            }
            size--;
            i++;
        }
        return crc;
    }

    void cmdCryption(int cmdUum) {
        int i;
        for (i = 0; i < packetData[cmdUum].length(); i++) {
            tempPacket[i] = packetData[cmdUum].charAt(i) ^ sonkey[i];
            tempSeq[i] = seqData[cmdUum].charAt(i) ^ sonkey[0];
        }
        tempPacket[i] = '\n';
        tempSeq[i] = '\n';
    }

    void addSeqPacket(int cmdNum) {
        int i;
        char value;

        seqData[cmdNum] = new StringBuffer(packetData[cmdNum]);

        for (i = 0; i < seqData[cmdNum].length(); i++) {
            if (cmdNum == 0)
                value = (char) ((0x0ff & (i)));
            else if (cmdNum == 1)
                value = (char) ((0x0ff & (i << 1) | 0x01));
            else
                value = (char) ((0x0ff & (i << 2) | 0x02));
            seqData[cmdNum].setCharAt(i, value);
        }
    }

    void KSA() {
        int i, j = 0, temp;
        for (i = 0; i < 256; i++)
            stable[i] = i;
        for (i = 0; i < 256; i++) {
            j = (j + stable[i] + rc4Key.charAt(i % rc4Key.length())) % 256;
            temp = stable[i];
            stable[i] = stable[j];
            stable[j] = temp;
        }
    }

    void PRGA() {
        int m = 0, i = 0, j = 0, t, l, temp;
        l = 256;
        while (l > 0) {
            i = (i + 1) % 256;
            j = (j + stable[i]) % 256;
            temp = stable[i];
            stable[i] = stable[j];
            stable[j] = temp;
            t = (stable[j] + stable[i]) % 256;
            sonkey[m++] = stable[t];
            l--;
        }
    }
}
