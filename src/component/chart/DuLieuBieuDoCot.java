package component.chart;

import java.awt.Color;

public class DuLieuBieuDoCot {
    private String tenDanhMuc; // Category (Trục X - Tháng)
    private String tenNhom;    // Series (Chú thích - Bán/Nhập/Trả/Hủy)
    private double giaTri;     // Value (Trục Y - Tiền)
    private Color mauSac;

    /**
     * @param tenDanhMuc Tên hiển thị dưới trục hoành (Ví dụ: "Tháng 1")
     * @param tenNhom    Tên nhóm dữ liệu (Ví dụ: "Bán hàng", "Nhập hàng") - Sẽ tạo ra các cột khác màu
     * @param giaTri     Giá trị số
     * @param mauSac     Màu của cột
     */
    public DuLieuBieuDoCot(String tenDanhMuc, String tenNhom, double giaTri, Color mauSac) {
        this.tenDanhMuc = tenDanhMuc;
        this.tenNhom = tenNhom;
        this.giaTri = giaTri;
        this.mauSac = mauSac;
    }

    public String getTenDanhMuc() { return tenDanhMuc; }
    public String getTenNhom() { return tenNhom; }
    public double getGiaTri() { return giaTri; }
    public Color getMauSac() { return mauSac; }
}