/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.ndk;

/**
 * Created by lijunjie on 16/4/21.
 */
public class JUtils {
    static {
        System.loadLibrary("ScinanSecure");
    }
    public native static byte[] getStoreAccessValue(String reallyValue);
    public native static byte[] getReallyValue(byte[] storeValue);
}
