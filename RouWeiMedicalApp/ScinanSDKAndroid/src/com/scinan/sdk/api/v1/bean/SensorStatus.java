/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.bean;

import com.scinan.sdk.util.LogUtil;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/8.
 */
public class SensorStatus implements Serializable {

    String device_id;
    String sensor_id;
    String sensor_type;
    String data_type;
    String status;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getSensor_type() {
        return sensor_type;
    }

    public void setSensor_type(String sensor_type) {
        this.sensor_type = sensor_type;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TreeMap<String, String> getAddStatusTree() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("data_type", data_type);
        param.put("status", status);
        return param;
    }

    public void log() {
        LogUtil.d("------------------------------------------");
        LogUtil.d("device_id           = " + device_id);
        LogUtil.d("sensor_id           = " + sensor_id);
        LogUtil.d("sensor_type         = " + sensor_type);
        LogUtil.d("data_type           = " + data_type);
        LogUtil.d("status              = " + status);
        LogUtil.d("------------------------------------------");
    }
}
