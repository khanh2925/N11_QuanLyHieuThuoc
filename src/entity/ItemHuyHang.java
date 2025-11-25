package entity;

public class ItemHuyHang {

    private String maLo;
    private String tenSanPham;
    private int soLuongTon;
    private int soLuongHuy;

    private double donGiaNhap;
    private String lyDo;

    public ItemHuyHang(String maLo, String tenSanPham, int soLuongTon, double donGiaNhap) {
        this.maLo = maLo;
        this.tenSanPham = tenSanPham;
        this.soLuongTon = soLuongTon;
        this.donGiaNhap = donGiaNhap;
        this.soLuongHuy = 1;     // default
        this.lyDo = "";
    }

    public String getMaLo() {
        return maLo;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public int getSoLuongHuy() {
        return soLuongHuy;
    }

    public void setSoLuongHuy(int soLuongHuy) {
        this.soLuongHuy = soLuongHuy;
    }

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public double getThanhTien() {
        return donGiaNhap * soLuongHuy;
    }

    public String getLyDo() {
        return lyDo;
    }

    public void setLyDo(String lyDo) {
        this.lyDo = lyDo;
    }
}
