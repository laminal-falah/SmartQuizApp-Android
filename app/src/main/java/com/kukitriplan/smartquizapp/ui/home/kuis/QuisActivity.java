package com.kukitriplan.smartquizapp.ui.home.kuis;

import android.content.res.Configuration;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.data.model.Soal;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.utils.KeyboardUtils;
import com.kukitriplan.smartquizapp.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.utils.SetOrientationUtils;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class QuisActivity extends AppCompatActivity {

    @BindView(R.id.tvJudulKuis) TextView tvJudulKuis;
    @BindView(R.id.toolbar_user) Toolbar toolbar;

    private SharedLoginManager prefManager;
    private KeyboardUtils keyboardUtils;
    private ProgressUtils progressUtils;

    private ApiServices services;
    private Call<HomeResponse> call;

    private ArrayList<Soal> soals;

    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetOrientationUtils.SetFull(this);
        setContentView(R.layout.activity_quis);
        ButterKnife.bind(this);
        toolbar.setTitle(getIntent().getStringExtra("judul"));
        setSupportActionBar(toolbar);

        prefManager = new SharedLoginManager(this);
        progressUtils = new ProgressUtils(this);
        keyboardUtils = new KeyboardUtils();

        tvJudulKuis.setText(getIntent().getStringExtra("judul"));

    }

    private void getSoal() {

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_soal, menu);
        mCountDownTimer = new CountDownTimer(3000000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                menu.getItem(0).setTitle(getResources().getString(R.string.txtCountTimer,
                        String.format(Locale.getDefault(),"%02d",millisUntilFinished / (60 * 60 * 1000) % 24),
                        String.format(Locale.getDefault(),"%02d",millisUntilFinished / (60 * 1000) % 60),
                        String.format(Locale.getDefault(),"%02d",millisUntilFinished / 1000 % 60)));
            }

            @Override
            public void onFinish() {

            }
        }.start();
        return true;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Toast.makeText(getApplicationContext(), "Disabled Clicked Home", Toast.LENGTH_LONG).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(getApplicationContext(), "Disabled Back Apps", Toast.LENGTH_LONG).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
            Toast.makeText(getApplicationContext(), "Disabled Recent Apps", Toast.LENGTH_LONG).show();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_POWER) {
            Toast.makeText(getApplicationContext(), "Disabled Clicked Power", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
