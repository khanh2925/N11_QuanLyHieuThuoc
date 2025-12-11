package gui.panel;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.toedter.calendar.JDateChooser;

import component.button.PillButton;
import component.chart.BieuDoCotJFreeChart;
import component.chart.DuLieuBieuDoCot;

/**
 * Panel thống kê Top sản phẩm bán chạy
 * Hiển thị biểu đồ cột + bảng chi tiết top 10 sản phẩm
 * Bao gồm: Insight cards, % đóng góp, xu hướng
 */
public class TopSanPhamBanChay_Panel extends JPanel {

    private JDateChooser ngayBatDau;
    private JDateChooser ngayKetThuc;
    private BieuDoCotJFreeChart bieuDoTop;
    private JTable tblTopSanPham;
    private DefaultTableModel tableModel;

    // Insight cards labels
    private JLabel lblTongDoanhThu;
    private JLabel lblTopContribution;
    private JLabel lblBestSeller;
    private JLabel lblTrend;

    public TopSanPhamBanChay_Panel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel pnMain = new JPanel(new BorderLayout(0, 10));
        pnMain.setBackground(Color.WHITE);
        pnMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(pnMain, BorderLayout.CENTER);

        // ===== PANEL BỘ LỌC =====
        JPanel pnTieuChiLoc = new JPanel();
        pnTieuChiLoc.setBackground(new Color(0xE3F2F5));
        pnTieuChiLoc.setBorder(BorderFactory.createTitledBorder("Tiêu chí lọc"));
        pnTieuChiLoc.setPreferredSize(new Dimension(0, 100));
        pnTieuChiLoc.setLayout(null);

        JLabel lblTuNgay = new JLabel("Từ ngày");
        lblTuNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblTuNgay.setBounds(20, 25, 80, 20);
        pnTieuChiLoc.add(lblTuNgay);

        ngayBatDau = new JDateChooser();
        ngayBatDau.setDateFormatString("dd-MM-yyyy");
        ngayBatDau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ngayBatDau.setBounds(20, 50, 150, 30);
        pnTieuChiLoc.add(ngayBatDau);

        JLabel lblDenNgay = new JLabel("Đến ngày");
        lblDenNgay.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblDenNgay.setBounds(200, 25, 80, 20);
        pnTieuChiLoc.add(lblDenNgay);

        ngayKetThuc = new JDateChooser();
        ngayKetThuc.setDateFormatString("dd-MM-yyyy");
        ngayKetThuc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ngayKetThuc.setBounds(200, 50, 150, 30);
        pnTieuChiLoc.add(ngayKetThuc);

        JLabel lblSoLuong = new JLabel("Số lượng Top");
        lblSoLuong.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblSoLuong.setBounds(380, 25, 100, 20);
        pnTieuChiLoc.add(lblSoLuong);

        Integer[] topOptions = { 5, 10, 15, 20 };
        JComboBox<Integer> cmbSoLuong = new JComboBox<>(topOptions);
        cmbSoLuong.setSelectedItem(10);
        cmbSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbSoLuong.setBounds(380, 50, 100, 30);
        pnTieuChiLoc.add(cmbSoLuong);

        JButton btnThongKe = new PillButton("📊 Thống Kê");
        btnThongKe.setBounds(520, 45, 120, 35);
        pnTieuChiLoc.add(btnThongKe);

        JButton btnXuatExcel = new PillButton("📥 Xuất Excel");
        btnXuatExcel.setBounds(660, 45, 120, 35);
        pnTieuChiLoc.add(btnXuatExcel);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ===== INSIGHT CARDS =====
        JPanel pnInsights = createInsightCardsPanel();

        // ===== PANEL CHỨA BIỂU ĐỒ VÀ BẢNG =====
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);

        // Panel biểu đồ
        JPanel pnBieuDo = new JPanel(new BorderLayout());
        pnBieuDo.setBorder(BorderFactory.createTitledBorder("Biểu đồ Top sản phẩm bán chạy"));
        pnBieuDo.setBackground(Color.WHITE);
        pnBieuDo.setPreferredSize(new Dimension(0, 300));

        bieuDoTop = new BieuDoCotJFreeChart();
        bieuDoTop.setTieuDeBieuDo("Top 10 Sản Phẩm Bán Chạy");
        bieuDoTop.setTieuDeTrucX("Sản phẩm");
        bieuDoTop.setTieuDeTrucY("Số lượng bán");
        bieuDoTop.setBuocNhayTrucY(50);
        pnBieuDo.add(bieuDoTop, BorderLayout.CENTER);

        // Panel bảng với cột mới
        JPanel pnBang = new JPanel(new BorderLayout());
        pnBang.setBorder(BorderFactory.createTitledBorder("Chi tiết Top sản phẩm"));
        pnBang.setBackground(Color.WHITE);

        // Thêm cột % Đóng góp và Xu hướng
        String[] columnNames = { "STT", "Mã SP", "Tên sản phẩm", "Loại", "SL bán", "Doanh thu", "% Đóng góp",
                "Xu hướng" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTopSanPham = new JTable(tableModel);
        tblTopSanPham.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblTopSanPham.setRowHeight(30);
        tblTopSanPham.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblTopSanPham.getTableHeader().setBackground(new Color(0x0077B6));
        tblTopSanPham.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa các cột số
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblTopSanPham.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblTopSanPham.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblTopSanPham.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Căn phải cột doanh thu
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblTopSanPham.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        // Custom renderer cho cột xu hướng
        tblTopSanPham.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String trend = value.toString();
                if (trend.contains("↑")) {
                    setForeground(new Color(0x28A745)); // Xanh lá
                } else if (trend.contains("↓")) {
                    setForeground(new Color(0xDC3545)); // Đỏ
                } else {
                    setForeground(new Color(0x6C757D)); // Xám
                }

                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // Độ rộng cột
        tblTopSanPham.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblTopSanPham.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblTopSanPham.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblTopSanPham.getColumnModel().getColumn(3).setPreferredWidth(120);
        tblTopSanPham.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblTopSanPham.getColumnModel().getColumn(5).setPreferredWidth(120);
        tblTopSanPham.getColumnModel().getColumn(6).setPreferredWidth(80);
        tblTopSanPham.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(tblTopSanPham);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pnBang.add(scrollPane, BorderLayout.CENTER);

        // Thêm insight cards vào content
        JPanel pnTopSection = new JPanel(new BorderLayout(0, 10));
        pnTopSection.setBackground(Color.WHITE);
        pnTopSection.add(pnInsights, BorderLayout.NORTH);
        pnTopSection.add(pnBieuDo, BorderLayout.CENTER);

        pnContent.add(pnTopSection, BorderLayout.CENTER);
        pnContent.add(pnBang, BorderLayout.SOUTH);
        pnBang.setPreferredSize(new Dimension(0, 220));

        pnMain.add(pnContent, BorderLayout.CENTER);

        // Load dữ liệu mẫu
        loadDuLieuMau();
    }

    /**
     * Tạo panel chứa các Insight Cards
     */
    private JPanel createInsightCardsPanel() {
        JPanel pnInsights = new JPanel(new GridLayout(1, 4, 15, 0));
        pnInsights.setBackground(Color.WHITE);
        pnInsights.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnInsights.setPreferredSize(new Dimension(0, 80));

        // Card 1: Tổng doanh thu
        JPanel card1 = createInsightCard("💰 TỔNG DOANH THU", "260,100,000 VNĐ", new Color(0x0077B6));
        lblTongDoanhThu = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        // Card 2: Top 10 đóng góp
        JPanel card2 = createInsightCard("📊 TOP 10 CHIẾM", "78.5% doanh thu", new Color(0x00B4D8));
        lblTopContribution = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        // Card 3: SP bán chạy nhất
        JPanel card3 = createInsightCard("🏆 BÁN CHẠY #1", "Paracetamol 500mg", new Color(0x48CAE4));
        lblBestSeller = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        // Card 4: Xu hướng
        JPanel card4 = createInsightCard("📈 XU HƯỚNG", "↑ +12.5% vs tháng trước", new Color(0x28A745));
        lblTrend = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(1);

        pnInsights.add(card1);
        pnInsights.add(card2);
        pnInsights.add(card3);
        pnInsights.add(card4);

        return pnInsights;
    }

    /**
     * Tạo một Insight Card
     */
    private JPanel createInsightCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                new EmptyBorder(10, 15, 10, 15)));
        card.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
                card.getBorder()));

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 5));
        content.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(0x6C757D));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblValue.setForeground(accentColor);

        content.add(lblTitle);
        content.add(lblValue);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Load dữ liệu mẫu để hiển thị giao diện
     */
    private void loadDuLieuMau() {
        bieuDoTop.xoaToanBoDuLieu();
        tableModel.setRowCount(0);

        // Dữ liệu mẫu với xu hướng
        Object[][] duLieuMau = {
                { "SP001", "Paracetamol 500mg", "Thuốc giảm đau", 450, 22500000L, "+15%" },
                { "SP002", "Vitamin C 1000mg", "Thực phẩm CN", 380, 38000000L, "+8%" },
                { "SP003", "Amoxicillin 500mg", "Thuốc kháng sinh", 320, 48000000L, "-5%" },
                { "SP004", "Omeprazole 20mg", "Thuốc dạ dày", 280, 28000000L, "+12%" },
                { "SP005", "Calcium + D3", "Thực phẩm CN", 250, 37500000L, "+3%" },
                { "SP006", "Ibuprofen 400mg", "Thuốc giảm đau", 220, 17600000L, "-2%" },
                { "SP007", "Cetirizine 10mg", "Thuốc dị ứng", 200, 12000000L, "+25%" },
                { "SP008", "Metformin 500mg", "Thuốc tiểu đường", 180, 18000000L, "0%" },
                { "SP009", "Aspirin 81mg", "Thuốc tim mạch", 160, 8000000L, "-8%" },
                { "SP010", "Multivitamin", "Thực phẩm CN", 150, 30000000L, "+18%" }
        };

        Color[] colors = {
                new Color(255, 99, 132), new Color(54, 162, 235), new Color(255, 206, 86),
                new Color(75, 192, 192), new Color(153, 102, 255), new Color(255, 159, 64),
                new Color(199, 199, 199), new Color(83, 102, 255), new Color(255, 99, 255),
                new Color(99, 255, 132)
        };

        // Tính tổng doanh thu
        long tongDoanhThu = 0;
        for (Object[] row : duLieuMau) {
            tongDoanhThu += (long) row[4];
        }

        DecimalFormat dfMoney = new DecimalFormat("#,### VNĐ");
        DecimalFormat dfPercent = new DecimalFormat("0.0%");
        String tenNhom = "Số lượng";

        for (int i = 0; i < duLieuMau.length; i++) {
            Object[] row = duLieuMau[i];
            String maSP = (String) row[0];
            String tenSP = (String) row[1];
            String loai = (String) row[2];
            int soLuong = (int) row[3];
            long doanhThu = (long) row[4];
            String trendRaw = (String) row[5];

            // Tính % đóng góp
            double phanTram = (double) doanhThu / tongDoanhThu;

            // Format xu hướng
            String trend;
            if (trendRaw.startsWith("+")) {
                trend = "↑ " + trendRaw;
            } else if (trendRaw.startsWith("-")) {
                trend = "↓ " + trendRaw;
            } else {
                trend = "→ " + trendRaw;
            }

            // Thêm vào biểu đồ
            String tenRutGon = tenSP.length() > 15 ? tenSP.substring(0, 12) + "..." : tenSP;
            bieuDoTop.themDuLieu(new DuLieuBieuDoCot(tenRutGon, tenNhom, soLuong, colors[i % colors.length]));

            // Thêm vào bảng
            tableModel.addRow(new Object[] {
                    i + 1,
                    maSP,
                    tenSP,
                    loai,
                    soLuong,
                    dfMoney.format(doanhThu),
                    dfPercent.format(phanTram),
                    trend
            });
        }

        // Cập nhật insight cards
        lblTongDoanhThu.setText(dfMoney.format(tongDoanhThu));
        lblTopContribution.setText("78.5% doanh thu");
        lblBestSeller.setText("Paracetamol 500mg");
        lblTrend.setText("↑ +12.5% vs tháng trước");
    }
}
