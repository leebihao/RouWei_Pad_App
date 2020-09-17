/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.network.base;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class VendorSSLSocketFactory {

    // The default SSL socket factory
    private static SSLSocketFactory defaultSocketFactory;

    public static synchronized SSLSocketFactory getInstance(String protocol) {
        if (defaultSocketFactory != null) {
            return defaultSocketFactory;
        }

        try {
            SSLContext sc = SSLContext.getInstance(protocol);
            sc.init(null, new TrustManager[] {
                    new VeriSignTrustManager()
            }, new java.security.SecureRandom());
            defaultSocketFactory = sc.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        if (defaultSocketFactory == null) {
            defaultSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        }

        return defaultSocketFactory;
    }

    public static synchronized SSLContext getSSLContext(String protocol) {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance(protocol);
            sc.init(null, new TrustManager[]{new VeriSignTrustManager()}, new java.security.SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sc;
    }

}
