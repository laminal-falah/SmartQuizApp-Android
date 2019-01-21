package com.kukitriplan.smartquizapp.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.AuthJson;
import com.kukitriplan.smartquizapp.data.response.AuthResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.dashboard.DashboardActivity;
import com.kukitriplan.smartquizapp.ui.home.HomeActivity;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.LogoutUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private View view;

    @BindView(R.id.email) TextInputLayout edtEmail;
    @BindView(R.id.password) TextInputLayout edtPass;
    @BindView(R.id.btn_login) Button btnLogin;

    @BindView(R.id.txtEmail) TextInputEditText txtEmail;
    @BindView(R.id.txtPass) TextInputEditText txtPass;

    private ApiServices services;
    private Call<AuthResponse> call;

    private String email, password, msg = "MSG";

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private OnFragmentInteractionListener mListener;
    
    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        progressUtils = new ProgressUtils(getContext());
        keyboardUtils = new KeyboardUtils();
        prefManager = new SharedLoginManager(getContext());
        services = RetrofitBuilder.createServices(ApiServices.class);
        txtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    login();
                }
                return false;
            }
        });
        if (prefManager.getSpLogon() && prefManager.getSpToken() != null) {
            switch (prefManager.getSpLevel()) {
                case "author":
                    startActivity(new Intent(getActivity(), DashboardActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    getActivity().finish();
                    break;
                case "user":
                    startActivity(new Intent(getActivity(), HomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    getActivity().finish();
                    break;
                case "admin":
                    prefManager.clearShared();
                    PopupUtils.PopAdmin(getActivity());
                    break;
                default:
                    getActivity().finish();
                    break;
            }
        }

        progressUtils.hide();
        return view;
    }

    private boolean validate() {
        boolean valid = true;

        email = Objects.requireNonNull(edtEmail.getEditText()).getText().toString();
        password = Objects.requireNonNull(edtPass.getEditText()).getText().toString();

        if (!isEmail(email)) {
            edtEmail.setError(getString(R.string.txtErrorEmail1));
            valid = false;
        } else if (email.isEmpty()) {
            edtEmail.setError(getString(R.string.txtErrorEmail2));
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        if (password.isEmpty()) {
            edtPass.setError(getString(R.string.txtErrorPass2));
            valid = false;
        } else if (password.length() < 8) {
            edtPass.setError(getString(R.string.txtErrorPass1));
            valid = false;
        } else {
            edtPass.setError(null);
        }

        return valid;
    }

    private static boolean isEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @OnClick(R.id.btn_login) void login() {
        keyboardUtils.hideSoftKeyboard(getActivity());

        if (!validate()) {
            btnLogin.setEnabled(true);
            return;
        }

        btnLogin.setEnabled(false);
        progressUtils.show();
        email = Objects.requireNonNull(edtEmail.getEditText()).getText().toString();
        password = Objects.requireNonNull(edtPass.getEditText()).getText().toString();

        call = services.login(email, password);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        AuthResponse res = response.body();
                        AuthJson json = res.getAuth();
                        if (json.getKode().equals("1")) {
                            prefManager.saveSPString(SharedLoginManager.SP_NAME, json.getUser().getNama());
                            prefManager.saveSPString(SharedLoginManager.SP_EMAIL, json.getUser().getEmail());
                            prefManager.saveSPString(SharedLoginManager.SP_LEVEL, json.getUser().getLevel());
                            prefManager.saveSPString(SharedLoginManager.SP_TOKEN, json.getUser().getToken());
                            prefManager.saveSPString(SharedLoginManager.SP_SALDO, json.getUser().getSaldo());
                            prefManager.saveSPBoolean(SharedLoginManager.SP_LOGON, true);
                            if (json.getNotif().isSet()) {
                                prefManager.saveSPBoolean(SharedLoginManager.SP_NOTIF, json.getNotif().isSet());
                                prefManager.saveSPString(SharedLoginManager.SP_TITLE_NOTIF, json.getNotif().getTitle());
                                prefManager.saveSPString(SharedLoginManager.SP_MSG_NOTIF, json.getNotif().getMessage());
                            }
                            switch (json.getUser().getLevel()) {
                                case "author":
                                    progressUtils.hide();
                                    startActivity(new Intent(getActivity(), DashboardActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    break;
                                case "user":
                                    progressUtils.hide();
                                    startActivity(new Intent(getActivity(), HomeActivity.class)
                                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    break;
                                default:
                                    progressUtils.hide();
                                    LogoutUtils.Logout(getContext());
                                    prefManager.clearShared();
                                    PopupUtils.PopAdmin(getActivity());
                                    break;
                            }
                            btnLogin.setEnabled(true);
                            txtEmail.setText(null);
                            txtPass.setText(null);
                        } else {
                            btnLogin.setEnabled(true);
                            txtEmail.setText(null);
                            txtPass.setText(null);
                            progressUtils.hide();
                            Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        btnLogin.setEnabled(true);
                        txtEmail.setText(null);
                        txtPass.setText(null);
                        progressUtils.hide();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 502) {
                    btnLogin.setEnabled(true);
                    txtEmail.setText(null);
                    txtPass.setText(null);
                    progressUtils.hide();
                    Toast.makeText(getActivity(), "502 Connection Timed Out", Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    btnLogin.setEnabled(true);
                    txtEmail.setText(null);
                    txtPass.setText(null);
                    progressUtils.hide();
                    Toast.makeText(getActivity(), "404 Not Found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                btnLogin.setEnabled(true);
                progressUtils.hide();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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
        void onFragmentInteraction(Uri uri);
    }
}
