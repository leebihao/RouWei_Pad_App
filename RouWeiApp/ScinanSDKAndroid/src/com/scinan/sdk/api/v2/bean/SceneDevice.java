/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

import com.scinan.sdk.bean.SmartDevice;
import com.scinan.sdk.util.AndroidUtil;

import java.io.Serializable;

/**
 * Created by lijunjie on 16/5/15.
 */
public class SceneDevice implements Serializable {
    String id;
    String product_id;
    String device_id;
    String title;
    String image;
    String company_id;
    String type;
    String scene_id;

    public SceneDevice() {
    }

    public SmartDevice getSmartDevice() {
        SmartDevice device = new SmartDevice();
        device.setDevice_id(getDevice_id());
        device.setTitle(getTitle());
        device.setCompany_id(getCompany_id());
        device.setType(getType());
        device.setTitle(getTitle());
        return device;
    }

    public SceneDevice(SceneBean bean, SmartDevice device) {
        this.device_id = device.getDevice_id();
        this.title = bean.getTitle();
        this.company_id = device.getCompany_id();
        this.type = device.getType();
        this.scene_id = bean.getScene_id();
    }

    public String getPlugin_id() {
        return AndroidUtil.getPluginId(this.getCompany_id(), this.getType());
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

}
