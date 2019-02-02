package com.kukitriplan.smartquizapp.skripsi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.NotificationAdapter;

public final class PopupUtils {

    private static Activity activity;
    private static Context context;

    public PopupUtils(@NonNull Activity activity) {
        this.activity = activity;
    }

    public PopupUtils(@NonNull Activity activity, @NonNull Context context) {
        this.activity = activity;
        this.context = context;
    }

    public static void PopAdmin(@NonNull final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Warning !");
        builder.setCancelable(false);
        builder.setMessage("Please Login to website");
        builder.setPositiveButton("Yes, Open Browser", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(ConfigUtils.BASE_URL[0]))
                        /*.setClassName(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(ConfigUtils.BASE_URL)).resolveActivityInfo(activity.getPackageManager(),0).packageName,
                                new Intent(Intent.ACTION_VIEW, Uri.parse(ConfigUtils.BASE_URL)).resolveActivityInfo(activity.getPackageManager(),0).name
                        )*/
                );
                activity.finish();

            }
        });
        builder.setNegativeButton("Exit App", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.finish();
            }
        });
        builder.create();
        builder.show();
    }

    public static void PopAuth(@NonNull final Context context, @NonNull String title, @NonNull String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public static void loadAbout(@NonNull final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("About " + activity.getResources().getString(R.string.app_name));
        builder.setCancelable(false);
        builder.setMessage("Version " + activity.getResources().getString(R.string.app_version));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public static void loadError(@NonNull final Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    public static void popTopUp(@NonNull final Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
