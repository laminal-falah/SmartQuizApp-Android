package com.kukitriplan.smartquizapp.data.json;

import com.kukitriplan.smartquizapp.data.model.HistoryWithDraw;
import com.kukitriplan.smartquizapp.data.model.Kategori;
import com.kukitriplan.smartquizapp.data.model.Kuis;
import com.kukitriplan.smartquizapp.data.model.Mapel;
import com.kukitriplan.smartquizapp.data.model.Soal;

public class DashboardJson {
    private String kode;
    private String title;
    private String message;
    private int jumlahSoal;

    private String slugKuis;
    private String judulKuis;
    private int nomorSoal;

    private Kuis kuis;
    private Soal soal;

    private Kategori[] kategori;
    private Mapel[] mapel;
    private Kuis[] kuisList;
    private Soal[] soalList;
    private HistoryWithDraw[] historyList;
    private int saldo;

    private boolean setLogin;

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

    public String getSlugKuis() {
        return slugKuis;
    }

    public void setSlugKuis(String slugKuis) {
        this.slugKuis = slugKuis;
    }

    public String getJudulKuis() {
        return judulKuis;
    }

    public void setJudulKuis(String judulKuis) {
        this.judulKuis = judulKuis;
    }

    public int getNomorSoal() {
        return nomorSoal;
    }

    public void setNomorSoal(int nomorSoal) {
        this.nomorSoal = nomorSoal;
    }

    public Kuis getKuis() {
        return kuis;
    }

    public void setKuis(Kuis kuis) {
        this.kuis = kuis;
    }

    public Soal getSoal() {
        return soal;
    }

    public void setSoal(Soal soal) {
        this.soal = soal;
    }

    public Kategori[] getKategori() {
        return kategori;
    }

    public void setKategori(Kategori[] kategori) {
        this.kategori = kategori;
    }

    public Mapel[] getMapel() {
        return mapel;
    }

    public void setMapel(Mapel[] mapel) {
        this.mapel = mapel;
    }

    public Kuis[] getKuisList() {
        return kuisList;
    }

    public void setKuisList(Kuis[] kuisList) {
        this.kuisList = kuisList;
    }

    public Soal[] getSoalList() {
        return soalList;
    }

    public void setSoalList(Soal[] soalList) {
        this.soalList = soalList;
    }

    public boolean isSetLogin() {
        return setLogin;
    }

    public void setSetLogin(boolean setLogin) {
        this.setLogin = setLogin;
    }

    public int getJumlahSoal() {
        return jumlahSoal;
    }

    public void setJumlahSoal(int jumlahSoal) {
        this.jumlahSoal = jumlahSoal;
    }

    public HistoryWithDraw[] getHistoryList() {
        return historyList;
    }

    public void setHistoryList(HistoryWithDraw[] historyList) {
        this.historyList = historyList;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }
}
