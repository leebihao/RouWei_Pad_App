/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.pay;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.scinan.sdk.api.v2.base.BaseHelper;
import com.scinan.sdk.util.LogUtil;

/**
 * Created by lijunjie on 16/3/30.
 */
public class AliPay extends PayChannel {

    public AliPay(Activity activity, PayCallback callback) {
        super(activity, callback);
    }

    @Override
    public void run(final BaseHelper helper, final String deviceId, final String moneny) {
        getPayInfo(helper, deviceId, moneny);
    }

    @Override
    protected void pay(final String payInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(mActivity);
                // 调用支付接口，获取支付结果
                LogUtil.d("======##############" + payInfo);
                String result = alipay.pay(payInfo, true);
                AliPayResult payResult = new AliPayResult(result);
                /**
                 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                 * docType=1) 建议商户依赖异步通知
                 */
                String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    mCallback.onPaySuccess(getPayType());
                } else {
                    // 判断resultStatus 为非"9000"则代表可能支付失败
                    // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        mCallback.onPayPendding(getPayType());
                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        mCallback.onPayFailed(getPayType());
                    }
                }
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    public int getPayType() {
        return PayFactory.PAY_ALI;
    }

}
