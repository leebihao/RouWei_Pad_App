/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.base;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.scinan.sdk.api.v1.bean.HardwareUpdateResponse;
import com.scinan.sdk.api.v1.bean.UpdateResponse;
import com.scinan.sdk.api.v1.bean.UpdateStatus;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.volley.FetchDataCallback;
import com.scinan.sdk.api.v1.network.RequestHelper;
import com.scinan.sdk.util.AndroidUtil;

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

    public void getAppUpdate(final ScinanUpdateListener listener) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("version", String.valueOf(AndroidUtil.getVersionCode(mContext)));
        param.put("os", "android");
        RequestHelper.getInstance(mContext).getAppUpdate(param, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    UpdateResponse response = JSON.parseObject(jsonObject.optString("result_data"), UpdateResponse.class);
                    if (response != null && response.getVersion() > AndroidUtil.getVersionCode(mContext)) {
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
        param.put("company_id", Configuration.getCompanyId(mContext));
        RequestHelper.getInstance(mContext).addSuggestion(param, this);
    }

    public void getCountryCode() {
        RequestHelper.getInstance(mContext).getCountryCode(null, this);
    }
}
