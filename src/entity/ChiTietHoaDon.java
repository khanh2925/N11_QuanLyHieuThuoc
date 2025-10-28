package entity;

import java.util.Objects;

public class ChiTietHoaDon {

    private HoaDon hoaDon;
    private LoSanPham loSanPham; 
    private double soLuong;
    private double giaBan;
    private KhuyenMai khuyenMai;

    public ChiTietHoaDon() {}

    // Constructor đã được cập nhật để dùng LoSanPham thay vì SanPham
    public ChiTietHoaDon(HoaDon hoaDon, LoSanPham loSanPham, double soLuong, double giaBan, KhuyenMai khuyenMai) {
        setHoaDon(hoaDon);
        setLoSanPham(loSanPham); // 💡 Dùng LoSanPham
        setSoLuong(soLuong);
        setGiaBan(giaBan);
        setKhuyenMai(khuyenMai);
    }

    public ChiTietHoaDon(ChiTietHoaDon other) {
        this.hoaDon = other.hoaDon;
        this.loSanPham = other.loSanPham; // 💡 Dùng LoSanPham
        this.soLuong = other.soLuong;
        this.giaBan = other.giaBan;
        this.khuyenMai = other.khuyenMai;
    }

    // 🔹 Thành tiền dẫn xuất (GIỮ NGUYÊN)
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

    // 💡 GETTER/SETTER CHO LO SAN PHAM (THAY THẾ SanPham)
    public LoSanPham getLoSanPham() { return loSanPham; }
    public void setLoSanPham(LoSanPham loSanPham) {
        if (loSanPham == null) throw new IllegalArgumentException("Lô sản phẩm không được null.");
        this.loSanPham = loSanPham;
    }

    // 💡 PHƯƠNG THỨC HỖ TRỢ ĐỂ GIỮ CÁC HÀM CŨ KHÔNG BỊ LỖI BIÊN DỊCH
    // VÍ DỤ: Nếu phương thức cũ gọi cthd.getSanPham().getMaSanPham()
    public SanPham getSanPham() { 
        return loSanPham != null ? loSanPham.getSanPham() : null;
    }
    // Hủy setSanPham vì không cần thiết sau khi đã có setLoSanPham
    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) throw new IllegalArgumentException("Sản phẩm không được null.");
        // Bắt buộc phải tạo LoSanPham nếu dùng hàm này:
        // throw new UnsupportedOperationException("Sử dụng setLoSanPham thay vì setSanPham.");
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

    // 💡 GIỮ NGUYÊN PHƯƠNG THỨC NÀY
    @Override
    public String toString() {
        return String.format("CTHD[%s - %s] SL=%.0f, Giá=%.0f, Thành tiền=%.0f%s",
                hoaDon != null ? hoaDon.getMaHoaDon() : "N/A",
                getSanPham() != null ? getSanPham().getTenSanPham() : "N/A",
                soLuong, giaBan, getThanhTien(),
                khuyenMai != null ? ", KM=" + khuyenMai.getHinhThuc() : "");
    }

    // 💡 GIỮ NGUYÊN PHƯƠNG THỨC NÀY (Đã sửa lại logic để dùng LoSanPham)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietHoaDon)) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        // Dùng LoSanPham thay vì SanPham để xác định sự bằng nhau
        return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(loSanPham, that.loSanPham);
    }

    // 💡 GIỮ NGUYÊN PHƯƠNG THỨC NÀY (Đã sửa lại logic để dùng LoSanPham)
    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, loSanPham);
    }
}