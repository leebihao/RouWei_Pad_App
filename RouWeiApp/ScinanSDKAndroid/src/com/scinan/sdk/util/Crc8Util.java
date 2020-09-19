package com.scinan.sdk.util;

/**
 * Created by lijunjie on 17/4/13.
 */

public class Crc8Util {

    public static byte calcCrc8(byte[] data) {
        int crc = 0xFF;
        for (int i = 0; i < data.length; i++) {
            crc ^= data[i] & 0xFF;
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x01) != 0)
                    crc = (crc >> 1) ^ 0x8C;
                else
                    crc = crc >> 1;
            }
        }
        return (byte) crc;
    }

    public static String calcCrcCommon(String data) {
        LogUtil.d("getCheckSum data is " + data);
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num+=2;
        }
        String checkSum = Integer.toHexString(total & 255).toUpperCase();

        while (checkSum.length() < 2) {
            checkSum = "0" + checkSum;
        }

        LogUtil.d(data + " --> checksum is " + checkSum);
        return checkSum;
    }

    public static String calcCrcCommon(byte[] data) {
        return calcCrcCommon(ByteUtil.bytes2HexString(data));
    }
}
