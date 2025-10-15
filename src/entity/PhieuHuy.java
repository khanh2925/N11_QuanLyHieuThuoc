package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhieuHuy {

    private String maPhieuHuy;
    private LocalDate ngayLapPhieu;
    private NhanVien nhanVien;
    private boolean trangThai;
    private List<ChiTietPhieuHuy> chiTietPhieuHuyList;

    public PhieuHuy() {
        this.chiTietPhieuHuyList = new ArrayList<>();
    }

    public PhieuHuy(String maPhieuHuy, LocalDate ngayLapPhieu, NhanVien nhanVien, boolean trangThai) {
        setMaPhieuHuy(maPhieuHuy);
        setNgayLapPhieu(ngayLapPhieu);
        setNhanVien(nhanVien);
        setTrangThai(trangThai);
        this.chiTietPhieuHuyList = new ArrayList<>();
    }

    public PhieuHuy(PhieuHuy other) {
        this.maPhieuHuy = other.maPhieuHuy;
        this.ngayLapPhieu = other.ngayLapPhieu;
        this.nhanVien = other.nhanVien;
        this.trangThai = other.trangThai;
        this.chiTietPhieuHuyList = new ArrayList<>(other.chiTietPhieuHuyList);
    }

    public String getMaPhieuHuy() {
        return maPhieuHuy;
    }

    public void setMaPhieuHuy(String maPhieuHuy) {
        if (maPhieuHuy != null && maPhieuHuy.matches("^PH-\\d{8}-\\d{4}$")) {
            this.maPhieuHuy = maPhieuHuy;
        } else {
            throw new IllegalArgumentException("Mã phiếu hủy không hợp lệ. Định dạng yêu cầu: PH-yyyymmdd-xxxx");
        }
    }

    public LocalDate getNgayLapPhieu() {
        return ngayLapPhieu;
    }

    public void setNgayLapPhieu(LocalDate ngayLapPhieu) {
        if (ngayLapPhieu != null && ngayLapPhieu.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày lập phiếu không hợp lệ.");
        }
        this.ngayLapPhieu = ngayLapPhieu;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if (nhanVien == null) {
            throw new IllegalArgumentException("Nhân viên quản lý không tồn tại.");
        }
        this.nhanVien = nhanVien;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
    
    public double getTongTien() {
        if (this.chiTietPhieuHuyList == null) {
            return 0;
        }
        double total = 0;
        for (ChiTietPhieuHuy ct : this.chiTietPhieuHuyList) {
            total += ct.getThanhTien();
        }
        return total;
    }

    public List<ChiTietPhieuHuy> getChiTietPhieuHuyList() {
        return chiTietPhieuHuyList;
    }

    public void setChiTietPhieuHuyList(List<ChiTietPhieuHuy> chiTietPhieuHuyList) {
        this.chiTietPhieuHuyList = chiTietPhieuHuyList;
    }

    @Override
    public String toString() {
        return "PhieuHuy{" +
                "maPhieuHuy='" + maPhieuHuy + '\'' +
                ", ngayLapPhieu=" + ngayLapPhieu +
                ", nhanVien=" + nhanVien +
                ", trangThai=" + trangThai +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuHuy phieuHuy = (PhieuHuy) o;
        return Objects.equals(maPhieuHuy, phieuHuy.maPhieuHuy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuHuy);
    }
}