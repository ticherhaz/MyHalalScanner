package com.hafis.myhalalscanner.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hafis.myhalalscanner.MainActivity;
import com.hafis.myhalalscanner.R;

import static com.hafis.myhalalscanner.tool.Utils.CreateDelay;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        CreateDelay(2000, () -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        });
    }
}