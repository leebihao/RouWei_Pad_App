/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.pay;

/**
 * Created by lijunjie on 16/3/31.
 */
public interface PayCallback {

    void onPaySuccess(int payType);
    void onPayFailed(int payType);
    void onPayFailed(int payType, String reason);
    void onPayPendding(int payType);
    void onPayWeixin();
}
