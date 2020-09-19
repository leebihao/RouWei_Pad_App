/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.bean;

import com.scinan.sdk.util.AndroidUtil;

import java.io.Serializable;

/**
 * Created by lijunjie on 16/4/26.
 */
public class SmartDevice implements Serializable {
    String version_android;
    String company_id;
    String title;
    String product_id;
    String s00;
    String device_id;
    String type;
    String image;
    String online;
    String model; //子设备类型的值，通常是1，2，3，4
    String model_name; //子设备类型的名称
    String mstype;
    String type1_name; //大类
    String type2_name; //小类
    String device_module;//区分是GPRS/WIFI添加设备

    public SmartDevice() {
    }

    public SmartDevice(SmartDevice device) {
        this.version_android = device.getVersion_android();
        this.company_id = device.getCompany_id();
        this.title = device.getTitle();
        this.product_id = device.getProduct_id();
        this.s00 = device.getS00();
        this.device_id = device.getDevice_id();
        this.type = device.getType();
        this.image = device.getImage();
        this.online = device.getOnline();
        this.model = device.getModel();
        this.model_name = device.getModel_name();
    }

    public SmartDevice(String company_id, String type) {
        this.company_id = company_id;
        this.type = type;
    }

    public String getType1_name() {
        return type1_name;
    }

    public void setType1_name(String type1_name) {
        this.type1_name = type1_name;
    }

    public String getType2_name() {
        return type2_name;
    }

    public void setType2_name(String type2_name) {
        this.type2_name = type2_name;
    }


    public String getPlugin_id() {
        return AndroidUtil.getPluginId(getCompany_id(), getType());
    }

    public boolean isOnline() {
        try {
            return Integer.valueOf(getOnline()) == 1;
        } catch (Exception e) {
        }
        return false;
    }

    public String getVersion_android() {
        return version_android;
    }

    public int getVersion() {
        try {
            return Integer.valueOf(version_android);
        } catch (Exception e) {
        }
        return 0;
    }

    public void setVersion_android(String version_android) {
        this.version_android = version_android;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getS00() {
        return s00;
    }

    public void setS00(String s00) {
        this.s00 = s00;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public String getMstype() {
        return mstype;
    }

    public void setMstype(String mstype) {
        this.mstype = mstype;
    }

    public String getDevice_module() {
        return device_module;
    }

    public void setDevice_module(String device_module) {
        this.device_module = device_module;
    }

}
