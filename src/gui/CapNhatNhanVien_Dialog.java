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

import com.toedter.calendar.JDateChooser; //Import JDateChooser

import entity.NhanVien;

public class CapNhatNhanVien_Dialog extends JDialog {

    private JTextField txtTenNhanVien;
    private JTextField txtEmail;
    private JTextField txtDiaChi;
    private JTextField txtSoDienThoai;
    private JPasswordField txtMatKhau;
  
    private JDateChooser ngaySinhDateChooser;
    
    private JRadioButton radNam, radNu;
    private JCheckBox chkQuanLy;
    private JComboBox<String> cmbCaLam;
    private JButton btnLuu;
    private JButton btnThoat;

    private NhanVien nhanVienCanCapNhat;
    private boolean isUpdateSuccess = false;

    public CapNhatNhanVien_Dialog(Frame owner, NhanVien nvToUpdate) {
        super(owner, "Cập nhật thông tin nhân viên", true);
        this.nhanVienCanCapNhat = nvToUpdate;
        initialize();
        populateData();
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

        // ... (Các component khác không thay đổi) ...
        JLabel lblTen = new JLabel("Tên nhân viên:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 120, 25);
        getContentPane().add(lblTen);
        txtTenNhanVien = new JTextField();
        txtTenNhanVien.setBounds(40, 110, 250, 35);
        txtTenNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenNhanVien);

        JLabel lblEmail = new JLabel("Tên đăng nhập:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblEmail.setBounds(340, 80, 200, 25);
        getContentPane().add(lblEmail);
        txtEmail = new JTextField();
        txtEmail.setBounds(340, 110, 250, 35);
        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtEmail);

        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDiaChi.setBounds(40, 160, 120, 25);
        getContentPane().add(lblDiaChi);
        txtDiaChi = new JTextField();
        txtDiaChi.setBounds(40, 190, 250, 35);
        txtDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtDiaChi);

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

        ngaySinhDateChooser = new JDateChooser();
        ngaySinhDateChooser.setBounds(40, 270, 250, 35);
        ngaySinhDateChooser.setDateFormatString("dd-MM-yyyy");
        ngaySinhDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(ngaySinhDateChooser);
      
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

        JLabel lblMatKhau = new JLabel("Mật khẩu (để trống nếu không đổi):");
        lblMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMatKhau.setBounds(40, 400, 300, 25);
        getContentPane().add(lblMatKhau);
        txtMatKhau = new JPasswordField();
        txtMatKhau.setBounds(40, 430, 550, 35);
        txtMatKhau.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtMatKhau);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(480, 500, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        getContentPane().add(btnThoat);

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(320, 500, 140, 35);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(null);
        getContentPane().add(btnLuu);

        btnThoat.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> onLuuButtonClick());
    }
    
    private void populateData() {
        txtTenNhanVien.setText(nhanVienCanCapNhat.getTenNhanVien());
        txtEmail.setText(nhanVienCanCapNhat.getTaiKhoan().getTenDangNhap());
        txtDiaChi.setText(nhanVienCanCapNhat.getDiaChi());
        txtSoDienThoai.setText(nhanVienCanCapNhat.getSoDienThoai());
        LocalDate ngaySinhLocalDate = nhanVienCanCapNhat.getNgaySinh();
        if (ngaySinhLocalDate != null) {
            Date ngaySinhDate = Date.from(ngaySinhLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            ngaySinhDateChooser.setDate(ngaySinhDate);
        }

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
            String ten = txtTenNhanVien.getText();
            String email = txtEmail.getText();
            String diaChi = txtDiaChi.getText();
            String sdt = txtSoDienThoai.getText();
            String matKhau = new String(txtMatKhau.getPassword());
            
            Date selectedDate = ngaySinhDateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) {
                JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean gioiTinh = radNam.isSelected();
            boolean isQuanLy = chkQuanLy.isSelected();
            String caLam = cmbCaLam.getSelectedItem().toString();
            
            nhanVienCanCapNhat.setTenNhanVien(ten);
            nhanVienCanCapNhat.getTaiKhoan().setTenDangNhap(email);
            nhanVienCanCapNhat.setDiaChi(diaChi);
            nhanVienCanCapNhat.setSoDienThoai(sdt);
            nhanVienCanCapNhat.setNgaySinh(ngaySinh);
            nhanVienCanCapNhat.setGioiTinh(gioiTinh);
            nhanVienCanCapNhat.setQuanLy(isQuanLy);
            nhanVienCanCapNhat.setCaLam(caLam);

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