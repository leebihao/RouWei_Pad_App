package com.lbh.rouwei.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lbh.rouwei.activity.MainActivity;
import com.lbh.rouwei.activity.SplashActivity;

/**
 * <pre>
 *     author : kentli
 *     e-mail : leebihao@outlook.com
 *     time   : 2020/08/24
 *     desc   : 开机广播接收者
 * </pre>
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {

            Log.d("lbh_boot", "onReceive: " + intent.getAction());
            Intent bootIntent = new Intent(context, MainActivity.class);
            bootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(bootIntent);
        }
    }
}
