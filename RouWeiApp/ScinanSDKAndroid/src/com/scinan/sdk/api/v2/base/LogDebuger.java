/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.base;


import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import org.json.JSONObject;

import java.util.TreeMap;

/**
 * Created by lijunjie on 16/6/14.
 */
public class LogDebuger {

    public static void send(String log) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("level", "2");
        param.put("log", log);
        RequestHelper.getInstance(Configuration.getContext()).saveLog(param, null);
    }

    public static void sendCrash(String log) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("level", "0");
        param.put("log", log);
        RequestHelper.getInstance(Configuration.getContext()).saveLog(param, null);
    }

    public static void sendError(String log) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("level", "1");
        param.put("log", log);
        RequestHelper.getInstance(Configuration.getContext()).saveLog(param, null);
    }

    public static void getLogSwitch() {
        RequestHelper.getInstance(Configuration.getContext()).getLogSwitch(null, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                BuildConfig.LOG_TRACE_LEVEL = 1;
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
                BuildConfig.LOG_TRACE_LEVEL = 0;
            }
        });
    }

    public static void getSSLCheck() {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("mobile_model", android.os.Build.MODEL);
        RequestHelper.getInstance(Configuration.getContext()).getSSLCheck(param, new FetchDataCallback() {
            @Override
            public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject(JsonUtil.parseV2JsonResult(responseBody));
                    boolean support = jsonObject.getInt("state") == 0;
                    LogUtil.t("report ssl push is support is " + support);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
            }
        });
    }
}
