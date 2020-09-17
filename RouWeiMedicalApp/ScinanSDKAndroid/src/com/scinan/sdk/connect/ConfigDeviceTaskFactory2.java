/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.connect;

import android.content.Context;

import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.interfaces.ConfigDeviceCallback;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;

/**
 * Created by ruibiao on 2018.5.18
 */
public class ConfigDeviceTaskFactory2 {
    public static final int TYPE_ERROR                               = 0;
    //AP配置
    public static final int AP_STANDARD_MODE                         = 1;
    //新601配置（Airkiss方案和南方硅谷方案Mix）
    public static final int SMART_SOUTHSV_601                        = 2;
    //7681配置
    public static final int SMART_MEDIATEK_7681                      = 3;
    //7681一配多
    public static final int SMART_MEDIATEK_7681_MULTI                = 4;
    //有线配置
    public static final int SMART_WIRE                               = 5;
    //微信Airkiss配置
    public static final int SMART_AIRKISS                            = 6;
    //601（仅Airkiss方案）和7681兼容配置
    public static final int SMART_SOUTHSV_601_MIX_7681               = 7;
    //工厂微信Airkiss配置，不限定配置数量，没有超时
    public static final int SMART_AIRKISS_FACTORY                    = 8;
    //6120一键配置
    public static final int SMART_6120                               = 9;
    //601（仅Airkiss方案）和7681兼容配置一配多
    public static final int SMART_SOUTHSV_601_MIX_7681_MULTI         = 10;


    /*
    这个类将不再建议使用，因为ConfigDeviceCallback接口即将被废弃，鼓励使用新接口ConfigDeviceCallback2
     */
    @Deprecated
    public static ScinanConfigDeviceTask getTask(Context context, int mode, ScinanConnectDevice scinanDevice, ConfigDeviceCallback callback) {
        ScinanConfigDeviceTask task = null;
        switch (mode) {
            case SMART_SOUTHSV_601:
                task = new SmartSouthSV601ConfigTask(context, scinanDevice, callback);
                break;
            case SMART_MEDIATEK_7681:
                task = new SmartMediatek7681DeviceTask(context, scinanDevice, callback);
                break;
            case SMART_MEDIATEK_7681_MULTI:
                task = new SmartMediatek7681MultiDeviceTask(context, scinanDevice, callback);
                break;
            case SMART_WIRE:
                task = new SmartWireDeviceTask(context, scinanDevice, callback);
                break;
            case SMART_AIRKISS:
                task = new AirKissConfigTask(context, scinanDevice, callback);
                break;
            case SMART_SOUTHSV_601_MIX_7681:
                task = new SouthSV601Mix7681ConfigTask(context, scinanDevice, callback);
                break;
            case SMART_AIRKISS_FACTORY:
                task = new AirKissMultiConfigTask(context, scinanDevice, callback);
                break;
            case SMART_6120:
                task = new Smart6120DeviceTask(context, scinanDevice, callback);
                break;
            case SMART_SOUTHSV_601_MIX_7681_MULTI:
                task = new SouthSV601Mix7681MultiConfigTask(context, scinanDevice, callback);
                break;
            default:
                task = new APConfigDeviceTask(context, scinanDevice, callback);
        }
        return task.setTaskType(mode);
    }

    public static ScinanConfigDeviceTask getTask(Context context, int mode, ScinanConnectDevice scinanDevice, ConfigDeviceCallback2 callback) {
        ScinanConfigDeviceTask task = null;
        switch (mode) {
            case SMART_SOUTHSV_601:
                task = new SmartSouthSV601ConfigTask(context, scinanDevice, callback);
                break;
            case SMART_MEDIATEK_7681:
                task = new SmartMediatek7681DeviceTask(context, scinanDevice, callback);
                break;
            case SMART_MEDIATEK_7681_MULTI:
                task = new SmartMediatek7681MultiDeviceTask(context, scinanDevice, callback);
                break;
            case SMART_WIRE:
                task = new SmartWireDeviceTask(context, scinanDevice, callback);
                break;
            case SMART_AIRKISS:
                task = new AirKissConfigTask(context, scinanDevice, callback);
                break;
            case SMART_SOUTHSV_601_MIX_7681:
                task = new SouthSV601Mix7681ConfigTask(context, scinanDevice, callback);
                break;
            case SMART_AIRKISS_FACTORY:
                task = new AirKissMultiConfigTask(context, scinanDevice, callback);
                break;
            case SMART_6120:
                task = new Smart6120DeviceTask(context, scinanDevice, callback);
                break;
            case SMART_SOUTHSV_601_MIX_7681_MULTI:
                task = new SouthSV601Mix7681MultiConfigTask(context, scinanDevice, callback);
                break;
            default:
                task = new APConfigDeviceTask(context, scinanDevice, callback);
        }
        return task.setTaskType(mode);
    }
}
