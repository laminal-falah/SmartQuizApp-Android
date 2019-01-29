package com.kukitriplan.smartquizapp.skripsi.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class ServiceApps extends Service {
    private static final String TAG = ServiceApps.class.getSimpleName();

    private int counter = 0;
    private Context context;
    private Timer timer;
    private TimerTask timerTask;

    public ServiceApps(Context context) {
        super();
        this.context = context;
        Log.i(TAG, "ServiceApps: Here Service created");
    }

    public ServiceApps() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: EXIT");
        sendBroadcast(new Intent("com.kukitriplan.smartquizapp.ActivityRecognition.RestartSensor"));
        stopTimerTask();
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask,1000,1000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //Log.i(TAG, "run: in timer ++++" + (counter++));
            }
        };
    }

    public void stopTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
