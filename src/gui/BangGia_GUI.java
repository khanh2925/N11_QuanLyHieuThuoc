package gui;

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

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

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
        splitPane.setDividerLocation(280);
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
        p.add(txtTenBG);

        // CHECKBOX: "Đặt làm mặc định"
        chkHoatDong = new JCheckBox("Đặt làm bảng giá mặc định (Áp dụng ngay)");
        chkHoatDong.setFont(new Font("Segoe UI", Font.BOLD, 15));
        chkHoatDong.setForeground(new Color(0, 100, 0));
        chkHoatDong.setBackground(Color.WHITE);
        chkHoatDong.setBounds(xStart + wLbl, yStart + (hText + gap)*2 + 5, 400, hText);
        chkHoatDong.setToolTipText("Khi chọn: Bảng giá này sẽ hoạt động, các bảng giá khác sẽ ngừng hoạt động.");
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

        btnThem = createPillButton("Tạo mới", btnW, btnH);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = createPillButton("Cập nhật", btnW, btnH);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnNgungHoatDong = createPillButton("Ngưng HĐ", btnW, btnH);
        btnNgungHoatDong.setBackground(new Color(255, 235, 238)); // Đỏ nhạt
        btnNgungHoatDong.setForeground(Color.RED);
        gbc.gridy = 2; p.add(btnNgungHoatDong, gbc);

        btnLamMoi = createPillButton("Làm mới", btnW, btnH);
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
        pnToolBar.add(txtGiaDen);
        
        // 3. Checkbox "Trở lên"
        chkKhoangCuoi = new JCheckBox("Trở lên (Cuối)");
        chkKhoangCuoi.setBackground(Color.WHITE);
        chkKhoangCuoi.setFont(new Font("Segoe UI", Font.ITALIC, 14));
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
        pnToolBar.add(txtTiLe);

        // 5. Nút Thêm
        btnThemCT = createPillButton("Thêm quy tắc", 130, 35);
        btnThemCT.setFont(FONT_TEXT);
        pnToolBar.add(btnThemCT);
        
        // 6. Nút Xóa (Xóa dòng cuối)
        btnXoaCT = createPillButton("Xóa dòng cuối", 130, 35);
        btnXoaCT.setBackground(new Color(255, 235, 238));
        btnXoaCT.setForeground(Color.RED);
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
        txtNgayApDung.setText(LocalDate.now().format(dtf));
        chkHoatDong.setSelected(false);
        
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
        
        for(BangGia bg : list) {
            if(bg.getMaBangGia().toLowerCase().contains(tuKhoa.toLowerCase()) ||
               bg.getTenBangGia().toLowerCase().contains(tuKhoa.toLowerCase())) {
                ketQua.add(bg);
            }
        }
        renderDanhSachBangGia(ketQua);
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
            JFrame frame = new JFrame("Quản Lý Bảng Giá");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new BangGia_GUI());
            frame.setVisible(true);
    }
}