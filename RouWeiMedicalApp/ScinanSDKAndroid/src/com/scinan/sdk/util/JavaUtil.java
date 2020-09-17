/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Created by lijunjie on 15/12/7.
 */
public class JavaUtil {

    public static void removeMapValueNULL(TreeMap<String, String> data) {
        Iterator it = data.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (TextUtils.isEmpty(data.get(key)))
                it.remove();
        }
    }

    public static String getV1Token(String str) {
        if (str != null) {
            int yes0 = str.indexOf("token");
            int yes1 = str.indexOf("expires_in");
            if ((yes1 != -1) && (yes0 != -1)) {
                int size1 = str.indexOf("token:");
                int size2 = str.indexOf("<br/>");
                return str.substring(size1 + 6, size2).trim();
            }
        }
        return null;
    }

    public static int getHashKeyByValue(Map<Integer, String> map, String value) {
        int key = -1;
        Set set = map.entrySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(value)) {
                key = Integer.valueOf(entry.getKey().toString());
                break;
            }
        }
        return key;
    }

    public static boolean validEmail(String email) {
        Pattern pattern = Build.VERSION.SDK_INT >= 8 ? Patterns.EMAIL_ADDRESS : Pattern
                .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                        + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                        + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
        return pattern.matcher(email.trim()).matches();
    }
}
