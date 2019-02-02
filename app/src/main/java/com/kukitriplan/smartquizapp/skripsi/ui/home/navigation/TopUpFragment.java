package com.kukitriplan.smartquizapp.skripsi.ui.home.navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.HistoryTopUpAdapter;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.HomeJson;
import com.kukitriplan.smartquizapp.skripsi.data.model.HistoryTopUp;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.ui.home.HomeActivity;
import com.kukitriplan.smartquizapp.skripsi.util.IabHelper;
import com.kukitriplan.smartquizapp.skripsi.util.IabResult;
import com.kukitriplan.smartquizapp.skripsi.util.Inventory;
import com.kukitriplan.smartquizapp.skripsi.util.Purchase;
import com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.PopupUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpFragment extends Fragment {
    private static final String TAG = TopUpFragment.class.getSimpleName();
    private View view;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.rvHistoryVoucher) RecyclerView rvHistoryVoucher;
    @BindView(R.id.tvSaldo) TextView tvSaldo;
    @BindView(R.id.voucher) TextInputLayout voucher;
    @BindView(R.id.txtVoucher) TextInputEditText txtVoucher;
    @BindView(R.id.btn_voucher) Button btnVoucher;
    @BindView(R.id.tvMsg) TextView tvMsg;

    private String strVoucher;

    private SharedLoginManager prefManager;

    private ApiServices services;
    private Call<HomeResponse> call;

    private ArrayList<HistoryTopUp> histories;

    private HistoryTopUpAdapter historyTopUpAdapter;

    private ProgressUtils progressUtils;

    private OnFragmentInteractionListener mListener;

    private static final String s = "SALDO";

    // Biling
    private boolean isAlreadyPurchase;
    private String PUBLIC_KEY = ConfigUtils.PUBLIC_KEY;
    private String SKU_BELI_SALDO = ConfigUtils.SKU_BELI_SALDO;
    private IabHelper mIabHelper;
    private int RC_REQUEST = 115;
    private String payload = "";

    public TopUpFragment() {
        // Required empty public constructor
    }

    public static TopUpFragment newInstance(String param1, String param2) {
        TopUpFragment fragment = new TopUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        progressUtils = new ProgressUtils(getActivity());
        progressUtils.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        getHistory();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_top_up, container, false);
        ButterKnife.bind(this, view);

        prefManager = new SharedLoginManager(view.getContext());
        tvSaldo.setText(getResources().getString(R.string.txtSaldo, prefManager.getSpSaldo()));
        rvHistoryVoucher = view.findViewById(R.id.rvHistoryVoucher);
        rvHistoryVoucher.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration decoration = new DividerItemDecoration(rvHistoryVoucher.getContext(), RecyclerView.VERTICAL);
        rvHistoryVoucher.addItemDecoration(decoration);
        rvHistoryVoucher.setItemAnimator(new DefaultItemAnimator());
        rvHistoryVoucher.setHasFixedSize(true);
        services = RetrofitBuilder.createServices(ApiServices.class);

        //getHistoryTopUp();

        initBilling();

        txtVoucher.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    isiSaldo();
                }
                return false;
            }
        });
        return view;
    }

    private boolean validate() {
        boolean valid = true;

        strVoucher = voucher.getEditText().getText().toString();

        if (strVoucher.isEmpty()) {
            voucher.setError(getString(R.string.txtErrorVoucher1));
            valid = false;
        } else if (strVoucher.length() < 16 || strVoucher.length() > 16) {
            voucher.setError(getString(R.string.txtErrorVoucher2));
            valid = false;
        } else {
            voucher.setError(null);
        }

        return valid;
    }

    @OnClick(R.id.btn_voucher) void isiSaldo() {
        KeyboardUtils keyboardUtils = new KeyboardUtils();
        keyboardUtils.hideSoftKeyboard(getActivity());
        keyboardUtils.setupUI(getView(), getActivity());

        if (!validate()) {
            btnVoucher.setEnabled(true);
            return;
        }
        btnVoucher.setEnabled(false);
        progressUtils.show();
        strVoucher = voucher.getEditText().getText().toString();
        call = services.isiSaldo(strVoucher, prefManager.getSpToken(),"home","topUp", prefManager.getSpEmail());
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        HomeResponse res = response.body();
                        HomeJson json = res.getHome();
                        if (json.getKode().equals("1")) {
                            progressUtils.hide();
                            btnVoucher.setEnabled(true);
                            tvSaldo.setText(getResources().getString(R.string.txtSaldo, json.getSaldo()));
                            prefManager.saveSPString(SharedLoginManager.SP_SALDO, json.getSaldo());
                            PopupUtils.loadError(view.getContext(), json.getTitle(), json.getMessage());
                            txtVoucher.setText(null);
                            onResume();
                        } else if (json.getKode().equals("2")) {
                            prefManager.clearShared();
                            startActivity(new Intent(view.getContext(), AuthActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            btnVoucher.setEnabled(true);
                            progressUtils.hide();
                            PopupUtils.loadError(view.getContext(), json.getTitle(), json.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressUtils.hide();
                        PopupUtils.loadError(view.getContext(), "Error", e.getMessage());
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    PopupUtils.loadError(view.getContext(), "Error", "502");
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    PopupUtils.loadError(view.getContext(), "Error", "404");
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                btnVoucher.setEnabled(true);
                progressUtils.hide();
                PopupUtils.loadError(view.getContext(), "Error", t.getMessage());
            }
        });
        progressUtils.hide();
    }

    private void getHistory() {
        progressUtils.show();
        call = services.getHistoryTopUp(prefManager.getSpToken(), "home","historyTopUp", prefManager.getSpEmail());
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        HomeResponse res = response.body();
                        HomeJson json = res.getHome();
                        if (json.getKode().equals("1")) {
                            tvSaldo.setText(getResources().getString(R.string.txtSaldo, json.getSaldo()));
                            histories = new ArrayList<>(Arrays.asList(json.getHistoryTopUp()));
                            List<HistoryTopUp> historyTopUpList = new ArrayList<>();
                            for (int i = 0; i < histories.size(); i++) {
                                historyTopUpList.add(new HistoryTopUp(
                                        histories.get(i).getNomor(),
                                        histories.get(i).getJumlah(),
                                        histories.get(i).getJumlah()
                                ));
                            }
                            historyTopUpAdapter = new HistoryTopUpAdapter(view.getContext(), histories);
                            rvHistoryVoucher.setAdapter(historyTopUpAdapter);
                            historyTopUpAdapter.notifyDataSetChanged();

                        } else {
                            rvHistoryVoucher.setVisibility(View.GONE);
                            tvMsg.setVisibility(View.VISIBLE);
                            tvMsg.setText(json.getMessage());
                        }
                        progressUtils.hide();
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressUtils.hide();
                        PopupUtils.loadError(view.getContext(), "Error", e.getMessage());
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    PopupUtils.loadError(view.getContext(), "Error", "502");
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    PopupUtils.loadError(view.getContext(), "Error", "404");
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                progressUtils.hide();
                PopupUtils.loadError(view.getContext(), "Error", t.getMessage());
            }
        });
    }

    private void initBilling() {
        mIabHelper = new IabHelper(view.getContext(), PUBLIC_KEY);
        mIabHelper.enableDebugLogging(false);
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    return;
                }
                if (mIabHelper == null) {
                    return;
                }
                try {
                    mIabHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                        @Override
                        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                            if (mIabHelper == null) {
                                return;
                            }
                            if (result.isFailure()) {
                                return;
                            }
                            isAlreadyPurchase = inv.hasPurchase(SKU_BELI_SALDO);
                            if (isAlreadyPurchase) {
                                Toast.makeText(getActivity(), ""+result, Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "onQueryInventoryFinished: " + result);
                            } else {
                                Toast.makeText(getActivity(), ""+result, Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "onQueryInventoryFinished: " + result);
                            }
                        }
                    });
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void purchaseSaldo() {
        try {
            mIabHelper.launchPurchaseFlow(TopUpFragment.this.getActivity(), SKU_BELI_SALDO, RC_REQUEST, new IabHelper.OnIabPurchaseFinishedListener() {
                @Override
                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                    if (mIabHelper == null)
                        return;

                    if (result.isFailure()) {
                        //Error purchasing
                        return;
                    }

                    //Purchase successful
                    if (info.getSku().equals(SKU_BELI_SALDO)) {
                        PopupUtils.loadError(view.getContext(), "In-App-Purchase", ""+result);
                        Toast.makeText(getContext(), "Beli Item Saldo", Toast.LENGTH_SHORT).show();
                    }
                }
            }, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
    @OnClick(R.id.btnBayarGoogle) void bayarSaldo() {
        purchaseSaldo();
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
