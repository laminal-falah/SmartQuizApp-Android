package com.kukitriplan.smartquizapp.skripsi.ui.home;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.ui.barcode.ScanBarcodeActivity;
import com.kukitriplan.smartquizapp.skripsi.ui.dashboard.navigation.ListKuisFragment;
import com.kukitriplan.smartquizapp.skripsi.ui.dashboard.navigation.ProfileFragment;
import com.kukitriplan.smartquizapp.skripsi.ui.home.navigation.FeedbackFragment;
import com.kukitriplan.smartquizapp.skripsi.ui.home.navigation.HomeFragment;
import com.kukitriplan.smartquizapp.skripsi.ui.home.navigation.TopUpFragment;
import com.kukitriplan.smartquizapp.skripsi.ui.home.navigation.UserFragment;
import com.kukitriplan.smartquizapp.skripsi.utils.PopupUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SetOrientationUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SnackBarUtils;

import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeInterface, HomeFragment.OnFragmentInteractionListener,
        TopUpFragment.OnFragmentInteractionListener, FeedbackFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, ListKuisFragment.OnFragmentInteractionListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static long back_pressed;

    private Toolbar toolbar;
    private BottomNavigationView navigation;

    private SharedLoginManager prefManager;
    private String TITLE_NOTIFICATION = "TITLE_NOTIFICATION";
    private String MESSAGE_NOTIFICATION = "MESSAGE_NOTIFICATION";
    private static String saldo = "SALDO";
    private int counter = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    toolbar.setTitle(R.string.title_home);
                    home();
                    return true;
                case R.id.navigation_topup:
                    toolbar.setTitle(R.string.title_top_up);
                    topup();
                    return true;
                case R.id.navigation_feedback:
                    toolbar.setTitle(R.string.title_feedback);
                    feedback();
                    return true;
                case R.id.navigation_user:
                    toolbar.setTitle(R.string.title_account);
                    user();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetOrientationUtils.SetTitle(this);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        prefManager = new SharedLoginManager(this);
        if (!prefManager.getSpLogon()) {
            startActivity(new Intent(this, AuthActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.toolbar_user);
        toolbar.setTitle(R.string.title_home);
        setSupportActionBar(toolbar);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        saldo = getResources().getString(R.string.txtSaldo, prefManager.getSpSaldo());
        //home();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user, menu);
        menu.getItem(0).setTitle(saldo);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_saldo) {
            //Toast.makeText(this, "Jumlah Saldo " + getResources().getString(R.string.txtSaldo, prefManager.getSpSaldo()), Toast.LENGTH_SHORT).show();
            SnackBarUtils.SnackBarUtils(findViewById(android.R.id.content), "Jumlah Saldo " + getResources().getString(R.string.txtSaldo, prefManager.getSpSaldo()), Snackbar.LENGTH_LONG);
        } else if (id == R.id.menu_scan) {
            startActivity(new Intent(this, ScanBarcodeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (id == R.id.menu_cari) {
            startActivity(new Intent(this, SeacrhingActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (id == R.id.menu_notif) {
            startActivity(new Intent(this, NotificationActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        } else if (id == R.id.menu_info) {
            PopupUtils.loadAbout(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        home();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
                Log.d(TAG, "ON RESULT CALLED");
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        counter = counter++;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.txtExitPress), Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        counter = 0;
    }

    @Override
    public void home() {
        getFragment(new HomeFragment());
        counter++;
    }

    @Override
    public void topup() {
        getFragment(new TopUpFragment());
    }

    @Override
    public void feedback() {
        getFragment(new FeedbackFragment());
    }

    @Override
    public void user() {
        getFragment(new UserFragment());
    }

    private void getFragment(@NonNull final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_user, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(TAG, "onFragmentInteraction: " + uri);
    }
}
