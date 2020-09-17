package com.scinan.sdk.alive;

import android.content.Context;
import android.os.CountDownTimer;

import com.scinan.sdk.connect.TimerWakeLock;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 作者：Yann.Yang on 2017/11/23 11:41
 * 邮箱：yannyang@scinan.com
 * 说明：
 */

public class FiveSecondTimer {

    private static FiveSecondTimer sFiveSeconeTimerCount = null;
    private CopyOnWriteArrayList<FiveSeconeTimerCallback> mFetchDataListeners;
    private Context mContext;
    private TimeOutDownTimer timeOutDownTimer;

    private FiveSecondTimer(Context context) {
        this.mContext = context.getApplicationContext();
        mFetchDataListeners = new CopyOnWriteArrayList<FiveSeconeTimerCallback>();

        timeOutDownTimer = new TimeOutDownTimer(5000, 5000);
    }

    public static synchronized FiveSecondTimer getInstance(Context context) {
        if (sFiveSeconeTimerCount == null)
            sFiveSeconeTimerCount = new FiveSecondTimer(context.getApplicationContext());
        return sFiveSeconeTimerCount;
    }

    public void registerFiveSecondTimerListener(FiveSeconeTimerCallback listener) {
        if (mFetchDataListeners.contains(listener)) {
            return;
        }

        mFetchDataListeners.add(listener);

        if (mFetchDataListeners.size() == 1) {
            TimerWakeLock.acquireWakeLock(mContext);
            timeOutDownTimer.start();
        }
    }

    public void unRegisterFiveSecondTimerListener(FiveSeconeTimerCallback listener) {
        if (!mFetchDataListeners.contains(listener)) {
            return;
        }

        mFetchDataListeners.remove(listener);

        if (mFetchDataListeners.size() == 0) {
            TimerWakeLock.releaseWakeLock();
            timeOutDownTimer.cancel();
        }
    }


    public interface FiveSeconeTimerCallback {
        void onTick();
    }

    protected class TimeOutDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeOutDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }


        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            notifyCallbacks();
            timeOutDownTimer.start();
        }
    }

    private void notifyCallbacks() {
        for (FiveSeconeTimerCallback callback : mFetchDataListeners) {
            callback.onTick();
        }
    }
}
