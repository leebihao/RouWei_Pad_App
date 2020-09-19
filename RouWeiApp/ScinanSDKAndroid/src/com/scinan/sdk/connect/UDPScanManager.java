/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.scinan.sdk.bean.UDPScanInfo;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.protocol.UDPClient;
import com.scinan.sdk.protocol.UDPData;
import com.scinan.sdk.util.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lijunjie on 16/8/30.
 */

public class UDPScanManager {
    private static UDPScanManager sInstance;
    private Context mContext;
    private UDPClient mUDPClient;
    private ConcurrentHashMap<String, UDPScanInfo> mUDPResult;
    private CopyOnWriteArrayList<UDPScanCallback> mScanCallbackListeners;

    public interface UDPScanCallback {
        void onUDPScanEnd(Map<String, UDPScanInfo> data);
        void onUDPScanProgress(UDPScanInfo data);
    }

    public static UDPScanManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UDPScanManager.class) {
                if (sInstance == null) {
                    sInstance = new UDPScanManager(context);
                }
            }
        }
        return sInstance;
    }

    private UDPScanManager(Context context) {
        mContext = context.getApplicationContext();
        mUDPClient = new UDPClient(mContext, Configuration.getCompanyId(mContext), "S0000", false);
        mUDPClient.setCallback(mUDPCallback);
        mUDPResult = new ConcurrentHashMap<String, UDPScanInfo>();
        mScanCallbackListeners = new CopyOnWriteArrayList<UDPScanCallback>();
    }

    public void start() {
        start(3000);
    }

    public void start(int timeout) {
        mUDPResult.clear();
        stop();
        mUDPClient.connect();
        mUDPTimeoutHandler.sendEmptyMessageDelayed(0, timeout);
    }

    public void stop() {
        mUDPTimeoutHandler.removeMessages(0);
        mUDPClient.disconnect();
    }

    protected UDPScanCallback mUDPScanCallback = new UDPScanCallback() {
        @Override
        public void onUDPScanEnd(Map<String, UDPScanInfo> data) {
            notifyCallbacks(1, data);
        }

        @Override
        public void onUDPScanProgress(UDPScanInfo data) {
            notifyCallbacks(0, data);
        }
    };

    protected UDPClient.UDPClientCallback mUDPCallback = new UDPClient.UDPClientCallback() {
        @Override
        public void onUDPError() {
        }

        @Override
        public void onUDPEnd(UDPData data) {
            LogUtil.d("onUDPEnd########" + data);
            UDPScanInfo incomming;
            try {
                String[] info = data.getData().split(",");
                String ip = info[0];
                String msg = info[1];
                HardwareCmd cmd = HardwareCmd.parse(msg);
                incomming = new UDPScanInfo(ip, cmd.deviceId, cmd.data);
                if (mUDPResult.containsValue(incomming)) {
                    LogUtil.d("onUDPEnd, this deviceId has in result return");
                    return;
                }
                mUDPResult.put(cmd.deviceId, incomming);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            mUDPScanCallback.onUDPScanProgress(incomming);
        }
    };

    protected Handler mUDPTimeoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LogUtil.d("UDPScanManager scan timeout");
            if (mUDPResult.size() > 0) {
                Map<String, UDPScanInfo> info = new HashMap<String, UDPScanInfo>();
                info.putAll(mUDPResult);
                mUDPScanCallback.onUDPScanEnd(info);
            } else {
                mUDPScanCallback.onUDPScanEnd(null);
            }
            mUDPResult.clear();
            stop();
        }
    };

    public void registerPushListener(UDPScanCallback listener) {
        if (!mScanCallbackListeners.contains(listener)) {
            mScanCallbackListeners.add(listener);
        }
    }

    public void unRegisterPushListener(UDPScanCallback listener) {
        if (mScanCallbackListeners.contains(listener)) {
            mScanCallbackListeners.remove(listener);
        }
    }

    protected void notifyCallbacks(int type, Object object) {
        for (UDPScanCallback callback : mScanCallbackListeners) {
            switch (type) {
                case 0:
                    callback.onUDPScanProgress((UDPScanInfo) object);
                    break;
                case 1:
                    callback.onUDPScanEnd((Map<String, UDPScanInfo>) object);
                    break;
            }
        }
    }
}
