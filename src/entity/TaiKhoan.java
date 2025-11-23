package entity;

import java.util.Objects;

public class TaiKhoan {

    private String maTaiKhoan;   // VD: TK-20251031-0001
    private String tenDangNhap;
    private String matKhau;
    private NhanVien nhanVien;   // ✅ FK tới nhân viên

    // ===== CONSTRUCTORS =====
    public TaiKhoan() {}

    // ✅ Constructor rút gọn cho login JOIN NhanVien
    public TaiKhoan(String tenDangNhap, String matKhau, NhanVien nhanVien) {
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setNhanVien(nhanVien);
    }

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau, NhanVien nhanVien) {
        setMaTaiKhoan(maTaiKhoan);
        setTenDangNhap(tenDangNhap);
        setMatKhau(matKhau);
        setNhanVien(nhanVien);
    }

    // ===== GETTERS / SETTERS =====
    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        if (maTaiKhoan == null)
            throw new IllegalArgumentException("Mã tài khoản không được để trống");

        maTaiKhoan = maTaiKhoan.trim(); // loại bỏ khoảng trắng đầu/cuối

        // Regex chuẩn: TK-yyyymmdd-xxxx (ví dụ TK-20251104-0001)
        if (!maTaiKhoan.matches("^TK-\\d{8}-\\d{4}$")) {
            throw new IllegalArgumentException("Mã tài khoản không hợp lệ. Định dạng: TK-yyyymmdd-xxxx");
        }

        this.maTaiKhoan = maTaiKhoan;
    }
    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null)
            throw new IllegalArgumentException("Tên đăng nhập không được null.");
        tenDangNhap = tenDangNhap.trim();
        if (tenDangNhap.isEmpty())
            throw new IllegalArgumentException("Tên đăng nhập không được rỗng.");
        if (!tenDangNhap.matches("^[\\S]{5,30}$"))
            throw new IllegalArgumentException("Tên đăng nhập không được chứa khoảng trắng, độ dài 5–30 ký tự.");
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        if (matKhau == null)
            throw new IllegalArgumentException("Mật khẩu không được null.");
        if (matKhau.length() < 8)
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 8 ký tự.");
        if (!matKhau.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$"))
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 1 chữ hoa, 1 chữ thường và 1 số.");
        this.matKhau = matKhau;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null)
            throw new IllegalArgumentException("Tài khoản phải gắn với một nhân viên hợp lệ (không null).");
        this.nhanVien = nhanVien;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
                "TaiKhoan{ma='%s', tenDangNhap='%s', nhanVien='%s'}",
                maTaiKhoan,
                tenDangNhap,
                nhanVien != null ? nhanVien.getTenNhanVien() : "null"
        );
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
