/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.service.ForgroundHeartService;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by lijunjie on 15/12/7.
 */
public class AndroidUtil {

    private static char hexDigits[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    //手机各自Rom的键值组
    private static final HashMap<String, String> MOBILE_HOST_KEYS = new HashMap<String, String>();

    //特殊的手机版本添加
    private static final HashMap<String, String> MOBILE_HOST_EXTRA = new HashMap<String, String>();

    static {
        //小米手机
        MOBILE_HOST_KEYS.put("xiaomi", "ro.miui.ui.version.name");
        //华为emui手机
        MOBILE_HOST_KEYS.put("huawei", "ro.build.version.emui");
        //锤子smartisan手机
        MOBILE_HOST_KEYS.put("smartisan", "ro.smartisan.version");
        //魅族flyme手机
        MOBILE_HOST_KEYS.put("meizu", "ro.build.display.id");
        //VIVOfuntouch手机
        MOBILE_HOST_KEYS.put("vivo", "ro.vivo.os.build.display.id");
        //努比亚手机
        MOBILE_HOST_KEYS.put("nubia", "ro.build.rom.id");
        //联想手机
        MOBILE_HOST_KEYS.put("lenovo", "ro.build.display.id");
        //摩托罗拉手机
        MOBILE_HOST_KEYS.put("motorola", "ro.build.display.id");
        //HTC手机
        MOBILE_HOST_KEYS.put("htc", "ro.build.display.id");
        //三星手机
        MOBILE_HOST_KEYS.put("samsung", "ro.build.display.id");

        MOBILE_HOST_EXTRA.put("xiaomi", "MIUI_");
        MOBILE_HOST_EXTRA.put("lenovo", "Lenovo_");
        MOBILE_HOST_EXTRA.put("motorola", "MOTO_");
        MOBILE_HOST_EXTRA.put("samsung", "Samsung_");
    }

    public static boolean isServiceAlive(Context context, String serviceClassName) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> running = manager
                .getRunningServices(200);

        for (int i = 0; i < running.size(); i++) {
            if (serviceClassName.equals(running.get(i).service.getClassName())) {
                return true;
            }
        }

        return false;

    }
    public static boolean checkPermission(Context context, String permName) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.checkPermission(permName, context.getPackageName()) == 0;
    }

    public static boolean isNeedRequestPermission() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean isPhoneSupportBLE() {
        return Build.VERSION.SDK_INT >= 19;
    }

    public static String getWifiMac(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String address =  wifiInfo.getMacAddress();

                //02000000是个默认隐私地址，过滤掉
                if (TextUtils.isEmpty(address) || address.equals("02:00:00:00:00:00")) {
                    try {
                        String jm = getWifiMacByJava();
                        if (jm.length() > 10) {
                            return jm;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return "";
                } else {
                    return address;
                }
            }

            LogUtil.e(context.getPackageName() + ",Could not get mac address.[no permission android.permission.ACCESS_WIFI_STATE");
        } catch (Exception e) {
            LogUtil.e(context.getPackageName() + ", Could not get mac address." + e.toString());
        }

        return "";
    }

    public static String getWifiMacByJava() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iF = interfaces.nextElement();

            byte[] addr = iF.getHardwareAddress();
            if (addr == null || addr.length == 0) {
                continue;
            }

            if (!"wlan0".equals(iF.getName())) {
                continue;
            }

            StringBuilder buf = new StringBuilder();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }

            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            return buf.toString();
        }

        return "";

    }

    public static String getIMEI(Context context) {

        String cache = PreferenceUtil.getIMEI(context);
        if (!TextUtils.isEmpty(cache)) {
            return cache;
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            LogUtil.e("No IMEI.");
        }

        String imei = "";

        try {
//            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
//                imei = telephonyManager.getDeviceId();
//            }
            Method method = telephonyManager.getClass().getMethod("getImei", int.class);
            String imei1 = (String) method.invoke(telephonyManager, 0);
            String imei2 = (String) method.invoke(telephonyManager, 1);
            if(TextUtils.isEmpty(imei2)){
                imei = imei1;
            }
            if(!TextUtils.isEmpty(imei1)){
                //因为手机卡插在不同位置，获取到的imei1和imei2值会交换，所以取它们的最小值,保证拿到的imei都是同一个
                if(imei1.compareTo(imei2) <= 0){
                    imei = imei1;
                }else{
                    imei = imei2;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("No IMEI.");
        }

        if (TextUtils.isEmpty(imei) || imei.length() < 10) {
            LogUtil.e("No IMEI, then get wifi mac");
            imei = getWifiMac(context);
        }

        if (TextUtils.isEmpty(imei)) {
            imei = String.valueOf(System.currentTimeMillis());
        }

        PreferenceUtil.saveIMEI(context, imei);

        return imei;
    }

    public static long getTimeInMillis(String time, String format) {
        try {
            Date date = new SimpleDateFormat(format, Locale.CHINESE).parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getGMT8String() {
        Calendar calendar = Calendar.getInstance();
        return getGMT8String(calendar.getTimeInMillis());
    }

    public static String getGMT8MilliString() {
        Calendar calendar = Calendar.getInstance();
        return getTimeString(calendar.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static String getGMT8String(long milliseconds) {
        Date date = new Date(milliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        TimeZone gmtZone = TimeZone.getTimeZone("GMT+8");
        sdf.setTimeZone(gmtZone);
        GregorianCalendar gc = new GregorianCalendar(gmtZone);
        gc.setTimeInMillis(date.getTime());
        return sdf.format(date);
    }

    public static String getTimeString(long milliseconds, String format) {
        Date date = new Date(milliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        TimeZone gmtZone = TimeZone.getTimeZone("GMT+8");
        sdf.setTimeZone(gmtZone);
        GregorianCalendar gc = new GregorianCalendar(gmtZone);
        gc.setTimeInMillis(date.getTime());
        return sdf.format(date);
    }

    public static String getAppKey(Context context) {
        return getMetaData(context, "SCINAN_APPKEY");
    }

    /*
       底板数据都是中国时区，来自底板的数据均需要转换为当前时区
     */
    public static int[] chinaZoneCover2CurrentZone(String time) {
        Calendar today = Calendar.getInstance();
        LogUtil.d(today.toString());
        String inputDate = new SimpleDateFormat("yyyy-MM-dd ").format(today.getTime()) + time;
        LogUtil.d("inputDate=" + inputDate);
        TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone timeZoneCur = TimeZone.getDefault();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        inputFormat.setTimeZone(timeZoneSH);
        Date date = null;
        try
        {
            date = inputFormat.parse(inputDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance(timeZoneCur);
        calendar.setTime(date);
        LogUtil.d(calendar.toString());
        int[] result = new int[3];
        int delta = today.get(Calendar.DAY_OF_YEAR) - calendar.get(Calendar.DAY_OF_YEAR);
        if (delta <0 && (today.get(Calendar.YEAR) > calendar.get(Calendar.YEAR))) {
            delta = 1;
        } else if (delta > 0 && (today.get(Calendar.YEAR) < calendar.get(Calendar.YEAR))) {
            delta = -1;
        }
        LogUtil.d("delta time is " + delta);
        result[0] =  delta;
        result[1] = calendar.get(Calendar.HOUR_OF_DAY);
        result[2] = calendar.get(Calendar.MINUTE);
        return result;
    }

    /*
       UI获得的时间均为本地时区，发送给底板时候需要转换为中国时区
     */
    public static int[] currentZoneCover2ChinaZone(String time) {
        Calendar today = Calendar.getInstance();
        LogUtil.d(today.toString());
        String inputDate = new SimpleDateFormat("yyyy-MM-dd ").format(today.getTime()) + time;
        LogUtil.d("inputDate=" + inputDate);
        TimeZone timeZoneSH = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone timeZoneCur = TimeZone.getDefault();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        inputFormat.setTimeZone(timeZoneCur);
        Date date = null;
        try
        {
            date = inputFormat.parse(inputDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance(timeZoneSH);
        calendar.setTime(date);
        LogUtil.d(calendar.toString());
        int[] result = new int[3];
        int delta = calendar.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR);
        if (delta <0 && (calendar.get(Calendar.YEAR) > today.get(Calendar.YEAR))) {
            delta = 1;
        } else if (delta > 0 && (calendar.get(Calendar.YEAR) < today.get(Calendar.YEAR))) {
            delta = -1;
        }
        LogUtil.d("delta time is " + delta);
        result[0] =  delta;
        result[1] = calendar.get(Calendar.HOUR_OF_DAY);
        result[2] = calendar.get(Calendar.MINUTE);
        LogUtil.d("=AA======" + result[0]);
        LogUtil.d("=BB======" + result[1]);
        LogUtil.d("=CC======" + result[2]);
        return result;
    }


    /***
     * luogical
     * 存放app下载目录
     * @param context
     * @return
     */
    public  static String getDownLoadAPPPath(Context context){
        // 默认存放app下载的路径
        return "/sn_app";
    }

    public static String getDownLoadPackagePath(Context context){
        // 默认存放app下载的路径
        return "/sn_package";
    }

    /***
     * luogical
     * 存放固件下载目录
     * @param context
     * @return
     */
    public  static String getDownLoadHardwarePath(Context context){
        // 默认存放固件下载的路径
        return "/sn_hw";
    }

    public static String getSmartPluginPath(Context context) {
        return "/sn_plugin";
    }


    public static String getAppSecret(Context context) {
        return getMetaData(context, "SCINAN_APPSECRET");
    }

    public static String getCompanyId(Context context) {
        return getMetaData(context, "SCINAN_COMPANY");
    }

    public static String getTecentAppId(Context context) {
        return getMetaData(context, "TECENT_APPID");
    }

    public static String getWeiboAppkey(Context context) {
        return getMetaData(context, "WEIBO_APPKEY");
    }

    public static String getWeixinPayAppID(Context context) {
        return getMetaData(context, "WEIXIN_PAY_APPID");
    }

    public static String getALIPayAppID(Context context) {
        return getMetaData(context, "ALI_PAY_APPID");
    }

    public static String getMetaData(Context context, String key) {
        String data = "Unknown";
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (info != null && info.metaData != null) {
                Object o = info.metaData.get(key);
                if (o != null) {
                    String s = o.toString();
                    if (s != null) {
                        data = s;
                    } else {
                        LogUtil.e("Could not read meta-data from AndroidManifest.xml, key is " + key);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e("Could not read meta-data from AndroidManifest.xml, key is " + key);
            e.printStackTrace();
        }

        return data;
    }

    //获取版本号
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        }catch(PackageManager.NameNotFoundException e){
            return 0;
        }
    }


    /**
     * 获取当前程序的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),
                    0);
            return packInfo.versionName;
        } catch (Exception e) {
        }
        return "NA";
    }

    //获取内部版本号
    public static String getVersionName2() {
        return getVersion(Configuration.getContext());
    }

    public static String getWifiIP(Context context) {
        String ip = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiinfo = wifiManager.getConnectionInfo();
        if (wifiinfo != null) {
            int ipAddress = wifiinfo.getIpAddress();
            ip = intToIp(ipAddress);
        }
        return ip;
    }

    public static String getWifiName(Context context) {
        String ssid = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiinfo = wifiManager.getConnectionInfo();
        if (wifiinfo == null) {
            return ssid;
        }
        String router = wifiinfo.getSSID();
        if (TextUtils.isEmpty(router))
            return ssid;
        if ("0.0.0.0".equals(getWifiIP(context))) {
            return ssid;
        }
        if (!"<unknown ssid>".equals(router) && !"0X".equals(router) && !"0x".equals(router))
            ssid = router.replace("\"", "");
        return ssid;
    }

    public static String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    public static boolean isNetworkEnabled(Context context) {
        if (context == null)
            return false;

        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Deprecated
    public static boolean isWifiConnected(Context context) {
        return isNetworkEnabled(context);
    }

    public static int dip2px(Context context, int dipValue) {
        float reSize = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * reSize) + 0.5);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }

    public static int[] getScreenDispaly(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        int result[] = {width, height};
        return result;
    }

    public static String getScinanSDPath(Context context) {
        if (checkSDCardAvailable()) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/scinan"+"/";
            File file = new File(path);
            if (!file.exists())
                file.mkdir();
            return path;
        }
        return context.getCacheDir().getAbsolutePath() + "/";
    }

    public static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     *  luogical
     * 判断手机所处系统语言版本
     * @return
     */
    public  static String getLanguage() {
        return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
    }

    public static String getSystemOS() {
        return "Android " + Build.VERSION.RELEASE + ";" + Build.MODEL;
    }

    public static boolean isSupportSSL() {
        String model = Build.MODEL;

        if (model.equals("Redmi Note 2")) {
            return false;
        }

        return PreferenceUtil.isSupportMQTTSSL(Configuration.getContext());
    }

    public static void setMQTTSSL() {
        BuildConfig.MQTT_SSL = BuildConfig.MQTT_SSL && isSupportSSL();
    }

    public static boolean isEmui3Version() {
        String emui = getSystemProperty("ro.build.version.emui");
        if (TextUtils.isEmpty(emui)) {
            return false;
        }

        emui = emui.trim().toLowerCase();

        if (!emui.contains("emotionui_") && emui.length() > 10)
            return false;
        int start = emui.indexOf("_");
        try {
            if (emui.contains(".")) {
                int firstPointIndex = emui.indexOf(".");
                return Integer.valueOf(emui.substring(start + 1, firstPointIndex)) < 4;
            } else {
                return Integer.valueOf(emui.substring(start + 1)) < 4;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isEmui4Version() {
        String emui = getSystemProperty("ro.build.version.emui");
        if (TextUtils.isEmpty(emui)) {
            return false;
        }

        emui = emui.trim().toLowerCase();
        if (!emui.contains("emotionui_") && emui.length() > 10)
            return false;
        int start = emui.indexOf("_");
        try {
            if (emui.contains(".")) {
                int firstPointIndex = emui.indexOf(".");
                return Integer.valueOf(emui.substring(start + 1, firstPointIndex)) > 3;
            } else {
                return Integer.valueOf(emui.substring(start + 1)) > 3;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isMiui8NougatVersion() {
        String miui = getSystemProperty("ro.miui.ui.version.name");
        if (TextUtils.isEmpty(miui)) {
            return false;
        }

        miui = miui.trim().toLowerCase();
        int start = miui.indexOf("v");
        if (start < 0)
            return false;

        try {
            boolean miui8 = Integer.valueOf(miui.substring(1).trim()) >= 8;
            return miui8 && (Build.VERSION.SDK_INT >= 24);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            LogUtil.e("Unable to read sysprop " + propName);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    public static String getClientVersion(Context context) {
        return getVersion(context) + ";" + getVersionCode(context);
    }

    public static String getNetWorkType(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                String type = networkInfo.getTypeName();
                return type;
            }
        } catch (Exception e) {
        }
        return "NA";
    }

    public static void startPushService(Context context) {
        Intent intent = new Intent(Constants.ACTION_START_PUSH_CONNECT);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    public static void stopPushService(Context context) {
        Intent intent = new Intent(Constants.ACTION_START_PUSH_CLOSE);
        intent.setPackage(context.getPackageName());
        context.sendBroadcast(intent);
    }

    public static void startForgroundHeartbeatService(Context context) {
        Intent intent = new Intent(context, ForgroundHeartService.class);
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    public static void stopForgroundHeartbeatService(Context context) {
        Intent intent = new Intent(context, ForgroundHeartService.class);
        intent.setPackage(context.getPackageName());
        context.stopService(intent);
    }

    public static void hideSoftInput(Context context, View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }
    public static void installApp(Context context,String apkPath,String apkName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setDataAndType(
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                            + apkPath, apkName)),
                    "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName;
            }
        }
        return null;
    }

    @Deprecated
    public static boolean isConnected(Context context, boolean isWifiOnly) {
        return isNetworkEnabled(context);
    }

    public static String getPluginId(String companyID, String type) {
        return ("com.scinan.p_" + companyID + "_" + type).toLowerCase();
    }

    public static String getBugInfo() {
        if (!BuildConfig.API_DEBUG) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(测试，");
        sb.append(BuildConfig.API_HTTPS ? "HTTPS，" : "HTTP，");
        sb.append(BuildConfig.LOG_DEBUG ? "日志打开）" : "日志关闭）,");
        return sb.toString();
    }

    public static String getLocation(Context context) {

        Location location = null;
//        if (!checkPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION))
//            return null;
//
//
//        if (BuildConfig.CLOSE_LOCATION){
//            return null;
//        }
//
//        LocationManager locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        if (locMan != null) {
//            location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if (location == null) {
//                location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            }
//        }
//
//        if (location == null) {
//            LogUtil.d("location is null to start location service");
//            //SNLocationManager.getInstance().requestLocation();
//        }

        return location != null ? String.format("%.6f,%.6f", location.getLatitude(), location.getLongitude()) : null;
    }

    public static int getPushPort() {
       return BuildConfig.MQTT_SSL ? (BuildConfig.PUSH_DEVICE ? 2883 : 2886) : (BuildConfig.PUSH_DEVICE ? 1883 : 1886);
    }

    public static String getSDKBuildInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("[a.v]");sb.append(getVersionName2());sb.append("\n");
        sb.append("[a.p]");sb.append(getCurProcessName(Configuration.getContext()));sb.append("\n");
        sb.append("[a.n]");sb.append(getVersion(Configuration.getContext()));sb.append("\n");
        sb.append("[a.c]");sb.append(getVersionCode(Configuration.getContext()));sb.append("\n");
        sb.append("[s.v]");sb.append(com.scinan.sdk.BuildConfig.SDK_VERSION);sb.append("\n");
        sb.append("[b.h]");sb.append(com.scinan.sdk.BuildConfig.SDK_BUILD_HOST);sb.append("\n");
        sb.append("[b.t]");sb.append(com.scinan.sdk.BuildConfig.SDK_BUILD_TIME);sb.append("\n");
        sb.append("[b.o]");sb.append(com.scinan.sdk.BuildConfig.SDK_BUILD_OS);sb.append("\n");
        sb.append("[a.h]");sb.append(String.valueOf(BuildConfig.API_HTTPS).substring(0, 1));sb.append("\n");
        sb.append("[a.d]");sb.append(String.valueOf(BuildConfig.API_DEBUG).substring(0,1));sb.append("\n");
        sb.append("[l.d]");sb.append(String.valueOf(BuildConfig.LOG_DEBUG).substring(0,1));sb.append("\n");
        sb.append("[l.w]");sb.append(String.valueOf(BuildConfig.LOG_WRITE).substring(0,1));sb.append("\n");
        sb.append("[l.t]");sb.append(BuildConfig.LOG_TRACE_LEVEL);sb.append("\n");
        sb.append("[p.s]");sb.append(String.valueOf(BuildConfig.MQTT_SSL).substring(0,1));sb.append("\n");
        sb.append("[i.m]");sb.append(getIMEI(Configuration.getContext()));sb.append("\n");
        sb.append("[a.k]");sb.append(Configuration.getAppKey(Configuration.getContext()));sb.append("\n");
        sb.append("[u.l]");sb.append(getLocation(Configuration.getContext()));sb.append("\n");
        sb.append("[u.o]");sb.append(getSystemOS());sb.append("\n");
        return sb.toString();
    }

    public static String generateRequestID(Context context) {
        return getMD5Str(getIMEI(context) + getGMT8MilliString());
    }

    public static String getMD5Str(String data) {
        return getMD5Str(data.getBytes());
    }

    public static String getMD5Str(byte[] data) {
        String md5 = null;
        if (data == null) {
            return md5;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(data);
            byte bytes[] = messageDigest.digest();
            StringBuffer md5Buffer = new StringBuffer(2 * bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                char c0 = hexDigits[(bytes[i] & 0xf0) >> 4];
                char c1 = hexDigits[bytes[i] & 0xf];
                md5Buffer.append(c0);
                md5Buffer.append(c1);
            }
            md5 = md5Buffer.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String getMobileRomVersion() {
        String manufacturer = getSystemProperty("ro.product.manufacturer");

        if (TextUtils.isEmpty(manufacturer)) {
            //获取厂商信息失败，一般不可能除非ROM定制很业余
            return null;
        }

        manufacturer = manufacturer.toLowerCase().trim();
        String versionKey = MOBILE_HOST_KEYS.get(manufacturer);
        if (TextUtils.isEmpty(versionKey)) {
            //非已知系统调用默认的id
            return getSystemProperty("ro.build.display.id");
        }

        return MOBILE_HOST_EXTRA.containsKey(manufacturer) ? MOBILE_HOST_EXTRA.get(manufacturer) + getSystemProperty(versionKey) : getSystemProperty(versionKey);
    }

    public static String getVendorName() {
        String name = getSystemProperty("ro.product.manufacturer");
        if (TextUtils.isEmpty(name)) {
            return null;
        }
        return name.toLowerCase().trim();
    }

    public static boolean isAppBuildDebug() {
        return (Configuration.getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
//        return getAppBuildConfigValue("DEBUG");
    }

    public static boolean getAppBuildConfigValue(String key) {
    try {
        Class clazz = Class.forName(Configuration.getContext().getPackageName() + ".BuildConfig");
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (field.getName().equals(key)) {
                return field.getBoolean(null);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return false;
}
}
