package entity;

import java.util.Objects;

public class NhaCungCap {

    private String maNhaCungCap;

	private String tenNhaCungCap;
    private String soDienThoai;
    private String diaChi;

    public NhaCungCap() {
    }

    public NhaCungCap(String maNhaCungCap, String tenNhaCungCap, String soDienThoai, String diaChi) {
        setMaNhaCungCap(maNhaCungCap);
        setTenNhaCungCap(tenNhaCungCap);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
    }

    public NhaCungCap(String maNhaCungCap) {
		this.maNhaCungCap = maNhaCungCap;
	}

	public NhaCungCap(NhaCungCap ncc) {
        this.maNhaCungCap = ncc.maNhaCungCap;
        this.tenNhaCungCap = ncc.tenNhaCungCap;
        this.soDienThoai = ncc.soDienThoai;
        this.diaChi = ncc.diaChi;
    }

    public String getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        if (maNhaCungCap != null && maNhaCungCap.matches("^NCC-\\d{3}$")) {
            this.maNhaCungCap = maNhaCungCap;
        } else {
            throw new IllegalArgumentException("Mã nhà cung cấp không hợp lệ. Định dạng yêu cầu: NCC-xxx");
        }
    }

    public String getTenNhaCungCap() {
        return tenNhaCungCap;
    }

    public void setTenNhaCungCap(String tenNhaCungCap) {
        if (tenNhaCungCap == null || tenNhaCungCap.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhà cung cấp không được rỗng.");
        }
        if (tenNhaCungCap.length() > 100) {
            throw new IllegalArgumentException("Tên nhà cung cấp không được vượt quá 100 ký tự.");
        }
        this.tenNhaCungCap = tenNhaCungCap;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được rỗng.");
        }
        if (!soDienThoai.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ, phải gồm 10 chữ số và bắt đầu bằng 0.");
        }
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi != null && diaChi.length() > 200) {
            throw new IllegalArgumentException("Địa chỉ quá dài, không được vượt quá 200 ký tự.");
        }
        this.diaChi = diaChi;
    }

    @Override
    public String toString() {
        return "NhaCungCap{" +
                "maNhaCungCap='" + maNhaCungCap + '\'' +
                ", tenNhaCungCap='" + tenNhaCungCap + '\'' +
                ", soDienThoai='" + soDienThoai + '\'' +
                ", diaChi='" + diaChi + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhaCungCap that = (NhaCungCap) o;
        return Objects.equals(maNhaCungCap, that.maNhaCungCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhaCungCap);
    }
}