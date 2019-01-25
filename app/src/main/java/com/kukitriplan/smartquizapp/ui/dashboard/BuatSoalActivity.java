package com.kukitriplan.smartquizapp.ui.dashboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.DashboardJson;
import com.kukitriplan.smartquizapp.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuatSoalActivity extends AppCompatActivity {

    private static final String TAG = BuatSoalActivity.class.getSimpleName();

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

    private RadioButton radioButton;
    private String judulKuis, areaSoal, slug, areaBahas, pilihanA, pilihanB, pilihanC, pilihanD, pilihanE;
    private int nomorSoal;

    private SharedLoginManager prefManager;
    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private ApiServices services;
    private Call<DashboardResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_soal);
        ButterKnife.bind(this);
        progressUtils = new ProgressUtils(this);
        keyboardUtils = new KeyboardUtils();
        prefManager = new SharedLoginManager(this);
        services = RetrofitBuilder.createServices(ApiServices.class);
        slug = getIntent().getStringExtra("slugKuis");
        judulKuis = getIntent().getStringExtra("judulKuis");
        nomorSoal = getIntent().getIntExtra("nomorSoal",0);
        tvJudulKuis.setText(judulKuis);
        tvNomor.setText(getString(R.string.txtNomorSoal, String.valueOf(nomorSoal)));
        progressUtils.hide();
    }

    private boolean validate() {
        boolean valid = true;

        areaSoal = edtAreaSoal.getText().toString();
        areaBahas = edtAreaBahas.getText().toString();
        pilihanA = edtPilihanA.getText().toString();
        pilihanB = edtPilihanB.getText().toString();
        pilihanC = edtPilihanC.getText().toString();
        pilihanD = edtPilihanD.getText().toString();
        pilihanE = edtPilihanE.getText().toString();

        if (rgPilihan.getCheckedRadioButtonId() == -1) {
            radioButtonA.setError("Pilih Kunci Jawaban !");
            radioButtonB.setError("Pilih Kunci Jawaban !");
            radioButtonC.setError("Pilih Kunci Jawaban !");
            radioButtonD.setError("Pilih Kunci Jawaban !");
            radioButtonE.setError("Pilih Kunci Jawaban !");
            valid = false;
        } else {
            radioButtonA.setError(null);
            radioButtonB.setError(null);
            radioButtonC.setError(null);
            radioButtonD.setError(null);
            radioButtonE.setError(null);
        }

        if (areaSoal.isEmpty()) {
            edtAreaSoal.setError("Isi soal !");
            valid = false;
        } else {
            edtAreaSoal.setError(null);
        }

        if (pilihanA.isEmpty()) {
            edtPilihanA.setError("Tidak Boleh Kosong !");
            valid = false;
        } else {
            edtPilihanA.setError(null);
        }

        if (pilihanB.isEmpty()) {
            edtPilihanB.setError("Tidak Boleh Kosong !");
            valid = false;
        } else {
            edtPilihanB.setError(null);
        }

        if (pilihanC.isEmpty()) {
            edtPilihanC.setError("Tidak Boleh Kosong !");
            valid = false;
        } else {
            edtPilihanC.setError(null);
        }

        if (pilihanD.isEmpty()) {
            edtPilihanD.setError("Tidak Boleh Kosong !");
            valid = false;
        } else {
            edtPilihanD.setError(null);
        }

        if (pilihanE.isEmpty()) {
            edtPilihanE.setError("Tidak Boleh Kosong !");
            valid = false;
        } else {
            edtPilihanE.setError(null);
        }

        return valid;
    }

    @OnClick(R.id.btnTmbhSoal) void tambahSoal() {
        keyboardUtils.hideSoftKeyboard(this);
        if (!validate()) {
            btnTmbhSoal.setEnabled(true);
            return;
        }
        btnTmbhSoal.setEnabled(false);
        progressUtils.show();

        int jwb = rgPilihan.getCheckedRadioButtonId();
        radioButton = findViewById(jwb);
        String jawaban = radioButton.getText().toString() != null ? radioButton.getText().toString() : null;
        areaSoal = edtAreaSoal.getText().toString();
        areaBahas = edtAreaBahas.getText().toString();
        pilihanA = edtPilihanA.getText().toString();
        pilihanB = edtPilihanB.getText().toString();
        pilihanC = edtPilihanC.getText().toString();
        pilihanD = edtPilihanD.getText().toString();
        pilihanE = edtPilihanE.getText().toString();
        Map<String, String> params = new HashMap<>();
        params.put("soal",areaSoal);
        params.put("pilihanA", pilihanA);
        params.put("pilihanB", pilihanB);
        params.put("pilihanC", pilihanC);
        params.put("pilihanD", pilihanD);
        params.put("pilihanE", pilihanE);
        params.put("jawaban", jawaban);
        params.put("bahas",areaBahas);
        call = services.simpanSoal(prefManager.getSpToken(),"dashboard","simpanSoal", slug, params);
        call.enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful()) {
                    DashboardResponse res = response.body();
                    DashboardJson json = res.getDashboard();
                    if (json.getKode().equals("1")) {
                        progressUtils.hide();
                        btnTmbhSoal.setEnabled(true);
                        edtAreaSoal.setText(null);
                        edtAreaBahas.setText(null);
                        edtPilihanA.setText(null);
                        edtPilihanB.setText(null);
                        edtPilihanC.setText(null);
                        edtPilihanD.setText(null);
                        edtPilihanE.setText(null);
                        rgPilihan.clearCheck();
                        Toast.makeText(getApplicationContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                        slug = json.getSlugKuis();
                        tvNomor.setText(getString(R.string.txtNomorSoal, String.valueOf(json.getNomorSoal())));
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        progressUtils.hide();
                        btnTmbhSoal.setEnabled(true);
                        Toast.makeText(getApplicationContext(),json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    btnTmbhSoal.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"502 Connection Timed Out", Toast.LENGTH_LONG).show();
                } else if (response.code() == 503) {
                    progressUtils.hide();
                    btnTmbhSoal.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"503", Toast.LENGTH_LONG).show();
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    btnTmbhSoal.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"404 Not Found", Toast.LENGTH_LONG).show();
                } else if (response.code() == 403) {
                    progressUtils.hide();
                    btnTmbhSoal.setEnabled(true);
                    Toast.makeText(getApplicationContext(),"403", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DashboardActivity.class));
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
