package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.NhaCungCap; // Vẫn dùng entity để hứng dữ liệu

@SuppressWarnings("serial")
public class NhaCungCap_GUI extends JPanel implements ActionListener {

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

    // Buttons
    private PillButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

    // Dữ liệu giả lập (Thay thế DAO)
    private List<NhaCungCap> listNCC = new ArrayList<>();

    // Font & Color
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);

    public NhaCungCap_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // Tạo dữ liệu giả
        fakeData();
        
        initialize();
    }

    private void fakeData() {
        listNCC.add(new NhaCungCap("NCC-20251101-0001", "Công Ty Dược Phẩm A", "0901234567", "123 QL1A, TP.HCM", "contact@duocphama.com"));
        listNCC.add(new NhaCungCap("NCC-20251101-0002", "Vimedimex Group", "0283838383", "246 Cống Quỳnh, Q.1", "sales@vimedimex.vn"));
        listNCC.add(new NhaCungCap("NCC-20251101-0003", "Zuellig Pharma", "0999888777", "KCN Tân Tạo, Bình Tân", "info@zuellig.com"));
        // Set trạng thái giả
        listNCC.get(2).setHoatDong(false); // Zuellig ngừng
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

        // 3. LOAD DATA
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã, tên, sđt nhà cung cấp...");
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
        splitPane.setDividerLocation(300); // Form NCC cao hơn ĐVT xíu
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 40, hText = 35, wLbl = 100, wTxt = 300, gap = 25;
        
        // Cột 1
        p.add(createLabel("Mã NCC:", xStart, yStart));
        txtMaNCC = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaNCC.setEditable(false); // Mã tự sinh
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
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Ngừng"});
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

        btnXoa = createPillButton("Xóa", btnW, btnH);
        gbc.gridy = 2; p.add(btnXoa, gbc);

        btnLamMoi = createPillButton("Làm mới", btnW, btnH);
        gbc.gridy = 3; p.add(btnLamMoi, gbc);
    }

    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã NCC", "Tên nhà cung cấp", "SĐT", "Email", "Địa chỉ", "Trạng thái"};
        modelNhaCungCap = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblNhaCungCap = setupTable(modelNhaCungCap);
        
        // Width columns
        tblNhaCungCap.getColumnModel().getColumn(0).setPreferredWidth(150); // Mã
        tblNhaCungCap.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên
        tblNhaCungCap.getColumnModel().getColumn(4).setPreferredWidth(300); // Địa chỉ

        // Render Trạng thái
        tblNhaCungCap.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Hoạt động".equals(value)) lbl.setForeground(new Color(0, 128, 0));
                else lbl.setForeground(Color.RED);
                return lbl;
            }
        });

        // Click event
        tblNhaCungCap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                doToForm(tblNhaCungCap.getSelectedRow());
            }
        });

        JScrollPane scr = new JScrollPane(tblNhaCungCap);
        scr.setBorder(createTitledBorder("Danh sách nhà cung cấp"));
        p.add(scr, BorderLayout.CENTER);
    }

    // ==========================================================================
    //                              XỬ LÝ LOGIC (FAKE)
    // ==========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnThem)) {
            if (validData()) {
                NhaCungCap ncc = getFromForm();
                // Giả lập thêm: Sinh mã mới
                ncc.setMaNhaCungCap("NCC-20251120-" + (listNCC.size() + 1));
                listNCC.add(ncc);
                
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
                loadDataLenBang();
                lamMoiForm();
            }
        } 
        else if (o.equals(btnSua)) {
            int row = tblNhaCungCap.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn dòng cần sửa!"); return;
            }
            if (validData()) {
                NhaCungCap nccMoi = getFromForm();
                // Cập nhật vào list giả
                String maCu = listNCC.get(row).getMaNhaCungCap();
                nccMoi.setMaNhaCungCap(maCu); // Giữ nguyên mã
                listNCC.set(row, nccMoi);
                
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadDataLenBang();
            }
        }
        else if (o.equals(btnXoa)) {
            int row = tblNhaCungCap.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa!"); return;
            }
            if (JOptionPane.showConfirmDialog(this, "Xóa NCC này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                listNCC.remove(row);
                JOptionPane.showMessageDialog(this, "Đã xóa!");
                loadDataLenBang();
                lamMoiForm();
            }
        }
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang(); // Reset lại bảng nếu đang tìm kiếm
        }
        else if (o.equals(btnTimKiem)) { // Nút tìm kiếm (nếu bạn click nút thay vì enter)
             xuLyTimKiem();
        }
    }

    private void doToForm(int row) {
        if (row < 0) return;
        // Lấy từ list thay vì lấy từ bảng để đảm bảo dữ liệu gốc
        NhaCungCap ncc = listNCC.get(row); 
        
        txtMaNCC.setText(ncc.getMaNhaCungCap());
        txtTenNCC.setText(ncc.getTenNhaCungCap());
        txtSDT.setText(ncc.getSoDienThoai());
        txtEmail.setText(ncc.getEmail());
        txtDiaChi.setText(ncc.getDiaChi());
        cboTrangThai.setSelectedItem(ncc.isHoatDong() ? "Hoạt động" : "Ngừng");
    }

    private void loadDataLenBang() {
        modelNhaCungCap.setRowCount(0);
        for (NhaCungCap ncc : listNCC) {
            modelNhaCungCap.addRow(new Object[]{
                ncc.getMaNhaCungCap(),
                ncc.getTenNhaCungCap(),
                ncc.getSoDienThoai(),
                ncc.getEmail(),
                ncc.getDiaChi(),
                ncc.isHoatDong() ? "Hoạt động" : "Ngừng"
            });
        }
    }

    private NhaCungCap getFromForm() {
        String ten = txtTenNCC.getText();
        String sdt = txtSDT.getText();
        String email = txtEmail.getText();
        String dc = txtDiaChi.getText();
        // Mã sẽ tự sinh hoặc lấy từ form
        NhaCungCap ncc = new NhaCungCap("", ten, sdt, dc, email);
        ncc.setHoatDong(cboTrangThai.getSelectedItem().equals("Hoạt động"));
        return ncc;
    }

    private void lamMoiForm() {
        txtMaNCC.setText("");
        txtTenNCC.setText("");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        cboTrangThai.setSelectedIndex(0);
        txtTenNCC.requestFocus();
        tblNhaCungCap.clearSelection();
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().toLowerCase().trim();
        if (kw.isEmpty() || kw.equals("tìm kiếm...")) { // Check placeholder
            loadDataLenBang();
            return;
        }
        
        modelNhaCungCap.setRowCount(0);
        for (NhaCungCap ncc : listNCC) {
            if (ncc.getTenNhaCungCap().toLowerCase().contains(kw) || 
                ncc.getSoDienThoai().contains(kw)) {
                modelNhaCungCap.addRow(new Object[]{
                    ncc.getMaNhaCungCap(), ncc.getTenNhaCungCap(), ncc.getSoDienThoai(),
                    ncc.getEmail(), ncc.getDiaChi(), ncc.isHoatDong() ? "Hoạt động" : "Ngừng"
                });
            }
        }
    }

    private boolean validData() {
        if (txtTenNCC.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên không được rỗng"); return false;
        }
        if (!txtSDT.getText().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "SĐT phải là 10 chữ số"); return false;
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
        
        // Center align
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<table.getColumnCount(); i++) {
            if(i!=1 && i!=4) // Trừ Tên và Địa chỉ
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
            JFrame frame = new JFrame("Quản Lý Nhà Cung Cấp (Fake Data)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NhaCungCap_GUI());
            frame.setVisible(true);
        });
    }
}