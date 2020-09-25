package com.lbh.rouwei.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.TimeUtils;
import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseControlActivity;
import com.lbh.rouwei.common.bean.AllStatus;
import com.lbh.rouwei.common.constant.Constant;
import com.lbh.rouwei.common.hardware.AppOptionCode;
import com.lbh.rouwei.common.utils.AppUtil;
import com.lbh.rouwei.zmodule.config.ui.activity.AirkissConfigStep1Activity;
import com.lbh.rouwei.zmodule.login.ui.activity.LoginActivity;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.TimeUtil;
import com.socks.library.KLog;
import com.tencent.mmkv.MMKV;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseControlActivity {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_date_time)
    TextView tvDateTime;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.ll_date)
    LinearLayout llDate;
    @BindView(R.id.btn_power)
    ImageView btnPower;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.tv_cur_mode)
    TextView tvCurMode;
    @BindView(R.id.tv_cur_temp)
    TextView tv_cur_temp;
    @BindView(R.id.tv_cur_wind)
    TextView tvCurWind;
    @BindView(R.id.tv_cur_function)
    TextView tvCurFunction;
    @BindView(R.id.tv_cur_timer)
    TextView tvCurTimer;
    @BindView(R.id.ll_cur_data)
    LinearLayout llCurData;
    @BindView(R.id.rb_home)
    RadioButton rbHome;
    @BindView(R.id.rb_function)
    RadioButton rbFunction;
    @BindView(R.id.rb_wind)
    RadioButton rbWind;
    @BindView(R.id.rb_timer)
    RadioButton rbTimer;
    @BindView(R.id.rb_mode)
    RadioButton rbMode;
    @BindView(R.id.rb_bizhi)
    RadioButton rbBizhi;
    @BindView(R.id.container)
    ConstraintLayout container;

    AllStatus allStatus;
    int windValue = 1;
    private boolean isPowerOn = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void getExtarDataFromPrePage(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }

        String id = getIntent().getStringExtra(Constant.KEY_DEVICE_ID);
        if (!TextUtils.isEmpty(id)) {
            deviceId = id;
            MMKV.defaultMMKV().encode(Constant.KEY_DEVICE_ID, deviceId);
        }
    }

    @Override
    public void initData() {
        super.initData();
        AndroidUtil.startPushService(this);
        AndroidUtil.startForgroundHeartbeatService(this);
    }

    @Override
    public void initView() {
        super.initView();
        deviceId = MMKV.defaultMMKV().decodeString(Constant.KEY_DEVICE_ID);
        KLog.d("fafasfa :" + deviceId);
        if (!TextUtils.isEmpty(deviceId)) {
            mAppController.sendCommand(AppOptionCode.STATUS_GET_STATUS, deviceId, "-1");
        }
        tvDateTime.setText(TimeUtil.getCurrentDate(TimeUtil.dateFormatHM));
        String date = TimeUtil.getCurrentDate("MM月dd日") + " " + TimeUtil.getWeekOfDate(new Date());
        tvDay.setText(date);
    }

    @OnClick(R.id.iv_back)
    public void onIvBackClicked() {
        AppUtil.goAndroidHome(context);
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @OnClick(R.id.btn_power)
    public void onBtnPowerClicked() {
        if (TextUtils.isEmpty(deviceId)) {
            showToast("未连接设备");
            return;
        }
        mAppController.sendCommand(AppOptionCode.STATUS_SWITCH_POWER, deviceId, isPowerOn ? "0" : "1");
    }

    @OnClick(R.id.rb_home)
    public void onRbHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

    @OnClick(R.id.rb_function)
    public void onRbFunctionClicked() {
        if (TextUtils.isEmpty(deviceId)) {
            showToast("未连接设备");
            return;
        }

        if (allStatus == null) {
            showToast("设备无全状态");
            return;
        }

        if (!allStatus.isPowerOn()) {
            showToast(getString(R.string.tip_open_power));
            return;
        }
        Intent intent = new Intent(this, FunctionActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", allStatus);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.rb_wind)
    public void onRbWindClicked() {
        if (TextUtils.isEmpty(deviceId)) {
            showToast("未连接设备");
            return;
        }

        if (allStatus == null) {
            showToast("设备无全状态");
            return;
        }

        if (!allStatus.isPowerOn()) {
            showToast(getString(R.string.tip_open_power));
            return;
        }
        Intent intent = new Intent(this, WindActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", allStatus);
        bundle.putInt("windspeed", windValue);
        bundle.putInt("flagPage", 1);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.rb_timer)
    public void onRbTimerClicked() {
        if (TextUtils.isEmpty(deviceId)) {
            showToast("未连接设备");
            return;
        }

        if (allStatus == null) {
            showToast("设备无全状态");
            return;
        }

        if (!allStatus.isPowerOn()) {
            showToast(getString(R.string.tip_open_power));
            return;
        }
        Intent intent = new Intent(this, TimerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", allStatus);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.rb_mode)
    public void onRbModeClicked() {
        if (TextUtils.isEmpty(deviceId)) {
            showToast("未连接设备");
            return;
        }

        if (allStatus == null) {
            showToast("设备无全状态");
            return;
        }

        if (!allStatus.isPowerOn()) {
            showToast(getString(R.string.tip_open_power));
            return;
        }

        mAppController.sendCommand(AppOptionCode.STATUS_MODE, deviceId, "1");

        Intent intent = new Intent(this, WindActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", allStatus);
        bundle.putInt("windspeed", windValue);
        bundle.putInt("flagPage", 0);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.rb_bizhi)
    public void onRbBizhiClicked() {
        if (TextUtils.isEmpty(deviceId)) {
            showToast("未连接设备");
            return;
        }

        if (!allStatus.isPowerOn()) {
            showToast(getString(R.string.tip_open_power));
            return;
        }

        startActivity(new Intent(MainActivity.this, AirkissConfigStep1Activity.class));
    }

    @Override
    public void updateUIPush(HardwareCmd hardwareCmd) {
        super.updateUIPush(hardwareCmd);
        if (!deviceId.equals(hardwareCmd.deviceId)) {
            return;
        }

        allStatus = AllStatus.parseAllStatus(hardwareCmd.data);
        updateUI(allStatus);
    }

    private void updateUI(AllStatus allStatus) {
        isPowerOn = allStatus.isPowerOn();
        btnPower.setImageResource(allStatus.isPowerOn() ? R.drawable.icon_switch_on : R.drawable.icon_switch_off);
        llCurData.setVisibility(allStatus.isPowerOn() ? View.VISIBLE : View.GONE);

        tvDateTime.setText(TimeUtils.millis2String(System.currentTimeMillis(), "HH:mm"));
        String mode = allStatus.mode;
        if ("1".equals(mode)) {
            tvCurMode.setText("模式：手动");
        } else if ("2".equals(mode)) {
            tvCurMode.setText("模式：自动");
        }

        tv_cur_temp.setText("温度：" + allStatus.temperature + "℃");
        windValue = allStatus.getWindValue();
        tvCurWind.setText("风速：" + windValue);
        tvCurFunction.setText(allStatus.isNegativeIonSwitchOn() ? "功能：负离子开" : "功能：负离子关");

        rbFunction.setChecked(allStatus.isPowerOn());
        rbWind.setChecked(allStatus.isPowerOn());
        rbTimer.setChecked(allStatus.isPowerOn());
        rbMode.setChecked(allStatus.isPowerOn());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppController.unRegisterAPIListener(this);
        fiveSecendTimerCount.unRegisterFiveSecondTimerListener(this);
        if (mPushService != null) {
            try {
                mPushService.removeCallback(mClassName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(mPushServiceConnection);
        }
        AndroidUtil.stopForgroundHeartbeatService(this);
    }

}