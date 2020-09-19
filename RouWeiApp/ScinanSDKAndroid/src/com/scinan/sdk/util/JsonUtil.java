/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by lijunjie on 15/12/7.
 */
public class JsonUtil {

    public static String parseErrorMsg(String error) {
        LogUtil.d("error is " + error);
        String msg = null;
        try {
            JSONObject json = new JSONObject(error);
            msg = json.optString("result_message");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg == null ? error : msg;
    }

    public static int getErrorMsgCode(String error) {
        int code = -1;
        try {
            JSONObject json = new JSONObject(error);
            code = json.optInt("result_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }


    public static int getResultCode(String result) {
        int code = -1;
        try {
            JSONObject json = new JSONObject(result);
            code = json.optInt("result_code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String getToken(String msg) {
        String token = null;
        try {
            JSONObject json = new JSONObject(msg);
            if (!json.isNull("result_data")) {
                token = json.optJSONObject("result_data").optString("access_token");
            } else if (!json.isNull("access_token")) {
                token = json.optString("access_token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public static String parseV2JsonResult(String msg) {
        String result = null;
        try {
            JSONObject json = new JSONObject(msg);
            result = json.getJSONObject("result_data").toString();
        } catch (Exception e) {
            result = parseV2JsonArrayResult(msg);
        }

        if (result == null) {
            result = parseV2JsonStringResult(msg);
        }

        return result == null ? msg : result;
    }

    public static String parseV2JsonArrayResult(String msg) {
        String result = null;
        try {
            JSONObject json = new JSONObject(msg);
            result = json.getJSONArray("result_data").toString();
        } catch (Exception e) {
        }
        return result;
    }

    public static String parseV2JsonStringResult(String msg) {
        String result = null;
        try {
            JSONObject json = new JSONObject(msg);
            result = json.getString("result_data").toString();
        } catch (Exception e) {
        }
        return result;
    }

    public static String parseJsonStringValue(String msg, String key) {
        String result = null;
        try {
            JSONObject json = new JSONObject(msg);
            result = json.getString(key).toString();
        } catch (Exception e) {
        }
        return result;
    }

}
