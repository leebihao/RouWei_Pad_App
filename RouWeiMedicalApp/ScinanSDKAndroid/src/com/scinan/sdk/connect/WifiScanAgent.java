/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.error.ErrorCode;
import com.scinan.sdk.interfaces.WifiScanResultCallback;
import com.scinan.sdk.util.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijunjie on 15/12/10.
 */
public class WifiScanAgent {

    private Context mContext;
    private static WifiScanAgent sInstance;
    private WifiManager mWifiManager;

    private String[] mKeyWords;
    private WifiScanResultCallback mCallback;

    private WifiScanAgent(Context context) {
        mContext = context.getApplicationContext();
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiScanAgent getInstance(Context context) {
        if (sInstance == null) {
            synchronized (WifiScanAgent.class) {
                if (sInstance == null) {
                    sInstance = new WifiScanAgent(context);
                }
            }
        }
        return sInstance;
    }

    private boolean checkWifiStatus() {
        if (!mWifiManager.isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(true);
        }
        return true;
    }

    private boolean checkPermission(String permission) {
        return AndroidUtil.checkPermission(Configuration.getContext(), permission);
    }

    private void registerWifiReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(mWifiReceiver, filter);
    }

    private void unregisterWifiReceiver() {
        mContext.unregisterReceiver(mWifiReceiver);
    }

    private boolean isHopeSSID(String ssid) {
        if (TextUtils.isEmpty(ssid)) {
            return false;
        }
        for (String keyword : mKeyWords) {
            if (ssid.toUpperCase().startsWith(keyword.toUpperCase())) {
                return true;
            }
        }

        return false;
    }

    private void filterScanResults(List<ScanResult> all) {
        if (all == null) {
            mCallback.onFail(ErrorCode.REQUIRE_PERMISSION_ACCESS_WIFI_STATE);
            return;
        }
        if (mKeyWords != null) {
            List<ScanResult> results = new ArrayList<ScanResult>();
            for (ScanResult result : all) {
                if (isHopeSSID(result.SSID))
                    results.add(result);
            }
            mCallback.onSuccess(all, results);
        } else {
           mCallback.onSuccess(all, all);
        }
    }

    public void startScan(WifiScanResultCallback callback) {
        startScan(null, callback);
    }

    public void startScan(String[] keywords, WifiScanResultCallback callback) {
        mKeyWords = keywords;
        mCallback = callback;

        if (!checkWifiStatus()) {
            if (checkPermission(Manifest.permission.CHANGE_WIFI_STATE)) {
                mCallback.onFail(ErrorCode.NEED_TO_OPEN_WIFI);
            } else {
                mCallback.onFail(ErrorCode.REQUIRE_PERMISSION_CHANGE_WIFI_STATE);
            }
            return;
        }

        registerWifiReceiver();
        mWifiManager.startScan();
    }

    public WifiManager getWifiManager() {
        return mWifiManager;
    }

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterWifiReceiver();
            if (!checkPermission(Manifest.permission.ACCESS_WIFI_STATE)) {
                mCallback.onFail(ErrorCode.REQUIRE_PERMISSION_ACCESS_WIFI_STATE);
                return;
            }
            filterScanResults(mWifiManager.getScanResults());

        }
    };
}
