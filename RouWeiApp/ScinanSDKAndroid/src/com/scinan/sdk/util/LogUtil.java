/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.text.TextUtils;
import android.util.Log;

import com.scinan.sdk.api.v2.base.LogDebuger;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.contants.Constants;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

/**
 * 日志工具类 会自动在Log中增加类名方法名
 *
 * @author wright
 */
public class LogUtil {

    private static final String TAG = Constants.LOG_TAG;
    private static final String DEFAULT_MSG = Constants.LOG_DEFAULT_MSG;
    private static final int MAX_ENABLED_LOG_LEVEL = BuildConfig.LOG_DEBUG ? Log.VERBOSE : Log.ERROR;

    public static boolean isLoggable(int level) {
        boolean log =  MAX_ENABLED_LOG_LEVEL <= level || isTrace();
//        if (!log) {
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        return log;
    }

    public static void i(String msg) {
        if (isLoggable(Log.INFO)) {
            log('i', TAG, msg);
        }
    }

    public static void d(String msg) {
        if (isLoggable(Log.DEBUG)) {
            log('d', TAG, msg);
        }
    }


    //该方法必须在CrashHandler中调用
    public static void c(Throwable e) throws Exception {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

        if (trace.length > 2 && trace[1].getFileName().equals("CrashHandler.java")) {
            StringBuilder sb = new StringBuilder();
            sb.append("===============Crash Begin===============").append("\n");

            sb.append("---------------------Crash Device Info---------------------").append("\n");
            //收集设备参数信息
            sb.append(AndroidUtil.getSDKBuildInfo());
            sb.append("---------------------Crash Device End---------------------").append("\n");

            sb.append(getExceptionString(e));

            sb.append("===============Crash End===============").append("\n");
            log('c', TAG, sb.toString());
            return;
        }

        throw new Exception("LogUtil.c function must called in sdk");
    }

    public static void t(String msg) {
        if (isLoggable(Log.DEBUG)) {
            log('t', TAG, msg);
        }
    }

    public static void t(Throwable e) {
        t(getExceptionString(e));
    }

    public static void e(String msg) {
        if (isLoggable(Log.ERROR)) {
            log('e', TAG, msg);
        }
    }

    public static void e(Throwable e) {
        e(getExceptionString(e));
    }

    public static String getExceptionString(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }

        return sw == null ? "" : sw.toString();
    }

    public static void v(String msg) {
        if (isLoggable(Log.VERBOSE)) {
            log('v', TAG, msg);
        }
    }

    public static void w(String msg) {
        if (isLoggable(Log.WARN)) {
            log('w', TAG, msg);
        }
    }

    public static void wtf(String msg) {
        if (isLoggable(Log.ASSERT)) {
            log('a', TAG, msg);
        }
    }

    private static void log(char flag, String tag, String msg) {
        String logMsg = null;
        final int maxLength = 3500;
        for (int start = 0; start < msg.length(); start += maxLength) {
            if (start + maxLength < msg.length()) {
                logMsg = checkMsg(msg.substring(start, start + maxLength));
            } else {
                logMsg = checkMsg(msg.substring(start, msg.length()));
            }
            logMsg = buildMessage(logMsg);
            switch (flag) {
                case 'v':
                    Log.v(tag, logMsg);
                    break;
                case 'i':
                    Log.i(tag, logMsg);
                    break;
                case 'c':
                case 't':
                case 'd':
                    Log.d(tag, logMsg);
                    break;
                case 'e':
                    Log.e(tag, logMsg);
                    break;
                case 'w':
                    Log.w(tag, logMsg);
                    break;
                default:
                    Log.wtf(tag, logMsg);
                    break;
            }
            if (BuildConfig.LOG_WRITE || flag == 'c')
                LogFile.writeLog(tag + "==" + logMsg);
            if (isTrace() && AndroidUtil.isNetworkEnabled(Configuration.getContext()) && (flag == 't')) {
                LogDebuger.send(tag + "==" + logMsg);
            }
            if (flag == 'c' && !AndroidUtil.isAppBuildDebug()) {
                LogDebuger.sendCrash(logMsg);
            }
        }
    }

    public static String checkMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = DEFAULT_MSG;
        }
        return msg;
    }

    private static String buildMessage(String msg) {
        StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();
        String caller = "<unknown>";
        String className = null;
        for (int i = 3; i < trace.length; i++) {
            className = trace[i].getClassName();
            if (!className.equals(LogUtil.class.getName())) {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
        }
        return String.format(Locale.US, "[%d][%d][%s] %s: %s", android.os.Process.myPid(), Thread.currentThread().getId(), AndroidUtil.getVersionName2(), caller, msg);
    }

    public static boolean isTrace() {
        return BuildConfig.LOG_TRACE_LEVEL > 0;
    }
}
