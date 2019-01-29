package com.kukitriplan.smartquizapp.ui.dashboard.navigation;

import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.adapter.HistoryWithDrawAdapter;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.DashboardJson;
import com.kukitriplan.smartquizapp.data.model.HistoryWithDraw;
import com.kukitriplan.smartquizapp.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WithDrawFragment extends Fragment {

    private View view;

    @BindView(R.id.edtJumlah) EditText edtJumlah;
    @BindView(R.id.btnWithDraw) Button btnWithDraw;
    @BindView(R.id.rvHistoryWithDraw) RecyclerView rvHistory;
    @BindView(R.id.tvMsg) TextView tvMsg;
    @BindView(R.id.tvSaldo) TextView tvSaldo;

    private SharedLoginManager prefManager;

    private ApiServices services;
    private Call<DashboardResponse> call;

    private ArrayList<HistoryWithDraw> historyWithDraws;

    private HistoryWithDrawAdapter drawAdapter;

    private ProgressUtils progressUtils;

    private String jmlh;

    public WithDrawFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_with_draw, container, false);
        ButterKnife.bind(this, view);
        progressUtils = new ProgressUtils(getContext());
        prefManager = new SharedLoginManager(getContext());
        services = RetrofitBuilder.createServices(ApiServices.class);
        progressUtils.hide();
        DividerItemDecoration decoration = new DividerItemDecoration(rvHistory.getContext(), RecyclerView.VERTICAL);
        rvHistory.addItemDecoration(decoration);
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.setHasFixedSize(true);

        edtJumlah.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    submitWithDraw();
                }
                return false;
            }
        });
        return view;
    }

    private void getHistory() {
        progressUtils.show();
        call = services.getHistoryWithDraw(prefManager.getSpToken(), "dashboard", "historyWithDraw", prefManager.getSpEmail());
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        tvMsg.setVisibility(View.INVISIBLE);
                        rvHistory.setVisibility(View.VISIBLE);
                        tvSaldo.setText(getString(R.string.txtSaldo, String.valueOf(json.getSaldo())));
                        historyWithDraws = new ArrayList<>(Arrays.asList(json.getHistoryList()));
                        List<HistoryWithDraw> withDrawList = new ArrayList<>();
                        for (int i = 0; i < historyWithDraws.size(); i++) {
                            withDrawList.add(new HistoryWithDraw(
                                    historyWithDraws.get(i).getNomor(),
                                    historyWithDraws.get(i).getTitle(),
                                    historyWithDraws.get(i).getStatus(),
                                    historyWithDraws.get(i).getTanggal(),
                                    historyWithDraws.get(i).getNamaBank(),
                                    historyWithDraws.get(i).getRekening(),
                                    historyWithDraws.get(i).getAtasNama()
                            ));
                        }
                        drawAdapter = new HistoryWithDrawAdapter(getContext(), historyWithDraws);
                        rvHistory.setAdapter(drawAdapter);
                        drawAdapter.notifyDataSetChanged();
                        progressUtils.hide();
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        progressUtils.hide();
                        tvSaldo.setText(getString(R.string.txtSaldo, String.valueOf(json.getSaldo())));
                        tvMsg.setVisibility(View.VISIBLE);
                        rvHistory.setVisibility(View.INVISIBLE);
                        tvMsg.setText(json.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                progressUtils.hide();
                tvMsg.setVisibility(View.VISIBLE);
                tvMsg.setText(t.getMessage());
            }
        });
    }

    @OnClick(R.id.btnWithDraw) void submitWithDraw() {
        jmlh = edtJumlah.getText().toString();

        call = services.withDraw(prefManager.getSpToken(), "dashboard", "withDraw", prefManager.getSpEmail(), jmlh);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        progressUtils.hide();
                        edtJumlah.setText(null);
                        Toast.makeText(getContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                        onResume();
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        progressUtils.hide();
                        Toast.makeText(getContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getHistory();
    }

}
