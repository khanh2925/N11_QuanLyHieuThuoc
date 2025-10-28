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
        // Constructor này gọi capNhatTongTienTheoChiTiet() sau khi set list, nên ok
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

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không được null.");
        }
        this.nhanVien = nhanVien;
    }

    public double getTongTien() {
        return tongTien;
    }

    /**
     * === SỬA LỖI: Thêm setter này ===
     * Dùng để DAO gán giá trị TongTien đọc từ CSDL
     * cho các phương thức không tải chi tiết (như layDanhSachPhieuNhap).
     */
    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }
    
    public List<ChiTietPhieuNhap> getChiTietPhieuNhapList() {
        return chiTietPhieuNhapList;
    }

    /**
     * Khi danh sách chi tiết được set hoặc thay đổi,
     * tổng tiền sẽ được tính lại tự động.
     */
    public void setChiTietPhieuNhapList(List<ChiTietPhieuNhap> chiTietPhieuNhapList) {
        this.chiTietPhieuNhapList = chiTietPhieuNhapList;
        capNhatTongTienTheoChiTiet(); // Gọi hàm tính toán sau khi set list
    }

    /**
     * Thuộc tính dẫn xuất: Tính tổng tiền dựa trên danh sách chi tiết hiện có.
     */
    public void capNhatTongTienTheoChiTiet() {
        if (chiTietPhieuNhapList == null || chiTietPhieuNhapList.isEmpty()) {
            this.tongTien = 0;
            return;
        }
        double tong = 0;
        for (ChiTietPhieuNhap ctpn : chiTietPhieuNhapList) {
            // Đảm bảo ctpn không null và có giá trị hợp lệ
            if (ctpn != null) {
                tong += ctpn.getThanhTien(); // Nên dùng getThanhTien() đã tính sẵn
            }
        }
        // Làm tròn đến 2 chữ số thập phân nếu cần, hoặc dùng Math.round như cũ
        this.tongTien = Math.round(tong * 100.0) / 100.0; 
        // this.tongTien = Math.round(tong); // Hoặc làm tròn đến số nguyên nếu bạn muốn
    }

    @Override
    public String toString() {
        // Nên hiển thị 2 số lẻ cho tiền tệ
        return String.format("PhieuNhap{ma='%s', ngay=%s, tongTien=%.2f}", maPhieuNhap, ngayNhap, tongTien);
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
