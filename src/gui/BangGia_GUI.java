package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;

public class BangGia_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JPanel pnLeft;
    private JTextField txtTimThuoc;
    private JTextField txtTiLe;

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
        txtTimThuoc = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Tìm theo mã, tên...");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtTimThuoc.setBounds(10, 10, 285, 68);
        txtTimThuoc.setBorder(new LineBorder(new Color(0x00C0E2), 2, true));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setForeground(Color.GRAY);
        pnHeader.add(txtTimThuoc);

        // --- Label + ComboBox Loại sản phẩm ---
        JLabel lblLoaiSP = new JLabel("Loại sản phẩm:");
        lblLoaiSP.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLoaiSP.setBounds(320, 15, 130, 25);
        pnHeader.add(lblLoaiSP);

        String[] dsLoaiSP = {
            "Tất cả",
            "Thuốc giảm đau",
            "Kháng sinh",
            "Vitamin & Khoáng chất",
            "Dược mỹ phẩm",
            "Thiết bị y tế"
        };
        JComboBox<String> cbLoaiSP = new JComboBox<>(dsLoaiSP);
        cbLoaiSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbLoaiSP.setBounds(320, 45, 200, 32);
        pnHeader.add(cbLoaiSP);

        // --- Label + Radio Giá bán ---
        JLabel lblGiaBan = new JLabel("Giá bán:");
        lblGiaBan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGiaBan.setBounds(550, 15, 80, 25);
        pnHeader.add(lblGiaBan);

        JRadioButton rdbTangDan = new JRadioButton("Tăng dần");
        JRadioButton rdbGiamDan = new JRadioButton("Giảm dần");
        ButtonGroup groupGia = new ButtonGroup();
        groupGia.add(rdbTangDan);
        groupGia.add(rdbGiamDan);

        rdbTangDan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rdbGiamDan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rdbTangDan.setBackground(new Color(0xE3F2F5));
        rdbGiamDan.setBackground(new Color(0xE3F2F5));

        rdbTangDan.setBounds(550, 45, 100, 30);
        rdbGiamDan.setBounds(660, 45, 100, 30);
        pnHeader.add(rdbTangDan);
        pnHeader.add(rdbGiamDan);
        rdbTangDan.setSelected(true);

        // ===== LEFT =====
        pnLeft = new JPanel(null); // 🔥 absolute layout
        pnLeft.setPreferredSize(new Dimension(300, 1080));
        pnLeft.setBackground(Color.WHITE);
        pnLeft.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(pnLeft, BorderLayout.WEST);

        // ===== TIÊU ĐỀ =====
        JLabel lblDieuChinhGia = new JLabel("Điều chỉnh giá");
        lblDieuChinhGia.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblDieuChinhGia.setBounds(20, 10, 260, 30);
        pnLeft.add(lblDieuChinhGia);

        JSeparator line = new JSeparator();
        line.setBounds(20, 45, 260, 1);
        pnLeft.add(line);

        // ===== CÔNG THỨC =====
        JLabel lblCongThuc = new JLabel("Giá bán = Giá nhập + Tỉ lệ (%)");
        lblCongThuc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCongThuc.setForeground(Color.RED);
        lblCongThuc.setBounds(20, 60, 260, 25);
        pnLeft.add(lblCongThuc);

        // ===== Ô TỈ LỆ =====
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

        // ===== KHOẢNG GIÁ NHẬP =====
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

        // ===== NÚT ÁP DỤNG =====
        JButton btnApDung = new PillButton("Áp dụng");
        btnApDung.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnApDung.setBounds(80, 210, 120, 35);
        pnLeft.add(btnApDung);

        // ===== BẢNG MỐC GIÁ =====
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

        // ===== SỰ KIỆN ÁP DỤNG =====
        btnApDung.addActionListener(e -> {
            String tuStr = cbGiaTu.getSelectedItem().toString().trim();
            String denStr = cbGiaDen.getSelectedItem().toString().trim();
            String tile = txtTiLe.getText().trim();

            if (tuStr.isEmpty() || denStr.isEmpty() || tile.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            double tu = parseGia(tuStr);
            double den = parseGia(denStr);

            if (den <= tu) {
                JOptionPane.showMessageDialog(null, "Khoảng giá không hợp lệ (Giá đến phải lớn hơn giá từ)!");
                return;
            }

            // ✅ Kiểm tra trùng hoặc giao nhau
            for (int i = 0; i < modelMoc.getRowCount(); i++) {
                double tuCu = parseGia(modelMoc.getValueAt(i, 0).toString());
                double denCu = parseGia(modelMoc.getValueAt(i, 1).toString());

                if (!(den < tuCu || tu > denCu)) {
                    JOptionPane.showMessageDialog(null,
                        String.format("Khoảng giá %.0f - %.0f đã trùng hoặc giao với khoảng %.0f - %.0f!",
                                      tu, den, tuCu, denCu));
                    return;
                }
            }

            modelMoc.addRow(new Object[]{tuStr, denStr, tile});
        });

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        // ===== DỮ LIỆU MẪU =====
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

    // ===== HÀM HỖ TRỢ PARSE GIÁ =====
    private double parseGia(String giaStr) {
        try {
            giaStr = giaStr.replaceAll("[^\\d]", "");
            if (giaStr.isEmpty()) return 0;
            return Double.parseDouble(giaStr);
        } catch (Exception e) {
            return 0;
        }
    }

    // ===== MAIN TEST =====
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
