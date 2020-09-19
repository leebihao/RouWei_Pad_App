/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.network;

import android.text.TextUtils;

import com.scinan.sdk.api.v1.bean.BaseResponseInfo;
import com.scinan.sdk.api.v2.network.base.AbstractResponse;
import com.scinan.sdk.api.v2.network.base.error.SSLNetworkError;
import com.scinan.sdk.cache.data.UserInfoCache;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.volley.FetchDataCallback;
import com.scinan.sdk.volley.NetworkError;

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
                if (bodyJson.isNull("error_code")) {
                    bodyHeadCode = -1;
                } else {
                    bodyHeadCode = bodyJson.getInt("error_code");
                }
                if (!bodyJson.isNull("error_description")) {
                    msg = bodyJson.getString("error_description");
                }
            }
        } catch (Exception e) {
            bodyHeadCode = -1;
        }

        if (bodyHeadCode == -1) {
            LogUtil.d("[ApiCode:" + mApiCode + "]===========================ScinanAPIResponse.onSuccess=====================================");
            LogUtil.d("[ApiCode:" + mApiCode + "] StatusCode : " + response.statusCode);
            LogUtil.d("[ApiCode:" + mApiCode + "] body       : " + response.body);
            LogUtil.d("[ApiCode:" + mApiCode + "] Error      : " + response.error);
            LogUtil.d("[ApiCode:" + mApiCode + "]==========================================================================================");
            if (mFetchDataCallback != null) {
                mFetchDataCallback.OnFetchDataSuccess(mApiCode, response.statusCode, response.body);
            }
        } else {
            LogUtil.d("[ApiCode:" + mApiCode + "]===========================ScinanAPIResponse.onFailure=====================================");
            LogUtil.d("[ApiCode:" + mApiCode + "] StatusCode : " + response.statusCode);
            LogUtil.d("[ApiCode:" + mApiCode + "] body       : " + response.body);
            LogUtil.d("[ApiCode:" + mApiCode + "] Error      : " + response.error);
            LogUtil.d("[ApiCode:" + mApiCode + "]==========================================================================================");
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
        LogUtil.d("[ApiCode:" + mApiCode + "]===========================ScinanAPIResponse.onFailure=====================================");
        LogUtil.d("[ApiCode:" + mApiCode + "] StatusCode : " + response.statusCode);
        LogUtil.d("[ApiCode:" + mApiCode + "] body       : " + response.body);
        LogUtil.d("[ApiCode:" + mApiCode + "] Error      : " + response.error);
        LogUtil.d("[ApiCode:" + mApiCode + "]==========================================================================================");
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
        LogUtil.d("[ApiCode:" + mApiCode + "]===========================ScinanAPIResponse.onError=======================================");
        LogUtil.d("[ApiCode:" + mApiCode + "] StatusCode : " + response.statusCode);
        LogUtil.d("[ApiCode:" + mApiCode + "] body       : " + response.body);
        LogUtil.d("[ApiCode:" + mApiCode + "] Error      : " + response.error);
        LogUtil.d("[ApiCode:" + mApiCode + "]==========================================================================================");
        handleFail(response);
        clear();
    }

    private void handleFail(Response response) {
        // http请求错误retcode均为-1
        handleFail(response, new BaseResponseInfo(-1, null));
    }

    private void handleFail(Response response, BaseResponseInfo info) {
        String errorBody = response.body;
        switch (response.statusCode / 100) {
            case 0:
                if (response.error instanceof SSLNetworkError) {
                    // catch the SSLNetworkError
                    info.setResult_message("安全证书错误");
                } else if (response.error instanceof NetworkError) {
                    // catch the NetworkError
                    info.setResult_message("网络无连接");
                } else {
                    // catch inner
                    info.setResult_message("网络出错");
                }
                break;
            case 2:
                switch (info.getResult_code()) {
                    case 10002:
                        UserInfoCache.getCache(null).removeAccount();
                        break;
                }
                break;
            case 4:
                info.setResult_message("访问被禁止");
                break;
            case 5:
                info.setResult_message("服务器正在维护");
                break;
            default:
                info.setResult_message("未知错误");
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
