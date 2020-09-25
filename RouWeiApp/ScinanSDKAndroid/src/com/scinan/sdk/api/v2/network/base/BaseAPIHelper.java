/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.network.base;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.config.Configuration;
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
    protected static final String HEADER_CONTENT_TYPE = "content-type";
    //protected static final String HEADER_REQUEST_ID = "request_id";
    protected static final String HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded; charset=UTF-8";
    protected static final String HEADER_REQUESTED_WITH_VALUE = "XMLHttpRequest";

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
        params = getParams(params);
        final String targetUrl = getUrl(method, api, urlParams, params);
        headers = getHeaders(headers);
        BaseRequest request = new BaseRequest(method, targetUrl, reponse, params);
        if (headers != null && headers.size() != 0) {
            request.setHeaders(headers);
        }
        if (!TextUtils.isEmpty(body)) {
            request.setBody(body.getBytes());
        }


        LogUtil.i("[ApiCode:" + api + "]===========================BaseHelper.sendRequest======================================");
        LogUtil.i("[ApiCode:" + api + "]method   : " + method);
        LogUtil.i("[ApiCode:" + api + "]url      : " + targetUrl);
        LogUtil.i("[ApiCode:" + api + "]headers  : " + headers);
        LogUtil.i("[ApiCode:" + api + "]params   : " + params);
        LogUtil.i("[ApiCode:" + api + "]body     : " + body);
        LogUtil.i("[ApiCode:" + api + "]APIAllUrlForBrowser    : " + getAPIAllUrlForBrowser(targetUrl, params));
        LogUtil.i("[ApiCode:" + api + "]==========================================================================================");
        mRequestQueue.add(request);
    }


    /**
     * 获取完整链接,方便和云平台联调
     * @param targetUrl
     * @param params
     * @return
     */
    private String getAPIAllUrlForBrowser(String targetUrl, TreeMap<String, String> params){
         StringBuffer urlBuffer =new StringBuffer(targetUrl);
        urlBuffer.append("?");

        for (String key : params.keySet()) {

            String value = params.get(key);
            urlBuffer.append(key).append("=").append(value).append("&");

        }

        return urlBuffer.toString();
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

    protected Map<String, String> getHeaders(Map<String, String>  headers) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(HEADER_REQUESTED_WITH, HEADER_REQUESTED_WITH_VALUE);
        headers.put(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
        //headers.put(HEADER_REQUEST_ID, AndroidUtil.generateRequestID(mContext.getApplicationContext()));
        return headers;
    }

    protected  TreeMap<String, String> getParams(TreeMap<String, String>  params) {
        if (params == null) {
            params = new TreeMap<String, String>();
        }
        params.put("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
        if (!params.containsKey("company_id")) {
            params.put("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
        }
        params.put("token", Configuration.getToken(mContext.getApplicationContext()));
        params.put("imei", Configuration.getIMEI(mContext.getApplicationContext()));
        params.put("timestamp", AndroidUtil.getGMT8MilliString());
        params.put("language", AndroidUtil.getLanguage());
        params.put("client", AndroidUtil.getSystemOS());
        params.put("client_version", AndroidUtil.getClientVersion(mContext.getApplicationContext()));
        params.put("network", AndroidUtil.getNetWorkType(mContext.getApplicationContext()));
        params.put("location", AndroidUtil.getLocation(mContext.getApplicationContext()));
        params.put("request_id", AndroidUtil.generateRequestID(mContext.getApplicationContext()));
        JavaUtil.removeMapValueNULL(params);
        params.put("sign", ScinanSigCheck.md5Signature(params, Configuration.getAppSecret(mContext)));

        return params;
    }

    protected static String getUrl(final int method, int api, final Object[] urlParams, final Map<String, String> params) {
        //如果是control的api，需单独处理
        if (api > 9999 && api < 20000) {
            api = 10000;
        }
        if(api > 19999 && api < 29999) {
            api = 20000;
        }
        //千帕smartcontrol使用 30000
        if(api > 29999 && api < 40000) {
            api = 30000;
        }

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
