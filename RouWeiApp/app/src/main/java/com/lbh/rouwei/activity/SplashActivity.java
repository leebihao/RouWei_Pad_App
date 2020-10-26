package com.lbh.rouwei.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.lbh.rouwei.R;
import com.lbh.rouwei.common.constant.Constant;
import com.lbh.rouwei.zmodule.config.ui.activity.AirkissConfigStep1Activity;
import com.scinan.sdk.util.PreferenceUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        if (TextUtils.isEmpty(MMKV.defaultMMKV().decodeString(Constant.KEY_DEVICE_ID, ""))) {
        if (TextUtils.isEmpty(PreferenceUtil.getString(SplashActivity.this, Constant.KEY_DEVICE_ID))) {
            startActivity(new Intent(SplashActivity.this, AirkissConfigStep1Activity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}