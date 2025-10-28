package entity;

import java.util.Objects;

public class ChiTietPhieuHuy {

    private PhieuHuy phieuHuy;
    private LoSanPham loSanPham;
    private int soLuongHuy;
    private String lyDoChiTiet;
    private double donGiaNhap;   
    private double thanhTien;    // dẫn xuất, auto tính

    public ChiTietPhieuHuy() {}

    public ChiTietPhieuHuy(PhieuHuy phieuHuy, LoSanPham loSanPham,
                           int soLuongHuy, double donGiaNhap, String lyDoChiTiet) {
        setPhieuHuy(phieuHuy);
        setLoSanPham(loSanPham);
        setSoLuongHuy(soLuongHuy);
        setDonGiaNhap(donGiaNhap);
        setLyDoChiTiet(lyDoChiTiet);
        capNhatThanhTien();
    }

    public PhieuHuy getPhieuHuy() { return phieuHuy; }
    public void setPhieuHuy(PhieuHuy phieuHuy) {
        if (phieuHuy == null) throw new IllegalArgumentException("Phiếu hủy không được null.");
        this.phieuHuy = phieuHuy;
    }

    public LoSanPham getLoSanPham() { return loSanPham; }
    public void setLoSanPham(LoSanPham loSanPham) {
        if (loSanPham == null)
            throw new IllegalArgumentException("Lô sản phẩm không được null.");
        this.loSanPham = loSanPham;
    }

    public int getSoLuongHuy() { return soLuongHuy; }
    public void setSoLuongHuy(int soLuongHuy) {
        if (soLuongHuy <= 0)
            throw new IllegalArgumentException("Số lượng hủy phải lớn hơn 0.");
        this.soLuongHuy = soLuongHuy;
        capNhatThanhTien();
    }

    public String getLyDoChiTiet() { return lyDoChiTiet; }
    public void setLyDoChiTiet(String lyDoChiTiet) {
        if (lyDoChiTiet != null && lyDoChiTiet.length() > 500)
            throw new IllegalArgumentException("Lý do chi tiết không được vượt quá 500 ký tự.");
        this.lyDoChiTiet = lyDoChiTiet;
    }

    public double getDonGiaNhap() { return donGiaNhap; }
    public void setDonGiaNhap(double donGiaNhap) {
        if (donGiaNhap <= 0)
            throw new IllegalArgumentException("Đơn giá nhập phải lớn hơn 0.");
        this.donGiaNhap = donGiaNhap;
        capNhatThanhTien();
    }

    public double getThanhTien() { return thanhTien; }
    public void capNhatThanhTien() {
        this.thanhTien = Math.round(soLuongHuy * donGiaNhap * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("CTPH[%s - Lô:%s - SL:%d - Giá nhập:%.2f - Thành tiền:%.2f]",
                phieuHuy != null ? phieuHuy.getMaPhieuHuy() : "N/A",
                loSanPham != null ? loSanPham.getMaLo() : "N/A",
                soLuongHuy, donGiaNhap, thanhTien);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietPhieuHuy)) return false;
        ChiTietPhieuHuy that = (ChiTietPhieuHuy) o;
        return Objects.equals(phieuHuy, that.phieuHuy) &&
               Objects.equals(loSanPham, that.loSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuHuy, loSanPham);
    }
}
