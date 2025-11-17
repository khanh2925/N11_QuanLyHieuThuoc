package entity;
import java.util.Objects;
import enums.HinhThucKM;

public class ChiTietKhuyenMaiSanPham {
    private SanPham sanPham;
    private KhuyenMai khuyenMai;

    // ===== CONSTRUCTORS =====
    public ChiTietKhuyenMaiSanPham() {}

    public ChiTietKhuyenMaiSanPham(SanPham sanPham, KhuyenMai khuyenMai) {
        setSanPham(sanPham);
        setKhuyenMai(khuyenMai);
    }

    // ===== GETTERS / SETTERS =====
    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không được null.");
        this.sanPham = sanPham;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai == null)
            throw new IllegalArgumentException("Khuyến mãi không được null.");
        if (khuyenMai.isKhuyenMaiHoaDon())
            throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho chi tiết sản phẩm.");
        if (!khuyenMai.isDangHoatDong())
            throw new IllegalArgumentException("Không thể gán khuyến mãi đã hết hạn hoặc ngừng hoạt động.");

        this.khuyenMai = khuyenMai;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
            "CTKM{KM='%s', SP='%s', Hình thức=%s}",
            khuyenMai != null ? khuyenMai.getMaKM() : "N/A",
            sanPham != null ? sanPham.getTenSanPham() : "N/A",
            khuyenMai != null ? khuyenMai.getHinhThuc() : "N/A"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietKhuyenMaiSanPham)) return false;
        ChiTietKhuyenMaiSanPham that = (ChiTietKhuyenMaiSanPham) o;
        return Objects.equals(sanPham, that.sanPham) &&
               Objects.equals(khuyenMai, that.khuyenMai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sanPham, khuyenMai);
    }
}