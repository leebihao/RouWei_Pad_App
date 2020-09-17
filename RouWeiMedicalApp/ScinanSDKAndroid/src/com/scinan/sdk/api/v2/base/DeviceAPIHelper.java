/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.base;

import android.content.Context;

import com.scinan.sdk.api.v2.bean.Device;
import com.scinan.sdk.api.v2.bean.MultiDevicesBean;
import com.scinan.sdk.api.v2.network.RequestHelper;
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

    public void addDevice(Device device) {
        RequestHelper.getInstance(mContext).addDevice(device.getAddDeviceTree(), this);
    }

    public void addMultiDevices(MultiDevicesBean devicesBean) {
        RequestHelper.getInstance(mContext).addMultiDevices(devicesBean.getAddMultiDeviceTree(),this);
    }

    public void editDevice(Device device) {
        RequestHelper.getInstance(mContext).editDevice(device.getEditDeviceTree(), this);
    }

    public void removeDevice(String deviceId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        RequestHelper.getInstance(mContext).removeDevice(param, this);
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

    @Deprecated
    public void getDeviceShareList() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        RequestHelper.getInstance(mContext).getDeviceShareList(param, this);
    }

    public void removeDeviceShare(String deviceId, String targetUserId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("target_user_id", targetUserId);
        RequestHelper.getInstance(mContext).removeDeviceShare(param, this);
    }

    public void getDeviceShareAll() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        RequestHelper.getInstance(mContext).getDevicesShareAll(param, this);
    }


    public void addDeviceImage(String deviceId, Response.ErrorListener errorListener, Response.Listener listener, File imageFile) {
        RequestHelper.getInstance(mContext).addDeviceImage(deviceId, errorListener, listener, imageFile);
    }

    public void getBarcode(String deviceId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("action_type", "01");
        param.put("device_id", deviceId);
        RequestHelper.getInstance(mContext).getBarcode(param, this);
    }

    public void scanBarcode(String bar_code) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("bar_code", bar_code);
        RequestHelper.getInstance(mContext).scanBarcode(param, this);
    }
}
