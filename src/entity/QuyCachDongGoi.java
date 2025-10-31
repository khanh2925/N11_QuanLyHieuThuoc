package entity;

import java.util.Objects;

/**
 * Entity: QuyCachDongGoi
 * 
 * Mô tả:
 *  - Biểu diễn các cách đóng gói của một sản phẩm.
 *  - Mỗi sản phẩm có thể có nhiều đơn vị (viên, vỉ, hộp...).
 *  - Nếu là đơn vị gốc → HeSoQuyDoi = 1.
 *  - Nếu không gốc → HeSoQuyDoi > 1.
 */
public class QuyCachDongGoi {

    private String maQuyCach;      // QCxxxxxx
    private DonViTinh donViTinh;   // FK: Đơn vị tính
    private SanPham sanPham;       // FK: Sản phẩm
    private int heSoQuyDoi;        // VD: 10 viên = 1 vỉ
    private double tiLeGiam;       // 0–1
    private boolean donViGoc;      // Có phải đơn vị gốc hay không?

    // ===== CONSTRUCTORS =====
    public QuyCachDongGoi() {}

    public QuyCachDongGoi(String maQuyCach, DonViTinh donViTinh, SanPham sanPham,
                          int heSoQuyDoi, double tiLeGiam, boolean donViGoc) {
        setMaQuyCach(maQuyCach);
        setDonViTinh(donViTinh);
        setSanPham(sanPham);
        setHeSoQuyDoi(heSoQuyDoi);
        setTiLeGiam(tiLeGiam);
        setDonViGoc(donViGoc);
        kiemTraRangBuocDonViGoc();
    }

    // ===== GETTERS / SETTERS =====
    public String getMaQuyCach() {
        return maQuyCach;
    }

    public void setMaQuyCach(String maQuyCach) {
        if (maQuyCach == null || !maQuyCach.matches("^QC\\d{6}$"))
            throw new IllegalArgumentException("Mã quy cách không hợp lệ (định dạng: QCxxxxxx).");
        this.maQuyCach = maQuyCach;
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
        kiemTraRangBuocDonViGoc();
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
        kiemTraRangBuocDonViGoc();
    }

    /**
     *  Kiểm tra ràng buộc:
     * - Nếu là đơn vị gốc → HeSoQuyDoi = 1
     * - Nếu không gốc → HeSoQuyDoi > 1
     */
    private void kiemTraRangBuocDonViGoc() {
        if (heSoQuyDoi > 0) {
            if (donViGoc && heSoQuyDoi != 1)
                throw new IllegalArgumentException("Đơn vị gốc phải có hệ số quy đổi = 1.");
            if (!donViGoc && heSoQuyDoi == 1)
                throw new IllegalArgumentException("Đơn vị không gốc phải có hệ số quy đổi > 1.");
        }
    }

    // ===== OVERRIDES =====
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
        return Objects.hash(maQuyCach);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuyCachDongGoi other = (QuyCachDongGoi) obj;
        return Objects.equals(maQuyCach, other.maQuyCach);
    }
}