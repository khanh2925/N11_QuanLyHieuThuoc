package entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class PhieuNhap {

    private String maPhieuNhap;
    private LocalDate ngayNhap;
    private NhaCungCap nhaCungCap;
    private NhanVien nhanVien;
    private double tongTien; 
    private List<ChiTietPhieuNhap> chiTietPhieuNhapList;

    public PhieuNhap() {}

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

    // ===== GETTER / SETTER =====

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        if (maPhieuNhap == null || !maPhieuNhap.matches("^PN\\d{7}$")) {
            throw new IllegalArgumentException("Mã phiếu nhập không hợp lệ (định dạng PNxxxxxxx).");
        }
        this.maPhieuNhap = maPhieuNhap;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        if (ngayNhap == null || ngayNhap.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhập không hợp lệ (phải nhỏ hơn hoặc bằng ngày hiện tại).");
        }
        this.ngayNhap = ngayNhap;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        if (nhaCungCap == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được null.");
        }
        this.nhaCungCap = nhaCungCap;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }
    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }
    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được null.");
        }
        this.nhanVien = nhanVien;
    }

    public double getTongTien() {
        return tongTien;
    }
    public List<ChiTietPhieuNhap> getChiTietPhieuNhapList() {
        return chiTietPhieuNhapList;
    }

    public void setChiTietPhieuNhapList(List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        this.chiTietPhieuNhapList = chiTietPhieuNhapList;
        capNhatTongTienTheoChiTiet();
    }

    /** ✅ Hàm tính lại tổng tiền từ danh sách chi tiết phiếu nhập */
    public void capNhatTongTienTheoChiTiet() {
        if (chiTietPhieuNhapList == null || chiTietPhieuNhapList.isEmpty()) {
            this.tongTien = 0;
            return;
        }
        double tong = 0;
        for (ChiTietPhieuNhap ctpn : chiTietPhieuNhapList) {
            tong += ctpn.getDonGiaNhap() * ctpn.getSoLuongNhap();
        }
        this.tongTien = Math.round(tong);
    }

    @Override
    public String toString() {
        return String.format("PhieuNhap{ma='%s', ngay=%s, tongTien=%.0f}", maPhieuNhap, ngayNhap, tongTien);
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
