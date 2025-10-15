package entity;

import java.time.LocalDate;

public class ChiTietPhieuNhap {
    private PhieuNhap phieuNhap;
    private SanPham sanPham;
    private int soLuongNhap;
    private double donGiaNhap;
    private LocalDate ngaySanXuat;
    private double thanhTien;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(PhieuNhap phieuNhap, SanPham sanPham, int soLuongNhap, double donGiaNhap, LocalDate ngaySanXuat, double thanhTien) {
        this.phieuNhap = phieuNhap;
        this.sanPham = sanPham;
        this.soLuongNhap = soLuongNhap;
        this.donGiaNhap = donGiaNhap;
        this.ngaySanXuat = ngaySanXuat;
        this.thanhTien = thanhTien;
    }

    // Getters and Setters

    public PhieuNhap getPhieuNhap() {
        return phieuNhap;
    }

    public void setPhieuNhap(PhieuNhap phieuNhap) {
        this.phieuNhap = phieuNhap;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        this.soLuongNhap = soLuongNhap;
    }

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(double donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }

    public LocalDate getNgaySanXuat() {
        return ngaySanXuat;
    }

    public void setNgaySanXuat(LocalDate ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" +
                "phieuNhap=" + phieuNhap.getMaPhieuNhap() +
                ", sanPham=" + sanPham.getMaSanPham() +
                ", soLuongNhap=" + soLuongNhap +
                '}';
    }
}