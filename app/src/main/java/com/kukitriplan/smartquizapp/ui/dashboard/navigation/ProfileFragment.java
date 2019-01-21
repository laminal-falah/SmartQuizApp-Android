package com.kukitriplan.smartquizapp.ui.dashboard.navigation;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.adapter.ProfileAdapter;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.AuthJson;
import com.kukitriplan.smartquizapp.data.json.HomeJson;
import com.kukitriplan.smartquizapp.data.model.Account;
import com.kukitriplan.smartquizapp.data.response.AuthResponse;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.ui.dashboard.EditProfileActivity;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;

    @BindView(R.id.btnLogout) Button btnLogout;
    @BindView(R.id.btnUbahPassword) Button btnUbahPassword;
    @BindView(R.id.btnEditProfile) Button btnEditProfile;
    @BindView(R.id.rvProfile) RecyclerView rvProfile;
    @BindView(R.id.tvError) TextView tvError;

    private ArrayList<Account> accounts;

    private ApiServices services;
    private Call<AuthResponse> callLogout;
    private Call<HomeResponse> callProfile;

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private ProfileAdapter profileAdapter;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
    public void onResume() {
        super.onResume();
        profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        prefManager = new SharedLoginManager(getContext());
        services = RetrofitBuilder.createServices(ApiServices.class);
        if (prefManager.getSpLevel().equals("author")) {
            btnLogout.setVisibility(View.INVISIBLE);
        }
        rvProfile.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration decoration = new DividerItemDecoration(rvProfile.getContext(), RecyclerView.VERTICAL);
        rvProfile.addItemDecoration(decoration);
        rvProfile.setItemAnimator(new DefaultItemAnimator());
        rvProfile.setHasFixedSize(true);

        return view;
    }

    @OnClick(R.id.btnLogout) void logout() {
        keyboardUtils.hideSoftKeyboard(getActivity());
        progressUtils.show();
        callLogout = services.logout(prefManager.getSpToken());
        callLogout.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        AuthResponse res = response.body();
                        AuthJson json = res.getAuth();
                        if (json.getKode().equals("1")) {
                            prefManager.clearShared();
                            progressUtils.hide();
                            startActivity(new Intent(getContext(), AuthActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        }
                        progressUtils.hide();
                    } catch (Exception e) {
                        progressUtils.hide();
                        PopupUtils.loadError(getContext(), "Error", e.getMessage());
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    PopupUtils.loadError(getContext(), "Error", "502");
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    PopupUtils.loadError(getContext(), "Error", "404");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                progressUtils.hide();
                PopupUtils.loadError(getContext(), "Error", t.getMessage());
            }
        });
    }

    @OnClick(R.id.btnEditProfile) void editProfile() {
        startActivity(new Intent(getContext(), EditProfileActivity.class));
    }
    
    private void profile() {
        progressUtils.show();
        callProfile = services.getProfile(prefManager.getSpToken(),"home", "profileUser", prefManager.getSpEmail());
        callProfile.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    try {
                        accounts = new ArrayList<>();
                        if (json.getKode().equals("1")) {
                            accounts.add(new Account("Nama", json.getUser().getNama() != null ? json.getUser().getNama() : "-"));
                            accounts.add(new Account("Username", json.getUser().getUsername() != null ? json.getUser().getUsername() : "-"));
                            accounts.add(new Account("Email", json.getUser().getEmail() != null ? json.getUser().getEmail() : "-"));
                            accounts.add(new Account("Tipe Akun", json.getUser().getLevel() != null ? json.getUser().getLevel() : "-"));
                            accounts.add(new Account("Tanggal Lahir", json.getUser().getLahir() != null ? json.getUser().getLahir() : "-"));
                            accounts.add(new Account("Jenis Kelamin", json.getUser().getKelamin().equals("L") ? "Laki - Laki" : (json.getUser().getKelamin().equals("P") ? "Perempuan" : "-")));
                            accounts.add(new Account("No Telepon / Hp", json.getUser().getNo() != null ? json.getUser().getNo() : "-"));
                            accounts.add(new Account("Alamat", json.getUser().getAlmt() != null ? json.getUser().getAlmt() : "-"));
                            accounts.add(new Account("Saldo Terakhir", json.getUser().getSaldo() != null ? json.getUser().getSaldo() : "-"));
                            accounts.add(new Account("Nama Bank", json.getUser().getBank() != null ? json.getUser().getBank() : "-"));
                            accounts.add(new Account("No. Rekening", json.getUser().getRek() != null ? json.getUser().getRek() : "-"));
                            accounts.add(new Account("Atas Nama", json.getUser().getAtasNama() != null ? json.getUser().getAtasNama() : "-"));

                            profileAdapter = new ProfileAdapter(getContext(), accounts);
                            rvProfile.setAdapter(profileAdapter);
                            profileAdapter.notifyDataSetChanged();
                            progressUtils.hide();
                        } else if (json.getKode().equals("2")) {
                            prefManager.clearShared();
                            startActivity(new Intent(view.getContext(), AuthActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressUtils.hide();
                        PopupUtils.loadError(view.getContext(), "Error", e.getMessage());
                    }
                }
                progressUtils.hide();
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
