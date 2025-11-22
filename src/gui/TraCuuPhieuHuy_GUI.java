/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Giao diện tra cứu phiếu hủy hàng (3 loại: NV tạo, Hệ thống tạo, Duyệt trả hàng).
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

public class TraCuuPhieuHuy_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;
    
    // Bảng Phiếu Hủy (Trên)
    private JTable tblPhieuHuy;
    private DefaultTableModel modelPhieuHuy;

    // Bảng Chi Tiết Phiếu Hủy (Dưới)
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;

    // Các component lọc
    private JTextField txtTimKiem;
    private JDateChooser dateTuNgay;
    private JDateChooser dateDenNgay;
    private JComboBox<String> cbTrangThai;
    private JComboBox<String> cbLoaiPhieu; // 🟢 Lọc theo 3 loại hủy

    public TraCuuPhieuHuy_GUI() {
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
        loadDuLieuPhieuHuy();
        addEvents();
    }

    // ==============================================================================
    //                              PHẦN HEADER
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94)); // Chiều cao chuẩn
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- 1. Ô TÌM KIẾM TO (Bên trái) ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã phiếu, tên nhân viên...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 350, 60); // Thu nhỏ xíu để nhường chỗ cho bộ lọc
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- 2. BỘ LỌC (Ở giữa) ---
        int yFilter = 28;
        int hFilter = 38;

        // Từ ngày
        JLabel lblTu = new JLabel("Từ:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTu.setBounds(390, yFilter, 30, 35);
        pnHeader.add(lblTu);

        dateTuNgay = new JDateChooser();
        dateTuNgay.setDateFormatString("dd/MM/yyyy");
        dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateTuNgay.setBounds(420, yFilter, 130, hFilter);
        dateTuNgay.setDate(new Date()); 
        pnHeader.add(dateTuNgay);

        // Đến ngày
        JLabel lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDen.setBounds(560, yFilter, 40, 35);
        pnHeader.add(lblDen);

        dateDenNgay = new JDateChooser();
        dateDenNgay.setDateFormatString("dd/MM/yyyy");
        dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateDenNgay.setBounds(600, yFilter, 130, hFilter);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, 1);
        dateDenNgay.setDate(cal.getTime());
        pnHeader.add(dateDenNgay);

        // Trạng thái (Đã duyệt / Chờ duyệt)
        JLabel lblTT = new JLabel("Trạng thái:");
        lblTT.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTT.setBounds(740, yFilter, 80, 35);
        pnHeader.add(lblTT);

        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã duyệt", "Chờ duyệt"});
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbTrangThai.setBounds(820, yFilter, 110, hFilter);
        pnHeader.add(cbTrangThai);
        
        // 🟢 Loại phiếu (3 loại)
        JLabel lblLoai = new JLabel("Loại:");
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblLoai.setBounds(940, yFilter, 40, 35);
        pnHeader.add(lblLoai);

        cbLoaiPhieu = new JComboBox<>(new String[]{"Tất cả", "Nhân viên tạo", "Hệ thống (Hết hạn)", "Duyệt trả hàng"});
        cbLoaiPhieu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        cbLoaiPhieu.setBounds(980, yFilter, 150, hFilter);
        pnHeader.add(cbLoaiPhieu);

        // --- 3. CÁC NÚT CHỨC NĂNG (Bên phải ngoài cùng) ---
        // Do nhiều bộ lọc nên đẩy nút sang phải hoặc thu nhỏ lại
        PillButton btnTimKiem = new PillButton("Tìm");
        btnTimKiem.setBounds(1150, 22, 100, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTimKiem);
        
        PillButton btnLamMoi = new PillButton("Mới");
        btnLamMoi.setBounds(1260, 22, 100, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnLamMoi);

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

        // --- BẢNG 1: DANH SÁCH PHIẾU HỦY (TOP) ---
        // Thêm cột "Nguồn gốc" để phân biệt 3 loại
        String[] colPhieuHuy = {"STT", "Mã phiếu hủy", "Người lập / Hệ thống", "Ngày lập", "Nguồn gốc", "Tổng tiền", "Trạng thái"};
        modelPhieuHuy = new DefaultTableModel(colPhieuHuy, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblPhieuHuy = setupTable(modelPhieuHuy);
        
        // Căn lề & Render màu sắc
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);

        tblPhieuHuy.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblPhieuHuy.getColumnModel().getColumn(1).setCellRenderer(center); // Mã
        tblPhieuHuy.getColumnModel().getColumn(3).setCellRenderer(center); // Ngày
        tblPhieuHuy.getColumnModel().getColumn(4).setCellRenderer(center); // Nguồn gốc
        tblPhieuHuy.getColumnModel().getColumn(5).setCellRenderer(right);  // Tiền

        // Render cột Trạng Thái (Màu sắc)
        tblPhieuHuy.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                String status = (String) value;
                if ("Đã duyệt".equals(status)) {
                    lbl.setForeground(new Color(0x2E7D32)); // Xanh lá
                } else {
                    lbl.setForeground(new Color(0xE65100)); // Cam
                }
                return lbl;
            }
        });
        
        // Render cột Nguồn gốc (Màu sắc để dễ phân biệt 3 loại)
        tblPhieuHuy.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String src = (String) value;
                if (src.contains("Hệ thống")) {
                    lbl.setForeground(Color.RED);
                } else if (src.contains("Trả hàng")) {
                    lbl.setForeground(Color.BLUE);
                } else {
                    lbl.setForeground(Color.DARK_GRAY);
                }
                return lbl;
            }
        });

        tblPhieuHuy.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblPhieuHuy.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblPhieuHuy.getColumnModel().getColumn(4).setPreferredWidth(180);
        
        JScrollPane scrollPH = new JScrollPane(tblPhieuHuy);
        scrollPH.setBorder(createTitledBorder("Danh sách phiếu hủy hàng"));
        splitPane.setTopComponent(scrollPH);

        // --- BẢNG 2: CHI TIẾT PHIẾU HỦY (BOTTOM) ---
        String[] colChiTiet = {"STT", "Mã Lô", "Sản phẩm", "Lý do chi tiết", "Số lượng", "Giá vốn", "Thành tiền", "Trạng thái"};
        modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tblChiTiet = setupTable(modelChiTiet);
        
        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(center);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(center); // SL
        tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(right);  // Giá vốn
        tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(right);  // Thành tiền

        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(250); // Tên SP
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(200); // Lý do

        JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
        scrollChiTiet.setBorder(createTitledBorder("Chi tiết sản phẩm hủy"));
        splitPane.setBottomComponent(scrollChiTiet);
    }

    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(30);
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
        // Click phiếu hủy -> Load chi tiết
        tblPhieuHuy.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPhieuHuy.getSelectedRow();
                if (row >= 0) {
                    String maPH = tblPhieuHuy.getValueAt(row, 1).toString();
                    loadChiTietPhieuHuy(maPH);
                }
            }
        });
    }

    private void loadDuLieuPhieuHuy() {
        // 3 LOẠI PHIẾU HỦY GIẢ LẬP
        Object[][] data = {
            // Loại 1: Nhân viên tự tạo (Hàng vỡ, hỏng trong kho)
            {"1", "PH-20251019-0001", "Trần Thu Hà", "19/10/2025", "NV tạo (Hư hỏng)", "500,000 đ", "Chờ duyệt"},
            
            // Loại 2: Hệ thống tự tạo (Quét lô hết hạn)
            {"2", "PH-20251019-AUTO", "HỆ THỐNG", "19/10/2025", "Hệ thống (Hết hạn)", "1,200,000 đ", "Chờ duyệt"},
            
            // Loại 3: Từ duyệt trả hàng (Manager duyệt nhập kho nhưng hàng hỏng -> Hủy luôn)
            {"3", "PH-20251018-0005", "Nguyễn Văn Quản Lý", "18/10/2025", "Từ Trả hàng (PT-001)", "40,000 đ", "Đã duyệt"},
            
            // Thêm vài cái nữa
            {"4", "PH-20251017-0002", "Lê Văn C", "17/10/2025", "NV tạo (Hư hỏng)", "150,000 đ", "Đã duyệt"},
            {"5", "PH-20251015-AUTO", "HỆ THỐNG", "15/10/2025", "Hệ thống (Hết hạn)", "2,500,000 đ", "Đã duyệt"},
        };
        
        for (Object[] row : data) {
            modelPhieuHuy.addRow(row);
        }
    }

    private void loadChiTietPhieuHuy(String maPH) {
        modelChiTiet.setRowCount(0);
        
        // Fake data chi tiết theo mã phiếu
        if (maPH.equals("PH-20251019-0001")) {
            modelChiTiet.addRow(new Object[]{"1", "L001", "Chai thủy tinh A", "Vỡ khi vận chuyển kho", "5", "100,000 đ", "500,000 đ", "Chờ duyệt"});
        } 
        else if (maPH.contains("AUTO")) {
            modelChiTiet.addRow(new Object[]{"1", "L999", "Thực phẩm CN B", "Hết hạn sử dụng (Auto)", "10", "120,000 đ", "1,200,000 đ", "Chờ duyệt"});
        } 
        else if (maPH.equals("PH-20251018-0005")) {
            // Loại từ trả hàng: Khách trả về, Manager thấy hỏng nên hủy
            modelChiTiet.addRow(new Object[]{"1", "L002", "Vitamin C", "Hàng trả lại bị móp méo", "1", "40,000 đ", "40,000 đ", "Đã hủy hàng"});
        } 
        else {
            modelChiTiet.addRow(new Object[]{"1", "LXXX", "Sản phẩm mẫu", "Lý do mẫu", "1", "0 đ", "0 đ", "..."});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            JFrame frame = new JFrame("Tra cứu phiếu hủy");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1450, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new TraCuuPhieuHuy_GUI());
            frame.setVisible(true);
        });
    }
}