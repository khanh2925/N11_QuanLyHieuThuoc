package gui.panel;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.Year;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import component.button.PillButton;
import component.chart.BieuDoTronJFreeChart;
import component.chart.DuLieuBieuDoTron;

/**
 * Panel thống kê sản phẩm theo loại
 * Hiển thị biểu đồ tròn phân bố doanh thu theo từng loại sản phẩm
 * Bao gồm: So sánh kỳ trước, lợi nhuận theo loại
 */
public class ThongKeTheoLoai_Panel extends JPanel {

    private BieuDoTronJFreeChart bieuDoTron;
    private JTable tblChiTiet;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> cmbNam;

    // Insight cards
    private JLabel lblTongDoanhThu;
    private JLabel lblLoiNhuan;
    private JLabel lblLoaiTotNhat;
    private JLabel lblXuHuong;

    public ThongKeTheoLoai_Panel() {
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
        pnTieuChiLoc.setPreferredSize(new Dimension(0, 80));
        pnTieuChiLoc.setLayout(null);

        JLabel lblNam = new JLabel("Chọn năm:");
        lblNam.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNam.setBounds(20, 30, 80, 25);
        pnTieuChiLoc.add(lblNam);

        int currentYear = Year.now().getValue();
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - i;
        }
        cmbNam = new JComboBox<>(years);
        cmbNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNam.setBounds(100, 28, 100, 30);
        pnTieuChiLoc.add(cmbNam);

        JButton btnThongKe = new PillButton("📊 Thống Kê");
        btnThongKe.setBounds(230, 25, 120, 35);
        pnTieuChiLoc.add(btnThongKe);

        JButton btnXuatExcel = new PillButton("📥 Xuất Excel");
        btnXuatExcel.setBounds(370, 25, 120, 35);
        pnTieuChiLoc.add(btnXuatExcel);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ===== INSIGHT CARDS =====
        JPanel pnInsights = createInsightCardsPanel();

        // ===== PANEL NỘI DUNG CHÍNH =====
        JPanel pnContent = new JPanel(new GridLayout(1, 2, 20, 0));
        pnContent.setBackground(Color.WHITE);

        // Panel biểu đồ tròn (bên trái)
        JPanel pnBieuDo = new JPanel(new BorderLayout());
        pnBieuDo.setBorder(BorderFactory.createTitledBorder("Phân bổ doanh thu theo loại sản phẩm"));
        pnBieuDo.setBackground(Color.WHITE);

        bieuDoTron = new BieuDoTronJFreeChart();
        bieuDoTron.setKieuBieuDo(BieuDoTronJFreeChart.KieuBieuDo.HINH_VANH_KHUYEN);
        pnBieuDo.add(bieuDoTron, BorderLayout.CENTER);

        // Panel chi tiết (bên phải)
        JPanel pnChiTiet = new JPanel(new BorderLayout(0, 10));
        pnChiTiet.setBorder(BorderFactory.createTitledBorder("Chi tiết theo loại"));
        pnChiTiet.setBackground(Color.WHITE);

        // Bảng chi tiết với cột mới
        String[] columnNames = { "Loại sản phẩm", "SL SP", "Doanh thu", "Chi phí", "Lợi nhuận", "Tỷ lệ LN",
                "So với năm trước" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTiet = new JTable(tableModel);
        tblChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblChiTiet.setRowHeight(35);
        tblChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblChiTiet.getTableHeader().setBackground(new Color(0x0077B6));
        tblChiTiet.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa/phải các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Custom renderer cho cột so sánh năm trước
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(getFont().deriveFont(Font.BOLD));

                String trend = value.toString();
                if (trend.contains("↑")) {
                    setForeground(new Color(0x28A745));
                } else if (trend.contains("↓")) {
                    setForeground(new Color(0xDC3545));
                } else {
                    setForeground(new Color(0x6C757D));
                }

                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // Độ rộng cột
        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(140);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(50);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblChiTiet.getColumnModel().getColumn(5).setPreferredWidth(60);
        tblChiTiet.getColumnModel().getColumn(6).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblChiTiet);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Panel tổng quan
        JPanel pnTongQuan = new JPanel(new GridLayout(2, 4, 10, 10));
        pnTongQuan.setBackground(new Color(0xE3F2F5));
        pnTongQuan.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnTongQuan.setPreferredSize(new Dimension(0, 100));

        Font labelFont = new Font("Tahoma", Font.PLAIN, 12);
        Font valueFont = new Font("Tahoma", Font.BOLD, 14);
        Color valueColor = new Color(0x0077B6);

        pnTongQuan.add(createSummaryItem("💰 Tổng doanh thu:", "850,000,000 VNĐ", labelFont, valueFont, valueColor));
        pnTongQuan.add(
                createSummaryItem("💵 Tổng chi phí:", "510,000,000 VNĐ", labelFont, valueFont, new Color(0xDC3545)));
        pnTongQuan.add(
                createSummaryItem("📈 Tổng lợi nhuận:", "340,000,000 VNĐ", labelFont, valueFont, new Color(0x28A745)));
        pnTongQuan
                .add(createSummaryItem("📊 Tỷ lệ LN trung bình:", "40.0%", labelFont, valueFont, new Color(0x28A745)));
        pnTongQuan.add(createSummaryItem("📦 Tổng số sản phẩm:", "156 sản phẩm", labelFont, valueFont, valueColor));
        pnTongQuan.add(
                createSummaryItem("🏆 Loại LN cao nhất:", "Thuốc kê đơn", labelFont, valueFont, new Color(0x28A745)));
        pnTongQuan.add(
                createSummaryItem("📉 Loại LN thấp nhất:", "Dụng cụ y tế", labelFont, valueFont, new Color(0xDC3545)));
        pnTongQuan
                .add(createSummaryItem("🔄 So với năm trước:", "↑ +15.5%", labelFont, valueFont, new Color(0x28A745)));

        pnChiTiet.add(scrollPane, BorderLayout.CENTER);
        pnChiTiet.add(pnTongQuan, BorderLayout.SOUTH);

        pnContent.add(pnBieuDo);
        pnContent.add(pnChiTiet);

        // Panel chứa insight + content
        JPanel pnMainContent = new JPanel(new BorderLayout(0, 10));
        pnMainContent.setBackground(Color.WHITE);
        pnMainContent.add(pnInsights, BorderLayout.NORTH);
        pnMainContent.add(pnContent, BorderLayout.CENTER);

        pnMain.add(pnMainContent, BorderLayout.CENTER);

        // Load dữ liệu mẫu
        loadDuLieuMau();
    }

    /**
     * Tạo một summary item
     */
    private JPanel createSummaryItem(String label, String value, Font labelFont, Font valueFont, Color valueColor) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 2));
        panel.setBackground(new Color(0xE3F2F5));

        JLabel lblLabel = new JLabel(label, SwingConstants.LEFT);
        lblLabel.setFont(labelFont);
        lblLabel.setForeground(new Color(0x6C757D));

        JLabel lblValue = new JLabel(value, SwingConstants.LEFT);
        lblValue.setFont(valueFont);
        lblValue.setForeground(valueColor);

        panel.add(lblLabel);
        panel.add(lblValue);
        return panel;
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
        JPanel card1 = createInsightCard("💰 TỔNG DOANH THU", "850,000,000 VNĐ", new Color(0x0077B6));
        lblTongDoanhThu = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        // Card 2: Lợi nhuận
        JPanel card2 = createInsightCard("📈 LỢI NHUẬN", "340,000,000 VNĐ (40%)", new Color(0x28A745));
        lblLoiNhuan = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        // Card 3: Loại sinh lời nhất
        JPanel card3 = createInsightCard("🏆 LOẠI SINH LỜI NHẤT", "Thuốc kê đơn (45%)", new Color(0xFD7E14));
        lblLoaiTotNhat = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        // Card 4: So với năm trước
        JPanel card4 = createInsightCard("🔄 SO VỚI NĂM TRƯỚC", "↑ +15.5% doanh thu", new Color(0x28A745));
        lblXuHuong = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(1);

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
                BorderFactory.createLineBorder(new Color(0xE0E0E0), 1),
                new CompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                        new EmptyBorder(10, 15, 10, 15))));

        JPanel content = new JPanel(new GridLayout(2, 1, 0, 5));
        content.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(0x6C757D));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblValue.setForeground(accentColor);

        content.add(lblTitle);
        content.add(lblValue);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void loadDuLieuMau() {
        bieuDoTron.xoaDuLieu();
        tableModel.setRowCount(0);

        DecimalFormat dfMoney = new DecimalFormat("#,###");
        DecimalFormat dfPercent = new DecimalFormat("0.0%");

        // Dữ liệu mẫu: Loại, SL SP, Doanh thu, Chi phí, % thay đổi so với năm trước
        Object[][] duLieuMau = {
                { "Thuốc kê đơn", 45, 320000000L, 176000000L, 15, new Color(0x0077B6) },
                { "Thuốc không kê đơn", 38, 180000000L, 108000000L, -5, new Color(0x00B4D8) },
                { "Thực phẩm chức năng", 42, 250000000L, 150000000L, 22, new Color(0x90E0EF) },
                { "Dụng cụ y tế", 31, 100000000L, 76000000L, 8, new Color(0xCAF0F8) }
        };

        long tongDoanhThu = 0;
        long tongLoiNhuan = 0;
        String loaiTotNhat = "";
        double tyLeTotNhat = 0;

        for (Object[] row : duLieuMau) {
            long doanhThu = (long) row[2];
            long chiPhi = (long) row[3];
            tongDoanhThu += doanhThu;
            tongLoiNhuan += (doanhThu - chiPhi);

            double tyLeLN = (double) (doanhThu - chiPhi) / doanhThu;
            if (tyLeLN > tyLeTotNhat) {
                tyLeTotNhat = tyLeLN;
                loaiTotNhat = (String) row[0];
            }
        }

        for (Object[] row : duLieuMau) {
            String loai = (String) row[0];
            int soLuong = (int) row[1];
            long doanhThu = (long) row[2];
            long chiPhi = (long) row[3];
            int thayDoi = (int) row[4];
            Color mau = (Color) row[5];

            long loiNhuan = doanhThu - chiPhi;
            double tyLeLN = (double) loiNhuan / doanhThu;

            // Format xu hướng
            String trend;
            if (thayDoi > 0) {
                trend = "↑ +" + thayDoi + "%";
            } else if (thayDoi < 0) {
                trend = "↓ " + thayDoi + "%";
            } else {
                trend = "→ 0%";
            }

            // Thêm vào biểu đồ tròn
            bieuDoTron.themDuLieu(new DuLieuBieuDoTron(loai, doanhThu, mau));

            // Thêm vào bảng
            tableModel.addRow(new Object[] {
                    loai,
                    soLuong,
                    dfMoney.format(doanhThu),
                    dfMoney.format(chiPhi),
                    dfMoney.format(loiNhuan),
                    dfPercent.format(tyLeLN),
                    trend
            });
        }

        // Cập nhật insight cards
        double tyLeLNTB = (double) tongLoiNhuan / tongDoanhThu;
        lblTongDoanhThu.setText(dfMoney.format(tongDoanhThu) + " VNĐ");
        lblLoiNhuan.setText(dfMoney.format(tongLoiNhuan) + " VNĐ (" + dfPercent.format(tyLeLNTB) + ")");
        lblLoaiTotNhat.setText(loaiTotNhat + " (" + dfPercent.format(tyLeTotNhat) + ")");
        lblXuHuong.setText("↑ +15.5% doanh thu");
    }
}
