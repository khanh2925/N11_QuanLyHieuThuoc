package component.chart;

import java.awt.Color;

/**
 * Lớp này chứa dữ liệu cho một cột (hoặc một phần của nhóm cột) trong biểu đồ.
 */
public class DuLieuBieuDoCot {

    private String tenDanhMuc; // Tên hiển thị dưới cột (Vd: "Sản phẩm A")
    private String tenNhom;    // Tên của nhóm dữ liệu (Vd: "Tháng 10", hiển thị ở chú thích)
    private double giaTri;     // Chiều cao của cột
    private Color mauSac;      // Màu của cột

    // Constructor
    public DuLieuBieuDoCot(String tenDanhMuc, String tenNhom, double giaTri, Color mauSac) {
        this.tenDanhMuc = tenDanhMuc;
        this.tenNhom = tenNhom;
        this.giaTri = giaTri;
        this.mauSac = mauSac;
    }

    // --- Các phương thức Getter ---
    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public String getTenNhom() {
        return tenNhom;
    }

    public double getGiaTri() {
        return giaTri;
    }

    public Color getMauSac() {
        return mauSac;
    }
}