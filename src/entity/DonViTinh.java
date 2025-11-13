package entity;

import java.util.Objects;

/**
 * Entity: DonViTinh
 * 
 * Mô tả:
 *  - Biểu diễn đơn vị tính cơ bản cho sản phẩm (viên, vỉ, hộp, chai...).
 *  - Mỗi đơn vị tính có thể được dùng trong nhiều quy cách đóng gói.
 */
public class DonViTinh {

    private String maDonViTinh;   // DVT-xxx
    private String tenDonViTinh;  // Ví dụ: "Viên", "Vỉ", "Hộp"

    // ===== CONSTRUCTORS =====
    public DonViTinh() {}

    public DonViTinh(String maDonViTinh) {
        setMaDonViTinh(maDonViTinh);
    }

    public DonViTinh(String maDonViTinh, String tenDonViTinh) {
        setMaDonViTinh(maDonViTinh);
        setTenDonViTinh(tenDonViTinh);
    }

    public DonViTinh(DonViTinh other) {
        if (other == null)
            throw new IllegalArgumentException("Đối tượng sao chép không được null.");
        this.maDonViTinh = other.maDonViTinh;
        this.tenDonViTinh = other.tenDonViTinh;
    }

    // ===== GETTERS / SETTERS =====
    public String getMaDonViTinh() {
        return maDonViTinh;
    }

    public void setMaDonViTinh(String maDonViTinh) {
        if (maDonViTinh == null)
            throw new IllegalArgumentException("Mã đơn vị tính không được để trống");

        maDonViTinh = maDonViTinh.trim(); // loại bỏ khoảng trắng đầu/cuối

        if (!maDonViTinh.matches("^DVT-\\d{3}$")) {
            throw new IllegalArgumentException("Mã đơn vị tính không hợp lệ. Định dạng: DVT-xxx");
        }

        this.maDonViTinh = maDonViTinh;
    }
    public String getTenDonViTinh() {
        return tenDonViTinh;
    }

    public void setTenDonViTinh(String tenDonViTinh) {
        if (tenDonViTinh == null)
            throw new IllegalArgumentException("Tên đơn vị tính không được null.");
        tenDonViTinh = tenDonViTinh.trim();
        if (tenDonViTinh.isEmpty())
            throw new IllegalArgumentException("Tên đơn vị tính không được rỗng.");
        if (tenDonViTinh.length() > 50)
            throw new IllegalArgumentException("Tên đơn vị tính không được vượt quá 50 ký tự.");
        this.tenDonViTinh = tenDonViTinh;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("DonViTinh[%s - %s]", maDonViTinh, tenDonViTinh);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DonViTinh)) return false;
        DonViTinh that = (DonViTinh) o;
        return Objects.equals(maDonViTinh, that.maDonViTinh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDonViTinh);
    }
}
