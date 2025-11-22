package gui;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

// 🟢 Class giả lập Entity (Nằm ngay trong file này để không bị lỗi validate)
class DummyKM {
    String ma, ten, ngayBD, ngayKT, loai, hinhThuc, trangThai;
    double giaTri, dieuKien;
    int soLuong;

    public DummyKM(String ma, String ten, String ngayBD, String ngayKT, String loai, String hinhThuc, double giaTri, double dieuKien, int soLuong, String trangThai) {
        this.ma = ma; this.ten = ten; this.ngayBD = ngayBD; this.ngayKT = ngayKT;
        this.loai = loai; this.hinhThuc = hinhThuc; this.giaTri = giaTri;
        this.dieuKien = dieuKien; this.soLuong = soLuong; this.trangThai = trangThai;
    }
}

@SuppressWarnings("serial")
public class KhuyenMai_GUI extends JPanel implements ActionListener {

    // UI Components
    private JPanel pnHeader, pnCenter;
    private JSplitPane splitPane;

    // Inputs
    private JTextField txtMaKM, txtTenKM, txtNgayBD, txtNgayKT, txtGiaTri, txtDieuKien, txtSoLuong;
    private JComboBox<String> cboLoaiKM, cboHinhThuc, cboTrangThai;
    
    // Buttons
    private PillButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem, btnChonSP, btnXoaSP;
    private JTextField txtTimKiem;

    // Tables
    private JTable tblKhuyenMai, tblSanPhamApDung;
    private DefaultTableModel modelKhuyenMai, modelSanPhamApDung;

    // 🟢 DATA FAKE (Lưu trên RAM)
    private List<DummyKM> listKM = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#,###");
    private Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
    private Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);

    public KhuyenMai_GUI() {
        setPreferredSize(new Dimension(1537, 850));
        
        // 1. NẠP DATA GIẢ
        fakeData();
        
        // 2. DỰNG GIAO DIỆN
        initialize();
    }

    private void fakeData() {
        listKM.add(new DummyKM("KM001", "Khai trương", "01/11/2025", "30/11/2025", "Theo hóa đơn", "Giảm tiền", 50000, 1000000, 100, "Đang hoạt động"));
        listKM.add(new DummyKM("KM002", "Tri ân khách VIP", "05/11/2025", "15/11/2025", "Theo sản phẩm", "Giảm %", 10, 0, 50, "Hết hạn"));
        listKM.add(new DummyKM("KM003", "Mùa đông không lạnh", "01/12/2025", "31/12/2025", "Theo hóa đơn", "Tặng quà", 0, 500000, 200, "Tạm ngưng"));
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

        // Load Data lên bảng
        loadDataLenBang();
    }

    // ====================== HEADER ======================
    private void taoPhanHeader() {
        pnHeader = new JPanel(null);
        pnHeader.setPreferredSize(new Dimension(1073, 94));
        pnHeader.setBackground(new Color(0xE3F2F5));

        txtTimKiem = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm khuyến mãi...");
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        txtTimKiem.setBounds(25, 17, 500, 60);
        txtTimKiem.setBorder(new RoundedBorder(20));
        pnHeader.add(txtTimKiem);

        btnTimKiem = new PillButton("Tìm kiếm");
        btnTimKiem.setBounds(540, 22, 130, 50);
        btnTimKiem.setFont(FONT_BOLD);
        btnTimKiem.addActionListener(e -> xuLyTimKiem());
        pnHeader.add(btnTimKiem);
    }

    // ====================== CENTER (SPLIT) ======================
    private void taoPhanCenter() {
        pnCenter = new JPanel(new BorderLayout());
        pnCenter.setBackground(Color.WHITE);
        pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- TOP: FORM + BUTTONS ---
        JPanel pnTopWrapper = new JPanel(new BorderLayout());
        pnTopWrapper.setBackground(Color.WHITE);
        pnTopWrapper.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin khuyến mãi", 
            TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY));

        JPanel pnForm = new JPanel(null);
        pnForm.setBackground(Color.WHITE);
        taoFormNhapLieu(pnForm);
        pnTopWrapper.add(pnForm, BorderLayout.CENTER);

        JPanel pnButton = new JPanel();
        pnButton.setBackground(Color.WHITE);
        taoPanelNutBam(pnButton);
        pnTopWrapper.add(pnButton, BorderLayout.EAST);

        // --- BOTTOM: TABS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_TEXT);

        // Tab 1: Danh sách KM
        JPanel pnTab1 = new JPanel(new BorderLayout());
        pnTab1.setBackground(Color.WHITE);
        taoBangDanhSach(pnTab1);
        tabbedPane.addTab("Danh sách khuyến mãi", pnTab1);

        // Tab 2: Sản phẩm áp dụng
        JPanel pnTab2 = new JPanel(new BorderLayout());
        pnTab2.setBackground(Color.WHITE);
        taoBangSanPhamApDung(pnTab2);
        tabbedPane.addTab("Sản phẩm áp dụng", pnTab2);

        // SplitPane
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, tabbedPane);
        splitPane.setDividerLocation(380); 
        splitPane.setResizeWeight(0.0); 
        
        pnCenter.add(splitPane, BorderLayout.CENTER);
    }

    // --- FORM NHẬP LIỆU ---
    private void taoFormNhapLieu(JPanel p) {
        int xStart = 50, yStart = 30;
        int hText = 35, wLbl = 120, wTxt = 320, gap = 25;

        // ĐẨY CỘT 2 SANG PHẢI NHIỀU HƠN CHO THOÁNG
        int xCol2 = xStart + wLbl + wTxt + 120;

        // ===== HÀNG 1 =====
        p.add(createLabel("Mã KM:", xStart, yStart));
        txtMaKM = createTextField(xStart + wLbl, yStart, wTxt);
        txtMaKM.setEditable(false);
        p.add(txtMaKM);

        p.add(createLabel("Tên KM:", xCol2, yStart));
        txtTenKM = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtTenKM);

        // ===== HÀNG 2 =====
        yStart += hText + gap;
        p.add(createLabel("Ngày BĐ:", xStart, yStart));
        txtNgayBD = createTextField(xStart + wLbl, yStart, wTxt);
        p.add(txtNgayBD);

        p.add(createLabel("Ngày KT:", xCol2, yStart));
        txtNgayKT = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtNgayKT);

        // ===== HÀNG 3 =====
        yStart += hText + gap;
        p.add(createLabel("Loại KM:", xStart, yStart));
        cboLoaiKM = new JComboBox<>(new String[]{"Theo hóa đơn", "Theo sản phẩm"});
        cboLoaiKM.setBounds(xStart + wLbl, yStart, wTxt, hText);
        cboLoaiKM.setFont(FONT_TEXT);
        p.add(cboLoaiKM);

        p.add(createLabel("Hình thức:", xCol2, yStart));
        cboHinhThuc = new JComboBox<>(new String[]{"Giảm tiền", "Giảm %", "Tặng quà"});
        cboHinhThuc.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboHinhThuc.setFont(FONT_TEXT);
        p.add(cboHinhThuc);

        // ===== HÀNG 4 =====
        yStart += hText + gap;
        p.add(createLabel("Giá trị:", xStart, yStart));
        txtGiaTri = createTextField(xStart + wLbl, yStart, wTxt);
        p.add(txtGiaTri);

        p.add(createLabel("Điều kiện:", xCol2, yStart));
        txtDieuKien = createTextField(xCol2 + wLbl, yStart, wTxt);
        p.add(txtDieuKien);

        // ===== HÀNG 5 =====
        yStart += hText + gap;
        p.add(createLabel("Số lượng:", xStart, yStart));
        txtSoLuong = createTextField(xStart + wLbl, yStart, wTxt);
        p.add(txtSoLuong);

        p.add(createLabel("Trạng thái:", xCol2, yStart));
        cboTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Tạm ngưng", "Hết hạn"});
        cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
        cboTrangThai.setFont(FONT_TEXT);
        p.add(cboTrangThai);
    }


    // --- PANEL NÚT ---
    private void taoPanelNutBam(JPanel p) {
        p.setPreferredSize(new Dimension(200, 0));
        p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
        p.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(10, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL;

        btnThem = createPillButton("Tạo KM", 140, 45);
        gbc.gridy = 0; p.add(btnThem, gbc);

        btnSua = createPillButton("Cập nhật", 140, 45);
        gbc.gridy = 1; p.add(btnSua, gbc);

        btnXoa = createPillButton("Xóa", 140, 45);
        gbc.gridy = 2; p.add(btnXoa, gbc);

        btnLamMoi = createPillButton("Làm mới", 140, 45);
        gbc.gridy = 3; p.add(btnLamMoi, gbc);
    }

    // --- CÁC BẢNG ---
    private void taoBangDanhSach(JPanel p) {
        String[] cols = {"Mã", "Tên", "Hình thức", "Giá trị", "Bắt đầu", "Kết thúc", "Loại", "Trạng thái"};
        modelKhuyenMai = new DefaultTableModel(cols, 0) {
             @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblKhuyenMai = setupTable(modelKhuyenMai);
        
        // Event Click
        tblKhuyenMai.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { doToForm(tblKhuyenMai.getSelectedRow()); }
        });

        p.add(new JScrollPane(tblKhuyenMai), BorderLayout.CENTER);
    }

    private void taoBangSanPhamApDung(JPanel p) {
        // Toolbar thêm sản phẩm
        JPanel pnTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnTool.setBackground(Color.WHITE);
        btnChonSP = createPillButton("Chọn SP", 120, 35);
        btnXoaSP = createPillButton("Xóa SP", 120, 35);
        pnTool.add(btnChonSP); pnTool.add(btnXoaSP);
        p.add(pnTool, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên sản phẩm", "Đơn vị", "Giá gốc", "Giá KM"};
        modelSanPhamApDung = new DefaultTableModel(cols, 0);
        tblSanPhamApDung = setupTable(modelSanPhamApDung);
        p.add(new JScrollPane(tblSanPhamApDung), BorderLayout.CENTER);
    }

    // ====================== LOGIC (FAKE CRUD) ======================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        // 1. THÊM MỚI (Bất tử: Mã tự sinh, không check gì sất)
        if (o.equals(btnThem)) {
            DummyKM km = getFromForm();
            km.ma = "KM" + (listKM.size() + 1001); // Tự sinh mã: KM1001, KM1002...
            listKM.add(km);
            loadDataLenBang();
            JOptionPane.showMessageDialog(this, "Thêm thành công! (Fake Data)");
            lamMoiForm();
        } 
        
        // 2. CẬP NHẬT
        else if (o.equals(btnSua)) {
            int row = tblKhuyenMai.getSelectedRow();
            if (row != -1) {
                DummyKM kmMoi = getFromForm();
                kmMoi.ma = listKM.get(row).ma; // Giữ nguyên mã cũ
                listKM.set(row, kmMoi);
                loadDataLenBang();
                JOptionPane.showMessageDialog(this, "Cập nhật xong!");
            } else JOptionPane.showMessageDialog(this, "Chọn dòng để sửa!");
        }
        
        // 3. XÓA
        else if (o.equals(btnXoa)) {
            int row = tblKhuyenMai.getSelectedRow();
            if (row != -1) {
                listKM.remove(row);
                loadDataLenBang();
                lamMoiForm();
                JOptionPane.showMessageDialog(this, "Đã xóa!");
            }
        }
        
        // 4. LÀM MỚI
        else if (o.equals(btnLamMoi)) lamMoiForm();

        // 5. CHỌN SẢN PHẨM (Cho tab chi tiết)
        else if (o.equals(btnChonSP)) {
            if("Theo sản phẩm".equals(cboLoaiKM.getSelectedItem())) {
                modelSanPhamApDung.addRow(new Object[]{"SP-FAKE-" + System.currentTimeMillis()%100, "Thuốc mẫu giả lập", "Hộp", "100,000", txtGiaTri.getText()});
            } else {
                JOptionPane.showMessageDialog(this, "KM Hóa đơn không cần chọn SP!");
            }
        }
        else if (o.equals(btnXoaSP)) {
            if(tblSanPhamApDung.getSelectedRow() != -1) modelSanPhamApDung.removeRow(tblSanPhamApDung.getSelectedRow());
        }
    }

    private void doToForm(int row) {
        if (row < 0) return;
        DummyKM km = listKM.get(row);
        txtMaKM.setText(km.ma);
        txtTenKM.setText(km.ten);
        txtNgayBD.setText(km.ngayBD);
        txtNgayKT.setText(km.ngayKT);
        txtGiaTri.setText(String.valueOf((long)km.giaTri));
        txtDieuKien.setText(String.valueOf((long)km.dieuKien));
        txtSoLuong.setText(String.valueOf(km.soLuong));
        cboLoaiKM.setSelectedItem(km.loai);
        cboHinhThuc.setSelectedItem(km.hinhThuc);
        cboTrangThai.setSelectedItem(km.trangThai);
        
        // Fake load chi tiết sản phẩm
        modelSanPhamApDung.setRowCount(0);
        if(km.loai.equals("Theo sản phẩm")) {
            modelSanPhamApDung.addRow(new Object[]{"SP001", "Paracetamol", "Vỉ", "10,000", "9,000"});
        } else {
            modelSanPhamApDung.addRow(new Object[]{"-", "Toàn bộ cửa hàng", "-", "-", "-"});
        }
    }

    private void loadDataLenBang() {
        modelKhuyenMai.setRowCount(0);
        for (DummyKM km : listKM) {
            modelKhuyenMai.addRow(new Object[]{
                km.ma, km.ten, km.hinhThuc, df.format(km.giaTri), 
                km.ngayBD, km.ngayKT, km.loai, km.trangThai
            });
        }
    }

    private DummyKM getFromForm() {
        String ten = txtTenKM.getText();
        String bd = txtNgayBD.getText();
        String kt = txtNgayKT.getText();
        String loai = cboLoaiKM.getSelectedItem().toString();
        String ht = cboHinhThuc.getSelectedItem().toString();
        String tt = cboTrangThai.getSelectedItem().toString();
        
        double gt = 0, dk = 0; 
        int sl = 0;
        try { gt = Double.parseDouble(txtGiaTri.getText().replace(",", "")); } catch(Exception e){}
        try { dk = Double.parseDouble(txtDieuKien.getText().replace(",", "")); } catch(Exception e){}
        try { sl = Integer.parseInt(txtSoLuong.getText().replace(",", "")); } catch(Exception e){}
        
        return new DummyKM("", ten, bd, kt, loai, ht, gt, dk, sl, tt);
    }

    private void lamMoiForm() {
        txtMaKM.setText(""); txtTenKM.setText(""); txtNgayBD.setText(""); txtNgayKT.setText("");
        txtGiaTri.setText(""); txtDieuKien.setText(""); txtSoLuong.setText("");
        cboLoaiKM.setSelectedIndex(0);
        tblKhuyenMai.clearSelection();
        modelSanPhamApDung.setRowCount(0);
    }

    private void xuLyTimKiem() {
        String kw = txtTimKiem.getText().toLowerCase();
        modelKhuyenMai.setRowCount(0);
        for(DummyKM km : listKM) {
            if(km.ten.toLowerCase().contains(kw) || km.ma.toLowerCase().contains(kw)) {
                 modelKhuyenMai.addRow(new Object[]{
                    km.ma, km.ten, km.hinhThuc, df.format(km.giaTri), 
                    km.ngayBD, km.ngayKT, km.loai, km.trangThai
                });
            }
        }
    }

    // --- HELPER UI ---
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
        table.getTableHeader().setBackground(new Color(33, 150, 243));
        table.getTableHeader().setForeground(Color.WHITE);
        return table;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản Lý Khuyến Mãi (Fake Data)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1500, 850);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new KhuyenMai_GUI());
            frame.setVisible(true);
        });
    }
}