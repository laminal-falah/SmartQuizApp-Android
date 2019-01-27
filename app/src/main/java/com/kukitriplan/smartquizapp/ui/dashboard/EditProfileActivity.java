package com.kukitriplan.smartquizapp.ui.dashboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.HomeJson;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.ui.home.HomeActivity;
import com.kukitriplan.smartquizapp.ui.home.navigation.UserFragment;
import com.kukitriplan.smartquizapp.utils.DatePickerSpinner;
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

public class EditProfileActivity extends AppCompatActivity implements UserFragment.OnFragmentInteractionListener {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private ApiServices services;
    private Call<HomeResponse> callProfile;

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;
    @BindView(R.id.toolbar_user) Toolbar toolbar;
    @BindView(R.id.nama) TextInputLayout tvNama;
    @BindView(R.id.email) TextInputLayout tvEmail;
    @BindView(R.id.tanggal) TextInputLayout tvTanggal;
    @BindView(R.id.btnTgl) Button btnTanggal;
    @BindView(R.id.tipeJenis) RadioGroup tipeJenis;
    @BindView(R.id.rb_laki) RadioButton rbLaki;
    @BindView(R.id.rb_perempuan) RadioButton rbPerempuan;
    @BindView(R.id.nope) TextInputLayout tvNope;
    @BindView(R.id.alamat) TextInputLayout tvAlamat;
    @BindView(R.id.namaBank) TextInputLayout tvNamaBank;
    @BindView(R.id.noRek) TextInputLayout tvNorek;
    @BindView(R.id.cbAtasNama) CheckBox cbAtasNama;
    @BindView(R.id.atasNama) TextInputLayout tvAtasNama;
    @BindView(R.id.btnUbahProfile) Button btnUbahProfile;

    @BindView(R.id.txtNama) TextInputEditText edtNama;
    @BindView(R.id.txtEmail) TextInputEditText edtEmail;
    @BindView(R.id.txtTgl) TextInputEditText edtTanggal;
    @BindView(R.id.txtNope) TextInputEditText edtNope;
    @BindView(R.id.txtAlamat) TextInputEditText edtAlamat;
    @BindView(R.id.txtNamaBank) TextInputEditText edtNamaBank;
    @BindView(R.id.txtNoRek) TextInputEditText edtNoRek;
    @BindView(R.id.txtAtasNama) TextInputEditText edtAtasNama;

    @BindView(R.id.nestedProfileEdit) NestedScrollView nsProfileEdit;
    @BindView(R.id.tvError) TextView tvError;

    private RadioButton radioButton;

    private String nama, email, tanggal, noTelp, alamat, nmBank, noRek, anBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        progressUtils = new ProgressUtils(this);
        keyboardUtils = new KeyboardUtils();
        progressUtils.hide();
        prefManager = new SharedLoginManager(this);
        toolbar.setTitle("Edit Profile " + prefManager.getSpName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        services = RetrofitBuilder.createServices(ApiServices.class);
        tvError.setVisibility(View.INVISIBLE);

        cbAtasNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                if (checked) {
                    edtAtasNama.setText(edtNama.getText().toString());
                } else {
                    edtAtasNama.setText(edtAtasNama.getText().toString());
                }

            }
        });
        edtTanggal.setEnabled(false);
    }

    private void back() {
        Log.i(TAG, "back: back pressed");
        if (prefManager.getSpLevel().equals("user")) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    private void getProfile() {
        nsProfileEdit.setVisibility(View.INVISIBLE);
        callProfile = services.getProfile(prefManager.getSpToken(), "home", "profileUser", prefManager.getSpEmail());
        callProfile.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    final HomeJson json = res.getHome();
                    nsProfileEdit.setVisibility(View.VISIBLE);
                    edtNama.setText(json.getUser().getNama());
                    edtEmail.setText(json.getUser().getEmail());
                    edtTanggal.setText(json.getUser().getLahir());
                    btnTanggal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment dialogFragment = DatePickerSpinner.newInstance("title", json.getUser().getLahir(), new DatePickerSpinner.OnPositiveClickListener() {
                                @Override
                                public void onClick(String date) {
                                    edtTanggal.setText(date);
                                }
                            });
                            dialogFragment.show(getSupportFragmentManager(), "dialog");
                        }
                    });
                    switch (json.getUser().getKelamin()) {
                        case "L":
                            rbLaki.setChecked(true);
                            break;
                        case "P":
                            rbPerempuan.setChecked(true);
                            break;
                        default:
                            tipeJenis.clearCheck();
                            break;
                    }
                    edtNope.setText(json.getUser().getNo());
                    edtAlamat.setText(json.getUser().getAlmt());
                    edtNamaBank.setText(json.getUser().getBank());
                    edtNoRek.setText(json.getUser().getRek());
                    edtAtasNama.setText(json.getUser().getAtasNama());
                } else if (response.code() == 502) {
                    nsProfileEdit.setVisibility(View.INVISIBLE);
                    progressUtils.hide();
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("502 Connection Timed Out");
                } else if (response.code() == 404) {
                    nsProfileEdit.setVisibility(View.INVISIBLE);
                    progressUtils.hide();
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("404 Not Found");
                }

            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                nsProfileEdit.setVisibility(View.INVISIBLE);
                progressUtils.hide();
                tvError.setVisibility(View.VISIBLE);
                tvError.setText(t.getMessage());
            }
        });
    }

    private boolean valid() {
        boolean valid = true;

        nama = tvNama.getEditText().getText().toString();
        email = tvEmail.getEditText().getText().toString();
        tanggal = tvTanggal.getEditText().getText().toString();
        noTelp = tvNope.getEditText().getText().toString();
        alamat = tvAlamat.getEditText().getText().toString();
        nmBank = tvNamaBank.getEditText().getText().toString();
        noRek = tvNorek.getEditText().getText().toString();
        anBank = tvAtasNama.getEditText().getText().toString();

        if (nama.isEmpty()) {
            tvNama.setError(getString(R.string.txtErrorName1));
            valid = false;
        } else {
            tvNama.setError(null);
        }

        if (!isEmail(email)) {
            tvEmail.setError(getString(R.string.txtErrorEmail1));
            valid = false;
        } else {
            tvEmail.setError(null);
        }

        if (tanggal.equals("-")) {
            tvTanggal.setError("Isi tanggal lahir");
            valid = false;
        } else {
            tvTanggal.setError(null);
        }

        if (tipeJenis.getCheckedRadioButtonId() == -1) {
            rbPerempuan.setError(getString(R.string.txtErrorTipe));
            valid = false;
        } else {
            rbPerempuan.setError(null);
        }

        if (noTelp.isEmpty()) {
            tvNope.setError("Isi nomor telepon");
            valid = false;
        } else {
            tvNope.setError(null);
        }

        if (alamat.isEmpty()) {
            tvAlamat.setError("Isi Alamat");
            valid = false;
        } else {
            tvAlamat.setError(null);
        }

        if (nmBank.isEmpty()) {
            tvNamaBank.setError("Isi Nama Bank");
            valid = false;
        } else {
            tvNamaBank.setError(null);
        }

        if (noRek.isEmpty()) {
            tvNorek.setError("Isi No. Rekening");
            valid = false;
        } else {
            tvNorek.setError(null);
        }

        if (anBank.isEmpty()) {
            tvAtasNama.setError("Isi Atas Nama !");
            valid = false;
        } else {
            tvAtasNama.setError(null);
        }
        return valid;
    }

    private static boolean isEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @OnClick(R.id.btnUbahProfile) void ubahProfile() {
        keyboardUtils.hideSoftKeyboard(this);
        if (!valid()) {
            btnUbahProfile.setEnabled(true);
            return;
        }
        btnUbahProfile.setEnabled(false);
        progressUtils.show();

        nama = tvNama.getEditText().getText().toString();
        email = tvEmail.getEditText().getText().toString();
        tanggal = tvTanggal.getEditText().getText().toString();
        noTelp = tvNope.getEditText().getText().toString();
        alamat = tvAlamat.getEditText().getText().toString();
        nmBank = tvNamaBank.getEditText().getText().toString();
        noRek = tvNorek.getEditText().getText().toString();
        anBank = tvAtasNama.getEditText().getText().toString();
        int selectid = tipeJenis.getCheckedRadioButtonId();
        radioButton = findViewById(selectid);
        String jk;
        if (radioButton.getText().equals("Laki - Laki")) {
            jk = "L";
        } else {
            jk = "P";
        }
        Map<String, String> params = new HashMap<>();
        params.put("mail", email);
        params.put("nama", nama);
        params.put("tgl", tanggal);
        params.put("jk", jk);
        params.put("no", noTelp);
        params.put("almt", alamat);
        params.put("nama_bank", nmBank);
        params.put("no_rek", noRek);
        params.put("atas_nama", anBank);

        callProfile = services.submitProfile(prefManager.getSpToken(), "home","ubahProfile", prefManager.getSpEmail(), params);
        callProfile.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                try {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    if (response.isSuccessful()) {
                        if (json.getKode().equals("1")) {
                            btnUbahProfile.setEnabled(true);
                            progressUtils.hide();
                            if (json.isSetLogin()) {
                                Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                                prefManager.clearShared();
                                startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                                onResume();
                            }
                            Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            btnUbahProfile.setEnabled(true);
                            progressUtils.hide();
                            Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else if (response.code() == 502) {
                        btnUbahProfile.setEnabled(true);
                        progressUtils.hide();
                        Toast.makeText(getApplicationContext(), "502 Connection Timed Out", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 404) {
                        btnUbahProfile.setEnabled(true);
                        progressUtils.hide();
                        Toast.makeText(getApplicationContext(), "404 Not Found", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    btnUbahProfile.setEnabled(true);
                    progressUtils.hide();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                t.getMessage();
                btnUbahProfile.setEnabled(true);
                progressUtils.hide();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //back();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
