/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Nov 19, 2025
 *
 * Mô tả: Giao diện tra cứu khách hàng và lịch sử giao dịch (Mua, Trả).
 * (Form chuẩn theo TraCuuNhanVien_GUI)
 */
package gui.tracuu;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

// Import các component riêng của bạn
import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;

public class TraCuuKhachHang_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Khách Hàng (Master)
    private JTable tblKhachHang;
    private DefaultTableModel modelKhachHang;

    // TabbedPane chứa các bảng chi tiết (Detail)
    private JTabbedPane tabChiTiet;
    private JTable tblLichSuMuaHang; // Đổi từ Bán -> Mua
    private DefaultTableModel modelLichSuMuaHang;
    
    private JTable tblLichSuTraHang;
    private DefaultTableModel modelLichSuTraHang;

    // Components lọc (Thay đổi cho phù hợp khách hàng)
    private JTextField txtTimKiem;
    private JComboBox<String> cbGioiTinh;    // Thay cho Chức vụ
    private JComboBox<String> cbHangThanhVien; // Thay cho Ca làm
    private JComboBox<String> cbTrangThai;

    public TraCuuKhachHang_GUI() {
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
        loadDuLieuKhachHang();
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm khách hàng theo mã, tên, sđt...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 400, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- BỘ LỌC ---
        int yFilter = 28;
        int hFilter = 38;

        // Lọc 1: Giới tính
        JLabel lblGT = new JLabel("Giới tính:");
        lblGT.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGT.setBounds(450, yFilter, 70, 35);
        pnHeader.add(lblGT);

        cbGioiTinh = new JComboBox<>(new String[]{"Tất cả", "Nam", "Nữ"});
        cbGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbGioiTinh.setBounds(520, yFilter, 100, hFilter);
        pnHeader.add(cbGioiTinh);

        // Lọc 2: Hạng thành viên (Ví dụ: Thân thiết, VIP)
        JLabel lblHang = new JLabel("Hạng:");
        lblHang.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblHang.setBounds(640, yFilter, 50, 35);
        pnHeader.add(lblHang);

        cbHangThanhVien = new JComboBox<>(new String[]{"Tất cả", "Mới", "Thân thiết", "VIP"});
        cbHangThanhVien.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbHangThanhVien.setBounds(690, yFilter, 120, hFilter);
        pnHeader.add(cbHangThanhVien);

        // Lọc 3: Trạng thái
        JLabel lblTT = new JLabel("Trạng thái:");
        lblTT.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTT.setBounds(820, yFilter, 80, 35);
        pnHeader.add(lblTT);

        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngừng"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbTrangThai.setBounds(900, yFilter, 120, hFilter);
        pnHeader.add(cbTrangThai);

        // --- NÚT ---
        PillButton btnTim = new PillButton("Tìm kiếm");
        btnTim.setBounds(1050, 22, 120, 50);
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTim);

        PillButton btnMoi = new PillButton("Làm mới");
        btnMoi.setBounds(1190, 22, 120, 50);
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
        splitPane.setDividerLocation(400); // Chia tỉ lệ giống bên Nhân viên
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);

        // --- TOP: BẢNG KHÁCH HÀNG ---
        // Cột dữ liệu phù hợp với Khách Hàng
        String[] colKH = {"STT", "Mã KH", "Tên khách hàng", "SĐT", "Ngày sinh", "Giới tính", "Điểm tích lũy", "Hạng", "Trạng thái"};
        modelKhachHang = new DefaultTableModel(colKH, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKhachHang = setupTable(modelKhachHang);
        
        // Render căn lề
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i=0; i<tblKhachHang.getColumnCount(); i++) {
            if (i != 2) tblKhachHang.getColumnModel().getColumn(i).setCellRenderer(center); // Tên canh trái
        }
        tblKhachHang.getColumnModel().getColumn(2).setPreferredWidth(200); 

        // Render Hạng thành viên (Đậm)
        tblKhachHang.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("VIP".equals(value)) {
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lbl.setForeground(new Color(255, 140, 0)); // Màu cam cho VIP
                } else {
                    lbl.setForeground(Color.BLACK);
                }
                return lbl;
            }
        });
        
        // Render Trạng thái
        tblKhachHang.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Hoạt động".equals(value)) lbl.setForeground(new Color(0x2E7D32)); // Xanh lá
                else lbl.setForeground(Color.RED);
                return lbl;
            }
        });

        JScrollPane scrollKH = new JScrollPane(tblKhachHang);
        scrollKH.setBorder(createTitledBorder("Danh sách khách hàng"));
        splitPane.setTopComponent(scrollKH);

        // --- BOTTOM: TABBED PANE (LỊCH SỬ) ---
        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // Tab 1: Lịch sử Mua Hàng
        tabChiTiet.addTab("Lịch sử mua hàng", createTabMuaHang());
        
        // Tab 2: Lịch sử Trả Hàng
        tabChiTiet.addTab("Lịch sử trả hàng", createTabTraHang());

        splitPane.setBottomComponent(tabChiTiet);
    }

    // Tạo Panel cho Tab Mua Hàng (Khác với bán hàng là hiển thị Nhân viên bán)
    private JComponent createTabMuaHang() {
        String[] cols = {"STT", "Mã hóa đơn", "Ngày mua", "Nhân viên bán", "Tổng tiền", "Điểm cộng"};
        modelLichSuMuaHang = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSuMuaHang = setupTable(modelLichSuMuaHang);
        setupTableAlign(tblLichSuMuaHang);
        return new JScrollPane(tblLichSuMuaHang);
    }

    // Tạo Panel cho Tab Trả Hàng
    private JComponent createTabTraHang() {
        String[] cols = {"STT", "Mã đơn trả", "Ngày trả", "Lý do trả", "Tiền hoàn lại", "Điểm trừ"};
        modelLichSuTraHang = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSuTraHang = setupTable(modelLichSuTraHang);
        setupTableAlign(tblLichSuTraHang);
        return new JScrollPane(tblLichSuTraHang);
    }

    // Setup chung cho table (Giữ nguyên style)
    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(0xC8E6C9)); // Màu xanh nhạt khi chọn
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243)); // Màu xanh header
        header.setForeground(Color.WHITE);
        return table;
    }
    
    // Setup căn lề
    private void setupTableAlign(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        int lastCol = table.getColumnCount() - 1;
        for (int i=0; i<lastCol; i++) {
            if (i!=3) // Cột tên/lý do có thể để left, ở đây để center
                table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        // 2 cột cuối thường là tiền/điểm
        table.getColumnModel().getColumn(lastCol).setCellRenderer(right); 
        if (lastCol > 0) table.getColumnModel().getColumn(lastCol-1).setCellRenderer(right);
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
        // Sự kiện click vào khách hàng -> Load dữ liệu tab bên dưới
        tblKhachHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblKhachHang.getSelectedRow();
                if (row >= 0) {
                    String maKH = tblKhachHang.getValueAt(row, 1).toString();
                    loadLichSuGiaoDich(maKH);
                }
            }
        });
    }

    private void loadDuLieuKhachHang() {
        // Dữ liệu fake cho Khách Hàng
        Object[][] data = {
            {"1", "KH-20251104-0001", "Nguyễn Thị Lan", "0912345678", "01/01/1990", "Nữ", "150", "Thân thiết", "Hoạt động"},
            {"2", "KH-20251104-0002", "Trần Văn Tùng", "0988777666", "20/05/1985", "Nam", "1200", "VIP", "Hoạt động"},
            {"3", "KH-20251104-0003", "Lê Thanh H", "0909090909", "15/08/2000", "Nữ", "10", "Mới", "Ngừng"},
        };
        
        for (Object[] row : data) {
            modelKhachHang.addRow(row);
        }
    }

    private void loadLichSuGiaoDich(String maKH) {
        // Xóa dữ liệu cũ
        modelLichSuMuaHang.setRowCount(0);
        modelLichSuTraHang.setRowCount(0);

        // Giả lập dữ liệu dựa trên Mã KH
        if (maKH.equals("KH-20251104-0001")) { // Nguyễn Thị Lan
            modelLichSuMuaHang.addRow(new Object[]{"1", "HD001", "10/11/2025", "Trần Thu Hà", "500,000 đ", "+50"});
            modelLichSuMuaHang.addRow(new Object[]{"2", "HD009", "01/11/2025", "Lê Văn C", "200,000 đ", "+20"});
        } 
        else if (maKH.equals("KH-20251104-0002")) { // Trần Văn Tùng (VIP)
            modelLichSuMuaHang.addRow(new Object[]{"1", "HD005", "15/11/2025", "Trần Thu Hà", "2,000,000 đ", "+200"});
            
            modelLichSuTraHang.addRow(new Object[]{"1", "DT001", "16/11/2025", "Thuốc bị vỡ hộp", "200,000 đ", "-20"});
        }
    }
}