package com.lbh.rouwei.zmodule.login.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpFragment;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.PreferenceUtil;
import com.scinan.sdk.util.TextUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/01
 *     desc   :
 * </pre>
 */
public class FragmentRegistByEmail extends BaseMvpFragment implements FetchDataCallback, View.OnFocusChangeListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    @BindView(R.id.emailEdittext)
    EditText emailEdittext;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.passwdEdittext)
    EditText passwdEdittext;
    @BindView(R.id.iv_see_password)
    CheckBox ivSeePassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.container)
    ConstraintLayout container;

    private String email, password;


    public static FragmentRegistByEmail newInstance(String param1, String param2) {
        FragmentRegistByEmail fragment = new FragmentRegistByEmail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected void initView() {
        mUserAgent.registerAPIListener(this);
        emailEdittext.setOnFocusChangeListener(this);
        passwdEdittext.setOnFocusChangeListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register_email;
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

    @OnClick(R.id.btn_register)
    public void register(View view) {
        email = emailEdittext.getText().toString();
        password = passwdEdittext.getText().toString();
        if (TextUtils.isEmpty(email)) {
            showToast(R.string.email_not_null);
            return;
        }

        if (!TextUtil.isEmail(email)) {
            showToast(R.string.email_format_does_not);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast(R.string.password_null);
            return;
        }
        if (password.length() < 6) {
            showToast(R.string.password_too_short);
            return;
        }
        if (password.length() > 16) {
            showToast(R.string.password_too_long);
            return;
        }

        mUserAgent.register(emailEdittext.getText().toString().trim(), passwdEdittext.getText().toString(), getString(R.string.app_name));
    }

    @OnClick(R.id.iv_clear)
    public void setIvClear() {
        emailEdittext.setText("");
    }

    @OnCheckedChanged(R.id.iv_see_password)
    public void onCheckbox(boolean isChecked) {
        if (isChecked) {
            passwdEdittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            passwdEdittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
        switch (api) {
            case RequestHelper.API_USER_REGISTER_EMAIL:

                showToast(R.string.register_success);
                PreferenceUtil.saveString(getActivity(), PreferenceUtil.KEY_ACCOUNT_USER_NAME, email);
                PreferenceUtil.saveString(getActivity(), PreferenceUtil.KEY_ACCOUNT_USER_PASSWORD, password);
                getActivity().setResult(getActivity().RESULT_OK, new Intent().putExtra(PreferenceUtil.KEY_ACCOUNT_USER_NAME, email)
                        .putExtra(PreferenceUtil.KEY_ACCOUNT_USER_PASSWORD, password));
//                dismissWaitDialog();
                getActivity().finish();
                break;
        }
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        switch (api) {
            case RequestHelper.API_USER_REGISTER_EMAIL:
//                dismissWaitDialog();
                showToast(JsonUtil.parseErrorMsg(responseBody));
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserAgent.unRegisterAPIListener(this);
    }
}
