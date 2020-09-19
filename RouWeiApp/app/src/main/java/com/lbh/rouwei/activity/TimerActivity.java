package com.lbh.rouwei.activity;

import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;

public class TimerActivity extends BaseMvpActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.pickerHour)
    NumberPickerView pickerHour;
    @BindView(R.id.pickerMin)
    NumberPickerView pickerMin;
    @BindView(R.id.tv_text_time)
    TextView tvTextTime;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_stop)
    Button btnStop;
    @BindView(R.id.rl_btn)
    RelativeLayout rlBtn;
    @BindView(R.id.btn_home)
    CheckedTextView btnHome;

    @Override
    public int getLayoutId() {
        return R.layout.activity_timer;
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

    @OnClick(R.id.btn_start)
    public void onBtnStartClicked() {
    }

    @OnClick(R.id.btn_stop)
    public void onBtnStopClicked() {
    }

    @OnClick(R.id.btn_home)
    public void onBtnHomeClicked() {
    }
}