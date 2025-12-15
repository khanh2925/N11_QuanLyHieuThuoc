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

@SuppressWarnings("serial")
public class KhachHang_NV_GUI extends JPanel implements ActionListener, DocumentListener, KeyListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu
    private JTextField txtMaKH, txtTenKH, txtSDT, txtNgaySinh;
    private JComboBox<String> cboGioiTinh;

    // Panel Nút bấm (Bên phải form)
    private PillButton btnThem, btnSua, btnLamMoi;
    
    // Header (Tìm kiếm)
    private JTextField txtTimKiem;

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
        
        // F5, Ctrl+N: Làm mới
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "lamMoi");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiForm();
                loadDataLenBang();
            }
        });
    }

    // =====================================================================
    //                              PHẦN HEADER
    // =====================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên hoặc số điện thoại... (F1/Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Gõ để lọc dữ liệu theo thời gian thực</html>");
        pnHeader.add(txtTimKiem);

        // 🔹 Gõ tới đâu lọc tới đó (DocumentListener)
        txtTimKiem.getDocument().addDocumentListener(this);

        // ✅ KHÔNG còn nút Tìm kiếm nữa
    }

    // =====================================================================
    //                              PHẦN CENTER
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
        p.add(txtMaKH);

        p.add(createLabel("Tên KH:", xStart, yStart + gap + hText));
        txtTenKH = createTextField(xStart + wLbl, yStart + gap + hText, wTxt);
        p.add(txtTenKH);
        // hỗ trợ người dùng nhập tên
        txtTenKH.addKeyListener(this);

        p.add(createLabel("Giới tính:", xStart, yStart + (gap + hText) * 2));
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setBounds(xStart + wLbl, yStart + (gap + hText) * 2, wTxt, hText);
        cboGioiTinh.setFont(FONT_TEXT);
        p.add(cboGioiTinh);

        // Cột 2
        p.add(createLabel("Số ĐT:", xCol2, yStart));
        txtSDT = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtSDT);

        p.add(createLabel("Ngày sinh:", xCol2, yStart + gap + hText));
        txtNgaySinh = createTextField(xCol2 + wLbl, yStart + gap + hText, wTxt);
        PlaceholderSupport.addPlaceholder(txtNgaySinh, "dd/MM/yyyy");
        p.add(txtNgaySinh);
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

        btnThem = createPillButton("Thêm", btnW, btnH);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = createPillButton("Cập nhật", btnW, btnH);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnLamMoi = new PillButton(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5/Ctrl+N)</span>" +
                    "</center>" +
                "</html>"
            );
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
        btnLamMoi.addActionListener(this);
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5 hoặc Ctrl+N<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
        gbc.gridy = 3; p.add(btnLamMoi, gbc);
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
            TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY
        );
    }

    // =====================================================================
    //                          Tạo bảng
    // =====================================================================
    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"STT", "Mã khách hàng", "Tên khách hàng", "Giới tính", "Số điện thoại", "Ngày sinh"};
        modelKhachHang = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
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
        if (row < 0) return;

        txtMaKH.setText(tblKhachHang.getValueAt(row, 1).toString());
        txtTenKH.setText(tblKhachHang.getValueAt(row, 2).toString());
        String gt = tblKhachHang.getValueAt(row, 3).toString();
        cboGioiTinh.setSelectedItem(gt);
        txtSDT.setText(tblKhachHang.getValueAt(row, 4).toString());
        txtNgaySinh.setText(tblKhachHang.getValueAt(row, 5).toString());
        txtMaKH.setEditable(false);
    }

    // =====================================================================
    //                          DATA TỪ DAO
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
                    kh.getNgaySinh() != null ? kh.getNgaySinh().format(dtf) : ""
            });
        }
    }

    // =====================================================================
    //                          CRUD BUTTONS
    // =====================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThem)) {
            ThemKH();
        } else if (o.equals(btnSua)) {
            SuaKH();
        } else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang();
        }
    }
    
    // =====================================================================
    //                          VALIDATE + ENTITY
    // =====================================================================
    private boolean validData() {
    	String ten = txtTenKH.getText() != null ? txtTenKH.getText().trim() : "";

    	if (ten.isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Tên khách hàng không được rỗng!!");
    	    return false;
    	}

    	//  Không quá 100 ký tự
    	if (ten.length() > 100) {
    	    JOptionPane.showMessageDialog(this, "Tên khách hàng không được vượt quá 100 ký tự");
    	    return false;
    	}

    	//  Kiểm tra đúng định dạng (viết chữ cái, có dấu, không chứa số hoặc ký tự đặc biệt)
    	String nameRegex = "([A-ZÀ-Ỵ][a-zà-ỹ]+)(\\s[A-ZÀ-Ỵ][a-zà-ỹ]+)*$";
    	if (!ten.matches(nameRegex)) {
    	    JOptionPane.showMessageDialog(this,
    	        "Tên khách hàng phải viết hoa chữ cái đầu mỗi từ và không chứa số hoặc ký tự đặc biệt.");
    	    return false;
    	}

        String sdt = txtSDT.getText() != null ? txtSDT.getText().trim() : "";
        if (!sdt.matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải gồm 10 số và bắt đầu bằng số 0");
            return false;
        }

        String ngaySinhStr = txtNgaySinh.getText().trim();
        try {
            LocalDate ngaySinh = LocalDate.parse(ngaySinhStr,dtf);
            if (ngaySinh.isAfter(LocalDate.now().minusYears(16))) {
                JOptionPane.showMessageDialog(this, "Khách hàng phải ít nhất 16 tuổi");
                return false;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không hợp lệ (định dạng dd/mm/yyyy)");
            return false;
        }

        return true;
    }

    private KhachHang getKhachHangFromForm(String maKH) {
        String ten = txtTenKH.getText().trim();
        boolean gioiTinh = "Nam".equals(cboGioiTinh.getSelectedItem());
        String sdt = txtSDT.getText().trim();
        LocalDate ngaySinh = LocalDate.parse(txtNgaySinh.getText().trim(),dtf);
        return new KhachHang(maKH, ten, gioiTinh, sdt, ngaySinh);
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

        if (!validData()) return;

        KhachHang kh = getKhachHangFromForm(maKH);

        if (kh_dao.capNhatKhachHang(kh)) {
            modelKhachHang.setValueAt(kh.getMaKhachHang(), row, 1);
            modelKhachHang.setValueAt(kh.getTenKhachHang(), row, 2);
            modelKhachHang.setValueAt(kh.isGioiTinh() ? "Nam" : "Nữ", row, 3);
            modelKhachHang.setValueAt(kh.getSoDienThoai(), row, 4);
            modelKhachHang.setValueAt(kh.getNgaySinh().format(dtf), row, 5);
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công");
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ThemKH() {
        if (!validData()) return;

        String maKH = kh_dao.phatSinhMaKhachHangTiepTheo();
        KhachHang kh = getKhachHangFromForm(maKH);

        if (kh_dao.themKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công");
            loadDataLenBang();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void lamMoiForm() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        txtNgaySinh.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtTenKH.requestFocus();
        tblKhachHang.clearSelection();
        if (txtTimKiem != null) txtTimKiem.setText("");
    }

    // =====================================================================
    //                          TÌM KIẾM (DocumentListener)
    // =====================================================================
    private void refreshFilters() {
        if (sorter == null) return;

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
    //                 hỗ trợ nhập tên         
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
	    if (text == null || text.isEmpty()) return;

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

	        if (c == ' ') vietHoa = true;
	    }

	    String ketQua = sb.toString();

	    // 🔹 cập nhật text và giữ caret không nhảy lung tung
	    txtTenKH.setText(ketQua);

	    if (caret > ketQua.length()) caret = ketQua.length();
	    txtTenKH.setCaretPosition(caret);
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
