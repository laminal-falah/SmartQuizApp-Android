package com.kukitriplan.smartquizapp.data.model;

public class HistoryWithDraw {
    private String nomor;
    private String title;
    private String status;
    private String tanggal;
    private String namaBank;
    private String rekening;
    private String atasNama;

    public HistoryWithDraw(String nomor, String title, String status, String tanggal, String namaBank, String rekening, String atasNama) {
        this.nomor = nomor;
        this.title = title;
        this.status = status;
        this.tanggal = tanggal;
        this.namaBank = namaBank;
        this.rekening = rekening;
        this.atasNama = atasNama;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getNamaBank() {
        return namaBank;
    }

    public void setNamaBank(String namaBank) {
        this.namaBank = namaBank;
    }

    public String getRekening() {
        return rekening;
    }

    public void setRekening(String rekening) {
        this.rekening = rekening;
    }

    public String getAtasNama() {
        return atasNama;
    }

    public void setAtasNama(String atasNama) {
        this.atasNama = atasNama;
    }
}
