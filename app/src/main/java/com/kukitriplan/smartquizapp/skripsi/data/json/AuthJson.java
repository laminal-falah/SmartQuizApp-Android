package com.kukitriplan.smartquizapp.skripsi.data.json;

import com.kukitriplan.smartquizapp.skripsi.data.model.NotificationModel;
import com.kukitriplan.smartquizapp.skripsi.data.model.User;

public class AuthJson {
    private String kode;
    private String message;
    private User user;
    private NotificationModel notif;
    private boolean setLogin;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationModel getNotif() {
        return notif;
    }

    public void setNotif(NotificationModel notif) {
        this.notif = notif;
    }

    public boolean isSetLogin() {
        return setLogin;
    }

    public void setSetLogin(boolean setLogin) {
        this.setLogin = setLogin;
    }
}
