/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HashUtil {

    private MessageDigest md = null;

    private static MD5HashUtil md5 = null;

    private static final char[] hexChars = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Constructor is private so you must use the getInstance method
     */
    private MD5HashUtil() throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("MD5");
    }

    /**
     * This returns the singleton instance
     */
    public static MD5HashUtil getInstance() throws NoSuchAlgorithmException {

        if (md5 == null) {
            md5 = new MD5HashUtil();

        }

        return (md5);
    }

    public static String hashCode(String dataToHash) {
        try {
            return getInstance().hashData(dataToHash.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return dataToHash;
    }

    public static String hashCode(byte[] dataToHash)
            throws NoSuchAlgorithmException {
        return getInstance().hashData(dataToHash);
    }

    public String hashData(byte[] dataToHash) {
        return hexStringFromBytes((calculateHash(dataToHash))).toLowerCase();
    }

    private byte[] calculateHash(byte[] dataToHash)

    {
        md.update(dataToHash, 0, dataToHash.length);

        return (md.digest());
    }

    public String hexStringFromBytes(byte[] b)

    {

        String hex = "";

        int msb;

        int lsb = 0;
        int i;

        // MSB maps to idx 0

        for (i = 0; i < b.length; i++)

        {

            msb = ((int) b[i] & 0x000000FF) / 16;

            lsb = ((int) b[i] & 0x000000FF) % 16;
            hex = hex + hexChars[msb] + hexChars[lsb];
        }
        return (hex);
    }
}
