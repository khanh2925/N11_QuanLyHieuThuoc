package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhachHang {
    private String maKhachHang;
    private String tenKhachHang;
    private boolean gioiTinh;
    private String soDienThoai;
    private LocalDate ngaySinh;
    
    // Thuộc tính cần thiết để tính toán điểm tích lũy
    private List<HoaDon> danhSachHoaDon; 

    public KhachHang() {
        // Khởi tạo danh sách hóa đơn để tránh lỗi NullPointerException
        this.danhSachHoaDon = new ArrayList<>();
    }

    // Constructor đã loại bỏ diemTichLuy
    public KhachHang(String maKhachHang, String tenKhachHang, boolean gioiTinh, String soDienThoai, LocalDate ngaySinh) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.gioiTinh = gioiTinh;
        this.soDienThoai = soDienThoai;
        this.ngaySinh = ngaySinh;
        this.danhSachHoaDon = new ArrayList<>();
    }

    /**
     * THUỘC TÍNH DẪN SUẤT: Điểm tích lũy được tính toán từ tổng giá trị các hóa đơn.
     * Phương thức này thay thế cho việc lưu trữ trực tiếp.
     * * Giả sử quy tắc: 10,000 VNĐ chi tiêu = 1 điểm.
     * @return Tổng số điểm tích lũy đã được làm tròn.
     */
    public int getDiemTichLuy() {
        if (danhSachHoaDon == null || danhSachHoaDon.isEmpty()) {
            return 0;
        }

        double tongChiTieu = 0;
        for (HoaDon hd : danhSachHoaDon) {
            tongChiTieu += hd.getThanhToan(); // Lấy số tiền thực tế khách trả cho mỗi hóa đơn
        }
        
        // Áp dụng quy tắc quy đổi (ví dụ: 10000đ = 1 điểm)
        int diem = (int) (tongChiTieu / 10000);
        return diem;
    }

    // Các Getters and Setters khác (loại bỏ setDiemTichLuy)
    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    
    public List<HoaDon> getDanhSachHoaDon() {
        return danhSachHoaDon;
    }

    public void setDanhSachHoaDon(List<HoaDon> danhSachHoaDon) {
        this.danhSachHoaDon = danhSachHoaDon;
    }

    @Override
    public String toString() {
        // Gọi getDiemTichLuy() để hiển thị giá trị được tính toán
        return "KhachHang [maKhachHang=" + maKhachHang + ", tenKhachHang=" + tenKhachHang + ", gioiTinh=" + gioiTinh
                + ", soDienThoai=" + soDienThoai + ", ngaySinh=" + ngaySinh + ", diemTichLuy=" + getDiemTichLuy() + "]";
    }
}