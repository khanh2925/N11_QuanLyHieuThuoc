package entity;

import java.util.Objects;

public class TaiKhoan {

    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;

    public TaiKhoan() {
    }

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau) {
        setMaTaiKhoan(maTaiKhoan);
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
    }

    public TaiKhoan(TaiKhoan tk) {
        this.maTaiKhoan = tk.maTaiKhoan;
        this.tenDangNhap = tk.tenDangNhap;
        this.matKhau = tk.matKhau;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        if (maTaiKhoan != null && maTaiKhoan.matches("^TK\\d{6}$")) {
            this.maTaiKhoan = maTaiKhoan;
        } else {
            throw new IllegalArgumentException("Mã tài khoản không hợp lệ.");
        }
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap != null && tenDangNhap.matches("^[a-zA-Z0-9]{5,30}$")) {
            this.tenDangNhap = tenDangNhap;
        } else {
            throw new IllegalArgumentException("Tên đăng nhập không hợp lệ, chỉ được chứa chữ và số, độ dài từ 5-30 ký tự.");
        }
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        if (matKhau != null && matKhau.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")) {
            this.matKhau = matKhau;
        } else {
            throw new IllegalArgumentException("Mật khẩu không hợp lệ, cần ≥ 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt.");
        }
    }

    @Override
    public String toString() {
        return "TaiKhoan{" +
                "maTaiKhoan='" + maTaiKhoan + '\'' +
                ", tenDangNhap='" + tenDangNhap + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan taiKhoan = (TaiKhoan) o;
        return Objects.equals(maTaiKhoan, taiKhoan.maTaiKhoan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maTaiKhoan);
    }
}