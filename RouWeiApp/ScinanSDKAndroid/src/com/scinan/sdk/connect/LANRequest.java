/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.lan.v1.FetchLANDataCallback;
import com.scinan.sdk.lan.v1.FetchLANDataCallback2;

/**
 * Created by lijunjie on 15/12/13.
 */
public class LANRequest implements Comparable {

    public int api;
    public String cmd;
    public FetchLANDataCallback callback;
    public FetchLANDataCallback2 callback2;

    public LANRequest(HardwareCmd hardwareCmd, FetchLANDataCallback callback) {
        this.api = hardwareCmd.optionCode;
        this.cmd = hardwareCmd.toString();
        this.callback = callback;
    }

    public LANRequest(HardwareCmd hardwareCmd, FetchLANDataCallback2 callback) {
        this.api = hardwareCmd.optionCode;
        this.cmd = hardwareCmd.toString();
        this.callback2 = callback;
    }

    @Override
    public int compareTo(Object another) {
        try {
            LANRequest a = (LANRequest) another;
            if (a.api == api)
                return 0;
            return api > a.api ? 1 : -1;
        } catch (Exception e) {
        }
        return -1;
    }
}
