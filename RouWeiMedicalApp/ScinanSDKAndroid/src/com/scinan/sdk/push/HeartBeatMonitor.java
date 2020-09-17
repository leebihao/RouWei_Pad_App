/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.util.LogUtil;

import org.fusesource.hawtdispatch.Dispatch;
import org.fusesource.hawtdispatch.Task;
import org.fusesource.hawtdispatch.transport.ProtocolCodec;
import org.fusesource.hawtdispatch.transport.Transport;

import java.util.concurrent.TimeUnit;

/**
 * <p>A HeartBeatMonitor can be used to watch the read and write
 * activity of a transport and raise events when the write side
 * or read side has been idle too long.</p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class HeartBeatMonitor {

    Transport transport;
    Context context;
    AlarmManager alarm;
    PendingIntent pendingIntent;
    long initialWriteCheckDelay;
    long initialReadCheckDelay;
    long writeInterval;
    long readInterval;

    Task onKeepAlive = Dispatch.NOOP;
    Task onRespTask = Dispatch.NOOP;
    Task onDead = Dispatch.NOOP;

    volatile short session = 0;

    boolean readSuspendedInterval;
    short readSuspendCount;

    Object lock = new Object();

    public HeartBeatMonitor(Context context) {
        this.context = context;
    }

    public void suspendRead() {
        readSuspendCount++;
        readSuspendedInterval = true;
    }

    public void resumeRead() {
        readSuspendCount--;
    }

    private void schedule(final short session, long interval, final Task func) {
        if (this.session == session) {
            transport.getDispatchQueue().executeAfter(interval, TimeUnit.MILLISECONDS, new Task() {
                public void run() {
                    synchronized (lock) {
                        if (HeartBeatMonitor.this.session == session) {
                            func.run();
                        }
                    }
                }
            });
        }
    }

    private void scheduleCheckWrites(final short session) {
        final ProtocolCodec codec = transport.getProtocolCodec();
        Task func;
        if (codec == null) {
            func = new Task() {
                public void run() {
                    scheduleCheckWrites(session);
                }
            };
        } else {
            final long lastWriteCounter = codec.getWriteCounter();
            func = new Task() {
                public void run() {
                    if (lastWriteCounter == codec.getWriteCounter()) {
                        onKeepAlive.run();
                    }
                    scheduleCheckWrites(session);
                }
            };
        }
        //schedule(session, writeInterval, func);
        startAlarm();
    }

    private void scheduleCheckReads(final short session) {
        final ProtocolCodec codec = transport.getProtocolCodec();
        Task func;
        if (codec == null) {
            func = new Task() {
                public void run() {
                    scheduleCheckReads(session);
                }
            };
        } else {
            final long lastReadCounter = codec.getReadCounter();
            func = new Task() {
                public void run() {
                    if (lastReadCounter == codec.getReadCounter() && !readSuspendedInterval && readSuspendCount == 0) {
                        onDead.run();
                    }
                    readSuspendedInterval = false;
                    scheduleCheckReads(session);
                }
            };
        }
        schedule(session, readInterval, func);
    }

    public void start() {
        session++;
        readSuspendedInterval = false;
        if (writeInterval != 0) {
            if (initialWriteCheckDelay != 0) {
                transport.getDispatchQueue().executeAfter(initialWriteCheckDelay, TimeUnit.MILLISECONDS, new Task() {
                    public void run() {
                        scheduleCheckWrites(session);
                    }
                });
            } else {
                scheduleCheckWrites(session);
            }
        }
        if (readInterval != 0) {
            if (initialReadCheckDelay != 0) {
                transport.getDispatchQueue().executeAfter(initialReadCheckDelay, TimeUnit.MILLISECONDS, new Task() {
                    public void run() {
                        scheduleCheckReads(session);
                    }
                });
            } else {
                scheduleCheckReads(session);
            }
        }
    }

    public void stop() {
        synchronized (lock) {
            session = 0;
            cancelAlarm();
        }
    }


    public long getInitialReadCheckDelay() {
        return initialReadCheckDelay;
    }

    public void setInitialReadCheckDelay(long initialReadCheckDelay) {
        this.initialReadCheckDelay = initialReadCheckDelay;
    }

    public long getInitialWriteCheckDelay() {
        return initialWriteCheckDelay;
    }

    public void setInitialWriteCheckDelay(long initialWriteCheckDelay) {
        this.initialWriteCheckDelay = initialWriteCheckDelay;
    }

    public Task getOnDead() {
        return onDead;
    }

    public void setOnDead(Task onDead) {
        this.onDead = onDead;
    }

    public Task getOnKeepAlive() {
        return onKeepAlive;
    }

    public void setOnKeepAlive(Task onKeepAlive) {
        this.onKeepAlive = onKeepAlive;
    }

    public void setPingResp(Task task) {
        this.onRespTask = task;
    }

    public long getWriteInterval() {
        return writeInterval;
    }

    public void setWriteInterval(long writeInterval) {
        this.writeInterval = writeInterval;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public long getReadInterval() {
        return readInterval;
    }

    public void setReadInterval(long readInterval) {
        this.readInterval = readInterval;
    }

    public void startAlarm() {
        // Instance the alarm.
        cancelAlarm();
        Intent alarmIntent = new Intent(Constants.ACTION_START_PUSH_HEARTBEAT);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), writeInterval, pendingIntent);
        LogUtil.d("The push alarmIntent intent will be send after " + writeInterval / 1000 + " seconds... ");
        HeartBeatWakeLock.acquireWakeLock(context);
    }

    public void cancelAlarm() {
        if (null != alarm && null != pendingIntent) {
            LogUtil.d("cancel push alarm");
            alarm.cancel(pendingIntent);
            pendingIntent = null;
            alarm = null;
        }
    }

    public void runTask() {
        LogUtil.d("================");
        onKeepAlive.run();
    }

    public void runPingRespTask() {
        LogUtil.d("================");
        onRespTask.run();
    }
}
