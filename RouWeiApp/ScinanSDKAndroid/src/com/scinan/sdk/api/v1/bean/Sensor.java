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
public class Sensor implements Serializable {
    String id;
    String type;
    String title;
    String about;
    String s_icon;
    String s_position;
    String su_price;
    String su_measure;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    String device_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getS_icon() {
        return s_icon;
    }

    public void setS_icon(String s_icon) {
        this.s_icon = s_icon;
    }

    public String getS_position() {
        return s_position;
    }

    public void setS_position(String s_position) {
        this.s_position = s_position;
    }

    public String getSu_price() {
        return su_price;
    }

    public void setSu_price(String su_price) {
        this.su_price = su_price;
    }

    public String getSu_measure() {
        return su_measure;
    }

    public void setSu_measure(String su_measure) {
        this.su_measure = su_measure;
    }

    public TreeMap<String, String> getAddSensorTree() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", device_id);
        param.put("sensor_id", id);
        param.put("sensor_type", type);
        param.put("sensor_name", title);
        param.put("s_icon", s_icon);
        param.put("s_position", s_position);
        param.put("su_price", su_price);
        param.put("su_measure", su_measure);
        return param;
    }

    public void log() {
        LogUtil.d("------------------------------------------");
        LogUtil.d("sensor_id           = " + id);
        LogUtil.d("type                = " + type);
        LogUtil.d("title               = " + title);
        LogUtil.d("about               = " + about);
        LogUtil.d("s_icon              = " + s_icon);
        LogUtil.d("s_position          = " + s_position);
        LogUtil.d("su_price            = " + su_price);
        LogUtil.d("su_measure          = " + su_measure);
        LogUtil.d("------------------------------------------");
    }
}
