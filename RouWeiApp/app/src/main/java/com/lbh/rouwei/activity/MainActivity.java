package com.lbh.rouwei.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.TimeUtils;
import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;
import com.lbh.rouwei.common.bean.AllStatus;
import com.lbh.rouwei.common.hardware.AppOptionCode;
import com.lbh.rouwei.common.utils.AppUtil;
import com.lbh.rouwei.zmodule.config.ui.activity.AirkissConfigStep1Activity;
import com.lbh.rouwei.zmodule.login.ui.activity.LoginActivity;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.util.TimeUtil;

import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseMvpActivity {


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
    ImageButton btnPower;
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

    String deviceId;
    AllStatus allStatus;
    int windValue = 1;
    private boolean isPowerOn = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        tvDateTime.setText(TimeUtil.getCurrentDate(TimeUtil.dateFormatHM));
        String date = TimeUtil.getCurrentDate("MM月dd日") + " " + TimeUtil.getWeekOfDate(new Date());
        tvDay.setText(date);
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
        mAppController.sendCommand(AppOptionCode.STATUS_SWITCH_POWER, deviceId, isPowerOn ? "0" : "1");
    }

    @OnClick(R.id.rb_home)
    public void onRbHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

    @OnClick(R.id.rb_function)
    public void onRbFunctionClicked() {
        if (allStatus.isPowerOn()) {
            showToast(getString(R.string.tip_open_power));
            return;
        }
        Intent intent = new Intent(this, FunctionActivity.class);
        intent.putExtra("bean", allStatus);
        startActivity(intent);
    }

    @OnClick(R.id.rb_wind)
    public void onRbWindClicked() {
        if (allStatus.isPowerOn()) {
            showToast(getString(R.string.tip_open_power));
            return;
        }
        Intent intent = new Intent(this, WindActivity.class);
        intent.putExtra("windspeed", windValue);
        intent.putExtra("bean", allStatus);
        intent.putExtra("flagPage", 1);
        startActivity(intent);
    }

    @OnClick(R.id.rb_timer)
    public void onRbTimerClicked() {
        startActivity(new Intent(this, TimerActivity.class));
    }

    @OnClick(R.id.rb_mode)
    public void onRbModeClicked() {
        if (allStatus.isPowerOn()) {
            showToast(getString(R.string.tip_open_power));
            return;
        }
        Intent intent = new Intent(this, WindActivity.class);
        intent.putExtra("windspeed", windValue);
        intent.putExtra("bean", allStatus);
        intent.putExtra("flagPage", 0);
        startActivity(intent);
    }

    @OnClick(R.id.rb_bizhi)
    public void onRbBizhiClicked() {
        startActivity(new Intent(MainActivity.this, AirkissConfigStep1Activity.class));
    }

    @Override
    public void updateUIPush(HardwareCmd hardwareCmd) {
        super.updateUIPush(hardwareCmd);

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
            tvCurMode.setText("手动");
        } else if ("2".equals(mode)) {
            tvCurMode.setText("自动");
        }

        tv_cur_temp.setText(allStatus.temperature + "℃");
        windValue = allStatus.getWindValue();
        tvCurWind.setText("风速：" + windValue);
        tvCurFunction.setText(allStatus.isNegativeIonSwitchOn() ? "功能：负离子开" : "功能：负离子关");


    }
}