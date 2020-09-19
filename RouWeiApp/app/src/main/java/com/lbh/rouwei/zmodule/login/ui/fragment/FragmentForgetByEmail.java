package com.lbh.rouwei.zmodule.login.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpFragment;
import com.scinan.sdk.api.v2.network.RequestHelper;
import com.scinan.sdk.util.JsonUtil;
import com.scinan.sdk.util.TextUtil;
import com.scinan.sdk.volley.FetchDataCallback;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/01
 *     desc   :
 * </pre>
 */
public class FragmentForgetByEmail extends BaseMvpFragment implements FetchDataCallback, View.OnFocusChangeListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.emailEdittext)
    EditText emailEdittext;
    @BindView(R.id.tv_text)
    TextView tvText;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    private String mParam1;
    private String mParam2;


    private String email, password;


    public static FragmentForgetByEmail newInstance(String param1, String param2) {
        FragmentForgetByEmail fragment = new FragmentForgetByEmail();
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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forget_email;
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

    @OnClick(R.id.btn_submit)
    public void register(View view) {
        email = emailEdittext.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showToast(R.string.email_not_null);
            return;
        }

        if (!TextUtil.isEmail(email)) {
            showToast(R.string.email_format_does_not);
            return;
        }
        mUserAgent.resetPwdByEmail(email);
    }

    @Override
    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
        switch (api) {
            case RequestHelper.API_USER_RESET_PWD_EMAIL:
//                dismissWaitDialog();
                showToast(R.string.email_has_been_sent);
                getActivity().finish();
                break;
        }
    }

    @Override
    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
        switch (api) {
            case RequestHelper.API_USER_RESET_PWD_EMAIL:
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
