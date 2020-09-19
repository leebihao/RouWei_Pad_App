/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.base;

import android.content.Context;

import com.scinan.sdk.api.v1.bean.Sensor;
import com.scinan.sdk.api.v1.network.RequestHelper;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/8.
 */
public class SensorAPIHelper extends BaseHelper implements Serializable {

    public SensorAPIHelper(Context context) {
        super(context);
    }

    public void getSensorList(String deviceId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        RequestHelper.getInstance(mContext).getSensorList(param, this);
    }

    public void getDeviceSensors(String deviceId, String sensorType) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_type", sensorType);
        RequestHelper.getInstance(mContext).getDeviceSensors(param, this);
    }

    public void controlSensor(String deviceId, String sensorId, String sensorType, String data) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensorId);
        param.put("sensor_type", sensorType);
        param.put("control_data", data);
        RequestHelper.getInstance(mContext).controlSensor(param, this);
    }

    public void addSensor(Sensor sensor) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        RequestHelper.getInstance(mContext).addSensor(sensor.getAddSensorTree(), this);
    }

    public void removeSensor(String deviceId, String sensorId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensorId);
        RequestHelper.getInstance(mContext).removeSensor(param, this);
    }
}
