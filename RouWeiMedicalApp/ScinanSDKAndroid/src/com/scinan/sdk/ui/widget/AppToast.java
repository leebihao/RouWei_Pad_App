package com.scinan.sdk.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.scinan.sdk.R;
import com.scinan.sdk.util.ScreenUtils;

import java.lang.ref.WeakReference;

/**
 * 自定义的Toast
 * <p>
 * AppToast 集成为单例Toast 预防多次提示
 */
public class AppToast extends Toast {
    private static final String TAG = "AppToast";

    private static WeakReference<AppToast> mToast;
    private static int mScreenHeight;

    public AppToast(Context context) {
        super(context);
        mScreenHeight = ScreenUtils.getScreenHeight(context);
    }

    public static void show(Context context, int resId) {
        show(context, resId, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        String content = context.getString(resId);
        show(context, content, duration);
    }

    public static void show(Context context, CharSequence text, int duration) {
        AppToast.makeText(context.getApplicationContext(), text, duration).show();
    }

    public static Toast makeText(Context context, CharSequence text, int duration) {
        AppToast toast;
        if (mToast == null || mToast.get() == null) {
            toast = new AppToast(context);
            mToast = new WeakReference<AppToast>(toast);
        } else {
            toast = mToast.get();
        }

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.widget_app_toast, null);
        TextView tv = (TextView) v.findViewById(R.id.message);
        tv.setText(text);

        toast.setView(v);
        toast.setDuration(duration);
        toast.setGravity(toast.getGravity(), toast.getXOffset(), mScreenHeight / 6);
        return toast;
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param context  The context to use.  Usually your {@link android.app.Application}
     *                 or {@link android.app.Activity} object.
     * @param resId    The resource id of the string resource to use.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or
     *                 {@link #LENGTH_LONG}
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    public static Toast makeText(Context context, int resId, int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    /**
     * 隐藏Toast
     */
    public static void dismiss() {
        if (mToast == null || mToast.get() == null) {
            Log.i(TAG, "dismiss: toast is null");
            return;
        }

        Toast toast = mToast.get();
        toast.cancel();
    }
}
