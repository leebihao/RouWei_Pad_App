package com.lbh.rouwei.bese;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lbh.rouwei.activity.ADPlayerActivity;
import com.scinan.sdk.api.v2.agent.DataAgent;
import com.scinan.sdk.api.v2.agent.DeviceAgent;
import com.scinan.sdk.api.v2.agent.SensorAgent;
import com.scinan.sdk.api.v2.agent.UserAgent;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.lan.v1.LANRequestHelper;
import com.scinan.sdk.volley.FetchDataCallback;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/08/30
 *     desc   :
 * </pre>
 */
public abstract class BaseActivity extends AppCompatActivity implements FetchDataCallback {
    protected AppApplication app;
    protected Context context;
    protected RequestHelper mRequestHelper;
    protected UserAgent mUserAgent;
    protected LANRequestHelper mLANRequestHelper;
    protected String mClassName;
    protected DataAgent mDataAgent;

    protected DeviceAgent mDeviceAgent;

    protected boolean isRusume = false;
    protected SensorAgent sensorAgent;
    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getExtarDataFromPrePage(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;
        app = (AppApplication) context.getApplicationContext();
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
        stopAdTimer();
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
        app.isTouchedApp = true;
//        startAdTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRusume = false;
//        stopAdTimer();
    }

    public void startAdTimer() {
        Log.d("#lbh_timer","startAdTimer");
        Observable.timer(60, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }

                    @Override
                    public void onNext(@NonNull Long number) {
                        //跳转到广告页面
                        Log.d("#lbh_timer","startAdTimer 60s 后开启广告");
                        startActivity(new Intent(context, ADPlayerActivity.class));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        //取消订阅
                    }

                    @Override
                    public void onComplete() {
                        //取消订阅
                    }
                });
    }

    public void stopAdTimer() {
        Log.d("#lbh_timer","stopAdTimer");
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG_timer", "onTouchEvent  start: ");
                stopAdTimer();
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                startAdTimer();
                Log.d("TAG_timer", "onTouchEvent  stop: ");
                break;
        }
        return super.onTouchEvent(event);
    }

}
