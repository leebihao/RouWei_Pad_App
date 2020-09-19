/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.os.PowerManager;

import com.scinan.sdk.util.LogUtil;

/**
 * Created by lijunjie on 15/12/28.
 */
public class TimerWakeLock {

    private static PowerManager.WakeLock mWakeLock;

    private static Object wakeLockObject = new Object();

    public static void acquireWakeLock(Context context) {
        if (mWakeLock == null) {
            synchronized (wakeLockObject) {
                if (mWakeLock == null) {
                    PowerManager pm = (PowerManager) context
                            .getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "ScinanSDK TimerWakeLock");
                    mWakeLock.setReferenceCounted(false);
                }
            }
        }

        if (mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock.acquire();
        } else {
            mWakeLock.acquire();
        }
        LogUtil.d("ConnectWakeLock acquireWakeLock");
    }

    public static void releaseWakeLock() {
        LogUtil.d("ConnectWakeLock releaseWakeLock");
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

}
