/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.base;

import android.content.Context;

import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by lijunjie on 15/12/7.
 */
public class BaseHelper implements FetchDataCallback {
    protected CopyOnWriteArrayList<FetchDataCallback> mFetchDataListeners;
    protected Context mContext;

    public BaseHelper(Context context) {
        mContext = context.getApplicationContext();
        mFetchDataListeners = new CopyOnWriteArrayList<FetchDataCallback>();
    }

    public void registerAPIListener(FetchDataCallback listener) {
        if (!mFetchDataListeners.contains(listener)) {
            mFetchDataListeners.add(listener);
        }
    }

    public void unRegisterAPIListener(FetchDataCallback listener) {
        if (mFetchDataListeners.contains(listener)) {
            mFetchDataListeners.remove(listener);
        }
    }

    public void notifyAPISuccessListeners(int api, int responseCode, String responseBody) {
        for (FetchDataCallback callBack : mFetchDataListeners) {
            callBack.OnFetchDataSuccess(api, responseCode, responseBody);
        }
    }

    public void notifyAPIFailListeners(int api, Throwable error, String responseBody) {
        for (FetchDataCallback callBack : mFetchDataListeners) {
            callBack.OnFetchDataFailed(api, error, responseBody);
        }
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
        notifyAPISuccessListeners(api, responseCode, JsonUtil.parseV2JsonResult(responseBody));
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        notifyAPIFailListeners(api, error, responseBody);
    }

    public void pay(String deviceId, int payType, String totalFee) {
    }

    public void pay(String deviceId, int payType, String totalFee, FetchDataCallback callBack) {
    }

    public void pay(String deviceId,int rechargeType, int payType, String totalFee, FetchDataCallback callBack) {
    }
}
