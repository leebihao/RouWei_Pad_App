/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.bean;

import java.io.Serializable;

/**
 * Created by Luogical on 2016/4/8.
 */
public class SenceActionList implements Serializable {

    String action_id;

    public String getGateway_device_id() {
        return gateway_device_id;
    }

    public void setGateway_device_id(String gateway_device_id) {
        this.gateway_device_id = gateway_device_id;
    }

    public String getAction_id() {
        return action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public String getAction_code() {
        return action_code;
    }

    public void setAction_code(String action_code) {
        this.action_code = action_code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAction_desc() {
        return action_desc;
    }

    public void setAction_desc(String action_desc) {
        this.action_desc = action_desc;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    String gateway_device_id;
    String action_code;
    String title;
    String action_desc;
    String command;




}
