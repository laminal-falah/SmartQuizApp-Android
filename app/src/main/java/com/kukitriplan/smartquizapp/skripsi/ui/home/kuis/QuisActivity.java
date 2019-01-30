package com.kukitriplan.smartquizapp.skripsi.ui.home.kuis;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.ListJawabSoalAdapter;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.HomeJson;
import com.kukitriplan.smartquizapp.skripsi.data.model.Soal;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuisActivity extends AppCompatActivity implements ResultFragment.OnFragmentInteractionListener {

    private static final String TAG = QuisActivity.class.getSimpleName();

    @BindView(R.id.toolbar_user) Toolbar toolbar;
    @BindView(R.id.rvListSoal) RecyclerView rvListSoal;
    @BindView(R.id.fabJawab) FloatingActionButton fabJawab;
    @BindView(R.id.csLayout) ConstraintLayout csLayout;
    @BindView(R.id.tvCountTimer) TextView tvCounter;

    private SharedLoginManager prefManager;
    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private ApiServices services;
    private Call<HomeResponse> call;

    private ArrayList<Soal> soals;
    private List<Soal> soalList;
    private ListJawabSoalAdapter soalAdapter;

    private RadioButton radioButton;

    private CountDownTimer mCountDownTimer;
    int durasi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SetOrientationUtils.SetFull(this);
        setContentView(R.layout.activity_quis);
        ButterKnife.bind(this);
        toolbar.setTitle(getIntent().getStringExtra("judul"));
        setSupportActionBar(toolbar);

        prefManager = new SharedLoginManager(this);
        progressUtils = new ProgressUtils(this);
        keyboardUtils = new KeyboardUtils();
        services = RetrofitBuilder.createServices(ApiServices.class);

        DividerItemDecoration decoration = new DividerItemDecoration(rvListSoal.getContext(), RecyclerView.VERTICAL);
        rvListSoal.addItemDecoration(decoration);
        rvListSoal.setLayoutManager(new LinearLayoutManager(this));
        rvListSoal.setItemAnimator(new DefaultItemAnimator());
        rvListSoal.setHasFixedSize(true);

        getSoal();
    }

    private void getSoal() {
        call = services.tampilSoal(prefManager.getSpToken(), "home", "tampilSoal", prefManager.getSpEmail(), getIntent().getStringExtra("id"));
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    if (json.getKode().equals("1")) {
                        prefManager.saveSPString(SharedLoginManager.SP_IDKUIS, json.getKuis().getId_kuis());
                        mCountDownTimer = new CountDownTimer(json.getDurasi(), 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                tvCounter.setText(getResources().getString(R.string.txtCountTimer,
                                        String.format(Locale.getDefault(),"%02d",millisUntilFinished / (60 * 60 * 1000) % 24),
                                        String.format(Locale.getDefault(),"%02d",millisUntilFinished / (60 * 1000) % 60),
                                        String.format(Locale.getDefault(),"%02d",millisUntilFinished / 1000 % 60)));
                            }

                            @Override
                            public void onFinish() {
                                jawabSoal();
                            }
                        }.start();
                        soals = new ArrayList<>(Arrays.asList(json.getSoal()));
                        soalList = new ArrayList<>();
                        for (int i = 0; i < soals.size(); i++) {
                            soalList.add(new Soal(
                                    soals.get(i).getIdKuis(),
                                    soals.get(i).getIdSoal(),
                                    soals.get(i).getJudulSoal(),
                                    soals.get(i).getA(),
                                    soals.get(i).getB(),
                                    soals.get(i).getC(),
                                    soals.get(i).getD(),
                                    soals.get(i).getE(),
                                    soals.get(i).getKunciJawaban(),
                                    soals.get(i).getPembahasan()
                            ));
                        }
                        soalAdapter = new ListJawabSoalAdapter(getApplicationContext(), soals);
                        soalAdapter.notifyDataSetChanged();
                        rvListSoal.setAdapter(soalAdapter);
                        progressUtils.hide();
                        soalAdapter.SetOnItemClickListener(new ListJawabSoalAdapter.setOnItemSelector() {
                            @Override
                            public void onItemClick(String a, String b, String c) {
                                submitJawaban(a, b, c);
                            }
                        });
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        progressUtils.hide();
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void submitJawaban(String idKuis, String idSoal, String jawaban) {
        progressUtils.show();
        Map<String, String> params = new HashMap<>();
        params.put("idKuis", idKuis);
        params.put("idSoal", idSoal);
        params.put("pilihan", jawaban);
        call = services.submitJawaban(prefManager.getSpToken(), "home", "submitJawaban", prefManager.getSpEmail(), params);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    if (json.getKode().equals("1")) {
                        progressUtils.hide();
                    } else {
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.fabJawab) void jawabSoal() {
        progressUtils.show();
        call = services.selesaiKuis(prefManager.getSpToken(), "home", "selesaiKuis", prefManager.getSpEmail(), prefManager.getSpIdkuis());
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    if (json.getKode().equals("1")) {
                        progressUtils.hide();
                        csLayout.setVisibility(View.INVISIBLE);
                        Bundle bundle = new Bundle();
                        bundle.putDouble("NILAI", json.getNilai());
                        bundle.putString("IDKUIS", json.getKuis().getId_kuis());
                        ResultFragment resultFragment = new ResultFragment();
                        resultFragment.setArguments(bundle);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame_main_kuis, resultFragment)
                                .commit();
                    } else {
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Toast.makeText(getApplicationContext(), "Disabled Clicked Home", Toast.LENGTH_LONG).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(), "Disabled Back Apps", Toast.LENGTH_LONG).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
            Toast.makeText(getApplicationContext(), "Disabled Recent Apps", Toast.LENGTH_LONG).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            Toast.makeText(getApplicationContext(), "Disabled Clicked Power", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
