package com.lbh.rouwei.bese;

import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;

import com.lbh.rouwei.R;
import com.lbh.rouwei.common.network.AppController;
import com.lbh.rouwei.mvp.base.BasePresenter;
import com.lbh.rouwei.mvp.base.BaseView;
import com.scinan.sdk.api.v2.agent.SensorAgent;
import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.hardware.HardwareCmd;
import com.scinan.sdk.lan.v1.LANRequestHelper;
import com.scinan.sdk.util.AndroidUtil;
import com.scinan.sdk.util.LogUtil;

import java.util.Locale;

import autodispose2.AutoDispose;
import autodispose2.AutoDisposeConverter;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public abstract class BaseMvpActivity<T extends BasePresenter> extends BaseActivity implements BaseView {

    protected T mPresenter;

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }

    /**
     * 绑定生命周期 防止MVP内存泄漏
     *
     * @param <T>
     * @return
     */
    @Override
    public <T> AutoDisposeConverter<T> bindAutoDispose() {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider
                .from(this, Lifecycle.Event.ON_DESTROY));
    }

    protected void showToast(String tip) {
//        ToastUtil.showMessage(this, tip);
        Toast.makeText(context, tip, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int strId) {
//        ToastUtil.showMessage(this, strId);
        Toast.makeText(context, getResources().getString(strId), Toast.LENGTH_SHORT).show();
    }

    // 获取当前语言
    public String getLocaleLanguage() {
        return Locale.getDefault().getLanguage();
    }

    // 请求权限
    public boolean requestPermission(@NonNull String permissions, final int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(this, permissions);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permissions}, requestCode);

                return true;
            }
        }

        return false;
    }
}
