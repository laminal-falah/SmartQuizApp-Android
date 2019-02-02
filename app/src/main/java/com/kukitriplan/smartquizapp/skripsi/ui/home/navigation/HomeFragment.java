package com.kukitriplan.smartquizapp.skripsi.ui.home.navigation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.adapter.KategoriAdapter;
import com.kukitriplan.smartquizapp.skripsi.adapter.ListKuisHomeAdapter;
import com.kukitriplan.smartquizapp.skripsi.adapter.PilihanAdapter;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.HomeJson;
import com.kukitriplan.smartquizapp.skripsi.data.model.Kategori;
import com.kukitriplan.smartquizapp.skripsi.data.model.Kuis;
import com.kukitriplan.smartquizapp.skripsi.data.model.Pilihan;
import com.kukitriplan.smartquizapp.skripsi.data.model.SnapModel;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.PopupUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private View home;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    @BindView(R.id.rv_kategori) RecyclerView rvKategori;
    @BindView(R.id.rv_kuis) RecyclerView rvKuis;
    @BindView(R.id.rv_list_kuis_kategori) RecyclerView rvListKuis;
    @BindView(R.id.tvError) TextView tvError;

    private SharedLoginManager prefManager;

    private ApiServices services;
    private Call<HomeResponse> call;

    private ArrayList<Kategori> kategoris;
    private ArrayList<Pilihan> pilihans;
    private ArrayList<Kuis> kuis;

    private KategoriAdapter kategoriAdapter;
    private PilihanAdapter pilihanAdapter;
    private ListKuisHomeAdapter listKuisAdapter;

    private ProgressUtils progressUtils;

    private String token, from, dest;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        progressUtils = new ProgressUtils(getActivity());
        progressUtils.hide();
    }

    @Override
    public void onResume() {
        super.onResume();
        home();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        home = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, home);

        prefManager = new SharedLoginManager(home.getContext());
        tvError.setVisibility(View.INVISIBLE);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        rvKategori.setLayoutManager(layoutManager);
        rvKategori.setItemAnimator(new DefaultItemAnimator());
        rvKategori.setHasFixedSize(true);

        rvKuis.setVisibility(View.INVISIBLE);
        DividerItemDecoration decoration = new DividerItemDecoration(rvKuis.getContext(), RecyclerView.VERTICAL);
        rvKuis.addItemDecoration(decoration);
        rvKuis.setLayoutManager(new LinearLayoutManager(getContext()));
        rvKuis.setItemAnimator(new DefaultItemAnimator());
        rvKuis.setHasFixedSize(true);

        rvListKuis.setVisibility(View.INVISIBLE);
        RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(), 3, RecyclerView.VERTICAL, false);
        rvListKuis.setLayoutManager(lm);
        rvListKuis.setItemAnimator(new DefaultItemAnimator());
        rvListKuis.setHasFixedSize(true);

        return home;
    }

    private void home() {
        progressUtils.show();
        token = prefManager.getSpToken();
        from = "home";
        dest = "fetchAll";
        services = RetrofitBuilder.createServices(ApiServices.class);
        call = services.getHome(token,from,dest);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        HomeResponse res = response.body();
                        HomeJson json = res.getHome();
                        if (json.getKode().equals("1")) {
                            prefManager.saveSPString(SharedLoginManager.SP_SALDO, json.getSaldo());
                            kategoris = new ArrayList<>(Arrays.asList(json.getKategori()));
                            List<Kategori> kategoriList = new ArrayList<>();
                            for (int i = 0; i < kategoris.size(); i++) {
                                kategoriList.add(new Kategori(
                                        kategoris.get(i).getId(),
                                        kategoris.get(i).getTitle(),
                                        kategoris.get(i).getIcon()
                                ));
                            }
                            kategoriAdapter = new KategoriAdapter(kategoris, getContext());
                            rvKategori.setAdapter(kategoriAdapter);
                            kategoriAdapter.notifyDataSetChanged();
                            if (json.getPilihan().length != 0) {
                                rvKuis.setVisibility(View.VISIBLE);
                                pilihanAdapter = new PilihanAdapter(getContext());
                                pilihans = new ArrayList<>(Arrays.asList(json.getPilihan()));
                                List<Pilihan> pilihanList = new ArrayList<>();
                                for (int i = 0; i < pilihans.size(); i++) {
                                    kuis = new ArrayList<>(Arrays.asList(pilihans.get(i).getKuis()));
                                    List<Kuis> kuisList = new ArrayList<>();
                                    for (int j = 0; j < kuis.size(); j++) {
                                        kuisList.add(new Kuis(
                                                kuis.get(j).getId_kuis(),
                                                kuis.get(j).getJudul(),
                                                kuis.get(j).getSlug(),
                                                kuis.get(j).getSoal(),
                                                kuis.get(j).getDurasi(),
                                                kuis.get(j).getHarga(),
                                                kuis.get(j).getCover(),
                                                kuis.get(j).getAuthor(),
                                                kuis.get(j).getRating()
                                        ));
                                    }
                                    pilihanAdapter.addSnap(new SnapModel(Gravity.CENTER_HORIZONTAL, pilihans.get(i).getTitle(), kuisList));
                                    rvKuis.setAdapter(pilihanAdapter);
                                    pilihanAdapter.notifyDataSetChanged();
                                }
                            }
                            if (json.getListKuis().length != 0) {
                                rvListKuis.setVisibility(View.VISIBLE);
                                kuis = new ArrayList<>(Arrays.asList(json.getListKuis()));
                                List<Kuis> kuisList = new ArrayList<>();
                                for (int i = 0; i < kuis.size(); i++) {
                                    kuisList.add(new Kuis(
                                            kuis.get(i).getId_kuis(),
                                            kuis.get(i).getJudul(),
                                            kuis.get(i).getSlug(),
                                            kuis.get(i).getSoal(),
                                            kuis.get(i).getDurasi(),
                                            kuis.get(i).getHarga(),
                                            kuis.get(i).getCover(),
                                            kuis.get(i).getAuthor(),
                                            kuis.get(i).getRating()
                                    ));
                                }
                                listKuisAdapter = new ListKuisHomeAdapter(kuis, getActivity());
                                rvListKuis.setAdapter(listKuisAdapter);
                                new GravitySnapHelper(Gravity.TOP).attachToRecyclerView(rvListKuis);
                                listKuisAdapter.notifyDataSetChanged();
                            }

                            progressUtils.hide();
                        } else if (json.getKode().equals("2")) {
                            prefManager.clearShared();
                            startActivity(new Intent(home.getContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            progressUtils.hide();
                            tvError.setVisibility(View.VISIBLE);
                            tvError.setText(json.getMessage());
                            //SnackBarUtils.SnackBarUtils(home, json.getMessage(), Snackbar.LENGTH_SHORT);
                            //prefManager.clearShared();
                            /*startActivity(new Intent(getContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));*/
                            //Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressUtils.hide();
                        PopupUtils.loadError(home.getContext(), "Error", e.getMessage());
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    PopupUtils.loadError(home.getContext(), "Error", "502");
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    PopupUtils.loadError(home.getContext(), "Error", "404");
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                progressUtils.hide();
                PopupUtils.loadError(home.getContext(), "Error", t.getMessage());
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
