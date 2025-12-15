package gui.quanly;

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

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.SanPham_DAO;
import dao.QuyCachDongGoi_DAO;
import entity.SanPham;
import entity.QuyCachDongGoi;
import enums.DuongDung;
import enums.LoaiSanPham;
import gui.dialog.QuyCachDongGoi_Dialog;

@SuppressWarnings("serial")
public class QuanLySanPham_GUI extends JPanel implements ActionListener, MouseListener {

    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    private JTextField txtMaSP, txtTenSP, txtSoDK, txtGiaNhap, txtGiaBan, txtKeBan;
    private JComboBox<String> cboLoaiSP, cboDuongDung, cboTrangThai;
    private JLabel lblHinhAnh;
    private JButton btnChonAnh;
    private String duongDanAnhHienTai = "icon_anh_sp_null.png";

    private PillButton btnThem, btnSua, btnLamMoi;
    
    private JTextField txtTimKiem;
    private PillButton btnTimKiem;

    private JTable tblSanPham;
    private DefaultTableModel modelSanPham;
    private JTable tblQuyCach;
    private DefaultTableModel modelQuyCach;
    private PillButton btnThemQC, btnXoaQC, btnSuaQC;

    private SanPham_DAO sanPhamDAO;
    private QuyCachDongGoi_DAO quyCachDAO;

    private final DecimalFormat df = new DecimalFormat("#,###");
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public QuanLySanPham_GUI() {
        sanPhamDAO = new SanPham_DAO();
        quyCachDAO = new QuyCachDongGoi_DAO();
        
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        taoPhanDau();
        add(pnHeader, BorderLayout.NORTH);

        taoPhanGiua();
        add(pnCenter, BorderLayout.CENTER);

        taiDuLieuSanPham();
        xoaTrangForm();
        
        // Thiết lập phím tắt
        thietLapPhimTat();
    }

    /**
     * Thiết lập phím tắt cho các component
     */
    private void thietLapPhimTat() {
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
        
        // Ctrl+F: Focus tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke("control F"), "timKiem");
        actionMap.put("timKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });
        
        // F2: Focus tên sản phẩm
        inputMap.put(KeyStroke.getKeyStroke("F2"), "focusTenSP");
        actionMap.put("focusTenSP", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTenSP.requestFocus();
                txtTenSP.selectAll();
            }
        });
        
        // F5: Làm mới
        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaTrangForm();
                taiDuLieuSanPham();
            }
        });
        
        // Ctrl+N: Thêm mới
        inputMap.put(KeyStroke.getKeyStroke("control N"), "themMoi");
        actionMap.put("themMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (kiemTraDuLieu()) {
                    SanPham sp = layThongTinTuForm();
                    if (sanPhamDAO.themSanPham(sp)) {
                        JOptionPane.showMessageDialog(QuanLySanPham_GUI.this, "Thêm thành công!");
                        taiDuLieuSanPham();
                        xoaTrangForm();
                    } else {
                        JOptionPane.showMessageDialog(QuanLySanPham_GUI.this, "Thêm thất bại!");
                    }
                }
            }
        });
        
        // Ctrl+S: Lưu (Thêm hoặc Cập nhật - Smart Save)
        inputMap.put(KeyStroke.getKeyStroke("control S"), "luu");
        actionMap.put("luu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Kiểm tra xem đang chọn sản phẩm hay không
                if (tblSanPham.getSelectedRow() != -1) {
                    // Đang sửa
                    if (kiemTraDuLieu()) {
                        SanPham sp = layThongTinTuForm();
                        if (sanPhamDAO.capNhatSanPham(sp)) {
                            JOptionPane.showMessageDialog(QuanLySanPham_GUI.this, "Cập nhật thành công!");
                            taiDuLieuSanPham();
                            xoaTrangForm();
                        }
                    }
                } else {
                    // Đang thêm mới
                    if (kiemTraDuLieu()) {
                        SanPham sp = layThongTinTuForm();
                        if (sanPhamDAO.themSanPham(sp)) {
                            JOptionPane.showMessageDialog(QuanLySanPham_GUI.this, "Thêm thành công!");
                            taiDuLieuSanPham();
                            xoaTrangForm();
                        }
                    }
                }
            }
        });
        
        // Ctrl+U: Cập nhật
        inputMap.put(KeyStroke.getKeyStroke("control U"), "capNhat");
        actionMap.put("capNhat", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tblSanPham.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(QuanLySanPham_GUI.this, "Vui lòng chọn sản phẩm cần sửa!");
                    return;
                }
                if (kiemTraDuLieu()) {
                    SanPham sp = layThongTinTuForm();
                    if (sanPhamDAO.capNhatSanPham(sp)) {
                        JOptionPane.showMessageDialog(QuanLySanPham_GUI.this, "Cập nhật thành công!");
                        taiDuLieuSanPham();
                        xoaTrangForm();
                    }
                }
            }
        });
        
        // F7: Thêm quy cách
        inputMap.put(KeyStroke.getKeyStroke("F7"), "themQuyCach");
        actionMap.put("themQuyCach", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyThemQuyCach();
            }
        });
        
        // F8: Xóa quy cách
        inputMap.put(KeyStroke.getKeyStroke("F8"), "xoaQuyCach");
        actionMap.put("xoaQuyCach", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLyXoaQuyCach();
            }
        });
        
        // F9: Sửa quy cách
        inputMap.put(KeyStroke.getKeyStroke("F9"), "suaQuyCach");
        actionMap.put("suaQuyCach", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuLySuaQuyCach();
            }
        });
        
        // Enter trên ô tìm kiếm
        txtTimKiem.addActionListener(e -> timKiemSanPham());
    }

    private void taoPhanDau() {
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
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton(
                "<html>" +
                    "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                    "</center>" +
                "</html>"
            );
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTimKiem.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên, số đăng ký</html>");
        btnTimKiem.addActionListener(e -> timKiemSanPham());
        pnHeader.add(btnTimKiem);
    }

    private void taoPhanGiua() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(taoVienTieuDe("Thông tin sản phẩm"));

        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhap(pnForm); 
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoVungNutBam(pnButton); 
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_TEXT);

        JPanel pnTab1 = new JPanel(new BorderLayout());
        pnTab1.setBackground(Color.WHITE);
        taoBangSanPham(pnTab1);
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

    private void taoFormNhap(JPanel p) {
        lblHinhAnh = new JLabel("", SwingConstants.CENTER);
        lblHinhAnh.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblHinhAnh.setBounds(30, 40, 180, 180);
        datHinhAnh("icon_anh_sp_null.png");
        p.add(lblHinhAnh);

        btnChonAnh = new JButton("Chọn ảnh");
        btnChonAnh.setBounds(60, 230, 120, 30);
        btnChonAnh.setFont(FONT_TEXT);
        btnChonAnh.addActionListener(this);
        p.add(btnChonAnh);

        int xStart = 300, yStart = 30;
        int hText = 35, wLbl = 110, wTxt = 280, gap = 25;
        int xCol2 = xStart + wLbl + wTxt + 50; 

        p.add(taoNhan("Mã SP:", xStart, yStart));
        txtMaSP = taoTruongNhap(xStart + wLbl, yStart, wTxt);
        txtMaSP.setEditable(false);
        txtMaSP.setBackground(new Color(245,245,245));
        p.add(txtMaSP);

        p.add(taoNhan("Trạng thái:", xCol2, yStart));
        cboTrangThai = new JComboBox<>(new String[]{"Đang bán", "Ngừng bán"});
        cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);

        yStart += hText + gap;
        p.add(taoNhan("Tên SP:", xStart, yStart));
        txtTenSP = taoTruongNhap(xStart + wLbl, yStart, wTxt + 50 + wLbl + wTxt - wTxt); 
        txtTenSP.setSize((xCol2 + wLbl + wTxt) - (xStart + wLbl), hText);
        PlaceholderSupport.addPlaceholder(txtTenSP, "Nhập tên sản phẩm (F2)");
        txtTenSP.setToolTipText("<html><b>Phím tắt:</b> F2<br>Nhập tên sản phẩm</html>");
        p.add(txtTenSP);

        yStart += hText + gap;
        p.add(taoNhan("Loại SP:", xStart, yStart));
        cboLoaiSP = new JComboBox<>();
        for (LoaiSanPham l : LoaiSanPham.values()) cboLoaiSP.addItem(l.name());
        cboLoaiSP.setBounds(xStart + wLbl, yStart, wTxt, hText);
        cboLoaiSP.setFont(FONT_TEXT);
        p.add(cboLoaiSP);
        
        p.add(taoNhan("Số ĐK:", xCol2, yStart));
        txtSoDK = taoTruongNhap(xCol2 + wLbl, yStart, wTxt);
        p.add(txtSoDK);

        yStart += hText + gap;
        p.add(taoNhan("Giá nhập:", xStart, yStart));
        txtGiaNhap = taoTruongNhap(xStart + wLbl, yStart, wTxt);
        p.add(txtGiaNhap);

        p.add(taoNhan("Đường dùng:", xCol2, yStart));
        cboDuongDung = new JComboBox<>();
        for (DuongDung d : DuongDung.values()) cboDuongDung.addItem(d.name());
        cboDuongDung.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboDuongDung.setFont(FONT_TEXT);
        p.add(cboDuongDung);
        
        yStart += hText + gap;
        p.add(taoNhan("Kệ bán:", xStart, yStart));
        txtKeBan = taoTruongNhap(xStart + wLbl, yStart, wTxt);
        p.add(txtKeBan);
        
        JLabel lblGiaBan = taoNhan("Giá bán:", xCol2, yStart);
        lblGiaBan.setFont(FONT_BOLD);
        lblGiaBan.setForeground(new Color(199, 0, 0));
        p.add(lblGiaBan);
        
        txtGiaBan = taoTruongNhap(xCol2 + wLbl, yStart, wTxt);
        txtGiaBan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtGiaBan.setForeground(new Color(199, 0, 0));
        txtGiaBan.setEditable(false);
        txtGiaBan.setToolTipText("Giá bán được tính tự động dựa trên Bảng giá hiện hành");
        p.add(txtGiaBan);
    }

    private void taoVungNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY)); 
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(10, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL;

        int w=140, h=45;
        
        btnThem = new PillButton(
                "<html>" +
                    "<center>" +
                        "THÊM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" +
                    "</center>" +
                "</html>"
            );
        btnThem.setFont(FONT_BOLD);
        btnThem.setPreferredSize(new Dimension(w, h));
        btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Thêm sản phẩm mới</html>");
        btnThem.addActionListener(this);
        gbc.gridy=0; 
        p.add(btnThem, gbc);
        
        btnSua = new PillButton(
                "<html>" +
                    "<center>" +
                        "CẬP NHẬT<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" +
                    "</center>" +
                "</html>"
            );
        btnSua.setFont(FONT_BOLD);
        btnSua.setPreferredSize(new Dimension(w, h));
        btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U hoặc Ctrl+S (smart save)<br>Cập nhật sản phẩm đã chọn</html>");
        btnSua.addActionListener(this);
        gbc.gridy=1; 
        p.add(btnSua, gbc);
        
        btnLamMoi = new PillButton(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                    "</center>" +
                "</html>"
            );
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(w, h));
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5 hoặc Ctrl+N<br>Làm mới toàn bộ dữ liệu</html>");
        btnLamMoi.addActionListener(this);
        gbc.gridy=2; 
        p.add(btnLamMoi, gbc);
    }

    private void taoBangSanPham(JPanel p) {
        String[] cols = {"Mã SP", "Tên sản phẩm", "Loại", "Số ĐK", "Đường dùng", "Giá nhập", "Giá bán", "Kệ bán", "Trạng thái"};
        modelSanPham = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSanPham = thietLapBang(modelSanPham);
        
        TableColumnModel cm = tblSanPham.getColumnModel();
        cm.getColumn(0).setPreferredWidth(100); 
        cm.getColumn(1).setPreferredWidth(250);
        cm.getColumn(5).setCellRenderer(new RightAlignRenderer());
        cm.getColumn(6).setCellRenderer(new RightAlignRenderer());
        
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
        
        btnThemQC = new PillButton(
                "<html>" +
                    "<center>" +
                        "Thêm quy cách<br>" +
                        "<span style='font-size:9px; color:#888888;'>(F7)</span>" +
                    "</center>" +
                "</html>"
            );
        btnThemQC.setPreferredSize(new Dimension(150, 40));
        btnThemQC.setToolTipText("<html><b>Phím tắt:</b> F7<br>Thêm quy cách đóng gói mới</html>");
        btnThemQC.addActionListener(this);
        pnToolBar.add(btnThemQC);
        
        btnXoaQC = new PillButton(
                "<html>" +
                    "<center>" +
                        "Xóa quy cách<br>" +
                        "<span style='font-size:9px; color:#888888;'>(F8)</span>" +
                    "</center>" +
                "</html>"
            );
        btnXoaQC.setPreferredSize(new Dimension(150, 40));
        btnXoaQC.setToolTipText("<html><b>Phím tắt:</b> F8<br>Xóa quy cách đã chọn</html>");
        btnXoaQC.addActionListener(this);
        pnToolBar.add(btnXoaQC);
        
        btnSuaQC = new PillButton(
                "<html>" +
                    "<center>" +
                        "Sửa quy cách<br>" +
                        "<span style='font-size:9px; color:#888888;'>(F9)</span>" +
                    "</center>" +
                "</html>"
            );
        btnSuaQC.setPreferredSize(new Dimension(150, 40));
        btnSuaQC.setToolTipText("<html><b>Phím tắt:</b> F9<br>Sửa quy cách đã chọn</html>");
        btnSuaQC.addActionListener(this);
        pnToolBar.add(btnSuaQC);


        
        p.add(pnToolBar, BorderLayout.NORTH);
        
        String[] cols = {"Mã quy cách", "Đơn vị tính", "Hệ số quy đổi", "Tỉ lệ giảm", "Là gốc", "Giá bán"};
        modelQuyCach = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblQuyCach = thietLapBang(modelQuyCach);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblQuyCach.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblQuyCach.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblQuyCach.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        p.add(new JScrollPane(tblQuyCach), BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnThem)) {
            if (kiemTraDuLieu()) {
                SanPham sp = layThongTinTuForm();
                if (sanPhamDAO.themSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    taiDuLieuSanPham();
                    xoaTrangForm();
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
            if (kiemTraDuLieu()) {
                SanPham sp = layThongTinTuForm();
                if (sanPhamDAO.capNhatSanPham(sp)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    taiDuLieuSanPham();
                    xoaTrangForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            }
        }
        else if (o.equals(btnLamMoi)) {
            xoaTrangForm();
        }
        else if (o.equals(btnChonAnh)) {
            chonHinhAnh();
        }
        
        else if (o.equals(btnThemQC)) {
            xuLyThemQuyCach();
        }
        else if (o.equals(btnSuaQC)) {
            xuLySuaQuyCach();
        }
        else if (o.equals(btnXoaQC)) {
            xuLyXoaQuyCach();
        }
    }

    private void xuLyThemQuyCach() {
        String maSP = txtMaSP.getText();
        if (maSP.isEmpty() || sanPhamDAO.laySanPhamTheoMa(maSP) == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm hợp lệ trước khi thêm quy cách!");
            return;
        }
        new QuyCachDongGoi_Dialog(this, maSP, null).setVisible(true);
    }

    private void xuLySuaQuyCach() {
        int row = tblQuyCach.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng quy cách cần sửa!");
            return;
        }
        String maSP = txtMaSP.getText();
        String maQC = tblQuyCach.getValueAt(row, 0).toString();
        new QuyCachDongGoi_Dialog(this, maSP, maQC).setVisible(true);
    }

    private void xuLyXoaQuyCach() {
        int row = tblQuyCach.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng quy cách cần xóa!");
            return;
        }
        String maQC = tblQuyCach.getValueAt(row, 0).toString();
        String isGoc = tblQuyCach.getValueAt(row, 4).toString();

        if (isGoc.equals("Có")) {
            JOptionPane.showMessageDialog(this, "Không thể xóa Đơn vị gốc! Hãy thiết lập đơn vị khác làm gốc trước.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa quy cách " + maQC + " không?", 
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (quyCachDAO.xoaQuyCachDongGoi(maQC)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                taiDuLieuQuyCach(txtMaSP.getText());
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void taoMaSanPhamTuDong() {
        ArrayList<SanPham> list = sanPhamDAO.layTatCaSanPham();
        if (list.isEmpty()) {
            txtMaSP.setText("SP-000001");
        } else {
            int max = 0;
            for(SanPham s : list) {
                if(s.getMaSanPham().startsWith("SP-")) {
                    try {
                        String numPart = s.getMaSanPham().substring(3);
                        int num = Integer.parseInt(numPart);
                        if(num > max) max = num;
                    } catch(NumberFormatException e) {}
                }
            }
            txtMaSP.setText(String.format("SP-%06d", max + 1));
        }
    }

    private void taiDuLieuSanPham() {
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

    private void hienThiThongTinSanPham(int row) {
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
            txtGiaBan.setText(df.format(sp.getGiaBan()));
            txtKeBan.setText(sp.getKeBanSanPham());
            cboTrangThai.setSelectedItem(sp.isHoatDong() ? "Đang bán" : "Ngừng bán");
            
            datHinhAnh(sp.getHinhAnh());
            duongDanAnhHienTai = sp.getHinhAnh();
            
            taiDuLieuQuyCach(ma);
        }
    }

    public void taiDuLieuQuyCach(String maSP) {
        modelQuyCach.setRowCount(0);
        List<QuyCachDongGoi> listQC = quyCachDAO.layDanhSachQuyCachTheoSanPham(maSP);
        
        SanPham spFull = sanPhamDAO.laySanPhamTheoMa(maSP); 
        double giaBanSP = (spFull != null) ? spFull.getGiaBan() : 0;

        if(listQC != null) {
            for(QuyCachDongGoi qc : listQC) {
                double giaBanQuyCach = (giaBanSP * qc.getHeSoQuyDoi()) * (1 - qc.getTiLeGiam());
                
                modelQuyCach.addRow(new Object[] {
                    qc.getMaQuyCach(),
                    qc.getDonViTinh().getTenDonViTinh(),
                    qc.getHeSoQuyDoi(),
                    (int)(qc.getTiLeGiam() * 100) + "%",
                    qc.isDonViGoc() ? "Có" : "Không",
                    df.format(giaBanQuyCach)
                });
            }
        }
    }

    private SanPham layThongTinTuForm() {
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

        return new SanPham(ma, ten, loai, soDK, dd, gn, duongDanAnhHienTai, ke, hd);
    }

    private boolean kiemTraDuLieu() {
        String ten = txtTenSP.getText().trim();
        String gia = txtGiaNhap.getText().trim();

        if(ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên sản phẩm không được để trống!");
            txtTenSP.requestFocus(); return false;
        }
        if(!gia.matches("[0-9,.]+")) { 
            JOptionPane.showMessageDialog(this, "Giá nhập phải là số dương!");
            txtGiaNhap.requestFocus(); return false;
        }
        return true;
    }

    private void xoaTrangForm() {
        taoMaSanPhamTuDong();
        txtTenSP.setText("");
        txtSoDK.setText("");
        txtGiaNhap.setText("");
        txtGiaBan.setText("");
        txtKeBan.setText("");
        cboLoaiSP.setSelectedIndex(0);
        cboDuongDung.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        datHinhAnh("icon_anh_sp_null.png");
        duongDanAnhHienTai = "icon_anh_sp_null.png";
        
        txtTenSP.requestFocus();
        tblSanPham.clearSelection();
        modelQuyCach.setRowCount(0);
    }

    private void timKiemSanPham() {
        String key = txtTimKiem.getText().trim();
        if(key.isEmpty()) {
            taiDuLieuSanPham();
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

    private void chonHinhAnh() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Hình ảnh (JPG, PNG)", "jpg", "png"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            duongDanAnhHienTai = f.getAbsolutePath(); 
            
            ImageIcon icon = new ImageIcon(f.getAbsolutePath());
            lblHinhAnh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
            lblHinhAnh.setText("");
        }
    }

    private void datHinhAnh(String pathOrName) {
        if (pathOrName == null || pathOrName.isEmpty()) pathOrName = "icon_anh_sp_null.png";
        
        URL url = getClass().getResource("/resources/images/" + pathOrName);
        
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
            lblHinhAnh.setIcon(null);
            lblHinhAnh.setText("Không có ảnh");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource().equals(tblSanPham)) {
            hienThiThongTinSanPham(tblSanPham.getSelectedRow());
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private JLabel taoNhan(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TEXT);
        lbl.setBounds(x, y, 100, 35);
        return lbl;
    }
    private JTextField taoTruongNhap(int x, int y, int w) {
        JTextField txt = new JTextField();
        txt.setFont(FONT_TEXT);
        txt.setBounds(x, y, w, 35);
        return txt;
    }
    private PillButton taoNutBam(String text, int w, int h) {
        PillButton btn = new PillButton(text);
        btn.setFont(FONT_BOLD);
        btn.setPreferredSize(new Dimension(w, h));
        btn.addActionListener(this);
        return btn;
    }
    private JTable thietLapBang(DefaultTableModel model) {
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
    private TitledBorder taoVienTieuDe(String title) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
            TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY
        );
    }
    private class RightAlignRenderer extends DefaultTableCellRenderer {
        public RightAlignRenderer() { setHorizontalAlignment(JLabel.RIGHT); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1550, 850);
            f.setContentPane(new QuanLySanPham_GUI());
            f.setVisible(true);
        });
    }
}