package com.kukitriplan.smartquizapp.ui.dashboard.navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kukitriplan.smartquizapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BuatSoalFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;

    @BindView(R.id.tvJudulKuis) TextView tvJudulKuis;
    @BindView(R.id.tvNomor) TextView tvNomor;
    @BindView(R.id.textAreaSoal) EditText edtAreaSoal;
    @BindView(R.id.rgPilihan) RadioGroup rgPilihan;
    @BindView(R.id.rbA) RadioButton radioButtonA;
    @BindView(R.id.edtPilihanA) EditText edtPilihanA;
    @BindView(R.id.rbB) RadioButton radioButtonB;
    @BindView(R.id.edtPilihanB) EditText edtPilihanB;
    @BindView(R.id.rbC) RadioButton radioButtonC;
    @BindView(R.id.edtPilihanC) EditText edtPilihanC;
    @BindView(R.id.rbD) RadioButton radioButtonD;
    @BindView(R.id.edtPilihanD) EditText edtPilihanD;
    @BindView(R.id.rbE) RadioButton radioButtonE;
    @BindView(R.id.edtPilihanE) EditText edtPilihanE;
    @BindView(R.id.textAreaBahas) EditText edtAreaBahas;
    @BindView(R.id.btnTmbhSoal) Button btnTmbhSoal;

    private String judulKuis, nomorSoal, areaSoal, areaBahas, pilihanA, pilihanB, pilihanC, pilihanD, pilihanE;

    private OnFragmentInteractionListener mListener;

    public BuatSoalFragment() {
        // Required empty public constructor
    }

    public static BuatSoalFragment newInstance(String param1, String param2) {
        BuatSoalFragment fragment = new BuatSoalFragment();
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
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_buat_soal, container, false);
        ButterKnife.bind(this, view);
        judulKuis = getArguments().getString("judulSoal");
        nomorSoal = getArguments().getString("nomorSoal");
        tvJudulKuis.setText(judulKuis);
        tvNomor.setText(getResources().getString(R.string.txtNomorSoal, nomorSoal));
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
