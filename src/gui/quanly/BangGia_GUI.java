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
public class BangGia_GUI extends JPanel implements ActionListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu (Master)
    private JTextField txtMaBG, txtTenBG, txtNgayApDung;
    private JCheckBox chkHoatDong;

    // Panel Nút bấm (Master)
    private PillButton btnThem, btnSua, btnNgungHoatDong, btnLamMoi;
    
    // Header (Tìm kiếm)
    private JTextField txtTimKiem;
    private PillButton btnTimKiem;

    // Tab 1: Danh sách Bảng Giá
    private JTable tblBangGia;
    private DefaultTableModel modelBangGia;

    // Tab 2: Chi tiết Quy tắc giá
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;
    private PillButton btnThemCT, btnXoaCT; 
    
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

    public BangGia_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // Init DAO
        bangGiaDAO = new BangGia_DAO();
        chiTietDAO = new ChiTietBangGia_DAO();
        sanPhamDAO = new SanPham_DAO();
        dsChiTietTam = new ArrayList<>();
        
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

        btnTimKiem = new PillButton("Tìm kiếm (Enter)");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.addActionListener(this);
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
        splitPane.setDividerLocation(290);
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // --- FORM NHẬP LIỆU ---
    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 40;
        int hText = 35, wLbl = 120, wTxt = 300, gap = 25;

        // CỘT 1
        p.add(createLabel("Mã BG:", xStart, yStart));
        txtMaBG = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaBG.setEditable(false); 
        p.add(txtMaBG);

        p.add(createLabel("Tên BG:", xStart, yStart + (hText + gap)));
        txtTenBG = createTextField(xStart + wLbl, yStart + (hText + gap), wTxt);
        PlaceholderSupport.addPlaceholder(txtTenBG, "Nhập tên bảng giá (F2)");
        txtTenBG.setToolTipText("<html><b>Phím tắt:</b> F2<br>Nhập tên bảng giá (VD: Bảng giá tháng 1/2025)</html>");
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

        p.add(createLabel("Ngày áp dụng:", xCol2, yStart));
        txtNgayApDung = createTextField(xCol2 + wLbl, yStart, wTxt);
        txtNgayApDung.setEditable(false); 
        txtNgayApDung.setText(LocalDate.now().format(dtf));
        p.add(txtNgayApDung);
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

        btnThem = createPillButton(
                "<html>" +
                    "<center>" +
                        "TẠO MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" +
                    "</center>" +
                "</html>", btnW, btnH);
        btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Tạo bảng giá mới (sẽ hỏi xác nhận nếu đang nhập dở)</html>");
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = createPillButton(
                "<html>" +
                    "<center>" +
                        "CẬP NHẬT<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" +
                    "</center>" +
                "</html>", btnW, btnH);
        btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật bảng giá đang chọn (phải chọn bảng giá trước)</html>");
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnNgungHoatDong = createPillButton(
                "<html>" +
                    "<center>" +
                        "NGƯNG HĐ<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+D)</span>" +
                    "</center>" +
                "</html>", btnW, btnH);
        btnNgungHoatDong.setToolTipText("<html><b>Phím tắt:</b> Ctrl+D<br>Ngừng hoạt động bảng giá đang chọn</html>");
        gbc.gridy = 2; p.add(btnNgungHoatDong, gbc);

        btnLamMoi = createPillButton(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                    "</center>" +
                "</html>", btnW, btnH);
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Xóa form và làm mới (sẽ hỏi xác nhận nếu đang nhập dở)</html>");
        gbc.gridy = 3; p.add(btnLamMoi, gbc);
    }

    // --- TAB 1: DANH SÁCH ---
    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã Bảng Giá", "Tên Bảng Giá", "Ngày áp dụng", "Người lập", "Trạng thái"};
        modelBangGia = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBangGia = setupTable(modelBangGia);

        tblBangGia.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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

        tblBangGia.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblBangGia.getSelectedRow();
                if(row >= 0) {
                    String maBG = tblBangGia.getValueAt(row, 0).toString();
                    loadBangGiaLenForm(maBG);
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
        txtGiaTu = new JTextField(8); 
        txtGiaTu.setEditable(false); // ✅ Không cho sửa
        txtGiaTu.setBackground(new Color(245, 245, 245));
        txtGiaTu.setFont(FONT_BOLD);
        txtGiaTu.setForeground(Color.BLUE);
        pnToolBar.add(txtGiaTu);
        
        // 2. Giá đến
        pnToolBar.add(new JLabel("Đến:"));
        txtGiaDen = new JTextField(8);
        PlaceholderSupport.addPlaceholder(txtGiaDen, "F3");
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
        txtTiLe = new JTextField(5);
        PlaceholderSupport.addPlaceholder(txtTiLe, "F4");
        txtTiLe.setToolTipText("<html><b>Phím tắt:</b> F4<br>Nhập tỉ lệ định giá (VD: 1.2 = bán gấp 1.2 lần giá vốn)<br>Nhấn Enter để thêm quy tắc vào bảng</html>");
        pnToolBar.add(txtTiLe);

        // 5. Nút Thêm
        btnThemCT = createPillButton("Thêm quy tắc (F7)", 150, 35);
        btnThemCT.setFont(FONT_TEXT);
        btnThemCT.setToolTipText("<html><b>Phím tắt:</b> F7<br>Thêm quy tắc giá vào bảng tạm<br>(Giá Từ sẽ tự động nhảy)</html>");
        pnToolBar.add(btnThemCT);
        
        // 6. Nút Xóa (Xóa dòng cuối)
        btnXoaCT = createPillButton("Xóa dòng cuối (F8)", 160, 35);
        btnXoaCT.setBackground(new Color(255, 235, 238));
        btnXoaCT.setForeground(Color.RED);
        btnXoaCT.setFont(FONT_TEXT);
        btnXoaCT.setToolTipText("<html><b>Phím tắt:</b> F8<br>Xóa dòng cuối cùng trong bảng quy tắc</html>");
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
        
     // ✅BỔ SUNG SỰ KIỆN CLICK VÀO HÀNG
        tblChiTiet.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblChiTiet.getSelectedRow();
                if (row >= 0) {
                    xuLyClickDongChiTiet(row);
                }
            }
        });
    }
    
    // --- TAB 3: MÔ PHỎNG ---
    private void taoBangMoPhong(JPanel p) {
        String[] cols = {"Mã SP", "Tên thuốc mẫu", "Giá nhập (Vốn)", "Tỉ lệ áp dụng", "Giá bán ra (Tính toán)"};
        modelMoPhong = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblMoPhong = setupTable(modelMoPhong);
        
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
    //                              XỬ LÝ SỰ KIỆN
    // ==========================================================================
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o.equals(btnThem)) {
            xuLyThemBangGia();
        } else if (o.equals(btnSua)) {
            xuLyCapNhatBangGia();
        } else if (o.equals(btnNgungHoatDong)) {
            xuLyNgungHoatDong();
        } else if (o.equals(btnLamMoi)) {
            xuLyLamMoi();
        } else if (o.equals(btnTimKiem)) {
            xuLyTimKiem();
        } else if (o.equals(btnThemCT)) {
            xuLyThemChiTietVaoBangTam();
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

    // --- 3. NGƯNG HOẠT ĐỘNG ---
    private void xuLyNgungHoatDong() {
        String maBG = txtMaBG.getText();
        if (maBG.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bảng giá.");
            return;
        }

        if (chkHoatDong.isSelected()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bảng giá này ĐANG HOẠT ĐỘNG.\nViệc ngưng hoạt động sẽ khiến sản phẩm không có giá bán.\nBạn có chắc chắn không?", 
                "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        } else {
            JOptionPane.showMessageDialog(this, "Bảng giá này đã ở trạng thái ngừng hoạt động rồi.");
            return;
        }

        BangGia bg = bangGiaDAO.timBangGiaTheoMa(maBG);
        if (bg != null) {
            bg.setHoatDong(false);
            if (bangGiaDAO.capNhatBangGia(bg)) {
                JOptionPane.showMessageDialog(this, "Đã ngưng hoạt động bảng giá: " + maBG);
                xuLyLamMoi();
            }
        }
    }

    // --- 4. LÀM MỚI & RESET ---
    private void xuLyLamMoi() {
        txtMaBG.setText(bangGiaDAO.taoMaBangGia());
        txtTenBG.setText("");
        PlaceholderSupport.addPlaceholder(txtTenBG, "Nhập tên bảng giá (F2)");
        txtNgayApDung.setText(LocalDate.now().format(dtf));
        chkHoatDong.setSelected(true);
        chkHoatDong.setEnabled(false);
        
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

    // --- 6. XỬ LÝ CHI TIẾT (TỐI ƯU UX) ---
    private void xuLyThemChiTietVaoBangTam() {
        try {
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
        for (BangGia bg : list) {
            String tenNV = bg.getNhanVien() != null ? bg.getNhanVien().getMaNhanVien() : "N/A";
            
            modelBangGia.addRow(new Object[]{
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
            txtMaBG.setText(bg.getMaBangGia());
            txtTenBG.setText(bg.getTenBangGia());
            txtTenBG.setForeground(Color.BLACK); // Đảm bảo chữ màu đen, không phải placeholder
            txtNgayApDung.setText(dtf.format(bg.getNgayApDung()));
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
        }
    }

    private BangGia taoBangGiaTuForm() {
        String ma = txtMaBG.getText();
        String ten = txtTenBG.getText();
        boolean active = chkHoatDong.isSelected(); 
        
        try {
            LocalDate ngay = LocalDate.parse(txtNgayApDung.getText(), dtf);
            NhanVien nv = null;
            try {
                nv = Session.getInstance().getTaiKhoanDangNhap().getNhanVien();
            } catch (Exception e) {
                nv = new NhanVien("NV0001"); 
            }
            return new BangGia(ma, nv, ten, ngay, active);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu ngày tháng hoặc hệ thống.");
            return null;
        }
    }

    /** ✅ VALIDATION CẢI TIẾN: Bắt buộc 3 quy tắc + Có vô cực */
    private boolean validInput() {
        if (txtTenBG.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên bảng giá không được rỗng.");
            return false;
        }
        
        // 1. Kiểm tra số lượng quy tắc
        if (dsChiTietTam.size() < 3) {
            JOptionPane.showMessageDialog(this, "Bảng giá phải có tối thiểu 3 khoảng giá (Quy tắc).");
            return false;
        }

        // 2. Kiểm tra quy tắc cuối cùng phải là vô cực
        ChiTietBangGia lastRule = dsChiTietTam.get(dsChiTietTam.size() - 1);
        if (lastRule.getGiaDen() != Double.MAX_VALUE) {
            JOptionPane.showMessageDialog(this, "Quy tắc cuối cùng phải là khoảng mở (Chọn 'Trở lên').");
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

        // Ctrl+D: Ngưng hoạt động
        inputMap.put(KeyStroke.getKeyStroke("control D"), "ngungHoatDong");
        actionMap.put("ngungHoatDong", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyNgungHoatDong();
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

    public static void main(String[] args) {
            JFrame frame = new JFrame("Quản Lý Bảng Giá");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new BangGia_GUI());
            frame.setVisible(true);
    }
}