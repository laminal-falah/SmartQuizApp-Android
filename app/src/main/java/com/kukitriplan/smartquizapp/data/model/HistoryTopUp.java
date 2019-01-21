package com.kukitriplan.smartquizapp.data.model;

public class HistoryTopUp {

    private String nomor;
    private String jumlah;
    private String tanggal;

    public HistoryTopUp(String nomor, String jumlah, String tanggal) {
        this.nomor = nomor;
        this.jumlah = jumlah;
        this.tanggal = tanggal;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }
}
