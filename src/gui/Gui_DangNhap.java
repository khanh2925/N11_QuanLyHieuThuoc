package gui;

import javax.swing.*;
import java.awt.*;

public class Gui_DangNhap extends JFrame {

    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private JLabel lblQuenMK;

    public Gui_DangNhap() {
        initUI();
    }

    private void initUI() {
        setTitle("Hòa An Pharmacy - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // full màn hình laptop
        setUndecorated(true);
        setLayout(new BorderLayout());

        // ====== PANEL TIÊU ĐỀ ======
        JPanel pnlTitle = new JPanel() {
            private final Image bg = new ImageIcon(getClass().getResource("/images/backround_dangNhap.png")).getImage();
            private final Image logo = new ImageIcon(getClass().getResource("/images/LOGO.jpg")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                g.drawImage(logo, 20, 10, 50, 50, this);
            }
        };
        pnlTitle.setPreferredSize(new Dimension(0, 80));
        pnlTitle.setLayout(null);

        JLabel lblTitle = new JLabel("Hòa An Pharmacy", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 26));
        lblTitle.setForeground(new Color(178, 34, 34));
        lblTitle.setBounds(90, 20, 400, 40);
        pnlTitle.add(lblTitle);

        JLabel lblDong = new JLabel("✕");
        lblDong.setFont(new Font("Arial", Font.BOLD, 22));
        lblDong.setForeground(Color.DARK_GRAY);
        lblDong.setBounds(1870, 20, 30, 30);
        lblDong.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblDong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.exit(0);
            }
        });
        pnlTitle.add(lblDong);

        add(pnlTitle, BorderLayout.NORTH);

        // ====== PANEL NỘI DUNG CHÍNH ======
        JPanel pnlContent = new JPanel(new BorderLayout());

        // Bên trái là ảnh dược sĩ
        JLabel lblLeft = new JLabel(new ImageIcon(getClass().getResource("/images/2f7eec36-c34e-44e1-ab71-2394020c443f.png")));
        lblLeft.setHorizontalAlignment(SwingConstants.CENTER);
        pnlContent.add(lblLeft, BorderLayout.CENTER);

        // ====== FORM ĐĂNG NHẬP ======
        JPanel pnlForm = new JPanel();
        pnlForm.setPreferredSize(new Dimension(500, 0));
        pnlForm.setLayout(null);
        pnlForm.setBackground(new Color(208, 235, 242));

        JLabel lblXinChao = new JLabel("Hòa An xin chào", SwingConstants.CENTER);
        lblXinChao.setFont(new Font("Times New Roman", Font.BOLD, 24));
        lblXinChao.setForeground(new Color(70, 70, 70));
        lblXinChao.setBounds(0, 60, 500, 40);
        pnlForm.add(lblXinChao);

        JLabel lblTaiKhoan = new JLabel("Tài khoản");
        lblTaiKhoan.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        lblTaiKhoan.setBounds(60, 150, 380, 30);
        pnlForm.add(lblTaiKhoan);

        txtTaiKhoan = new JTextField();
        txtTaiKhoan.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        txtTaiKhoan.setBounds(60, 190, 380, 40);
        txtTaiKhoan.setBackground(Color.WHITE);
        pnlForm.add(txtTaiKhoan);

        JLabel lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        lblMatKhau.setBounds(60, 260, 380, 30);
        pnlForm.add(lblMatKhau);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setFont(new Font("Times New Roman", Font.PLAIN, 18));
        txtMatKhau.setBounds(60, 300, 380, 40);
        txtMatKhau.setBackground(Color.WHITE);
        pnlForm.add(txtMatKhau);

        btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setFont(new Font("Times New Roman", Font.BOLD, 20));
        btnDangNhap.setBounds(100, 380, 300, 50);
        btnDangNhap.setBackground(new Color(192, 192, 192));
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorder(BorderFactory.createEmptyBorder());
        btnDangNhap.setOpaque(true);
        pnlForm.add(btnDangNhap);

        lblQuenMK = new JLabel("Quên mật khẩu?");
        lblQuenMK.setFont(new Font("Times New Roman", Font.ITALIC, 14));
        lblQuenMK.setForeground(Color.RED);
        lblQuenMK.setBounds(330, 440, 150, 30);
        lblQuenMK.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlForm.add(lblQuenMK);

        pnlContent.add(pnlForm, BorderLayout.EAST);
        add(pnlContent, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Gui_DangNhap();
    }
}
