package com.kukitriplan.smartquizapp.data.model;

public class HistoryIkutKuis {
    private String nomor;
    private String author;
    private String judul;
    private String benar;
    private String salah;
    private String nilai;
    private String tanggal;
    private String pemain;
    private float rating;

    public HistoryIkutKuis(String nomor, String author, String judul, String benar, String salah, String nilai, String tanggal, String pemain, float rating) {
        this.nomor = nomor;
        this.author = author;
        this.judul = judul;
        this.benar = benar;
        this.salah = salah;
        this.nilai = nilai;
        this.tanggal = tanggal;
        this.pemain = pemain;
        this.rating = rating;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getBenar() {
        return benar;
    }

    public void setBenar(String benar) {
        this.benar = benar;
    }

    public String getSalah() {
        return salah;
    }

    public void setSalah(String salah) {
        this.salah = salah;
    }

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getPemain() {
        return pemain;
    }

    public void setPemain(String pemain) {
        this.pemain = pemain;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
