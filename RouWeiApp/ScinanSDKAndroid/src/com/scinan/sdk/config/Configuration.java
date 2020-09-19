/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.config;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.app.CrashHandler;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.PreferenceUtil;

/**
 * Created by lijunjie on 15/12/7.
 */
public class Configuration {

    private static String APP_KEY;
    private static String APP_SECRET;
    private static String COMPANY_ID;
    private static String IMEI;
    private static String TOKEN;
    private static String TECENT_APPID;
    private static String WEIBO_APPKEY;
    private static String PAY_WEIXIN_APPID;
    private static String PAY_ALI_APPID;
    private static String CONNECT_ID;

    private static Context CONTEXT;

    public static String getAppKey(Context context) {
        if (APP_KEY == null) {
            APP_KEY = AndroidUtil.getAppKey(context);
        }

        return APP_KEY;
    }

    public static void setAppKey(String appKey) {
        APP_KEY = appKey;
    }

    public static void setAppSecret(String appSecret) {
        APP_SECRET = appSecret;
    }

    public static String getAppSecret(Context context) {
        if (APP_SECRET == null) {
            APP_SECRET = AndroidUtil.getAppSecret(context);
        }

        return APP_SECRET;
    }

    public static String getCompanyId(Context context) {
        if (COMPANY_ID == null) {
            COMPANY_ID = AndroidUtil.getCompanyId(context);
        }

        return COMPANY_ID;
    }

    public static String getIMEI(Context context) {
        if (TextUtils.isEmpty(IMEI)) {
            IMEI = AndroidUtil.getIMEI(context);
        }

        return IMEI;
    }

    public static String getTecentAppId(Context context) {
        if (TECENT_APPID == null) {
            TECENT_APPID = AndroidUtil.getTecentAppId(context);
        }

        return TECENT_APPID;
    }

    public static String getWeiboAppkey(Context context) {
        if (WEIBO_APPKEY == null) {
            WEIBO_APPKEY = AndroidUtil.getWeiboAppkey(context);
        }

        return WEIBO_APPKEY;
    }

    public static void setApp(String key, String secret) {
        APP_KEY = key;
        APP_SECRET = secret;
    }

    public static void setWeixinPayAppID(String id) {
        PAY_WEIXIN_APPID = id;
    }

    public static String getWeixinPayAppID() {
        if (PAY_WEIXIN_APPID == null) {
            PAY_WEIXIN_APPID = AndroidUtil.getWeixinPayAppID(getContext());
        }
        return PAY_WEIXIN_APPID;
    }

    public static String getAliPayAppID() {
        if (PAY_ALI_APPID == null) {
            PAY_ALI_APPID = AndroidUtil.getALIPayAppID(getContext());
        }
        return PAY_ALI_APPID;
    }

    public static void setContext(Context context) {
        CONTEXT = context;
        CrashHandler.getInstance().init(CONTEXT);
    }

    public static Context getContext() {
        return CONTEXT;
    }

    public static void setCompanyId(String company) {
        COMPANY_ID = company;
    }

    public static void setToken(String token) {
        TOKEN = token;
    }

    public static String getToken(Context context) {
        if (TOKEN == null) {
            TOKEN = PreferenceUtil.getToken(context);
        }
        return TOKEN;
    }

    public static String getToken() {
        return TOKEN;
    }

    public static String getConnectId() {
        return CONNECT_ID;
    }

    public static void setConnectId(String id) {
        CONNECT_ID = id;
    }
}
