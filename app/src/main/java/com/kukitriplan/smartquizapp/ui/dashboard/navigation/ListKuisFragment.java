package com.kukitriplan.smartquizapp.ui.dashboard.navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.adapter.HistoryKuisAdapter;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.HomeJson;
import com.kukitriplan.smartquizapp.data.model.HistoryKuis;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

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

    private ApiServices services;
    private Call<HomeResponse> call;

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private HistoryKuisAdapter historyKuisAdapter;

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

        DividerItemDecoration decoration = new DividerItemDecoration(rvListKuisHistory.getContext(), RecyclerView.VERTICAL);
        rvListKuisHistory.addItemDecoration(decoration);
        rvListKuisHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvListKuisHistory.setItemAnimator(new DefaultItemAnimator());
        rvListKuisHistory.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getHistoryKuis();
    }

    private void bottomSheet() {
        View bottomSheet = view.findViewById(R.id.framelayout_bottom_sheet);
        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        (bottomSheetLayout.findViewById(R.id.button_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        (bottomSheetLayout.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mBottomSheetDialog.setContentView(bottomSheetLayout);
        mBottomSheetDialog.show();
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
