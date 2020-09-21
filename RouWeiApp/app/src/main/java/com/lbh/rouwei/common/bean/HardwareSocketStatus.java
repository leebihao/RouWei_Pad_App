package com.lbh.rouwei.common.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by lijunjie on 16/1/10.
 */
public class HardwareSocketStatus implements Serializable {

    public static final int ID_CHAKONG_POWER                         = 0;
    public static final int ID_CHAKONG_POWER_CHANGE_REASON           = 1;
    public static final int ID_USB_POWER                             = 2;
    public static final int ID_USB_POWER_CHANGE_REASON               = 3;

    public String chakong_power;
    public String chakong_power_change_reason;
    public String usb_power;
    public String usb_power_change_reason;

    public void setValue(int id, String value) {
        switch (id) {
            case ID_CHAKONG_POWER:
                chakong_power = value;
                break;
            case ID_CHAKONG_POWER_CHANGE_REASON:
                chakong_power_change_reason = value;
                break;
            case ID_USB_POWER:
                usb_power = value;
                break;
            case ID_USB_POWER_CHANGE_REASON:
                usb_power_change_reason = value;
                break;
        }
    }

    public boolean isChakongOpen() {
        return TextUtils.equals(chakong_power, "1");
    }

    public boolean isUSBOpen() {
        return TextUtils.equals(usb_power, "1");
    }

    public static HardwareSocketStatus parseS00(String s00) {
        if (TextUtils.isEmpty(s00)) {
            return null;
        }
        HardwareSocketStatus status = new HardwareSocketStatus();
        try {
            String[] msgs = s00.split(",");
            for (int i = 0 ; i < msgs.length; i++) {
                status.setValue(i, msgs[i]);
            }
        } catch (Exception e) {
            status = null;
        }
        return status;
    }

    public static int parseS03(String s03) {
        try {
            return Integer.valueOf(s03);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
