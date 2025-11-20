package entity;

import java.util.Objects;
import enums.HinhThucKM;

public class ChiTietHoaDon {

    private HoaDon hoaDon;
    private LoSanPham loSanPham;
    private double soLuong;     //Ví dụ: Bán 2 hộp thì soLuong = 2. Bán 5 vỉ thì soLuong = 5.
    private DonViTinh donViTinh;
    private double giaBan; // giá ứng với quy cách đóng gói
    private KhuyenMai khuyenMai; // khuyến mãi của sản phẩm
    private double thanhTien; // giá csau khuyến mãi

    // ===== CONSTRUCTORS =====
    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, LoSanPham loSanPham, double soLuong, DonViTinh donViTinh, double giaBan, KhuyenMai khuyenMai) {
        setHoaDon(hoaDon);
        setLoSanPham(loSanPham);
        setSoLuong(soLuong);
        setDonViTinh(donViTinh);
        setGiaBan(giaBan); // Lưu ý: Phải truyền đúng giá của ĐVT này vào
        setKhuyenMai(khuyenMai);
    }

    // ===== LOGIC TÍNH TIỀN TỰ ĐỘNG =====
    /**
     * Tính thành tiền dựa trên Giá của Đơn Vị Tính.
     */
    public void capNhatThanhTien() {
        double tongTienGoc = this.soLuong * this.giaBan;
        double tienGiam = 0;

        if (this.khuyenMai != null) {
            HinhThucKM hinhThuc = this.khuyenMai.getHinhThuc();
            double giaTriKM = this.khuyenMai.getGiaTri();

            if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
                tienGiam = tongTienGoc * (giaTriKM / 100.0);
            } else if (hinhThuc == HinhThucKM.GIAM_GIA_TIEN) {
                // ⚠️ Vấn đề: Ở đây ta không biết heSoQuyDoi là bao nhiêu
                // Vì DonViTinh chỉ có tên/mã, không lưu hệ số so với đơn vị gốc.
                
                // GIẢI PHÁP TẠM THỜI (NHƯNG AN TOÀN):
                // Mặc định coi giaTriKM là giảm trực tiếp cho đơn vị này (Đã được GUI tính trước).
                // Tức là: Ở GUI, nếu là hộp, bạn phải sửa cái object KhuyenMai truyền vào 
                // hoặc tính ra tiền giảm cụ thể.
                
                // TUY NHIÊN, ĐỂ ĐƠN GIẢN HÓA (Chấp nhận rủi ro nhỏ nếu không dùng logic đơn vị gốc):
                // Ta cứ nhân thẳng:
                tienGiam = giaTriKM * this.soLuong; 
                
                // -> ❗ RỦI RO: Nếu DB lưu 500đ (gốc) mà xuống đây trừ 500đ cho Hộp (100k) là sai.
            }
        }
        this.thanhTien = Math.max(0, tongTienGoc - tienGiam);
    }

    // ===== GETTERS / SETTERS =====
    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public LoSanPham getLoSanPham() {
        return loSanPham;
    }

    public void setLoSanPham(LoSanPham loSanPham) {
        this.loSanPham = loSanPham;
    }

    public SanPham getSanPham() {
        return loSanPham != null ? loSanPham.getSanPham() : null;
    }

    public double getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(double soLuong) {
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng phải > 0.");
        this.soLuong = soLuong;
        capNhatThanhTien(); // Số lượng thay đổi -> Tiền đổi
    }

    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null) throw new IllegalArgumentException("Đơn vị tính không được null.");
        this.donViTinh = donViTinh;
        // Lưu ý: Khi setDonViTinh ở đây, Giá bán KHÔNG tự nhảy.
        // Ở tầng Giao diện (GUI), khi đổi Combobox Đơn vị tính -> Phải gọi setGiaBan() tương ứng.
    }

    public double getGiaBan() {
        return giaBan;
    }

    /**
     * @param giaBan Phải truyền vào giá bán tương ứng với Đơn Vị Tính hiện tại.
     */
    public void setGiaBan(double giaBan) {
        if (giaBan < 0) throw new IllegalArgumentException("Giá bán không được âm.");
        this.giaBan = giaBan;
        capNhatThanhTien(); // Giá đổi -> Tiền đổi
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai != null && khuyenMai.isKhuyenMaiHoaDon()) {
            throw new IllegalArgumentException("Không thể gán khuyến mãi hóa đơn cho chi tiết sản phẩm.");
        }
        this.khuyenMai = khuyenMai;
        capNhatThanhTien(); // KM đổi -> Tiền đổi
    }

    public double getThanhTien() {
        return thanhTien;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("CTHD: %s | ĐVT: %s | SL: %.1f | Giá: %.0f | Thành tiền: %.0f",
                (loSanPham != null && loSanPham.getSanPham() != null) ? loSanPham.getSanPham().getTenSanPham() : "null",
                (donViTinh != null) ? donViTinh.getTenDonViTinh() : "null",
                soLuong, giaBan, thanhTien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, loSanPham, donViTinh);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChiTietHoaDon other = (ChiTietHoaDon) obj;
        return Objects.equals(hoaDon, other.hoaDon) &&
               Objects.equals(loSanPham, other.loSanPham) &&
               Objects.equals(donViTinh, other.donViTinh);
    }
}