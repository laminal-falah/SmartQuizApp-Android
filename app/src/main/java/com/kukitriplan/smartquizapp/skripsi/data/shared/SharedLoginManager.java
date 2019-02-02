package com.kukitriplan.smartquizapp.skripsi.data.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class SharedLoginManager {
    private static final String SP_APP = "SP_APP";
    public static final String SP_LEVEL = "SP_LEVEL";
    public static final String SP_NAME = "SP_NAME";
    public static final String SP_EMAIL = "SP_EMAIL";
    public static final String SP_LOGON = "SP_LOGON";
    public static final String SP_INTRO = "SP_INTRO";
    public static final String SP_TOKEN = "SP_TOKEN";
    public static final String SP_SALDO = "SP_SALDO";
    public static final String SP_IDKUIS = "SP_IDKUIS";
    public static final String SP_MAINKUIS = "SP_MAINKUIS";

    private final SharedPreferences sp;
    private final SharedPreferences.Editor editor;

    public SharedLoginManager(@NonNull Context context) {
        this.sp = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.apply();
    }

    public void saveSPString(String keySP, String value){
        editor.putString(keySP, value);
        editor.apply();
    }

    public void saveSPInt(String keySP, int value){
        editor.putInt(keySP, value);
        editor.apply();
    }

    public void saveSPBoolean(String keySP, boolean value){
        editor.putBoolean(keySP, value);
        editor.apply();
    }

    public String getSpToken() {
        return sp.getString(SP_TOKEN, "");
    }

    public String getSpLevel() {
        return sp.getString(SP_LEVEL, "");
    }

    public String getSpName() {
        return sp.getString(SP_NAME, "");
    }

    public String getSpEmail() {
        return sp.getString(SP_EMAIL, "");
    }

    public String getSpSaldo() {
        return sp.getString(SP_SALDO, "0");
    }

    public Boolean getSpLogon() {
        return sp.getBoolean(SP_LOGON, false);
    }

    public String getSpIdkuis() {
        return sp.getString(SP_IDKUIS, "");
    }

    public Boolean getMainKuis() {
        return sp.getBoolean(SP_MAINKUIS, false);
    }

    public void setSpIntro(boolean firstTime) {
        editor.putBoolean(SP_INTRO, firstTime);
        editor.commit();
    }

    public Boolean getSpIntro() {
        return sp.getBoolean(SP_INTRO, true);
    }

    public void clearShared() {
        editor.putString(SP_TOKEN, "");
        editor.putString(SP_LEVEL, "");
        editor.putString(SP_EMAIL, "");
        editor.putString(SP_NAME, "");
        editor.putBoolean(SP_LOGON, false);
        editor.commit();
    }
}
