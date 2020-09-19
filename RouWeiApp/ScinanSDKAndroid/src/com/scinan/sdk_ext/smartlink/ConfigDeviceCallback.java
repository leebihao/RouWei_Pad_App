package com.scinan.sdk_ext.smartlink;

/**
 * Created by lijunjie on 17/2/22.
 */

public interface ConfigDeviceCallback {
    void onConfigLog(String log);
    void onConfigFail();
    void onConfigSuccess(String result);
}
