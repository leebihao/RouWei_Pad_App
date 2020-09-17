/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.network.base;

import android.os.SystemClock;

import com.scinan.sdk.volley.Cache;
import com.scinan.sdk.volley.Network;
import com.scinan.sdk.volley.NetworkError;
import com.scinan.sdk.volley.NetworkResponse;
import com.scinan.sdk.volley.Request;
import com.scinan.sdk.volley.RetryPolicy;
import com.scinan.sdk.volley.ServerError;
import com.scinan.sdk.volley.VolleyError;
import com.scinan.sdk.volley.VolleyLog;
import com.scinan.sdk.volley.toolbox.ByteArrayPool;
import com.scinan.sdk.volley.toolbox.HttpStack;
import com.scinan.sdk.volley.toolbox.PoolingByteArrayOutputStream;
import com.scinan.sdk.api.v2.network.base.error.SSLNetworkError;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

public class BaseNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;

    private static int SLOW_REQUEST_THRESHOLD_MS = 3000;

    private static int DEFAULT_POOL_SIZE = 4096;

    protected final HttpStack mHttpStack;

    protected final ByteArrayPool mPool;

    /**
     * @param httpStack HTTP stack to be used
     */
    public BaseNetwork(HttpStack httpStack) {
        // If a pool isn't passed in, then build a small default pool that will give us a lot of
        // benefit and not use too much memory.
        this(httpStack, new ByteArrayPool(DEFAULT_POOL_SIZE));
    }

    /**
     * @param httpStack HTTP stack to be used
     * @param pool      a buffer pool that improves GC performance in copy operations
     */
    public BaseNetwork(HttpStack httpStack, ByteArrayPool pool) {
        mHttpStack = httpStack;
        mPool = pool;
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            HttpResponse httpResponse = null;
            byte[] responseContents = null;
            Map<String, String> responseHeaders = new HashMap<String, String>();
            try {
                // Gather headers.
                Map<String, String> headers = new HashMap<String, String>();
                addCacheHeaders(headers, request.getCacheEntry());
                httpResponse = mHttpStack.performRequest(request, headers);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                responseHeaders = convertHeaders(httpResponse.getAllHeaders());
                // Handle cache validation.
                if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
                    return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, request.getCacheEntry().data,
                            responseHeaders, true);
                }

                responseContents = entityToBytes(httpResponse.getEntity());
                // if the request is slow, log it.
                long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
                logSlowRequests(requestLifetime, request, responseContents, statusLine);

                if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                    throw new NetworkError(new IOException("http status code is " + statusCode));
                }
                return new NetworkResponse(statusCode, responseContents, responseHeaders, false);

            } catch (SSLHandshakeException e) {
                e.printStackTrace();
                throw new SSLNetworkError(e);
            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
                throw new SSLNetworkError(e);
            } catch (SSLException e) {
                e.printStackTrace();
                throw new SSLNetworkError(e);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw new NetworkError(e);
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                attemptRetryOnException("socket", request, new NetworkError(e));
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                attemptRetryOnException("connection", request, new NetworkError(e));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new NetworkError(e);
            }
        }
    }

    /**
     * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
     */
    private void logSlowRequests(long requestLifetime, Request<?> request, byte[] responseContents,
                                 StatusLine statusLine) {
        if (DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
            VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], " + "[rc=%d], [retryCount=%s]",
                    request, requestLifetime, responseContents != null ? responseContents.length : "null",
                    statusLine.getStatusCode(), request.getRetryPolicy().getCurrentRetryCount());
        }
    }

    /**
     * Attempts to prepare the request for a retry. If there are no more attempts remaining in the
     * request's retry policy, a timeout exception is thrown.
     *
     * @param request The request to use.
     */
    private static void attemptRetryOnException(String logPrefix, Request<?> request, VolleyError exception)
            throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int oldTimeout = request.getTimeoutMs();

        try {
            retryPolicy.retry(exception);
        } catch (VolleyError e) {
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
            throw e;
        }
        request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
    }

    private void addCacheHeaders(Map<String, String> headers, Cache.Entry entry) {
        // If there's no cache entry, we're done.
        if (entry == null) {
            return;
        }

        if (entry.etag != null) {
            headers.put("If-None-Match", entry.etag);
        }

        // if (entry.serverDate > 0) {
        // Date refTime = new Date(entry.serverDate);
        // headers.put("If-Modified-Since", DateUtils.formatDate(refTime));
        // }
    }

    protected void logError(String what, String url, long start) {
        long now = SystemClock.elapsedRealtime();
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", what, (now - start), url);
    }

    /**
     * Reads the contents of HttpEntity into a byte[].
     */
    private byte[] entityToBytes(HttpEntity entity) throws IOException, ServerError {
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(mPool, (int) entity.getContentLength());
        byte[] buffer = null;
        try {
            InputStream in = entity.getContent();
            if (in == null) {
                throw new ServerError();
            }
            buffer = mPool.getBuf(1024);
            int count;
            while ((count = in.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        } finally {
            try {
                // Close the InputStream and release the resources by "consuming the content".
                entity.consumeContent();
            } catch (IOException e) {
                // This can happen if there was an exception above that left the entity in
                // an invalid state.
                VolleyLog.v("Error occured when calling consumingContent");
            }
            mPool.returnBuf(buffer);
            bytes.close();
        }
    }

    /**
     * Converts Headers[] to Map<String, String>.
     */
    private static Map<String, String> convertHeaders(Header[] headers) {
        Map<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < headers.length; i++) {
            result.put(headers[i].getName(), headers[i].getValue());
        }
        return result;
    }
}
