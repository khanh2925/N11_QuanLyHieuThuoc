package entity;

import java.util.Objects;

public class ChiTietBangGia {

    private BangGia bangGia;
    private double giaTu;
    private double giaDen;
    private double tiLe; // Ví dụ: 1.2 = giá bán = giá nhập * 1.2

    // ===== CONSTRUCTORS =====
    public ChiTietBangGia() {}

    public ChiTietBangGia(BangGia bangGia,
                          double giaTu, double giaDen, double tiLe) {
        setBangGia(bangGia);
        setGiaTu(giaTu);
        setGiaDen(giaDen);
        setTiLe(tiLe);
    }

    // ===== GETTERS / SETTERS =====
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
            throw new IllegalArgumentException("Giá từ phải lớn hơn hoặc bằng 0.");
        this.giaTu = giaTu;
    }

    public double getGiaDen() {
        return giaDen;
    }

    public void setGiaDen(double giaDen) {
        if (giaDen < giaTu)
            throw new IllegalArgumentException("Giá đến phải lớn hơn hoặc bằng giá từ.");
        this.giaDen = giaDen;
    }

    public double getTiLe() {
        return tiLe;
    }

    public void setTiLe(double tiLe) {
        if (tiLe <= 0 || tiLe > 5)
            throw new IllegalArgumentException("Tỉ lệ giá phải > 0 và ≤ 5 (ví dụ: 1.2 = lời 20%).");
        this.tiLe = tiLe;
    }

    // ===== OVERRIDES =====
    @Override
    public String toString() {
        return String.format(
            "CTBG[%s, tỉ lệ=%.2f, khoảng=%.0f–%.0f]",
            bangGia != null ? bangGia.getMaBangGia() : "N/A",
            tiLe, giaTu, giaDen
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChiTietBangGia)) return false;
        ChiTietBangGia that = (ChiTietBangGia) o;
        // So sánh dựa trên MaBangGia, GiaTu và GiaDen
        return Objects.equals(bangGia, that.bangGia)
               && giaTu == that.giaTu && giaDen == that.giaDen;
    }

    @Override
    public int hashCode() {
        // Hash dựa trên MaBangGia, GiaTu và GiaDen
        return Objects.hash(bangGia, giaTu, giaDen);
    }
}