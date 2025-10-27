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

import com.toedter.calendar.JDateChooser;

import entity.KhachHang;
import entity.NhanVien;

public class CapNhatKhachHang_Dialog extends JDialog {

	 private JTextField txtTenKhachHang;
	    private JTextField txtSoDienThoai;
	    private JRadioButton radNam, radNu;
	    private JDateChooser ngaySinhDateChooser;
	    private JButton btnThoat;
	    private JButton btnLuu;
    


    private KhachHang khachHangCanCapNhat;
    private boolean isUpdateSuccess = false;

    public CapNhatKhachHang_Dialog(Frame owner, KhachHang khToUpdate) {
        super(owner, "Cập nhật thông tin khách hàng", true);
        this.khachHangCanCapNhat = khToUpdate;
        initialize();
        populateData();
    }

    private void initialize() {
        // Tăng chiều cao Dialog để có chỗ cho component mới
        setSize(650, 400); 
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Cập nhật thông tin");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(213, 20, 250, 35);
        getContentPane().add(lblTitle);

        // ... (Các component cũ giữ nguyên vị trí) ...
        JLabel lblTen = new JLabel("Tên khách hàng:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 120, 25);
        getContentPane().add(lblTen);
        txtTenKhachHang = new JTextField();
        txtTenKhachHang.setBounds(40, 110, 250, 35);
        txtTenKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenKhachHang);


        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSdt.setBounds(340, 80, 120, 25);
        getContentPane().add(lblSdt);
        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setBounds(340, 115, 250, 35);
        txtSoDienThoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtSoDienThoai);
        
        JLabel lblNgaySinh = new JLabel("Ngày sinh:");
        lblNgaySinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblNgaySinh.setBounds(40, 169, 120, 25);
        getContentPane().add(lblNgaySinh);

        ngaySinhDateChooser = new JDateChooser();
        ngaySinhDateChooser.setBounds(40, 204, 250, 35);
        ngaySinhDateChooser.setDateFormatString("dd-MM-yyyy");
        ngaySinhDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(ngaySinhDateChooser);
      
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGioiTinh.setBounds(340, 169, 120, 25);
        getContentPane().add(lblGioiTinh);
        radNam = new JRadioButton("Nam");
        radNam.setBounds(340, 204, 80, 35);
        radNam.setBackground(Color.WHITE);
        radNam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radNam);
        radNu = new JRadioButton("Nữ");
        radNu.setBounds(422, 204, 80, 35);
        radNu.setBackground(Color.WHITE);
        radNu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(radNu);
        ButtonGroup bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(radNam);
        bgGioiTinh.add(radNu);
        




        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(500, 268, 110, 40); // Đổi y và height
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        getContentPane().add(btnThoat);

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(335, 268, 140, 40); // Đổi y và height
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(null);
        getContentPane().add(btnLuu);

        btnThoat.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> onLuuButtonClick());
    }
    
    private void populateData() {
        txtTenKhachHang.setText(khachHangCanCapNhat.getTenKhachHang());
        txtSoDienThoai.setText(khachHangCanCapNhat.getSoDienThoai());
        LocalDate ngaySinhLocalDate = khachHangCanCapNhat.getNgaySinh();
        if (ngaySinhLocalDate != null) {
            Date ngaySinhDate = Date.from(ngaySinhLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            ngaySinhDateChooser.setDate(ngaySinhDate);
        }

        if (khachHangCanCapNhat.isGioiTinh()) {
            radNam.setSelected(true);
        } else {
            radNu.setSelected(true);
        }


    }

    private void onLuuButtonClick() {
        try {
            String ten = txtTenKhachHang.getText();
            String sdt = txtSoDienThoai.getText();
            boolean gioiTinh = radNam.isSelected();
            Date selectedDate = ngaySinhDateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate ngaySinh = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            if (ngaySinh.isAfter(LocalDate.now().minusYears(18))) {
                JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 16 tuổi.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }            
            khachHangCanCapNhat.setTenKhachHang(ten);
            khachHangCanCapNhat.setSoDienThoai(sdt);
            khachHangCanCapNhat.setNgaySinh(ngaySinh);
            khachHangCanCapNhat.setGioiTinh(gioiTinh);           
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