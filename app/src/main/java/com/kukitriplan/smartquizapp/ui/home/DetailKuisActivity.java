package com.kukitriplan.smartquizapp.ui.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.HomeJson;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.utils.SnackBarUtils;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailKuisActivity extends AppCompatActivity {

    private static final String TAG = DetailKuisActivity.class.getSimpleName();
    private Toolbar toolbar;
    private View view;
    private CardView cvDetailKuis;
    private TextView tvTitle, tvHrg, tvSoal, tvDurasi, tvAuthor, tvDeskripsi;
    private ImageView ivCover;
    private AppCompatRatingBar ratingBar;
    private Button btnPlay;
    private SharedLoginManager prefManager;
    private ProgressUtils progressUtils;
    private ApiServices services;
    private Call<HomeResponse> call;
    private String idKategori, nmKategori, title;

    private void getKuis() {
        call = services.getDetailKuis(prefManager.getSpToken(),"home","detailKuis", getIntent().getStringExtra("slug"));
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    if (json.getKode().equals("1")) {
                        title = json.getKuis().getJudul();
                        tvTitle.setText(json.getKuis().getJudul());
                        tvHrg.setText(json.getKuis().getHarga());
                        tvDurasi.setText(getResources().getString(R.string.txtDurasiKuis, json.getKuis().getDurasi()));
                        tvSoal.setText(getResources().getString(R.string.txtSoalKuis, json.getKuis().getSoal()));
                        tvAuthor.setText(json.getKuis().getAuthor());
                        tvDeskripsi.setText(json.getKuis().getDeskripsi());
                        ratingBar.setEnabled(false);
                        ratingBar.setRating(json.getKuis().getRating());
                        Picasso.with(getApplicationContext()).load(json.getKuis().getCover()).fit().into(ivCover);
                        cvDetailKuis.setVisibility(View.VISIBLE);
                        progressUtils.hide();
                    } else {
                        cvDetailKuis.setVisibility(View.INVISIBLE);
                        progressUtils.hide();
                        SnackBarUtils.SnackBarUtils(view, json.getMessage(), Snackbar.LENGTH_LONG);
                    }
                } else if (response.code() == 502) {
                    cvDetailKuis.setVisibility(View.INVISIBLE);
                    progressUtils.hide();
                    SnackBarUtils.SnackBarUtils(view, "502", Snackbar.LENGTH_LONG);
                } else if (response.code() == 404) {
                    cvDetailKuis.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_detail_kuis);
        view = findViewById(android.R.id.content);
        cvDetailKuis = findViewById(R.id.cv_detail_kuis);
        cvDetailKuis.setVisibility(View.INVISIBLE);
        toolbar = findViewById(R.id.toolbar_user);
        if (getIntent().getStringExtra("nama") != null) {
            toolbar.setTitle(getIntent().getStringExtra("nama"));
        } else {
            toolbar.setTitle(title);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        idKategori = getIntent().getStringExtra("id");
        nmKategori = getIntent().getStringExtra("kategori");
        switch (getIntent().getStringExtra("tipe")) {
            case "home":
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
                break;
            case "kategori":
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), KategoriActivity.class)
                                .putExtra("id", idKategori)
                                .putExtra("nama", nmKategori)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
                break;
            case "cari":
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), SeacrhingActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
                break;
            default:
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
                break;
        }
        services = RetrofitBuilder.createServices(ApiServices.class);
        prefManager = new SharedLoginManager(this);
        progressUtils = new ProgressUtils(this);
        progressUtils.show();

        tvTitle = findViewById(R.id.tv_title_detail_kuis);
        tvHrg = findViewById(R.id.tv_harga_detail_kuis);
        tvSoal = findViewById(R.id.tv_soal_detail_kuis);
        tvDurasi = findViewById(R.id.tv_durasi_detail_kuis);
        ivCover = findViewById(R.id.iv_cover_detail_kuis);
        ratingBar = findViewById(R.id.ratingBarDetail);
        tvAuthor = findViewById(R.id.tv_author_detail_kuis);
        tvDeskripsi = findViewById(R.id.tv_deskripsi_detail_kuis);
        btnPlay = findViewById(R.id.btnMainKuis);

        getKuis();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: " + getIntent().getStringExtra("slug"));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        switch (getIntent().getStringExtra("tipe")) {
            case "home":
                startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            case "kategori":
                startActivity(new Intent(getApplicationContext(), KategoriActivity.class)
                        .putExtra("id", idKategori)
                        .putExtra("nama", nmKategori)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
            default:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                break;
        }
    }
}
