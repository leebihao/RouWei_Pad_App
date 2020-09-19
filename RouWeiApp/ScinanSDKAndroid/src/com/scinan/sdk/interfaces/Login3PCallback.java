/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.interfaces;

/**
 * Created by lijunjie on 15/12/9.
 */
public interface Login3PCallback {
    void onSuccess(int thirdPartyId, boolean registered, String openId, String accessToken);

    void onFail(String reason);
}