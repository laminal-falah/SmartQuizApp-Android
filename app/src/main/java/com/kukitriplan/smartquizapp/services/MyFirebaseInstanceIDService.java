package com.kukitriplan.smartquizapp.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kukitriplan.smartquizapp.data.shared.SharedPrefFirebase;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //calling the method store token and passing token
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        //we will save the token in sharedpreferences later
        //SharedPrefFirebase.getInstance(getApplicationContext()).saveDeviceToken(token);
    }
}
