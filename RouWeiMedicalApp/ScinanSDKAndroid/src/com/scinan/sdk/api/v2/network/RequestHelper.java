/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.network;

import android.content.Context;
import android.os.Bundle;

import com.scinan.sdk.api.v2.network.base.AbstractResponse;
import com.scinan.sdk.api.v2.network.base.BaseAPIHelper;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.volley.FetchDataCallback;
import com.scinan.sdk.volley.PhotoMultipartRequest;
import com.scinan.sdk.volley.Request;
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
    public static final String PRE_HOST_NAME_DEBUG              = "pretestapi.scinan.com";
    public static final String HOST_NAME_DEBUG                  = "testapi.scinan.com";
    public static final String HOST_NAME_RELEASE                = "api.scinan.com";
    public static final String HOST_NAME_PUSH_DEBUG             = "testpush.scinan.com";
    public static final String HOST_NAME_PUSH_RELEASE           = "push.scinan.com";
    public static final String HOST_NAME_DEVICE_PUSH_DEBUG      = "testwww.scinan.com";
    public static final String HOST_NAME_DEVICE_PUSH_RELEASE    = "www.scinan.com";
    public static final String HOST_NAME                        = BuildConfig.API_DEBUG ? (BuildConfig.API_DEBUG_PRE  ?PRE_HOST_NAME_DEBUG :HOST_NAME_DEBUG): HOST_NAME_RELEASE;
    public static final String HOST_NAME_PUSH                   = BuildConfig.API_DEBUG ? (BuildConfig.PUSH_DEVICE ? HOST_NAME_DEVICE_PUSH_DEBUG : HOST_NAME_PUSH_DEBUG) : (BuildConfig.PUSH_DEVICE ? HOST_NAME_DEVICE_PUSH_RELEASE : HOST_NAME_PUSH_RELEASE);
    public static final String DOMAIN                           = PROTOCOL + HOST_NAME;

    public static final String DOMAIN_USER                      = DOMAIN + "/v2.0/user";
    public static final String DOMAIN_COMMON                    = DOMAIN + "/v2.0/common";
    public static final String DOMAIN_DEVICE                    = DOMAIN + "/v2.0/device";
    public static final String DOMAIN_SENSOR                    = DOMAIN + "/v2.0/sensor";
    public static final String DOMAIN_DATA                      = DOMAIN + "/v2.0/data";
    public static final String DOMAIN_UPDATE                    = DOMAIN + "/v2.0/update";
    public static final String DOMAIN_SUGGESTION                = DOMAIN + "/v2.0/suggestion";
    public static final String DOMAIN_GPS                       = DOMAIN + "/v2.0/gps";
    public static final String DOMAIN_WEATHER                   = DOMAIN + "/v2.0/weather";
    public static final String DOMAIN_3P                        = DOMAIN + "/v2.0/thirdparty";
    public static final String DOMAIN_FACECOOK                  = DOMAIN + "/v2.0/food";
    public static final String DOMAIN_INNER                     = DOMAIN + "/v2.0/inner";
    public static final String DOMAIN_BRCODE                    = DOMAIN + "/v2.0/barcode";
    public static final String DOMAIN_BLUETOOTH                 = DOMAIN + "/v2.0/bluetooth";

    //Gateway
    public static final String DOMAIN_GATEWAY                   = DOMAIN + "/v2.0/gateway";
    public static final String DOMAIN_HOTEL_MG                  = DOMAIN + "/v2.0/hotel/mg";
    public static final String DOMAIN_HOTEL_PS                  = DOMAIN + "/v2.0/hotel/ps";
    public static final String DOMAIN_SUPERAPP                  = DOMAIN + "/v2.0/smart";

    public static final String DOMAIN_LOG                       = DOMAIN + "/v2.0/appnode";

    public static final String URL_PUSH_ADDRESS                 = BuildConfig.API_DEBUG ? "http://testwww.scinan.com/connectpre.json" : "http://www.scinan.com/connectpre.json";

    public static final int API_PUSH_ADDRESS                   = 1000;

    // Third party oauth
    public static final int API_3P_CHECK                        = 2001;
    public static final int API_3P_BIND                         = 2002;
    public static final int API_3P_LOGIN                        = 2003;
    public static final int API_3P_REGISTER                     = 2004;
    public static final int API_3P_BIND_EXIST                   = 2005;
    public static final int API_3P_BIND_LIST                    = 2006;
    public static final int API_3P_BIND_DEL                     = 2007;
    // OAuth & User
    public static final int API_USER_LOGIN                      = 2101;
    public static final int API_USER_REGISTER_EMAIL             = 2102;
    public static final int API_USER_REGISTER_MOBILE            = 2103;
    public static final int API_USER_CHANGE_PWD                 = 2104;
    public static final int API_USER_CHANGE_BASE                = 2105;
    public static final int API_USER_GET_INFO                   = 2106;
    public static final int API_USER_BIND_MOBILE                = 2107;
    public static final int API_USER_BIND_EMAIL                 = 2108;
    public static final int API_USER_BIND_QQ                    = 2109;
    public static final int API_USER_UNBIND_QQ                  = 2110;
    public static final int API_USER_RESET_PWD_EMAIL            = 2111;
    public static final int API_USER_RESET_PWD_MOBILE           = 2112;
    public static final int API_USER_UPLOAD_AVATAR              = 2113;
    public static final int API_USER_REFRESH_TOKEN              = 2114;

    // Device
    public static final int API_DEVICE_LIST                     = 2201;
    public static final int API_DEVICE_ADD                      = 2202;
    public static final int API_DEVICE_EDIT                     = 2203;
    public static final int API_DEVICE_DELETE                   = 2204;
    public static final int API_DEVICE_ADD_IMGAGE               = 2205;
    public static final int API_DEVICE_STATUS                   = 2206;
    public static final int API_DEVICE_SHARE                    = 2207;
    public static final int API_DEVICE_GET_SHARE_LIST           = 2208;
    public static final int API_DEVICE_SHARE_DELETE             = 2209;
    public static final int API_DEVICE_SHARE_ALL                = 2210;
    public static final int API_DEVICE_ADD_MULTI                = 2211;

    // Sensor
    public static final int API_SENSOR_LIST_ALL                 = 2301;
    // control api比较特殊需要拿api 跟optionCode 相加
    public static final int API_SENSOR_CONTROL                  = 10000;
    public static final int API_SENSOR_ADD                      = 2303;
    public static final int API_SENSOR_UPDATE                   = 2304;
    public static final int API_SENSOR_DELETE                   = 2305;
	public static final int API_SENSOR_SAVE                     = 2306;

    // Data
    public static final int API_DATA_CONTROL                    = 2401;
    public static final int API_DATA_POWER_HISTOY               = 2402;
    public static final int API_DATA_HISTOY                     = 2403;
    public static final int API_DATA_TIMER                      = 2404;
    public static final int API_DATA_POWER_DAY                  = 2405;
    public static final int API_DATA_POWER_MONTH                = 2406;
    public static final int API_DATA_POWER_HOUR                 = 2407;

    // Update
    public static final int API_UPDATE_APP                      = 2501;
    public static final int API_UPDATE_HARDWARE                 = 2502;

    // Suggestion
    public static final int API_SUGGESTION_LIST                 = 2601;
    public static final int API_SUGGESTION_ADD                  = 2602;

    // Common
    public static final int API_SEND_MOBILE_VERIFYCODE          = 2701;
    public static final int API_MARKET                          = 2702;
    public static final int API_BOOT_START_PROTECTED            = 2703;

    // GPS
    public static final int API_GPRS_FENCE                      = 2801;

    // Weather
    public static final int API_WEATHER_AIR                     = 2900;

    //Facecook
    public static final int API_FACECOOK_FOOD_DETAIL            = 2902;
    public static final int API_SENSOR_FACECOOK_CONTROL         = 20000;
    public static final int API_FACECOOK_FOOD_MARQUEE           = 2907;
    public static final int API_FACECOOK_FOOD_LIST              = 2903;
    public static final int API_FACECOOK_FOOD_SHARE             = 2904;
    public static final int API_FACECOOK_FOOD_FAVORITE          = 2905;
    public static final int API_FACECOOK_FOOD_FAVORITE_LIST     = 2906;

    //Smart
    // 只开放一个接口供公共调用
    public static final int API_SUPERAPP_DEVICE_ADD_SCENE       = 3100;
    public static final int API_SUPERAPP_UPDATE_PLUGIN          = 3103;

    public static final int API_LOG                             = 3200;
    public static final int API_LOG_SWITCH                      = 3201;
    public static final int API_INNER_SSL_CHECK                 = 3202;

    public static final int API_SUPERAPP_USER_THIRD_LOGIN       = 3203;
    public static final int API_SUPERAPP_UNBIND_THIRDPARTY      = 3204;
    public static final int API_SUPERAPP_CHECK_THIRDPARTY       = 3205;

    public static final int API_SUPERAPP_USER_THIRD_REGIST      = 3206;
    public static final int API_SUPERAPP_USER_THIRD_BOUND       = 3207;
    public static final int API_SUPERAPP_GET_AGREEMENT          = 3208;

    // qrcode
    public static final int API_GET_BARCODE                     = 3209;
    public static final int API_SACN_BARCODE                    = 3210;
    public static final int API_SACN_BARCODE_GET_DEVICEINFO     = 3211;

    //ble
    public static final int API_BLE_UPLOAD_DATA                 = 3212;
    public static final int API_BLE_GET_OTA_UPDATE              = 3213;
    public static final int API_BLE_REPORT_DATA                 = 3214;

    static {

        urlMap.put(API_PUSH_ADDRESS,                            URL_PUSH_ADDRESS);

        // Third party oauth
        urlMap.put(API_3P_CHECK,                                DOMAIN_3P + "/check");
        urlMap.put(API_3P_BIND,                                 DOMAIN_3P + "/bind");
        urlMap.put(API_3P_LOGIN,                                DOMAIN_3P + "/login");
        urlMap.put(API_3P_REGISTER,                             DOMAIN_3P + "/register");
        urlMap.put(API_3P_BIND_EXIST,                           DOMAIN_3P + "/bind/exist");
        urlMap.put(API_3P_BIND_LIST,                            DOMAIN_3P + "/bind/list");
        urlMap.put(API_3P_BIND_DEL,                             DOMAIN_3P + "/bind/del");
        // OAuth & User
        urlMap.put(API_USER_LOGIN,                              DOMAIN_USER + "/login");
        urlMap.put(API_USER_REGISTER_EMAIL,                     DOMAIN_USER + "/register_email");
        urlMap.put(API_USER_REGISTER_MOBILE,                    DOMAIN_USER + "/register_mobile");
        urlMap.put(API_USER_CHANGE_PWD,                         DOMAIN_USER + "/modify_pwd");
        urlMap.put(API_USER_CHANGE_BASE,                        DOMAIN_USER + "/modify_base");
        urlMap.put(API_USER_GET_INFO,                           DOMAIN_USER + "/info");
        urlMap.put(API_USER_BIND_MOBILE,                        DOMAIN_USER + "/bind_mobile");
        urlMap.put(API_USER_BIND_EMAIL,                         DOMAIN_USER + "/bind_email");
        urlMap.put(API_USER_BIND_QQ,                            DOMAIN_USER + "/bind_qq");
        urlMap.put(API_USER_UNBIND_QQ,                          DOMAIN_USER + "/unbind_qq");
        urlMap.put(API_USER_RESET_PWD_EMAIL,                    DOMAIN_USER + "/forgotpwd_email");
        urlMap.put(API_USER_RESET_PWD_MOBILE,                   DOMAIN_USER + "/forgotpwd_mobile");
        urlMap.put(API_USER_UPLOAD_AVATAR,                      DOMAIN_USER + "/avatar");
        urlMap.put(API_USER_REFRESH_TOKEN,                      DOMAIN_USER + "/refresh_token");
        //获取用户协议
        urlMap.put(API_SUPERAPP_GET_AGREEMENT,                  DOMAIN + "/v2.0/notoken/agreement/detail");

        // Device
        urlMap.put(API_DEVICE_LIST,                             DOMAIN_DEVICE + "/list");
        urlMap.put(API_DEVICE_ADD,                              DOMAIN_DEVICE + "/add");
        urlMap.put(API_DEVICE_ADD_MULTI,                        DOMAIN_DEVICE + "/batchAdd");
        urlMap.put(API_DEVICE_EDIT,                             DOMAIN_DEVICE + "/modify");
        urlMap.put(API_DEVICE_DELETE,                           DOMAIN_DEVICE + "/delete");
        urlMap.put(API_DEVICE_ADD_IMGAGE,                       DOMAIN_DEVICE + "/image");
        urlMap.put(API_DEVICE_STATUS,                           DOMAIN_DEVICE + "/status");
        urlMap.put(API_DEVICE_SHARE,                            DOMAIN_DEVICE + "/share");
        urlMap.put(API_DEVICE_GET_SHARE_LIST,                   DOMAIN_DEVICE + "/share_list");
        urlMap.put(API_DEVICE_SHARE_DELETE,                     DOMAIN_DEVICE + "/share_delete");
        urlMap.put(API_DEVICE_SHARE_ALL,                        DOMAIN_DEVICE + "/share_all");

        // Sensor
        urlMap.put(API_SENSOR_LIST_ALL,                         DOMAIN_SENSOR + "/list");
        urlMap.put(API_SENSOR_CONTROL,                          DOMAIN_SENSOR + "/control");
        urlMap.put(API_SENSOR_ADD,                              DOMAIN_SENSOR + "/add");
        urlMap.put(API_SENSOR_UPDATE,                           DOMAIN_SENSOR + "/update");
        urlMap.put(API_SENSOR_DELETE,                           DOMAIN_SENSOR + "/delete");
        urlMap.put(API_SENSOR_SAVE,                             DOMAIN_SENSOR + "/save");

        // Common
        urlMap.put(API_SEND_MOBILE_VERIFYCODE,                  DOMAIN_COMMON + "/message_valid");
        urlMap.put(API_MARKET,                                  DOMAIN_COMMON + "/market?company_id=%1$s&language=%2$s");
        urlMap.put(API_BOOT_START_PROTECTED,                    DOMAIN_COMMON + "/app/vendor");

        // Data
        urlMap.put(API_DATA_CONTROL,                            DOMAIN_DATA + "/control");
        urlMap.put(API_DATA_POWER_HISTOY,                       DOMAIN_DATA + "/power");
        urlMap.put(API_DATA_HISTOY,                             DOMAIN_DATA + "/history");
        urlMap.put(API_DATA_TIMER,                              DOMAIN_DATA + "/timer");
        urlMap.put(API_DATA_POWER_HOUR,                         DOMAIN_DATA + "/power");
        urlMap.put(API_DATA_POWER_DAY,                          DOMAIN_DATA + "/power");
        urlMap.put(API_DATA_POWER_MONTH,                        DOMAIN_DATA + "/power");

        // GPS
        urlMap.put(API_GPRS_FENCE,                              DOMAIN_GPS + "/fence");

        // Update
        urlMap.put(API_UPDATE_APP,                              DOMAIN_UPDATE + "/app");
        urlMap.put(API_UPDATE_HARDWARE,                         DOMAIN_UPDATE + "/hardware");

        // Suggestion
        urlMap.put(API_SUGGESTION_LIST,                         DOMAIN_SUGGESTION + "/type_list");
        urlMap.put(API_SUGGESTION_ADD,                          DOMAIN_SUGGESTION + "/add");

        // Weather
        urlMap.put(API_WEATHER_AIR,                             DOMAIN_WEATHER + "/air");

        //Facecook
        urlMap.put(API_FACECOOK_FOOD_DETAIL,                    DOMAIN_FACECOOK + "/detail");
        urlMap.put(API_SENSOR_FACECOOK_CONTROL,                 DOMAIN_FACECOOK + "/control");
        urlMap.put(API_FACECOOK_FOOD_FAVORITE,                  DOMAIN_FACECOOK + "/favorite");
        urlMap.put(API_FACECOOK_FOOD_FAVORITE_LIST,             DOMAIN_FACECOOK + "/favorite/list");
        urlMap.put(API_FACECOOK_FOOD_LIST,                      DOMAIN_FACECOOK + "/list");
        urlMap.put(API_FACECOOK_FOOD_MARQUEE,                   DOMAIN_FACECOOK + "/marquee");
        urlMap.put(API_FACECOOK_FOOD_SHARE,                     DOMAIN_FACECOOK + "/share");

        //Smart
        urlMap.put(API_SUPERAPP_UPDATE_PLUGIN,                  DOMAIN_SUPERAPP + "/product/update");
        //情景下设备动作添加修改，包括单品和子设备，保存动作时候用
        urlMap.put(API_SUPERAPP_DEVICE_ADD_SCENE,               DOMAIN_SUPERAPP + "/scene/device/add");

        urlMap.put(API_LOG,                                     DOMAIN_LOG      + "/save");
        urlMap.put(API_LOG_SWITCH,                              DOMAIN_LOG      + "/switch");

        urlMap.put(API_INNER_SSL_CHECK,                         DOMAIN_INNER    + "/ssl/check");

        //第三方用户登录
        urlMap.put(API_SUPERAPP_USER_THIRD_LOGIN,               DOMAIN_3P + "/login");
        urlMap.put(API_SUPERAPP_CHECK_THIRDPARTY,               DOMAIN_3P + "/check");
        urlMap.put(API_SUPERAPP_UNBIND_THIRDPARTY,              DOMAIN_3P + "/bind/del");

        //第三方用户快速注册
        urlMap.put(API_SUPERAPP_USER_THIRD_REGIST,              DOMAIN_3P + "/register");
        urlMap.put(API_SUPERAPP_USER_THIRD_BOUND,               DOMAIN_3P + "/bind/exist");

        // qrcode
        urlMap.put(API_GET_BARCODE,                             DOMAIN_BRCODE + "/generate");
        urlMap.put(API_SACN_BARCODE,                            DOMAIN_BRCODE + "/parse");

        urlMap.put(API_SACN_BARCODE_GET_DEVICEINFO,             DOMAIN_DEVICE + "/barcode_info");

        //BLE
        urlMap.put(API_BLE_UPLOAD_DATA,                         DOMAIN_BLUETOOTH + "/upload");
        urlMap.put(API_BLE_GET_OTA_UPDATE,                      DOMAIN_BLUETOOTH + "/update");
        urlMap.put(API_BLE_REPORT_DATA,                      DOMAIN_BLUETOOTH + "/data/report");
    }

    private static RequestHelper instance;

    //给外界自定义API列表的接口，后续不是所有API都需要加入SDK
    public RequestHelper(Context context) {
        super(context);
    }

    public static synchronized RequestHelper getInstance(Context context) {
        if (instance == null) {
            instance = new RequestHelper(context.getApplicationContext());
        }
        return instance;
    }

    public static String getUrlByAPI(int api) {
        return urlMap.get(api);
    }

    public static void putUrl2UrlMap(int api, String url) {
        urlMap.put(api, url);
    }

    public void getPushAddress(final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_PUSH_ADDRESS, null, null, null, callBack);
    }

    public void check3P(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_3P_CHECK, null, params, null, callBack);
    }

    public void bind3P(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_3P_BIND, null, params, null, callBack);
    }

    public void login3P(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_3P_LOGIN, null, params, null, callBack);
    }

    public void bind_exist3P(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_3P_BIND_EXIST, null, params, null, callBack);
    }

    public void register3P(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_3P_REGISTER, null, params, null, callBack);
    }

    public void get3PList(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_3P_BIND_LIST, null, params, null, callBack);
    }

    public void bind_del3P(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_3P_BIND_DEL, null, params, null, callBack);
    }
    /*
     * User
     * ************************************************************************************************************************************
     */
    public void login(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_LOGIN, null, params, null, callBack);
    }

    public void registerEmail(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_REGISTER_EMAIL, null, params, null, callBack);
    }

    public void registerMobile(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_REGISTER_MOBILE, null, params, null, callBack);
    }

    public void changePasswd(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_CHANGE_PWD, null, params, null, callBack);
    }

    public void changeUserName(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_CHANGE_BASE, null, params, null, callBack);
    }

    public void changeEmail(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_BIND_EMAIL, null, params, null, callBack);
    }

    public void sendMobileVerifyCode(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SEND_MOBILE_VERIFYCODE, null, params, null, callBack);
    }

    public void changeBasicInfo(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_CHANGE_BASE, null, params, null, callBack);
    }

    public void checkThirdParty(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUPERAPP_CHECK_THIRDPARTY, null, params, null, callBack);
    }

    public void changeExtendInfo(Response.ErrorListener errorListener, Response.Listener listener, File imageFile, String nickName) {
        RequestQueue mQueue = Volley.newRequestQueue(mContext);
        Bundle bundle = new Bundle();
        bundle.putString("url", getUrlByAPI(API_USER_UPLOAD_AVATAR));
        bundle.putString("nickName", nickName);
        bundle.putInt("type", PhotoMultipartRequest.UPLOAD_USER_AVATAR);
        PhotoMultipartRequest request = new PhotoMultipartRequest(mContext, bundle, errorListener, listener, imageFile);
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

    public void refreshToken(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_USER_REFRESH_TOKEN, null, params, null, callBack);
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

    public void addDevice(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_ADD, null, params, null, callBack);
    }

    public void addMultiDevices(final TreeMap<String,String> params,final FetchDataCallback callback) {
        sendRequest(Request.Method.POST, API_DEVICE_ADD_MULTI, null, params, null, callback);
    }

    public void editDevice(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_EDIT, null, params, null, callBack);
    }

    public void removeDevice(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_DELETE, null, params, null, callBack);
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

    public void getDevicesShareAll(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DEVICE_SHARE_ALL, null, params, null, callBack);
    }

    public void unbindThirdParty(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUPERAPP_UNBIND_THIRDPARTY, null, params, null, callBack);
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

    public void updateSensor(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_UPDATE, null, params, null, callBack);
    }

    public void controlSensor(final int sensorId, final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_CONTROL + sensorId, null, params, null, callBack);
    }

    public void addSensor(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_ADD, null, params, null, callBack);
    }

    public void removeSensor(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_DELETE, null, params, null, callBack);
    }

	 public void saveSensor(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_SAVE, null, params, null, callBack);
    }

    /*
     * Data
     * ************************************************************************************************************************************
     */
    public void getHistory(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_CONTROL, null, params, null, callBack);
    }

    public void getGPSData(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_GPRS_FENCE, null, params, null, callBack);
    }

    public void getPowerHistory(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_POWER_HISTOY, null, params, null, callBack);
    }

    public void getPowerHour(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_POWER_HOUR, null, params, null, callBack);
    }

    public void getPowerDay(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_POWER_DAY, null, params, null, callBack);
    }

    public void getPowerMonth(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_POWER_MONTH, null, params, null, callBack);
    }

    public void getAllHistory(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_HISTOY, null, params, null, callBack);
    }

    public void getTimer(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_DATA_TIMER, null, params, null, callBack);
    }
    /*
     * Other
     * ************************************************************************************************************************************
     */

    public void getAppUpdate(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_UPDATE_APP, null, params, null, callBack);
    }

    public void getHardwareUpdate(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_UPDATE_HARDWARE, null, params, null, callBack);
    }

    public void getSuggestionList(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUGGESTION_LIST, null, params, null, callBack);
    }

    public void addSuggestion(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUGGESTION_ADD, null, params, null, callBack);
    }

    public void getAir(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST,API_WEATHER_AIR, null, params, null, callBack);
    }

    public void getWeatherDetail(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_WEATHER_AIR, null, params, null, callBack);
    }

    public void saveLog(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_LOG, null, params, null, callBack);
    }

    public void getLogSwitch(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_LOG_SWITCH, null, params, null, callBack);
    }

    public void getSSLCheck(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_INNER_SSL_CHECK, null, params, null, callBack);
    }

    public void getBootStartAndProtectStart(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_BOOT_START_PROTECTED, null, params, null, callBack);
    }

    /*
     * Facecook
     * ************************************************************************************************************************************
     */
    public void getFoodDetail(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_FACECOOK_FOOD_DETAIL, null, params, null, callBack);
    }

    public void controlFood(final int sensorId, final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SENSOR_FACECOOK_CONTROL+sensorId, null, params, null, callBack);
    }

    public void getFoodMarquee(final TreeMap<String, String> params, final FetchDataCallback callback) {
        sendRequest(Request.Method.POST, API_FACECOOK_FOOD_MARQUEE, null, params, null, callback);
    }

    public void getFoodList(final TreeMap<String, String> params, final FetchDataCallback callback) {
        sendRequest(Request.Method.POST, API_FACECOOK_FOOD_LIST, null, params, null, callback);
    }

    public void setFoodFavorite(final TreeMap<String, String> params, final FetchDataCallback callback) {
        sendRequest(Request.Method.POST, API_FACECOOK_FOOD_FAVORITE, null, params, null, callback);
    }

    public void getFoodFavoriteList(final TreeMap<String, String> params, final FetchDataCallback callback) {
        sendRequest(Request.Method.POST, API_FACECOOK_FOOD_FAVORITE_LIST, null, params, null, callback);
    }

    public void foodShard(final TreeMap<String, String> params, final FetchDataCallback callback) {
        sendRequest(Request.Method.POST, API_FACECOOK_FOOD_SHARE, null, params, null, callback);
    }

     /*
     * Smart
     * ************************************************************************************************************************************
     */

    public void sceneDeviceAdd(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUPERAPP_DEVICE_ADD_SCENE, null, params, null, callBack);
    }

    public void getSmartPluginUpdate(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUPERAPP_UPDATE_PLUGIN, null, params, null, callBack);
    }


    /**
     * 3-rd
     */
    public void getUserAgreement(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUPERAPP_GET_AGREEMENT, null, params, null, callBack);
    }

    public void thirdBoundForScinan(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUPERAPP_USER_THIRD_BOUND, null, params, null, callBack);
    }


    public void thirdRegisterForScinan(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SUPERAPP_USER_THIRD_REGIST, null, params, null, callBack);
    }

    /**
     * 二维码
     */
    // 生成二维码
    public void getBarcode(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_GET_BARCODE, null, params, null, callBack);
    }

    // 扫描二维码
    public void scanBarcode(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SACN_BARCODE, null, params, null, callBack);
    }


    public void getBarcodeDeviceInfo(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_SACN_BARCODE_GET_DEVICEINFO, null, params, null, callBack);
    }

    /**
     * 蓝牙
     */
    // 上传蓝牙设备数据
    public void uploadBleData(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_BLE_UPLOAD_DATA, null, params, null, callBack);
    }

    // 获取蓝牙OTA升级数据
    public void getBleOTA(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_BLE_GET_OTA_UPDATE, null, params, null, callBack);
    }

    // 蓝牙数据上报
    public void reportBleData(final TreeMap<String, String> params, final FetchDataCallback callBack) {
        sendRequest(Request.Method.POST, API_BLE_REPORT_DATA, null, params, null, callBack);
    }

    /*
     * Base
     * ************************************************************************************************************************************
     */

    public void sendRequest(final int methodType, final int api, final Object[] urlParams,
                             final TreeMap<String, String> params, final String body, final FetchDataCallback callBack) {
        AbstractResponse reponse = new ResponseHelper(api, callBack);
        sendRequest(methodType, api, urlParams, params, body, reponse);
    }

    public void sendRequest(final int methodType, final int api, final Object[] urlParams, final Map<String, String> heads,
                             final TreeMap<String, String> params, final String body, final FetchDataCallback callBack) {
        AbstractResponse reponse = new ResponseHelper(api, callBack);
        sendRequest(methodType, api, urlParams, heads, params, body, reponse);
    }
}
