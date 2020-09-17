/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.scinan.sdk.ui.widget.AppToast;

/**
 * Created by lijunjie on 15/12/24.
 */

public class ToastUtil {

    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static Object mLockObject = new Object();
    private static Toast mToast = null;

    public static void showMessage(final Context context, final int msg) {
        showMessage(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(final Context context, final String msg) {
        showMessage(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showMessage(final Context context, final int msg, final int len) {
        showMessage(context, context.getString(msg), len);
    }

    public static void showMessage(final Context context, final String msg, final int len) {
        new Thread(new Runnable() {
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mLockObject) {
                            showToast(context, msg, len);
                        }
                    }
                });
            }
        }).start();
    }

    private static void showToast(final Context context, final String msg, final int len) {
//        if (mToast != null) {
//            mToast.cancel();
//            mToast.setText(msg);
//            mToast.setDuration(len);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mToast.show();
//                }
//            }, Toast.LENGTH_SHORT);
//        } else {
//            mToast = Toast.makeText(context, msg, len);
//            mToast.show();
//        }
        AppToast.show(context, msg, len);
    }

    public static void cancelCurrentToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
