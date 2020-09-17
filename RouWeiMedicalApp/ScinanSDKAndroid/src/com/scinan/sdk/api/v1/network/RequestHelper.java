/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v1.network;

import android.content.Context;
import android.os.Bundle;

import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.util.JavaUtil;
import com.scinan.sdk.volley.FetchDataCallback;
import com.scinan.sdk.volley.PhotoMultipartRequest;
import com.scinan.sdk.volley.Request;
import com.scinan.sdk.api.v2.network.base.AbstractResponse;
import com.scinan.sdk.api.v1.network.base.BaseAPIHelper;
import com.scinan.sdk.volley.RequestQueue;
import com.scinan.sdk.volley.Response;
import com.scinan.sdk.volley.toolbox.Volley;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class RequestHelper extends BaseAPIHelper {

    public static final int HTTP_OK                             = 0;
    public static final int HTTP_QQ_BIND                        = 20017;
    public static final String HTTP_OK_KEY                      = "result_code";

    public static final String HTTP                             = "http://";
    public static final String HTTPS                            = "https://";
    public static final String PROTOCOL                         = BuildConfig.API_HTTPS ? HTTPS : HTTP;
    public static final String HOST_NAME_DEBUG                  = "api.scinan.com";
    public static final String HOST_NAME_RELEASE                = "api.scinan.com";
    public static final String HOST_NAME                        = BuildConfig.API_DEBUG ? HOST_NAME_DEBUG : HOST_NAME_RELEASE;
    public static final String DOMAIN                           = PROTOCOL + HOST_NAME;

    public static final String DOMAIN_OAUTH                     = DOMAIN + "/oauth2";
    public static final String DOMAIN_USER                      = DOMAIN + "/v1.0/user";
    public static final String DOMAIN_DEVICE                    = DOMAIN + "/v1.0/devices";
    public static final String DOMAIN_SENSOR                    = DOMAIN + "/v1.0/sensors";
    public static final String DOMAIN_DATA                      = DOMAIN + "/v1.0/data";
    public static final String DOMAIN_APP                       = DOMAIN + "/v1.0/app";
    public static final String DOMAIN_SUGGESTION                = DOMAIN + "/v1.0/suggestion";
    public static final String DOMAIN_COUNTRY                   = DOMAIN + "/v1.0/country";
    public static final String DOMAIN_HARDWARE                  = DOMAIN + "/v1.0/hardware";

    // OAuth & User
    public static final int API_USER_OAUTH                      = 1101;
    public static final int API_USER_REGISTER                   = 1102;
    public static final int API_USER_GET_INFO                   = 1103;
    public static final int API_USER_CHANGE_PWD                 = 1104;
    public static final int API_USER_CHANGE_NAME                = 1105;
    public static final int API_USER_CHANGE_EMAIL               = 1106;
    public static final int API_USER_RESET_PWD_EMAIL            = 1107;
    public static final int API_USER_BIND_QQ                    = 1108;
    public static final int API_USER_UNBIND_QQ                  = 1109;
    public static final int API_USER_UPLOAD_AVATAR              = 1110; // USERNAME, AVATAR
    public static final int API_USER_CHANGE_BASIC_INFO          = 1111; // EMAIL, MOBILE, USERNAME
    public static final int API_USER_REGISTER_MOBILE            = 1112;
    public static final int API_USER_SEND_MOBILE_VERIFYCODE     = 1113;
    public static final int API_USER_RESET_PWD_MOBILE           = 1114;
    public static final int API_USER_BIND_MOBILE                = 1115;
    public static final int API_USER_CHECK_BIND_QQ              = 1115;

    // Device
    public static final int API_DEVICE_LIST                     = 1201;
    public static final int API_DEVICE_STATUS                   = 1202;
    public static final int API_DEVICE_IP                       = 1203;
    public static final int API_DEVICE_ADD                      = 1204;
    public static final int API_DEVICE_EDIT                     = 1205;
    public static final int API_DEVICE_DELETE                   = 1206;
    public static final int API_DEVICE_CHANGE_TYPE              = 1207;
    public static final int API_DEVICE_SHARE                    = 1208;
    public static final int API_DEVICE_GET_SHARE_LIST           = 1209;
    public static final int API_DEVICE_SHARE_DELETE             = 1210;
    public static final int API_DEVICE_ADD_IMGAGE               = 1211;

    // Sensor
    public static final int API_SENSOR_LIST_ALL                 = 1301;
    public static final int API_SENSOR_LIST_SPECIAL             = 1302;
    public static final int API_SENSOR_CONTROL                  = 1303;
    public static final int API_SENSOR_ADD                      = 1304;
    public static final int API_SENSOR_DELETE                   = 1305;

    // Data
    public static final int API_DATA_HISTORY                    = 1401;
    public static final int API_DATA_GPS_FENCE                  = 1402;
    public static final int API_DATA_UPLOAD_STATUS              = 1403;
    public static final int API_DATA_UPLOAD_POWER               = 1404;
    public static final int API_DATA_POWER_HISTOY               = 1405;

    // App
    public static final int API_APP_UPDATE                      = 1501;

    // Suggestion
    public static final int API_SUGGESTION_LIST                 = 1601;
    public static final int API_SUGGESTION_ADD                  = 1602;

    // Country
    public static final int API_COUNTRY_AREACODE                = 1701;

    // Hardware
    public static final int API_HARDWARE_UPDATE                 = 1801;

    static {
        // OAuth & User
        urlMap.put(API_USER_OAUTH,                              DOMAIN_OAUTH + "/authorize");
        urlMap.put(API_USER_REGISTER,                           DOMAIN_USER + "/register");
        urlMap.put(API_USER_GET_INFO,                           DOMAIN_USER + "/info");
        urlMap.put(API_USER_CHANGE_PWD,                         DOMAIN_USER + "/changepassword");
        urlMap.put(API_USER_CHANGE_NAME,                        DOMAIN_USER + "/changename");
        urlMap.put(API_USER_CHANGE_EMAIL,                       DOMAIN_USER + "/changeemail");
        urlMap.put(API_USER_RESET_PWD_EMAIL,                    DOMAIN_USER + "/forgotpwd");
        urlMap.put(API_USER_BIND_QQ,                            DOMAIN_USER + "/bind_qq");
        urlMap.put(API_USER_UNBIND_QQ,                          DOMAIN_USER + "/unbind_qq");
        urlMap.put(API_USER_UPLOAD_AVATAR,                      DOMAIN_USER + "/changeinfo");
        urlMap.put(API_USER_CHANGE_BASIC_INFO,                  DOMAIN_USER + "/changebaseinfo");
        urlMap.put(API_USER_REGISTER_MOBILE,                    DOMAIN_USER + "/register_m");
        urlMap.put(API_USER_SEND_MOBILE_VERIFYCODE,             DOMAIN_USER + "/message_valid");
        urlMap.put(API_USER_RESET_PWD_MOBILE,                   DOMAIN_USER + "/changepassword_m");
        urlMap.put(API_USER_BIND_MOBILE,                        DOMAIN_USER + "/bind_mobile");
        urlMap.put(API_USER_CHECK_BIND_QQ,                      DOMAIN_USER + "/check_qqbind");

        // Device
        urlMap.put(API_DEVICE_LIST,                             DOMAIN_DEVICE + "/list");
        urlMap.put(API_DEVICE_STATUS,                           DOMAIN_DEVICE + "/status");
        urlMap.put(API_DEVICE_IP,                               DOMAIN_DEVICE + "/ip");
        urlMap.put(API_DEVICE_ADD,                              DOMAIN_DEVICE + "/add");
        urlMap.put(API_DEVICE_EDIT,                             DOMAIN_DEVICE + "/edit");
        urlMap.put(API_DEVICE_DELETE,                           DOMAIN_DEVICE + "/del");
        urlMap.put(API_DEVICE_CHANGE_TYPE,                      DOMAIN_DEVICE + "/changetype");
        urlMap.put(API_DEVICE_SHARE,                            DOMAIN_DEVICE + "/share");
        urlMap.put(API_DEVICE_GET_SHARE_LIST,                   DOMAIN_DEVICE + "/sharelist");
        urlMap.put(API_DEVICE_SHARE_DELETE,                     DOMAIN_DEVICE + "/sharedel");
        urlMap.put(API_DEVICE_ADD_IMGAGE,                       DOMAIN_DEVICE + "/image");

        // Sensor
        urlMap.put(API_SENSOR_LIST_ALL,                         DOMAIN_SENSOR + "/list_all");
        urlMap.put(API_SENSOR_LIST_SPECIAL,                     DOMAIN_SENSOR + "/list");
        urlMap.put(API_SENSOR_CONTROL,                          DOMAIN_SENSOR + "/control");
        urlMap.put(API_SENSOR_ADD,                              DOMAIN_SENSOR + "/save");
        urlMap.put(API_SENSOR_DELETE,                           DOMAIN_SENSOR + "/delete");

        // Data
        urlMap.put(API_DATA_HISTORY,                            DOMAIN_DATA + "/");
        urlMap.put(API_DATA_GPS_FENCE,                          DOMAIN_DATA + "/fence");
        urlMap.put(API_DATA_UPLOAD_STATUS,                      DOMAIN_DATA + "/upload");
        urlMap.put(API_DATA_UPLOAD_POWER,                       DOMAIN_DATA + "/power/upload");
        urlMap.put(API_DATA_POWER_HISTOY,                       DOMAIN_DATA + "/power");

        // App
        urlMap.put(API_APP_UPDATE,                              DOMAIN_APP + "/update");

        // Suggestion
        urlMap.put(API_SUGGESTION_LIST,                         DOMAIN_SUGGESTION + "/list");
        urlMap.put(API_SUGGESTION_ADD,                          DOMAIN_SUGGESTION + "/add");

        // Country
        urlMap.put(API_COUNTRY_AREACODE,                        DOMAIN_COUNTRY + "/areacode");

        // Hardware
        urlMap.put(API_HARDWARE_UPDATE,                         DOMAIN_HARDWARE + "/update");
    }

    private static RequestHelper instance;

    private RequestHelper(Context context) {
        super(context);
    }

    public static synchronized RequestHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RequestHelper(context);
        }
        return instance;
    }

    public static String getUrlByAPI(int api) {
        return urlMap.get(api);
    }

    public static int getAPIByUrl(String url) {
        return JavaUtil.getHashKeyByValue(urlMap, url);
    }

    /*
     * User
     * ************************************************************************************************************************************
     */
    public void login(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_OAUTH, null, params, null, callBack);
    }

    public void registerEmail(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_REGISTER, null, params, null, callBack);
    }

    public void registerMobile(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_REGISTER_MOBILE, null, params, null, callBack);
    }

    public void changePasswd(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_CHANGE_PWD, null, params, null, callBack);
    }

    public void changeUserName(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_CHANGE_NAME, null, params, null, callBack);
    }

    public void changeEmail(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_CHANGE_EMAIL, null, params, null, callBack);
    }

    public void sendMobileVerifyCode(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_SEND_MOBILE_VERIFYCODE, null, params, null, callBack);
    }

    public void changeBasicInfo(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_CHANGE_BASIC_INFO, null, params, null, callBack);
    }

    public void changeExtendInfo(String name, Response.ErrorListener errorListener, Response.Listener listener, File imageFile) {
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        Bundle bundle = new Bundle();
        bundle.putString("url", getUrlByAPI(API_USER_UPLOAD_AVATAR));
        bundle.putString("name", name);
        bundle.putInt("type", PhotoMultipartRequest.UPLOAD_USER_AVATAR);
        PhotoMultipartRequest request = new PhotoMultipartRequest(mContext,bundle, errorListener, listener, imageFile);
        mQueue.add(request);
    }

    public void resetPwdByEmail(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_RESET_PWD_EMAIL, null, params, null, callBack);
    }

    public void resetPwdByMobile(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_RESET_PWD_MOBILE, null, params, null, callBack);
    }

    public void bindQQ(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_BIND_QQ, null, params, null, callBack);
    }

    public void unbindQQ(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_UNBIND_QQ, null, params, null, callBack);
    }

    public void getUserInfo(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_GET_INFO, null, params, null, callBack);
    }

    public void bindMobile(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_BIND_MOBILE, null, params, null, callBack);
    }

    public void checkQQBind(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_CHECK_BIND_QQ, null, params, null, callBack);
    }

    /*
     * Device
     * ************************************************************************************************************************************
     */
    public void getDeviceList(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_LIST, null, params, null, callBack);
    }

    public void getDeviceStatus(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_STATUS, null, params, null, callBack);
    }

    public void getDeviceIP(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_IP, null, params, null, callBack);
    }

    public void addDevice(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_ADD, null, params, null, callBack);
    }

    public void editDevice(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_EDIT, null, params, null, callBack);
    }

    public void removeDevice(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_DELETE, null, params, null, callBack);
    }

    public void changeDeviceType(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_CHANGE_TYPE, null, params, null, callBack);
    }

    public void shareDevice(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_SHARE, null, params, null, callBack);
    }

    public void getDeviceShareList(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_GET_SHARE_LIST, null, params, null, callBack);
    }

    public void removeDeviceShare(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_SHARE_DELETE, null, params, null, callBack);
    }

    public void addDeviceImage(String deviceId, Response.ErrorListener errorListener, Response.Listener listener, File imageFile) {
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        Bundle bundle = new Bundle();
        bundle.putString("url", getUrlByAPI(API_DEVICE_ADD_IMGAGE));
        bundle.putString("device_id", deviceId);
        bundle.putInt("type", PhotoMultipartRequest.UPLOAD_USER_AVATAR);
        PhotoMultipartRequest request = new PhotoMultipartRequest(mContext,bundle, errorListener, listener, imageFile);
        mQueue.add(request);
    }

    /*
     * Sensor
     * ************************************************************************************************************************************
     */

    public void getSensorList(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_LIST_ALL, null, params, null, callBack);
    }

    public void getDeviceSensors(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_LIST_SPECIAL, null, params, null, callBack);
    }

    public void controlSensor(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_CONTROL, null, params, null, callBack);
    }

    public void addSensor(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_ADD, null, params, null, callBack);
    }

    public void removeSensor(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_DELETE, null, params, null, callBack);
    }

    /*
     * Data
     * ************************************************************************************************************************************
     */
    public void getHistory(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_HISTORY, null, params, null, callBack);
    }

    public void getGPSData(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_GPS_FENCE, null, params, null, callBack);
    }

    public void uploadStatus(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_UPLOAD_STATUS, null, params, null, callBack);
    }

    public void uploadPower(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_UPLOAD_POWER, null, params, null, callBack);
    }

    public void getPowerHistory(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_POWER_HISTOY, null, params, null, callBack);
    }
    /*
     * Other
     * ************************************************************************************************************************************
     */

    public void getAppUpdate(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_APP_UPDATE, null, params, null, callBack);
    }

    public void getSuggestionList(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUGGESTION_LIST, null, params, null, callBack);
    }

    public void addSuggestion(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUGGESTION_ADD, null, params, null, callBack);
    }

    public void getCountryCode(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_COUNTRY_AREACODE, null, params, null, callBack);
    }

    public void getHardwareUpdate(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_HARDWARE_UPDATE, null, params, null, callBack);
    }

    /*
     * Base
     * ************************************************************************************************************************************
     */

    private void sendRequest(final int methodType, final int api, final Object[] urlParams,
                             final TreeMap<String, String> params, final String body, final FetchDataCallback callBack) {
        AbstractResponse reponse = new ResponseHelper(api, callBack);
        sendRequest(methodType, api, urlParams, params, body, reponse);
    }

    private void sendRequest(final int methodType, final int api, final Object[] urlParams, final Map<String, String> heads,
                             final TreeMap<String, String> params, final String body, final FetchDataCallback callBack) {
        AbstractResponse reponse = new ResponseHelper(api, callBack);
        sendRequest(methodType, api, urlParams, heads, params, body, reponse);
    }
}
