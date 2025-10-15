package gui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class NhanVien_GUI extends JFrame {
	private final JPanel cardPanel = new JPanel(new CardLayout());
	private final Map<String, JButton> menuButtons = new LinkedHashMap<>();
	
	private int MENU_WIDTH = 250;
	private int MENU_BUTTON_HEIGHT = 60;
	private int LOGO_WIDTH = 100;
	private int MENU_ICON_WIDTH = 33;
	
	public NhanVien_GUI() {
		setTitle("Hiệu thuốc Hòa An - Hệ thống quản lý");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1920, 1080);
		setLocationRelativeTo(null);

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(MENU_WIDTH);
		split.setDividerSize(0);
		split.setLeftComponent(createMenu());
		split.setRightComponent(cardPanel);

		add(split);

		// Thêm các panel chức năng - sau này gắn tên panel vào đây
		cardPanel.add(new BanHang_GUI(), "banhang");
		cardPanel.add(new JPanel(), "tracuu");
		cardPanel.add(new JPanel(), "trahang");
		cardPanel.add(new JPanel(), "khachhang");
		cardPanel.add(new JPanel(), "thongtin");

		showCard("banhang");
	}

	private JPanel createMenu() {
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
		menu.setBackground(new Color(0xCBE7E9));
		
		ImageIcon iconLogo = new ImageIcon(getClass().getResource("/images/Logo.png"));
		Image scaled = iconLogo.getImage().getScaledInstance(LOGO_WIDTH, LOGO_WIDTH, Image.SCALE_SMOOTH); // chỉnh kích thước logo
		JLabel logo = new JLabel(new ImageIcon(scaled));
		logo.setAlignmentX(Component.LEFT_ALIGNMENT);
		menu.add(logo);

		addMenuButton(menu, "Bán hàng", "banhang", "/images/icon_ban_hang.png");
		addMenuButton(menu, "Tra cứu", "tracuu", "/images/icon_tra_cuu.png");
		addMenuButton(menu, "Trả hàng", "trahang", "/images/icon_tra_hang.png");
		addMenuButton(menu, "Khách hàng", "khachhang", "/images/icon_khach_hang.png");
		addMenuButton(menu, "Thông tin cá nhân", "thongtin", "/images/icon_thong_tin.png");

		menu.add(Box.createVerticalGlue());
		addMenuButton(menu, "Đăng xuất", "logout", "/images/icon_dang_xuat.png");
		

		return menu;
	}

	private void addMenuButton(JPanel menu, String text, String key, String iconPath) {
		JButton btn = new JButton(text);
		btn.setFocusPainted(false);
		btn.setPreferredSize(new Dimension(MENU_WIDTH, MENU_BUTTON_HEIGHT));
		btn.setMaximumSize(new Dimension(MENU_WIDTH, MENU_BUTTON_HEIGHT));
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		btn.setBackground(new Color(0, 0, 0, 0));
		btn.setBorder(null);
		btn.setFont(new Font("SansSerif", Font.BOLD, 16));
		// Thêm icon nhỏ phía trước
		ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
		Image scaledIcon = icon.getImage().getScaledInstance(MENU_ICON_WIDTH, MENU_ICON_WIDTH, Image.SCALE_SMOOTH); // Scale kích thước icon
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
	public static void main(String[] args) {
	    java.awt.EventQueue.invokeLater(() -> {
	        try {
	            new NhanVien_GUI().setVisible(true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	}

}
