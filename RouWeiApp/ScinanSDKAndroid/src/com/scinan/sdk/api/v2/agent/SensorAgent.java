/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.agent;

import android.content.Context;

import com.scinan.sdk.api.v2.base.SensorAPIHelper;

/**
 * Created by lijunjie on 15/12/6.
 */
public class SensorAgent extends SensorAPIHelper {

    public SensorAgent(Context context) {
        super(context.getApplicationContext());
    }
}