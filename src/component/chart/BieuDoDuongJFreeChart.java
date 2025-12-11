package component.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Biểu đồ đường (Line Chart) hiển thị xu hướng theo thời gian
 */
public class BieuDoDuongJFreeChart extends JPanel {

    private final DefaultCategoryDataset tapDuLieu;
    private final JFreeChart bieuDo;

    public BieuDoDuongJFreeChart() {
        tapDuLieu = new DefaultCategoryDataset();
        bieuDo = taoBieuDo(tapDuLieu);
        ChartPanel khungBieuDo = new ChartPanel(bieuDo);
        khungBieuDo.setMouseWheelEnabled(true);
        
        setLayout(new BorderLayout());
        add(khungBieuDo, BorderLayout.CENTER);
    }

    private JFreeChart taoBieuDo(DefaultCategoryDataset dataset) {
        JFreeChart bieuDoDuong = ChartFactory.createLineChart(
                null,                     // Tiêu đề biểu đồ
                null,                     // Nhãn trục X
                null,                     // Nhãn trục Y
                dataset,                  // Dữ liệu
                PlotOrientation.VERTICAL, // Hướng vẽ
                true,                     // Hiển thị chú thích
                true,                     // Tooltips
                false                     // URLs
        );

        // Tùy chỉnh nền biểu đồ
        bieuDoDuong.setBackgroundPaint(Color.WHITE);
        bieuDoDuong.setAntiAlias(true);
        bieuDoDuong.setTextAntiAlias(true);

        // Tùy chỉnh vùng vẽ
        CategoryPlot vungVe = bieuDoDuong.getCategoryPlot();
        vungVe.setOutlineVisible(false);
        vungVe.setBackgroundPaint(Color.WHITE);
        vungVe.setRangeGridlinePaint(new Color(220, 220, 220));
        vungVe.setDomainGridlinesVisible(false);

        // Tùy chỉnh font
        Font fontTruc = new Font("Segoe UI", Font.PLAIN, 12);
        Color mauChu = new Color(100, 100, 100);

        // Trục X (Category)
        CategoryAxis trucX = vungVe.getDomainAxis();
        trucX.setAxisLineVisible(false);
        trucX.setTickMarksVisible(false);
        trucX.setTickLabelFont(fontTruc);
        trucX.setTickLabelPaint(mauChu);

        // Trục Y (Value)
        NumberAxis trucY = (NumberAxis) vungVe.getRangeAxis();
        trucY.setAxisLineVisible(false);
        trucY.setTickMarksVisible(false);
        trucY.setTickLabelFont(fontTruc);
        trucY.setTickLabelPaint(mauChu);
        trucY.setNumberFormatOverride(new DecimalFormat("#,##0"));

        // Tùy chỉnh renderer (đường và điểm)
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        
        // Đường 1: Doanh thu - màu xanh dương
        renderer.setSeriesPaint(0, new Color(52, 152, 219));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, true);
        
        // Đường 2: Lợi nhuận - màu xanh lá
        renderer.setSeriesPaint(1, new Color(46, 204, 113));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(1, true);

        vungVe.setRenderer(renderer);

        // Tùy chỉnh chú thích (Legend)
        bieuDoDuong.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 13));
        bieuDoDuong.getLegend().setBackgroundPaint(Color.WHITE);
        bieuDoDuong.getLegend().setFrame(org.jfree.chart.block.BlockBorder.NONE);

        return bieuDoDuong;
    }

    /**
     * Thêm dữ liệu vào biểu đồ
     * @param series Tên chuỗi dữ liệu (VD: "Doanh thu", "Lợi nhuận")
     * @param category Nhãn trục X (VD: "Tháng 1", "Tháng 2")
     * @param value Giá trị
     */
    public void themDuLieu(String series, String category, double value) {
        tapDuLieu.addValue(value, series, category);
    }

    /**
     * Xóa toàn bộ dữ liệu
     */
    public void xoaDuLieu() {
        tapDuLieu.clear();
    }

    /**
     * Cập nhật lại biểu đồ
     */
    public void capNhatBieuDo() {
        bieuDo.fireChartChanged();
    }
}
