/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.network;

import android.text.TextUtils;

import com.scinan.sdk.api.v2.bean.BaseResponseInfo;
import com.scinan.sdk.cache.data.v2.UserInfoCache;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.SNResource;
import com.scinan.sdk.volley.FetchDataCallback;
import com.scinan.sdk.volley.NetworkError;
import com.scinan.sdk.api.v2.network.base.AbstractResponse;
import com.scinan.sdk.api.v2.network.base.error.SSLNetworkError;

import org.json.JSONObject;

public class ResponseHelper extends AbstractResponse {

    final private int mApiCode;
    private FetchDataCallback mFetchDataCallback;

    public ResponseHelper(int api, FetchDataCallback fetchDataCallback) {
        mApiCode = api;
        mFetchDataCallback = fetchDataCallback;
    }

    // status_code < 400
    @Override
    public void onSuccess(Response response) {
        if (response == null) {
            return;
        }
        if (response.error != null) {
            LogUtil.e(response.error.getMessage());
            response.error.printStackTrace();
        }
        int bodyHeadCode = -1;
        String msg = null;
        try {
            String body = response.body;
            if (!TextUtils.isEmpty(body)) {
                JSONObject bodyJson = new JSONObject(body);
                if (bodyJson.isNull(RequestHelper.HTTP_OK_KEY)) {
                    bodyHeadCode = -1;
                } else {
                    bodyHeadCode = bodyJson.getInt(RequestHelper.HTTP_OK_KEY);
                }
                if (!bodyJson.isNull("result_message")) {
                    msg = bodyJson.getString("result_message");
                }
            }
        } catch (Exception e) {
            bodyHeadCode = -1;
        }
        if (bodyHeadCode == RequestHelper.HTTP_OK) {
            LogUtil.i("[ApiCode:" + mApiCode + "]===========================ScinanAPIResponse.onSuccess=====================================");
            LogUtil.i("[ApiCode:" + mApiCode + "] StatusCode : " + response.statusCode);
            LogUtil.i("[ApiCode:" + mApiCode + "] headers    : " + response.headers);
            LogUtil.i("[ApiCode:" + mApiCode + "] body       : " + response.body);
            LogUtil.i("[ApiCode:" + mApiCode + "] Error      : " + response.error);
            LogUtil.i("[ApiCode:" + mApiCode + "]==========================================================================================");
            if (mFetchDataCallback != null) {
                mFetchDataCallback.OnFetchDataSuccess(mApiCode, response.statusCode, response.body);
            }
        } else {
            LogUtil.e("[ApiCode:" + mApiCode + "]===========================ScinanAPIResponse.onFailure=====================================");
            LogUtil.e("[ApiCode:" + mApiCode + "] StatusCode : " + response.statusCode);
            LogUtil.e("[ApiCode:" + mApiCode + "] headers    : " + response.headers);
            LogUtil.i("[ApiCode:" + mApiCode + "] body       : " + response.body);
            LogUtil.e("[ApiCode:" + mApiCode + "] Error      : " + response.error);
            LogUtil.e("[ApiCode:" + mApiCode + "]==========================================================================================");
            handleFail(response, new BaseResponseInfo(bodyHeadCode, msg));
        }
        clear();
    }

    // status_code >= 400
    @Override
    public void onFailure(Response response) {
        if (response == null) {
            return;
        }
        if (response.error != null) {
            LogUtil.e(response.error.getMessage());
            response.error.printStackTrace();
        }
        LogUtil.e("[ApiCode:" + mApiCode + "]===========================ScinanAPIResponse.onFailure=====================================");
        LogUtil.e("[ApiCode:" + mApiCode + "] StatusCode : " + response.statusCode);
        LogUtil.e("[ApiCode:" + mApiCode + "] headers    : " + response.headers);
        LogUtil.e("[ApiCode:" + mApiCode + "] body       : " + response.body);
        LogUtil.e("[ApiCode:" + mApiCode + "] Error      : " + response.error);
        LogUtil.e("[ApiCode:" + mApiCode + "]==========================================================================================");
        handleFail(response);
        clear();
    }

    // http error
    @Override
    public void onError(Response response) {
        if (response == null) {
            return;
        }
        if (response.error != null) {
            LogUtil.e(response.error.getMessage());
            response.error.printStackTrace();
        }
        LogUtil.e("[ApiCode:" + mApiCode + "]===========================ScinanAPIResponse.onError=======================================");
        LogUtil.e("[ApiCode:" + mApiCode + "] StatusCode : " + response.statusCode);
        LogUtil.e("[ApiCode:" + mApiCode + "] headers    : " + response.headers);
        LogUtil.e("[ApiCode:" + mApiCode + "] body       : " + response.body);
        LogUtil.e("[ApiCode:" + mApiCode + "] Error      : " + response.error);
        LogUtil.e("[ApiCode:" + mApiCode + "]==========================================================================================");
        handleFail(response);
        clear();
    }

    private void handleFail(Response response) {
        // http请求错误retcode均为-1
        handleFail(response, new BaseResponseInfo(-1, null));
    }

    private void handleFail(Response response, BaseResponseInfo info) {
        String errorBody = response.body;
        SNResource R = SNResource.getInstance(Configuration.getContext());
        switch (response.statusCode / 100) {
            case 0:
                if (response.error instanceof SSLNetworkError) {
                    // catch the SSLNetworkError
                    info.setResult_message(R.getString("sdk_error_ssl"));
                } else if (response.error instanceof NetworkError) {
                    // catch the NetworkError
                    info.setResult_message(R.getString("sdk_error_disable"));
                } else {
                    // catch inner
                    info.setResult_message(R.getString("sdk_error_network"));
                }
                break;
            case 2:
                info.setResult_message(null);
                switch (info.getResult_code()) {
                    case 10002:
                    case 10012:
                    case 10013:
                        info.setResult_message(R.getString("sdk_error_login_expired"));
                        UserInfoCache.getCache(Configuration.getContext()).removeAccount();
                        break;
                    case 10003:
                        info.setResult_message(R.getString("sdk_error_network"));
                        UserInfoCache.getCache(Configuration.getContext()).refreshToken();
                        break;
                    case 49999:
                        BuildConfig.LOG_TRACE_LEVEL = 0;
                        break;
                }
                break;
            case 4:
                info.setResult_message(R.getString("sdk_error_forbidden"));
                break;
            case 5:
                info.setResult_message(R.getString("sdk_error_server_rest"));
                break;
            default:
                info.setResult_message(R.getString("sdk_error_network"));
        }
        if (info.getResult_message() != null) {
            // 重新构造返回信息，用于UI显示
            errorBody = info.toJSONString();
        }
        if (mFetchDataCallback != null) {
            mFetchDataCallback.OnFetchDataFailed(mApiCode, response.error, errorBody);
        }
    }

    private void clear() {
        mFetchDataCallback = null;
    }
}
