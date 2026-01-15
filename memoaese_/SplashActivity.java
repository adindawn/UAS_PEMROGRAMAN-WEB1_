package com.example.memoaese_; // Ganti dengan package name Anda

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 2000; // 2 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Setelah timer selesai, mulai MainActivity
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // Tutup activity ini
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}