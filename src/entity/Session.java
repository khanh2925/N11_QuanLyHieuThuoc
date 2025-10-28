package entity;


import entity.TaiKhoan;

public class Session {
    private static Session instance;
    private TaiKhoan taiKhoanDangNhap;

    private Session() {}

    public static Session getInstance() {
        if (instance == null)
            instance = new Session();
        return instance;
    }

    public void setTaiKhoanDangNhap(TaiKhoan taiKhoan) {
        this.taiKhoanDangNhap = taiKhoan;
    }

    public TaiKhoan getTaiKhoanDangNhap() {
        return taiKhoanDangNhap;
    }

    public void clearSession() {
        taiKhoanDangNhap = null;
    }
}
