package entity;

import java.time.LocalDate;
import java.util.Objects;

public class KhuyenMai {

    private String maKM;
    private String tenKM;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private boolean trangThai;
    private boolean khuyenMaiHoaDon;
    private HinhThucKM hinhThuc;
    private double giaTri;
    private String dieuKienApDungHoaDon;
    private int soLuongToiThieu;
    private int soLuongTangThem;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKM, String tenKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, boolean trangThai, boolean khuyenMaiHoaDon, HinhThucKM hinhThuc, double giaTri, String dieuKienApDungHoaDon, int soLuongToiThieu, int soLuongTangThem) {
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
    }

    public KhuyenMai(KhuyenMai other) {
        this.maKM = other.maKM;
        this.tenKM = other.tenKM;
        this.ngayBatDau = other.ngayBatDau;
        this.ngayKetThuc = other.ngayKetThuc;
        this.trangThai = other.trangThai;
        this.khuyenMaiHoaDon = other.khuyenMaiHoaDon;
        this.hinhThuc = other.hinhThuc;
        this.giaTri = other.giaTri;
        this.dieuKienApDungHoaDon = other.dieuKienApDungHoaDon;
        this.soLuongToiThieu = other.soLuongToiThieu;
        this.soLuongTangThem = other.soLuongTangThem;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        if (maKM != null && maKM.matches("^KM-\\d{8}-\\d{4}$")) {
            this.maKM = maKM;
        } else {
            throw new IllegalArgumentException("Mã khuyến mãi không hợp lệ. Định dạng yêu cầu: KM-yyyymmdd-xxxx");
        }
    }

    public String getTenKM() {
        return tenKM;
    }

    public void setTenKM(String tenKM) {
        if (tenKM == null || tenKM.trim().isEmpty() || tenKM.length() > 200) {
            throw new IllegalArgumentException("Tên khuyến mãi không hợp lệ.");
        }
        this.tenKM = tenKM;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
         if (ngayBatDau == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được rỗng.");
        }
        if (this.ngayKetThuc != null && ngayBatDau.isAfter(this.ngayKetThuc)) {
            throw new IllegalArgumentException("Ngày bắt đầu không hợp lệ.");
        }
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        if (ngayKetThuc == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được rỗng.");
        }
        if (this.ngayBatDau != null && ngayKetThuc.isBefore(this.ngayBatDau)) {
            throw new IllegalArgumentException("Ngày kết thúc không hợp lệ.");
        }
        this.ngayKetThuc = ngayKetThuc;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isKhuyenMaiHoaDon() {
        return khuyenMaiHoaDon;
    }

    public void setKhuyenMaiHoaDon(boolean khuyenMaiHoaDon) {
        this.khuyenMaiHoaDon = khuyenMaiHoaDon;
    }

    public HinhThucKM getHinhThuc() {
        return hinhThuc;
    }

    public void setHinhThuc(HinhThucKM hinhThuc) {
        if (hinhThuc == null) {
            throw new IllegalArgumentException("Hình thức khuyến mãi không hợp lệ.");
        }
        this.hinhThuc = hinhThuc;
    }

    public double getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(double giaTri) {
        if (giaTri < 0) {
            throw new IllegalArgumentException("Giá trị khuyến mãi không hợp lệ.");
        }
        this.giaTri = giaTri;
    }

    public String getDieuKienApDungHoaDon() {
        return dieuKienApDungHoaDon;
    }

    public void setDieuKienApDungHoaDon(String dieuKienApDungHoaDon) {
        this.dieuKienApDungHoaDon = dieuKienApDungHoaDon;
    }

    public int getSoLuongToiThieu() {
        return soLuongToiThieu;
    }

    public void setSoLuongToiThieu(int soLuongToiThieu) {
        if (soLuongToiThieu < 0) {
            throw new IllegalArgumentException("Số lượng tối thiểu không hợp lệ.");
        }
        this.soLuongToiThieu = soLuongToiThieu;
    }

    public int getSoLuongTangThem() {
        return soLuongTangThem;
    }

    public void setSoLuongTangThem(int soLuongTangThem) {
        if (soLuongTangThem < 0) {
            throw new IllegalArgumentException("Số lượng tặng thêm không hợp lệ.");
        }
        this.soLuongTangThem = soLuongTangThem;
    }

    @Override
    public String toString() {
        return "KhuyenMai{" +
                "maKM='" + maKM + '\'' +
                ", tenKM='" + tenKM + '\'' +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", trangThai=" + trangThai +
                ", khuyenMaiHoaDon=" + khuyenMaiHoaDon +
                ", hinhThuc=" + hinhThuc +
                ", giaTri=" + giaTri +
                ", dieuKienApDungHoaDon='" + dieuKienApDungHoaDon + '\'' +
                ", soLuongToiThieu=" + soLuongToiThieu +
                ", soLuongTangThem=" + soLuongTangThem +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhuyenMai khuyenMai = (KhuyenMai) o;
        return Objects.equals(maKM, khuyenMai.maKM);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKM);
    }
}