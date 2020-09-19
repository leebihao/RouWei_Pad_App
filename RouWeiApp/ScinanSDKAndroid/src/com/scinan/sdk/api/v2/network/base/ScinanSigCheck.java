/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.network.base;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ScinanSigCheck {
	/**
	 * 新的md5签名，首尾放secret
	 * @param params 请求参数
	 * @param secret 分配给App的key_secret
	 */
	public static String md5Signature(TreeMap<String, String> params, String secret) {
		String result = null;
		StringBuffer orgin = getBeforeSign(params, new StringBuffer(secret));
		if (orgin == null)
			return result;
		orgin.append(secret);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			result = byte2hex(md.digest(orgin.toString().getBytes("utf-8")));
		} catch (Exception e) {
			throw new RuntimeException("sign error !");
		}
		return result;
	}

	/**
	 * 二行制转字符串
	 */
	private static String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs.append("0").append(stmp);
			else
				hs.append(stmp);
		}
		return hs.toString().toUpperCase();
	}

	/**
	 * 添加参数的封装方法 
	 * @param params
	 * @param orgin
	 * @return
	 */
	private static StringBuffer getBeforeSign(TreeMap<String, String> params, StringBuffer orgin) {
		if (params == null)
			return null;
		Map<String, String> treeMap = new TreeMap<String, String>();
		treeMap.putAll(params);
		Iterator<String> iter = treeMap.keySet().iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			orgin.append(name).append(params.get(name));
		}
		return orgin;
	}

	/**
	 * 签名验证
	 * @param params 请求参数
	 * @param secret 分配给App的key_secret
	 * @param sign 接收到的签名
	 */
    public static boolean verifySig(Map<String, String> params, String secret, String sig) 
    {
        // 确保不含sig
        params.remove("sign");
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.putAll(params);
        // 计算签名
        String sig_new = md5Signature(treeMap, secret);
        // 对比和腾讯返回的签名
        return sig_new.equals(sig);
    }
}
