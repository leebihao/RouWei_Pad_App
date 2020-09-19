/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.lan.v1;

import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;

import com.scinan.sdk.connect.LANRequest;
import com.scinan.sdk.connect.LANConnection;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.interfaces.LANConnectCallback;
import com.scinan.sdk.util.LogUtil;

import java.util.concurrent.BlockingQueue;

/**
 * Created by lijunjie on 15/12/13.
 */
public class LANWorkThread extends Thread implements LANConnectCallback {
    public interface OnPushCallback {
        void onPush(String deviceId, String push);
        void onPushError(String deviceId);
    }

    protected Context mContext;
    protected volatile LANConnection mLANConnection;
    protected final BlockingQueue<LANRequest> mQueue;
    protected String mDeviceId;
    private Handler mHandler;

    protected LANRequest mCurrentLANRequest;
    protected String mDeviceIP;
    private OnPushCallback onPushCallback;

    public LANWorkThread(Context context, Handler handler, String deviceId, BlockingQueue<LANRequest> queue) {
        this(context, handler, deviceId, null, queue);
    }

    public LANWorkThread(Context context, Handler handler, String deviceId, String ip, BlockingQueue<LANRequest> queue) {
        mContext = context;
        mHandler = handler;
        mDeviceId = deviceId;
        mQueue = queue;
        mDeviceIP = ip;
    }

    public void setOnPushCallback(OnPushCallback onPushCallback) {
        this.onPushCallback = onPushCallback;
    }

    public void addCMD(final LANRequest LANRequest) {
        LogUtil.d("add LANRequest called");
        if (mLANConnection == null || !mLANConnection.isConnected()) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.d("LANWorkThread=============errorrrr");
                    if (LANRequest != null && LANRequest.callback != null) {
                        LANRequest.callback.OnFetchLANDataFailed(LANRequest.api, null);
                    }
                    if (LANRequest != null && LANRequest.callback2 != null) {
                        LANRequest.callback2.OnFetchLANDataFailed(LANRequest.api, null, HardwareCmd.parse(LANRequest.cmd));
                    }
                }
            });
        } else
            mQueue.add(LANRequest);
    }

    public LANConnection getConnection() {
        return mLANConnection;
    }

    @Override
    public void run() {
        LogUtil.d("LANWorkThread start");
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mLANConnection = new LANConnection(mContext, mDeviceId, this);
        if (TextUtils.isEmpty(mDeviceIP)) {
            mLANConnection.openConnection();
        } else {
            mLANConnection.openConnection(mDeviceIP);
        }

        while (true) {
            try {
                // Take a request from the queue.
                mCurrentLANRequest = mQueue.take();
                LogUtil.d("=========receive command is " + mCurrentLANRequest.cmd);
                LogUtil.d("=========now lan connection is " + mLANConnection.isConnected());
                if (mLANConnection.isConnected()) {
                    mLANConnection.cmd(mCurrentLANRequest.cmd);
                } else {
                    if (mCurrentLANRequest != null) {
                        if (mCurrentLANRequest.callback != null) {
                            mCurrentLANRequest.callback.OnFetchLANDataFailed(mCurrentLANRequest.api, null);
                        }
                        if (mCurrentLANRequest.callback2 != null) {
                            mCurrentLANRequest.callback2.OnFetchLANDataFailed(mCurrentLANRequest.api, null, HardwareCmd.parse(mCurrentLANRequest.cmd));
                        }
                        mCurrentLANRequest = null;
                    }
                }
            } catch (InterruptedException e) {
                mLANConnection.close();
                mLANConnection = null;
                LogUtil.d("LANWorkThread die");
                break;
            }catch (Exception e) {
                mLANConnection.close();
                mLANConnection = null;
                LogUtil.d("LANWorkThread die");
                break;
            }
        }
    }

    public void cancel() {
        interrupt();
    }

    @Override
    public void onError() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("LANWorkThread=============errorrrr");
                if (mCurrentLANRequest != null) {
                    if (mCurrentLANRequest.callback != null) {
                        mCurrentLANRequest.callback.OnFetchLANDataFailed(mCurrentLANRequest.api, null);
                    }
                    if (mCurrentLANRequest.callback2 != null) {
                        mCurrentLANRequest.callback2.OnFetchLANDataFailed(mCurrentLANRequest.api, null, HardwareCmd.parse(mCurrentLANRequest.cmd));
                    }
                    mCurrentLANRequest = null;
                } else {

                    if (onPushCallback != null) {
                        onPushCallback.onPushError(mDeviceId);
                    }
                }
            }
        });
        cancel();
    }

    @Override
    public void onConnected() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("LANWorkThread=============connected");
            }
        });
    }

    @Override
    public void onResponse(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d((mCurrentLANRequest == null) + "LANWorkThread=========" + msg);
                if (mCurrentLANRequest != null) {
                    if (mCurrentLANRequest.callback != null) {
                        mCurrentLANRequest.callback.OnFetchLANDataSuccess(mCurrentLANRequest.api, msg);
                    }
                    if (mCurrentLANRequest.callback2 != null) {
                        mCurrentLANRequest.callback2.OnFetchLANDataSuccess(mCurrentLANRequest.api, msg, HardwareCmd.parse(mCurrentLANRequest.cmd));
                    }
                    mCurrentLANRequest = null;
                } else {
                    if (onPushCallback != null) {
                        onPushCallback.onPush(mDeviceId, msg);
                    }
                }
            }
        });
        if (TextUtils.isEmpty(msg)) {
            cancel();
        }
    }

    public String getIp() {
        return mDeviceIP;
    }

}
