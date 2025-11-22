/**
 * @author Quốc Khánh
 * @version 1.0
 * @since Nov 20, 2025
 *
 * Mô tả: Giao diện tra cứu Khuyến Mãi (Master) kèm Sản phẩm áp dụng & Lịch sử đơn hàng (Detail).
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
public class TraCuuKhuyenMai_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;

    // Bảng Master: Khuyến Mãi
    private JTable tblKhuyenMai;
    private DefaultTableModel modelKhuyenMai;

    // Tabs Detail
    private JTabbedPane tabChiTiet;

    // Tab 1: Sản phẩm được áp dụng (Chi tiết KM SP)
    private JTable tblSanPhamApDung;
    private DefaultTableModel modelSanPhamApDung;

    // Tab 2: Lịch sử đơn hàng đã dùng KM này
    private JTable tblLichSuApDung;
    private DefaultTableModel modelLichSuApDung;

    private final DecimalFormat df = new DecimalFormat("#,###");

    public TraCuuKhuyenMai_GUI() {
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
        loadDuLieuKhuyenMai();
        addEvents();
    }

    // ==============================================================================
    //                              PHẦN HEADER
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- Ô TÌM KIẾM ---
        JTextField txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã KM, tên chương trình...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 450, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- BỘ LỌC ---
        // 1. Loại khuyến mãi (Hóa đơn vs Sản phẩm)
        JLabel lblLoai = new JLabel("Loại KM:");
        lblLoai.setBounds(500, 28, 70, 35);
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblLoai);

        JComboBox<String> cbLoaiKM = new JComboBox<>(new String[]{"Tất cả", "Theo hóa đơn", "Theo sản phẩm"});
        cbLoaiKM.setBounds(570, 28, 140, 38);
        cbLoaiKM.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbLoaiKM);

        // 2. Hình thức (Tiền/Phần trăm)
        JLabel lblHinhThuc = new JLabel("Hình thức:");
        lblHinhThuc.setBounds(730, 28, 80, 35);
        lblHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblHinhThuc);

        JComboBox<String> cbHinhThuc = new JComboBox<>(new String[]{"Tất cả", "Giảm tiền", "Giảm %", "Tặng quà"});
        cbHinhThuc.setBounds(810, 28, 120, 38);
        cbHinhThuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbHinhThuc);

        // 3. Trạng thái
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setBounds(950, 28, 80, 35);
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblTrangThai);

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang chạy", "Sắp chạy", "Đã kết thúc"});
        cbTrangThai.setBounds(1030, 28, 120, 38);
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbTrangThai);

        // --- NÚT ---
        PillButton btnTim = new PillButton("Tìm kiếm");
        btnTim.setBounds(1180, 22, 120, 50);
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTim);

    }

    // ==============================================================================
    //                              PHẦN CENTER
    // ==============================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        // --- TOP: BẢNG KHUYẾN MÃI (MASTER) ---
        String[] colKM = {
            "Mã KM", "Tên chương trình", "Loại KM", "Hình thức", 
            "Giá trị", "Ngày bắt đầu", "Ngày kết thúc", "SL còn", "Trạng thái"
        };
        modelKhuyenMai = new DefaultTableModel(colKM, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKhuyenMai = setupTable(modelKhuyenMai);

        // Render Màu sắc trạng thái & Loại KM
        tblKhuyenMai.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Đang áp dụng".equals(value)) {
                    lbl.setForeground(new Color(0x2E7D32)); // Xanh
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if ("Ngừng".equals(value) || "Hết hạn".equals(value)) {
                    lbl.setForeground(Color.RED);
                    lbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                } else {
                    lbl.setForeground(new Color(255, 140, 0)); // Cam (Sắp chạy)
                }
                return lbl;
            }
        });
        
        // Render Loại KM (Đậm)
        tblKhuyenMai.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Hóa đơn".equals(value)) lbl.setForeground(new Color(0, 102, 204));
                return lbl;
            }
        });

        JScrollPane scrollKM = new JScrollPane(tblKhuyenMai);
        scrollKM.setBorder(createTitledBorder("Danh sách chương trình khuyến mãi"));
        splitPane.setTopComponent(scrollKM);

        // --- BOTTOM: TABBED PANE (DETAIL) ---
        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Tab 1: Sản phẩm áp dụng (Dành cho KM Sản Phẩm)
        tabChiTiet.addTab("Sản phẩm áp dụng", createTabSanPhamApDung());

        // Tab 2: Lịch sử áp dụng (Đơn hàng đã dùng)
        tabChiTiet.addTab("Lịch sử áp dụng (Đơn hàng)", createTabLichSu());

        splitPane.setBottomComponent(tabChiTiet);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // Tab 1: Bảng Sản phẩm (ChiTietKhuyenMaiSanPham)
    private JComponent createTabSanPhamApDung() {
        String[] cols = {"STT", "Mã SP", "Tên sản phẩm", "Đơn vị tính", "Giá gốc", "Giá sau giảm"};
        modelSanPhamApDung = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPhamApDung = setupTable(modelSanPhamApDung);
        
        // Căn lề
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblSanPhamApDung.getColumnModel().getColumn(4).setCellRenderer(right);
        tblSanPhamApDung.getColumnModel().getColumn(5).setCellRenderer(right);
        
        return new JScrollPane(tblSanPhamApDung);
    }

    // Tab 2: Lịch sử đơn hàng (Hóa đơn)
    private JComponent createTabLichSu() {
        String[] cols = {"STT", "Mã Hóa Đơn", "Ngày lập", "Khách hàng", "Tổng tiền HĐ", "Số tiền được giảm"};
        modelLichSuApDung = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSuApDung = setupTable(modelLichSuApDung);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblLichSuApDung.getColumnModel().getColumn(4).setCellRenderer(right);
        tblLichSuApDung.getColumnModel().getColumn(5).setCellRenderer(right);

        return new JScrollPane(tblLichSuApDung);
    }

    // Helper: Setup Table chung
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
        
        // Center align mặc định
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY
        );
    }

    // ==============================================================================
    //                              DATA & EVENTS
    // ==============================================================================
    private void addEvents() {
        // Click vào KM -> Load chi tiết
        tblKhuyenMai.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblKhuyenMai.getSelectedRow();
                if (row >= 0) {
                    String maKM = tblKhuyenMai.getValueAt(row, 0).toString();
                    String loaiKM = tblKhuyenMai.getValueAt(row, 2).toString(); // "Hóa đơn" hoặc "Sản phẩm"
                    loadChiTietKM(maKM, loaiKM);
                }
            }
        });
    }

    private void loadDuLieuKhuyenMai() {
        // Fake data khớp với Entity KhuyenMai
        Object[][] data = {
            {"KM-20251101-001", "Mừng khai trương", "Hóa đơn", "Giảm tiền", "50,000", "01/11/2025", "30/11/2025", "90", "Đang áp dụng"},
            {"KM-20251105-002", "Sale thuốc đau đầu", "Sản phẩm", "Giảm %", "10%", "05/11/2025", "15/11/2025", "0", "Hết hạn"},
            {"KM-20251201-003", "Chào tháng 12", "Hóa đơn", "Giảm %", "5%", "01/12/2025", "31/12/2025", "100", "Sắp chạy"},
            {"KM-20251110-004", "Mua Panadol giá sốc", "Sản phẩm", "Giảm tiền", "2,000", "10/11/2025", "20/11/2025", "45", "Đang áp dụng"}
        };
        for (Object[] row : data) modelKhuyenMai.addRow(row);
    }

    private void loadChiTietKM(String maKM, String loaiKM) {
        modelSanPhamApDung.setRowCount(0);
        modelLichSuApDung.setRowCount(0);

        // 1. LOAD TAB SẢN PHẨM
        if ("Hóa đơn".equals(loaiKM)) {
            // KM Hóa đơn thì tab sản phẩm trống hoặc hiện thông báo
            modelSanPhamApDung.addRow(new Object[]{"-", "Toàn bộ cửa hàng", "Áp dụng cho tổng bill", "-", "-", "-"});
        } else {
            // KM Sản phẩm -> Load list từ ChiTietKhuyenMaiSanPham
            if (maKM.equals("KM-20251105-002")) {
                modelSanPhamApDung.addRow(new Object[]{"1", "SP001", "Paracetamol 500mg", "Vỉ", "5,000", "4,500"});
                modelSanPhamApDung.addRow(new Object[]{"2", "SP004", "Panadol Extra", "Viên", "2,000", "1,800"});
            } else if (maKM.equals("KM-20251110-004")) {
                modelSanPhamApDung.addRow(new Object[]{"1", "SP004", "Panadol Extra", "Hộp", "50,000", "48,000"});
            }
        }

        // 2. LOAD TAB LỊCH SỬ (Đơn hàng đã áp dụng)
        // Cái này thú vị nè: cho biết KM này đã được dùng ở đâu
        if (maKM.equals("KM-20251101-001")) { // Khai trương (Đang áp dụng)
            modelLichSuApDung.addRow(new Object[]{"1", "HD-20251119-005", "19/11/2025", "Nguyễn Văn A", "1,200,000", "50,000"});
            modelLichSuApDung.addRow(new Object[]{"2", "HD-20251119-008", "19/11/2025", "Trần Thị B", "800,000", "50,000"});
        } 
        else if (maKM.equals("KM-20251110-004")) { // Panadol
            modelLichSuApDung.addRow(new Object[]{"1", "HD-20251112-001", "12/11/2025", "Khách vãng lai", "150,000", "2,000"});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý khuyến mãi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1450, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuKhuyenMai_GUI());
            frame.setVisible(true);
        });
    }
}