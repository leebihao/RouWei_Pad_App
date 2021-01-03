package com.lbh.rouwei.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 *     @author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/12
 *     desc   :
 * </pre>
 */
public class FunctionActivity extends BaseControlActivity {
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

    AllStatus allStatus;

    @Override
    protected void getExtarDataFromPrePage(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        allStatus = (AllStatus) extras.getSerializable("bean");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_function;
    }

    @Override
    public void initView() {
        super.initView();
        updateFunUi(allStatus);
    }

    private void updateFunUi(AllStatus allStatus) {
        if (allStatus != null) {
            rlNegativeicon.setBackgroundColor(allStatus.isNegativeIonSwitchOn() ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey));
            rlUv.setBackgroundColor(allStatus.isUVSwitchOn() ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey));
            ivNegativeicon.setImageResource(allStatus.isNegativeIonSwitchOn() ? R.drawable.icon_negative_p : R.drawable.icon_negative_n);
            ivUv.setImageResource(allStatus.isUVSwitchOn() ? R.drawable.icon_uv_p : R.drawable.icon_uv_p);
        }
    }


    @OnClick(R.id.iv_back)
    public void onIvBackClicked() {
        finish();
    }

    @OnClick(R.id.title_layout)
    public void onBackClicked() {
        finish();
    }

    @OnClick(R.id.rl_negativeicon)
    public void onRlNegativeiconClicked() {
        if (allStatus == null) {
            showToast("全状态为空");
            return;
        }
        if (app.isUartOk) {
            cmdControlManager.sendUartCmd(AppOptionCode.STATUS_SWITCH_NEGAT, allStatus.isNegativeIonSwitchOn() ? "0" : "1");
        } else {
            mAppController.sendCommand(AppOptionCode.STATUS_SWITCH_NEGAT, deviceId, allStatus.isNegativeIonSwitchOn() ? "0" : "1");
        }
    }

    @OnClick(R.id.rl_uv)
    public void onRlUvClicked() {
        if (allStatus == null) {
            showToast("全状态为空");
            return;
        }
        if (app.isUartOk) {
            cmdControlManager.sendUartCmd(AppOptionCode.STATUS_SWITCH_UV, allStatus.isUVSwitchOn() ? "0" : "1");
        } else {
            mAppController.sendCommand(AppOptionCode.STATUS_SWITCH_UV, deviceId, allStatus.isUVSwitchOn() ? "0" : "1");
        }
    }

    @OnClick(R.id.btn_home)
    public void onBtnHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

    @Override
    public void updateUIPush(HardwareCmd hardwareCmd) {
        super.updateUIPush(hardwareCmd);
        allStatus = AllStatus.parseAllStatus(hardwareCmd.data);
        updateFunUi(allStatus);
    }

    @Override
    protected void updateUiFromUart(AllStatus allStatus) {
        Log.d("TAG_uart", "updateUiFromUart---> " + allStatus.toString());
        this.allStatus = allStatus;
        updateFunUi(allStatus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!app.isUartOk && TextUtils.isEmpty(deviceId)) {
            showLoginDialog(this);
        }
    }
}
