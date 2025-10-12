package entity;

public class DonViTinh {
    private String maDonViTinh;
    private String tenDonViTinh;
    private String moTa;

    public DonViTinh() {
    }

    public DonViTinh(String maDonViTinh, String tenDonViTinh, String moTa) {
        this.maDonViTinh = maDonViTinh;
        this.tenDonViTinh = tenDonViTinh;
        this.moTa = moTa;
    }

    // Getters and Setters
    public String getMaDonViTinh() {
        return maDonViTinh;
    }

    public void setMaDonViTinh(String maDonViTinh) {
        this.maDonViTinh = maDonViTinh;
    }

    public String getTenDonViTinh() {
        return tenDonViTinh;
    }

    public void setTenDonViTinh(String tenDonViTinh) {
        this.tenDonViTinh = tenDonViTinh;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    @Override
    public String toString() {
        return "DonViTinh{" + "tenDonViTinh='" + tenDonViTinh + '\'' + '}';
    }
}