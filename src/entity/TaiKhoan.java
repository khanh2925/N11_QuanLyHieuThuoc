package entity;

import java.util.Objects;

public class TaiKhoan {

    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;

    public TaiKhoan() {}

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau) {
        setMaTaiKhoan(maTaiKhoan);
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
    }

    // ===== GETTERS / SETTERS =====
    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        if (maTaiKhoan == null || !maTaiKhoan.matches("^TK\\d{6}$"))
            throw new IllegalArgumentException("Mã tài khoản không hợp lệ (định dạng: TKxxxxxx).");
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || !tenDangNhap.matches("^[A-Za-z0-9]{5,30}$"))
            throw new IllegalArgumentException("Tên đăng nhập chỉ được chứa chữ và số, độ dài 5–30 ký tự.");
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.length() < 8)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        this.matKhau = matKhau;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("TaiKhoan{ma='%s', tenDangNhap='%s'}", maTaiKhoan, tenDangNhap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaiKhoan)) return false;
        TaiKhoan that = (TaiKhoan) o;
        return Objects.equals(maTaiKhoan, that.maTaiKhoan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTaiKhoan);
    }
}
