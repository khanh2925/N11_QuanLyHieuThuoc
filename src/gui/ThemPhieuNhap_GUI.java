package gui;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.NumberFormatter;

import com.toedter.calendar.JDateChooser;

import connectDB.connectDB;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.LoSanPham_DAO;
import dao.NhaCungCap_DAO;
import dao.NhanVien_DAO;
import dao.PhieuNhap_DAO;
import dao.SanPham_DAO;
import entity.ChiTietPhieuNhap;
import entity.LoSanPham;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;
import entity.SanPham;
import entity.TaiKhoan;

public class ThemPhieuNhap_GUI extends JPanel implements ActionListener {
    private JPanel pnDanhSachDon;
    private JTextField txtSearch;
    private JTextField txtTimNCC;
    private JLabel lblNhaCungCapValue;
    private JLabel lblTongTienHangValue;
    private JButton btnThemLo, btnNhapFile, btnNhapPhieu;
    private JLabel lblQuayLai;
    private JScrollPane scrollPane;
    private JButton btnTimNCC; // *** THÊM: Nút tìm NCC ***

    // ===== DAOs =====
    private SanPham_DAO sanPhamDAO;
    private LoSanPham_DAO loSanPhamDAO;
    private PhieuNhap_DAO phieuNhapDAO;
    private NhaCungCap_DAO nhaCungCapDAO;

    // ===== Formatting =====
    private final DecimalFormat df = new DecimalFormat("#,###");
    private final DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter fmtDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===== Dữ liệu phiên làm việc =====
    private NhaCungCap nhaCungCapDaChon = null;
    private NhanVien nhanVienDangNhap = null;
    private NhapHang_GUI previousPanel;
    private JFrame mainFrame;

    public ThemPhieuNhap_GUI(JFrame frame, NhapHang_GUI previous, NhanVien nhanVien) {
        this.mainFrame = frame;
        this.previousPanel = previous;
        this.nhanVienDangNhap = nhanVien;

        sanPhamDAO = new SanPham_DAO();
        loSanPhamDAO = new LoSanPham_DAO();
        phieuNhapDAO = new PhieuNhap_DAO();
        nhaCungCapDAO = new NhaCungCap_DAO();

        if (this.nhanVienDangNhap == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không có thông tin nhân viên đăng nhập!", "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }

        this.setPreferredSize(new Dimension(1537, 850));
        initialize();
    }

    public ThemPhieuNhap_GUI() {
        this(null, null, new NhanVien_DAO().getNhanVienTheoMa("NV2020102001"));
        
        if(nhanVienDangNhap == null) {
            try {
              TaiKhoan tkTest = new TaiKhoan("TK999999", "tester", "12345678");
              nhanVienDangNhap = new NhanVien("NV9999999999", "Nhân Viên Test", true, 
                    LocalDate.now().minusYears(20), "0987654321", "123 Test, TPHCM", false, 
                    tkTest, "SANG", true);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // ... (Phần Header không đổi) ...
        JPanel pnCotPhaiHead = new JPanel(null);
        pnCotPhaiHead.setPreferredSize(new Dimension(0, 88));
        pnCotPhaiHead.setBackground(new Color(0xE3F2F5));
        add(pnCotPhaiHead, BorderLayout.NORTH);

        txtSearch = new JTextField();
        PlaceholderSupport.addPlaceholder(txtSearch, "Nhập Mã SP để thêm lô");
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBounds(20, 17, 420, 50);
        txtSearch.setBorder(new RoundedBorder(15));
        txtSearch.addActionListener(this);
        pnCotPhaiHead.add(txtSearch);

        btnThemLo = new PillButton("Thêm lô");
        btnThemLo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnThemLo.setBounds(460, 22, 120, 40);
        btnThemLo.addActionListener(this);
        pnCotPhaiHead.add(btnThemLo);

        btnNhapFile = new PillButton("Nhập từ file");
        btnNhapFile.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnNhapFile.setBounds(600, 22, 150, 40);
        btnNhapFile.addActionListener(this);
        pnCotPhaiHead.add(btnNhapFile);
        
        // ... (Phần Center không đổi) ...
        JPanel pnCotPhaiCenter = new JPanel();
        pnCotPhaiCenter.setBackground(Color.WHITE);
        add(pnCotPhaiCenter, BorderLayout.CENTER);
        pnCotPhaiCenter.setBorder(new CompoundBorder(new LineBorder(new Color(0x00C853), 3, true), new EmptyBorder(5, 5, 5, 5)));
        pnCotPhaiCenter.setLayout(new BorderLayout(0, 0));
        pnDanhSachDon = new JPanel();
        pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
        pnDanhSachDon.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(pnDanhSachDon);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pnCotPhaiCenter.add(scrollPane);
        
        // ====== CỘT PHẢI (Thông tin phiếu & nút bấm) ======
        JPanel pnCotPhaiRight = new JPanel();
        pnCotPhaiRight.setPreferredSize(new Dimension(450, 0));
        pnCotPhaiRight.setBackground(Color.WHITE);
        pnCotPhaiRight.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnCotPhaiRight.setLayout(new BoxLayout(pnCotPhaiRight, BoxLayout.Y_AXIS));
        add(pnCotPhaiRight, BorderLayout.EAST);
        
        // ... (Phần thông tin nhân viên không đổi) ...
        JPanel pnNhanVien = new JPanel(new BorderLayout(5, 5));
        pnNhanVien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        pnNhanVien.setOpaque(false);
        JLabel lblNhanVienLabel = new JLabel("Nhân viên:");
        lblNhanVienLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel lblNhanVienValue = new JLabel(nhanVienDangNhap != null ? nhanVienDangNhap.getTenNhanVien() : "N/A");
        lblNhanVienValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblThoiGian = new JLabel(java.time.LocalDateTime.now().format(fmtDateTime), SwingConstants.RIGHT);
        lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnNhanVien.add(lblNhanVienLabel, BorderLayout.WEST);
        pnNhanVien.add(lblNhanVienValue, BorderLayout.CENTER);
        pnNhanVien.add(lblThoiGian, BorderLayout.EAST);
        pnCotPhaiRight.add(pnNhanVien);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));
        JSeparator lineNV = new JSeparator();
        lineNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnCotPhaiRight.add(Box.createVerticalStrut(4));
        pnCotPhaiRight.add(lineNV);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));

        // *** SỬA ĐỔI: Giao diện tìm kiếm NCC ***
        JLabel lblTimNCC = new JLabel("Tìm Nhà Cung Cấp:");
        lblTimNCC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTimNCC.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnCotPhaiRight.add(lblTimNCC);
        pnCotPhaiRight.add(Box.createVerticalStrut(5));

        // Panel chứa ô text và nút tìm
        JPanel pnTimNCC = new JPanel(new BorderLayout(5, 0));
        pnTimNCC.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        txtTimNCC = new JTextField();
        PlaceholderSupport.addPlaceholder(txtTimNCC, "Nhập mã NCC hoặc SĐT rồi nhấn Enter");
        txtTimNCC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimNCC.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xCCCCCC), 1, true),
                new EmptyBorder(5,10,5,10)
        ));
        txtTimNCC.addActionListener(this); // Giữ lại chức năng nhấn Enter
        
        btnTimNCC = new JButton("Tìm...");
        btnTimNCC.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTimNCC.addActionListener(this);

        pnTimNCC.add(txtTimNCC, BorderLayout.CENTER);
        pnTimNCC.add(btnTimNCC, BorderLayout.EAST);
        
        pnCotPhaiRight.add(pnTimNCC);
        pnCotPhaiRight.add(Box.createVerticalStrut(15));

        // ... (Phần còn lại của pnCotPhaiRight không đổi) ...
        lblNhaCungCapValue = makeInfoLabel("Nhà cung cấp:", "Chưa chọn");
        lblTongTienHangValue = makeInfoLabel("Tổng tiền hàng:", "0 đ");
        pnCotPhaiRight.add(lblNhaCungCapValue);
        pnCotPhaiRight.add(lblTongTienHangValue);
        pnCotPhaiRight.add(Box.createVerticalGlue());
        btnNhapPhieu = new PillButton("Nhập Phiếu");
        btnNhapPhieu.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btnNhapPhieu.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNhapPhieu.setMaximumSize(new Dimension(250, 50));
        btnNhapPhieu.addActionListener(this);
        pnCotPhaiRight.add(btnNhapPhieu);
        pnCotPhaiRight.add(Box.createVerticalStrut(10));
        lblQuayLai = new JLabel("Quay lại danh sách phiếu nhập");
        lblQuayLai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblQuayLai.setForeground(Color.BLUE.darker());
        lblQuayLai.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblQuayLai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblQuayLai.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (previousPanel != null && mainFrame != null) {
                    mainFrame.setContentPane(previousPanel);
                    previousPanel.loadDataPhieuNhap();
                    mainFrame.revalidate();
                    mainFrame.repaint();
                } else {
                    JOptionPane.showMessageDialog(ThemPhieuNhap_GUI.this,
                        "Không thể quay lại màn hình trước.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                }
            }
            @Override public void mouseEntered(MouseEvent e) {
                 lblQuayLai.setText("<html><u>Quay lại danh sách phiếu nhập</u></html>");
            }
            @Override public void mouseExited(MouseEvent e) {
                 lblQuayLai.setText("Quay lại danh sách phiếu nhập");
            }
        });
        pnCotPhaiRight.add(lblQuayLai);
    }

    // ... (createDonPanel, makeInfoLabel, capNhatTongTienHang, findComponentByName không đổi)
    private JPanel createDonPanel(LoSanPham lo, double donGiaNhap) {
        if (lo == null || lo.getSanPham() == null) return null;
        SanPham sp = lo.getSanPham();

        JPanel pnDonMau = new JPanel();
        pnDonMau.setPreferredSize(new Dimension(1040, 100));
        pnDonMau.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        pnDonMau.setLayout(null);
        pnDonMau.setBackground(Color.WHITE);
        pnDonMau.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        int centerY = 50;
        pnDonMau.putClientProperty("LoSanPham", lo);
        pnDonMau.putClientProperty("DonGiaNhap", donGiaNhap);

        JLabel lblHinhAnh = new JLabel("Ảnh", SwingConstants.CENTER);
        lblHinhAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblHinhAnh.setBounds(20, centerY - 40, 80, 80);
        pnDonMau.add(lblHinhAnh);

        JLabel lblTenThuoc = new JLabel(sp.getTenSanPham());
        lblTenThuoc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTenThuoc.setBounds(120, centerY - 35, 350, 25);
        pnDonMau.add(lblTenThuoc);

        JLabel lblMaSP = new JLabel("SP: " + sp.getMaSanPham());
        lblMaSP.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMaSP.setForeground(Color.GRAY);
        lblMaSP.setBounds(120, centerY - 10, 150, 20);
        pnDonMau.add(lblMaSP);

        JLabel lblMaLo = new JLabel("Lô: " + lo.getMaLo());
        lblMaLo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMaLo.setForeground(Color.BLUE.darker());
        lblMaLo.setBounds(280, centerY - 10, 190, 20);
        pnDonMau.add(lblMaLo);

        JLabel lblHSD = new JLabel("HSD: " + lo.getHanSuDung().format(fmtDate));
        lblHSD.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHSD.setForeground(Color.RED);
        lblHSD.setBounds(120, centerY + 15, 150, 20);
        pnDonMau.add(lblHSD);

        JPanel pnTangGiam = new JPanel(new BorderLayout(5, 0));
        pnTangGiam.setBounds(500, centerY - 18, 120, 36);
        pnTangGiam.setBackground(new Color(0xF8FAFB));
        pnTangGiam.setBorder(new LineBorder(new Color(0xB0BEC5), 1, true));
        pnDonMau.add(pnTangGiam);
        
        NumberFormatter formatter = new NumberFormatter(new DecimalFormat("#0"));
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField txtSoLuong = new JFormattedTextField(formatter);
        txtSoLuong.setValue(lo.getSoLuongNhap());
        txtSoLuong.setName("txtSoLuong");
        txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
        txtSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtSoLuong.setBorder(null);
        txtSoLuong.setBackground(Color.WHITE);
        pnTangGiam.add(txtSoLuong, BorderLayout.CENTER);
        
        final JLabel lblTongTienValue = new JLabel();
         txtSoLuong.addPropertyChangeListener("value", evt -> {
             if (evt.getNewValue() != null) {
                int sl = (Integer) evt.getNewValue();
                double thanhTien = sl * donGiaNhap;
                lblTongTienValue.setText(df.format(thanhTien) + " đ");
                capNhatTongTienHang();
             }
         });

        JLabel lblDonGia = new JLabel(df.format(donGiaNhap) + " đ");
        lblDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDonGia.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDonGia.setBounds(640, centerY - 15, 120, 30);
        pnDonMau.add(lblDonGia);

        lblTongTienValue.setText(df.format(lo.getSoLuongNhap() * donGiaNhap) + " đ");
        lblTongTienValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongTienValue.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTongTienValue.setBounds(780, centerY - 15, 140, 30);
        pnDonMau.add(lblTongTienValue);

        JButton btnXoa = new JButton("X");
        btnXoa.setBounds(940, centerY - 18, 40, 36);
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXoa.setForeground(Color.RED);
        btnXoa.setBackground(new Color(255, 230, 230));
        btnXoa.setBorder(new LineBorder(Color.RED, 1, true));
        btnXoa.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa lô sản phẩm này khỏi phiếu nhập?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                pnDanhSachDon.remove(pnDonMau);
                capNhatTongTienHang();
                pnDanhSachDon.revalidate();
                pnDanhSachDon.repaint();
            }
        });
        pnDonMau.add(btnXoa);

        return pnDonMau;
    }
    
    private JLabel makeInfoLabel(String labelText, String valueText) {
        JLabel label = new JLabel(String.format("<html>%s <b style='color: #333;'>%s</b></html>", labelText, valueText));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        return label;
    }
    
    private void capNhatTongTienHang() {
        double tongTien = 0;
        Component[] components = pnDanhSachDon.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel panel) {
                try {
                    double donGia = (Double) panel.getClientProperty("DonGiaNhap");
                    JFormattedTextField txtSoLuong = (JFormattedTextField) findComponentByName(panel, "txtSoLuong");
                    if (txtSoLuong != null) {
                        int soLuong = ((Number) txtSoLuong.getValue()).intValue();
                        tongTien += soLuong * donGia;
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi tính tổng tiền từ panel: " + e.getMessage());
                }
            }
        }
        lblTongTienHangValue.setText(String.format("<html>Tổng tiền hàng: <b style='color: red;'>%s đ</b></html>", df.format(tongTien)));
    }
    
    private Component findComponentByName(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (name.equals(comp.getName())) {
                return comp;
            }
            if (comp instanceof Container subContainer) {
                 Component found = findComponentByName(subContainer, name);
                 if (found != null) return found;
            }
        }
        return null;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnThemLo || source == txtSearch) {
            xuLyThemLo();
        } else if (source == btnNhapFile) {
            JOptionPane.showMessageDialog(this, "Chức năng Nhập từ file đang được phát triển.");
        } else if (source == btnNhapPhieu) {
            xuLyNhapPhieu();
        } else if (source == txtTimNCC) {
             xuLyTimNhaCungCap();
        } else if (source == btnTimNCC) { // *** THÊM: Sự kiện cho nút tìm mới ***
             xuLyMoDialogChonNCC();
        }
    }

    // *** THÊM: Phương thức mở Dialog chọn NCC ***
    private void xuLyMoDialogChonNCC() {
        ChonNhaCungCap_Dialog dialog = new ChonNhaCungCap_Dialog(mainFrame);
        dialog.setVisible(true); // Hiển thị dialog và chờ

        // Sau khi dialog đóng, lấy kết quả
        NhaCungCap nccDuocChon = dialog.getSelectedNhaCungCap();
        if (nccDuocChon != null) {
            // Cập nhật thông tin lên GUI
            capNhatThongTinNCC(nccDuocChon);
        }
    }

    // *** CẢI TIẾN: Tách logic cập nhật UI ra phương thức riêng ***
    private void capNhatThongTinNCC(NhaCungCap ncc) {
        nhaCungCapDaChon = ncc;
        txtTimNCC.setText(ncc.getMaNhaCungCap()); // Hiển thị mã lên textfield
        txtTimNCC.setForeground(Color.BLACK);
        lblNhaCungCapValue.setText(String.format("<html>Nhà cung cấp: <b style='color: #333;'>%s (%s)</b></html>",
                                                  ncc.getTenNhaCungCap(), ncc.getMaNhaCungCap()));
    }
    
    private void xuLyTimNhaCungCap() {
         String keyword = txtTimNCC.getText().trim();
         if(keyword.isEmpty()) {
              nhaCungCapDaChon = null;
              lblNhaCungCapValue.setText("<html>Nhà cung cấp: <b style='color: #333;'>Chưa chọn</b></html>");
              return;
         }
         
         NhaCungCap ncc = nhaCungCapDAO.timNhaCungCapTheoMaHoacSDT(keyword);

         if (ncc != null) {
              capNhatThongTinNCC(ncc); // Gọi phương thức cập nhật chung
         } else {
              nhaCungCapDaChon = null;
              lblNhaCungCapValue.setText("<html>Nhà cung cấp: <b style='color: red;'>Không tìm thấy</b></html>");
              txtTimNCC.setForeground(Color.RED);
         }
    }
    
    // ... (Các phương thức xuLyThemLo, xuLyNhapPhieu, kiemTraMaLoTonTaiTrongPanel không đổi)
    private void xuLyThemLo() {
        String maSP = txtSearch.getText().trim();
        if (maSP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Sản Phẩm.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            txtSearch.requestFocus();
            return;
        }

        SanPham sp = sanPhamDAO.getSanPhamTheoMa(maSP);
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm với mã: " + maSP, "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtSearch.selectAll();
            return;
        }

        JTextField txtMaLoField = new JTextField(loSanPhamDAO.taoMaLo());
        txtMaLoField.setEditable(false);
        JDateChooser dateHSDChooser = new JDateChooser();
        dateHSDChooser.setDateFormatString("dd/MM/yyyy");
        dateHSDChooser.setDate(Date.from(LocalDate.now().plusYears(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

        JSpinner spinnerSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        JSpinner spinnerDonGia = new JSpinner(new SpinnerNumberModel(sp.getGiaNhap(), 1.0, 10000000.0, 1000.0));
        spinnerDonGia.setEditor(new JSpinner.NumberEditor(spinnerDonGia, "#,##0.00"));

        JPanel panelDialog = new JPanel(new GridLayout(0, 2, 10, 10));
        panelDialog.add(new JLabel("Mã Lô (tự sinh):")); panelDialog.add(txtMaLoField);
        panelDialog.add(new JLabel("Hạn sử dụng:")); panelDialog.add(dateHSDChooser);
        panelDialog.add(new JLabel("Số lượng nhập:")); panelDialog.add(spinnerSoLuong);
        panelDialog.add(new JLabel("Đơn giá nhập:")); panelDialog.add(spinnerDonGia);

        int result = JOptionPane.showConfirmDialog(this, panelDialog, "Nhập lô cho: " + sp.getTenSanPham(),
                                                   JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String maLo = txtMaLoField.getText();
                Date selectedDate = dateHSDChooser.getDate();
                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn Hạn Sử Dụng.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                LocalDate hsd = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (hsd.isBefore(LocalDate.now().plusDays(30))) {
                     JOptionPane.showMessageDialog(this, "Hạn sử dụng phải lớn hơn 30 ngày kể từ hôm nay.", "Ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
                     return;
                }

                int soLuongNhap = (Integer) spinnerSoLuong.getValue();
                double donGiaNhap = (Double) spinnerDonGia.getValue();

                if (kiemTraMaLoTonTaiTrongPanel(maLo)) {
                    JOptionPane.showMessageDialog(this, "Mã lô " + maLo + " đã tồn tại trong phiếu nhập.", "Trùng mã lô", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LoSanPham lo = new LoSanPham(maLo, hsd, soLuongNhap, soLuongNhap, sp);
                JPanel donPanel = createDonPanel(lo, donGiaNhap);

                if (donPanel != null) {
                    pnDanhSachDon.add(donPanel);
                    pnDanhSachDon.add(Box.createVerticalStrut(5));
                    capNhatTongTienHang();
                    pnDanhSachDon.revalidate();
                    pnDanhSachDon.repaint();
                    SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
                }
                txtSearch.setText("");
                txtSearch.requestFocus();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm lô: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean kiemTraMaLoTonTaiTrongPanel(String maLoCanKiemTra) {
         Component[] components = pnDanhSachDon.getComponents();
         for(Component comp : components) {
              if(comp instanceof JPanel panel) {
                   LoSanPham lo = (LoSanPham) panel.getClientProperty("LoSanPham");
                   if(lo != null && lo.getMaLo().equalsIgnoreCase(maLoCanKiemTra)) {
                        return true;
                   }
              }
         }
         return false;
    }
    
    private void xuLyNhapPhieu() {
        if (nhaCungCapDaChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhà Cung Cấp.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtTimNCC.requestFocus();
            return;
        }
        if (nhanVienDangNhap == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không có thông tin Nhân Viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pnDanhSachDon.getComponentCount() == 0) {
            JOptionPane.showMessageDialog(this, "Phiếu nhập chưa có sản phẩm nào.", "Phiếu nhập rỗng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Xác nhận nhập phiếu với nhà cung cấp '" + nhaCungCapDaChon.getTenNhaCungCap() + "'?",
            "Xác nhận nhập phiếu", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        PhieuNhap phieuNhapMoi = new PhieuNhap();
        phieuNhapMoi.setMaPhieuNhap(phieuNhapDAO.taoMaPhieuNhap());
        phieuNhapMoi.setNgayNhap(LocalDate.now());
        phieuNhapMoi.setNhanVien(nhanVienDangNhap);
        phieuNhapMoi.setNhaCungCap(nhaCungCapDaChon);

        List<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();
        Component[] components = pnDanhSachDon.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel panel) {
                try {
                    LoSanPham lo = (LoSanPham) panel.getClientProperty("LoSanPham");
                    Double donGia = (Double) panel.getClientProperty("DonGiaNhap");
                    JFormattedTextField txtSoLuong = (JFormattedTextField) findComponentByName(panel, "txtSoLuong");

                    int soLuongHienTai = ((Number) txtSoLuong.getValue()).intValue();
                    if (soLuongHienTai <= 0) {
                         throw new Exception("Số lượng nhập phải > 0 cho lô " + lo.getMaLo());
                    }
                    lo.setSoLuongNhap(soLuongHienTai);
                    lo.setSoLuongTon(soLuongHienTai);
                    dsChiTiet.add(new ChiTietPhieuNhap(phieuNhapMoi, lo, soLuongHienTai, donGia));

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi xử lý dữ liệu từ panel:\n" + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        phieuNhapMoi.setChiTietPhieuNhapList(dsChiTiet);
        boolean success = phieuNhapDAO.themPhieuNhap(phieuNhapMoi);

        if (success) {
            JOptionPane.showMessageDialog(this, "Nhập phiếu thành công!\nMã phiếu: " + phieuNhapMoi.getMaPhieuNhap(),
                                          "Thành công", JOptionPane.INFORMATION_MESSAGE);
            pnDanhSachDon.removeAll();
            capNhatTongTienHang();
            nhaCungCapDaChon = null;
            txtTimNCC.setText("");
            txtTimNCC.setForeground(Color.BLACK);
            lblNhaCungCapValue.setText("<html>Nhà cung cấp: <b style='color: #333;'>Chưa chọn</b></html>");
            pnDanhSachDon.revalidate();
            pnDanhSachDon.repaint();
            if (previousPanel != null && mainFrame != null) {
                 lblQuayLai.getMouseListeners()[0].mouseClicked(null);
             }
        } else {
            JOptionPane.showMessageDialog(this, "Nhập phiếu thất bại! Vui lòng kiểm tra log lỗi.",
                                          "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}