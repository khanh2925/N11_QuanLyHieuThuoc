/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Giao diện tra cứu đơn trả hàng (Layout đồng bộ, Data chuẩn Entity).
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

public class TraCuuDonTraHang_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Phiếu Trả (Trên)
    private JTable tblPhieuTra;
    private DefaultTableModel modelPhieuTra;

    // Bảng Chi Tiết Phiếu Trả (Dưới)
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // Các component lọc
    private JTextField txtTimKiem;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;
    private JComboBox<String> cbTrangThai;

    public TraCuuDonTraHang_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        // 1. LAYOUT CHÍNH
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 2. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 3. CENTER (2 Bảng)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 4. DATA & EVENTS
        loadDuLieuPhieuTra();
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã phiếu trả, tên khách hàng...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 400, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- 2. BỘ LỌC (Ở giữa) ---
        // Từ ngày
        JLabel lblTu = new JLabel("Từ ngày:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTu.setBounds(440, 28, 70, 35);
        pnHeader.add(lblTu);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateTuNgay.setBounds(510, 28, 130, 38);
        dateTuNgay.setDate(new Date()); 
        pnHeader.add(dateTuNgay);

        // Đến ngày
        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDen.setBounds(650, 28, 40, 35);
        pnHeader.add(lblDen);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateDenNgay.setBounds(690, 28, 130, 38);
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        dateDenNgay.setDate(cal.getTime());
        pnHeader.add(dateDenNgay);

        // Trạng thái (Đã duyệt / Chờ duyệt)
        JLabel lblTT = new JLabel("Trạng thái:");
        lblTT.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTT.setBounds(835, 28, 80, 35);
        pnHeader.add(lblTT);

        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã duyệt", "Chờ duyệt"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbTrangThai.setBounds(920, 28, 120, 38);
        pnHeader.add(cbTrangThai);

        // --- 3. CÁC NÚT CHỨC NĂNG (Bên phải) ---
        PillButton btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(1060, 22, 120, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTimKiem);
        
        PillButton btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(1200, 22, 120, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnLamMoi);

        // Button duyệt đơn (Chỉ hiện khi chọn đơn chờ duyệt - Logic xử lý sau)
        PillButton btnDuyetDon = new PillButton("Duyệt đơn");
        btnDuyetDon.setBounds(1340, 22, 120, 50);
        btnDuyetDon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        // btnDuyetDon.setBackground(new Color(0x2E7D32)); // Màu xanh lá đặc biệt
        pnHeader.add(btnDuyetDon);
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

        // --- BẢNG 1: DANH SÁCH PHIẾU TRẢ (TOP) ---
        String[] colPhieuTra = {"STT", "Mã phiếu trả", "Khách hàng", "Nhân viên", "Ngày lập", "Tổng tiền hoàn", "Trạng thái"};
        modelPhieuTra = new DefaultTableModel(colPhieuTra, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblPhieuTra = setupTable(modelPhieuTra);
        
        // Căn lề & Render màu sắc trạng thái
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        tblPhieuTra.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblPhieuTra.getColumnModel().getColumn(1).setCellRenderer(center); // Mã
        tblPhieuTra.getColumnModel().getColumn(4).setCellRenderer(center); // Ngày
        tblPhieuTra.getColumnModel().getColumn(5).setCellRenderer(right);  // Tiền

        // Render cột Trạng Thái (Màu sắc)
        tblPhieuTra.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                if ("Đã duyệt".equals(value)) {
                    lbl.setForeground(new Color(0x2E7D32)); // Xanh lá đậm
                } else {
                    lbl.setForeground(new Color(0xE65100)); // Cam đậm
                }
                return lbl;
            }
        });

        tblPhieuTra.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblPhieuTra.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPhieuTra.getColumnModel().getColumn(2).setPreferredWidth(200);
        
        JScrollPane scrollPT = new JScrollPane(tblPhieuTra);
        scrollPT.setBorder(createTitledBorder("Danh sách phiếu trả hàng"));
        splitPane.setTopComponent(scrollPT);

        // --- BẢNG 2: CHI TIẾT PHIẾU TRẢ (BOTTOM) ---
        // Cột dựa trên Entity ChiTietPhieuTra: SP, Lý do, SL, Tiền Hoàn, Hướng xử lý (Trạng thái chi tiết)
        String[] colChiTiet = {"STT", "Sản phẩm", "Lý do trả", "Số lượng", "Tiền hoàn", "Hướng xử lý"};
        modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblChiTiet = setupTable(modelChiTiet);
        
        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(center); // SL
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(right);  // Tiền hoàn
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(center); // Hướng xử lý

        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên SP dài
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(200); // Lý do

        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        scrollChiTiet.setBorder(createTitledBorder("Chi tiết sản phẩm trả"));
        splitPane.setBottomComponent(scrollChiTiet);
    }

    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30); // Cao hơn chút cho dễ nhìn
        table.setSelectionBackground(new Color(0xC8E6C9));
        
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
        // Click phiếu trả -> Load chi tiết
        tblPhieuTra.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPhieuTra.getSelectedRow();
                if (row >= 0) {
                    String maPT = tblPhieuTra.getValueAt(row, 1).toString();
                    loadChiTietPhieuTra(maPT);
                }
            }
        });
    }

    private void loadDuLieuPhieuTra() {
        // Data fake khớp Entity PhieuTra
        Object[][] data = {
            {"1", "PT-20251019-0001", "Nguyễn Văn A", "Trần Thu Hà", "19/10/2025", "50,000 đ", "Đã duyệt"},
            {"2", "PT-20251018-0005", "Trần Thị B", "Lê Văn C", "18/10/2025", "120,000 đ", "Chờ duyệt"},
            {"3", "PT-20251017-0012", "Phạm Quốc Khánh", "Nguyễn Thị D", "17/10/2025", "2,500,000 đ", "Đã duyệt"},
            {"4", "PT-20251017-0003", "Chu Anh Khôi", "Trần Thu Hà", "17/10/2025", "300,000 đ", "Đã duyệt"},
            {"5", "PT-20251016-0008", "Lê Thanh Kha", "Lê Văn C", "16/10/2025", "15,000 đ", "Chờ duyệt"},
        };
        for (Object[] row : data) {
            modelPhieuTra.addRow(row);
        }
    }

    private void loadChiTietPhieuTra(String maPT) {
        modelChiTiet.setRowCount(0);
        
        // Data fake khớp Entity ChiTietPhieuTra (Lý do, Hướng xử lý: Nhập kho / Hủy)
        if (maPT.equals("PT-20251019-0001")) {
            modelChiTiet.addRow(new Object[]{"1", "Paracetamol 500mg", "Khách mua nhầm loại", "2 vỉ", "10,000 đ", "Nhập lại hàng"});
            modelChiTiet.addRow(new Object[]{"2", "Vitamin C", "Sản phẩm bị móp méo", "1 hộp", "40,000 đ", "Huỷ hàng"});
        } else if (maPT.equals("PT-20251018-0005")) {
            modelChiTiet.addRow(new Object[]{"1", "Thực phẩm chức năng A", "Hết hạn sử dụng (Lỗi NV)", "1 hộp", "120,000 đ", "Huỷ hàng"});
        } else {
            modelChiTiet.addRow(new Object[]{"1", "Sản phẩm mẫu", "Lý do mẫu", "1 cái", "0 đ", "Chờ duyệt"});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            JFrame frame = new JFrame("Tra cứu đơn trả hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuDonTraHang_GUI());
            frame.setVisible(true);
        });
    }
}