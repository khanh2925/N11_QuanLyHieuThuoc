package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import enums.HinhThucKM;

public class HoaDon {

    private String maHoaDon;
    private KhachHang khachHang;
    private LocalDate ngayLap;
    private NhanVien nhanVien;
    private KhuyenMai khuyenMai;
    private boolean thuocTheoDon;
    private List<ChiTietHoaDon> chiTietHoaDonList;
    private double tongTien; 

    // ===== CONSTRUCTORS =====
    public HoaDon() {
        this.chiTietHoaDonList = new ArrayList<>();
    }

    public HoaDon(String maHoaDon, KhachHang khachHang, LocalDate ngayLap, NhanVien nhanVien, KhuyenMai khuyenMai, boolean thuocTheoDon) {
        setMaHoaDon(maHoaDon);
        setKhachHang(khachHang);
        setNgayLap(ngayLap);
        setNhanVien(nhanVien);
        setKhuyenMai(khuyenMai);
        setThuocTheoDon(thuocTheoDon);
        this.chiTietHoaDonList = new ArrayList<>();
        capNhatTongTienSauKM(); // Tính toán lần đầu
    }


    public double getTongTienTruocKM() {
        if (this.chiTietHoaDonList == null || this.chiTietHoaDonList.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (ChiTietHoaDon ct : this.chiTietHoaDonList) {
            total += ct.getThanhTien();
        }
        return total;
    }


    public double getTongTien() {
        return tongTien;
    }
    
    private void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }
    

    public void capNhatTongTienSauKM() {
        double tongTienBanDau = getTongTienTruocKM();
        double tienGiam = 0;

        if (this.khuyenMai != null && this.khuyenMai.isKhuyenMaiHoaDon() && tongTienBanDau >= this.khuyenMai.getDieuKienApDungHoaDon()) {
            HinhThucKM hinhThuc = this.khuyenMai.getHinhThuc();
            if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
                tienGiam = tongTienBanDau * (this.khuyenMai.getGiaTri() / 100.0);
            } else if (hinhThuc == HinhThucKM.GIAM_GIA_TIEN) {
                tienGiam = this.khuyenMai.getGiaTri();
            }
        }
        this.tongTien = tongTienBanDau - tienGiam;
    }


    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        if (maHoaDon != null && maHoaDon.matches("^HD-\\d{8}-\\d{4}$")) {
            this.maHoaDon = maHoaDon;
        } else {
            throw new IllegalArgumentException("Mã hoá đơn không hợp lệ. Định dạng yêu cầu: HD-yyyymmdd-xxxx");
        }
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        if (khachHang == null) {
            throw new IllegalArgumentException("Khách hàng không tồn tại.");
        }
        this.khachHang = khachHang;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        if (ngayLap == null || ngayLap.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày lập không hợp lệ.");
        }
        this.ngayLap = ngayLap;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không tồn tại.");
        }
        this.nhanVien = nhanVien;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
        getTongTien();
    }

    public boolean isThuocTheoDon() {
        return thuocTheoDon;
    }

    public void setThuocTheoDon(boolean thuocTheoDon) {
        this.thuocTheoDon = thuocTheoDon;
    }

    public List<ChiTietHoaDon> getChiTietHoaDonList() {
        return chiTietHoaDonList;
    }

    public void setChiTietHoaDonList(List<ChiTietHoaDon> chiTietHoaDonList) {
        this.chiTietHoaDonList = chiTietHoaDonList;
        capNhatTongTienSauKM();
    }
    
    public void themChiTiet(ChiTietHoaDon chiTiet) {
        this.chiTietHoaDonList.add(chiTiet);
        capNhatTongTienSauKM();
    }
    
    public void xoaChiTiet(ChiTietHoaDon chiTiet) {
        this.chiTietHoaDonList.remove(chiTiet);
        capNhatTongTienSauKM();
    }


    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", tongTienSauKM=" + tongTien +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }
}