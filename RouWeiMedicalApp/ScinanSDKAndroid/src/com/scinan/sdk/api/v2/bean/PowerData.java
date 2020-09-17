/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

import java.io.Serializable;

/**
 * Created by lijunjie on 16/1/25.
 */
public class PowerData implements Serializable {

    String id;
    String date;
    String data;
    String time_data;
    String device_id;
    String sensor_id;
    String create_time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public double getDoubleData() {
        try {
            return Double.valueOf(data).doubleValue() / 3600000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTime_data() {
        return time_data;
    }

    public void setTime_data(String time_data) {
        this.time_data = time_data;
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

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
