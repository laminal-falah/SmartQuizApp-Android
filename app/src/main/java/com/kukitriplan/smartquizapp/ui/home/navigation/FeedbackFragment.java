package com.kukitriplan.smartquizapp.ui.home.navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRatingBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.HomeJson;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.PopupUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedbackFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment {
    private static final String TAG = FeedbackFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;

    @BindView(R.id.textArea_information) EditText info;
    @BindView(R.id.ratingBarFeedback) AppCompatRatingBar rating;
    @BindView(R.id.btnFeedback) Button btnFeedback;

    private ApiServices services;
    private Call<HomeResponse> call;
    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private String strInfo;
    private float intRating;

    private OnFragmentInteractionListener mListener;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
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
        view = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, view);
        progressUtils = new ProgressUtils(getContext());
        keyboardUtils = new KeyboardUtils();
        prefManager = new SharedLoginManager(getContext());
        services = RetrofitBuilder.createServices(ApiServices.class);
        progressUtils.hide();
        rating.setClickable(true);
        rating.setNumStars(0);
        rating.setStepSize(1);
        info.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        info.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    feedback();
                }
                return false;
            }
        });
        return view;
    }

    private boolean validate() {
        boolean valid = true;

        strInfo = info.getText().toString();
        intRating = rating.getRating();

        if (strInfo.isEmpty()) {
            info.setError(getString(R.string.txtErrorEmail2));
            valid = false;
        } else {
            info.setError(null);
        }

        if (intRating == 0) {
            valid = false;
            Toast.makeText(getContext(), "Harap Pilih Rating !", Toast.LENGTH_SHORT).show();
        }
        return valid;
    }

    @OnClick(R.id.btnFeedback) void feedback() {
        keyboardUtils.hideSoftKeyboard(getActivity());
        keyboardUtils.setupUI(getView(), getActivity());

        if (!validate()) {
            btnFeedback.setEnabled(true);
            return;
        }

        btnFeedback.setEnabled(false);
        progressUtils.show();

        strInfo = info.getText().toString();
        intRating = rating.getRating();

        call = services.kirimFeedback(strInfo,intRating,prefManager.getSpToken(),"home","feedback",prefManager.getSpEmail());
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        HomeResponse res = response.body();
                        HomeJson json = res.getHome();
                        if (json.getKode().equals("1")) {
                            btnFeedback.setEnabled(true);
                            progressUtils.hide();
                            info.setText(null);
                            rating.setRating(0);
                            PopupUtils.loadError(view.getContext(), json.getTitle(), json.getMessage());
                        } else {
                            btnFeedback.setEnabled(true);
                            progressUtils.hide();
                            PopupUtils.loadError(view.getContext(), json.getTitle(), json.getMessage());
                        }
                    } catch (Exception e) {
                        btnFeedback.setEnabled(true);
                        progressUtils.hide();
                        PopupUtils.loadError(view.getContext(), "Error", e.getMessage());
                    }
                } else if (response.code() == 502) {
                    btnFeedback.setEnabled(true);
                    progressUtils.hide();
                    PopupUtils.loadError(view.getContext(), "Error", "502");
                } else if (response.code() == 404) {
                    btnFeedback.setEnabled(true);
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
