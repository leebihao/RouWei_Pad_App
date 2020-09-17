/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.interfaces;

/**
 * Created by lijunjie on 15/12/27.
 */
public interface PushCallback {

    void onConnected();
    void onError();
    void onClose();
    void onPush(String msg);
    void onData(String msg);
}
