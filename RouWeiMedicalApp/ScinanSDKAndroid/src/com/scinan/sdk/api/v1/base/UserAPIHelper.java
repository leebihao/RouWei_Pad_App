/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.base;

import android.content.Context;

import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.volley.FetchDataCallback;
import com.scinan.sdk.api.v1.network.RequestHelper;
import com.scinan.sdk.util.MD5HashUtil;
import com.scinan.sdk.volley.Response;

import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by lijunjie on 15/12/6.
 */
public class UserAPIHelper extends BaseHelper implements Serializable {

    public static final String REGISTER_TYPE_MOBILE_ONLY        = "0";
    public static final String REGISTER_TYPE_WEB                = "1";

    public UserAPIHelper(Context context) {
        super(context);
    }

    public void login(String name, String passwd) {
        login(name, passwd, "86", this);
    }

    public void login(String name, String passwd, String areaCode, FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("userId", name);
        param.put("passwd", passwd);
        param.put("redirect_uri", "http://localhost.com:8080/testCallBack.action");
        param.put("response_type", "token");
        if (!"86".equals(areaCode))
            param.put("area_code", areaCode);
        param.put("client_id", Configuration.getAppKey(mContext));
        RequestHelper.getInstance(mContext).login(param, callBack);
    }

    public void bindQQ(String openId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("qq_openid", openId);
        RequestHelper.getInstance(mContext).bindQQ(param, this);
    }

    protected void bindQQ(String openId, FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("qq_openid", openId);
        RequestHelper.getInstance(mContext).bindQQ(param, callBack);
    }

    public void unbindQQ() {
        RequestHelper.getInstance(mContext).unbindQQ(null, this);
    }

    public void register(String email, String passwd) {
        register(email, passwd, REGISTER_TYPE_MOBILE_ONLY, null, null, null);
    }

    public void register(String email, String passwd, String type, String openId, String userName, String nickname) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("email", email);
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("type", type);
        param.put("qq_openid", openId);
        param.put("user_name", userName);
        param.put("user_nickname",nickname);
        RequestHelper.getInstance(mContext).registerEmail(param, this);
    }

    public void changePasswd(String oldPasswd, String newPasswd) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("password", MD5HashUtil.hashCode(newPasswd));
        param.put("oldpassword", MD5HashUtil.hashCode(oldPasswd));
        RequestHelper.getInstance(mContext).changePasswd(param, this);
    }

    public void changeUserName(String newUserName) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("name", newUserName);
        RequestHelper.getInstance(mContext).changeUserName(param, this);
    }

    public void changeEmail(String newEmail) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("email", newEmail);
        RequestHelper.getInstance(mContext).changeEmail(param, this);
    }

    public void changeBasicInfo(String address, String mobile, String name) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("user_address", address);
        param.put("user_phone",mobile);
        param.put("user_name", name);
        RequestHelper.getInstance(mContext).changeBasicInfo(param, this);
    }

    public void uploadAvatar(String name, Response.ErrorListener errorListener, Response.Listener listener, File imageFile) {
        RequestHelper.getInstance(mContext).changeExtendInfo(name, errorListener, listener, imageFile);
    }

    public void resetPwdByEmail(String newEmail) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("email", newEmail);
        RequestHelper.getInstance(mContext).resetPwdByEmail(param, this);
    }

    public void resetPwdByMobile(String passwd, String token, String code) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("valid_token", token);
        param.put("valid_code", code);
        RequestHelper.getInstance(mContext).resetPwdByMobile(param, this);
    }

    public void getUserInfo() {
        getUserInfo(this);
    }

    public void getUserInfo(FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        RequestHelper.getInstance(mContext).getUserInfo(param, this);
    }

    public void bindMobile(String token, String code) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("valid_token", token);
        param.put("valid_code", code);
        RequestHelper.getInstance(mContext).bindMobile(param, this);
    }

    public void sendMobileVerifyCode(String mobile, String areaCode) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("mobile", mobile);
        param.put("type", "0");
        param.put("area_code", areaCode);
        RequestHelper.getInstance(mContext).sendMobileVerifyCode(param, this);
    }

    public void checkQQBind(String openId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("qq_openid", openId);
        RequestHelper.getInstance(mContext).checkQQBind(param, this);
    }

    public void registerMobile(String email, String passwd, String type, String token, String code) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("email", email);
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("type", type);
        param.put("valid_token", token);
        param.put("valid_code", code);
        RequestHelper.getInstance(mContext).registerMobile(param, this);
    }
}
