/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.contants;

/**
 * Created by lijunjie on 15/12/4.
 */
public class Constants {

    // global settings
    public static final int THREAD_SLEEP_TIMELONG                       = 2000;
    public static final int RETRY_TIMES_CHECK                           = 5;
    public static final int RETRY_TIMES_CONNECT                         = 3;

    public static final String DEVICE_TCP_IP                            = "192.168.1.1";
    public static final String DEVICE_UDP_GROUP_IP                      = "224.5.0.7";
    public static final int DEVICE_TCP_PORT                             = 2000;
    public static final int DEVICE_UDP_PORT                             = 4000;

    /* Log Model */
    public static final String LOG_TAG                                  = "ScinanAPI";
    public static final String LOG_DEFAULT_MSG                          = "No msg for this report";

    public static final String ERROR_USERNAME_PWD                       = "用户名或者密码错误";
    public static final String ERROR_NETWORK                            = "网络错误，请检查网络";

    public static final String ACTION_START_PUSH_CONNECT                = "com.scinan.sdk.push.connect";
    public static final String ACTION_START_PUSH_CLOSE                  = "com.scinan.sdk.push.close";
    public static final String ACTION_START_PUSH_HEARTBEAT              = "com.scinan.sdk.push.heartbeat";
    public static final String ACTION_START_PUSH_KEEP_ALIVE             = "com.scinan.sdk.push.keepalive";
    public static final String ACTION_LISTEN_PUSH_STATUS                = "com.scinan.sdk.push.listen";
    public static final String ACTION_PUSH_ALARM                        = "com.scinan.sdk.push.alarm";
    public static final String ACTION_PUSH_AD                           = "com.scinan.sdk.push.ad";



}
