package com.lbh.rouwei.common.bean;

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

    public String switchStatus;
    public String windSpeed;
    public String mode;
    public String temperature;
    public String pm25;
    public String negativeIonSwitch;
    public String timer;
    public String uv;
    public String humidity;
    public String beepState;

    public static AllStatus parseAllStatus(String data) {
        AllStatus allStatus = new AllStatus();
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
