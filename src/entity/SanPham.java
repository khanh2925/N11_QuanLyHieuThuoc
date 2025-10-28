package entity;

import java.util.Objects;
import enums.DuongDung;
import enums.LoaiSanPham;

/**
 * @author
 * @version 2.0
 * @since Oct 2025
 *
 * Mô tả:
 *  - Lưu trữ thông tin sản phẩm trong hiệu thuốc.
 *  - Giá bán là thuộc tính DẪN XUẤT CÓ LƯU: tự động tính theo giá nhập nhưng vẫn được lưu DB.
 */
public class SanPham {

    private String maSanPham;
    private String tenSanPham;
    private LoaiSanPham loaiSanPham;
    private String soDangKy;
    private DuongDung duongDung;
    private double giaNhap;
    private double giaBan; // ✅ dẫn xuất có lưu
    private String hinhAnh;
    private String keBanSanPham;
    private boolean hoatDong;

    // ===== CONSTRUCTORS =====
    public SanPham() {}

    public SanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public SanPham(String maSanPham, String tenSanPham, LoaiSanPham loaiSanPham, String soDangKy,
                   DuongDung duongDung, double giaNhap, String hinhAnh,
                   String keBanSanPham, boolean hoatDong) {
        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setLoaiSanPham(loaiSanPham);
        setSoDangKy(soDangKy);
        setDuongDung(duongDung);
        setGiaNhap(giaNhap);
        capNhatGiaBanTheoHeSo();
        setHinhAnh(hinhAnh);
        setKeBanSanPham(keBanSanPham);
        setHoatDong(hoatDong);
    }

    public SanPham(SanPham sp) {
        this.maSanPham = sp.maSanPham;
        this.tenSanPham = sp.tenSanPham;
        this.loaiSanPham = sp.loaiSanPham;
        this.soDangKy = sp.soDangKy;
        this.duongDung = sp.duongDung;
        this.giaNhap = sp.giaNhap;
        this.giaBan = sp.giaBan;
        this.hinhAnh = sp.hinhAnh;
        this.keBanSanPham = sp.keBanSanPham;
        this.hoatDong = sp.hoatDong;
    }

    // ===== GETTERS / SETTERS =====

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null)
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        if (!maSanPham.matches("^SP\\d{6}$"))
            throw new IllegalArgumentException("Mã sản phẩm không hợp lệ (định dạng yêu cầu: SPxxxxxx).");
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        if (tenSanPham == null || tenSanPham.trim().isEmpty())
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng.");
        if (tenSanPham.length() > 100)
            throw new IllegalArgumentException("Tên sản phẩm không được vượt quá 100 ký tự.");
        this.tenSanPham = tenSanPham.trim();
    }

    public LoaiSanPham getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
        if (loaiSanPham == null)
            throw new IllegalArgumentException("Loại sản phẩm không được null.");
        this.loaiSanPham = loaiSanPham;
    }

    public String getSoDangKy() {
        return soDangKy;
    }

    public void setSoDangKy(String soDangKy) {
        if (soDangKy != null && soDangKy.length() > 20)
            throw new IllegalArgumentException("Số đăng ký không hợp lệ (tối đa 20 ký tự).");
        this.soDangKy = soDangKy;
    }

    public DuongDung getDuongDung() {
        return duongDung;
    }

    public void setDuongDung(DuongDung duongDung) {
        if (duongDung == null)
            throw new IllegalArgumentException("Đường dùng không được null.");
        this.duongDung = duongDung;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap <= 0)
            throw new IllegalArgumentException("Giá nhập phải lớn hơn 0.");
        this.giaNhap = giaNhap;
        capNhatGiaBanTheoHeSo(); // ✅ tự động cập nhật giá bán mỗi khi đổi giá nhập
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void capNhatGiaBanTheoHeSo() {
        if (giaNhap <= 0) return;

        double heSoLoiNhuan;
        if (giaNhap < 10000) heSoLoiNhuan = 1.5;
        else if (giaNhap < 50000) heSoLoiNhuan = 1.3;
        else if (giaNhap < 200000) heSoLoiNhuan = 1.2;
        else heSoLoiNhuan = 1.1;

        this.giaBan = Math.round(giaNhap * heSoLoiNhuan);
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        if (hinhAnh != null && hinhAnh.length() > 255)
            throw new IllegalArgumentException("Đường dẫn hình ảnh không được vượt quá 255 ký tự.");
        this.hinhAnh = hinhAnh;
    }

    public String getKeBanSanPham() {
        return keBanSanPham;
    }

    public void setKeBanSanPham(String keBanSanPham) {
        if (keBanSanPham != null && keBanSanPham.length() > 100)
            throw new IllegalArgumentException("Kệ bán sản phẩm không được vượt quá 100 ký tự.");
        this.keBanSanPham = keBanSanPham;
    }

    public boolean isHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(boolean hoatDong) {
        this.hoatDong = hoatDong;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("SanPham{ma='%s', ten='%s', giaNhap=%.0f, giaBan=%.0f, hoatDong=%s}",
                maSanPham, tenSanPham, giaNhap, giaBan, hoatDong);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SanPham)) return false;
        SanPham sp = (SanPham) o;
        return Objects.equals(maSanPham, sp.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }
}
