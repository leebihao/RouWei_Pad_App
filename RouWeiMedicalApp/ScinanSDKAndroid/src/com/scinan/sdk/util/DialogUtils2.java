/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import com.scinan.sdk.ui.widget.MaterialDialog;

public class DialogUtils2 {

    /***
     * 获取一个dialog
     *
     * @param context
     * @return
     */
    public static MaterialDialog.Builder getDialog(Context context) {
        return new MaterialDialog.Builder(context);
    }

    /***
     * 获取一个耗时等待对话框
     *
     * @param context
     * @param message
     * @return
     */
    public static MaterialDialog.Builder getWaitDialog(Context context, String message) {
        MaterialDialog.Builder builder = getDialog(context);
        builder.setMessage(message);
        return builder;
    }

    /***
     * 获取一个信息对话框，注意需要自己手动调用show方法显示
     *
     * @param context
     * @param message
     * @param onClickListener
     * @return
     */
    public static MaterialDialog.Builder getMessageDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        MaterialDialog.Builder materialDialog = getDialog(context);
        materialDialog.setMessage(message);
        materialDialog.setPositiveButton(android.R.string.ok, onClickListener);
        return materialDialog;
    }

    public static MaterialDialog.Builder getMessageDialog(Context context, String title, Spanned message, DialogInterface.OnClickListener onClickListener) {
        MaterialDialog.Builder materialDialog = getDialog(context);
        materialDialog.setMessage(message);
        if (!TextUtils.isEmpty(title)) {
            materialDialog.setTitle(title);
        }
        materialDialog.setPositiveButton(android.R.string.ok, onClickListener);
        return materialDialog;
    }

    public static MaterialDialog.Builder getMessageDialog(Context context, String message) {
        return getMessageDialog(context, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static MaterialDialog.Builder getMessageDialog(Context context, Spanned message) {
        return getMessageDialog(context, null, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static MaterialDialog.Builder getMessageDialog(Context context, String title, Spanned message) {
        return getMessageDialog(context, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static MaterialDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        final MaterialDialog.Builder builder = getDialog(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton(android.R.string.ok, onClickListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }

    public static MaterialDialog.Builder getConfirmDialog(Context context, String message, String left, String right, DialogInterface.OnClickListener onClickListener) {
        MaterialDialog.Builder builder = getDialog(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton(Html.fromHtml(left), onClickListener);
        builder.setNegativeButton(Html.fromHtml(right), onClickListener);
        return builder;
    }

    public static MaterialDialog.Builder getConfirmDialog(Context context, String title, String message, String left, String right, DialogInterface.OnClickListener onClickListener) {
        MaterialDialog.Builder builder = getDialog(context);
        builder.setTitle(Html.fromHtml(title));
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton(Html.fromHtml(left), onClickListener);
        builder.setNegativeButton(Html.fromHtml(right), onClickListener);
        return builder;
    }

    public static MaterialDialog.Builder getConfirmDialog(Context context, String title, String message, int left, int right, DialogInterface.OnClickListener onClickListener) {
        MaterialDialog.Builder builder = getDialog(context);
        builder.setTitle(Html.fromHtml(title));
        builder.setMessage((Html.fromHtml(message)));
        builder.setPositiveButton(left, onClickListener);
        builder.setNegativeButton(right, onClickListener);
        return builder;
    }

    public static MaterialDialog.Builder getConfirmDialog(Context context, String message, DialogInterface.OnClickListener onOkClickListener, DialogInterface.OnClickListener onCancleClickListener) {
        MaterialDialog.Builder builder = getDialog(context);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, onOkClickListener);
        builder.setNegativeButton(android.R.string.cancel, onCancleClickListener);
        return builder;
    }

    public static MaterialDialog.Builder getConfirmDialog(Context context, String title, String message, DialogInterface.OnClickListener onOkClickListener, DialogInterface.OnClickListener onCancleClickListener) {
        MaterialDialog.Builder builder = getDialog(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, onOkClickListener);
        builder.setNegativeButton(android.R.string.cancel, onCancleClickListener);
        return builder;
    }

    public static MaterialDialog.Builder getCustomDialog(Context context, String title, View view, DialogInterface.OnClickListener onClickListener) {
        final MaterialDialog.Builder materialDialog = getDialog(context);
        materialDialog.setView(view);
        if (!TextUtils.isEmpty(title)) {
            materialDialog.setTitle(title);
        }
        materialDialog.setPositiveButton(android.R.string.ok, onClickListener);
        materialDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return materialDialog;
    }

    public static MaterialDialog.Builder getCustomForceDialog(Context context, String title, View view, DialogInterface.OnClickListener onClickListener) {
        MaterialDialog.Builder materialDialog = getDialog(context);
        materialDialog.setView(view);
        if (!TextUtils.isEmpty(title)) {
            materialDialog.setTitle(title);
        }
        materialDialog.setPositiveButton(android.R.string.ok, onClickListener);
        materialDialog.setCanceledOnTouchOutside(false);
        return materialDialog;
    }

    public static MaterialDialog.Builder getCustomForceDialog(Context context, String title, String msg, DialogInterface.OnClickListener onClickListener) {
        MaterialDialog.Builder materialDialog = getDialog(context);
        if (!TextUtils.isEmpty(msg)) {
            materialDialog.setMessage(msg);
        }
        if (!TextUtils.isEmpty(title)) {
            materialDialog.setTitle(title);
        }
        materialDialog.setPositiveButton(android.R.string.ok, onClickListener);
        materialDialog.setCanceledOnTouchOutside(false);
        return materialDialog;
    }

    public static MaterialDialog.Builder getCustomDialog(Context context, String title, String message, View view, DialogInterface.OnClickListener onClickListener) {
        final MaterialDialog.Builder materialDialog = getDialog(context);
        materialDialog.setView(view);
        if (!TextUtils.isEmpty(title)) {
            materialDialog.setTitle(title);
        }
        materialDialog.setMessage(message);
        materialDialog.setPositiveButton(android.R.string.ok, onClickListener);
        return materialDialog;
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String title, String[] arrays, int checked, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(arrays, checked, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setPositiveButton(android.R.string.ok, onClickListener);
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder;
    }

    public static AlertDialog.Builder getMultiSelectDialog(Context context, String title, String[] arrays, boolean[] checked, DialogInterface.OnMultiChoiceClickListener listener, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMultiChoiceItems(arrays, checked, listener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setPositiveButton(android.R.string.ok, onClickListener);
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder;
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String title, String[] arrays, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(arrays, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setPositiveButton(android.R.string.cancel, null);
        return builder;
    }

    public static AlertDialog.Builder getSelectDialog(Context context, String[] arrays, DialogInterface.OnClickListener onClickListener) {
        return getSelectDialog(context, "", arrays, 0, onClickListener);
    }

    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String title, String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setSingleChoiceItems(arrays, selectIndex, onClickListener);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        builder.setNegativeButton(android.R.string.cancel, null);
        return builder;
    }

    public static AlertDialog.Builder getSingleChoiceDialog(Context context, String[] arrays, int selectIndex, DialogInterface.OnClickListener onClickListener) {
        return getSingleChoiceDialog(context, "", arrays, selectIndex, onClickListener);
    }

    public static MaterialDialog.Builder getInputDialog(Context context, String message, String hint, DialogInterface.OnClickListener onClickListener) {
        MaterialDialog.Builder builder = getConfirmDialog(context, message, onClickListener);
        builder.setInputEnable(true);
        builder.setInputHint(hint);

        return builder;
    }
}
