/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.base;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.scinan.sdk.api.v2.bean.HardwareUpdateResponse;
import com.scinan.sdk.api.v2.bean.UpdateResponse;
import com.scinan.sdk.api.v2.bean.UpdateStatus;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/8.
 */
public class ToolAPIHelper extends BaseHelper implements Serializable {

    public ToolAPIHelper(Context context) {
        super(context);
    }

    public interface ScinanUpdateListener {
        void onUpdateReturned(int updateStatus, UpdateResponse updateInfo);
    }

    public interface ScinanHardwareUpdateListener {
        void onUpdateReturned(int updateStatus, HardwareUpdateResponse updateInfo);
    }

    public void checkAppUpdate(final ScinanUpdateListener listener) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("version_code", String.valueOf(AndroidUtil.getVersionCode(mContext)));
        param.put("os", "android");
        RequestHelper.getInstance(mContext).getAppUpdate(param, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    UpdateResponse response = JSON.parseObject(jsonObject.optString("result_data"), UpdateResponse.class);
                    if (response != null && response.getVersion_code() > AndroidUtil.getVersionCode(mContext)) {
                        if (listener != null)
                            listener.onUpdateReturned(UpdateStatus.Yes, response);
                    } else {
                        if (listener != null)
                            listener.onUpdateReturned(UpdateStatus.No, response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                if (listener != null)
                    listener.onUpdateReturned(UpdateStatus.Timeout, null);
            }
        });
    }

    public void checkPluginUpdate(final String pluginId, final ScinanUpdateListener listener) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("plugin_id", pluginId);
        param.put("os", "android");
        RequestHelper.getInstance(mContext).getSmartPluginUpdate(param, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    UpdateResponse response = JSON.parseObject(jsonObject.optString("result_data"), UpdateResponse.class);
                    if (listener != null)
                        listener.onUpdateReturned(UpdateStatus.Yes, response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onUpdateReturned(UpdateStatus.No, null);
                }
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                if (listener != null)
                    listener.onUpdateReturned(UpdateStatus.Timeout, null);
            }
        });
    }

    public void getHardwareUpdate(final String deviceId, final ScinanHardwareUpdateListener listener) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        RequestHelper.getInstance(mContext).getHardwareUpdate(param, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    HardwareUpdateResponse response = JSON.parseObject(jsonObject.optString("result_data"), HardwareUpdateResponse.class);
                    if (response != null) {
                        if (listener != null)
                            listener.onUpdateReturned(UpdateStatus.Yes, response);
                    } else {
                        if (listener != null)
                            listener.onUpdateReturned(UpdateStatus.No, response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                if (listener != null)
                    listener.onUpdateReturned(UpdateStatus.Timeout, null);
            }
        });
    }

    public void getSuggestionList() {
        RequestHelper.getInstance(mContext).getSuggestionList(null, this);
    }

    public void addSuggestion(String type, String mobile, String email, String content) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("type", type);
        param.put("mobile", mobile);
        param.put("email", email);
        param.put("content", content);
        RequestHelper.getInstance(mContext).addSuggestion(param, this);
    }

    public void getAir(String cityId, String ip) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("city_id", cityId);
        param.put("ip", ip);
        RequestHelper.getInstance(mContext).getAir(param, this);
    }

    public void getWeatherDetail(String cityCode) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("city_code", cityCode );
        RequestHelper.getInstance(mContext).getWeatherDetail(param, this);
    }

    public void getWeatherDetailForLocation(String location) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("location", location);
        RequestHelper.getInstance(mContext).getWeatherDetail(param, this);
    }

    public void getBootStartAndProtectStart(String vendor) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("vendor_os", vendor);
        param.put("vendor_name", AndroidUtil.getVendorName());
        RequestHelper.getInstance(mContext).getBootStartAndProtectStart(param, this);
    }



    public void uploadBleData(String deviceId,String sensor_id,String sensor_type,String data) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_id", deviceId);
        param.put("sensor_id", sensor_id);
        param.put("sensor_type", sensor_type);
        param.put("data", data);
        RequestHelper.getInstance(mContext).uploadBleData(param, this);
    }

    public void getBleOTA(String device_type,String partition,String version_code) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("device_type", device_type);
        param.put("partition", partition);
        param.put("version_code", version_code);
        RequestHelper.getInstance(mContext).getBleOTA(param, this);
    }

    public void reportBleData(String data_list) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("data_list", data_list);
        RequestHelper.getInstance(mContext).reportBleData(param, this);
    }

}