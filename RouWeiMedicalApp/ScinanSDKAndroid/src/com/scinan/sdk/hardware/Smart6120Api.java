package com.scinan.sdk.hardware;

import android.util.Log;

import com.scinan.sdk.util.ByteUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lijunjie on 2017/11/30.
 */

public class Smart6120Api {

    public static final int OPTIONCODE_CONFIG = 1;
    public static final int OPTIONCODE_STATUS = 2;
    public static final int OPTIONCODE_CLOSE_BLE = 3;
    public static final int OPTIONCODE_VERSION = 10;


    private static Smart6120DataCmd buildConfigNetworkDataCmd(String ssid, String pwd) {
        //Log.e("AAAA----->", ByteUtil.bytes2HexString(ssid.getBytes()) + "," + ByteUtil.bytes2HexString(pwd.getBytes()));
        //Log.e("AAAA----->", ByteUtil.ascill2hex(ssid) + "," + ByteUtil.ascill2hex(pwd));
        return getCommonCmd(OPTIONCODE_CONFIG, ByteUtil.bytes2HexString(ssid.getBytes()) + "," + ByteUtil.bytes2HexString(pwd.getBytes()));
    }

    private static Smart6120DataCmd buildQueryStatusDataCmd() {
        return getCommonCmd(OPTIONCODE_STATUS, "0");
    }

    private static Smart6120DataCmd buildCloseBLEAdDataCmd() {
        return getCommonCmd(OPTIONCODE_CLOSE_BLE, "1");
    }

    private static Smart6120DataCmd buildQueryVersionDataCmd() {
        return getCommonCmd(OPTIONCODE_VERSION, "0");
    }

    private static Smart6120DataCmd getCommonCmd(int code, String data) {
        return new Smart6120DataCmd(code, data);
    }

    public static Smart6120Transfer buildResponseTransfer(boolean ok, Smart6120Transfer old) {
        return new Smart6120Transfer(Smart6120Transfer.Category.From_APP_response, old.getCmdId(), old.getNo(), (byte)(ok ? 0x00 : 0x01));
    }

    public static ArrayList<Smart6120Transfer> buildConfigNetworkTransfers(String ssid, String pwd) {
        return buildRequestTransfer(buildConfigNetworkDataCmd(ssid, pwd));
    }

    public static ArrayList<Smart6120Transfer> buildQueryStatusTransfers() {
        return buildRequestTransfer(buildQueryStatusDataCmd());
    }

    public static ArrayList<Smart6120Transfer> buildCloseBLETransfers() {
        return buildRequestTransfer(buildCloseBLEAdDataCmd());
    }

    public static ArrayList<Smart6120Transfer> buildQueryVersionTransfers() {
        return buildRequestTransfer(buildQueryVersionDataCmd());
    }

    public static ArrayList<Smart6120Transfer> buildRequestTransfer(Smart6120DataCmd cmd) {
        ArrayList<Smart6120Transfer> transfers = new ArrayList<Smart6120Transfer>();
        String hex = cmd.getHexString();
        int length = hex.length();
        int total = length / 34 + (length % 34 == 0 ? 0 : 1);
        int cmdId = getNextCmdID();
        for (int i = 0; i< total; i ++) {
            Smart6120Transfer transfer = new Smart6120Transfer();
            transfer.setCategory(Smart6120Transfer.Category.From_APP_request);
            transfer.setCmdId(cmdId);
            transfer.setNo((byte) i);
            transfer.setTotal((byte) total);
            transfer.setData(hex.substring(i*34, (i == total - 1 ) ? length : i * 34 + 34));
            transfers.add(transfer);
        }
        return transfers;
    }

    public static int CMD_ID = -1;
    public static synchronized int getNextCmdID() {
        CMD_ID = (CMD_ID + 1) & 0xF;
        return CMD_ID;
    }
}
