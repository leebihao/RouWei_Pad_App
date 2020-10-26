package com.lbh.rouwei.bese;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.lbh.rouwei.common.constant.Constant;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.util.AndroidUtil;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.socks.library.KLog;
import com.tencent.mmkv.MMKV;

import tv.danmaku.ijk.media.exo2.Exo2PlayerManager;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/08/30
 *     desc   :
 * </pre>
 */
public class AppApplication extends MultiDexApplication {

    private static AppApplication app;

    public boolean isTouchedApp = true;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
//        strategy.setAppPackageName("com.lbh_rouwei.full");
//        strategy.setAppReportDelay(10000);
//        strategy.setAppVersion("1.0.3");
//        CrashReport.initCrashReport(getApplicationContext(), "df6ebe44f6", true, strategy);
        //初始化MMKV组件
        MMKV.initialize(this);
        //初始化Klog日志组件
        KLog.init(true);

        initScinanConfig();

        //视频播放控件
        PlayerFactory.setPlayManager(Exo2PlayerManager.class);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initScinanConfig() {
        // 初始化软件信息
        com.scinan.sdk.config.BuildConfig.API_DEBUG = BuildConfig.API_DEBUG;
        com.scinan.sdk.config.BuildConfig.LOG_DEBUG = BuildConfig.LOG_DEBUG;
        com.scinan.sdk.config.BuildConfig.MQTT_SSL = BuildConfig.MQTT_SSL;
        com.scinan.sdk.config.BuildConfig.LOG_WRITE = BuildConfig.LOG_WRITE;
        // 初始化软件信息
        Configuration.setContext(this);
        Configuration.setAppKey(BuildConfig.API_DEBUG ? Constant.APP_KEY_DEBUG : Constant.APP_KEY_RELEASE);
        Configuration.setAppSecret(BuildConfig.API_DEBUG ? Constant.APP_SECRET_DEBUG : Constant.APP_SECRET_RELEASE);
        AndroidUtil.startPushService(this);
        AndroidUtil.startForgroundHeartbeatService(this);
    }

    public static AppApplication getApp() {
        return app;
    }
}
