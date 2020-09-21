package com.lbh.rouwei.common.bean;

import android.content.Context;
import android.text.TextUtils;

import com.scinan.sdk.api.v2.bean.Device;
import com.scinan.sdk.util.LogUtil;

/**
 * Created by lijunjie on 16/1/14.
 */
public class SocketDevice extends Device {

    public HardwareSocketStatus mHardwareSocketStatus;

    public boolean isOnline() {
        if (TextUtils.isEmpty(getOnline())) {
            return false;
        }

        return Integer.valueOf(getOnline()) == 1;
    }

    public String getTitle(Context context) {
        if (!TextUtils.isEmpty(getTitle()))
            return getTitle();

        try {
            return getId().substring(8);
        } catch (Exception e) {
        }
        return getId();
    }

    public void setS00(String s00) {
        try {
            String[] msgs = s00.split(",");
            if (msgs[0].length() > 10) {
                LogUtil.d("found http s00 data");
                s00 = s00.substring(s00.indexOf(",") + 1);
            }
            super.setS00(s00);
            mHardwareSocketStatus = HardwareSocketStatus.parseS00(getS00());
        } catch (Exception e) {
        }
    }
}
