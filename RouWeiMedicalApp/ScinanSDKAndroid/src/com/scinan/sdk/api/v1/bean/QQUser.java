/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.bean;

import com.scinan.sdk.util.LogUtil;

import java.io.Serializable;

/**
 * Created by lijunjie on 15/12/8.
 */
public class QQUser implements Serializable {
    int ret;
    String nickname;
    String gender;
    String province;
    String city;
    String scinanToken;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getScinanToken() {
        return scinanToken;
    }

    public void setScinanToken(String scinanToken) {
        this.scinanToken = scinanToken;
    }

    public void log() {
        LogUtil.d("------------------------------------------");
        LogUtil.d("id                  = " + id);
        LogUtil.d("ret                 = " + ret);
        LogUtil.d("nickName            = " + nickname);
        LogUtil.d("gender              = " + gender);
        LogUtil.d("province            = " + province);
        LogUtil.d("city                = " + city);
        LogUtil.d("------------------------------------------");
    }
}
