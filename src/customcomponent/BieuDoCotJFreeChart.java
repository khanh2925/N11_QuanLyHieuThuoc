package customcomponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

public class BieuDoCotJFreeChart extends JPanel {

    private final DefaultCategoryDataset tapDuLieu;
    private final JFreeChart bieuDo;
    private final List<DuLieuBieuDoCot> danhSachDuLieu;

    public BieuDoCotJFreeChart() {
        danhSachDuLieu = new ArrayList<>();
        tapDuLieu = new DefaultCategoryDataset();
        bieuDo = taoBieuDo(tapDuLieu);
        ChartPanel khungBieuDo = new ChartPanel(bieuDo);
        
        setLayout(new BorderLayout());
        add(khungBieuDo, BorderLayout.CENTER);
    }

    private JFreeChart taoBieuDo(DefaultCategoryDataset dataset) {
        JFreeChart bieuDoCot = ChartFactory.createBarChart(
                "Thống kê",               // Tiêu đề mặc định của biểu đồ
                "Danh mục",               // Tên trục X
                "Giá trị",                // Tên trục Y
                dataset,
                PlotOrientation.VERTICAL, // Hướng biểu đồ: Dọc
                true,                     // Hiển thị chú thích (legend)
                true,                     // Hiển thị tooltip (khi di chuột)
                false                     // Không dùng URLs
        );

        // Tùy chỉnh giao diện
        CategoryPlot vungVe = bieuDoCot.getCategoryPlot();
        vungVe.setBackgroundPaint(Color.WHITE); // Màu nền của vùng vẽ
        vungVe.setRangeGridlinePaint(new Color(220, 220, 220)); // Màu đường kẻ ngang

        // Sử dụng một renderer tùy chỉnh để tô màu cho từng cột
        BarRenderer rendererTuyChinh = new RendererTuyChinh();
        vungVe.setRenderer(rendererTuyChinh);

        // Các tùy chỉnh khác cho cột
        rendererTuyChinh.setDrawBarOutline(false); // Bỏ đường viền cột
        rendererTuyChinh.setBarPainter(new StandardBarPainter()); // Dùng màu đặc, không có hiệu ứng gradient
        rendererTuyChinh.setShadowVisible(false); // Tắt bóng đổ

        // Định dạng cho tooltip
        rendererTuyChinh.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
                "{1} ({0}): {2}", new DecimalFormat("#,##0.#")));

        return bieuDoCot;
    }
    
    /**
     * Lớp nội bộ (inner class) để tùy chỉnh việc tô màu cho từng cột.
     * Nó sẽ tìm màu trong danh sách dữ liệu bạn đã thêm vào.
     */
    private class RendererTuyChinh extends BarRenderer {
        @Override
        public Paint getItemPaint(int hang, int cot) {
            String tenNhom = (String) getPlot().getDataset().getRowKey(hang);
            String tenDanhMuc = (String) getPlot().getDataset().getColumnKey(cot);

            for (DuLieuBieuDoCot duLieu : danhSachDuLieu) {
                if (duLieu.getTenNhom().equals(tenNhom) && duLieu.getTenDanhMuc().equals(tenDanhMuc)) {
                    return duLieu.getMauSac();
                }
            }
            // Trả về màu mặc định nếu không tìm thấy
            return super.getItemPaint(hang, cot);
        }
    }

    private void capNhatBieuDo() {
        tapDuLieu.clear();
        for (DuLieuBieuDoCot duLieu : danhSachDuLieu) {
            tapDuLieu.addValue(duLieu.getGiaTri(), duLieu.getTenNhom(), duLieu.getTenDanhMuc());
        }
    }

    // --- Các phương thức public để bên ngoài sử dụng ---
    public void themDuLieu(DuLieuBieuDoCot duLieu) {
        danhSachDuLieu.add(duLieu);
        capNhatBieuDo();
    }

    public void xoaToanBoDuLieu() {
        danhSachDuLieu.clear();
        capNhatBieuDo();
    }
    
    public void setTieuDeBieuDo(String tieuDe) {
        bieuDo.setTitle(tieuDe);
    }
}