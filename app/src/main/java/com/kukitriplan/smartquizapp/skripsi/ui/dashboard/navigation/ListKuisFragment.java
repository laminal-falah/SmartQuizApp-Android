package com.kukitriplan.smartquizapp.skripsi.ui.dashboard.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.HistoryKuisAdapter;
import com.kukitriplan.smartquizapp.skripsi.adapter.ListKuisAuthorAdapter;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.DashboardJson;
import com.kukitriplan.smartquizapp.skripsi.data.model.HistoryKuis;
import com.kukitriplan.smartquizapp.skripsi.data.model.Kuis;
import com.kukitriplan.smartquizapp.skripsi.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.ui.dashboard.EditKuisActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SwipeRecyclerView;
import com.kukitriplan.smartquizapp.skripsi.utils.SwipeRecyclerViewAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListKuisFragment extends Fragment {
    private static final String TAG = ListKuisFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;
    @BindView(R.id.rvListKuisHistory) RecyclerView rvListKuisHistory;
    @BindView(R.id.tvError) TextView tvError;

    private ArrayList<HistoryKuis> historyKuis;
    private ArrayList<Kuis> kuis;

    private ApiServices services;
    private Call<HomeResponse> call;
    private Call<DashboardResponse> callDashboard;

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private HistoryKuisAdapter historyKuisAdapter;
    private ListKuisAuthorAdapter listKuisAuthorAdapter;
    private SwipeRecyclerView swipeRecyclerView = null;
    private ItemTouchHelper itemTouchHelper;

    private OnFragmentInteractionListener mListener;

    public ListKuisFragment() {
        // Required empty public constructor
    }

    public static ListKuisFragment newInstance(String param1, String param2) {
        ListKuisFragment fragment = new ListKuisFragment();
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
        prefManager = new SharedLoginManager(getContext());
        progressUtils = new ProgressUtils(getContext());
        keyboardUtils = new KeyboardUtils();
        progressUtils.hide();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_kuis, container, false);
        ButterKnife.bind(this, view);
        services = RetrofitBuilder.createServices(ApiServices.class);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getListKuis();
    }

    private void getListKuis() {
        progressUtils.show();
        callDashboard = services.getListKuisAuthor(prefManager.getSpToken(), "dashboard","listKuis", prefManager.getSpEmail());
        callDashboard.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        kuis = new ArrayList<>(Arrays.asList(json.getKuisList()));
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
                        rvListKuisHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                        rvListKuisHistory.setItemAnimator(new DefaultItemAnimator());
                        listKuisAuthorAdapter = new ListKuisAuthorAdapter(getContext(), kuis);
                        rvListKuisHistory.setAdapter(listKuisAuthorAdapter);
                        swipeRecyclerView = new SwipeRecyclerView(new SwipeRecyclerViewAction() {
                            @Override
                            public void onLeftClicked(int position) {
                                listKuisAuthorAdapter.notifyDataSetChanged();
                                startActivity(new Intent(getContext(), EditKuisActivity.class)
                                        .putExtra("ID", kuis.get(position).getId_kuis()));
                            }

                            @Override
                            public void onRightClicked(int position) {
                                hapusKuis(kuis.get(position).getId_kuis());
                                listKuisAuthorAdapter.kuis.remove(position);
                                listKuisAuthorAdapter.notifyItemRemoved(position);
                                listKuisAuthorAdapter.notifyItemRangeRemoved(position, listKuisAuthorAdapter.getItemCount());
                                onResume();
                            }
                        });
                        itemTouchHelper = new ItemTouchHelper(swipeRecyclerView);
                        itemTouchHelper.attachToRecyclerView(rvListKuisHistory);
                        rvListKuisHistory.addItemDecoration(new RecyclerView.ItemDecoration() {
                            @Override
                            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                                swipeRecyclerView.onDraw(c);
                            }
                        });
                        listKuisAuthorAdapter.notifyDataSetChanged();
                        progressUtils.hide();
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        progressUtils.hide();
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(json.getMessage());
                        Toast.makeText(getContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                progressUtils.hide();
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(t.getMessage());
            }
        });
    }

    private void hapusKuis(String id) {
        callDashboard = services.deleteKuis(prefManager.getSpToken(), "dashboard","hapusKuis", id);
        callDashboard.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        Toast.makeText(getContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                        listKuisAuthorAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
