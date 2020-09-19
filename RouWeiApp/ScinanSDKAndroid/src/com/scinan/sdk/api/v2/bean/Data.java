/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

import com.scinan.sdk.util.LogUtil;

import java.io.Serializable;

/**
 * Created by lijunjie on 15/12/8.
 */
public class Data implements Serializable {
    String timestamp;
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void log() {
        LogUtil.d("------------------------------------------");
        LogUtil.d("timestamp           = " + timestamp);
        LogUtil.d("value               = " + value);
        LogUtil.d("------------------------------------------");
    }
}
