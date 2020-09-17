/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.push;

import android.content.Context;
import android.os.PowerManager;

import com.scinan.sdk.util.LogUtil;

/**
 * Created by lijunjie on 15/12/28.
 */
public class HeartBeatWakeLock {

    private static PowerManager.WakeLock mWakeLock;

    private static Object wakeLockObject = new Object();

    public static void acquireWakeLock(Context context) {
        if (mWakeLock == null) {
            synchronized (wakeLockObject) {
                if (mWakeLock == null) {
                    PowerManager pm = (PowerManager) context
                            .getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ScinanSDK HeartBeat");
                    mWakeLock.setReferenceCounted(false);
                }
            }
        }

        if (mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock.acquire(5000);
        } else {
            mWakeLock.acquire(5000);
        }
        LogUtil.d("HeartBeatWakeLock acquireWakeLock");
    }

    public static void releaseWakeLock() {
        LogUtil.d("HeartBeatWakeLock releaseWakeLock");
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

}
