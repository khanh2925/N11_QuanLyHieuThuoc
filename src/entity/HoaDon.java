package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HoaDon {

    private String maHoaDon;
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private LocalDate ngayLap;

    // Tổng tiền hàng (sau KM sản phẩm, trước KM hóa đơn) - chỉ tính trong code, không lưu DB
    private double tongTien;

    // Tổng thanh toán (map với cột TongThanhToan)
    private double tongThanhToan;

    // Số tiền giảm khuyến mãi hóa đơn (map với cột SoTienGiamKhuyenMai)
    private double soTienGiamKhuyenMai;

    // Khuyến mãi áp ở mức HÓA ĐƠN (MaKM trong DB)
    private KhuyenMai khuyenMai;

    private List<ChiTietHoaDon> danhSachChiTiet;

    // ✅ Thuốc kê đơn (true = có toa bác sĩ) - map với ThuocKeDon
    private boolean thuocKeDon;

    // ===== CONSTRUCTORS =====
    public HoaDon() {
        this.danhSachChiTiet = new ArrayList<>();
        this.ngayLap = LocalDate.now();
        this.thuocKeDon = false;
        this.tongTien = 0;
        this.tongThanhToan = 0;
        this.soTienGiamKhuyenMai = 0;
    }

    public HoaDon(String maHoaDon,
                  NhanVien nhanVien,
                  KhachHang khachHang,
                  LocalDate ngayLap,
                  KhuyenMai khuyenMai,
                  List<ChiTietHoaDon> danhSachChiTiet,
                  boolean thuocKeDon) {

        setMaHoaDon(maHoaDon);
        setNhanVien(nhanVien);
        setKhachHang(khachHang);
        setNgayLap(ngayLap);
        setDanhSachChiTiet(danhSachChiTiet != null ? danhSachChiTiet : new ArrayList<>());
        setKhuyenMai(khuyenMai);
        setThuocKeDon(thuocKeDon);
        capNhatTongTien();
    }

    // ===== GETTERS / SETTERS =====
    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon == null)
            throw new IllegalArgumentException("Mã hoá đơn không được để trống");

        maHoaDon = maHoaDon.trim(); // loại bỏ khoảng trắng đầu/cuối

        // Regex chuẩn: HD-yyyymmdd-xxxx (ví dụ HD-20251104-0001)
        if (!maHoaDon.matches("^HD-\\d{8}-\\d{4}$")) {
            throw new IllegalArgumentException("Mã hoá đơn không hợp lệ. Định dạng: HD-yyyymmdd-xxxx");
        }

        this.maHoaDon = maHoaDon;
    }

    public boolean isThuocKeDon() {
        return thuocKeDon;
    }

    public void setThuocKeDon(boolean thuocKeDon) {
        this.thuocKeDon = thuocKeDon;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null)
            throw new IllegalArgumentException("Nhân viên không được null.");
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        if (khachHang == null)
            throw new IllegalArgumentException("Khách hàng không được null.");
        this.khachHang = khachHang;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày lập không hợp lệ.");
        this.ngayLap = ngayLap;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        if (danhSachChiTiet == null)
            throw new IllegalArgumentException("Danh sách chi tiết hoá đơn không được null.");
        this.danhSachChiTiet = danhSachChiTiet;
        capNhatTongTien();
    }

    public double getTongTien() {
        return tongTien;
    }

    public double getTongThanhToan() {
        return tongThanhToan;
    }

    public void setTongThanhToan(double tongThanhToan) {
        this.tongThanhToan = tongThanhToan;
    }

    public double getSoTienGiamKhuyenMai() {
        return soTienGiamKhuyenMai;
    }

    public void setSoTienGiamKhuyenMai(double soTienGiamKhuyenMai) {
        this.soTienGiamKhuyenMai = soTienGiamKhuyenMai;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        // 1️⃣ Nếu null → bỏ khuyến mãi hóa đơn
        if (khuyenMai == null) {
            this.khuyenMai = null;
            capNhatTongThanhToan();
            return;
        }

        // 2️⃣ Nếu khuyến mãi là loại "sản phẩm" → không áp dụng ở hóa đơn
        if (!khuyenMai.isKhuyenMaiHoaDon()) {
            this.khuyenMai = null;
            capNhatTongThanhToan();
            return;
        }

        // 3️⃣ Nếu khuyến mãi là loại "hóa đơn" → phải kiểm tra xung đột với chi tiết
        for (ChiTietHoaDon ct : getDanhSachChiTiet()) {
            if (ct.getKhuyenMai() != null) {
                throw new IllegalStateException(
                        "Không thể áp dụng khuyến mãi hóa đơn khi chi tiết có khuyến mãi sản phẩm.");
            }
        }

        // 4️⃣ Gán và tính lại
        this.khuyenMai = khuyenMai;
        capNhatTongThanhToan();
    }

    // ===== BUSINESS LOGIC =====
    /** Tổng tiền hàng = sum(ThanhTien của từng chi tiết) */
    public void capNhatTongTien() {
        tongTien = 0;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            if (ct != null && ct.getThanhTien() > 0)
                tongTien += ct.getThanhTien();
        }
        capNhatTongThanhToan();
    }

    /** Tổng thanh toán = tongTien - tiền giảm hóa đơn (KHÔNG có điểm) */
    public void capNhatTongThanhToan() {
        double giamHoaDon = tinhTienGiamHoaDon();
        this.soTienGiamKhuyenMai = giamHoaDon;              // map với SoTienGiamKhuyenMai trong DB
        this.tongThanhToan = Math.max(0, tongTien - giamHoaDon); // map với TongThanhToan
    }

    /** Hàm tính TIỀN GIẢM hóa đơn theo khuyến mãi */
    private double tinhTienGiamHoaDon() {
        if (khuyenMai == null || !khuyenMai.isDangHoatDong())
            return 0;

        if (!khuyenMai.isKhuyenMaiHoaDon())
            return 0;

        if (tongTien < khuyenMai.getDieuKienApDungHoaDon())
            return 0;

        double giam;
        switch (khuyenMai.getHinhThuc()) {
            case GIAM_GIA_PHAN_TRAM -> giam = tongTien * (khuyenMai.getGiaTri() / 100.0);
            case GIAM_GIA_TIEN -> giam = khuyenMai.getGiaTri();
            default -> giam = 0;
        }

        if (giam < 0)
            giam = 0;
        if (giam > tongTien)
            giam = tongTien;

        return giam;
    }

    public void hoanTatHoaDon() {
        // Chỗ này trước dùng để cộng điểm, giờ bỏ điểm nên để trống
    }

    public boolean coKhuyenMaiSanPham() {
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            if (ct.getKhuyenMai() != null)
                return true;
        }
        return false;
    }

    /**
     * Áp dụng tự động KM hóa đơn (chỉ khi KHÔNG có KM sản phẩm)
     */
    public void tuDongApDungKhuyenMaiHoaDon(KhuyenMai kmHoaDon) {
        // Nếu có KM sản phẩm thì bỏ KM hóa đơn
        if (coKhuyenMaiSanPham()) {
            this.khuyenMai = null;
            capNhatTongThanhToan();
            return;
        }

        if (kmHoaDon != null
                && kmHoaDon.isKhuyenMaiHoaDon()
                && kmHoaDon.isDangHoatDong()
                && tongTien >= kmHoaDon.getDieuKienApDungHoaDon()) {

            this.khuyenMai = kmHoaDon;
        } else {
            this.khuyenMai = null;
        }

        capNhatTongThanhToan();
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
                "HoaDon[%s | KH:%s | Tổng:%.0fđ | KM:-%.0fđ | Còn:%.0fđ | Thuốc kê đơn:%s]",
                maHoaDon,
                khachHang != null ? khachHang.getTenKhachHang() : "Khách lẻ",
                tongTien,
                soTienGiamKhuyenMai,
                tongThanhToan,
                thuocKeDon ? "Có" : "Không"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof HoaDon))
            return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }
}
