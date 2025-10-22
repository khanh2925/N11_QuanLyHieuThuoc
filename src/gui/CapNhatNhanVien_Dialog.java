package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.border.LineBorder;

import entity.NhanVien;

public class CapNhatNhanVien_Dialog extends JDialog {

    private JTextField txtTenNhanVien;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cmbNgay, cmbThang, cmbNam;
    private JRadioButton radNam, radNu;
    private JCheckBox chkQuanLy;
    private JComboBox<String> cmbCaLam;
    private JButton btnLuu;
    private JButton btnThoat;

    // Biến để lưu trữ nhân viên đang được cập nhật
    private NhanVien nhanVienCanCapNhat;
    private boolean isUpdateSuccess = false;

    public CapNhatNhanVien_Dialog(Frame owner, NhanVien nvToUpdate) {
        super(owner, "Cập nhật thông tin nhân viên", true);
        this.nhanVienCanCapNhat = nvToUpdate; // Nhận nhân viên từ bên ngoài
        initialize();
        populateData(); // Điền dữ liệu của nhân viên vào form
    }

    private void initialize() {
        setSize(650, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Cập nhật thông tin");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(225, 20, 250, 35);
        getContentPane().add(lblTitle);

        // Các component giao diện (tương tự ThemNhanVien_Dialog)
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
        JLabel lblMatKhau = new JLabel("Mật khẩu (để trống nếu không đổi):");
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMatKhau.setBounds(40, 400, 300, 25);
        getContentPane().add(lblMatKhau);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setBounds(40, 430, 550, 35);
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtMatKhau);

        // --- Các nút ---
        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(480, 500, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280)); // Màu xám
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        getContentPane().add(btnThoat);

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(320, 500, 140, 35);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x3B82F6)); // Màu xanh
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(null);
        getContentPane().add(btnLuu);

        // --- Thêm sự kiện cho các nút ---
        btnThoat.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> onLuuButtonClick());
    }
    
    /**
     * Điền dữ liệu của nhân viên vào các trường trong form.
     */
    private void populateData() {
        txtTenNhanVien.setText(nhanVienCanCapNhat.getTenNhanVien());
        txtEmail.setText(nhanVienCanCapNhat.getTaiKhoan().getTenDangNhap());
        txtDiaChi.setText(nhanVienCanCapNhat.getDiaChi());
        txtSoDienThoai.setText(nhanVienCanCapNhat.getSoDienThoai());

        LocalDate ns = nhanVienCanCapNhat.getNgaySinh();
        cmbNgay.setSelectedItem(String.format("%02d", ns.getDayOfMonth()));
        cmbThang.setSelectedItem(String.format("%02d", ns.getMonthValue()));
        cmbNam.setSelectedItem(String.valueOf(ns.getYear()));

        if (nhanVienCanCapNhat.isGioiTinh()) {
            radNam.setSelected(true);
        } else {
            radNu.setSelected(true);
        }

        chkQuanLy.setSelected(nhanVienCanCapNhat.isQuanLy());
        cmbCaLam.setSelectedItem(nhanVienCanCapNhat.getCaLam());
    }

    private void onLuuButtonClick() {
        try {
            // Lấy dữ liệu mới từ form
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
            
            // Cập nhật các thuộc tính của đối tượng nhân viên đã có
            nhanVienCanCapNhat.setTenNhanVien(ten);
            nhanVienCanCapNhat.getTaiKhoan().setTenDangNhap(email);
            nhanVienCanCapNhat.setDiaChi(diaChi);
            nhanVienCanCapNhat.setSoDienThoai(sdt);
            nhanVienCanCapNhat.setNgaySinh(ngaySinh);
            nhanVienCanCapNhat.setGioiTinh(gioiTinh);
            nhanVienCanCapNhat.setQuanLy(isQuanLy);
            nhanVienCanCapNhat.setCaLam(caLam);

            // Chỉ cập nhật mật khẩu nếu người dùng nhập mật khẩu mới
            if (!matKhau.isEmpty()) {
                nhanVienCanCapNhat.getTaiKhoan().setMatKhau(matKhau);
            }
            
            isUpdateSuccess = true;
            dispose();
            
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdateSuccess() {
        return isUpdateSuccess;
    }
}