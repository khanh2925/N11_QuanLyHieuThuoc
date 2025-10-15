package entity;

import java.util.Objects;

public class ChiTietHoaDon {

    private HoaDon hoaDon;
    private SanPham sanPham;
    private double soLuong;
    private KhuyenMai khuyenMai;
    private double giaBan;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, double soLuong, double giaBan, KhuyenMai khuyenMai) {
        setHoaDon(hoaDon);
        setSanPham(sanPham);
        setSoLuong(soLuong);
        setGiaBan(giaBan);
        setKhuyenMai(khuyenMai);
    }

    public ChiTietHoaDon(ChiTietHoaDon other) {
        this.hoaDon = other.hoaDon;
        this.sanPham = other.sanPham;
        this.soLuong = other.soLuong;
        this.khuyenMai = other.khuyenMai;
        this.giaBan = other.giaBan;
    }

    public double getThanhTien() {
        // Tính thành tiền ban đầu trước khi áp dụng khuyến mãi
        double thanhTienChuaGiam = this.soLuong * this.giaBan;

        // Nếu không có khuyến mãi, trả về giá trị ban đầu ngay lập tức
        if (khuyenMai == null) {
            return thanhTienChuaGiam;
        }

        double giaTriGiam = 0;

        // Sử dụng switch-case để xử lý các loại hình khuyến mãi rõ ràng hơn
        switch (khuyenMai.getHinhThuc()) {
            case GIAM_GIA_PHAN_TRAM:
                giaTriGiam = thanhTienChuaGiam * (khuyenMai.getGiaTri() / 100.0);
                break;

            case GIAM_GIA_TIEN:
                giaTriGiam = khuyenMai.getGiaTri();
                break;
                
            case TANG_THEM:
                // Khuyến mãi dạng tặng kèm không làm thay đổi thành tiền của sản phẩm này
                giaTriGiam = 0;
                break;
                
            default:
                // Mặc định không giảm giá nếu không xác định được hình thức
                giaTriGiam = 0;
                break;
        }

        // Tính thành tiền cuối cùng sau khi đã trừ đi giá trị giảm
        double thanhTienSauGiam = thanhTienChuaGiam - giaTriGiam;

        // Đảm bảo thành tiền không bao giờ là số âm
        return Math.max(0, thanhTienSauGiam);
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        if (hoaDon == null) {
            throw new IllegalArgumentException("Hóa đơn không tồn tại.");
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
    }

    public double getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(double soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        this.soLuong = soLuong;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        if (giaBan <= 0) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0.");
        }
        this.giaBan = giaBan;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "hoaDon=" + (hoaDon != null ? hoaDon.getMaHoaDon() : "N/A") +
                ", sanPham=" + (sanPham != null ? sanPham.getTenSanPham() : "N/A") +
                ", soLuong=" + soLuong +
                ", giaBan=" + giaBan +
                ", thanhTien=" + getThanhTien() +
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