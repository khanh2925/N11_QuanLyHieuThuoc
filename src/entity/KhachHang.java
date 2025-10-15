package entity;

import java.time.LocalDate;
import java.util.Objects;

public class KhachHang {

    private String maKhachHang;
    private String tenKhachHang;
    private boolean gioiTinh;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private int diemTichLuy;

    public KhachHang() {
    }

    public KhachHang(String maKhachHang, String tenKhachHang, boolean gioiTinh, String soDienThoai, LocalDate ngaySinh, int diemTichLuy) {
        this.maKhachHang = maKhachHang;
        setTenKhachHang(tenKhachHang);
        setGioiTinh(gioiTinh);
        setSoDienThoai(soDienThoai);
        setNgaySinh(ngaySinh);
        setDiemTichLuy(diemTichLuy);
    }

<<<<<<< HEAD
    public KhachHang(KhachHang other) {
        this.maKhachHang = other.maKhachHang;
        this.tenKhachHang = other.tenKhachHang;
        this.gioiTinh = other.gioiTinh;
        this.soDienThoai = other.soDienThoai;
        this.ngaySinh = other.ngaySinh;
        this.diemTichLuy = other.diemTichLuy;
=======
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
            tongChiTieu += hd.getTongTien(); // Lấy số tiền thực tế khách trả cho mỗi hóa đơn
        }
        
        // Áp dụng quy tắc quy đổi (ví dụ: 10000đ = 1 điểm)
        int diem = (int) (tongChiTieu / 10000);
        return diem;
>>>>>>> main
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang != null && maKhachHang.matches("^KH-\\d{4}$")) {
            this.maKhachHang = maKhachHang;
        } else {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ. Định dạng yêu cầu: KH-xxxx");
        }
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        if (tenKhachHang == null || tenKhachHang.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được rỗng.");
        }
        if (tenKhachHang.length() > 100) {
            throw new IllegalArgumentException("Tên khách hàng không được vượt quá 100 ký tự.");
        }
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
        if (soDienThoai != null && !soDienThoai.trim().isEmpty()) {
            if (!soDienThoai.matches("^0\\d{9}$")) {
                throw new IllegalArgumentException("Số điện thoại không hợp lệ (phải gồm 10 chữ số và bắt đầu bằng 0).");
            }
        }
        this.soDienThoai = soDienThoai;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ (không được là ngày trong tương lai).");
        }
        if (ngaySinh.isAfter(LocalDate.now().minusYears(6))) {
            throw new IllegalArgumentException("Khách hàng phải từ 6 tuổi trở lên.");
        }
        this.ngaySinh = ngaySinh;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) {
            throw new IllegalArgumentException("Điểm tích lũy phải lớn hơn hoặc bằng 0.");
        }
        this.diemTichLuy = diemTichLuy;
    }

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maKhachHang + '\'' +
                ", tenKhachHang='" + tenKhachHang + '\'' +
                ", gioiTinh=" + (gioiTinh ? "Nam" : "Nữ") +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", ngaySinh=" + ngaySinh +
                ", diemTichLuy=" + diemTichLuy +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang khachHang = (KhachHang) o;
        return Objects.equals(maKhachHang, khachHang.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }
}