/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.bean;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.device.ScinanConnectDevice;
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
    String mstype;
    String product_id;
    String company_id;
    String online;
    String c_timestamp;
    String as_timestamp;
    String tags;
    String gps_name;
    String lon;
    String lat;
    String door_type;
    String public_type;

    public Device() {
    }

    public Device(String id) {
        this.id = id;
    }

    public Device(String id, String type) {
        this.id = id;
        this.type = type;
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
        return title;
    }

    public String getDisplayName(Context context) {
        if (!TextUtils.isEmpty(title)) {
            return title;
        }

        return getOriginTitle(context);
    }

    public String getOriginTitle(Context context) {
        return null;
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

    public boolean isOnline() {
        return "1".equals(online);
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

    public TreeMap<String, String> getAddDeviceTree() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", id);
        param.put("title", title);
        param.put("type", type);
        param.put("about", about);
        param.put("tags", tags);
        param.put("gps_name", gps_name);
        param.put("lon", lon);
        param.put("lat", lat);
        param.put("door_type", door_type);
        param.put("public_type", public_type);
        param.put("mstype", mstype);
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
        LogUtil.d("------------------------------------------");
    }
}
