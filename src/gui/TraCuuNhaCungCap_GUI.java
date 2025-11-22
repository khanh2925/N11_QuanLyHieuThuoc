/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Nov 19, 2025
 *
 * Mô tả: Giao diện tra cứu Nhà Cung Cấp và Lịch sử Nhập hàng.
 * (Form chuẩn theo TraCuuNhanVien_GUI)
 */
package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

// Import các component riêng của bạn
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

public class TraCuuNhaCungCap_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;

    // Bảng Nhà Cung Cấp (Master)
    private JTable tblNhaCungCap;
    private DefaultTableModel modelNhaCungCap;

    // TabbedPane chứa các bảng chi tiết (Detail)
    private JTabbedPane tabChiTiet;
    
    // Tab 1: Lịch sử nhập hàng (Phiếu Nhập)
    private JTable tblLichSuNhap;
    private DefaultTableModel modelLichSuNhap;
    
    // Tab 2: Sản phẩm cung cấp (Optional: Xem NCC này bán món gì)
    private JTable tblSanPhamCungCap;
    private DefaultTableModel modelSanPhamCungCap;

    // Components lọc
    private JTextField txtTimKiem;
    private JComboBox<String> cbKhuVuc;    // Thay cho Chức vụ
    private JComboBox<String> cbTieuChi;   // Thay cho Ca làm
    private JComboBox<String> cbTrangThai;

    public TraCuuNhaCungCap_GUI() {
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
        loadDuLieuNhaCungCap();
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

        // --- Ô TÌM KIẾM TO ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm NCC theo mã, tên, sđt, email...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 400, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- BỘ LỌC ---
        int yFilter = 28;
        int hFilter = 38;

        // Lọc 1: Khu vực (Ví dụ: Hà Nội, HCM...)
        JLabel lblKhuVuc = new JLabel("Khu vực:");
        lblKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblKhuVuc.setBounds(450, yFilter, 70, 35);
        pnHeader.add(lblKhuVuc);

        cbKhuVuc = new JComboBox<>(new String[]{"Tất cả", "TP.HCM", "Hà Nội", "Đà Nẵng"});
        cbKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbKhuVuc.setBounds(520, yFilter, 100, hFilter);
        pnHeader.add(cbKhuVuc);

        // Lọc 2: Tiêu chí sắp xếp
        JLabel lblSort = new JLabel("Sắp xếp:");
        lblSort.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSort.setBounds(640, yFilter, 60, 35);
        pnHeader.add(lblSort);

        cbTieuChi = new JComboBox<>(new String[]{"Mới nhất", "Tên A-Z", "Nhập nhiều nhất"});
        cbTieuChi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbTieuChi.setBounds(710, yFilter, 120, hFilter);
        pnHeader.add(cbTieuChi);

        // Lọc 3: Trạng thái
        JLabel lblTT = new JLabel("Trạng thái:");
        lblTT.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTT.setBounds(850, yFilter, 80, 35);
        pnHeader.add(lblTT);

        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang hợp tác", "Ngừng hợp tác"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbTrangThai.setBounds(930, yFilter, 120, hFilter);
        pnHeader.add(cbTrangThai);

        // --- NÚT ---
        PillButton btnTim = new PillButton("Tìm kiếm");
        btnTim.setBounds(1080, 22, 120, 50);
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTim);

        PillButton btnMoi = new PillButton("Làm mới");
        btnMoi.setBounds(1220, 22, 120, 50);
        btnMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnMoi);

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
        pnCenter.add(splitPane, BorderLayout.CENTER);

        // --- TOP: BẢNG NHÀ CUNG CẤP ---
        String[] colNCC = {"STT", "Mã NCC", "Tên Nhà Cung Cấp", "SĐT", "Email", "Địa chỉ", "Trạng thái"};
        modelNhaCungCap = new DefaultTableModel(colNCC, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhaCungCap = setupTable(modelNhaCungCap);
        
        // Custom width cho bảng NCC
        tblNhaCungCap.getColumnModel().getColumn(0).setPreferredWidth(50);  // STT
        tblNhaCungCap.getColumnModel().getColumn(1).setPreferredWidth(150); // Mã
        tblNhaCungCap.getColumnModel().getColumn(2).setPreferredWidth(250); // Tên (Dài)
        tblNhaCungCap.getColumnModel().getColumn(5).setPreferredWidth(300); // Địa chỉ (Rất dài)

        // Render Căn lề & Màu sắc
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Căn giữa các cột ngắn
        tblNhaCungCap.getColumnModel().getColumn(0).setCellRenderer(center);
        tblNhaCungCap.getColumnModel().getColumn(1).setCellRenderer(center);
        tblNhaCungCap.getColumnModel().getColumn(3).setCellRenderer(center);

        // Render Trạng thái
        tblNhaCungCap.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Đang hợp tác".equals(value)) {
                    lbl.setForeground(new Color(0x2E7D32)); // Xanh lá đậm
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else {
                    lbl.setForeground(Color.RED);
                    lbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                }
                return lbl;
            }
        });

        JScrollPane scrollNCC = new JScrollPane(tblNhaCungCap);
        scrollNCC.setBorder(createTitledBorder("Danh sách Nhà Cung Cấp"));
        splitPane.setTopComponent(scrollNCC);

        // --- BOTTOM: TABBED PANE (LỊCH SỬ & SP) ---
        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Tab 1: Lịch sử Nhập Hàng
        tabChiTiet.addTab("Lịch sử nhập hàng", createTabLichSuNhap());

        // Tab 2: Sản phẩm cung cấp
        tabChiTiet.addTab("Sản phẩm cung cấp", createTabSanPham());

        splitPane.setBottomComponent(tabChiTiet);
    }

    // Tạo Panel cho Tab Lịch Sử Nhập
    private JComponent createTabLichSuNhap() {
        String[] cols = {"STT", "Mã Phiếu Nhập", "Ngày nhập", "Nhân viên phụ trách", "Tổng tiền nhập", "Ghi chú"};
        modelLichSuNhap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSuNhap = setupTable(modelLichSuNhap);
        setupTableAlign(tblLichSuNhap); // Căn tiền sang phải
        return new JScrollPane(tblLichSuNhap);
    }

    // Tạo Panel cho Tab Sản Phẩm (Để biết NCC này bán cái gì)
    private JComponent createTabSanPham() {
        String[] cols = {"STT", "Mã Thuốc", "Tên Thuốc", "Đơn vị tính", "Giá nhập gần nhất", "Xuất xứ"};
        modelSanPhamCungCap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPhamCungCap = setupTable(modelSanPhamCungCap);
        setupTableAlign(tblSanPhamCungCap);
        return new JScrollPane(tblSanPhamCungCap);
    }

    // Setup chung cho table
    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(0xC8E6C9)); // Màu xanh nhạt khi chọn
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        return table;
    }

    // Setup căn lề (Tiền số bên phải, Text bên trái/giữa)
    private void setupTableAlign(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Cột STT và Mã luôn giữa
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);

        // Cột áp chót và cuối thường là Tiền -> Phải
        int lastCol = table.getColumnCount() - 1;
        table.getColumnModel().getColumn(lastCol-1).setCellRenderer(right); 
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
        // Click vào NCC -> Load phiếu nhập
        tblNhaCungCap.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblNhaCungCap.getSelectedRow();
                if (row >= 0) {
                    String maNCC = tblNhaCungCap.getValueAt(row, 1).toString();
                    loadChiTietNCC(maNCC);
                }
            }
        });
    }

    private void loadDuLieuNhaCungCap() {
        // Dữ liệu Fake chuẩn Entity NhaCungCap
        Object[][] data = {
            {"1", "NCC-20251101-001", "Công Ty Dược Phẩm A", "0901234567", "contact@duocphama.com", "123 QL1A, Q.Bình Tân, TP.HCM", "Đang hợp tác"},
            {"2", "NCC-20251101-002", "Vimedimex Group", "0283838383", "sales@vimedimex.vn", "246 Cống Quỳnh, Q.1, TP.HCM", "Đang hợp tác"},
            {"3", "NCC-20240505-999", "Công Ty TNHH Hoàng Long", "0999888777", "hlong@gmail.com", "Hà Nội", "Ngừng hợp tác"},
        };
        
        for (Object[] row : data) {
            modelNhaCungCap.addRow(row);
        }
    }

    private void loadChiTietNCC(String maNCC) {
        modelLichSuNhap.setRowCount(0);
        modelSanPhamCungCap.setRowCount(0);

        // Giả lập dữ liệu theo Mã NCC
        if (maNCC.equals("NCC-20251101-001")) { // Cty Dược A
            // Tab 1: Phiếu Nhập
            modelLichSuNhap.addRow(new Object[]{"1", "PN-20251119-001", "19/11/2025", "Trần Thu Hà", "50,000,000 đ", "Nhập lô thuốc ho"});
            modelLichSuNhap.addRow(new Object[]{"2", "PN-20251001-005", "01/10/2025", "Nguyễn Quản Lý", "120,000,000 đ", "Nhập hàng quý 4"});
            
            // Tab 2: Sản phẩm hay cung cấp
            modelSanPhamCungCap.addRow(new Object[]{"1", "T001", "Panadol Extra", "Hộp", "180,000 đ", "Việt Nam"});
            modelSanPhamCungCap.addRow(new Object[]{"2", "T005", "Siro Prospan", "Chai", "150,000 đ", "Đức"});
        } 
        else if (maNCC.equals("NCC-20251101-002")) { // Vimedimex
            modelLichSuNhap.addRow(new Object[]{"1", "PN-20251115-002", "15/11/2025", "Lê Văn C", "30,000,000 đ", "Nhập bổ sung Vitamin"});
            
            modelSanPhamCungCap.addRow(new Object[]{"1", "T009", "Vitamin C 500mg", "Hộp", "45,000 đ", "Mỹ"});
        }
    }
}