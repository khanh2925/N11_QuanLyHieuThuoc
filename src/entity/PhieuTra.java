package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PhieuTra {

    private String maPhieuTra;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private LocalDate ngayLap;
    private String trangThai;
    private List<ChiTietPhieuTra> chiTietPhieuTraList;

    public PhieuTra() {
        this.chiTietPhieuTraList = new ArrayList<>();
        this.ngayLap = LocalDate.now();
    }

    public PhieuTra(String maPhieuTra, KhachHang khachHang, NhanVien nhanVien, LocalDate ngayLap, String trangThai) {
        setMaPhieuTra(maPhieuTra);
        setKhachHang(khachHang);
        setNhanVien(nhanVien);
        setNgayLap(ngayLap);
        setTrangThai(trangThai);
        this.chiTietPhieuTraList = new ArrayList<>();
    }

    public PhieuTra(PhieuTra other) {
        this.maPhieuTra = other.maPhieuTra;
        this.khachHang = other.khachHang;
        this.nhanVien = other.nhanVien;
        this.ngayLap = other.ngayLap;
        this.trangThai = other.trangThai;
        this.chiTietPhieuTraList = new ArrayList<>(other.chiTietPhieuTraList);
    }

    public double getTongTienHoan() {
        if (this.chiTietPhieuTraList == null) {
            return 0;
        }
        double total = 0;
        for (ChiTietPhieuTra ct : this.chiTietPhieuTraList) {
            total += ct.getThanhTienHoan();
        }
        return total;
    }

    public String getMaPhieuTra() {
        return maPhieuTra;
    }

    public void setMaPhieuTra(String maPhieuTra) {
        if (maPhieuTra != null && maPhieuTra.matches("^PT\\d{6}$")) {
            this.maPhieuTra = maPhieuTra;
        } else {
            throw new IllegalArgumentException("Mã phiếu trả không hợp lệ. Định dạng yêu cầu: PTxxxxxx");
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

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên không tồn tại.");
        }
        this.nhanVien = nhanVien;
    }



    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        if (ngayLap != null && ngayLap.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày lập không được là ngày trong tương lai.");
        }
        this.ngayLap = ngayLap;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        List<String> validStates = Arrays.asList("Đã nhập lại hàng", "Đã huỷ hàng", "Đang chờ duyệt");
        if (trangThai != null && validStates.contains(trangThai)) {
            this.trangThai = trangThai;
        } else {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
    }

    public List<ChiTietPhieuTra> getChiTietPhieuTraList() {
        return chiTietPhieuTraList;
    }

    public void setChiTietPhieuTraList(List<ChiTietPhieuTra> chiTietPhieuTraList) {
        if (chiTietPhieuTraList == null) {
             throw new IllegalArgumentException("Chi tiết phiếu trả không tồn tại.");
        }
        this.chiTietPhieuTraList = chiTietPhieuTraList;
    }

    @Override
    public String toString() {
        return "PhieuTra{" +
                "maPhieuTra='" + maPhieuTra + '\'' +
                ", khachHang=" + khachHang +
                ", nhanVien=" + nhanVien +
                ", ngayLap=" + ngayLap +
                ", trangThai='" + trangThai + '\'' +
                ", tongTienHoan=" + getTongTienHoan() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuTra phieuTra = (PhieuTra) o;
        return Objects.equals(maPhieuTra, phieuTra.maPhieuTra);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuTra);
    }
}