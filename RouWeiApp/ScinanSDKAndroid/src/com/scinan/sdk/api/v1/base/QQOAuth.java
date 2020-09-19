package com.scinan.sdk.api.v1.base;///*
// * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
// *
// * This software is the property of Guangdong Scinan IoT, Inc.
// * You have to accept the terms in the license file before use.
// */
//
//package com.scinan.sdk.api.v1.base;
//
//import android.app.Activity;
//import android.text.TextUtils;
//
//import com.scinan.sdk.api.v1.bean.QQUser;
//import com.scinan.sdk.config.Configuration;
//import com.scinan.sdk.interfaces.Login3PCallback;
//import com.scinan.sdk.volley.FetchDataCallback;
//import com.scinan.sdk.api.v1.network.RequestHelper;
//import com.scinan.sdk.util.JavaUtil;
//import com.scinan.sdk.util.JsonUtil;
//import com.scinan.sdk.util.MD5HashUtil;
//import com.tencent.open.HttpStatusException;
//import com.tencent.open.NetworkUnavailableException;
//import com.tencent.tauth.Constants;
//import com.tencent.tauth.IRequestListener;
//import com.tencent.tauth.IUiListener;
//import com.tencent.tauth.Tencent;
//import com.tencent.tauth.UiError;
//
//import org.apache.http.conn.ConnectTimeoutException;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.SocketTimeoutException;
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
//    private QQUser user;
//
//    private static QQOAuth sInstance;
//
//    private QQOAuth(Activity activity, Login3PCallback callback) {
//        this.activity = activity;
//        this.callback = callback;
//        this.tencent = Tencent.createInstance(Configuration.getTecentAppId(activity.getApplicationContext()), activity.getApplicationContext());
//    }
//
//    public static QQOAuth getInstance(Activity activity, Login3PCallback callback) {
//        if (sInstance == null) {
//            synchronized (QQOAuth.class) {
//                if (sInstance == null) {
//                    sInstance = new QQOAuth(activity, callback);
//                }
//            }
//        }
//        return sInstance;
//    }
//
//    public void run() {
//        startSDKLogin();
//    }
//
//    private void startSDKLogin() {
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
//                        getTecentUserInfo();
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
//            checkQQbind();
//        }
//    }
//
//    private void getTecentUserInfo() {
//        tencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET, requestListener, null);
//    }
//
//    private void registerQQAPI() {
//        user.log();
//        String passwd = tencent.getOpenId().substring(0, 9);
//        TreeMap<String, String> param = new TreeMap<String, String>();
//        param.put("password", MD5HashUtil.hashCode(passwd));
//        param.put("type", UserAPIHelper.REGISTER_TYPE_WEB);
//        param.put("qq_openid", tencent.getOpenId());
//        param.put("user_name", user.getNickname());
//        //param.put("user_nickname",nickName);
//        RequestHelper.getInstance(activity.getApplicationContext()).registerEmail(param, this);
//    }
//
//    private void loginQQAPI() {
//        TreeMap<String, String> param = new TreeMap<String, String>();
//        param.put("userId", user.getId());
//        param.put("passwd", tencent.getOpenId().substring(0, 9));
//        param.put("redirect_uri", "http://localhost.com:8080/testCallBack.action");
//        param.put("response_type", "token");
//        param.put("client_id", Configuration.getAppKey(activity.getApplicationContext()));
//        RequestHelper.getInstance(activity.getApplicationContext()).login(param, this);
//    }
//
//    private void checkQQbind() {
//        TreeMap<String, String> param = new TreeMap<String, String>();
//        param.put("qq_openid", tencent.getOpenId());
//        RequestHelper.getInstance(activity.getApplicationContext()).checkQQBind(param, this);
//    }
//    private IRequestListener requestListener = new IRequestListener() {
//
//        @Override
//        public void onComplete(JSONObject jo, Object o) {
//            user = com.alibaba.fastjson.JSON.parseObject(jo.toString(), QQUser.class);
//            if (user == null) {
//                loginQQComplete(false);
//                return;
//            }
//            user.log();
//            registerQQAPI();
//        }
//
//        @Override
//        public void onIOException(IOException e, Object o) {
//            loginQQComplete(false);
//        }
//
//        @Override
//        public void onMalformedURLException(MalformedURLException e, Object o) {
//            loginQQComplete(false);
//        }
//
//        @Override
//        public void onJSONException(JSONException e, Object o) {
//            loginQQComplete(false);
//        }
//
//        @Override
//        public void onConnectTimeoutException(ConnectTimeoutException e, Object o) {
//            loginQQComplete(false);
//        }
//
//        @Override
//        public void onSocketTimeoutException(SocketTimeoutException e, Object o) {
//            loginQQComplete(false);
//        }
//
//        @Override
//        public void onNetworkUnavailableException(NetworkUnavailableException e, Object o) {
//            loginQQComplete(false);
//        }
//
//        @Override
//        public void onHttpStatusException(HttpStatusException e, Object o) {
//            loginQQComplete(false);
//        }
//
//        @Override
//        public void onUnknowException(Exception e, Object o) {
//            loginQQComplete(false);
//        }
//    };
//
//    // Login QQ finish then bind QQ to server
//    protected void loginQQComplete(boolean isSuccess) {
//        if (tencent != null) {
//            tencent.setAccessToken(null, null);
//        }
//        if (isSuccess) {
//            callback.onSuccess(1, true, tencent.getOpenId(), user.getScinanToken());
//            return;
//        }
//        callback.onFail(com.scinan.sdk.contants.Constants.ERROR_NETWORK);
//    }
//
//    @Override
//    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
//        switch (api) {
//            case RequestHelper.API_USER_REGISTER:
//                checkQQbind();
//                break;
//            case RequestHelper.API_USER_OAUTH:
//                String token = JavaUtil.getV1Token(responseBody);
//                if (TextUtils.isEmpty(token)) {
//                    loginQQComplete(false);
//                    return;
//                }
//                Configuration.setToken(token);
//                user.setScinanToken(token);
//                loginQQComplete(true);
//                break;
//            case RequestHelper.API_USER_CHECK_BIND_QQ:
//                try {
//                    String id = new JSONObject(responseBody).getString("user_digit");
//                    if (user == null) {
//                        loginQQComplete(false);
//                        return;
//                    }
//                    user.setId(id);
//                    loginQQAPI();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    loginQQComplete(false);
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
//        switch (api) {
//            case RequestHelper.API_USER_REGISTER:
//                if (JsonUtil.getErrorMsgCode(responseBody) == RequestHelper.HTTP_QQ_BIND) {
//                    checkQQbind();
//                    return;
//                }
//                loginQQComplete(false);
//                break;
//            case RequestHelper.API_USER_OAUTH:
//                loginQQComplete(false);
//                break;
//            case RequestHelper.API_USER_CHECK_BIND_QQ:
//                loginQQComplete(false);
//                break;
//        }
//    }
//}
