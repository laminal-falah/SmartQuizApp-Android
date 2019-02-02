package com.kukitriplan.smartquizapp.skripsi.ui.dashboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
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
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.DashboardJson;
import com.kukitriplan.smartquizapp.skripsi.data.model.Kategori;
import com.kukitriplan.smartquizapp.skripsi.data.model.Mapel;
import com.kukitriplan.smartquizapp.skripsi.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.ImageUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SetOrientationUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

public class EditKuisActivity extends AppCompatActivity {

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
    @BindView(R.id.btnUbahKuis) Button btnUbahKuis;
    @BindView(R.id.nestedScrollView2) NestedScrollView nestedScrollView;

    private TextView tvErrorKategori, tvErrorMapel, tvErrorHarga;

    private String idKategori, idMapel, judul, hargaKuis, deskripsi, acakKuis = "0", tmplBahas = "0";
    private int jmlhSoal = 5, durasiSoal = 5;

    private String ID = "0";

    private String[] harga = new String[] {
            "Pilih Harga Kuis",
            "0","2500","5000","10000","20000","25000","50000","100000"
    };

    private List<String> hargaList = new ArrayList<>(Arrays.asList(harga));

    private List<Kategori> kategoriList;
    private ArrayAdapter<Kategori> kategoriArrayAdapter;

    private ArrayList<Kategori> kategoris;
    private ArrayList<Mapel> mapels;

    private SharedLoginManager prefManager;

    private ApiServices services;
    private Call<DashboardResponse> call;

    private Bitmap bitmap;
    private Uri filePath;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetOrientationUtils.SetTitle(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kuis);
        ButterKnife.bind(this);
        btnPilihGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilGallery();
            }
        });
        coverKuis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tampilGallery();
                return false;
            }
        });
        ID = getIntent().getStringExtra("ID");
        services = RetrofitBuilder.createServices(ApiServices.class);
        prefManager = new SharedLoginManager(this);
        keyboardUtils = new KeyboardUtils();
        progressUtils = new ProgressUtils(this);
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

        getKuis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getKategoriMapel();
        getHarga();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
        call = services.getItemKuis(prefManager.getSpToken(), "dashboard", "editKuis", ID);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();

                    if (json.getKode().equals("1")) {
                        progressUtils.hide();
                        kategoris = new ArrayList<>(Arrays.asList(json.getKategori()));
                        kategoriList = new ArrayList<>();
                        for (int i = 0; i < kategoris.size(); i++) {
                            kategoriList.add(new Kategori(
                                    kategoris.get(i).getId(),
                                    kategoris.get(i).getTitle(),
                                    kategoris.get(i).getIcon()
                            ));
                        }
                        kategoriArrayAdapter = new ArrayAdapter<Kategori>(getApplicationContext(), R.layout.spinner_item, kategoris) {
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
                                    Log.i("TAG", "onItemSelected: Kategori " + kategoriList.get(position).getId());
                                    idKategori = kategoriList.get(position).getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        for (int i = 0; i < spKategori.getCount(); i++) {
                            if (json.getKuis().getNm_kategori().equalsIgnoreCase(spKategori.getItemAtPosition(i).toString()))
                            spKategori.setSelection(i);
                        }

                        mapels = new ArrayList<>(Arrays.asList(json.getMapel()));
                        final List<Mapel> mapelList = new ArrayList<>();
                        for (int i = 0; i < mapels.size(); i++) {
                            mapelList.add(new Mapel(
                                    mapels.get(i).getId(),
                                    mapels.get(i).getMapel()
                            ));
                        }
                        ArrayAdapter<Mapel> mapelAdapter = new ArrayAdapter<Mapel>(getApplicationContext(), R.layout.spinner_item, mapels){
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
                                    Log.i("TAG", "onItemSelected: Mapel " + mapels.get(position).getId());
                                    idMapel = mapels.get(position).getId();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        for (int i = 0; i < spMapel.getCount(); i++) {
                            if (json.getKuis().getNm_mapel().equalsIgnoreCase(spMapel.getItemAtPosition(i).toString()))
                                spMapel.setSelection(i);
                        }
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (response.code() == 502) {
                    Toast.makeText(getApplicationContext(), "502 Connection Timed Out", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 503) {
                    Toast.makeText(getApplicationContext(), "503", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 404) {
                    Toast.makeText(getApplicationContext(), "404 Not Found", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 403) {
                    Toast.makeText(getApplicationContext(), "403", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                t.getMessage();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    // end kategori mapel

    // start harga
    private void getHarga() {
        ArrayAdapter<String> hargaAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, hargaList){
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

    // start getKuis
    private void getKuis() {
        progressUtils.show();
        call = services.getItemKuis(prefManager.getSpToken(), "dashboard","editKuis", ID);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        edtJudulSoal.setText(json.getKuis().getJudul());
                        edtDeskripsi.setText(json.getKuis().getDeskripsi());
                        Picasso.with(getApplicationContext()).load(json.getKuis().getCover())
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        coverKuis.setImageBitmap(bitmap);
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                        jmlhSoal = Integer.valueOf(json.getKuis().getSoal());
                        durasiSoal = Integer.valueOf(json.getKuis().getDurasi());

                        int selectedHarga = 0;
                        for (int i = 0; i < harga.length; i++) {
                            if (json.getKuis().getHarga().equalsIgnoreCase(harga[i]))
                                selectedHarga = i;
                        }
                        spHarga.setSelection(selectedHarga);
                        tvJmlhSoal.setText(json.getKuis().getSoal());
                        tvDurasiSoal.setText(json.getKuis().getDurasi());
                        switchAcak.setChecked(true);
                        if (json.getKuis().getAcak().equals("1")) {
                            switchAcak.setChecked(true);
                        } else {
                            switchAcak.setChecked(false);
                        }
                        if (json.getKuis().getBahas().equals("1")) {
                            switchBahas.setChecked(true);
                        } else {
                            switchBahas.setChecked(false);
                        }
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        btnUbahKuis.setEnabled(true);
                        prefManager.clearShared();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        progressUtils.hide();
                        nestedScrollView.setVisibility(View.INVISIBLE);
                        btnUbahKuis.setEnabled(true);
                        Toast.makeText(getApplicationContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    nestedScrollView.setVisibility(View.INVISIBLE);
                    btnUbahKuis.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"502 Connection Timed Out", Toast.LENGTH_LONG).show();
                } else if (response.code() == 503) {
                    progressUtils.hide();
                    nestedScrollView.setVisibility(View.INVISIBLE);
                    btnUbahKuis.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"503", Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    nestedScrollView.setVisibility(View.INVISIBLE);
                    btnUbahKuis.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"404 Not Found", Toast.LENGTH_LONG).show();
                } else if (response.code() == 403) {
                    progressUtils.hide();
                    nestedScrollView.setVisibility(View.INVISIBLE);
                    btnUbahKuis.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"403", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                progressUtils.hide();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    // end getKuis

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

    @OnClick(R.id.btnUbahKuis) void ubahKuis() {
        keyboardUtils.hideSoftKeyboard(this);
        if (!validate()) {
            btnUbahKuis.setEnabled(true);
            return;
        }
        btnUbahKuis.setEnabled(false);
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

        call = services.ubahKuis(prefManager.getSpToken(), "dashboard", "ubahKuis", ID, params);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        progressUtils.hide();
                        btnUbahKuis.setEnabled(true);
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        btnUbahKuis.setEnabled(true);
                        prefManager.clearShared();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        progressUtils.hide();
                        btnUbahKuis.setEnabled(true);
                        Toast.makeText(getApplicationContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    btnUbahKuis.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"502 Connection Timed Out", Toast.LENGTH_LONG).show();
                } else if (response.code() == 503) {
                    progressUtils.hide();
                    btnUbahKuis.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"503", Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    btnUbahKuis.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"404 Not Found", Toast.LENGTH_LONG).show();
                } else if (response.code() == 403) {
                    progressUtils.hide();
                    btnUbahKuis.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"403", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                progressUtils.hide();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        kosong();
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                coverKuis.setImageURI(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
