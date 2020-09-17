/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by lijunjie on 16/8/31.
 */
public class UDPScanInfo implements Serializable {

    String ip;
    String deviceId;
    String type;

    public UDPScanInfo(String i, String id, String t) {
        ip = i;
        deviceId = id;
        type = t;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof UDPScanInfo)) {
            return false;
        }
        UDPScanInfo other = (UDPScanInfo)o;
        return TextUtils.equals(other.ip, ip) &&
                TextUtils.equals(other.deviceId, deviceId) &&
                TextUtils.equals(other.type, type);
    }

    @Override
    public String toString() {
        return "ip is " + ip + ", deviceId is " + deviceId + ", type is " + type;
    }
}
