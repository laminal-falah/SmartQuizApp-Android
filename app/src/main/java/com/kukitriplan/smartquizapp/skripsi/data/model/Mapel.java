package com.kukitriplan.smartquizapp.skripsi.data.model;

public class Mapel {
    private String id;
    private String mapel;

    public Mapel(String id, String mapel) {
        this.id = id;
        this.mapel = mapel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMapel() {
        return mapel;
    }

    public void setMapel(String mapel) {
        this.mapel = mapel;
    }

    @Override
    public String toString() {
        return mapel;
    }
}
