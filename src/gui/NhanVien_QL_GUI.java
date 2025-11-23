package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

// 🟢 Class giả lập (Nằm ngay trong file này để không cần tạo file Entity riêng)
class NhanVienFake {
    String ma, ten, sdt, diaChi, ngaySinh, gioiTinh, chucVu, caLam, trangThai;

    public NhanVienFake(String ma, String ten, String sdt, String diaChi, String ngaySinh, String gioiTinh, String chucVu, String caLam, String trangThai) {
        this.ma = ma; this.ten = ten; this.sdt = sdt; this.diaChi = diaChi;
        this.ngaySinh = ngaySinh; this.gioiTinh = gioiTinh; this.chucVu = chucVu;
        this.caLam = caLam; this.trangThai = trangThai;
    }
}

@SuppressWarnings("serial")
public class NhanVien_QL_GUI extends JPanel implements ActionListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu
    private JTextField txtMaNV, txtTenNV, txtSDT, txtDiaChi, txtNgaySinh;
    private JComboBox<String> cboGioiTinh, cboChucVu, cboCaLam, cboTrangThai;
    private JLabel lblHinhAnh;
    private JButton btnChonAnh;
    private String currentImagePath = "icon_anh_nv_null.png";

    // Buttons
    private PillButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    
    // Search & Table
    private JTextField txtTimKiem;
    private JTable tblNhanVien;
    private DefaultTableModel modelNhanVien;

    // 🟢 DATA FAKE (Thay thế Database)
    private List<NhanVienFake> listNV = new ArrayList<>();

    // Utils
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public NhanVien_QL_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // 1. TẠO DỮ LIỆU GIẢ
        fakeData();
        
        // 2. KHỞI TẠO GIAO DIỆN
        initialize();
    }

    private void fakeData() {
        listNV.add(new NhanVienFake("NV-001", "Nguyễn Quản Lý", "0909111222", "TP.HCM", "15/05/1990", "Nam", "Quản lý", "Hành chính", "Đang làm"));
        listNV.add(new NhanVienFake("NV-002", "Trần Thu Hà", "0912333444", "Bình Dương", "20/08/1998", "Nữ", "Nhân viên", "Sáng", "Đang làm"));
        listNV.add(new NhanVienFake("NV-003", "Lê Văn Cường", "0988777666", "Đồng Nai", "01/12/2000", "Nam", "Nhân viên", "Chiều", "Đang làm"));
        listNV.add(new NhanVienFake("NV-004", "Phạm Thị D", "0355111222", "Long An", "10/02/1995", "Nữ", "Nhân viên", "Tối", "Đã nghỉ"));
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // Center (SplitPane)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // Load Data
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã, tên, số điện thoại...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(FONT_BOLD);
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

        // --- A. PHẦN TRÊN: FORM + NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin nhân viên"));

        // 1. Form Nhập Liệu (Center)
        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm); 
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        // 2. Panel Nút (East)
        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton); 
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- B. PHẦN DƯỚI: BẢNG ---
        JPanel pnTable = new JPanel(new BorderLayout());
        pnTable.setBackground(Color.WHITE);
        taoBangDanhSach(pnTable);

        // --- C. SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
        splitPane.setDividerLocation(380); 
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {

        // ===== FORM CHUẨN HOÁ THEO HỆ THỐNG =====
        int xStart = 50, yStart = 30;
        int hText = 35, wLbl = 110, wTxt = 300, gap = 25;

        // Cột 2 (đẩy xa cho thoáng)
        int xCol2 = xStart + wLbl + wTxt + 120;

        // ===== HÀNG 1 =====
        p.add(createLabel("Mã NV:", xStart, yStart));
        txtMaNV = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaNV.setEditable(false);
        p.add(txtMaNV);

        p.add(createLabel("Trạng thái:", xCol2, yStart));
        cboTrangThai = new JComboBox<>(new String[]{"Đang làm", "Đã nghỉ"});
        cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);

        // ===== HÀNG 2 =====
        yStart += hText + gap;

        p.add(createLabel("Họ tên:", xStart, yStart));
        txtTenNV = createTextField(xStart + wLbl, yStart, wTxt);
        p.add(txtTenNV);

        p.add(createLabel("Ngày sinh:", xCol2, yStart));
        txtNgaySinh = createTextField(xCol2 + wLbl, yStart, wTxt);
        PlaceholderSupport.addPlaceholder(txtNgaySinh, "dd/MM/yyyy");
        p.add(txtNgaySinh);

        // ===== HÀNG 3 =====
        yStart += hText + gap;

        p.add(createLabel("Giới tính:", xStart, yStart));
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setBounds(xStart + wLbl, yStart, wTxt, hText);
        cboGioiTinh.setFont(FONT_TEXT);
        p.add(cboGioiTinh);

        p.add(createLabel("SĐT:", xCol2, yStart));
        txtSDT = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtSDT);

        // ===== HÀNG 4 =====
        yStart += hText + gap;

        p.add(createLabel("Chức vụ:", xStart, yStart));
        cboChucVu = new JComboBox<>(new String[]{"Nhân viên", "Quản lý"});
        cboChucVu.setBounds(xStart + wLbl, yStart, wTxt, hText);
        cboChucVu.setFont(FONT_TEXT);
        p.add(cboChucVu);

        p.add(createLabel("Ca làm:", xCol2, yStart));
        cboCaLam = new JComboBox<>(new String[]{"Sáng", "Chiều", "Tối", "Hành chính"});
        cboCaLam.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboCaLam.setFont(FONT_TEXT);
        p.add(cboCaLam);

        // ===== HÀNG 5 =====
        yStart += hText + gap;

        p.add(createLabel("Địa chỉ:", xStart, yStart));
        txtDiaChi = createTextField(xStart + wLbl, yStart, wTxt);
        p.add(txtDiaChi);
    }



    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        p.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(10, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL;

        btnThem = createPillButton("Thêm NV", 140, 45);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = createPillButton("Cập nhật", 140, 45);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnXoa = createPillButton("Xóa NV", 140, 45);
        gbc.gridy = 2; p.add(btnXoa, gbc);

        btnLamMoi = createPillButton("Làm mới", 140, 45);
        gbc.gridy = 3; p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Chức vụ", "Ca làm", "Trạng thái"};
        modelNhanVien = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhanVien = setupTable(modelNhanVien);

        // Render màu trạng thái
        tblNhanVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if("Đang làm".equals(value)) lbl.setForeground(new Color(0, 128, 0));
                else lbl.setForeground(Color.RED);
                return lbl;
            }
        });

        tblNhanVien.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { doToForm(tblNhanVien.getSelectedRow()); }
        });

        JScrollPane scr = new JScrollPane(tblNhanVien);
        scr.setBorder(createTitledBorder("Danh sách nhân viên"));
        p.add(scr, BorderLayout.CENTER);
    }

    // ==========================================================================
    //                              LOGIC FAKE
    // ==========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // 1. THÊM
        if (o.equals(btnThem)) {
            if (validData()) {
                NhanVienFake nv = getFromForm();
                nv.ma = "NV-2025-" + (listNV.size() + 100); // Tự sinh mã giả
                listNV.add(nv);
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công! (Fake)");
                loadDataLenBang();
                lamMoiForm();
            }
        } 
        // 2. SỬA
        else if (o.equals(btnSua)) {
            int row = tblNhanVien.getSelectedRow();
            if (row != -1 && validData()) {
                NhanVienFake nvMoi = getFromForm();
                nvMoi.ma = listNV.get(row).ma; // Giữ nguyên mã
                listNV.set(row, nvMoi);
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadDataLenBang();
            } else JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa!");
        }
        // 3. XÓA
        else if (o.equals(btnXoa)) {
            int row = tblNhanVien.getSelectedRow();
            if (row != -1) {
                if(JOptionPane.showConfirmDialog(this, "Xóa nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    listNV.remove(row);
                    loadDataLenBang();
                    lamMoiForm();
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                }
            } else JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa!");
        }
        // 4. LÀM MỚI
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
        }
        // 5. CHỌN ẢNH
        else if (o.equals(btnChonAnh)) {
            chonAnh();
        }
    }

    private void doToForm(int row) {
        if (row < 0) return;
        NhanVienFake nv = listNV.get(row);
        txtMaNV.setText(nv.ma);
        txtTenNV.setText(nv.ten);
        cboGioiTinh.setSelectedItem(nv.gioiTinh);
        txtNgaySinh.setText(nv.ngaySinh);
        txtSDT.setText(nv.sdt);
        cboChucVu.setSelectedItem(nv.chucVu);
        txtDiaChi.setText(nv.diaChi);
        cboCaLam.setSelectedItem(nv.caLam);
        cboTrangThai.setSelectedItem(nv.trangThai);
    }

    private void loadDataLenBang() {
        modelNhanVien.setRowCount(0);
        for (NhanVienFake nv : listNV) {
            modelNhanVien.addRow(new Object[] {
                nv.ma, nv.ten, nv.gioiTinh, nv.ngaySinh, nv.sdt, nv.chucVu, nv.caLam, nv.trangThai
            });
        }
    }

    private NhanVienFake getFromForm() {
        String ten = txtTenNV.getText();
        String gt = cboGioiTinh.getSelectedItem().toString();
        String ns = txtNgaySinh.getText();
        String sdt = txtSDT.getText();
        String dc = txtDiaChi.getText();
        String cv = cboChucVu.getSelectedItem().toString();
        String ca = cboCaLam.getSelectedItem().toString();
        String tt = cboTrangThai.getSelectedItem().toString();
        return new NhanVienFake("", ten, sdt, dc, ns, gt, cv, ca, tt);
    }

    private void lamMoiForm() {
        txtMaNV.setText(""); txtTenNV.setText(""); txtSDT.setText("");
        txtNgaySinh.setText(""); txtDiaChi.setText("");
        cboGioiTinh.setSelectedIndex(0); cboChucVu.setSelectedIndex(0);
        cboCaLam.setSelectedIndex(0); cboTrangThai.setSelectedIndex(0);
        txtTenNV.requestFocus();
        tblNhanVien.clearSelection();
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().toLowerCase();
        modelNhanVien.setRowCount(0);
        for(NhanVienFake nv : listNV) {
            if(nv.ten.toLowerCase().contains(kw) || nv.sdt.contains(kw) || nv.ma.toLowerCase().contains(kw)) {
                modelNhanVien.addRow(new Object[] {
                    nv.ma, nv.ten, nv.gioiTinh, nv.ngaySinh, nv.sdt, nv.chucVu, nv.caLam, nv.trangThai
                });
            }
        }
    }

    // --- Helpers UI ---
    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image", "jpg", "png"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            setHinhAnhLocal(file.getAbsolutePath());
        }
    }
    private void setHinhAnh(String name) {
        try {
            URL url = getClass().getResource("/images/" + name);
            if(url==null) url = getClass().getResource("/images/icon_anh_nv_null.png");
            lblHinhAnh.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
        } catch(Exception e) { lblHinhAnh.setText("Ảnh lỗi"); }
    }
    private void setHinhAnhLocal(String path) {
        lblHinhAnh.setIcon(new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
        lblHinhAnh.setText("");
    }
    private boolean validData() {
        if(txtTenNV.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Tên trống!"); return false; }
        return true;
    }
    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text); lbl.setFont(FONT_TEXT); lbl.setBounds(x, y, 100, 35); return lbl;
    }
    private JTextField createTextField(int x, int y, int w) {
        JTextField txt = new JTextField(); txt.setFont(FONT_TEXT); txt.setBounds(x, y, w, 35); return txt;
    }
    private PillButton createPillButton(String text, int w, int h) {
        PillButton btn = new PillButton(text); btn.setFont(FONT_BOLD); btn.setPreferredSize(new Dimension(w, h)); btn.addActionListener(this); return btn;
    }
    private JTable setupTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_TEXT); table.setRowHeight(35);
        table.setSelectionBackground(new Color(0xC8E6C9)); table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(FONT_BOLD); table.getTableHeader().setBackground(COLOR_PRIMARY); table.getTableHeader().setForeground(Color.WHITE);
        return table;
    }
    private TitledBorder createTitledBorder(String title) {
        return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title, TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Nhân Viên (Fake Data)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhanVien_QL_GUI());
            frame.setVisible(true);
        });
    }
}