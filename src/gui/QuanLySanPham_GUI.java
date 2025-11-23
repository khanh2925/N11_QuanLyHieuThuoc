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

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.SanPham_DAO;
import dao.QuyCachDongGoi_DAO;
import entity.SanPham;
import entity.QuyCachDongGoi;
import enums.DuongDung;
import enums.LoaiSanPham;

@SuppressWarnings("serial")
public class QuanLySanPham_GUI extends JPanel implements ActionListener, MouseListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu
    private JTextField txtMaSP, txtTenSP, txtSoDK, txtGiaNhap, txtGiaBan, txtKeBan;
    private JComboBox<String> cboLoaiSP, cboDuongDung, cboTrangThai;
    private JLabel lblHinhAnh;
    private JButton btnChonAnh;
    private String currentImagePath = "icon_anh_sp_null.png"; // Ảnh mặc định

    // Panel Nút bấm (Đã xóa btnXoa)
    private PillButton btnThem, btnSua, btnLamMoi;
    
    // Header
    private JTextField txtTimKiem;
    private PillButton btnTimKiem;

    // Bảng
    private JTable tblSanPham;
    private DefaultTableModel modelSanPham;
    private JTable tblQuyCach;
    private DefaultTableModel modelQuyCach;
    private PillButton btnThemQC, btnXoaQC;

    // DAO & Utils
    private SanPham_DAO sanPhamDAO;
    private QuyCachDongGoi_DAO quyCachDAO;

    private final DecimalFormat df = new DecimalFormat("#,###");
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public QuanLySanPham_GUI() {
        // 1. Khởi tạo DAO
        sanPhamDAO = new SanPham_DAO();
        quyCachDAO = new QuyCachDongGoi_DAO();
        
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

        // 3. LOAD DATA BAN ĐẦU
        loadDataLenBang();
        lamMoiForm(); // Để sinh mã tự động ngay khi mở
    }

    // ==========================================================================
    //                              UI COMPONENTS
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
        // Enter để tìm
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // TOP: Form + Buttons
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin sản phẩm"));

        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm); 
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton); 
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // BOTTOM: Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_TEXT);

        JPanel pnTab1 = new JPanel(new BorderLayout());
        pnTab1.setBackground(Color.WHITE);
        taoBangDanhSach(pnTab1);
        tabbedPane.addTab("Danh sách sản phẩm", pnTab1);

        JPanel pnTab2 = new JPanel(new BorderLayout());
        pnTab2.setBackground(Color.WHITE);
        taoBangQuyCach(pnTab2);
        tabbedPane.addTab("Quy cách đóng gói", pnTab2);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, tabbedPane);
        splitPane.setDividerLocation(380); 
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {
        // Ảnh
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

        // Fields
        int xStart = 300, yStart = 30;
        int hText = 35, wLbl = 110, wTxt = 280, gap = 25;
        int xCol2 = xStart + wLbl + wTxt + 50; 

        // Row 1
        p.add(createLabel("Mã SP:", xStart, yStart));
        txtMaSP = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaSP.setEditable(false);
        txtMaSP.setBackground(new Color(245,245,245));
        p.add(txtMaSP);

        p.add(createLabel("Trạng thái:", xCol2, yStart));
        cboTrangThai = new JComboBox<>(new String[]{"Đang bán", "Ngừng bán"});
        cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);

        // Row 2
        yStart += hText + gap;
        p.add(createLabel("Tên SP:", xStart, yStart));
        txtTenSP = createTextField(xStart + wLbl, yStart, wTxt + 50 + wLbl + wTxt - wTxt); 
        // Hack width cho field tên dài
        txtTenSP.setSize((xCol2 + wLbl + wTxt) - (xStart + wLbl), hText);
        p.add(txtTenSP);

        // Row 3
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

        // Row 4
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
        
        // Row 5
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
        txtGiaBan.setToolTipText("Giá bán được tính tự động dựa trên Bảng giá hiện hành");
        p.add(txtGiaBan);
    }

    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY)); 
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(10, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL;

        int w=140, h=45;
        
        // Nút Thêm
        btnThem = createPillButton("Thêm", w, h); 
        gbc.gridy=0; 
        p.add(btnThem, gbc);
        
        // Nút Cập nhật
        btnSua = createPillButton("Cập nhật", w, h); 
        gbc.gridy=1; 
        p.add(btnSua, gbc);
        
        // Đã xóa btnXoa ở đây và đẩy btnLamMoi lên
        
        // Nút Làm mới
        btnLamMoi = createPillButton("Làm mới", w, h); 
        gbc.gridy=2; 
        p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã SP", "Tên sản phẩm", "Loại", "Số ĐK", "Đường dùng", "Giá nhập", "Giá bán", "Kệ bán", "Trạng thái"};
        modelSanPham = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPham = setupTable(modelSanPham);
        
        TableColumnModel cm = tblSanPham.getColumnModel();
        cm.getColumn(0).setPreferredWidth(100); 
        cm.getColumn(1).setPreferredWidth(250);
        cm.getColumn(5).setCellRenderer(new RightAlignRenderer());
        cm.getColumn(6).setCellRenderer(new RightAlignRenderer());
        
        // Render Trạng thái màu sắc
        cm.getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setHorizontalAlignment(CENTER);
                if("Đang bán".equals(v)) { lbl.setForeground(new Color(0, 128, 0)); lbl.setFont(FONT_BOLD); }
                else { lbl.setForeground(Color.RED); lbl.setFont(FONT_TEXT); }
                return lbl;
            }
        });

        tblSanPham.addMouseListener(this);
        p.add(new JScrollPane(tblSanPham), BorderLayout.CENTER);
    }

    private void taoBangQuyCach(JPanel p) {
        JPanel pnToolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnToolBar.setBackground(Color.WHITE);
        
        btnThemQC = new PillButton("Thêm quy cách");
        btnThemQC.setPreferredSize(new Dimension(150, 35));
        btnThemQC.addActionListener(this);
        pnToolBar.add(btnThemQC);
        
        btnXoaQC = new PillButton("Xóa quy cách");
        btnXoaQC.setPreferredSize(new Dimension(150, 35));
        btnXoaQC.addActionListener(this);
        pnToolBar.add(btnXoaQC);
        
        p.add(pnToolBar, BorderLayout.NORTH);

        String[] cols = {"Mã quy cách", "Đơn vị tính", "Hệ số quy đổi", "Tỉ lệ giảm", "Là gốc"};
        modelQuyCach = new DefaultTableModel(cols, 0);
        tblQuyCach = setupTable(modelQuyCach);
        p.add(new JScrollPane(tblQuyCach), BorderLayout.CENTER);
    }

    // ==========================================================================
    //                              XỬ LÝ SỰ KIỆN (LOGIC CHÍNH)
    // ==========================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // ------------------------- THÊM SẢN PHẨM -------------------------
        if (o.equals(btnThem)) {
            if (validData()) {
                SanPham sp = getFromForm();
                // 1. Sinh mã tự động
                sp.setMaSanPham(txtMaSP.getText()); 
                
                // 2. Thêm vào DB
                if (sanPhamDAO.themSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công: " + sp.getMaSanPham());
                    loadDataLenBang();
                    lamMoiForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng mã hoặc lỗi CSDL)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } 
        
        // ------------------------- CẬP NHẬT SẢN PHẨM -------------------------
        else if (o.equals(btnSua)) {
            int row = tblSanPham.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần cập nhật!");
                return;
            }
            if (validData()) {
                SanPham sp = getFromForm();
                sp.setMaSanPham(txtMaSP.getText()); // Mã không đổi
                
                if (sanPhamDAO.capNhatSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadDataLenBang();
                    tblSanPham.setRowSelectionInterval(row, row); // Giữ selection
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        // --- ĐÃ XÓA NÚT XÓA VÀ SỰ KIỆN LIÊN QUAN ---
        
        // ------------------------- CÁC NÚT KHÁC -------------------------
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
        }
        else if (o.equals(btnChonAnh)) {
            chonAnh();
        }
        else if (o.equals(btnTimKiem)) {
            xuLyTimKiem();
        }
        
        // ------------------------- QUY CÁCH (DEMO) -------------------------
        else if (o.equals(btnThemQC)) {
            if(txtMaSP.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc thêm sản phẩm trước khi thêm quy cách!");
                return;
            }
            // Mở Dialog thêm quy cách (Cần class JDialog riêng)
            JOptionPane.showMessageDialog(this, "Chức năng hiển thị Dialog thêm quy cách cho SP: " + txtMaSP.getText());
        }
        else if (o.equals(btnXoaQC)) {
            JOptionPane.showMessageDialog(this, "Chức năng xóa quy cách đang được phát triển.");
        }
    }

    // ==========================================================================
    //                              HELPERS & LOGIC
    // ==========================================================================

    private void tuDongLayMa() {
        // Format: SP-000001
        ArrayList<SanPham> list = sanPhamDAO.layTatCaSanPham();
        if (list.isEmpty()) {
            txtMaSP.setText("SP-000001");
        } else {
            // Lấy mã cuối cùng (Giả sử danh sách sắp xếp tăng dần, hoặc loop tìm max)
            // Cách an toàn: Parse số đuôi
            int max = 0;
            for(SanPham s : list) {
                if(s.getMaSanPham().startsWith("SP-")) {
                    try {
                        String numPart = s.getMaSanPham().substring(3); // Bỏ SP-
                        int num = Integer.parseInt(numPart);
                        if(num > max) max = num;
                    } catch(NumberFormatException e) {}
                }
            }
            txtMaSP.setText(String.format("SP-%06d", max + 1));
        }
    }

    private void loadDataLenBang() {
        modelSanPham.setRowCount(0);
        List<SanPham> ds = sanPhamDAO.layTatCaSanPham();
        for (SanPham sp : ds) {
            modelSanPham.addRow(new Object[] {
                sp.getMaSanPham(), sp.getTenSanPham(),
                sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "",
                sp.getSoDangKy(),
                sp.getDuongDung() != null ? sp.getDuongDung().getMoTa() : "",
                df.format(sp.getGiaNhap()), 
                df.format(sp.getGiaBan()),
                sp.getKeBanSanPham(),
                sp.isHoatDong() ? "Đang bán" : "Ngừng bán"
            });
        }
    }

    private void doToForm(int row) {
        if (row < 0) return;
        String ma = tblSanPham.getValueAt(row, 0).toString();
        SanPham sp = sanPhamDAO.laySanPhamTheoMa(ma);

        if (sp != null) {
            txtMaSP.setText(sp.getMaSanPham());
            txtTenSP.setText(sp.getTenSanPham());
            if(sp.getLoaiSanPham() != null) cboLoaiSP.setSelectedItem(sp.getLoaiSanPham().name());
            if(sp.getDuongDung() != null) cboDuongDung.setSelectedItem(sp.getDuongDung().name());
            txtSoDK.setText(sp.getSoDangKy());
            txtGiaNhap.setText(String.valueOf((long)sp.getGiaNhap()));
            txtGiaBan.setText(df.format(sp.getGiaBan())); // Chỉ hiển thị, không set được vì readonly
            txtKeBan.setText(sp.getKeBanSanPham());
            cboTrangThai.setSelectedItem(sp.isHoatDong() ? "Đang bán" : "Ngừng bán");
            
            // Xử lý ảnh
            setHinhAnh(sp.getHinhAnh());
            currentImagePath = sp.getHinhAnh();
            
            // Load Quy Cách
            loadQuyCach(ma);
        }
    }

    private void loadQuyCach(String maSP) {
        modelQuyCach.setRowCount(0);
        List<QuyCachDongGoi> listQC = quyCachDAO.layDanhSachQuyCachTheoSanPham(maSP);
        if(listQC != null) {
            for(QuyCachDongGoi qc : listQC) {
                modelQuyCach.addRow(new Object[] {
                    qc.getMaQuyCach(),
                    qc.getDonViTinh().getTenDonViTinh(),
                    qc.getHeSoQuyDoi(),
                    (qc.getTiLeGiam() * 100) + "%",
                    qc.isDonViGoc() ? "Có" : "Không"
                });
            }
        }
    }

    private SanPham getFromForm() {
        String ma = txtMaSP.getText();
        String ten = txtTenSP.getText().trim();
        LoaiSanPham loai = LoaiSanPham.valueOf(cboLoaiSP.getSelectedItem().toString());
        DuongDung dd = DuongDung.valueOf(cboDuongDung.getSelectedItem().toString());
        String soDK = txtSoDK.getText().trim();
        String ke = txtKeBan.getText().trim();
        boolean hd = cboTrangThai.getSelectedItem().equals("Đang bán");
        
        double gn = 0;
        try {
            gn = Double.parseDouble(txtGiaNhap.getText().replace(",", "").replace(".", ""));
        } catch (Exception e) {}

        // Tạo đối tượng SanPham
        // Lưu ý: SanPham Constructor trong Entity có tham số (ma, ten, loai, soDK, duongDung, giaNhap, hinhAnh, keBan, hoatDong)
        return new SanPham(ma, ten, loai, soDK, dd, gn, currentImagePath, ke, hd);
    }

    private boolean validData() {
        String ten = txtTenSP.getText().trim();
        String gia = txtGiaNhap.getText().trim();

        if(ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!");
            txtTenSP.requestFocus(); return false;
        }
        // Regex cho số (chấp nhận dấu phẩy hoặc chấm phân cách hàng nghìn)
        if(!gia.matches("[0-9,.]+")) { 
            JOptionPane.showMessageDialog(this, "Giá nhập phải là số dương!");
            txtGiaNhap.requestFocus(); return false;
        }
        return true;
    }

    private void lamMoiForm() {
        tuDongLayMa(); // Sinh mã mới
        txtTenSP.setText("");
        txtSoDK.setText("");
        txtGiaNhap.setText("");
        txtGiaBan.setText("");
        txtKeBan.setText("");
        cboLoaiSP.setSelectedIndex(0);
        cboDuongDung.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        setHinhAnh("icon_anh_sp_null.png");
        currentImagePath = "icon_anh_sp_null.png";
        
        txtTenSP.requestFocus();
        tblSanPham.clearSelection();
        modelQuyCach.setRowCount(0);
    }

    private void xuLyTimKiem() {
        String key = txtTimKiem.getText().trim();
        if(key.isEmpty()) {
            loadDataLenBang();
            return;
        }
        ArrayList<SanPham> ds = sanPhamDAO.timKiemSanPham(key);
        modelSanPham.setRowCount(0);
        for (SanPham sp : ds) {
            modelSanPham.addRow(new Object[] {
                sp.getMaSanPham(), sp.getTenSanPham(),
                sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "",
                sp.getSoDangKy(),
                sp.getDuongDung() != null ? sp.getDuongDung().getMoTa() : "",
                df.format(sp.getGiaNhap()), 
                df.format(sp.getGiaBan()),
                sp.getKeBanSanPham(),
                sp.isHoatDong() ? "Đang bán" : "Ngừng bán"
            });
        }
    }

    // --- ẢNH ---
    private void chonAnh() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Hình ảnh (JPG, PNG)", "jpg", "png"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            // Lưu đường dẫn file để lưu vào DB (trong thực tế nên copy file vào project)
            currentImagePath = f.getAbsolutePath(); 
            
            // Hiển thị preview
            ImageIcon icon = new ImageIcon(f.getAbsolutePath());
            lblHinhAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
            lblHinhAnh.setText("");
        }
    }

    private void setHinhAnh(String pathOrName) {
        if (pathOrName == null || pathOrName.isEmpty()) pathOrName = "icon_anh_sp_null.png";
        
        // 1. Thử load từ resource (file trong project, ví dụ ảnh mặc định)
        URL url = getClass().getResource("/images/" + pathOrName);
        
        // 2. Nếu không thấy, thử load như file tuyệt đối (ảnh người dùng chọn từ máy)
        ImageIcon icon = null;
        if (url != null) {
            icon = new ImageIcon(url);
        } else {
            try {
                File f = new File(pathOrName);
                if(f.exists()) {
                    icon = new ImageIcon(pathOrName);
                }
            } catch (Exception e) {}
        }

        if (icon != null && icon.getIconWidth() > 0) {
            lblHinhAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
            lblHinhAnh.setText("");
        } else {
            // Fallback nếu lỗi
            lblHinhAnh.setIcon(null);
            lblHinhAnh.setText("Không có ảnh");
        }
    }

    // --- MOUSE LISTENER ---
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(tblSanPham)) {
            doToForm(tblSanPham.getSelectedRow());
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // --- UI HELPERS ---
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
    // Class render căn phải cho số tiền
    private class RightAlignRenderer extends DefaultTableCellRenderer {
        public RightAlignRenderer() { setHorizontalAlignment(JLabel.RIGHT); }
    }
    
    // Main test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1550, 850);
            f.setContentPane(new QuanLySanPham_GUI());
            f.setVisible(true);
        });
    }
}