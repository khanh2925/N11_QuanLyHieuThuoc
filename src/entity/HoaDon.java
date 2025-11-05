package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HoaDon {

    private static final double TY_LE_DIEM_QUY_DOI = 1000; // 1 điểm = 1000đ

    private String maHoaDon;
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private LocalDate ngayLap;
    private double tongTien;
    private double tongThanhToan;
    private double diemSuDung;
    private KhuyenMai khuyenMai;
    private double soTienGiamKhuyenMai;
    private List<ChiTietHoaDon> danhSachChiTiet;
    
    // ✅ Thuốc kê đơn (true = có toa bác sĩ)
    private boolean thuocKeDon;

    // ===== CONSTRUCTORS =====
    public HoaDon() {
        this.danhSachChiTiet = new ArrayList<>();
        this.ngayLap = LocalDate.now();
        this.diemSuDung = 0;
        this.soTienGiamKhuyenMai = 0;
        this.thuocKeDon = false;
    }

    public HoaDon(String maHoaDon, NhanVien nhanVien, KhachHang khachHang,
                  LocalDate ngayLap, List<ChiTietHoaDon> danhSachChiTiet, boolean thuocKeDon) {
        setMaHoaDon(maHoaDon);
        setNhanVien(nhanVien);
        setKhachHang(khachHang);
        setNgayLap(ngayLap);
        setDanhSachChiTiet(danhSachChiTiet);
        setThuocKeDon(thuocKeDon);
        capNhatTongTien();
    }

    // ===== GETTERS / SETTERS =====
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon == null || !maHoaDon.matches("^HD-\\d{8}-\\d{4}$"))
            throw new IllegalArgumentException("Mã hoá đơn không hợp lệ (định dạng: HD-yyyymmdd-xxxx).");
        this.maHoaDon = maHoaDon;
    }

    public boolean isThuocKeDon() { return thuocKeDon; }
    public void setThuocKeDon(boolean thuocKeDon) { this.thuocKeDon = thuocKeDon; }

    public NhanVien getNhanVien() { return nhanVien; }
    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null)
            throw new IllegalArgumentException("Nhân viên không được null.");
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) {
        if (khachHang == null)
            throw new IllegalArgumentException("Khách hàng không được null.");
        this.khachHang = khachHang;
    }

    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày lập không hợp lệ.");
        this.ngayLap = ngayLap;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() { return danhSachChiTiet; }
    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        if (danhSachChiTiet == null)
            throw new IllegalArgumentException("Danh sách chi tiết hoá đơn không được null.");
        this.danhSachChiTiet = danhSachChiTiet;
        capNhatTongTien();
    }

    public double getTongTien() { return tongTien; }
    public double getTongThanhToan() { return tongThanhToan; }
    public double getDiemSuDung() { return diemSuDung; }

    public double getSoTienGiamTuDiem() {
        return diemSuDung * TY_LE_DIEM_QUY_DOI;
    }

    public KhuyenMai getKhuyenMai() { return khuyenMai; }
    public double getSoTienGiamKhuyenMai() { return soTienGiamKhuyenMai; }

    public void setDiemSuDung(double diemSuDung) {
        if (diemSuDung < 0)
            throw new IllegalArgumentException("Số điểm sử dụng không hợp lệ.");
        this.diemSuDung = diemSuDung;
        capNhatTongThanhToan();
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        if (khuyenMai != null && !khuyenMai.isKhuyenMaiHoaDon())
            throw new IllegalArgumentException("Chỉ có thể gán khuyến mãi loại Hóa đơn.");

        for (ChiTietHoaDon ct : danhSachChiTiet) {
            if (ct.getKhuyenMai() != null)
                throw new IllegalStateException("Không thể áp dụng khuyến mãi hóa đơn khi chi tiết có khuyến mãi sản phẩm.");
        }

        this.khuyenMai = khuyenMai;
        apDungKhuyenMaiHoaDon();
    }

    // ===== BUSINESS LOGIC =====
    public void capNhatTongTien() {
        tongTien = 0;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            if (ct != null && ct.getThanhTien() > 0)
                tongTien += ct.getThanhTien();
        }
        apDungKhuyenMaiHoaDon();
        capNhatTongThanhToan();
    }

    public void capNhatTongThanhToan() {
        this.tongThanhToan = Math.max(0, tongTien - getSoTienGiamTuDiem() - soTienGiamKhuyenMai);
    }

    public void apDungDiemTichLuy(double soDiemMuonDung) {
        if (khachHang == null)
            throw new IllegalStateException("Hóa đơn chưa gắn khách hàng, không thể sử dụng điểm.");
        khachHang.dungDiemTichLuy(soDiemMuonDung);
        setDiemSuDung(soDiemMuonDung);
    }

    private void apDungKhuyenMaiHoaDon() {
        soTienGiamKhuyenMai = 0;

        if (khuyenMai == null || !khuyenMai.isDangHoatDong())
            return;

        if (tongTien < khuyenMai.getDieuKienApDungHoaDon())
            return;

        switch (khuyenMai.getHinhThuc()) {
            case GIAM_GIA_PHAN_TRAM -> soTienGiamKhuyenMai = tongTien * (khuyenMai.getGiaTri() / 100.0);
            case GIAM_GIA_TIEN -> soTienGiamKhuyenMai = khuyenMai.getGiaTri();
            default -> soTienGiamKhuyenMai = 0;
        }

        capNhatTongThanhToan();
    }

    public void hoanTatHoaDon() {
        if (khachHang != null)
            khachHang.congDiemTheoHoaDon(this);
    }

    public boolean coKhuyenMaiSanPham() {
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            if (ct.getKhuyenMai() != null) return true;
        }
        return false;
    }

    public void tuDongApDungKhuyenMaiHoaDon(KhuyenMai kmHoaDon) {
        if (coKhuyenMaiSanPham()) {
            this.khuyenMai = null;
            this.soTienGiamKhuyenMai = 0;
            capNhatTongTien();
            return;
        }

        if (kmHoaDon != null && kmHoaDon.isKhuyenMaiHoaDon() && kmHoaDon.isDangHoatDong()) {
            if (tongTien >= kmHoaDon.getDieuKienApDungHoaDon()) {
                this.khuyenMai = kmHoaDon;
                apDungKhuyenMaiHoaDon();
                return;
            }
        }

        this.khuyenMai = null;
        this.soTienGiamKhuyenMai = 0;
        capNhatTongThanhToan();
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
                "HoaDon[%s | KH:%s | Tổng:%.0fđ | KM:-%.0fđ | Điểm:-%.0f (%.0fđ) | Còn:%.0fđ | Thuốc kê đơn:%s]",
                maHoaDon,
                khachHang != null ? khachHang.getTenKhachHang() : "Khách lẻ",
                tongTien, soTienGiamKhuyenMai, diemSuDung, getSoTienGiamTuDiem(), tongThanhToan,
                thuocKeDon ? "Có" : "Không"
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HoaDon)) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }
}
