package com.lbh.rouwei.bese;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.lbh.rouwei.R;
import com.lbh.rouwei.common.network.AppController;
import com.scinan.sdk.alive.FiveSecondTimer;
import com.scinan.sdk.api.v2.agent.DataAgent;
import com.scinan.sdk.api.v2.agent.DeviceAgent;
import com.scinan.sdk.api.v2.agent.SensorAgent;
import com.scinan.sdk.api.v2.agent.UserAgent;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.lan.v1.LANRequestHelper;
import com.scinan.sdk.service.IPushCallback;
import com.scinan.sdk.service.IPushService;
import com.scinan.sdk.service.PushService;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import butterknife.ButterKnife;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/08/30
 *     desc   :
 * </pre>
 */
public abstract class BaseActivity extends AppCompatActivity implements AppController.ControllerCallback, FiveSecondTimer.FiveSeconeTimerCallback, FetchDataCallback {

    protected Context context;
    protected AppController mAppController;
    protected RequestHelper mRequestHelper;
    protected UserAgent mUserAgent;
    protected LANRequestHelper mLANRequestHelper;
    protected IPushService mPushService;
    String mClassName;
    protected DataAgent mDataAgent;

    protected DeviceAgent mDeviceAgent;
    TimeOutDownTimer timeOutDownTimer;
    FiveSecondTimer fiveSecendTimerCount;
    boolean isRusume = false;
    protected SensorAgent sensorAgent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(this.getLayoutId());
        ButterKnife.bind(this);
        this.initView();
        mUserAgent = new UserAgent(this);
        mRequestHelper = RequestHelper.getInstance(this);
        mDeviceAgent = new DeviceAgent(this);
        mDeviceAgent.registerAPIListener(this);
        // 控制组件
        mAppController = AppController.getController(this);
        mAppController.registerAPIListener(this);
        // bind push service
        Intent bindPushService = new Intent(this, PushService.class);
        bindPushService.setAction(Constants.ACTION_LISTEN_PUSH_STATUS);
        bindService(bindPushService, mPushServiceConnection, Context.BIND_AUTO_CREATE);

        // init LAN
        mLANRequestHelper = LANRequestHelper.getInstance(this);

        timeOutDownTimer = new TimeOutDownTimer(5000, 1000);
        fiveSecendTimerCount = FiveSecondTimer.getInstance(this);
        fiveSecendTimerCount.registerFiveSecondTimerListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
//        mDataAgent.unRegisterAPIListener(this);
//        mDeviceAgent.unRegisterAPIListener(this);
        mAppController.unRegisterAPIListener(this);
//        fiveSecendTimerCount.unRegisterFiveSecondTimerListener(this);
        if (mPushService != null) {
            try {
                mPushService.removeCallback(mClassName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(mPushServiceConnection);
        }
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 初始化视图
     */
    public abstract void initView();

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    ServiceConnection mPushServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPushService = IPushService.Stub.asInterface(service);
            try {
                mPushService.addCallback(mClassName, mPushCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPushService = null;
        }
    };

    IPushCallback mPushCallback = new IPushCallback.Stub() {
        @Override
        public void onConnected() throws RemoteException {
            OnPushConnected();
        }

        @Override
        public void onError() throws RemoteException {
        }

        @Override
        public void onClose() throws RemoteException {
        }

        @Override
        public void onPush(final String msg) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HardwareCmd hardwareCmd = HardwareCmd.parse(msg);
                    if (hardwareCmd == null)
                        return;
                    updateUIPush(hardwareCmd);
                }
            });
        }
    };

    protected void OnPushConnected() {
    }

    @Override
    public void OnControlSuccess(int optionCode, int protocol, String response) {
        if (protocol == 2) {
            runOnUiThread(() -> {
                HardwareCmd hardwareCmd = HardwareCmd.parse(response);
                if (hardwareCmd == null)
                    return;
                updateUIPush(hardwareCmd);
            });
        }
    }

    public void updateUIPush(HardwareCmd hardwareCmd) {
    }


    @Override
    public void OnControlFailed(int optionCode, int protocol, String error) {

    }

    @Override
    public void onTick() {
        LogUtil.t("onTick " + mClassName);
        if (isFinishing() || !isRusume) {
//            LogUtil.t("mPushService isFinishing() " + isFinishing() + " isRusume " + isRusume);
            return;
        }

        if (mPushService == null || !mPushService.asBinder().isBinderAlive()) {
            // bind push service
            Intent bindPushService = new Intent(this, PushService.class);
            bindPushService.setAction(Constants.ACTION_LISTEN_PUSH_STATUS);
            bindService(bindPushService, mPushServiceConnection, Context.BIND_AUTO_CREATE);

            return;
        }

        try {
            if (mPushService.isPushConnected()) {
                LogUtil.t("mPushService PushConnected");

                return;
            }

            LogUtil.t("mPushService repushConnect");
            mPushService.closePush();
            mPushService.connectPush();
        } catch (RemoteException e) {
            LogUtil.e(e);
        }
    }

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {

    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {

    }

    protected class TimeOutDownTimer extends CountDownTimer {

        public TimeOutDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
//            dismissWaitDialog();
//            showToast(R.string.send_timeout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRusume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRusume = false;
    }

    // 下发底板命令
    protected void sendCmd(String deviceId, String data) {
        if (!AndroidUtil.isNetworkEnabled(context)) {
//            showToast("网络故障");
            return;
        }

//        showWaitDialog(getString(R.string.zhengzaijiazai));
        timeOutDownTimer.start();
        HardwareCmd hardwareCmd = new HardwareCmd(deviceId, 1, data);
        try {
            if (mPushService == null || !mPushService.isPushConnected()) {
                sensorAgent.controlSensor(hardwareCmd);
            } else {
                mPushService.onSend(Configuration.getToken() + hardwareCmd.toString());
            }
        } catch (Exception e) {
            LogUtil.e(e);
            sensorAgent.controlSensor(hardwareCmd);
        }
    }
}
