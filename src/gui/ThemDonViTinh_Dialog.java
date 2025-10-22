package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.*;
import javax.swing.border.LineBorder;
import entity.DonViTinh;

public class ThemDonViTinh_Dialog extends JDialog {

    private JTextField txtTenDonViTinh;
    private JTextArea txtMoTa;
    private JButton btnThem;
    private JButton btnThoat;

    private DonViTinh donViTinhMoi = null;

    public ThemDonViTinh_Dialog(Frame owner) {
        super(owner, "Thêm đơn vị tính", true);
        initialize();
    }

    private void initialize() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel("Thêm đơn vị tính");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(145, 20, 210, 35);
        getContentPane().add(lblTitle);

        JLabel lblTen = new JLabel("Tên đơn vị tính:");
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTen.setBounds(40, 80, 150, 25);
        getContentPane().add(lblTen);

        txtTenDonViTinh = new JTextField();
        txtTenDonViTinh.setBounds(40, 110, 400, 35);
        txtTenDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        getContentPane().add(txtTenDonViTinh);

        JLabel lblMoTa = new JLabel("Mô tả:");
        lblMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblMoTa.setBounds(40, 160, 120, 25);
        getContentPane().add(lblMoTa);

        txtMoTa = new JTextArea();
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtMoTa);
        scrollPane.setBounds(40, 190, 400, 80);
        getContentPane().add(scrollPane);

        btnThoat = new JButton("Thoát");
        btnThoat.setBounds(330, 300, 110, 35);
        btnThoat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThoat.setBackground(new Color(0x6B7280));
        btnThoat.setForeground(Color.WHITE);
        btnThoat.setBorder(null);
        getContentPane().add(btnThoat);

        btnThem = new JButton("Thêm");
        btnThem.setBounds(200, 300, 110, 35);
        btnThem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThem.setBackground(new Color(0x3B82F6));
        btnThem.setForeground(Color.WHITE);
        btnThem.setBorder(null);
        getContentPane().add(btnThem);

        btnThoat.addActionListener(e -> dispose());
        btnThem.addActionListener(e -> onThemButtonClick());
    }

    private void onThemButtonClick() {
        try {
            String ten = txtTenDonViTinh.getText();
            String moTa = txtMoTa.getText();
            
            // Tạo mã DVT-xxx ngẫu nhiên
            String maDVT = String.format("DVT-%03d", (int) (Math.random() * 1000));
            
            this.donViTinhMoi = new DonViTinh(maDVT, ten, moTa);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public DonViTinh getDonViTinhMoi() {
        return donViTinhMoi;
    }
}