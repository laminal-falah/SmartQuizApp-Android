package com.kukitriplan.smartquizapp.ui.home.navigation;

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
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.adapter.HistoryTopUpAdapter;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.HomeJson;
import com.kukitriplan.smartquizapp.data.model.HistoryTopUp;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.ui.home.HomeActivity;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TopUpFragment extends Fragment {
    private static final String TAG = TopUpFragment.class.getSimpleName();
    private View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
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

    public TopUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopUpFragment.
     */
    // TODO: Rename and change types and number of parameters
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
