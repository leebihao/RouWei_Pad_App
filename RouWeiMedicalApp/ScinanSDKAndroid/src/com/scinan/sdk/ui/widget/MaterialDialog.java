package com.scinan.sdk.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scinan.sdk.R;
import com.scinan.sdk.util.AndroidUtil;

/**
 * Created by Jason on 15/6/7.
 */
public class MaterialDialog extends Dialog {
    public MaterialDialog(Context context) {
        super(context);
    }

    public MaterialDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {

        private Context context;
        private CharSequence title;
        private CharSequence message;
        private int gravity;
        private CharSequence positiveButtonText;
        private CharSequence negativeButtonText;

        private OnClickListener
                positiveButtonClickListener,
                negativeButtonClickListener;
        private int imageRid;
        private boolean outsideCancelable = true;
        private View customView;
        private boolean isInputEnable = false;
        private EditText input;
        private String inputHintString;

        private TextView positiveView;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setTitle(int title) {
            return setTitle(context.getString(title));
        }

        public Builder setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(int message) {
            return setMessage(Html.fromHtml(context.getString(message)));
        }

        public Builder setMessage(int message, int gravity) {
            this.gravity = gravity;
            return setMessage(Html.fromHtml(context.getString(message)));
        }

        public Builder setMessage(String message, int gravity) {
            this.gravity = gravity;
            this.message = message;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessage(Spanned message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            return setPositiveButton(Html.fromHtml(context.getString(positiveButtonText)), listener);
        }

        public Builder setPositiveButton(CharSequence positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            return setNegativeButton(Html.fromHtml(context.getString(negativeButtonText)), listener);
        }

        public Builder setNegativeButton(CharSequence negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setImagePositiveButton(int imageRid, OnClickListener listener) {
            this.imageRid = imageRid;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setView(View view) {
            this.customView = view;
            return this;
        }

        public Builder setCanceledOnTouchOutside(boolean outside) {
            this.outsideCancelable = outside;
            return this;
        }

        public Builder setInputEnable(boolean isInputEnable) {
            this.isInputEnable = isInputEnable;
            return this;
        }

        public Builder setInputHint(String hintString) {
            this.inputHintString = hintString;
            return this;
        }

        public String getInputString() {
            return input.getText().toString().trim();
        }

        public void show() {
            create().show();
        }

        public TextView getPositiveButton() {
            return positiveView;
        }

        public MaterialDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final MaterialDialog dialog = new MaterialDialog(context, R.style.DialogTheme);
            View dialogView = inflater.inflate(R.layout.sdk_material_dialog, null);
            dialog.addContentView(dialogView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView titleView = (TextView) dialogView.findViewById(R.id.dialog_title);
            TextView contentView = (TextView) dialogView.findViewById(R.id.dialog_content);
            positiveView = (TextView) dialogView.findViewById(R.id.dialog_positive);
            TextView negativeView = (TextView) dialogView.findViewById(R.id.dialog_negative);
            LinearLayout contentLayout = (LinearLayout) dialogView.findViewById(R.id.dialog_content_layout);
            ViewGroup.LayoutParams lp = contentLayout.getLayoutParams();
            ImageView imagePositiveView = (ImageView) dialogView.findViewById(R.id.dialog_image_positive);
            View dialog_line = dialogView.findViewById(R.id.dialog_line);
            input = (EditText) dialogView.findViewById(R.id.input);

            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
            } else {
                titleView.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(message)) {
                contentView.setText(message);
                contentView.setGravity(gravity);
            } else {
                contentView.setVisibility(View.GONE);
                LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                titleParams.gravity = Gravity.CENTER;
                titleView.setLayoutParams(titleParams);
                lp.height = AndroidUtil.dip2px(context, 71);
                contentLayout.setLayoutParams(lp);
            }

            if (positiveButtonClickListener != null) {
                if (negativeButtonClickListener == null) {
                    positiveView.setBackgroundResource(R.drawable.sdk_selector_background_dialog_btn_single);
                } else {
                    positiveView.setBackgroundResource(R.drawable.sdk_selector_background_dialog_btn_left);
                }
                positiveView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        dialog.cancel();
                    }
                });
            } else {
                positiveView.setVisibility(View.GONE);
                dialog_line.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(positiveButtonText)) {
                positiveView.setText(positiveButtonText);
            }
            if (negativeButtonClickListener != null) {
                if (positiveButtonClickListener == null) {
                    negativeView.setBackgroundResource(R.drawable.sdk_selector_background_dialog_btn_single);
                } else {
                    negativeView.setBackgroundResource(R.drawable.sdk_selector_background_dialog_btn_right);
                }
                negativeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        dialog.dismiss();
                    }
                });
            } else {
                negativeView.setVisibility(View.GONE);
                dialog_line.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(negativeButtonText)) {
                negativeView.setText(negativeButtonText);
            }
            if (imageRid != 0 && positiveButtonClickListener != null) {
                imagePositiveView.setImageResource(imageRid);
                imagePositiveView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                    }
                });
                positiveView.setVisibility(View.GONE);
                imagePositiveView.setVisibility(View.VISIBLE);
            } else {
                imagePositiveView.setVisibility(View.GONE);
            }

            if (isInputEnable) {
                input.setVisibility(View.VISIBLE);
                input.requestFocus();

                if (!TextUtils.isEmpty(inputHintString)) {
                    input.setHint(inputHintString);
                }

                if (input.getText().length() == 0) {
                    positiveView.setEnabled(false);
                }

                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            positiveView.setEnabled(false);
                        } else {
                            positiveView.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }

            dialog.setCanceledOnTouchOutside(outsideCancelable);
            if (customView != null) {
                dialog.setContentView(customView);
            }
            return dialog;
        }
    }
}
