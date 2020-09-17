/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.agent;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.api.v1.base.UserAPIHelper;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.interfaces.Login3PCallback;
import com.scinan.sdk.volley.FetchDataCallback;
import com.scinan.sdk.interfaces.LoginCallback;
import com.scinan.sdk.interfaces.UploadImageCallback;
import com.scinan.sdk.util.JavaUtil;
import com.scinan.sdk.volley.Response;
import com.scinan.sdk.volley.VolleyError;

import java.io.File;

/**
 * Created by lijunjie on 15/12/6.
 */
public class UserAgent extends UserAPIHelper {

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

    public void login(String name, String passwd, final LoginCallback callback) {
        login(name, passwd, "86", callback);
    }

    public void login(String name, String passwd, String areaCode, final LoginCallback callback) {
        super.login(name, passwd, areaCode, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                String token = JavaUtil.getV1Token(responseBody);
                if (TextUtils.isEmpty(token)) {
                    if (callback != null)
                        callback.onFail(Constants.ERROR_USERNAME_PWD);
                    return;
                }
                Configuration.setToken(token);
                if (callback != null)
                    callback.onSuccess(null, null, token);
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                if (callback != null)
                    callback.onFail(Constants.ERROR_NETWORK);
            }
        });
    }

    public void uploadAvatar(String name, File file, final UploadImageCallback callback) {
        uploadAvatar(name, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null)
                    callback.onFail(error.getCause().getMessage());
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (callback != null)
                    callback.onSuccess();
            }
        }, file);
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
