package entity;

import java.time.LocalDate;
import java.util.Objects;
import enums.HinhThucKM;

public class KhuyenMai {

    private String maKM;
    private String tenKM;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private boolean trangThai;
    private boolean khuyenMaiHoaDon; // true = KM hóa đơn, false = KM sản phẩm
    private HinhThucKM hinhThuc;
    private double giaTri;
    private double dieuKienApDungHoaDon;
    private int soLuongToiThieu;
    private int soLuongTangThem;

    public KhuyenMai() {}

    public KhuyenMai(String maKM, String tenKM, LocalDate ngayBatDau, LocalDate ngayKetThuc,
                     boolean trangThai, boolean khuyenMaiHoaDon, HinhThucKM hinhThuc,
                     double giaTri, double dieuKienApDungHoaDon, int soLuongToiThieu, int soLuongTangThem) {
        setMaKM(maKM);
        setTenKM(tenKM);
        setNgayBatDau(ngayBatDau);
        setNgayKetThuc(ngayKetThuc);
        setTrangThai(trangThai);
        setKhuyenMaiHoaDon(khuyenMaiHoaDon);
        setHinhThuc(hinhThuc);
        setGiaTri(giaTri);
        setDieuKienApDungHoaDon(dieuKienApDungHoaDon);
        setSoLuongToiThieu(soLuongToiThieu);
        setSoLuongTangThem(soLuongTangThem);
        apDungRangBuocLoaiKM(); // ✅ tự áp logic sau khi khởi tạo
    }

    // ===== GETTERS / SETTERS =====
    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) {
        if (maKM == null || !maKM.matches("^KM-\\d{8}-\\d{4}$"))
            throw new IllegalArgumentException("Mã khuyến mãi không hợp lệ. Định dạng: KM-yyyymmdd-xxxx");
        this.maKM = maKM;
    }

    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKM) {
        if (tenKM == null || tenKM.trim().isEmpty() || tenKM.length() > 200)
            throw new IllegalArgumentException("Tên khuyến mãi không hợp lệ (không rỗng, ≤200 ký tự).");
        this.tenKM = tenKM.trim();
    }

    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) {
        if (ngayBatDau == null)
            throw new IllegalArgumentException("Ngày bắt đầu không được rỗng.");
        if (this.ngayKetThuc != null && ngayBatDau.isAfter(this.ngayKetThuc))
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        if (ngayKetThuc == null)
            throw new IllegalArgumentException("Ngày kết thúc không được rỗng.");
        if (this.ngayBatDau != null && ngayKetThuc.isBefore(this.ngayBatDau))
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        this.ngayKetThuc = ngayKetThuc;
    }

    public boolean isTrangThai() 
    { return trangThai; }
    public void setTrangThai(boolean trangThai) { this.trangThai = trangThai; }

    public boolean isKhuyenMaiHoaDon() { return khuyenMaiHoaDon; }
    public void setKhuyenMaiHoaDon(boolean khuyenMaiHoaDon) {
        this.khuyenMaiHoaDon = khuyenMaiHoaDon;
        apDungRangBuocLoaiKM();
    }

    public HinhThucKM getHinhThuc() { return hinhThuc; }
    public void setHinhThuc(HinhThucKM hinhThuc) {
        if (hinhThuc == null)
            throw new IllegalArgumentException("Hình thức khuyến mãi không được null.");
        this.hinhThuc = hinhThuc;
        apDungRangBuocLoaiKM();
    }

    public double getGiaTri() { return giaTri; }
    public void setGiaTri(double giaTri) {
        if (giaTri < 0)
            throw new IllegalArgumentException("Giá trị khuyến mãi phải >= 0.");
        this.giaTri = giaTri;
    }

    public double getDieuKienApDungHoaDon() { return dieuKienApDungHoaDon; }
    public void setDieuKienApDungHoaDon(double dieuKienApDungHoaDon) {
        if (dieuKienApDungHoaDon < 0)
            throw new IllegalArgumentException("Điều kiện áp dụng hóa đơn phải >= 0.");
        this.dieuKienApDungHoaDon = dieuKienApDungHoaDon;
    }

    public int getSoLuongToiThieu() { return soLuongToiThieu; }
    public void setSoLuongToiThieu(int soLuongToiThieu) {
        if (soLuongToiThieu < 0)
            throw new IllegalArgumentException("Số lượng tối thiểu phải >= 0.");
        this.soLuongToiThieu = soLuongToiThieu;
    }

    public int getSoLuongTangThem() { return soLuongTangThem; }
    public void setSoLuongTangThem(int soLuongTangThem) {
        if (soLuongTangThem < 0)
            throw new IllegalArgumentException("Số lượng tặng thêm phải >= 0.");
        this.soLuongTangThem = soLuongTangThem;
    }

    // ====== RÀNG BUỘC NGHIỆP VỤ ======
    private void apDungRangBuocLoaiKM() {
        if (khuyenMaiHoaDon) {
            // 🔹 Khuyến mãi hóa đơn
            // Chỉ dùng GIAM_GIA_PHAN_TRAM hoặc GIAM_GIA_TIEN
            if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM || hinhThuc == HinhThucKM.GIAM_GIA_TIEN) {
                this.soLuongToiThieu = 0;
                this.soLuongTangThem = 0;
            }
        } else {
            // 🔹 Khuyến mãi theo sản phẩm
            if (hinhThuc == HinhThucKM.TANG_THEM) {
                this.giaTri = 0; // Không có giá trị giảm
            } else if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM || hinhThuc == HinhThucKM.GIAM_GIA_TIEN) {
                this.soLuongToiThieu = 0;
                this.soLuongTangThem = 0;
            }
        }
    }

    // ===== UTILS =====
    public boolean isDangHoatDong() {
        LocalDate now = LocalDate.now();
        return trangThai && !now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc);
    }

    public void capNhatTrangThaiTuDong() {
        LocalDate now = LocalDate.now();
        if (now.isAfter(ngayKetThuc)) this.trangThai = false;
        else if (!now.isBefore(ngayBatDau) && !now.isAfter(ngayKetThuc)) this.trangThai = true;
    }

    @Override
    public String toString() {
        return String.format("KhuyenMai[%s] %s (%s - %.2f) [%s]", maKM, tenKM, hinhThuc, giaTri,
                khuyenMaiHoaDon ? "KM hóa đơn" : "KM sản phẩm");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KhuyenMai)) return false;
        KhuyenMai km = (KhuyenMai) o;
        return Objects.equals(maKM, km.maKM);
    }

    @Override
    public int hashCode() { return Objects.hash(maKM); }
}