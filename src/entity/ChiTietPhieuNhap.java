package entity;

import java.util.Objects;

public class ChiTietPhieuNhap {

    private PhieuNhap phieuNhap;
    private LoSanPham loSanPham;
    private DonViTinh donViTinh;   
    private int soLuongNhap;
    private double donGiaNhap;
    private double thanhTien;

    // ===== CONSTRUCTORS =====
    public ChiTietPhieuNhap() {}

    public ChiTietPhieuNhap(PhieuNhap phieuNhap, LoSanPham loSanPham,
                             DonViTinh donViTinh, int soLuongNhap, double donGiaNhap) {
        setPhieuNhap(phieuNhap);
        setLoSanPham(loSanPham);
        setDonViTinh(donViTinh);
        setSoLuongNhap(soLuongNhap);
        setDonGiaNhap(donGiaNhap);
        capNhatThanhTien();
    }

    public ChiTietPhieuNhap(ChiTietPhieuNhap other) {
        this.phieuNhap = other.phieuNhap;
        this.loSanPham = other.loSanPham;
        this.donViTinh = other.donViTinh;
        this.soLuongNhap = other.soLuongNhap;
        this.donGiaNhap = other.donGiaNhap;
        this.thanhTien = other.thanhTien;
    }

    // ===== GETTERS / SETTERS =====
    public PhieuNhap getPhieuNhap() {
        return phieuNhap;
    }

    public void setPhieuNhap(PhieuNhap phieuNhap) {
        if (phieuNhap == null)
            throw new IllegalArgumentException("Phiếu nhập không được null.");
        this.phieuNhap = phieuNhap;
    }

    public LoSanPham getLoSanPham() {
        return loSanPham;
    }

    public void setLoSanPham(LoSanPham loSanPham) {
        if (loSanPham == null)
            throw new IllegalArgumentException("Lô sản phẩm không được null.");
        this.loSanPham = loSanPham;
    }

    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(DonViTinh donViTinh) {
        if (donViTinh == null)
            throw new IllegalArgumentException("Đơn vị tính không được null.");
        this.donViTinh = donViTinh;
    }

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap <= 0)
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        this.soLuongNhap = soLuongNhap;
        capNhatThanhTien();
    }

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(double donGiaNhap) {
        if (donGiaNhap <= 0)
            throw new IllegalArgumentException("Đơn giá nhập phải lớn hơn 0.");
        this.donGiaNhap = donGiaNhap;
        capNhatThanhTien();
    }

    public double getThanhTien() {
        return thanhTien;
    }

    /** ✅ Tự động cập nhật thành tiền khi thay đổi số lượng hoặc đơn giá */
    public void capNhatThanhTien() {
        this.thanhTien = Math.round(soLuongNhap * donGiaNhap * 100.0) / 100.0;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
            "ChiTietPhieuNhap{PN='%s', Lo='%s', ĐVT='%s', SL=%d, ĐơnGiá=%.2f, ThànhTiền=%.2f}",
            phieuNhap != null ? phieuNhap.getMaPhieuNhap() : "N/A",
            loSanPham != null ? loSanPham.getMaLo() : "N/A",
            donViTinh != null ? donViTinh.getTenDonViTinh() : "N/A",
            soLuongNhap, donGiaNhap, thanhTien
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietPhieuNhap)) return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return Objects.equals(phieuNhap, that.phieuNhap) &&
               Objects.equals(loSanPham, that.loSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuNhap, loSanPham);
    }
}
