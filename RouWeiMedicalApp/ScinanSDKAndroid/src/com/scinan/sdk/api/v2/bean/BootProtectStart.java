package com.scinan.sdk.api.v2.bean;

import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.scinan.sdk.config.Configuration;
import com.scinan.sdk.util.AndroidUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by lijunjie on 17/2/20.
 */

public class BootProtectStart implements Serializable {

    public String bootstartIntent;
    public String bootstartPkgNameExtra;
    public String bootstartPkgLabelExtra;
    public String protectstartIntent;
    public String protectstartPkgNameExtra;
    public String protectstartPkgLabelExtra;

    public String getBootstartIntent() {
        return bootstartIntent;
    }

    public void setBootstartIntent(String bootstartIntent) {
        this.bootstartIntent = bootstartIntent;
    }

    public String getBootstartPkgNameExtra() {
        return bootstartPkgNameExtra;
    }

    public void setBootstartPkgNameExtra(String bootstartPkgNameExtra) {
        this.bootstartPkgNameExtra = bootstartPkgNameExtra;
    }

    public String getBootstartPkgLabelExtra() {
        return bootstartPkgLabelExtra;
    }

    public void setBootstartPkgLabelExtra(String bootstartPkgLabelExtra) {
        this.bootstartPkgLabelExtra = bootstartPkgLabelExtra;
    }

    public String getProtectstartIntent() {
        return protectstartIntent;
    }

    public void setProtectstartIntent(String protectstartIntent) {
        this.protectstartIntent = protectstartIntent;
    }

    public String getProtectstartPkgNameExtra() {
        return protectstartPkgNameExtra;
    }

    public void setProtectstartPkgNameExtra(String protectstartPkgNameExtra) {
        this.protectstartPkgNameExtra = protectstartPkgNameExtra;
    }

    public String getProtectstartPkgLabelExtra() {
        return protectstartPkgLabelExtra;
    }

    public void setProtectstartPkgLabelExtra(String protectstartPkgLabelExtra) {
        this.protectstartPkgLabelExtra = protectstartPkgLabelExtra;
    }

    public Intent getBootStartIntent(String label) {
        Intent intent = null;
        if (!TextUtils.isEmpty(bootstartIntent)) {
            intent = new Intent();
            intent.setClassName(bootstartIntent.split("/")[0], bootstartIntent.split("/")[1]);
            if (!TextUtils.isEmpty(bootstartPkgNameExtra)) {
                intent.putExtra(bootstartPkgNameExtra, Configuration.getContext().getPackageName());
            }
            if (!TextUtils.isEmpty(bootstartPkgLabelExtra)) {
                intent.putExtra(bootstartPkgLabelExtra, label);
            }
        } else {
            intent = new Intent(Settings.ACTION_SETTINGS);
        }
        return intent;
    }

    public Intent getProtectStartIntent(String label) {
        Intent intent = null;
        if (!TextUtils.isEmpty(protectstartIntent)) {
            intent = new Intent();
            intent.setClassName(protectstartIntent.split("/")[0], protectstartIntent.split("/")[1]);
            if (!TextUtils.isEmpty(protectstartPkgNameExtra)) {
                intent.putExtra(protectstartPkgNameExtra, Configuration.getContext().getPackageName());
            }
            if (!TextUtils.isEmpty(protectstartPkgLabelExtra)) {
                intent.putExtra(protectstartPkgLabelExtra, label);
            }
        }  else {
            intent = new Intent(Settings.ACTION_SETTINGS);
        }
        return intent;
    }

    public static BootProtectStart parse(String jsonStr) {
        BootProtectStart bootProtectStart = new BootProtectStart();
        if (TextUtils.isEmpty(jsonStr)) {
            return bootProtectStart;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (!jsonObject.isNull("bootstart")) {
                JSONObject bootstart = jsonObject.getJSONObject("bootstart");

                if (!bootstart.isNull("intent")) {
                    bootProtectStart.setBootstartIntent(bootstart.getString("intent"));
                }

                if (!bootstart.isNull("pkg_name_extra")) {
                    bootProtectStart.setBootstartPkgNameExtra(bootstart.getString("pkg_name_extra"));
                }

                if (!bootstart.isNull("pkg_label_extra")) {
                    bootProtectStart.setBootstartPkgLabelExtra(bootstart.getString("pkg_label_extra"));
                }
            }

            if (!jsonObject.isNull("protectstart")) {
                JSONObject protectstart = jsonObject.getJSONObject("protectstart");

                if (!protectstart.isNull("intent")) {
                    bootProtectStart.setProtectstartIntent(protectstart.getString("intent"));
                }

                if (!protectstart.isNull("pkg_name_extra")) {
                    bootProtectStart.setProtectstartPkgNameExtra(protectstart.getString("pkg_name_extra"));
                }

                if (!protectstart.isNull("pkg_label_extra")) {
                    bootProtectStart.setProtectstartPkgLabelExtra(protectstart.getString("pkg_label_extra"));
                }
            }

            return bootProtectStart;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bootProtectStart;
    }
}
