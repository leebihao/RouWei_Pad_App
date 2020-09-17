/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.bean;

import java.io.Serializable;

/**
 * Created by lijunjie on 15/12/17.
 */
public class Account implements Serializable {

    private String mUserName;
    private String mPasswd;
    private String mToken;
    private String mQQOpenId;
    private String mDigst;
    private String mSavePasswd;

    public Account(String name, String pwd, String token, String openId, String digst, String savePwd) {
        this.mUserName = name;
        this.mPasswd = pwd;
        this.mToken = token;
        this.mQQOpenId = openId;
        this.mDigst = digst;
        this.mSavePasswd = savePwd;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public String getPasswd() {
        return mPasswd;
    }

    public void setPasswd(String pwd) {
        this.mPasswd = pwd;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public String getQQOpenId() {
        return mQQOpenId;
    }

    public void setQQOpenId(String openId) {
        this.mQQOpenId = openId;
    }

    public String getDigst() {
        return mDigst;
    }

    public void setDigst(String digst) {
        this.mDigst = digst;
    }

    public String getSavePasswd() {
        return mSavePasswd;
    }

    public void setmSavePasswd(String mSavePasswd) {
        this.mSavePasswd = mSavePasswd;
    }
}
