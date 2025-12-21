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

    // ✅ Chỉ có Getter, không có Setter công khai
    private double tongTien;           // Tổng tiền hàng GỐC (chưa trừ gì cả)
    private double tongThanhToan;      // Số tiền khách phải trả cuối cùng
    private double soTienGiamKhuyenMai;// Tổng tiền được giảm (từ SP hoặc từ HĐ)

    // Khuyến mãi áp ở mức HÓA ĐƠN
    private KhuyenMai khuyenMai;

    private List<ChiTietHoaDon> danhSachChiTiet;

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
        setThuocKeDon(thuocKeDon);
        
        // Lưu ý: setDanhSachChiTiet và setKhuyenMai sẽ tự động gọi tính toán tiền
        setDanhSachChiTiet(danhSachChiTiet != null ? danhSachChiTiet : new ArrayList<>());
        setKhuyenMai(khuyenMai);
    }

    // ===== CORE LOGIC: TÍNH TOÁN TIỀN & KHUYẾN MÃI =====

    /**
     * Hàm tính toán trung tâm. 
     * Tự động chạy khi set danh sách chi tiết hoặc set khuyến mãi.
     */
    public void capNhatDuLieuHoaDon() {
        double tongTienHangGoc = 0;       // Tổng tiền niêm yết
        double tongTienGiamTuSanPham = 0; // Tổng tiền giảm được tích lũy từ từng sản phẩm
        boolean coKhuyenMaiSanPham = false;

        // 1. Duyệt chi tiết để lấy số liệu
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            // Giá gốc của dòng này (SL * Giá Bán Niêm Yết)
            // Lưu ý: ct.getGiaBan() phải là giá gốc.
            double thanhTienGoc = ct.getSoLuong() * ct.getGiaBan();
            tongTienHangGoc += thanhTienGoc;

            // Kiểm tra chi tiết này có áp dụng KM sản phẩm không
            if (ct.getKhuyenMai() != null) {
                coKhuyenMaiSanPham = true;
                // Tiền giảm = Giá gốc - Giá thực tế (thanh tiền trong chi tiết đã trừ KM)
                tongTienGiamTuSanPham += (thanhTienGoc - ct.getThanhTien());
            }
        }

        // 2. Cập nhật TỔNG TIỀN (Luôn là tổng giá gốc)
        this.tongTien = tongTienHangGoc;

        // 3. Tính TỔNG TIỀN GIẢM (Chọn 1 trong 2)
        if (coKhuyenMaiSanPham) {
            // Ưu tiên KM sản phẩm -> Hủy KM hóa đơn
            this.khuyenMai = null; 
            this.soTienGiamKhuyenMai = tongTienGiamTuSanPham;
        } else {
            // Không có KM sản phẩm -> Tính KM hóa đơn (nếu có)
            this.soTienGiamKhuyenMai = tinhGiamGiaTheoHoaDon(tongTienHangGoc);
        }

        // 4. Tính TỔNG THANH TOÁN
        this.tongThanhToan = this.tongTien - this.soTienGiamKhuyenMai;

        // Validate không âm
        if (this.tongThanhToan < 0) this.tongThanhToan = 0;
    }

    /**
     * Helper: Tính tiền giảm dựa trên khuyến mãi hóa đơn hiện tại
     */
    private double tinhGiamGiaTheoHoaDon(double tongTienGoc) {
        // Check null, check hoạt động, check loại KM
        if (this.khuyenMai == null 
                || !this.khuyenMai.isDangHoatDong() 
                || !this.khuyenMai.isKhuyenMaiHoaDon()) {
            return 0;
        }

        // Check điều kiện tổng tiền tối thiểu
        if (tongTienGoc < this.khuyenMai.getDieuKienApDungHoaDon()) {
            return 0;
        }

        double tienGiam = 0;
        switch (this.khuyenMai.getHinhThuc()) {
            case GIAM_GIA_PHAN_TRAM -> 
                tienGiam = tongTienGoc * (this.khuyenMai.getGiaTri() / 100.0);
            case GIAM_GIA_TIEN -> 
                tienGiam = this.khuyenMai.getGiaTri();
            default -> 
                tienGiam = 0;
        }

        // Không giảm quá tổng tiền
        return Math.min(tienGiam, tongTienGoc);
    }

    // ===== GETTERS (NO SETTERS FOR CALCULATED FIELDS) =====
    
    public double getTongTien() {
        return tongTien;
    }

    public double getTongThanhToan() {
        return tongThanhToan;
    }

    public double getSoTienGiamKhuyenMai() {
        return soTienGiamKhuyenMai;
    }

    // ===== GETTERS / SETTERS KHÁC =====

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon == null)
            throw new IllegalArgumentException("Mã hoá đơn không được để trống");
        maHoaDon = maHoaDon.trim();
        if (!maHoaDon.matches("^HD-\\d{8}-\\d{4}$")) {
            throw new IllegalArgumentException("Mã hoá đơn không hợp lệ. Định dạng: HD-yyyymmdd-xxxx");
        }
        this.maHoaDon = maHoaDon;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) throw new IllegalArgumentException("Nhân viên không được null.");
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        if (khachHang == null) throw new IllegalArgumentException("Khách hàng không được null.");
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

    public boolean isThuocKeDon() {
        return thuocKeDon;
    }

    public void setThuocKeDon(boolean thuocKeDon) {
        this.thuocKeDon = thuocKeDon;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    // Setter này quan trọng: Gán list -> Tính lại tiền ngay
    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        if (danhSachChiTiet == null)
            throw new IllegalArgumentException("Danh sách chi tiết hoá đơn không được null.");
        this.danhSachChiTiet = danhSachChiTiet;
        
        // Cập nhật lại toàn bộ số liệu
        capNhatDuLieuHoaDon();
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    // Setter này quan trọng: Gán KM -> Tính lại tiền ngay
    public void setKhuyenMai(KhuyenMai khuyenMai) {
        // Nếu null thì gán null
        if (khuyenMai == null) {
            this.khuyenMai = null;
        } 
        // Nếu không phải loại KM hóa đơn thì từ chối (gán null)
        else if (!khuyenMai.isKhuyenMaiHoaDon()) {
            this.khuyenMai = null;
        } 
        else {
            this.khuyenMai = khuyenMai;
        }

        // Cập nhật lại toàn bộ số liệu (hàm này sẽ tự check nếu có KM SP thì hủy KM HĐ này đi)
        capNhatDuLieuHoaDon();
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
                "HoaDon[%s | KH:%s | Tổng Gốc:%.0f | Giảm KM:-%.0f | Thanh Toán:%.0f | Thuốc kê đơn:%s]",
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
        if (this == o) return true;
        if (!(o instanceof HoaDon)) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }
}