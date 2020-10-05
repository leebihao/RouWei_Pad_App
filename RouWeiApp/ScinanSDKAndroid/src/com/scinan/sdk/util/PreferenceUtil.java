/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.scinan.sdk.api.v2.bean.User;
import com.scinan.sdk.bean.Account;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.security.AESSecurity;

/**
 * Created by lijunjie on 15/12/17.
 */
public class PreferenceUtil {

    public static final String PERFERENCE_SCINAN_SDK = "SCINAN-SDK";
    public static final String KEY_ACCOUNT_USER_NAME = "login_user_name";
    public static final String KEY_ACCOUNT_USER_PASSWORD = "login_password";
    public static final String KEY_ACCOUNT_TOKEN = "token";
    public static final String KEY_ACCOUNT_OPEN_ID = "qq_openid";
    public static final String KEY_ACCOUNT_SAVE_PASSWORD = "save_password";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_NICK_NAME = "user_nickname";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_MOBILE = "user_mobile";
    public static final String KEY_USER_PHONE = "user_phone";
    public static final String KEY_USER_AVATAR_URL = "user_avatar";
    public static final String KEY_IMEI = "phone_imei";

    public static final String KEY_COUNTR_AREA_CODE = "area_code";
    public static final String KEY_COUNTR_AREA_NAME = "area_name";

    public static final String KEY_PUSH_MESSAGE_COUNT = "push_msgcount";
    public static final String KEY_PUSH_SSL_SUPPORT = "push_ssl";

    //连接服务的id
    public static final String KEY_CONNECT_SERVICE_ID = "connect_service_id";

    public static final String KEY_HEADER_SSID_PWD = "ssid_";


    public static SharedPreferences getDefaultPreference(Context context) {
        Context global = Configuration.getContext();
        if (global != null) {
            context = global;
        }
        return context.getSharedPreferences(PERFERENCE_SCINAN_SDK, Context.MODE_PRIVATE);
    }

    public static Account getAccount(Context context) {
        String name = getString(context, KEY_ACCOUNT_USER_NAME);
        String pwd = getString(context, KEY_ACCOUNT_USER_PASSWORD);
        String token = getString(context, KEY_ACCOUNT_TOKEN);
        String openId = getString(context, KEY_ACCOUNT_OPEN_ID);
        String save = getString(context, KEY_ACCOUNT_SAVE_PASSWORD);
        return new Account(name, pwd, token, openId, null, save);
    }

    public static User getUser(Context context) {
        String id = getString(context, KEY_USER_ID);
        String name = getString(context, KEY_USER_NAME);
        String nickName = getString(context, KEY_USER_NICK_NAME);
        String email = getString(context, KEY_USER_EMAIL);
        String mobile = getString(context, KEY_USER_MOBILE);
        String phone = getString(context, KEY_USER_PHONE);
        String avatar = getString(context, KEY_USER_AVATAR_URL);
        User user = new User();
        user.setId(id);
        user.setUser_name(name);
        user.setUser_nickname(nickName);
        user.setUser_email(email);
        user.setUser_mobile(mobile);
        user.setUser_phone(phone);
        user.setAvatar(avatar);
        return user;
    }

    public static void saveUser(Context context, User user) {
        String[] data = new String[]{user.getId(), user.getUser_name(), user.getUser_nickname(), user.getUser_email(), user.getUser_mobile(), user.getUser_phone(), user.getAvatar()};
        String[] keys = new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_NICK_NAME, KEY_USER_EMAIL,KEY_USER_MOBILE,KEY_USER_PHONE, KEY_USER_AVATAR_URL};
        saveStringList(context, keys, data);
    }

    public static void clearDefault(Context context) {
        getDefaultPreference(context).edit().clear().commit();
    }

    public static void removeAccount(Context context) {
        saveString(context, KEY_ACCOUNT_TOKEN, "");
        String save = getString(context, KEY_ACCOUNT_SAVE_PASSWORD);
        if (!TextUtils.isEmpty(save) && !Boolean.valueOf(save)) {
            saveString(context, KEY_ACCOUNT_USER_PASSWORD, "");
            saveString(context, KEY_ACCOUNT_SAVE_PASSWORD, "");
        }
        String[] keys = new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_NICK_NAME, KEY_USER_EMAIL,KEY_USER_MOBILE,KEY_USER_PHONE, KEY_USER_AVATAR_URL};
        for (String userKey : keys) {
            saveString(context, userKey, "");
        }
    }

    public static void rmAccountByChangPW(Context context) {
        saveString(context, KEY_ACCOUNT_TOKEN, "");
        saveString(context, KEY_ACCOUNT_USER_PASSWORD, "");
    }

    public static void saveAccount(Context context, Account account) {
        String[] data = new String[]{account.getUserName(), account.getPasswd(), account.getToken(), account.getQQOpenId(), account.getSavePasswd()};
        String[] keys = new String[]{KEY_ACCOUNT_USER_NAME, KEY_ACCOUNT_USER_PASSWORD, KEY_ACCOUNT_TOKEN, KEY_ACCOUNT_OPEN_ID, KEY_ACCOUNT_SAVE_PASSWORD};
        saveStringList(context, keys, data);
    }

    public static String getPwdBySsid(Context context, String ssid) {
        return getString(context, KEY_HEADER_SSID_PWD + ssid);
    }

    public static void saveSsidPwd(Context context, String ssid, String pwd) {
        saveString(context, KEY_HEADER_SSID_PWD + ssid, pwd);
    }

    public static String getAreaCode(Context context) {
        String code = getString(context, KEY_COUNTR_AREA_CODE);
        return TextUtils.isEmpty(code) ? "+86" : code;
    }

    public static void saveAreaCode(Context context, String code) {
        saveString(context, KEY_COUNTR_AREA_CODE, code);
    }

    public static String getCountryName(Context context) {
        String name = getString(context, KEY_COUNTR_AREA_NAME);
        return TextUtils.isEmpty(name) ? context.getString(SNResource.getInstance(context).string("china")) : name;
    }

    public static void saveCountryName(Context context, String name) {
        saveString(context, KEY_COUNTR_AREA_NAME, name);
    }

    public static String getIMEI(Context context) {
        return getString(context, KEY_IMEI);
    }

    public static void saveIMEI(Context context, String imei) {
        saveString(context, KEY_IMEI, imei);
    }

    public static String getToken(Context context) {
        return getString(context, KEY_ACCOUNT_TOKEN);
    }

    public static String getPassword(Context context) {
        return getString(context, KEY_ACCOUNT_USER_PASSWORD);
    }

    public static void savePassword(Context context, String password) {
        saveString(context, KEY_ACCOUNT_USER_PASSWORD, password);
    }

    public static String getConnectServiceId(Context context) {
        return getString(context, KEY_CONNECT_SERVICE_ID);
    }

    public static void saveConnectServiceId(Context context, String id) {
        saveString(context, KEY_CONNECT_SERVICE_ID, id);
    }

    public static void saveToken(Context context, String token) {
        saveString(context, KEY_ACCOUNT_TOKEN, token);
    }

    public static int getPushMessageCount(Context context) {
        String count = getString(context, KEY_PUSH_MESSAGE_COUNT);
        if (TextUtils.isEmpty(count))
            return 0;
        return Integer.valueOf(count);
    }

    public static void savePushMessageCount(Context context, int count) {
        saveString(context, KEY_PUSH_MESSAGE_COUNT, String.valueOf(count));
    }

    public static void saveSupportMQTTSSL(Context context, boolean support) {
        saveBoolean(context, KEY_PUSH_SSL_SUPPORT, support);
    }

    public static boolean isSupportMQTTSSL(Context context) {
        return getBoolean(context, KEY_PUSH_SSL_SUPPORT);
    }

    public static synchronized String getString(Context context, String key) {
        return AESSecurity.getReallyValue(getDefaultPreference(context).getString(key, null));
    }

    public static synchronized boolean getBoolean(Context context, String key) {
        return getDefaultPreference(context).getBoolean(key, true);
    }

    public static synchronized void saveString(Context context, String key, String value) {
        LogUtil.d("PreferenceUtil saveString " + key + "=====" + AndroidUtil.getCurProcessName(context));
        SharedPreferences share = getDefaultPreference(context);
        SharedPreferences.Editor editor = share.edit();
        editor.putString(key, AESSecurity.getStoreAccessValue(value));
        editor.commit();
    }

    public static synchronized void saveBoolean(Context context, String key, boolean value) {
        LogUtil.d("PreferenceUtil saveString " + key + "=====" + AndroidUtil.getCurProcessName(context));
        SharedPreferences share = getDefaultPreference(context);
        SharedPreferences.Editor editor = share.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static synchronized void saveStringList(Context context, String[] key, String[] value) {
        LogUtil.d("PreferenceUtil saveStringList " + key + "=====" + AndroidUtil.getCurProcessName(context));
        SharedPreferences share = getDefaultPreference(context);
        SharedPreferences.Editor editor = share.edit();
        for (int i = 0; i < key.length; i++) {
            editor.putString(key[i], AESSecurity.getStoreAccessValue(value[i]));
        }
        editor.commit();
    }

    public static synchronized Long getLong(Context context, String key) {
        return getDefaultPreference(context).getLong(key, -1);
    }

    public static synchronized void saveLong(Context context, String key, Long value) {
        LogUtil.d("PreferenceUtil saveLong " + key + "=====" + AndroidUtil.getCurProcessName(context));
        SharedPreferences share = getDefaultPreference(context);
        SharedPreferences.Editor editor = share.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static synchronized int getInt(Context context, String key) {
        return getDefaultPreference(context).getInt(key, -1);
    }

    public static synchronized void saveInt(Context context, String key, int value) {
        LogUtil.d("PreferenceUtil saveLong " + key + "=====" + AndroidUtil.getCurProcessName(context));
        SharedPreferences share = getDefaultPreference(context);
        SharedPreferences.Editor editor = share.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
