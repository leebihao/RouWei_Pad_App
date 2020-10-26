package com.lbh.rouwei.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseControlActivity;
import com.lbh.rouwei.common.bean.AllStatus;
import com.lbh.rouwei.common.constant.Constant;
import com.lbh.rouwei.common.event.DeviceIdEvent;
import com.lbh.rouwei.common.hardware.AppOptionCode;
import com.lbh.rouwei.common.utils.AppUtil;
import com.lbh.rouwei.common.utils.RxJavaUtils;
import com.lbh.rouwei.zmodule.login.ui.activity.LoginActivity;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.PreferenceUtil;
import com.scinan.sdk.util.TimeUtil;
import com.socks.library.KLog;
import com.tencent.mmkv.MMKV;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    CheckBox rbHome;
    @BindView(R.id.rb_function)
    CheckBox rbFunction;
    @BindView(R.id.rb_wind)
    CheckBox rbWind;
    @BindView(R.id.rb_timer)
    CheckBox rbTimer;
    @BindView(R.id.rb_mode)
    CheckBox rbMode;
    @BindView(R.id.rb_bizhi)
    CheckBox rbBizhi;
    @BindView(R.id.container)
    ConstraintLayout container;
    @BindView(R.id.tv_pm25)
    TextView tv_pm25;

    AllStatus allStatus;
    int windValue = 1;
    private boolean isPowerOn = false;

    private boolean isLogined = true;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceId(DeviceIdEvent event) {
        deviceId = event.deviceId;
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
        } else {
            deviceId = PreferenceUtil.getString(context, Constant.KEY_DEVICE_ID);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String id = intent.getStringExtra(Constant.KEY_DEVICE_ID);
        if (!TextUtils.isEmpty(id)) {
            deviceId = id;
            MMKV.defaultMMKV().encode(Constant.KEY_DEVICE_ID, deviceId);
        } else {
            deviceId = PreferenceUtil.getString(context, Constant.KEY_DEVICE_ID);
        }
        if (!TextUtils.isEmpty(deviceId)) {
            mAppController.sendCommand(AppOptionCode.STATUS_GET_STATUS, deviceId, "-1");
        }
        updateDateLayout();
        updateUI(allStatus);
    }

    @Override
    public void initData() {
        super.initData();
        if (checkDeviceIsNull()) return;
        AndroidUtil.startPushService(this);
        AndroidUtil.startForgroundHeartbeatService(this);
        //定时任务
        RxJavaUtils.interval(1, number -> {
            updateDateLayout();
        });

//        Account account = PreferenceUtil.getAccount(getApplicationContext());
//        if (account == null) {
//            btnLogin.setText("登录");
//            isLogined = false;
//        } else {
//            btnLogin.setText("退出登录");
//            isLogined = true;
//        }
    }

    private boolean checkDeviceIsNull() {
        if (TextUtils.isEmpty(deviceId)) {
            new AlertDialog.Builder(context).setMessage("未发现串口数据，请通过登录账号进行wifi控制").setPositiveButton("好的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }).setCancelable(false).show();
            return true;
        }
        return false;
    }

    @Override
    public void initView() {
        super.initView();
        deviceId = MMKV.defaultMMKV().decodeString(Constant.KEY_DEVICE_ID);
        KLog.d("fafasfa :" + deviceId);
        if (!TextUtils.isEmpty(deviceId)) {
            mAppController.sendCommand(AppOptionCode.STATUS_GET_STATUS, deviceId, "-1");
        }
        updateDateLayout();
        updateUI(allStatus);
    }

    private void updateDateLayout() {
        tvDateTime.setText(TimeUtil.getCurrentDate(TimeUtil.dateFormatHM));
        tvDay.setText(TimeUtil.getCurrentDate("MM月dd日") + " " + TimeUtil.getWeekOfDate(new Date()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkDeviceIsNull();
    }

    @OnClick(R.id.iv_back)
    public void onIvBackClicked() {
        AppUtil.goAndroidHome(context);
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
//        if (isLogined) {
//            PreferenceUtil.rmAccountByChangPW(getApplicationContext());
//            PreferenceUtil.saveAccount(getApplicationContext(),null);
//        } else {
        startActivity(new Intent(this, LoginActivity.class));
//        }
    }

    @OnClick(R.id.btn_power)
    public void onBtnPowerClicked() {
        if (TextUtils.isEmpty(deviceId)) {
            showToast("未连接设备");
            return;
        }

        if (allStatus == null) {
            showToast("设备无全状态");
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

        rbFunction.setChecked(!rbFunction.isChecked());

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

        rbWind.setChecked(!rbWind.isChecked());

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
        bundle.putString(Constant.KEY_DEVICE_ID, deviceId);
        bundle.putInt("flagPage", 1);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnClick(R.id.rb_timer)
    public void onRbTimerClicked() {
        rbTimer.setChecked(!rbTimer.isChecked());

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

        rbMode.setChecked(!rbMode.isChecked());

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
        bundle.putString(Constant.KEY_DEVICE_ID, deviceId);
        bundle.putInt("flagPage", 0);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void updateUIPush(HardwareCmd hardwareCmd) {
        super.updateUIPush(hardwareCmd);
        if (TextUtils.isEmpty(deviceId)) {
            return;
        }

        if (!deviceId.equals(hardwareCmd.deviceId)) {
            return;
        }

        allStatus = AllStatus.parseAllStatus(hardwareCmd.data);
        updateUI(allStatus);
    }

    private void updateUI(AllStatus allStatus) {
        if (allStatus == null) {
            llCurData.setVisibility(View.GONE);
            return;
        }
        isPowerOn = allStatus.isPowerOn();
        tvPower.setText(isPowerOn ? "点击关机" : "点击开机");
        btnPower.setImageResource(isPowerOn ? R.drawable.icon_switch_on : R.drawable.icon_switch_off);
        llCurData.setVisibility(isPowerOn ? View.VISIBLE : View.GONE);
        setPM25Layout();
        tvDateTime.setText(TimeUtil.getCurrentDate(TimeUtil.dateFormatHM));
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

        rbFunction.setChecked(isPowerOn);
        rbWind.setChecked(isPowerOn);
        rbTimer.setChecked(isPowerOn);
        rbMode.setChecked(isPowerOn);
    }

//    @Override
//    protected void onTickEventCallback() {
//        super.onTickEventCallback();
//        updateDateLayout();
//    }

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

    private void setPM25Layout() {
        tv_pm25.setVisibility(allStatus.isPowerOn() ? View.VISIBLE : View.INVISIBLE);
        int pm25 = allStatus.getPM25();
        if (pm25 > 0 && pm25 <= 35) {
            tv_pm25.setText("室内污染指数：" + pm25 + "  优");
        } else if (pm25 > 35 && pm25 <= 70) {
            tv_pm25.setText("室内污染指数：" + pm25 + "  良");
        } else if (pm25 > 70 && pm25 <= 105) {
            tv_pm25.setText("室内污染指数：" + pm25 + "  中");
        } else if (pm25 > 105 && pm25 <= 150) {
            tv_pm25.setText("室内污染指数：" + pm25 + "  差");
        } else if (pm25 >= 151) {
            tv_pm25.setText("室内污染指数：" + pm25 + "  污染严重");
        } else {
            tv_pm25.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.rb_bizhi)
    public void onRbBizhiClicked() {
        //在这里跳转到手机系统相册里面
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        deviceId = PreferenceUtil.getString(context, Constant.KEY_DEVICE_ID);
        //在相册里面选择好相片之后调回到现在的这个activity中
        switch (requestCode) {
            case 101://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                    try {
                        Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String path = cursor.getString(columnIndex);  //获取照片路径
                        cursor.close();
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
//                        iv_photo.setImageBitmap(bitmap);
                        btnPower.setImageBitmap(bitmap);
//                        Glide.with(context).load(selectedImage).into(container);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}