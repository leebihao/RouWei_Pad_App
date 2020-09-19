package com.scinan.sdk_ext.smartlink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.mediatek.elian.ElianNative;
import com.scinan.sdk_ext.protocol.UDPClient;
import com.scinan.sdk_ext.protocol.UDPServer;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by lijunjie on 16/1/20.
 */
public class ScinanConfigTask extends AsyncTask<String, String, Void> implements UDPClient.UDPClientCallback {

    private Context mContext;
    private ConfigDeviceCallback mSmartLinkCallback;

    private Object mLock = new Object();
    private String mAPSSID, mAPPasswd;

    private Thread sendUdpThread;
    private InetAddress address;
    private Random rand = new Random();
    private StringBuffer ipData;
    private String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private UDPClient mUDP;
    private UDPServer mUDPServer;

    private boolean stopSendPassword;

    private ArrayList<Integer> sendUDPData;
    private char mRandomStr;
    private StringBuffer sb;

    private volatile boolean sbSended;

    private ElianNative mElianNative;
    private byte mAuthMode = -9;
    private Object mGettCurrentAPMetaLock = new Object();
    private ScinanConfigExtra l;
    private WifiManager mWifiManager;
    private static final byte AuthModeOpen = 0x00;
    private static final byte AuthModeWPA = 0x03;
    private static final byte AuthModeWPAPSK = 0x04;
    private static final byte AuthModeWPA2 = 0x06;
    private static final byte AuthModeWPA2PSK = 0x07;
    private static final byte AuthModeWPA1WPA2 = 0x08;
    private static final byte AuthModeWPA1PSKWPA2PSK = 0x09;

    private AirKissEncoder airKissEncoder;

    public ScinanConfigTask(Context context, ConfigDeviceCallback callback, ScinanConfigExtra extra) {
        mContext = context.getApplicationContext();
        mSmartLinkCallback = callback;
        sendUDPData = new ArrayList<Integer>();
        mRandomStr = AB.charAt(rand.nextInt(AB.length()));
        l = extra;
    }

    @Override
    public Void doInBackground(String... params) {
        SmartLinkWakeLock.acquireWakeLock(mContext);
        mAPSSID = params[0];
        mAPPasswd = params[1];
        stopSendPassword = false;
        sbSended = false;
        mElianNative = new ElianNative();
        log("params is mAPSSID=" + mAPSSID + ",mAPPasswd=" + mAPPasswd);
        mHandler.sendEmptyMessage(0);
        start7681Config();
        holdTask();
        SmartLinkWakeLock.releaseWakeLock();
        return null;
    }

    private void start7681Config() {
        if (!getCurrentAPMeta()) {
            return;
        }

        log("===begin to StartSmartConnection");
        mElianNative.InitSmartConnection(null, 1, 0);
        mElianNative.StartSmartConnection(mAPSSID, mAPPasswd, "", mAuthMode);
    }

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mContext.unregisterReceiver(mWifiReceiver);
            List<ScanResult> wifis = mWifiManager.getScanResults();
            if (wifis == null || wifis.size() == 0) {
                log("=====getCurrentAPMeta fail");
                mAuthMode = -9;
                getCurrentAPMetaFinish();
            } else {
                log("=====getCurrentAPMeta finish ok");
                for (ScanResult AccessPoint : wifis) {
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
        }
    };

    private boolean getCurrentAPMeta() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(mWifiReceiver, filter);
        mWifiManager.startScan();
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

    private Handler mHandler = new Handler() {
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
                    log("stop send password and ssid");
                    stopSendPassword = true;
                    break;
            }
        }
    };

    @Override
    public void onProgressUpdate(String... values) {
        if (!getStatus().equals(Status.RUNNING)) {
            return;
        }

        if (mSmartLinkCallback != null) {
            if (values.length == 2) {
                mSmartLinkCallback.onConfigLog(values[1]);
                return;
            } else if (values.length == 0)
                mSmartLinkCallback.onConfigFail();
            else
                mSmartLinkCallback.onConfigSuccess(values[0]);
        }
        finish();
    }

    private boolean checkWifiConnected() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getIpAddress() == 0)
            return false;
        savePhoneIp(wifiInfo.getIpAddress());
        return true;
    }

    private void savePhoneIp(int ipAddress) {
        ipData = new StringBuffer();
        ipData.append((char) (ipAddress & 0xff));
        ipData.append((char) (ipAddress >> 8 & 0xff));
        ipData.append((char) (ipAddress >> 16 & 0xff));
        ipData.append((char) (ipAddress >> 24 & 0xff));
    }

    public void finish() {
        log("begin to finish the task================");
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

        sendUDPData.clear();
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
        publishProgress();
    }

    @Override
    public void onUDPEnd(UDPData udpData) {
        log(udpData.toString());
        String data = udpData.getData().trim();
        String deviceId = data.split("/")[1];
        String type = data.substring(data.lastIndexOf("/") + 1);
        log("===onUDPEnd receive data=====" + data);
        publishProgress(deviceId + "," + (TextUtils.isEmpty(type.trim()) ? "1" : type.trim()));
    }

    @Override
    public void onUDPLog(String data) {
        log(data);
    }

    public class sendUdpThread extends Thread {

        public void run() {
            while (!isCancelled() && !stopSendPassword) {
                SendbroadCast();
            }
        }
    }

    private String readAsString(InputStream is, String encode) throws Exception {
        byte[] data = readAsBytes(is);
        String result = new String(data, encode);
        if (!TextUtils.isEmpty(result)) {
            result = result.replaceAll("\\r", " ");
            result = result.replaceAll("\\n", " ");
            result = result.trim();
            return result;
        } else {
            return null;
        }
    }

    private byte[] readAsBytes(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        byte[] data = baos.toByteArray();
        baos.close();
        is.close();
        return data;
    }

    private void enableThread() {
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
                log("receive the UDP : " + data + ",and random str is " + mRandomStr);
                if (TextUtils.equals(data.getData(), String.valueOf(mRandomStr)) && !sbSended) {
                    log("==begin to send smnt========");
                    stopSendPassword = true;
                    sbSended = true;
                    mUDP = new UDPClient(mContext, getKeywords(), sb.toString());
                    mUDP.setCallback(ScinanConfigTask.this);
                    mUDP.connect();
                }
            }

            @Override
            public void onUDPLog(String data) {
                log(data);
            }
        });
        mUDPServer.connect();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(l.isTestApi() ? "http://testwww.scinan.com/connectpre.json?from=android_ext_sdk_v1.1" : "http://www.scinan.com/connectpre.json?from=android_ext_sdk_v1.1");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    conn.getOutputStream().write("android_ext_sdk_v1.1".getBytes());
                    conn.getOutputStream().close();
                    int responseCode = conn.getResponseCode();
                    mSmartLinkCallback.onConfigLog("responseCode=" + responseCode);
                    if (responseCode == 200) {
                        String responseBody = readAsString(conn.getInputStream(), "UTF-8");
                        mSmartLinkCallback.onConfigLog("responseBody=" + responseBody);
                        JSONArray hosts = new JSONArray(responseBody);
                        sb = new StringBuffer();
                        sb.append("SMNT_");
                        if (hosts.length() <= 3) {
                            for (int i = 0; i < hosts.length(); i++) {
                                sb.append(hosts.getString(i));
                                if (i != hosts.length() - 1)
                                    sb.append(",");
                            }
                        } else {
                            ArrayList<Integer> indexs = new ArrayList<Integer>();
                            for (int i = 0; i < 100; i ++) {
                                int index = new Random().nextInt(hosts.length());
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

                        sbSended = true;
                        mUDP = new UDPClient(mContext, getKeywords(), sb.toString());
                        mUDP.setCallback(ScinanConfigTask.this);
                        mUDP.connect();
                    } else {
                        publishProgress();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    publishProgress();
                }
            }
        }).start();
    }

    private String getKeywords() {
        return "/" + l.getCompanyId();
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

    private static final int PUBLISH_PROGRESS = 301;

    private void log(String log) {
        if (l.isLoggable()) {
            System.out.println("ScinanConfigTask---->" + log);
        }
        publishProgress("" + PUBLISH_PROGRESS, log);
    }

    public class AirKissEncoder {
        private int mEncodedData[] = new int[2 << 14];
        private int mLength = 0;

        // Random char should be in range [0, 127).
        public AirKissEncoder(char random, String ssid, String password) {
            //构建锁屏数据
            leadingPart();

            //构建magicCode
            for (int a = 0; a < 10; a++) {
                magicCode(ssid, password);
            }

            //构建prefixCode
            for (int a = 0; a < 20; a++) {
                prefixCode(password);
            }

            //构建sequenceData
            for (int j = 0; j < 15; j++) {
                byte[] data3 = new byte[password.length() + 1 + ssid.getBytes().length];
                System.arraycopy(password.getBytes(), 0, data3, 0, password.length());
                data3[password.length()] = (byte) random;
                System.arraycopy(ssid.getBytes(), 0, data3, password.length() + 1, ssid.getBytes().length);

                int index;
                byte content[] = new byte[4];
                for (index = 0; index < data3.length / 4; ++index) {
                    System.arraycopy(data3, index * 4, content, 0, content.length);
                    sequence(index, content);
                }

                if (data3.length % 4 != 0) {
                    content = new byte[data3.length % 4];
                    System.arraycopy(data3, index * 4, content, 0, content.length);
                    sequence(index, content);
                }
            }
        }

        public int[] getEncodedData() {
            return Arrays.copyOf(mEncodedData, mLength);
        }

        private void appendEncodedData(int length) {
            mEncodedData[mLength++] = length;
        }

        private int CRC8(byte data[]) {
            int len = data.length;
            int i = 0;
            byte crc = 0x00;
            while (len-- > 0) {
                byte extract = data[i++];
                for (byte tempI = 8; tempI != 0; tempI--) {
                    byte sum = (byte) ((crc & 0xFF) ^ (extract & 0xFF));
                    sum = (byte) ((sum & 0xFF) & 0x01);
                    crc = (byte) ((crc & 0xFF) >>> 1);
                    if (sum != 0) {
                        crc = (byte) ((crc & 0xFF) ^ 0x8C);
                    }
                    extract = (byte) ((extract & 0xFF) >>> 1);
                }
            }
            return (crc & 0xFF);
        }

        private int CRC8(String stringData) {
            return CRC8(stringData.getBytes());
        }

        private void leadingPart() {
            for (int i = 0; i < 100; ++i) {
                for (int j = 1; j <= 4; ++j)
                    appendEncodedData(j);
            }
        }

        private void magicCode(String ssid, String password) {
            int length = ssid.getBytes().length + password.length() + 1;
            int magicCode[] = new int[4];
            magicCode[0] = 0x00 | (length >>> 4 & 0xF);
            if (magicCode[0] == 0)
                magicCode[0] = 0x08;
            magicCode[1] = 0x10 | (length & 0xF);
            int crc8 = CRC8(ssid);
            magicCode[2] = 0x20 | (crc8 >>> 4 & 0xF);
            magicCode[3] = 0x30 | (crc8 & 0xF);
            for (int j = 0; j < 4; ++j)
                appendEncodedData(magicCode[j]);
        }

        private void prefixCode(String password) {
            int length = password.length();
            int prefixCode[] = new int[4];
            prefixCode[0] = 0x40 | (length >>> 4 & 0xF);
            prefixCode[1] = 0x50 | (length & 0xF);
            int crc8 = CRC8(new byte[]{(byte) length});
            prefixCode[2] = 0x60 | (crc8 >>> 4 & 0xF);
            prefixCode[3] = 0x70 | (crc8 & 0xF);
            for (int j = 0; j < 4; ++j)
                appendEncodedData(prefixCode[j]);
        }

        private void sequence(int index, byte data[]) {
            byte content[] = new byte[data.length + 1];
            content[0] = (byte) (index & 0xFF);
            System.arraycopy(data, 0, content, 1, data.length);
            int crc8 = CRC8(content);

            int sequence[] = new int[data.length + 2];
            sequence[0] = 0x80 | crc8;
            sequence[1] = 0x80 | index;

            for (int i = 0; i < data.length; i++) {
                sequence[2 + i] = 0x100 | (data[i] & 0xFF);
            }

            for (int a : sequence) {
                appendEncodedData(a);
            }
        }
    }
}
