/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.lan.v1;

/**
 * Created by lijunjie on 15/12/14.
 * @see FetchLANDataCallback2
 */
@Deprecated
public interface FetchLANDataCallback {
    void OnFetchLANDataSuccess(final int api, final String responseBody);

    void OnFetchLANDataFailed(final int api, final Throwable error);
}
