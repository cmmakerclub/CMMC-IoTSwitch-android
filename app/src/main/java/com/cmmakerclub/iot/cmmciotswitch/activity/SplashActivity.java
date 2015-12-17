package com.cmmakerclub.iot.cmmciotswitch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends BaseActivity {
    Handler handler;
    Runnable runnable;
    Long delay_time;
    Long time = 1000L;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        // Hide the Title bar of this activity screen
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}
