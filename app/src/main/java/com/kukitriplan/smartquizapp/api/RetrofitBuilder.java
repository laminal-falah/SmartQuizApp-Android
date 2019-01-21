package com.kukitriplan.smartquizapp.api;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.kukitriplan.smartquizapp.BuildConfig;
import com.kukitriplan.smartquizapp.utils.ConfigUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class RetrofitBuilder {

    private static final OkHttpClient client = buildClient();
    private static final Retrofit retrofit = buildRetrofit(client);

    @NonNull
    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @NonNull
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder builder = request.newBuilder()
                        .addHeader("Accept","application/json")
                        .addHeader("Connection", "close");
                request = builder.build();
                return chain.proceed(request);
            }
        });

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return builder.build();
    }

    @NonNull
    private static Retrofit buildRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(ConfigUtils.BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build();
    }

    public static <T> T createServices(Class<T> services) {
        return retrofit.create(services);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
