/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.cache.data.v2;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.scinan.sdk.api.v2.agent.UserAgent;
import com.scinan.sdk.api.v2.bean.User;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.PreferenceUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import java.util.Observable;

/**
 * Created by wright on 15/6/25.
 */
public class UserInfoCache extends Observable implements FetchDataCallback {

    private static UserInfoCache sCache = null;
    private User mUserInfo;
    private UserAgent mUserAgent;
    private Context mContext;

    private UserInfoCache(Context context) {
        mContext = context.getApplicationContext();
        mUserAgent = new UserAgent(context);
        mUserAgent.registerAPIListener(this);
        readSotredUserInfo();
    }

    public static synchronized UserInfoCache getCache(Context context) {
        if (sCache == null)
            sCache = new UserInfoCache(context.getApplicationContext());
        return sCache;
    }

    public void readSotredUserInfo() {
        mUserInfo = PreferenceUtil.getUser(mContext);
    }

    public void refreshCache() {
        if (!isLogin()) {
            return;
        }

        mUserAgent.getUserInfo();
    }

    public void refreshToken() {
        mUserAgent.refreshToken();
    }

    public void removeAccount() {
        Configuration.setToken(null);
        PreferenceUtil.removeAccount(mContext);

        setChanged();
        notifyObservers(null);
    }


    public void rmAccountByChangePW() {
        Configuration.setToken(null);
        PreferenceUtil.rmAccountByChangPW(mContext);
        setChanged();
        notifyObservers(null);
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(PreferenceUtil.getToken(mContext));
    }

    public synchronized User getUserInfo() {
        return mUserInfo;
    }

    /* @hide */
    public void clear() {
        if (mUserInfo != null) {
            mUserInfo = null;
        }
        notifyAlls(true);
    }

    private void notifyAlls(boolean ok) {
        setChanged();
        notifyObservers(ok);
    }

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
        switch (api) {
            case RequestHelper.API_USER_GET_INFO:
                User user = JSON.parseObject(responseBody, User.class);
                if (user != null) {
                    user.log();
                    mUserInfo = user;
                    PreferenceUtil.saveUser(mContext, mUserInfo);
                }
                notifyAlls(user != null);
                break;
            case RequestHelper.API_USER_REFRESH_TOKEN:
                String token = JsonUtil.getToken(responseBody);
                if (!TextUtils.isEmpty(token)) {
                    Configuration.setToken(token);
                    PreferenceUtil.saveToken(mContext, token);
                }
                break;
        }
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        switch (api) {
            case RequestHelper.API_USER_GET_INFO:
                notifyAlls(false);
                break;
            case RequestHelper.API_USER_REFRESH_TOKEN:
                break;
        }
    }
}
