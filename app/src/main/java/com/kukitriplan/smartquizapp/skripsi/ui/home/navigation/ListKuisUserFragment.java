package com.kukitriplan.smartquizapp.skripsi.ui.home.navigation;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.HistoryKuisAdapter;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.HomeJson;
import com.kukitriplan.smartquizapp.skripsi.data.model.HistoryKuis;
import com.kukitriplan.smartquizapp.skripsi.data.model.Kuis;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.PopupUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListKuisUserFragment extends Fragment {

    private View view;
    @BindView(R.id.rvListKuisHistoryUser) RecyclerView getRvListKuisHistoryUser;
    @BindView(R.id.tvError) TextView tvError;

    private ArrayList<HistoryKuis> historyKuis;
    private ArrayList<Kuis> kuis;

    private ApiServices services;
    private Call<HomeResponse> call;

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private HistoryKuisAdapter historyKuisAdapter;

    public ListKuisUserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_kuis_user, container, false);
        ButterKnife.bind(this, view);
        progressUtils = new ProgressUtils(getContext());
        keyboardUtils = new KeyboardUtils();
        progressUtils.hide();
        services = RetrofitBuilder.createServices(ApiServices.class);
        prefManager = new SharedLoginManager(getContext());
        getRvListKuisHistoryUser.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration decoration = new DividerItemDecoration(getRvListKuisHistoryUser.getContext(), RecyclerView.VERTICAL);
        getRvListKuisHistoryUser.addItemDecoration(decoration);
        getRvListKuisHistoryUser.setItemAnimator(new DefaultItemAnimator());
        getRvListKuisHistoryUser.setHasFixedSize(true);
        getHistoryKuis();
        return view;
    }

    private void getHistoryKuis() {
        keyboardUtils.hideSoftKeyboard(getActivity());
        progressUtils.show();
        call = services.getHistoryKuis(prefManager.getSpToken(), "home", "historyKuis", prefManager.getSpEmail());
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    try {
                        if (json.getKode().equals("1")) {
                            historyKuis = new ArrayList<>(Arrays.asList(json.getHistoryKuis()));
                            List<HistoryKuis> historyKuisList = new ArrayList<>();
                            for (int i = 0; i < historyKuis.size(); i++) {
                                historyKuisList.add(new HistoryKuis(
                                        historyKuis.get(i).getIdNilai(),
                                        historyKuis.get(i).getNomor(),
                                        historyKuis.get(i).getNamaKuis(),
                                        historyKuis.get(i).getNilai()
                                ));
                            }
                            historyKuisAdapter = new HistoryKuisAdapter(getContext(), historyKuis);
                            getRvListKuisHistoryUser.setAdapter(historyKuisAdapter);
                            historyKuisAdapter.notifyDataSetChanged();
                            progressUtils.hide();
                        } else if (json.getKode().equals("2")) {
                            prefManager.clearShared();
                            startActivity(new Intent(view.getContext(), AuthActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            progressUtils.hide();
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText(json.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressUtils.hide();
                        PopupUtils.loadError(view.getContext(), "Error", e.getMessage());
                    }
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
}
