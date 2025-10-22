package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.LineBorder;

import entity.NhanVien;
import entity.TaiKhoan;

public class ThemNhanVien_Dialog extends JDialog {

    private JTextField txtTenNhanVien;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JPasswordField txtMatKhau;
    
    // Thêm các component mới
    private JComboBox<String> cmbNgay, cmbThang, cmbNam;
    private JRadioButton radNam, radNu;
    private JCheckBox chkQuanLy;
    private JComboBox<String> cmbCaLam;
    
    private JButton btnThem;
    private JButton btnThoat;

    private NhanVien nhanVienMoi = null;

    public ThemNhanVien_Dialog(Frame owner) {
        super(owner, "Thêm nhân viên", true);
        initialize();
    }

    private void initialize() {
        // Tăng chiều cao để chứa các component mới
        setSize(650, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Thêm nhân viên");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(225, 20, 200, 35);
        getContentPane().add(lblTitle);

        // --- Tên nhân viên ---
        JLabel lblTen = new JLabel("Tên nhân viên:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 120, 25);
        getContentPane().add(lblTen);

        txtTenNhanVien = new JTextField();
        txtTenNhanVien.setBounds(40, 110, 250, 35);
        txtTenNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenNhanVien);

        // --- Email (Tên đăng nhập) ---
        JLabel lblEmail = new JLabel("Tên đăng nhập:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblEmail.setBounds(340, 80, 200, 25);
        getContentPane().add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(340, 110, 250, 35);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtEmail);

        // --- Địa chỉ ---
        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDiaChi.setBounds(40, 160, 120, 25);
        getContentPane().add(lblDiaChi);

        txtDiaChi = new JTextField();
        txtDiaChi.setBounds(40, 190, 250, 35);
        txtDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtDiaChi);

        // --- Số điện thoại ---
        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSdt.setBounds(340, 160, 120, 25);
        getContentPane().add(lblSdt);

        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setBounds(340, 190, 250, 35);
        txtSoDienThoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtSoDienThoai);
        
        // --- Ngày sinh ---
        JLabel lblNgaySinh = new JLabel("Ngày sinh:");
        lblNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblNgaySinh.setBounds(40, 240, 120, 25);
        getContentPane().add(lblNgaySinh);

        cmbNgay = new JComboBox<>();
        for (int i = 1; i <= 31; i++) cmbNgay.addItem(String.format("%02d", i));
        cmbNgay.setBounds(40, 270, 70, 35);
        getContentPane().add(cmbNgay);

        cmbThang = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cmbThang.addItem(String.format("%02d", i));
        cmbThang.setBounds(120, 270, 70, 35);
        getContentPane().add(cmbThang);

        cmbNam = new JComboBox<>();
        for (int i = LocalDate.now().getYear() - 18; i >= 1950; i--) cmbNam.addItem(String.valueOf(i));
        cmbNam.setBounds(200, 270, 90, 35);
        getContentPane().add(cmbNam);
        
        // --- Giới tính ---
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGioiTinh.setBounds(340, 240, 120, 25);
        getContentPane().add(lblGioiTinh);

        radNam = new JRadioButton("Nam");
        radNam.setSelected(true);
        radNam.setBounds(340, 270, 80, 35);
        radNam.setBackground(Color.WHITE);
        radNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radNam);
        
        radNu = new JRadioButton("Nữ");
        radNu.setBounds(430, 270, 80, 35);
        radNu.setBackground(Color.WHITE);
        radNu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radNu);
        
        ButtonGroup bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(radNam);
        bgGioiTinh.add(radNu);
        
        // --- Vai trò & Ca làm ---
        chkQuanLy = new JCheckBox("Là quản lý");
        chkQuanLy.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        chkQuanLy.setBackground(Color.WHITE);
        chkQuanLy.setBounds(40, 320, 120, 35);
        getContentPane().add(chkQuanLy);
        
        JLabel lblCaLam = new JLabel("Ca làm:");
        lblCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCaLam.setBounds(340, 320, 120, 25);
        getContentPane().add(lblCaLam);
        
        cmbCaLam = new JComboBox<>(new String[]{"Sáng", "Chiều", "Tối", "Hành chính"});
        cmbCaLam.setBounds(340, 350, 250, 35);
        getContentPane().add(cmbCaLam);

        // --- Mật khẩu ---
        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMatKhau.setBounds(40, 400, 120, 25);
        getContentPane().add(lblMatKhau);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setBounds(40, 430, 550, 35);
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtMatKhau);

        // --- Các nút ---
        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(480, 500, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x3B82F6));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        getContentPane().add(btnThoat);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(350, 500, 110, 35);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(Color.LIGHT_GRAY);
        btnThem.setBorder(new LineBorder(Color.GRAY));
        getContentPane().add(btnThem);

        // --- Thêm sự kiện cho các nút ---
        btnThoat.addActionListener(e -> dispose());
        btnThem.addActionListener(e -> onThemButtonClick());
    }

    private void onThemButtonClick() {
        try {
            // 1. Lấy dữ liệu từ các component
            String ten = txtTenNhanVien.getText();
            String email = txtEmail.getText();
            String diaChi = txtDiaChi.getText();
            String sdt = txtSoDienThoai.getText();
            String matKhau = new String(txtMatKhau.getPassword());
            
            String ngay = cmbNgay.getSelectedItem().toString();
            String thang = cmbThang.getSelectedItem().toString();
            String nam = cmbNam.getSelectedItem().toString();
            LocalDate ngaySinh = LocalDate.parse(nam + "-" + thang + "-" + ngay);
            
            boolean gioiTinh = radNam.isSelected();
            boolean isQuanLy = chkQuanLy.isSelected();
            String caLam = cmbCaLam.getSelectedItem().toString();

            // 2. Tạo mã tự động theo đúng định dạng
            String maTK = String.format("TK%06d", (int) (Math.random() * 1000000));
            // Lấy 10 chữ số cuối của timestamp để đảm bảo đúng định dạng
            String maNV = String.format("NV%s", String.valueOf(System.currentTimeMillis()).substring(3));

            // 3. Tạo đối tượng TaiKhoan và NhanVien
            // Việc validate sẽ được thực hiện tự động trong các hàm setter của entity
            TaiKhoan tk = new TaiKhoan(maTK, email, matKhau); 
            this.nhanVienMoi = new NhanVien(maNV, ten, gioiTinh, ngaySinh, sdt, diaChi, isQuanLy, tk, caLam, true);
            
            // 4. Đóng dialog nếu thành công
            dispose();
            
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            // Bắt lỗi từ các hàm setter trong entity hoặc lỗi parse ngày tháng
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
        	 JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public NhanVien getNhanVienMoi() {
        return nhanVienMoi;
    }
}