/**
 * @author Quốc Khánh
 * @version 1.0
 * @since Oct 15, 2025
 *
 * Mô tả: Lớp này được tạo bởi Quốc Khánh vào ngày Oct 15, 2025.
 */
package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

import customcomponent.PillButton;

public class DangNhap_Gui {

	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DangNhap_Gui window = new DangNhap_Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); // ✅ chỉ đóng 1 lần ở đây
	}

	public DangNhap_Gui() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame("Đăng nhập");
		frame.setBounds(100, 100, 1920, 1080);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		// ===== Panel chính =====
		JPanel pnMain = new JPanel(new BorderLayout());
		frame.add(pnMain, BorderLayout.CENTER);

		// ===== Panel bên trái =====
		JPanel pnLeft = new JPanel();
		pnLeft.setPreferredSize(new Dimension(1256, 1080));
		pnLeft.setBackground(new Color(0xB2EBF2)); // xanh ngọc nhạt
		pnLeft.setBorder(new LineBorder(Color.GRAY, 1));
		pnMain.add(pnLeft, BorderLayout.WEST);

		// ===== Panel form đăng nhập =====
		JPanel pnFormDangNhap = new JPanel(null); // dùng absolute layout để setBounds hoạt động
		pnFormDangNhap.setBackground(new Color(0xF5F5F5)); // trắng xám nhạt
		pnMain.add(pnFormDangNhap, BorderLayout.CENTER);

		int formX = 50;
		int topMargin = 80;

		JLabel lblTieuDeForm = new JLabel("Hòa An xin chào");
		lblTieuDeForm.setFont(new Font("Arial", Font.BOLD, 18));
		lblTieuDeForm.setForeground(Color.BLACK);
		lblTieuDeForm.setBounds(formX + 130, topMargin, 200, 30);
		pnFormDangNhap.add(lblTieuDeForm);

		// ===== Tài khoản =====
		JLabel lblTaiKhoan = new JLabel("Tài khoản");
		lblTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 16));
		lblTaiKhoan.setBounds(formX, topMargin + 60, 100, 30);
		pnFormDangNhap.add(lblTaiKhoan);

		JTextField txtTaiKhoan = new JTextField("Nhập tài khoản của bạn");
		txtTaiKhoan.setFont(new Font("Arial", Font.PLAIN, 16));
		txtTaiKhoan.setBounds(formX, topMargin + 90, 410, 40);
		txtTaiKhoan.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		pnFormDangNhap.add(txtTaiKhoan);

		// ===== Mật khẩu =====
		JLabel lblMatKhau = new JLabel("Mật khẩu");
		lblMatKhau.setFont(new Font("Arial", Font.PLAIN, 16));
		lblMatKhau.setBounds(formX, topMargin + 150, 100, 30);
		pnFormDangNhap.add(lblMatKhau);

		JPasswordField txtMatKhau = new JPasswordField("Nhập mật khẩu của bạn");
		txtMatKhau.setBounds(formX, topMargin + 180, 410, 40);
		txtMatKhau.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
		pnFormDangNhap.add(txtMatKhau);

		// ===== Nút Đăng nhập =====
		JButton btnDangNhap = new PillButton("ĐĂNG NHẬP");
		btnDangNhap.setFont(new Font("Arial", Font.BOLD, 18));
		btnDangNhap.setForeground(Color.WHITE);
		btnDangNhap.setBounds(formX, topMargin + 260, 410, 50);
		btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pnFormDangNhap.add(btnDangNhap);

		// ===== Quên mật khẩu =====
		JLabel lblQuenMK = new JLabel("Quên mật khẩu");
		lblQuenMK.setFont(new Font("Arial", Font.ITALIC, 14));
		lblQuenMK.setForeground(Color.RED);
		lblQuenMK.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQuenMK.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblQuenMK.setBounds(formX, topMargin + 320, 410, 30);
		pnFormDangNhap.add(lblQuenMK);

		// ===== Label minh họa =====
		JLabel lblLeft = new JLabel("Panel bên trái (1256px)");
		lblLeft.setFont(new Font("Segoe UI", Font.BOLD, 20));
		pnLeft.add(lblLeft);

		JLabel lblRight = new JLabel("pnFormDangNhap (phần còn lại)");
		lblRight.setFont(new Font("Segoe UI", Font.BOLD, 20));
		pnFormDangNhap.add(lblRight);
	}
}
