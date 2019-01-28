package com.kukitriplan.smartquizapp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.stetho.Stetho;
import com.google.firebase.FirebaseApp;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseApp.initializeApp(this);
        AppEventsLogger.activateApp(getApplicationContext());
        Stetho.initializeWithDefaults(getApplicationContext());
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/action-man")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
