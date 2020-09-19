/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.pay;

import android.app.Activity;

/**
 * Created by lijunjie on 16/3/30.
 */
public class PayFactory {

    public static final int PAY_ALI                                  = 1;
    public static final int PAY_WEIXIN                               = 2;

    public static PayChannel getPayChannel(int type, Activity activity, PayCallback callback) {
        PayChannel pay = null;
        switch (type) {
            case PAY_ALI:
                pay = new AliPay(activity, callback);
                break;
            case PAY_WEIXIN:
                pay = new WeixinPay(activity, callback);
                break;
        }
        return pay;
    }

}