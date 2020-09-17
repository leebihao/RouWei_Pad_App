/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scinan.sdk.R;


/**
 * Created by Jason on 15/7/8.
 */
public class ListViewFooter extends AbListViewFooter {

    public ListViewFooter(Context context) {
        super(context);
    }

    public ListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initView(Context context) {
        mContext = context;

        //底部刷新
        footerView = new LinearLayout(context);
        //设置布局 水平方向
        footerView.setOrientation(HORIZONTAL);
        footerView.setGravity(Gravity.CENTER);
        footerView.setBackgroundColor(getResources().getColor(R.color.sdk_background_color_grey));
        footerView.setMinimumHeight(AbViewUtil.scale(mContext, 100));
        footerTextView = new TextView(context);
        footerTextView.setGravity(Gravity.CENTER_VERTICAL);
        setTextColor(getResources().getColor(R.color.sdk_font_color_grey));
        AbViewUtil.setTextSize(footerTextView, 30);

        AbViewUtil.setPadding(footerView, 0, 10, 0, 10);

        footerProgressBar = (ProgressBar) inflate(context, R.layout.widget_progressbar, null);
        footerProgressBar.setVisibility(GONE);

        LayoutParams layoutParamsWW = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsWW.gravity = Gravity.CENTER;
        layoutParamsWW.width = AbViewUtil.scale(mContext, 50);
        layoutParamsWW.height = AbViewUtil.scale(mContext, 50);
        layoutParamsWW.rightMargin = AbViewUtil.scale(mContext, 10);
        footerView.addView(footerProgressBar, layoutParamsWW);

        LayoutParams layoutParamsWW1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        footerView.addView(footerTextView, layoutParamsWW1);

        LayoutParams layoutParamsFW = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(footerView, layoutParamsFW);

        //获取View的高度
        AbViewUtil.measureView(this);
        footerHeight = this.getMeasuredHeight();
    }
}
