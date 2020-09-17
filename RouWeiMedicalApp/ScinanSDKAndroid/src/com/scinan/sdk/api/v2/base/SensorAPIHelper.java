/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.base;

import android.content.Context;

import com.scinan.sdk.api.v2.bean.Sensor;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.hardware.HardwareCmd;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/8.
 */
public class SensorAPIHelper extends BaseHelper implements Serializable {

    public SensorAPIHelper(Context context) {
        super(context);
    }

    public void getSensorList(String deviceId, String sensorType) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_type", sensorType);
        RequestHelper.getInstance(mContext).getSensorList(param, this);
    }

    public void updateSensor(Sensor sensor) {
        RequestHelper.getInstance(mContext).updateSensor(sensor.getAddSensorTree(), this);
    }

    public void controlSensor(HardwareCmd cmd) {
        controlSensor(cmd.deviceId, cmd.optionCode, cmd.sensorType, cmd.data);
    }

    public void controlSensor(String deviceId, int sensorId, String sensorType, String data) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", String.format("%02d", sensorId));
        param.put("sensor_type", sensorType);
        try {
            param.put("control_data", new JSONObject().put("value", data).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestHelper.getInstance(mContext).controlSensor(sensorId, param, this);
    }

    public void addSensor(Sensor sensor) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        RequestHelper.getInstance(mContext).addSensor(sensor.getAddSensorTree(), this);
    }

    public void saveSensor(Sensor sensor) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        RequestHelper.getInstance(mContext).saveSensor(sensor.getSaveSensorTree(), this);
    }



    public void removeSensor(String deviceId, String sensorId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensorId);
        RequestHelper.getInstance(mContext).removeSensor(param, this);
    }
}
