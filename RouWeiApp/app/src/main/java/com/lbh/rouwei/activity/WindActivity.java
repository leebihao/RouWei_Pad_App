package com.lbh.rouwei.activity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseControlActivity;
import com.lbh.rouwei.common.bean.AllStatus;
import com.lbh.rouwei.common.hardware.AppOptionCode;
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
public class WindActivity extends BaseControlActivity {
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
    AllStatus allStatus = new AllStatus();
    int windspeed = 0;
    TypedArray arrWIND;//获取物品图片的数组资源
    @BindView(R.id.btn_add_wind)
    Button btnAddWind;
    @BindView(R.id.tv_wind_level)
    TextView tvWindLevel;
    @BindView(R.id.reduce_add_wind)
    Button reduceAddWind;
    private int flagPage = 0;//0:模式 1: 风速
    private int mode = 0;

    @Override
    protected void getExtarDataFromPrePage(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        windspeed = bundle.getInt("windspeed", 0);
        allStatus = (AllStatus) bundle.getSerializable("bean");
        flagPage = bundle.getInt("flagPage", 1);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wind;
    }

    @Override
    public void initView() {
        super.initView();
//        arrWIND = context.getResources().obtainTypedArray(R.array.array_wind);
//        ivWindSrc.setImageResource(arrWIND.getResourceId(windspeed - 1, R.drawable.icon_wind_1));
        setWindBackImg();
        //只要模式页面才设置
        if (flagPage == 0) {
            setLayoutBg();
            tv_pm25.setVisibility(View.VISIBLE);
            mode = Integer.parseInt(allStatus.mode);
        } else {
            tv_pm25.setVisibility(View.GONE);
        }
    }

    private void setWindBackImg() {
        switch (windspeed) {
            case 1:
                ivWindSrc.setImageResource(R.drawable.icon_wind_1);
                break;
            case 2:
                ivWindSrc.setImageResource(R.drawable.icon_wind_2);
                break;
            case 3:
                ivWindSrc.setImageResource(R.drawable.icon_wind_3);
                break;
            case 4:
                ivWindSrc.setImageResource(R.drawable.icon_wind_4);
                break;
            case 5:
                ivWindSrc.setImageResource(R.drawable.icon_wind_5);
                break;
            default:
                ivWindSrc.setImageResource(R.drawable.icon_wind_1);
                break;

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
        } else if (pm25 > 105 && pm25 <= 150) {
            cl_bg.setBackgroundResource(R.color.wind_bg_red);
            tv_pm25.setText("室内污染指数：" + pm25 + "  差");
        } else if (pm25 >= 151) {
            cl_bg.setBackgroundResource(R.color.wind_bg_red_black);
            tv_pm25.setText("室内污染指数：" + pm25 + "  污染严重");
        } else {
            cl_bg.setBackgroundResource(R.drawable.bg_app);
            tv_pm25.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.iv_back)
    public void onIvBackClicked() {
        finish();
    }

    @OnClick(R.id.btn_home)
    public void onBtnHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

    @OnClick(R.id.btn_add_wind)
    public void onBtnAddWindClicked() {
        if (mode == 2) {
            showToast("自动模式下不能调节风速");
            return;
        }
        if (windspeed >= 5) {
            windspeed = 5;
            showToast("最大档位为5档");
            return;
        }

        windspeed++;
        mAppController.sendCommand(AppOptionCode.STATUS_WIND_LEVEL, deviceId, String.valueOf(windspeed));

    }

    @OnClick(R.id.reduce_add_wind)
    public void onBtnReduceWindClicked() {
        if (mode == 2) {
            showToast("自动模式下不能调节风速");
            return;
        }

        if (windspeed <= 1) {
            windspeed = 1;
            showToast("最低档位为1档");
            return;
        }

        windspeed--;
        mAppController.sendCommand(AppOptionCode.STATUS_WIND_LEVEL, deviceId, String.valueOf(windspeed));
    }

    @Override
    public void updateUIPush(HardwareCmd hardwareCmd) {
        super.updateUIPush(hardwareCmd);
        allStatus = AllStatus.parseAllStatus(hardwareCmd.data);
        windspeed = Integer.parseInt(allStatus.windSpeed);
        setWindBackImg();
        if (flagPage == 0) {
            setLayoutBg();
            tv_pm25.setVisibility(View.VISIBLE);
        } else {
            tv_pm25.setVisibility(View.GONE);
        }
    }
}
