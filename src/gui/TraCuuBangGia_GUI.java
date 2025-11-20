/**
 * @author Quốc Khánh
 * @version 1.0
 * @since Nov 20, 2025
 *
 * Mô tả: Giao diện quản lý Bảng Giá bán hàng (Theo quy tắc khoảng giá nhập).
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
public class TraCuuBangGia_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;

    // Bảng Master: Danh sách Bảng giá
    private JTable tblBangGia;
    private DefaultTableModel modelBangGia;

    // Bảng Detail 1: Chi tiết các quy tắc giá (GiaTu - GiaDen - TiLe)
    private JTable tblChiTietQuyTac;
    private DefaultTableModel modelChiTietQuyTac;

    // Bảng Detail 2: Mô phỏng giá bán (Demo áp dụng lên sản phẩm)
    private JTable tblMoPhongGia;
    private DefaultTableModel modelMoPhongGia;

    private final DecimalFormat dfTien = new DecimalFormat("#,### đ");
    private final DecimalFormat dfTiLe = new DecimalFormat("#,##0.0 %"); // 1.2 -> 120%

    public TraCuuBangGia_GUI() {
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
        loadDuLieuBangGia();
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã bảng giá, tên bảng giá...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- BỘ LỌC ---
        // Lọc theo Trạng thái
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setBounds(550, 28, 80, 35);
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblTrangThai);

        JComboBox<String> cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang hoạt động", "Ngừng hoạt động"});
        cbTrangThai.setBounds(640, 28, 150, 38);
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbTrangThai);

        // Lọc theo Năm áp dụng
        JLabel lblNam = new JLabel("Năm:");
        lblNam.setBounds(810, 28, 50, 35);
        lblNam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(lblNam);

        JComboBox<String> cbNam = new JComboBox<>(new String[]{"Tất cả", "2024", "2025", "2026"});
        cbNam.setBounds(860, 28, 100, 38);
        cbNam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnHeader.add(cbNam);

        // --- NÚT CHỨC NĂNG ---
        PillButton btnTim = new PillButton("Tìm kiếm");
        btnTim.setBounds(1000, 22, 120, 50);
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTim);

        // Nút Tạo Mới
        PillButton btnThem = new PillButton("Lập bảng giá");
        btnThem.setBounds(1140, 22, 150, 50);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnThem);
        
        // Nút Kích Hoạt (Set Active)
        PillButton btnKichHoat = new PillButton("Áp dụng ngay");
        btnKichHoat.setBounds(1310, 22, 150, 50);
        btnKichHoat.setFont(new Font("Segoe UI", Font.BOLD, 18));
        // btnKichHoat.setBackground(new Color(46, 125, 50)); // Màu xanh lá đậm nếu muốn
        pnHeader.add(btnKichHoat);
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

        // --- TOP: BẢNG DANH SÁCH BẢNG GIÁ ---
        String[] colBG = {"STT", "Mã Bảng Giá", "Tên Bảng Giá", "Ngày áp dụng", "Người lập", "Trạng thái"};
        modelBangGia = new DefaultTableModel(colBG, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBangGia = setupTable(modelBangGia);
        
        // Render Trạng thái (Xanh lá vs Xám)
        tblBangGia.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Đang hoạt động".equals(value)) {
                    lbl.setForeground(new Color(0, 153, 51)); // Xanh lá
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lbl.setIcon(UIManager.getIcon("FileView.floppyDriveIcon")); // Demo icon
                } else {
                    lbl.setForeground(Color.GRAY);
                    lbl.setIcon(null);
                }
                return lbl;
            }
        });

        JScrollPane scrollBG = new JScrollPane(tblBangGia);
        scrollBG.setBorder(createTitledBorder("Danh sách Bảng giá bán hàng"));
        splitPane.setTopComponent(scrollBG);

        // --- BOTTOM: TABBED PANE ---
        JTabbedPane tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Tab 1: Chi tiết quy tắc
        tabChiTiet.addTab("Cấu hình quy tắc giá", createTabQuyTac());
        
        // Tab 2: Mô phỏng (Preview)
        tabChiTiet.addTab("Xem thử giá bán (Mô phỏng)", createTabMoPhong());

        splitPane.setBottomComponent(tabChiTiet);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // Tab 1: Bảng Quy Tắc (Khoảng giá -> Tỉ lệ)
    private JComponent createTabQuyTac() {
        String[] cols = {"STT", "Giá nhập từ", "Giá nhập đến", "Tỉ lệ định giá", "Lợi nhuận dự kiến"};
        modelChiTietQuyTac = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTietQuyTac = setupTable(modelChiTietQuyTac);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblChiTietQuyTac.getColumnModel().getColumn(1).setCellRenderer(right);
        tblChiTietQuyTac.getColumnModel().getColumn(2).setCellRenderer(right);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblChiTietQuyTac.getColumnModel().getColumn(3).setCellRenderer(center); // Tỉ lệ

        return new JScrollPane(tblChiTietQuyTac);
    }

    // Tab 2: Bảng Mô phỏng (Lấy vài SP mẫu ra tính thử)
    private JComponent createTabMoPhong() {
        String[] cols = {"Mã SP", "Tên thuốc mẫu", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra (Tính toán)"};
        modelMoPhongGia = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMoPhongGia = setupTable(modelMoPhongGia);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblMoPhongGia.getColumnModel().getColumn(2).setCellRenderer(right); // Giá vốn
        tblMoPhongGia.getColumnModel().getColumn(4).setCellRenderer(right); // Giá bán

        // Giá bán tô màu đỏ
        tblMoPhongGia.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                lbl.setForeground(new Color(220, 0, 0));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                return lbl;
            }
        });

        return new JScrollPane(tblMoPhongGia);
    }

    // Helper: Setup Table
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
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        
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
        tblBangGia.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblBangGia.getSelectedRow();
                if (row >= 0) {
                    String maBG = tblBangGia.getValueAt(row, 1).toString();
                    loadChiTiet(maBG);
                }
            }
        });
    }

    private void loadDuLieuBangGia() {
        // Data giả lập Entity BangGia
        Object[][] data = {
            {"1", "BG-20251101-0001", "Bảng giá tiêu chuẩn 2025", "01/01/2025", "Nguyễn Quản Lý", "Đang hoạt động"},
            {"2", "BG-20240101-0005", "Bảng giá cũ 2024", "01/01/2024", "Trần Cũ", "Ngừng hoạt động"},
            {"3", "BG-20250501-0002", "Bảng giá Khuyến mãi Hè", "01/05/2025", "Lê Văn C", "Chưa áp dụng"}
        };
        
        for (Object[] row : data) {
            modelBangGia.addRow(row);
        }
    }

    private void loadChiTiet(String maBG) {
        modelChiTietQuyTac.setRowCount(0);
        modelMoPhongGia.setRowCount(0);

        // Giả lập logic load từ DB theo mã BG
        if (maBG.equals("BG-20251101-0001")) { // Bảng giá chuẩn
            // 1. Load quy tắc (ChiTietBangGia)
            // Quy tắc: Giá thấp thì lời nhiều, giá cao thì lời ít
            modelChiTietQuyTac.addRow(new Object[]{"1", "0", "10,000", "1.5 (150%)", "50%"});
            modelChiTietQuyTac.addRow(new Object[]{"2", "10,001", "100,000", "1.3 (130%)", "30%"});
            modelChiTietQuyTac.addRow(new Object[]{"3", "100,001", "500,000", "1.15 (115%)", "15%"});
            modelChiTietQuyTac.addRow(new Object[]{"4", "500,001", "Trở lên", "1.05 (105%)", "5%"});
            
            // 2. Load Mô phỏng (Lấy vài thuốc điển hình tính thử)
            // SP1: 5k (Rơi vào khoảng 1: x1.5) -> 7.5k
            modelMoPhongGia.addRow(new Object[]{"SP001", "Paracetamol vỉ", "5,000", "1.5", "7,500"});
            // SP2: 50k (Rơi vào khoảng 2: x1.3) -> 65k
            modelMoPhongGia.addRow(new Object[]{"SP005", "Siro ho", "50,000", "1.3", "65,000"});
            // SP3: 200k (Rơi vào khoảng 3: x1.15) -> 230k
            modelMoPhongGia.addRow(new Object[]{"SP009", "Thực phẩm chức năng ABC", "200,000", "1.15", "230,000"});
        } 
        else if (maBG.equals("BG-20250501-0002")) { // Bảng giá KM Hè (Lời ít hơn để bán chạy)
            modelChiTietQuyTac.addRow(new Object[]{"1", "0", "Trở lên", "1.1 (110%)", "10%"}); // Đồng giá lời 10%
            
            modelMoPhongGia.addRow(new Object[]{"SP001", "Paracetamol vỉ", "5,000", "1.1", "5,500"});
            modelMoPhongGia.addRow(new Object[]{"SP005", "Siro ho", "50,000", "1.1", "55,000"});
        }
    }
}