package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.*;
import javax.swing.border.LineBorder;
import entity.DonViTinh;

public class CapNhatDonViTinh_Dialog extends JDialog {

    private JTextField txtTenDonViTinh;
    private JTextArea txtMoTa;
    private JButton btnLuu;
    private JButton btnThoat;

    private DonViTinh donViTinhCanCapNhat;
    private boolean isUpdateSuccess = false;

    public CapNhatDonViTinh_Dialog(Frame owner, DonViTinh dvtToUpdate) {
        super(owner, "Cập nhật đơn vị tính", true);
        this.donViTinhCanCapNhat = dvtToUpdate;
        initialize();
        populateData();
    }

    private void initialize() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel("Cập nhật đơn vị tính");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBounds(125, 20, 250, 35);
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

        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setBounds(180, 300, 130, 35);
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(0x3B82F6));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setBorder(null);
        getContentPane().add(btnLuu);

        btnThoat.addActionListener(e -> dispose());
        btnLuu.addActionListener(e -> onLuuButtonClick());
    }

    private void populateData() {
        txtTenDonViTinh.setText(donViTinhCanCapNhat.getTenDonViTinh());
        txtMoTa.setText(donViTinhCanCapNhat.getMoTa());
    }

    private void onLuuButtonClick() {
        try {
            String ten = txtTenDonViTinh.getText();
            String moTa = txtMoTa.getText();
            
            donViTinhCanCapNhat.setTenDonViTinh(ten);
            donViTinhCanCapNhat.setMoTa(moTa);
            
            isUpdateSuccess = true;
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isUpdateSuccess() {
        return isUpdateSuccess;
    }
}