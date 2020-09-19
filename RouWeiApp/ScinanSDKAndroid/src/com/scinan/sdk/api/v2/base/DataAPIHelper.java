/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.base;

import android.content.Context;

import com.scinan.sdk.api.v2.network.RequestHelper;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/8.
 */
public class DataAPIHelper extends BaseHelper implements Serializable {

    public DataAPIHelper(Context context) {
        super(context);
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

    public void getAllHistory(String deviceId, int page) {
        getAllHistory(deviceId, null, null, page);
    }

    public void getAllHistory(String deviceId, String sensorId, String date, int page) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensorId);
        param.put("date", date);
        param.put("page", String.valueOf(page));
        RequestHelper.getInstance(mContext).getAllHistory(param, this);
    }

    public void getPowerHistory(String device_id, String sensor_id, String sensor_type, String date, String data_type) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("data_type", data_type);
        param.put("date", date);
        RequestHelper.getInstance(mContext).getPowerHistory(param, this);
    }

    public void getPowerHour(String device_id, String sensor_id, String sensor_type, String date) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("data_type", "1");
        param.put("date", date);
        RequestHelper.getInstance(mContext).getPowerHour(param, this);
    }

    public void getPowerDay(String device_id, String sensor_id, String sensor_type, String date) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("data_type", "2");
        param.put("date", date);
        RequestHelper.getInstance(mContext).getPowerDay(param, this);
    }

    public void getPowerMonth(String device_id, String sensor_id, String sensor_type, String date) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("data_type", "3");
        param.put("date", date);
        RequestHelper.getInstance(mContext).getPowerMonth(param, this);
    }



    public void getTimer(String deviceId, String sensor_id, String sensor_type) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        RequestHelper.getInstance(mContext).getTimer(param, this);
    }
    public void getTimer(String deviceId, String sensor_id, String sensor_type, String timer_id) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("timer_id", timer_id);
        RequestHelper.getInstance(mContext).getTimer(param, this);
    }
}
