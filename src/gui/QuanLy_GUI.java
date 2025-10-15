package gui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuanLy_GUI extends JFrame {
	private final JPanel cardPanel = new JPanel(new CardLayout());
	private final Map<String, JButton> menuButtons = new LinkedHashMap<>();

	public QuanLy_GUI() {
		setTitle("Hiệu thuốc Hòa An - Hệ thống quản lý");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1280, 800);
		setLocationRelativeTo(null);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(300);
		split.setDividerSize(0);
		split.setLeftComponent(createMenu());
		split.setRightComponent(cardPanel);
		add(split);

		// Thêm các panel chức năng - sau này gắn tên panel vào đây
		cardPanel.add(new JPanel(), "tongquan");
		cardPanel.add(new JPanel(), "donhang");
		cardPanel.add(new JPanel(), "sanpham");
		cardPanel.add(new JPanel(), "kho");
		cardPanel.add(new JPanel(), "khachhang");
		cardPanel.add(new JPanel(), "khuyenmai");
		cardPanel.add(new JPanel(), "nhanvien");
		cardPanel.add(new JPanel(), "thongke");
		cardPanel.add(new JPanel(), "thongtin");

		showCard("tongquan");
	}

	private JPanel createMenu() {
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
		menu.setBackground(new Color(0xCBE7E9));

		ImageIcon iconLogo = new ImageIcon(getClass().getResource("/images/Logo.png"));
		Image scaled = iconLogo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // chỉnh kích thước logo
		JLabel logo = new JLabel(new ImageIcon(scaled));
		logo.setAlignmentX(Component.LEFT_ALIGNMENT);
		menu.add(logo);

		addMenuButton(menu, "Tổng quan", "tongquan", "/images/icon_tong_quan");
		addMenuButton(menu, "Đơn hàng", "donhang", "/images/icon_don_hang");
		addMenuButton(menu, "Sản phẩm", "sanpham", "/images/icon_san_pham.png");
		addMenuButton(menu, "Kho", "kho", "/images/icon_kho.png");
		addMenuButton(menu, "Khách hàng", "khachhang", "/images/icon_khach_hang.png");
		addMenuButton(menu, "Khuyến mãi", "khuyenmai", "/images/icon_khuyen_mai.png");
		addMenuButton(menu, "Nhân viên", "nhanvien", "/images/icon_nhan_vien.png");
		addMenuButton(menu, "Thống kê - Báo cáo", "thongke", "/images/icon_thong_ke.png");
		addMenuButton(menu, "Thông tin cá nhân", "thongtin", "/images/icon_thong_tin.png");

		menu.add(Box.createVerticalGlue());
		addMenuButton(menu, "Đăng xuất", "logout", "/images/icon_dang_xuat.png");
		

		return menu;
	}

	private void addMenuButton(JPanel menu, String text, String key, String iconPath) {
		JButton btn = new JButton(text);
		btn.setFocusPainted(false);
		btn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 60));
		btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setBackground(new Color(0, 0, 0, 0));
		btn.setBorder(null);
		btn.setFont(new Font("SansSerif", Font.BOLD, 16));
		// Thêm icon nhỏ phía trước
		ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
		Image scaledIcon = icon.getImage().getScaledInstance(33, 33, Image.SCALE_SMOOTH); // Scale kích thước icon
		btn.setIcon(new ImageIcon(scaledIcon));
		btn.addActionListener(e -> showCard(key));
		
	    if ("logout".equals(key)) {
	        btn.addActionListener(e -> onLogout()); // gọi hàm xử lý đăng xuất riêng
	    } else {
	        btn.addActionListener(e -> showCard(key));
	        menuButtons.put(key, btn); // chỉ lưu các nút có card
	    }
		
		menu.add(btn);
		menuButtons.put(key, btn);
	}
	
	private void onLogout() {
	    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất không?", 
	        "Đăng xuất", JOptionPane.YES_NO_OPTION);
	    if (confirm == JOptionPane.YES_OPTION) {
	        dispose(); // hoặc chuyển về form đăng nhập
//	        new DangNhap_GUI().setVisible(true);
	    }
	}

	

	private void showCard(String key) {
	    ((CardLayout) cardPanel.getLayout()).show(cardPanel, key);
	    menuButtons.forEach((k, b) -> {
	        boolean active = k.equals(key);
	        b.setBackground(active ? new Color(0x1E9086) : new Color(0,0,0,0));
	        b.setForeground(active ? Color.WHITE : Color.BLACK);
	        b.setOpaque(active);
	    });
	}

}
