package com.kukitriplan.smartquizapp.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.adapter.ListKuisAdapter;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.HomeJson;
import com.kukitriplan.smartquizapp.data.model.Kuis;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.utils.SnackBarUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KategoriActivity extends AppCompatActivity {

    private static final String TAG = KategoriActivity.class.getSimpleName();

    private Toolbar toolbar;
    private RecyclerView rvKuis;
    private TextView tvKategori, tvjumlah;
    private SharedLoginManager prefManager;
    private ProgressUtils progressUtils;
    private View view;
    private ApiServices services;
    private Call<HomeResponse> call;

    private ArrayList<Kuis> kuis;

    private ListKuisAdapter listKuisAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_kategori);
        view = findViewById(android.R.id.content);
        progressUtils = new ProgressUtils(this);
        toolbar = findViewById(R.id.toolbar_user);
        toolbar.setTitle(getIntent().getStringExtra("nama"));
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
        rvKuis = findViewById(R.id.rv_list_kuis_kategori);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);
        rvKuis.setLayoutManager(layoutManager);
        rvKuis.setItemAnimator(new DefaultItemAnimator());
        rvKuis.setHasFixedSize(true);

        tvKategori = findViewById(R.id.tvTitleKategori);
        tvjumlah = findViewById(R.id.tvJumlahData);

        services = RetrofitBuilder.createServices(ApiServices.class);
        progressUtils.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getKuis();
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
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }

    private void getKuis() {
        call = services.getKuis(prefManager.getSpToken(),"home","kategori", getIntent().getStringExtra("id"));
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        HomeResponse res = response.body();
                        HomeJson json = res.getHome();
                        if (json.getKode().equals("1")) {
                            tvKategori.setVisibility(View.VISIBLE);
                            tvjumlah.setVisibility(View.VISIBLE);
                            tvKategori.setText(getResources().getString(R.string.txtTitleKategori, json.getTitle()));
                            tvjumlah.setText(getResources().getString(R.string.txtJumlahData, json.getMessage()));
                            kuis = new ArrayList<>(Arrays.asList(json.getKuisKategori()));
                            List<Kuis> kuisList = new ArrayList<>();
                            for (int i = 0; i < kuis.size(); i++) {
                                kuisList.add(new Kuis(
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
                            listKuisAdapter = new ListKuisAdapter(kuis, getApplicationContext());
                            rvKuis.setAdapter(listKuisAdapter);
                            new GravitySnapHelper(Gravity.TOP).attachToRecyclerView(rvKuis);
                            listKuisAdapter.notifyDataSetChanged();
                            progressUtils.hide();
                        } else if (json.getKode().equals("2")) {
                            prefManager.clearShared();
                            startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            progressUtils.hide();
                            SnackBarUtils.SnackBarUtils(view, json.getMessage(), Snackbar.LENGTH_LONG);
                        }
                    } catch (Exception e) {
                        progressUtils.hide();
                        SnackBarUtils.SnackBarUtils(view, e.getMessage(), Snackbar.LENGTH_LONG);
                    }
                } else if (response.code() == 503) {
                    progressUtils.hide();
                    SnackBarUtils.SnackBarUtils(view, "503", Snackbar.LENGTH_LONG);
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    SnackBarUtils.SnackBarUtils(view, "404", Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                progressUtils.hide();
                SnackBarUtils.SnackBarUtils(view, t.getMessage(), Snackbar.LENGTH_LONG);
            }
        });
    }

}
