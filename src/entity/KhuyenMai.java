package entity;

import java.time.LocalDate;

public class KhuyenMai {

    private String maKM;
    private String tenKM;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private boolean trangThai;
    private boolean khuyenMaiHoaDon;
    private HinhThucKM hinhThuc;
    private double giaTri;
    private String dieuKienApDungHoaDon;
    private int soLuongToiThieu;
    private int soLuongTangThem;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKM, String tenKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, boolean trangThai, boolean khuyenMaiHoaDon, HinhThucKM hinhThuc, double giaTri, String dieuKienApDungHoaDon, int soLuongToiThieu, int soLuongTangThem) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
        this.khuyenMaiHoaDon = khuyenMaiHoaDon;
        this.hinhThuc = hinhThuc;
        this.giaTri = giaTri;
        this.dieuKienApDungHoaDon = dieuKienApDungHoaDon;
        this.soLuongToiThieu = soLuongToiThieu;
        this.soLuongTangThem = soLuongTangThem;
    }

    // --- GETTERS AND SETTERS ---

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        this.tenKM = tenKM;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isKhuyenMaiHoaDon() {
        return khuyenMaiHoaDon;
    }

    public void setKhuyenMaiHoaDon(boolean khuyenMaiHoaDon) {
        this.khuyenMaiHoaDon = khuyenMaiHoaDon;
    }

    public HinhThucKM getHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(HinhThucKM hinhThuc) {
        this.hinhThuc = hinhThuc;
    }

    public double getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(double giaTri) {
        this.giaTri = giaTri;
    }

    public String getDieuKienApDungHoaDon() {
        return dieuKienApDungHoaDon;
    }

    public void setDieuKienApDungHoaDon(String dieuKienApDungHoaDon) {
        this.dieuKienApDungHoaDon = dieuKienApDungHoaDon;
    }

    public int getSoLuongToiThieu() {
        return soLuongToiThieu;
    }

    public void setSoLuongToiThieu(int soLuongToiThieu) {
        this.soLuongToiThieu = soLuongToiThieu;
    }

    public int getSoLuongTangThem() {
        return soLuongTangThem;
    }

    public void setSoLuongTangThem(int soLuongTangThem) {
        this.soLuongTangThem = soLuongTangThem;
    }

    @Override
    public String toString() {
        return "KhuyenMai{" +
                "maKM='" + maKM + '\'' +
                ", tenKM='" + tenKM + '\'' +
                ", trangThai=" + trangThai +
                ", hinhThuc=" + hinhThuc +
                ", giaTri=" + giaTri +
                '}';
    }
}