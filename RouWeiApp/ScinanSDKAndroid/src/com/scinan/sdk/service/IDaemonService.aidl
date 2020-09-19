// IDaemonService.aidl
package com.scinan.sdk.service;

// Declare any non-default types here with import statements

interface IDaemonService {

        String getToken();
        String getPassword();
        int getTrace();
        String getValue(String key);
}
