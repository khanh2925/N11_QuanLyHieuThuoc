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

public class ThongTinCaNhan_Dialog extends JDialog {

    private JTextField txtMaNV;
    private JTextField txtTenNV;
    private JTextField txtGioiTinh;
    private JTextField txtSoDienThoai;
    private JTextField txtCaLam;
    private JTextField txtChucVu;
    private JTextArea textDiaChi;
    private JTextField txtDiaChi;



    public ThongTinCaNhan_Dialog(Frame owner) {
        super(owner, "Thông tin cá nhân", true);
        initialize();
    }

    private void initialize() {
        setSize(650, 600);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);

        
        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Thông tin cá nhân");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(214, 20, 222, 35);
        getContentPane().add(lblTitle);


        JLabel lblMaNV = new JLabel("Mã NV:");
        lblMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMaNV.setBounds(40, 80, 120, 25);
        getContentPane().add(lblMaNV);

        txtMaNV = new JTextField();
        txtMaNV.setBounds(40, 110, 250, 35);
        txtMaNV.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtMaNV);

     
        JLabel lblTenNV = new JLabel("Tên NV:");
        lblTenNV.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTenNV.setBounds(340, 80, 200, 25);
        getContentPane().add(lblTenNV);

        txtTenNV = new JTextField();
        txtTenNV.setBounds(340, 110, 250, 35);
        txtTenNV.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenNV);


        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblGioiTinh.setBounds(40, 160, 120, 25);
        getContentPane().add(lblGioiTinh);

        txtGioiTinh = new JTextField();
        txtGioiTinh.setBounds(40, 190, 250, 35);
        txtGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtGioiTinh);

        // --- Số điện thoại ---
        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSdt.setBounds(340, 160, 120, 25);
        getContentPane().add(lblSdt);

        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setBounds(340, 190, 250, 35);
        txtSoDienThoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtSoDienThoai);
        
        
        JLabel lblCaLam = new JLabel("Ca làm:");
        lblCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblCaLam.setBounds(340, 249, 120, 25);
        getContentPane().add(lblCaLam);
        
        txtCaLam = new JTextField();
        txtCaLam.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCaLam.setBounds(340, 293, 250, 35);
        getContentPane().add(txtCaLam);

        JLabel lblChucVu = new JLabel("Chức vụ:");
        lblChucVu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblChucVu.setBounds(40, 249, 120, 25);
        getContentPane().add(lblChucVu);
        
        txtChucVu = new JTextField();
        txtChucVu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtChucVu.setBounds(40, 293, 250, 35);
        getContentPane().add(txtChucVu);
        
        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDiaChi.setBounds(40, 351, 120, 25);
        getContentPane().add(lblDiaChi);
        
        txtDiaChi = new JTextField();
        txtDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDiaChi.setBounds(40, 396, 550, 106);
        getContentPane().add(txtDiaChi);
        

    }


}