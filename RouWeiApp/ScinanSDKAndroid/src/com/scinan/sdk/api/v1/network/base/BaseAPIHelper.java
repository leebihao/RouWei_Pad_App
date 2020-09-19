/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.network.base;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.api.v2.network.base.AbstractResponse;
import com.scinan.sdk.api.v2.network.base.BaseRequest;
import com.scinan.sdk.api.v2.network.base.BaseRequestQueue;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.JavaUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.volley.Request;
import com.scinan.sdk.volley.RequestQueue;
import com.scinan.sdk.volley.RequestQueue.RequestFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BaseAPIHelper {

    protected static final String HEADER_REQUESTED_WITH = "X-Requested-With";
    protected static final String HEADER_TOKEN_KEY = "cookie";
    protected static final String HEADER_TOKEN_VALUE = "token=";
    protected static final String HEADER_REQUESTED_WITH_VALUE = "XMLHttpRequest";
    protected static final String HEADER_MAC_KEY = "mac";
    protected static final String HEADER_LOCATION_KEY = "location";

    protected static Map<Integer, String> urlMap = new HashMap<Integer, String>();
    protected Context mContext;
    protected int mRetryMethodType;
    protected int mRetryApi;
    protected Object[] mRetryUrlParams;
    protected TreeMap<String, String> mRetryParams;
    protected String mRetryBody;
    protected AbstractResponse mResponse;
    protected RequestQueue mRequestQueue;

    public BaseAPIHelper(Context context) {
        mContext = context;
        mRequestQueue = BaseRequestQueue.newNormalRequestQueue(mContext.getApplicationContext());
    }

    public void sendRequest(final int method, final int api, final Object[] urlParams,
                            final TreeMap<String, String> params, final String body, final AbstractResponse reponse) {
        sendRequest(method, api, urlParams, null, params, body, reponse);
    }

    public void sendRequest(final int method, final int api, final Object[] urlParams, Map<String, String> headers,
                            TreeMap<String, String> params, final String body, final AbstractResponse reponse) {
        final String targetUrl = getUrl(method, api, urlParams, params);
        headers = getHeaders(headers);
        params = getParams(params);
        BaseRequest request = new BaseRequest(method, targetUrl, reponse, params);
        if (headers != null && headers.size() != 0) {
            request.setHeaders(headers);
        }
        if (!TextUtils.isEmpty(body)) {
            request.setBody(body.getBytes());
        }

        LogUtil.d("[ApiCode:" + api + "]===========================BaseHelper.sendRequest======================================");
        LogUtil.d("[ApiCode:" + api + "]method   : " + method);
        LogUtil.d("[ApiCode:" + api + "]url      : " + targetUrl);
        LogUtil.d("[ApiCode:" + api + "]headers  : " + headers);
        LogUtil.d("[ApiCode:" + api + "]params   : " + params);
        LogUtil.d("[ApiCode:" + api + "]body     : " + body);
        LogUtil.d("[ApiCode:" + api + "]==========================================================================================");
        mRequestQueue.add(request);
    }
    public void cancel(final int method, final int api, final Object[] urlParams,
                       final Map<String, String> params) {
        final String targetUrl = getUrl(method, api, urlParams, params);
        mRequestQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return String.valueOf(request.getTag()) == targetUrl;
            }
        });
    }

    public void cancel() {
        mRequestQueue.cancelAll(new RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return (request instanceof BaseRequest);
            }
        });
    }

    public void clear() {
        cancel();
        mRequestQueue.getCache().clear();
    }

    public void refresh() {
        sendRequest(mRetryMethodType, mRetryApi, mRetryUrlParams, mRetryParams, mRetryBody, mResponse);
    }

    protected void setRetryData(final int methodType, final int api, final Object[] urlParams,
                                final TreeMap<String, String> params, final String body, final AbstractResponse response) {
        mRetryMethodType = methodType;
        mRetryApi = api;
        mRetryUrlParams = urlParams;
        mRetryParams = params;
        mRetryBody = body;
        mResponse = response;
    }

    protected static Map<String, String> getHeaders(Map<String, String>  headers) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(HEADER_REQUESTED_WITH, HEADER_REQUESTED_WITH_VALUE);
        return headers;
    }

    protected  TreeMap<String, String> getParams(TreeMap<String, String> params) {
        if (params == null) {
            params = new TreeMap<String, String>();
        }
        params.put("token", Configuration.getToken(mContext.getApplicationContext()));
        params.put("timestamp", AndroidUtil.getGMT8String());
        JavaUtil.removeMapValueNULL(params);
        return params;
    }

    protected static String getUrl(final int method, final int api, final Object[] urlParams, final Map<String, String> params) {
        String url = urlMap.get(api);
        if (urlParams != null && urlParams.length > 0) {
            url = formatUrlWithParams(url, urlParams);
        }
        if (method == Request.Method.GET) {
            return BaseRequest.setUrlByParams(url, params);
        } else {
            return BaseRequest.setUrlByParams(url, null);
        }
    }

    protected static String formatUrlWithParams(String url, Object... values) {
        String formateUrl = url;
        for (Object obj : values) {
            formateUrl = formateUrl.replaceFirst("#", String.valueOf(obj));
        }
        return formateUrl;
    }

}
