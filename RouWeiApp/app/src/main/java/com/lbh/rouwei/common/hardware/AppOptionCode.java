package com.lbh.rouwei.common.hardware;

import com.scinan.sdk.hardware.OptionCode;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/20
 *     desc   :
 * </pre>
 */
public class AppOptionCode extends OptionCode {

    //-1 , 0, 3, 901, 902

    public static final int STATUS_SWITCH_POWER                      = 1;
    public static final int STATUS_SWITCH_NEGAT                      = 5;
    public static final int STATUS_SWITCH_UV                         = 7;


    public static String getOptionCodeNoS(int optionCode) {
        return String.format("%02d", optionCode);
    }
}
