package com.lbh.rouwei.zmodule.login.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lbh.rouwei.R;
import com.lbh.rouwei.bese.BaseMvpActivity;
import com.lbh.rouwei.zmodule.login.ui.fragment.FragmentRegistByEmail;
import com.lbh.rouwei.zmodule.login.ui.fragment.FragmentRegistByPhone;
import com.scinan.sdk.util.LogUtil;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/01
 *     desc   :
 * </pre>
 */
public class RegisterActivity extends BaseMvpActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.phoneRB)
    RadioButton phoneRB;
    @BindView(R.id.emailRB)
    RadioButton emailRB;
    @BindView(R.id.chooseRG)
    RadioGroup chooseRG;
    @BindView(R.id.content)
    FrameLayout content;

    private Fragment currentFragment;
    private FragmentRegistByEmail mFragmentByEmail;
    private FragmentRegistByPhone mFragmentByPhone;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        selectFragment(R.id.phoneRB);
        chooseRG.setOnCheckedChangeListener(this);
        if (!Locale.CHINESE.getLanguage().equals(getLocaleLanguage())) {
            ((RadioButton) chooseRG.getChildAt(0)).setVisibility(View.GONE);
            ((RadioButton) chooseRG.getChildAt(1)).setChecked(true);
        }
    }

    public void selectFragment(int checkedId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        switch (checkedId) {
            case R.id.phoneRB:
                if (mFragmentByPhone == null) {
                    mFragmentByPhone = FragmentRegistByPhone.newInstance("", "");
                }

                if (getSupportFragmentManager().findFragmentByTag("mFragmentByPhone") == null) {
                    transaction.add(R.id.content, mFragmentByPhone, "mFragmentByPhone");
                }

                currentFragment = mFragmentByPhone;

                break;
            case R.id.emailRB:
                if (mFragmentByEmail == null) {
                    mFragmentByEmail = FragmentRegistByEmail.newInstance("", "");
                }

                if (getSupportFragmentManager().findFragmentByTag("mFragmentByEmail") == null) {
                    transaction.add(R.id.content, mFragmentByEmail, "mFragmentByEmail");
                }

                currentFragment = mFragmentByEmail;
                break;
        }

        transaction.show(currentFragment);
        transaction.commitAllowingStateLoss();
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

    @Override
    protected void getExtarDataFromPrePage(Bundle savedInstanceState) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        selectFragment(checkedId);
    }

    public static final int CHOOSE_COUNTRY_AREA_CODE = 12;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case CHOOSE_COUNTRY_AREA_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String countryName = bundle.getString("countryName");
                    String countryNumber = bundle.getString("countryNumber");
//                    callBack.getData(countryNumber,countryName);
//                    areaCodeTV.setText(countryNumber);
//                    countryNameTV.setText(countryName);

                    LogUtil.d("Register-----" + countryNumber);

                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
