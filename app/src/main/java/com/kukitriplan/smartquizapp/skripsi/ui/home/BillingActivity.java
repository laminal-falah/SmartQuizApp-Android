package com.kukitriplan.smartquizapp.skripsi.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kukitriplan.smartquizapp.R;
import com.kukitriplan.smartquizapp.skripsi.api.ApiServices;
import com.kukitriplan.smartquizapp.skripsi.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.skripsi.data.json.HomeJson;
import com.kukitriplan.smartquizapp.skripsi.data.response.HomeResponse;
import com.kukitriplan.smartquizapp.skripsi.data.shared.SharedLoginManager;
import com.kukitriplan.smartquizapp.skripsi.utilBilling.IabBroadcastReceiver;
import com.kukitriplan.smartquizapp.skripsi.utilBilling.IabHelper;
import com.kukitriplan.smartquizapp.skripsi.utilBilling.IabResult;
import com.kukitriplan.smartquizapp.skripsi.utilBilling.Inventory;
import com.kukitriplan.smartquizapp.skripsi.utilBilling.Purchase;
import com.kukitriplan.smartquizapp.skripsi.utils.ProgressUtils;
import com.kukitriplan.smartquizapp.skripsi.utils.SetOrientationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils.PUBLIC_KEY;
import static com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils.SKU_SALDO_10000;
import static com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils.SKU_SALDO_100000;
import static com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils.SKU_SALDO_20000;
import static com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils.SKU_SALDO_2500;
import static com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils.SKU_SALDO_25000;
import static com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils.SKU_SALDO_5000;
import static com.kukitriplan.smartquizapp.skripsi.utils.ConfigUtils.SKU_SALDO_50000;

public class BillingActivity extends AppCompatActivity {

    private static final String TAG = BillingActivity.class.getSimpleName();
    private ProgressUtils progressUtils;

    @BindView(R.id.toolbar_user) Toolbar toolbar;
    @BindView(R.id.nsLayout) NestedScrollView nsLayout;

    private SharedLoginManager prefManager;
    private ApiServices services;
    private Call<HomeResponse> call;

    // Biling
    private boolean isAlreadyPurchase;
    private String KEY_APP = PUBLIC_KEY;
    private String SALDO_2500 = SKU_SALDO_2500;
    private String SALDO_5000 = SKU_SALDO_5000;
    private String SALDO_10000 = SKU_SALDO_10000;
    private String SALDO_20000 = SKU_SALDO_20000;
    private String SALDO_25000 = SKU_SALDO_25000;
    private String SALDO_50000 = SKU_SALDO_50000;
    private String SALDO_100000 = SKU_SALDO_100000;
    private IabHelper mIabHelper;
    private IabBroadcastReceiver mIabBroadcastReceiver;
    private int RC_REQUEST = 115;
    private String PAYLOAD = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SetOrientationUtils.SetTitle(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        ButterKnife.bind(this);
        progressUtils = new ProgressUtils(this);
        toolbar.setTitle("Top up saldo dengan google");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressUtils.hide();

        if (!KEY_APP.contains("km8PF")) {
            throw new RuntimeException("Please put your app's public key in BillingActivity.java. See README.");
        }
        if (getPackageName().startsWith("com.kukitriplan.smartquizapp.skripsi.ui.home")) {
            throw new RuntimeException("Please change the sample's package name! See README.");
        }

        prefManager = new SharedLoginManager(this);
        services = RetrofitBuilder.createServices(ApiServices.class);

        initBiling();
    }

    private void initBiling() {
        mIabHelper = new IabHelper(getApplicationContext(), KEY_APP);
        mIabHelper.enableDebugLogging(true);
        Log.d(TAG, "Starting setup.");
        progressUtils.show();
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    complain("Error", "Problem setting up in-app billing: " + result);
                    return;
                }
                if (mIabHelper == null) return;

                mIabBroadcastReceiver = new IabBroadcastReceiver(new IabBroadcastReceiver.IabBroadcastListener() {
                    @Override
                    public void receivedBroadcast() {
                        Log.d(TAG, "Received broadcast notification. Querying inventory.");
                        try {
                            mIabHelper.queryInventoryAsync(getInventori());
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            complain("Error", "Error querying inventory. Another async operation in progress.");
                        }
                    }
                });
                IntentFilter intentFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mIabBroadcastReceiver, intentFilter);
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mIabHelper.queryInventoryAsync(getInventori());
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error", "Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    private IabHelper.QueryInventoryFinishedListener getInventori() {
        IabHelper.QueryInventoryFinishedListener mInventoryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                Log.d(TAG, "Query inventory finished.");
                if (mIabHelper == null) {
                    return;
                }
                if (result.isFailure()) {
                    complain("Error", "Failed to query inventory: " + result);
                    nsLayout.setVisibility(View.INVISIBLE);
                    progressUtils.hide();
                    return;
                }
                Log.d(TAG, "Query inventory was successful.");
                nsLayout.setVisibility(View.VISIBLE);
                progressUtils.hide();
            }
        };
        return mInventoryFinishedListener;
    }

    private IabHelper.OnIabPurchaseFinishedListener getPurchaseFinish(@NonNull final String s) {
        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                Log.d(TAG, "Purchase finished: " + result + ", purchase: " + info);
                if (mIabHelper == null) return;

                if (result.isFailure()) {
                    progressUtils.hide();
                    if (result.getResponse() == 1 || result.getResponse() == -1005) {
                        complain("Canceled", "Canceled top up saldo");
                        nsLayout.setVisibility(View.VISIBLE);
                    } else if (result.getResponse() == 7) {
                        complain("Error", "Item already owned");
                        nsLayout.setVisibility(View.VISIBLE);
                    } else {
                        complain("Error", "Error purchasing: " + result.getMessage());
                        nsLayout.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (!verifyDeveloperPayload(info)) {
                    complain("Error", "Error purchasing. Authenticity verification failed.");
                    return;
                }
                Log.d(TAG, "Purchase successful.");

                if (info.getSku().equals(s)) {
                    Log.d(TAG, "Purchase is saldo. Starting play quiz.");
                    try {
                        mIabHelper.consumeAsync(info, getConsumeFinish());
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        complain("Error", "Error consuming saldo. Another async operation in progress.");
                        return;
                    }
                }
            }
        };
        return mPurchaseFinishedListener;
    }

    private IabHelper.OnConsumeFinishedListener getConsumeFinish() {
        IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

                if (mIabHelper == null) return;

                if (result.isSuccess()) {
                    nsLayout.setVisibility(View.VISIBLE);
                    progressUtils.hide();
                    try {
                        JSONObject jsonObject = new JSONObject(purchase.getOriginalJson());
                        String orderId = jsonObject.getString("orderId");
                        String packageName = jsonObject.getString("packageName");
                        String productId = jsonObject.getString("productId");
                        String purchaseTime = jsonObject.getString("purchaseTime");
                        String purchaseToken = jsonObject.getString("purchaseToken");
                        String[] parts = productId.split("_");
                        String namaSaldo = parts[1];
                        progressUtils.show();
                        saveDb(orderId, packageName,namaSaldo, purchaseTime, purchaseToken);
                        alert("Success", "Top up saldo sebesar Rp " + namaSaldo + " Berhasil");
                        Log.i(TAG, "onConsumeFinished: JSON : " + jsonObject);
                    } catch (JSONException e) {
                        Log.e(TAG, "onConsumeFinished: JSON : ", e);
                    }
                    Log.d(TAG, "Consumption successful. Provisioning.");
                }
                else {
                    nsLayout.setVisibility(View.VISIBLE);
                    progressUtils.hide();
                    complain("Error while consuming: ", ""+result);
                }
                Log.d(TAG, "End consumption flow.");
                nsLayout.setVisibility(View.VISIBLE);
                progressUtils.hide();
            }
        };
        return mConsumeFinishedListener;
    }

    boolean verifyDeveloperPayload(Purchase p) {
        PAYLOAD = p.getDeveloperPayload();
        return true;
    }

    private void launchPurchase(@NonNull final String saldo) {
        nsLayout.setVisibility(View.INVISIBLE);
        progressUtils.show();
        try {
            mIabHelper.launchPurchaseFlow(this, saldo, RC_REQUEST, getPurchaseFinish(saldo), PAYLOAD);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error","Error launching purchase flow. Another async operation in progress.");
        }
    }

    private void saveDb(String orderId, String packageName, final String productId, String time, String token) {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        params.put("packageName", packageName);
        params.put("purchaseTime", time);
        params.put("purchaseToken", token);
        params.put("productId", productId);
        call = services.submitInapp(prefManager.getSpToken(), "home", "inapp", prefManager.getSpEmail(), params);
        call.enqueue(new Callback<HomeResponse>() {
            @Override
            public void onResponse(Call<HomeResponse> call, Response<HomeResponse> response) {
                if (response.isSuccessful()) {
                    HomeResponse res = response.body();
                    HomeJson json = res.getHome();
                    if (json.getKode().equals("1")) {
                        prefManager.saveSPString(SharedLoginManager.SP_SALDO, json.getSaldo());
                        Log.i(TAG, "onResponse: " + json.getMessage());
                        nsLayout.setVisibility(View.VISIBLE);
                        progressUtils.hide();
                    } else {
                        Log.i(TAG, "onResponse: " + json.getMessage());
                        nsLayout.setVisibility(View.VISIBLE);
                        progressUtils.hide();
                    }
                }
            }

            @Override
            public void onFailure(Call<HomeResponse> call, Throwable t) {
                progressUtils.hide();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btn2500) void bayar2500() {
        Log.d(TAG, "Launching purchase flow for sku2500.");
        launchPurchase(SALDO_2500);
    }

    @OnClick(R.id.btn5000) void bayar5000() {
        Log.d(TAG, "Launching purchase flow for sku5000.");
        launchPurchase(SALDO_5000);
    }

    @OnClick(R.id.btn10000) void bayar10() {
        Log.d(TAG, "Launching purchase flow for sku10000.");
        launchPurchase(SALDO_10000);
    }

    @OnClick(R.id.btn20000) void bayar20() {
        Log.d(TAG, "Launching purchase flow for sku20000.");
        launchPurchase(SALDO_20000);
    }

    @OnClick(R.id.btn25000) void bayar25() {
        Log.d(TAG, "Launching purchase flow for sku25000.");
        launchPurchase(SALDO_25000);
    }

    @OnClick(R.id.btn50000) void bayar50() {
        Log.d(TAG, "Launching purchase flow for sku50000.");
        launchPurchase(SALDO_50000);
    }

    @OnClick(R.id.btn100000) void bayar100() {
        Log.d(TAG, "Launching purchase flow for sku100000.");
        launchPurchase(SALDO_100000);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mIabHelper == null) return;
        if (!mIabHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIabBroadcastReceiver != null) {
            unregisterReceiver(mIabBroadcastReceiver);
        }
        Log.d(TAG, "Destroying helper.");
        if (mIabHelper != null) {
            mIabHelper.disposeWhenFinished();
            mIabHelper = null;
        }
    }

    void complain(String title, String message) {
        Log.e(TAG, "**** SmartquizApp Error: " + message);
        alert("Error: " ,message);
    }

    void alert(String title, String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle(title);
        bld.setMessage(message);
        bld.setCancelable(false);
        bld.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    /*
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
            Log.d(TAG, "Query inventory finished.");
            if (mIabHelper == null) {
                return;
            }
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            Log.d(TAG, "Query inventory was successful.");
            Purchase saldoPurchase = inv.getPurchase(SALDO_2500);
            if (saldoPurchase != null && verifyDeveloperPayload(saldoPurchase)) {
                Log.d(TAG, "We have saldo. Consuming it.");
                try {
                    mIabHelper.consumeAsync(inv.getPurchase(SALDO_2500), mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error consuming gas. Another async operation in progress.");
                }
                return;
            }
            //Toast.makeText(getApplicationContext(), "onQueryInventoryFinished : " + result, Toast.LENGTH_SHORT).show();
        }
    };
    */

    /*
    private void purchaseSaldo(@NonNull final String saldo) {
        String[] parts = saldo.split("_");
        final String s = parts[1];

        try {
            mIabHelper.launchPurchaseFlow(this, saldo, RC_REQUEST, new IabHelper.OnIabPurchaseFinishedListener() {
                @Override
                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                    if (mIabHelper == null) {
                        return;
                    }
                    if (result.isFailure()) {
                        complain("Failed to query inventory: " + result);
                        Toast.makeText(getApplicationContext(), "Cancel Buy Saldo InAppPurchase Rp " + s, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (info.getSku().equals(saldo)) {
                        Toast.makeText(getApplicationContext(), "Pesan : " + result + "\n" + "Saldo : Rp " + s + "\n" + "Info : " + info.toString(), Toast.LENGTH_LONG).show();
                        PopupUtils.popTopUp(getApplicationContext(), "InAppPurchase", "Pesan : " + result + "\n" + "Saldo : Rp " + s + "\n" + "Info : " + info.toString());
                        RC_REQUEST++;
                        mIabHelper.handleActivityResult(RC_REQUEST, 200, getParentActivityIntent());
                    }
                }
            }, PAYLOAD);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    */
}
