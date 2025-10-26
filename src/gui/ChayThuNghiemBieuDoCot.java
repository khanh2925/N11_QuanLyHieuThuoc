package gui;

import customcomponent.BieuDoCotJFreeChart;
import customcomponent.DuLieuBieuDoCot;
import java.awt.Color;
import javax.swing.JFrame;

public class ChayThuNghiemBieuDoCot {
    public static void main(String[] args) {
        JFrame cuaSo = new JFrame("Ví dụ Biểu đồ cột");
        cuaSo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cuaSo.setSize(900, 650);
        cuaSo.setLocationRelativeTo(null);

        // 1. Tạo đối tượng biểu đồ cột
        BieuDoCotJFreeChart bieuDoCot = new BieuDoCotJFreeChart();
        
        // 2. Đặt lại tiêu đề cho biểu đồ
        bieuDoCot.setTieuDeBieuDo("Thống kê doanh thu 2 tháng cuối năm");

        // 3. Chuẩn bị dữ liệu
        String nhomThang10 = "Tháng 10";
        String nhomThang11 = "Tháng 11";

        // Thêm dữ liệu cho Tháng 10
        bieuDoCot.themDuLieu(new DuLieuBieuDoCot("Điện thoại", nhomThang10, 50, new Color(23, 126, 238)));
        bieuDoCot.themDuLieu(new DuLieuBieuDoCot("Laptop", nhomThang10, 80, new Color(221, 65, 65)));
        bieuDoCot.themDuLieu(new DuLieuBieuDoCot("Phụ kiện", nhomThang10, 20, new Color(47, 157, 64)));

        // Thêm dữ liệu cho Tháng 11
        bieuDoCot.themDuLieu(new DuLieuBieuDoCot("Điện thoại", nhomThang11, 65, new Color(114, 191, 255)));
        bieuDoCot.themDuLieu(new DuLieuBieuDoCot("Laptop", nhomThang11, 75, new Color(255, 128, 128)));
        bieuDoCot.themDuLieu(new DuLieuBieuDoCot("Phụ kiện", nhomThang11, 35, new Color(126, 217, 138)));

        // 4. Thêm biểu đồ vào cửa sổ và hiển thị
        cuaSo.add(bieuDoCot);
        cuaSo.setVisible(true);
    }
}