/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;

import com.scinan.sdk.bean.APBean;
import com.scinan.sdk.bluetooth.BLEAdvertising;
import com.scinan.sdk.bluetooth.BLEBluzDevice;
import com.scinan.sdk.bluetooth.IBluzDevice;
import com.scinan.sdk.bluetooth.ScanDeviceResult;
import com.scinan.sdk.bluetooth.ScanFilter;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.util.ByteUtil;
import com.scinan.sdk.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijunjie on 15/12/10.
 */
public class S6120ScanAgent implements IBluzDevice.OnDiscoveryListener {

    private static S6120ScanAgent sInstance;

    private BLEBluzDevice mBLEBluzDevice;

    private S6120ScanCallback mS6120ScanCallback;

    private List<APBean> mTempList;

    public interface S6120ScanCallback {
        //涉及到UI的需要反馈到外部
        boolean on6120ScanPermissions(String[] permissions);
        //涉及到UI的需要反馈到外部
        boolean on6120BTEnable();
        void on6120ScanStart();
        void on6120ScanProgress(APBean apBean, boolean isNew);
        void on6120ScanEnd();
    }

    @Override
    public void onDiscoveryStarted() {
        if (mS6120ScanCallback != null) {
            mS6120ScanCallback.on6120ScanStart();
        }
    }

    @Override
    public void onDiscoveryFinished() {
        if (mS6120ScanCallback != null) {
            mS6120ScanCallback.on6120ScanEnd();
            mS6120ScanCallback = null;
        }
        mBLEBluzDevice.unRegisterOnDiscoveryListener(this);
    }

    @Override
    public void onFound(ScanDeviceResult result) {
        if (mS6120ScanCallback == null) {
            return;
        }

        try {
            BLEAdvertising ad = BLEAdvertising.parse(result.getBle_record());
            String deviceId = ad.getManufacturer().substring(0, 12);
            String displayName = ad.getLocalName().toString() + deviceId.substring(6);
            if (!mTempList.contains(new APBean(deviceId))) {
                //LogUtil.d("receive the record is " + ByteUtil.bytes2HexString(result.getBle_record()));
                APBean apBean = new APBean(displayName, deviceId, result);
                mTempList.add(apBean);
                mS6120ScanCallback.on6120ScanProgress(apBean, true);
            } else {
                //如果已经存在的，则需要查找广播数据是否有更新
                for (APBean apBean : mTempList) {
                    if (TextUtils.equals(apBean.getKey(), deviceId)) {
                        if (!TextUtils.equals(ByteUtil.bytes2HexString(((ScanDeviceResult)apBean.getData()).getBle_record()), ByteUtil.bytes2HexString(result.getBle_record()))) {
                            //LogUtil.e("old name is not equals new name, update it");
                            //LogUtil.t("update the record is " + ByteUtil.bytes2HexString(result.getBle_record()));
                            apBean.setData(result);
                            apBean.setDisplayName(displayName);
                            mS6120ScanCallback.on6120ScanProgress(apBean, false);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private S6120ScanAgent() {
        mBLEBluzDevice = new BLEBluzDevice(Configuration.getContext());
        mTempList = new ArrayList<APBean>();
    }

    public static S6120ScanAgent getInstance() {
        if (sInstance == null) {
            synchronized (S6120ScanAgent.class) {
                if (sInstance == null) {
                    sInstance = new S6120ScanAgent();
                }
            }
        }
        return sInstance;
    }

    public void startScan(S6120ScanCallback callback) {
        startScan(callback, null, null);
    }

    public boolean isEnable(Activity activity) {
        return mBLEBluzDevice.checkEnable(activity);
    }

    public void startScan(S6120ScanCallback callback, String companyId, String type) {
        if (callback == null) {
            return;
        }

        if (!callback.on6120ScanPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION})) {
            return;
        }

        if (!callback.on6120BTEnable()) {
            return;
        }

        mTempList.clear();
        mS6120ScanCallback = callback;
        mBLEBluzDevice.registerOnDiscoveryListener(this);
        mBLEBluzDevice.startDiscovery(new ScanFilter(companyId, type), getTimeOut());
    }

    int getTimeOut() {
        return 5000;
    }
}
