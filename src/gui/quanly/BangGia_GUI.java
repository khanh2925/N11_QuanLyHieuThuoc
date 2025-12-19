package gui.quanly;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import com.toedter.calendar.JDateChooser;

// Import Entity & DAO
import dao.BangGia_DAO;
import dao.ChiTietBangGia_DAO;
import dao.SanPham_DAO;
import entity.BangGia;
import entity.ChiTietBangGia;
import entity.NhanVien;
import entity.SanPham;
import entity.Session;

/**
 * @author Quốc Khánh
 * @version 2.0 (Optimized UX: Auto-fill Range, Infinite Checkbox, Strict Validation)
 */
@SuppressWarnings("serial")
public class BangGia_GUI extends JPanel implements ActionListener,MouseListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu (Master)
    private JTextField txtMaBG, txtTenBG;
    private JDateChooser txtNgayApDung;
    private JComboBox<String> cboTrangThai;
    private JCheckBox chkHoatDong;

    // Panel Nút bấm (Master)
    private PillButton btnThem, btnSua, btnLamMoi;
    
    // Header (Tìm kiếm)
    private JTextField txtTimKiem;
    private PillButton btnTimKiem;

    // Tab 1: Danh sách Bảng Giá
    private JTable tblBangGia;
    private DefaultTableModel modelBangGia;

    // Tab 2: Chi tiết Quy tắc giá
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;
    private PillButton btnThemCT, btnSuaCT, btnXoaCT, btnLamMoiCT; 
    
    // Input nhập nhanh chi tiết (Tối ưu UX)
    private JTextField txtGiaTu, txtGiaDen, txtTiLe; 
    private JCheckBox chkKhoangCuoi; // ✅ MỚI: Checkbox "Trở lên"

    // Tab 3: Mô phỏng giá
    private JTable tblMoPhong;
    private DefaultTableModel modelMoPhong;

    // Utils & DAO
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);
    private final DecimalFormat dfTien = new DecimalFormat("#,###");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private BangGia_DAO bangGiaDAO;
    private ChiTietBangGia_DAO chiTietDAO;
    private SanPham_DAO sanPhamDAO;
    
    // Cache & Logic Variables
    private List<ChiTietBangGia> dsChiTietTam; 
    private double nextStartPrice = 0; // ✅ Biến theo dõi giá bắt đầu tiếp theo
    private int indexDangSua = -1; // ✅ Chỉ số dòng đang được sửa (-1 = không sửa)
    private boolean dangLoadDuLieu = false; // ✅ Flag để phân biệt load dữ liệu vs nhập liệu mới

    public BangGia_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // Init DAO
        bangGiaDAO = new BangGia_DAO();
        chiTietDAO = new ChiTietBangGia_DAO();
        sanPhamDAO = new SanPham_DAO();
        dsChiTietTam = new ArrayList<>();
        indexDangSua = -1;
        
        initialize();
        setupKeyboardShortcuts(); // Thiết lập phím tắt
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
        
        // Load data
        xuLyLamMoi();
    }

    // ==========================================================================
    //                              PHẦN HEADER
    // ==========================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã, tên bảng giá... (F1 / Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton(
                "<html>" +
                    "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                    "</center>" +
                "</html>"
            );
        btnTimKiem.setBounds(540, 22, 160, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên bảng giá</html>");
        btnTimKiem.addActionListener(this);
        pnHeader.add(btnTimKiem);
    }

    // ==========================================================================
    //                              PHẦN CENTER (SPLIT PANE)
    // ==========================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);

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
        splitPane.setDividerLocation(290);
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // --- FORM NHẬP LIỆU ---
    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 40;
        int hText = 35, wLbl = 120, wTxt = 300, gap = 25;

        // CỘT 1
        JLabel lblMaBG = new JLabel("Mã BG:");
        lblMaBG.setFont(FONT_TEXT);
        lblMaBG.setBounds(xStart, yStart, 120, 35);
        p.add(lblMaBG);
        
        txtMaBG = new JTextField();
        txtMaBG.setFont(FONT_TEXT);
        txtMaBG.setBounds(xStart + wLbl, yStart, wTxt, 35);
        txtMaBG.setEditable(false); 
        p.add(txtMaBG);

        JLabel lblTenBG = new JLabel("Tên BG:");
        lblTenBG.setFont(FONT_TEXT);
        lblTenBG.setBounds(xStart, yStart + (hText + gap), 120, 35);
        p.add(lblTenBG);
        
        txtTenBG = new JTextField();
        txtTenBG.setFont(FONT_TEXT);
        txtTenBG.setBounds(xStart + wLbl, yStart + (hText + gap), wTxt, 35);
        PlaceholderSupport.addPlaceholder(txtTenBG, "Nhập tên bảng giá");
        p.add(txtTenBG);

        // CHECKBOX: "Đặt làm mặc định"
        chkHoatDong = new JCheckBox("Đặt làm bảng giá mặc định (Áp dụng ngay)");
        chkHoatDong.setFont(new Font("Segoe UI", Font.BOLD, 15));
        chkHoatDong.setForeground(new Color(0, 100, 0));
        chkHoatDong.setBackground(Color.WHITE);
        chkHoatDong.setBounds(xStart + wLbl, yStart + (hText + gap)*2 + 5, 400, hText);
        chkHoatDong.setToolTipText("<html>Khi chọn: Bảng giá này sẽ hoạt động, các bảng giá khác sẽ ngừng hoạt động.<br><b>Lưu ý:</b> Chỉ có 1 bảng giá hoạt động tại một thời điểm!</html>");
        p.add(chkHoatDong);

        // CỘT 2
        int xCol2 = xStart + wLbl + wTxt + 50;

        JLabel lblNgayApDung = new JLabel("Ngày áp dụng:");
        lblNgayApDung.setFont(FONT_TEXT);
        lblNgayApDung.setBounds(xCol2, yStart, 120, 35);
        p.add(lblNgayApDung);
        
        txtNgayApDung = new JDateChooser();
        txtNgayApDung.setFont(FONT_TEXT);
        txtNgayApDung.setBounds(xCol2 + wLbl, yStart, wTxt, 35);
        txtNgayApDung.setDateFormatString("dd/MM/yyyy");
        txtNgayApDung.setDate(java.sql.Date.valueOf(LocalDate.now()));
        p.add(txtNgayApDung);

        // Trạng thái (cùng hàng với Tên BG)
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(FONT_TEXT);
        lblTrangThai.setBounds(xCol2, yStart + (hText + gap), 120, 35);
        p.add(lblTrangThai);
        
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngưng hoạt động"});
        cboTrangThai.setFont(FONT_TEXT);
        cboTrangThai.setBounds(xCol2 + wLbl, yStart + (hText + gap), wTxt, 35);
        cboTrangThai.setBackground(Color.WHITE);
        cboTrangThai.setToolTipText("<html>Chọn trạng thái bảng giá:<br>- <b>Hoạt động:</b> Bảng giá đang được sử dụng<br>- <b>Ngưng hoạt động:</b> Bảng giá không còn sử dụng</html>");
        p.add(cboTrangThai);
        
        // Đồng bộ sự kiện giữa combobox và checkbox
        cboTrangThai.addActionListener(e -> {
            if (cboTrangThai.getSelectedIndex() == 0) { // Hoạt động
                chkHoatDong.setEnabled(true);
            } else { // Ngưng hoạt động
                chkHoatDong.setSelected(false);
                chkHoatDong.setEnabled(false);
            }
        });
        
        chkHoatDong.addActionListener(e -> {
            if (chkHoatDong.isSelected()) {
                cboTrangThai.setSelectedIndex(0); // Đặt về Hoạt động
            }
        });
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

        btnThem = new PillButton(
                "<html>" +
                    "<center>" +
                        "TẠO MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" +
                    "</center>" +
                "</html>");
        btnThem.setFont(FONT_BOLD);
        btnThem.setPreferredSize(new Dimension(btnW, btnH));
        btnThem.addActionListener(this);
        btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Tạo bảng giá mới (sẽ hỏi xác nhận nếu đang nhập dở)</html>");
        btnThem.setEnabled(true); // ✅ Luôn mở, chỉ khóa khi chọn dòng
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = new PillButton(
                "<html>" +
                    "<center>" +
                        "CẬP NHẬT<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" +
                    "</center>" +
                "</html>");
        btnSua.setFont(FONT_BOLD);
        btnSua.setPreferredSize(new Dimension(btnW, btnH));
        btnSua.addActionListener(this);
        btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật bảng giá đang chọn (phải chọn bảng giá trước)</html>");
        btnSua.setEnabled(false); // ✅ Ban đầu không cho chọn
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnLamMoi = new PillButton(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                    "</center>" +
                "</html>");
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
        btnLamMoi.addActionListener(this);
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Xóa form và làm mới (sẽ hỏi xác nhận nếu đang nhập dở)</html>");
        gbc.gridy = 2; p.add(btnLamMoi, gbc);
    }

    // --- TAB 1: DANH SÁCH ---
    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"STT", "Mã Bảng Giá", "Tên Bảng Giá", "Ngày áp dụng", "Người lập", "Trạng thái"};
        modelBangGia = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBangGia = setupTable(modelBangGia);

        // Căn chỉnh cột theo quy chuẩn
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        
        // STT: Giữa (cột 0 - đã có từ setupTable)
        tblBangGia.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Mã Bảng Giá: Giữa (mã)
        tblBangGia.getColumnModel().getColumn(2).setCellRenderer(leftRenderer); // Tên Bảng Giá: Trái (văn bản)
        tblBangGia.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Ngày áp dụng: Giữa
        tblBangGia.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Người lập: Giữa (mã)

        tblBangGia.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if("Đang hoạt động".equals(value)) {
                    lbl.setForeground(new Color(0, 128, 0)); // Xanh lá đậm
                    lbl.setFont(FONT_BOLD);
                    lbl.setText("✔ ĐANG HOẠT ĐỘNG");
                } else {
                    lbl.setForeground(Color.GRAY);
                    lbl.setFont(FONT_TEXT);
                }
                return lbl;
            }
        });

        // ✅ Sử dụng MouseListener interface
        tblBangGia.addMouseListener(this);
        
        // ✅ Thêm ListSelectionListener để enable btnSua khi chọn dòng
        tblBangGia.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblBangGia.getSelectedRow();
                if (row >= 0) {
                    btnSua.setEnabled(true);
                    btnThem.setEnabled(false); // Khóa btnThem khi đang chọn dòng
                } else {
                    btnSua.setEnabled(false);
                    btnThem.setEnabled(true); // Mở lại btnThem khi bỏ chọn
                }
            }
        });

        JScrollPane scr = new JScrollPane(tblBangGia);
        scr.setBorder(BorderFactory.createEmptyBorder());
        p.add(scr, BorderLayout.CENTER);
    }

    // --- TAB 2: CHI TIẾT QUY TẮC (ĐÃ TỐI ƯU UX) ---
    private void taoBangChiTiet(JPanel p) {
        JPanel pnToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnToolBar.setBackground(Color.WHITE);
        pnToolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // 1. Giá từ (Read Only - Tự động nhảy)
        pnToolBar.add(new JLabel("Giá từ (Auto):"));
        txtGiaTu = new JTextField(10); 
        txtGiaTu.setEditable(false); // ✅ Không cho sửa
        txtGiaTu.setBackground(new Color(245, 245, 245));
        txtGiaTu.setFont(FONT_BOLD);
        txtGiaTu.setForeground(Color.BLUE);
        pnToolBar.add(txtGiaTu);
        
        // 2. Giá đến
        pnToolBar.add(new JLabel("Đến:"));
        txtGiaDen = new JTextField(10);
        PlaceholderSupport.addPlaceholder(txtGiaDen, "Nhập giá đến (F3)");
        txtGiaDen.setToolTipText("<html><b>Phím tắt:</b> F3<br>Nhập giá kết thúc của khoảng (VD: 100000)<br>Nhấn Enter để nhảy sang Tỉ lệ</html>");
        pnToolBar.add(txtGiaDen);
        
        // 3. Checkbox "Trở lên"
        chkKhoangCuoi = new JCheckBox("Trở lên (Cuối)");
        chkKhoangCuoi.setBackground(Color.WHITE);
        chkKhoangCuoi.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        chkKhoangCuoi.setToolTipText("<html><b>Phím tắt:</b> F6<br>Tích vào nếu đây là khoảng giá cuối cùng (từ X trở lên)</html>");
        // Logic UX: Chọn "Trở lên" thì vô hiệu hóa ô "Đến"
        chkKhoangCuoi.addActionListener(e -> {
            if (chkKhoangCuoi.isSelected()) {
                txtGiaDen.setText("∞");
                txtGiaDen.setEnabled(false);
            } else {
                txtGiaDen.setText("");
                txtGiaDen.setEnabled(true);
                txtGiaDen.requestFocus();
            }
        });
        pnToolBar.add(chkKhoangCuoi);
        
     // 4. Tỉ lệ
        pnToolBar.add(new JLabel("Tỉ lệ (VD 1.2):"));
        txtTiLe = new JTextField(7);
        PlaceholderSupport.addPlaceholder(txtTiLe, "Nhập tỉ lệ (F4)");
        txtTiLe.setToolTipText("<html><b>Phím tắt:</b> F4<br>Nhập tỉ lệ định giá (VD: 1.2 = bán gấp 1.2 lần giá vốn)<br>Nhấn Enter để thêm quy tắc vào bảng</html>");
        pnToolBar.add(txtTiLe);

        // 5. Nút Thêm (F7)
        btnThemCT = new PillButton(
            "<html>" +
                "<center>" +
                    "THÊM QUY TẮC<br>" +
                    "<span style='font-size:10px; color:#888888;'>(F7)</span>" +
                "</center>" +
            "</html>");
        btnThemCT.setFont(FONT_BOLD);
        btnThemCT.setPreferredSize(new Dimension(160, 35));
        btnThemCT.setToolTipText("<html><b>Phím tắt:</b> F7<br>Thêm quy tắc giá vào bảng tạm<br>(Giá Từ sẽ tự động nhảy)</html>");
        btnThemCT.addActionListener(this);
        pnToolBar.add(btnThemCT);
        
        // 6. Nút Xóa (F8)
        btnXoaCT = new PillButton(
            "<html>" +
                "<center>" +
                    "XÓA DÒNG CUỐI<br>" +
                    "<span style='font-size:10px; color:#888888;'>(F8)</span>" +
                "</center>" +
            "</html>");
        btnXoaCT.setFont(FONT_BOLD);
        btnXoaCT.setPreferredSize(new Dimension(170, 35));
        btnXoaCT.setToolTipText("<html><b>Phím tắt:</b> F8<br>Xóa dòng cuối cùng trong bảng quy tắc</html>");
        btnXoaCT.addActionListener(this);
        pnToolBar.add(btnXoaCT);
        
        // 7. Nút Làm mới (F9)
        btnLamMoiCT = new PillButton(
            "<html>" +
                "<center>" +
                    "LÀM MỚI<br>" +
                    "<span style='font-size:10px; color:#888888;'>(F9)</span>" +
                "</center>" +
            "</html>");
        btnLamMoiCT.setFont(FONT_BOLD);
        btnLamMoiCT.setPreferredSize(new Dimension(150, 35));
        btnLamMoiCT.setToolTipText("<html><b>Phím tắt:</b> F9<br>Làm mới các ô nhập liệu<br>Để tiếp tục thêm quy tắc mới</html>");
        btnLamMoiCT.addActionListener(ev -> {
            xuLyLamMoiFormChiTiet();
        });
        pnToolBar.add(btnLamMoiCT);
        
        // 8. Nút Sửa (F10)
        btnSuaCT = new PillButton(
            "<html>" +
                "<center>" +
                    "SỬA QUY TẮC<br>" +
                    "<span style='font-size:10px; color:#888888;'>(F10)</span>" +
                "</center>" +
            "</html>");
        btnSuaCT.setFont(FONT_BOLD);
        btnSuaCT.setPreferredSize(new Dimension(160, 35));
        btnSuaCT.setToolTipText("<html><b>Phím tắt:</b> F10<br>Sửa quy tắc giá đang chọn<br>(Chọn 1 dòng trong bảng rồi nhấn nút này)</html>");
        btnSuaCT.addActionListener(this);
        pnToolBar.add(btnSuaCT);
        
        p.add(pnToolBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"STT", "Giá nhập từ", "Giá nhập đến", "Tỉ lệ định giá", "Lợi nhuận dự kiến"};
        modelChiTiet = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblChiTiet = setupTable(modelChiTiet);
        
        // Căn chỉnh cột theo quy chuẩn
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        // STT: Giữa (cột 0 - đã có từ setupTable)
        tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(rightRenderer); // Giá nhập từ: Phải (tiền tệ)
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Giá nhập đến: Phải (tiền tệ)
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Tỉ lệ định giá: Phải (số)
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Lợi nhuận dự kiến: Phải (%)
        
        JScrollPane scr = new JScrollPane(tblChiTiet);
        scr.setBorder(BorderFactory.createEmptyBorder());
        p.add(scr, BorderLayout.CENTER);
        
        // ✅ XÓA HOÀN TOÀN F8 khỏi bảng (dùng remove thay vì put "none")
        tblChiTiet.getInputMap(JComponent.WHEN_FOCUSED).remove(KeyStroke.getKeyStroke("F8"));
        tblChiTiet.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(KeyStroke.getKeyStroke("F8"));
        
        // ✅ THÊM PHÍM TẮT TRỰC TIẾP VÀO PANEL TAB CHI TIẾT
        InputMap tabInputMap = p.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap tabActionMap = p.getActionMap();
        
        // F6: Toggle checkbox "Trở lên"
        tabInputMap.put(KeyStroke.getKeyStroke("F6"), "toggleKhoangCuoiTab");
        tabActionMap.put("toggleKhoangCuoiTab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chkKhoangCuoi.isEnabled()) {
                    chkKhoangCuoi.doClick();
                }
            }
        });
        
        // F7: Thêm chi tiết
        tabInputMap.put(KeyStroke.getKeyStroke("F7"), "themChiTietTab");
        tabActionMap.put("themChiTietTab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnThemCT.isEnabled()) {
                    xuLyThemChiTietVaoBangTam();
                }
            }
        });
        
        // F8: Xóa dòng cuối
        tabInputMap.put(KeyStroke.getKeyStroke("F8"), "xoaDongCuoiTab");
        tabActionMap.put("xoaDongCuoiTab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyXoaChiTietKhoiBangTam();
            }
        });
        
        // F9: Làm mới form chi tiết
        tabInputMap.put(KeyStroke.getKeyStroke("F9"), "lamMoiFormTab");
        tabActionMap.put("lamMoiFormTab", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyLamMoiFormChiTiet();
            }
        });
        
        // ✅ Sử dụng MouseListener interface
        tblChiTiet.addMouseListener(this);
    }
    
    // --- TAB 3: MÔ PHỎNG ---
    private void taoBangMoPhong(JPanel p) {
        String[] cols = {"STT", "Mã SP", "Tên thuốc mẫu", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra (Tính toán)"};
        modelMoPhong = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMoPhong = setupTable(modelMoPhong);
        
        // Căn chỉnh cột theo quy chuẩn
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        // STT: Giữa (cột 0 - đã có từ setupTable)
        tblMoPhong.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Mã SP: Giữa (mã)
        tblMoPhong.getColumnModel().getColumn(2).setCellRenderer(leftRenderer); // Tên thuốc mẫu: Trái (văn bản)
        tblMoPhong.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Giá nhập (Vốn): Phải (tiền tệ)
        tblMoPhong.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Tỉ lệ áp dụng: Phải (số)
        
        tblMoPhong.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
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
    //                              XỬ LÝ SỰ KIỆN
    // ==========================================================================
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o.equals(btnThem)) {
            xuLyThemBangGia();
        } else if (o.equals(btnSua)) {
            xuLyCapNhatBangGia();
        } else if (o.equals(btnLamMoi)) {
            xuLyLamMoi();
        } else if (o.equals(btnTimKiem)) {
            xuLyTimKiem();
        } else if (o.equals(btnThemCT)) {
            xuLyThemChiTietVaoBangTam();
        } else if (o.equals(btnSuaCT)) {
            xuLySuaChiTiet();
        } else if (o.equals(btnXoaCT)) {
            xuLyXoaChiTietKhoiBangTam();
        }
    }

    // --- 1. THÊM BẢNG GIÁ ---
    private void xuLyThemBangGia() {
        if (!validInput()) return;
        String maBG = txtMaBG.getText().trim();
        if (bangGiaDAO.timBangGiaTheoMa(maBG) != null) {
            JOptionPane.showMessageDialog(this,
                "Mã bảng giá đã tồn tại!\nVui lòng làm mới để tạo mã mới.",
                "Trùng mã bảng giá",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        BangGia bgThat = taoBangGiaTuForm(); 
        if (bgThat == null) return;

        // Transaction: Thêm BG -> Thêm Chi tiết
        if (bangGiaDAO.themBangGia(bgThat)) {
            
            if (bgThat.isHoatDong()) {
                bangGiaDAO.huyHoatDongTatCaTruBangGia(bgThat.getMaBangGia());
            }

            for (ChiTietBangGia ct : dsChiTietTam) {
                ct.setBangGia(bgThat); // Gán BG thật
                chiTietDAO.themChiTietBangGia(ct);
            }

            JOptionPane.showMessageDialog(this, "Thêm bảng giá thành công!");
            xuLyLamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm bảng giá!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- 2. CẬP NHẬT BẢNG GIÁ ---
    private void xuLyCapNhatBangGia() {
        // Cập nhật chỉ cho sửa tên, trạng thái, và làm lại chi tiết
        // Mã BG không đổi
        if (!validInput()) return;
        BangGia bgThat = taoBangGiaTuForm();
        if (bgThat == null) return;
        
        if (bangGiaDAO.capNhatBangGia(bgThat)) {
            if (bgThat.isHoatDong()) {
                bangGiaDAO.huyHoatDongTatCaTruBangGia(bgThat.getMaBangGia());
            }
            
            chiTietDAO.xoaChiTietTheoMaBangGia(bgThat.getMaBangGia());
            for (ChiTietBangGia ct : dsChiTietTam) {
                ct.setBangGia(bgThat);
                chiTietDAO.themChiTietBangGia(ct);
            }

            JOptionPane.showMessageDialog(this, "Cập nhật bảng giá thành công!");
            xuLyLamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- 3. LÀM MỚI & RESET ---
    private void xuLyLamMoi() {
        txtMaBG.setText(bangGiaDAO.taoMaBangGia());
        txtTenBG.setText("");
        PlaceholderSupport.addPlaceholder(txtTenBG, "Nhập tên bảng giá (F2)");
        txtNgayApDung.setDate(java.sql.Date.valueOf(LocalDate.now()));
        cboTrangThai.setSelectedIndex(0); // Mặc định: Hoạt động
        chkHoatDong.setSelected(true);
        chkHoatDong.setEnabled(false);
        
        // ✅ Reset trạng thái nút
        btnThem.setEnabled(true); // Luôn mở khi làm mới
        btnSua.setEnabled(false);
        tblBangGia.clearSelection(); // Bỏ chọn dòng trong bảng
        
        // ✅ BỔ SUNG: Reset ô tìm kiếm
        txtTimKiem.setText(""); 
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã, tên bảng giá... (F1 / Ctrl+F)");
        // Reset quy trình nhập chi tiết
        resetInputChiTiet();
        dsChiTietTam.clear();
        renderBangChiTiet(dsChiTietTam);
        modelMoPhong.setRowCount(0); 
        
        List<BangGia> list = bangGiaDAO.layTatCaBangGia();
        renderDanhSachBangGia(list);
    }
    
    /** ✅ Reset form nhập chi tiết về trạng thái ban đầu */
    private void resetInputChiTiet() {
        nextStartPrice = 0;
        txtGiaTu.setText("0");
        txtGiaDen.setText("");
        txtGiaDen.setEnabled(true);
        txtTiLe.setText("");
        chkKhoangCuoi.setSelected(false);
        chkKhoangCuoi.setEnabled(true);
        btnThemCT.setEnabled(true);
    }

    // --- 5. TÌM KIẾM ---
    private void xuLyTimKiem() {
        String tuKhoa = txtTimKiem.getText().trim();
        List<BangGia> list = bangGiaDAO.layTatCaBangGia();
        List<BangGia> ketQua = new ArrayList<>();
        
        // Nếu từ khóa rỗng, hiển thị tất cả
        if (tuKhoa.isEmpty()) {
            renderDanhSachBangGia(list);
            return;
        }

        for(BangGia bg : list) {
            if(bg.getMaBangGia().toLowerCase().contains(tuKhoa.toLowerCase()) ||
               bg.getTenBangGia().toLowerCase().contains(tuKhoa.toLowerCase())) {
                ketQua.add(bg);
            }
        }
        
        renderDanhSachBangGia(ketQua); // Render kết quả (có thể rỗng)

        // ✅ BỔ SUNG: Kiểm tra và thông báo nếu không tìm thấy
        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy Bảng Giá nào phù hợp với từ khóa: '" + tuKhoa + "'", 
                "Không tìm thấy", JOptionPane.INFORMATION_MESSAGE);
            xuLyLamMoi();
        }
    }

    // --- 6. XỬ LÝ SỬA CHI TIẾT ---
    private void xuLySuaChiTiet() {
        int selectedRow = tblChiTiet.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn 1 quy tắc trong bảng để sửa!",
                "Chưa chọn quy tắc", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedRow >= dsChiTietTam.size()) {
            JOptionPane.showMessageDialog(this, "Dòng được chọn không hợp lệ!");
            return;
        }

        // Load thông tin lên form
        ChiTietBangGia ct = dsChiTietTam.get(selectedRow);
        txtGiaTu.setText(dfTien.format(ct.getGiaTu()));
        
        if (ct.getGiaDen() == Double.MAX_VALUE) {
            txtGiaDen.setText("∞");
            txtGiaDen.setEnabled(false);
            chkKhoangCuoi.setSelected(true);
        } else {
            txtGiaDen.setText(dfTien.format(ct.getGiaDen()));
            txtGiaDen.setEnabled(false); // Không cho sửa giá đến
            chkKhoangCuoi.setSelected(false);
        }
        
        txtTiLe.setText(String.valueOf(ct.getTiLe()));
        txtTiLe.setEnabled(true);
        txtTiLe.requestFocus();
        txtTiLe.selectAll();
        
        // Đánh dấu đang ở chế độ sửa
        indexDangSua = selectedRow;
        
        // Thay đổi text của nút Thêm thành Lưu
        btnThemCT.setText(
            "<html>" +
                "<center>" +
                    "LƯU SỬA ĐỔI<br>" +
                    "<span style='font-size:10px; color:#888888;'>(F7)</span>" +
                "</center>" +
            "</html>");
        btnThemCT.setToolTipText("<html><b>Phím tắt:</b> F7<br>Lưu thay đổi vào quy tắc đang sửa</html>");
        
        // Vô hiệu hóa các nút khác
        btnSuaCT.setEnabled(false);
        btnXoaCT.setEnabled(false);
        chkKhoangCuoi.setEnabled(false);
        
        // Đổi text nút Làm mới thành Hủy sửa đổi
        btnLamMoiCT.setText(
            "<html>" +
                "<center>" +
                    "HỦY SỬA ĐỔI<br>" +
                    "<span style='font-size:10px; color:#888888;'>(F9)</span>" +
                "</center>" +
            "</html>");
        btnLamMoiCT.setToolTipText("<html><b>Phím tắt:</b> F9<br>Hủy chế độ sửa và quay lại nhập liệu bình thường</html>");
        
        JOptionPane.showMessageDialog(this,
            "Bạn đang ở chế độ SỬA quy tắc thứ " + (selectedRow + 1) + "\n" +
            "- Chỉ có thể sửa TỈ LỆ (Giá từ/đến không được thay đổi để giữ tính liên tục)\n" +
            "- Nhấn 'LƯU SỬA ĐỔI' (F7) để lưu\n" +
            "- Nhấn 'HỦY SỬA ĐỔI' (F9) để hủy",
            "Chế độ sửa",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // --- 7. XỬ LÝ CHI TIẾT (TỐI ƯU UX) ---
    private void xuLyThemChiTietVaoBangTam() {
        try {
            // Kiểm tra xem có đang ở chế độ sửa không
            if (indexDangSua >= 0) {
                xuLyLuuSuaDoiChiTiet();
                return;
            }
            
            // 1. Lấy giá từ (Auto)
            double giaTu = nextStartPrice;
            
            // 2. Lấy giá đến
            double giaDen = 0;
            if (chkKhoangCuoi.isSelected()) {
                giaDen = Double.MAX_VALUE; // Vô cực
            } else {
                String strDen = txtGiaDen.getText().replace(",", "").trim();
                if (strDen.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập giá đến (hoặc chọn 'Trở lên')");
                    txtGiaDen.requestFocus();
                    return;
                }
                giaDen = Double.parseDouble(strDen);
            }

            // 3. Lấy tỉ lệ
            String strTiLe = txtTiLe.getText().replace(",", "").trim();
            if (strTiLe.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tỉ lệ.");
                txtTiLe.requestFocus();
                return;
            }
            double tiLe = Double.parseDouble(strTiLe);

            // 4. Validate Logic
            if (giaDen <= giaTu) {
                JOptionPane.showMessageDialog(this, "Giá đến phải lớn hơn giá từ (" + dfTien.format(giaTu) + ").");
                return;
            }
            if (tiLe <= 0) {
                JOptionPane.showMessageDialog(this, "Tỉ lệ phải lớn hơn 0.");
                return;
            }
            
            // 4.1. Validate: Giá nhập càng cao thì tỉ lệ lợi nhuận càng thấp
            if (!dsChiTietTam.isEmpty()) {
                ChiTietBangGia lastRule = dsChiTietTam.get(dsChiTietTam.size() - 1);
                if (tiLe >= lastRule.getTiLe()) {
                    JOptionPane.showMessageDialog(this, 
                        "Tỉ lệ mới (" + tiLe + ") phải nhỏ hơn tỉ lệ quy tắc trước (" + lastRule.getTiLe() + ").\n" +
                        "Giá nhập càng cao thì lợi nhuận càng thấp.",
                        "Tỉ lệ không hợp lệ", 
                        JOptionPane.WARNING_MESSAGE);
                    txtTiLe.requestFocus();
                    txtTiLe.selectAll();
                    return;
                }
            }

            // 5. Tạo đối tượng tạm (Dùng Dummy BangGia để qua validation)
            BangGia bgDummy = new BangGia("BG-00000000-0000");
            ChiTietBangGia ct = new ChiTietBangGia(bgDummy, giaTu, giaDen, tiLe);
            dsChiTietTam.add(ct);
            
            // 6. Cập nhật UI & Biến tiếp theo
            renderBangChiTiet(dsChiTietTam);
            renderBangMoPhong(dsChiTietTam);
            
            // Chuẩn bị cho vòng lặp kế tiếp
            if (giaDen == Double.MAX_VALUE) {
                // Đã là khoảng cuối -> Khóa nhập liệu
                btnThemCT.setEnabled(false);
                txtGiaTu.setText("---");
                txtGiaDen.setText("---");
                txtGiaDen.setEnabled(false);
                chkKhoangCuoi.setEnabled(false);
                txtTiLe.setEnabled(false);
            } else {
                // Cập nhật Giá Từ cho dòng tiếp theo = Giá Đến cũ + 1
                nextStartPrice = giaDen + 1;
                txtGiaTu.setText(dfTien.format(nextStartPrice));
                txtGiaDen.setText("");
                txtTiLe.setText("");
                txtGiaDen.requestFocus();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    /** ✅ Lưu sửa đổi tỉ lệ của quy tắc đang sửa */
    private void xuLyLuuSuaDoiChiTiet() {
        try {
            // Lấy tỉ lệ mới
            String strTiLe = txtTiLe.getText().replace(",", "").trim();
            if (strTiLe.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tỉ lệ.");
                txtTiLe.requestFocus();
                return;
            }
            double tiLeMoi = Double.parseDouble(strTiLe);

            if (tiLeMoi <= 0) {
                JOptionPane.showMessageDialog(this, "Tỉ lệ phải lớn hơn 0.");
                return;
            }

            // Validate: Tỉ lệ phải nhỏ hơn quy tắc trước và lớn hơn quy tắc sau
            if (indexDangSua > 0) {
                double tiLeTruoc = dsChiTietTam.get(indexDangSua - 1).getTiLe();
                if (tiLeMoi >= tiLeTruoc) {
                    JOptionPane.showMessageDialog(this,
                        "Tỉ lệ mới (" + tiLeMoi + ") phải nhỏ hơn tỉ lệ quy tắc trước (" + tiLeTruoc + ")\n" +
                        "Giá nhập càng cao thì lợi nhuận càng thấp.",
                        "Tỉ lệ không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            if (indexDangSua < dsChiTietTam.size() - 1) {
                double tiLeSau = dsChiTietTam.get(indexDangSua + 1).getTiLe();
                if (tiLeMoi <= tiLeSau) {
                    JOptionPane.showMessageDialog(this,
                        "Tỉ lệ mới (" + tiLeMoi + ") phải lớn hơn tỉ lệ quy tắc sau (" + tiLeSau + ")\n" +
                        "Giá nhập càng cao thì lợi nhuận càng thấp.",
                        "Tỉ lệ không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Cập nhật tỉ lệ
            ChiTietBangGia ctDangSua = dsChiTietTam.get(indexDangSua);
            ctDangSua.setTiLe(tiLeMoi);

            // Reset chế độ sửa
            indexDangSua = -1;
            btnThemCT.setText(
                "<html>" +
                    "<center>" +
                        "THÊM QUY TẮC<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F7)</span>" +
                    "</center>" +
                "</html>");
            btnThemCT.setToolTipText("<html><b>Phím tắt:</b> F7<br>Thêm quy tắc giá vào bảng tạm<br>(Giá Từ sẽ tự động nhảy)</html>");
            btnSuaCT.setEnabled(true);
            btnXoaCT.setEnabled(true);
            
            // Đổi lại text nút Làm mới về ban đầu
            btnLamMoiCT.setText(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F9)</span>" +
                    "</center>" +
                "</html>");
            btnLamMoiCT.setToolTipText("<html><b>Phím tắt:</b> F9<br>Làm mới các ô nhập liệu<br>Để tiếp tục thêm quy tắc mới</html>");

            // Render lại bảng
            renderBangChiTiet(dsChiTietTam);
            renderBangMoPhong(dsChiTietTam);

            // Reset form input (không xóa dữ liệu)
            resetInputFormSauKhiLuu();

            JOptionPane.showMessageDialog(this, "Cập nhật tỉ lệ thành công!");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void xuLyXoaChiTietKhoiBangTam() {
        // Logic xóa kiểu Stack (chỉ được xóa dòng cuối cùng) để bảo toàn tính liên tục
        if (dsChiTietTam.isEmpty()) return;

        int lastIndex = dsChiTietTam.size() - 1;
        ChiTietBangGia deletedItem = dsChiTietTam.remove(lastIndex);
        
        // Khôi phục lại trạng thái nhập liệu
        nextStartPrice = deletedItem.getGiaTu();
        txtGiaTu.setText(dfTien.format(nextStartPrice));
        
        // Mở khóa lại các nút (nếu lỡ đã thêm dòng vô cực)
        btnThemCT.setEnabled(true);
        txtGiaDen.setEnabled(true);
        txtGiaDen.setText("");
        chkKhoangCuoi.setEnabled(true);
        chkKhoangCuoi.setSelected(false);
        txtTiLe.setEnabled(true);
        txtTiLe.setText("");
        
        renderBangChiTiet(dsChiTietTam);
        renderBangMoPhong(dsChiTietTam);
    }

    // ==========================================================================
    //                              RENDER DATA
    // ==========================================================================

    private void renderDanhSachBangGia(List<BangGia> list) {
        modelBangGia.setRowCount(0);
        int stt = 1;
        for (BangGia bg : list) {
            String tenNV = bg.getNhanVien() != null ? bg.getNhanVien().getMaNhanVien() : "N/A";
            
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

    private void renderBangChiTiet(List<ChiTietBangGia> list) {
        modelChiTiet.setRowCount(0);
        int stt = 1;
        // Không cần sort vì mình add tuần tự, nhưng sort cho chắc
        list.sort((a, b) -> Double.compare(a.getGiaTu(), b.getGiaTu()));
        
        for (ChiTietBangGia ct : list) {
            double loiNhuan = (ct.getTiLe() - 1) * 100;
            
            String giaDenStr = (ct.getGiaDen() == Double.MAX_VALUE) 
                    ? "Trở lên (∞)" 
                    : dfTien.format(ct.getGiaDen());

            modelChiTiet.addRow(new Object[]{
                stt++,
                dfTien.format(ct.getGiaTu()),
                giaDenStr,
                ct.getTiLe(),
                String.format("%.0f%%", loiNhuan)
            });
        }
    }

    private void renderBangMoPhong(List<ChiTietBangGia> listRules) {
        modelMoPhong.setRowCount(0);
        List<SanPham> listSP = sanPhamDAO.layTatCaSanPham(); 
        int count = 0;
        int stt = 1;
        for(SanPham sp : listSP) {
            if(count++ > 15) break; 
            
            double giaVon = sp.getGiaNhap();
            double tiLe = 0;
            
            for(ChiTietBangGia rule : listRules) {
                if(giaVon >= rule.getGiaTu() && giaVon <= rule.getGiaDen()) {
                    tiLe = rule.getTiLe();
                    break;
                }
            }
            
            modelMoPhong.addRow(new Object[]{
                stt++,
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                dfTien.format(giaVon),
                tiLe > 0 ? tiLe : "Chưa có",
                tiLe > 0 ? dfTien.format(giaVon * tiLe) : "N/A"
            });
        }
    }

    private void loadBangGiaLenForm(String maBG) {
        BangGia bg = bangGiaDAO.timBangGiaTheoMa(maBG);
        if (bg != null) {
            // ✅ Set flag để ngăn DocumentListener làm sai logic nút
            dangLoadDuLieu = true;
            
            txtMaBG.setText(bg.getMaBangGia());
            txtTenBG.setText(bg.getTenBangGia());
            txtTenBG.setForeground(Color.BLACK); // Đảm bảo chữ màu đen, không phải placeholder
            txtNgayApDung.setDate(java.sql.Date.valueOf(bg.getNgayApDung()));
            cboTrangThai.setSelectedIndex(bg.isHoatDong() ? 0 : 1); // 0: Hoạt động, 1: Ngưng hoạt động
            chkHoatDong.setSelected(bg.isHoatDong());
            
            // Load và reset quy trình nhập liệu
            dsChiTietTam = chiTietDAO.layChiTietTheoMaBangGia(maBG);
            
            // Logic để set nextStartPrice cho đúng khi load lại
            if (!dsChiTietTam.isEmpty()) {
                ChiTietBangGia last = dsChiTietTam.get(dsChiTietTam.size() - 1);
                if (last.getGiaDen() == Double.MAX_VALUE) {
                    // Đã full, khóa nhập
                    nextStartPrice = -1; 
                    btnThemCT.setEnabled(false);
                    chkHoatDong.setEnabled(!bg.isHoatDong());
                    txtGiaTu.setText("---");
                } else {
                    nextStartPrice = last.getGiaDen() + 1;
                    txtGiaTu.setText(dfTien.format(nextStartPrice));
                    btnThemCT.setEnabled(true);
                }
            } else {
                resetInputChiTiet();
            }

            renderBangChiTiet(dsChiTietTam);
            renderBangMoPhong(dsChiTietTam);
            
            // ✅ Tắt flag và đảm bảo btnSua enabled (vì đang ở chế độ sửa)
            dangLoadDuLieu = false;
            btnSua.setEnabled(true);
            // btnThem sẽ tự động bị disable bởi ListSelectionListener khi dòng được chọn
        }
    }

    private BangGia taoBangGiaTuForm() {
        String ma = txtMaBG.getText().trim();
        String ten = txtTenBG.getText().trim();
        // Lấy trạng thái từ combobox: 0 = Hoạt động, 1 = Ngưng hoạt động
        boolean active = cboTrangThai.getSelectedIndex() == 0;
        
        // Nếu checkbox được chọn thì ưu tiên checkbox (đặt làm mặc định)
        if (chkHoatDong.isSelected()) {
            active = true;
        }
        
        try {
            if (txtNgayApDung.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày áp dụng.");
                return null;
            }
            
            // Chuyển đổi Date sang LocalDate an toàn hơn
            java.util.Date date = txtNgayApDung.getDate();
            LocalDate ngay;
            if (date instanceof java.sql.Date) {
                ngay = ((java.sql.Date) date).toLocalDate();
            } else {
                ngay = date.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
            }
            
            NhanVien nv = null;
            try {
                nv = Session.getInstance().getTaiKhoanDangNhap().getNhanVien();
            } catch (Exception e) {
                nv = new NhanVien("NV0001"); 
            }
            return new BangGia(ma, nv, ten, ngay, active);
        } catch (Exception e) {
            e.printStackTrace(); // In ra console để debug
            JOptionPane.showMessageDialog(this, 
                "Lỗi: " + e.getMessage() + "\nVui lòng kiểm tra lại dữ liệu.", 
                "Lỗi dữ liệu", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /** ✅ VALIDATION: Bắt buộc 3 quy tắc, không bắt buộc phải có vô cực */
    private boolean validInput() {
        String tenBG = txtTenBG.getText().trim();
        if (tenBG.isEmpty() || tenBG.equals("Nhập tên bảng giá (F2)")) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên bảng giá.");
            txtTenBG.requestFocus();
            txtTenBG.selectAll();
            return false;
        }
        
        // Kiểm tra số lượng quy tắc
        if (dsChiTietTam.size() < 3) {
            JOptionPane.showMessageDialog(this, "Bảng giá phải có tối thiểu 3 khoảng giá (Quy tắc).");
            return false;
        }

        return true;
    }
    private void xuLyClickDongChiTiet(int row) {
        if (dsChiTietTam.isEmpty() || row < 0 || row >= dsChiTietTam.size()) return;

        ChiTietBangGia ct = dsChiTietTam.get(row);
        
        // Hiển thị thông tin
        txtGiaTu.setText(dfTien.format(ct.getGiaTu()));
        txtTiLe.setText(String.valueOf(ct.getTiLe()));

        if (ct.getGiaDen() == Double.MAX_VALUE) {
            txtGiaDen.setText("∞");
            chkKhoangCuoi.setSelected(true);
            // Khóa tất cả các ô input vì đây là dòng cuối
            txtGiaDen.setEnabled(false); 
            txtTiLe.setEnabled(false);
            chkKhoangCuoi.setEnabled(false);
            btnThemCT.setEnabled(false);
        } else {
            txtGiaDen.setText(dfTien.format(ct.getGiaDen()));
            chkKhoangCuoi.setSelected(false);
            // Vẫn giữ trạng thái input cho dòng tiếp theo (nếu dòng được click không phải dòng cuối)
            // Tuy nhiên, vì mục đích là chỉ xem, ta chỉ cần hiển thị
            
            // Cảnh báo nếu không phải dòng cuối
            if (row != dsChiTietTam.size() - 1) {
                 JOptionPane.showMessageDialog(this, 
                    "⚠️ Lưu ý: Để chỉnh sửa quy tắc này, bạn cần xóa quy tắc cuối cùng (Xóa dòng cuối) trước.", 
                    "Xem Quy Tắc", JOptionPane.WARNING_MESSAGE);
            }
            
            // Sau khi click xem, ta reset lại trạng thái nhập liệu về dòng tiếp theo (nếu có)
            // Hoặc giữ nguyên trạng thái đang nhập dở
            if (row == dsChiTietTam.size() - 1 && nextStartPrice > 0) {
                // Nếu click vào dòng cuối (và nó chưa phải vô cực) -> có thể muốn sửa
                // Giữ nguyên input để cho người dùng quyết định
            } else if (dsChiTietTam.size() > 0 && dsChiTietTam.get(dsChiTietTam.size() - 1).getGiaDen() != Double.MAX_VALUE) {
                // Tự động set lại input về trạng thái nhập tiếp theo
                txtGiaTu.setText(dfTien.format(nextStartPrice));
                txtGiaDen.setText("");
                txtTiLe.setText("");
                chkKhoangCuoi.setSelected(false);
            }
        }
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

    /**
     * Thiết lập phím tắt cho màn hình Quản lý Bảng Giá
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

        // F2: Focus tên bảng giá
        inputMap.put(KeyStroke.getKeyStroke("F2"), "focusTenBG");
        actionMap.put("focusTenBG", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTenBG.requestFocus();
                txtTenBG.selectAll();
            }
        });

        // F3: Focus giá đến (tab chi tiết)
        inputMap.put(KeyStroke.getKeyStroke("F3"), "focusGiaDen");
        actionMap.put("focusGiaDen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtGiaDen.isEnabled()) {
                    txtGiaDen.requestFocus();
                    txtGiaDen.selectAll();
                }
            }
        });

        // F4: Focus tỉ lệ (tab chi tiết)
        inputMap.put(KeyStroke.getKeyStroke("F4"), "focusTiLe");
        actionMap.put("focusTiLe", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (txtTiLe.isEnabled()) {
                    txtTiLe.requestFocus();
                    txtTiLe.selectAll();
                }
            }
        });

        // F5: Làm mới
        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kiểm tra xem có dữ liệu chưa lưu không
                if (!dsChiTietTam.isEmpty() || !txtTenBG.getText().trim().isEmpty()) {
                    int confirm = JOptionPane.showConfirmDialog(BangGia_GUI.this,
                        "Bạn có dữ liệu chưa lưu. Bạn có chắc muốn làm mới?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        xuLyLamMoi();
                    }
                } else {
                    xuLyLamMoi();
                }
            }
        });

        // F6: Toggle checkbox "Trở lên"
        inputMap.put(KeyStroke.getKeyStroke("F6"), "toggleKhoangCuoi");
        actionMap.put("toggleKhoangCuoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chkKhoangCuoi.isEnabled()) {
                    chkKhoangCuoi.doClick();
                }
            }
        });

        // F7: Thêm quy tắc chi tiết
        inputMap.put(KeyStroke.getKeyStroke("F7"), "themChiTiet");
        actionMap.put("themChiTiet", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnThemCT.isEnabled()) {
                    xuLyThemChiTietVaoBangTam();
                }
            }
        });

        // F8: Xóa dòng cuối
        inputMap.put(KeyStroke.getKeyStroke("F8"), "xoaDongCuoi");
        actionMap.put("xoaDongCuoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyXoaChiTietKhoiBangTam();
            }
        });

        // F9: Làm mới form chi tiết
        inputMap.put(KeyStroke.getKeyStroke("F9"), "lamMoiFormChiTiet");
        actionMap.put("lamMoiFormChiTiet", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyLamMoiFormChiTiet();
            }
        });

        // F10: Sửa quy tắc chi tiết
        inputMap.put(KeyStroke.getKeyStroke("F10"), "suaChiTiet");
        actionMap.put("suaChiTiet", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnSuaCT.isEnabled()) {
                    xuLySuaChiTiet();
                }
            }
        });

        // Ctrl+N: Tạo mới bảng giá
        inputMap.put(KeyStroke.getKeyStroke("control N"), "taoMoi");
        actionMap.put("taoMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dsChiTietTam.isEmpty()) {
                    int confirm = JOptionPane.showConfirmDialog(BangGia_GUI.this,
                        "Bạn có dữ liệu chưa lưu. Bạn có chắc muốn tạo mới?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        xuLyLamMoi();
                    }
                } else {
                    xuLyLamMoi();
                }
            }
        });

        // Ctrl+S: Lưu (Thêm/Cập nhật)
        inputMap.put(KeyStroke.getKeyStroke("control S"), "luu");
        actionMap.put("luu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kiểm tra xem đang ở chế độ thêm hay sửa
                String maBG = txtMaBG.getText().trim();
                if (bangGiaDAO.timBangGiaTheoMa(maBG) != null) {
                    xuLyCapNhatBangGia();
                } else {
                    xuLyThemBangGia();
                }
            }
        });

        // Ctrl+U: Cập nhật
        inputMap.put(KeyStroke.getKeyStroke("control U"), "capNhat");
        actionMap.put("capNhat", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyCapNhatBangGia();
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

        // Enter trên ô tìm kiếm
        txtTimKiem.addActionListener(e -> xuLyTimKiem());

        // Enter trên ô giá đến
        txtGiaDen.addActionListener(e -> {
            if (txtGiaDen.isEnabled() && !txtGiaDen.getText().trim().isEmpty()) {
                txtTiLe.requestFocus();
                txtTiLe.selectAll();
            }
        });

        // Enter trên ô tỉ lệ
        txtTiLe.addActionListener(e -> {
            if (btnThemCT.isEnabled()) {
                xuLyThemChiTietVaoBangTam();
            }
        });

        // ESC: Làm mới input chi tiết
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "clearInput");
        actionMap.put("clearInput", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component focused = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (focused instanceof JTextField) {
                    JTextField txt = (JTextField) focused;
                    txt.setText("");
                    if (txt == txtTimKiem) {
                        xuLyLamMoi();
                    }
                }
            }
        });
    }
    /** ✅ Reset form input sau khi lưu sửa - GIỮ NGUYÊN dữ liệu */
    private void resetInputFormSauKhiLuu() {
        // Tính lại nextStartPrice dựa trên quy tắc cuối cùng
        if (!dsChiTietTam.isEmpty()) {
            ChiTietBangGia last = dsChiTietTam.get(dsChiTietTam.size() - 1);
            if (last.getGiaDen() == Double.MAX_VALUE) {
                // Đã là khoảng cuối -> Khóa nhập liệu
                nextStartPrice = -1;
                txtGiaTu.setText("---");
                txtGiaDen.setText("---");
                txtGiaDen.setEnabled(false);
                txtTiLe.setText("");
                txtTiLe.setEnabled(false);
                chkKhoangCuoi.setSelected(false);
                chkKhoangCuoi.setEnabled(false);
                btnThemCT.setEnabled(false);
            } else {
                // Còn có thể thêm tiếp
                nextStartPrice = last.getGiaDen() + 1;
                txtGiaTu.setText(dfTien.format(nextStartPrice));
                txtGiaDen.setText("");
                txtGiaDen.setEnabled(true);
                txtTiLe.setText("");
                txtTiLe.setEnabled(true);
                chkKhoangCuoi.setSelected(false);
                chkKhoangCuoi.setEnabled(true);
                btnThemCT.setEnabled(true);
                txtGiaDen.requestFocus();
            }
        } else {
            // Không có quy tắc nào -> reset về ban đầu
            nextStartPrice = 0;
            txtGiaTu.setText("0");
            txtGiaDen.setText("");
            txtGiaDen.setEnabled(true);
            txtTiLe.setText("");
            txtTiLe.setEnabled(true);
            chkKhoangCuoi.setSelected(false);
            chkKhoangCuoi.setEnabled(true);
            btnThemCT.setEnabled(true);
            txtGiaDen.requestFocus();
        }
    }
    
    /** ✅ Làm mới form chi tiết - CHỈ reset input, KHÔNG xóa dữ liệu */
    private void xuLyLamMoiFormChiTiet() {
        // Reset chế độ sửa nếu đang sửa
        if (indexDangSua >= 0) {
            indexDangSua = -1;
            btnThemCT.setText(
                "<html>" +
                    "<center>" +
                        "THÊM QUY TẮC<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F7)</span>" +
                    "</center>" +
                "</html>");
            btnThemCT.setToolTipText("<html><b>Phím tắt:</b> F7<br>Thêm quy tắc giá vào bảng tạm<br>(Giá Từ sẽ tự động nhảy)</html>");
            btnSuaCT.setEnabled(true);
            btnXoaCT.setEnabled(true);
            
            // Đổi lại text nút Làm mới về ban đầu
            btnLamMoiCT.setText(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F9)</span>" +
                    "</center>" +
                "</html>");
            btnLamMoiCT.setToolTipText("<html><b>Phím tắt:</b> F9<br>Làm mới các ô nhập liệu<br>Để tiếp tục thêm quy tắc mới</html>");
        }
        
        // CHỈ reset input, KHÔNG xóa dsChiTietTam
        resetInputFormSauKhiLuu();
    }
    public static void main(String[] args) {
            JFrame frame = new JFrame("Quản Lý Bảng Giá");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new BangGia_GUI());
            frame.setVisible(true);
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		Object source = e.getSource();
		
		// Xử lý click vào bảng Bảng Giá
		if (source.equals(tblBangGia)) {
			int row = tblBangGia.getSelectedRow();
			if(row >= 0) {
				String maBG = tblBangGia.getValueAt(row, 1).toString();
				loadBangGiaLenForm(maBG);
			}
		}
		// Xử lý click vào bảng Chi Tiết
		else if (source.equals(tblChiTiet)) {
			int row = tblChiTiet.getSelectedRow();
			if (row >= 0) {
				xuLyClickDongChiTiet(row);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Không cần xử lý
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Không cần xử lý
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Không cần xử lý
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Không cần xử lý
	}
}