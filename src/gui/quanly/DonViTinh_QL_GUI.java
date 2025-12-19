package gui.quanly;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import component.button.PillButton;
import dao.DonViTinh_DAO;
import entity.DonViTinh;

@SuppressWarnings("serial")
public class DonViTinh_QL_GUI extends JPanel implements ActionListener {

    // Components UI
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Input fields
    private JTextField txtMaDVT, txtTenDVT;
    
    // Search & Table
    private JTextField txtTimKiem;
    private JTable tblDonViTinh;
    private DefaultTableModel modelDonViTinh;

    // Buttons
    private PillButton btnThem, btnSua, btnLamMoi, btnTimKiem;

    // DAO & data
    private DonViTinh_DAO dvtDAO;
    private List<DonViTinh> dsDonViTinh;
    
    // Style
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public DonViTinh_QL_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        dvtDAO = new DonViTinh_DAO();
        // 1. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER (SplitPane)
        taoPhanCenter();
        add(pnCenter, BorderLayout.CENTER);

        // 3. LOAD DATA
        loadDataLenBang();
        
        // 4. THIẾT LẬP PHÍM TẮT
        setupKeyboardShortcuts();
    }

    // ======================================================================
    //                              PHẦN HEADER
    // ======================================================================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        // Ô tìm kiếm
        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên đơn vị tính... (F1 / Ctrl+F)");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        txtTimKiem.setBackground(Color.WHITE);
        txtTimKiem.setForeground(Color.GRAY);
        txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
        txtTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(txtTimKiem);

        // Nút Tìm kiếm
        btnTimKiem = new PillButton(
                "<html>" +
                        "<center>" +
                        "TÌM KIẾM<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
                        "</center>" +
                        "</html>");
        btnTimKiem.setBounds(560, 22, 180, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.setToolTipText(
                "<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo tên đơn vị tính</html>");
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    // ======================================================================
    //                              PHẦN CENTER
    // ======================================================================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- PHẦN TRÊN: FORM + NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin đơn vị tính"));

        // Form
        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm);
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        // Nút
        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton);
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- PHẦN DƯỚI: BẢNG ---
        JPanel pnTable = new JPanel(new BorderLayout());
        pnTable.setBackground(Color.WHITE);
        taoBangDanhSach(pnTable);

        // --- SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.0);
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {
        int xStart = 100;       
        int yStart = 50;
        int hText = 40;         
        int wTxt = 400;         
        int gap = 30;           

        // Hàng 1: Mã Đơn Vị
        p.add(createLabel("Mã ĐVT:", xStart, yStart));
        txtMaDVT = createTextField(xStart + 100, yStart, wTxt);
        txtMaDVT.setEditable(false); // Mã tự sinh từ DAO
        p.add(txtMaDVT);
        PlaceholderSupport.addPlaceholder(txtMaDVT, dvtDAO.taoMaTuDong());

        // Hàng 2: Tên Đơn Vị Tính
        yStart += hText + gap;
        p.add(createLabel("Tên ĐVT:", xStart, yStart));
        txtTenDVT = createTextField(xStart + 100, yStart, wTxt);
        p.add(txtTenDVT);
        PlaceholderSupport.addPlaceholder(txtTenDVT, "Nhập tên đơn vị tính");
    }

    // Panel nút bên phải
    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0)); 
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY)); 
        
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 0, 8, 0); 
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
        btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Thêm đơn vị tính mới</html>");
        btnThem.addActionListener(this);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = new PillButton(
                "<html>" +
                        "<center>" +
                        "CẬP NHẬT<br>" +
                        "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" +
                        "</center>" +
                        "</html>");
        btnSua.setFont(FONT_BOLD);
        btnSua.setPreferredSize(new Dimension(btnW, btnH));
        btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật thông tin đơn vị tính đang chọn</html>");
        btnSua.addActionListener(this);
        btnSua.setEnabled(false);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnLamMoi = new PillButton(
                "<html>" +
                        "<center>" +
                        "LÀM MỚI<br>" +
                        "<span style='font-size:10px; color:#888888;'>(F5)</span>" +
                        "</center>" +
                        "</html>");
        btnLamMoi.setFont(FONT_BOLD);
        btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
        btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới form nhập liệu</html>");
        btnLamMoi.addActionListener(this);
        gbc.gridy = 2; p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã Đơn Vị Tính", "Tên Đơn Vị Tính"};
        modelDonViTinh = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDonViTinh = setupTable(modelDonViTinh);

        // Căn lề
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setHorizontalAlignment(JLabel.LEFT);

        tblDonViTinh.getColumnModel().getColumn(0).setCellRenderer(center);
        tblDonViTinh.getColumnModel().getColumn(1).setCellRenderer(center);       
        tblDonViTinh.getColumnModel().getColumn(0).setPreferredWidth(200);
        
        // Click: đổ dữ liệu lên form
        tblDonViTinh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doToForm(tblDonViTinh.getSelectedRow());
            }
        });

        JScrollPane scr = new JScrollPane(tblDonViTinh);
        scr.setBorder(createTitledBorder("Danh sách đơn vị tính"));
        p.add(scr, BorderLayout.CENTER);
    }

    // Đổ từ bảng lên form
    private void doToForm(int row) {
        if (row < 0) return;
        txtMaDVT.setText(tblDonViTinh.getValueAt(row, 0).toString());
        txtTenDVT.setText(tblDonViTinh.getValueAt(row, 1).toString());
        txtMaDVT.setEditable(false);
        
        // Disable nút Thêm, Enable nút Cập nhật khi có selection
        btnThem.setEnabled(false);
        btnSua.setEnabled(true);
    }


    /** Load dữ liệu từ DB lên bảng */
    private void loadDataLenBang() {
        modelDonViTinh.setRowCount(0);
        dsDonViTinh = dvtDAO.layTatCaDonViTinh();
        for (DonViTinh dvt : dsDonViTinh) {
            modelDonViTinh.addRow(new Object[]{
                dvt.getMaDonViTinh(),
                dvt.getTenDonViTinh()
            });
        }
    }

    /** Tạo entity từ form với mã truyền vào (dùng cho thêm / sửa) */
    private DonViTinh getFromForm(String maDVT) {
        String ten = txtTenDVT.getText() != null ? txtTenDVT.getText().trim() : "";
        try {
            return new DonViTinh(maDVT, ten);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void lamMoiForm() {
        txtMaDVT.setText("");
        txtTenDVT.setText("");
        txtTenDVT.requestFocus();
        tblDonViTinh.clearSelection();
        
        // Enable nút Thêm, Disable nút Cập nhật khi không có selection
        btnThem.setEnabled(true);
        btnSua.setEnabled(false);
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().trim();
        if (kw.isEmpty() || kw.equalsIgnoreCase("Tìm kiếm theo tên đơn vị tính...")) {
            loadDataLenBang();
            return;
        }
        kw = kw.toLowerCase();

        modelDonViTinh.setRowCount(0);
        if (dsDonViTinh != null) {
            for (DonViTinh dvt : dsDonViTinh) {
                if (dvt.getTenDonViTinh().toLowerCase().contains(kw)) {
                    modelDonViTinh.addRow(new Object[]{
                        dvt.getMaDonViTinh(),
                        dvt.getTenDonViTinh()
                    });
                }
            }
        }
    }

    /** Validate dữ liệu form (tên ĐVT) */
    private boolean validData() {
        String ten = txtTenDVT.getText() != null ? txtTenDVT.getText().trim() : "";
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đơn vị tính không được rỗng!");
            txtTenDVT.requestFocus();
            return false;
        }
        if (ten.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên đơn vị tính không được vượt quá 50 ký tự!");
            txtTenDVT.requestFocus();
            return false;
        }
        return true;
    }

    // ======================================================================
    //                              SỰ KIỆN CRUD
    // ======================================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnThem)) {
            themDonViTinh();
            return;
        } 
        else if (o.equals(btnSua)) {
            
            suaDonViTinh();
            return;

        }
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang();
            return;
        }
    }

    /** Thêm đơn vị tính mới (mã tự sinh như KhachHang_NV_GUI) */
    private void themDonViTinh() {
        if (!validData()) {
            return;
        }

        // Sinh mã mới từ DAO (DVT-xxx)
        String maMoi = dvtDAO.taoMaTuDong();
        if (maMoi == null || maMoi.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không sinh được mã đơn vị tính mới", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DonViTinh dvt = getFromForm(maMoi);
        if (dvt == null) {
            return; // getFromForm đã báo lỗi
        }

        if (dvtDAO.themDonViTinh(dvt)) {
            JOptionPane.showMessageDialog(this, "Thêm đơn vị tính thành công!");
            txtMaDVT.setText(maMoi);
            modelDonViTinh.addRow(new Object[]{
                    dvt.getMaDonViTinh(),
                    dvt.getTenDonViTinh()
                });
            lamMoiForm();        // nếu muốn giữ lại mã thì bỏ dòng này
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng mã hoặc lỗi DB)!");
        }
    }

    /** Cập nhật tên đơn vị tính */
    private void suaDonViTinh() {
        int row = tblDonViTinh.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần sửa!");
            return;
        }
        

        String ma = txtMaDVT.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã đơn vị tính không hợp lệ!");
            return;
        }

        if (!validData()) {
            return;
        }

        DonViTinh dvt = getFromForm(ma);
        if (dvt == null) {
            return;
        }

        if (dvtDAO.capNhatDonViTinh(dvt)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            modelDonViTinh.setValueAt(txtTenDVT.getText(), row, 1);
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    

    // ======================================================================
    //                              UI Helpers
    // ======================================================================
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
    
    /**
     * Thiết lập phím tắt cho màn hình Quản lý Đơn vị tính
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

        // F5: Làm mới
        inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
        actionMap.put("lamMoi", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lamMoiForm();
                loadDataLenBang();
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

        // Ctrl+N: Thêm
        inputMap.put(KeyStroke.getKeyStroke("control N"), "themDVT");
        actionMap.put("themDVT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                themDonViTinh();
            }
        });

        // Ctrl+U: Cập nhật
        inputMap.put(KeyStroke.getKeyStroke("control U"), "capNhatDVT");
        actionMap.put("capNhatDVT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suaDonViTinh();
            }
        });
    }
    
    // Test riêng màn hình
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Đơn Vị Tính");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new DonViTinh_QL_GUI());
            frame.setVisible(true);
        });
    }
}
