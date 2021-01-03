package com.lbh.rouwei.bese;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.kongzue.dialog.util.DialogSettings;
import com.lbh.rouwei.common.constant.Constant;
import com.lbh.rouwei.common.event.SerialDataEvent;
import com.lbh.rouwei.common.utils.ByteUtil;
import com.scinan.sdk.config.BuildConfig;
import com.scinan.sdk.config.Configuration;
import com.shuyu.gsyvideoplayer.player.PlayerFactory;
import com.socks.library.KLog;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Objects;

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
    private SerialPortManager mSerialPortManager;
    public boolean isTouchedApp = true;
    private static final String PORT_PATH = "/dev/ttyS4";
    private static final int PORT_BAUDRATE = 9600;
    private static final String HEAD_FLAG = "/S00/1/";
        private static final String HEX_HEAD_FLAG = "2F5330302F31";
    private static final String END_FLAG = "0A";
    private boolean openSerialPort;
    //    public SerialHelper getSerialHelper() {
//        return serialHelper;
//    }
//
//    private SerialHelper serialHelper;
    private String lastSerialData = "";
    public boolean isUartOk = true;

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
        initSerialConfig();
        DialogSettings.theme = DialogSettings.THEME.LIGHT;
        DialogSettings.init();
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

//        AndroidUtil.startPushService(this);
//        AndroidUtil.startForgroundHeartbeatService(this);
    }

    StringBuffer stringBuffer = new StringBuffer();
    String[] dataArray = new String[3];

    volatile boolean isStartFlag = false;

    private void initSerialConfig() {
        mSerialPortManager = new SerialPortManager();
        mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File device) {
                isUartOk = true;
                Log.d("TAG_serial", "open serial onSuccess   :  " + device.getPath());
            }

            @Override
            public void onFail(File device, Status status) {
                isUartOk = false;
                Log.d("TAG_serial", "open serial fail   :  " + device.getPath());
            }
        });


        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                String hexString = ByteUtil.bytes2HexString(bytes);
                String sourceData = ByteUtil.hex2Ascii(hexString).trim();
                Log.d("TAG_serial", "原始数据 :  " + sourceData);

//                Log.d("TAG_serial", "原始数据2 :  " + hexString);

                if (stringBuffer == null) {
                    stringBuffer = new StringBuffer();
                }

                if (dataArray == null) {
                    dataArray = new String[3];
                }

                if (sourceData.startsWith(HEAD_FLAG)) {
//                if (hexString.startsWith(HEAD_FLAG)) {
                    isStartFlag = true;
                }

                if (isStartFlag) {
//                    stringBuffer.append(sourceData);
                    if (hexString.startsWith(HEX_HEAD_FLAG)) {
                        dataArray[0] = sourceData;
                    } else if (hexString.endsWith(END_FLAG)) {
                        dataArray[2] = sourceData;
                    } else {
                        dataArray[1] = sourceData;
                    }

                }

//                if (stringBuffer.length() >= 36) {
                String targetData = getTargetData();
                Log.d("TAG_serial", "onDataReceived  targetData:  " + targetData);
                if (targetData.length() >= 36) {
//                    String dataResult = stringBuffer.toString();
                    if (targetData.startsWith(HEAD_FLAG)) {
                        Log.d("TAG_serial", "onDataReceived  最终结果:  " + targetData);
                        EventBus.getDefault().post(new SerialDataEvent(1, targetData));
                        isStartFlag = false;
                        stringBuffer.setLength(0);
                        dataArray = null;
                    }
                }
            }

            @Override
            public void onDataSent(byte[] bytes) {
                Log.d("TAG_send", "onDataSent: " + ByteUtil.hex2Ascii(ByteUtil.bytes2HexString(bytes)).trim());
            }
        });
        openSerialPort = mSerialPortManager.openSerialPort(new File(PORT_PATH), PORT_BAUDRATE);

    }

    private String getTargetData() {
        stringBuffer.setLength(0);
        for (String data : dataArray) {
            if (!TextUtils.isEmpty(data)) {
                stringBuffer.append(data);
            }
        }
        return stringBuffer.toString();
    }

    public static AppApplication getApp() {
        return app;
    }

    public SerialPortManager getSerialPortManager() {
        return mSerialPortManager;
    }

    public void sendSerialCmdData(String data) {
        mSerialPortManager.sendBytes(data.getBytes());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mSerialPortManager.closeSerialPort();
    }
}
