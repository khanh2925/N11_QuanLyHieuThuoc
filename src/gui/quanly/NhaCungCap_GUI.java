package gui.quanly;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

@SuppressWarnings("serial")
public class NhaCungCap_GUI extends JPanel implements ActionListener, MouseListener {

    // Components UI
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu
    private JTextField txtMaNCC, txtTenNCC, txtSDT, txtEmail, txtDiaChi;
    private JComboBox<String> cboTrangThai;

    // Search & Table
    private JTextField txtTimKiem;
    private JTable tblNhaCungCap;
    private DefaultTableModel modelNhaCungCap;

    // Buttons (Đã xóa btnXoa)
    private PillButton btnThem, btnSua, btnLamMoi, btnTimKiem;

    // DAO
    private NhaCungCap_DAO nccDAO;

    // Font & Color
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public NhaCungCap_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // Khởi tạo DAO
        nccDAO = new NhaCungCap_DAO();
        
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER (SplitPane)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 3. LOAD DATA TỪ CSDL
        loadDataLenBang();
        
        // 4. THIẾT LẬP PHÍM TẮT
        thietLapPhimTat();
    }

    // ==========================================================================
    //                              PHẦN HEADER
    // ==========================================================================
    
    /**
     * Thiết lập phím tắt cho các component
     */
    private void thietLapPhimTat() {
        // Lấy InputMap và ActionMap của JPanel chính
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        
        // --- PHÍM TẮT CHO txtTimKiem (F1, Ctrl+F) ---
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "focusTimKiem");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "focusTimKiem");
        actionMap.put("focusTimKiem", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTimKiem.requestFocus();
                txtTimKiem.selectAll();
            }
        });
        
        // --- PHÍM TẮT CHO btnTimKiem (Enter khi đang focus vào txtTimKiem đã có sẵn) ---
        // Không cần thêm vì đã có txtTimKiem.addActionListener(e -> xuLyTimKiem());
        
        // --- PHÍM TẮT CHO btnLamMoi (F5, Ctrl+N) ---
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
    
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Nhập mã hoặc số điện thoại NCC(F1/Ctrl+F)");
        
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        // Sự kiện nhấn Enter để tìm kiếm
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton(
                "<html>" +
                        "<center>" +
                            "Tìm Kiếm<br>" +
                            "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                        "</center>" +
                    "</html>"
                );

        btnTimKiem.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã hoặc SĐT</html>");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    // ==========================================================================
    //                              PHẦN CENTER
    // ==========================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- PHẦN TRÊN (TOP): FORM + NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin nhà cung cấp"));

        // 1. Form Nhập Liệu
        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm);
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        // 2. Panel Nút
        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton);
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- PHẦN DƯỚI (BOTTOM): BẢNG ---
        JPanel pnTable = new JPanel(new BorderLayout());
        pnTable.setBackground(Color.WHITE);
        taoBangDanhSach(pnTable);

        // --- SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
        splitPane.setDividerLocation(300); 
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 40, hText = 35, wLbl = 100, wTxt = 300, gap = 25;
        
        // Cột 1
        p.add(createLabel("Mã NCC:", xStart, yStart));
        txtMaNCC = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaNCC.setEditable(false); // Mã tự sinh, không cho sửa
        txtMaNCC.setBackground(new Color(245, 245, 245)); // Màu xám nhẹ
        p.add(txtMaNCC);

        p.add(createLabel("Tên NCC:", xStart, yStart + gap + hText));
        txtTenNCC = createTextField(xStart + wLbl, yStart + gap + hText, wTxt);
        p.add(txtTenNCC);
        
        p.add(createLabel("SĐT:", xStart, yStart + (gap + hText) * 2));
        txtSDT = createTextField(xStart + wLbl, yStart + (gap + hText) * 2, wTxt);
        p.add(txtSDT);

        // Cột 2
        int xCol2 = xStart + wLbl + wTxt + 50; // Cách cột 1 50px
        
        p.add(createLabel("Email:", xCol2, yStart));
        txtEmail = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtEmail);
        
        p.add(createLabel("Địa chỉ:", xCol2, yStart + gap + hText));
        txtDiaChi = createTextField(xCol2 + wLbl, yStart + gap + hText, wTxt);
        p.add(txtDiaChi);
        
        p.add(createLabel("Trạng thái:", xCol2, yStart + (gap + hText) * 2));
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});
        cboTrangThai.setBounds(xCol2 + wLbl, yStart + (gap + hText) * 2, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);
    }

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

        // Đã xóa nút Xóa ở vị trí này

        btnLamMoi = new PillButton(
                "<html>" +
                    "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5/Ctrl+N)</span>" +
                    "</center>" +
                "</html>"
            );

        gbc.gridy = 2; p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã NCC", "Tên nhà cung cấp", "SĐT", "Email", "Địa chỉ", "Trạng thái"};
        modelNhaCungCap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhaCungCap = setupTable(modelNhaCungCap);
        
        // Setup chiều rộng cột
        tblNhaCungCap.getColumnModel().getColumn(0).setPreferredWidth(150); // Mã
        tblNhaCungCap.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên
        tblNhaCungCap.getColumnModel().getColumn(4).setPreferredWidth(300); // Địa chỉ

        // Custom Renderer cho cột Trạng thái (Xanh/Đỏ)
        tblNhaCungCap.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Hoạt động".equals(value)) {
                    lbl.setForeground(new Color(0, 128, 0));
                    lbl.setFont(FONT_BOLD);
                } else {
                    lbl.setForeground(Color.RED);
                    lbl.setFont(FONT_TEXT);
                }
                return lbl;
            }
        });

        // Đăng ký sự kiện click chuột
        tblNhaCungCap.addMouseListener(this);

        JScrollPane scr = new JScrollPane(tblNhaCungCap);
        scr.setBorder(createTitledBorder("Danh sách nhà cung cấp"));
        p.add(scr, BorderLayout.CENTER);
    }

    // ==========================================================================
    //                              XỬ LÝ LOGIC (DAO)
    // ==========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // --- NÚT THÊM ---
        if (o.equals(btnThem)) {
            if (validData()) {
                NhaCungCap ncc = getFromForm();
                // Sinh mã tự động trước khi thêm
                String maMoi = nccDAO.taoMaTuDong();
                ncc.setMaNhaCungCap(maMoi);
                
                if (nccDAO.themNhaCungCap(ncc)) {
                    JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công: " + maMoi);
                    loadDataLenBang();
                    lamMoiForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } 
        
        // --- NÚT SỬA ---
        else if (o.equals(btnSua)) {
            int row = tblNhaCungCap.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần cập nhật!");
                return;
            }
            if (validData()) {
                NhaCungCap ncc = getFromForm();
                // Khi sửa, mã NCC lấy từ textfield (đã set từ bảng)
                if (nccDAO.capNhatNhaCungCap(ncc)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
                    loadDataLenBang();
                    // Chọn lại dòng vừa sửa
                    tblNhaCungCap.setRowSelectionInterval(row, row);
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        // --- ĐÃ XÓA LOGIC NÚT XÓA ---
        
        // --- NÚT LÀM MỚI ---
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang(); // Reset bảng nếu đang tìm kiếm
        }
    }

    /**
     * Tải dữ liệu từ CSDL lên bảng
     */
    private void loadDataLenBang() {
        modelNhaCungCap.setRowCount(0);
        List<NhaCungCap> list = nccDAO.layTatCaNhaCungCap();
        for (NhaCungCap ncc : list) {
            themDongVaoBang(ncc);
        }
    }

    /**
     * Thêm 1 đối tượng entity vào model bảng
     */
    private void themDongVaoBang(NhaCungCap ncc) {
        modelNhaCungCap.addRow(new Object[]{
            ncc.getMaNhaCungCap(),
            ncc.getTenNhaCungCap(),
            ncc.getSoDienThoai(),
            ncc.getEmail(),
            ncc.getDiaChi(),
            ncc.isHoatDong() ? "Hoạt động" : "Ngừng hoạt động"
        });
    }

    /**
     * Lấy dữ liệu từ form, đóng gói thành Object
     * Mã NCC được set rỗng ở đây, sẽ được xử lý tùy trường hợp Thêm/Sửa
     */
    private NhaCungCap getFromForm() {
        String ma = txtMaNCC.getText();
        String ten = txtTenNCC.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String dc = txtDiaChi.getText().trim();
        boolean hoatDong = cboTrangThai.getSelectedItem().equals("Hoạt động");
        
        NhaCungCap ncc = new NhaCungCap();
        // Nếu mã không rỗng (đã có trên form) thì set vào, nếu rỗng thì constructor mặc định
        if(!ma.isEmpty()) ncc.setMaNhaCungCap(ma); 
        ncc.setTenNhaCungCap(ten);
        ncc.setSoDienThoai(sdt);
        ncc.setEmail(email);
        ncc.setDiaChi(dc);
        ncc.setHoatDong(hoatDong);
        return ncc;
    }

    private void lamMoiForm() {
        // Tự động gợi ý mã mới
        txtMaNCC.setText(nccDAO.taoMaTuDong());
        txtTenNCC.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        cboTrangThai.setSelectedIndex(0);
        txtTenNCC.requestFocus();
        tblNhaCungCap.clearSelection();
    }

    private void xuLyTimKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty() || keyword.equals("Nhập mã hoặc số điện thoại NCC...")) { 
            loadDataLenBang();
            return;
        }
        
        // Gọi DAO tìm kiếm
        NhaCungCap ketQua = nccDAO.timNhaCungCapTheoMaHoacSDT(keyword);
        
        modelNhaCungCap.setRowCount(0);
        if (ketQua != null) {
            themDongVaoBang(ketQua);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhà cung cấp với thông tin: " + keyword);
            loadDataLenBang(); // Load lại toàn bộ nếu không thấy
        }
    }

    private boolean validData() {
        String ten = txtTenNCC.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (ten.isEmpty()) {
            showError("Tên nhà cung cấp không được rỗng", txtTenNCC);
            return false;
        }
        if (!sdt.matches("^0\\d{9}$")) {
            showError("Số điện thoại phải bắt đầu bằng số 0 và có 10 chữ số", txtSDT);
            return false;
        }
        if (!email.isEmpty() && !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            showError("Email không đúng định dạng", txtEmail);
            return false;
        }
        if (diaChi.isEmpty()) {
            showError("Địa chỉ không được rỗng", txtDiaChi);
            return false;
        }
        return true;
    }
    
    private void showError(String mess, JTextField txt) {
        JOptionPane.showMessageDialog(this, mess, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        txt.requestFocus();
        txt.selectAll();
    }

    // ==========================================================================
    //                              UI HELPERS & EVENTS
    // ==========================================================================

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblNhaCungCap.getSelectedRow();
        if (row >= 0) {
            txtMaNCC.setText(modelNhaCungCap.getValueAt(row, 0).toString());
            txtTenNCC.setText(modelNhaCungCap.getValueAt(row, 1).toString());
            txtSDT.setText(modelNhaCungCap.getValueAt(row, 2).toString());
            txtEmail.setText(modelNhaCungCap.getValueAt(row, 3) != null ? modelNhaCungCap.getValueAt(row, 3).toString() : "");
            txtDiaChi.setText(modelNhaCungCap.getValueAt(row, 4).toString());
            
            String trangThai = modelNhaCungCap.getValueAt(row, 5).toString();
            cboTrangThai.setSelectedItem(trangThai.equals("Hoạt động") ? "Hoạt động" : "Ngừng hoạt động");
        }
    }

    // Các method MouseListener chưa dùng đến
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    // --- UI Component Creators ---
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
        
        // Center align cho các cột (trừ Tên và Địa chỉ)
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            if(i!=1 && i!=4) 
                table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
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
            try {
            } catch (Exception e) {
            }
            JFrame frame = new JFrame("Tra cứu phiếu nhập");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 800);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhaCungCap_GUI());
            frame.setVisible(true);
        });
    }
}