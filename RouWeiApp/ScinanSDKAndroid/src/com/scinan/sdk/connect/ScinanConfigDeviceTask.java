/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.interfaces.ConfigDeviceCallback;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;
import com.scinan.sdk.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by lijunjie on 16/3/25.
 */
public abstract class ScinanConfigDeviceTask extends AsyncTask<String, String, Void> {

    private int taskType;

    static final int STEP_START                            = 0x30;
    static final int STEP_PROGRESS                         = 0x31;
    static final int STEP_SUCCESS                          = 0x32;
    static final int STEP_FAIL                             = 0x33;
    static final int STEP_DATA                             = 0x34;

    Context mContext;
    ScinanConnectDevice mScinanConnectDevice;
    ConfigDeviceCallback mConfigDeviceCallback;
    ConfigDeviceCallback2 mConfigDeviceCallback2;

    ArrayList<HardwareCmd> mHardwareCmds;

    protected OnHardwareConfigListener onHardwareConfigListener;

    public interface OnHardwareConfigListener {
        void onHardwareConfig(int type, HardwareCmd hardwareCmd);
    }

    public OnHardwareConfigListener getOnHardwareConfigListener() {
        return onHardwareConfigListener;
    }

    public void setOnHardwareConfigListener(OnHardwareConfigListener onHardwareConfigListener) {
        this.onHardwareConfigListener = onHardwareConfigListener;
    }

    //全局成功标志位(目前只有在AP配置里面，全局成功和TCP成功不是一回事，其他都是一回事)
    boolean isConfigSuccess;

    StringBuffer stringBuffer;

    public ScinanConfigDeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        this.mContext = context;
        this.mScinanConnectDevice = scinanDevice;
        this.mConfigDeviceCallback = callback;
        this.mHardwareCmds = new ArrayList<HardwareCmd>();
        this.stringBuffer = new StringBuffer();
    }

    public ScinanConfigDeviceTask(Context context, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
        this.mContext = context;
        this.mScinanConnectDevice = scinanDevice;
        this.mConfigDeviceCallback2 = callback;
        this.mHardwareCmds = new ArrayList<HardwareCmd>();
        this.stringBuffer = new StringBuffer();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (!getStatus().equals(Status.RUNNING)) {
            return;
        }

        switch (Integer.valueOf(values[0])) {
            case STEP_FAIL:
                if (mConfigDeviceCallback != null) {
                    mConfigDeviceCallback.onTCPConfigFail();
                } else if (mConfigDeviceCallback2 != null) {
                    mConfigDeviceCallback2.onFailConfig(getTaskType(), values[1]);
                }
                break;
            case STEP_START:
                if (mConfigDeviceCallback2 != null) {
                    mConfigDeviceCallback2.onStartConfig(getTaskType());
                }
                break;
            case STEP_PROGRESS:
                if (mConfigDeviceCallback2 != null) {
                    mConfigDeviceCallback2.onProgressConfig(getTaskType(), values[1]);
                }
                break;
            case STEP_DATA://观察一下数据
                if (mConfigDeviceCallback2 != null) {
                    HardwareCmd[] arrays = new HardwareCmd[mHardwareCmds.size()];
                    mHardwareCmds.toArray(arrays);
                    mConfigDeviceCallback2.onSuccessConfig(getTaskType(), arrays);
                }
                break;
            case STEP_SUCCESS:
                if (mConfigDeviceCallback != null) {
                    mConfigDeviceCallback.onTCPConfigSuccess(getOldVersionResponse());
                } else if (mConfigDeviceCallback2 != null) {
                    HardwareCmd[] arrays = new HardwareCmd[mHardwareCmds.size()];
                    mHardwareCmds.toArray(arrays);
                    mConfigDeviceCallback2.onSuccessConfig(getTaskType(), arrays);
                }
                finish();
                break;
            default:

        }
    }

    String getOldVersionResponse() {
        if (mHardwareCmds.size() == 1) {
            return mHardwareCmds.get(0).deviceId + "," + mHardwareCmds.get(0).data;
        } else if (mHardwareCmds.size() > 1) {
            StringBuffer sb = new StringBuffer();
            for (HardwareCmd cmd : mHardwareCmds) {
                sb.append(cmd.deviceId);
                sb.append(",");
                sb.append(cmd.data);
                sb.append(",");
            }
            return sb.toString();
        }
        return "";
    }

    String getKeywords() {
        if (mScinanConnectDevice == null || TextUtils.isEmpty(mScinanConnectDevice.getCompanyId())) {
            return "/type/1";
        }
        return "/" + mScinanConnectDevice.getCompanyId();
    }

    protected void logT(String msg) {
        LogUtil.t(msg);
        publishProgress(String.valueOf(STEP_PROGRESS), msg);
    }

    protected void logD(String msg) {
        LogUtil.d(msg);
        publishProgress(String.valueOf(STEP_PROGRESS), msg);
    }

    protected void logE(Throwable throwable) {
        LogUtil.e(throwable);
        publishProgress(String.valueOf(STEP_PROGRESS), LogUtil.getExceptionString(throwable));
    }

    protected HardwareCmd getHardwareCmd(String fullData) {
        if (fullData.endsWith("/")) {
            fullData = fullData + "1";
        }

        HardwareCmd cmd = HardwareCmd.parse(fullData);
        if (cmd == null) {
            cmd = new HardwareCmd(fullData.split("/")[1], "type", "1", fullData.substring(fullData.lastIndexOf("/") + 1).trim());
        }
        return cmd;
    }

    public int getTaskType() {
        return taskType;
    }

    public ScinanConfigDeviceTask setTaskType(int taskType) {
        this.taskType = taskType;
        return this;
    }

    public abstract void finish();
}
