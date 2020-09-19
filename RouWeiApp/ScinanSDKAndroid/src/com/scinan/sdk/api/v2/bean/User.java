/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

import com.scinan.sdk.util.LogUtil;

import java.io.Serializable;

/**
 * Created by lijunjie on 15/12/8.
 */
public class User implements Serializable {

    String id;
    String user_name;
    String user_nickname;
    String user_email;
    String avatar;
    String user_phone;
    String user_mobile;
    String user_address;
    String user_sex;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_mobile() {
        return user_mobile;
    }

    public void setUser_mobile(String user_mobile) {
        this.user_mobile = user_mobile;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }

    public void log() {
        LogUtil.d("------------------------------------------");
        LogUtil.d("id                  = " + id);
        LogUtil.d("user_name           = " + user_name);
        LogUtil.d("user_nickname       = " + user_nickname);
        LogUtil.d("user_email          = " + user_email);
        LogUtil.d("avatar              = " + avatar);
        LogUtil.d("user_phone          = " + user_phone);
        LogUtil.d("user_mobile         = " + user_mobile);
        LogUtil.d("user_address        = " + user_address);
        LogUtil.d("user_sex            = " + user_sex);
        LogUtil.d("------------------------------------------");
    }
}
