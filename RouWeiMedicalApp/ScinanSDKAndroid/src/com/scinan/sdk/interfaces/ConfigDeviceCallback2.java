package com.scinan.sdk.interfaces;

import com.scinan.sdk.hardware.HardwareCmd;

/**
 * Created by lijunjie on 17/5/8.
 */

public interface ConfigDeviceCallback2 {

    void onStartConfig(int type);

    void onProgressConfig(int type, String msg);

    void onSuccessConfig(int type, HardwareCmd[] hardwareCmds);

    void onFailConfig(int type, String msg);
}
