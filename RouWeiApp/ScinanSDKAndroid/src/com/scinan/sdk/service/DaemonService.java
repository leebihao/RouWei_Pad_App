/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.scinan.sdk.R;
import com.scinan.sdk.bean.ADPushData;
import com.scinan.sdk.cache.data.v2.UserInfoCache;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.hardware.OptionCode;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.PreferenceUtil;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by lijunjie on 15/12/29.
 */
public class DaemonService extends Service {

    IPushService mPushService;
    Context mContext;
    UserInfoCache mUserInfoCache;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        listenPushService();
        mUserInfoCache = UserInfoCache.getCache(this);
        mUserInfoCache.addObserver(mAccountRemovable);
    }

    private void listenPushService() {
        Intent intent = new Intent(this, PushService.class);
        intent.setAction(Constants.ACTION_LISTEN_PUSH_STATUS);
        bindService(intent, mPushServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPushServiceConnection != null) {
            unbindService(mPushServiceConnection);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IDaemonService.Stub mBinder = new IDaemonService.Stub() {

        @Override
        public String getPassword() throws RemoteException {
            LogUtil.d("getPassword() = " + PreferenceUtil.getPassword(mContext));
            return PreferenceUtil.getPassword(mContext);
        }

        @Override
        public String getToken() throws RemoteException {
            LogUtil.d("getToken() = " +PreferenceUtil.getToken(mContext));
            return PreferenceUtil.getToken(mContext);
        }

        @Override
        public int getTrace() throws RemoteException {
            LogUtil.d("getTrace = " + BuildConfig.LOG_TRACE_LEVEL);
            return BuildConfig.LOG_TRACE_LEVEL;
        }

        @Override
        public String getValue(String key) throws RemoteException {
            LogUtil.d("getValue key is " + key + ", value is " + PreferenceUtil.getString(mContext, key));
            if (PreferenceUtil.KEY_CONNECT_SERVICE_ID.equals(key)) {
                return PreferenceUtil.getConnectServiceId(mContext);
            }
            return PreferenceUtil.getString(mContext, key);
        }
    };

    private IPushCallback mPushCallback = new IPushCallback.Stub() {
        @Override
        public void onConnected() throws RemoteException {

        }

        @Override
        public void onError() throws RemoteException {

        }

        @Override
        public void onClose() throws RemoteException {

        }

        @Override
        public void onPush(String msg) throws RemoteException {
            LogUtil.d("from DaemonService===========" + msg);
            try {
                HardwareCmd cmd = HardwareCmd.parse(msg);
                if (cmd != null && cmd.optionCode == OptionCode.STATUS_ERROR) {
                    LogUtil.d("from DaemonService receive alarm msg broadcast it");
                    Intent intent = new Intent(Constants.ACTION_PUSH_ALARM);
                    intent.setPackage(mContext.getPackageName());
                    intent.putExtra("msg", msg);
                    sendBroadcast(intent);
                    return;
                }
                List<ADPushData> adPushData = JSON.parseArray(msg, ADPushData.class);
                if (adPushData != null && adPushData.size() > 0) {
                    LogUtil.d("from DaemonService receive ad data msg broadcast it");
                    Intent intent = new Intent(Constants.ACTION_PUSH_AD);
                    intent.setPackage(mContext.getPackageName());
                    intent.putExtra("msg", adPushData.get(0));
                    sendBroadcast(intent);
                    return;
                }

            } catch (Exception e) {
            }
        }
    };

    ServiceConnection mPushServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPushService = IPushService.Stub.asInterface(service);
            try {
                mPushService.addCallback(DaemonService.class.getName(), mPushCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPushService = null;
        }
    };

    Observer mAccountRemovable = new Observer() {
        @Override
        public void update(Observable observable, Object data) {
            if (!mUserInfoCache.isLogin()) {
                try {

                    if(AndroidUtil.isServiceAlive(mContext,"com.scinan.sdk.service.PushService")){
                        mPushService.onSend("TOKEN");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("com.scinan.sdk", "SCINAN_BACKGROUND", NotificationManager.IMPORTANCE_NONE);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null)
                manager.createNotificationChannel(channel);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "com.scinan.sdk");
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("App is running in background")
                    .setPriority(NotificationManager.IMPORTANCE_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(100,notification);
        } else {
            startForeground(100, new Notification());
        }
        return START_STICKY;
    }
}
