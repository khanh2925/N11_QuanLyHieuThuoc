package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.NhanVien_DAO;
import entity.NhanVien;

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

    // DAO & DATA THẬT
    private NhanVien_DAO nvDAO = new NhanVien_DAO();
    private List<NhanVien> dsNhanVien = new ArrayList<>();

    // Utils
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);
    private final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public NhanVien_QL_GUI() {
        setPreferredSize(new Dimension(1537, 850));

        // KHỞI TẠO GIAO DIỆN
        initialize();

        // LOAD DỮ LIỆU THẬT
        loadDataNhanVien();

        // TẠO SẴN MÃ MỚI CHO FORM
        lamMoiForm();

        // Nếu có dữ liệu thì chọn dòng đầu
        if (!dsNhanVien.isEmpty()) {
            tblNhanVien.setRowSelectionInterval(0, 0);
            doToForm(0);
        }
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
    }

    // =====================================================================
    //                              PHẦN HEADER
    // =====================================================================
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
        txtTimKiem.addActionListener(e -> xuLyTimKiem());

        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    // =====================================================================
    //                         PHẦN CENTER (SPLIT PANE)
    // =====================================================================
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

        int xStart = 50, yStart = 30;
        int hText = 35, wLbl = 110, wTxt = 300, gap = 25;

        int xCol2 = xStart + wLbl + wTxt + 120;

        // ===== HÀNG 1 =====
        p.add(createLabel("Mã NV:", xStart, yStart));
        txtMaNV = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaNV.setEditable(false);
        p.add(txtMaNV);

        p.add(createLabel("Trạng thái:", xCol2, yStart));
        cboTrangThai = new JComboBox<>(new String[] { "Đang làm", "Đã nghỉ" });
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
        cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
        cboGioiTinh.setBounds(xStart + wLbl, yStart, wTxt, hText);
        cboGioiTinh.setFont(FONT_TEXT);
        p.add(cboGioiTinh);

        p.add(createLabel("SĐT:", xCol2, yStart));
        txtSDT = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtSDT);

        // ===== HÀNG 4 =====
        yStart += hText + gap;

        p.add(createLabel("Chức vụ:", xStart, yStart));
        cboChucVu = new JComboBox<>(new String[] { "Nhân viên", "Quản lý" });
        cboChucVu.setBounds(xStart + wLbl, yStart, wTxt, hText);
        cboChucVu.setFont(FONT_TEXT);
        p.add(cboChucVu);

        p.add(createLabel("Ca làm:", xCol2, yStart));
        // Entity quy ước: 1=Sáng, 2=Chiều, 3=Tối
        cboCaLam = new JComboBox<>(new String[] { "Sáng", "Chiều", "Tối" });
        cboCaLam.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboCaLam.setFont(FONT_TEXT);
        p.add(cboCaLam);

        // ===== HÀNG 5 =====
        yStart += hText + gap;

        p.add(createLabel("Địa chỉ:", xStart, yStart));
        txtDiaChi = createTextField(xStart + wLbl, yStart, wTxt);
        p.add(txtDiaChi);

        // (Nếu sau này bạn muốn hiển thị ảnh ở bên phải, có thể thêm lblHinhAnh ở đây)
    }

    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        p.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        btnThem = createPillButton("Thêm NV", 140, 45);
        gbc.gridy = 0;
        p.add(btnThem, gbc);

        btnSua = createPillButton("Cập nhật", 140, 45);
        gbc.gridy = 1;
        p.add(btnSua, gbc);

        btnXoa = createPillButton("Thôi việc", 140, 45);
        gbc.gridy = 2;
        p.add(btnXoa, gbc);

        btnLamMoi = createPillButton("Làm mới", 140, 45);
        gbc.gridy = 3;
        p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = { "Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Chức vụ", "Ca làm", "Trạng thái" };
        modelNhanVien = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblNhanVien = setupTable(modelNhanVien);

        // Render màu trạng thái
        tblNhanVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Đang làm".equals(value))
                    lbl.setForeground(new Color(0, 128, 0));
                else
                    lbl.setForeground(Color.RED);
                return lbl;
            }
        });

        tblNhanVien.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doToForm(tblNhanVien.getSelectedRow());
            }
        });

        JScrollPane scr = new JScrollPane(tblNhanVien);
        scr.setBorder(createTitledBorder("Danh sách nhân viên"));
        p.add(scr, BorderLayout.CENTER);
    }

    // =====================================================================
    //                              LOGIC CHÍNH
    // =====================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // 1. THÊM
        if (o.equals(btnThem)) {
            xuLyThem();
        }
        // 2. SỬA
        else if (o.equals(btnSua)) {
            xuLyCapNhat();
        }
        // 3. "XÓA" = CHO NGHỈ (SOFT DELETE)
        else if (o.equals(btnXoa)) {
            xuLyChoNghi();
        }
        // 4. LÀM MỚI
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
        }
        // 5. CHỌN ẢNH (nếu bạn muốn dùng)
        else if (o.equals(btnChonAnh)) {
            chonAnh();
        }
    }

    // ---------- CRUD ----------

    private void xuLyThem() {
        try {
            NhanVien nv = getNhanVienFromForm(true);
            if (nv == null)
                return;

            if (nvDAO.themNhanVien(nv)) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                loadDataNhanVien();
                chonDongTheoMa(nv.getMaNhanVien());
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            // showErrorAndFocus đã hiển thị dialog rồi
        }
    }

    private void xuLyCapNhat() {
        int row = tblNhanVien.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân viên để cập nhật!");
            return;
        }

        try {
            NhanVien nv = getNhanVienFromForm(false);
            if (nv == null)
                return;

            if (nvDAO.capNhatNhanVien(nv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
                loadDataNhanVien();
                chonDongTheoMa(nv.getMaNhanVien());
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            // showErrorAndFocus đã hiển thị dialog rồi
        }
    }

    // KHÔNG XOÁ RECORD -> CHUYỂN SANG "ĐÃ NGHỈ"
    private void xuLyChoNghi() {
        int row = tblNhanVien.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân viên để cho nghỉ!");
            return;
        }

        String maNV = (String) tblNhanVien.getValueAt(row, 0);

        int opt = JOptionPane.showConfirmDialog(this,
                "Đánh dấu nhân viên " + maNV + " là 'Đã nghỉ'? (Không xóa khỏi hệ thống)", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION)
            return;

        if (nvDAO.capNhatTrangThai(maNV, false)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái nhân viên.");
            loadDataNhanVien();
            chonDongTheoMa(maNV);
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =====================================================================
    //                              DATA BẢNG
    // =====================================================================

    private void loadDataNhanVien() {
        dsNhanVien = nvDAO.layTatCaNhanVien();
        modelNhanVien.setRowCount(0);

        for (NhanVien nv : dsNhanVien) {
            modelNhanVien.addRow(new Object[] {
                    nv.getMaNhanVien(),
                    nv.getTenNhanVien(),
                    nv.isGioiTinh() ? "Nam" : "Nữ",
                    formatNgay(nv.getNgaySinh()),
                    nv.getSoDienThoai(),
                    nv.isQuanLy() ? "Quản lý" : "Nhân viên",
                    nv.getTenCaLam(),
                    nv.isTrangThai() ? "Đang làm" : "Đã nghỉ"
            });
        }
    }

    private void doToForm(int row) {
        if (row < 0)
            return;
        String maNV = (String) tblNhanVien.getValueAt(row, 0);
        NhanVien nv = timNhanVienTrongDanhSach(maNV);
        if (nv == null)
            return;

        txtMaNV.setText(nv.getMaNhanVien());
        txtTenNV.setText(nv.getTenNhanVien());
        cboGioiTinh.setSelectedItem(nv.isGioiTinh() ? "Nam" : "Nữ");
        txtNgaySinh.setText(formatNgay(nv.getNgaySinh()));
        txtNgaySinh.setForeground(Color.BLACK);
        txtSDT.setText(nv.getSoDienThoai());
        txtDiaChi.setText(nv.getDiaChi());
        cboChucVu.setSelectedItem(nv.isQuanLy() ? "Quản lý" : "Nhân viên");

        int ca = nv.getCaLam();
        switch (ca) {
            case 1 -> cboCaLam.setSelectedItem("Sáng");
            case 2 -> cboCaLam.setSelectedItem("Chiều");
            case 3 -> cboCaLam.setSelectedItem("Tối");
            default -> cboCaLam.setSelectedIndex(0);
        }

        cboTrangThai.setSelectedItem(nv.isTrangThai() ? "Đang làm" : "Đã nghỉ");
    }

    private NhanVien timNhanVienTrongDanhSach(String maNV) {
        for (NhanVien nv : dsNhanVien) {
            if (nv.getMaNhanVien().equals(maNV))
                return nv;
        }
        return null;
    }

    private void chonDongTheoMa(String maNV) {
        for (int i = 0; i < modelNhanVien.getRowCount(); i++) {
            if (maNV.equals(modelNhanVien.getValueAt(i, 0))) {
                tblNhanVien.setRowSelectionInterval(i, i);
                tblNhanVien.scrollRectToVisible(tblNhanVien.getCellRect(i, 0, true));
                doToForm(i);
                break;
            }
        }
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().trim();
        if (kw.isEmpty()) {
            loadDataNhanVien();
            return;
        }

        List<NhanVien> ketQua = nvDAO.timNhanVien(kw);
        modelNhanVien.setRowCount(0);
        for (NhanVien nv : ketQua) {
            modelNhanVien.addRow(new Object[] {
                    nv.getMaNhanVien(),
                    nv.getTenNhanVien(),
                    nv.isGioiTinh() ? "Nam" : "Nữ",
                    formatNgay(nv.getNgaySinh()),
                    nv.getSoDienThoai(),
                    nv.isQuanLy() ? "Quản lý" : "Nhân viên",
                    nv.getTenCaLam(),
                    nv.isTrangThai() ? "Đang làm" : "Đã nghỉ"
            });
        }
    }

    // =====================================================================
    //                              FORM <-> ENTITY
    // =====================================================================

    private NhanVien getNhanVienFromForm(boolean isThemMoi) {
        // 1. Mã NV
        String maNV;
        if (isThemMoi) {
            maNV = nvDAO.taoMaNhanVienTuDong();
            txtMaNV.setText(maNV);
        } else {
            maNV = txtMaNV.getText().trim();
            if (maNV.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã nhân viên không hợp lệ!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        // 2. Tên NV
        String ten = txtTenNV.getText().trim();
        if (ten.isEmpty()) {
            showErrorAndFocus(txtTenNV, "Tên nhân viên không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
        }

        // 3. Ngày sinh
        String strNgaySinh = txtNgaySinh.getText().trim();
        if (strNgaySinh.isEmpty()) {
            showErrorAndFocus(txtNgaySinh, "Ngày sinh không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
        }

        LocalDate ngaySinh;
        try {
            ngaySinh = LocalDate.parse(strNgaySinh, dfDate);
        } catch (DateTimeParseException e) {
            showErrorAndFocus(txtNgaySinh, "Ngày sinh không đúng định dạng dd/MM/yyyy!", JOptionPane.ERROR_MESSAGE);
            return null; // unreachable vì showErrorAndFocus ném exception, nhưng để rõ ràng
        }

        int age = Period.between(ngaySinh, LocalDate.now()).getYears();
        if (age < 18) {
            showErrorAndFocus(txtNgaySinh, "Nhân viên phải đủ 18 tuổi!", JOptionPane.WARNING_MESSAGE);
        }

        // 4. Giới tính
        boolean gioiTinh = "Nam".equals(cboGioiTinh.getSelectedItem());

        // 5. SĐT
        String sdt = txtSDT.getText().trim();
        if (sdt.isEmpty()) {
            showErrorAndFocus(txtSDT, "Số điện thoại không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
        }
        if (!sdt.matches("^0\\d{9}$")) {
            showErrorAndFocus(txtSDT, "Số điện thoại phải gồm 10 số và bắt đầu bằng 0!", JOptionPane.WARNING_MESSAGE);
        }

        // 6. Địa chỉ
        String diaChi = txtDiaChi.getText().trim();
        if (diaChi.isEmpty()) {
            showErrorAndFocus(txtDiaChi, "Địa chỉ không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
        }

        // 7. Chức vụ
        boolean quanLy = "Quản lý".equals(cboChucVu.getSelectedItem());

        // 8. Ca làm
        String caText = (String) cboCaLam.getSelectedItem();
        int caLam = switch (caText) {
            case "Sáng" -> 1;
            case "Chiều" -> 2;
            case "Tối" -> 3;
            default -> 1;
        };

        // 9. Trạng thái
        boolean trangThai = "Đang làm".equals(cboTrangThai.getSelectedItem());

        // Tạo entity (entity cũng có validate ở setter)
        return new NhanVien(maNV, ten, gioiTinh, ngaySinh, sdt, diaChi, quanLy, caLam, trangThai);
    }

    // =====================================================================
    //                              HELPER
    // =====================================================================

    private String formatNgay(LocalDate d) {
        return d != null ? dfDate.format(d) : "";
    }

    private void lamMoiForm() {
        String newMa = nvDAO.taoMaNhanVienTuDong();
        txtMaNV.setText(newMa);
        txtMaNV.setEditable(false);

        txtTenNV.setText("");
        txtNgaySinh.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        cboGioiTinh.setSelectedIndex(0);
        cboChucVu.setSelectedIndex(0);
        cboCaLam.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        txtTenNV.requestFocus();
        tblNhanVien.clearSelection();
    }

    // --- Helpers UI & Ảnh ---
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
            if (url == null)
                url = getClass().getResource("/images/icon_anh_nv_null.png");
            lblHinhAnh = new JLabel();
            lblHinhAnh.setIcon(new ImageIcon(
                    new ImageIcon(url).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            if (lblHinhAnh != null)
                lblHinhAnh.setText("Ảnh lỗi");
        }
    }

    private void setHinhAnhLocal(String path) {
        if (lblHinhAnh == null)
            return;
        lblHinhAnh.setIcon(new ImageIcon(
                new ImageIcon(path).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
        lblHinhAnh.setText("");
    }

    private void showErrorAndFocus(JTextField txt, String message, int messageType) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Thông báo", messageType);
            txt.requestFocus();
            txt.selectAll();
        });
        throw new IllegalArgumentException(message);
    }

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
        return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY);
    }

    // MAIN TEST
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Nhân Viên");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhanVien_QL_GUI());
            frame.setVisible(true);
        });
    }
}
