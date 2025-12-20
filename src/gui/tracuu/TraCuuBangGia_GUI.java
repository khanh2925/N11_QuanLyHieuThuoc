package gui.tracuu;

import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
        addFocusOnShow(); // Tự động focus ô tìm kiếm khi hiển thị
        // Events
        addEvents();
        
        // Load data asynchronously
        SwingUtilities.invokeLater(() -> xuLyLamMoi()); 
    }

    // ==============================================================================
    //                                  UI: HEADER
    // ==============================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel();
        pnHeader.setLayout(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // --- 1. Ô TÌM KIẾM TO (Bên trái) - KHỞP VỚI BÁN HÀNG/SẢN PHẨM ---
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã bảng giá, tên bảng giá... (F1 / Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Font 20
        // Set width = 480 để bằng với bên TraCuuSanPham
        txtTimKiem.setBounds(25, 17, 480, 60); 
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimKiem);

        // --- 2. BỘ LỌC (Ở giữa) - KHỞP VỊ TRÍ COMBOX SẢN PHẨM ---
        
        // Trạng thái (Vị trí tương đương ComboBox Loại)
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18)); 
        lblTrangThai.setBounds(530, 28, 90, 35); // x=530 giống label Loại
        pnHeader.add(lblTrangThai);
        
        cbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đang hoạt động", "Ngừng hoạt động"});
        // x=620 (dịch sang phải xíu vì chữ Trạng thái dài hơn chữ Loại), Width=170
        cbTrangThai.setBounds(620, 28, 170, 38); 
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(cbTrangThai);

        // Năm (Vị trí tưƱng đương ComboBox Trạng thái)
        JLabel lblNam = new JLabel("Năm:");
        lblNam.setFont(new Font("Segoe UI", Font.PLAIN, 18)); 
        lblNam.setBounds(830, 28, 50, 35); 
        pnHeader.add(lblNam);

        // Tự động sinh năm từ 2023 đến hiện tại + 2
        int namHienTai = java.time.LocalDate.now().getYear();
        cbNam = new JComboBox<>();
        cbNam.addItem("Tất cả");
        for (int i = namHienTai - 2; i <= namHienTai + 2; i++) {
            cbNam.addItem(String.valueOf(i));
        }
        cbNam.setSelectedItem(String.valueOf(namHienTai)); 
        
        // x=890 giống ComboBox Trạng Thái, Width=180
        cbNam.setBounds(890, 28, 180, 38); 
        cbNam.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        pnHeader.add(cbNam);

        // --- 3. CÁC NÚT CHỨC NĂNG (Bên phải) - KHỞP 100% ---
        btnTimKiem = new PillButton(
                "<html>" +
                    "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                    "</center>" +
                "</html>"
            );
        btnTimKiem.setBounds(1120, 22, 130, 50);
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
        btnLamMoi.setBounds(1265, 22, 130, 50);
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
        btnXuatExcel.setBounds(1410, 22, 170, 50);
        btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        btnXuatExcel.setToolTipText("<html><b>Phím tắt:</b> Ctrl+E<br>Xuất dữ liệu ra file Excel (Danh sách, Quy tắc, Mô phỏng)</html>");
        pnHeader.add(btnXuatExcel);
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
        
        // Căn giữa cho STT, Mã, Ngày, Trạng thái
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblBangGia.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        tblBangGia.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Mã
        tblBangGia.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Ngày
        
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
        
        // Căn giữa STT
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblChiTietQuyTac.getColumnModel().getColumn(0).setCellRenderer(center); // STT
        tblChiTietQuyTac.getColumnModel().getColumn(3).setCellRenderer(center); // Tỉ lệ
        tblChiTietQuyTac.getColumnModel().getColumn(4).setCellRenderer(center); // Lợi nhuận
        
        // Căn phải cho số tiền
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblChiTietQuyTac.getColumnModel().getColumn(1).setCellRenderer(right);
        tblChiTietQuyTac.getColumnModel().getColumn(2).setCellRenderer(right);

        return new JScrollPane(tblChiTietQuyTac);
    }

    private JComponent createTabMoPhong() {
        String[] cols = {"Mã SP", "Tên sản phẩm", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra (Tính toán)"};
        modelMoPhongGia = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMoPhongGia = setupTable(modelMoPhongGia);
        
        // Căn giữa Mã SP
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblMoPhongGia.getColumnModel().getColumn(0).setCellRenderer(center); // Mã SP
        
        // Căn phải cho số tiền và tỉ lệ
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tblMoPhongGia.getColumnModel().getColumn(2).setCellRenderer(right); // Giá vốn
        tblMoPhongGia.getColumnModel().getColumn(3).setCellRenderer(right); // Tỉ lệ

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
    private void addFocusOnShow() {
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(() -> {
                	txtTimKiem.requestFocusInWindow();
                	txtTimKiem.selectAll();
                });
            }
        });
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

    // --- 2. Tìm Kiếm & Lọc (Sử dụng Cache) ---
    private void xuLyTimKiem() {
        // Đảm bảo cache đã được load
        if (dsBangGiaHienTai == null || dsBangGiaHienTai.isEmpty()) {
            dsBangGiaHienTai = bangGiaDAO.layTatCaBangGia();
        }
        
        tuKhoa = txtTimKiem.getText().trim();
        if (tuKhoa.contains("Tìm theo mã")) {
            tuKhoa = "";
        }
        
        // Lọc danh sách từ cache
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
            String tenNV = "Hệ thống"; // Mặc định
            if (bg.getNhanVien() != null && bg.getNhanVien().getTenNhanVien() != null && !bg.getNhanVien().getTenNhanVien().isEmpty()) {
                tenNV = bg.getNhanVien().getTenNhanVien();
            }
            
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
    /**
     * Xuất danh sách bảng giá ra file Excel
     * - Nếu có dòng được chọn: xuất những bảng giá đã chọn
     * - Nếu không chọn: xuất toàn bộ danh sách theo bộ lọc
     */
    private void xuLyXuatExcel() {
        // Kiểm tra xem có dòng nào được chọn không
        int[] selectedRows = tblBangGia.getSelectedRows();
        boolean coChonDong = (selectedRows != null && selectedRows.length > 0);
        
        List<BangGia> danhSachCanXuat;
        String tenFile;
        
        if (coChonDong) {
            // Xuất những bảng giá đã chọn
            danhSachCanXuat = new ArrayList<>();
            for (int row : selectedRows) {
                String maBG = tblBangGia.getValueAt(row, 1).toString();
                BangGia bg = dsBangGiaHienTai.stream()
                        .filter(b -> b.getMaBangGia().equals(maBG))
                        .findFirst()
                        .orElse(null);
                if (bg != null) {
                    danhSachCanXuat.add(bg);
                }
            }
            
            if (danhSachCanXuat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            tenFile = "BangGiaDaChon_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        } else {
            // Tự động tìm kiếm trước khi xuất để chắc chắn xuất đúng tiêu chí
            xuLyTimKiem();
            
            if (modelBangGia.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            danhSachCanXuat = new ArrayList<>(dsBangGiaHienTai);
            tenFile = "DanhSachBangGia_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
        fileChooser.setSelectedFile(new File(tenFile));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".xlsx")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            // Style cho tiêu đề
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Style cho dữ liệu
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // ===== SHEET 1: DANH SÁCH BẢNG GIÁ =====
            Sheet sheetBG = workbook.createSheet("Danh sách Bảng giá");

            // Tạo header
            Row headerRow = sheetBG.createRow(0);
            String[] headers = {"STT", "Mã Bảng Giá", "Tên Bảng Giá", "Ngày áp dụng", "Người lập", "Trạng thái"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu từ danh sách cần xuất
            int rowIdx = 1;
            for (BangGia bg : danhSachCanXuat) {
                Row dataRow = sheetBG.createRow(rowIdx);
                String tenNV = "Hệ thống";
                if (bg.getNhanVien() != null && bg.getNhanVien().getTenNhanVien() != null && !bg.getNhanVien().getTenNhanVien().isEmpty()) {
                    tenNV = bg.getNhanVien().getTenNhanVien();
                }
                
                dataRow.createCell(0).setCellValue(rowIdx);
                dataRow.createCell(1).setCellValue(bg.getMaBangGia());
                dataRow.createCell(2).setCellValue(bg.getTenBangGia());
                dataRow.createCell(3).setCellValue(dtf.format(bg.getNgayApDung()));
                dataRow.createCell(4).setCellValue(tenNV);
                dataRow.createCell(5).setCellValue(bg.isHoatDong() ? "Đang hoạt động" : "Ngừng hoạt động");
                
                for (int col = 0; col < 6; col++) {
                    dataRow.getCell(col).setCellStyle(dataStyle);
                }
                rowIdx++;
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheetBG.autoSizeColumn(i);
            }

            // ===== SHEET 2: QUY TẮC GIÁ =====
            Sheet sheetQT = workbook.createSheet("Quy tắc định giá");

            // Header quy tắc
            Row headerRowQT = sheetQT.createRow(0);
            String[] headersQT = {"Mã Bảng Giá", "Tên Bảng Giá", "STT", "Giá nhập từ", "Giá nhập đến", "Tỉ lệ định giá", "Lợi nhuận dự kiến"};
            for (int i = 0; i < headersQT.length; i++) {
                Cell cell = headerRowQT.createCell(i);
                cell.setCellValue(headersQT[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu quy tắc cho danh sách bảng giá cần xuất
            int qtRowIdx = 1;
            for (BangGia bg : danhSachCanXuat) {
                String maBG = bg.getMaBangGia();
                String tenBG = bg.getTenBangGia();
                
                List<ChiTietBangGia> listCT = chiTietBangGiaDAO.layChiTietTheoMaBangGia(maBG);
                if (listCT != null && !listCT.isEmpty()) {
                    // Sắp xếp theo Giá Từ tăng dần
                    listCT.sort((a, b) -> Double.compare(a.getGiaTu(), b.getGiaTu()));
                    
                    int stt = 1;
                    for (ChiTietBangGia ct : listCT) {
                        Row dataRow = sheetQT.createRow(qtRowIdx++);
                        
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
            Sheet sheetMP = workbook.createSheet("Mô phỏng giá bán");

            // Header mô phỏng
            Row headerRowMP = sheetMP.createRow(0);
            String[] headersMP = {"Mã Bảng Giá", "Tên Bảng Giá", "Mã SP", "Tên sản phẩm", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra"};
            for (int i = 0; i < headersMP.length; i++) {
                Cell cell = headerRowMP.createCell(i);
                cell.setCellValue(headersMP[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu mô phỏng cho danh sách bảng giá cần xuất
            int mpRowIdx = 1;
            List<SanPham> listSP = sanPhamDAO.layTatCaSanPham();
            int limitSP = 20; // Giới hạn 20 SP mẫu cho mỗi bảng giá
            
            for (BangGia bg : danhSachCanXuat) {
                String maBG = bg.getMaBangGia();
                String tenBG = bg.getTenBangGia();
                
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
                        
                        Row dataRow = sheetMP.createRow(mpRowIdx++);
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
            try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this, 
                "Xuất Excel thành công!\nFile: " + fileToSave.getAbsolutePath() + 
                "\n\nĐã xuất " + danhSachCanXuat.size() + " bảng giá kèm đầy đủ Quy tắc và Mô phỏng giá.", 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Mở file sau khi xuất
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(fileToSave);
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