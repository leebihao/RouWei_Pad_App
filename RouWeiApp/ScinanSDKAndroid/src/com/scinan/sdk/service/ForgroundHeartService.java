/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.push.HeartBeatWakeLock;
import com.scinan.sdk.util.LogUtil;

/**
 * Created by lijunjie on 16/6/12.
 */
public class ForgroundHeartService extends Service {

    PendingIntent pendingIntent;
    AlarmManager alarm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startAlarm();
    }

    public void startAlarm() {
        // Instance the alarm.
        cancelAlarm();
        Intent alarmIntent = new Intent(Constants.ACTION_START_PUSH_CONNECT);
        alarmIntent.setPackage(Configuration.getContext().getPackageName());
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 10000, pendingIntent);
        LogUtil.t("The push alarmIntent intent will be send after 10 second... ");
        HeartBeatWakeLock.acquireWakeLock(this);
    }

    public void cancelAlarm() {
        if (null != alarm && null != pendingIntent) {
            LogUtil.d("cancel push alarm");
            alarm.cancel(pendingIntent);
            pendingIntent = null;
            alarm = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelAlarm();
    }
}
