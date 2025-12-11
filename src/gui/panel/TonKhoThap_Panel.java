package gui.panel;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import component.button.PillButton;

/**
 * Panel hiển thị danh sách sản phẩm có tồn kho thấp
 * Cảnh báo các sản phẩm cần nhập thêm hàng
 * Bao gồm: Dự báo hết hàng, SL đề xuất nhập, chi phí ước tính
 */
public class TonKhoThap_Panel extends JPanel {

    private JTable tblTonKho;
    private DefaultTableModel tableModel;
    private JLabel lblTongQuan;
    private JComboBox<Integer> cmbNguong;

    // Insight cards
    private JLabel lblTongSP;
    private JLabel lblChiPhiNhap;
    private JLabel lblCanNhapGap;
    private JLabel lblNCCGoiY;

    public TonKhoThap_Panel() {
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

        JLabel lblNguong = new JLabel("Ngưỡng tồn kho tối thiểu:");
        lblNguong.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblNguong.setBounds(20, 30, 180, 25);
        pnTieuChiLoc.add(lblNguong);

        Integer[] nguongOptions = { 5, 10, 20, 30, 50, 100 };
        cmbNguong = new JComboBox<>(nguongOptions);
        cmbNguong.setSelectedItem(10);
        cmbNguong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbNguong.setBounds(200, 28, 100, 30);
        pnTieuChiLoc.add(cmbNguong);

        JLabel lblLoaiSP = new JLabel("Loại sản phẩm:");
        lblLoaiSP.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblLoaiSP.setBounds(330, 30, 120, 25);
        pnTieuChiLoc.add(lblLoaiSP);

        String[] loaiOptions = { "Tất cả", "Thuốc kê đơn", "Thuốc không kê đơn", "Thực phẩm chức năng",
                "Dụng cụ y tế" };
        JComboBox<String> cmbLoaiSP = new JComboBox<>(loaiOptions);
        cmbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiSP.setBounds(450, 28, 180, 30);
        pnTieuChiLoc.add(cmbLoaiSP);

        JButton btnLoc = new PillButton("🔍 Lọc");
        btnLoc.setBounds(660, 25, 100, 35);
        pnTieuChiLoc.add(btnLoc);

        JButton btnXuatExcel = new PillButton("📥 Xuất Excel");
        btnXuatExcel.setBounds(780, 25, 120, 35);
        pnTieuChiLoc.add(btnXuatExcel);

        pnMain.add(pnTieuChiLoc, BorderLayout.NORTH);

        // ===== INSIGHT CARDS =====
        JPanel pnInsights = createInsightCardsPanel();

        // ===== PANEL TỔNG QUAN =====
        JPanel pnTongQuan = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnTongQuan.setBackground(new Color(0xFFF3CD));
        pnTongQuan.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(0xFFC107)),
                new EmptyBorder(10, 15, 10, 15)));
        pnTongQuan.setPreferredSize(new Dimension(0, 50));

        JLabel lblIcon = new JLabel("⚠️");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        pnTongQuan.add(lblIcon);

        lblTongQuan = new JLabel("Có 8 sản phẩm tồn kho thấp. Ưu tiên nhập 4 SP cần gấp!");
        lblTongQuan.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTongQuan.setForeground(new Color(0x856404));
        pnTongQuan.add(lblTongQuan);

        // ===== PANEL BẢNG =====
        JPanel pnBang = new JPanel(new BorderLayout());
        pnBang.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm tồn kho thấp"));
        pnBang.setBackground(Color.WHITE);

        // Thêm các cột mới: Dự báo hết, TB bán/ngày, SL đề xuất nhập, Chi phí ước tính
        String[] columnNames = { "STT", "Mã SP", "Tên sản phẩm", "Tồn kho", "TB bán/ngày", "Dự báo hết", "SL đề xuất",
                "Chi phí ước tính", "NCC gợi ý", "Trạng thái" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblTonKho = new JTable(tableModel);
        tblTonKho.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblTonKho.setRowHeight(32);
        tblTonKho.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblTonKho.getTableHeader().setBackground(new Color(0x0077B6));
        tblTonKho.getTableHeader().setForeground(Color.WHITE);

        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblTonKho.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblTonKho.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblTonKho.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblTonKho.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tblTonKho.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Căn phải cột chi phí
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblTonKho.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);

        // Custom renderer cho cột dự báo hết (màu theo urgency)
        tblTonKho.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String forecast = value.toString();
                if (forecast.contains("1 ngày") || forecast.contains("2 ngày") || forecast.contains("Hết")) {
                    setBackground(new Color(0xF8D7DA));
                    setForeground(new Color(0x721C24));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if (forecast.contains("3 ngày") || forecast.contains("4 ngày") || forecast.contains("5 ngày")) {
                    setBackground(new Color(0xFFF3CD));
                    setForeground(new Color(0x856404));
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        // Custom renderer cho cột trạng thái
        tblTonKho.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                String status = value.toString();
                if (status.contains("Cần nhập gấp")) {
                    setBackground(new Color(0xF8D7DA));
                    setForeground(new Color(0x721C24));
                } else if (status.contains("Cần nhập")) {
                    setBackground(new Color(0xFFF3CD));
                    setForeground(new Color(0x856404));
                } else {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                }
                return c;
            }
        });

        // Độ rộng cột
        tblTonKho.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblTonKho.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblTonKho.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblTonKho.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblTonKho.getColumnModel().getColumn(4).setPreferredWidth(75);
        tblTonKho.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblTonKho.getColumnModel().getColumn(6).setPreferredWidth(70);
        tblTonKho.getColumnModel().getColumn(7).setPreferredWidth(100);
        tblTonKho.getColumnModel().getColumn(8).setPreferredWidth(100);
        tblTonKho.getColumnModel().getColumn(9).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblTonKho);
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

        // Card 1: Tổng SP cần nhập
        JPanel card1 = createInsightCard("📦 TỔNG SP CẦN NHẬP", "8 sản phẩm", new Color(0xDC3545));
        lblTongSP = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(1);

        // Card 2: Chi phí nhập ước tính
        JPanel card2 = createInsightCard("💵 CHI PHÍ ƯỚC TÍNH", "45,600,000 VNĐ", new Color(0xFD7E14));
        lblChiPhiNhap = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(1);

        // Card 3: Cần nhập gấp
        JPanel card3 = createInsightCard("🚨 CẦN NHẬP GẤP", "4 SP (hết trong 3 ngày)", new Color(0xDC3545));
        lblCanNhapGap = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(1);

        // Card 4: NCC gợi ý
        JPanel card4 = createInsightCard("🏢 NCC GỢI Ý", "Dược phẩm Hậu Giang", new Color(0x0077B6));
        lblNCCGoiY = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(1);

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

        DecimalFormat dfMoney = new DecimalFormat("#,### VNĐ");

        // Dữ liệu mẫu với các metric mới
        // Format: Mã, Tên, Tồn kho, TB bán/ngày, Giá nhập, NCC
        Object[][] duLieuMau = {
                { "SP001", "Paracetamol 500mg", 3, 2.5, 50000L, "Dược Hậu Giang" },
                { "SP005", "Amoxicillin 250mg", 5, 1.8, 150000L, "Dược Cửu Long" },
                { "SP012", "Vitamin B Complex", 7, 1.2, 85000L, "Traphaco" },
                { "SP018", "Omeprazole 20mg", 8, 2.0, 120000L, "Dược Hậu Giang" },
                { "SP023", "Cetirizine 10mg", 4, 3.0, 45000L, "Imexpharm" },
                { "SP031", "Calcium + D3", 9, 1.5, 180000L, "Traphaco" },
                { "SP045", "Ibuprofen 400mg", 6, 1.0, 75000L, "Dược Cửu Long" },
                { "SP052", "Aspirin 81mg", 2, 2.2, 35000L, "Dược Hậu Giang" }
        };

        long tongChiPhi = 0;
        int countUrgent = 0;

        for (int i = 0; i < duLieuMau.length; i++) {
            Object[] row = duLieuMau[i];
            String maSP = (String) row[0];
            String tenSP = (String) row[1];
            int tonKho = (int) row[2];
            double tbBan = (double) row[3];
            long giaNhap = (long) row[4];
            String ncc = (String) row[5];

            // Tính dự báo hết hàng
            int duBaoHet = (int) Math.ceil(tonKho / tbBan);
            String duBaoText;
            if (duBaoHet <= 0) {
                duBaoText = "Đã hết!";
            } else if (duBaoHet == 1) {
                duBaoText = "1 ngày";
            } else {
                duBaoText = duBaoHet + " ngày";
            }

            // Tính SL đề xuất nhập (đủ bán 30 ngày)
            int slDeXuat = (int) Math.ceil(tbBan * 30) - tonKho;
            if (slDeXuat < 0)
                slDeXuat = 0;

            // Chi phí ước tính
            long chiPhi = slDeXuat * giaNhap;
            tongChiPhi += chiPhi;

            // Trạng thái
            String trangThai;
            if (duBaoHet <= 3) {
                trangThai = "🔴 Cần nhập gấp";
                countUrgent++;
            } else {
                trangThai = "🟡 Cần nhập";
            }

            tableModel.addRow(new Object[] {
                    i + 1,
                    maSP,
                    tenSP,
                    tonKho,
                    String.format("%.1f", tbBan),
                    duBaoText,
                    slDeXuat,
                    dfMoney.format(chiPhi),
                    ncc,
                    trangThai
            });
        }

        // Cập nhật insight cards
        lblTongSP.setText(duLieuMau.length + " sản phẩm");
        lblChiPhiNhap.setText(dfMoney.format(tongChiPhi));
        lblCanNhapGap.setText(countUrgent + " SP (hết trong 3 ngày)");

        // Cập nhật tổng quan
        lblTongQuan.setText(String.format("Có %d sản phẩm tồn kho thấp. Ưu tiên nhập %d SP cần gấp trước!",
                duLieuMau.length, countUrgent));
    }
}
