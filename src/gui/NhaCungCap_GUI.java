package gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.List;

import customcomponent.ImagePanel;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.NhaCungCap;

public class NhaCungCap_GUI extends JPanel {

    private JPanel pnCenter;
    private JPanel pnHeader;
    private JTextField txtTimKiem;
    private JTable table;
    private JLabel lbThem;

    // === KHAI BÁO BIẾN THÀNH VIÊN ===
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    public NhaCungCap_GUI() {
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên nhà cung cấp / SĐT");
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.setBounds(20, 27, 350, 44);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);

        // ... (các thành phần khác trong header của bạn)
        ImageIcon iconSearch = new ImageIcon(getClass().getResource("/images/search.png"));

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/add.png"));
        ImagePanel btnThem = new ImagePanel(icon.getImage());
        btnThem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThem.setBounds(456, 27, 30, 30);
        pnHeader.add(btnThem);
        btnThem.setLayout(null);
        
        lbThem = new JLabel("Thêm", SwingConstants.CENTER);
        lbThem.setBounds(435, 58, 70, 19);
        pnHeader.add(lbThem);
        lbThem.setFont(new Font("Arial", Font.BOLD, 16));
        lbThem.setForeground(Color.BLACK);
        
        ImageIcon iconSua = new ImageIcon(getClass().getResource("/images/edit.png"));
        ImagePanel btnSua = new ImagePanel(iconSua.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
        btnSua.setLayout(null);
        btnSua.setBounds(577, 27, 30, 30);
        btnSua.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnHeader.add(btnSua);
        
        JLabel lblSua = new JLabel("Cập nhật", SwingConstants.CENTER);
        lblSua.setBounds(553, 55, 70, 25);
        pnHeader.add(lblSua);
        lblSua.setFont(new Font("Arial", Font.BOLD, 16));
        lblSua.setForeground(Color.BLACK);

        // ===== CENTER =====
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new LineBorder(new Color(200, 200, 200)));
        add(pnCenter, BorderLayout.CENTER);

        List<NhaCungCap> dsNhaCungCap = new ArrayList<>();
        dsNhaCungCap.add(new NhaCungCap("NCC-001", "Công Ty TNHH Thực Phẩm Sạch An Tâm", "0901112222", "123 Lê Lợi, Quận 1, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-002", "Nhà Phân Phối Nông Sản Việt", "0987654321", "45 Nguyễn Trãi, Quận 5, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-003", "Công Ty CP Nước Giải Khát ABC", "0912345678", "KCN Tân Bình, Quận Tân Phú, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-004", "Công Ty TNHH Gia Vị Toàn Cầu", "0939888777", "789 Cách Mạng Tháng Tám, Quận 3, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-005", "Trang Trại Rau Hữu Cơ Đà Lạt", "0945123789", "Đà Lạt, Lâm Đồng"));
        dsNhaCungCap.add(new NhaCungCap("NCC-006", "Vựa Hải Sản Tươi Sống Vũng Tàu", "0977456123", "Vũng Tàu, Bà Rịa - Vũng Tàu"));

        String[] columnNames = {"Mã nhà cung cấp", "Tên nhà cung cấp", "Số điện thoại", "Địa chỉ"};
        model = new DefaultTableModel(columnNames, 0);

        for (NhaCungCap ncc : dsNhaCungCap) {
            model.addRow(new Object[]{
                ncc.getMaNhaCungCap(),
                ncc.getTenNhaCungCap(),
                ncc.getSoDienThoai(),
                ncc.getDiaChi()
            });
        }

        table = new JTable(model);
        // ... (Cấu hình JTable giữ nguyên)
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

        table.getColumnModel().getColumn(0).setPreferredWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(350);

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
        
        // ===== Sắp xếp và Lọc =====
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        // ===== PHẦN THÊM SỰ KIỆN TÌM KIẾM (ĐẶT Ở ĐÂY) =====
        txtTimKiem.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applySearchFilter();
            }
        });
    }
    
    // ===== PHƯƠNG THỨC LỌC DỮ LIỆU TRÊN BẢNG (ĐẶT Ở ĐÂY) =====

    private void applySearchFilter() {
        String text = txtTimKiem.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Tạo một danh sách các bộ lọc
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            
            // Lọc trên cột Tên NCC (index 1) - (?i) để không phân biệt hoa thường
            filters.add(RowFilter.regexFilter("(?i)" + text, 1));
            // Lọc trên cột SĐT (index 2)
            filters.add(RowFilter.regexFilter("(?i)" + text, 2));
            
            // Áp dụng bộ lọc "OR", hàng nào khớp với 1 trong các điều kiện sẽ được hiển thị
            sorter.setRowFilter(RowFilter.orFilter(filters));
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Nhà Cung Cấp");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setContentPane(new NhaCungCap_GUI());
            frame.setVisible(true);
        });
    }
}