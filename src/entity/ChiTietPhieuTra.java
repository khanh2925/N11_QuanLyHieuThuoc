package entity;

import java.util.Objects;

public class ChiTietPhieuTra {

    private PhieuTra phieuTra;
    private ChiTietHoaDon chiTietHoaDon;
    private String lyDoChiTiet;
    private int soLuong;
    private boolean trangThai;

    public ChiTietPhieuTra() {
    }

    public ChiTietPhieuTra(PhieuTra phieuTra, ChiTietHoaDon chiTietHoaDon, String lyDoChiTiet, int soLuong, boolean trangThai) {
        setPhieuTra(phieuTra);
        setChiTietHoaDon(chiTietHoaDon);
        setLyDoChiTiet(lyDoChiTiet);
        setSoLuong(soLuong);
        setTrangThai(trangThai);
    }

    public ChiTietPhieuTra(ChiTietPhieuTra ctpt) {
        this.phieuTra = ctpt.phieuTra;
        this.chiTietHoaDon = ctpt.chiTietHoaDon;
        this.lyDoChiTiet = ctpt.lyDoChiTiet;
        this.soLuong = ctpt.soLuong;
        this.trangThai = ctpt.trangThai;
    }

    public double getThanhTienHoan() {
        if (chiTietHoaDon == null || chiTietHoaDon.getSoLuong() == 0) {
            return 0;
        }
        double donGiaThucTra = chiTietHoaDon.getThanhTien() / chiTietHoaDon.getSoLuong();
        return donGiaThucTra * this.soLuong;
    }

    public PhieuTra getPhieuTra() {
        return phieuTra;
    }

    public void setPhieuTra(PhieuTra phieuTra) {
        if (phieuTra == null) {
            throw new IllegalArgumentException("Phiếu trả không tồn tại.");
        }
        this.phieuTra = phieuTra;
    }

    public ChiTietHoaDon getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn không tồn tại.");
        }
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public String getLyDoChiTiet() {
        return lyDoChiTiet;
    }

    public void setLyDoChiTiet(String lyDoChiTiet) {
        if (lyDoChiTiet != null && lyDoChiTiet.length() > 200) {
            throw new IllegalArgumentException("Lý do trả không được vượt quá 200 ký tự.");
        }
        this.lyDoChiTiet = lyDoChiTiet;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        if (this.chiTietHoaDon != null && soLuong > this.chiTietHoaDon.getSoLuong()) {
            throw new IllegalArgumentException("Số lượng trả không được vượt quá số lượng đã mua.");
        }
        this.soLuong = soLuong;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuTra{" +
                "phieuTra=" + (phieuTra != null ? phieuTra.getMaPhieuTra() : "N/A") +
                ", chiTietHoaDon=" + chiTietHoaDon +
                ", soLuong=" + soLuong +
                ", thanhTienHoan=" + getThanhTienHoan() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuTra that = (ChiTietPhieuTra) o;
        return Objects.equals(phieuTra, that.phieuTra) && Objects.equals(chiTietHoaDon, that.chiTietHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phieuTra, chiTietHoaDon);
    }
}