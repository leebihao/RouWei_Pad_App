package com.lbh.rouwei.bese;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lbh.rouwei.common.network.AppController;
import com.scinan.sdk.alive.FiveSecondTimer;
import com.scinan.sdk.api.v2.agent.DataAgent;
import com.scinan.sdk.api.v2.agent.DeviceAgent;
import com.scinan.sdk.api.v2.agent.SensorAgent;
import com.scinan.sdk.api.v2.agent.UserAgent;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.lan.v1.LANRequestHelper;
import com.scinan.sdk.service.IPushService;
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
public abstract class BaseActivity extends AppCompatActivity implements FetchDataCallback {

    protected Context context;
    protected RequestHelper mRequestHelper;
    protected UserAgent mUserAgent;
    protected LANRequestHelper mLANRequestHelper;
    protected String mClassName;
    protected DataAgent mDataAgent;

    protected DeviceAgent mDeviceAgent;

    protected boolean isRusume = false;
    protected SensorAgent sensorAgent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtarDataFromPrePage(savedInstanceState);
        context = this;
        mClassName = getClass().getName();
        setContentView(this.getLayoutId());
        ButterKnife.bind(this);
        mUserAgent = new UserAgent(this);
        mRequestHelper = RequestHelper.getInstance(this);
        mDeviceAgent = new DeviceAgent(this);
        mDeviceAgent.registerAPIListener(this);

        // init LAN
        mLANRequestHelper = LANRequestHelper.getInstance(this);

        this.initData();
        this.initView();
    }

    protected abstract void getExtarDataFromPrePage(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.bind(this).unbind();
//        mDataAgent.unRegisterAPIListener(this);
//        mDeviceAgent.unRegisterAPIListener(this);

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

    public abstract void initData();

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {

    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {

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


}
