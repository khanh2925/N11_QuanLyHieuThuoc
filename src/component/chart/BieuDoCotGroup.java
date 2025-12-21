package component.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Class biểu đồ riêng dành cho Thống Kê Theo Tháng
 * Hỗ trợ vẽ nhiều cột sát nhau (Clustered Bar Chart)
 */
public class BieuDoCotGroup extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final DefaultCategoryDataset tapDuLieu;
    private final JFreeChart bieuDo;
    private final List<DuLieuBieuDoCot> danhSachDuLieu;

    public BieuDoCotGroup() {
        danhSachDuLieu = new ArrayList<>();
        tapDuLieu = new DefaultCategoryDataset();
        bieuDo = taoBieuDo(tapDuLieu);
        
        ChartPanel khungBieuDo = new ChartPanel(bieuDo);
        khungBieuDo.setDisplayToolTips(true);
        
        setLayout(new BorderLayout());
        add(khungBieuDo, BorderLayout.CENTER);
    }

    private JFreeChart taoBieuDo(DefaultCategoryDataset dataset) {
        // Tham số thứ 2 là 'true' để hiện Legend (Chú thích màu)
        JFreeChart chart = ChartFactory.createBarChart(
                null, null, null, dataset,
                PlotOrientation.VERTICAL, true, true, false
        );

        // 1. Cấu hình nền và viền
        chart.setBackgroundPaint(Color.WHITE);
        chart.setPadding(new RectangleInsets(10, 10, 10, 10));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200)); // Lưới ngang màu xám
        plot.setDomainGridlinesVisible(false);
        plot.setOutlineVisible(true);
        plot.setOutlinePaint(Color.BLACK); // Viền đen bao quanh biểu đồ

        // 2. Cấu hình Trục
        Font fontTruc = new Font("Segoe UI", Font.BOLD, 13);
        
        CategoryAxis trucX = plot.getDomainAxis();
        trucX.setTickLabelFont(fontTruc);
        trucX.setTickLabelPaint(Color.BLACK);
        trucX.setCategoryMargin(0.2); // Khoảng cách giữa các tháng

        NumberAxis trucY = (NumberAxis) plot.getRangeAxis();
        trucY.setTickLabelFont(fontTruc);
        trucY.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // 3. Cấu hình Renderer (QUAN TRỌNG NHẤT)
        BarRenderer renderer = new RendererTuyChinhFlat();
        
        // --- Style Phẳng (Giống hình mẫu) ---
        renderer.setBarPainter(new StandardBarPainter()); // Bỏ hiệu ứng bóng 3D/Gradient
        renderer.setShadowVisible(false); // Bỏ bóng đổ
        
        // --- Viền cột ---
        renderer.setDrawBarOutline(true);
        renderer.setDefaultOutlinePaint(Color.BLACK);
        
        // --- Dính sát nhau ---
        renderer.setItemMargin(0.0); // Khoảng cách giữa các cột trong cùng 1 tháng = 0
        
        // --- Tooltip ---
        renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator(
                "{0} - {1}: {2}", new DecimalFormat("#,##0")));

        plot.setRenderer(renderer);

        // 4. Cấu hình Chú thích (Legend)
        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
            chart.getLegend().setFrame(new org.jfree.chart.block.LineBorder());
        }

        return chart;
    }
    
    // Class con để tô màu tùy chỉnh (theo dữ liệu truyền vào)
private class RendererTuyChinhFlat extends BarRenderer {
        
        /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		// 1. Giữ nguyên hàm tô màu cột
        @SuppressWarnings("rawtypes")
		@Override
        public Paint getItemPaint(int row, int column) {
            Comparable rowKey = getPlot().getDataset().getRowKey(row);
            Comparable colKey = getPlot().getDataset().getColumnKey(column);
            
            for (DuLieuBieuDoCot duLieu : danhSachDuLieu) {
                if (duLieu.getTenNhom().equals(rowKey) && duLieu.getTenDanhMuc().equals(colKey)) {
                    return duLieu.getMauSac();
                }
            }
            return super.getItemPaint(row, column);
        }

        // 2. === THÊM MỚI: Ghi đè hàm này để Chú thích (Legend) đúng màu và thứ tự ===
        @SuppressWarnings("rawtypes")
		@Override
        public LegendItemCollection getLegendItems() {
            LegendItemCollection result = new LegendItemCollection();
            CategoryDataset dataset = getPlot().getDataset();
            
            if (dataset != null) {
                // Duyệt qua từng Series (Nhóm) theo đúng thứ tự đã thêm vào
                for (int i = 0; i < dataset.getRowCount(); i++) {
                    Comparable key = dataset.getRowKey(i);
                    String label = key.toString();
                    Paint paint = Color.GRAY; // Màu fallback
                    
                    // Tìm màu chính xác từ danh sách dữ liệu của bạn
                    for (DuLieuBieuDoCot data : danhSachDuLieu) {
                        if (data.getTenNhom().equals(key)) {
                            paint = data.getMauSac();
                            break; // Tìm thấy thì dừng ngay
                        }
                    }
                    
                    // Tạo item chú thích thủ công
                    LegendItem item = new LegendItem(
                        label,                  // Tên (Ví dụ: Bán hàng)
                        null, null, null,       // Các mô tả phụ (bỏ qua)
                        new java.awt.Rectangle(10, 10), // Hình dáng trong chú thích (Hình vuông 10x10)
                        paint                   // Màu sắc đúng
                    );
                    
                    // Thiết lập font chữ cho đẹp (optional)
                    item.setLabelFont(new Font("Segoe UI", Font.PLAIN, 13));
                    
                    result.add(item);
                }
            }
            return result;
        }
}

    public void themDuLieu(DuLieuBieuDoCot duLieu) {
        danhSachDuLieu.add(duLieu);
        // Add value: (Giá trị, Tên Nhóm/Series, Tên Danh Mục/Trục X)
        tapDuLieu.addValue(duLieu.getGiaTri(), duLieu.getTenNhom(), duLieu.getTenDanhMuc());
    }

    public void xoaToanBoDuLieu() {
        danhSachDuLieu.clear();
        tapDuLieu.clear();
    }
    
    public void setTieuDeBieuDo(String tieuDe) {
        bieuDo.setTitle(tieuDe);
        bieuDo.getTitle().setFont(new Font("Segoe UI", Font.BOLD, 18));
    }
    
    public void setTieuDeTrucX(String tieuDe) {
        bieuDo.getCategoryPlot().getDomainAxis().setLabel(tieuDe);
        bieuDo.getCategoryPlot().getDomainAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
    }
    
    public void setTieuDeTrucY(String tieuDe) {
        bieuDo.getCategoryPlot().getRangeAxis().setLabel(tieuDe);
        bieuDo.getCategoryPlot().getRangeAxis().setLabelFont(new Font("Segoe UI", Font.BOLD, 14));
    }
}