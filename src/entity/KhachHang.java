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
    private boolean hoatDong = true;
    private List<HoaDon> danhSachHoaDon; // chỉ dùng trong code, DB không lưu

    // ===== CONSTRUCTORS =====
    public KhachHang() {
    }

    public KhachHang(String maKhachHang, String tenKhachHang, boolean gioiTinh,
                     String soDienThoai, LocalDate ngaySinh, boolean hoatDong) {
        setMaKhachHang(maKhachHang);
        setTenKhachHang(tenKhachHang);
        setGioiTinh(gioiTinh);
        setSoDienThoai(soDienThoai);
        setNgaySinh(ngaySinh);
        setHoatDong(hoatDong);
    }

    // ===== GETTERS / SETTERS =====
    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        if (maKhachHang == null)
            throw new IllegalArgumentException("Mã khách hàng không được để trống");

        maKhachHang = maKhachHang.trim(); // loại bỏ khoảng trắng đầu/cuối

        // Regex chuẩn: KH-yyyymmdd-xxxx (ví dụ KH-20251104-0001)
        if (!maKhachHang.matches("^KH-\\d{8}-\\d{4}$")) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ. Định dạng: KH-yyyymmdd-xxxx");
        }

        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        if (tenKhachHang == null || tenKhachHang.trim().isEmpty())
            throw new IllegalArgumentException("Tên khách hàng không được rỗng.");
        if (tenKhachHang.length() > 100)
            throw new IllegalArgumentException("Tên khách hàng không vượt quá 100 ký tự.");
        this.tenKhachHang = tenKhachHang.trim();
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
        if (soDienThoai == null || !soDienThoai.matches("^0\\d{9}$"))
            throw new IllegalArgumentException("SĐT không hợp lệ (10 chữ số, bắt đầu bằng 0).");
        this.soDienThoai = soDienThoai;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
        if (ngaySinh.isAfter(LocalDate.now().minusYears(16)))
            throw new IllegalArgumentException("Khách hàng phải từ 16 tuổi trở lên.");
        this.ngaySinh = ngaySinh;
    }

    public boolean isHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(boolean hoatDong) {
        this.hoatDong = hoatDong;
    }

    public List<HoaDon> getDanhSachHoaDon() {
        return danhSachHoaDon;
    }

    public void setDanhSachHoaDon(List<HoaDon> danhSachHoaDon) {
        this.danhSachHoaDon = danhSachHoaDon;
    }
    public String getTrangThaiText() {
		return hoatDong ? "Hoạt động" : "Ngừng";
	}

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
                "KhachHang{ma='%s', ten='%s', sdt='%s', %s}",
                maKhachHang,
                tenKhachHang,
                soDienThoai,
                hoatDong ? "Hoạt động" : "Ngừng"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof KhachHang))
            return false;
        KhachHang that = (KhachHang) o;
        return Objects.equals(maKhachHang, that.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }
}
