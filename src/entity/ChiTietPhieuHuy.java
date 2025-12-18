package entity;

import java.util.Objects;

public class ChiTietPhieuHuy {

    private PhieuHuy phieuHuy;
    private LoSanPham loSanPham;
    private int soLuongHuy;
    private String lyDoChiTiet;
    private double donGiaNhap;   
    private double thanhTien;    
    private int trangThai;  // 🟢 1 = Chờ duyệt, 2 = Đã hủy, 3 = Từ chối hủy
    private DonViTinh donViTinh;

    // ===== CONSTANTS =====
    public static final int CHO_DUYET = 1;
    public static final int HUY_HANG = 2;
    public static final int TU_CHOI = 3;

    // ===== CONSTRUCTORS =====
    public ChiTietPhieuHuy() {}

    public ChiTietPhieuHuy(PhieuHuy phieuHuy, LoSanPham loSanPham,
                           int soLuongHuy, double donGiaNhap,
                           String lyDoChiTiet,DonViTinh donViTinh, int trangThai) {
        setPhieuHuy(phieuHuy);
        setLoSanPham(loSanPham);
        setSoLuongHuy(soLuongHuy);
        setDonGiaNhap(donGiaNhap);
        setLyDoChiTiet(lyDoChiTiet);
        setDonViTinh(donViTinh);
        setTrangThai(trangThai);
        capNhatThanhTien();
    }

    // ===== GETTERS / SETTERS =====
    public PhieuHuy getPhieuHuy() { return phieuHuy; }
    public void setPhieuHuy(PhieuHuy phieuHuy) {
        if (phieuHuy == null)
            throw new IllegalArgumentException("Phiếu hủy không được rỗng.");
        this.phieuHuy = phieuHuy;
    }

    public LoSanPham getLoSanPham() { return loSanPham; }
    public void setLoSanPham(LoSanPham loSanPham) {
        if (loSanPham == null)
            throw new IllegalArgumentException("Lô sản phẩm không được rỗng.");
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
    
    public DonViTinh getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(DonViTinh donViTinh) {
        // ✅ Cho phép null (dành cho sản phẩm chưa có đơn vị tính)
        this.donViTinh = donViTinh;
    }

    public int getTrangThai() { return trangThai; }
    public void setTrangThai(int trangThai) {
        if (trangThai < 1 || trangThai > 3)
            throw new IllegalArgumentException("Trạng thái chi tiết không hợp lệ (1=Chờ duyệt, 2=Đã hủy hàng, 3=Đã từ chối hủy).");
        this.trangThai = trangThai;
    }

    /** Lấy mô tả trạng thái (hiển thị trong bảng / GUI) */
    public String getTrangThaiText() {
        switch (trangThai) {
            case CHO_DUYET: return "Chờ duyệt";
            case HUY_HANG: return "Đã hủy hàng";
            case TU_CHOI: return "Đã từ chối hủy";
            default: return "Không rõ";
        }
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("CTPH[%s - Lô:%s - SL:%d - Trạng thái:%s - Giá:%.2f - Thành tiền:%.2f]",
                phieuHuy != null ? phieuHuy.getMaPhieuHuy() : "N/A",
                loSanPham != null ? loSanPham.getMaLo() : "N/A",
                soLuongHuy, getTrangThaiText(), donGiaNhap, thanhTien);
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
