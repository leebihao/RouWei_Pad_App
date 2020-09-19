/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.interfaces;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by lijunjie on 15/12/10.
 */
public interface WifiScanResultCallback {

    void onSuccess(List<ScanResult> all, List<ScanResult> filter);

    void onFail(int errorCode);
}
