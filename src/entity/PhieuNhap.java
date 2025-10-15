package entity;

import java.time.LocalDate;
import java.util.List;

public class PhieuNhap {
    private String maPhieuNhap;
    private LocalDate ngayNhap;
    private NhaCungCap nhaCungCap;
    private NhanVien nhanVien;
    private double tongTien;

    private List<ChiTietPhieuNhap> chiTietPhieuNhapList;

    public PhieuNhap() {
    }

    public PhieuNhap(String maPhieuNhap, LocalDate ngayNhap, NhaCungCap nhaCungCap, NhanVien nhanVien, double tongTien) {
        this.maPhieuNhap = maPhieuNhap;
        this.ngayNhap = ngayNhap;
        this.nhaCungCap = nhaCungCap;
        this.nhanVien = nhanVien;
        this.tongTien = tongTien;
    }
    
    // Getters and Setters for all fields
    
    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public List<ChiTietPhieuNhap> getChiTietPhieuNhapList() {
        return chiTietPhieuNhapList;
    }

    public void setChiTietPhieuNhapList(List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        this.chiTietPhieuNhapList = chiTietPhieuNhapList;
    }

    @Override
    public String toString() {
        return "PhieuNhap{" +
                "maPhieuNhap='" + maPhieuNhap + '\'' +
                '}';
    }
}