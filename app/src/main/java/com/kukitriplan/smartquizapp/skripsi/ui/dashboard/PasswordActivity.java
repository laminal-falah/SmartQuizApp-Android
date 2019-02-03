package com.kukitriplan.smartquizapp.skripsi.ui.dashboard;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.HomeJson;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.ui.auth.AuthActivity;
import com.kukitriplan.smartquizapp.skripsi.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SetOrientationUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_user) Toolbar toolbar;
    @BindView(R.id.passwordOld) TextInputLayout edtPassOld;
    @BindView(R.id.passwordNew) TextInputLayout edtPassNew;
    @BindView(R.id.txtPassNew) TextInputEditText txtPassOld;
    @BindView(R.id.txtPassOld) TextInputEditText txtPassNew;
    @BindView(R.id.btnPassword) Button btnPassword;

    private ApiServices services;
    private Call<HomeResponse> call;

    private SharedLoginManager prefManager;

    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    String passOld, passNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetOrientationUtils.SetTitle(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        prefManager = new SharedLoginManager(this);
        toolbar.setTitle("Edit Password " + prefManager.getSpName());
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
        prefManager = new SharedLoginManager(this);
        keyboardUtils = new KeyboardUtils();
        progressUtils = new ProgressUtils(this);
        progressUtils.hide();

        txtPassNew.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    ubahPassword();
                }
                return false;
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        passOld = edtPassOld.getEditText().getText().toString();
        passNew = edtPassNew.getEditText().getText().toString();

        if (passOld.isEmpty()) {
            edtPassOld.setError("Password lama tidak boleh kosong");
            valid = false;
        } else if (passOld.length() < 8) {
            edtPassOld.setError("Password lama minimal 8 karakter");
            valid = false;
        } else {
            edtPassOld.setError(null);
        }

        if (passNew.isEmpty()) {
            edtPassNew.setError("Password baru tidak boleh kosong");
            valid = false;
        } else if (passNew.length() < 8) {
            edtPassNew.setError("Password baru minimal 8 karakter");
            valid = false;
        } else {
            edtPassNew.setError(null);
        }

        return  valid;
    }

    @OnClick(R.id.btnPassword) void ubahPassword() {
        keyboardUtils.hideSoftKeyboard(this);
        if (!validate()) {
            return;
        }

        progressUtils.show();

        passOld = edtPassOld.getEditText().getText().toString();
        passNew = edtPassNew.getEditText().getText().toString();

        Map<String, String> params = new HashMap<>();
        params.put("passold", passOld);
        params.put("passnew", passNew);

        call = services.submitPassword(prefManager.getSpToken(), "home", "ubahPassword", prefManager.getSpEmail(), params);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeResponse> call, @NonNull Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    if (json.getKode().equals("1")) {
                        txtPassOld.setText(null);
                        txtPassNew.setText(null);
                        progressUtils.hide();
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else if (json.getKode().equals("2")) {
                        progressUtils.hide();
                        prefManager.clearShared();
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        txtPassOld.setText(null);
                        txtPassNew.setText(null);
                        progressUtils.hide();
                        Toast.makeText(getApplicationContext(), json.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeResponse> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
    protected void onDestroy() {
        super.onDestroy();
    }
}
