package com.kukitriplan.smartquizapp.data.shared;

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
    public static final String SP_NOTIF = "SP_NOTIF";
    public static final String SP_TITLE_NOTIF = "SP_TITLE_NOTIFICATION";
    public static final String SP_MSG_NOTIF = "SP_MESSAGE_NOTIFICATION";
    public static final String SP_SALDO = "SP_SALDO";

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

    public String getSpTitleNotif() {
        return sp.getString(SP_TITLE_NOTIF, "");
    }

    public String getSpMsgNotif() {
        return sp.getString(SP_MSG_NOTIF, "");
    }

    public Boolean getSpLogon() {
        return sp.getBoolean(SP_LOGON, false);
    }

    public Boolean getSpNotif() {
        return sp.getBoolean(SP_NOTIF, false);
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
        editor.putBoolean(SP_NOTIF, false);
        editor.putBoolean(SP_LOGON, false);
        editor.commit();
    }
}
