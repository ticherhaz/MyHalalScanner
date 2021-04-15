package com.hafis.myhalalscanner;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.appizona.yehiahd.fastsave.FastSave;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FastSave.init(getApplicationContext());
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
