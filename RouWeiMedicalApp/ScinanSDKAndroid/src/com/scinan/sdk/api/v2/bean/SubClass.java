/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

import java.io.Serializable;

/**
 * Created by Luogical on 2016/4/18.
 */
public class SubClass implements Serializable {

    String action_num;
    String type_name;
    String type_code;
    String sub_type_code;

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getAction_num() {
        return action_num;
    }

    public void setAction_num(String action_num) {
        this.action_num = action_num;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getSub_type_code() {
        return sub_type_code;
    }

    public void setSub_type_code(String sub_type_code) {
        this.sub_type_code = sub_type_code;
    }


}
