package customcomponent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class BieuDoTronJFreeChart extends JPanel {

    // Enum để xác định loại biểu đồ
    public static enum KieuBieuDo {
        MAC_DINH,       // Biểu đồ tròn thông thường
        HINH_VANH_KHUYEN // Biểu đồ Donut
    }

    private DefaultPieDataset tapDuLieu;
    private JFreeChart bieuDo;
    private ChartPanel khungBieuDo;
    private final List<DuLieuBieuDoTron> danhSachDuLieu;
    private KieuBieuDo kieuBieuDo = KieuBieuDo.MAC_DINH;
    private int chiSoDaChon = -1;

    public BieuDoTronJFreeChart() {
        danhSachDuLieu = new ArrayList<>();
        tapDuLieu = new DefaultPieDataset();
        bieuDo = taoBieuDo(tapDuLieu);
        khungBieuDo = new ChartPanel(bieuDo);
        khungBieuDo.setMouseWheelEnabled(true);
        
        setLayout(new BorderLayout());
        add(khungBieuDo, BorderLayout.CENTER);
        
        themSuKienChuot();
    }
    
    private JFreeChart taoBieuDo(DefaultPieDataset dataset) {
        JFreeChart bieuDoMoi;

        if (kieuBieuDo == KieuBieuDo.HINH_VANH_KHUYEN) {
            bieuDoMoi = ChartFactory.createRingChart(null, dataset, false, true, false);
        } else {
            bieuDoMoi = ChartFactory.createPieChart(null, dataset, false, true, false);
        }

        PiePlot vungVe = (PiePlot) bieuDoMoi.getPlot();
        vungVe.setBackgroundPaint(null);
        vungVe.setOutlineVisible(false);
        vungVe.setShadowPaint(null);
        vungVe.setLabelBackgroundPaint(null);
        vungVe.setLabelOutlinePaint(null);
        vungVe.setLabelShadowPaint(null);
        vungVe.setLabelPaint(Color.WHITE);
        vungVe.setLabelFont(new Font("SansSerif", Font.BOLD, 12));
        
        vungVe.setSimpleLabels(true);
        vungVe.setLabelGenerator(new StandardPieSectionLabelGenerator(
            "{2}", new DecimalFormat("0"), new DecimalFormat("0.0%")));
        vungVe.setToolTipGenerator(new StandardPieToolTipGenerator(
            "{0}: {1} ({2})", new DecimalFormat("#,##0.#"), new DecimalFormat("0.0%")));

        bieuDoMoi.setBackgroundPaint(this.getBackground());
        
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

        for (int i = 0; i < tapDuLieu.getItemCount(); i++) {
            vungVe.setExplodePercent(tapDuLieu.getKey(i), 0.0);
        }
        if (chiSoDaChon != -1) {
            Comparable khoa = tapDuLieu.getKey(chiSoDaChon);
            vungVe.setExplodePercent(khoa, 0.10);
        }
    }

    private void themSuKienChuot() {
        khungBieuDo.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                ChartEntity doiTuong = event.getEntity();
                if (doiTuong instanceof PieSectionEntity) {
                    PieSectionEntity doiTuongLatCat = (PieSectionEntity) doiTuong;
                    int chiSo = doiTuongLatCat.getSectionIndex();
                    
                    if (chiSo == chiSoDaChon) {
                        chiSoDaChon = -1;
                    } else {
                        chiSoDaChon = chiSo;
                    }
                    capNhatBieuDo();
                }
            }
            @Override
            public void chartMouseMoved(ChartMouseEvent event) {}
        });
    }

    // --- Các phương thức public để tương tác với biểu đồ ---
    public void xoaDuLieu() {
        danhSachDuLieu.clear();
        chiSoDaChon = -1;
        capNhatBieuDo();
    }

    public void themDuLieu(DuLieuBieuDoTron data) {
        danhSachDuLieu.add(data);
        capNhatBieuDo();
    }

    public int getChiSoDaChon() {
        return chiSoDaChon;
    }

    public void setChiSoDaChon(int chiSo) {
        if (chiSo >= -1 && chiSo < danhSachDuLieu.size()) {
            this.chiSoDaChon = chiSo;
            capNhatBieuDo();
        }
    }

    public KieuBieuDo getKieuBieuDo() {
        return kieuBieuDo;
    }

    public void setKieuBieuDo(KieuBieuDo kieuBieuDo) {
        if (this.kieuBieuDo != kieuBieuDo) {
            this.kieuBieuDo = kieuBieuDo;
            this.bieuDo = taoBieuDo(tapDuLieu);
            this.khungBieuDo.setChart(this.bieuDo);
            capNhatBieuDo();
        }
    }
}