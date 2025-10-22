package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;

public class BangGia_GUI extends JPanel {

    private JPanel pnCenter;   // vùng trung tâm
    private JPanel pnHeader;   // vùng đầu trang
    private JPanel pnLeft;     // vùng bên trái
    private JTextField txtTimThuoc;

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
        pnLeft = new JPanel();
        pnLeft.setPreferredSize(new Dimension(300, 1080));
        pnLeft.setBackground(Color.WHITE);
        pnLeft.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnLeft.setLayout(null); // dùng layout tự do
        add(pnLeft, BorderLayout.WEST);

        // ----- Tiêu đề -----
        JLabel lblTitle = new JLabel("Điều chỉnh giá");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(10, 10, 250, 30);
        pnLeft.add(lblTitle);

        // ----- Đường line ngăn cách -----
        JSeparator line = new JSeparator();
        line.setBounds(10, 45, 260, 2);
        pnLeft.add(line);

        // ----- Label “Công thức” -----
        JLabel lblCongThuc = new JLabel("Công thức:");
        lblCongThuc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCongThuc.setBounds(10, 60, 150, 25);
        pnLeft.add(lblCongThuc);

        // ----- Label mô tả công thức -----
        JLabel lblMoTa = new JLabel("Giá bán = Giá nhập + tỉ lệ (%)");
        lblMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMoTa.setForeground(new Color(90, 90, 90));
        lblMoTa.setBounds(10, 85, 250, 25);
        pnLeft.add(lblMoTa);

        // ----- Label + ô nhập tỉ lệ -----
        JLabel lblGiaNhap = new JLabel("Giá nhập + ");
        lblGiaNhap.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblGiaNhap.setBounds(10, 120, 80, 25);
        pnLeft.add(lblGiaNhap);

     // ----- Label + ô nhập tỉ lệ -----
        JLabel lblGiaNhap1 = new JLabel("Giá nhập + ");
        lblGiaNhap1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblGiaNhap1.setBounds(10, 120, 80, 25);
        pnLeft.add(lblGiaNhap1);

        // --- ComboBox tỉ lệ (%) ---
        String[] tiLeMau = {"5", "10", "15", "20", "25"};
        JComboBox<String> cbTiLe = new JComboBox<>(tiLeMau);
        cbTiLe.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cbTiLe.setBounds(90, 120, 60, 30);
        cbTiLe.setEditable(true); // Cho phép nhập tay
        cbTiLe.setBackground(Color.WHITE);
        pnLeft.add(cbTiLe);

        JLabel lblPhanTram = new JLabel("%");
        lblPhanTram.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblPhanTram.setBounds(155, 120, 20, 25);
        pnLeft.add(lblPhanTram);

     // ----- Label “Khoảng giá áp dụng” -----
        JLabel lblKhoangGia = new JLabel("Khoảng giá áp dụng:");
        lblKhoangGia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblKhoangGia.setBounds(10, 170, 250, 25);
        pnLeft.add(lblKhoangGia);

        // --- ComboBox giá từ ---
        String[] giaTuMau = {"0", "1.000", "5.000", "10.000", "20.000"};
        JComboBox<String> cbGiaTu = new JComboBox<>(giaTuMau);
        cbGiaTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbGiaTu.setBounds(10, 200, 100, 30);
        cbGiaTu.setEditable(true);
        pnLeft.add(cbGiaTu);

        // --- Label “đến” ---
        JLabel lblDen = new JLabel("đến");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDen.setBounds(120, 200, 30, 25);
        pnLeft.add(lblDen);

        // --- ComboBox giá đến ---
        String[] giaDenMau = {"10.000", "20.000", "50.000", "100.000", "200.000", "Trở lên"};
        JComboBox<String> cbGiaDen = new JComboBox<>(giaDenMau);
        cbGiaDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbGiaDen.setBounds(160, 200, 110, 30);
        cbGiaDen.setEditable(true);
        pnLeft.add(cbGiaDen);

        // ----- Nút áp dụng -----
        JButton btnApDung = new PillButton("Áp dụng");
        btnApDung.setBounds(80, 250, 120, 35);
        pnLeft.add(btnApDung);
        

        // ----- Bảng hiển thị các mốc giá đã set -----
        String[] cols = {"Giá từ (VNĐ)", "Giá đến (VNĐ)", "Tỉ lệ (%)"};
        DefaultTableModel modelMoc = new DefaultTableModel(cols, 0);
        JTable tblMocGia = new JTable(modelMoc);
        tblMocGia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMocGia.setRowHeight(28);
        tblMocGia.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblMocGia.getTableHeader().setBackground(new Color(230, 240, 250));
        tblMocGia.getTableHeader().setForeground(Color.BLACK);
        tblMocGia.setShowVerticalLines(false);
        tblMocGia.setGridColor(new Color(230, 230, 230));
     // ===== Sự kiện khi nhấn “Áp dụng” =====
        btnApDung.addActionListener(e -> {
            String tu = cbGiaTu.getSelectedItem().toString().trim();
            String den = cbGiaDen.getSelectedItem().toString().trim();
            String tile = "10"; // giả định tạm tỉ lệ 10%, có thể sửa sau
            if (tu.isEmpty() || den.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn khoảng giá hợp lệ!");
                return;
            }
            modelMoc.addRow(new Object[]{tu, den, tile});
        });
        JScrollPane sp = new JScrollPane(tblMocGia);
        sp.setBounds(10, 300, 260, 200);
        sp.setBorder(new LineBorder(new Color(220, 220, 220)));
        pnLeft.add(sp);
        
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

        for (Object[] row : duLieu) {
            model.addRow(row);
        }

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
