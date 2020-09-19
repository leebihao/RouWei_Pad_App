/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.security;

import android.text.TextUtils;

import com.scinan.sdk.ndk.JUtils;
import com.scinan.sdk.util.LogUtil;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESSecurity {

    public static String getReallyValue(String storeValue) {
        if (TextUtils.isEmpty(storeValue))
            return "";
        String value = null;
        try {
            value = new String(JUtils.getReallyValue(toByte(storeValue)));
        } catch (Exception e) {
        }
        return value;
    }

    public static String getStoreAccessValue(String reallyValue) {
        if (TextUtils.isEmpty(reallyValue))
            return "";
        String storeValue = null;
        try {
            storeValue = toHex(JUtils.getStoreAccessValue(reallyValue));
        } catch (Exception e) {
            storeValue = reallyValue;
        }
        return storeValue;
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(
                    hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        final String HEX = "0123456789ABCDEF";
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
