package entity;

public class ChiTietKhuyenMaiSanPham {
    private SanPham sanPham;
    private KhuyenMai khuyenMai;

    public ChiTietKhuyenMaiSanPham() {
    }

    public ChiTietKhuyenMaiSanPham(SanPham sanPham, KhuyenMai khuyenMai) {
        this.sanPham = sanPham;
        this.khuyenMai = khuyenMai;
    }
    
    // Getters and Setters

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    @Override
    public String toString() {
        return "ChiTietKhuyenMaiSanPham{" +
                "sanPham=" + sanPham.getMaSanPham() +
                ", khuyenMai=" + khuyenMai.getMaKM() +
                '}';
    }
}