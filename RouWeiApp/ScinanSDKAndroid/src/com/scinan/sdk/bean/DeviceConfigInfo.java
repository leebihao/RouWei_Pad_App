/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.bean;

import java.io.Serializable;

/**
 * Created by Luogical on 16/1/15.
 */
public class DeviceConfigInfo  implements Serializable {


    private String mSSID;
    private String mPasswd;

    public DeviceConfigInfo(String ssid, String pwd) {
        this.mSSID = ssid;
        this.mPasswd = pwd;

    }

    public String getSSID() {
        return mSSID;
    }

    public void setSSID(String ssid) {
        this.mSSID = ssid;
    }

    public String getPasswd() {
        return mPasswd;
    }

    public void setPasswd(String pwd) {
        this.mPasswd = pwd;
    }
}


