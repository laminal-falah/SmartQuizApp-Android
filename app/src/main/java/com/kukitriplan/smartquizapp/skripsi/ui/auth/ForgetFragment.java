package com.kukitriplan.smartquizapp.skripsi.ui.auth;

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
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.AuthJson;
import com.kukitriplan.smartquizapp.skripsi.data.response.AuthResponse;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.PopupUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;

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
 * {@link ForgetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForgetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgetFragment extends Fragment {

    private static final String TAG = ForgetFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.email) TextInputLayout edtEmail;
    @BindView(R.id.btn_forget) Button btnForget;
    @BindView(R.id.txtEmail) TextInputEditText txtEmail;

    private ApiServices services;
    private Call<AuthResponse> call;

    private String email, msg = "MSG";

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private OnFragmentInteractionListener mListener;

    public ForgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgetFragment newInstance(String param1, String param2) {
        ForgetFragment fragment = new ForgetFragment();
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
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_forget, container, false);
        ButterKnife.bind(this, root);
        progressUtils = new ProgressUtils(getContext());
        keyboardUtils = new KeyboardUtils();
        services = RetrofitBuilder.createServices(ApiServices.class);
        progressUtils.hide();
        txtEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    forget();
                }
                return false;
            }
        });
        return root;
    }

    private boolean validate() {
        boolean valid = true;

        email = Objects.requireNonNull(edtEmail.getEditText()).getText().toString();

        if (!isEmail(email)) {
            edtEmail.setError(getString(R.string.txtErrorEmail1));
            valid = false;
        } else if (email.isEmpty()) {
            edtEmail.setError(getString(R.string.txtErrorEmail2));
            valid = false;
        } else {
            edtEmail.setError(null);
        }

        return valid;
    }

    private static boolean isEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @OnClick(R.id.btn_forget) void forget() {

        keyboardUtils.hideSoftKeyboard(getActivity());

        if (!validate()) {
            //Toast.makeText(getActivity(), getString(R.string.txtErrorValid), Toast.LENGTH_SHORT).show();
            btnForget.setEnabled(true);
            return;
        }

        btnForget.setEnabled(false);

        progressUtils.show();

        email = Objects.requireNonNull(edtEmail.getEditText()).getText().toString();
        call = services.forget(email);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        AuthResponse res = response.body();
                        AuthJson json = res.getAuth();
                        if (json.getKode().equals("1")) {
                            btnForget.setEnabled(true);
                            txtEmail.setText(null);
                            progressUtils.hide();
                            PopupUtils.PopAuth(getContext(), "Success", json.getMessage());
                        } else {
                            btnForget.setEnabled(true);
                            txtEmail.setText(null);
                            progressUtils.hide();
                            Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        btnForget.setEnabled(true);
                        txtEmail.setText(null);
                        progressUtils.hide();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 502) {
                    btnForget.setEnabled(true);
                    txtEmail.setText(null);
                    progressUtils.hide();
                    Toast.makeText(getActivity(), "502 Connection Timed Out", Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    btnForget.setEnabled(true);
                    txtEmail.setText(null);
                    progressUtils.hide();
                    Toast.makeText(getActivity(), "404 Not Found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                btnForget.setEnabled(true);
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
