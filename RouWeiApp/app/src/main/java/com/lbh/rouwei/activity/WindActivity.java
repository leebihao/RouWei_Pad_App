package com.lbh.rouwei.activity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;
import com.lbh.rouwei.common.bean.AllStatus;
import com.lbh.rouwei.common.utils.AppUtil;
import com.scinan.sdk.hardware.HardwareCmd;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/12
 *     desc   :
 * </pre>
 */
public class WindActivity extends BaseMvpActivity {
    @BindView(R.id.cl_bg)
    ConstraintLayout cl_bg;
    @BindView(R.id.tv_pm25)
    TextView tv_pm25;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_wind_src)
    ImageView ivWindSrc;
    @BindView(R.id.btn_home)
    CheckedTextView btnHome;
    AllStatus allStatus;
    int windspeed = 0;
    TypedArray arrWIND;//获取物品图片的数组资源
    private int flagPage = 0;//0:模式 1: 风速

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        windspeed = getIntent().getIntExtra("windspeed", 0);
        allStatus = (AllStatus) getIntent().getSerializableExtra("bean");
        flagPage = getIntent().getIntExtra("flagPage", 1);
        arrWIND = getResources().obtainTypedArray(R.array.array_wind);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wind;
    }

    @Override
    public void initView() {
        ivWindSrc.setImageResource(arrWIND.getResourceId(windspeed - 1, R.drawable.icon_wind_1));
        //只要模式页面才设置
        if (flagPage == 0) {
            setLayoutBg();
            tv_pm25.setVisibility(View.VISIBLE);
        } else {
            tv_pm25.setVisibility(View.GONE);
        }
    }

    /**
     * //        根据室内空气污染指数变换背景颜色：
     * //        1）优：当空气污染指数(传感器探测) 小于35时，背景绿色#5FD5A0
     * //        2）良：36-70时，背景蓝色#00C5C6
     * //        3）中：71-105时背景橙色#FF9956
     * //        4）差：106-140时，背景红色#FF175B
     * //        5）污染严重：141以上时，背景黑红色#B1538A
     */
    private void setLayoutBg() {
        int pm25 = allStatus.getPM25();
        if (pm25 > 0 && pm25 <= 35) {
            cl_bg.setBackgroundResource(R.color.wind_bg_green);
            tv_pm25.setText("室内污染指数：" + pm25 + "  优");
        } else if (pm25 > 35 && pm25 <= 70) {
            cl_bg.setBackgroundResource(R.color.wind_bg_blue);
            tv_pm25.setText("室内污染指数：" + pm25 + "  良");
        } else if (pm25 > 70 && pm25 <= 105) {
            cl_bg.setBackgroundResource(R.color.wind_bg_orange);
            tv_pm25.setText("室内污染指数：" + pm25 + "  中");
        } else if (pm25 > 105 && pm25 <= 140) {
            cl_bg.setBackgroundResource(R.color.wind_bg_red);
            tv_pm25.setText("室内污染指数：" + pm25 + "  差");
        } else if (pm25 >= 141) {
            cl_bg.setBackgroundResource(R.color.wind_bg_red_black);
            tv_pm25.setText("室内污染指数：" + pm25 + "  污染严重");
        } else {
            cl_bg.setBackgroundResource(R.drawable.bg_app);
            tv_pm25.setVisibility(View.GONE);
        }

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

    @OnClick(R.id.btn_home)
    public void onBtnHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

    @Override
    public void updateUIPush(HardwareCmd hardwareCmd) {
        super.updateUIPush(hardwareCmd);
        allStatus = AllStatus.parseAllStatus(hardwareCmd.data);
        windspeed = Integer.parseInt(allStatus.windSpeed);
        ivWindSrc.setImageResource(arrWIND.getResourceId(windspeed - 1, R.drawable.icon_wind_1));
        if (flagPage == 0) {
            setLayoutBg();
            tv_pm25.setVisibility(View.VISIBLE);
        } else {
            tv_pm25.setVisibility(View.GONE);
        }
    }
}