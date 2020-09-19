/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

/**
 * Created by Luogical on 2016/4/8.
 */
public class SubDevice extends Device {
    String type;
    String frequency;
    String product_type;
    String product_type_sub;
    String sub_device_id;
    String sub_name;
    String device_desc;
    String gateway_device_id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }


    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getProduct_type_sub() {
        return product_type_sub;
    }

    public void setProduct_type_sub(String product_type_sub) {
        this.product_type_sub = product_type_sub;
    }

    public String getSub_device_id() {
        return sub_device_id;
    }

    public void setSub_device_id(String sub_device_id) {
        this.sub_device_id = sub_device_id;
    }

    public String getSub_name() {
        return sub_name;
    }

    public void setSub_name(String sub_name) {
        this.sub_name = sub_name;
    }

    public String getDevice_desc() {
        return device_desc;
    }

    public void setDevice_desc(String device_desc) {
        this.device_desc = device_desc;
    }

    public String getGateway_device_id() {
        return gateway_device_id;
    }

    public void setGateway_device_id(String gateway_device_id) {
        this.gateway_device_id = gateway_device_id;
    }



}
