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
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.Layer;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
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
                null, null, null, dataset,
                PlotOrientation.VERTICAL, false, true, false
        );

        bieuDoCot.setBackgroundPaint(Color.WHITE);
        bieuDoCot.setAntiAlias(true);
        bieuDoCot.setTextAntiAlias(true);

        CategoryPlot vungVe = bieuDoCot.getCategoryPlot();
        vungVe.setOutlineVisible(false);
        vungVe.setBackgroundPaint(Color.WHITE);
        vungVe.setRangeGridlinePaint(new Color(220, 220, 220));
        vungVe.setDomainGridlinesVisible(false);

        Font fontTruc = new Font("Segoe UI", Font.PLAIN, 13);
        
        CategoryAxis trucX = vungVe.getDomainAxis();
        trucX.setAxisLineVisible(false);
        trucX.setTickMarksVisible(false);
        trucX.setTickLabelFont(fontTruc);
        trucX.setTickLabelPaint(new Color(100, 100, 100));

        NumberAxis trucY = (NumberAxis) vungVe.getRangeAxis();
        trucY.setAxisLineVisible(false);
        trucY.setTickMarksVisible(false);
        trucY.setTickLabelFont(fontTruc);
        trucY.setTickLabelPaint(new Color(100, 100, 100));
        // Sử dụng DecimalFormat để hiển thị số lớn không dùng ký hiệu khoa học
        DecimalFormat formatTrucY = new DecimalFormat("#,##0");
        formatTrucY.setGroupingUsed(true);
        trucY.setNumberFormatOverride(formatTrucY);

        BarRenderer rendererTuyChinh = new RendererTuyChinhEnhanced();
        rendererTuyChinh.setDrawBarOutline(false);
        rendererTuyChinh.setShadowVisible(false);
        rendererTuyChinh.setMaximumBarWidth(0.08); 
        rendererTuyChinh.setBarPainter(new StandardBarPainter());

        rendererTuyChinh.setDefaultItemLabelsVisible(true);
        Font fontGiaTri = new Font("Segoe UI", Font.BOLD, 15);
        // DecimalFormat cho label trên cột - hiển thị số lớn không dùng ký hiệu khoa học
        DecimalFormat dinhDangSo = new DecimalFormat("#,##0");
        dinhDangSo.setGroupingUsed(true);
        rendererTuyChinh.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", dinhDangSo));
        rendererTuyChinh.setDefaultItemLabelFont(fontGiaTri);
        rendererTuyChinh.setDefaultItemLabelPaint(new Color(50, 50, 50));
        
        // DecimalFormat cho tooltip - hiển thị số lớn không dùng ký hiệu khoa học
        DecimalFormat formatTooltip = new DecimalFormat("#,##0");
        formatTooltip.setGroupingUsed(true);
        rendererTuyChinh.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
                "{1} ({0}): {2}", formatTooltip));
        
        vungVe.setRenderer(rendererTuyChinh);

        return bieuDoCot;
    }
    
    private class RendererTuyChinhEnhanced extends BarRenderer {
        @Override
        public Paint getItemPaint(int hang, int cot) {
            String tenNhom = getPlot().getDataset().getRowKey(hang).toString();
            String tenDanhMuc = getPlot().getDataset().getColumnKey(cot).toString();
            for (DuLieuBieuDoCot duLieu : danhSachDuLieu) {
                if (duLieu.getTenNhom().equals(tenNhom) && duLieu.getTenDanhMuc().equals(tenDanhMuc)) {
                    Color mauGoc = duLieu.getMauSac();
                    Color mauNhatHon = new Color(
                        Math.min(255, mauGoc.getRed() + 30),
                        Math.min(255, mauGoc.getGreen() + 30),
                        Math.min(255, mauGoc.getBlue() + 30)
                    );
                    return new GradientPaint(0f, 0f, mauNhatHon, 0f, 100f, mauGoc); 
                }
            }
            return super.getItemPaint(hang, cot);
        }
    }

    private void capNhatBieuDo() {
        tapDuLieu.clear();
        for (DuLieuBieuDoCot duLieu : danhSachDuLieu) {
            tapDuLieu.addValue(duLieu.getGiaTri(), duLieu.getTenNhom(), duLieu.getTenDanhMuc());
        }
    }

    public void themDuLieu(DuLieuBieuDoCot duLieu) {
        danhSachDuLieu.add(duLieu);
        capNhatBieuDo();
    }

    public void xoaToanBoDuLieu() {
        danhSachDuLieu.clear();
        capNhatBieuDo();
    }
    
    public void setTieuDeBieuDo(String tieuDe) {
        Font fontTieuDe = new Font("Segoe UI", Font.BOLD, 18);
        bieuDo.setTitle(tieuDe);
        bieuDo.getTitle().setFont(fontTieuDe);
        bieuDo.getTitle().setPaint(new Color(50, 50, 50));
    }

    public void setLegendVisible(boolean visible) {
        bieuDo.getLegend().setVisible(visible);
    }
    
    public void setBuocNhayTrucY(double buocNhay) {
        CategoryPlot plot = bieuDo.getCategoryPlot();
        NumberAxis trucY = (NumberAxis) plot.getRangeAxis();
        if (buocNhay > 0) {
            trucY.setTickUnit(new NumberTickUnit(buocNhay));
        }
    }
    
    public void setDaiTrucY(double giaTriThapNhat, double giaTriCaoNhat) {
        CategoryPlot plot = bieuDo.getCategoryPlot();
        NumberAxis trucY = (NumberAxis) plot.getRangeAxis();
        if (giaTriCaoNhat > giaTriThapNhat) {
            trucY.setRange(giaTriThapNhat, giaTriCaoNhat);
        }
    }
    
    /**
     * === PHƯƠNG THỨC MỚI: Đặt tiêu đề cho trục ngang (X) ===
     * @param tieuDe Tên tiêu đề bạn muốn hiển thị
     */
    public void setTieuDeTrucX(String tieuDe) {
        CategoryPlot plot = bieuDo.getCategoryPlot();
        CategoryAxis trucX = plot.getDomainAxis();
        trucX.setLabel(tieuDe);
        trucX.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        trucX.setLabelPaint(new Color(80, 80, 80));
    }
    
    /**
     * === PHƯƠNG THỨC MỚI: Đặt tiêu đề cho trục dọc (Y) ===
     * @param tieuDe Tên tiêu đề bạn muốn hiển thị
     */
    public void setTieuDeTrucY(String tieuDe) {
        CategoryPlot plot = bieuDo.getCategoryPlot();
        ValueAxis trucY = plot.getRangeAxis();
        trucY.setLabel(tieuDe);
        trucY.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
        trucY.setLabelPaint(new Color(80, 80, 80));
    }
    /**
     * === PHƯƠNG THỨC MỚI: Thêm đường trung bình ===
     * @param giaTri Giá trị trung bình để vẽ đường kẻ ngang
     */
    public void themDuongTrungBinh(double giaTri) {
        if (giaTri <= 0) return;
        
        CategoryPlot plot = bieuDo.getCategoryPlot();
        
        // Tạo marker (đường kẻ)
        ValueMarker marker = new ValueMarker(giaTri);
        
        // Thiết lập màu sắc (Màu đỏ cam)
        marker.setPaint(new Color(255, 100, 100));
        
        // Thiết lập kiểu nét đứt (Dashed line)
        marker.setStroke(new BasicStroke(
            2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 
            1.0f, new float[]{10.0f, 6.0f}, 0.0f
        ));
        
        // Thiết lập nhãn hiển thị bên cạnh dòng
        marker.setLabel("Trung bình: " + new DecimalFormat("#,##0").format(giaTri));
        marker.setLabelFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 13));
        marker.setLabelPaint(new Color(200, 50, 50));
        marker.setLabelAnchor(RectangleAnchor.TOP_LEFT);
        marker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
        
        // Thêm vào lớp ForeGround (vẽ đè lên cột) hoặc Background (vẽ sau cột)
        plot.addRangeMarker(marker, Layer.FOREGROUND);
    }
}