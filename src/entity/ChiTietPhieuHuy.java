package entity;

import java.util.Objects;

public class ChiTietPhieuHuy {

    private PhieuHuy phieuHuy;
    private int soLuongHuy;
    private String lyDoChiTiet;
    private LoSanPham loSanPham;
    private double donGiaNhap;

    public ChiTietPhieuHuy() {
    }

    public ChiTietPhieuHuy(PhieuHuy phieuHuy, int soLuongHuy, String lyDoChiTiet, LoSanPham loSanPham, double donGiaNhap) {
        setPhieuHuy(phieuHuy);
        setSoLuongHuy(soLuongHuy);
        setLyDoChiTiet(lyDoChiTiet);
        setLoSanPham(loSanPham);
        setDonGiaNhap(donGiaNhap);
    }

    public ChiTietPhieuHuy(ChiTietPhieuHuy other) {
        this.phieuHuy = other.phieuHuy;
        this.soLuongHuy = other.soLuongHuy;
        this.lyDoChiTiet = other.lyDoChiTiet;
        this.loSanPham = other.loSanPham;
        this.donGiaNhap = other.donGiaNhap;
    }

    public PhieuHuy getPhieuHuy() {
        return phieuHuy;
    }

    public void setPhieuHuy(PhieuHuy phieuHuy) {
        if (phieuHuy == null) {
            throw new IllegalArgumentException("Phiếu hủy không tồn tại.");
        }
        this.phieuHuy = phieuHuy;
    }

    public int getSoLuongHuy() {
        return soLuongHuy;
    }

    public void setSoLuongHuy(int soLuongHuy) {
        if (soLuongHuy <= 0) {
            throw new IllegalArgumentException("Số lượng hủy phải lớn hơn 0.");
        }
        this.soLuongHuy = soLuongHuy;
    }

    public String getLyDoChiTiet() {
        return lyDoChiTiet;
    }

    public void setLyDoChiTiet(String lyDoChiTiet) {
        if (lyDoChiTiet != null && lyDoChiTiet.length() > 500) {
            throw new IllegalArgumentException("Lý do chi tiết không được vượt quá 500 ký tự.");
        }
        this.lyDoChiTiet = lyDoChiTiet;
    }

    public LoSanPham getLoSanPham() {
        return loSanPham;
    }

    public void setLoSanPham(LoSanPham loSanPham) {
        if (loSanPham == null) {
            throw new IllegalArgumentException("Lô sản phẩm không tồn tại.");
        }
        this.loSanPham = loSanPham;
    }
    
    public double getDonGiaNhap() {
        return donGiaNhap;
    }
    
    public void setDonGiaNhap(double donGiaNhap) {
        if (donGiaNhap <= 0) {
            throw new IllegalArgumentException("Đơn giá nhập phải lớn hơn 0.");
        }
        this.donGiaNhap = donGiaNhap;
    }
    
    public double getThanhTien() {
        return this.soLuongHuy * this.donGiaNhap;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuHuy{" +
                "phieuHuy=" + (phieuHuy != null ? phieuHuy.getMaPhieuHuy() : "N/A") +
                ", soLuongHuy=" + soLuongHuy +
                ", lyDoChiTiet='" + lyDoChiTiet + '\'' +
                ", loSanPham=" + (loSanPham != null ? loSanPham.getMaLo() : "N/A") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuHuy that = (ChiTietPhieuHuy) o;
        return Objects.equals(phieuHuy, that.phieuHuy) && Objects.equals(loSanPham, that.loSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuHuy, loSanPham);
    }
}