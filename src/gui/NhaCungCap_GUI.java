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
        dsNhaCungCap.add(new NhaCungCap("NCC-021", "Công Ty TNHH Dược Phẩm Trung Ương CPC1", "0901234561", "356 Nguyễn Trãi, Thanh Xuân, Hà Nội"));
        dsNhaCungCap.add(new NhaCungCap("NCC-022", "Công Ty CP Dược Phẩm OPC", "0912345672", "1010 Nguyễn Văn Linh, Quận 7, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-023", "Công Ty TNHH Dược Phẩm Eco Pharma", "0923456783", "27 Trường Chinh, Đà Nẵng"));
        dsNhaCungCap.add(new NhaCungCap("NCC-024", "Công Ty CP Dược Phẩm Hà Tây", "0934567894", "15 Nguyễn Du, Hà Đông, Hà Nội"));
        dsNhaCungCap.add(new NhaCungCap("NCC-025", "Công Ty CP Dược Hậu Giang", "0945678905", "288 Bis Nguyễn Văn Cừ, Quận Ninh Kiều, Cần Thơ"));
        dsNhaCungCap.add(new NhaCungCap("NCC-026", "Công Ty CP Pymepharco", "0956789016", "166-170 Nguyễn Huệ, TP. Tuy Hòa, Phú Yên"));
        dsNhaCungCap.add(new NhaCungCap("NCC-027", "Công Ty TNHH United Pharma Việt Nam", "0967890127", "KCN VSIP, Bình Dương"));
        dsNhaCungCap.add(new NhaCungCap("NCC-028", "Công Ty TNHH Sanofi Việt Nam", "0978901238", "KCN Sài Đồng, Long Biên, Hà Nội"));
        dsNhaCungCap.add(new NhaCungCap("NCC-029", "Công Ty TNHH Dược Phẩm Traphaco", "0989012349", "75 Yên Ninh, Ba Đình, Hà Nội"));
        dsNhaCungCap.add(new NhaCungCap("NCC-030", "Công Ty CP Dược Phẩm Imexpharm", "0990123450", "KCN Việt Nam – Singapore, Bình Dương"));
        dsNhaCungCap.add(new NhaCungCap("NCC-031", "Công Ty CP Dược Danapha", "0902123456", "KCN Liên Chiểu, Đà Nẵng"));
        dsNhaCungCap.add(new NhaCungCap("NCC-032", "Công Ty CP Dược Phẩm Vimedimex", "0913234567", "46 Tô Hiến Thành, Hai Bà Trưng, Hà Nội"));
        dsNhaCungCap.add(new NhaCungCap("NCC-033", "Công Ty CP Dược Phẩm Mekophar", "0924345678", "297 Trần Hưng Đạo, Quận 1, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-034", "Công Ty TNHH Trang Thiết Bị Y Tế Hoàng Gia", "0935456789", "45 Trần Hưng Đạo, Quận 5, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-035", "Công Ty TNHH Dược Liệu Đông Y An Thịnh", "0946567890", "Tân Bình, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-036", "Công Ty TNHH Dược Mỹ Phẩm Lotus", "0957678901", "Quận 3, TP.HCM"));
        dsNhaCungCap.add(new NhaCungCap("NCC-037", "Công Ty CP Dược Phẩm Bidiphar", "0968789012", "498 Nguyễn Thái Học, Quy Nhơn, Bình Định"));
        dsNhaCungCap.add(new NhaCungCap("NCC-038", "Công Ty TNHH Dược Phẩm Thành Công", "0979890123", "Đà Lạt, Lâm Đồng"));
        dsNhaCungCap.add(new NhaCungCap("NCC-039", "Công Ty TNHH Vật Tư Y Tế Kim Long", "0980901234", "Huế"));
        dsNhaCungCap.add(new NhaCungCap("NCC-040", "Công Ty TNHH Dược Phẩm Minh Châu", "0991012345", "Long An"));



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
        
        // Kiểm tra xem người dùng đã xóa hết chữ chưa
        // Hoặc kiểm tra xem ô tìm kiếm có đang hiển thị placeholder không (nếu có)
        if (text.trim().isEmpty() || txtTimKiem.getForeground().equals(Color.GRAY)) {
            sorter.setRowFilter(null);
        } else {
            // Tạo một danh sách các bộ lọc
            List<RowFilter<Object, Object>> filters = new ArrayList<>();
            
            // Thêm "^" để chỉ tìm kiếm những dòng BẮT ĐẦU BẰNG chuỗi `text`
            // Lọc trên cột Tên NCC (index 1) - (?i) để không phân biệt hoa thường
            filters.add(RowFilter.regexFilter("(?i)^" + text, 1));
            // Lọc trên cột SĐT (index 2)
            filters.add(RowFilter.regexFilter("(?i)^" + text, 2));
            
            // Áp dụng bộ lọc "OR", hàng nào khớp với 1 trong các điều kiện sẽ được hiển thị
            sorter.setRowFilter(RowFilter.orFilter(filters));
        }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý nhà cung cấp");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhaCungCap_GUI());
            frame.setVisible(true);
        });
    }
}