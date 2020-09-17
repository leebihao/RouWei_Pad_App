/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.cache.data;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.scinan.sdk.api.v1.agent.UserAgent;
import com.scinan.sdk.api.v1.bean.User;
import com.scinan.sdk.api.v1.network.RequestHelper;
import com.scinan.sdk.config.Configuration;
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
        if (TextUtils.isEmpty(Configuration.getToken(context))) {
            mUserInfo = null;
        } else {
            mUserAgent = new UserAgent(context);
            mUserAgent.registerAPIListener(this);
        }
        mContext = context;
    }

    public static synchronized UserInfoCache getCache(Context context)  {


        if (sCache == null)
            sCache = new UserInfoCache(context.getApplicationContext());
        return sCache;
    }

    public void refreshCache() {
        if (mUserAgent == null) {
            return;
        }
        mUserAgent.getUserInfo();
    }

    public void removeAccount() {
        Configuration.setToken(null);
        PreferenceUtil.removeAccount(mContext);
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
                user.log();
                mUserInfo = user;
                notifyAlls(true);
                break;
        }
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        notifyAlls(false);
    }
}
