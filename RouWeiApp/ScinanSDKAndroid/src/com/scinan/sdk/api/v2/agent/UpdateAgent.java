/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.api.v2.agent;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import com.scinan.sdk.R;
import com.scinan.sdk.api.v2.base.ToolAPIHelper;
import com.scinan.sdk.api.v2.bean.UpdateResponse;
import com.scinan.sdk.api.v2.bean.UpdateStatus;
import com.scinan.sdk.update.UpdateTask;
import com.scinan.sdk.util.DialogUtils2;
import com.scinan.sdk.util.LogUtil;
import com.scinan.sdk.util.ToastUtil;

/**
 * Created by Luogical on 16/1/16.
 */
public class UpdateAgent extends ToolAPIHelper {

    private Context mContext;
    private boolean isManualUpdate = false;

    @Deprecated
    public UpdateAgent(Context context, Activity activity) {
        super(context.getApplicationContext());
        this.mContext = context;
    }

    public UpdateAgent(Context context) {
        this(context, false);

    }

    public UpdateAgent(Context context, boolean isManualUpdate) {
        super(context.getApplicationContext());
        this.mContext = context;
        this.isManualUpdate = isManualUpdate;
    }


    public void appUpdate() {
        checkAppUpdate(new ScinanUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, final UpdateResponse updateInfo) {
                LogUtil.d("updateStatus-----" + updateStatus);
                if (updateStatus == UpdateStatus.Yes) {
                    if (!TextUtils.isEmpty(updateInfo.getUrl())) {
                        DialogUtils2.getCustomDialog(mContext, mContext.getString(R.string.check_app_update) + updateInfo.getShow_version(), updateInfo.getContent(),
                                null,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (DialogInterface.BUTTON_POSITIVE == which) {
                                            new UpdateTask(mContext, UpdateTask.UPDATE_TYPE_FOR_APP)
                                                    .execute(updateInfo.getUrl());
                                        }
                                    }
                                }).show();
                    }
                } else {

                    if (isManualUpdate) {
                        ToastUtil.showMessage(mContext, mContext.getString(R.string.check_app_update_fail));
                    }
                }
            }
        });
    }

    public void pluginUpdate(final String pluginId, final UpdateTask.UpdateListener listener) {
        checkPluginUpdate(pluginId, new ScinanUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, final UpdateResponse updateInfo) {
                LogUtil.d("updateStatus-----" + updateStatus);
                if (updateStatus == UpdateStatus.Yes) {
                    if (!TextUtils.isEmpty(updateInfo.getUrl())) {
                        new UpdateTask(mContext, UpdateTask.UPDATE_TYPE_FOR_PLUGIN, listener).execute(updateInfo.getUrl());
                    } else {
                        listener.onCancel();
                    }
                } else {
                    listener.onCancel();
                }
            }
        });
    }
}
