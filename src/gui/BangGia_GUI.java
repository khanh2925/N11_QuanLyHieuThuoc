package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

public class BangGia_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JPanel pnLeft;
    private JTextField txtTimThuoc;
    private JTextField txtTiLe;
    private JPanel pnLoc;

    public BangGia_GUI() {
        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1537, 1168));

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        // --- Ô tìm kiếm ---
        txtTimThuoc = new JTextField("");
        txtTimThuoc.setBounds(20, 17, 420, 60);
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Tìm kiếm sản phẩm theo mã");
        txtTimThuoc.setForeground(Color.GRAY);
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtTimThuoc.setPreferredSize(new Dimension(420, 44));
        txtTimThuoc.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimThuoc);

        // ===== PANEL LỌC VÀ SẮP XẾP =====
        pnLoc = new JPanel(new GridBagLayout());
        pnLoc.setBounds(460, 9, 600, 70);
        pnLoc.setOpaque(false);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(" Lọc và Sắp xếp ");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        pnLoc.setBorder(titledBorder);
        pnHeader.add(pnLoc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // === Các component trong pnLoc (fix lỗi add nhiều lần) ===
        GridBagConstraints gbc1 = (GridBagConstraints) gbc.clone();
        gbc1.gridx = 0; gbc1.gridy = 0;
        JLabel lblLoaiSP = new JLabel("Loại sản phẩm:");
        lblLoaiSP.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnLoc.add(lblLoaiSP, gbc1);

        GridBagConstraints gbc2 = (GridBagConstraints) gbc.clone();
        gbc2.gridx = 1; gbc2.gridy = 0;
        gbc2.insets = new Insets(5, 5, 5, 20);
        String[] dsLoaiSP = {"Tất cả", "Thuốc giảm đau", "Kháng sinh", "Vitamin & Khoáng chất", "Dược mỹ phẩm", "Thiết bị y tế"};
        JComboBox<String> cbLoaiSP = new JComboBox<>(dsLoaiSP);
        cbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnLoc.add(cbLoaiSP, gbc2);

        GridBagConstraints gbc3 = (GridBagConstraints) gbc.clone();
        gbc3.gridx = 2; gbc3.gridy = 0;
        JLabel lblGiaBan = new JLabel("Giá bán:");
        lblGiaBan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pnLoc.add(lblGiaBan, gbc3);

        GridBagConstraints gbc4 = (GridBagConstraints) gbc.clone();
        gbc4.gridx = 3; gbc4.gridy = 0;
        JRadioButton rdbTangDan = new JRadioButton("Tăng dần");
        rdbTangDan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rdbTangDan.setOpaque(false);
        rdbTangDan.setSelected(true);
        pnLoc.add(rdbTangDan, gbc4);

        GridBagConstraints gbc5 = (GridBagConstraints) gbc.clone();
        gbc5.gridx = 4; gbc5.gridy = 0;
        JRadioButton rdbGiamDan = new JRadioButton("Giảm dần");
        rdbGiamDan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rdbGiamDan.setOpaque(false);
        pnLoc.add(rdbGiamDan, gbc5);

        ButtonGroup groupGia = new ButtonGroup();
        groupGia.add(rdbTangDan);
        groupGia.add(rdbGiamDan);

        // ===== LEFT =====
        pnLeft = new JPanel(null);
        pnLeft.setPreferredSize(new Dimension(300, 1080));
        pnLeft.setBackground(Color.WHITE);
        pnLeft.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(pnLeft, BorderLayout.WEST);

        JLabel lblDieuChinhGia = new JLabel("Điều chỉnh giá");
        lblDieuChinhGia.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDieuChinhGia.setBounds(20, 10, 260, 30);
        pnLeft.add(lblDieuChinhGia);

        JSeparator line = new JSeparator();
        line.setBounds(20, 45, 260, 1);
        pnLeft.add(line);

        JLabel lblCongThuc = new JLabel("Giá bán = Giá nhập + Tỉ lệ (%)");
        lblCongThuc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCongThuc.setForeground(Color.RED);
        lblCongThuc.setBounds(20, 60, 260, 25);
        pnLeft.add(lblCongThuc);

        JLabel lblTiLe = new JLabel("Tỉ lệ %:");
        lblTiLe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTiLe.setBounds(20, 95, 60, 25);
        pnLeft.add(lblTiLe);

        txtTiLe = new JTextField();
        txtTiLe.setBounds(80, 90, 100, 30);
        txtTiLe.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTiLe.setForeground(new Color(0, 121, 107));
        txtTiLe.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtTiLe.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0x00C0E2), 2, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        txtTiLe.setBackground(new Color(240, 250, 250));
        pnLeft.add(txtTiLe);

        JLabel lblPhanTram = new JLabel("%");
        lblPhanTram.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPhanTram.setBounds(190, 95, 20, 25);
        pnLeft.add(lblPhanTram);

        JLabel lblKhoang = new JLabel("Khoảng giá nhập:");
        lblKhoang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKhoang.setBounds(20, 135, 200, 25);
        pnLeft.add(lblKhoang);

        String[] giaTuMau = {"0", "1.000", "5.000", "10.000", "20.000"};
        String[] giaDenMau = {"10.000", "20.000", "50.000", "100.000", "200.000", "Trở lên"};

        JComboBox<String> cbGiaTu = new JComboBox<>(giaTuMau);
        cbGiaTu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbGiaTu.setEditable(true);
        cbGiaTu.setBounds(20, 165, 90, 30);
        pnLeft.add(cbGiaTu);

        JLabel lblDen = new JLabel("đến");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDen.setBounds(115, 170, 30, 20);
        pnLeft.add(lblDen);

        JComboBox<String> cbGiaDen = new JComboBox<>(giaDenMau);
        cbGiaDen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbGiaDen.setEditable(true);
        cbGiaDen.setBounds(150, 165, 100, 30);
        pnLeft.add(cbGiaDen);

        JButton btnApDung = new PillButton("Áp dụng");
        btnApDung.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnApDung.setBounds(80, 210, 120, 35);
        pnLeft.add(btnApDung);

        String[] cols = {"Giá từ", "Giá đến", "Tỉ lệ (%)"};
        DefaultTableModel modelMoc = new DefaultTableModel(cols, 0);
        JTable tblMocGia = new JTable(modelMoc);
        tblMocGia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMocGia.setRowHeight(24);
        tblMocGia.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblMocGia.getTableHeader().setBackground(new Color(245, 247, 250));
        tblMocGia.getTableHeader().setForeground(Color.BLACK);
        tblMocGia.setGridColor(new Color(230, 230, 230));
        tblMocGia.setShowVerticalLines(false);

        JScrollPane sp = new JScrollPane(tblMocGia);
        sp.setBounds(20, 260, 260, 200);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        pnLeft.add(sp);

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        String[] columnNames = {"Mã sản phẩm", "Tên sản phẩm", "Giá nhập (VNĐ)", "Giá bán (VNĐ)"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        Object[][] duLieu = {
            {"SP-001", "Paracetamol 500mg", 1500, 2500},
            {"SP-002", "Amoxicillin 500mg", 2500, 4000},
            {"SP-003", "Vitamin C 500mg", 800, 1500},
            {"SP-004", "Panadol Extra", 2000, 3500},
            {"SP-005", "Cefuroxime 250mg", 4500, 7000},
            {"SP-006", "Aspirin 81mg", 1200, 2000},
            {"SP-007", "Efferalgan 500mg", 1800, 3000},
            {"SP-008", "Clarithromycin 500mg", 5000, 8000},
            {"SP-009", "Azithromycin 250mg", 4200, 7000},
            {"SP-010", "Omeprazol 20mg", 2000, 3500}
        };

        for (Object[] row : duLieu) model.addRow(row);

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(34);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setIntercellSpacing(new Dimension(8, 5));
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 252));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnCenter.add(scrollPane, BorderLayout.CENTER);
    }

    private double parseGia(String giaStr) {
        try {
            giaStr = giaStr.replaceAll("[^\\d]", "");
            if (giaStr.isEmpty()) return 0;
            return Double.parseDouble(giaStr);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bảng giá");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new BangGia_GUI());
            frame.setVisible(true);
        });
    }
}
