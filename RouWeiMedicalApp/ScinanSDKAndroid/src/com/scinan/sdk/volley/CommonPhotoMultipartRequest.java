/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.volley;

/**
 * Created by lijunjie on 15/12/9.
 */

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.scinan.sdk.api.v1.network.RequestHelper;
import com.scinan.sdk.api.v2.network.base.ScinanSigCheck;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.JavaUtil;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.volley.Response.ErrorListener;
import com.scinan.sdk.volley.Response.Listener;
import com.scinan.sdk.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CommonPhotoMultipartRequest<T> extends Request<String> {

    public static final int UPLOAD_USER_AVATAR                  = 1;
    public static final int UPLOAD_DEVICE_IMAGE                 = 2;
    public static final int UPLOAD_COMMONE_IMAGE                 = 3;

    private static final String RESPONSE_STATUS_CODE            = "LANStatus-Code";
    private static final String RESPONSE_HEADERS                = "Response-Header";
    public static final String RESPONSE_BODY                   = "Response-Body";

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Listener<String> mListener;
    private final File mImageFile;
    protected Map<String, String> headers;
    private Context mContext;

    public CommonPhotoMultipartRequest(Context context, Bundle bundle, ErrorListener errorListener, Listener<String> listener, File imageFile) {
        super(Method.POST, bundle.getString("url"), errorListener);
        mListener = listener;
        mImageFile = imageFile;
        this.mContext =context;
        switch (bundle.getInt("type")) {
            case UPLOAD_USER_AVATAR:
                builAvatardMultipartEntity(bundle.getString("nickName"));
                break;
            case UPLOAD_DEVICE_IMAGE:
                builDeviceImageMultipartEntity(bundle.getString("device_id"));
                break;
            case UPLOAD_COMMONE_IMAGE:
                builCommonImageMultipartEntity();
                break;
        }
        log();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("Accept", "application/json");

        return headers;
    }

//    @Override
//    protected Map<String, String> getParams() throws AuthFailureError {
//        if (mParams != null && mParams.size() != 0) {
//            return mParams;
//        }
//        return super.getParams();
//    }


    private String getDeviceSign(String deviceId) {
       TreeMap<String, String> params = new TreeMap<String, String>();
       params.put("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
       params.put("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
       params.put("imei", Configuration.getIMEI(mContext.getApplicationContext()));
       params.put("token", Configuration.getToken(mContext.getApplicationContext()));
       params.put("timestamp", AndroidUtil.getGMT8MilliString());
       params.put("language", AndroidUtil.getLanguage());
       params.put("location", AndroidUtil.getLocation(mContext.getApplicationContext()));
       params.put("os", AndroidUtil.getSystemOS());
       if (!TextUtils.isEmpty(deviceId))
           params.put("device_id", deviceId);
       JavaUtil.removeMapValueNULL(params);
       return ScinanSigCheck.md5Signature(params, Configuration.getAppSecret(mContext));
    }

    private String getAvatarSign(String nickName) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
        params.put("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
        params.put("imei", Configuration.getIMEI(mContext.getApplicationContext()));
        params.put("token", Configuration.getToken(mContext.getApplicationContext()));
        params.put("timestamp", AndroidUtil.getGMT8MilliString());
        params.put("language", AndroidUtil.getLanguage());
        params.put("location", AndroidUtil.getLocation(mContext.getApplicationContext()));
        params.put("os", AndroidUtil.getSystemOS());
        if (!TextUtils.isEmpty(nickName))
            params.put("user_nickname", nickName);
        JavaUtil.removeMapValueNULL(params);
        return ScinanSigCheck.md5Signature(params, Configuration.getAppSecret(mContext));
    }

    private String getCommonAvatarSign() {
        TreeMap<String, String> mParams = new TreeMap<String, String>();
        mParams.put("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
        mParams.put("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
        mParams.put("imei", Configuration.getIMEI(mContext.getApplicationContext()));
        mParams.put("timestamp", AndroidUtil.getGMT8MilliString());
        mParams.put("language", AndroidUtil.getLanguage());
        mParams.put("token", Configuration.getToken(mContext.getApplicationContext()));
        mParams.put("language", AndroidUtil.getLanguage());
        mParams.put("location", AndroidUtil.getLocation(mContext.getApplicationContext()));
        mParams.put("os", AndroidUtil.getSystemOS());
        mParams.put("client", AndroidUtil.getSystemOS());
        mParams.put("client_version", AndroidUtil.getClientVersion(mContext));
        mParams.put("network", AndroidUtil.getNetWorkType(mContext));
        mParams.put("version_code", String.valueOf(AndroidUtil.getVersionCode(mContext)));
        JavaUtil.removeMapValueNULL(mParams);
        return ScinanSigCheck.md5Signature(mParams, Configuration.getAppSecret(mContext));
    }

    private void buildCommonParams() {
        mBuilder.addTextBody("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
        mBuilder.addTextBody("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
        mBuilder.addTextBody("imei", Configuration.getIMEI(mContext.getApplicationContext()));
        mBuilder.addTextBody("timestamp", AndroidUtil.getGMT8MilliString());
        mBuilder.addTextBody("language", AndroidUtil.getLanguage());
        mBuilder.addTextBody("token", Configuration.getToken(mContext.getApplicationContext()));
    }

    private void builAvatardMultipartEntity(String nickName) {
        mBuilder.addBinaryBody("avatar", mImageFile, ContentType.create("image/*"), mImageFile.getName());
        if (!TextUtils.isEmpty(nickName))
            mBuilder.addTextBody("user_nickname", nickName, ContentType.APPLICATION_JSON);
        buildCommonParams();
        mBuilder.addTextBody("sign", getAvatarSign(nickName));
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    private void builDeviceImageMultipartEntity(String deviceId) {
        mBuilder.addBinaryBody("image", mImageFile, ContentType.create("image/*"), mImageFile.getName());
        mBuilder.addTextBody("device_id", deviceId);
        buildCommonParams();
        mBuilder.addTextBody("sign", getDeviceSign(deviceId));
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    private void builCommonImageMultipartEntity( ) {
        mBuilder.addBinaryBody("image", mImageFile, ContentType.create("image/*"), mImageFile.getName());
        buildCommonParams();
        mBuilder.addTextBody("sign", getCommonAvatarSign());
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    private void log() {
        int apiCode = RequestHelper.getAPIByUrl(getUrl());
        try {
            LogUtil.d("[ApiCode:" + apiCode + "]===========================BaseHelper.sendRequest======================================");
            LogUtil.d("[ApiCode:" + apiCode + "]method   : " + "POST");
            LogUtil.d("[ApiCode:" + apiCode + "]url      : " + getUrl());
            LogUtil.d("[ApiCode:" + apiCode + "]headers  : " + headers);
//            LogUtil.d("[ApiCode:" + apiCode + "]params   : " + mParams);
            LogUtil.d("[ApiCode:" + apiCode + "]bodySize : " + getBody().length);
            LogUtil.d("[ApiCode:" + apiCode + "]token : " +  mBuilder.build().toString());
            LogUtil.d("[ApiCode:" + apiCode + "]==========================================================================================");
        } catch (Exception e) {}

    }

    @Override
    public String getBodyContentType() {
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mBuilder.build().writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {

        int apiCode = RequestHelper.getAPIByUrl(getUrl());
        if (JsonUtil.getResultCode(new String(response.data)) == 0) {
            LogUtil.d("[ApiCode1:" + apiCode  + "]===========================ScinanAPIUploadResponse.onSuccess=====================================");
            LogUtil.d("[ApiCode1:" + apiCode  + "] StatusCode : " + response.statusCode);
            LogUtil.d("[ApiCode1:" + apiCode  + "] body       : " + new String(response.data));
            LogUtil.d("[ApiCode1:" + apiCode  + "] Header     : " + response.headers);
            LogUtil.d("[ApiCode1:" + apiCode  + "]==========================================================================================");
        } else {
            LogUtil.d("[ApiCode1:" + apiCode  + "]===========================ScinanAPIUploadResponse.onFail=====================================");
            LogUtil.d("[ApiCode1:" + apiCode  + "] StatusCode : " + response.statusCode);
            LogUtil.d("[ApiCode1:" + apiCode  + "] body       : " + new String(response.data));
            LogUtil.d("[ApiCode1:" + apiCode  + "] Header     : " + response.headers);
            LogUtil.d("[ApiCode1:" + apiCode  + "]==========================================================================================");
        }

//        if (JsonUtil.getResultCode(new String(response.data)) != 0) {
//            return Response.error(new VolleyError(JsonUtil.parseErrorMsg(new String(response.data))));
//        }
//        return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));


        if (isCanceled() || response == null) {
            return null;
        }
        String bodyString;
        try {
            bodyString = new String(response.data, getParamsEncoding());
        } catch (UnsupportedEncodingException e) {
            bodyString = new String(response.data);
        }
        return Response.success(bodyString, HttpHeaderParser.parseCacheHeaders(response));

    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
//
//        if (isCanceled()) {
//            return;
//        }
//        if (mListener != null) {
//            int statusCode = 0;
//            Map<String, String> headers = null;
//            String body = null;
//            try {
//                JSONObject responseJson = new JSONObject(response);
//                if (!responseJson.isNull(RESPONSE_STATUS_CODE)) {
//                    statusCode = responseJson.getInt(RESPONSE_STATUS_CODE);
//                }
////                if (!responseJson.isNull(RESPONSE_HEADERS)) {
////                    headers = toMap(responseJson);
////                }
//                if (!responseJson.isNull(RESPONSE_BODY)) {
//                    body = responseJson.getString(RESPONSE_BODY);
//                }
//                if (statusCode < HttpStatus.SC_BAD_REQUEST) {
//                    mListener.onResponse(body);
//                }
//                else {
////                    mResponse.onFailure(new AbstractResponse.Response(statusCode, headers, body));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
////                mListener.onError(new AbstractResponse.Response(statusCode, headers, new VolleyError(e)));
//            }
//        }

    }

    private JSONObject toJsonObject(Map<String, String> headers) throws JSONException {
        JSONObject responseHeadersJson = new JSONObject();
        for (String key : headers.keySet()) {
            responseHeadersJson.put(key, headers.get(key));
        }
        return responseHeadersJson;
    }
}