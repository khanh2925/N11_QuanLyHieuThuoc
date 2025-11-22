/**
 * @author Quốc Khánh
 * @version 1.0
 * @since Nov 20, 2025
 *
 * Mô tả: Giao diện quản lý & tra cứu Đơn vị tính (Kèm danh sách thuốc sử dụng).
 */
package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

@SuppressWarnings("serial")
public class TraCuuDonViTinh_GUI extends JPanel {

    private JPanel pnHeader;
    private JPanel pnCenter;

    // Bảng Master: Đơn vị tính
    private JTable tblDonViTinh;
    private DefaultTableModel modelDonViTinh;

    // Bảng Detail: Sản phẩm sử dụng đơn vị này
    private JTabbedPane tabChiTiet;
    private JTable tblSanPhamSuDung;
    private DefaultTableModel modelSanPhamSuDung;

    public TraCuuDonViTinh_GUI() {
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
        loadDuLieuDonVi();
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm mã hoặc tên đơn vị tính...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        // --- NÚT CHỨC NĂNG ---
        // Nút Tìm
        PillButton btnTim = new PillButton("Tìm kiếm");
        btnTim.setBounds(550, 22, 140, 50);
        btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnHeader.add(btnTim);
        
        // Nút Làm mới
        PillButton btnLamMoi = new PillButton("Làm mới");
        btnLamMoi.setBounds(870, 22, 140, 50);
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

        // Chia đôi màn hình: Trên (Ds Đơn vị) - Dưới (Ds Thuốc dùng đơn vị đó)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);

        // --- TOP: BẢNG ĐƠN VỊ TÍNH ---
        String[] colDVT = {"STT", "Mã Đơn Vị", "Tên Đơn Vị Tính", "Số lượng thuốc đang dùng"};
        modelDonViTinh = new DefaultTableModel(colDVT, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDonViTinh = setupTable(modelDonViTinh);
        
        // Căn giữa toàn bộ
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tblDonViTinh.getColumnCount(); i++) {
            tblDonViTinh.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollDVT = new JScrollPane(tblDonViTinh);
        scrollDVT.setBorder(createTitledBorder("Danh mục Đơn vị tính"));
        splitPane.setTopComponent(scrollDVT);

        // --- BOTTOM: TAB CHI TIẾT (Sản phẩm sử dụng) ---
        tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tabChiTiet.addTab("Sản phẩm sử dụng đơn vị này", createTabSanPhamSuDung());
        
        splitPane.setBottomComponent(tabChiTiet);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private JComponent createTabSanPhamSuDung() {
        String[] cols = {"STT", "Mã Sản Phẩm", "Tên Sản Phẩm", "Vai trò đơn vị", "Quy đổi"};
        modelSanPhamSuDung = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPhamSuDung = setupTable(modelSanPhamSuDung);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        
        tblSanPhamSuDung.getColumnModel().getColumn(0).setCellRenderer(center);
        tblSanPhamSuDung.getColumnModel().getColumn(1).setCellRenderer(center);
        // Tên sản phẩm để mặc định (Left)
        tblSanPhamSuDung.getColumnModel().getColumn(3).setCellRenderer(center);
        tblSanPhamSuDung.getColumnModel().getColumn(4).setCellRenderer(center);
        
        return new JScrollPane(tblSanPhamSuDung);
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
        // Sự kiện click vào Đơn vị -> Load danh sách thuốc
        tblDonViTinh.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblDonViTinh.getSelectedRow();
                if (row >= 0) {
                    String tenDVT = tblDonViTinh.getValueAt(row, 2).toString();
                    loadSanPhamTheoDonVi(tenDVT);
                }
            }
        });
    }

    private void loadDuLieuDonVi() {
        // Data giả lập (Entity DonViTinh)
        Object[][] data = {
            {"1", "DVT-001", "Viên", "150"},
            {"2", "DVT-002", "Vỉ", "45"},
            {"3", "DVT-003", "Hộp", "200"},
            {"4", "DVT-004", "Chai", "30"},
            {"5", "DVT-005", "Gói", "12"},
            {"6", "DVT-006", "Tuýp", "8"},
            {"7", "DVT-007", "Thùng", "15"}
        };
        
        for (Object[] row : data) {
            modelDonViTinh.addRow(row);
        }
    }

    private void loadSanPhamTheoDonVi(String tenDVT) {
        modelSanPhamSuDung.setRowCount(0);

        // Giả lập logic tìm kiếm trong DB (Bảng QuyCachDongGoi)
        if (tenDVT.equals("Viên")) {
            modelSanPhamSuDung.addRow(new Object[]{"1", "SP001", "Paracetamol 500mg", "Đơn vị gốc", "1"});
            modelSanPhamSuDung.addRow(new Object[]{"2", "SP004", "Panadol Extra", "Đơn vị gốc", "1"});
            modelSanPhamSuDung.addRow(new Object[]{"3", "SP009", "Vitamin C", "Đơn vị gốc", "1"});
        } 
        else if (tenDVT.equals("Hộp")) {
            modelSanPhamSuDung.addRow(new Object[]{"1", "SP001", "Paracetamol 500mg", "Quy đổi", "100 Viên"});
            modelSanPhamSuDung.addRow(new Object[]{"2", "SP003", "Băng cá nhân", "Đơn vị gốc", "1"});
            modelSanPhamSuDung.addRow(new Object[]{"3", "SP008", "Khẩu trang y tế", "Đơn vị gốc", "1"});
        }
        else if (tenDVT.equals("Chai")) {
            modelSanPhamSuDung.addRow(new Object[]{"1", "SP005", "Siro ho Prospan", "Đơn vị gốc", "1"});
            modelSanPhamSuDung.addRow(new Object[]{"2", "SP006", "Nước muối sinh lý", "Đơn vị gốc", "1"});
        }
    }
}