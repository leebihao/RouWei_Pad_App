/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.base;

import android.content.Context;

import com.scinan.sdk.api.v1.bean.Device;
import com.scinan.sdk.api.v1.network.RequestHelper;
import com.scinan.sdk.volley.Response;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/8.
 */
public class DeviceAPIHelper extends BaseHelper implements Serializable {

    public DeviceAPIHelper(Context context) {
        super(context);
    }

    public void getDeviceList() {
        RequestHelper.getInstance(mContext).getDeviceList(null, this);
    }

    public void getDeviceStatus(String deviceId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        RequestHelper.getInstance(mContext).getDeviceStatus(param, this);
    }

    public void getDeviceIP(String deviceId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        RequestHelper.getInstance(mContext).getDeviceIP(param, this);
    }

    public void addDevice(Device device) {
        RequestHelper.getInstance(mContext).addDevice(device.getAddDeviceTree(), this);
    }

    public void editDevice(Device device) {
        RequestHelper.getInstance(mContext).editDevice(device.getAddDeviceTree(), this);
    }

    public void removeDevice(String deviceId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        RequestHelper.getInstance(mContext).removeDevice(param, this);
    }

    public void changeDeviceType(String deviceId, String type) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("type", type);
        RequestHelper.getInstance(mContext).changeDeviceType(param, this);
    }

    public void shareDevice(String deviceId, String email, String mobile, String areaCode) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("mobile", mobile);
        param.put("area_code", areaCode);
        param.put("email", email);
        RequestHelper.getInstance(mContext).shareDevice(param, this);
    }

    public void getDeviceShareList(String deviceId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        RequestHelper.getInstance(mContext).getDeviceShareList(param, this);
    }

    public void removeDeviceShare(String deviceId, String targetUserId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("target_user_id", targetUserId);
        RequestHelper.getInstance(mContext).removeDeviceShare(param, this);
    }

    public void addDeviceImage(String deviceId, Response.ErrorListener errorListener, Response.Listener listener, File imageFile) {
        RequestHelper.getInstance(mContext).addDeviceImage(deviceId, errorListener, listener, imageFile);
    }
}
