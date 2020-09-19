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
public class PowerData implements Serializable {

    String device_id;
    String sensor_id;
    String sensor_type;
    String log_data;
    String data_type;
    String date;

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

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

    public String getLog_data() {
        return log_data;
    }

    public void setLog_data(String log_data) {
        this.log_data = log_data;
    }

    public TreeMap<String, String> getAddPowerDataTree() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("log_data", log_data);
        return param;
    }

    public TreeMap<String, String> getQueryPowerDataTree() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("data_type", data_type);
        param.put("date", date);
        return param;
    }

    public void log() {
        LogUtil.d("------------------------------------------");
        LogUtil.d("sensor_id           = " + sensor_id);
        LogUtil.d("sensor_type         = " + sensor_type);
        LogUtil.d("device_id           = " + device_id);
        LogUtil.d("log_data            = " + log_data);
        LogUtil.d("------------------------------------------");
    }
}
