package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;

// Import Entity & DAO
import dao.BangGia_DAO;
import dao.ChiTietBangGia_DAO;
import dao.SanPham_DAO;
import entity.BangGia;
import entity.ChiTietBangGia;
import entity.SanPham;

/**
 * @author Quốc Khánh
 * @version 1.3 (Standardized UI Layout & Fonts)
 */
@SuppressWarnings("serial")
public class TraCuuBangGia_GUI extends JPanel implements ActionListener {

    // Components UI
    private JPanel pnHeader;
    private JPanel pnCenter;

    // Table BangGia
    private JTable tblBangGia;
    private DefaultTableModel modelBangGia;

    // Table ChiTiet (Quy Tac)
    private JTable tblChiTietQuyTac;
    private DefaultTableModel modelChiTietQuyTac;

    // Table MoPhong
    private JTable tblMoPhongGia;
    private DefaultTableModel modelMoPhongGia;

    // Filter Components
    private JTextField txtTimKiem;
    private JComboBox<String> cbTrangThai;
    private JComboBox<String> cbNam;
    private PillButton btnTimKiem;
    private PillButton btnLamMoi; 
    private PillButton btnXuatExcel; 

    // Utils & DAO
    private final DecimalFormat dfTien = new DecimalFormat("#,### đ");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private BangGia_DAO bangGiaDAO;
    private ChiTietBangGia_DAO chiTietBangGiaDAO;
    private SanPham_DAO sanPhamDAO;

    // Cache Data
    private List<BangGia> dsBangGiaHienTai;
    private String tuKhoa;

    public TraCuuBangGia_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // 1. Init DAO
        bangGiaDAO = new BangGia_DAO();
        chiTietBangGiaDAO = new ChiTietBangGia_DAO();
        sanPhamDAO = new SanPham_DAO();
        dsBangGiaHienTai = new ArrayList<>();

        initialize();
        setupKeyboardShortcuts(); // Thiết lập phím tắt
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // Center
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // Events
        addEvents();
        
        // Load data
        xuLyLamMoi(); 
    }

    // ==============================================================================
    //                                  UI: HEADER
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- Ô TÌM KIẾM (Font 20, Size 480x60 - Chuẩn) ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã bảng giá, tên bảng giá... (F1 / Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20)); 
        txtTimKiem.setBounds(25, 17, 480, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimKiem);

        // --- BỘ LỌC (Font 18) ---
        
        // 1. Trạng thái (Vị trí tương đương filter đầu tiên)
        addFilterLabel("Trạng thái:", 530, 28, 100, 35);
        
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang hoạt động", "Ngừng hoạt động"});
        cbTrangThai.setBounds(630, 28, 190, 38); // Width 190
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(cbTrangThai);

        // 2. Năm (Vị trí tiếp theo)
        addFilterLabel("Năm:", 840, 28, 60, 35);

        // Tự động sinh năm từ 2023 đến hiện tại + 2
        int namHienTai = java.time.LocalDate.now().getYear();
        cbNam = new JComboBox<>();
        cbNam.addItem("Tất cả");
        for (int i = namHienTai - 2; i <= namHienTai + 2; i++) {
            cbNam.addItem(String.valueOf(i));
        }
        cbNam.setSelectedItem(String.valueOf(namHienTai)); 
        
        cbNam.setBounds(900, 28, 150, 38); // Width 150 (nhỏ hơn xíu vì năm ngắn)
        cbNam.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(cbNam);

        // --- NÚT CHỨC NĂNG (Font 18 Bold - Chuẩn) ---
        btnTimKiem = new PillButton(
                "<html>" +
                    "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                    "</center>" +
                "</html>"
            );
        btnTimKiem.setBounds(1120, 22, 160, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTimKiem.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên bảng giá và bộ lọc</html>");
        pnHeader.add(btnTimKiem);

        btnLamMoi = new PillButton(
            "<html>" +
                "<center>" +
                    "LÀM MỚI<br>" +
                    "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                "</center>" +
            "</html>"
        );
        btnLamMoi.setBounds(1295, 22, 140, 50);
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        pnHeader.add(btnLamMoi);
        
        btnXuatExcel = new PillButton(
                "<html>" +
                    "<center>" +
                        "XUẤT EXCEL<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+E)</span>" +
                    "</center>" +
                "</html>"
            );
        btnXuatExcel.setBounds(1450, 22, 150, 50);
        btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnXuatExcel.setToolTipText("<html><b>Phím tắt:</b> Ctrl+E<br>Xuất dữ liệu ra file Excel (Danh sách, Quy tắc, Mô phỏng)</html>");
        pnHeader.add(btnXuatExcel);
    }
    
    // Helper tạo label font 18
    private void addFilterLabel(String text, int x, int y, int w, int h) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, w, h);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(lbl);
    }

    // ==============================================================================
    //                                  UI: CENTER
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
        
        // Render Trạng thái (Font to hơn chút)
        tblBangGia.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Đang hoạt động".equals(value)) {
                    lbl.setForeground(new Color(0, 153, 51)); // Xanh lá
                    lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
                } else {
                    lbl.setForeground(Color.GRAY);
                    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                }
                return lbl;
            }
        });

        JScrollPane scrollBG = new JScrollPane(tblBangGia);
        scrollBG.setBorder(createTitledBorder("Danh sách Bảng giá bán hàng"));
        splitPane.setTopComponent(scrollBG);

        // --- BOTTOM: TABBED PANE (Font 16) ---
        JTabbedPane tabChiTiet = new JTabbedPane();
        tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        tabChiTiet.addTab("Cấu hình quy tắc giá", createTabQuyTac());
        tabChiTiet.addTab("Xem thử giá bán (Mô phỏng)", createTabMoPhong());

        splitPane.setBottomComponent(tabChiTiet);
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

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
        tblChiTietQuyTac.getColumnModel().getColumn(4).setCellRenderer(center); // Lợi nhuận

        return new JScrollPane(tblChiTietQuyTac);
    }

    private JComponent createTabMoPhong() {
        String[] cols = {"Mã SP", "Tên sản phẩm", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra (Tính toán)"};
        modelMoPhongGia = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMoPhongGia = setupTable(modelMoPhongGia);
        
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblMoPhongGia.getColumnModel().getColumn(2).setCellRenderer(right); // Giá vốn
        tblMoPhongGia.getColumnModel().getColumn(3).setCellRenderer(right); // Tỉ lệ
        tblMoPhongGia.getColumnModel().getColumn(4).setCellRenderer(right); // Giá bán

        // Giá bán tô màu đỏ
        tblMoPhongGia.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                lbl.setForeground(new Color(220, 0, 0));
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
                return lbl;
            }
        });

        return new JScrollPane(tblMoPhongGia);
    }

    // Setup Table Chuẩn (Font 16, RowHeight 35)
    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight(35);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(0xC8E6C9));
        table.setSelectionForeground(Color.BLACK);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(new Color(33, 150, 243));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40)); // Header Height 40
        return table;
    }

    // Border Title Chuẩn (Font 18 Bold)
    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY
        );
    }

    // ==============================================================================
    //                                  SỰ KIỆN & LOGIC
    // ==============================================================================
    
    private void addEvents() {
        btnTimKiem.addActionListener(this);
        btnLamMoi.addActionListener(this);
        txtTimKiem.addActionListener(this); 
        btnXuatExcel.addActionListener(this);

        // Click bảng giá -> Load chi tiết
        tblBangGia.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadChiTietTuDongChon();
            }
        });
    }

    /**
     * Thiết lập phím tắt cho màn hình Tra cứu Bảng Giá
     */
    private void setupKeyboardShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // F1: Focus tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
        actionMap.put("focusTimKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });

        // F5: Làm mới
        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyLamMoi();
            }
        });

        // Ctrl+F: Focus tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke("control F"), "timKiem");
        actionMap.put("timKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });

        // Ctrl+E: Xuất Excel
        inputMap.put(KeyStroke.getKeyStroke("control E"), "xuatExcel");
        actionMap.put("xuatExcel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyXuatExcel();
            }
        });

        // Enter trên ô tìm kiếm
        txtTimKiem.addActionListener(ev -> xuLyTimKiem());
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnTimKiem || o == txtTimKiem) {
            xuLyTimKiem();
        } else if (o == btnLamMoi) {
            xuLyLamMoi();
        } else if (o == btnXuatExcel) {
			xuLyXuatExcel();
		}
    }

    // --- 1. Load Data ---
    private void xuLyLamMoi() {
        txtTimKiem.setText("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã bảng giá, tên bảng giá... (F1 / Ctrl+F)");
        cbTrangThai.setSelectedIndex(0);
        
        // Load tất cả từ DB
        dsBangGiaHienTai = bangGiaDAO.layTatCaBangGia(); 
        renderBangGia(dsBangGiaHienTai);
        
        // Clear chi tiết
        modelChiTietQuyTac.setRowCount(0);
        modelMoPhongGia.setRowCount(0);
    }

    // --- 2. Tìm Kiếm & Lọc ---
    private void xuLyTimKiem() {
         tuKhoa = txtTimKiem.getText().trim();
        if (tuKhoa.contains("Tìm theo mã")) {
            tuKhoa = "";
        }
        
        // Lọc danh sách trong RAM (vì dữ liệu bảng giá thường ít)
        String trangThaiChon = (String) cbTrangThai.getSelectedItem();
        String namChon = (String) cbNam.getSelectedItem();

        List<BangGia> ketQua = dsBangGiaHienTai.stream().filter(bg -> {
            // 1. Lọc từ khóa
            boolean matchKey = true;
            if (!tuKhoa.isEmpty()) {
                matchKey = bg.getMaBangGia().toLowerCase().contains(tuKhoa.toLowerCase()) 
                        || bg.getTenBangGia().toLowerCase().contains(tuKhoa.toLowerCase());
            }
            
            // 2. Lọc trạng thái
            boolean matchStatus = true;
            if (!"Tất cả".equals(trangThaiChon)) {
                boolean dangHoatDong = "Đang hoạt động".equals(trangThaiChon);
                matchStatus = (bg.isHoatDong() == dangHoatDong);
            }
            
            // 3. Lọc năm
            boolean matchYear = true;
            if (!"Tất cả".equals(namChon)) {
                int nam = Integer.parseInt(namChon);
                matchYear = (bg.getNgayApDung().getYear() == nam);
            }
            
            return matchKey && matchStatus && matchYear;
        }).collect(Collectors.toList());
        
        renderBangGia(ketQua);
        modelChiTietQuyTac.setRowCount(0);
        modelMoPhongGia.setRowCount(0);
        
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bảng giá phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void renderBangGia(List<BangGia> list) {
        modelBangGia.setRowCount(0);
        int stt = 1;
        for (BangGia bg : list) {
            String tenNV = (bg.getNhanVien() != null) ? bg.getNhanVien().getTenNhanVien() : "Hệ thống";
            
            modelBangGia.addRow(new Object[]{
                stt++,
                bg.getMaBangGia(),
                bg.getTenBangGia(),
                dtf.format(bg.getNgayApDung()),
                tenNV,
                bg.isHoatDong() ? "Đang hoạt động" : "Ngừng hoạt động"
            });
        }
    }

    // --- 3. Load Chi Tiết ---
    private void loadChiTietTuDongChon() {
        int row = tblBangGia.getSelectedRow();
        if (row >= 0) {
            String maBG = tblBangGia.getValueAt(row, 1).toString();
            
            // 1. Load Quy Tắc
            List<ChiTietBangGia> listCT = chiTietBangGiaDAO.layChiTietTheoMaBangGia(maBG);
            renderBangQuyTac(listCT);
            
            // 2. Load Mô Phỏng (Lấy top 20 sản phẩm để tính thử)
            renderBangMoPhong(listCT);
        }
    }

    private void renderBangQuyTac(List<ChiTietBangGia> list) {
        modelChiTietQuyTac.setRowCount(0);
        int stt = 1;
        // Sắp xếp theo Giá Từ tăng dần để dễ nhìn
        list.sort((a, b) -> Double.compare(a.getGiaTu(), b.getGiaTu()));
        
        for (ChiTietBangGia ct : list) {
            double loiNhuanPhanTram = (ct.getTiLe() - 1) * 100;
            String loiNhuanStr = String.format("%.0f %%", loiNhuanPhanTram);
            
            // Xử lý hiển thị "Trở lên" nếu giá đến là MAX_VALUE hoặc rất lớn
            String giaDenStr = (ct.getGiaDen() > 999999999) ? "Trở lên" : dfTien.format(ct.getGiaDen());

            modelChiTietQuyTac.addRow(new Object[]{
                stt++,
                dfTien.format(ct.getGiaTu()),
                giaDenStr,
                ct.getTiLe() + " (" + (int)(ct.getTiLe()*100) + "%)",
                loiNhuanStr
            });
        }
    }

    /**
     * Tính toán giá bán mô phỏng cho danh sách sản phẩm dựa trên quy tắc giá
     */
    private void renderBangMoPhong(List<ChiTietBangGia> listQuyTac) {
        modelMoPhongGia.setRowCount(0);
        
        // Lấy mẫu 20 sản phẩm từ DB
        List<SanPham> listSP = sanPhamDAO.layTatCaSanPham(); 
        int limit = 20;
        int count = 0;

        for (SanPham sp : listSP) {
            if (count >= limit) break;
            
            double giaNhap = sp.getGiaNhap();
            
            // Tìm quy tắc áp dụng cho SP này
            ChiTietBangGia ruleMatch = null;
            for (ChiTietBangGia rule : listQuyTac) {
                if (giaNhap >= rule.getGiaTu() && giaNhap <= rule.getGiaDen()) {
                    ruleMatch = rule;
                    break;
                }
            }
            
            double tiLe = (ruleMatch != null) ? ruleMatch.getTiLe() : 0;
            double giaBan = (tiLe > 0) ? giaNhap * tiLe : 0;
            
            modelMoPhongGia.addRow(new Object[]{
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                dfTien.format(giaNhap),
                (tiLe > 0) ? tiLe : "Chưa cấu hình",
                (giaBan > 0) ? dfTien.format(giaBan) : "N/A"
            });
            
            count++;
        }
    }
    
    // --- Xuất Excel ---
    private void xuLyXuatExcel() {
    	xuLyTimKiem();
        if (modelBangGia.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setSelectedFile(new java.io.File("DanhSachBangGia_" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".xlsx")) {
            fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            // Style cho tiêu đề
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // Style cho dữ liệu
            org.apache.poi.ss.usermodel.CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // ===== SHEET 1: DANH SÁCH BẢNG GIÁ =====
            org.apache.poi.ss.usermodel.Sheet sheetBG = workbook.createSheet("Danh sách Bảng giá");

            // Tạo header
            org.apache.poi.ss.usermodel.Row headerRow = sheetBG.createRow(0);
            String[] headers = {"STT", "Mã Bảng Giá", "Tên Bảng Giá", "Ngày áp dụng", "Người lập", "Trạng thái"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu từ bảng
            for (int row = 0; row < modelBangGia.getRowCount(); row++) {
                org.apache.poi.ss.usermodel.Row dataRow = sheetBG.createRow(row + 1);
                for (int col = 0; col < modelBangGia.getColumnCount(); col++) {
                    org.apache.poi.ss.usermodel.Cell cell = dataRow.createCell(col);
                    Object value = modelBangGia.getValueAt(row, col);
                    cell.setCellValue(value != null ? value.toString() : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheetBG.autoSizeColumn(i);
            }

            // ===== SHEET 2: QUY TẮC GIÁ =====
            org.apache.poi.ss.usermodel.Sheet sheetQT = workbook.createSheet("Quy tắc định giá");

            // Header quy tắc
            org.apache.poi.ss.usermodel.Row headerRowQT = sheetQT.createRow(0);
            String[] headersQT = {"Mã Bảng Giá", "Tên Bảng Giá", "STT", "Giá nhập từ", "Giá nhập đến", "Tỉ lệ định giá", "Lợi nhuận dự kiến"};
            for (int i = 0; i < headersQT.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRowQT.createCell(i);
                cell.setCellValue(headersQT[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu quy tắc cho tất cả bảng giá
            int qtRowIdx = 1;
            for (int row = 0; row < modelBangGia.getRowCount(); row++) {
                String maBG = modelBangGia.getValueAt(row, 1).toString();
                String tenBG = modelBangGia.getValueAt(row, 2).toString();
                
                List<ChiTietBangGia> listCT = chiTietBangGiaDAO.layChiTietTheoMaBangGia(maBG);
                if (listCT != null && !listCT.isEmpty()) {
                    // Sắp xếp theo Giá Từ tăng dần
                    listCT.sort((a, b) -> Double.compare(a.getGiaTu(), b.getGiaDen()));
                    
                    int stt = 1;
                    for (ChiTietBangGia ct : listCT) {
                        org.apache.poi.ss.usermodel.Row dataRow = sheetQT.createRow(qtRowIdx++);
                        
                        double loiNhuanPhanTram = (ct.getTiLe() - 1) * 100;
                        String giaDenStr = (ct.getGiaDen() > 999999999) ? "Trở lên" : dfTien.format(ct.getGiaDen());
                        
                        dataRow.createCell(0).setCellValue(maBG);
                        dataRow.createCell(1).setCellValue(tenBG);
                        dataRow.createCell(2).setCellValue(stt++);
                        dataRow.createCell(3).setCellValue(dfTien.format(ct.getGiaTu()));
                        dataRow.createCell(4).setCellValue(giaDenStr);
                        dataRow.createCell(5).setCellValue(ct.getTiLe() + " (" + (int)(ct.getTiLe()*100) + "%)");
                        dataRow.createCell(6).setCellValue(String.format("%.0f %%", loiNhuanPhanTram));
                        
                        for (int col = 0; col < 7; col++) {
                            dataRow.getCell(col).setCellStyle(dataStyle);
                        }
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headersQT.length; i++) {
                sheetQT.autoSizeColumn(i);
            }

            // ===== SHEET 3: MÔ PHỎNG GIÁ BÁN =====
            org.apache.poi.ss.usermodel.Sheet sheetMP = workbook.createSheet("Mô phỏng giá bán");

            // Header mô phỏng
            org.apache.poi.ss.usermodel.Row headerRowMP = sheetMP.createRow(0);
            String[] headersMP = {"Mã Bảng Giá", "Tên Bảng Giá", "Mã SP", "Tên sản phẩm", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra"};
            for (int i = 0; i < headersMP.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRowMP.createCell(i);
                cell.setCellValue(headersMP[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu mô phỏng cho tất cả bảng giá
            int mpRowIdx = 1;
            List<SanPham> listSP = sanPhamDAO.layTatCaSanPham();
            int limitSP = 20; // Giới hạn 20 SP mẫu cho mỗi bảng giá
            
            for (int row = 0; row < modelBangGia.getRowCount(); row++) {
                String maBG = modelBangGia.getValueAt(row, 1).toString();
                String tenBG = modelBangGia.getValueAt(row, 2).toString();
                
                List<ChiTietBangGia> listQuyTac = chiTietBangGiaDAO.layChiTietTheoMaBangGia(maBG);
                
                if (listQuyTac != null && !listQuyTac.isEmpty()) {
                    int count = 0;
                    for (SanPham sp : listSP) {
                        if (count >= limitSP) break;
                        
                        double giaNhap = sp.getGiaNhap();
                        
                        // Tìm quy tắc áp dụng cho SP này
                        ChiTietBangGia ruleMatch = null;
                        for (ChiTietBangGia rule : listQuyTac) {
                            if (giaNhap >= rule.getGiaTu() && giaNhap <= rule.getGiaDen()) {
                                ruleMatch = rule;
                                break;
                            }
                        }
                        
                        double tiLe = (ruleMatch != null) ? ruleMatch.getTiLe() : 0;
                        double giaBan = (tiLe > 0) ? giaNhap * tiLe : 0;
                        
                        org.apache.poi.ss.usermodel.Row dataRow = sheetMP.createRow(mpRowIdx++);
                        dataRow.createCell(0).setCellValue(maBG);
                        dataRow.createCell(1).setCellValue(tenBG);
                        dataRow.createCell(2).setCellValue(sp.getMaSanPham());
                        dataRow.createCell(3).setCellValue(sp.getTenSanPham());
                        dataRow.createCell(4).setCellValue(dfTien.format(giaNhap));
                        dataRow.createCell(5).setCellValue((tiLe > 0) ? String.valueOf(tiLe) : "Chưa cấu hình");
                        dataRow.createCell(6).setCellValue((giaBan > 0) ? dfTien.format(giaBan) : "N/A");
                        
                        for (int col = 0; col < 7; col++) {
                            dataRow.getCell(col).setCellStyle(dataStyle);
                        }
                        
                        count++;
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headersMP.length; i++) {
                sheetMP.autoSizeColumn(i);
            }

            // Ghi file
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this, 
                "Xuất Excel thành công!\nFile: " + fileToSave.getAbsolutePath() + 
                "\n\nĐã xuất " + modelBangGia.getRowCount() + " bảng giá kèm đầy đủ Quy tắc và Mô phỏng giá.", 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Mở file sau khi xuất
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(fileToSave);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi xuất file Excel:\n" + e.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    // ==============================================================================
    //                                  MAIN
    // ==============================================================================
    public static void main(String[] args) {           
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Tra cứu bảng giá");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1500, 850);
                frame.setLocationRelativeTo(null);
                frame.setContentPane(new TraCuuBangGia_GUI());
                frame.setVisible(true);
            });
    }
}