package com.lbh.rouwei.bese;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.lbh.rouwei.common.constant.Constant;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.socks.library.KLog;
import com.tencent.mmkv.MMKV;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/08/30
 *     desc   :
 * </pre>
 */
public class AppApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化MMKV组件
        MMKV.initialize(this);
        //初始化Klog日志组件
        KLog.init(true);

        initScinanConfig();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initScinanConfig() {
        // 初始化软件信息
        Configuration.setContext(this);
        Configuration.setAppKey(BuildConfig.API_DEBUG ? Constant.APP_KEY_DEBUG : Constant.APP_KEY_RELEASE);
        Configuration.setAppSecret(BuildConfig.API_DEBUG ? Constant.APP_SECRET_DEBUG : Constant.APP_SECRET_RELEASE);
    }
}
