package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import customcomponent.ImagePanel;
import customcomponent.PillButton;
import customcomponent.RoundedBorder;
import entity.NhanVien;
import entity.TaiKhoan;

public class DangNhap_GUI extends JFrame {

	private JTextField txtTaiKhoan;
	private JPasswordField txtMatKhau;

	public DangNhap_GUI() {
		initialize();
	}

	private void initialize() {
		setTitle("Đăng nhập");
		setSize(1920, 1080);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel pnMain = new JPanel(new BorderLayout());
		add(pnMain, BorderLayout.CENTER);

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
		ImageIcon iconOpen = new ImageIcon(new ImageIcon(getClass().getResource("/images/eye_open.png")).getImage()
				.getScaledInstance(25, 25, Image.SCALE_SMOOTH));
		ImageIcon iconClose = new ImageIcon(new ImageIcon(getClass().getResource("/images/eye_close.png")).getImage()
				.getScaledInstance(25, 25, Image.SCALE_SMOOTH));

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
		final boolean[] isHidden = { true };
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

		btnDangNhap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				String taiKhoan = txtTaiKhoan.getText();
//				String matKhau = new String(txtMatKhau.getPassword());
//				// Kiểm tra dữ liệu nhập
//				if (taiKhoan.equals("admin") && matKhau.equals("admin123")) {
//			        addPlaceholder(txtTaiKhoan, "Nhập tài khoản của bạn");
//			        addPlaceholder(txtMatKhau, "Nhập mật khẩu của bạn");
//		            frame.setContentPane(new Main_GUI());
//					// Chuyển đến giao diện chính của ứng dụng
//				} else {
//					JOptionPane.showMessageDialog(frame, "Tài khoản hoặc mật khẩu không đúng.", "Lỗi đăng nhập",
//							JOptionPane.ERROR_MESSAGE);
//			        addPlaceholder(txtTaiKhoan, "Nhập tài khoản của bạn");
//			        addPlaceholder(txtMatKhau, "Nhập mật khẩu của bạn");
//				}
				TaiKhoan tk1 = new TaiKhoan("TK000001", "admin", "Admin123@");
				TaiKhoan tk2 = new TaiKhoan("TK000002", "nhanvien1", "Nhanvien1@");
				List<NhanVien> dsnv = List.of(
						new NhanVien("NV2025100001", "Nguyễn Văn A", false, LocalDate.of(2005, 1, 1), "0987654321",
								"HCM", true, tk1, "SANG", true),
						new NhanVien("NV2025100002", "Nguyễn Văn B", false, LocalDate.of(2005, 1, 1), "0987654321",
								"HCM", false, tk2, "SANG", true));

				NhanVien nvDangNhap = dsnv.stream()
						.filter(nv -> nv.getTaiKhoan().getTenDangNhap().equals(txtTaiKhoan.getText().trim())
								&& nv.getTaiKhoan().getMatKhau().equals(new String(txtMatKhau.getPassword()).trim()))
						.findFirst().orElse(null);

				if (nvDangNhap != null) {
					JOptionPane.showMessageDialog(null,
							"Đăng nhập thành công!\nXin chào " + nvDangNhap.getTenNhanVien() + " ("
									+ (nvDangNhap.isQuanLy() ? "Quản lý" : "Nhân viên") + ")",
							"Thành công", JOptionPane.INFORMATION_MESSAGE);
					dispose();
					// Mở Main_GUI
					new Main_GUI(nvDangNhap).setVisible(true);;
				} else {
					JOptionPane.showMessageDialog(null, "Sai tài khoản hoặc mật khẩu!", "Đăng nhập thất bại",
							JOptionPane.ERROR_MESSAGE);
					System.out.println(txtTaiKhoan.getText().trim());
					System.out.println(new String(txtMatKhau.getPassword()));
				}
			}
		});

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
				JOptionPane.showMessageDialog(null, "Tính năng khôi phục mật khẩu đang được phát triển!", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
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

}