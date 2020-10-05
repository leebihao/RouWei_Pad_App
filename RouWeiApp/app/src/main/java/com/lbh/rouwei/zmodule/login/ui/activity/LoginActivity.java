package com.lbh.rouwei.zmodule.login.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.lbh.rouwei.R;
import com.lbh.rouwei.activity.DeviceListActivity;
import com.lbh.rouwei.bese.BaseMvpActivity;
import com.lbh.rouwei.common.constant.Constant;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.bean.Account;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.interfaces.LoginCallback;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.PreferenceUtil;
import com.tencent.mmkv.MMKV;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;


public class LoginActivity extends BaseMvpActivity implements LoginCallback {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.username)
    EditText usernameEdit;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.password)
    EditText pwEdit;
    @BindView(R.id.iv_eye)
    CheckBox ivEye;
    @BindView(R.id.login)
    Button btnLogin;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.tv_registered)
    TextView tvRegistered;
    @BindView(R.id.tv_forget_pwd)
    TextView tvForgetPwd;
    @BindView(R.id.container)
    ConstraintLayout container;

    RequestHelper mRequestHelper;

    private final static int LOGIN_BY_PHONE = 1;
    private final static int LOGIN_BY_OTHER = 2;
    int mCurrentLoginMode = LOGIN_BY_PHONE;

    private String usernameStr, passwordStr;


    @Override
    protected void getExtarDataFromPrePage(Bundle savedInstanceState) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        mRequestHelper = RequestHelper.getInstance(this);
        pwEdit.setTypeface(Typeface.DEFAULT);
        pwEdit.setTransformationMethod(new PasswordTransformationMethod());
        pwEdit.setOnKeyListener(onKeyListener);
    }

    @Override
    public void initData() {

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

    @OnClick(R.id.iv_clear)
    public void onIvClearClicked() {
        usernameEdit.setText("");
    }

    @OnClick(R.id.login)
    public void onLoginClicked() {
//        emailAndUserNameLogin();
        phoneLogin();
    }

    @OnClick(R.id.tv_registered)
    public void onTvRegisteredClicked() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.tv_forget_pwd)
    public void onTvForgetPwdClicked() {
        startActivity(new Intent(LoginActivity.this, ForgetChooseActivity.class));
    }

    @OnCheckedChanged(R.id.iv_eye)
    public void onChecKedBtn(boolean isChecked) {
        if (isChecked) {
            pwEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            pwEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }

    private View.OnKeyListener onKeyListener = (v, keyCode, event) -> {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            /*隐藏软键盘*/
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }

            login();

            return true;
        }
        return false;
    };

    private void login() {
        switch (mCurrentLoginMode) {
            case LOGIN_BY_PHONE:
                phoneLogin();
                break;
            case LOGIN_BY_OTHER:
                emailAndUserNameLogin();
                break;
        }
    }

    private boolean emailAndUserNameLogin() {
        //判断用户名密码是否为空

        usernameStr = usernameEdit.getText().toString();
        passwordStr = pwEdit.getText().toString();


        if (TextUtils.isEmpty(usernameStr)) {
            showToast(getString(R.string.username_null));
            return false;
        }


        if (TextUtils.isEmpty(passwordStr)) {
            showToast(getString(R.string.password_null));
            return false;

        }
//        showWaitDialog(getString(R.string.app_loading));
        mUserAgent.login(usernameStr, passwordStr, this);
        return true;
    }

    /**
     * 手机登录
     *
     * @return
     */
    private boolean phoneLogin() {
        //判断用户名密码是否为空

        usernameStr = usernameEdit.getText().toString();
        passwordStr = pwEdit.getText().toString();


        if (usernameStr == null || usernameStr.equals("")) {
            showToast(R.string.username_null);
            return false;
        }

        if (passwordStr == null || passwordStr.equals("")) {
            showToast(R.string.password_null);
            return false;
        }

        showLoading();
        mUserAgent.login(usernameStr, "86", passwordStr, this);
        return true;

    }


    @Override
    public void onSuccess(String openId, String digst, String scinanToken) {
//        dismissWaitDialog();
        PreferenceUtil.saveAccount(getApplicationContext(), new Account(usernameStr, passwordStr, scinanToken, openId, digst, "true"));
        Configuration.setToken(scinanToken);
        //判断是否存在设备，没有的话就进去添加设备页面
        String deviceid = MMKV.defaultMMKV().decodeString(Constant.KEY_DEVICE_ID);
        startActivity(new Intent(this, DeviceListActivity.class));
//        if (TextUtils.isEmpty(deviceid)) {
//            startActivity(new Intent(this, AirkissConfigStep1Activity.class));
//        } else {
//            startActivity(new Intent(this, MainActivity.class));
//        }
        finish();
    }

    @Override
    public void onFail(String reason) {
        LogUtil.d("PPPP==" + reason);
        usernameStr = null;
        passwordStr = null;
//        dismissWaitDialog();
        if (JsonUtil.getResultCode(reason) == 20014) {
            showToast(getString(R.string.user_does_not_exist));
        } else if (JsonUtil.getResultCode(reason) == 30111) {
            showToast(getString(R.string.email_has_not_active));
        } else if (JsonUtil.getResultCode(reason) == -1) {
            showToast(getString(R.string.network_error));
        } else {
            showToast(getString(R.string.username_or_password_error));
        }
    }
}