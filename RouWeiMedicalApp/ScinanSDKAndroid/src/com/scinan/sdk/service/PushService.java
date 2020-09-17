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
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.scinan.sdk.R;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.interfaces.PushCallback;
import com.scinan.sdk.push.HeartBeatWakeLock;
import com.scinan.sdk.push.PushClient;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.PreferenceUtil;

import org.fusesource.mqtt.client.MQTT;
import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lijunjie on 15/12/28.
 */

public class PushService extends Service {

    public static final int NOTIFY_CONNECTED = 1;
    public static final int NOTIFY_ERROR = 2;
    public static final int NOTIFY_CLOSE = 3;
    public static final int NOTIFY_PUSH = 4;

    static volatile PushClient mPushClient;

    private ConcurrentHashMap<String, IPushCallback> mListenerList;

    IDaemonService mCoreAppService;

    @Override
    public void onCreate() {
        LogUtil.d("================");
        super.onCreate();
        listenCoreAppService();
        mPushClient = PushClient.getInstance(this);
        mPushClient.registerPushServiceListener(mPushCallback);
        mListenerList = new ConcurrentHashMap<String, IPushCallback>();
    }

    private void listenCoreAppService() {
        Intent intent = new Intent(this, DaemonService.class);
        bindService(intent, mCoreAppServiceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mCoreAppServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d("PushService===========push service connect daemon success.");
            mCoreAppService = IDaemonService.Stub.asInterface(service);
            isTrace();
            handleIntent(null);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("PushService======onServiceDisconnected");
            mCoreAppService = null;
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.t("onStartCommand, (mCoreAppService == null) is " + (mCoreAppService == null));
        if (mCoreAppService != null) {
            LogUtil.t("onStartCommand, mCoreAppService.isAlive is " + mCoreAppService.asBinder().isBinderAlive());
        }
        try {
            if (mCoreAppService == null) {
                listenCoreAppService();
            } else if (!mCoreAppService.asBinder().isBinderAlive()) {
                unbindService(mCoreAppServiceConnection);
                listenCoreAppService();
            }
        } catch (Exception e) {
            LogUtil.t(e);
            e.printStackTrace();
        }
        startScinanForeground();
//        startForeground(100, new Notification());
        Intent push = new Intent();
        push.setClass(this, InnerService.class);
        startService(push);
        if (intent == null) {
            LogUtil.d("======receive the intent null");
        } else {
            LogUtil.d("======receive the intent " + intent.getAction());
        }
        handleIntent(intent);
        return START_STICKY;
    }

    private void startScinanForeground() {
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
    }


    @Override
    public IBinder onBind(Intent arg0) {
        LogUtil.d("======on Bind service");
        return mBinder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        LogUtil.d("===========");
        super.unbindService(conn);
    }

    private IPushService.Stub mBinder = new IPushService.Stub() {

        @Override
        public boolean isPushConnected() throws RemoteException {
            LogUtil.d("isPushConnected=" + mPushClient.isConnected());
            LogUtil.d("mPushClient=" + mPushClient);
            return mPushClient.isConnected();
        }

        @Override
        public void onSend(String message) {
            LogUtil.t("receive the send command, and command is " + message);
            if ("TOKEN".equals(message)) {
                LogUtil.e("disconnect the push service from logout");
                Configuration.setToken("");
                mPushClient.disconnect(null);
            } else {
                try {
                    mPushClient.publishTopic(message, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void closePush() {
            LogUtil.d("==============");
            mPushClient.disconnect(null);
        }

        @Override
        public void connectPush() {
            LogUtil.d("==============");
            mPushClient.connect(getPushKey(), getClientInfo());
        }

        @Override
        public void removeCallback(String id) {
            LogUtil.d("=======remove call back...");
            if (mListenerList.containsKey(id)) {
                mListenerList.remove(id);
            }
            LogUtil.d("======size is " + mListenerList.keySet());
        }

        @Override
        public void addCallback(String id, IPushCallback callback) {
            if (mListenerList.containsKey(id)) {
                mListenerList.remove(id);
            }
            mListenerList.put(id, callback);
            LogUtil.d("======size is " + mListenerList.keySet());
        }
    };

    protected void handleIntent(Intent intent) {
        try {
            String action = intent == null ? Constants.ACTION_START_PUSH_KEEP_ALIVE : intent.getAction();

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                if (AndroidUtil.isNetworkEnabled(this)) {
                    action = Constants.ACTION_START_PUSH_KEEP_ALIVE;
                } else {
                    action = Constants.ACTION_START_PUSH_CLOSE;
                }
            }

            if (Constants.ACTION_START_PUSH_CLOSE.equals(action)) {
                mPushClient.disconnect(null);
            }

            if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
                    Constants.ACTION_START_PUSH_KEEP_ALIVE.equals(action) ||
                    Constants.ACTION_START_PUSH_HEARTBEAT.equals(action) ||
                    Constants.ACTION_START_PUSH_CONNECT.equals(action)) {
                LogUtil.d("isNeedtoStartPush=" + isNeedtoStartPush());
                if (isNeedtoStartPush()) {
                    LogUtil.d("handleIntent mPushClient = " + mPushClient);
                    LogUtil.d("handleIntent mPushClient.isConnected() = " + mPushClient.isConnected());

                    MQTT mqtt = mPushClient.getMQTT();
                    if (mqtt != null) {
                        try {
                            if (!mqtt.getUserName().toString().equals(getPushKey())) {
                                LogUtil.e("why token is diff, mqtt is " + mqtt.getUserName().toString() + ", new token is " + Configuration.getToken());
                                mPushClient.disconnect(null);
                                mPushClient.connect(getPushKey(), getClientInfo());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (!mPushClient.isConnected()) {
                        mPushClient.connect(getPushKey(), getClientInfo());
                    } else {
                        mPushClient.sendHeartBeat();
                    }
                } else {
                    if (mPushClient.isConnected()) {
                        mPushClient.disconnect(null);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isNeedtoStartPush() {
        String token = getPushKey();
        //密码为空也可以登录推送服务，这是在2016年11月新增的需求
        String info = getClientInfo();
        boolean network = AndroidUtil.isNetworkEnabled(getApplicationContext());
        LogUtil.d("!TextUtils.isEmpty(PreferenceUtil.getAccount(this).getPushKey())=" + !TextUtils.isEmpty(token));
        LogUtil.d("getClientInfo()!=null ? " + !TextUtils.isEmpty(info));
        LogUtil.d("network = " + network);
        if (isTrace())
            LogUtil.t("isNeedtoStartPush token=" + token + ";password=" + info + ";network=" + network);
        return !TextUtils.isEmpty(token) &&
                network;
    }

    String getPushKey() {

        //连接服务返回的是设备ID
        if (BuildConfig.PUSH_DEVICE) {
            return getConnectServiceId();
        }

        //推送服务返回的是token
        return getToken();
    }

    String getToken() {
        try {
            String token = mCoreAppService.getToken();
            Configuration.setToken(token);
            return token;
        } catch (Exception e) {
            LogUtil.t(e);
            e.printStackTrace();
        }
        Configuration.setToken("");
        return null;
    }

    String getConnectServiceId() {
        try {
            String value = mCoreAppService.getValue(PreferenceUtil.KEY_CONNECT_SERVICE_ID);
            LogUtil.d("==========" + value);
            Configuration.setConnectId(value);
            return value;
        } catch (Exception e) {
            LogUtil.t(e);
            e.printStackTrace();
        }
        return null;
    }

    String getClientInfo() {
        try {
            JSONObject info = new JSONObject();
            info.put("password", mCoreAppService.getPassword());
            info.put("client-info", AndroidUtil.getSDKBuildInfo());
            return info.toString();
        } catch (Exception e) {
            LogUtil.t(e);
            e.printStackTrace();
        }
        return "{\"client-info\":\"" + AndroidUtil.getSDKBuildInfo() + "\"}";
    }

    boolean isTrace() {
        try {
            if (BuildConfig.LOG_TRACE_LEVEL <= 0) {
                BuildConfig.LOG_TRACE_LEVEL = mCoreAppService.getTrace();
            }
        } catch (Exception e) {
        }
        return LogUtil.isTrace();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPushClient.unregisterPushServiceListener(mPushCallback);
        listenCoreAppService();
    }

    private PushCallback mPushCallback = new PushCallback() {
        @Override
        public void onConnected() {
            notifyICallbacks(NOTIFY_CONNECTED);
        }

        @Override
        public void onError() {
            notifyICallbacks(NOTIFY_ERROR);
        }

        @Override
        public void onClose() {
            notifyICallbacks(NOTIFY_CLOSE);
        }

        @Override
        public void onPush(String msg) {
            notifyICallbacks(NOTIFY_PUSH, msg);
        }

        @Override
        public void onData(String msg) {
            notifyICallbacks(NOTIFY_PUSH, msg);
        }
    };

    private void notifyICallbacks(int type, String... msg) {
        LogUtil.d("=========" + mListenerList.keySet());
        for (IPushCallback callback : mListenerList.values()) {
            try {
                switch (type) {
                    case NOTIFY_CONNECTED:
                        callback.onConnected();
                        break;
                    case NOTIFY_ERROR:
                        callback.onError();
                        break;
                    case NOTIFY_CLOSE:
                        callback.onClose();
                        break;
                    case NOTIFY_PUSH:
                        callback.onPush(msg[0]);
                        break;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static class InnerService extends Service {
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
//            startForeground(100, new Notification());
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
            mHandler.sendEmptyMessageDelayed(0, 2000);
            return START_STICKY;
        }

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                stopSelf();
            }
        };
    }

    public static class PushReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            HeartBeatWakeLock.acquireWakeLock(context);
            String action = intent.getAction();
            LogUtil.t("=========receive broadcast action is " + action);
            startDaemonService(context);
            startPushService(context, action);
        }

        private void startPushService(Context context, String action) {
            Intent push = new Intent();
            push.setClass(context, PushService.class);
            push.setAction(action);
            //            context.startService(push);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(push);
            } else {
                context.startService(push);
            }
        }

        private void startDaemonService(Context context) {
            Intent push = new Intent();
            push.setClass(context, DaemonService.class);
//            context.startService(push);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(push);
            } else {
                context.startService(push);
            }
        }
    }
}
