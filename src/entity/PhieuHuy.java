package entity;

import java.time.LocalDate;
import java.util.List;

public class PhieuHuy {
    private String maPhieuHuy;
    private LocalDate ngayLapPhieu;
    private NhanVien nhanVien;
    private String trangThai;

    private List<ChiTietPhieuHuy> chiTietPhieuHuyList;

    public PhieuHuy() {
    }

    public PhieuHuy(String maPhieuHuy, LocalDate ngayLapPhieu, NhanVien nhanVien, String trangThai) {
        this.maPhieuHuy = maPhieuHuy;
        this.ngayLapPhieu = ngayLapPhieu;
        this.nhanVien = nhanVien;
        this.trangThai = trangThai;
    }

    // Getters and Setters for all fields

    public String getMaPhieuHuy() {
        return maPhieuHuy;
    }

    public void setMaPhieuHuy(String maPhieuHuy) {
        this.maPhieuHuy = maPhieuHuy;
    }

    public LocalDate getNgayLapPhieu() {
        return ngayLapPhieu;
    }

    public void setNgayLapPhieu(LocalDate ngayLapPhieu) {
        this.ngayLapPhieu = ngayLapPhieu;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public List<ChiTietPhieuHuy> getChiTietPhieuHuyList() {
        return chiTietPhieuHuyList;
    }

    public void setChiTietPhieuHuyList(List<ChiTietPhieuHuy> chiTietPhieuHuyList) {
        this.chiTietPhieuHuyList = chiTietPhieuHuyList;
    }

    @Override
    public String toString() {
        return "PhieuPhaHuy{" +
                "maPhieuHuy='" + maPhieuHuy + '\'' +
                '}';
    }
}