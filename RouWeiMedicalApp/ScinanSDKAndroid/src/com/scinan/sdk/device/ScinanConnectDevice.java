/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.device;

/**
 * Created by lijunjie on 15/12/11.
 */
public class ScinanConnectDevice {

    public String getDelimiter() {
        return "/";
    }

    public String getSensorId() {
        return "S99";
    }

    public String getSensorType() {
        return "1";
    }

    //last  wifi mode
    public String getDeviceIdRequestKey() {
        return "/type/1";
    }

    public String getDeviceConfigSuccessRequestKey() {
        return "/OK";
    }

    public String getConfigInfo(String deviceId, String ssid, String password) {
        StringBuffer info = new StringBuffer();
        info.append(getDelimiter()).append(deviceId).append(getDelimiter())
                .append(getSensorId()).append(getDelimiter()).append(getSensorType())
                .append(getDelimiter()).append(ssid).append(",")
                .append(password).append("!");
        return info.toString();
    }

    public String getCompanyId() {
        return null;
    }

    public String getDeviceType() {
        return null;
    }
}
