package com.project.hackathon.motorola.bluetoothexample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            /*
             * Exibindo splash com um timer.
             */
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, bleActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }
}
