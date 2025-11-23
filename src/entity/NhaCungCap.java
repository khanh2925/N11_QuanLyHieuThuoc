package entity;

import java.util.Objects;

public class NhaCungCap {

    private String maNhaCungCap;   // VD: NCC-20251031-0001
    private String tenNhaCungCap;
    private String soDienThoai;
    private String diaChi;
    private String email;
    private boolean hoatDong = true; // ✅ trạng thái hoạt động

    // ===== CONSTRUCTORS =====
    public NhaCungCap() {}

    public NhaCungCap(String maNhaCungCap, String tenNhaCungCap,
                      String soDienThoai, String diaChi, String email) {
        setMaNhaCungCap(maNhaCungCap);
        setTenNhaCungCap(tenNhaCungCap);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setEmail(email);
    }

    public NhaCungCap(String maNhaCungCap) {
        setMaNhaCungCap(maNhaCungCap);
    }

    public NhaCungCap(NhaCungCap ncc) {
        this.maNhaCungCap = ncc.maNhaCungCap;
        this.tenNhaCungCap = ncc.tenNhaCungCap;
        this.soDienThoai = ncc.soDienThoai;
        this.diaChi = ncc.diaChi;
        this.email = ncc.email;
        this.hoatDong = ncc.hoatDong;
    }

    // ===== GETTERS / SETTERS =====
    public String getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        if (maNhaCungCap == null)
            throw new IllegalArgumentException("Mã nhà cung cấp không được để trống");

        maNhaCungCap = maNhaCungCap.trim(); // loại bỏ khoảng trắng đầu/cuối

        // Regex chuẩn: NCC-yyyymmdd-xxxx (ví dụ NCC-20251104-0001)
        if (!maNhaCungCap.matches("^NCC-\\d{8}-\\d{4}$")) {
            throw new IllegalArgumentException("Mã nhà cung cấp không hợp lệ. Định dạng: NCC-yyyymmdd-xxxx");
        }

        this.maNhaCungCap = maNhaCungCap;
    }
    
    public String getTenNhaCungCap() {
        return tenNhaCungCap;
    }

    public void setTenNhaCungCap(String tenNhaCungCap) {
        if (tenNhaCungCap == null)
            throw new IllegalArgumentException("Tên nhà cung cấp không được null.");
        tenNhaCungCap = tenNhaCungCap.trim();
        if (tenNhaCungCap.isEmpty())
            throw new IllegalArgumentException("Tên nhà cung cấp không được rỗng.");
        if (tenNhaCungCap.length() > 100)
            throw new IllegalArgumentException("Tên nhà cung cấp không được vượt quá 100 ký tự.");
        this.tenNhaCungCap = tenNhaCungCap;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null)
            throw new IllegalArgumentException("Số điện thoại không được null.");
        soDienThoai = soDienThoai.trim();
        if (!soDienThoai.matches("^0\\d{9}$"))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (10 chữ số, bắt đầu bằng 0).");
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null)
            throw new IllegalArgumentException("Địa chỉ không được null.");
        if (diaChi.trim().isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được rỗng.");
        if (diaChi.length() > 200)
            throw new IllegalArgumentException("Địa chỉ quá dài (tối đa 200 ký tự).");
        this.diaChi = diaChi.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.isEmpty()) {
            if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$"))
                throw new IllegalArgumentException("Email không hợp lệ.");
        }
        this.email = email;
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
        return String.format(
                "NhaCungCap{ma='%s', ten='%s', sdt='%s', email='%s', diaChi='%s', hoatDong=%s}",
                maNhaCungCap, tenNhaCungCap, soDienThoai, email, diaChi,
                hoatDong ? "Hoạt động" : "Ngừng hợp tác"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhaCungCap)) return false;
        NhaCungCap that = (NhaCungCap) o;
        return Objects.equals(maNhaCungCap, that.maNhaCungCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhaCungCap);
    }
}
