package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GUI_DangNhap extends JFrame {

    public GUI_DangNhap() {
        setTitle("Hòa An Pharmacy - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());

        // Panel chính chia 2 bên
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Vẽ ảnh nền bên trái
                ImageIcon icon = new ImageIcon("src/images/pharmacy.png"); // đường dẫn ảnh bạn muốn
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        leftPanel.setPreferredSize(new Dimension(500, 0));

        // Panel bên phải
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(214, 235, 241));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Logo + tiêu đề
        JLabel lblLogo = new JLabel("Hòa An xin chào", JLabel.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setForeground(new Color(60, 60, 60));

        rightPanel.add(lblLogo);
        rightPanel.add(Box.createVerticalStrut(30));

        // Đăng nhập quản lý
        JCheckBox chkQuanLy = new JCheckBox("Đăng nhập quản lý");
        chkQuanLy.setBackground(new Color(214, 235, 241));
        chkQuanLy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightPanel.add(chkQuanLy);
        rightPanel.add(Box.createVerticalStrut(15));

        // Ô tài khoản
        JLabel lblUser = new JLabel("Tài khoản");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUser.setPreferredSize(new Dimension(250, 35));
        txtUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(5, 10, 5, 10)
        ));

        rightPanel.add(lblUser);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(txtUser);
        rightPanel.add(Box.createVerticalStrut(15));

        // Ô mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPass.setPreferredSize(new Dimension(250, 35));
        txtPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(5, 10, 5, 10)
        ));

        rightPanel.add(lblPass);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(txtPass);
        rightPanel.add(Box.createVerticalStrut(25));

        // Nút đăng nhập
        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(180, 200, 210));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setPreferredSize(new Dimension(200, 40));

        // Hiệu ứng hover
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(160, 180, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(180, 200, 210));
            }
        });

        rightPanel.add(btnLogin);
        rightPanel.add(Box.createVerticalStrut(25));

        // Liên hệ
        JLabel lblSupport = new JLabel("Liên hệ hỗ trợ: 0339893008", JLabel.CENTER);
        lblSupport.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSupport.setForeground(Color.DARK_GRAY);
        lblSupport.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(lblSupport);

        // Thêm 2 panel vào frame
        getContentPane().add(leftPanel, BorderLayout.NORTH);
        getContentPane().add(rightPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI_DangNhap().setVisible(true);
        });
    }
}
