package com.lbh.rouwei.zmodule.config.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lbh.rouwei.R;
import com.lbh.rouwei.activity.MainActivity;
import com.lbh.rouwei.bese.BaseMvpActivity;
import com.lbh.rouwei.common.constant.Constant;
import com.lbh.rouwei.zmodule.config.ui.views.CircleProgressBar;
import com.scinan.sdk.api.v2.agent.DeviceAgent;
import com.scinan.sdk.api.v2.bean.Device;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.connect.ConfigDeviceTaskFactory;
import com.scinan.sdk.connect.ConnectWakeLock;
import com.scinan.sdk.connect.ScinanConfigDeviceTask;
import com.scinan.sdk.device.ScinanConnectDevice;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.interfaces.ConfigDeviceCallback2;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.DialogUtils2;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.PreferenceUtil;
import com.scinan.sdk.volley.FetchDataCallback;
import com.tencent.mmkv.MMKV;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class AirkissConfigStep3Activity extends BaseMvpActivity implements ConfigDeviceCallback2, FetchDataCallback {


    WifiManager wifiManager;
    ScinanConfigDeviceTask mCurrentConfigTask;
    int configDeviceType = ConfigDeviceTaskFactory.SMART_AIRKISS;
    String mDeviceId, mDeviceType;
    String companyId;
    final ScinanConnectDevice mConnectScinanDevice = new ScinanConnectDevice() {
        @Override
        public String getCompanyId() {
            return "2001";
        }
    };

    String ssid, pwd;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_rooter)
    TextView tvRooter;
    @BindView(R.id.ssidEdit)
    EditText ssidEdit;
    @BindView(R.id.tv_pwd)
    TextView tvPwd;
    @BindView(R.id.pwEdit)
    EditText pwEdit;
    @BindView(R.id.btn_add_device)
    Button connectStart;
    @BindView(R.id.connectProgress)
    CircleProgressBar connectProgress;
    @BindView(R.id.layout_input)
    LinearLayout layoutInput;
    @BindView(R.id.layout_circle)
    RelativeLayout layoutCircle;

    DeviceAgent mDeviceAgent;
    Timer mAddDeviceTimer;
    Timer mConfigDeviceTimer;
    int mRetryTimes = 0;
    private final int MAX_RETRY_TIMES = 120;
    boolean isAddSuccess = false;
    boolean isConfigSuccess = false;
    private final int START_ADD_DEVICE_FOR_WAIT = 101;
    private final int START_ADD_DEVICE_FOR_TIME_OUT = 102;
    private final int START_CONFIG_DEVICE_FOR_TIME_OUT = 103;
    private final int START_CONFIG_DEVICE_FOR_WAIT = 105;
    int currentNetworkId;
    String extraObj;

    private Handler mRefreshDeviceStatusHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case START_ADD_DEVICE_FOR_WAIT:
                    // add start
                    waitingForAddDevice();
                    break;
                case START_ADD_DEVICE_FOR_TIME_OUT:
                    // add time out
                    showToast(R.string.add_device_fail);
                    cancellAll();
                    break;
                case START_CONFIG_DEVICE_FOR_TIME_OUT:
                    // config time out
                    if (mDeviceId == null || !isConfigSuccess) {
                        showToast(R.string.add_device_fail);
                    }
                    cancellAll();

                    break;
                case START_CONFIG_DEVICE_FOR_WAIT:
                    waitingForConfigDevice();
                    break;

            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_airkiss_config_step3;
    }

    @Override
    public void initView() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        ssid = AndroidUtil.getWifiName(this);
//        ssid = AndroidUtil.getWIFISSID(this);
        if (!TextUtils.isEmpty(ssid)) {
            ssidEdit.setText(ssid);
            ssidEdit.setSelection(ssid.length());
        }
        pwEdit.setOnEditorActionListener(onKeyListener);
        mCurrentConfigTask = ConfigDeviceTaskFactory.getTask(this, configDeviceType, mConnectScinanDevice, this);
        mDeviceAgent = new DeviceAgent(getApplicationContext());
        mDeviceAgent.registerAPIListener(this);
        connectProgress.setTextRGBColor(getResources().getColor(R.color.main_color));
        connectProgress.setCircleRGBColor(getResources().getColor(R.color.main_color));
    }

    @OnClick({R.id.btn_add_device})
    public void onIconClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_device:
                onConnectStartClicked();
                break;
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(String errMessage) {

    }

    @Override
    public void onStartConfig(int type) {

    }

    @Override
    public void onProgressConfig(int type, String msg) {

    }

    @Override
    public void onSuccessConfig(int type, HardwareCmd[] hardwareCmds) {
        //一配多的设备会返回多个HardwareCmd，一配一的只返回一个
        if (hardwareCmds != null && hardwareCmds.length > 0) {
            beginToAddDevice(hardwareCmds[0]);
        }
    }

    @Override
    public void onFailConfig(int type, String msg) {
        mRefreshDeviceStatusHandler.sendEmptyMessage(START_CONFIG_DEVICE_FOR_TIME_OUT);
    }


    /**
     * 开始配置
     */
    private void startRealConfig() {
        AndroidUtil.hideSoftInput(this, pwEdit);
        AndroidUtil.hideSoftInput(this, ssidEdit);
        try {
            LogUtil.d(mCurrentConfigTask.getStatus().toString());
            mDeviceId = null;
            switch (mCurrentConfigTask.getStatus()) {
                case FINISHED:
                    mCurrentConfigTask = ConfigDeviceTaskFactory.getTask(this, configDeviceType, mConnectScinanDevice, this);
                case PENDING:
                    mCurrentConfigTask.execute(ssid, ssidEdit.getText().toString().trim(), pwEdit.getText().toString(), String.valueOf(currentNetworkId), extraObj);
                    showProgressView();
                    break;
                case RUNNING:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ConnectWakeLock.releaseWakeLock();
        }
    }

    private void showProgressView() {
        mRetryTimes = 0;
        connectProgress.setMaxProgress(MAX_RETRY_TIMES);
        layoutInput.setVisibility(View.GONE);
        layoutCircle.setVisibility(View.VISIBLE);
        mRefreshDeviceStatusHandler.sendEmptyMessage(START_CONFIG_DEVICE_FOR_WAIT);
    }

    private void waitingForAddDevice() {
        if (mAddDeviceTimer == null)
            mAddDeviceTimer = new Timer();
        else
            return;
        mAddDeviceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mRetryTimes++;
                if (mRetryTimes > MAX_RETRY_TIMES) {
                    mRefreshDeviceStatusHandler.sendEmptyMessage(START_ADD_DEVICE_FOR_TIME_OUT);
                    return;
                }
                connectProgress.setProgressNotInUiThread(mRetryTimes);
                Device device = new Device(mDeviceId, mDeviceType);
                device.setProduct_id("");
                mDeviceAgent.addDevice(device);
            }
        }, 1000, 1000);
    }

    private void waitingForConfigDevice() {
        if (mConfigDeviceTimer == null)
            mConfigDeviceTimer = new Timer();
        else
            return;
        mConfigDeviceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mRetryTimes++;
                if (mRetryTimes > MAX_RETRY_TIMES) {
                    mRefreshDeviceStatusHandler.sendEmptyMessage(START_CONFIG_DEVICE_FOR_TIME_OUT);
                    return;
                }
                connectProgress.setProgressNotInUiThread(mRetryTimes);
            }
        }, 1000, 1000);
    }

    void beginToAddDevice(HardwareCmd hardwareCmd) {
        if (mConfigDeviceTimer != null) {
            mConfigDeviceTimer.cancel();
        }
        try {
            //注意，hardwareCmd的deviced是你的deviceId
            mDeviceId = hardwareCmd.deviceId;
            //注意，hardwareCmd的data是你的type
            mDeviceType = hardwareCmd.data;
            mRefreshDeviceStatusHandler.sendEmptyMessage(START_ADD_DEVICE_FOR_WAIT);
        } catch (Exception e) {
            e.printStackTrace();
            mRefreshDeviceStatusHandler.sendEmptyMessage(START_CONFIG_DEVICE_FOR_TIME_OUT);
        }
    }

    private void cancellAll() {
        if (mAddDeviceTimer != null)
            mAddDeviceTimer.cancel();
        if (mConfigDeviceTimer != null)
            mConfigDeviceTimer.cancel();
        if (mCurrentConfigTask != null)
            mCurrentConfigTask.finish();
        if (isAddSuccess) {
            setResult(RESULT_OK);
            PreferenceUtil.saveSsidPwd(this, ssidEdit.getText().toString(), pwEdit.getText().toString());
            finish();
        } else {
//            ConfigDeviceChoiceActivity_.intent(this).allSSID(allSSID).currentNetworkId(currentNetworkId).SCAN_QRCODE_CODE(SCAN_QRCODE_CODE).deviceSSID(deviceSSID).start();
            finish();
        }
    }

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
        switch (api) {
            case RequestHelper.API_DEVICE_ADD:
                mAddDeviceTimer.cancel();
                showToast(getString(R.string.add_success));
                isAddSuccess = true;
                MMKV.defaultMMKV().encode(Constant.KEY_DEVICE_ID, mDeviceId);
                AndroidUtil.startPushService(this);
                AndroidUtil.startForgroundHeartbeatService(this);
                startActivity(new Intent(this, MainActivity.class));
                cancellAll();
                break;
        }
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        switch (api) {
            case RequestHelper.API_DEVICE_ADD:
                if (JsonUtil.getResultCode(responseBody) == 20002) {
                    mAddDeviceTimer.cancel();
                    showToast(R.string.add_success);
                    isAddSuccess = true;
                    cancellAll();
                    return;
                }
                if (mRetryTimes > MAX_RETRY_TIMES) {
                    showToast(getString(R.string.add_device_fail));
                    cancellAll();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mAddDeviceTimer != null)
            mAddDeviceTimer.cancel();
        if (mConfigDeviceTimer != null)
            mConfigDeviceTimer.cancel();
        if (mCurrentConfigTask != null)
            mCurrentConfigTask.finish();
        super.onBackPressed();
    }

    private TextView.OnEditorActionListener onKeyListener = (v, actionId, event) -> {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }

            onConnectStartClicked();

            return true;
        }
        return false;
    };

    @OnClick(R.id.iv_back)
    public void onIvBackClicked() {
        finish();
    }

    public void onConnectStartClicked() {
        if (TextUtils.isEmpty(ssidEdit.getText().toString().trim())) {
            showToast(R.string.ssid_null);
            return;
        }

        if (TextUtils.isEmpty(pwEdit.getText().toString().trim())) {
            showToast(" 密码不能为空");
            return;
        }

        //6120模块需要判断ssid和pwd长度不能超过32字节
        if (configDeviceType == ConfigDeviceTaskFactory.SMART_6120) {
            if (ssidEdit.getText().toString().trim().getBytes().length > 32) {
                showToast(getString(R.string.add_ssid_too_long));
                return;
            }
            if (pwEdit.getText().toString().trim().getBytes().length > 32) {
                showToast(R.string.device_add_pwd_long);
                return;
            }
        }

        AndroidUtil.hideSoftInput(this, pwEdit);
        AndroidUtil.hideSoftInput(this, ssidEdit);

        if (pwEdit.getText().toString().endsWith(" ")) {
            DialogUtils2.getConfirmDialog(this, getString(R.string.device_add_pwd_end_error), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startRealConfig();
                }

            }).show();
            return;
        }
        startRealConfig();
    }

    @Override
    protected void getExtarDataFromPrePage(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDeviceAgent.unRegisterAPIListener(this);
        cancellAll();
    }
}