package entity;

import java.util.Objects;

import enums.DuongDung;
import enums.LoaiSanPham;


public class SanPham {
	
    private String maSanPham;
    private String tenSanPham;
    private LoaiSanPham loaiSanPham;
    private String soDangKy;
    private DuongDung duongDung;
    private double giaNhap;
    private double giaBan;
    private String hinhAnh;
    private String keBanSanPham;
    private boolean hoatDong;
    


    // ===== CONSTRUCTOR =====
    
    public SanPham() {
    }
    
    public SanPham(String maSanPham) {
		this.maSanPham = maSanPham;
	}
    
    public SanPham(String maSanPham, String tenSanPham, LoaiSanPham loaiSanPham, String soDangKy,
                    DuongDung duongDung,
                   double giaNhap, double giaBan, String hinhAnh,
                   String keBanSanPham, boolean hoatDong) {

        setMaSanPham(maSanPham);
        setTenSanPham(tenSanPham);
        setLoaiSanPham(loaiSanPham);
        setSoDangKy(soDangKy);
        setDuongDung(duongDung);
        setGiaNhap(giaNhap);
        setGiaBan(giaBan);
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

    // ===== GETTER / SETTER =====

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
        this.tenSanPham = tenSanPham;
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
            throw new IllegalArgumentException("Đường dùng không tồn tại.");
        this.duongDung = duongDung;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap <= 0)
            throw new IllegalArgumentException("Giá nhập phải lớn hơn 0.");
        this.giaNhap = giaNhap;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        if (giaBan <= 0)
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0.");

        if (this.giaNhap <= 0)
            throw new IllegalStateException("Cần nhập giá nhập trước khi xác định giá bán.");

        // Tính hệ số lợi nhuận tối thiểu
        double heSoLoiNhuan;
        if (this.giaNhap < 10000) heSoLoiNhuan = 1.5;
        else if (this.giaNhap < 50000) heSoLoiNhuan = 1.3;
        else if (this.giaNhap < 200000) heSoLoiNhuan = 1.2;
        else heSoLoiNhuan = 1.1;

        double giaBanToiThieu = this.giaNhap * heSoLoiNhuan;

        if (giaBan < giaBanToiThieu)
            throw new IllegalArgumentException("Giá bán phải cao hơn giá nhập theo tỷ lệ lợi nhuận tối thiểu.");

        this.giaBan = giaBan; // ✅ giữ nguyên giá người nhập, không ép về giá tối thiểu
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

    // ===== OVERRIDE =====

    @Override
    public String toString() {
        return String.format("SanPham{ma='%s', ten='%s', giaBan=%.0f, hoatDong=%s}",
                maSanPham, tenSanPham, giaBan, hoatDong);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sp = (SanPham) o;
        return Objects.equals(maSanPham, sp.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }
}
