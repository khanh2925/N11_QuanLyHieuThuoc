package gui;

import java.awt.Color;
import javax.swing.JFrame;
import customcomponent.BieuDoTronJFreeChart;
import customcomponent.DuLieuBieuDoTron;

public class ChayThuNghiem {

    public static void main(String[] args) {
        JFrame cuaSo = new JFrame("Ví dụ Biểu đồ tròn JFreeChart");
        cuaSo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cuaSo.setSize(800, 600);
        cuaSo.setLocationRelativeTo(null);

        // Tạo một đối tượng biểu đồ tròn
        BieuDoTronJFreeChart bieuDoTron = new BieuDoTronJFreeChart();
        
        // Thêm dữ liệu vào biểu đồ
        bieuDoTron.themDuLieu(new DuLieuBieuDoTron("Sản phẩm A", 50, new Color(23, 126, 238)));
        bieuDoTron.themDuLieu(new DuLieuBieuDoTron("Sản phẩm B", 80, new Color(221, 65, 65)));
        bieuDoTron.themDuLieu(new DuLieuBieuDoTron("Sản phẩm C", 20, new Color(47, 157, 64)));
        bieuDoTron.themDuLieu(new DuLieuBieuDoTron("Sản phẩm D", 100, new Color(243, 156, 18)));
        
        // Tùy chọn: Chuyển sang dạng Donut
        // bieuDoTron.setKieuBieuDo(BieuDoTronJFreeChart.KieuBieuDo.HINH_VANH_KHUYEN);

        // Thêm biểu đồ vào cửa sổ và hiển thị
        cuaSo.add(bieuDoTron);
        cuaSo.setVisible(true);
    }
}