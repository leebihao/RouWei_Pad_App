package com.scinan.sdk.hardware;

import com.scinan.sdk.util.ByteUtil;
import com.scinan.sdk.util.Crc8Util;

import java.io.Serializable;

/**
 * Created by lijunjie on 2017/11/30.
 */

public class Smart6120DataCmd implements Serializable {

    public String optionCodeString;
    public String data;
    public int optionCode;
    public String sensorType;

    public Smart6120DataCmd(String optionCode, String sensorType, String data) {
        this.optionCodeString = optionCode;
        this.data = data;
        this.optionCode = OptionCode.getOptionCode(optionCode);
        this.sensorType = sensorType;
    }

    public Smart6120DataCmd(int optionCode, String sensorType, String data) {
        this(OptionCode.get6120OptionCode(optionCode), sensorType, data);
    }

    public Smart6120DataCmd(int optionCode, String data) {
        this(optionCode, "1", data);
    }

    public Smart6120DataCmd(String optionCode, String data) {
        this(optionCode, "1", data);
    }

    public static Smart6120DataCmd parse(String fullCmdString) {
        try {
            String[] list = fullCmdString.split("/",-1);
            String optionCodeString = list[1];
            String sensorType = list[2];
            String data = list[3];
            return new Smart6120DataCmd(optionCodeString, sensorType, data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String toString() {
        return String.format("/%s/%s/%s", optionCodeString, sensorType, data);
    }

    public String getHexString() {
        String hex = ByteUtil.ascill2hex(toString());
        return hex + Crc8Util.calcCrcCommon(hex) + "0A";
    }
}