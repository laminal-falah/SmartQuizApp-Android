package com.kukitriplan.smartquizapp.ui.auth;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.dashboard.DashboardActivity;
import com.kukitriplan.smartquizapp.ui.home.HomeActivity;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
import com.kukitriplan.smartquizapp.utils.SetOrientationUtils;

public class AuthActivity extends AppCompatActivity implements AuthFragment, LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener, ForgetFragment.OnFragmentInteractionListener {

    private static final String TAG = AuthActivity.class.getSimpleName();
    private static long back_pressed;
    private KeyboardUtils keyboardUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetOrientationUtils.SetTitle(this);
        setContentView(R.layout.activity_auth);
        SharedLoginManager prefManager = new SharedLoginManager(this);

        if (prefManager.getSpLogon() && prefManager.getSpToken() != null) {
            switch (prefManager.getSpLevel()) {
                case "author":
                    startActivity(new Intent(this, DashboardActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                    break;
                case "user":
                    startActivity(new Intent(this, HomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                    break;
                case "admin":
                    prefManager.clearShared();
                    PopupUtils.PopAdmin(this);
                    break;
                default:
                    finish();
                    break;
            }
        }
        keyboardUtils = new KeyboardUtils();
        keyboardUtils.hideSoftKeyboard(this);
        fragmentLogin();
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
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
    public void onBackPressed() {
        if (back_pressed + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.txtExitPress), Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.i(TAG, "onFragmentInteraction: " + uri);
    }

    @Override
    public void fragmentLogin() {
        Auth(new LoginFragment());
    }

    @Override
    public void fragemntRegister() {
        Auth(new RegisterFragment());
    }

    @Override
    public void fragmentForget() {
        Auth(new ForgetFragment());
    }

    @Override
    public void login(View view) {
        fragmentLogin();
    }

    @Override
    public void register(View view) {
        fragemntRegister();
    }

    @Override
    public void forget(View view) {
        fragmentForget();
    }

    private void Auth(@NonNull final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment, fragment.getClass().getSimpleName())
                .commit();
        keyboardUtils.setupUI(findViewById(R.id.frame_container), this);
    }
}
