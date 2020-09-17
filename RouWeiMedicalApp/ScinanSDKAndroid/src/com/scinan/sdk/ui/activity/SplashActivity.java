/*
 * Copyright (c) 2017 Guangdong Scinan IoT, Inc.
 *
 * This software is the property of Guangdong Scinan IoT, Inc.
 * You have to accept the terms in the license file before use.
 */

package com.scinan.sdk.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import com.scinan.sdk.R;

/**
 * Created by wright on 15/7/6.
 */
public abstract class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setTextView((TextView) findViewById(R.id.textView));
        setImageView((ImageView) findViewById(R.id.splashImage));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splash2Activity();
            }
        }, 1500);
    }

    public abstract void setImageView(ImageView imageView);
    public abstract void setTextView(TextView textView);

    public abstract void splash2Activity();
}
