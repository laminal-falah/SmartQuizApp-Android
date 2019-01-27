package com.kukitriplan.smartquizapp.ui.dashboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.adapter.ListSoalAdapter;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.DashboardJson;
import com.kukitriplan.smartquizapp.data.model.Soal;
import com.kukitriplan.smartquizapp.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.utils.SwipeRecyclerView;
import com.kukitriplan.smartquizapp.utils.SwipeRecyclerViewAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListSoalActivity extends AppCompatActivity {

    @BindView(R.id.tvErrorListSoal) TextView tvError;
    @BindView(R.id.tvJudulKuis) TextView tvJudulKuis;
    @BindView(R.id.tvJumlahDataSoal) TextView tvJumlahDataSoal;
    @BindView(R.id.rvListSoal) RecyclerView rvListSoal;
    @BindView(R.id.nsListSoal) NestedScrollView scrollView;

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private ArrayList<Soal> soals;
    private ApiServices services;
    private Call<DashboardResponse> call;

    private ListSoalAdapter listSoalAdapter;
    private SwipeRecyclerView swipeRecyclerView = null;
    private ItemTouchHelper itemTouchHelper;

    private String slug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_soal);
        ButterKnife.bind(this);
        prefManager = new SharedLoginManager(this);
        keyboardUtils = new KeyboardUtils();
        progressUtils = new ProgressUtils(this);
        progressUtils.hide();
        services = RetrofitBuilder.createServices(ApiServices.class);
        slug = getIntent().getStringExtra("slugKuis");
    }

    private void getListSoal() {
        progressUtils.show();
        call = services.getListSoalAuthor(prefManager.getSpToken(), "dashboard", "listSoal", slug);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(final Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    final DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        tvJudulKuis.setText(json.getTitle());
                        soals = new ArrayList<>(Arrays.asList(json.getSoalList()));
                        final List<Soal> soalList = new ArrayList<>();
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

                        rvListSoal.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        //rvListSoal.setItemAnimator(new DefaultItemAnimator());
                        //rvListSoal.setHasFixedSize(true);
                        rvListSoal.setItemAnimator(new DefaultItemAnimator());
                        listSoalAdapter = new ListSoalAdapter(getApplicationContext(), soals);
                        tvJumlahDataSoal.setText(getString(R.string.txtJmlhSoal, listSoalAdapter.getItemCount()));
                        rvListSoal.setAdapter(listSoalAdapter);
                        swipeRecyclerView = new SwipeRecyclerView(new SwipeRecyclerViewAction() {
                            @Override
                            public void onLeftClicked(int position) {
                                listSoalAdapter.notifyDataSetChanged();
                                startActivity(new Intent(getApplicationContext(), EditSoalActivity.class)
                                        .putExtra("ID", soals.get(position).getIdSoal())
                                        .putExtra("JUDUL", json.getTitle())
                                        .putExtra("NOMOR SOAL", soals.get(position).getNomorSoal()));

                            }

                            @Override
                            public void onRightClicked(int position) {
                                hapusSoal(soals.get(position).getIdSoal());
                                //Toast.makeText(getApplicationContext(), "id Soal = " + soals.get(position).getIdSoal() + " Dengan Judul = " + soalList.get(position).getJudulSoal(), Toast.LENGTH_SHORT).show();
                                listSoalAdapter.soals.remove(position);
                                listSoalAdapter.notifyItemRemoved(position);
                                listSoalAdapter.notifyItemRangeChanged(position, listSoalAdapter.getItemCount());
                                onResume();
                            }
                        });
                        itemTouchHelper = new ItemTouchHelper(swipeRecyclerView);
                        itemTouchHelper.attachToRecyclerView(rvListSoal);
                        rvListSoal.addItemDecoration(new RecyclerView.ItemDecoration() {
                            @Override
                            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                                swipeRecyclerView.onDraw(c);
                            }
                        });
                        listSoalAdapter.notifyDataSetChanged();
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        tvJudulKuis.setText(json.getTitle());
                        tvJumlahDataSoal.setText(getString(R.string.txtJmlhSoal, json.getJumlahSoal()));
                        progressUtils.hide();
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(json.getMessage());
                        Toast.makeText(getApplicationContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressUtils.hide();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                progressUtils.hide();
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(t.getMessage());
            }
        });
    }

    private void hapusSoal(String id) {
        call = services.deleteSoal(prefManager.getSpToken(), "dashboard","hapusSoal", id);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                        listSoalAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListSoal();
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

