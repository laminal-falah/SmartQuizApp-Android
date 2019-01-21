package com.kukitriplan.smartquizapp.data.model;

public class Kuis {
    private String judul;
    private String slug;
    private String soal;
    private String durasi;
    private String harga;
    private String cover;
    private String author;
    private float rating;
    private String deskripsi;
    private String id_kategori;
    private String nm_kategori;

    public Kuis(String judul, String slug, String soal, String durasi, String harga, String cover, String author, float rating) {
        this.judul = judul;
        this.slug = slug;
        this.soal = soal;
        this.durasi = durasi;
        this.harga = harga;
        this.cover = cover;
        this.author = author;
        this.rating = rating;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getId_kategori() {
        return id_kategori;
    }

    public void setId_kategori(String id_kategori) {
        this.id_kategori = id_kategori;
    }

    public String getNm_kategori() {
        return nm_kategori;
    }

    public void setNm_kategori(String nm_kategori) {
        this.nm_kategori = nm_kategori;
    }
}
