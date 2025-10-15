package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class DangNhap_GUI extends JFrame {

    public DangNhap_GUI() {
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setTitle("Đăng nhập hệ thống");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());


        ImagePanel pnlCenterBackground = new ImagePanel(
            new ImageIcon(getClass().getResource("/images/backround_dangNhap.png")).getImage()
        );
        contentPane.add(pnlCenterBackground, BorderLayout.CENTER);

        JPanel pnlForm = new JPanel(null);
        pnlForm.setBackground(new Color(230, 235, 237)); 
        pnlForm.setPreferredSize(new Dimension(500, 0)); 
        contentPane.add(pnlForm, BorderLayout.EAST);
        
        int formX = 50;
        int topMargin = 80;


        ImageIcon plusIcon = new ImageIcon(getClass().getResource("/images/add.png"));
        // Cần chỉnh kích thước ảnh để nó nhỏ và phù hợp với chữ
        Image scaledPlusImg = plusIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH); 
        JLabel lblPlusIcon = new JLabel(new ImageIcon(scaledPlusImg));
        lblPlusIcon.setBounds(formX + 95, topMargin + 2, 30, 30); 


        JLabel lblTieuDeForm = new JLabel("Hòa An xin chào");
        lblTieuDeForm.setFont(new Font("Arial", Font.BOLD, 18));
        lblTieuDeForm.setForeground(Color.BLACK);
  
        lblTieuDeForm.setBounds(formX + 130, topMargin, 200, 30); 
        pnlForm.add(lblTieuDeForm);

        // Tài khoản
        JLabel lblTaiKhoan = new JLabel("Tài khoản");
        lblTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 16));
        lblTaiKhoan.setBounds(formX, topMargin + 60, 100, 30);
        pnlForm.add(lblTaiKhoan);

        JTextField txtTaiKhoan = new JTextField();
        txtTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 16));
        txtTaiKhoan.setBounds(formX, topMargin + 90, 410, 40);
        txtTaiKhoan.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); // Viền dưới
        txtTaiKhoan.setBackground(pnlForm.getBackground()); // Cùng màu nền
        addPlaceholder(txtTaiKhoan, "Nhập tài khoản của bạn");
        pnlForm.add(txtTaiKhoan);

        // Mật khẩu
        JLabel lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(new Font("Arial", Font.PLAIN, 16));
        lblMatKhau.setBounds(formX, topMargin + 150, 100, 30);
        pnlForm.add(lblMatKhau);

        JPasswordField txtMatKhau = new JPasswordField();
        txtMatKhau.setFont(new Font("Arial", Font.PLAIN, 16));
        txtMatKhau.setBounds(formX, topMargin + 180, 410, 40);
        txtMatKhau.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); // Viền dưới
        txtMatKhau.setBackground(pnlForm.getBackground());
        addPlaceholder(txtMatKhau, "Nhập mật khẩu của bạn");
        pnlForm.add(txtMatKhau);

        // Nút Đăng nhập
        RoundedButton btnDangNhap = new RoundedButton("ĐĂNG NHẬP", 40, new Color(221, 136, 136));
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 18));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBounds(formX, topMargin + 260, 410, 50);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlForm.add(btnDangNhap);

        // Quên mật khẩu
        JLabel lblQuenMK = new JLabel("Quên mật khẩu");
        lblQuenMK.setFont(new Font("Arial", Font.ITALIC, 14));
        lblQuenMK.setForeground(Color.RED);
        lblQuenMK.setHorizontalAlignment(SwingConstants.RIGHT);
        lblQuenMK.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblQuenMK.setBounds(formX, topMargin + 320, 410, 30);
        pnlForm.add(lblQuenMK);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DangNhap_GUI().setVisible(true));
    }
}

/**
 * Lớp panel tùy chỉnh để vẽ một hình ảnh làm nền.
 * Hình ảnh sẽ tự động co giãn để lấp đầy toàn bộ panel.
 */
class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ ảnh nền, co giãn theo kích thước của panel
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }
}


class RoundedButton extends JButton {
    private int cornerRadius;
    private Color backgroundColor;

    public RoundedButton(String text, int radius, Color bgColor) {
        super(text);
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        super.paintComponent(g2);
        g2.dispose();
    }
}