package com.kukitriplan.smartquizapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kukitriplan.smartquizapp.api.ApiServices;
import com.kukitriplan.smartquizapp.api.RetrofitBuilder;
import com.kukitriplan.smartquizapp.data.json.AuthJson;
import com.kukitriplan.smartquizapp.data.response.AuthResponse;
import com.kukitriplan.smartquizapp.data.shared.SharedLoginManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class LogoutUtils {

    public static void Logout(@NonNull final Context context) {
        final ProgressUtils progressUtils = new ProgressUtils(context);
        final SharedLoginManager prefManager = new SharedLoginManager(context);
        ApiServices services = RetrofitBuilder.createServices(ApiServices.class);
        Call<AuthResponse> callLogout = services.logout(prefManager.getSpToken());
        callLogout.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        AuthResponse res = response.body();
                        AuthJson json = res.getAuth();
                        if (json.getKode().equals("1")) {
                            prefManager.clearShared();
                            progressUtils.hide();
                        }
                        progressUtils.hide();
                    } catch (Exception e) {
                        progressUtils.hide();
                        PopupUtils.loadError(context, "Error", e.getMessage());
                    }
                } else if (response.code() == 502) {
                    progressUtils.hide();
                    PopupUtils.loadError(context, "Error", "502");
                } else if (response.code() == 404) {
                    progressUtils.hide();
                    PopupUtils.loadError(context, "Error", "404");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                progressUtils.hide();
                PopupUtils.loadError(context, "Error", t.getMessage());
            }
        });
    }
}
