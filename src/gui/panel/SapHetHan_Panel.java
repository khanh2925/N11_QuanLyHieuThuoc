package gui.panel;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import component.button.PillButton;

/**
 * Panel hiển thị danh sách lô sản phẩm sắp hết hạn
 * Cảnh báo các sản phẩm cần xử lý trước khi hết hạn
 * Bao gồm: Giá trị thiệt hại, tốc độ bán, đề xuất hành động
 */
public class SapHetHan_Panel extends JPanel {

    private JTable tblSapHetHan;
    private DefaultTableModel tableModel;
    private JLabel lblTongQuan;
    private JComboBox<String> cmbThoiGian;

    // Insight cards
    private JLabel lblTongLo;
    private JLabel lblGiaTriThietHai;
    private JLabel lblCanXuLyGap;
    private JLabel lblDeXuatHanhDong;

    public SapHetHan_Panel() {
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

        JLabel lblThoiGian = new JLabel("Hết hạn trong vòng:");
        lblThoiGian.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblThoiGian.setBounds(20, 30, 150, 25);
        pnTieuChiLoc.add(lblThoiGian);

        String[] thoiGianOptions = { "7 ngày", "15 ngày", "30 ngày", "60 ngày", "90 ngày" };
        cmbThoiGian = new JComboBox<>(thoiGianOptions);
        cmbThoiGian.setSelectedItem("30 ngày");
        cmbThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbThoiGian.setBounds(170, 28, 120, 30);
        pnTieuChiLoc.add(cmbThoiGian);

        JLabel lblLoaiSP = new JLabel("Loại sản phẩm:");
        lblLoaiSP.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblLoaiSP.setBounds(320, 30, 120, 25);
        pnTieuChiLoc.add(lblLoaiSP);

        String[] loaiOptions = { "Tất cả", "Thuốc kê đơn", "Thuốc không kê đơn", "Thực phẩm chức năng",
                "Dụng cụ y tế" };
        JComboBox<String> cmbLoaiSP = new JComboBox<>(loaiOptions);
        cmbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiSP.setBounds(440, 28, 180, 30);
        pnTieuChiLoc.add(cmbLoaiSP);

        JButton btnLoc = new PillButton("🔍 Lọc");
        btnLoc.setBounds(650, 25, 100, 35);
        pnTieuChiLoc.add(btnLoc);

        JButton btnXuatExcel = new PillButton("📥 Xuất Excel");
        btnXuatExcel.setBounds(770, 25, 120, 35);
        pnTieuChiLoc.add(btnXuatExcel);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ===== INSIGHT CARDS =====
        JPanel pnInsights = createInsightCardsPanel();

        // ===== PANEL TỔNG QUAN =====
        JPanel pnTongQuan = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnTongQuan.setBackground(new Color(0xF8D7DA));
        pnTongQuan.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(0xDC3545)),
                new EmptyBorder(10, 15, 10, 15)));
        pnTongQuan.setPreferredSize(new Dimension(0, 50));

        JLabel lblIcon = new JLabel("⏰");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        pnTongQuan.add(lblIcon);

        lblTongQuan = new JLabel("Có 6 lô sản phẩm sắp hết hạn. 2 lô không kịp bán hết cần xử lý gấp!");
        lblTongQuan.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTongQuan.setForeground(new Color(0x721C24));
        pnTongQuan.add(lblTongQuan);

        // ===== PANEL BẢNG =====
        JPanel pnBang = new JPanel(new BorderLayout());
        pnBang.setBorder(BorderFactory.createTitledBorder("Danh sách lô sản phẩm sắp hết hạn"));
        pnBang.setBackground(Color.WHITE);

        // Thêm các cột mới
        String[] columnNames = { "STT", "Mã Lô", "Tên sản phẩm", "HSD", "Còn lại", "SL tồn", "TB bán/ngày", "Kịp bán?",
                "Giá trị", "Đề xuất" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSapHetHan = new JTable(tableModel);
        tblSapHetHan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblSapHetHan.setRowHeight(32);
        tblSapHetHan.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblSapHetHan.getTableHeader().setBackground(new Color(0x0077B6));
        tblSapHetHan.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblSapHetHan.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblSapHetHan.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblSapHetHan.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblSapHetHan.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tblSapHetHan.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Căn phải cột giá trị
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblSapHetHan.getColumnModel().getColumn(8).setCellRenderer(rightRenderer);

        // Custom renderer cho cột "Kịp bán?"
        tblSapHetHan.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String status = value.toString();
                if (status.contains("Không")) {
                    setBackground(new Color(0xF8D7DA));
                    setForeground(new Color(0x721C24));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (status.contains("Khó")) {
                    setBackground(new Color(0xFFF3CD));
                    setForeground(new Color(0x856404));
                } else {
                    setBackground(new Color(0xD4EDDA));
                    setForeground(new Color(0x155724));
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        // Custom renderer cho cột Đề xuất
        tblSapHetHan.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(getFont().deriveFont(Font.BOLD, 11f));

                String suggestion = value.toString();
                if (suggestion.contains("Hủy")) {
                    setBackground(new Color(0xF8D7DA));
                    setForeground(new Color(0x721C24));
                } else if (suggestion.contains("Giảm")) {
                    setBackground(new Color(0xFFF3CD));
                    setForeground(new Color(0x856404));
                } else {
                    setBackground(new Color(0xD4EDDA));
                    setForeground(new Color(0x155724));
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        // Độ rộng cột
        tblSapHetHan.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblSapHetHan.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblSapHetHan.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblSapHetHan.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblSapHetHan.getColumnModel().getColumn(4).setPreferredWidth(65);
        tblSapHetHan.getColumnModel().getColumn(5).setPreferredWidth(55);
        tblSapHetHan.getColumnModel().getColumn(6).setPreferredWidth(75);
        tblSapHetHan.getColumnModel().getColumn(7).setPreferredWidth(70);
        tblSapHetHan.getColumnModel().getColumn(8).setPreferredWidth(90);
        tblSapHetHan.getColumnModel().getColumn(9).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(tblSapHetHan);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        pnBang.add(scrollPane, BorderLayout.CENTER);

        // Panel chứa insight + tổng quan + bảng
        JPanel pnContent = new JPanel(new BorderLayout(0, 10));
        pnContent.setBackground(Color.WHITE);

        JPanel pnTop = new JPanel(new BorderLayout(0, 10));
        pnTop.setBackground(Color.WHITE);
        pnTop.add(pnInsights, BorderLayout.NORTH);
        pnTop.add(pnTongQuan, BorderLayout.SOUTH);

        pnContent.add(pnTop, BorderLayout.NORTH);
        pnContent.add(pnBang, BorderLayout.CENTER);

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

        // Card 1: Tổng lô sắp hết hạn
        JPanel card1 = createInsightCard("📦 TỔNG LÔ SẮP HẾT HẠN", "6 lô", new Color(0xDC3545));
        lblTongLo = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        // Card 2: Giá trị thiệt hại
        JPanel card2 = createInsightCard("💸 GIÁ TRỊ THIỆT HẠI", "18,500,000 VNĐ", new Color(0xDC3545));
        lblGiaTriThietHai = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        // Card 3: Cần xử lý gấp
        JPanel card3 = createInsightCard("🚨 KHÔNG KỊP BÁN", "2 lô (cần hủy/KM)", new Color(0xFD7E14));
        lblCanXuLyGap = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        // Card 4: Đề xuất
        JPanel card4 = createInsightCard("💡 ĐỀ XUẤT", "Giảm giá 4 lô", new Color(0x28A745));
        lblDeXuatHanhDong = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(1);

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
        tableModel.setRowCount(0);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DecimalFormat dfMoney = new DecimalFormat("#,### VNĐ");

        // Dữ liệu mẫu: Mã lô, Tên SP, HSD, SL tồn, TB bán/ngày, Giá trị/đơn vị
        Object[][] duLieuMau = {
                { "LO001", "Paracetamol 500mg", today.plusDays(5), 150, 8.0, 50000L },
                { "LO008", "Amoxicillin 250mg", today.plusDays(3), 80, 5.0, 150000L },
                { "LO015", "Vitamin C 1000mg", today.plusDays(12), 200, 12.0, 100000L },
                { "LO023", "Omeprazole 20mg", today.plusDays(25), 120, 4.0, 120000L },
                { "LO031", "Cetirizine 10mg", today.plusDays(8), 90, 15.0, 45000L },
                { "LO045", "Calcium + D3", today.plusDays(28), 180, 5.0, 150000L }
        };

        long tongThietHai = 0;
        int countKhongKip = 0;
        int countCanGiam = 0;

        for (int i = 0; i < duLieuMau.length; i++) {
            Object[] row = duLieuMau[i];
            String maLo = (String) row[0];
            String tenSP = (String) row[1];
            LocalDate hsd = (LocalDate) row[2];
            int slTon = (int) row[3];
            double tbBan = (double) row[4];
            long giaTri = (long) row[5];

            long daysLeft = ChronoUnit.DAYS.between(today, hsd);

            // Tính số lượng có thể bán được trong thời gian còn lại
            int coTheBan = (int) (tbBan * daysLeft);

            // Phân tích kịp bán không
            String kipBan;
            String deXuat;
            int slKhongBanDuoc = slTon - coTheBan;

            if (coTheBan >= slTon) {
                kipBan = "✅ Kịp";
                deXuat = "Bán bình thường";
            } else if (coTheBan >= slTon * 0.7) {
                kipBan = "⚠️ Khó";
                deXuat = "Giảm giá 10-20%";
                countCanGiam++;
                tongThietHai += slKhongBanDuoc * giaTri * 20 / 100; // Ước tính thiệt hại 20%
            } else {
                kipBan = "❌ Không";
                deXuat = "Hủy/Giảm 50%";
                countKhongKip++;
                tongThietHai += slKhongBanDuoc * giaTri * 50 / 100; // Ước tính thiệt hại 50%
            }

            long giaTriLo = slTon * giaTri;

            tableModel.addRow(new Object[] {
                    i + 1,
                    maLo,
                    tenSP,
                    hsd.format(formatter),
                    daysLeft + " ngày",
                    slTon,
                    String.format("%.1f", tbBan),
                    kipBan,
                    dfMoney.format(giaTriLo),
                    deXuat
            });
        }

        // Cập nhật insight cards
        lblTongLo.setText(duLieuMau.length + " lô");
        lblGiaTriThietHai.setText(dfMoney.format(tongThietHai));
        lblCanXuLyGap.setText(countKhongKip + " lô (cần hủy/KM)");
        lblDeXuatHanhDong.setText("Giảm giá " + countCanGiam + " lô");

        // Cập nhật tổng quan
        lblTongQuan.setText(String.format("Có %d lô sản phẩm sắp hết hạn. %d lô không kịp bán hết cần xử lý gấp!",
                duLieuMau.length, countKhongKip));
    }
}
