/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.hardware;

import java.io.Serializable;

/**
 * Created by lijunjie on 16/1/10.
 */
public class HardwareCmd implements Serializable {

    public String deviceId;
    public String optionCodeString;
    public String data;
    public int optionCode;
    public String sensorType;
    //某些场景下需要ip
    public String ip;

    public HardwareCmd(String deviceId, String optionCode, String sensorType, String data) {
        this.deviceId = deviceId;
        this.optionCodeString = optionCode;
        this.data = data;
        this.optionCode = OptionCode.getOptionCode(optionCode);
        this.sensorType = sensorType;
    }

    public HardwareCmd(String deviceId, int optionCode, String sensorType, String data) {
        this(deviceId, OptionCode.getOptionCode(optionCode), sensorType, data);
    }

    public HardwareCmd(String deviceId, int optionCode, String data) {
        this(deviceId, optionCode, "1", data);
    }

    public HardwareCmd(String deviceId, String optionCode, String data) {
        this(deviceId, optionCode, "1", data);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public static HardwareCmd parse(String fullCmdString) {
        try {
            String[] list = fullCmdString.split("/",-1);
            String deviceId = list[1];
            try {
                String optionCodeString = list[2];
                String sensorType = list[3];
                String data = list[4];
                return new HardwareCmd(deviceId, optionCodeString, sensorType, data);
            } catch (Exception e) {
                return new HardwareCmd(deviceId, null, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return String.format("/%s/%s/%s/%s", deviceId, optionCodeString, sensorType, data);
    }
}
