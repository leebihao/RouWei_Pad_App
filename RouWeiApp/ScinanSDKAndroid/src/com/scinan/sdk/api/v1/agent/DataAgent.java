/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.agent;

import android.content.Context;

import com.scinan.sdk.api.v1.base.DataAPIHelper;

/**
 * Created by lijunjie on 15/12/18.
 */
public class DataAgent extends DataAPIHelper {

    public DataAgent(Context context) {
        super(context.getApplicationContext());
    }
}
