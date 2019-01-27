package com.kukitriplan.smartquizapp.data.json;

import com.kukitriplan.smartquizapp.data.model.HistoryKuis;
import com.kukitriplan.smartquizapp.data.model.HistoryTopUp;
import com.kukitriplan.smartquizapp.data.model.Kategori;
import com.kukitriplan.smartquizapp.data.model.Kuis;
import com.kukitriplan.smartquizapp.data.model.Pilihan;
import com.kukitriplan.smartquizapp.data.model.Soal;
import com.kukitriplan.smartquizapp.data.model.User;

public class HomeJson {

    private String kode;
    private String title;
    private String message;

    private String saldo;
    private String harga;

    private Kuis kuis;
    private User user;
    private HistoryKuis historyKuisDetail;

    private Kategori[] kategori;
    private Pilihan[] pilihan;
    private Kuis[] listKuis;
    private Kuis[] kuisKategori;
    private Kuis[] kuisCari;
    private Soal[] soal;
    private HistoryTopUp[] historyTopUp;
    private HistoryKuis[] historyKuis;

    private boolean setLogin;
    private boolean setKuis;
    private boolean setAcak;
    private int durasi;

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public Kuis getKuis() {
        return kuis;
    }

    public void setKuis(Kuis kuis) {
        this.kuis = kuis;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HistoryKuis getHistoryKuisDetail() {
        return historyKuisDetail;
    }

    public void setHistoryKuisDetail(HistoryKuis historyKuisDetail) {
        this.historyKuisDetail = historyKuisDetail;
    }

    public Kategori[] getKategori() {
        return kategori;
    }

    public void setKategori(Kategori[] kategori) {
        this.kategori = kategori;
    }

    public Pilihan[] getPilihan() {
        return pilihan;
    }

    public void setPilihan(Pilihan[] pilihan) {
        this.pilihan = pilihan;
    }

    public Kuis[] getListKuis() {
        return listKuis;
    }

    public void setListKuis(Kuis[] listKuis) {
        this.listKuis = listKuis;
    }

    public Kuis[] getKuisKategori() {
        return kuisKategori;
    }

    public void setKuisKategori(Kuis[] kuisKategori) {
        this.kuisKategori = kuisKategori;
    }

    public Kuis[] getKuisCari() {
        return kuisCari;
    }

    public void setKuisCari(Kuis[] kuisCari) {
        this.kuisCari = kuisCari;
    }

    public Soal[] getSoal() {
        return soal;
    }

    public void setSoal(Soal[] soal) {
        this.soal = soal;
    }

    public HistoryTopUp[] getHistoryTopUp() {
        return historyTopUp;
    }

    public void setHistoryTopUp(HistoryTopUp[] historyTopUp) {
        this.historyTopUp = historyTopUp;
    }

    public HistoryKuis[] getHistoryKuis() {
        return historyKuis;
    }

    public void setHistoryKuis(HistoryKuis[] historyKuis) {
        this.historyKuis = historyKuis;
    }

    public boolean isSetLogin() {
        return setLogin;
    }

    public void setSetLogin(boolean setLogin) {
        this.setLogin = setLogin;
    }

    public boolean isSetKuis() {
        return setKuis;
    }

    public void setSetKuis(boolean setKuis) {
        this.setKuis = setKuis;
    }

    public boolean isSetAcak() {
        return setAcak;
    }

    public void setSetAcak(boolean setAcak) {
        this.setAcak = setAcak;
    }

    public int getDurasi() {
        return durasi;
    }

    public void setDurasi(int durasi) {
        this.durasi = durasi;
    }
}
