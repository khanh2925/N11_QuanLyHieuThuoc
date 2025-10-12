package entity;

import java.util.Objects;

public class ChiTietKhuyenMaiSanPham {

    private SanPham sanPham;
    private KhuyenMai khuyenMai;

    public ChiTietKhuyenMaiSanPham() {
    }

    public ChiTietKhuyenMaiSanPham(SanPham sanPham, KhuyenMai khuyenMai) {
        setSanPham(sanPham);
        setKhuyenMai(khuyenMai);
    }

    public ChiTietKhuyenMaiSanPham(ChiTietKhuyenMaiSanPham other) {
        this.sanPham = other.sanPham;
        this.khuyenMai = other.khuyenMai;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new IllegalArgumentException("Sản phẩm không hợp lệ.");
        }
        this.sanPham = sanPham;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai == null) {
            throw new IllegalArgumentException("Khuyến mãi không hợp lệ.");
        }
        this.khuyenMai = khuyenMai;
    }

    @Override
    public String toString() {
        return "ChiTietKhuyenMaiSanPham{" +
                "sanPham=" + sanPham +
                ", khuyenMai=" + khuyenMai +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietKhuyenMaiSanPham that = (ChiTietKhuyenMaiSanPham) o;
        return Objects.equals(sanPham, that.sanPham) && Objects.equals(khuyenMai, that.khuyenMai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sanPham, khuyenMai);
    }
}