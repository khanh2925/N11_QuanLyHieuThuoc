/**
 * @author Quốc Khánh
 * @version 1.1
 * @since Nov 20, 2025
 *
 * Mô tả: Giao diện tra cứu sản phẩm (Master) kèm Lô hàng & Quy cách (Detail tabs).
 */
package gui;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

@SuppressWarnings("serial")
public class TraCuuSanPham_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Master
    private JTable tblSanPham;
    private DefaultTableModel modelSanPham;
    
    // Tabs Detail
    private JTabbedPane tabChiTiet;
    
    // Bảng Detail 1: Lô
    private JTable tblLoSanPham;
    private DefaultTableModel modelLoSanPham;
    
    // Bảng Detail 2: Quy Cách
    private JTable tblQuyCach;
    private DefaultTableModel modelQuyCach;

    private final DecimalFormat df = new DecimalFormat("#,### đ");

    public TraCuuSanPham_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);
        
        // 3. DATA
        loadDuLieuSanPham();
        addEvents();
    }

    // ==============================================================================
    //                              PHẦN HEADER (Giữ nguyên)
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null); 
        pnHeader.setPreferredSize(new Dimension(1073, 94)); 
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- Ô TÌM KIẾM ---
        JTextField txtTimThuoc = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimThuoc, "Nhập tên thuốc, mã sản phẩm hoặc số đăng ký...");
        txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimThuoc.setBounds(25, 17, 480, 60);
        txtTimThuoc.setBorder(new RoundedBorder(20));
        txtTimThuoc.setBackground(Color.WHITE);
        txtTimThuoc.setForeground(Color.GRAY);
        pnHeader.add(txtTimThuoc);

        // --- BỘ LỌC ---
        // 1. Loại
        JLabel lblLoai = new JLabel("Loại:");
        lblLoai.setBounds(530, 28, 50, 35);
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblLoai);

        JComboBox<String> cbLoai = new JComboBox<>(new String[]{"Tất cả", "Thuốc", "TPCN", "Dụng cụ y tế"});
        cbLoai.setBounds(580, 28, 140, 38);
        cbLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbLoai);

        // 2. Kệ
        JLabel lblKe = new JLabel("Kệ:");
        lblKe.setBounds(735, 28, 40, 35);
        lblKe.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblKe);

        JComboBox<String> cbKe = new JComboBox<>(new String[]{"Tất cả", "A1", "A2", "B1", "B2", "C1", "D1"});
        cbKe.setBounds(770, 28, 100, 38);
        cbKe.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbKe);

        // 3. Trạng thái
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setBounds(885, 28, 80, 35);
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblTrangThai);

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang bán", "Ngừng bán"});
        cbTrangThai.setBounds(970, 28, 130, 38);
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbTrangThai);

        // --- NÚT ---
        PillButton btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1120, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTimKiem);

        PillButton btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnLamMoi);
    }

    // ==============================================================================
    //                              PHẦN CENTER (Master - Detail Tabs)
    // ==============================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // SplitPane: Trên là SP, Dưới là Tabs
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        // --- TOP: BẢNG SẢN PHẨM ---
        String[] colSanPham = {
            "Mã SP", "Tên sản phẩm", "Loại", "Số ĐK", "Hoạt chất", "Nước SX",
            "Giá vốn", "Kệ", "Trạng thái"
        };
        modelSanPham = new DefaultTableModel(colSanPham, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblSanPham = setupTable(modelSanPham);
        
        // Canh lề
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        // Cột 1 (Tên) để mặc định (Trái), Giá (6) phải, còn lại giữa
        for(int i=0; i<tblSanPham.getColumnCount(); i++) {
            if(i!=1 && i!=4) tblSanPham.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        tblSanPham.getColumnModel().getColumn(6).setCellRenderer(right); // Giá vốn
        
        // Render Trạng thái (Màu xanh/đỏ)
        tblSanPham.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if("Đang bán".equals(value)) lbl.setForeground(new Color(0x2E7D32));
                else lbl.setForeground(Color.RED);
                return lbl;
            }
        });

        JScrollPane scrollSP = new JScrollPane(tblSanPham);
        scrollSP.setBorder(createTitledBorder("Danh sách sản phẩm"));
        splitPane.setTopComponent(scrollSP);

        // --- BOTTOM: TABBED PANE ---
        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Tab 1: Danh sách Lô
        tabChiTiet.addTab("Danh sách lô hàng", createTabLoHang());
        
        // Tab 2: Quy cách đóng gói (Mới thêm)
        tabChiTiet.addTab("Quy cách đóng gói", createTabQuyCach());

        splitPane.setBottomComponent(tabChiTiet);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // Tạo Panel cho Tab Lô Hàng
    private JComponent createTabLoHang() {
        String[] colLo = {"STT", "Mã lô", "Hạn sử dụng", "Số lượng tồn", "Ngày nhập"};
        modelLoSanPham = new DefaultTableModel(colLo, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLoSanPham = setupTable(modelLoSanPham);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblLoSanPham.getColumnCount(); i++) {
            tblLoSanPham.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        
        return new JScrollPane(tblLoSanPham);
    }

    // Tạo Panel cho Tab Quy Cách (Theo entity QuyCachDongGoi)
    private JComponent createTabQuyCach() {
        // Cột: Mã QC, Đơn vị, Quy đổi, Giá bán (đã tính tỉ lệ), Tỉ lệ giảm, Là gốc?
        String[] colQC = {"STT", "Mã quy cách", "Đơn vị tính", "Quy đổi", "Giá bán", "Tỉ lệ giảm giá", "Loại đơn vị"};
        modelQuyCach = new DefaultTableModel(colQC, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblQuyCach = setupTable(modelQuyCach);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Canh chỉnh
        tblQuyCach.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblQuyCach.getColumnModel().getColumn(1).setCellRenderer(center); // Mã
        tblQuyCach.getColumnModel().getColumn(2).setCellRenderer(center); // Đơn vị
        tblQuyCach.getColumnModel().getColumn(3).setCellRenderer(center); // Quy đổi
        tblQuyCach.getColumnModel().getColumn(4).setCellRenderer(right);  // Giá bán
        tblQuyCach.getColumnModel().getColumn(5).setCellRenderer(center); // Tỉ lệ
        
        // Render Đơn vị gốc (In đậm, màu xanh)
        tblQuyCach.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Đơn vị gốc".equals(value)) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lbl.setForeground(new Color(0, 102, 204));
                } else {
                    lbl.setForeground(Color.GRAY);
                }
                return lbl;
            }
        });
        
        return new JScrollPane(tblQuyCach);
    }

    // Setup chung cho table
    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY
        );
    }

    // ==============================================================================
    //                              DỮ LIỆU & SỰ KIỆN
    // ==============================================================================
    private void addEvents() {
        // Sự kiện click vào bảng Sản Phẩm -> Load dữ liệu cho 2 tab bên dưới
        tblSanPham.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblSanPham.getSelectedRow();
                if (row >= 0) {
                    String maSP = tblSanPham.getValueAt(row, 0).toString();
                    double giaVon = parseMoney(tblSanPham.getValueAt(row, 6).toString()); // Lấy giá vốn để tính giá bán QC
                    loadChiTietSanPham(maSP, giaVon);
                }
            }
        });
    }

    private void loadDuLieuSanPham() {
        // Data giả lập
        Object[][] data = {
            {"SP001", "Paracetamol 500mg", "Thuốc", "VD-12345", "Paracetamol", "Việt Nam", "2,000 đ", "Kệ A1", "Đang bán"},
            {"SP002", "Vitamin C 1000mg", "TPCN", "VD-67890", "Ascorbic Acid", "Mỹ", "6,000 đ", "Kệ B2", "Đang bán"},
            {"SP003", "Băng cá nhân", "Dụng cụ", "VD-11111", "Vải, Keo", "Việt Nam", "10,000 đ", "Kệ C3", "Ngừng bán"},
            {"SP004", "Panadol Extra", "Thuốc", "VD-22222", "Para + Caffeine", "Úc", "12,000 đ", "Kệ A2", "Đang bán"}
        };
        for (Object[] row : data) modelSanPham.addRow(row);
    }

    private void loadChiTietSanPham(String maSP, double giaVon) {
        modelLoSanPham.setRowCount(0);
        modelQuyCach.setRowCount(0);

        // --- DATA GIẢ LẬP CHO TỪNG SẢN PHẨM ---
        
        if (maSP.equals("SP001")) { // Paracetamol
            // 1. Load Lô
            modelLoSanPham.addRow(new Object[]{"1", "L001", "15/12/2026", "150", "01/01/2024"});
            modelLoSanPham.addRow(new Object[]{"2", "L002", "20/11/2026", "200", "05/02/2024"});

            // 2. Load Quy Cách (Entity logic: Giá bán = Giá vốn * Quy đổi * (1 - Tỉ lệ giảm))
            // Đơn vị gốc: Viên (x1)
            modelQuyCach.addRow(new Object[]{"1", "QC-000001", "Viên", "1", df.format(giaVon * 1.2), "0%", "Đơn vị gốc"}); // Lãi 20%
            // Quy cách 2: Vỉ (x10)
            modelQuyCach.addRow(new Object[]{"2", "QC-000002", "Vỉ", "10", df.format((giaVon * 10 * 1.2) * 0.95), "5%", "Quy đổi"}); // Giảm 5%
            // Quy cách 3: Hộp (x100)
            modelQuyCach.addRow(new Object[]{"3", "QC-000003", "Hộp", "100", df.format((giaVon * 100 * 1.2) * 0.9), "10%", "Quy đổi"}); // Giảm 10%
        } 
        else if (maSP.equals("SP002")) { // Vitamin C
            modelLoSanPham.addRow(new Object[]{"1", "L003", "10/03/2027", "300", "10/10/2024"});
            
            modelQuyCach.addRow(new Object[]{"1", "QC-000004", "Viên", "1", df.format(giaVon * 1.3), "0%", "Đơn vị gốc"});
            modelQuyCach.addRow(new Object[]{"2", "QC-000005", "Lọ", "30", df.format((giaVon * 30 * 1.3) * 0.92), "8%", "Quy đổi"});
        }
        else if (maSP.equals("SP003")) { // Băng cá nhân
            modelLoSanPham.addRow(new Object[]{"1", "L004", "05/05/2026", "80", "12/12/2023"});
            
            modelQuyCach.addRow(new Object[]{"1", "QC-000006", "Hộp", "1", df.format(giaVon * 1.1), "0%", "Đơn vị gốc"});
            modelQuyCach.addRow(new Object[]{"2", "QC-000007", "Thùng", "50", df.format((giaVon * 50 * 1.1) * 0.95), "5%", "Quy đổi"});
        }
    }
    
    private double parseMoney(String money) {
        try {
            return Double.parseDouble(money.replaceAll("[^\\d]", ""));
        } catch (Exception e) { return 0; }
    }

    // ===== MAIN =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Tra cứu sản phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1450, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuSanPham_GUI());
            frame.setVisible(true);
        });
    }
}