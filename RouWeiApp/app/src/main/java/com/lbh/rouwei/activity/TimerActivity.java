package com.lbh.rouwei.activity;

import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;
import com.lbh.rouwei.common.hardware.AppOptionCode;
import com.lbh.rouwei.common.utils.AppUtil;
import com.lbh.rouwei.common.utils.RxJavaUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    private int mSecond = 59;

    @Override
    public int getLayoutId() {
        return R.layout.activity_timer;
    }

    @Override
    public void initView() {
        super.initView();
        initTime();
    }

    private void initTime() {
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
//        h = h % 12;

        setData(pickerHour, 0, 99, h);
        setData(pickerMin, 0, 59, m);
    }

    private void setData(NumberPickerView picker, int minValue, int maxValue, int value) {
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(value);
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
        String h = String.format("02d%", pickerHour.getContentByCurrValue());
        String m = String.format("02d%", pickerMin.getContentByCurrValue());
        mAppController.sendCommand(AppOptionCode.STATUS_TIMER_START, deviceId, h + m);

        RxJavaUtils.interval(1000, number -> {
            countTimer();
        });
    }

    @OnClick(R.id.btn_stop)
    public void onBtnStopClicked() {
        ///S04/1/0000
        mAppController.sendCommand(AppOptionCode.STATUS_TIMER_CANCLE, deviceId, "0000");
    }

    @OnClick(R.id.btn_home)
    public void onBtnHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

    private void countTimer() {
        int h = 0;
        int m = 0;
        mSecond--;
        if (mSecond < 0) {
            m--;
            if (m < 0) {
                mSecond = 59;
                h--;
                if (h < 0) {
                    //代表倒计时结束了
                    h = 0;
                    m = 0;
                    mSecond = 0;
                }
            }
        }

        tvTime.setText(String.format("02d%", h) + ":" + String.format("02d%", m) + ":" + String.format("02d%", mSecond));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxJavaUtils.cancel();
    }
}