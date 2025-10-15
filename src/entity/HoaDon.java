package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lớp HoaDon đại diện cho một hóa đơn bán hàng trong hệ thống.
 */
public class HoaDon {

    private String maHoaDon;
    private KhachHang khachHang;
    private LocalDate ngayLap;
    private NhanVien nhanVien;
    private KhuyenMai khuyenMai;
    private boolean thuocTheoDon;

    // Một hóa đơn bao gồm nhiều chi tiết hóa đơn (sản phẩm, số lượng,...)
    // Danh sách này là cần thiết để tính toán tổng tiền.
    private List<ChiTietHoaDon> danhSachChiTiet;

    /**
     * Constructor mặc định.
     */
    public HoaDon() {
        this.danhSachChiTiet = new ArrayList<>(); // Khởi tạo danh sách để tránh lỗi
    }

    /**
     * Constructor có tham số để khởi tạo một hóa đơn.
     */
    public HoaDon(String maHoaDon, KhachHang khachHang, LocalDate ngayLap, NhanVien nhanVien, KhuyenMai khuyenMai, boolean thuocTheoDon) {
        this.maHoaDon = maHoaDon;
        this.khachHang = khachHang;
        this.ngayLap = ngayLap;
        this.nhanVien = nhanVien;
        this.khuyenMai = khuyenMai;
        this.thuocTheoDon = thuocTheoDon;
        this.danhSachChiTiet = new ArrayList<>();
    }

    // --- PHƯƠNG THỨC CHO THUỘC TÍNH DẪN SUẤT ---

    /**
     * Thuộc tính dẫn suất /tongTien.
     * Giá trị này không được lưu trữ trực tiếp mà được tính toán động
     * bằng cách cộng dồn thành tiền của tất cả các ChiTietHoaDon.
     *
     * @return Tổng số tiền của hóa đơn.
     */
    public double getTongTien() {
        double tong = 0;
        for (ChiTietHoaDon chiTiet : this.danhSachChiTiet) {
            tong += chiTiet.getThanhTien();
        }
        return tong;
        
        // Hoặc dùng Stream API cho ngắn gọn:
        // return this.danhSachChiTiet.stream()
        //        .mapToDouble(ChiTietHoaDon::getThanhTien)
        //        .sum();
    }

    // --- GETTERS VÀ SETTERS CHO CÁC THUỘC TÍNH KHÁC ---

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public boolean isThuocTheoDon() {
        return thuocTheoDon;
    }

    public void setThuocTheoDon(boolean thuocTheoDon) {
        this.thuocTheoDon = thuocTheoDon;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    // --- CÁC PHƯƠNG THỨC KHÁC ---

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HoaDon other = (HoaDon) obj;
        return Objects.equals(maHoaDon, other.maHoaDon);
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", ngayLap=" + ngayLap +
                ", tongTien=" + getTongTien() + // Gọi phương thức để lấy giá trị tính toán
                ", khachHang=" + khachHang +
                ", nhanVien=" + nhanVien +
                '}';
    }
}