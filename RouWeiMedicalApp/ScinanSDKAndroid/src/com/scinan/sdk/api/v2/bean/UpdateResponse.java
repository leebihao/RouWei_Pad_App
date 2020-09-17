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
public class UpdateResponse implements Serializable {

    int version_code;
    String show_version;
    String ctype;
    String os;
    String content;
    String url;
    int utype;

    public int getVersion_code() {
        return version_code;
    }

    public void setVersion_code(int version_code) {
        this.version_code = version_code;
    }

    public String getShow_version() {
        return show_version;
    }

    public void setShow_version(String show_version) {
        this.show_version = show_version;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUtype() {
        return utype;
    }

    public void setUtype(int utype) {
        this.utype = utype;
    }

    public void log() {
        LogUtil.d("------------------------------------------");
        LogUtil.d("version_code             = " + version_code);
        LogUtil.d("show_version        = " + show_version);
        LogUtil.d("ctype               = " + ctype);
        LogUtil.d("content             = " + content);
        LogUtil.d("url                 = " + url);
        LogUtil.d("os                  = " + os);
        LogUtil.d("utype               = " + utype);
        LogUtil.d("------------------------------------------");
    }
}
