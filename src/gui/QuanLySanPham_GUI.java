package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

// Import các thành phần của bạn
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.SanPham_DAO;
import dao.QuyCachDongGoi_DAO; // ✅ Import DAO Quy cách
import entity.SanPham;
import entity.QuyCachDongGoi;
import enums.DuongDung;
import enums.LoaiSanPham;

@SuppressWarnings("serial")
public class QuanLySanPham_GUI extends JPanel implements ActionListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu
    private JTextField txtMaSP, txtTenSP, txtSoDK, txtGiaNhap, txtGiaBan, txtKeBan;
    private JComboBox<String> cboLoaiSP, cboDuongDung, cboTrangThai;
    private JLabel lblHinhAnh;
    private JButton btnChonAnh;
    private String currentImagePath = "icon_anh_sp_null.png";

    // Panel Nút bấm (Bên phải form)
    private PillButton btnThem, btnSua, btnXoa, btnLamMoi;
    
    // Header (Tìm kiếm)
    private JTextField txtTimKiem;
    private PillButton btnTimKiem;

    // Tab 1: Bảng Sản Phẩm
    private JTable tblSanPham;
    private DefaultTableModel modelSanPham;

    // Tab 2: Bảng Quy Cách & Nút chức năng của nó
    private JTable tblQuyCach;
    private DefaultTableModel modelQuyCach;
    private PillButton btnThemQC, btnXoaQC;

    // DAO & Utils
    private SanPham_DAO sanPhamDAO;
    private QuyCachDongGoi_DAO quyCachDAO; // ✅ Khai báo DAO Quy cách

    private final DecimalFormat df = new DecimalFormat("#,###");
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public QuanLySanPham_GUI() {
        // 1. Khởi tạo DAO
        sanPhamDAO = new SanPham_DAO();
        quyCachDAO = new QuyCachDongGoi_DAO(); // ✅ Khởi tạo DAO Quy cách
        
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

        // 3. LOAD DATA BAN ĐẦU
        loadDataLenBang();
    }

    // ==========================================================================
    //                              PHẦN HEADER
    // ==========================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã, tên thuốc, số đăng ký...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    // ==========================================================================
    //                              PHẦN CENTER (SPLIT PANE)
    // ==========================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- A. PHẦN TRÊN (TOP): CONTAINER CHỨA FORM VÀ PANEL NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin sản phẩm"));

        // A1. Form Nhập Liệu (Nằm giữa - CENTER)
        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm); 
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        // A2. Panel Nút Chức Năng (Nằm phải - EAST)
        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton); 
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- B. PHẦN DƯỚI (BOTTOM): TABBED PANE ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_TEXT);

        // Tab 1: Danh sách sản phẩm
        JPanel pnTab1 = new JPanel(new BorderLayout());
        pnTab1.setBackground(Color.WHITE);
        taoBangDanhSach(pnTab1);
        tabbedPane.addTab("Danh sách sản phẩm", pnTab1);

        // Tab 2: Quy cách đóng gói
        JPanel pnTab2 = new JPanel(new BorderLayout());
        pnTab2.setBackground(Color.WHITE);
        taoBangQuyCach(pnTab2);
        tabbedPane.addTab("Quy cách đóng gói", pnTab2);

        // --- C. TẠO SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, tabbedPane);
        splitPane.setDividerLocation(360); // Form cao khoảng 360px
        splitPane.setResizeWeight(0.0); // Cố định form, bảng co giãn
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // --- FORM NHẬP LIỆU (LAYOUT TUYỆT ĐỐI) ---
    private void taoFormNhapLieu(JPanel p) {

        // Ảnh (bên trái giống Nhân viên)
        lblHinhAnh = new JLabel("", SwingConstants.CENTER);
        lblHinhAnh.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblHinhAnh.setBounds(30, 40, 180, 180);
        setHinhAnh("icon_anh_sp_null.png");
        p.add(lblHinhAnh);

        btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.setBounds(60, 230, 120, 30);
        btnChonAnh.setFont(FONT_TEXT);
        btnChonAnh.addActionListener(this);
        p.add(btnChonAnh);

        // ===== FORM CHUẨN HOÁ (COPY STRUCTURE NHÂN VIÊN) =====
        int xStart = 300, yStart = 30;
        int hText = 35, wLbl = 110, wTxt = 300, gap = 25;

        int xCol2 = xStart + wLbl + wTxt + 120; // cột 2 (chuẩn hệ thống)

        // ===== HÀNG 1 =====
        p.add(createLabel("Mã SP:", xStart, yStart));
        txtMaSP = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaSP.setEditable(false);
        p.add(txtMaSP);

        p.add(createLabel("Trạng thái:", xCol2, yStart));
        cboTrangThai = new JComboBox<>(new String[]{"Đang bán", "Ngừng bán"});
        cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);

        // ===== HÀNG 2 =====
        yStart += hText + gap;
        p.add(createLabel("Tên SP:", xStart, yStart));
        txtTenSP = createTextField(xStart + wLbl, yStart, wTxt + 530); // tên dài → tăng width giống nhân viên
        p.add(txtTenSP);

        

        // ===== HÀNG 3 =====
        yStart += hText + gap;
        p.add(createLabel("Loại SP:", xStart, yStart));
        cboLoaiSP = new JComboBox<>();
        for (LoaiSanPham l : LoaiSanPham.values()) cboLoaiSP.addItem(l.name());
        cboLoaiSP.setBounds(xStart + wLbl, yStart, wTxt, hText);
        cboLoaiSP.setFont(FONT_TEXT);
        p.add(cboLoaiSP);
        
        p.add(createLabel("Số ĐK:", xCol2, yStart));
        txtSoDK = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtSoDK);
        

        // ===== HÀNG 4 =====
        yStart += hText + gap;
        p.add(createLabel("Giá nhập:", xStart, yStart));
        txtGiaNhap = createTextField(xStart + wLbl, yStart, wTxt);
        p.add(txtGiaNhap);

        p.add(createLabel("Đường dùng:", xCol2, yStart));
        cboDuongDung = new JComboBox<>();
        for (DuongDung d : DuongDung.values()) cboDuongDung.addItem(d.name());
        cboDuongDung.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboDuongDung.setFont(FONT_TEXT);
        p.add(cboDuongDung);

        
        // ===== HÀNG 5 =====
        yStart += hText + gap;
        p.add(createLabel("Kệ bán:", xStart, yStart));
        txtKeBan = createTextField(xStart + wLbl, yStart, wTxt);
        p.add(txtKeBan);
        
        JLabel lblGiaBan = createLabel("Giá bán:", xCol2, yStart);
        lblGiaBan.setFont(FONT_BOLD);
        lblGiaBan.setForeground(new Color(199, 0, 0));
        p.add(lblGiaBan);
        txtGiaBan = createTextField(xCol2 + wLbl, yStart, wTxt);
        txtGiaBan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtGiaBan.setForeground(new Color(199, 0, 0));
        txtGiaBan.setEditable(false);
        p.add(txtGiaBan);

    }


    // --- PANEL NÚT BẤM (BÊN PHẢI) ---
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

        btnThem = createPillButton("Thêm", btnW, btnH);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = createPillButton("Cập nhật", btnW, btnH);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnXoa = createPillButton("Xóa", btnW, btnH);
        gbc.gridy = 2; p.add(btnXoa, gbc);

        btnLamMoi = createPillButton("Làm mới", btnW, btnH);
        gbc.gridy = 3; p.add(btnLamMoi, gbc);
    }

    // --- BẢNG SẢN PHẨM ---
    private void taoBangDanhSach(JPanel p) {
        String[] cols = {
            "Mã SP", "Tên sản phẩm", "Loại", "Số ĐK", 
            "Đường dùng", "Giá nhập", "Giá bán", "Kệ bán", "Trạng thái"
        };
        modelSanPham = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPham = setupTable(modelSanPham);

        // Căn lề & Render
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);

        TableColumnModel cm = tblSanPham.getColumnModel();
        cm.getColumn(0).setPreferredWidth(100); // Mã
        cm.getColumn(1).setPreferredWidth(250); // Tên
        cm.getColumn(5).setCellRenderer(right); // Giá nhập
        cm.getColumn(6).setCellRenderer(right); // Giá bán

        // Render Trạng thái
        cm.getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if("Đang bán".equals(value)) lbl.setForeground(new Color(0, 128, 0));
                else lbl.setForeground(Color.RED);
                return lbl;
            }
        });

        tblSanPham.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doToForm(tblSanPham.getSelectedRow());
            }
        });

        JScrollPane scr = new JScrollPane(tblSanPham);
        scr.setBorder(BorderFactory.createEmptyBorder());
        p.add(scr, BorderLayout.CENTER);
    }

    // --- BẢNG QUY CÁCH ---
    private void taoBangQuyCach(JPanel p) {
        // Toolbar cho Tab Quy Cách
        JPanel pnToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnToolBar.setBackground(Color.WHITE);
        pnToolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        btnThemQC = new PillButton("Thêm quy cách");
        btnThemQC.setFont(FONT_TEXT);
        btnThemQC.setPreferredSize(new Dimension(150, 35));
        btnThemQC.addActionListener(this);
        pnToolBar.add(btnThemQC);
        
        btnXoaQC = new PillButton("Xóa quy cách");
        btnXoaQC.setFont(FONT_TEXT);
        btnXoaQC.setPreferredSize(new Dimension(150, 35));
        btnXoaQC.addActionListener(this);
        pnToolBar.add(btnXoaQC);
        
        p.add(pnToolBar, BorderLayout.NORTH);

        // Table Quy Cách
        String[] cols = {"Mã quy cách", "Đơn vị tính", "Hệ số quy đổi", "Giá bán (Tính)", "Tỉ lệ giảm", "Là gốc"};
        modelQuyCach = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblQuyCach = setupTable(modelQuyCach);
        
        // Render
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        
        tblQuyCach.getColumnModel().getColumn(2).setCellRenderer(center);
        tblQuyCach.getColumnModel().getColumn(3).setCellRenderer(right);
        
        JScrollPane scr = new JScrollPane(tblQuyCach);
        scr.setBorder(BorderFactory.createEmptyBorder());
        p.add(scr, BorderLayout.CENTER);
    }

    // ==========================================================================
    //                              XỬ LÝ LOGIC
    // ==========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // --- LOGIC SẢN PHẨM ---
        if (o.equals(btnThem)) {
            if (validData()) {
                SanPham sp = getFromForm();
                if (sanPhamDAO.themSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadDataLenBang();
                    lamMoiForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại!");
                }
            }
        } 
        else if (o.equals(btnSua)) {
            if (tblSanPham.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
                return;
            }
            if (validData()) {
                SanPham sp = getFromForm();
                sp.setMaSanPham(txtMaSP.getText());
                if (sanPhamDAO.capNhatSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadDataLenBang();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            }
        }
        else if (o.equals(btnXoa)) {
            int row = tblSanPham.getSelectedRow();
            if (row == -1) return;
            String ma = tblSanPham.getValueAt(row, 0).toString();
            if (JOptionPane.showConfirmDialog(this, "Xóa sản phẩm " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (sanPhamDAO.xoaSanPham(ma)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                    loadDataLenBang();
                    lamMoiForm();
                }
            }
        }
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang();
        }
        else if (o.equals(btnChonAnh)) {
            chonAnh();
        }

        // --- LOGIC QUY CÁCH (Giả lập) ---
        else if (o.equals(btnThemQC)) {
             JOptionPane.showMessageDialog(this, "Chức năng thêm quy cách (Cần implement Dialog)");
        }
        else if (o.equals(btnXoaQC)) {
            int row = tblQuyCach.getSelectedRow();
            if (row != -1) {
                // Logic xóa tạm thời
                modelQuyCach.removeRow(row);
                JOptionPane.showMessageDialog(this, "Đã xóa quy cách khỏi bảng tạm.");
            } else {
                JOptionPane.showMessageDialog(this, "Chọn quy cách cần xóa!");
            }
        }
    }

    // Load dữ liệu lên form khi click bảng SP
    private void doToForm(int row) {
        if (row < 0) return;
        String ma = tblSanPham.getValueAt(row, 0).toString();
        SanPham sp = sanPhamDAO.laySanPhamTheoMa(ma);

        if (sp != null) {
            txtMaSP.setText(sp.getMaSanPham());
            txtTenSP.setText(sp.getTenSanPham());
            if(sp.getLoaiSanPham()!=null) cboLoaiSP.setSelectedItem(sp.getLoaiSanPham().name());
            if(sp.getDuongDung()!=null) cboDuongDung.setSelectedItem(sp.getDuongDung().name());
            txtSoDK.setText(sp.getSoDangKy());
            txtKeBan.setText(sp.getKeBanSanPham());
            txtGiaNhap.setText(String.valueOf((long)sp.getGiaNhap()));
            txtGiaBan.setText(String.valueOf((long)sp.getGiaBan()));
            cboTrangThai.setSelectedItem(sp.isHoatDong() ? "Đang bán" : "Ngừng bán");
            setHinhAnh(sp.getHinhAnh());
            currentImagePath = sp.getHinhAnh();
            
            // ✅ Load Quy Cách từ DAO
            loadQuyCachLenBang(sp.getMaSanPham());
        }
    }

    private void loadDataLenBang() {
        modelSanPham.setRowCount(0);
        ArrayList<SanPham> ds = sanPhamDAO.layTatCaSanPham();
        for (SanPham sp : ds) {
            modelSanPham.addRow(new Object[] {
                sp.getMaSanPham(), sp.getTenSanPham(),
                sp.getLoaiSanPham()!=null ? sp.getLoaiSanPham().name() : "",
                sp.getSoDangKy(),
                sp.getDuongDung()!=null ? sp.getDuongDung().name() : "",
                df.format(sp.getGiaNhap()), df.format(sp.getGiaBan()),
                sp.getKeBanSanPham(),
                sp.isHoatDong() ? "Đang bán" : "Ngừng bán"
            });
        }
    }
    
    // ✅ Hàm Load Quy Cách (Dùng DAO)
    private void loadQuyCachLenBang(String maSP) {
        modelQuyCach.setRowCount(0);
        List<QuyCachDongGoi> dsQuyCach = quyCachDAO.layDanhSachQuyCachTheoSanPham(maSP);
        
        if (dsQuyCach != null) {
            for (QuyCachDongGoi qc : dsQuyCach) {
                // Tính giá bán ước lượng (vì QC không lưu giá bán trực tiếp)
                // double giaBanQC = qc.getSanPham().getGiaBan() * qc.getHeSoQuyDoi() * (1 - qc.getTiLeGiam()); 
                
                modelQuyCach.addRow(new Object[]{
                    qc.getMaQuyCach(),
                    qc.getDonViTinh().getTenDonViTinh(),
                    qc.getHeSoQuyDoi(),
                    "-", // df.format(giaBanQC) nếu có logic
                    (qc.getTiLeGiam() * 100) + "%",
                    qc.isDonViGoc() ? "Có" : "Không"
                });
            }
        }
    }

    private SanPham getFromForm() {
        String ma = txtMaSP.getText();
        String ten = txtTenSP.getText();
        LoaiSanPham loai = LoaiSanPham.valueOf(cboLoaiSP.getSelectedItem().toString());
        DuongDung dd = DuongDung.valueOf(cboDuongDung.getSelectedItem().toString());
        String soDK = txtSoDK.getText();
        String ke = txtKeBan.getText();
        double gn = 0, gb = 0;
        try {
            gn = Double.parseDouble(txtGiaNhap.getText().replace(",", ""));
            gb = Double.parseDouble(txtGiaBan.getText().replace(",", ""));
        } catch(Exception e) {}
        boolean hd = cboTrangThai.getSelectedItem().equals("Đang bán");
        return new SanPham(ma, ten, loai, soDK, dd, gn, currentImagePath, ke, hd);
    }

    private void lamMoiForm() {
        txtMaSP.setText(""); txtTenSP.setText(""); txtSoDK.setText("");
        txtKeBan.setText(""); txtGiaNhap.setText(""); txtGiaBan.setText("");
        cboLoaiSP.setSelectedIndex(0); cboDuongDung.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        setHinhAnh("icon_anh_sp_null.png");
        currentImagePath = "icon_anh_sp_null.png";
        txtTenSP.requestFocus();
        tblSanPham.clearSelection();
        modelQuyCach.setRowCount(0); // Clear bảng quy cách
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().trim();
        ArrayList<SanPham> ds = sanPhamDAO.timKiemSanPham(kw);
        modelSanPham.setRowCount(0);
        for (SanPham sp : ds) {
            modelSanPham.addRow(new Object[] {
                sp.getMaSanPham(), sp.getTenSanPham(),
                sp.getLoaiSanPham()!=null ? sp.getLoaiSanPham().name() : "",
                sp.getSoDangKy(), sp.getDuongDung()!=null ? sp.getDuongDung().name() : "",
                df.format(sp.getGiaNhap()), df.format(sp.getGiaBan()),
                sp.getKeBanSanPham(), sp.isHoatDong() ? "Đang bán" : "Ngừng bán"
            });
        }
    }

    // --- Helpers ---
    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image", "jpg", "png", "gif"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            currentImagePath = file.getName();
            setHinhAnhLocal(file.getAbsolutePath());
        }
    }

    private void setHinhAnh(String name) {
        if(name == null || name.isEmpty()) name = "icon_anh_sp_null.png";
        try {
            URL url = getClass().getResource("/images/" + name);
            if(url == null) url = getClass().getResource("/images/icon_anh_sp_null.png");
            ImageIcon icon = new ImageIcon(url);
            lblHinhAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
            lblHinhAnh.setText("");
        } catch(Exception e) { lblHinhAnh.setText("Lỗi ảnh"); }
    }
    
    private void setHinhAnhLocal(String path) {
        ImageIcon icon = new ImageIcon(path);
        lblHinhAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
        lblHinhAnh.setText("");
    }

    private boolean validData() {
        if(txtTenSP.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được rỗng");
            txtTenSP.requestFocus(); return false;
        }
        return true;
    }

    // --- UI Helpers ---
    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TEXT);
        lbl.setBounds(x, y, 100, 35);
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
            JFrame frame = new JFrame("Quản Lý Sản Phẩm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1550, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new QuanLySanPham_GUI());
            frame.setVisible(true);
        });
    }
}