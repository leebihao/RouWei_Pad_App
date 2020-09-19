// IPushCallback.aidl
package com.scinan.sdk.service;

interface IPushCallback {

    void onConnected();
    void onError();
    void onClose();
    void onPush(String msg);
}
