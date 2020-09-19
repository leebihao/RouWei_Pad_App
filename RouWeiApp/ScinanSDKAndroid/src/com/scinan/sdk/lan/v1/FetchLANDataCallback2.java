/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.lan.v1;

import com.scinan.sdk.hardware.HardwareCmd;

/**
 * Created by feng on 2016/9/9.
 */
public interface FetchLANDataCallback2 {
    void OnFetchLANDataSuccess(final int api, final String responseBody, HardwareCmd requestCmd);

    void OnFetchLANDataFailed(final int api, final Throwable error, HardwareCmd requestCmd);
}
