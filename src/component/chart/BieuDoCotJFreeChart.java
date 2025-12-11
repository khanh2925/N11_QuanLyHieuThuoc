package component.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;

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
                null, null, null, dataset,
                PlotOrientation.VERTICAL, false, true, false
        );

        bieuDoCot.setBackgroundPaint(Color.WHITE);
        
        CategoryPlot vungVe = bieuDoCot.getCategoryPlot();
        vungVe.setBackgroundPaint(Color.WHITE);
        vungVe.setRangeGridlinePaint(new Color(220, 220, 220)); // Đường lưới ngang mờ
        vungVe.setDomainGridlinesVisible(false);
        vungVe.setOutlineVisible(false); // Tắt khung viền bao quanh

        // --- CẤU HÌNH TRỤC X (TRỤC NGANG) ---
        Font fontTruc = new Font("Segoe UI", Font.PLAIN, 13);
        CategoryAxis trucX = vungVe.getDomainAxis();
        
        // === ĐÂY LÀ PHẦN BẠN CẦN ===
        trucX.setAxisLineVisible(true);  // Hiện đường kẻ trục X
        trucX.setAxisLinePaint(new Color(150, 150, 150)); // Màu xám cho đường kẻ
        trucX.setAxisLineStroke(new BasicStroke(1.0f)); // Độ dày đường kẻ
        // ============================
        
        trucX.setTickMarksVisible(false); // Ẩn các vạch nhỏ đánh dấu
        trucX.setTickLabelFont(fontTruc);
        trucX.setTickLabelPaint(new Color(80, 80, 80));
        // Tăng khoảng cách giữa nhãn và trục một chút cho thoáng
        trucX.setTickLabelInsets(new org.jfree.chart.ui.RectangleInsets(5, 5, 5, 5));

        // --- CẤU HÌNH TRỤC Y (TRỤC DỌC) ---
        NumberAxis trucY = (NumberAxis) vungVe.getRangeAxis();
        trucY.setAxisLineVisible(false); // Ẩn trục dọc (chỉ để lưới ngang)
        trucY.setTickMarksVisible(false);
        trucY.setTickLabelFont(fontTruc);
        trucY.setTickLabelPaint(new Color(80, 80, 80));
        trucY.setNumberFormatOverride(new DecimalFormat("#,##0"));

        // --- RENDERER CỘT (BAR) ---
        BarRenderer rendererCot = new RendererTuyChinhEnhanced();
        rendererCot.setDrawBarOutline(false);
        rendererCot.setShadowVisible(false);
        rendererCot.setMaximumBarWidth(0.08); // Độ rộng cột tối đa
        rendererCot.setBarPainter(new StandardBarPainter()); // Bỏ hiệu ứng bóng 3D
        
        // Hiển thị số trên đỉnh cột
        rendererCot.setDefaultItemLabelsVisible(true);
        rendererCot.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("#,##0")));
        rendererCot.setDefaultItemLabelFont(new Font("Segoe UI", Font.BOLD, 11));
        rendererCot.setDefaultItemLabelPaint(new Color(50, 50, 50));
        
        // Tooltip
        rendererCot.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator("{1}: {2}", new DecimalFormat("#,##0")));
        
        vungVe.setRenderer(rendererCot);

        return bieuDoCot;
    }
    
    // --- VẼ ĐƯỜNG TRUNG BÌNH (AVERAGE LINE) ---
    public void veDuongTrungBinh(double giaTri) {
        CategoryPlot plot = bieuDo.getCategoryPlot();
        plot.clearRangeMarkers(); // Xóa đường cũ
        
        if (giaTri > 0) {
            ValueMarker marker = new ValueMarker(giaTri);
            marker.setPaint(new Color(220, 53, 69)); // Màu đỏ
            // Nét đứt (Dashed line)
            marker.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{8.0f, 4.0f}, 0.0f));
            
            // Label hiển thị giá trị
            marker.setLabel("TB: " + new DecimalFormat("#,##0").format(giaTri));
            marker.setLabelFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 11));
            marker.setLabelPaint(new Color(220, 53, 69));
            marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
            marker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
            
            plot.addRangeMarker(marker);
        }
    }
    
    // Renderer màu Gradient cho cột
    private class RendererTuyChinhEnhanced extends BarRenderer {
        @Override
        public Paint getItemPaint(int hang, int cot) {
            String tenNhom = getPlot().getDataset().getRowKey(hang).toString();
            String tenDanhMuc = getPlot().getDataset().getColumnKey(cot).toString();
            for (DuLieuBieuDoCot duLieu : danhSachDuLieu) {
                if (duLieu.getTenNhom().equals(tenNhom) && duLieu.getTenDanhMuc().equals(tenDanhMuc)) {
                    Color mauGoc = duLieu.getMauSac();
                    Color mauNhat = new Color(
                        Math.min(255, mauGoc.getRed() + 40),
                        Math.min(255, mauGoc.getGreen() + 40),
                        Math.min(255, mauGoc.getBlue() + 40)
                    );
                    return new GradientPaint(0f, 0f, mauNhat, 0f, 1000f, mauGoc); 
                }
            }
            return super.getItemPaint(hang, cot);
        }
    }

    public void themDuLieu(DuLieuBieuDoCot duLieu) {
        danhSachDuLieu.add(duLieu);
        tapDuLieu.addValue(duLieu.getGiaTri(), duLieu.getTenNhom(), duLieu.getTenDanhMuc());
    }

    public void xoaToanBoDuLieu() {
        danhSachDuLieu.clear();
        tapDuLieu.clear();
        bieuDo.getCategoryPlot().clearRangeMarkers();
    }
    
    public void setTieuDeTrucX(String tieuDe) {
        bieuDo.getCategoryPlot().getDomainAxis().setLabel(tieuDe);
        bieuDo.getCategoryPlot().getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
    }
    
    public void setTieuDeTrucY(String tieuDe) {
        bieuDo.getCategoryPlot().getRangeAxis().setLabel(tieuDe);
        bieuDo.getCategoryPlot().getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
    }
    
    public void setTieuDeBieuDo(String tieuDe) {
        bieuDo.setTitle(tieuDe);
    }
}