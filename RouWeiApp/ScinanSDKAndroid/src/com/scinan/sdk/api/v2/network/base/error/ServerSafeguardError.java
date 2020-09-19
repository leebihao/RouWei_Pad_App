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
public class ServerSafeguardError extends NetworkError {

    public ServerSafeguardError() {
        super();
    }

    public ServerSafeguardError(Throwable cause) {
        super(cause);
    }

    public ServerSafeguardError(NetworkResponse networkResponse) {
        super(networkResponse);
    }
}
