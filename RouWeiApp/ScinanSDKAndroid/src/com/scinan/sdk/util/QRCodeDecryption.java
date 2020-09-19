/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

/**
 * Created by lijunjie on 15/12/23.
 */

import java.io.PrintStream;
import java.util.Locale;

public class QRCodeDecryption {
    public static void main(String[] args) {
    }

    public static String getEncrypt(String device_id) {
        return encrypt(device_id.replace("-", ""));
    }

    public static String getShowEncrypt(String device_id) {
        String result = encrypt(device_id.replace("-", ""));
        try {
            StringBuffer sb = new StringBuffer();
            sb.append(result.substring(0, 4)).append("-").append(result.substring(4, 8)).append("-")
                    .append(result.substring(8, 12)).append("-").append(result.substring(12, 16));
            result = sb.toString();
        } catch (Exception localException) {
        }
        return result;
    }

    private static String encrypt(String device_id) {
        String result = "-1";

        if (device_id == null)
            return result;

        try {
            LogUtil.d("device_id = " + device_id);
            String str8 = device_id.substring(0, 8);
            String str16 = device_id.substring(8, 16);
            LogUtil.d("str8 = " + str8);
            LogUtil.d("str16 = " + str16);

            int[] encodeNumber = {1, 226, 211, 196, 181, 166, 7, 152};

            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < encodeNumber.length; ++i) {
                String str_temp = str8.substring(i, i + 1) + str16.substring(i, i + 1);
                LogUtil.d("str_temp=" + str_temp);
                int hex = Integer.parseInt(str_temp, 16);
                hex ^= encodeNumber[i];

                String str_hex = Integer.toHexString(hex);
                if (str_hex.length() < 2) {
                    str_hex = "0" + str_hex;
                }

                LogUtil.d("str_hex=" + str_hex);
                sb.append(str_hex);
            }

            result = sb.toString().toUpperCase(Locale.ENGLISH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}