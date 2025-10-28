package entity;

import java.util.Objects;

public class ChiTietBangGia {

    private BangGia bangGia;
    private double giaTu;
    private double giaDen;
    private double tiLeTang;

    public ChiTietBangGia() {}

    public ChiTietBangGia(BangGia bangGia, double giaTu, double giaDen, double tiLeTang) {
        setBangGia(bangGia);
        setGiaTu(giaTu);
        setGiaDen(giaDen);
        setTiLeTang(tiLeTang);
    }

    public BangGia getBangGia() {
        return bangGia;
    }

    public void setBangGia(BangGia bangGia) {
        if (bangGia == null)
            throw new IllegalArgumentException("Bảng giá không được null.");
        this.bangGia = bangGia;
    }

    public double getGiaTu() {
        return giaTu;
    }

    public void setGiaTu(double giaTu) {
        if (giaTu < 0)
            throw new IllegalArgumentException("Giá từ không được âm.");
        this.giaTu = giaTu;
    }

    public double getGiaDen() {
        return giaDen;
    }

    public void setGiaDen(double giaDen) {
        if (giaDen <= giaTu)
            throw new IllegalArgumentException("Giá đến phải lớn hơn giá từ.");
        this.giaDen = giaDen;
    }

    public double getTiLeTang() {
        return tiLeTang;
    }

    public void setTiLeTang(double tiLeTang) {
        if (tiLeTang < 0)
            throw new IllegalArgumentException("Tỉ lệ tăng không hợp lệ.");
        this.tiLeTang = tiLeTang;
    }

    @Override
    public String toString() {
        return "ChiTietBangGia{" +
                "bangGia=" + (bangGia != null ? bangGia.getMaBangGia() : "N/A") +
                ", giaTu=" + giaTu +
                ", giaDen=" + giaDen +
                ", tiLeTang=" + tiLeTang +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietBangGia)) return false;
        ChiTietBangGia that = (ChiTietBangGia) o;
        return Objects.equals(bangGia, that.bangGia)
                && giaTu == that.giaTu
                && giaDen == that.giaDen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bangGia, giaTu, giaDen);
    }
}
