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

public class PullToRefreshView extends AbPullToRefreshView {

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshView(Context context) {
        super(context);
    }

    @Override
    public void addHeaderView() {
        mHeaderView = new ListViewHeader(mContext);
        mHeaderViewHeight = mHeaderView.getHeaderHeight();
        mHeaderView.setGravity(Gravity.BOTTOM);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderViewHeight);
        // 设置topMargin的值为负的header View高度,即将其隐藏在最上方
        params.topMargin = -(mHeaderViewHeight);
        addView(mHeaderView, params);
    }

    @Override
    public void addFooterView() {

        mFooterView = new ListViewFooter(mContext);
        mFooterViewHeight = mFooterView.getFooterHeight();

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mFooterViewHeight);
        addView(mFooterView, params);
    }
}
