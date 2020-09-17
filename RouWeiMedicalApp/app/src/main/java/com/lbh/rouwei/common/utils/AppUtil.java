package com.lbh.rouwei.common.utils;

import android.content.Context;
import android.content.Intent;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/09/12
 *     desc   :
 * </pre>
 */
public class AppUtil {
    public static void goAndroidHome(Context mContext) {
        Intent mIntent = new Intent(Intent.ACTION_MAIN);
        mIntent.addCategory(Intent.CATEGORY_HOME);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(mIntent);
    }
}
