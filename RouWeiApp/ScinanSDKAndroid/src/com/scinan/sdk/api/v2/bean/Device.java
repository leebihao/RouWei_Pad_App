/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

import android.text.TextUtils;

import com.scinan.sdk.util.LogUtil;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/8.
 */
public class Device implements Serializable {
    String id;
    String title;
    String about;
    String type;
    String image;
    String mstype; //主从类型，0是主，1是从
    String product_id; //用户产品ID
    String company_id;
    String online;
    String c_timestamp; //最后一次操作时间
    String as_timestamp; //最后一次全状态时间
    @Deprecated
    String tags; //设备标签
    @Deprecated
    String gps_name;
    @Deprecated
    String lon;
    @Deprecated
    String lat;
    @Deprecated
    String door_type; //室内室外
    @Deprecated
    String public_type; //是否公开
    @Deprecated
    String device_key; //内部使用
    @Deprecated
    String update_time; //内部使用
    @Deprecated
    String create_time; //内部使用
    @Deprecated
    String extend;
    @Deprecated
    String ip;
    @Deprecated
    String status;
    @Deprecated
    String deployment_status;
    @Deprecated
    String electric_quantity;
    String s00;
    String model; //子设备类型的值，通常是1，2，3，4
    String model_name; //子设备类型的名称
    String hardware_version;

    public Device() {
    }

    public Device(Device device) {
        this.id = device.getId();
        this.title = device.getTitle();
        this.about = device.getAbout();
        this.type = device.getType();
        this.image = device.getImage();
        this.mstype = device.getMstype();
        this.product_id = device.getProduct_id();
        this.company_id = device.getCompany_id();
        this.online = device.getOnline();
        this.status = device.getStatus();
        this.s00 = device.getS00();
        this.model = device.getModel();
        this.model_name = device.getModel_name();
    }

    public Device(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getExtend() {
        return extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeployment_status() {
        return deployment_status;
    }

    public void setDeployment_status(String deployment_status) {
        this.deployment_status = deployment_status;
    }

    public String getElectric_quantity() {
        return electric_quantity;
    }

    public void setElectric_quantity(String electric_quantity) {
        this.electric_quantity = electric_quantity;
    }

    public String getS00() {
        return s00;
    }

    public void setS00(String s00) {
        this.s00 = s00;
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

    public String getHardware_version() {
        return hardware_version;
    }

    public void setHardware_version(String hardware_version) {
        this.hardware_version = hardware_version;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getGps_name() {
        return gps_name;
    }

    public void setGps_name(String gps_name) {
        this.gps_name = gps_name;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getDoor_type() {
        return door_type;
    }

    public void setDoor_type(String door_type) {
        this.door_type = door_type;
    }

    public String getPublic_type() {
        return public_type;
    }

    public void setPublic_type(String public_type) {
        this.public_type = public_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        if (!TextUtils.isEmpty(title))
            return title;

        try {
            return id.substring(8).trim().toUpperCase();
        } catch (Exception e) {
        }
        return id;
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

    public String getMstype() {
        return mstype;
    }

    public void setMstype(String mstype) {
        this.mstype = mstype;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getC_timestamp() {
        return c_timestamp;
    }

    public void setC_timestamp(String c_timestamp) {
        this.c_timestamp = c_timestamp;
    }

    public String getAs_timestamp() {
        return as_timestamp;
    }

    public void setAs_timestamp(String as_timestamp) {
        this.as_timestamp = as_timestamp;
    }

    public boolean isOnline() {
        if (TextUtils.isEmpty(getOnline())) {
            return false;
        }

        return Integer.valueOf(getOnline()) == 1;
    }

    public TreeMap<String, String> getAddDeviceTree() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", id);
        param.put("title", title);
        param.put("type", type);
        param.put("model", model);
        param.put("product_id", product_id);
        param.put("hardware_version", hardware_version);
        return param;
    }

    public TreeMap<String, String> getEditDeviceTree() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", id);
        param.put("title", title);
        param.put("about", about);
        param.put("tags", tags);
        return param;
    }

    public void log() {
        LogUtil.d("------------------------------------------");
        LogUtil.d("device_id           = " + id);
        LogUtil.d("title               = " + title);
        LogUtil.d("about               = " + about);
        LogUtil.d("type                = " + type);
        LogUtil.d("image               = " + image);
        LogUtil.d("mstype              = " + mstype);
        LogUtil.d("product_id          = " + product_id);
        LogUtil.d("company_id          = " + company_id);
        LogUtil.d("online              = " + online);
        LogUtil.d("c_timestamp         = " + c_timestamp);
        LogUtil.d("as_timestamp        = " + as_timestamp);
        LogUtil.d("tags                = " + tags);
        LogUtil.d("gps_name            = " + gps_name);
        LogUtil.d("lon                 = " + lon);
        LogUtil.d("lat                 = " + lat);
        LogUtil.d("door_type           = " + door_type);
        LogUtil.d("public_type         = " + public_type);
        LogUtil.d("s00                 = " + s00);
        LogUtil.d("model               = " + model);
        LogUtil.d("model_name          = " + model_name);
        LogUtil.d("hardware_version    = " + hardware_version);
        LogUtil.d("------------------------------------------");
    }
}
