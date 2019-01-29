package com.kukitriplan.smartquizapp.ui.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.AuthJson;
import com.kukitriplan.smartquizapp.data.response.AuthResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.ui.dashboard.navigation.BuatKuisFragment;
import com.kukitriplan.smartquizapp.ui.dashboard.navigation.DashboardFragment;
import com.kukitriplan.smartquizapp.ui.dashboard.navigation.ListKuisFragment;
import com.kukitriplan.smartquizapp.ui.dashboard.navigation.PenjualanFragment;
import com.kukitriplan.smartquizapp.ui.dashboard.navigation.ProfileFragment;
import com.kukitriplan.smartquizapp.ui.dashboard.navigation.WithDrawFragment;
import com.kukitriplan.smartquizapp.ui.home.navigation.FeedbackFragment;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FeedbackFragment.OnFragmentInteractionListener,
        ListKuisFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, BuatKuisFragment.OnFragmentInteractionListener,
        PenjualanFragment.OnFragmentInteractionListener {

    private Toolbar toolbar;
    private ApiServices services;
    private Call<AuthResponse> callLogout;

    private SharedLoginManager prefManager;
    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private TextView tvNamaAuthor,tvEmailAuthor;
    private NavigationView navigationView;

    private static long back_pressed;

    private int onStack = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        progressUtils = new ProgressUtils(this);
        keyboardUtils = new KeyboardUtils();
        prefManager = new SharedLoginManager(this);
        services = RetrofitBuilder.createServices(ApiServices.class);

        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.title_dashboard, prefManager.getSpName()));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View viewHeader = navigationView.getHeaderView(0);
        tvNamaAuthor = viewHeader.findViewById(R.id.tvNamaAuthor);
        tvEmailAuthor = viewHeader.findViewById(R.id.tvEmailAuthor);
        tvNamaAuthor.setText(prefManager.getSpName());
        tvEmailAuthor.setText(prefManager.getSpEmail());

        navigationView.getMenu().getItem(0).setChecked(true);
        loadFragment(new DashboardFragment());
        progressUtils.hide();

        if (!prefManager.getSpLogon()) {
            startActivity(new Intent(this, AuthActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (back_pressed + 3000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.txtExitPress), Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            logout();
        } else if (id == R.id.nav_about) {
            PopupUtils.loadAbout(this);
        } else if (id == R.id.nav_feedback) {
            toolbar.setTitle(getResources().getString(R.string.title_feedback));
            loadFragment(new FeedbackFragment());
        } else if (id == R.id.nav_profile) {
            toolbar.setTitle(getResources().getString(R.string.title_profile));
            loadFragment(new ProfileFragment());
        } else if (id == R.id.nav_list_kuis) {
            toolbar.setTitle(getResources().getString(R.string.title_list_kuis));
            loadFragment(new ListKuisFragment());
        } else if (id == R.id.nav_dashboard) {
            toolbar.setTitle(getResources().getString(R.string.title_dashboard, prefManager.getSpName()));
            loadFragment(new DashboardFragment());
        } else if (id == R.id.nav_buat_kuis) {
            toolbar.setTitle("Create Quiz");
            loadFragment(new BuatKuisFragment());
        } else if (id == R.id.nav_with_draw) {
            toolbar.setTitle(getResources().getString(R.string.title_with_draw));
            loadFragment(new WithDrawFragment());
        } else if (id == R.id.nav_list_jual) {
            toolbar.setTitle(getResources().getString(R.string.title_list_sold));
            loadFragment(new PenjualanFragment());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        callLogout = services.logout(prefManager.getSpToken());
        callLogout.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        AuthResponse res = response.body();
                        AuthJson json = res.getAuth();
                        if (json.getKode().equals("1")) {
                            prefManager.clearShared();
                            progressUtils.hide();
                            startActivity(new Intent(DashboardActivity.this, AuthActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        progressUtils.hide();
                    } catch (Exception e) {
                        progressUtils.hide();
                        PopupUtils.loadError(getApplicationContext(), "Error", e.getMessage());
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    PopupUtils.loadError(getApplicationContext(), "Error", "502");
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    PopupUtils.loadError(getApplicationContext(), "Error", "404");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                progressUtils.hide();
                PopupUtils.loadError(getApplicationContext(), "Error", t.getMessage());
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_dashboard, fragment, fragment.getClass().getSimpleName())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
