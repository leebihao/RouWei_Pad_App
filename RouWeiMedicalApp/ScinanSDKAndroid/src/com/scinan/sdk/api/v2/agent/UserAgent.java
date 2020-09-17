/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.agent;

import android.app.Activity;
import android.content.Context;

//import com.scinan.sdk.api.v2.base.QQOAuth;
import com.scinan.sdk.api.v2.base.UserAPIHelper;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.interfaces.Login3PCallback;
import com.scinan.sdk.interfaces.LoginCallback;
import com.scinan.sdk.interfaces.UploadImageCallback;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.volley.FetchDataCallback;
import com.scinan.sdk.volley.Response;
import com.scinan.sdk.volley.VolleyError;

import java.io.File;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/6.
 */
public class UserAgent extends UserAPIHelper {

    public static final String TYPE_SEND_MSG_FOR_REGISTER_USER       = "1";
    public static final String TYPE_SEND_MSG_FOR_FORGOT_PASSWD       = "2";

    /*
    this function easy to leak
    so use getApplicationContext instead
     */
    public UserAgent(Context context) {
        super(context.getApplicationContext());
    }

    public void loginQQ(Activity activity, Login3PCallback callback) {
//        QQOAuth.getInstance(activity, callback).run();
    }

    public void login(String name, String passwd,final LoginCallback callback) {
        super.login(name, passwd, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                String token = JsonUtil.getToken(responseBody);
                Configuration.setToken(token);
                callback.onSuccess(null, null, token);
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                //callback.onFail(Constants.ERROR_NETWORK);
                callback.onFail(responseBody);
            }
        });
    }

    public void login(String name, String areaCode, String passwd,final LoginCallback callback) {
        super.login(name, areaCode, passwd,new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                String token = JsonUtil.getToken(responseBody);
                Configuration.setToken(token);
                callback.onSuccess(null, null, token);
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                //callback.onFail(Constants.ERROR_NETWORK);
                callback.onFail(responseBody);
            }
        });
    }

    public void uploadAvatar(File file, final UploadImageCallback callback) {
        uploadAvatar(file, null, callback);
    }

    public void uploadAvatar(File file, String nickName, final UploadImageCallback callback) {
        uploadAvatar(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null)
                    callback.onFail(error.toString());
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (callback != null)
                    callback.onSuccess();
            }
        }, file, nickName);
    }

    public void checkThirdParty(String type,String openid) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("third_party_type", type);
        params.put("third_party_openid", openid);
        super.checkThirdParty(params, this);
    }

    public void getUserAgreement() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        super.getUserAgreement(param, this);
    }

    /*
     * getUser sample code
    public void getUser() {
        getUserInfo(new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                User user = com.alibaba.fastjson.JSON.parseObject(responseBody, User.class);
                user.log();
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {

            }
        });
    }
    */
}
