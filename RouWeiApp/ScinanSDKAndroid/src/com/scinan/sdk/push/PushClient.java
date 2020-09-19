/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.push;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.scinan.sdk.api.v2.base.LogDebuger;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.api.v2.network.base.VendorSSLSocketFactory;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.interfaces.PushCallback;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.PreferenceUtil;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.Listener;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import java.net.URISyntaxException;

/**
 * Created by lijunjie on 15/12/27.
 */
public class PushClient {

    private static final String PUSH_HOST = RequestHelper.HOST_NAME_PUSH;

    private Context mContext;
    private static PushClient sInstance;


    private MQTT mMQTT;
    private volatile CallbackConnection mConnection;
    private volatile Status mStatus = Status.IDEL;

    private PushCallback mListener;

    private PushClient(Context context) {
        mContext = context;
    }

    public static PushClient getInstance(Context context) {

        //一些手机不支持MQTT SSL
        AndroidUtil.setMQTTSSL();

        if (sInstance == null) {
            synchronized (PushClient.class) {
                if (sInstance == null) {
                    sInstance = new PushClient(context);
                }
            }
        }
        return sInstance;
    }

    public MQTT getMQTT() {
        return mMQTT;
    }

    public void connect(String key, String password) {
        LogUtil.t("Push client status is " + mStatus);
        if (mStatus.equals(Status.CONNECTED)) {
            return;
        }

        if (mStatus.equals(Status.CONNECTING)) {
            return;
        }

        //密码为空的时候赋个空串的值，防止不必要的空指针
        if (TextUtils.isEmpty(password)) {
            password = "";
        }

        mStatus = Status.CONNECTING;
        try {
            if (mMQTT != null) {
                mMQTT.setCleanSession(true);
                mMQTT.clear();
                mMQTT = null;
                LogUtil.d("clear mqtt");
            }

            if (mConnection!= null) {
                mConnection.disconnect(null);
                mConnection = null;
                LogUtil.d("clear connection");
            }

            mMQTT = new MQTT();
            mMQTT.setContext(mContext.getApplicationContext());
            mMQTT.setHost(PUSH_HOST, AndroidUtil.getPushPort());
            mMQTT.setUserName(key);
            mMQTT.setSslContext(VendorSSLSocketFactory.getSSLContext("TLS"));
            //推送服务
            if (!BuildConfig.PUSH_DEVICE) {
                mMQTT.setClientId(Configuration.getCompanyId(mContext));
            } else {
                //连接服务
                mMQTT.setClientId(key);
            }
            mMQTT.setPassword(password);
            LogUtil.d("======host:" + mMQTT.getHost() + ",userName:" + mMQTT.getUserName() + ",passwd:" + mMQTT.getPassword() + ",clientId:" + mMQTT.getClientId());
            if (BuildConfig.MQTT_SSL) {
                startPushConnectCheck(key, password);
            }
            mConnection = new CallbackConnection(mMQTT);
            mConnection.listener(mCorePushCallback);
            mConnection.connect(mLoginCallback);


        } catch (URISyntaxException e) {
            if (LogUtil.isTrace()) {
                LogUtil.t("connect URISyntaxException" + e.getMessage());
            }
            mStatus = Status.IDEL;
        }
    }

    public void publishTopic(final String topic, final PublishCallback callback) throws Exception {
        if (mStatus != Status.CONNECTED) {
            throw new Exception("push service is not connected");
        }

        mConnection.publish(topic, new byte[0], QoS.AT_LEAST_ONCE, false, new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                LogUtil.d("publish topic success and topic is " + topic);
                if (callback != null) {
                    callback.onSuccess(topic);
                }
            }

            @Override
            public void onFailure(Throwable value) {
                LogUtil.d("publish topic fail and topic is " + topic);
                if (callback != null) {
                    callback.onFailure(topic, value);
                }
            }
        });
    }

    public interface PublishCallback {
        void onSuccess(String topic);
        void onFailure(String topic, Throwable value);
    }

    private void startPushConnectCheck(final String token, final String password) {
        try {

            new Handler(mContext.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtil.t("startPushConnectCheck and my status is " + mStatus);
                    if (AndroidUtil.isNetworkEnabled(mContext) && BuildConfig.MQTT_SSL) {
                        if (mStatus != Status.CONNECTED) {
                            PreferenceUtil.saveSupportMQTTSSL(mContext, false);
                            BuildConfig.MQTT_SSL = false;
                            LogDebuger.getSSLCheck();
                            disconnect(null);
                            connect(token, password);
                        }
                    }
                }
            }, 3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect(Callback callback) {
        if (LogUtil.isTrace()) {
            LogUtil.t("Push manual disconnect");
        }
        if (mStatus.equals(Status.DISCONNECTIONG))
            return;
        mStatus = Status.DISCONNECTIONG;
        if (mConnection != null)
            mConnection.kill(callback == null ? mLogoutCallback : callback);
    }

    public void sendHeartBeat() {
        if (!mStatus.equals(Status.CONNECTED)) {
            HeartBeatWakeLock.releaseWakeLock();
            return;
        }
        mConnection.sendHeartBeat();
    }

    public boolean isConnected() {
        LogUtil.t("MQTT Service Status is " + mStatus);
        return mStatus.equals(Status.CONNECTED);
    }

    private Callback mLoginCallback = new Callback() {

        @Override
        public void onSuccess(Object value) {
            LogUtil.d("=======1=====");
            mStatus = Status.CONNECTED;
        }

        @Override
        public void onFailure(Throwable value) {
            value.printStackTrace();
            LogUtil.d("=======2=====");
            if (LogUtil.isTrace()) {
                LogUtil.t("mLoginCallback " + value.getMessage());
            }
            mStatus = Status.IDEL;
        }
    };

    private Callback mLogoutCallback = new Callback() {

        @Override
        public void onSuccess(Object value) {
            LogUtil.t("mLogoutCallback onSuccess");
            mStatus = Status.IDEL;
        }

        @Override
        public void onFailure(Throwable value) {
            LogUtil.t("mLogoutCallback onFailure");
            mStatus = Status.IDEL;
        }
    };

    Listener mCorePushCallback = new Listener() {

        @Override
        public void onReconnect() {
            try {
                disconnect(new Callback() {
                    @Override
                    public void onSuccess(Object value) {
                        mLogoutCallback.onSuccess(value);
                        connect(mMQTT.getUserName().toString(), mMQTT.getPassword().toString());
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        mLogoutCallback.onFailure(value);
                        connect(mMQTT.getUserName().toString(), mMQTT.getPassword().toString());
                    }
                });
            } catch (Exception e){

            }
        }

        @Override
        public void onConnected() {
            LogUtil.d("==============");
            mStatus = Status.CONNECTED;
            if (mListener != null)
                mListener.onConnected();
        }

        @Override
        public void onDisconnected() {
            LogUtil.t("mCorePushCallback onDisconnected");
            mStatus = Status.IDEL;
            if (mListener != null)
                mListener.onClose();
        }

        @Override
        public void onPublish(UTF8Buffer topic, Buffer body, Runnable ack) {
            LogUtil.d("topic==============" + topic.toString());
            LogUtil.d("data==============" + body.utf8().toString());
            if (mListener != null) {
                if (!TextUtils.isEmpty(topic.toString())) {
                    mListener.onPush(topic.toString());
                }
                if (!TextUtils.isEmpty(body.utf8().toString())) {
                    mListener.onData(body.utf8().toString());
                }
            }
        }

        @Override
        public void onFailure(Throwable value) {
            LogUtil.t("mCorePushCallback onFailure");
            mStatus = Status.IDEL;
            if (mListener != null)
                mListener.onError();
        }
    };

    public void registerPushServiceListener(PushCallback listener) {
        mListener = listener;
    }

    public void unregisterPushServiceListener(PushCallback listener) {
        mListener = null;
    }

    public enum Status {
        IDEL, CONNECTING, CONNECTED, DISCONNECTIONG
    }
}
