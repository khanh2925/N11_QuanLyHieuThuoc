package entity;

public class ChiTietPhieuHuy {
    private PhieuHuy phieuHuy;
    private int soLuong;
    private String lyDoHuy;
    private double thanhTien;
    private SanPham sanPham;

    public ChiTietPhieuHuy() {
    }

    public ChiTietPhieuHuy(PhieuHuy phieuHuy, int soLuong, String lyDoHuy, double thanhTien, SanPham sanPham) {
        this.phieuHuy = phieuHuy;
        this.soLuong = soLuong;
        this.lyDoHuy = lyDoHuy;
        this.thanhTien = thanhTien;
        this.sanPham = sanPham;
    }

    // Getters and Setters

    public PhieuHuy getPhieuHuy() {
        return phieuHuy;
    }

    public void setPhieuHuy(PhieuHuy phieuHuy) {
        this.phieuHuy = phieuHuy;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuHuy{" +
                "phieuHuy=" + phieuHuy.getMaPhieuHuy() +
                ", sanPham=" + sanPham.getMaSanPham() +
                ", soLuong=" + soLuong +
                '}';
    }
}