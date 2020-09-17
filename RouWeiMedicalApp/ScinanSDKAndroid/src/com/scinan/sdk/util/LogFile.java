/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import com.scinan.sdk.config.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lijunjie on 15/12/29.
 */
public class LogFile {

    public static void writeLog(String str) {
        try {
            String path = "/sdcard/" + Configuration.getContext().getPackageName() + "_" +AndroidUtil.getTimeString(System.currentTimeMillis(), "yyyy-MM-dd") + "_log.txt";
            File file = new File(path);
            if (!file.exists())
                file.createNewFile();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            FileOutputStream out = new FileOutputStream(file, true);
            StringBuffer sb = new StringBuffer();
            sb.append("-----------" + sdf.format(new Date()) + "------------\n");
            sb.append(str + "\n");
            out.write(sb.toString().getBytes("utf-8"));
            out.close();
        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }
    }
}
