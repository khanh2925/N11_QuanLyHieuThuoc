package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuNhap {

    private String maPhieuNhap;                 // VD: PN-20251031-0001
    private LocalDate ngayNhap;
    private NhaCungCap nhaCungCap;
    private NhanVien nhanVien;
    private double tongTien;
    private List<ChiTietPhieuNhap> chiTietPhieuNhapList;

    // ===== CONSTRUCTORS =====
    public PhieuNhap() {
        this.chiTietPhieuNhapList = new ArrayList<>();
    }

    public PhieuNhap(String maPhieuNhap, LocalDate ngayNhap,
                     NhaCungCap nhaCungCap, NhanVien nhanVien,
                     List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        setMaPhieuNhap(maPhieuNhap);
        setNgayNhap(ngayNhap);
        setNhaCungCap(nhaCungCap);
        setNhanVien(nhanVien);
        setChiTietPhieuNhapList(chiTietPhieuNhapList);
        capNhatTongTienTheoChiTiet();
    }

    // ===== GETTERS / SETTERS =====
    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        if (maPhieuNhap == null || maPhieuNhap.trim().isEmpty())
            throw new IllegalArgumentException("Mã phiếu nhập không được để trống.");

//        if (!maPhieuNhap.matches("^PN-\\d{8}-\\d{4}$")) {
//            // Tự động chuyển về dạng PN-<ngày hôm nay>-0001
//            String today = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
//            this.maPhieuNhap = "PN-" + today + "-0001";
//            return;
//        }

        this.maPhieuNhap = maPhieuNhap;
    }



    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        if (ngayNhap == null)
            throw new IllegalArgumentException("Ngày nhập không được null.");
        if (ngayNhap.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày nhập không hợp lệ (không được sau ngày hiện tại).");
        this.ngayNhap = ngayNhap;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        if (nhaCungCap == null)
            throw new IllegalArgumentException("Nhà cung cấp không được null.");
        this.nhaCungCap = nhaCungCap;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null)
            throw new IllegalArgumentException("Nhân viên không được null.");
        this.nhanVien = nhanVien;
    }

    public double getTongTien() {
        return tongTien;
    }

    // 💡 THÊM SETTER NÀY ĐỂ DAO CÓ THỂ SET TRỰC TIẾP TỪ DB (KHI LOAD DANH SÁCH, KHÔNG CẦN CHI TIẾT)
    public void setTongTien(double tongTien) {
        if (tongTien < 0)
            throw new IllegalArgumentException("Tổng tiền không được âm.");
        this.tongTien = Math.round(tongTien * 100.0) / 100.0;  // Round để nhất quán với capNhatTongTienTheoChiTiet()
    }

    public List<ChiTietPhieuNhap> getChiTietPhieuNhapList() {
        return chiTietPhieuNhapList;
    }

    public void setChiTietPhieuNhapList(List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        this.chiTietPhieuNhapList = (chiTietPhieuNhapList != null)
                ? chiTietPhieuNhapList
                : new ArrayList<>();
        capNhatTongTienTheoChiTiet();
    }

    // ===== DẪN SUẤT =====
    public void capNhatTongTienTheoChiTiet() {
        if (chiTietPhieuNhapList == null || chiTietPhieuNhapList.isEmpty()) {
            this.tongTien = 0;
            return;
        }
        double tong = 0;
        for (ChiTietPhieuNhap ctpn : chiTietPhieuNhapList) {
            tong += ctpn.getThanhTien();
        }
        this.tongTien = Math.round(tong * 100.0) / 100.0;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
                "PhieuNhap{ma='%s', ngay=%s, nhaCungCap='%s', tongTien=%.0f}",
                maPhieuNhap,
                ngayNhap,
                nhaCungCap != null ? nhaCungCap.getTenNhaCungCap() : "null",
                tongTien
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhieuNhap)) return false;
        PhieuNhap that = (PhieuNhap) o;
        return Objects.equals(maPhieuNhap, that.maPhieuNhap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuNhap);
    }
}