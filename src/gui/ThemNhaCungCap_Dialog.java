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

import entity.KhachHang;


public class ThemNhaCungCap_Dialog extends JDialog {

    private JTextField txtTenNhaCungCap;
    private JTextField txtSoDienThoai;
    private JTextField txtDiaChi;


    private JButton btnThem;
    private JButton btnThoat;
    private JTextField textField;



    public ThemNhaCungCap_Dialog(Frame owner) {
        super(owner, "Thêm nhà cung cấp", true);
        initialize();
    }

    private void initialize() {
        setSize(650, 400);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().setLayout(null);


        
        // --- Tiêu đề Dialog ---
        JLabel lblTitle = new JLabel("Thêm nhà cung cấp");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(205, 21, 229, 35);
        getContentPane().add(lblTitle);

        // --- Tên khách hàng ---
        JLabel lblTen = new JLabel("Tên nhà cung cấp:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 147, 25);
        getContentPane().add(lblTen);

        txtTenNhaCungCap = new JTextField();
        txtTenNhaCungCap.setBounds(40, 110, 250, 35);
        txtTenNhaCungCap.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenNhaCungCap);



        // --- Số điện thoại ---
        JLabel lblSdt = new JLabel("Số điện thoại:");
        lblSdt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSdt.setBounds(340, 80, 120, 25);
        getContentPane().add(lblSdt);

        txtSoDienThoai = new JTextField();
        txtSoDienThoai.setBounds(340, 110, 250, 35);
        txtSoDienThoai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtSoDienThoai);
        
        // --- Dia chi---
        JLabel lblDiaChi = new JLabel("Địa chỉ:");
        lblDiaChi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDiaChi.setBounds(40, 155, 120, 25);
        getContentPane().add(lblDiaChi);


  

        

        // --- Các nút ---
        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(467, 275, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x3B82F6));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        btnThoat.setFocusPainted(false);
        getContentPane().add(btnThoat);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(340, 275, 110, 35);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(Color.LIGHT_GRAY);
        btnThem.setBorder(new LineBorder(Color.GRAY));
        getContentPane().add(btnThem);
        
        textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBounds(40, 199, 550, 35);
        getContentPane().add(textField);
        

    }
   
}