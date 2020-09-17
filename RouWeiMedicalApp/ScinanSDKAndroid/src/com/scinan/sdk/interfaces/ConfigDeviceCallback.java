/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.interfaces;

/**
 * Created by lijunjie on 15/12/11.
 * 这个类将会被丢弃，鼓励使用新的回调类ConfigDeviceCallback2
 */
@Deprecated
public interface ConfigDeviceCallback {

    void onConnectAPSuccess();
    void onPingDeviceSuccess();
    void onTCPConnectSuccess();
    void onTCPConfigSuccess(String response);
    void onTCPConfigFail();
    void onConnectWIFISSID(String ssid);
}
