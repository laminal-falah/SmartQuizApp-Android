package com.kukitriplan.smartquizapp.skripsi.ui.dashboard.navigation;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.HistoryIkutKuisAdapter;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.DashboardJson;
import com.kukitriplan.smartquizapp.skripsi.data.model.HistoryIkutKuis;
import com.kukitriplan.smartquizapp.skripsi.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PenjualanFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View view;
    @BindView(R.id.tvListIkutKuis) TextView tvTitle;
    @BindView(R.id.rvListIkutKuisHistory) RecyclerView rvListIkutKuis;
    @BindView(R.id.tvError) TextView tvError;

    private ApiServices services;
    private Call<DashboardResponse> call;

    private ArrayList<HistoryIkutKuis> historyIkutKuis;

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private HistoryIkutKuisAdapter ikutKuisAdapter;

    private OnFragmentInteractionListener mListener;

    public PenjualanFragment() {
        // Required empty public constructor
    }

    public static PenjualanFragment newInstance(String param1, String param2) {
        PenjualanFragment fragment = new PenjualanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        progressUtils = new ProgressUtils(getContext());
        keyboardUtils = new KeyboardUtils();
        progressUtils.hide();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_penjualan, container, false);
        prefManager = new SharedLoginManager(getContext());
        ButterKnife.bind(this, view);
        services = RetrofitBuilder.createServices(ApiServices.class);
        DividerItemDecoration decoration = new DividerItemDecoration(rvListIkutKuis.getContext(), RecyclerView.VERTICAL);
        rvListIkutKuis.addItemDecoration(decoration);
        rvListIkutKuis.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListIkutKuis.setItemAnimator(new DefaultItemAnimator());
        rvListIkutKuis.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getIkutKuis();
    }

    private void getIkutKuis() {
        progressUtils.show();
        call = services.getHistoryIkutKuis(prefManager.getSpToken(), "dashboard", "historyIkutKuis", prefManager.getSpEmail());
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        historyIkutKuis = new ArrayList<>(Arrays.asList(json.getIkutKuis()));
                        List<HistoryIkutKuis> ikutKuisList = new ArrayList<>();
                        for (int i = 0; i < historyIkutKuis.size(); i++) {
                            ikutKuisList.add(new HistoryIkutKuis(
                                    historyIkutKuis.get(i).getNomor(),
                                    historyIkutKuis.get(i).getAuthor(),
                                    historyIkutKuis.get(i).getJudul(),
                                    historyIkutKuis.get(i).getBenar(),
                                    historyIkutKuis.get(i).getSalah(),
                                    historyIkutKuis.get(i).getNilai(),
                                    historyIkutKuis.get(i).getTanggal(),
                                    historyIkutKuis.get(i).getPemain(),
                                    historyIkutKuis.get(i).getRating()
                            ));
                        }
                        ikutKuisAdapter = new HistoryIkutKuisAdapter(getContext(), historyIkutKuis);
                        rvListIkutKuis.setAdapter(ikutKuisAdapter);
                        ikutKuisAdapter.notifyDataSetChanged();
                        progressUtils.hide();
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getContext(), AuthActivity.class));
                        Toast.makeText(getContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        progressUtils.hide();
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(json.getMessage());
                    }
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
