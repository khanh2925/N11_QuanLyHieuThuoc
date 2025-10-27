package entity;

import java.time.LocalDate;
import java.util.Objects;

public class LoSanPham {

    private String maLo;
    private LocalDate ngaySanXuat;
    private LocalDate hanSuDung;
    private int soLuong;
    private SanPham sanPham;

    public LoSanPham() {
    }

    public LoSanPham(String maLo, LocalDate ngaySanXuat, LocalDate hanSuDung, int soLuong, SanPham sanPham) {
        this.maLo = maLo;
        setNgaySanXuat(ngaySanXuat);
        setHanSuDung(hanSuDung);
        setSoLuong(soLuong);
        setSanPham(sanPham);
    }

    public LoSanPham(LoSanPham other) {
        this.maLo = other.maLo;
        this.ngaySanXuat = other.ngaySanXuat;
        this.hanSuDung = other.hanSuDung;
        this.soLuong = other.soLuong;
        this.sanPham = other.sanPham;
    }
    public LoSanPham(String maLo) {
        this.maLo = maLo;
    }

    public String getMaLo() {
        return maLo;
    }

    public void setMaLo(String maLo) {
        if (maLo != null && maLo.matches("^LO-\\d{6}$")) {
            this.maLo = maLo;
        } else {
             throw new IllegalArgumentException("Mã lô không hợp lệ. Định dạng yêu cầu: LO-xxxxxx");
        }
    }

    public LocalDate getNgaySanXuat() {
        return ngaySanXuat;
    }

    public void setNgaySanXuat(LocalDate ngaySanXuat) {
        if (ngaySanXuat == null || ngaySanXuat.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sản xuất không hợp lệ.");
        }
        this.ngaySanXuat = ngaySanXuat;
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null) {
            throw new IllegalArgumentException("Hạn sử dụng không được rỗng.");
        }
        if (this.ngaySanXuat != null && hanSuDung.isBefore(this.ngaySanXuat)) {
            throw new IllegalArgumentException("Hạn sử dụng phải sau hoặc bằng ngày sản xuất.");
        }
        this.hanSuDung = hanSuDung;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn hoặc bằng 0.");
        }
        this.soLuong = soLuong;
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

    @Override
    public String toString() {
        return "LoSanPham{" +
                "maLo='" + maLo + '\'' +
                ", ngaySanXuat=" + ngaySanXuat +
                ", hanSuDung=" + hanSuDung +
                ", soLuong=" + soLuong +
                ", sanPham=" + (sanPham != null ? sanPham.getTenSanPham() : "N/A") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoSanPham loSanPham = (LoSanPham) o;
        return Objects.equals(maLo, loSanPham.maLo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLo);
    }
}