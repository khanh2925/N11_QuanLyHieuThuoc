package entity;

import java.util.Objects;

public class QuyCachDongGoi {

    private DonViTinh donViTinh; // Đơn vị quy đổi sang đơn vị gốc
    private SanPham sanPham;     // Sản phẩm tương ứng
    private int heSoQuyDoi;      // Hệ số quy đổi: ví dụ 10 viên = 1 vỉ → 10
    private double tiLeGiam;     // Tỷ lệ giảm giá theo đơn vị này (nếu có)
    private boolean donViGoc;    // Có phải đơn vị gốc hay không?

    public QuyCachDongGoi() {
    }

    public QuyCachDongGoi(DonViTinh donViTinh, SanPham sanPham,
                          int heSoQuyDoi, double tiLeGiam, boolean donViGoc) {
        setDonViTinh(donViTinh);
        setSanPham(sanPham);
        setHeSoQuyDoi(heSoQuyDoi);
        setTiLeGiam(tiLeGiam);
        setDonViGoc(donViGoc);
    }

    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null)
            throw new IllegalArgumentException("Đơn vị tính không được để trống!");
        this.donViTinh = donViTinh;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null)
            throw new IllegalArgumentException("Sản phẩm không được để trống!");
        this.sanPham = sanPham;
    }

    public int getHeSoQuyDoi() {
        return heSoQuyDoi;
    }

    public void setHeSoQuyDoi(int heSoQuyDoi) {
        if (heSoQuyDoi <= 0)
            throw new IllegalArgumentException("Hệ số quy đổi phải lớn hơn 0.");
        this.heSoQuyDoi = heSoQuyDoi;
    }

    public double getTiLeGiam() {
        return tiLeGiam;
    }

    public void setTiLeGiam(double tiLeGiam) {
        if (tiLeGiam < 0 || tiLeGiam > 1)
            throw new IllegalArgumentException("Tỉ lệ giảm phải nằm trong khoảng [0 - 1].");
        this.tiLeGiam = tiLeGiam;
    }

    public boolean isDonViGoc() {
        return donViGoc;
    }

    public void setDonViGoc(boolean donViGoc) {
        this.donViGoc = donViGoc;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (x%d, Giảm %.0f%%)%s",
                sanPham != null ? sanPham.getTenSanPham() : "Sản phẩm?",
                donViTinh != null ? donViTinh.getTenDonViTinh() : "ĐVT?",
                heSoQuyDoi,
                tiLeGiam * 100,
                donViGoc ? " - Đơn vị gốc" : ""
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(sanPham, donViTinh);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuyCachDongGoi other = (QuyCachDongGoi) obj;
        return Objects.equals(sanPham, other.sanPham)
                && Objects.equals(donViTinh, other.donViTinh);
    }
}
