package entity;

import java.util.Objects;

import enums.HinhThucKM;

public class ChiTietHoaDon {

    private HoaDon hoaDon;
    private LoSanPham loSanPham;

    // Số lượng theo ĐƠN VỊ BÁN (hộp / vỉ / chai...), không còn là "đơn vị gốc"
    private double soLuong;

    // ĐƠN GIÁ CUỐI CÙNG (đã trừ khuyến mãi SẢN PHẨM nếu có)
    private double giaBan;

    // Khuyến mãi áp cho SẢN PHẨM (nếu có) – chỉ để lưu vết
    private KhuyenMai khuyenMai;

    // THÀNH TIỀN = soLuong * giaBan (sau KM SP)
    private double thanhTien;

    // Đơn vị tính lúc bán (phải map với MaDonViTinh trong DB)
    private DonViTinh donViTinh;

    // ===== CONSTRUCTORS =====

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon,
                         LoSanPham loSanPham,
                         double soLuong,
                         double giaBan,
                         KhuyenMai khuyenMai,
                         DonViTinh donViTinh) {
        setHoaDon(hoaDon);
        setLoSanPham(loSanPham);
        setSoLuong(soLuong);
        setGiaBan(giaBan);
        setKhuyenMai(khuyenMai);   
        setDonViTinh(donViTinh);
        capNhatThanhTien();
    }

    public ChiTietHoaDon(ChiTietHoaDon other) {
        this.hoaDon = other.hoaDon;
        this.loSanPham = other.loSanPham;
        this.soLuong = other.soLuong;
        this.giaBan = other.giaBan;
        this.khuyenMai = other.khuyenMai;
        this.thanhTien = other.thanhTien;
        this.donViTinh = other.donViTinh;
    }

    // ===== DẪN SUẤT =====
    /** * Cập nhật thành tiền:
     * Thành tiền = (Số lượng * Giá bán) - Tiền khuyến mãi (nếu có)
     */
    public void capNhatThanhTien() {
        // 1. Tính tổng tiền gốc
        double tongTien = this.soLuong * this.giaBan;

        // 2. Tính giảm giá nếu có khuyến mãi
        // Lưu ý: Không check isDangHoatDong() ở đây vì hóa đơn cũ xem lại thì KM đã hết hạn nhưng giá phải giữ nguyên.
        if (this.khuyenMai != null) {
            double giaTriKM = this.khuyenMai.getGiaTri();
            HinhThucKM hinhThuc = this.khuyenMai.getHinhThuc();

            if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
                // Giảm theo % trên tổng tiền (hoặc trên đơn giá đều ra kết quả như nhau)
                double tienGiam = tongTien * (giaTriKM / 100);
                tongTien -= tienGiam;
                
            } else if (hinhThuc == HinhThucKM.GIAM_GIA_TIEN) {
                // Giảm tiền mặt trực tiếp trên TỪNG ĐƠN VỊ sản phẩm
                // Ví dụ: Giảm 5k/hộp, mua 10 hộp thì giảm 50k
                double tienGiam = giaTriKM * this.soLuong;
                tongTien -= tienGiam;
            }
        }

        // 3. Đảm bảo thành tiền không âm
        this.thanhTien = Math.max(0, tongTien);
    }

    public double getThanhTien() {
        return thanhTien;
    }

    // ===== GETTERS / SETTERS =====
    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        if (hoaDon == null)
            throw new IllegalArgumentException("Hóa đơn không được null.");
        this.hoaDon = hoaDon;
    }

    public LoSanPham getLoSanPham() {
        return loSanPham;
    }

    public void setLoSanPham(LoSanPham loSanPham) {
        if (loSanPham == null)
            throw new IllegalArgumentException("Lô sản phẩm không được null.");
        this.loSanPham = loSanPham;
    }

    public SanPham getSanPham() {
        return loSanPham != null ? loSanPham.getSanPham() : null;
    }

    public double getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(double soLuong) {
        if (soLuong <= 0)
            throw new IllegalArgumentException("Số lượng phải > 0.");
        this.soLuong = soLuong;
        capNhatThanhTien();
    }

    public double getGiaBan() {
        return giaBan;
    }

    /** 
     * GiaBan phải là GIÁ CUỐI CÙNG (sau km SP).
     * KM sản phẩm đã được tính ở phía GUI / ItemDonHang.
     */
    public void setGiaBan(double giaBan) {
        if (giaBan <= 0)
            throw new IllegalArgumentException("Giá bán phải > 0.");
        this.giaBan = giaBan;
        capNhatThanhTien();
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        // Chỉ để lưu vết KM SẢN PHẨM, không cho phép KM HÓA ĐƠN
        if (khuyenMai != null && khuyenMai.isKhuyenMaiHoaDon())
            throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho chi tiết sản phẩm.");
        this.khuyenMai = khuyenMai;
        // KHÔNG gọi capNhatThanhTien ở đây nữa (giaBan đã là giá sau KM SP)
    }

    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null)
            throw new IllegalArgumentException("Đơn vị tính không được null.");
        this.donViTinh = donViTinh;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
                "CTHD[%s - %s] SL=%.0f, Giá=%.0f, Thành tiền=%.0f%s",
                hoaDon != null ? hoaDon.getMaHoaDon() : "N/A",
                getSanPham() != null ? getSanPham().getTenSanPham() : "N/A",
                soLuong,
                giaBan,
                thanhTien,
                khuyenMai != null ? ", KM=" + khuyenMai.getHinhThuc() : ""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ChiTietHoaDon))
            return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(hoaDon, that.hoaDon)
                && Objects.equals(loSanPham, that.loSanPham)
                && Objects.equals(donViTinh, that.donViTinh);
    }

    @Override
    public int hashCode() {
        // PK trong DB: (MaHoaDon, MaLo, MaDonViTinh) → hashCode nên đủ 3 trường
        return Objects.hash(hoaDon, loSanPham, donViTinh);
    }
}
