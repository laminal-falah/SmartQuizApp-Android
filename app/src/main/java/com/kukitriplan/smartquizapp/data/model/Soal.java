package com.kukitriplan.smartquizapp.data.model;

public class Soal {
    private String nomorSoal;
    private String idKuis;
    private String idSoal;
    private String judulSoal;
    private String A;
    private String B;
    private String C;
    private String D;
    private String E;
    private String kunciJawaban;
    private String pembahasan;

    public Soal(String idKuis, String idSoal, String judulSoal, String a, String b, String c, String d, String e, String kunciJawaban, String pembahasan) {
        this.idKuis = idKuis;
        this.idSoal = idSoal;
        this.judulSoal = judulSoal;
        this.A = a;
        this.B = b;
        this.C = c;
        this.D = d;
        this.E = e;
        this.kunciJawaban = kunciJawaban;
        this.pembahasan = pembahasan;
    }

    public String getNomorSoal() {
        return nomorSoal;
    }

    public void setNomorSoal(String nomorSoal) {
        this.nomorSoal = nomorSoal;
    }

    public String getIdKuis() {
        return idKuis;
    }

    public void setIdKuis(String idKuis) {
        this.idKuis = idKuis;
    }

    public String getIdSoal() {
        return idSoal;
    }

    public void setIdSoal(String idSoal) {
        this.idSoal = idSoal;
    }

    public String getJudulSoal() {
        return judulSoal;
    }

    public void setJudulSoal(String judulSoal) {
        this.judulSoal = judulSoal;
    }

    public String getA() {
        return A;
    }

    public void setA(String a) {
        this.A = a;
    }

    public String getB() {
        return B;
    }

    public void setB(String b) {
        this.B = b;
    }

    public String getC() {
        return C;
    }

    public void setC(String c) {
        this.C = c;
    }

    public String getD() {
        return D;
    }

    public void setD(String d) {
        this.D = d;
    }

    public String getE() {
        return E;
    }

    public void setE(String e) {
        this.E = e;
    }

    public String getKunciJawaban() {
        return kunciJawaban;
    }

    public void setKunciJawaban(String kunciJawaban) {
        this.kunciJawaban = kunciJawaban;
    }

    public String getPembahasan() {
        return pembahasan;
    }

    public void setPembahasan(String pembahasan) {
        this.pembahasan = pembahasan;
    }

    @Override
    public String toString() {
        return "Soal{" +
                "nomorSoal='" + nomorSoal + '\'' +
                ", idKuis='" + idKuis + '\'' +
                ", idSoal='" + idSoal + '\'' +
                ", judulSoal='" + judulSoal + '\'' +
                ", A='" + A + '\'' +
                ", B='" + B + '\'' +
                ", C='" + C + '\'' +
                ", D='" + D + '\'' +
                ", E='" + E + '\'' +
                ", kunciJawaban='" + kunciJawaban + '\'' +
                ", pembahasan='" + pembahasan + '\'' +
                '}';
    }
}
