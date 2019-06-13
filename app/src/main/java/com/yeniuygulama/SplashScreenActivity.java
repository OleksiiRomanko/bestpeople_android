package com.yeniuygulama;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String token = status.getSubscriptionStatus().getUserId();
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreenActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        timer.start();
    }
}