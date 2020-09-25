package com.lbh.rouwei.bese;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.lbh.rouwei.activity.MainActivity;
import com.lbh.rouwei.common.constant.Constant;
import com.lbh.rouwei.common.network.AppController;
import com.scinan.sdk.alive.FiveSecondTimer;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.service.IPushCallback;
import com.scinan.sdk.service.IPushService;
import com.scinan.sdk.service.PushService;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;
import com.tencent.mmkv.MMKV;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/24
 *     desc   :
 * </pre>
 */
public abstract class BaseControlActivity extends BaseActivity implements AppController.ControllerCallback, FiveSecondTimer.FiveSeconeTimerCallback {
    protected TimeOutDownTimer timeOutDownTimer;
    protected FiveSecondTimer fiveSecendTimerCount;
    protected IPushService mPushService;
    protected String deviceId;
    protected AppController mAppController;

    @Override
    public void initData() {
        // 控制组件
        mAppController = AppController.getController(this);
        mAppController.registerAPIListener(this);
        // bind push service
        Intent bindPushService = new Intent(this, PushService.class);
        bindPushService.setAction(Constants.ACTION_LISTEN_PUSH_STATUS);
        bindService(bindPushService, mPushServiceConnection, Context.BIND_AUTO_CREATE);

        timeOutDownTimer = new MainActivity.TimeOutDownTimer(5000, 1000);
        fiveSecendTimerCount = FiveSecondTimer.getInstance(this);
        fiveSecendTimerCount.registerFiveSecondTimerListener(this);

        deviceId = MMKV.defaultMMKV().decodeString(Constant.KEY_DEVICE_ID);
    }

    @Override
    public void initView() {
    }

    protected ServiceConnection mPushServiceConnection = new ServiceConnection() {
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
    protected void showToast(String tip) {
//        ToastUtil.showMessage(this, tip);
        Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int strId) {
//        ToastUtil.showMessage(this, strId);
        Toast.makeText(context, getResources().getString(strId), Toast.LENGTH_SHORT).show();
    }

}
