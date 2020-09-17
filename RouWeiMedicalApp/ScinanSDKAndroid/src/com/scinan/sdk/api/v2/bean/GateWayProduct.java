/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Luogical on 2016/4/8.
 */
public class GateWayProduct implements Serializable {
    String product_type;
    String type_name;
    List<SubClass> list  = new ArrayList<SubClass>();

    public List<SubClass> getList() {
        return list;
    }

    public void setList(List<SubClass> list) {
        this.list = list;
    }


    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }


    public String getType_code() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }


}
