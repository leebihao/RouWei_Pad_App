/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.lan.v1;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.scinan.sdk.connect.LANRequest;
import com.scinan.sdk.connect.LANConnection;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.util.LogUtil;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by lijunjie on 15/12/14.
 */
public class LANRequestHelper implements LANWorkThread.OnPushCallback {

    private static LANRequestHelper sInstance;
    private Context mContext;

    private ConcurrentHashMap<String, LANWorkThread> mWorkThreadMap = new ConcurrentHashMap<String, LANWorkThread>();
    private CopyOnWriteArrayList<LANWorkThread.OnPushCallback> mPushCallbackListeners;

    public static final int CMD_SOCKET_SWITCH                   = 100;

    private LANRequestHelper(Context context) {
        mContext = context;
        mPushCallbackListeners = new CopyOnWriteArrayList<LANWorkThread.OnPushCallback>();
    }

    public static synchronized LANRequestHelper getInstance(Context context) {
        synchronized (LANRequestHelper.class) {
            if (sInstance == null) {
                sInstance = new LANRequestHelper(context);
            }
        }
        return sInstance;
    }

    public void addWorkThread(String deviceId) {
            if (!mWorkThreadMap.containsKey(deviceId)) {
                LogUtil.d("========add work thread " + deviceId);
                BlockingQueue<LANRequest> queue = new PriorityBlockingQueue<LANRequest>();
                LANWorkThread thread = new LANWorkThread(mContext, new Handler(Looper.getMainLooper()), deviceId, queue);
                thread.setOnPushCallback(this);
                thread.start();
                mWorkThreadMap.put(deviceId, thread);
            }
    }

    public void addWorkThread(String deviceId, String deviceIP) {
        if (!mWorkThreadMap.containsKey(deviceId)) {
            LogUtil.d("========add work thread " + deviceId + ", and ip is " + deviceIP);
            BlockingQueue<LANRequest> queue = new PriorityBlockingQueue<LANRequest>();
            LANWorkThread thread = new LANWorkThread(mContext, new Handler(Looper.getMainLooper()), deviceId, deviceIP, queue);
            thread.setOnPushCallback(this);
            thread.start();
            mWorkThreadMap.put(deviceId, thread);
        }
    }

    public LANWorkThread getThread(String deviceId) {
        if (mWorkThreadMap.containsKey(deviceId)) {
            return mWorkThreadMap.get(deviceId);
        }
        return null;
    }

    public void removeWorkThread(String deviceId) {
            if (mWorkThreadMap.containsKey(deviceId)) {
               mWorkThreadMap.get(deviceId).interrupt();
            }
        mWorkThreadMap.remove(deviceId);
    }

    public void removeAllWorkThread() {
        for (String deviceId : mWorkThreadMap.keySet()) {
            mWorkThreadMap.get(deviceId).interrupt();
            mWorkThreadMap.remove(deviceId);
        }
    }

    public LANConnection getConnection(String deviceId) {
        if (mWorkThreadMap.containsKey(deviceId)) {
            return mWorkThreadMap.get(deviceId).getConnection();
        }
        return null;
    }

    public Set<String> getAllDeviceIds() {
        return mWorkThreadMap.keySet();
    }

    public boolean isConnected(String deviceId) {
        return getConnection(deviceId) != null && getConnection(deviceId).isConnected();
    }

    public void control(int optionCode, String deviceId, String value, FetchLANDataCallback callback) {
        HardwareCmd cmd = new HardwareCmd(deviceId, optionCode, value);
        LANRequest LANRequest = new LANRequest(cmd, callback);
        if (mWorkThreadMap.containsKey(deviceId)) {
            mWorkThreadMap.get(deviceId).addCMD(LANRequest);
        }
    }

    public void control(HardwareCmd cmd, FetchLANDataCallback2 callback) {
        LANRequest LANRequest = new LANRequest(cmd, callback);
        if (mWorkThreadMap.containsKey(cmd.deviceId)) {
            mWorkThreadMap.get(cmd.deviceId).addCMD(LANRequest);
        }
    }

    @Override
    public void onPush(String deviceId, String push) {
        for (LANWorkThread.OnPushCallback callback : mPushCallbackListeners) {
            callback.onPush(deviceId, push);
        }
    }

    @Override
    public void onPushError(String deviceId) {
        for (LANWorkThread.OnPushCallback callback : mPushCallbackListeners) {
            callback.onPushError(deviceId);
        }
    }

    public void registerPushListener(LANWorkThread.OnPushCallback listener) {
        if (!mPushCallbackListeners.contains(listener)) {
            mPushCallbackListeners.add(listener);
        }
    }

    public void unRegisterPushListener(LANWorkThread.OnPushCallback listener) {
        if (mPushCallbackListeners.contains(listener)) {
            mPushCallbackListeners.remove(listener);
        }
    }
}
