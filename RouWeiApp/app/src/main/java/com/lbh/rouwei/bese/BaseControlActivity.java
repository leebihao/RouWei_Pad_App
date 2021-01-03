package com.lbh.rouwei.bese;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lbh.rouwei.activity.MainActivity;
import com.lbh.rouwei.common.bean.AllStatus;
import com.lbh.rouwei.common.constant.Constant;
import com.lbh.rouwei.common.event.SerialDataEvent;
import com.lbh.rouwei.common.event.TickEvent;
import com.lbh.rouwei.common.hardware.CmdControlManager;
import com.lbh.rouwei.common.network.AppController;
import com.lbh.rouwei.zmodule.login.ui.activity.LoginActivity;
import com.scinan.sdk.alive.FiveSecondTimer;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.contants.Constants;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.service.IPushCallback;
import com.scinan.sdk.service.IPushService;
import com.scinan.sdk.service.PushService;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.PreferenceUtil;
import com.scinan.sdk.util.ToastUtil;
import com.socks.library.KLog;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    protected CmdControlManager cmdControlManager;
    protected AllStatus allStatus;
    @Override
    public void initData() {
        // 控制组件
        mAppController = AppController.getController(this);
        mAppController.registerAPIListener(this);
        cmdControlManager = CmdControlManager.getInstance();
        // bind push service
        Intent bindPushService = new Intent(this, PushService.class);
        bindPushService.setAction(Constants.ACTION_LISTEN_PUSH_STATUS);
        bindService(bindPushService, mPushServiceConnection, Context.BIND_AUTO_CREATE);

        timeOutDownTimer = new MainActivity.TimeOutDownTimer(5000, 1000);
        fiveSecendTimerCount = FiveSecondTimer.getInstance(this);
        fiveSecendTimerCount.registerFiveSecondTimerListener(this);

        deviceId = MMKV.defaultMMKV().decodeString(Constant.KEY_DEVICE_ID);
        deviceId = PreferenceUtil.getString(context,Constant.KEY_DEVICE_ID);
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
            onPushServiceConnectError();
        }

        @Override
        public void onClose() throws RemoteException {
            onPushClose();
        }

        @Override
        public void onPush(final String msg) throws RemoteException {
            KLog.d("mPushService msg:" + msg);
            runOnUiThread(() -> {
                HardwareCmd hardwareCmd = HardwareCmd.parse(msg);
                if (hardwareCmd == null) {
                    return;
                }
                updateUIPush(hardwareCmd);
            });
        }
    };

    protected void onPushClose() {
        KLog.d("mPushService close");

    }

    protected void onPushServiceConnectError() {
        KLog.d("mPushService error");

    }

    protected void OnPushConnected() {
        KLog.d("mPushService PushConnected");
    }

    @Override
    public void OnControlSuccess(int optionCode, int protocol, String response) {
        if (protocol == 2) {
            runOnUiThread(() -> {
                HardwareCmd hardwareCmd = HardwareCmd.parse(response);
                if (hardwareCmd == null) {
                    return;
                }
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
            showToast("网络故障");
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


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }




    /**
     * 事件响应方法
     * 接收消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventTickCallback(TickEvent event) {
        onTickEventCallback();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivedUartData(SerialDataEvent event) {
        if (event.cmd_type == 0) {
            //串口错误
            showToast("串口发生错误");
            return;
        }
        String data = event.data;
        if (TextUtils.isEmpty(data)) {
            return;
        }
        try {
            String[] list = data.split("/", -1);
            allStatus = AllStatus.parseAllStatus(list[3]);
            updateUiFromUart(allStatus);
        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    protected abstract void updateUiFromUart(AllStatus allStatus);

    protected void onTickEventCallback() {

    }

    protected void showLoginDialog(Context context) {
        new AlertDialog.Builder(context).setMessage("未发现串口数据，请通过登录账号进行wifi控制设备").setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(context, LoginActivity.class));
            }
        }).setCancelable(false).show();
    }
}
