package entity;

import java.util.Objects;

public class ChiTietPhieuNhap {

    private PhieuNhap phieuNhap;
    private LoSanPham loSanPham;
    private int soLuongNhap;
    private double donGiaNhap;

    public ChiTietPhieuNhap() {
    }

    public ChiTietPhieuNhap(PhieuNhap phieuNhap, LoSanPham loSanPham, int soLuongNhap, double donGiaNhap) {
        setPhieuNhap(phieuNhap);
        setLoSanPham(loSanPham);
        setSoLuongNhap(soLuongNhap);
        setDonGiaNhap(donGiaNhap);
    }

    public ChiTietPhieuNhap(ChiTietPhieuNhap other) {
        this.phieuNhap = other.phieuNhap;
        this.loSanPham = other.loSanPham;
        this.soLuongNhap = other.soLuongNhap;
        this.donGiaNhap = other.donGiaNhap;
    }

    public double getThanhTien() {
        return this.soLuongNhap * this.donGiaNhap;
    }

    public PhieuNhap getPhieuNhap() {
        return phieuNhap;
    }

    public void setPhieuNhap(PhieuNhap phieuNhap) {
        if (phieuNhap == null) {
            throw new IllegalArgumentException("Phiếu nhập không tồn tại.");
        }
        this.phieuNhap = phieuNhap;
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

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        }
        this.soLuongNhap = soLuongNhap;
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

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" +
                "phieuNhap=" + (phieuNhap != null ? phieuNhap.getMaPhieuNhap() : "N/A") +
                ", loSanPham=" + (loSanPham != null ? loSanPham.getMaLo() : "N/A") +
                ", soLuongNhap=" + soLuongNhap +
                ", donGiaNhap=" + donGiaNhap +
                ", thanhTien=" + getThanhTien() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return Objects.equals(phieuNhap, that.phieuNhap) && Objects.equals(loSanPham, that.loSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuNhap, loSanPham);
    }
}