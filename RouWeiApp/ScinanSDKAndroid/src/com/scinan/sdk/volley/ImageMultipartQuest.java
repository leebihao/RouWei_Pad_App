/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.volley;

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
import com.scinan.sdk.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Luogical on 2016/7/15.
 */
public class ImageMultipartQuest<T> extends Request<T> {

    public static final int UPLOAD_VITALONG_IMAGE = 1;
    public static final int UPLOAD_FOOD_IMAGE = 2;
    public static final int UPLOAD_STEP_IMAGE = 3;

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private final Response.Listener<T> mListener;
    private final File mImageFile;
    protected Map<String, String> headers;
    private Context mContext;

    public ImageMultipartQuest(Context context, Bundle bundle, Response.ErrorListener errorListener, Response.Listener<T> listener, File imageFile) {
        super(Method.POST, bundle.getString("url"), errorListener);
        mListener = listener;
        mImageFile = imageFile;
        this.mContext = context;
        switch (bundle.getInt("type")) {
            case UPLOAD_FOOD_IMAGE:

                buildFoodImageMultipartEntity(
                        bundle.getString("food_menu_name"), bundle.getString("category_id")
                        , bundle.getString("material"), bundle.getString("description"),
                        bundle.getString("device_type"), bundle.getString("run_command")
                        ,bundle.getString("menu_step")
                );
                break;
            case UPLOAD_STEP_IMAGE:
                buildStepImageMultipartEntity();
                break;
            case UPLOAD_VITALONG_IMAGE:

                buildVitalongImageMultipartEntity();
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


    private String getFoodSign(String name, String categoryId, String material, String description, String deviceType, String runCommand,String menuStep) {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
        params.put("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
        params.put("imei", Configuration.getIMEI(mContext.getApplicationContext()));
        params.put("token", Configuration.getToken(mContext.getApplicationContext()));
        params.put("timestamp", AndroidUtil.getGMT8MilliString());
        params.put("language", AndroidUtil.getLanguage());
        params.put("location", AndroidUtil.getLocation(mContext.getApplicationContext()));
        params.put("os", AndroidUtil.getSystemOS());
        if (!TextUtils.isEmpty(name))
            params.put("food_menu_name", name);
        if (!TextUtils.isEmpty(categoryId))
            params.put("category_id", categoryId);
        if (!TextUtils.isEmpty(material))
            params.put("material", material);
        if (!TextUtils.isEmpty(description))
            params.put("description", description);
        if (!TextUtils.isEmpty(deviceType))
            params.put("device_type", deviceType);
        if (!TextUtils.isEmpty(runCommand))
            params.put("run_command", runCommand);
        if (!TextUtils.isEmpty(menuStep))
            params.put("menu_step", menuStep);
        JavaUtil.removeMapValueNULL(params);
        return ScinanSigCheck.md5Signature(params, Configuration.getAppSecret(mContext));
    }

    private String getStepSign() {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
        params.put("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
        params.put("imei", Configuration.getIMEI(mContext.getApplicationContext()));
        params.put("token", Configuration.getToken(mContext.getApplicationContext()));
        params.put("timestamp", AndroidUtil.getGMT8MilliString());
        params.put("language", AndroidUtil.getLanguage());
        params.put("location", AndroidUtil.getLocation(mContext.getApplicationContext()));
        params.put("os", AndroidUtil.getSystemOS());
        JavaUtil.removeMapValueNULL(params);
        return ScinanSigCheck.md5Signature(params, Configuration.getAppSecret(mContext));
    }


    private String getVitalongSign() {
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
        params.put("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
        params.put("imei", Configuration.getIMEI(mContext.getApplicationContext()));
        params.put("token", Configuration.getToken(mContext.getApplicationContext()));
        params.put("timestamp", AndroidUtil.getGMT8MilliString());
        params.put("language", AndroidUtil.getLanguage());
        params.put("location", AndroidUtil.getLocation(mContext.getApplicationContext()));
        params.put("os", AndroidUtil.getSystemOS());
        JavaUtil.removeMapValueNULL(params);
        return ScinanSigCheck.md5Signature(params, Configuration.getAppSecret(mContext));
    }


    private void buildCommonParams() {
        mBuilder.addTextBody("app_key", Configuration.getAppKey(mContext.getApplicationContext()));
        mBuilder.addTextBody("company_id", Configuration.getCompanyId(mContext.getApplicationContext()));
        mBuilder.addTextBody("imei", Configuration.getIMEI(mContext.getApplicationContext()));
        mBuilder.addTextBody("timestamp", AndroidUtil.getGMT8MilliString());
        mBuilder.addTextBody("language", AndroidUtil.getLanguage());
        mBuilder.addTextBody("token", Configuration.getToken(mContext.getApplicationContext()));
    }


    private void buildFoodImageMultipartEntity(String name, String categoryId, String material, String description, String deviceType, String runCommand,String menuStep) {
        mBuilder.addBinaryBody("imgurl", mImageFile, ContentType.create("image/*"), mImageFile.getName());
        mBuilder.addTextBody("food_menu_name", name, ContentType.APPLICATION_JSON);
        mBuilder.addTextBody("category_id", categoryId);
        mBuilder.addTextBody("material", material, ContentType.APPLICATION_JSON);
        mBuilder.addTextBody("menu_step", menuStep, ContentType.APPLICATION_JSON);
        mBuilder.addTextBody("description", description, ContentType.APPLICATION_JSON);
        mBuilder.addTextBody("device_type", deviceType);
        mBuilder.addTextBody("run_command", runCommand);
        buildCommonParams();
        mBuilder.addTextBody("sign", getFoodSign(name, categoryId, material, description, deviceType, runCommand,menuStep));
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    private void buildStepImageMultipartEntity() {
        mBuilder.addBinaryBody("stepImg", mImageFile, ContentType.create("image/*"), mImageFile.getName());
        buildCommonParams();
        mBuilder.addTextBody("sign", getStepSign());
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }


    private void buildVitalongImageMultipartEntity() {
        mBuilder.addBinaryBody("image", mImageFile, ContentType.create("image/*"), mImageFile.getName());
        buildCommonParams();
        mBuilder.addTextBody("sign", getVitalongSign());
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
            LogUtil.d("[ApiCode:" + apiCode + "]params   : " + "not null");
            LogUtil.d("[ApiCode:" + apiCode + "]bodySize : " + getBody().length);
            LogUtil.d("[ApiCode:" + apiCode + "]==========================================================================================");
        } catch (Exception e) {
        }

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
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        T result = null;

        int apiCode = RequestHelper.getAPIByUrl(getUrl());
        if (JsonUtil.getResultCode(new String(response.data)) == 0) {
            LogUtil.d("[ApiCode1:" + apiCode + "]===========================ScinanAPIUploadResponse.onSuccess=====================================");
            LogUtil.d("[ApiCode1:" + apiCode + "] StatusCode : " + response.statusCode);
            LogUtil.d("[ApiCode1:" + apiCode + "] body       : " + new String(response.data));
            LogUtil.d("[ApiCode1:" + apiCode + "] Header     : " + response.headers);
            LogUtil.d("[ApiCode1:" + apiCode + "]==========================================================================================");
        } else {
            LogUtil.d("[ApiCode1:" + apiCode + "]===========================ScinanAPIUploadResponse.onFail=====================================");
            LogUtil.d("[ApiCode1:" + apiCode + "] StatusCode : " + response.statusCode);
            LogUtil.d("[ApiCode1:" + apiCode + "] body       : " + new String(response.data));
            LogUtil.d("[ApiCode1:" + apiCode + "] Header     : " + response.headers);
            LogUtil.d("[ApiCode1:" + apiCode + "]==========================================================================================");
        }

        if (JsonUtil.getResultCode(new String(response.data)) != 0) {
            return Response.error(new VolleyError(JsonUtil.parseErrorMsg(new String(response.data))));
        }
        return Response.success((T) response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T response) {

        mListener.onResponse(response);
    }
}
