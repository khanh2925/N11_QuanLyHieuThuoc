/**
 * @author Thanh Kha
 * @version 3.0
 * @since Oct 16, 2025
 *
 * Mô tả: Màn hình chính gồm Menu dọc bên trái và card layout chuyển màn hình
 */
package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import entity.NhanVien;

import java.awt.*;
import java.net.URL;
import java.util.*;

public class Main_GUI extends JFrame {
	private final JPanel cardPanel = new JPanel(new CardLayout());
	private final Map<String, JButton> menuContainers = new LinkedHashMap<>();
	// Lưu container submenu theo key của nút cha
	private final Map<String, JPanel> submenuContainers = new LinkedHashMap<>();
	// Tham chiếu tới panel menu chính để chèn submenu ngay dưới nút cha
	private JPanel menuPanel;

	private int MENU_WIDTH = 250;
	private int MENU_BUTTON_HEIGHT = 60;
	private int LOGO_WIDTH = 100;
	private int MENU_ICON_WIDTH = 33;

	private NhanVien nvDangNhap;
	private JLabel lblUserTop;

	public Main_GUI(NhanVien nv) {
		this.nvDangNhap = nv;
		setTitle("Hiệu thuốc Hòa An - Hệ thống quản lý");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1920, 1080);
		setLocationRelativeTo(null);
		buildUI();
		hienThongTinNhanVien();
	}

	public Main_GUI() {
		this(null);
	}

	private void buildUI() { // ⬅️ DI CHUYỂN NỘI DUNG từ constructor mặc định vào đây
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setDividerLocation(MENU_WIDTH);
		split.setDividerSize(0);
		split.setLeftComponent(createMenu());
		split.setRightComponent(cardPanel);

		add(split);

		boolean isQL = nvDangNhap != null && nvDangNhap.isQuanLy();
		if (isQL) { // Thêm các panel chức năng - sau này gắn tên panel vào đây
			cardPanel.add(new JPanel(new GridBagLayout()) {
				{
					JLabel lbl = new JLabel("Màn hình tổng quan");
					lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
					add(lbl);
				}
			}, "tongquan");
			cardPanel.add(new ThongKeDoanhThu_GUI(), "thongke");
			cardPanel.add(new ThemPhieuNhap_GUI(), "nhaphang");
			cardPanel.add(new HuyHang_GUI(), "xuathuy");
			cardPanel.add(new QLTraHang_GUI(), "trahang");
			cardPanel.add(new NhaCungCap_GUI(), "nhacungcap");
			cardPanel.add(new KhachHang_NV_GUI(), "khachhang");
			cardPanel.add(new KhuyenMai_GUI(), "khuyenmai");
			cardPanel.add(new NhanVien_QL_GUI(), "nhanvien");
			showCard("tongquan");
		} else {
			cardPanel.add(new TongQuanNV_GUI(), "tongquan");
			cardPanel.add(new BanHang_GUI(), "banhang");
			cardPanel.add(new TraHangNhanVien_GUI(), "trahang");
			cardPanel.add(new HuyHang_GUI(), "xuathuy");
			cardPanel.add(new KhachHang_NV_GUI(), "khachhang");
			showCard("banhang");
		}
	}
	
	private JPanel createMenu() {
	    // Panel chính bên trái, chia thành 2 phần: cuộn và cố định
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    mainPanel.setBackground(new Color(199, 234, 239));

	    // ===== Header: Logo cố định trên cùng =====
	    JPanel headerPanel = new JPanel();
	    headerPanel.setBackground(new Color(199, 234, 239));
	    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
	    headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

	    try {
	        ImageIcon iconLogo = new ImageIcon(getClass().getResource("/images/Logo.png"));
	        Image scaled = iconLogo.getImage().getScaledInstance(LOGO_WIDTH, LOGO_WIDTH, Image.SCALE_SMOOTH);
	        JLabel logo = new JLabel(new ImageIcon(scaled));
	        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
	        headerPanel.add(logo);
	    } catch (Exception ex) {
	        System.err.println("⚠️ Không tìm thấy Logo: " + ex.getMessage());
	    }
	    
	    // Panel chứa các nút menu (cuộn được)
	    JPanel menuScrollContent = new JPanel();
	    menuScrollContent.setLayout(new BoxLayout(menuScrollContent, BoxLayout.Y_AXIS));
	    menuScrollContent.setBackground(new Color(199, 234, 239));
	    this.menuPanel = menuScrollContent;

	    // Thêm menu button
	    boolean isQL = nvDangNhap != null && nvDangNhap.isQuanLy();
	    if (isQL) {
			addMenuButton(menuScrollContent, "Tổng quan", "tongquan", "/images/icon_tong_quan.png");
			addMenuButton(menuScrollContent, "Thống kê - Báo cáo", "thongke", "/images/icon_thong_ke.png");
			
			addMenuButton(menuScrollContent, "Tra cứu", "tracuu", "/images/icon_tra_cuu.png");
			addSubmenuButton("tracuu", "tracuusanpham", "Sản phẩm", "/images/icon_san_pham.png",
					new JPanel(new GridBagLayout()) {
				{
					JLabel lbl = new JLabel("Màn hình tra cứu sản phẩm");
					lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
					add(lbl);
				}
			});
			addSubmenuButton("tracuu", "tracuudonhang", "Đơn hàng", "/images/icon_don_hang.png",
					new TraCuuDonHang_GUI());
			addSubmenuButton("tracuu", "tracuudontrahang", "Đơn trả hàng", "/images/icon_tra_hang.png",
					new TraCuuDonTraHang_GUI());
			addSubmenuButton("tracuu", "tracuudonhuyhang", "Đơn huỷ hàng", "/images/icon_xuat_huy.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu đơn huỷ hàng");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});
			addSubmenuButton("tracuu", "tracuudonnhaphang", "Đơn nhập hàng", "/images/icon_nhap_hang.png",
					new NhapHang_GUI());
			addSubmenuButton("tracuu", "tracuunhanvien", "Nhân viên", "/images/icon_nhan_vien.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu đơn huỷ hàng");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});
			addSubmenuButton("tracuu", "tracuukhachhang", "Khách hàng", "/images/icon_khach_hang.png",
					new JPanel(new GridBagLayout()) {
				{
					JLabel lbl = new JLabel("Màn hình tra cứu khách hàng");
					lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
					add(lbl);
				}
			});
			addSubmenuButton("tracuu", "tracuunhacungcap", "Nhà cung cấp", "/images/icon_nha_cung_cap.png",
					new JPanel(new GridBagLayout()) {
				{
					JLabel lbl = new JLabel("Màn hình tra cứu nhà cung cấp");
					lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
					add(lbl);
				}
			});
			addSubmenuButton("tracuu", "tracuukhuyenmai", "Khuyến mãi", "/images/icon_khuyen_mai.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu khuyến mãi");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});
			addSubmenuButton("tracuu", "tracuudonvitinh", "Đơn vị tính", "/images/icon_don_vi_tinh.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu đơn vị tính");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});
			addSubmenuButton("tracuu", "tracuubanggia", "Bảng giá", "/images/icon_bang_gia.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu bảng giá");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});
			
			addMenuButton(menuScrollContent, "Quản lý nhập hàng", "nhaphang", "/images/icon_nhap_hang.png");
			addMenuButton(menuScrollContent, "Quản lý xuất huỷ", "xuathuy", "/images/icon_xuat_huy.png");
			addMenuButton(menuScrollContent, "Quản lý trả hàng", "trahang", "/images/icon_tra_hang.png");

			addMenuButton(menuScrollContent, "Sản phẩm", "sanpham", "/images/icon_san_pham.png");
			addSubmenuButton("sanpham", "danhsachsanpham", "Danh sách sản phẩm", "/images/icon_danh_sach.png",
					new SanPham_GUI());
			addSubmenuButton("sanpham", "donvitinh", "Đơn vị tính", "/images/icon_don_vi_tinh.png",
					new DonViTinh_QL_GUI());
			addSubmenuButton("sanpham", "banggia", "Bảng giá", "/images/icon_bang_gia.png", new BangGia_GUI());

			addMenuButton(menuScrollContent, "Quản lý nhà cung cấp", "nhacungcap", "/images/icon_nha_cung_cap.png");
			addMenuButton(menuScrollContent, "Quẩn lý khách hàng", "khachhang", "/images/icon_khach_hang.png");
			addMenuButton(menuScrollContent, "Quản lý khuyến mãi", "khuyenmai", "/images/icon_khuyen_mai.png");
			addMenuButton(menuScrollContent, "Quản lý nhân viên", "nhanvien", "/images/icon_nhan_vien.png");

			menuScrollContent.add(Box.createVerticalGlue());
		} else {
			addMenuButton(menuScrollContent, "Tổng quan", "tongquan", "/images/icon_tong_quan.png");
			addMenuButton(menuScrollContent, "Bán hàng", "banhang", "/images/icon_ban_hang.png");
			addMenuButton(menuScrollContent, "Trả hàng", "trahang", "/images/icon_tra_hang.png");
			addMenuButton(menuScrollContent, "Xuất huỷ", "xuathuy", "/images/icon_xuat_huy.png");

			addMenuButton(menuScrollContent, "Tra cứu", "tracuu", "/images/icon_tra_cuu.png");
			addSubmenuButton("tracuu", "tracuudonhang", "Đơn hàng", "/images/icon_don_hang.png",
					new TraCuuDonHang_GUI());
			addSubmenuButton("tracuu", "tracuudontrahang", "Đơn trả hàng", "/images/icon_tra_hang.png",
					new TraCuuDonTraHang_GUI());
			addSubmenuButton("tracuu", "tracuudonhuyhang", "Đơn huỷ hàng", "/images/icon_xuat_huy.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu đơn huỷ hàng");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});
			addSubmenuButton("tracuu", "tracuusanpham", "Sản phẩm", "/images/icon_san_pham.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu sản phẩm");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});
			addSubmenuButton("tracuu", "tracuukhuyenmai", "Khuyến mãi", "/images/icon_khuyen_mai.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu khuyến mãi");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});
			addSubmenuButton("tracuu", "tracuukhachhang", "Khách hàng", "/images/icon_khach_hang.png",
					new JPanel(new GridBagLayout()) {
						{
							JLabel lbl = new JLabel("Màn hình tra cứu khách hàng");
							lbl.setFont(new Font("Times New Roman", Font.PLAIN, 48));
							add(lbl);
						}
					});

			addMenuButton(menuScrollContent, "Quản lý khách hàng", "khachhang", "/images/icon_khach_hang.png");

			menuScrollContent.add(Box.createVerticalGlue());
		}

	 // ScrollPane cho phần menu chính (cuộn mượt và hiện đại)
	    JScrollPane scrollPane = new JScrollPane(menuScrollContent,
	            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    scrollPane.setBorder(null);
	    scrollPane.getViewport().setBackground(new Color(199, 234, 239));

	    // Tùy chỉnh scrollbar hiện đại
	    scrollPane.getVerticalScrollBar().setUnitIncrement(20);
	    scrollPane.getVerticalScrollBar().setOpaque(false);
	    scrollPane.setOpaque(false);

	    // ==== Tùy biến giao diện thanh cuộn ====
	    scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
	        private final Dimension d = new Dimension();

	        @Override
	        protected JButton createDecreaseButton(int orientation) {
	            return createZeroButton();
	        }

	        @Override
	        protected JButton createIncreaseButton(int orientation) {
	            return createZeroButton();
	        }

	        private JButton createZeroButton() {
	            JButton button = new JButton();
	            button.setPreferredSize(d);
	            button.setMinimumSize(d);
	            button.setMaximumSize(d);
	            return button;
	        }

	        @Override
	        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
	            // Ẩn phần nền track để thanh cuộn “nổi” hơn
	            Graphics2D g2 = (Graphics2D) g.create();
	            g2.setComposite(AlphaComposite.SrcOver.derive(0f)); // hoàn toàn trong suốt
	            g2.dispose();
	        }

	        @Override
	        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
	            if (!c.isEnabled()) return;
	            Graphics2D g2 = (Graphics2D) g.create();

	            // Màu thumb: xám trong suốt + bo tròn + hiệu ứng hover
	            Color base = new Color(80, 80, 80, 80);
	            Color hover = new Color(80, 80, 80, 130);

	            if (isThumbRollover()) {
	                g2.setColor(hover);
	            } else {
	                g2.setColor(base);
	            }

	            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 10, 10);
	            g2.dispose();
	        }

	        @Override
	        protected Dimension getMinimumThumbSize() {
	            return new Dimension(8, 40); // mảnh hơn mặc định
	        }

	        @Override
	        protected Dimension getMaximumThumbSize() {
	            return new Dimension(8, 9999);
	        }
	    });

	    
	    // Panel dưới cùng (không cuộn)
	    JPanel bottomPanel = new JPanel();
	    bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
	    bottomPanel.setBackground(new Color(199, 234, 239));
	    bottomPanel.setBorder(new EmptyBorder(10, 8, 10, 8));

	    lblUserTop = new JLabel("Chưa đăng nhập");
	    lblUserTop.setFont(new Font("SansSerif", Font.BOLD, 14));
	    lblUserTop.setAlignmentX(Component.LEFT_ALIGNMENT);
	    bottomPanel.add(lblUserTop);
	    bottomPanel.add(Box.createVerticalStrut(8));

	    JButton btnLogout = new JButton("Đăng xuất");
	    btnLogout.setFocusPainted(false);
	    btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
	    btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
	    btnLogout.setBackground(new Color(0, 0, 0, 0));
	    btnLogout.setBorder(null);
	    ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/images/icon_dang_xuat.png"));
	    Image scaledLogout = logoutIcon.getImage().getScaledInstance(MENU_ICON_WIDTH, MENU_ICON_WIDTH, Image.SCALE_SMOOTH);
	    btnLogout.setIcon(new ImageIcon(scaledLogout));
	    btnLogout.addActionListener(e -> onLogout());
	    bottomPanel.add(btnLogout);

	    mainPanel.add(headerPanel, BorderLayout.NORTH);
	    mainPanel.add(scrollPane, BorderLayout.CENTER);
	    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

	    // Giữ nguyên kích thước
	    mainPanel.setPreferredSize(new Dimension(MENU_WIDTH, getHeight()));
	    return mainPanel;
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
//		ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
		ImageIcon icon = null;
		if (iconPath == null || iconPath.trim().isEmpty()) {
			System.err.println("⚠️ [Main_GUI] Icon path is null or empty for menu: " + text);
		} else {
			URL url = getClass().getResource(iconPath);
			if (url == null) {
				System.err.println("❌ [Main_GUI] Icon not found for menu: " + text + " | Path: " + iconPath);
				try {
					String base = getClass().getProtectionDomain().getCodeSource().getLocation().toExternalForm();
					System.err.println("🔍 [Main_GUI] Base classpath: " + base);
				} catch (Exception ex) {
					System.err.println("⚠️ [Main_GUI] Cannot determine base classpath: " + ex.getMessage());
				}
			} else {
				icon = new ImageIcon(url);
				System.out.println("✅ [Main_GUI] Loaded icon: " + iconPath + " for menu: " + text);
			}
		}
		// Scale kích thước icon
		Image scaledIcon = icon.getImage().getScaledInstance(MENU_ICON_WIDTH, MENU_ICON_WIDTH, Image.SCALE_SMOOTH);
		btn.setIcon(new ImageIcon(scaledIcon));
		btn.addActionListener(e -> showCard(key));

		if ("logout".equals(key)) {
			btn.addActionListener(e -> onLogout()); // gọi hàm xử lý đăng xuất
		} else {
			btn.addActionListener(e -> showCard(key));
			menuContainers.put(key, btn); // chỉ lưu các nút có card
		}
		menu.add(btn);
		menuContainers.put(key, btn);
	}

	private void onLogout() {
		int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất không?", "Đăng xuất",
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			dispose(); // hoặc chuyển về form đăng nhập
			new DangNhap_GUI().setVisible(true);
		}
	}

	private void showCard(String key) {
		((CardLayout) cardPanel.getLayout()).show(cardPanel, key);

		// Reset màu tất cả
		menuContainers.forEach((k, b) -> {
			b.setBackground(new Color(0, 0, 0, 0));
			b.setForeground(Color.BLACK);
			b.setOpaque(false);
		});

		JButton activeBtn = menuContainers.get(key);
		if (activeBtn == null)
			return;

		// Kiểm tra xem có phải là submenu
		boolean isSubmenu = submenuContainers.values().stream()
				.anyMatch(panel -> Arrays.asList(panel.getComponents()).contains(activeBtn));

		if (isSubmenu) {
			// submenu active
			activeBtn.setBackground(new Color(0x0E736A));
			activeBtn.setForeground(Color.WHITE);
			activeBtn.setOpaque(true);

			// tô màu cha + chỉ giữ mở submenu của cha, ẩn các submenu khác
			final String[] parentKeyHolder = { null };
			submenuContainers.forEach((parentKey, panel) -> {
				if (Arrays.asList(panel.getComponents()).contains(activeBtn)) {
					JButton parentBtn = menuContainers.get(parentKey);
					if (parentBtn != null) {
						parentBtn.setBackground(new Color(0x1E9086));
						parentBtn.setForeground(Color.WHITE);
						parentBtn.setOpaque(true);
					}
					parentKeyHolder[0] = parentKey;
				}
			});
			// Ẩn mọi submenu KHÁC, chỉ giữ mở của cha hiện tại
			final String keepKey = parentKeyHolder[0];
			submenuContainers.forEach((k, p) -> p.setVisible(k.equals(keepKey)));
		} else {
			// Là menu cha — tìm submenu đầu tiên (nếu có)
			activeBtn.setBackground(new Color(0x1E9086));
			activeBtn.setForeground(Color.WHITE);
			activeBtn.setOpaque(true);

			JPanel sub = submenuContainers.get(key);
			if (sub != null && sub.getComponentCount() > 0 && sub.getComponent(0) instanceof JButton) {
				JButton firstSub = (JButton) sub.getComponent(0);
				String firstSubKey = menuContainers.entrySet().stream().filter(entry -> entry.getValue() == firstSub)
						.map(Map.Entry::getKey).findFirst().orElse(null);
				if (firstSubKey != null) {
					((CardLayout) cardPanel.getLayout()).show(cardPanel, firstSubKey);
					firstSub.setBackground(new Color(0x0E736A));
					firstSub.setForeground(Color.WHITE);
					firstSub.setOpaque(true);
				}
			}
			// Khi click sang menu cha khác: Ẩn toàn bộ submenu KHÔNG thuộc menu này
			submenuContainers.forEach((k, p) -> {
				if (!k.equals(key))
					p.setVisible(false);
			});
		}
	}

	public JPanel createSubmenu(String parentKey) {
		if (menuPanel == null) {
			throw new IllegalStateException("menuPanel chưa được khởi tạo. Hãy gán this.menuPanel trong createMenu().");
		}
		if (!menuContainers.containsKey(parentKey)) {
			throw new IllegalArgumentException("parentKey không tồn tại trong menu: " + parentKey);
		}
		// Nếu đã có thì trả về luôn
		if (submenuContainers.containsKey(parentKey)) {
			return submenuContainers.get(parentKey);
		}

		JPanel sub = new JPanel();
		sub.setLayout(new BoxLayout(sub, BoxLayout.Y_AXIS));
		sub.setOpaque(false);
		// thụt vào 1 chút
		sub.setBorder(new EmptyBorder(0, 16, 8, 0));
		sub.setVisible(false); // đóng mặc định

		submenuContainers.put(parentKey, sub);

		// Chèn ngay sau nút cha trong menuPanel
		JButton parentBtn = menuContainers.get(parentKey);
		int insertIdx = -1;
		for (int i = 0; i < menuPanel.getComponentCount(); i++) {
			if (menuPanel.getComponent(i) == parentBtn) {
				insertIdx = i + 1;
				break;
			}
		}
		if (insertIdx >= 0)
			menuPanel.add(sub, insertIdx);
		else
			menuPanel.add(sub); // fallback

		// Đảm bảo nút cha bấm sẽ toggle đóng/mở khối submenu
		ensureParentToggle(parentKey);

		menuPanel.revalidate();
		menuPanel.repaint();
		return sub;
	}

	/*
	 * Thêm 1 nút submenu vào dưới nút cha, đồng thời đăng ký 1 card mới để show.
	 */
	public void addSubmenuButton(String parentKey, String subKey, String text, String iconPath, JPanel content) {
		JPanel subContainer = submenuContainers.get(parentKey);
		if (subContainer == null) {
			subContainer = createSubmenu(parentKey);
		}
		if (content != null) {
			// Đăng ký card nội dung
			cardPanel.add(content, subKey);
		}

		JButton btn = new JButton(text);
		btn.setHorizontalAlignment(SwingConstants.LEFT);
		// kích thước “nhỏ” hơn button chính 1 chút
		btn.setPreferredSize(new Dimension(MENU_WIDTH, Math.max(36, MENU_BUTTON_HEIGHT - 16)));
		btn.setMaximumSize(new Dimension(MENU_WIDTH, Math.max(36, MENU_BUTTON_HEIGHT - 16)));
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		// thụt vào sâu hơn các nút cha
		btn.setBorder(new EmptyBorder(4, 24, 4, 8));

		// Thêm icon nhỏ phía trước (có kiểm tra null và log)
		ImageIcon icon = null;
		if (iconPath == null || iconPath.isBlank()) {
			System.err.println("iconPath bị null hoặc rỗng khi tạo icon!");
		} else {
			URL url = getClass().getResource(iconPath);

			if (url == null) {
				System.err.println("Không tìm thấy resource icon tại: " + iconPath);
			} else {
				icon = new ImageIcon(url);
				// Scale kích thước icon
				Image scaledIcon = icon.getImage().getScaledInstance(MENU_ICON_WIDTH - 7, MENU_ICON_WIDTH - 7,
						Image.SCALE_SMOOTH);
				icon = new ImageIcon(scaledIcon);
				btn.setIcon(new ImageIcon(scaledIcon));
			}
		}

		btn.addActionListener(e -> showCard(subKey));

		subContainer.add(btn);
		menuContainers.put(subKey, btn);

		menuPanel.revalidate();
		menuPanel.repaint();
	}

	/*
	 * Gắn toggle đóng/mở container submenu khi bấm nút cha.
	 */
	private void ensureParentToggle(String parentKey) {
		JButton parent = menuContainers.get(parentKey);
		if (parent == null)
			return;
		// Gắn cờ boolean để biết nút này đã gắn hành vi toggle submenu, nếu đã là true
		// thì thoát ngay
		// Đảm bảo chỉ gắn listener 1 lần
		if (Boolean.TRUE.equals(parent.getClientProperty("submenuBound")))
			return;
		parent.putClientProperty("submenuBound", Boolean.TRUE);

		parent.addActionListener(e -> {
			JPanel sub = submenuContainers.get(parentKey);
			if (sub != null) {
				boolean wasVisible = sub.isVisible();
				sub.setVisible(!wasVisible);
				menuPanel.revalidate();
				menuPanel.repaint();
				// Khi submenu mới mở, tự show card đầu tiên
				if (!wasVisible && sub.getComponentCount() > 0 && sub.getComponent(0) instanceof JButton) {
					JButton firstSub = (JButton) sub.getComponent(0);
					// Lấy key card từ map
					String firstKey = menuContainers.entrySet().stream().filter(entry -> entry.getValue() == firstSub)
							.map(Map.Entry::getKey).findFirst().orElse(null);
					if (firstKey != null) {
						showCard(firstKey);
					}
				}
			}
		});
	}

	private void hienThongTinNhanVien() {
		if (lblUserTop == null)
			return;
		if (nvDangNhap == null) {
			lblUserTop.setText("Chưa đăng nhập");
			return;
		}
		// Sẽ gọi getHoTen() (xem mục #2)
		lblUserTop.setText(nvDangNhap.getMaNhanVien() + " - " + nvDangNhap.getTenNhanVien());
	}

}