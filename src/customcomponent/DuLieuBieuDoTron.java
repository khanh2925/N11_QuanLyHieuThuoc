package customcomponent;

import java.awt.Color;

/**
 * Lớp này đại diện cho dữ liệu của một phần trong biểu đồ tròn.
 */
public class DuLieuBieuDoTron {

    private String ten;      // Tên của lát cắt (ví dụ: "Sản phẩm A")
    private double giaTri;   // Giá trị tương ứng
    private Color mauSac;    // Màu sắc để hiển thị

    // Constructor để khởi tạo đối tượng với đầy đủ thông tin
    public DuLieuBieuDoTron(String ten, double giaTri, Color mauSac) {
        this.ten = ten;
        this.giaTri = giaTri;
        this.mauSac = mauSac;
    }

    // Constructor rỗng
    public DuLieuBieuDoTron() {
    }

    // --- Các phương thức Getter và Setter ---
    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public double getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(double giaTri) {
        this.giaTri = giaTri;
    }

    public Color getMauSac() {
        return mauSac;
    }

    public void setMauSac(Color mauSac) {
        this.mauSac = mauSac;
    }
}