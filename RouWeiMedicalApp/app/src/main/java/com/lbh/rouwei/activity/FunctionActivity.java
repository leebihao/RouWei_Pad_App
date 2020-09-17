package com.lbh.rouwei.activity;

import android.os.Bundle;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;
import com.lbh.rouwei.common.utils.AppUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/12
 *     desc   :
 * </pre>
 */
public class FunctionActivity extends BaseMvpActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_negativeicon)
    ImageView ivNegativeicon;
    @BindView(R.id.rl_negativeicon)
    RelativeLayout rlNegativeicon;
    @BindView(R.id.iv_uv)
    ImageView ivUv;
    @BindView(R.id.rl_uv)
    RelativeLayout rlUv;
    @BindView(R.id.btn_home)
    CheckedTextView btnHome;

    @Override
    public int getLayoutId() {
        return R.layout.activity_function;
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

    @OnClick(R.id.rl_negativeicon)
    public void onRlNegativeiconClicked() {
    }

    @OnClick(R.id.rl_uv)
    public void onRlUvClicked() {
    }

    @OnClick(R.id.btn_home)
    public void onBtnHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

}
