package entity;

import java.time.LocalDate;

public class LoSanPham {

    private String maLo;
    private LocalDate hanSuDung;
    private int soLuongTon;
    private SanPham sanPham;

    public LoSanPham() {
    }

    public LoSanPham(String maLo, LocalDate hanSuDung, int soLuongTon, SanPham sanPham) {
        this.maLo = maLo;
        this.hanSuDung = hanSuDung;
        this.soLuongTon = soLuongTon;
        this.sanPham = sanPham;
    }

    // --- GETTERS AND SETTERS ---

    public String getMaLo() {
        return maLo;
    }

    public void setMaLo(String maLo) {
        this.maLo = maLo;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    @Override
    public String toString() {
        return "LoSanPham{" +
                "maLo='" + maLo + '\'' +
                ", hanSuDung=" + hanSuDung +
                ", soLuongTon=" + soLuongTon +
                ", sanPham=" + (sanPham != null ? sanPham.getTenSanPham() : "N/A") +
                '}';
    }
}