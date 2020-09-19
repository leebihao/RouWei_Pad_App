package com.scinan.sdk_ext.smartlink;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by lijunjie on 15/12/28.
 */
public class SmartLinkWakeLock {

    private static PowerManager.WakeLock mWakeLock;

    private static Object wakeLockObject = new Object();

    public static void acquireWakeLock(Context context) {
        if (mWakeLock == null) {
            synchronized (wakeLockObject) {
                if (mWakeLock == null) {
                    PowerManager pm = (PowerManager) context
                            .getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "ScinanSDK ConnectWakeLock");
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
    }

    public static void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

}
