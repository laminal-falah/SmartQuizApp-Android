package com.kukitriplan.smartquizapp.skripsi.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverBroadcastServices extends BroadcastReceiver {
    private static final String TAG = ReceiverBroadcastServices.class.getSimpleName();
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: Services Stops !");
        context.startService(new Intent(context, ServiceApps.class));
    }
}
