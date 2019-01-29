package com.kukitriplan.smartquizapp.skripsi.data.model;

import java.util.List;

public class SnapModel {
    private final int gravity;
    private final String text;
    private final List<Kuis> kuisList;

    public SnapModel(int gravity, String text, List<Kuis> kuisList) {
        this.gravity = gravity;
        this.text = text;
        this.kuisList = kuisList;
    }

    public int getGravity() {
        return gravity;
    }

    public String getText() {
        return text;
    }

    public List<Kuis> getKuisList() {
        return kuisList;
    }
}
