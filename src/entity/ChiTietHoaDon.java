package entity;

import java.util.Objects;
import enums.HinhThucKM; // Giả định bạn có enum này


public class ChiTietHoaDon {

    private HoaDon hoaDon;
    private SanPham sanPham;
    private int soLuong;
    private KhuyenMai khuyenMai; // Khuyến mãi áp dụng trên dòng chi tiết này
    private double thanhTien; // ✅ Dẫn xuất có lưu

    // ===== CONSTRUCTORS =====
    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, KhuyenMai khuyenMai) {
        setHoaDon(hoaDon);
        setSanPham(sanPham);
        setSoLuong(soLuong);
        setKhuyenMai(khuyenMai);
        // capNhatThanhTien() đã được gọi bên trong các setter nên không cần gọi lại ở đây
    }

    // ===== GETTERS / SETTERS =====

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            throw new IllegalArgumentException("Hoá đơn không tồn tại.");
        }
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        if (sanPham == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại.");
        }
        this.sanPham = sanPham;
        capNhatThanhTien(); // ✅ Tự động cập nhật khi đổi sản phẩm
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        this.soLuong = soLuong;
        capNhatThanhTien(); // ✅ Tự động cập nhật khi đổi số lượng
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
        capNhatThanhTien(); // ✅ Tự động cập nhật khi đổi khuyến mãi
    }

    public double getThanhTien() {
        return thanhTien;
    }

    private void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }

    public void capNhatThanhTien() {
        if (sanPham == null || soLuong <= 0) {
            this.thanhTien = 0;
            return;
        }

        double tongTienBanDau = this.soLuong * this.sanPham.getGiaBan();
        double tienGiam = 0;

        if (this.khuyenMai != null) {
            // Chỉ áp dụng KM cho sản phẩm, không áp dụng KM cho hóa đơn ở đây
            if (!this.khuyenMai.isKhuyenMaiHoaDon()) {
                 HinhThucKM hinhThuc = this.khuyenMai.getHinhThuc();
                 if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
                     tienGiam = tongTienBanDau * (this.khuyenMai.getGiaTri() / 100.0);
                 } else if (hinhThuc == HinhThucKM.GIAM_GIA_TIEN) {
                     // Giảm giá tiền thường áp dụng trên mỗi sản phẩm
                     tienGiam = this.soLuong * this.khuyenMai.getGiaTri();
                 }
                 // Hình thức TANG_THEM không ảnh hưởng đến thành tiền
            }
        }
        
        // Cập nhật lại giá trị cho thuộc tính
        this.thanhTien = tongTienBanDau - tienGiam;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "hoaDon=" + (hoaDon != null ? hoaDon.getMaHoaDon() : "N/A") +
                ", sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : "N/A") +
                ", soLuong=" + soLuong +
                ", thanhTien=" + thanhTien +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(sanPham, that.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, sanPham);
    }
}