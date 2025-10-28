package entity;

import java.util.Objects;

public class ChiTietKhuyenMaiSanPham {

    private SanPham sanPham;
    private KhuyenMai khuyenMai;

    public ChiTietKhuyenMaiSanPham() {}

    public ChiTietKhuyenMaiSanPham(SanPham sanPham, KhuyenMai khuyenMai) {
        setSanPham(sanPham);
        setKhuyenMai(khuyenMai);
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không hợp lệ.");
        this.sanPham = sanPham;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai == null)
            throw new IllegalArgumentException("Khuyến mãi không hợp lệ.");
        if (khuyenMai.isKhuyenMaiHoaDon())
            throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho sản phẩm.");
        this.khuyenMai = khuyenMai;
    }

    @Override
    public String toString() {
        return String.format("CTKM[%s → %s]", 
            khuyenMai != null ? khuyenMai.getMaKM() : "N/A", 
            sanPham != null ? sanPham.getTenSanPham() : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietKhuyenMaiSanPham)) return false;
        ChiTietKhuyenMaiSanPham that = (ChiTietKhuyenMaiSanPham) o;
        return Objects.equals(sanPham, that.sanPham) && Objects.equals(khuyenMai, that.khuyenMai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sanPham, khuyenMai);
    }
}
