package entity;

import java.util.Objects;

public class ChiTietPhieuTra {

    private PhieuTra phieuTra;
    private ChiTietHoaDon chiTietHoaDon;
    private String lyDoChiTiet;
    private int soLuong;
    private double thanhTienHoan; 
    private int trangThai; // 0=Chờ duyệt, 1=Nhập lại hàng, 2=Huỷ hàng

    // ===== CONSTRUCTORS =====
    public ChiTietPhieuTra() {}

    public ChiTietPhieuTra(PhieuTra phieuTra, ChiTietHoaDon chiTietHoaDon,
                           String lyDoChiTiet, int soLuong, int trangThai) {
        setPhieuTra(phieuTra);
        setChiTietHoaDon(chiTietHoaDon);
        setLyDoChiTiet(lyDoChiTiet);
        setSoLuong(soLuong);
        setTrangThai(trangThai);
        capNhatThanhTienHoan(); // ✅ tự động tính
    }

    public ChiTietPhieuTra(ChiTietPhieuTra other) {
        this.phieuTra = other.phieuTra;
        this.chiTietHoaDon = other.chiTietHoaDon;
        this.lyDoChiTiet = other.lyDoChiTiet;
        this.soLuong = other.soLuong;
        this.thanhTienHoan = other.thanhTienHoan;
        this.trangThai = other.trangThai;
    }

    // ===== GETTERS / SETTERS =====
    public PhieuTra getPhieuTra() {
        return phieuTra;
    }

    public void setPhieuTra(PhieuTra phieuTra) {
        if (phieuTra == null)
            throw new IllegalArgumentException("Phiếu trả không tồn tại.");
        this.phieuTra = phieuTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon == null)
            throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại.");
        this.chiTietHoaDon = chiTietHoaDon;
        capNhatThanhTienHoan(); // ✅ cập nhật khi đổi hóa đơn
    }

    public String getLyDoChiTiet() {
        return lyDoChiTiet;
    }

    public void setLyDoChiTiet(String lyDoChiTiet) {
        if (lyDoChiTiet != null && lyDoChiTiet.length() > 200)
            throw new IllegalArgumentException("Lý do chi tiết không được vượt quá 200 ký tự.");
        this.lyDoChiTiet = lyDoChiTiet;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0)
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        if (this.chiTietHoaDon != null && soLuong > this.chiTietHoaDon.getSoLuong()) {
            throw new IllegalArgumentException(String.format(
                "Số lượng trả (%d) không được vượt quá số lượng đã mua (%d).",
                soLuong, this.chiTietHoaDon.getSoLuong()
            ));
        }
        this.soLuong = soLuong;
        capNhatThanhTienHoan(); // ✅ cập nhật lại khi thay đổi số lượng
    }

    public double getThanhTienHoan() {
        return thanhTienHoan;
    }

    /** ✅ Tự động tính lại thành tiền hoàn (derived but stored) */
    public void capNhatThanhTienHoan() {
        if (chiTietHoaDon == null || chiTietHoaDon.getSoLuong() <= 0) {
            this.thanhTienHoan = 0;
            return;
        }
        double donGiaThuc = chiTietHoaDon.getThanhTien() / chiTietHoaDon.getSoLuong();
        this.thanhTienHoan = Math.round(donGiaThuc * this.soLuong * 100.0) / 100.0;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        if (trangThai < 0 || trangThai > 2)
            throw new IllegalArgumentException("Trạng thái chỉ hợp lệ trong [0=Chờ duyệt, 1=Nhập lại hàng, 2=Huỷ hàng].");
        this.trangThai = trangThai;
    }

    public String getTrangThaiText() {
        switch (trangThai) {
            case 0: return "Chờ duyệt";
            case 1: return "Nhập lại hàng";
            case 2: return "Huỷ hàng";
            default: return "Không xác định";
        }
    }

    public boolean isHoanTien() {
        return trangThai == 2;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format("CTPT[%s - %s - SL:%d - Hoàn:%.2fđ - %s]",
                phieuTra != null ? phieuTra.getMaPhieuTra() : "N/A",
                chiTietHoaDon != null ? chiTietHoaDon.getSanPham().getTenSanPham() : "N/A",
                soLuong, thanhTienHoan, getTrangThaiText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietPhieuTra)) return false;
        ChiTietPhieuTra that = (ChiTietPhieuTra) o;
        return Objects.equals(phieuTra, that.phieuTra) &&
               Objects.equals(chiTietHoaDon, that.chiTietHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuTra, chiTietHoaDon);
    }
}
