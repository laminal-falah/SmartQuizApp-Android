package com.kukitriplan.smartquizapp.ui.dashboard.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.DashboardJson;
import com.kukitriplan.smartquizapp.data.model.Kategori;
import com.kukitriplan.smartquizapp.data.model.Mapel;
import com.kukitriplan.smartquizapp.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.ui.dashboard.BuatSoalActivity;
import com.kukitriplan.smartquizapp.utils.ImageUtils;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class BuatKuisFragment extends Fragment {

    private static final String TAG = BuatKuisFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;

    @BindView(R.id.spinnerKategori) Spinner spKategori;
    @BindView(R.id.spinnerMapel) Spinner spMapel;
    @BindView(R.id.judulSoal) TextInputLayout judulSoal;
    @BindView(R.id.edtJudulSoal) TextInputEditText edtJudulSoal;
    @BindView(R.id.btnMinSoal) Button btnMinSoal;
    @BindView(R.id.tvJmlhSoal) TextView tvJmlhSoal;
    @BindView(R.id.btnAddSoal) Button btnAddSoal;
    @BindView(R.id.btnMinDurasi) Button btnMinDurasi;
    @BindView(R.id.tvDurasiSoal) TextView tvDurasiSoal;
    @BindView(R.id.btnAddDurasi) Button btnAddDurasi;
    @BindView(R.id.spinnerHrg) Spinner spHarga;
    @BindView(R.id.switchAcak) SwitchCompat switchAcak;
    @BindView(R.id.switchBahas) SwitchCompat switchBahas;
    @BindView(R.id.textArea_information) EditText edtDeskripsi;
    @BindView(R.id.cover) ImageView coverKuis;
    @BindView(R.id.btnPilihGambar) Button btnPilihGambar;
    @BindView(R.id.btnTmbhKuis) Button btnTmbhKuis;

    private TextView tvErrorKategori, tvErrorMapel, tvErrorHarga;

    private String idKategori, idMapel, judul, hargaKuis, deskripsi, acakKuis = "0", tmplBahas = "0";
    private int jmlhSoal = 5, durasiSoal = 5;

    private String[] harga = new String[] {
            "Pilih Harga Kuis",
            "0","2500","5000","10000","20000","25000","50000","100000"
    };

    private List<String> hargaList = new ArrayList<>(Arrays.asList(harga));

    private ArrayList<Kategori> kategoris;
    private ArrayList<Mapel> mapels;

    private SharedLoginManager prefManager;

    private ApiServices services;
    private Call<DashboardResponse> call;

    private Bitmap bitmap;
    private Uri filePath;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private OnFragmentInteractionListener mListener;

    public BuatKuisFragment() {
        // Required empty public constructor
    }

    public static BuatKuisFragment newInstance(String param1, String param2) {
        BuatKuisFragment fragment = new BuatKuisFragment();
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
        view = inflater.inflate(R.layout.fragment_buat_kuis, container, false);
        ButterKnife.bind(this,view);
        btnPilihGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilGallery();
            }
        });
        services = RetrofitBuilder.createServices(ApiServices.class);
        prefManager = new SharedLoginManager(getContext());
        keyboardUtils = new KeyboardUtils();
        progressUtils = new ProgressUtils(getContext());
        progressUtils.hide();

        tvJmlhSoal.setText(String.valueOf(jmlhSoal));
        btnMinSoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jmlhSoal <= 5) {
                    jmlhSoal = 5;
                } else {
                    jmlhSoal--;
                }
                tvJmlhSoal.setText(String.valueOf(jmlhSoal));
            }
        });
        btnAddSoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jmlhSoal++;
                tvJmlhSoal.setText(String.valueOf(jmlhSoal));
            }
        });

        tvDurasiSoal.setText(String.valueOf(durasiSoal));
        btnMinDurasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (durasiSoal <= 5) {
                    durasiSoal = 5;
                } else {
                    durasiSoal--;
                }
                tvDurasiSoal.setText(String.valueOf(durasiSoal));
            }
        });
        btnAddDurasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                durasiSoal++;
                tvDurasiSoal.setText(String.valueOf(durasiSoal));
            }
        });

        edtDeskripsi.setOnTouchListener(new View.OnTouchListener() {
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

        switchAcak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    acakKuis = "1";
                } else {
                    acakKuis = "0";
                }
            }
        });

        switchBahas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tmplBahas = "1";
                } else {
                    tmplBahas = "0";
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getKategoriMapel();
        getHarga();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                coverKuis.setImageURI(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void tampilGallery() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
    }

    private void kosong() {
        coverKuis.setImageResource(R.drawable.round_view_images);
    }

    // start kategori mapel
    private void getKategoriMapel() {
        progressUtils.show();
        call = services.getKonfigKuis(prefManager.getSpToken(), "dashboard", "buatKuis");
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();

                    if (json.getKode().equals("1")) {
                        progressUtils.hide();
                        kategoris = new ArrayList<>(Arrays.asList(json.getKategori()));
                        final List<Kategori> kategoriList = new ArrayList<>();
                        for (int i = 0; i < kategoris.size(); i++) {
                            kategoriList.add(new Kategori(
                                    kategoris.get(i).getId(),
                                    kategoris.get(i).getTitle(),
                                    kategoris.get(i).getIcon()
                            ));
                        }
                        ArrayAdapter<Kategori> kategoriArrayAdapter = new ArrayAdapter<Kategori>(getContext(), R.layout.spinner_item, kategoris) {
                            @Override
                            public boolean isEnabled(int position) {
                                if(position == 0) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if(position == 0) {
                                    // Set the hint text color gray
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };
                        kategoriArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                        spKategori.setAdapter(kategoriArrayAdapter);
                        spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    idKategori = kategoriList.get(position).getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                        mapels = new ArrayList<>(Arrays.asList(json.getMapel()));
                        final List<Mapel> mapelList = new ArrayList<>();
                        for (int i = 0; i < mapels.size(); i++) {
                            mapelList.add(new Mapel(
                                    mapels.get(i).getId(),
                                    mapels.get(i).getMapel()
                            ));
                        }
                        ArrayAdapter<Mapel> mapelAdapter = new ArrayAdapter<Mapel>(getContext(), R.layout.spinner_item, mapels){
                            @Override
                            public boolean isEnabled(int position){
                                if(position == 0) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if(position == 0){
                                    // Set the hint text color gray
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };
                        mapelAdapter.setDropDownViewResource(R.layout.spinner_item);
                        spMapel.setAdapter(mapelAdapter);
                        spMapel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position > 0) {
                                    idMapel = mapels.get(position).getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 502) {
                    Toast.makeText(getContext(), "502 Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 503) {
                    Toast.makeText(getContext(), "503", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(getContext(), "404 Not Found", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 403) {
                    Toast.makeText(getContext(), "403", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                t.getMessage();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    // end kategori mapel

    // start harga
    private void getHarga() {
        ArrayAdapter<String> hargaAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, hargaList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0) {
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        hargaAdapter.setDropDownViewResource(R.layout.spinner_item);
        spHarga.setAdapter(hargaAdapter);
        spHarga.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    hargaKuis = spHarga.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    // end harga

    // start validasi
    private boolean validate() {
        boolean valid = true;
        String nmKategori = spKategori.getSelectedItem().toString();
        String nmMapel = spMapel.getSelectedItem().toString();
        String nmHarga = spHarga.getSelectedItem().toString();
        tvErrorKategori = (TextView) spKategori.getSelectedView();
        tvErrorMapel = (TextView) spMapel.getSelectedView();
        tvErrorHarga = (TextView) spHarga.getSelectedView();

        judul = judulSoal.getEditText().getText().toString();
        deskripsi = edtDeskripsi.getText().toString();

        if (nmKategori.equals("Pilih Kategori Kuis")) {
            tvErrorKategori.setError("Harap Pilih Kategori Kuis !!!");
            valid = false;
        } else {
            valid = true;
        }

        if (nmMapel.equals("Pilih Mata Pelajaran Kuis")) {
            tvErrorMapel.setError("Harap Pilih Mata Pelajaran Kuis !!!");
            valid = false;
        } else {
            valid = true;
        }

        if (nmHarga.equals("Pilih Harga Kuis")) {
            tvErrorHarga.setError("Harap Pilih Harga Kuis !!!");
            valid = false;
        } else {
            valid = true;
        }

        if (judul.isEmpty()) {
            judulSoal.setError("Judul Tidak boleh kosong !");
            valid = false;
        } else {
            judulSoal.setError(null);
            valid = true;
        }

        if (deskripsi.isEmpty()) {
            edtDeskripsi.setError("Deskripsi tidak boleh kosong !");
            valid = false;
        } else {
            edtDeskripsi.setError(null);
            valid = true;
        }

        return valid;
    }
    // end validasi

    // start click tambah kuis
    @OnClick(R.id.btnTmbhKuis) void tambahKuis() {
        keyboardUtils.hideSoftKeyboard(getActivity());

        if (!validate()) {
            btnTmbhKuis.setEnabled(true);
            return;
        }
        btnTmbhKuis.setEnabled(false);
        progressUtils.show();

        String encoded = bitmap != null ? ImageUtils.bitmapToBase64String(bitmap, 60) : "default.png";

        judul = judulSoal.getEditText().getText().toString();
        deskripsi = edtDeskripsi.getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("idKategori", idKategori);
        params.put("idMapel", idMapel);
        params.put("judul", judul);
        params.put("deskripsi", deskripsi);
        params.put("cover", encoded);
        params.put("soal", String.valueOf(jmlhSoal));
        params.put("durasi", String.valueOf(durasiSoal));
        params.put("harga", hargaKuis);
        params.put("acakSoal", acakKuis);
        params.put("tmplBahas", tmplBahas);

        call = services.simpanKuis(prefManager.getSpToken(), "dashboard", "simpanKuis", prefManager.getSpEmail(), params);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        progressUtils.hide();
                        btnTmbhKuis.setEnabled(true);
                        Toast.makeText(getContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getContext(), BuatSoalActivity.class)
                                .putExtra("slugKuis", json.getSlugKuis())
                                .putExtra("judulKuis", json.getJudulKuis())
                                .putExtra("nomorSoal", json.getNomorSoal()));
                        Log.i(TAG, "onResponse: judul soal" + json.getJudulKuis() + " nomor soal" + json.getNomorSoal());
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        btnTmbhKuis.setEnabled(true);
                        prefManager.clearShared();
                        startActivity(new Intent(getContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getActivity(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        progressUtils.hide();
                        btnTmbhKuis.setEnabled(true);
                        Toast.makeText(getContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    btnTmbhKuis.setEnabled(true);
                    Toast.makeText(getContext(),"502 Connection Timed Out", Toast.LENGTH_LONG).show();
                } else if (response.code() == 503) {
                    progressUtils.hide();
                    btnTmbhKuis.setEnabled(true);
                    Toast.makeText(getContext(),"503", Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    btnTmbhKuis.setEnabled(true);
                    Toast.makeText(getContext(),"404 Not Found", Toast.LENGTH_LONG).show();
                } else if (response.code() == 403) {
                    progressUtils.hide();
                    btnTmbhKuis.setEnabled(true);
                    Toast.makeText(getContext(),"403", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    // end click tambah kuis

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
