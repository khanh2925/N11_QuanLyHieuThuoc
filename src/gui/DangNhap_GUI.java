package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
// import java.util.List; // Không dùng list cứng nữa

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

import connectDB.connectDB; // Import connectDB
import customcomponent.ImagePanel;
import customcomponent.PillButton;
import customcomponent.RoundedBorder;
// Import DAO và Entity
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

public class DangNhap_GUI extends JFrame {

	private JTextField txtTaiKhoan;
	private JPasswordField txtMatKhau;
	
	// Khai báo DAO
	private TaiKhoan_DAO taiKhoan_DAO;
	private NhanVien_DAO nhanVien_DAO;

	public DangNhap_GUI() {
		// Khởi tạo DAO
		taiKhoan_DAO = new TaiKhoan_DAO();
		nhanVien_DAO = new NhanVien_DAO();
		
		// Kết nối CSDL
		try {
			connectDB.getInstance().connect();
			System.out.println("Kết nối CSDL thành công!");
		} catch (SQLException e) {
			System.err.println("Lỗi kết nối CSDL:");
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu.", "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE);
		}
		
		initialize();
	}

	private void initialize() {
		setTitle("Đăng nhập");
		setSize(1920, 1080);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
//		setUndecorated(true);

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
		
		// Test data
		txtTaiKhoan.setText("admin01");
		txtTaiKhoan.setForeground(Color.BLACK);


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
		
		// Test data
		txtMatKhau.setText("Admin@12345");
		txtMatKhau.setForeground(Color.BLACK);
		txtMatKhau.setEchoChar('●');


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
		// txtMatKhau.setEchoChar('●'); // Đã set ở trên

		// Sự kiện click vào nút mắt
		btnTogglePassword.addActionListener(e -> {
			// Bỏ qua nếu là placeholder
			String passText = new String(txtMatKhau.getPassword());
			if (passText.equals("Nhập mật khẩu của bạn")) {
				return;
			}
			
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

		// === SỰ KIỆN NÚT ĐĂNG NHẬP ===
		btnDangNhap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String tenDangNhap = txtTaiKhoan.getText().trim();
				String matKhau = new String(txtMatKhau.getPassword()).trim();

				// Kiểm tra rỗng
				if (tenDangNhap.isEmpty() || tenDangNhap.equals("Nhập tài khoản của bạn")) {
					JOptionPane.showMessageDialog(null, "Vui lòng nhập tên tài khoản!", "Lỗi", JOptionPane.WARNING_MESSAGE);
					txtTaiKhoan.requestFocus();
					return;
				}
				if (matKhau.isEmpty() || matKhau.equals("Nhập mật khẩu của bạn")) {
					JOptionPane.showMessageDialog(null, "Vui lòng nhập mật khẩu!", "Lỗi", JOptionPane.WARNING_MESSAGE);
					txtMatKhau.requestFocus();
					return;
				}
				
				// --- Logic kiểm tra đăng nhập ---
				// Do DAO và Entity của bạn không đồng bộ với CSDL (ví dụ NhanVien_DAO truy vấn cột MaTaiKhoan không tồn tại trong bảng NhanVien),
				// nên tôi sẽ viết logic truy vấn trực tiếp ở đây để đảm bảo đúng với CSDL bạn cung cấp.
				
				NhanVien nvDangNhap = getNhanVienDangNhap(tenDangNhap, matKhau);

				if (nvDangNhap != null) {
					// Kiểm tra trạng thái
					if(!nvDangNhap.isTrangThai()) {
						JOptionPane.showMessageDialog(null, "Tài khoản này đã bị khóa. Vui lòng liên hệ quản lý.", "Đăng nhập thất bại",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					JOptionPane.showMessageDialog(null,
							"Đăng nhập thành công!\nXin chào " + nvDangNhap.getTenNhanVien() + " ("
									+ (nvDangNhap.isQuanLy() ? "Quản lý" : "Nhân viên") + ")",
							"Thành công", JOptionPane.INFORMATION_MESSAGE);
					dispose();
					// Mở Main_GUI và truyền nhân viên đăng nhập vào
					new Main_GUI(nvDangNhap).setVisible(true);;
				} else {
					JOptionPane.showMessageDialog(null, "Sai tài khoản hoặc mật khẩu!", "Đăng nhập thất bại",
							JOptionPane.ERROR_MESSAGE);
					System.out.println("Thất bại: " + tenDangNhap + " / " + matKhau);
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
	
	/**
	 * Kiểm tra thông tin đăng nhập và lấy thông tin Nhân Viên tương ứng.
	 * Phương thức này truy vấn CSDL dựa trên SCHEMA bạn cung cấp (TaiKhoan JOIN NhanVien)
	 * @param tenDangNhap
	 * @param matKhau
	 * @return Đối tượng NhanVien nếu đăng nhập thành công, ngược lại trả về null
	 */
	private NhanVien getNhanVienDangNhap(String tenDangNhap, String matKhau) {
		NhanVien nv = null;
		Connection con = connectDB.getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		// Câu SQL này JOIN 2 bảng dựa trên CSDL bạn cung cấp
		String sql = "SELECT nv.*, tk.MaTaiKhoan, tk.TenDangNhap, tk.MatKhau "
				   + "FROM NhanVien nv "
				   + "JOIN TaiKhoan tk ON nv.MaNhanVien = tk.MaNhanVien "
				   + "WHERE tk.TenDangNhap = ? AND tk.MatKhau = ?";
		
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, tenDangNhap);
			stmt.setString(2, matKhau); // Lưu ý: CSDL đang lưu mật khẩu dạng clear text
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				// 1. Tạo đối tượng Tài Khoản
				String maTK = rs.getString("MaTaiKhoan");
				String tenDN = rs.getString("TenDangNhap");
				String mk = rs.getString("MatKhau");
				TaiKhoan tk = new TaiKhoan(maTK, tenDN, mk);
				
				// 2. Tạo đối tượng Nhân Viên
				String maNV = rs.getString("MaNhanVien");
				String tenNV = rs.getString("TenNhanVien");
				boolean gioiTinh = rs.getBoolean("GioiTinh");
				LocalDate ngaySinh = rs.getDate("NgaySinh").toLocalDate();
				String sdt = rs.getString("SoDienThoai");
				String diaChi = rs.getString("DiaChi");
				boolean quanLy = rs.getBoolean("QuanLy");
				String caLam = rs.getString("CaLam");
				boolean trangThai = rs.getBoolean("TrangThai");
				
				// Khởi tạo nhân viên
				nv = new NhanVien(maNV, tenNV, gioiTinh, ngaySinh, sdt, diaChi, quanLy, tk, caLam, trangThai);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi truy vấn dữ liệu đăng nhập.", "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
		} finally {
			// Đóng kết nối
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				// Không đóng Connection ở đây để có thể tái sử dụng
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		
		return nv;
	}

	private void addPlaceholder(JTextField field, String placeholder) {
		// Nếu field đã có text (do set test data) thì không set placeholder
		if(!field.getText().isEmpty() && !field.getText().equals(placeholder)) {
			return;
		}

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