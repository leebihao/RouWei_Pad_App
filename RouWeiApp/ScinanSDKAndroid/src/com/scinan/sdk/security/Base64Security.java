/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.security;

import java.io.ByteArrayInputStream;

import android.util.Base64;
import android.util.Base64InputStream;

public class Base64Security {

    public static String decode(String str) {
        byte[] buffer = new byte[getBufferSize(str)];
        try {
            new Base64InputStream(new ByteArrayInputStream(str.getBytes()),
                    Base64.DEFAULT).read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(buffer);
    }

    public static int getBufferSize(String s) {
        int size = s.length() / 4 * 3;
        int equalFlagIndex = s.indexOf('=');
        if (equalFlagIndex != -1)
            size -= (s.length() - equalFlagIndex);
        return size;
    }
}
