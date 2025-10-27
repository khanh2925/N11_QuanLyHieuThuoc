package entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class KhachHang {

    private String maKhachHang;
    private String tenKhachHang;
    private boolean gioiTinh;
    private String soDienThoai;
    private LocalDate ngaySinh;

    // Nếu cần tính điểm theo hóa đơn thì sau này có thể thêm DAO xử lý
    private List<HoaDon> danhSachHoaDon;

    public KhachHang() {
    }

    public KhachHang(String maKhachHang, String tenKhachHang, boolean gioiTinh,
                     String soDienThoai, LocalDate ngaySinh) {
        setMaKhachHang(maKhachHang);
        setTenKhachHang(tenKhachHang);
        setGioiTinh(gioiTinh);
        setSoDienThoai(soDienThoai);
        setNgaySinh(ngaySinh);
    }

    public KhachHang(KhachHang other) {
        this.maKhachHang = other.maKhachHang;
        this.tenKhachHang = other.tenKhachHang;
        this.gioiTinh = other.gioiTinh;
        this.soDienThoai = other.soDienThoai;
        this.ngaySinh = other.ngaySinh;
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

    public List<HoaDon> getDanhSachHoaDon() {
        return danhSachHoaDon;
    }

    public void setDanhSachHoaDon(List<HoaDon> danhSachHoaDon) {
        this.danhSachHoaDon = danhSachHoaDon;
    }

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maKhachHang + '\'' +
                ", tenKhachHang='" + tenKhachHang + '\'' +
                ", gioiTinh=" + (gioiTinh ? "Nam" : "Nữ") +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", ngaySinh=" + ngaySinh +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang that = (KhachHang) o;
        return Objects.equals(maKhachHang, that.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }
}
