/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by mfhj-18 on 15/6/4.
 */
public class BaseResponseInfo implements Serializable {

    private int result_code;
    private String result_message;

    public BaseResponseInfo() {
    }

    public BaseResponseInfo(int retcode, String msg) {
        this.result_code = retcode;
        this.result_message = msg;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public String getResult_message() {
        return result_message;
    }

    public void setResult_message(String result_message) {
        this.result_message = result_message;
    }

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

}
