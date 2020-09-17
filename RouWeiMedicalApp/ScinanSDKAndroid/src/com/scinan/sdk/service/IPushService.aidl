// IPushService.aidl
package com.scinan.sdk.service;

import com.scinan.sdk.service.IPushCallback;

interface IPushService {

    boolean isPushConnected();
    void connectPush();
    void closePush();
    void onSend(in String message);
    void addCallback(in String id, in IPushCallback callback);
    void removeCallback(in String id);
}
