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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scinan.sdk.R;

public class ListViewHeader extends AbListViewHeader {

    public ListViewHeader(Context context) {
        super(context);
    }

    public ListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView(Context context) {

        mContext = context;

        //顶部刷新栏整体内容
        headerView = new LinearLayout(context);
        headerView.setOrientation(HORIZONTAL);
        headerView.setGravity(Gravity.CENTER);
        headerView.setBackgroundColor(getResources().getColor(R.color.sdk_background_color_grey));

        AbViewUtil.setPadding(headerView, 0, 28, 0, 28);

        //显示箭头与进度
        FrameLayout headImage = new FrameLayout(context);
        arrowImageView = new ImageView(context);
        //从包里获取的箭头图片
        //arrowImage = JavaUtil.getBitmapFromSrc("image/arrow.png");
        arrowImageView.setImageResource(R.drawable.arrow);

        //style="?android:attr/progressBarStyleSmall" 默认的样式
        headerProgressBar = (ProgressBar) inflate(context, R.layout.widget_progressbar, null);
        headerProgressBar.setVisibility(GONE);

        LayoutParams layoutParamsWW = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsWW.gravity = Gravity.CENTER;
        layoutParamsWW.width = AbViewUtil.scale(mContext, 50);
        layoutParamsWW.height = AbViewUtil.scale(mContext, 50);
        headImage.addView(arrowImageView, layoutParamsWW);
        headImage.addView(headerProgressBar, layoutParamsWW);

        //顶部刷新栏文本内容
        LinearLayout headTextLayout = new LinearLayout(context);
        tipsTextview = new TextView(context);
        headerTimeView = new TextView(context);
        headTextLayout.setOrientation(VERTICAL);
        headTextLayout.setGravity(Gravity.CENTER_VERTICAL);
        AbViewUtil.setPadding(headTextLayout, 0, 0, 0, 0);
        LayoutParams layoutParamsWW2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        headTextLayout.addView(tipsTextview, layoutParamsWW2);
        headTextLayout.addView(headerTimeView, layoutParamsWW2);
        tipsTextview.setTextColor(getResources().getColor(R.color.sdk_font_color_grey));
        headerTimeView.setTextColor(getResources().getColor(R.color.sdk_font_color_grey));
        AbViewUtil.setTextSize(tipsTextview, 28);
        AbViewUtil.setTextSize(headerTimeView, 27);

        // set headerTimeView is gone
        headerTimeView.setVisibility(GONE);

        LayoutParams layoutParamsWW3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsWW3.gravity = Gravity.CENTER;
        layoutParamsWW3.rightMargin = AbViewUtil.scale(mContext, 10);

        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setOrientation(HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER);

        headerLayout.addView(headImage, layoutParamsWW3);
        headerLayout.addView(headTextLayout, layoutParamsWW3);

        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        //添加大布局
        headerView.addView(headerLayout, lp);

        this.addView(headerView, lp);
        //获取View的高度
        AbViewUtil.measureView(this);
        headerHeight = this.getMeasuredHeight();

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        setState(STATE_NORMAL);
    }
}
