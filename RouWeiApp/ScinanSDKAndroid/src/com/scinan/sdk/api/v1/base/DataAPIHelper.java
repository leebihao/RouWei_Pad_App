/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.base;

import android.content.Context;

import com.scinan.sdk.api.v1.bean.PowerData;
import com.scinan.sdk.api.v1.bean.SensorStatus;
import com.scinan.sdk.api.v1.network.RequestHelper;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/8.
 */
public class DataAPIHelper extends BaseHelper implements Serializable {

    public DataAPIHelper(Context context) {
        super(context);
    }

    public void getLastHistory(String deviceId, String sensorId, String sensorType) {
        getHistory(deviceId, sensorId, sensorType, "0", "1", 1);
    }

    public void getHistory(String deviceId, String sensorId, String sensorType, String dataType, String fType, int page) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensorId);
        param.put("sensor_type", sensorType);
        param.put("data_type", dataType);
        param.put("f_type", fType);
        param.put("page", String.valueOf(page));
        RequestHelper.getInstance(mContext).getHistory(param, this);
    }

    public void getGPSData(String deviceId, String sensorId, String sensorType) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensorId);
        param.put("sensor_type", sensorType);
        RequestHelper.getInstance(mContext).getGPSData(param, this);
    }

    public void uploadStatus(SensorStatus status) {
        RequestHelper.getInstance(mContext).uploadStatus(status.getAddStatusTree(), this);
    }

    public void uploadPower(PowerData power) {
        RequestHelper.getInstance(mContext).uploadPower(power.getAddPowerDataTree(), this);
    }

    public void getPowerHistory(PowerData power) {
        RequestHelper.getInstance(mContext).getPowerHistory(power.getQueryPowerDataTree(), this);
    }
}
