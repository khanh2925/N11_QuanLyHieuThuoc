package gui.quanly;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.KhachHang_DAO;
import entity.KhachHang; // Vẫn dùng entity để hứng dữ liệu
import com.toedter.calendar.JDateChooser; // Import JDateChooser

@SuppressWarnings("serial")
public class KhachHang_NV_GUI extends JPanel implements ActionListener, DocumentListener, KeyListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu
    private JTextField txtMaKH, txtTenKH, txtSDT;
    private JDateChooser dateNgaySinh;
    private JComboBox<String> cboGioiTinh;
    private JComboBox<String> cboTrangThai;

    // Panel Nút bấm (Bên phải form)
    private PillButton btnThem, btnSua, btnLamMoi;

    // Header (Tìm kiếm)
    private JTextField txtTimKiem;
    private PillButton btnTimKiem;

    // Bảng dữ liệu
    private JTable tblKhachHang;
    private DefaultTableModel modelKhachHang;
    private TableRowSorter<DefaultTableModel> sorter;

    // Dữ liệu
    private List<KhachHang> listKH = new ArrayList<>();
    private KhachHang_DAO kh_dao;

    // Utils & Style
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public KhachHang_NV_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        kh_dao = new KhachHang_DAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. HEADER (Thanh tìm kiếm)
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER (SplitPane: Form + Table)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 3. LOAD DATA
        loadDataLenBang();

        // 4. THIẾT LẬP PHÍM TẮT
        thietLapPhimTat();

        // 5. AUTO FOCUS
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                SwingUtilities.invokeLater(() -> {
                    txtTimKiem.requestFocusInWindow();
                });
            }
        });
    }

    // =====================================================================
    // PHẦN HEADER
    // =====================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên hoặc số điện thoại... (F1/Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        // txtTimKiem.setForeground(Color.GRAY); // Let PlaceholderSupport handle this
        txtTimKiem.setToolTipText(
                "<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Gõ để lọc dữ liệu theo thời gian thực</html>");
        pnHeader.add(txtTimKiem);

        // 🔹 Gõ tới đâu lọc tới đó (DocumentListener)
        txtTimKiem.getDocument().addDocumentListener(this);

        // Nút Tìm kiếm
        btnTimKiem = new PillButton("<html>" + "<center>" + "TÌM KIẾM<br>"
                + "<span style='font-size:10px; color:#888888;'>(Enter)</span>" + "</center>" + "</html>");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnTimKiem.addActionListener(this);
        pnHeader.add(btnTimKiem);
    }

    // =====================================================================
    // PHẦN CENTER
    // =====================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- A. PHẦN TRÊN (TOP): FORM + NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin khách hàng"));

        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm);
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton);
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- B. PHẦN DƯỚI (BOTTOM): BẢNG ---
        JPanel pnTable = new JPanel(new BorderLayout());
        pnTable.setBackground(Color.WHITE);
        taoBangDanhSach(pnTable);

        // --- SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.0);

        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // --- FORM NHẬP LIỆU ---
    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 40;
        int hText = 35, wLbl = 100, wTxt = 300, gap = 25;

        int xCol2 = xStart + wLbl + wTxt + 50;

        // Cột 1
        p.add(createLabel("Mã KH:", xStart, yStart));
        txtMaKH = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaKH.setEditable(false);
        PlaceholderSupport.addPlaceholder(txtMaKH, kh_dao.phatSinhMaKhachHangTiepTheo());
        p.add(txtMaKH);

        p.add(createLabel("Tên KH:", xStart, yStart + gap + hText));
        txtTenKH = createTextField(xStart + wLbl, yStart + gap + hText, wTxt);
        p.add(txtTenKH);
        PlaceholderSupport.addPlaceholder(txtTenKH, "Nhập tên khách hàng");
        txtTenKH.addKeyListener(this);

        p.add(createLabel("Giới tính:", xStart, yStart + (gap + hText) * 2));
        cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
        cboGioiTinh.setBounds(xStart + wLbl, yStart + (gap + hText) * 2, wTxt, hText);
        cboGioiTinh.setFont(FONT_TEXT);
        p.add(cboGioiTinh);

        // Cột 2
        p.add(createLabel("Số ĐT:", xCol2, yStart));
        txtSDT = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtSDT);
        PlaceholderSupport.addPlaceholder(txtSDT, "Nhập số điện thoại");

        p.add(createLabel("Ngày sinh:", xCol2, yStart + gap + hText));
        dateNgaySinh = new JDateChooser();
        dateNgaySinh.setBounds(xCol2 + wLbl, yStart + gap + hText, wTxt, 35);
        dateNgaySinh.setDateFormatString("dd/MM/yyyy");
        dateNgaySinh.setFont(FONT_TEXT);
        p.add(dateNgaySinh);

        // Trạng thái
        p.add(createLabel("Trạng thái:", xCol2, yStart + (gap + hText) * 2));
        cboTrangThai = new JComboBox<>(new String[] { "Hoạt động", "Ngưng" });
        cboTrangThai.setBounds(xCol2 + wLbl, yStart + (gap + hText) * 2, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);
    }

    // --- PANEL NÚT BÊN PHẢI ---
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
                        "THÊM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" +
                        "</center>" +
                        "</html>");
        btnThem.setFont(FONT_BOLD);
        btnThem.setPreferredSize(new Dimension(btnW, btnH));
        btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Thêm khách hàng mới</html>");
        btnThem.addActionListener(this);
        gbc.gridy = 0;
        p.add(btnThem, gbc);

        btnSua = new PillButton(
                "<html>" +
                        "<center>" +
                        "CẬP NHẬT<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" +
                        "</center>" +
                        "</html>");
        btnSua.setFont(FONT_BOLD);
        btnSua.setPreferredSize(new Dimension(btnW, btnH));
        btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật thông tin khách hàng đang chọn</html>");
        btnSua.addActionListener(this);
        btnSua.setEnabled(false);
        gbc.gridy = 1;
        p.add(btnSua, gbc);

        btnLamMoi = new PillButton(
                "<html>" +
                        "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                        "</center>" +
                        "</html>");
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        btnLamMoi.addActionListener(this);
        gbc.gridy = 2;
        p.add(btnLamMoi, gbc);
    }

    // tạo lable
    private JLabel createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TEXT);
        lbl.setBounds(x, y, 100, 35);
        return lbl;
    }

    // tạo textfield
    private JTextField createTextField(int x, int y, int w) {
        JTextField txt = new JTextField();
        txt.setFont(FONT_TEXT);
        txt.setBounds(x, y, w, 35);
        return txt;
    }

    // tạo button và gán sự kiện
    private PillButton createPillButton(String text, int w, int h) {
        PillButton btn = new PillButton(text);
        btn.setFont(FONT_BOLD);
        btn.setPreferredSize(new Dimension(w, h));
        btn.addActionListener(this);
        return btn;
    }

    // tạo table
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
                TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY);
    }

    // =====================================================================
    // Tạo bảng
    // =====================================================================
    private void taoBangDanhSach(JPanel p) {
        String[] cols = { "STT", "Mã khách hàng", "Tên khách hàng", "Giới tính", "Số điện thoại", "Ngày sinh",
                "Trạng thái" };
        modelKhachHang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblKhachHang = setupTable(modelKhachHang);

        sorter = new TableRowSorter<>(modelKhachHang);
        tblKhachHang.setRowSorter(sorter);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel cm = tblKhachHang.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50);
        cm.getColumn(0).setCellRenderer(center);
        cm.getColumn(1).setPreferredWidth(150);
        cm.getColumn(1).setCellRenderer(center);
        cm.getColumn(3).setCellRenderer(center);
        cm.getColumn(4).setCellRenderer(center);
        cm.getColumn(5).setCellRenderer(center);

        cm.getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);

                String text = value == null ? "" : value.toString().trim();

                if (text.equalsIgnoreCase("Hoạt động")) {
                    lbl.setForeground(new Color(0, 128, 0));
                    lbl.setFont(FONT_BOLD);
                } else if (text.equalsIgnoreCase("Ngừng")) {
                    lbl.setForeground(Color.RED);
                    lbl.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                } else {
                    lbl.setForeground(Color.BLACK);
                }

                return lbl;
            }
        });

        tblKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doToForm(tblKhachHang.getSelectedRow());
            }
        });

        JScrollPane scr = new JScrollPane(tblKhachHang);
        scr.setBorder(createTitledBorder("Danh sách khách hàng"));
        p.add(scr, BorderLayout.CENTER);
    }

    private void doToForm(int row) {
        if (row < 0)
            return;

        txtMaKH.setText(tblKhachHang.getValueAt(row, 1).toString());
        txtMaKH.setForeground(Color.BLACK);

        txtTenKH.setText(tblKhachHang.getValueAt(row, 2).toString());
        txtTenKH.setForeground(Color.BLACK); // Set text to BLACK

        String gt = tblKhachHang.getValueAt(row, 3).toString();
        cboGioiTinh.setSelectedItem(gt);

        txtSDT.setText(tblKhachHang.getValueAt(row, 4).toString());
        txtSDT.setForeground(Color.BLACK); // Set text to BLACK

        String ngaySinhStr = tblKhachHang.getValueAt(row, 5).toString();
        try {
            if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
                dateNgaySinh.setDate(java.sql.Date.valueOf(LocalDate.parse(ngaySinhStr, dtf)));
            } else {
                dateNgaySinh.setDate(null);
            }
        } catch (Exception e) {
            dateNgaySinh.setDate(null);
        }

        String trangThai = tblKhachHang.getValueAt(row, 6).toString();
        cboTrangThai.setSelectedItem(trangThai.equals("Hoạt động") ? "Hoạt động" : "Ngưng");
        txtMaKH.setEditable(false);
        btnSua.setEnabled(true);
        btnThem.setEnabled(false);
    }

    // =====================================================================
    // DATA TỪ DAO
    // =====================================================================
    private void loadDataLenBang() {
        listKH = kh_dao.layTatCaKhachHang();
        modelKhachHang.setRowCount(0);
        int stt = 1;
        for (KhachHang kh : listKH) {
            modelKhachHang.addRow(new Object[] {
                    stt++,
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.isGioiTinh() ? "Nam" : "Nữ",
                    kh.getSoDienThoai(),
                    kh.getNgaySinh() != null ? kh.getNgaySinh().format(dtf) : "",
                    kh.getTrangThaiText()
            });
        }
    }

    // =====================================================================
    // CRUD BUTTONS
    // =====================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThem)) {
            ThemKH();
            return;
        } else if (o.equals(btnSua)) {
            SuaKH();
            return;
        } else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang();
            return;
        } else if (o.equals(btnTimKiem) || o.equals(txtTimKiem)) {
            refreshFilters();
        }
    }

    // =====================================================================
    // VALIDATE + ENTITY
    // =====================================================================
    private boolean validData() {
        String ten = txtTenKH.getText() != null ? txtTenKH.getText().trim() : "";

        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được rỗng!!");
            txtTenKH.requestFocus();
            return false;
        }

        // Không quá 100 ký tự
        if (ten.length() > 100) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được vượt quá 100 ký tự");
            txtTenKH.requestFocus();
            return false;
        }

        // Kiểm tra đúng định dạng (viết chữ cái, có dấu, không chứa số hoặc ký tự đặc
        // biệt)
        String nameRegex = "([A-ZÀ-Ỵ][a-zà-ỹ]+)(\\s[A-ZÀ-Ỵ][a-zà-ỹ]+)*$";
        if (!ten.matches(nameRegex)) {
            JOptionPane.showMessageDialog(this,
                    "Tên khách hàng phải viết hoa chữ cái đầu mỗi từ và không chứa số hoặc ký tự đặc biệt.");
            txtTenKH.requestFocus();
            return false;
        }

        String sdt = txtSDT.getText() != null ? txtSDT.getText().trim() : "";
        if (!sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 số và bắt đầu bằng số 0");
            txtSDT.requestFocus();
            return false;
        }

        java.util.Date d = dateNgaySinh.getDate();
        if (d == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh");
            dateNgaySinh.requestFocus();
            return false;
        }

        try {
			LocalDate ngaySinh = d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
			LocalDate today = LocalDate.now();
			if (ngaySinh.isAfter(today)) {
				JOptionPane.showMessageDialog(this, "Ngày sinh không được sau ngày hiện tại");
				dateNgaySinh.requestFocus();
				return false;
			}
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ");
            dateNgaySinh.requestFocus();
            return false;
        }

        return true;
    }

    private KhachHang getKhachHangFromForm(String maKH) {
        String ten = txtTenKH.getText().trim();
        boolean gioiTinh = "Nam".equals(cboGioiTinh.getSelectedItem());
        String sdt = txtSDT.getText().trim();

        java.util.Date d = dateNgaySinh.getDate();
        LocalDate ngaySinh = d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        boolean hoatDong = "Hoạt động".equals(cboTrangThai.getSelectedItem());
        KhachHang kh = new KhachHang(maKH, ten, gioiTinh, sdt, ngaySinh, hoatDong);
        return kh;
    }

    private void SuaKH() {
        int row = tblKhachHang.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật");
            return;
        }

        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng không hợp lệ");
            return;
        }

        if (!validData())
            return;

        KhachHang kh = getKhachHangFromForm(maKH);

        if (kh_dao.capNhatKhachHang(kh)) {
            modelKhachHang.setValueAt(kh.getMaKhachHang(), row, 1);
            modelKhachHang.setValueAt(kh.getTenKhachHang(), row, 2);
            modelKhachHang.setValueAt(kh.isGioiTinh() ? "Nam" : "Nữ", row, 3);
            modelKhachHang.setValueAt(kh.getSoDienThoai(), row, 4);
            modelKhachHang.setValueAt(kh.getNgaySinh().format(dtf), row, 5);
            modelKhachHang.setValueAt(kh.getTrangThaiText(), row, 6);
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công");
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ThemKH() {
        if (!validData())
            return;

        String maKH = kh_dao.phatSinhMaKhachHangTiepTheo();
        KhachHang kh = getKhachHangFromForm(maKH);

        if (kh_dao.themKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công");
            loadDataLenBang();
            lamMoiForm();
            txtTenKH.requestFocus(); // Focus vào ô tên sau khi thêm xong
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void lamMoiForm() {
        txtMaKH.setText("");
        PlaceholderSupport.addPlaceholder(txtMaKH, kh_dao.phatSinhMaKhachHangTiepTheo());

        txtTenKH.setText("");
        PlaceholderSupport.addPlaceholder(txtTenKH, "Nhập tên khách hàng");

        txtSDT.setText("");
        PlaceholderSupport.addPlaceholder(txtSDT, "Nhập số điện thoại");

        dateNgaySinh.setDate(null); // Reset date to empty or current date if preferred
        cboGioiTinh.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        txtTenKH.requestFocus();
        tblKhachHang.clearSelection();
        if (txtTimKiem != null) {
            txtTimKiem.setText("");
            PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên hoặc số điện thoại... (F1/Ctrl+F)");
            txtTimKiem.requestFocus();
        }

        // Disable nút Cập nhật khi không có selection
        btnSua.setEnabled(false);
        btnThem.setEnabled(true);
    }

    // =====================================================================
    // TÌM KIẾM (DocumentListener)
    // =====================================================================
    private void refreshFilters() {
        if (sorter == null)
            return;

        String text = txtTimKiem.getText().trim();

        // Trống hoặc placeholder → bỏ filter
        if (text.isEmpty() || txtTimKiem.getForeground().equals(Color.GRAY)) {
            sorter.setRowFilter(null);
            return;
        }

        // Lọc theo: Mã KH, Tên KH, SĐT (cột 1, 2, 4)
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1, 2, 4));
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        refreshFilters();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        refreshFilters();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        refreshFilters();
    }

    // =====================================================================
    // hỗ trợ nhập tên
    // =====================================================================
    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getSource() == txtTenKH) {
            xuLyNhapTen();
        }
    }

    private void xuLyNhapTen() {
        String text = txtTenKH.getText();
        if (text == null || text.isEmpty())
            return;

        int caret = txtTenKH.getCaretPosition(); // lưu vị trí con trỏ để không bị nhảy

        // 🔹 B2: viết hoa chữ cái đầu mỗi từ
        StringBuilder sb = new StringBuilder();
        boolean vietHoa = true;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (vietHoa && Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
                vietHoa = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }

            if (c == ' ')
                vietHoa = true;
        }

        String ketQua = sb.toString();

        // 🔹 cập nhật text và giữ caret không nhảy lung tung
        txtTenKH.setText(ketQua);

        if (caret > ketQua.length())
            caret = ketQua.length();
        txtTenKH.setCaretPosition(caret);
    }

    /**
     * Thiết lập phím tắt cho các component
     */
    private void thietLapPhimTat() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // F1, Ctrl+F: Focus vào ô tìm kiếm
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "focusTimKiem");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "focusTimKiem");
        actionMap.put("focusTimKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });

        // F5: Làm mới
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiForm();
                loadDataLenBang();
            }
        });

        // Ctrl+N: Thêm
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "themKH");
        actionMap.put("themKH", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ThemKH();
            }
        });

        // Ctrl+U: Cập nhật
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK), "suaKH");
        actionMap.put("suaKH", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SuaKH();
            }
        });
    }

    // Test riêng
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Khách Hàng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KhachHang_NV_GUI());
            frame.setVisible(true);
        });
    }

}
