package entity;

import java.time.LocalDate;
import java.util.List;

public class PhieuTra {

    private String maPhieuTra;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private boolean trangThai;
    private LocalDate ngayLapPhieu;
    
    // Dựa theo mối quan hệ 1..*, thuộc tính này nên là một danh sách.
    private List<ChiTietPhieuTra> chiTietPhieuTraList;

    public PhieuTra() {
    }

    public PhieuTra(String maPhieuTra, KhachHang khachHang, NhanVien nhanVien, boolean trangThai, LocalDate ngayLapPhieu, List<ChiTietPhieuTra> chiTietPhieuTraList) {
        this.maPhieuTra = maPhieuTra;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.trangThai = trangThai;
        this.ngayLapPhieu = ngayLapPhieu;
        this.chiTietPhieuTraList = chiTietPhieuTraList;
    }

    // --- GETTERS AND SETTERS ---

    public String getMaPhieuTra() {
        return maPhieuTra;
    }

    public void setMaPhieuTra(String maPhieuTra) {
        this.maPhieuTra = maPhieuTra;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDate getNgayLapPhieu() {
        return ngayLapPhieu;
    }

    public void setNgayLapPhieu(LocalDate ngayLapPhieu) {
        this.ngayLapPhieu = ngayLapPhieu;
    }

    public List<ChiTietPhieuTra> getChiTietPhieuTraList() {
        return chiTietPhieuTraList;
    }

    public void setChiTietPhieuTraList(List<ChiTietPhieuTra> chiTietPhieuTraList) {
        this.chiTietPhieuTraList = chiTietPhieuTraList;
    }

    @Override
    public String toString() {
        return "PhieuTra{" +
                "maPhieuTra='" + maPhieuTra + '\'' +
                ", khachHang=" + (khachHang != null ? khachHang.getTenKhachHang() : "N/A") +
                ", nhanVien=" + (nhanVien != null ? nhanVien.getTenNhanVien() : "N/A") +
                ", trangThai=" + trangThai +
                ", ngayLapPhieu=" + ngayLapPhieu +
                '}';
    }
}