/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.pay;

import android.app.Activity;

import com.scinan.sdk.api.v2.base.BaseHelper;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.volley.FetchDataCallback;

/**
 * Created by lijunjie on 16/3/30.
 */
public abstract class PayChannel {

    protected Activity mActivity;
    protected PayCallback mCallback;
    protected boolean isCancel;

    public PayChannel(Activity activity, PayCallback callback) {
        mActivity = activity;
        mCallback = callback;
    }

    protected void getPayInfo(final BaseHelper helper, final String deviceId, final String moneny) {
        helper.pay(deviceId, getPayType(), moneny, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                if (isCancel)
                    return;
                pay(JsonUtil.parseV2JsonResult(responseBody));
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                if (isCancel)
                    return;
                mCallback.onPayFailed(getPayType(), JsonUtil.parseErrorMsg(responseBody));
            }
        });
    }

    public abstract void run(final BaseHelper helper, final String deviceId, final String money);
    protected abstract void pay(final String payInfo);
    public abstract int getPayType();

    public void cancel() {
        isCancel = true;
    }

    public boolean isCancel() {
        return isCancel;
    }
}
