/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.pay;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.scinan.sdk.api.v2.base.BaseHelper;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.util.LogUtil;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lijunjie on 16/3/30.
 */
public class WeixinPay extends PayChannel {

    private IWXAPI mAPI;

    public WeixinPay(Activity activity, PayCallback callback) {
        super(activity, callback);
        mAPI = WXAPIFactory.createWXAPI(activity.getBaseContext(), Configuration.getWeixinPayAppID());
    }

    @Override
    public void run(final BaseHelper helper, final String deviceId, final String moneny) {
        getPayInfo(helper, deviceId, moneny);
    }

    @Override
    protected void pay(final String payInfo) {
        LogUtil.d("=======#########" + payInfo);
        WeixinPayReq req = new WeixinPayReq();
        try {
            JSONObject json = new JSONObject(payInfo);
            if(null != json) {
                req.appId = json.getString("appid");
                req.partnerId = json.getString("partnerid");
                req.prepayId = json.getString("prepayid");
                req.nonceStr = json.getString("noncestr");
                req.timeStamp = json.getString("timestamp");
                req.packageValue = json.getString("package");
                req.sign = json.getString("sign");
            }
            LogUtil.d("========" + req.toString());
            mAPI.sendReq(req);
            mCallback.onPayWeixin();
        } catch (Exception e) {
            mCallback.onPayFailed(getPayType());
        }

    }

    @Override
    public int getPayType() {
        return PayFactory.PAY_WEIXIN;
    }

    @Override
    public void cancel() {
        super.cancel();
        mAPI.unregisterApp();
    }
}
