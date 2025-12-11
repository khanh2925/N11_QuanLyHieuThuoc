package component.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;

/**
 * Biểu đồ tròn với chú thích (legend) ở góc trên bên trái
 * Hiển thị % trên các phần của biểu đồ
 */
public class BieuDoTronLegend extends JPanel {

    private DefaultPieDataset tapDuLieu;
    private JFreeChart bieuDo;
    private ChartPanel khungBieuDo;
    private final List<DuLieuBieuDoTron> danhSachDuLieu;

    public BieuDoTronLegend() {
        danhSachDuLieu = new ArrayList<>();
        tapDuLieu = new DefaultPieDataset();
        bieuDo = taoBieuDo(tapDuLieu);
        khungBieuDo = new ChartPanel(bieuDo);
        khungBieuDo.setMouseWheelEnabled(true);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(khungBieuDo, BorderLayout.CENTER);
    }

    private JFreeChart taoBieuDo(DefaultPieDataset dataset) {
        // Tạo biểu đồ tròn với legend
        JFreeChart bieuDoMoi = ChartFactory.createPieChart(
                null, // Không có title
                dataset,
                true, // Có legend
                true, // Có tooltip
                false // Không có URL
        );

        // Cấu hình plot
        PiePlot vungVe = (PiePlot) bieuDoMoi.getPlot();
        vungVe.setBackgroundPaint(Color.WHITE);
        vungVe.setOutlineVisible(false);
        vungVe.setShadowPaint(null);

        // Hiển thị % trực tiếp trên slice
        vungVe.setSimpleLabels(true);
        vungVe.setLabelBackgroundPaint(null);
        vungVe.setLabelOutlinePaint(null);
        vungVe.setLabelShadowPaint(null);
        vungVe.setLabelPaint(Color.WHITE);
        vungVe.setLabelFont(new Font("Segoe UI", Font.BOLD, 14));

        // Format label: chỉ hiển thị %
        vungVe.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{2}", new DecimalFormat("0"), new DecimalFormat("0.0%")));
        vungVe.setToolTipGenerator(new StandardPieToolTipGenerator(
                "{0}: {1} ({2})", new DecimalFormat("#,##0"), new DecimalFormat("0.0%")));

        // Cấu hình legend - đặt ở góc trên bên trái
        LegendTitle legend = bieuDoMoi.getLegend();
        if (legend != null) {
            legend.setPosition(RectangleEdge.LEFT);
            legend.setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
            legend.setBackgroundPaint(new Color(255, 255, 255, 200));
            legend.setItemPaint(new Color(0x333333));
            legend.setPadding(new RectangleInsets(10, 10, 10, 10));
        }

        // Background trong suốt
        bieuDoMoi.setBackgroundPaint(Color.WHITE);

        return bieuDoMoi;
    }

    private void capNhatBieuDo() {
        tapDuLieu.clear();
        for (DuLieuBieuDoTron duLieu : danhSachDuLieu) {
            tapDuLieu.setValue(duLieu.getTen(), duLieu.getGiaTri());
        }

        PiePlot vungVe = (PiePlot) bieuDo.getPlot();
        for (int i = 0; i < danhSachDuLieu.size(); i++) {
            vungVe.setSectionPaint(danhSachDuLieu.get(i).getTen(), danhSachDuLieu.get(i).getMauSac());
        }
    }

    // --- Các phương thức public ---

    public void xoaDuLieu() {
        danhSachDuLieu.clear();
        capNhatBieuDo();
    }

    public void themDuLieu(DuLieuBieuDoTron data) {
        danhSachDuLieu.add(data);
        capNhatBieuDo();
    }
}
