package com.kukitriplan.smartquizapp.api;

import com.kukitriplan.smartquizapp.data.response.AuthResponse;
import com.kukitriplan.smartquizapp.data.response.DashboardResponse;
import com.kukitriplan.smartquizapp.data.response.HomeResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiServices {

    @POST("api/v1/register")
    @FormUrlEncoded
    Call<AuthResponse> register (
            @Field("tipe_user") String tipe,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("api/v1/login")
    @FormUrlEncoded
    Call<AuthResponse> login (
            @Field("email") String email,
            @Field("password") String password
    );

    @POST("api/v1/forget-password")
    @FormUrlEncoded
    Call<AuthResponse> forget (
            @Field("email") String email
    );

    @POST("api/v1/logout")
    @FormUrlEncoded
    Call<AuthResponse> logout (
            @Field("_token") String token
    );

    @POST("api/v1/user/home")
    Call<HomeResponse> getHome(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d
    );

    @POST("api/v1/user/home")
    Call<HomeResponse> getKuis(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("id") String id
    );

    @POST("api/v1/user/home")
    Call<HomeResponse> getDetailKuis(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("slug") String slug
    );

    @POST("api/v1/user/home")
    Call<HomeResponse> getSearchKuis (
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("q") String q
    );

    @POST("api/v1/user/home")
    Call<HomeResponse> getHistoryTopUp(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("email") String email
    );

    @POST("api/v1/user/home")
    @FormUrlEncoded
    Call<HomeResponse> isiSaldo(
            @Field("kode") String kode,
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("email") String email
    );

    @POST("api/v1/user/home")
    @FormUrlEncoded
    Call<HomeResponse> kirimFeedback(
            @Field("info") String info,
            @Field("rating") float rating,
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("email") String email
    );

    @POST("api/v1/user/home")
    Call<HomeResponse> getProfile(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("email") String email
    );

    @POST("api/v1/user/home")
    @FormUrlEncoded
    Call<HomeResponse> submitProfile(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("email") String email,
            @FieldMap Map<String, String> params
    );

    @POST("api/v1/user/home")
    Call<HomeResponse> getHistoryKuis(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("email") String email
    );

    @POST("api/v1/user/home")
    @FormUrlEncoded
    Call<HomeResponse> submitPassword(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("email") String email,
            @FieldMap Map<String, String> params
    );

    @POST("api/v1/user/dashboard")
    Call<DashboardResponse> getKonfigKuis(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d
    );

    @POST("api/v1/user/dashboard")
    @FormUrlEncoded
    Call<DashboardResponse> simpanKuis(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("email") String email,
            @FieldMap Map<String, String> params
    );

    @POST("api/v1/user/dashboard")
    @FormUrlEncoded
    Call<DashboardResponse> simpanSoal(
            @Query("_token") String token,
            @Query("f") String f,
            @Query("d") String d,
            @Query("slug") String slug,
            @FieldMap Map<String, String> params
    );
}
