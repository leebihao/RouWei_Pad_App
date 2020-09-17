/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.volley;

/**
 * Created by Luogical on 16/1/14.
 */
public interface FetchDataCallback {

    void OnFetchDataSuccess(final int api, final int responseCode, final String responseBody);

    void OnFetchDataFailed(final int api, final Throwable error, final String responseBody);
}