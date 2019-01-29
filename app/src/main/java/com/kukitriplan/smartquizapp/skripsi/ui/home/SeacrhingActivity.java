package com.kukitriplan.smartquizapp.skripsi.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.ListKuisCariAdapter;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.HomeJson;
import com.kukitriplan.smartquizapp.skripsi.data.model.Kuis;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SetOrientationUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeacrhingActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = SeacrhingActivity.class.getSimpleName();

    private View view;
    private Toolbar toolbar;
    private SharedLoginManager prefManager;
    @BindView(R.id.rv_list_cari_kuis) RecyclerView rvListKuis;
    @BindView(R.id.tvError) TextView tvError;

    private ArrayList<Kuis> kuis;
    private ListKuisCariAdapter listKuisCariAdapter;

    private ApiServices services;
    private Call<HomeResponse> call;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetOrientationUtils.SetTitle(this);
        setContentView(R.layout.activity_seacrhing);
        ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar_user);
        toolbar.setTitle(R.string.txtSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        prefManager = new SharedLoginManager(this);
        keyboardUtils = new KeyboardUtils();
        progressUtils = new ProgressUtils(this);

        RecyclerView.LayoutManager lm = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        rvListKuis.setLayoutManager(lm);
        rvListKuis.setItemAnimator(new DefaultItemAnimator());
        rvListKuis.setHasFixedSize(true);
        services = RetrofitBuilder.createServices(ApiServices.class);
        progressUtils.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(getString(R.string.txtTypeSearch));
        searchView.setIconified(true);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        cariKuis(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    private void cariKuis(final String keywords) {
        keyboardUtils.hideSoftKeyboard(this);
        //keyboardUtils.setupUI(this.view, this);

        progressUtils.show();
        call = services.getSearchKuis(prefManager.getSpToken(), "home","cariKuis",keywords);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    if (json.getKode().equals("1")) {
                        tvError.setVisibility(View.INVISIBLE);
                        rvListKuis.setVisibility(View.VISIBLE);
                        kuis = new ArrayList<>(Arrays.asList(json.getKuisCari()));
                        List<Kuis> kuisList = new ArrayList<>();
                        for (int i = 0; i < kuis.size(); i++) {
                            kuisList.add(new Kuis(
                                    kuis.get(i).getId_kuis(),
                                    kuis.get(i).getJudul(),
                                    kuis.get(i).getSlug(),
                                    kuis.get(i).getSoal(),
                                    kuis.get(i).getDurasi(),
                                    kuis.get(i).getHarga(),
                                    kuis.get(i).getCover(),
                                    kuis.get(i).getAuthor(),
                                    kuis.get(i).getRating()
                            ));
                        }
                        listKuisCariAdapter = new ListKuisCariAdapter(kuis, getApplicationContext());
                        rvListKuis.setAdapter(listKuisCariAdapter);
                        new GravitySnapHelper(Gravity.TOP).attachToRecyclerView(rvListKuis);
                        listKuisCariAdapter.notifyDataSetChanged();
                        progressUtils.hide();
                    } else if (json.getKode().equals("2")) {
                        prefManager.clearShared();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        progressUtils.hide();
                        rvListKuis.setVisibility(View.INVISIBLE);
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(json.getMessage());
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    SnackBarUtils.SnackBarUtils(view, "502", Snackbar.LENGTH_SHORT);
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    SnackBarUtils.SnackBarUtils(view, "404", Snackbar.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                progressUtils.hide();
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
    }
}
