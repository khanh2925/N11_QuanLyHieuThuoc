package gui;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import customcomponent.PillButton;

public class QuyCachDongGoi_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;
    private DefaultTableModel model;

    // ===== Dữ liệu mẫu =====
    private static class QuyCachDongGoi {
        private String maQCDG;
        private String sanPham;
        private String donViTinh;
        private int heSoQuyDoi;
        private double tiLeGiam;
        private boolean donViGoc;

        public QuyCachDongGoi(String maQCDG, String sanPham, String donViTinh,
                              int heSoQuyDoi, double tiLeGiam, boolean donViGoc) {
            this.maQCDG = maQCDG;
            this.sanPham = sanPham;
            this.donViTinh = donViTinh;
            this.heSoQuyDoi = heSoQuyDoi;
            this.tiLeGiam = tiLeGiam;
            this.donViGoc = donViGoc;
        }
    }

    private List<QuyCachDongGoi> dsQuyCach;

    public QuyCachDongGoi_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // ===== HEADER =====
        pnHeader = new JPanel();
        pnHeader.setPreferredSize(new Dimension(1073, 88));
        pnHeader.setBackground(new Color(0xE3F2F5));
        pnHeader.setLayout(null);
        add(pnHeader, BorderLayout.NORTH);

        txtTimKiem = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên sản phẩm...");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBounds(20, 27, 350, 44);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);

        PillButton btnThem = new PillButton("Thêm");
        btnThem.setBounds(393, 35, 93, 28);
        pnHeader.add(btnThem);

        PillButton btnCapNhat = new PillButton("Cập nhật");
        btnCapNhat.setBounds(496, 35, 100, 28);
        pnHeader.add(btnCapNhat);

        PillButton btnXoa = new PillButton("Xoá");
        btnXoa.setBounds(604, 35, 93, 28);
        pnHeader.add(btnXoa);

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        // ===== DỮ LIỆU MẪU =====
        dsQuyCach = new ArrayList<>();
        dsQuyCach.add(new QuyCachDongGoi("QCDG-001", "Paracetamol 500mg", "Viên", 1, 0.0, true));
        dsQuyCach.add(new QuyCachDongGoi("QCDG-002", "Paracetamol 500mg", "Vỉ", 10, 0.05, false));
        dsQuyCach.add(new QuyCachDongGoi("QCDG-003", "Paracetamol 500mg", "Hộp", 50, 0.10, false));

        String[] columnNames = {"Mã Quy Cách", "Sản Phẩm", "Đơn Vị Tính", "Hệ Số Quy Đổi", "Tỉ Lệ Giảm", "Đơn Vị Gốc"};
        model = new DefaultTableModel(columnNames, 0);

        for (QuyCachDongGoi qc : dsQuyCach) {
            model.addRow(new Object[]{
                    qc.maQCDG,
                    qc.sanPham,
                    qc.donViTinh,
                    qc.heSoQuyDoi,
                    String.format("%.0f%%", qc.tiLeGiam * 100),
                    qc.donViGoc ? "Có" : "Không"
            });
        }

        // ===== TABLE =====
        table = new JTable(model);
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
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);

        // Tô màu xen kẽ hàng
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                setBorder(new EmptyBorder(0, 8, 0, 8));
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
            JFrame frame = new JFrame("Quản lý Quy Cách Đóng Gói");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new QuyCachDongGoi_GUI ());
            frame.setVisible(true);
        });
    }
}
