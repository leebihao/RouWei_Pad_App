package com.lbh.rouwei.activity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseControlActivity;
import com.lbh.rouwei.common.bean.AllStatus;
import com.lbh.rouwei.common.constant.Constant;
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
    int windspeed = 0;
    //获取物品图片的数组资源
    TypedArray arrWIND;
    @BindView(R.id.iv_go_add)
    ImageView ivGoAdd;
    @BindView(R.id.btn_5)
    TextView btn5;
    @BindView(R.id.btn_1)
    TextView btn1;
    @BindView(R.id.btn_2)
    TextView btn2;
    @BindView(R.id.btn_4)
    TextView btn4;
    @BindView(R.id.btn_3)
    TextView btn3;
    //0:模式 1: 风速
    private int flagPage = 0;
    private int mode = 0;

    @Override
    protected void getExtarDataFromPrePage(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        windspeed = bundle.getInt("windspeed", 0);
        String id = getIntent().getStringExtra(Constant.KEY_DEVICE_ID);
        if (!TextUtils.isEmpty(id)) {
            deviceId = id;
        }
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
        //只要模式页面才设置
        if (flagPage == 0) {
            setLayoutBg();
            tv_pm25.setVisibility(View.VISIBLE);
            try {
                if (allStatus != null) {
                    mode = Integer.parseInt(allStatus.mode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            tv_pm25.setVisibility(View.GONE);
            setWindBackImg(windspeed);
        }

        ivBack.setOnClickListener(v -> finish());
    }

    private void setWindBackImg(int windspeed) {
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
        if (allStatus == null) {
            return;
        }
        int pm25 = allStatus.getPM25();
        if (pm25 >= 0 && pm25 <= 35) {
            cl_bg.setBackgroundResource(R.color.wind_bg_green);
            setWindBackImg(1);
            tv_pm25.setText("室内污染指数：" + pm25 + "  优");
        } else if (pm25 > 35 && pm25 <= 70) {
            cl_bg.setBackgroundResource(R.color.wind_bg_blue);
            setWindBackImg(2);
            tv_pm25.setText("室内污染指数：" + pm25 + "  良");
        } else if (pm25 > 70 && pm25 <= 105) {
            cl_bg.setBackgroundResource(R.color.wind_bg_orange);
            setWindBackImg(3);
            tv_pm25.setText("室内污染指数：" + pm25 + "  中");
        } else if (pm25 > 105 && pm25 <= 150) {
            cl_bg.setBackgroundResource(R.color.wind_bg_red);
            tv_pm25.setText("室内污染指数：" + pm25 + "  差");
            setWindBackImg(4);
        } else if (pm25 >= 151) {
            cl_bg.setBackgroundResource(R.color.wind_bg_red_black);
            tv_pm25.setText("室内污染指数：" + pm25 + "  污染严重");
            setWindBackImg(5);
        }

    }

    @OnClick(R.id.btn_home)
    public void onBtnHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

    @OnClick(R.id.title_layout)
    public void onBackClicked() {
        finish();
    }

    @Override
    public void updateUIPush(HardwareCmd hardwareCmd) {
        super.updateUIPush(hardwareCmd);
        allStatus = AllStatus.parseAllStatus(hardwareCmd.data);
        windspeed = Integer.parseInt(allStatus.windSpeed);
        mode = allStatus.getMode();

        if (flagPage == 0) {
            setLayoutBg();
            tv_pm25.setVisibility(View.VISIBLE);
        } else {
            tv_pm25.setVisibility(View.GONE);
            setWindBackImg(windspeed);
        }
    }

    @Override
    protected void updateUiFromUart(AllStatus allStatus) {
        this.allStatus = allStatus;
        windspeed = Integer.parseInt(allStatus.windSpeed);
        mode = allStatus.getMode();


        if (flagPage == 0) {
            setLayoutBg();
            tv_pm25.setVisibility(View.VISIBLE);
        } else {
            setWindBackImg(windspeed);
            tv_pm25.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btn_5, R.id.btn_1, R.id.btn_2, R.id.btn_4, R.id.btn_3})
    public void onViewClicked(View view) {
        if (mode != 1) {
            return;
        }

        switch (view.getId()) {
            case R.id.btn_5:
                if (app.isUartOk) {
                    cmdControlManager.sendUartCmd(AppOptionCode.STATUS_WIND_LEVEL, "5");
                } else {
                    mAppController.sendCommand(AppOptionCode.STATUS_WIND_LEVEL, deviceId, "5");
                }
                break;
            case R.id.btn_1:
                if (app.isUartOk) {
                    cmdControlManager.sendUartCmd(AppOptionCode.STATUS_WIND_LEVEL, "1");
                } else {
                    mAppController.sendCommand(AppOptionCode.STATUS_WIND_LEVEL, deviceId, "1");
                }
                break;
            case R.id.btn_2:
                if (app.isUartOk) {
                    cmdControlManager.sendUartCmd(AppOptionCode.STATUS_WIND_LEVEL, "2");
                } else {
                    mAppController.sendCommand(AppOptionCode.STATUS_WIND_LEVEL, deviceId, "2");
                }
                break;
            case R.id.btn_4:
                if (app.isUartOk) {
                    cmdControlManager.sendUartCmd(AppOptionCode.STATUS_WIND_LEVEL, "4");
                } else {
                    mAppController.sendCommand(AppOptionCode.STATUS_WIND_LEVEL, deviceId, "4");
                }
                break;
            case R.id.btn_3:
                if (app.isUartOk) {
                    cmdControlManager.sendUartCmd(AppOptionCode.STATUS_WIND_LEVEL, "3");
                } else {
                    mAppController.sendCommand(AppOptionCode.STATUS_WIND_LEVEL, deviceId, "3");
                }
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.iv_back)
    public void iv_back() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!app.isUartOk && TextUtils.isEmpty(deviceId)) {
            showLoginDialog(this);
        }
    }
}
