/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Giao diện tra cứu đơn hàng (Layout đồng bộ với TraCuuSanPham).
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

public class TraCuuDonHang_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Hóa Đơn (Trên)
    private JTable tblHoaDon;
    private DefaultTableModel modelHoaDon;

    // Bảng Chi Tiết Hóa Đơn (Dưới)
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // Các component lọc
    private JTextField txtTimKiem;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;

    public TraCuuDonHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        // 1. LAYOUT CHÍNH
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 2. HEADER (Vùng Bắc)
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 3. CENTER (Vùng Giữa - Chứa 2 bảng)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 4. DATA & EVENTS
        loadDuLieuHoaDon();
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

        // --- 1. Ô TÌM KIẾM TO (Bên trái) ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã hóa đơn, SĐT khách hàng");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22)); // Font to đồng bộ
        txtTimKiem.setBounds(25, 17, 450, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- 2. BỘ LỌC NGÀY (Ở giữa) ---
        // Từ ngày
        JLabel lblTu = new JLabel("Từ ngày:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTu.setBounds(500, 28, 70, 35);
        pnHeader.add(lblTu);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateTuNgay.setBounds(570, 28, 140, 38);
        dateTuNgay.setDate(new Date()); // Mặc định hôm nay
        pnHeader.add(dateTuNgay);

        // Đến ngày
        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDen.setBounds(730, 28, 40, 35);
        pnHeader.add(lblDen);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateDenNgay.setBounds(770, 28, 140, 38);
        
        // Mặc định ngày mai
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        dateDenNgay.setDate(cal.getTime());
        pnHeader.add(dateDenNgay);

        // --- 3. CÁC NÚT CHỨC NĂNG (Bên phải) ---
        PillButton btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1120, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTimKiem);

        PillButton btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1265, 22, 130, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnLamMoi);

        // Sự kiện nút Lọc/Tìm kiếm
        btnTimKiem.addActionListener(e -> {
            System.out.println("Tìm kiếm đơn hàng...");
        });
    }

    // ==============================================================================
    //                              PHẦN CENTER
    // ==============================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tạo SplitPane chia đôi trên dưới
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        pnCenter.add(splitPane, BorderLayout.CENTER);

        // --- BẢNG 1: DANH SÁCH HÓA ĐƠN (TOP) ---
        String[] colHoaDon = {"STT", "Mã hóa đơn", "Khách hàng", "Nhân viên", "Ngày lập", "Tổng tiền"};
        modelHoaDon = new DefaultTableModel(colHoaDon, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblHoaDon = setupTable(modelHoaDon);
        
        // Căn lề bảng Hóa Đơn
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        tblHoaDon.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblHoaDon.getColumnModel().getColumn(1).setCellRenderer(center); // Mã
        tblHoaDon.getColumnModel().getColumn(4).setCellRenderer(center); // Ngày
        tblHoaDon.getColumnModel().getColumn(5).setCellRenderer(right);  // Tiền

        // Độ rộng cột
        tblHoaDon.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblHoaDon.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblHoaDon.getColumnModel().getColumn(2).setPreferredWidth(250);
        
        JScrollPane scrollHD = new JScrollPane(tblHoaDon);
        scrollHD.setBorder(createTitledBorder("Danh sách hóa đơn"));
        splitPane.setTopComponent(scrollHD);

        // --- BẢNG 2: CHI TIẾT HÓA ĐƠN (BOTTOM) ---
        String[] colChiTiet = {"STT", "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblChiTiet = setupTable(modelChiTiet);
        
        // Căn lề bảng Chi Tiết
        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(center); // Mã SP
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(center); // Đơn vị
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(center); // SL
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(right);  // Đơn giá
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(right);  // Thành tiền

        // Độ rộng cột
        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(300);

        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        scrollChiTiet.setBorder(createTitledBorder("Chi tiết đơn hàng"));
        splitPane.setBottomComponent(scrollChiTiet);
    }

    // Hàm setup Table chung (Style giống TraCuuSanPham)
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

    // Hàm tạo border tiêu đề chung
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
        // Sự kiện click vào hóa đơn -> Load chi tiết
        tblHoaDon.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblHoaDon.getSelectedRow();
                if (row >= 0) {
                    String maHD = tblHoaDon.getValueAt(row, 1).toString();
                    loadChiTietCuaHoaDon(maHD);
                }
            }
        });
    }

    private void loadDuLieuHoaDon() {
        // Dữ liệu giả lập
        Object[][] data = {
            {"1", "HD-20251019-0001", "Nguyễn Văn A", "Trần Thu Hà", "19/10/2025", "150,000 đ"},
            {"2", "HD-20251018-0005", "Trần Thị B", "Lê Văn C", "18/10/2025", "320,000 đ"},
            {"3", "HD-20251017-0012", "Phạm Quốc Khánh", "Nguyễn Thị D", "17/10/2025", "5,500,000 đ"},
            {"4", "HD-20251017-0003", "Chu Anh Khôi", "Trần Thu Hà", "17/10/2025", "120,000 đ"},
            {"5", "HD-20251016-0008", "Lê Thanh Kha", "Lê Văn C", "16/10/2025", "210,000 đ"},
        };
        for (Object[] row : data) {
            modelHoaDon.addRow(row);
        }
    }

    private void loadChiTietCuaHoaDon(String maHD) {
        modelChiTiet.setRowCount(0);
        
        // Giả lập dữ liệu chi tiết dựa trên mã hóa đơn
        if (maHD.equals("HD-20251019-0001")) {
            modelChiTiet.addRow(new Object[]{"1", "SP001", "Paracetamol 500mg", "Vỉ", "2", "5,000 đ", "10,000 đ"});
            modelChiTiet.addRow(new Object[]{"2", "SP005", "Vitamin C", "Hộp", "1", "140,000 đ", "140,000 đ"});
        } else if (maHD.equals("HD-20251017-0012")) {
            modelChiTiet.addRow(new Object[]{"1", "SP003", "Thực phẩm chức năng A", "Hộp", "5", "1,000,000 đ", "5,000,000 đ"});
            modelChiTiet.addRow(new Object[]{"2", "SP004", "Khẩu trang y tế", "Hộp", "10", "50,000 đ", "500,000 đ"});
        } else {
            // Mặc định
            modelChiTiet.addRow(new Object[]{"1", "SP999", "Sản phẩm mẫu", "Cái", "1", "100,000 đ", "100,000 đ"});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            JFrame frame = new JFrame("Tra cứu đơn hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuDonHang_GUI());
            frame.setVisible(true);
        });
    }
}