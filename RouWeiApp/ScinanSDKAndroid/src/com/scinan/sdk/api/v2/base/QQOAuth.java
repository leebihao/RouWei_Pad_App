package com.scinan.sdk.api.v2.base;///*
// * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
// *
// * This software is the property of Guangdong Scinan IoT, Inc.
// * You have to accept the terms in the license file before use.
// */
//
//package com.scinan.sdk.api.v2.base;
//
//import android.app.Activity;
//
//import com.scinan.sdk.config.Configuration;
//import com.scinan.sdk.api.v2.network.RequestHelper;
//import com.scinan.sdk.interfaces.Login3PCallback;
//import com.scinan.sdk.util.JsonUtil;
//import com.scinan.sdk.volley.FetchDataCallback;
//import com.tencent.tauth.IUiListener;
//import com.tencent.tauth.Tencent;
//import com.tencent.tauth.UiError;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.TreeMap;
//
///**
// * Created by lijunjie on 15/12/8.
// */
//public class QQOAuth implements FetchDataCallback {
//
//    private Activity activity;
//    private Login3PCallback callback;
//    private Tencent tencent;
//    private String scinanToken = "";
//
//    private static QQOAuth sInstance;
//
//    private QQOAuth(Activity activity) {
//        this.activity = activity;
//        this.tencent = Tencent.createInstance(Configuration.getTecentAppId(activity.getApplicationContext()), activity.getApplicationContext());
//    }
//
//    private void setCallback(Login3PCallback callback) {
//        this.callback = callback;
//    }
//
//    public static QQOAuth getInstance(Activity activity, Login3PCallback callback) {
//        if (sInstance == null) {
//            synchronized (QQOAuth.class) {
//                if (sInstance == null) {
//                    sInstance = new QQOAuth(activity);
//                }
//            }
//        }
//        sInstance.setCallback(callback);
//        return sInstance;
//    }
//
//    public void run() {
//        startSDKLogin();
//    }
//
//    private void startSDKLogin() {
//        tencent.logout(activity);
//        if (!tencent.isSessionValid()) {
//            IUiListener listener = new IUiListener() {
//                @Override
//                public void onComplete(JSONObject jo) {
//                    try {
//                        String openid = jo.getString("openid");
//                        String token = jo.getString("access_token");
//                        String expires = jo.getString("expires_in");
//                        tencent.setAccessToken(token, expires);
//                        tencent.setOpenId(openid);
//                        checkQQAPI();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        loginQQComplete(false);
//                    }
//                }
//
//                @Override
//                public void onError(UiError uiError) {
//                    loginQQComplete(false);
//                }
//
//                @Override
//                public void onCancel() {
//                    loginQQComplete(false);
//                }
//            };
//            tencent.login(activity, "all", listener);
//        } else {
//            //已经登录过了
//            checkQQAPI();
//        }
//    }
//
//    private void checkQQAPI() {
//        TreeMap<String, String> param = new TreeMap<String, String>();
//        param.put("third_party_openid", tencent.getOpenId());
//        param.put("third_party_token", tencent.getAccessToken());
//        param.put("third_party_type", "1");
//        RequestHelper.getInstance(activity.getApplicationContext()).check3P(param, this);
//    }
//
//    // Login QQ finish then bind QQ to server
//    protected void loginQQComplete(boolean isSuccess, boolean ... registered) {
//        if (isSuccess) {
//            callback.onSuccess(1, registered[0], tencent.getOpenId(), scinanToken);
//            return;
//        }
//        callback.onFail(com.scinan.sdk.contants.Constants.ERROR_NETWORK);
//    }
//
//    @Override
//    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
//        switch (api) {
//            case RequestHelper.API_3P_CHECK:
//                loginQQComplete(false);
//                break;
//        }
//    }
//
//    @Override
//    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
//        switch (api) {
//            case RequestHelper.API_3P_CHECK:
//                switch (JsonUtil.getResultCode(responseBody)) {
//                    case 30117:
//                        // not register
//                        loginQQComplete(true, false);
//                        break;
//                    case 30118:
//                        // registered
//                        scinanToken = JsonUtil.getToken(responseBody);
//                        loginQQComplete(true, true);
//                        break;
//                    default:
//                        loginQQComplete(false);
//                }
//                break;
//        }
//    }
//}
