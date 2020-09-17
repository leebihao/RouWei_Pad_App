/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.network.base.error;

import com.scinan.sdk.volley.NetworkError;
import com.scinan.sdk.volley.NetworkResponse;

@SuppressWarnings("serial")
public class ClientVersionLowError extends NetworkError {

    public ClientVersionLowError() {
        super();
    }

    public ClientVersionLowError(Throwable cause) {
        super(cause);
    }

    public ClientVersionLowError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
