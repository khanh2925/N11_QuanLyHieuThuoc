package gui;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import entity.KhachHang; // Vẫn dùng entity để hứng dữ liệu

@SuppressWarnings("serial")
public class KhachHang_NV_GUI extends JPanel implements ActionListener {

    // --- COMPONENTS UI ---
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Form nhập liệu
    private JTextField txtMaKH, txtTenKH, txtSDT, txtNgaySinh;
    private JComboBox<String> cboGioiTinh;

    // Panel Nút bấm (Bên phải form)
    private PillButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    
    // Header (Tìm kiếm)
    private JTextField txtTimKiem;

    // Bảng dữ liệu
    private JTable tblKhachHang;
    private DefaultTableModel modelKhachHang;

    // Dữ liệu giả lập (Thay thế DAO)
    private List<KhachHang> listKH = new ArrayList<>();

    // Utils & Style
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    private final Color COLOR_PRIMARY = new Color(33, 150, 243);
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public KhachHang_NV_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // 1. Tạo dữ liệu giả
        fakeData();
        
        // 2. Khởi tạo giao diện
        initialize();
    }

    private void fakeData() {
        // Tạo vài khách hàng mẫu
        listKH.add(new KhachHang("KH-20251120-0001", "Nguyễn Văn An", true, "0909123456", LocalDate.of(1990, 5, 15)));
        listKH.add(new KhachHang("KH-20251120-0002", "Trần Thị Bích", false, "0912345678", LocalDate.of(1995, 8, 20)));
        listKH.add(new KhachHang("KH-20251120-0003", "Lê Hoàng Nam", true, "0988777666", LocalDate.of(1988, 12, 1)));
        listKH.add(new KhachHang("KH-20251120-0004", "Phạm Thu Hà", false, "0355111222", LocalDate.of(2000, 1, 10)));
    }

    private void initialize() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. HEADER
        taoPhanHeader();
        add(pnHeader, BorderLayout.NORTH);

        // 2. CENTER (SplitPane: Form + Table)
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
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo tên hoặc số điện thoại...");
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

        // --- A. PHẦN TRÊN (TOP): CONTAINER CHỨA FORM VÀ PANEL NÚT ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(createTitledBorder("Thông tin khách hàng"));

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

        // --- B. PHẦN DƯỚI (BOTTOM): BẢNG DANH SÁCH ---
        JPanel pnTable = new JPanel(new BorderLayout());
        pnTable.setBackground(Color.WHITE);
        taoBangDanhSach(pnTable);

        // --- C. TẠO SPLIT PANE ---
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
        splitPane.setDividerLocation(300); // Form KH ngắn hơn SP nên để 300 là đẹp
        splitPane.setResizeWeight(0.0); // Cố định form
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // --- FORM NHẬP LIỆU ---
    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 40;
        int hText = 35, wLbl = 100, wTxt = 300, gap = 25;

        // Cột 2 giống Quản lý NCC
        int xCol2 = xStart + wLbl + wTxt + 50;

        // ===== CỘT 1 =====
        // Mã KH
        p.add(createLabel("Mã KH:", xStart, yStart));
        txtMaKH = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaKH.setEditable(false);
        p.add(txtMaKH);

        // Tên KH
        p.add(createLabel("Tên KH:", xStart, yStart + gap + hText));
        txtTenKH = createTextField(xStart + wLbl, yStart + gap + hText, wTxt);
        p.add(txtTenKH);

        // Giới tính
        p.add(createLabel("Giới tính:", xStart, yStart + (gap + hText) * 2));
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setBounds(xStart + wLbl, yStart + (gap + hText) * 2, wTxt, hText);
        cboGioiTinh.setFont(FONT_TEXT);
        p.add(cboGioiTinh);

        // ===== CỘT 2 =====
        // Số ĐT
        p.add(createLabel("Số ĐT:", xCol2, yStart));
        txtSDT = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtSDT);

        // Ngày sinh
        p.add(createLabel("Ngày sinh:", xCol2, yStart + gap + hText));
        txtNgaySinh = createTextField(xCol2 + wLbl, yStart + gap + hText, wTxt);
        PlaceholderSupport.addPlaceholder(txtNgaySinh, "dd/MM/yyyy");
        p.add(txtNgaySinh);
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

    // --- BẢNG DANH SÁCH ---
    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"STT", "Mã khách hàng", "Tên khách hàng", "Giới tính", "Số điện thoại", "Ngày sinh"};
        modelKhachHang = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKhachHang = setupTable(modelKhachHang);

        // Căn lề & Width
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        
        TableColumnModel cm = tblKhachHang.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50);  // STT
        cm.getColumn(0).setCellRenderer(center);
        cm.getColumn(1).setPreferredWidth(150); // Mã
        cm.getColumn(1).setCellRenderer(center);
        cm.getColumn(3).setCellRenderer(center); // Giới tính
        cm.getColumn(4).setCellRenderer(center); // SĐT
        cm.getColumn(5).setCellRenderer(center); // Ngày sinh

        // Event click
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

    // ==========================================================================
    //                              XỬ LÝ LOGIC (FAKE DATA)
    // ==========================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnThem)) {
            if (validData()) {
                KhachHang kh = getFromForm();
                // Giả lập sinh mã
                kh.setMaKhachHang("KH-20251120-000" + (listKH.size() + 1));
                listKH.add(kh);
                
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
                loadDataLenBang();
                lamMoiForm();
            }
        } 
        else if (o.equals(btnSua)) {
            int row = tblKhachHang.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!"); return;
            }
            if (validData()) {
                KhachHang khMoi = getFromForm();
                // Cập nhật vào list fake
                KhachHang khCu = listKH.get(row);
                khMoi.setMaKhachHang(khCu.getMaKhachHang()); // Giữ nguyên mã
                listKH.set(row, khMoi);
                
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadDataLenBang();
            }
        }
        else if (o.equals(btnXoa)) {
            int row = tblKhachHang.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa!"); return;
            }
            if (JOptionPane.showConfirmDialog(this, "Xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                listKH.remove(row);
                JOptionPane.showMessageDialog(this, "Đã xóa!");
                loadDataLenBang();
                lamMoiForm();
            }
        }
        else if (o.equals(btnLamMoi)) {
            lamMoiForm();
            loadDataLenBang(); // Reset bộ lọc nếu có
        }
    }

    // Load dữ liệu lên form
    private void doToForm(int row) {
        if (row < 0) return;
        KhachHang kh = listKH.get(row);
        
        txtMaKH.setText(kh.getMaKhachHang());
        txtTenKH.setText(kh.getTenKhachHang());
        cboGioiTinh.setSelectedItem(kh.isGioiTinh() ? "Nam" : "Nữ");
        txtSDT.setText(kh.getSoDienThoai());
        if (kh.getNgaySinh() != null) {
            txtNgaySinh.setText(kh.getNgaySinh().format(dtf));
        } else {
            txtNgaySinh.setText("");
        }
    }

    // Load dữ liệu lên bảng
    private void loadDataLenBang() {
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

    private KhachHang getFromForm() {
        String ten = txtTenKH.getText();
        boolean gioiTinh = cboGioiTinh.getSelectedItem().equals("Nam");
        String sdt = txtSDT.getText();
        
        LocalDate ngaySinh = null;
        try {
            // Parse ngày sinh đơn giản (dd/MM/yyyy)
            ngaySinh = LocalDate.parse(txtNgaySinh.getText(), dtf);
        } catch (Exception e) {
            // Nếu lỗi format thì mặc định hoặc để null
            ngaySinh = LocalDate.now(); 
        }

        // Constructor KhachHang(ma, ten, gioiTinh, sdt, ngaySinh)
        return new KhachHang("", ten, gioiTinh, sdt, ngaySinh);
    }

    private void lamMoiForm() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        txtNgaySinh.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtTenKH.requestFocus();
        tblKhachHang.clearSelection();
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().toLowerCase().trim();
        if (kw.isEmpty() || kw.equals("tìm kiếm...")) {
            loadDataLenBang();
            return;
        }
        
        modelKhachHang.setRowCount(0);
        int stt = 1;
        for (KhachHang kh : listKH) {
            if (kh.getTenKhachHang().toLowerCase().contains(kw) || 
                kh.getSoDienThoai().contains(kw)) {
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
    }

    private boolean validData() {
        if(txtTenKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên không được rỗng"); return false;
        }
        if(!txtSDT.getText().matches("\\d{10}")) {
             JOptionPane.showMessageDialog(this, "Số điện thoại phải là 10 chữ số"); return false;
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
            JFrame frame = new JFrame("Quản Lý Khách Hàng (Fake Data)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KhachHang_NV_GUI());
            frame.setVisible(true);
        });
    }
}