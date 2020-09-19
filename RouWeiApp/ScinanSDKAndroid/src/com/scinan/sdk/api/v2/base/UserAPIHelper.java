/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.base;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.util.MD5HashUtil;
import com.scinan.sdk.volley.FetchDataCallback;
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

    public void login3p(int thirdPartyId, String openid, String passwd, FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("third_party_type", String.valueOf(thirdPartyId));
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("third_party_openid", openid);
        RequestHelper.getInstance(mContext).login3P(param, callBack);
    }

    public void bind3p(int thirdPartyId, String openid, String passwd, String userName, String accessToken, FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("third_party_type", String.valueOf(thirdPartyId));
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("user_name", userName);
        param.put("third_party_openid", openid);
        param.put("third_party_token", accessToken);
        RequestHelper.getInstance(mContext).bind3P(param, callBack);
    }

    public void checkThirdParty(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        RequestHelper.getInstance(mContext).checkThirdParty(params, callBack);
    }

    public void getUserAgreement(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        RequestHelper.getInstance(mContext).getUserAgreement(params, callBack);
    }

    public void thirdRegisterForScinan(String thirdPartyType, String thirdPartyOpenid,
                                       String mobile, String validCode, String ticket,String password) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("third_party_type", thirdPartyType);
        param.put("third_party_openid", thirdPartyOpenid);
        param.put("user_mobile", mobile);
        param.put("valid_code", validCode);
        param.put("ticket", ticket);
        param.put("password", MD5HashUtil.hashCode(password));
        RequestHelper.getInstance(mContext).thirdRegisterForScinan(param, this);
    }

    public void thirdBindForScinan(String thirdPartyType, String thirdPartyOpenid,
                                   String mobile,String password) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("third_party_type", thirdPartyType);
        param.put("third_party_openid", thirdPartyOpenid);
        param.put("account", mobile);
        param.put("password",  MD5HashUtil.hashCode(password));
        RequestHelper.getInstance(mContext).thirdBoundForScinan(param, this);
    }

    public void bind_exist3P(int thirdPartyId, String openid, String passwd, String account, String accessToken, FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("third_party_type", String.valueOf(thirdPartyId));
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("account", account);
        param.put("third_party_openid", openid);
        param.put("third_party_token", accessToken);
        RequestHelper.getInstance(mContext).bind_exist3P(param, callBack);
    }

    public void register3P(int thirdPartyId, String openid, String passwd, String user_mobile, String valid_code, String ticket,FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("third_party_type", String.valueOf(thirdPartyId));
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("user_mobile", user_mobile);
        param.put("third_party_openid", openid);
        param.put("valid_code", valid_code);
        param.put("ticket", ticket);
        RequestHelper.getInstance(mContext).register3P(param, callBack);
    }

    public void get3PList() {
        RequestHelper.getInstance(mContext).get3PList(null, this);
    }

    public void bind_del3P(String thirdPartyId) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("third_party_type", thirdPartyId);
        RequestHelper.getInstance(mContext).bind_del3P(param, this);
    }

    public void login(String name, String passwd) {
        login(name, passwd, this);
    }

    protected void login(String name, String passwd, FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("account", name);
        param.put("password", MD5HashUtil.hashCode(passwd));
        RequestHelper.getInstance(mContext).login(param, callBack);
    }

    protected void login(String name, String areaCode, String passwd, FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("account", name);
        if (!TextUtils.isEmpty(areaCode))
            param.put("area_code", areaCode.replace("+", ""));
        param.put("password", MD5HashUtil.hashCode(passwd));
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

    public void register(String email, String passwd,String source) {
        register(email, passwd, REGISTER_TYPE_WEB, null, null, null, source);
    }

    public void register(String email, String passwd, String type, String openId, String userName, String nickname,String source) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("email", email);
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("type", type);
        param.put("qq_openid", openId);
        param.put("user_name", userName);
        param.put("user_nickname",nickname);
        param.put("source",source);
        RequestHelper.getInstance(mContext).registerEmail(param, this);
    }

    public void changePasswd(String oldPasswd, String newPasswd) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("password", MD5HashUtil.hashCode(newPasswd));
        param.put("old_password", MD5HashUtil.hashCode(oldPasswd));
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

    public void changeBasicInfo(String address, String mobile, String nickName) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("user_address", address);
        param.put("user_phone",mobile);
        param.put("user_nickname", nickName);
        RequestHelper.getInstance(mContext).changeBasicInfo(param, this);
    }

    public void changeBasicInfo_All(String address, String mobile, String nickName,String sex) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("user_address", address);
        param.put("user_phone",mobile);
        param.put("user_nickname", nickName);
        param.put("user_sex", sex);
        RequestHelper.getInstance(mContext).changeBasicInfo(param, this);
    }

    public void uploadAvatar(Response.ErrorListener errorListener, Response.Listener listener, File imageFile) {
        uploadAvatar(errorListener, listener, imageFile, null);
    }

    public void uploadAvatar(Response.ErrorListener errorListener, Response.Listener listener, File imageFile, String nickName) {
        RequestHelper.getInstance(mContext).changeExtendInfo(errorListener, listener, imageFile, nickName);
    }

    public void resetPwdByEmail(String newEmail) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("email", newEmail);
        RequestHelper.getInstance(mContext).resetPwdByEmail(param, this);
    }

    public void resetPwdByMobile(String passwd, String token, String code) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("ticket", token);
        param.put("valid_code", code);
        RequestHelper.getInstance(mContext).resetPwdByMobile(param, this);
    }

    public void getUserInfo() {
        getUserInfo(this);
    }

    protected void getUserInfo(FetchDataCallback callBack) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        RequestHelper.getInstance(mContext).getUserInfo(param, this);
    }

    public void refreshToken() {
        RequestHelper.getInstance(mContext).refreshToken(null, this);
    }

    public void bindMobile(String token, String code) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("ticket", token);
        param.put("valid_code", code);
        RequestHelper.getInstance(mContext).bindMobile(param, this);
    }

    public void sendMobileVerifyCode(String mobile, String areaCode,String type) {
        TreeMap<String, String> param = new TreeMap<String, String>();
        param.put("mobile", mobile);
        param.put("type", type);
        param.put("area_code", areaCode);
        RequestHelper.getInstance(mContext).sendMobileVerifyCode(param, this);
    }

    public void registerMobile( String passwd, String token, String code) {
        TreeMap<String, String> param = new TreeMap<String, String>();
       // param.put("email", email);
        param.put("password", MD5HashUtil.hashCode(passwd));
        param.put("type", REGISTER_TYPE_MOBILE_ONLY);
        param.put("ticket", token);
        param.put("valid_code", code);
        RequestHelper.getInstance(mContext).registerMobile(param, this);
    }

    public void unbindThirdParty(String type) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("third_party_type", type);
        RequestHelper.getInstance(mContext).unbindThirdParty(params, this);
    }
}
