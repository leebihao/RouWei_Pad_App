package com.lbh.rouwei.common.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/08
 *     desc   : /S00/1/1,2,2,25,055,1,0005,0,070,0
 *     /S00/1/开关状态，风速，模式，温度，室内PM2.5值，负离子开关，定时时间，杀菌状态，湿度，蜂鸣状态
 * </pre>
 */
public class AllStatus implements Serializable {

    public String switchStatus = "0";
    public String windSpeed = "1";

    public int getMode() {
        return Integer.parseInt(mode);
    }

    public String mode = "0";
    public String temperature = "26";
    public String pm25 = "000001";
    public String negativeIonSwitch = "0";
    public String timer = "0000";
    public String uv = "1";
    public String humidity = "000";
    public String beepState = "0";

    public static AllStatus parseAllStatus(String data) {
        AllStatus allStatus = new AllStatus();
        try {
            String[] strs = data.split(",", -1);
            allStatus.switchStatus = strs[0];
            allStatus.windSpeed = strs[1];
            allStatus.mode = strs[2];
            allStatus.temperature = strs[3];
            allStatus.pm25 = strs[4];
            allStatus.negativeIonSwitch = strs[5];
            allStatus.timer = strs[6];
            allStatus.uv = strs[7];
            allStatus.humidity = strs[8];
            allStatus.beepState = strs[9];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allStatus;
    }

    public boolean isPowerOn() {
        return "1".equals(this.switchStatus);
    }

    public int getWindValue() {
        int intWind = 0;
        try {
            intWind = Integer.parseInt(windSpeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intWind;
    }

    public int getPM25() {
        int pm25Value = 0;
        try {
            pm25Value = Integer.parseInt(this.pm25);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return pm25Value;
    }

    public boolean isNegativeIonSwitchOn() {
        return "1".equals(this.negativeIonSwitch);
    }

    public boolean isUVSwitchOn() {
        return "1".equals(this.uv);
    }

    public boolean isBeepStateSwitchOn() {
        return "1".equals(this.beepState);
    }

    public boolean isTimerOpen() {
        try {
            int t = Integer.parseInt(timer);
            if (t > 0) {
                return true;
            }
        } catch (Exception e) {

        }

        return false;
    }

    public String getTimeStr() {
        if (TextUtils.isEmpty(this.timer)) {
            return "00:00";
        }
        if (timer.length() != 4) {
            timer = String.format("%04d", timer);
        }
        String hour = timer.substring(0, 2);
        String min = timer.substring(2);
        return Integer.parseInt(hour) + " 小时" + Integer.parseInt(min) + " 分钟";
    }

    @Override
    public String toString() {
        return "AllStatus{" +
                "switchStatus='" + switchStatus + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", mode='" + mode + '\'' +
                ", temperature='" + temperature + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", negativeIonSwitch='" + negativeIonSwitch + '\'' +
                ", timer='" + timer + '\'' +
                ", uv='" + uv + '\'' +
                ", humidity='" + humidity + '\'' +
                ", beepState='" + beepState + '\'' +
                '}';
    }
}
