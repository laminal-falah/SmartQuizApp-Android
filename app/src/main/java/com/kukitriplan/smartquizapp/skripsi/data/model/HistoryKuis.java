package com.kukitriplan.smartquizapp.skripsi.data.model;

public class HistoryKuis {
    private String idNilai;
    private String nomor;
    private String namaKuis;
    private String nilai;
    private String harga;
    private String soal;
    private String durasi;
    private String benar;
    private String salah;
    private String waktu;
    private String tanggal;
    private float rating;

    public HistoryKuis(String idNilai, String nomor, String namaKuis, String nilai) {
        this.idNilai = idNilai;
        this.nomor = nomor;
        this.namaKuis = namaKuis;
        this.nilai = nilai;
    }

    public String getIdNilai() {
        return idNilai;
    }

    public void setIdNilai(String idNilai) {
        this.idNilai = idNilai;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getNamaKuis() {
        return namaKuis;
    }

    public void setNamaKuis(String namaKuis) {
        this.namaKuis = namaKuis;
    }

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getSoal() {
        return soal;
    }

    public void setSoal(String soal) {
        this.soal = soal;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
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

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
