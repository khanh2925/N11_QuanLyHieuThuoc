package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.BangGia; // Giả định bạn có entity này
import entity.ChiTietBangGia; // Giả định bạn có entity này

@SuppressWarnings("serial")
public class BangGia_GUI extends JPanel implements ActionListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu (Master)
    private JTextField txtMaBG, txtTenBG, txtNgayApDung;
    private JComboBox<String> cboTrangThai;
    private JCheckBox chkHoatDong;

    // Panel Nút bấm (Master)
    private PillButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    
    // Header (Tìm kiếm)
    private JTextField txtTimKiem;

    // Tab 1: Danh sách Bảng Giá
    private JTable tblBangGia;
    private DefaultTableModel modelBangGia;

    // Tab 2: Chi tiết Quy tắc giá
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;
    private PillButton btnThemCT, btnXoaCT; // Nút thao tác chi tiết
    private JTextField txtGiaTu, txtGiaDen, txtTiLe; // Input nhập nhanh chi tiết

    // Tab 3: Mô phỏng giá
    private JTable tblMoPhong;
    private DefaultTableModel modelMoPhong;

    // Utils
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public BangGia_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER (SplitPane: Form + Tabs)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);
        
        // Load data mẫu
        loadDataSample();
    }

    // ==========================================================================
    //                              PHẦN HEADER
    // ==========================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã, tên bảng giá...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng tìm kiếm"));
        pnHeader.add(btnTimKiem);
    }

    // ==========================================================================
    //                              PHẦN CENTER (SPLIT PANE)
    // ==========================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- A. PHẦN TRÊN (TOP): FORM + NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin bảng giá"));

        // A1. Form Nhập Liệu
        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm); 
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        // A2. Panel Nút Chức Năng
        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton); 
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- B. PHẦN DƯỚI (BOTTOM): TABBED PANE ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_TEXT);

        // Tab 1: Danh sách Bảng Giá
        JPanel pnTab1 = new JPanel(new BorderLayout());
        pnTab1.setBackground(Color.WHITE);
        taoBangDanhSach(pnTab1);
        tabbedPane.addTab("Danh sách Bảng Giá", pnTab1);

        // Tab 2: Cấu hình Quy tắc giá
        JPanel pnTab2 = new JPanel(new BorderLayout());
        pnTab2.setBackground(Color.WHITE);
        taoBangChiTiet(pnTab2);
        tabbedPane.addTab("Cấu hình Quy tắc giá", pnTab2);
        
        // Tab 3: Xem thử giá bán (Mô phỏng)
        JPanel pnTab3 = new JPanel(new BorderLayout());
        pnTab3.setBackground(Color.WHITE);
        taoBangMoPhong(pnTab3);
        tabbedPane.addTab("Xem thử giá bán (Mô phỏng)", pnTab3);

        // --- C. TẠO SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, tabbedPane);
        splitPane.setDividerLocation(280); // Form bảng giá ngắn hơn form sản phẩm
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // --- FORM NHẬP LIỆU ---
    private void taoFormNhapLieu(JPanel p) {
        // Cấu hình kích thước (Căn giữa vì ít trường)
        int xStart = 150;       
        int yStart = 40;
        int hText = 40;         
        int wTxt = 350;         
        int gap = 30;           

        // Hàng 1
        p.add(createLabel("Mã BG:", xStart, yStart));
        txtMaBG = createTextField(xStart + 100, yStart, wTxt);
        txtMaBG.setEditable(false); // Mã tự sinh
        p.add(txtMaBG);

        p.add(createLabel("Ngày áp dụng:", xStart + 500, yStart));
        txtNgayApDung = createTextField(xStart + 620, yStart, wTxt);
        txtNgayApDung.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        p.add(txtNgayApDung);

        // Hàng 2
        yStart += hText + gap;
        p.add(createLabel("Tên BG:", xStart, yStart));
        txtTenBG = createTextField(xStart + 100, yStart, wTxt);
        p.add(txtTenBG);

        p.add(createLabel("Trạng thái:", xStart + 500, yStart));
        cboTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Ngừng hoạt động", "Chưa áp dụng"});
        cboTrangThai.setBounds(xStart + 620, yStart, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);
        
        // Hàng 3 (Checkbox tùy chọn)
        yStart += hText + gap;
        chkHoatDong = new JCheckBox("Đặt làm bảng giá mặc định ngay khi tạo");
        chkHoatDong.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        chkHoatDong.setBackground(Color.WHITE);
        chkHoatDong.setBounds(xStart + 100, yStart, 400, 30);
        p.add(chkHoatDong);
    }

    // --- PANEL NÚT BẤM (MASTER) ---
    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int btnH = 45;
        int btnW = 140;

        btnThem = createPillButton("Tạo mới", btnW, btnH);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = createPillButton("Cập nhật", btnW, btnH);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnXoa = createPillButton("Xóa", btnW, btnH);
        gbc.gridy = 2; p.add(btnXoa, gbc);

        btnLamMoi = createPillButton("Làm mới", btnW, btnH);
        gbc.gridy = 3; p.add(btnLamMoi, gbc);
    }

    // --- TAB 1: BẢNG DANH SÁCH BẢNG GIÁ ---
    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã Bảng Giá", "Tên Bảng Giá", "Ngày áp dụng", "Người lập", "Trạng thái"};
        modelBangGia = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBangGia = setupTable(modelBangGia);

        // Render Trạng thái
        tblBangGia.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if("Đang hoạt động".equals(value)) {
                    lbl.setForeground(new Color(0, 128, 0));
                    lbl.setFont(FONT_BOLD);
                } else {
                    lbl.setForeground(Color.GRAY);
                }
                return lbl;
            }
        });

        // Event click
        tblBangGia.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Logic load dữ liệu lên form và load chi tiết xuống Tab 2
                int row = tblBangGia.getSelectedRow();
                if(row >= 0) {
                    txtMaBG.setText(tblBangGia.getValueAt(row, 0).toString());
                    txtTenBG.setText(tblBangGia.getValueAt(row, 1).toString());
                    // Load chi tiết (Giả lập)
                    loadChiTietMau(); 
                }
            }
        });

        JScrollPane scr = new JScrollPane(tblBangGia);
        scr.setBorder(BorderFactory.createEmptyBorder());
        p.add(scr, BorderLayout.CENTER);
    }

    // --- TAB 2: BẢNG CHI TIẾT QUY TẮC ---
    private void taoBangChiTiet(JPanel p) {
        // Toolbar nhập liệu nhanh cho chi tiết
        JPanel pnToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnToolBar.setBackground(Color.WHITE);
        pnToolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        pnToolBar.add(new JLabel("Giá từ:"));
        txtGiaTu = new JTextField(10); pnToolBar.add(txtGiaTu);
        
        pnToolBar.add(new JLabel("Đến:"));
        txtGiaDen = new JTextField(10); pnToolBar.add(txtGiaDen);
        
        pnToolBar.add(new JLabel("Tỉ lệ (VD 1.2):"));
        txtTiLe = new JTextField(5); pnToolBar.add(txtTiLe);

        btnThemCT = createPillButton("Thêm quy tắc", 130, 35);
        btnThemCT.setFont(FONT_TEXT);
        pnToolBar.add(btnThemCT);
        
        btnXoaCT = createPillButton("Xóa quy tắc", 130, 35);
        btnXoaCT.setFont(FONT_TEXT);
        pnToolBar.add(btnXoaCT);
        
        p.add(pnToolBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"STT", "Giá nhập từ", "Giá nhập đến", "Tỉ lệ định giá", "Lợi nhuận dự kiến"};
        modelChiTiet = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTiet = setupTable(modelChiTiet);
        
        JScrollPane scr = new JScrollPane(tblChiTiet);
        scr.setBorder(BorderFactory.createEmptyBorder());
        p.add(scr, BorderLayout.CENTER);
    }
    
    // --- TAB 3: BẢNG MÔ PHỎNG ---
    private void taoBangMoPhong(JPanel p) {
        // Table
        String[] cols = {"Mã SP", "Tên thuốc mẫu", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra (Tính toán)"};
        modelMoPhong = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMoPhong = setupTable(modelMoPhong);
        
        // Tô đỏ giá bán
        tblMoPhong.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                lbl.setForeground(Color.RED);
                lbl.setFont(FONT_BOLD);
                return lbl;
            }
        });
        
        JScrollPane scr = new JScrollPane(tblMoPhong);
        scr.setBorder(BorderFactory.createEmptyBorder());
        p.add(scr, BorderLayout.CENTER);
    }

    // ==========================================================================
    //                              DATA MẪU & HELPERS
    // ==========================================================================
    
    private void loadDataSample() {
        modelBangGia.addRow(new Object[]{"BG-20250101-001", "Bảng giá chuẩn 2025", "01/01/2025", "Admin", "Đang hoạt động"});
        modelBangGia.addRow(new Object[]{"BG-20240101-001", "Bảng giá cũ 2024", "01/01/2024", "NV01", "Ngừng hoạt động"});
    }
    
    private void loadChiTietMau() {
        modelChiTiet.setRowCount(0);
        modelChiTiet.addRow(new Object[]{"1", "0", "10,000", "1.5", "50%"});
        modelChiTiet.addRow(new Object[]{"2", "10,001", "100,000", "1.3", "30%"});
        modelChiTiet.addRow(new Object[]{"3", "100,001", "Trở lên", "1.1", "10%"});
        
        modelMoPhong.setRowCount(0);
        modelMoPhong.addRow(new Object[]{"SP001", "Paracetamol", "5,000", "1.5", "7,500"});
        modelMoPhong.addRow(new Object[]{"SP002", "Thuốc bổ não", "50,000", "1.3", "65,000"});
        modelMoPhong.addRow(new Object[]{"SP003", "Máy đo huyết áp", "500,000", "1.1", "550,000"});
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThemCT)) {
            // Logic thêm dòng vào bảng chi tiết từ ô nhập nhanh
            if(txtTiLe.getText().isEmpty()) return;
            modelChiTiet.addRow(new Object[]{
                modelChiTiet.getRowCount()+1, 
                txtGiaTu.getText(), 
                txtGiaDen.getText(), 
                txtTiLe.getText(), 
                "Unknown"
            });
        } else if (o.equals(btnXoaCT)) {
            int row = tblChiTiet.getSelectedRow();
            if(row != -1) modelChiTiet.removeRow(row);
        }
        // Các nút khác xử lý tương tự Quản lý Sản phẩm
    }

    // --- UI Helpers ---
    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TEXT);
        lbl.setBounds(x, y, 120, 35);
        return lbl;
    }

    private JTextField createTextField(int x, int y, int w) {
        JTextField txt = new JTextField();
        txt.setFont(FONT_TEXT);
        txt.setBounds(x, y, w, 35);
        return txt;
    }

    private PillButton createPillButton(String text, int w, int h) {
        PillButton btn = new PillButton(text);
        btn.setFont(FONT_BOLD);
        btn.setPreferredSize(new Dimension(w, h));
        btn.addActionListener(this);
        return btn;
    }

    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_TEXT);
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        
        return table;
    }

    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Bảng Giá");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new BangGia_GUI());
            frame.setVisible(true);
        });
    }
}