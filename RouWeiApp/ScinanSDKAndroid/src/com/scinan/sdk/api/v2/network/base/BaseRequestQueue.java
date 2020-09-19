/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.network.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.volley.Network;
import com.scinan.sdk.volley.RequestQueue;
import com.scinan.sdk.volley.toolbox.HttpClientStack;
import com.scinan.sdk.volley.toolbox.HttpStack;
import com.scinan.sdk.volley.toolbox.HurlStack;
import com.scinan.sdk.volley.toolbox.NoCache;

public class BaseRequestQueue {

    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 5;
    private static final int DEFAULT_IMAGE_THREAD_POOL_SIZE = 8;

    /**
     * Creates a normal network request instance of the worker pool and calls
     * {@link RequestQueue#start()} on it.
     */
    public static RequestQueue newNormalRequestQueue(Context context) {
        return newRequestQueue(context, false, new BaseStack(null, VendorSSLSocketFactory.getInstance("TLS")), DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    /**
     * Creates a images network request instance of the worker pool and calls
     * {@link RequestQueue#start()} on it.
     */
    public static RequestQueue newImageRequestQueue(Context context) {
        return newRequestQueue(context, true, null, DEFAULT_IMAGE_THREAD_POOL_SIZE);
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param isImageRequest An {@link Boolean} to mark for this request type.
     * @param stack An {@link HttpStack} to use for the network, or null for default.
     * @param defaultThreadPoolSize An {@link Integer} to use for the worker thread pool default size.
     * @return A started {@link RequestQueue} instance.
     */
    private static RequestQueue newRequestQueue(Context context, boolean isImageRequest,
            HttpStack stack, int defaultThreadPoolSize) {
        String userAgent = "appstore/0";
        PackageInfo info = null;

        try {
            String packageName = context.getPackageName();
            info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            info = null;
        }

        if (isImageRequest || stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        Network network = new BaseNetwork(stack);

        RequestQueue queue = new RequestQueue(new NoCache(), network, defaultThreadPoolSize);
        // File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        // RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        try {
            queue.start();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            LogUtil.e("Caught OOM in BaseRequestQueue newRequestQueue e:" + e.getMessage());
            queue.getCache().clear();
            queue.start();
        }

        return queue;
    }
}
