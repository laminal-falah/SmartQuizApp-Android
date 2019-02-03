package com.kukitriplan.smartquizapp.skripsi.ui.home.kuis;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.HomeJson;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.home.HomeActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;
    private TextView tvNilai;
    private Button btnBack, btnSubmit;
    private AppCompatRatingBar rating;

    private SharedLoginManager prefManager;

    private ApiServices services;
    private Call<HomeResponse> call;

    private ProgressUtils progressUtils;

    private OnFragmentInteractionListener mListener;

    public ResultFragment() {
        // Required empty public constructor
    }

    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
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
        view = inflater.inflate(R.layout.fragment_result, container, false);
        progressUtils = new ProgressUtils(getContext());
        progressUtils.hide();
        services = RetrofitBuilder.createServices(ApiServices.class);
        prefManager = new SharedLoginManager(getContext());
        rating = view.findViewById(R.id.ratingBarKuis);
        rating.setClickable(true);
        rating.setNumStars(0);
        rating.setStepSize(1);
        tvNilai = view.findViewById(R.id.tvNilai);
        tvNilai.setText(String.valueOf(getArguments().getDouble("NILAI")));
        btnBack = view.findViewById(R.id.btnBackHome);
        btnBack.setVisibility(View.INVISIBLE);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Float> params = new HashMap<>();
                params.put("rating", rating.getRating());
                call = services.beriBintang(prefManager.getSpToken(), "home", "beriBintang", prefManager.getSpEmail(), getArguments().getString("IDKUIS"), params);
                call.enqueue(new Callback<HomeResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                        if (response.isSuccessful()) {
                            HomeResponse res = response.body();
                            HomeJson json = res.getHome();
                            if (json.getKode().equals("1")) {
                                rating.setClickable(false);
                                Toast.makeText(getContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            btnBack.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        return view;
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
