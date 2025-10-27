package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;

import javax.swing.*;
import javax.swing.border.LineBorder;

import com.toedter.calendar.JDateChooser; // THAY ĐỔI 1: Import class mới từ thư viện JCalendar

import entity.NhanVien;
import entity.TaiKhoan;

public class ThemNhanVien_Dialog extends JDialog {

    private JTextField txtTenNhanVien;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JPasswordField txtMatKhau;

    // THAY ĐỔI 2: Khai báo JDateChooser thay cho DateChooser cũ
    private JDateChooser ngaySinhDateChooser;

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
        setSize(650, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        // ... (Code cho các component khác không thay đổi) ...
        
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

        // THAY ĐỔI 3: Khởi tạo JDateChooser và thiết lập định dạng ngày tháng
        ngaySinhDateChooser = new JDateChooser(); // Sử dụng constructor của JDateChooser
        ngaySinhDateChooser.setBounds(40, 270, 250, 35);
        ngaySinhDateChooser.setDateFormatString("dd-MM-yyyy"); // Đặt định dạng hiển thị
        ngaySinhDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(ngaySinhDateChooser);

        // ... (Code cho các component còn lại không thay đổi) ...

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
        chkQuanLy.setBounds(40, 348, 120, 35);
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
            // ... (Lấy dữ liệu các trường text không đổi) ...
            String ten = txtTenNhanVien.getText();
            String email = txtEmail.getText();
            String diaChi = txtDiaChi.getText();
            String sdt = txtSoDienThoai.getText();
            String matKhau = new String(txtMatKhau.getPassword());
            
            // THAY ĐỔI 4: Lấy ngày từ JDateChooser bằng phương thức getDate()
            Date selectedDate = ngaySinhDateChooser.getDate(); // Sử dụng getDate()
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Phần còn lại của logic chuyển đổi và kiểm tra tuổi không thay đổi
            LocalDate ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) {
                JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean gioiTinh = radNam.isSelected();
            boolean isQuanLy = chkQuanLy.isSelected();
            String caLam = cmbCaLam.getSelectedItem().toString();

            String maTK = String.format("TK%06d", (int) (Math.random() * 1000000));
            String maNV = String.format("NV%s", String.valueOf(System.currentTimeMillis()).substring(3));

            TaiKhoan tk = new TaiKhoan(maTK, email, matKhau); 
            this.nhanVienMoi = new NhanVien(maNV, ten, gioiTinh, ngaySinh, sdt, diaChi, isQuanLy, tk, caLam, true);
            
            dispose();
            
        } catch (IllegalArgumentException | DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public NhanVien getNhanVienMoi() {
        return nhanVienMoi;
    }
}