package com.kukitriplan.smartquizapp.skripsi.utils;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public final class SnackBarUtils {

    public static void SnackBarUtils(View view, String text, int duration) {
        final Snackbar snackbar = Snackbar.make(view, text, duration);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 145);
        View sbView = snackbar.getView();
        snackbar.getView().setLayoutParams(params);
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(12);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public static void Hide(View view) {
        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        snackbar.dismiss();
    }
}
