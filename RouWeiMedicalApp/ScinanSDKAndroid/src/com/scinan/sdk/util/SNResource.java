/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.content.Context;
import android.content.res.Resources;

public class SNResource {

    private static SNResource sInstance = null;
    private Resources mResource;
    private Context mContext;
    private final String PACKAGE;
    private final String DRAWABLE = "drawable";
    private final String ID = "id";
    private final String LAYOUT = "layout";
    private final String ANIM = "anim";
    private final String STYLE = "style";
    private final String STRING = "string";
    private final String ARRAY = "array";

    private SNResource(Context context) {
        this.mResource = context.getResources();
        this.PACKAGE = context.getPackageName();
        this.mContext = context;
    }

    public static synchronized SNResource getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SNResource(context.getApplicationContext());
        }

        return sInstance;
    }

    public int anim(String name) {
        return this.getSourceId(name, ANIM);
    }

    public int id(String name) {
        return this.getSourceId(name, ID);
    }

    public int drawable(String name) {
        return this.getSourceId(name, DRAWABLE);
    }

    public int layout(String name) {
        return this.getSourceId(name, LAYOUT);
    }

    public int style(String name) {
        return this.getSourceId(name, STYLE);
    }

    public int string(String name) {
        return this.getSourceId(name, STRING);
    }

    public String getString(String name) {
        return mContext.getString(string(name));
    }

    public int array(String name) {
        return this.getSourceId(name, ARRAY);
    }

    private int getSourceId(String name, String type) {
        int identifier = this.mResource.getIdentifier(name, type, this.PACKAGE);
        if (identifier == 0) {
            LogUtil.e("getRes(" + type + "/ " + name + ")");
            LogUtil.e("Error getting resource. Make sure you have copied all resources (res/) from SDK to your project. ");
            return 0;
        } else {
            return identifier;
        }
    }
}