/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by lijunjie on 16/1/21.
 */
@Deprecated
public class WeiboAuthActivity extends Activity {

//    private AuthInfo mAuthInfo;

    /**
     * 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能
     */
//    private Oauth2AccessToken mAccessToken;

    private static final String REDIRECT_URL = "http://sns.whalecloud.com";

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
//    private SsoHandler mSsoHandler;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String redirectUrl = savedInstanceState == null ? REDIRECT_URL : savedInstanceState.getString("redirectUrl", REDIRECT_URL);
//        mAuthInfo = new AuthInfo(this, Configuration.getWeiboAppkey(this), redirectUrl, null);
//        mSsoHandler = new SsoHandler(this, mAuthInfo);
//        mSsoHandler.authorize(new AuthListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
//        if (mSsoHandler != null) {
//            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
//        }
    }

//    class AuthListener implements WeiboAuthListener {
//
//        @Override
//        public void onComplete(Bundle values) {
//            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
//            String phoneNum = mAccessToken.getPhoneNum();
//            if (mAccessToken.isSessionValid()) {
//                TreeMap<String, String> param = new TreeMap<String, String>();
//                param.put("third_party_openid", mAccessToken.getUid());
//                param.put("third_party_token", mAccessToken.getToken());
//                param.put("third_party_type", "3");
//                RequestHelper.getInstance(WeiboAuthActivity.this).check3P(param, new FetchDataCallback() {
//                    @Override
//                    public void OnFetchDataSuccess(int api, int responseCode, String responseBody) {
//                        setResult(RESULT_CANCELED);
//                        finish();
//                    }
//
//                    @Override
//                    public void OnFetchDataFailed(int api, Throwable error, String responseBody) {
//                        switch (api) {
//                            case RequestHelper.API_3P_CHECK:
//                                Intent intent = new Intent();
//                                intent.putExtra("accessToken", mAccessToken.getToken());
//                                intent.putExtra("openId", mAccessToken.getUid());
//                                intent.putExtra("thirdPartyId", 3);
//                                switch (JsonUtil.getResultCode(responseBody)) {
//                                    case 30117:
//                                        // not register
//                                        intent.putExtra("registered", false);
//                                        break;
//                                    case 30118:
//                                        // registered
//                                        intent.putExtra("accessToken", JsonUtil.getToken(responseBody));
//                                        intent.putExtra("registered", true);
//                                        break;
//                                    default:
//                                        intent.putExtra("registered", false);
//                                }
//                                setResult(RESULT_OK, intent);
//                                finish();
//                                break;
//                        }
//                    }
//                });
//            } else {
//                // 以下几种情况，您会收到 Code：
//                // 1. 当您未在平台上注册的应用程序的包名与签名时；
//                // 2. 当您注册的应用程序包名与签名不正确时；
//                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
//                setResult(RESULT_CANCELED);
//                finish();
//            }
//        }
//
//        @Override
//        public void onCancel() {
//            setResult(RESULT_CANCELED);
//            finish();
//        }
//
//        @Override
//        public void onWeiboException(WeiboException e) {
//            setResult(RESULT_CANCELED);
//            finish();
//        }
//    }
}
