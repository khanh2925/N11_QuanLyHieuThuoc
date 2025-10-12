package entity;

public class ChiTietPhieuTra {
    private PhieuTra phieuTra;
    private ChiTietHoaDon chiTietHoaDon;
    private int soLuong;
    private String lyDoTraChiTiet;
    private String caLam;
    private double thanhTienHoan;

    public ChiTietPhieuTra() {
    }

    public ChiTietPhieuTra(PhieuTra phieuTra, ChiTietHoaDon chiTietHoaDon, int soLuong, String lyDoTraChiTiet, String caLam, double thanhTienHoan) {
        this.phieuTra = phieuTra;
        this.chiTietHoaDon = chiTietHoaDon;
        this.soLuong = soLuong;
        this.lyDoTraChiTiet = lyDoTraChiTiet;
        this.caLam = caLam;
        this.thanhTienHoan = thanhTienHoan;
    }

    // Getters and Setters

    public PhieuTra getPhieuTra() {
        return phieuTra;
    }

    public void setPhieuTra(PhieuTra phieuTra) {
        this.phieuTra = phieuTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getLyDoTraChiTiet() {
        return lyDoTraChiTiet;
    }

    public void setLyDoTraChiTiet(String lyDoTraChiTiet) {
        this.lyDoTraChiTiet = lyDoTraChiTiet;
    }

    public String getCaLam() {
        return caLam;
    }

    public void setCaLam(String caLam) {
        this.caLam = caLam;
    }

    public double getThanhTienHoan() {
        return thanhTienHoan;
    }

    public void setThanhTienHoan(double thanhTienHoan) {
        this.thanhTienHoan = thanhTienHoan;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuTra{" +
                "phieuTra=" + phieuTra.getMaPhieuTra() +
                ", soLuong=" + soLuong +
                '}';
    }
}