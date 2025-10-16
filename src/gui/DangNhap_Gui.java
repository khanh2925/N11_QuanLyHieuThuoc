package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import customcomponent.ImagePanel;
import customcomponent.PillButton;
import customcomponent.RoundedBorder;
import javax.swing.border.LineBorder;

public class DangNhap_Gui {

    private JFrame frame;
    private JTextField txtTaiKhoan;
    private JPasswordField txtMatKhau;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                DangNhap_Gui window = new DangNhap_Gui();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public DangNhap_Gui() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Đăng nhập");
        frame.setBounds(100, 100, 1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel pnMain = new JPanel(new BorderLayout());
        frame.getContentPane().add(pnMain, BorderLayout.CENTER);

        pnMain.add(createLeftPanel(), BorderLayout.WEST);
        pnMain.add(createLoginFormPanel(), BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        JPanel pnLeft = new JPanel(new BorderLayout());
        pnLeft.setPreferredSize(new Dimension(1256, 1080));
        pnLeft.setBackground(new Color(0xB2EBF2));

        ImagePanel pnlCenterBackground = new ImagePanel(
                new ImageIcon(getClass().getResource("/images/Login.png")).getImage());
        pnLeft.add(pnlCenterBackground, BorderLayout.CENTER);

        return pnLeft;
    }

    private JPanel createLoginFormPanel() {
        JPanel pnFormDangNhap = new JPanel(null);
        pnFormDangNhap.setBackground(new Color(0xE0F7FA));

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/images/Logo.png"));
        Image logoImage = logoIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(logoImage));
        lblLogo.setBounds(190, 30, 250, 250);
        pnFormDangNhap.add(lblLogo);

        JLabel lblTieuDeForm = new JLabel("Chào mừng đến với Hòa An");
        lblTieuDeForm.setHorizontalAlignment(SwingConstants.CENTER);
        lblTieuDeForm.setFont(new Font("Arial", Font.BOLD, 36));
        lblTieuDeForm.setForeground(new Color(0x006064));
        lblTieuDeForm.setBounds(39, 290, 570, 61);
        pnFormDangNhap.add(lblTieuDeForm);

        int inputWidth = 532;
        int inputHeight = 50;

        JLabel lblTaiKhoan = new JLabel("Tài khoản");
        lblTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 24));
        lblTaiKhoan.setBounds(50, 399, 129, 30);
        pnFormDangNhap.add(lblTaiKhoan);

        txtTaiKhoan = new JTextField();
        txtTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 20));
        txtTaiKhoan.setBounds(50, 439, inputWidth, inputHeight);
        txtTaiKhoan.setOpaque(false);
        txtTaiKhoan.setBorder(new RoundedBorder(20));
        txtTaiKhoan.setMargin(new Insets(5, 15, 5, 15));
        pnFormDangNhap.add(txtTaiKhoan);
        addPlaceholder(txtTaiKhoan, "Nhập tài khoản của bạn");

        JLabel lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(new Font("Arial", Font.PLAIN, 24));
        lblMatKhau.setBounds(50, 518, 100, 30);
        pnFormDangNhap.add(lblMatKhau);

        JPanel pnMatKhau = new JPanel(null);
        pnMatKhau.setBorder(UIManager.getBorder("PasswordField.border"));
        pnMatKhau.setBounds(50, 558, inputWidth, inputHeight);
        pnMatKhau.setOpaque(false);
        pnMatKhau.setBorder(new RoundedBorder(20));
        pnFormDangNhap.add(pnMatKhau);

     // === Ô nhập mật khẩu ===
        txtMatKhau = new JPasswordField();
        txtMatKhau.setFont(new Font("Arial", Font.PLAIN, 20));
        txtMatKhau.setBounds(60, 558, inputWidth - 60, inputHeight); 
        txtMatKhau.setOpaque(false);
        txtMatKhau.setBorder(null);

        txtMatKhau.setMargin(new Insets(5, 15, 5, 45));
        pnFormDangNhap.add(txtMatKhau);
        addPlaceholder(txtMatKhau, "Nhập mật khẩu của bạn");

        // === Icon mắt ===
        ImageIcon iconOpen = new ImageIcon(
                new ImageIcon(getClass().getResource("/images/eye_open.png"))
                        .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));
        ImageIcon iconClose = new ImageIcon(
                new ImageIcon(getClass().getResource("/images/eye_close.png"))
                        .getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH));

        // === Nút hiện/ẩn mật khẩu ===
        JButton btnTogglePassword = new JButton(iconOpen); // mặc định ẩn mật khẩu → hiện icon "mắt mở"
        btnTogglePassword.setBounds(50 + inputWidth - 50, 558, 40, inputHeight);
        btnTogglePassword.setFocusPainted(false);
        btnTogglePassword.setBorderPainted(false);
        btnTogglePassword.setContentAreaFilled(false);
        btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePassword.setFocusable(false);
        pnFormDangNhap.add(btnTogglePassword);

        // Trạng thái mặc định: ẩn mật khẩu
        final boolean[] isHidden = {true};
        txtMatKhau.setEchoChar('●');

        // Sự kiện click vào nút mắt
        btnTogglePassword.addActionListener(e -> {
            if (isHidden[0]) {
                // Hiện mật khẩu
                txtMatKhau.setEchoChar((char) 0);
                btnTogglePassword.setIcon(iconClose); // đổi sang icon mắt đóng
            } else {
                // Ẩn mật khẩu
                txtMatKhau.setEchoChar('●');
                btnTogglePassword.setIcon(iconOpen); 
            }
            isHidden[0] = !isHidden[0];
        });

        

        JButton btnDangNhap = new PillButton("ĐĂNG NHẬP");
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 18));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBounds(50, 669, inputWidth, 50);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnFormDangNhap.add(btnDangNhap);

        JButton btnQuenMK = new JButton("Quên mật khẩu?");
        btnQuenMK.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        btnQuenMK.setFont(new Font("Arial", Font.ITALIC, 16));
        btnQuenMK.setForeground(new Color(0xD32F2F));
        btnQuenMK.setBounds(403, 732, 179, 30);
        btnQuenMK.setContentAreaFilled(false);
        btnQuenMK.setBorderPainted(false);
        btnQuenMK.setFocusPainted(false);
        btnQuenMK.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnQuenMK.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnQuenMK.setForeground(new Color(0xB71C1C));
                btnQuenMK.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 16));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnQuenMK.setForeground(new Color(0xD32F2F));
                btnQuenMK.setText("Quên mật khẩu?");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(frame,
                        "Tính năng khôi phục mật khẩu đang được phát triển!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        pnFormDangNhap.add(btnQuenMK);

        return pnFormDangNhap;
    }

    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('●');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	}
}
