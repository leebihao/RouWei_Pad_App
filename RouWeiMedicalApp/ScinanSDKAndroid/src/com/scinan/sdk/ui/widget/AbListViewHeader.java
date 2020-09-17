/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */
package com.scinan.sdk.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import com.scinan.sdk.util.TimeUtil;

public class AbListViewHeader extends LinearLayout {

    /**
     * 上下文.
     */
    protected Context mContext;

    /**
     * 主View.
     */
    protected LinearLayout headerView;

    /**
     * 箭头图标View.
     */
    protected ImageView arrowImageView;

    /**
     * 进度图标View.
     */
    protected ProgressBar headerProgressBar;

    /**
     * 进度图标View Style.
     */
    public static int headerProgressBarStyle = android.R.attr.progressBarStyle;

    /**
     * 箭头图标.
     */
    protected Bitmap arrowImage = null;

    /**
     * 文本提示的View.
     */
    protected TextView tipsTextview;

    /**
     * 时间的View.
     */
    protected TextView headerTimeView;

    /**
     * 当前状态.
     */
    protected int mState = -1;

    /**
     * 向上的动画.
     */
    protected Animation mRotateUpAnim;

    /**
     * 向下的动画.
     */
    protected Animation mRotateDownAnim;

    /**
     * 动画时间.
     */
    protected final int ROTATE_ANIM_DURATION = 180;

    /**
     * 显示 下拉刷新.
     */
    public final static int STATE_NORMAL = 0;

    /**
     * 显示 松开刷新.
     */
    public final static int STATE_READY = 1;

    /**
     * 显示 正在刷新....
     */
    public final static int STATE_REFRESHING = 2;

    /**
     * 保存上一次的刷新时间.
     */
    protected String lastRefreshTime = null;

    /**
     * Header的高度.
     */
    protected int headerHeight;

    /**
     * 初始化Header.
     *
     * @param context the context
     */
    public AbListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * 初始化Header.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public AbListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * 初始化View.
     *
     * @param context the context
     */
    public void initView(Context context) {

        mContext = context;

        //顶部刷新栏整体内容
        headerView = new LinearLayout(context);
        headerView.setOrientation(LinearLayout.HORIZONTAL);
        headerView.setGravity(Gravity.CENTER);

        AbViewUtil.setPadding(headerView, 0, 10, 0, 10);

        //显示箭头与进度
        FrameLayout headImage = new FrameLayout(context);
        arrowImageView = new ImageView(context);
        //从包里获取的箭头图片
        //arrowImage = JavaUtil.getBitmapFromSrc("image/arrow.png");
        //arrowImageView.setImageBitmap(arrowImage);

        //style="?android:attr/progressBarStyleSmall" 默认的样式
        headerProgressBar = new ProgressBar(context, null, headerProgressBarStyle);
        headerProgressBar.setVisibility(View.GONE);

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
        headTextLayout.setOrientation(LinearLayout.VERTICAL);
        headTextLayout.setGravity(Gravity.CENTER_VERTICAL);
        AbViewUtil.setPadding(headTextLayout, 0, 0, 0, 0);
        LayoutParams layoutParamsWW2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        headTextLayout.addView(tipsTextview, layoutParamsWW2);
        headTextLayout.addView(headerTimeView, layoutParamsWW2);
        tipsTextview.setTextColor(Color.rgb(107, 107, 107));
        headerTimeView.setTextColor(Color.rgb(107, 107, 107));
        AbViewUtil.setTextSize(tipsTextview, 30);
        AbViewUtil.setTextSize(headerTimeView, 27);

        LayoutParams layoutParamsWW3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsWW3.gravity = Gravity.CENTER;
        layoutParamsWW3.rightMargin = AbViewUtil.scale(mContext, 10);

        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
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

    /**
     * 设置状态.
     *
     * @param state the new state
     */
    public void setState(int state) {
        if (state == mState) return;

        if (state == STATE_REFRESHING) {
            arrowImageView.clearAnimation();
            arrowImageView.setVisibility(View.INVISIBLE);
            headerProgressBar.setVisibility(View.VISIBLE);
        } else {
            arrowImageView.setVisibility(View.VISIBLE);
            headerProgressBar.setVisibility(View.INVISIBLE);
        }

        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_READY) {
                    arrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    arrowImageView.clearAnimation();
                }
                tipsTextview.setText(R.string.xialashuaxin);

                if (lastRefreshTime == null) {
                    lastRefreshTime = TimeUtil.getCurrentDate(TimeUtil.dateFormatHMS);
                    headerTimeView.setText("刷新时间：" + lastRefreshTime);
                } else {
                    headerTimeView.setText("上次刷新时间：" + lastRefreshTime);
                }

                break;
            case STATE_READY:
                if (mState != STATE_READY) {
                    arrowImageView.clearAnimation();
                    arrowImageView.startAnimation(mRotateUpAnim);
                    tipsTextview.setText(R.string.songkaishuaxin);
                    headerTimeView.setText("上次刷新时间：" + lastRefreshTime);
                    lastRefreshTime = TimeUtil.getCurrentDate(TimeUtil.dateFormatHMS);

                }
                break;
            case STATE_REFRESHING:
                tipsTextview.setText(R.string.shuaxin);
                headerTimeView.setText("本次刷新时间：" + lastRefreshTime);
                break;
            default:
        }

        mState = state;
    }

    /**
     * 设置header可见的高度.
     *
     * @param height the new visiable height
     */
    public void setVisiableHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
        lp.height = height;
        headerView.setLayoutParams(lp);
    }

    /**
     * 获取header可见的高度.
     *
     * @return the visiable height
     */
    public int getVisiableHeight() {
        LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
        return lp.height;
    }

    /**
     * 描述：获取HeaderView.
     *
     * @return the header view
     */
    public LinearLayout getHeaderView() {
        return headerView;
    }

    /**
     * 设置上一次刷新时间.
     *
     * @param time 时间字符串
     */
    public void setRefreshTime(String time) {
        headerTimeView.setText(time);
    }

    /**
     * 获取header的高度.
     *
     * @return 高度
     */
    public int getHeaderHeight() {
        return headerHeight;
    }

    /**
     * 描述：设置字体颜色.
     *
     * @param color the new text color
     */
    public void setTextColor(int color) {
        tipsTextview.setTextColor(color);
        headerTimeView.setTextColor(color);
    }

    /**
     * 描述：设置背景颜色.
     *
     * @param color the new background color
     */
    public void setBackgroundColor(int color) {
        headerView.setBackgroundColor(color);
    }

    /**
     * 描述：获取Header ProgressBar，用于设置自定义样式.
     *
     * @return the header progress bar
     */
    public ProgressBar getHeaderProgressBar() {
        return headerProgressBar;
    }

    /**
     * 描述：设置Header ProgressBar样式.
     *
     * @param indeterminateDrawable the new header progress bar drawable
     */
    public void setHeaderProgressBarDrawable(Drawable indeterminateDrawable) {
        headerProgressBar.setIndeterminateDrawable(indeterminateDrawable);
    }

    /**
     * 描述：得到当前状态.
     *
     * @return the state
     */
    public int getState() {
        return mState;
    }

    /**
     * 设置提示状态文字的大小.
     *
     * @param size the new state text size
     */
    public void setStateTextSize(int size) {
        tipsTextview.setTextSize(size);
    }

    /**
     * 设置提示时间文字的大小.
     *
     * @param size the new time text size
     */
    public void setTimeTextSize(int size) {
        headerTimeView.setTextSize(size);
    }

    /**
     * Gets the arrow image view.
     *
     * @return the arrow image view
     */
    public ImageView getArrowImageView() {
        return arrowImageView;
    }

    /**
     * 描述：设置顶部刷新图标.
     *
     * @param resId the new arrow image
     */
    public void setArrowImage(int resId) {
        this.arrowImageView.setImageResource(resId);
    }


}
