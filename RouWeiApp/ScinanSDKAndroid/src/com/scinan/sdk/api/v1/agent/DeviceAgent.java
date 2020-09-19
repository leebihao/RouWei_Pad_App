/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.agent;

import android.content.Context;

import com.scinan.sdk.api.v1.base.DeviceAPIHelper;
import com.scinan.sdk.interfaces.UploadImageCallback;
import com.scinan.sdk.volley.Response;
import com.scinan.sdk.volley.VolleyError;

import java.io.File;

/**
 * Created by lijunjie on 15/12/6.
 */
public class DeviceAgent extends DeviceAPIHelper {

    public DeviceAgent(Context context) {
        super(context.getApplicationContext());
    }

    public void uploadDeviceImage(String deviceId, File file, final UploadImageCallback callback) {
        addDeviceImage(deviceId, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (callback != null)
                    callback.onFail(error.getCause().getMessage());
            }
        }, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (callback != null)
                    callback.onSuccess();
            }
        }, file);
    }
}
