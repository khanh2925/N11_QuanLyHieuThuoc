package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuTra {

    private String maPhieuTra;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private LocalDate ngayLap;
    private boolean daDuyet; // true = Đã duyệt, false = Đang chờ duyệt
    private double tongTienHoan;
    private List<ChiTietPhieuTra> chiTietPhieuTraList;

    public PhieuTra() {
        this.chiTietPhieuTraList = new ArrayList<>();
        this.ngayLap = LocalDate.now();
        this.daDuyet = false; // Mặc định là chờ duyệt
        this.tongTienHoan = 0;
    }

    public PhieuTra(String maPhieuTra, KhachHang khachHang, NhanVien nhanVien,
                    LocalDate ngayLap, boolean daDuyet, List<ChiTietPhieuTra> chiTietPhieuTraList) {
        setMaPhieuTra(maPhieuTra);
        setKhachHang(khachHang);
        setNhanVien(nhanVien);
        setNgayLap(ngayLap);
        setDaDuyet(daDuyet);
        setChiTietPhieuTraList(chiTietPhieuTraList);
    }

    // ===== GETTERS / SETTERS =====
    public String getMaPhieuTra() {
        return maPhieuTra;
    }

    public void setMaPhieuTra(String maPhieuTra) {
        if (maPhieuTra == null || !maPhieuTra.matches("^PT\\d{6}$")) {
            throw new IllegalArgumentException("Mã phiếu trả không hợp lệ. Định dạng: PTxxxxxx");
        }
        this.maPhieuTra = maPhieuTra;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        if (khachHang == null) {
            throw new IllegalArgumentException("Khách hàng không được null.");
        }
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được null.");
        }
        this.nhanVien = nhanVien;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày lập không hợp lệ (phải nhỏ hơn hoặc bằng ngày hiện tại).");
        }
        this.ngayLap = ngayLap;
    }

    public boolean isDaDuyet() {
        return daDuyet;
    }

    public void setDaDuyet(boolean daDuyet) {
        this.daDuyet = daDuyet;
    }

    public double getTongTienHoan() {
        return tongTienHoan;
    }

    /**
     * === THÊM SETTER NÀY ===
     * Dùng bởi DAO khi đọc giá trị TongTienHoan từ CSDL cho các view tóm tắt
     * (không tải chi tiết để tính toán lại).
     */
    public void setTongTienHoan(double tongTienHoan) {
        this.tongTienHoan = tongTienHoan;
    }

    public List<ChiTietPhieuTra> getChiTietPhieuTraList() {
        return chiTietPhieuTraList;
    }

    /**
     * Khi set danh sách chi tiết, tự động tính lại tổng tiền hoàn.
     */
    public void setChiTietPhieuTraList(List<ChiTietPhieuTra> chiTietPhieuTraList) {
        if (chiTietPhieuTraList == null)
            throw new IllegalArgumentException("Danh sách chi tiết phiếu trả không được null.");
        this.chiTietPhieuTraList = chiTietPhieuTraList;
        capNhatTongTienHoan(); // Tính lại tổng tiền sau khi set list
    }


    /**
     * Thuộc tính dẫn xuất: Tự động cập nhật tổng tiền hoàn dựa trên chi tiết hợp lệ.
     * Chỉ tính những chi tiết có trạng thái là "Huỷ hàng" (trangThai == 2).
     */
    public void capNhatTongTienHoan() {
        if (chiTietPhieuTraList == null || chiTietPhieuTraList.isEmpty()) {
            this.tongTienHoan = 0;
            return;
        }
        double tong = 0;
        for (ChiTietPhieuTra ct : chiTietPhieuTraList) {
            if (ct != null && ct.getThanhTienHoan() > 0 && ct.isHoanTien()) {
                tong += ct.getThanhTienHoan();
            }
        }
        this.tongTienHoan = Math.round(tong * 100.0) / 100.0;
    }

    /** Lấy trạng thái hiển thị text */
    public String getTrangThaiText() {
        return daDuyet ? "Đã duyệt" : "Đang chờ duyệt";
    }


    @Override
    public String toString() {
        return String.format("PhieuTra[%s | %s | %s | %.2fđ | %s]",
                maPhieuTra,
                khachHang != null ? khachHang.getTenKhachHang() : "N/A",
                ngayLap,
                tongTienHoan,
                getTrangThaiText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuTra)) return false;
        PhieuTra phieuTra = (PhieuTra) o;
        return Objects.equals(maPhieuTra, phieuTra.maPhieuTra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuTra);
    }
}