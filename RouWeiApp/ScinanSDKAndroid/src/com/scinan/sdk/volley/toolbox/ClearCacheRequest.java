/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.volley.toolbox;

import android.os.Handler;
import android.os.Looper;

import com.scinan.sdk.volley.Cache;
import com.scinan.sdk.volley.NetworkResponse;
import com.scinan.sdk.volley.Request;
import com.scinan.sdk.volley.Response;

/**
 * A synthetic request used for clearing the cache.
 */
public class ClearCacheRequest extends Request<Object> {
    private final Cache mCache;
    private final Runnable mCallback;

    /**
     * Creates a synthetic request for clearing the cache.
     * @param cache Cache to clear
     * @param callback Callback to make on the main thread once the cache is clear,
     * or null for none
     */
    public ClearCacheRequest(Cache cache, Runnable callback) {
        super(Method.GET, null, null);
        mCache = cache;
        mCallback = callback;
    }

    @Override
    public boolean isCanceled() {
        // This is a little bit of a hack, but hey, why not.
        mCache.clear();
        if (mCallback != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postAtFrontOfQueue(mCallback);
        }
        return true;
    }

    @Override
    public Priority getPriority() {
        return Priority.IMMEDIATE;
    }

    @Override
    protected Response<Object> parseNetworkResponse(NetworkResponse response) {
        return null;
    }

    @Override
    protected void deliverResponse(Object response) {
    }
}
