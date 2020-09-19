/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.interfaces.ConfigDeviceCallback;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.WifiAutoConnectManager;

/**
 * Created by lijunjie on 15/12/11.
 */
public class APConfigDeviceTask extends ScinanConfigDeviceTask implements TCPConnectDeviceClient.TCPConnectCallback {

    private WifiManager mWifiManager;

    private String mDeviceSSID, mAPSSID, mAPPasswd, mAPNetworkId;

    private Object mLock = new Object();
    private WifiAutoConnectManager mAutoWifiConnectManager;

    //直连模式
    private boolean isDirectConnection;

    //TCP成功标志位
    private boolean isTcpSuccess;

    public APConfigDeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        super(context, scinanDevice, callback);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mAutoWifiConnectManager = new WifiAutoConnectManager(mWifiManager);
    }

    public APConfigDeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
        super(context, scinanDevice, callback);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mAutoWifiConnectManager = new WifiAutoConnectManager(mWifiManager);
    }

    @Override
    protected Void doInBackground(String... params) {
        ConnectWakeLock.acquireWakeLock(mContext);
        publishProgress(String.valueOf(STEP_START));
        mDeviceSSID = params[0];
        mAPSSID = params[1];
        mAPPasswd = params[2];
        mAPNetworkId = params[3];
        isTcpSuccess = false;
        isConfigSuccess = false;
        if (TextUtils.isEmpty(mAPSSID) && TextUtils.isEmpty(mAPPasswd)) {
            isDirectConnection = true;
        }
        logT("params is mDeviceSSID="+ mDeviceSSID + ",mAPSSID=" + mAPSSID + ",mAPPasswd=" + mAPPasswd + ",mAPNetworkId=" + mAPNetworkId + ", isDirectConnection=" + isDirectConnection);
        connect();
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
        while (!isCancelled()) {
            logT("begin to connect this is in while true");
            boolean isConnectSuccess = false;
            try {

                if (!connectDeviceAP()) {
                    throw new Exception("Connect AP fail");
                }

                if (isCancelled()) {
                    break;
                }

                if (!checkConnectDeviceAPSuccess()) {
                    throw new Exception("Check AP fail");
                }

                if (isCancelled()) {
                    break;
                }

                if (!checkPingDeviceSuccess()) {
                    throw new Exception("Ping AP fail");
                }

                if (isCancelled()) {
                    break;
                }
                isConnectSuccess = true;

            } catch (Exception e) {
                e.printStackTrace();
                if (BuildConfig.LOG_DEBUG) {
                    e.printStackTrace();
                }
                logT(e.getMessage());
                isConnectSuccess = false;
            }

            if (isConnectSuccess) {
                logT(String.format("Connect AP process success, ap is %s, sleep 5000 ms to start tcp connection", AndroidUtil.getWifiName(mContext)));
                sleep(5000);
                connectTCP();
                holdTask();
            }

            if (!isTcpSuccess) {
                logT("Connect AP process fail, sleep " + Constants.THREAD_SLEEP_TIMELONG + " ms to retry");
                sleep(Constants.THREAD_SLEEP_TIMELONG);
            } else {
                logT("Config AP task success! go to finish");
                break;
            }
        }

        if (isCancelled()) {
            return;
        }

        //还没成功的需要连接到路由器热点
        if (!isConfigSuccess) {
            logT("received the deviceId and type " + mHardwareCmds.toString() + ", go to connect router");
            while (!isCancelled()) {
                connectTargetWifi();
                sleep(5000);
                String current = AndroidUtil.getWifiName(mContext);
                logT("my current ap name is " + current);
                if (mAPSSID.equals(current)) {
                    isConfigSuccess = true;
                    publishProgress(String.valueOf(STEP_SUCCESS));
                    finish();
                    break;
                }
                logT("connect target wifi fail， continue retry...");
            }
        }
    }

    private void tcpSuccess() {
        if (isCancelled() || getStatus().equals(Status.FINISHED)) {
            logT("##############task isCancelled is  " + isCancelled() + " ,task status is " + getStatus().toString());
            return;
        }
        //直联模式收到deviceId后不需要切换到原来AP直接成功
        if (isDirectConnection) {
            isConfigSuccess = true;
        }
        isTcpSuccess = true;
        releaseLock();
    }

    private void tcpFail() {
        if (isCancelled() || getStatus().equals(Status.FINISHED)) {
            logT("##############task isCancelled is  " + isCancelled() + " ,task status is " + getStatus().toString());
            return;
        }
        isTcpSuccess = false;
        releaseLock();
    }

    private void releaseLock() {
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }

    private void connectTCP() {
        logT(">>>>>>>>>>>>>>>>>>>>>>>>>");
        mTCPConnectDeviceClient.connectTCP();
    }

    @Override
    public void finish() {
        logT("begin to finish the task================");
        cancel(true);
        releaseLock();
    }

    private boolean connectDeviceAP() {
        WifiConfiguration wc = mAutoWifiConnectManager.isExsits(mDeviceSSID);
        logT("connectDeviceAP isExsits " + (wc != null));

        //如果当前连接的AP就是设备AP不再连接
        String current = AndroidUtil.getWifiName(mContext);
        if (mDeviceSSID.equals(current)) {
            return true;
        }

        if (AndroidUtil.isEmui4Version() && wc != null) {
            logT("connectDeviceAP emui reconnect direct");
            String current2 = AndroidUtil.getWifiName(mContext);
            logT("connectDeviceAP my current ap name is " + current2);
            if (!mDeviceSSID.equals(current2)) {
                WifiConfiguration cu = mAutoWifiConnectManager.isExsits(current2);
                if (cu != null) {
                    logT("connectDeviceAP disable the current ap");
                    mWifiManager.disableNetwork(cu.networkId);
                }
            }
            mWifiManager.enableNetwork(wc.networkId, true);
            mWifiManager.reconnect();
            return true;
        }

        if (wc != null) {
            mWifiManager.removeNetwork(wc.networkId);
        }

        wc = mAutoWifiConnectManager.isExsits(mDeviceSSID);
        logT("connectDeviceAP2 isExsits " + (wc != null));
        if (wc != null) {
            mWifiManager.removeNetwork(wc.networkId);
        }

        mAutoWifiConnectManager.connect(mDeviceSSID, "", WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS);
        return true;
    }

    private boolean checkConnectDeviceAPSuccess(int... retry) {
        if (isCancelled()) {
            return false;
        }

        int leftRetryTimes = Constants.RETRY_TIMES_CHECK;

        if (retry != null && retry.length > 0) {
            leftRetryTimes = retry[0];
        }

        --leftRetryTimes;

        logT("leftRetryTimes is " + leftRetryTimes);

        if (leftRetryTimes < 0) {
            return false;
        }
        String ssid = AndroidUtil.getWifiName(mContext);
        if (!ssid.contains(mDeviceSSID)) {
            sleep(Constants.THREAD_SLEEP_TIMELONG);
            return checkConnectDeviceAPSuccess(leftRetryTimes);
        }
        return true;
    }

    private boolean checkPingDeviceSuccess(int... retry) {
        if (isCancelled()) {
            return false;
        }
        int leftRetryTimes = Constants.RETRY_TIMES_CHECK;

        if (retry != null && retry.length > 0) {
            leftRetryTimes = retry[0];
        }

        --leftRetryTimes;

        logT("leftRetryTimes is " + leftRetryTimes);

        if (leftRetryTimes < 0) {
            return false;
        }
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 " + Constants.DEVICE_TCP_IP);
            if (p.waitFor() != 0) {
                sleep(Constants.THREAD_SLEEP_TIMELONG);
                return checkPingDeviceSuccess(leftRetryTimes);
            } else {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    TCPConnectDeviceClient mTCPConnectDeviceClient = new TCPConnectDeviceClient(Constants.DEVICE_TCP_IP, Constants.DEVICE_TCP_PORT, this) {
        @Override
        public String getDeviceIdRequestKey() {
            return mScinanConnectDevice.getDeviceIdRequestKey();
        }

        @Override
        public String getDeviceConfigSuccessRequestKey() {
            return mScinanConnectDevice.getDeviceConfigSuccessRequestKey();
        }

        @Override
        public String getConfigInfo(String deviceId) {
            //直联模式收到deviceId后不需要发送ssid和pasword直接成功
            return isDirectConnection ? null : mScinanConnectDevice.getConfigInfo(deviceId, mAPSSID, mAPPasswd);
        }
    };

    private void sleep(long time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (InterruptedException e) {
            publishProgress(String.valueOf(STEP_FAIL), e.getMessage());
        }
    }

    private void connectTargetWifi() {
        WifiConfiguration wc = mAutoWifiConnectManager.isExsits(mAPSSID);
        logT("mAPSSID isExsits " + (wc != null));

        //如果当前连接的AP就是路由器AP不再连接
        String current2 = AndroidUtil.getWifiName(mContext);
        if (mAPSSID.equals(current2)) {
            return;
        }

        if (AndroidUtil.isEmui4Version() && wc != null) {
            logT("mAPSSID emui reconnect direct");
            String current = AndroidUtil.getWifiName(mContext);
            logT("mAPSSID my current ap name is " + current);
            if (!mAPSSID.equals(current)) {
                WifiConfiguration cu = mAutoWifiConnectManager.isExsits(current);
                if (cu != null) {
                    logT("mAPSSID disable the current ap");
                    mWifiManager.disableNetwork(cu.networkId);
                }
            }
            mWifiManager.enableNetwork(wc.networkId, true);
            mWifiManager.reconnect();
            //注意这里没有返回
        }

        if (wc != null) {
            mWifiManager.removeNetwork(wc.networkId);
        }

        wc = mAutoWifiConnectManager.isExsits(mAPSSID);
        logT("mAPSSID2 isExsits " + (wc != null));
        if (wc != null) {
            mWifiManager.removeNetwork(wc.networkId);
        }
        if (TextUtils.isEmpty(mAPPasswd)) {
            mAutoWifiConnectManager.connect(mAPSSID, "", WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS);
        } else {
            WifiAutoConnectManager.WifiCipherType type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_NOPASS;
            try {
                for (ScanResult result : mWifiManager.getScanResults()) {
                    if (mAPSSID.equals(result.SSID)) {
                        String capabilities = result.capabilities;
                        if (!TextUtils.isEmpty(capabilities)) {
                            logT("current ap capabilities is " + capabilities);
                            if (capabilities.toUpperCase().contains("WPA")) {
                                type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WPA;
                            } else if (capabilities.toUpperCase().contains("WEP")) {
                                type = WifiAutoConnectManager.WifiCipherType.WIFICIPHER_WEP;
                            }
                        }
                        break;
                    }
                }
            } catch (Exception e) {
            }

            mAutoWifiConnectManager.connect(mAPSSID, mAPPasswd, type);
        }
    }

    @Override
    public void onTCPConnected2() {
        logT("onTCPConnected2");
    }

    @Override
    public void onTCPConnectError() {
        logT("onTCPConnectError-->" + AndroidUtil.getWifiName(mContext));
        tcpFail();
    }

    @Override
    public void onTCPConfigEnd(String id) {
        logT("onTCPConfigEnd " + id);
        mHardwareCmds.add(getHardwareCmd(String.format("/%s/%s/%s/%s", id.split(",")[0],"type", "1", id.split(",")[1])));
        tcpSuccess();
    }
}
