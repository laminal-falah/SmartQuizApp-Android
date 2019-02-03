package com.kukitriplan.smartquizapp.skripsi.data.model;

import android.support.annotation.NonNull;

public class Kategori {

    private String id;
    private String title;
    private String icon;

    public Kategori(String id, String title, String icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

}
