package com.lbh.rouwei.zmodule.config.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;

import butterknife.BindView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AirkissConfigStep1Activity extends BaseMvpActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.addDevice)
    ImageView addDevice;
    @BindView(R.id.container)
    ConstraintLayout container;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        AirkissConfigStep1ActivityPermissionsDispatcher.onAddDeviceClickedWithPermissionCheck(this);
//    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_airkiss_config_step1;
    }

    @Override
    public void initView() {

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
        finish();
    }

    @OnClick(R.id.addDevice)
    public void onAddDeviceClicked() {
        go2ConfigerPage();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE})
    public void go2ConfigerPage() {
        startActivity(new Intent(AirkissConfigStep1Activity.this, AirkissConfigStep3Activity.class));
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE})
    void showDeniedForCamera() {
        showToast("请打开定位权限");
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE})
    void showNeverAskForCamera() {
        showToast("如果定位权限被禁止了，将会导致不能添加设备成功");
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        AirkissConfigStep1ActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
//    }
}