package com.kukitriplan.smartquizapp.skripsi.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.NotificationAdapter;
import com.kukitriplan.smartquizapp.skripsi.data.db.NotificationsHelper;
import com.kukitriplan.smartquizapp.skripsi.data.model.Notifications;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.dashboard.DashboardActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SetOrientationUtils;

import java.sql.SQLException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_user) Toolbar toolbar;
    @BindView(R.id.rvNotif) RecyclerView rvNotif;
    @BindView(R.id.tvError) TextView tvError;

    private ProgressUtils progressUtils;

    private SharedLoginManager prefManager;

    private ArrayList<Notifications> notifications;
    private NotificationAdapter mAdapter;
    private NotificationsHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetOrientationUtils.SetTitle(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        toolbar.setTitle("Notification");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvError.setVisibility(View.INVISIBLE);
        progressUtils = new ProgressUtils(this);

        rvNotif.setLayoutManager(new LinearLayoutManager(this));
        rvNotif.setItemAnimator(new DefaultItemAnimator());
        rvNotif.setHasFixedSize(true);
        rvNotif.getLayoutManager().smoothScrollToPosition(rvNotif, new RecyclerView.State(), 0);

        mHelper = new NotificationsHelper(this);
        try {
            mHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notifications = new ArrayList<>();

        mAdapter = new NotificationAdapter(this);
        mAdapter.setNotifications(notifications);

        rvNotif.setAdapter(mAdapter);

        new LoadNotificationAsync().execute();

        prefManager = new SharedLoginManager(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (prefManager.getSpLevel().equals("user")) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.close();
        }
    }

    private class LoadNotificationAsync extends AsyncTask<Void, Void, ArrayList<Notifications>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressUtils.show();
            if (notifications.size() > 0) {
                notifications.clear();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Notifications> list) {
            super.onPostExecute(list);
            progressUtils.hide();
            notifications.addAll(list);
            mAdapter.setNotifications(list);
            mAdapter.notifyDataSetChanged();
            Log.i("TAG", "onPostExecute: ");
            if (list.size() == 0) {
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("Tidak Ada Pemberitahuan Saat Ini");
            }
        }

        @Override
        protected ArrayList<Notifications> doInBackground(Void... voids) {
            Log.i("TAG", "doInBackground: " + mHelper.query());
            return mHelper.query();
        }
    }

    private void showSnackbarMessage(String message) {
        Snackbar.make(rvNotif, message, Snackbar.LENGTH_SHORT).show();
    }
}
