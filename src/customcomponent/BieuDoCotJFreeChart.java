package customcomponent;

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
        trucY.setNumberFormatOverride(new DecimalFormat("#,##0"));

        BarRenderer rendererTuyChinh = new RendererTuyChinhEnhanced();
        rendererTuyChinh.setDrawBarOutline(false);
        rendererTuyChinh.setShadowVisible(false);
        rendererTuyChinh.setMaximumBarWidth(0.08); 
        rendererTuyChinh.setBarPainter(new StandardBarPainter());

        rendererTuyChinh.setDefaultItemLabelsVisible(true);
        Font fontGiaTri = new Font("Segoe UI", Font.BOLD, 15);
        DecimalFormat dinhDangSo = new DecimalFormat("#,##0"); 
        rendererTuyChinh.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", dinhDangSo));
        rendererTuyChinh.setDefaultItemLabelFont(fontGiaTri);
        rendererTuyChinh.setDefaultItemLabelPaint(new Color(50, 50, 50));
        
        rendererTuyChinh.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
                "{1} ({0}): {2}", new DecimalFormat("#,##0")));
        
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
}