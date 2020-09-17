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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpFragment;
import com.lbh.rouwei.common.utils.CountDownButtonHelper;
import com.scinan.sdk.api.v2.agent.UserAgent;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.PreferenceUtil;
import com.scinan.sdk.util.TextUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import org.json.JSONObject;

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
public class FragmentRegistByPhone extends BaseMvpFragment implements FetchDataCallback, View.OnFocusChangeListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    @BindView(R.id.mobileNumberEdit)
    EditText mobileNumberEdit;
    @BindView(R.id.validateMessageEdit)
    EditText validateMessageEdit;
    @BindView(R.id.btn_validate)
    Button btnValidate;
    @BindView(R.id.ll_validate)
    LinearLayout llValidate;
    @BindView(R.id.registerPWEdit)
    EditText registerPWEdit;
    @BindView(R.id.iv_see_password)
    CheckBox ivSeePassword;
    @BindView(R.id.ll_pwd)
    LinearLayout llPwd;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.container)
    ConstraintLayout container;

    private String validTicket;//短信验证返回的令牌

    public static FragmentRegistByPhone newInstance(String param1, String param2) {
        FragmentRegistByPhone fragment = new FragmentRegistByPhone();
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
        mobileNumberEdit.setOnFocusChangeListener(this);
        validateMessageEdit.setOnFocusChangeListener(this);
        registerPWEdit.setOnFocusChangeListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register_phone;
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

    @OnClick(R.id.btn_validate)
    public void onBtnValidateClicked() {
        getPhoneSMS();
    }

    /**
     * 获取手机短信验证码
     */
    private void getPhoneSMS() {
        String phoneNumber = mobileNumberEdit.getText().toString();
        if (phoneNumber == null || "".equals(phoneNumber)) {
            showToast(R.string.mobile_number_not_null);
            return;
        }

        if (!TextUtil.isPhoneNumberValid("86", phoneNumber)) {
            showToast(R.string.mobile_number_not_correct);
            return;
        }
        getSMSForWait();
        mUserAgent.sendMobileVerifyCode(phoneNumber, "86", UserAgent.TYPE_SEND_MSG_FOR_REGISTER_USER);
    }

    private void getSMSForWait() {

        CountDownButtonHelper helper = new CountDownButtonHelper(btnValidate,
                getString(R.string.get_validate), 60, 1);
        helper.setOnFinishListener(new CountDownButtonHelper.OnFinishListener() {

            @Override
            public void finish() {
                //showToast("计算结束");
            }
        });

        helper.start();
        LogUtil.d("btn_validate-----2222");

    }

    @OnCheckedChanged(R.id.iv_see_password)
    public void onIvSeePasswordClicked(boolean isChecked) {
        if (isChecked) {
//            view.setSelected(false);
            registerPWEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
//            view.setSelected(true);
            registerPWEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }

    @OnClick(R.id.btn_register)
    public void onBtnRegisterClicked() {
        registerByphone();
    }

    private void registerByphone() {
        String password = registerPWEdit.getText().toString();
        String message = validateMessageEdit.getText().toString();
        String phone = mobileNumberEdit.getText().toString();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(message)) {
            showToast(R.string.enter_mobile_number_first);
            return;
        }

        if (validTicket == null) {
            showToast(R.string.validate_code_is_error);
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
        mUserAgent.registerMobile(password, validTicket, message);
//        showWaitDialog(getString(R.string.app_loading));
    }

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
        switch (api) {
            case RequestHelper.API_USER_REGISTER_MOBILE:

//                dismissWaitDialog();
                showToast(R.string.register_success);
                PreferenceUtil.saveString(getActivity(), PreferenceUtil.KEY_ACCOUNT_USER_NAME, mobileNumberEdit.getText().toString());
                PreferenceUtil.saveString(getActivity(), PreferenceUtil.KEY_ACCOUNT_USER_PASSWORD, registerPWEdit.getText().toString());
                getActivity().setResult(getActivity().RESULT_OK, new Intent().putExtra(PreferenceUtil.KEY_ACCOUNT_USER_NAME, mobileNumberEdit.getText().toString())
                        .putExtra(PreferenceUtil.KEY_ACCOUNT_USER_PASSWORD, registerPWEdit.getText().toString()));
                showToast(R.string.register_has_been_successed);
                getActivity().finish();
                break;
            case RequestHelper.API_SEND_MOBILE_VERIFYCODE:

                try {
                    validTicket = new JSONObject(responseBody).getString("ticket");
                    LogUtil.d("validTicket---------" + validTicket);
                    if (validTicket != null) {
                        showToast(R.string.validate_has_been_sent);
                    } else {
                        showToast(getString(R.string.get_validate_code_is_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(JsonUtil.parseErrorMsg(responseBody));
                }


                break;
        }
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        showToast(JsonUtil.parseErrorMsg(responseBody));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUserAgent.unRegisterAPIListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
}
