package com.kukitriplan.smartquizapp.skripsi.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.data.db.NotificationsHelper;
import com.kukitriplan.smartquizapp.skripsi.services.ServiceApps;
import com.kukitriplan.smartquizapp.skripsi.utils.SetOrientationUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.logo) ImageView imageView;
    @BindView(R.id.spin_kit) SpinKitView spinKitView;

    final String[] appPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSIONS_REQUEST_CODE = 1024;

    ServiceApps serviceApps = new ServiceApps(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetOrientationUtils.SetFull(this);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        if (checkAndRequestPermission()) {
            if (!isMyServiceRunning(serviceApps.getClass())) {
                startService(new Intent(getApplicationContext(),serviceApps.getClass()));
            }
            splash();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        imageView.animate();
        spinKitView.onSaveInstanceState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        newConfig.describeContents();
    }

    private void splash() {
        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } finally {
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            }
        };
        timer.start();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    Log.i ("isMyServiceRunning?", true+"");
                    return true;
                }
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    private boolean checkAndRequestPermission() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String perm : appPermissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(perm);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            HashMap<String, Integer> permissionResults = new HashMap<>();
            int deniedCount = 0;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResults.put(permissions[i], grantResults[i]);
                    deniedCount++;
                }
            }

            if (deniedCount == 0) {
                splash();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResults.entrySet()) {
                    String permName = entry.getKey();
                    int permResult = entry.getValue();
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permName)) {
                        showDialog("Need Permission", "This App needs permission to work without and problems.", "Yes, Grant Permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        checkAndRequestPermission();
                                    }
                                },
                                "No, Exit App", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, false);
                    } else {
                        showDialog("Need Permissions", "You have denied some permissions. Allow all permissions at [Setting] > [Apps Or Permissions]", "Go to Settings", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null))
                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();
                                    }
                                },
                                "No, Exit App", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }, false);
                        break;
                    }
                }
            }
        }
    }

    private void showDialog(String title, String msg, String positiveLabel, DialogInterface.OnClickListener positiveClick,
                            String negativeLabel, DialogInterface.OnClickListener negativeClick, boolean isCancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(isCancelable);
        builder.setMessage(msg);
        builder.setPositiveButton(positiveLabel, positiveClick);
        builder.setNegativeButton(negativeLabel, negativeClick);
        builder.create();
        builder.show();
    }
}
