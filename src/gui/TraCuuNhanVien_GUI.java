/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Giao diện tra cứu nhân viên và lịch sử hoạt động (Bán hàng, Trả, Hủy).
 */
package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

public class TraCuuNhanVien_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Nhân Viên (Master)
    private JTable tblNhanVien;
    private DefaultTableModel modelNhanVien;

    // TabbedPane chứa các bảng chi tiết (Detail)
    private JTabbedPane tabChiTiet;
    private JTable tblLichSuBanHang;
    private DefaultTableModel modelLichSuBanHang;
    
    private JTable tblLichSuTraHang;
    private DefaultTableModel modelLichSuTraHang;
    
    private JTable tblLichSuHuyHang;
    private DefaultTableModel modelLichSuHuyHang;

    // Components lọc
    private JTextField txtTimKiem;
    private JComboBox<String> cbChucVu;
    private JComboBox<String> cbCaLam;
    private JComboBox<String> cbTrangThai;

    public TraCuuNhanVien_GUI() {
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
        loadDuLieuNhanVien();
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã, tên, số điện thoại...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 400, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- BỘ LỌC ---
        int yFilter = 28;
        int hFilter = 38;

        // Chức vụ
        JLabel lblCV = new JLabel("Chức vụ:");
        lblCV.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCV.setBounds(450, yFilter, 70, 35);
        pnHeader.add(lblCV);

        cbChucVu = new JComboBox<>(new String[]{"Tất cả", "Quản lý", "Nhân viên"});
        cbChucVu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbChucVu.setBounds(520, yFilter, 120, hFilter);
        pnHeader.add(cbChucVu);

        // Ca làm
        JLabel lblCa = new JLabel("Ca:");
        lblCa.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCa.setBounds(660, yFilter, 30, 35);
        pnHeader.add(lblCa);

        cbCaLam = new JComboBox<>(new String[]{"Tất cả", "Sáng", "Chiều", "Tối"});
        cbCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbCaLam.setBounds(690, yFilter, 100, hFilter);
        pnHeader.add(cbCaLam);

        // Trạng thái
        JLabel lblTT = new JLabel("Trạng thái:");
        lblTT.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTT.setBounds(810, yFilter, 80, 35);
        pnHeader.add(lblTT);

        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang làm", "Đã nghỉ"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbTrangThai.setBounds(890, yFilter, 120, hFilter);
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
        
        // Nút thêm nhân viên (dành cho quản lý)
        PillButton btnThem = new PillButton("Thêm NV");
        btnThem.setBounds(1330, 22, 120, 50);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnThem);
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

        // --- TOP: BẢNG NHÂN VIÊN ---
        String[] colNV = {"STT", "Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Chức vụ", "Ca làm", "Trạng thái"};
        modelNhanVien = new DefaultTableModel(colNV, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhanVien = setupTable(modelNhanVien);
        
        // Render màu sắc và căn lề
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i=0; i<tblNhanVien.getColumnCount(); i++) {
            if (i != 2) tblNhanVien.getColumnModel().getColumn(i).setCellRenderer(center); // Tên canh trái, còn lại giữa
        }
        tblNhanVien.getColumnModel().getColumn(2).setPreferredWidth(200); // Tên rộng hơn

        // Render Chức vụ (Đậm) và Trạng thái (Màu)
        tblNhanVien.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Quản lý".equals(value)) lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                return lbl;
            }
        });
        
        tblNhanVien.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Đang làm".equals(value)) lbl.setForeground(new Color(0x2E7D32));
                else lbl.setForeground(Color.GRAY);
                return lbl;
            }
        });

        JScrollPane scrollNV = new JScrollPane(tblNhanVien);
        scrollNV.setBorder(createTitledBorder("Danh sách nhân viên"));
        splitPane.setTopComponent(scrollNV);

        // --- BOTTOM: TABBED PANE (LỊCH SỬ HOẠT ĐỘNG) ---
        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        // Tab 1: Đơn hàng đã bán
        tabChiTiet.addTab("Lịch sử bán hàng", createTabBanHang());
        
        // Tab 2: Đơn trả hàng đã duyệt
        tabChiTiet.addTab("Lịch sử duyệt trả", createTabTraHang());
        
        // Tab 3: Đơn hủy hàng
        tabChiTiet.addTab("Lịch sử hủy hàng", createTabHuyHang());

        splitPane.setBottomComponent(tabChiTiet);
    }

    // Tạo Panel cho Tab Bán Hàng
    private JComponent createTabBanHang() {
        String[] cols = {"STT", "Mã hóa đơn", "Ngày lập", "Khách hàng", "Tổng tiền"};
        modelLichSuBanHang = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSuBanHang = setupTable(modelLichSuBanHang);
        setupTableAlign(tblLichSuBanHang);
        return new JScrollPane(tblLichSuBanHang);
    }

    // Tạo Panel cho Tab Trả Hàng
    private JComponent createTabTraHang() {
        String[] cols = {"STT", "Mã phiếu trả", "Ngày lập", "Khách hàng", "Tiền hoàn lại"};
        modelLichSuTraHang = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSuTraHang = setupTable(modelLichSuTraHang);
        setupTableAlign(tblLichSuTraHang);
        return new JScrollPane(tblLichSuTraHang);
    }

    // Tạo Panel cho Tab Hủy Hàng
    private JComponent createTabHuyHang() {
        String[] cols = {"STT", "Mã phiếu hủy", "Ngày lập", "Lý do / Loại phiếu", "Tổng giá trị hủy"};
        modelLichSuHuyHang = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLichSuHuyHang = setupTable(modelLichSuHuyHang);
        setupTableAlign(tblLichSuHuyHang);
        return new JScrollPane(tblLichSuHuyHang);
    }

    // Setup chung cho table
    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(0xC8E6C9));
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        return table;
    }
    
    // Setup căn lề chung cho các bảng chi tiết
    private void setupTableAlign(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Cột cuối cùng thường là tiền -> Right, còn lại Center
        int lastCol = table.getColumnCount() - 1;
        for (int i=0; i<lastCol; i++) {
            if (i!=3) // Cột tên khách hàng/lý do có thể để left, ở đây tôi để center cho đều
                table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        table.getColumnModel().getColumn(lastCol).setCellRenderer(right);
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
        // Sự kiện click vào nhân viên -> Load dữ liệu 3 tab bên dưới
        tblNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblNhanVien.getSelectedRow();
                if (row >= 0) {
                    String maNV = tblNhanVien.getValueAt(row, 1).toString();
                    loadLichSuHoatDong(maNV);
                }
            }
        });
    }

    private void loadDuLieuNhanVien() {
        // Dữ liệu fake khớp Entity NhanVien
        Object[][] data = {
            {"1", "NV-20251031-0001", "Trần Thu Hà", "Nữ", "15/05/1995", "0909123456", "Nhân viên", "Sáng", "Đang làm"},
            {"2", "NV-20251031-0002", "Lê Văn C", "Nam", "20/10/1998", "0912345678", "Nhân viên", "Chiều", "Đang làm"},
            {"3", "NV-20251031-9999", "Nguyễn Văn Quản Lý", "Nam", "01/01/1990", "0999888777", "Quản lý", "Tối", "Đang làm"},
            {"4", "NV-20240101-0005", "Phạm Thị D", "Nữ", "12/12/2000", "0987654321", "Nhân viên", "Sáng", "Đã nghỉ"},
        };
        
        for (Object[] row : data) {
            modelNhanVien.addRow(row);
        }
    }

    private void loadLichSuHoatDong(String maNV) {
        // Xóa cũ
        modelLichSuBanHang.setRowCount(0);
        modelLichSuTraHang.setRowCount(0);
        modelLichSuHuyHang.setRowCount(0);

        // Giả lập dữ liệu dựa trên Mã NV
        if (maNV.equals("NV-20251031-0001")) { // Trần Thu Hà
            modelLichSuBanHang.addRow(new Object[]{"1", "HD-20251019-0001", "19/10/2025", "Nguyễn Văn A", "150,000 đ"});
            modelLichSuBanHang.addRow(new Object[]{"2", "HD-20251017-0003", "17/10/2025", "Chu Anh Khôi", "120,000 đ"});
            
            modelLichSuHuyHang.addRow(new Object[]{"1", "PH-20251019-0001", "19/10/2025", "Hư hỏng khi vận chuyển", "500,000 đ"});
        } 
        else if (maNV.equals("NV-20251031-0002")) { // Lê Văn C
            modelLichSuBanHang.addRow(new Object[]{"1", "HD-20251018-0005", "18/10/2025", "Trần Thị B", "320,000 đ"});
            modelLichSuBanHang.addRow(new Object[]{"2", "HD-20251016-0008", "16/10/2025", "Lê Thanh Kha", "210,000 đ"});
            
            modelLichSuTraHang.addRow(new Object[]{"1", "PT-20251018-0005", "18/10/2025", "Trần Thị B", "120,000 đ"});
            
            modelLichSuHuyHang.addRow(new Object[]{"1", "PH-20251017-0002", "17/10/2025", "Hàng hết hạn", "150,000 đ"});
        }
        else if (maNV.equals("NV-20251031-9999")) { // Quản lý
             modelLichSuHuyHang.addRow(new Object[]{"1", "PH-20251018-0005", "18/10/2025", "Từ Trả hàng (PT-001)", "40,000 đ"});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            JFrame frame = new JFrame("Tra cứu nhân viên");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuNhanVien_GUI());
            frame.setVisible(true);
        });
    }
}