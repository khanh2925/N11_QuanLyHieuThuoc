package entity;

import java.util.Objects;

public class ChiTietHoaDon {

    private HoaDon hoaDon;
    private SanPham sanPham;
    private double soLuong;
    private double giaBan;
    private KhuyenMai khuyenMai;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, double soLuong, double giaBan, KhuyenMai khuyenMai) {
        setHoaDon(hoaDon);
        setSanPham(sanPham);
        setSoLuong(soLuong);
        setGiaBan(giaBan);
        setKhuyenMai(khuyenMai);
    }

    public ChiTietHoaDon(ChiTietHoaDon other) {
        this.hoaDon = other.hoaDon;
        this.sanPham = other.sanPham;
        this.soLuong = other.soLuong;
        this.giaBan = other.giaBan;
        this.khuyenMai = other.khuyenMai;
    }

    // 🔹 Thành tiền dẫn xuất
    public double getThanhTien() {
        double thanhTienChuaGiam = this.soLuong * this.giaBan;

        if (khuyenMai == null) return thanhTienChuaGiam;

        // Nếu khuyến mãi là loại hóa đơn → bỏ qua (chỉ áp ở tổng hóa đơn)
        if (khuyenMai.isKhuyenMaiHoaDon()) return thanhTienChuaGiam;
        double giam = 0;
        switch (khuyenMai.getHinhThuc()) {
            case GIAM_GIA_PHAN_TRAM:
                giam = thanhTienChuaGiam * (khuyenMai.getGiaTri() / 100.0);
                break;
            case GIAM_GIA_TIEN:
                giam = khuyenMai.getGiaTri();
                break;
            case TANG_THEM:
                // Nếu đủ điều kiện thì cộng thêm sản phẩm, nhưng không thay đổi tiền
                giam = 0;
                break;
            default:
                giam = 0;
                break;
        }

        double thanhTienSauGiam = thanhTienChuaGiam - giam;
        return Math.max(0, thanhTienSauGiam);
    }

    // ===== GETTERS / SETTERS =====
    public HoaDon getHoaDon() { return hoaDon; }
    public void setHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) throw new IllegalArgumentException("Hóa đơn không được null.");
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) throw new IllegalArgumentException("Sản phẩm không được null.");
        this.sanPham = sanPham;
    }

    public double getSoLuong() { return soLuong; }
    public void setSoLuong(double soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải > 0.");
        this.soLuong = soLuong;
    }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) {
        if (giaBan <= 0) throw new IllegalArgumentException("Giá bán phải > 0.");
        this.giaBan = giaBan;
    }

    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public void setKhuyenMai(KhuyenMai khuyenMai) {
        // 🔹 Nếu khuyến mãi theo hóa đơn → không được gán cho chi tiết
        if (khuyenMai != null && khuyenMai.isKhuyenMaiHoaDon()) {
            throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho chi tiết sản phẩm.");
        }
        this.khuyenMai = khuyenMai;
    }

    @Override
    public String toString() {
        return String.format("CTHD[%s - %s] SL=%.0f, Giá=%.0f, Thành tiền=%.0f%s",
                hoaDon != null ? hoaDon.getMaHoaDon() : "N/A",
                sanPham != null ? sanPham.getTenSanPham() : "N/A",
                soLuong, giaBan, getThanhTien(),
                khuyenMai != null ? ", KM=" + khuyenMai.getHinhThuc() : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietHoaDon)) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, sanPham);
    }
}