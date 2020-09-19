package com.scinan.sdk.util;


import android.text.TextUtils;

public class ByteUtil {

    public static String byte2HexString(byte b) {
        return bytes2HexString(new byte[] {b});
    }

    public static String bytes2HexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String int2HexString(int[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public static String[] bytes2HexStrArray(byte[] bArray) {
        String[] array = new String[bArray.length];
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            StringBuffer sb = new StringBuffer(2);
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
            array[i] = sb.toString();
        }
        return array;
    }

    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            s = new String(baKeyword, "utf-8");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String bytes2HexString(byte[] paramArrayOfByte, int length)
    {
        int i = length;
        StringBuilder localStringBuilder = new StringBuilder(length);
        if (paramArrayOfByte.length < length)
        {
            i = paramArrayOfByte.length;
        }
        for (int j = 0; j < i; ++j)
        {
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Byte.valueOf(paramArrayOfByte[j]);
            localStringBuilder.append(String.format("%02X", arrayOfObject));
        }
        return localStringBuilder.toString();
    }

    public static byte[] hex2Bytes(String src) {
        src = src.toUpperCase();
        try {
            byte[] res = new byte[src.length() / 2];
            char[] chs = src.toCharArray();
            int[] b = new int[2];

            for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
                for (int j = 0; j < 2; j++) {
                    if (chs[i + j] >= '0' && chs[i + j] <= '9') {
                        b[j] = (chs[i + j] - '0');
                    } else if (chs[i + j] >= 'A' && chs[i + j] <= 'F') {
                        b[j] = (chs[i + j] - 'A' + 10);
                    } else if (chs[i + j] >= 'a' && chs[i + j] <= 'f') {
                        b[j] = (chs[i + j] - 'a' + 10);
                    }
                }

                b[0] = (b[0] & 0x0f) << 4;
                b[1] = (b[1] & 0x0f);
                res[c] = (byte) (b[0] | b[1]);
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    public static String int2hex(int src) {
        String origin = Integer.toHexString(src).toUpperCase();
        if (origin.length() % 2 != 0) {
            origin = "0" + origin;
        }

        if (src < 0 && origin.length() > 1) {
            origin = origin.substring(origin.length()-2);
        }
        return origin;
    }

    public static int hex2int(String src) {
        try {
            src = src.toUpperCase();
            return Integer.valueOf(src.length() % 2 == 0 ? src : "0" + src, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int bytes2int(byte[] paramArrayOfByte, int length) {
        return hex2int(bytes2HexString(paramArrayOfByte, length));
    }

    public static String getTimeHex(String format) {
        if (TextUtils.isEmpty(format)) {
            format = "yyMMddHHmm";
        }
        String old = AndroidUtil.getTimeString(System.currentTimeMillis(), format);
        String a = "";
        for (int i = 0; i < old.length() -1; i = i+2) {
            a = a + int2hex(Integer.valueOf(old.substring(i, i + 2)));
        }
        return a;
    }

    public static String ascill2hex(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString().toUpperCase();
    }

    public static String hex2Ascii(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "ASCII");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
}
