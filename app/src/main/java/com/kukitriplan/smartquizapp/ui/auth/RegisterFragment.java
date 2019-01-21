package com.kukitriplan.smartquizapp.ui.auth;

import android.content.Context;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.AuthJson;
import com.kukitriplan.smartquizapp.data.response.AuthResponse;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.nama) TextInputLayout edtNama;
    @BindView(R.id.email) TextInputLayout edtEmail;
    @BindView(R.id.password) TextInputLayout edtPass;
    @BindView(R.id.btn_register) Button btnRegister;

    @BindView(R.id.tipeRadio) RadioGroup rg_tipe;
    @BindView(R.id.rb_author) RadioButton rb_auhtor;
    @BindView(R.id.txtNama) TextInputEditText txtNama;
    @BindView(R.id.txtEmail) TextInputEditText txtEmail;
    @BindView(R.id.txtPass) TextInputEditText txtPass;

    private RadioButton radioButton;

    private View root;

    private ApiServices services;
    private Call<AuthResponse> call;

    private String nama, email, password;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private OnFragmentInteractionListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        root = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, root);
        progressUtils = new ProgressUtils(getContext());
        keyboardUtils = new KeyboardUtils();
        progressUtils.hide();
        services = RetrofitBuilder.createServices(ApiServices.class);
        txtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    register();
                }
                return false;
            }
        });
        return root;
    }

    private boolean validate() {
        boolean valid = true;

        nama = Objects.requireNonNull(edtNama.getEditText()).getText().toString();
        email = Objects.requireNonNull(edtEmail.getEditText()).getText().toString();
        password = Objects.requireNonNull(edtPass.getEditText()).getText().toString();

        if (rg_tipe.getCheckedRadioButtonId() == -1) {
            rb_auhtor.setError(getString(R.string.txtErrorTipe));
            valid = false;
        } else {
            rb_auhtor.setError(null);
        }
        if (nama.isEmpty()) {
            edtNama.setError(getString(R.string.txtErrorName1));
            valid = false;
        } else if (nama.length() < 3) {
            edtNama.setError(getString(R.string.txtErrorName2));
            valid = false;
        } else {
            edtNama.setError(null);
        }

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

    @OnClick(R.id.btn_register) void register() {

        keyboardUtils.hideSoftKeyboard(getActivity());

        if (!validate()) {
            //Toast.makeText(getActivity(), getString(R.string.txtErrorValid), Toast.LENGTH_SHORT).show();
            btnRegister.setEnabled(true);
            return;
        }

        btnRegister.setEnabled(false);
        progressUtils.show();

        int selectid = rg_tipe.getCheckedRadioButtonId();
        radioButton = root.findViewById(selectid);
        String tipe;
        if (radioButton.getText().equals("Author")) {
            tipe = "2";
        } else {
            tipe = "3";
        }
        nama = Objects.requireNonNull(edtNama.getEditText()).getText().toString();
        email = Objects.requireNonNull(edtEmail.getEditText()).getText().toString();
        password = Objects.requireNonNull(edtPass.getEditText()).getText().toString();

        call = services.register(tipe, nama, email, password);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    AuthResponse res = response.body();
                    AuthJson json = res.getAuth();
                    if (json.getKode().equals("1")) {
                        btnRegister.setEnabled(true);
                        txtNama.setText(null);
                        txtEmail.setText(null);
                        txtPass.setText(null);
                        rg_tipe.clearCheck();
                        progressUtils.hide();
                        PopupUtils.PopAuth(getContext(), "Success", json.getMessage());
                    } else {
                        btnRegister.setEnabled(true);
                        progressUtils.hide();
                        Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 502) {
                    btnRegister.setEnabled(true);
                    txtEmail.setText(null);
                    txtPass.setText(null);
                    progressUtils.hide();
                    Toast.makeText(getActivity(), "502 Connection Timed Out", Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    btnRegister.setEnabled(true);
                    txtEmail.setText(null);
                    txtPass.setText(null);
                    progressUtils.hide();
                    Toast.makeText(getActivity(), "404 Not Found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                btnRegister.setEnabled(true);
                progressUtils.hide();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
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
