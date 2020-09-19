/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lijunjie on 16/4/19.
 */
public class ADPushData implements Serializable, Comparable {

    String title;
    String push_describe;
    String icon_url;
    String msg_id;
    String push_time;
    String image_url;
    String link_url;
    String content;
    int push_type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPush_describe() {
        return push_describe;
    }

    public void setPush_describe(String push_describe) {
        this.push_describe = push_describe;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getPush_time() {
        return push_time;
    }

    public void setPush_time(String push_time) {
        this.push_time = push_time;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPush_type() {
        return push_type;
    }

    public void setPush_type(int push_type) {
        this.push_type = push_type;
    }

    @Override
    public int compareTo(Object another) {
        try {
            return ((ADPushData)another).getPush_time().compareTo(getPush_time());
        } catch (Exception e) {
        }
        return 0;
    }
}
