package com.kukitriplan.smartquizapp.ui.dashboard.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.adapter.HistoryKuisAdapter;
import com.kukitriplan.smartquizapp.adapter.ListKuisAuthorAdapter;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.DashboardJson;
import com.kukitriplan.smartquizapp.data.json.HomeJson;
import com.kukitriplan.smartquizapp.data.model.HistoryKuis;
import com.kukitriplan.smartquizapp.data.model.Kuis;
import com.kukitriplan.smartquizapp.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
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

public class ListKuisFragment extends Fragment {
    private static final String TAG = ListKuisFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;
    private BottomSheetDialog mBottomSheetDialog;
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
        progressUtils = new ProgressUtils(getContext());
        keyboardUtils = new KeyboardUtils();
        progressUtils.hide();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_kuis, container, false);
        ButterKnife.bind(this, view);
        prefManager = new SharedLoginManager(getContext());
        services = RetrofitBuilder.createServices(ApiServices.class);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefManager.getSpLevel().equals("author")) {
           getListKuis();
        } else {
           getHistoryKuis();
        }
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
                            rvListKuisHistory.setAdapter(historyKuisAdapter);
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

    private void getListKuis() {
        progressUtils.show();
        callDashboard = services.getListKuisAuthor(prefManager.getSpToken(), "dashboard","listKuis", prefManager.getSpEmail());
        callDashboard.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    kuis = new ArrayList<>(Arrays.asList(json.getKuisList()));
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
                    rvListKuisHistory.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
                    rvListKuisHistory.setItemAnimator(new DefaultItemAnimator());
                    listKuisAuthorAdapter = new ListKuisAuthorAdapter(getContext(), kuis);
                    rvListKuisHistory.setAdapter(listKuisAuthorAdapter);
                    swipeRecyclerView = new SwipeRecyclerView(new SwipeRecyclerViewAction() {
                        @Override
                        public void onLeftClicked(int position) {
                            Toast.makeText(getContext(), "Edit Kuis", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRightClicked(int position) {
                            Toast.makeText(getContext(), "Hapus Kuis", Toast.LENGTH_LONG).show();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
