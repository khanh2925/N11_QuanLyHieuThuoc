package entity;

import java.util.Objects;

public class DonViTinh {

    public DonViTinh(String maDonViTinh) {
		this.maDonViTinh = maDonViTinh;
	}

	private String maDonViTinh;
    private String tenDonViTinh;
    private String moTa;

    public DonViTinh() {
    }

    public DonViTinh(String maDonViTinh, String tenDonViTinh, String moTa) {
        setMaDonViTinh(maDonViTinh);
        setTenDonViTinh(tenDonViTinh);
        setMoTa(moTa);
    }

    public DonViTinh(DonViTinh dvt) {
        this.maDonViTinh = dvt.maDonViTinh;
        this.tenDonViTinh = dvt.tenDonViTinh;
        this.moTa = dvt.moTa;
    }

    public String getMaDonViTinh() {
        return maDonViTinh;
    }

    public void setMaDonViTinh(String maDonViTinh) {
        if (maDonViTinh != null && maDonViTinh.matches("^DVT-\\d{3}$")) {
            this.maDonViTinh = maDonViTinh;
        } else {
            throw new IllegalArgumentException("Mã đơn vị tính không hợp lệ. Định dạng yêu cầu: DVT-xxx");
        }
    }

    public String getTenDonViTinh() {
        return tenDonViTinh;
    }

    public void setTenDonViTinh(String tenDonViTinh) {
        if (tenDonViTinh == null || tenDonViTinh.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đơn vị tính không được rỗng.");
        }
        if (tenDonViTinh.length() > 50) {
            throw new IllegalArgumentException("Tên đơn vị tính không được vượt quá 50 ký tự.");
        }
        this.tenDonViTinh = tenDonViTinh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        if (moTa != null && moTa.length() > 200) {
            throw new IllegalArgumentException("Mô tả quá dài, không được vượt quá 200 ký tự.");
        }
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return "DonViTinh{" +
                "maDonViTinh='" + maDonViTinh + '\'' +
                ", tenDonViTinh='" + tenDonViTinh + '\'' +
                ", moTa='" + moTa + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonViTinh donViTinh = (DonViTinh) o;
        return Objects.equals(maDonViTinh, donViTinh.maDonViTinh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDonViTinh);
    }
}