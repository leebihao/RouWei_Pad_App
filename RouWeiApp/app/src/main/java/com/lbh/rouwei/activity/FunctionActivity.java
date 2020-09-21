package com.lbh.rouwei.activity;

import android.os.Bundle;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;
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

    AllStatus allStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allStatus = (AllStatus) getIntent().getSerializableExtra("bean");

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_function;
    }

    @Override
    public void initView() {
        super.initView();
        updateFunUi();
    }

    private void updateFunUi() {
        rlNegativeicon.setBackgroundColor(allStatus.isNegativeIonSwitchOn() ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey));
        rlUv.setBackgroundColor(allStatus.isUVSwitchOn() ? getResources().getColor(R.color.blue) : getResources().getColor(R.color.grey));
        ivNegativeicon.setImageResource(allStatus.isNegativeIonSwitchOn() ? R.drawable.icon_negative_p : R.drawable.icon_negative_n);
        ivUv.setImageResource(allStatus.isUVSwitchOn() ? R.drawable.icon_uv_p : R.drawable.icon_uv_p);
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
        mAppController.sendCommand(AppOptionCode.STATUS_SWITCH_NEGAT, deviceId, allStatus.isNegativeIonSwitchOn() ? "0" : "1");
    }

    @OnClick(R.id.rl_uv)
    public void onRlUvClicked() {
        mAppController.sendCommand(AppOptionCode.STATUS_SWITCH_UV, deviceId, allStatus.isUVSwitchOn() ? "0" : "1");
    }

    @OnClick(R.id.btn_home)
    public void onBtnHomeClicked() {
        AppUtil.goAndroidHome(context);
    }

    @Override
    public void updateUIPush(HardwareCmd hardwareCmd) {
        super.updateUIPush(hardwareCmd);
        allStatus = AllStatus.parseAllStatus(hardwareCmd.data);
        updateFunUi();
    }
}
