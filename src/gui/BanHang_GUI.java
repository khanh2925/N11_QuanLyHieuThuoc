package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;

import dao.SanPham_DAO;
import dao.LoSanPham_DAO;
import dao.KhachHang_DAO;
import dao.HoaDon_DAO; // 💡 THÊM DAO
import dao.ChiTietHoaDon_DAO; // 💡 THÊM DAO

import entity.SanPham;
import entity.LoSanPham;
import entity.TaiKhoan;
import entity.NhanVien;
import entity.Session;
import entity.KhachHang;
import entity.HoaDon; // 💡 THÊM ENTITY
import entity.ChiTietHoaDon; // 💡 THÊM ENTITY

public class BanHang_GUI extends JPanel {

	private JTextField txtTimThuoc;
	private JPanel pnDanhSachDon;

	// 💡 KHAI BÁO CÁC DAO MỚI
	private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
	private final ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();

	private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
	private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
	private final LoSanPham_DAO loDAO = new LoSanPham_DAO();

	private JLabel lblNhanVien;

	// --- KHAI BÁO THUỘC TÍNH TỔNG TIỀN ĐỂ CÓ THỂ CẬP NHẬT TRÊN GIAO DIỆN ---
	private JLabel lblTongHangValue; // Để cập nhật "Tổng tiền hàng:"
	private JLabel lblTongHDValue; // Để cập nhật "TỔNG CỘNG:"
	private JLabel lblTienThuaValue; // Để cập nhật "Tiền thừa:"

	// 💡 Thêm thuộc tính lưu Khách hàng được chọn
	private KhachHang khachHangHienTai = new KhachHang("KH-0001", "Khách vãng lai", true, "0900000000",
			LocalDate.now().minusYears(18)); // Mặc định là Khách lẻ
	private JTextField txtTienKhach; // Thêm để truy cập Tiền khách đưa

	public BanHang_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	/** Khởi tạo giao diện chính */
	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		// ===== HEADER =====
		JPanel pnHeader = new JPanel(null);
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setBackground(new Color(0xE3F2F5));
		add(pnHeader, BorderLayout.NORTH);

		// Ô tìm kiếm (số đăng ký thuốc)
		txtTimThuoc = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimThuoc, "Nhập số đăng ký thuốc (VD: VN-12345)...");
		txtTimThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		txtTimThuoc.setBounds(25, 17, 420, 60);
		txtTimThuoc.setBorder(new RoundedBorder(20));
		txtTimThuoc.setBackground(Color.WHITE);
		txtTimThuoc.setForeground(Color.GRAY);
		pnHeader.add(txtTimThuoc);

		// Khi nhấn Enter sẽ tìm thuốc
		txtTimThuoc.addActionListener(e -> timSanPhamTheoSoDangKy());

		// Nút thêm đơn (chưa dùng)
		JButton btnThemDon = new PillButton("Thêm đơn");
		btnThemDon.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnThemDon.setBounds(490, 30, 120, 40);
		pnHeader.add(btnThemDon);

		// ===== CENTER: DANH SÁCH SẢN PHẨM =====
		JPanel pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(
				new CompoundBorder(new LineBorder(new Color(0x00C853), 3, true), new EmptyBorder(5, 5, 5, 5)));
		add(pnCenter, BorderLayout.CENTER);

		pnDanhSachDon = new JPanel();
		pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
		pnDanhSachDon.setBackground(Color.WHITE);

		JScrollPane scrollPane = new JScrollPane(pnDanhSachDon);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pnCenter.add(scrollPane);

		// ===== CỘT PHẢI =====
		add(buildRightPanel(), BorderLayout.EAST);
	}

	// [Các hàm: timSanPhamTheoSoDangKy(), createDonPanel(), findByName(),
	// styleMiniButton(), parse()]
	// GIỮ NGUYÊN các hàm này từ mã gốc (đã được kiểm tra và sửa lỗi tính tổng tiền
	// ngầm ở các bước trước)

	/**
	 * Tìm sản phẩm theo SĐK, chọn lô cũ nhất, gộp nếu trùng lô; kiểm tra tồn & trừ
	 * tạm
	 */
	private void timSanPhamTheoSoDangKy() {
		String soDK = txtTimThuoc.getText().trim();
		if (soDK.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số đăng ký thuốc!");
			return;
		}

		SanPham sp = sanPhamDAO.timTheoSoDangKy(soDK);
		if (sp == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm có số đăng ký: " + soDK,
					"Không tìm thấy sản phẩm", JOptionPane.ERROR_MESSAGE);
			txtTimThuoc.setText("");
			return;
		}

		// === 1️⃣ Xác định xem sản phẩm này đã có lô nào trong danh sách chưa ===
		LoSanPham lo = null;

		// Duyệt tất cả panel hiện có, lấy lô cuối cùng của sản phẩm này (nếu có)
		for (Component comp : pnDanhSachDon.getComponents()) {
			if (!(comp instanceof JPanel pnDon))
				continue;
			Object maLoPanel = pnDon.getClientProperty("maLo");
			if (maLoPanel == null)
				continue;

			// Lấy mã sản phẩm từ panel hiện tại (nếu bạn có gán thêm sau này)
			// Tạm thời dùng heuristic: nếu có lô đó trong DB thuộc cùng sản phẩm
			LoSanPham loTmp = loDAO.layLoTheoMa((String) maLoPanel);
			if (loTmp != null && loTmp.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
				lo = loTmp; // nhớ lại lô cuối cùng đang dùng
			}
		}

		// === 2️⃣ Nếu chưa có lô trong danh sách → lấy lô cũ nhất còn hàng ===
		if (lo == null) {
			lo = loDAO.layLoCuNhat(sp.getMaSanPham());
			while (lo != null && lo.getSoLuongTon() <= 0)
				lo = loDAO.layLoKeTiep(sp.getMaSanPham(), lo.getHanSuDung());
		}

		// === 3️⃣ Nếu có rồi nhưng hết hàng → lấy lô kế tiếp ===
		else if (lo.getSoLuongTon() <= 0) {
			lo = loDAO.layLoKeTiep(sp.getMaSanPham(), lo.getHanSuDung());
		}

		// === 4️⃣ Nếu vẫn null thì hết hàng toàn bộ ===
		if (lo == null) {
			JOptionPane.showMessageDialog(this, "Tất cả các lô của sản phẩm này đã hết hàng!", "Hết hàng",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String maLo = lo.getMaLo();
		LocalDate hsd = lo.getHanSuDung();
		int tonGoc = lo.getSoLuongTon();

		// Nếu đã có lô này → tăng SL nếu còn tồn
		for (Component comp : pnDanhSachDon.getComponents()) {
			if (!(comp instanceof JPanel pnDon))
				continue;
			Object maLoPanel = pnDon.getClientProperty("maLo");
			if (maLoPanel == null || !maLo.equals(maLoPanel))
				continue;

			JTextField txtSL = (JTextField) findByName(pnDon, "txtSL");
			JLabel lblTong = (JLabel) findByName(pnDon, "lblTong");
			JLabel lblTon = (JLabel) findByName(pnDon, "lblTon");

			if (txtSL == null || lblTong == null || lblTon == null)
				continue;

			int sl = parse(txtSL.getText()) + 1;
			int tonBanDau = (int) pnDon.getClientProperty("tonGoc");

			if (sl > tonBanDau) {
				// Lấy lô kế tiếp
				LoSanPham loTiep = loDAO.layLoKeTiep(sp.getMaSanPham(), lo.getHanSuDung());
				if (loTiep != null) {
					JOptionPane.showMessageDialog(this,
							"Lô " + maLo + " đã hết hàng!\nTự động chuyển sang lô " + loTiep.getMaLo(),
							"Tự động đổi lô", JOptionPane.INFORMATION_MESSAGE);

					// tạo panel mới cho lô kế tiếp
					pnDanhSachDon
							.add(createDonPanel(sp, loTiep.getMaLo(), loTiep.getHanSuDung(), loTiep.getSoLuongTon()));
					pnDanhSachDon.revalidate();
					pnDanhSachDon.repaint();
					txtTimThuoc.setText("");
					capNhatTongTien(); // Cập nhật tổng tiền khi thêm lô mới
				} else {
					JOptionPane.showMessageDialog(this, "Tất cả các lô của sản phẩm này đã hết hàng!", "Hết hàng",
							JOptionPane.WARNING_MESSAGE);
				}
				return;
			}

			txtSL.setText(String.valueOf(sl));
			lblTong.setText(String.format("%,.0f đ", sl * sp.getGiaBan()));
			lblTon.setText("Tồn: " + (tonBanDau - sl));
			txtTimThuoc.setText("");
			capNhatTongTien(); // Cập nhật tổng tiền khi tăng SL
			return;
		}

		// Nếu chưa có panel → thêm mới
		if (tonGoc <= 0) {
			JOptionPane.showMessageDialog(this, "Lô " + maLo + " đã hết hàng!");
			return;
		}

		pnDanhSachDon.add(createDonPanel(sp, maLo, hsd, tonGoc));
		pnDanhSachDon.revalidate();
		pnDanhSachDon.repaint();
		txtTimThuoc.setText("");
		capNhatTongTien(); // Cập nhật tổng tiền khi thêm mới
	}

	/** Tạo panel sản phẩm, dùng tonGoc cố định */
	private JPanel createDonPanel(SanPham sp, String maLo, LocalDate hsd, int tonGoc) {
		JPanel pnDon = new JPanel(null);
		pnDon.setPreferredSize(new Dimension(1040, 120));
		pnDon.setBackground(Color.WHITE);
		pnDon.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
		pnDon.putClientProperty("maLo", maLo);
		pnDon.putClientProperty("tonGoc", tonGoc);

		int centerY = 120 / 2;

		// ==== ẢNH ====
		JLabel lblHinh = new JLabel("", SwingConstants.CENTER);
		lblHinh.setBounds(27, centerY - 30, 100, 100);
		lblHinh.setBorder(new LineBorder(Color.LIGHT_GRAY));
		try {
			ImageIcon icon = new ImageIcon(getClass().getResource(sp.getHinhAnh()));
			lblHinh.setIcon(new ImageIcon(icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
		} catch (Exception e) {
			lblHinh.setText("Ảnh");
		}
		pnDon.add(lblHinh);

		// ==== TÊN ====
		JLabel lblTen = new JLabel(sp.getTenSanPham());
		lblTen.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTen.setBounds(168, centerY - 30, 300, 34);
		pnDon.add(lblTen);

		// ==== LÔ / HSD / TỒN ====
		JLabel lblLo = new JLabel("Lô: " + maLo);
		lblLo.setBounds(168, centerY + 12, 150, 25);
		pnDon.add(lblLo);

		JLabel lblHsd = new JLabel("HSD: " + (hsd != null ? hsd.toString() : "--"));
		lblHsd.setBounds(310, centerY + 12, 120, 25);
		pnDon.add(lblHsd);

		JLabel lblTon = new JLabel("Tồn: " + (tonGoc - 1));
		lblTon.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblTon.setForeground(new Color(150, 0, 0));
		lblTon.setBounds(430, centerY + 12, 100, 25);
		lblTon.setName("lblTon");
		pnDon.add(lblTon);

		// ==== SL ====
		JPanel pnTangGiam = new JPanel(new BorderLayout(5, 0));
		pnTangGiam.setBounds(500, centerY, 137, 36);
		pnTangGiam.setBackground(new Color(0xF8FAFB));
		pnTangGiam.setBorder(new LineBorder(new Color(0xB0BEC5), 2, true));
		pnDon.add(pnTangGiam);

		JButton btnGiam = new JButton("−");
		JButton btnTang = new JButton("+");
		JTextField txtSL = new JTextField("1");
		txtSL.setHorizontalAlignment(SwingConstants.CENTER);
		txtSL.setFont(new Font("Segoe UI", Font.BOLD, 16));
		txtSL.setBorder(null);
		txtSL.setName("txtSL");

		styleMiniButton(btnGiam);
		styleMiniButton(btnTang);
		pnTangGiam.add(btnGiam, BorderLayout.WEST);
		pnTangGiam.add(txtSL, BorderLayout.CENTER);
		pnTangGiam.add(btnTang, BorderLayout.EAST);

		// ==== GIÁ & TỔNG ====
		JLabel lblGia = new JLabel(String.format("%,.0f đ", sp.getGiaBan()));
		lblGia.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblGia.setBounds(700, centerY, 120, 29);
		pnDon.add(lblGia);

		JLabel lblTong = new JLabel(String.format("%,.0f đ", sp.getGiaBan()));
		lblTong.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTong.setBounds(850, centerY, 120, 29);
		lblTong.setName("lblTong");
		pnDon.add(lblTong);

		// ==== NÚT XÓA ====
		JButton btnXoa = new JButton();
		btnXoa.setBounds(980, centerY, 35, 35);
		try {
			ImageIcon iconBin = new ImageIcon(getClass().getResource("/images/bin.png"));
			Image img = iconBin.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			btnXoa.setIcon(new ImageIcon(img));
		} catch (Exception ignored) {
		}
		btnXoa.setBorderPainted(false);
		btnXoa.setContentAreaFilled(false);
		btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pnDon.add(btnXoa);

		// ==== LOGIC TĂNG / GIẢM ====
		// 🔹 TĂNG SỐ LƯỢNG
		btnTang.addActionListener(e -> {
			int total = pnDanhSachDon.getComponentCount();
			int currentIndex = -1;

			// Xác định vị trí panel hiện tại
			for (int i = 0; i < total; i++) {
				if (pnDanhSachDon.getComponent(i) == pnDon) {
					currentIndex = i;
					break;
				}
			}

			// Nếu không phải panel cuối (lô mới nhất) → chặn
			if (currentIndex != total - 1) {
				JOptionPane.showMessageDialog(pnDon,
						"Chỉ được thao tác với lô mới nhất!\nVui lòng hoàn tất lô hiện tại trước.", "Ràng buộc thứ tự",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			int sl = parse(txtSL.getText()) + 1;
			int tonBanDau = (int) pnDon.getClientProperty("tonGoc");

			// Nếu hết hàng trong lô này → tự chuyển lô kế tiếp
			if (sl > tonBanDau) {
				LoSanPham loHienTai = loDAO.layLoTheoMa(maLo);
				LoSanPham loTiep = loDAO.layLoKeTiep(sp.getMaSanPham(), loHienTai.getHanSuDung());

				if (loTiep != null) {
					JOptionPane.showMessageDialog(pnDon,
							"Lô " + maLo + " đã hết hàng!\nTự động chuyển sang lô " + loTiep.getMaLo(),
							"Tự động đổi lô", JOptionPane.INFORMATION_MESSAGE);

					// Tạo panel mới cho lô kế tiếp
					pnDanhSachDon
							.add(createDonPanel(sp, loTiep.getMaLo(), loTiep.getHanSuDung(), loTiep.getSoLuongTon()));
					pnDanhSachDon.revalidate();
					pnDanhSachDon.repaint();
					capNhatTongTien(); // Cập nhật tổng tiền khi thêm lô mới
					return;
				} else {
					JOptionPane.showMessageDialog(pnDon, "Tất cả các lô của sản phẩm này đã hết hàng!", "Hết hàng",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			}

			// Cập nhật khi vẫn còn hàng
			txtSL.setText(String.valueOf(sl));
			lblTong.setText(String.format("%,.0f đ", sl * sp.getGiaBan()));
			lblTon.setText("Tồn: " + (tonBanDau - sl));
			capNhatTongTien(); // Cập nhật tổng tiền khi tăng SL
		});

		// 🔹 GIẢM SỐ LƯỢNG
		btnGiam.addActionListener(e -> {
			int total = pnDanhSachDon.getComponentCount();
			int currentIndex = -1;

			// Xác định vị trí panel hiện tại
			for (int i = 0; i < total; i++) {
				if (pnDanhSachDon.getComponent(i) == pnDon) {
					currentIndex = i;
					break;
				}
			}

			// Nếu không phải panel cuối (mới nhất) → chặn
			if (currentIndex != total - 1) {
				JOptionPane.showMessageDialog(pnDon,
						"Chỉ được thao tác với lô mới nhất!\nKhông thể thay đổi lô trước đó.", "Ràng buộc thứ tự",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			int sl = parse(txtSL.getText());
			int tonBanDau = (int) pnDon.getClientProperty("tonGoc");

			if (sl > 1) {
				sl--;
				txtSL.setText(String.valueOf(sl));
				lblTong.setText(String.format("%,.0f đ", sl * sp.getGiaBan()));
				lblTon.setText("Tồn: " + (tonBanDau - sl));
				capNhatTongTien(); // Cập nhật tổng tiền khi giảm SL
			}
		});

		// ==== XÓA DÒNG (chỉ cho xoá lô mới nhất) ====
		btnXoa.addActionListener(e -> {
			int total = pnDanhSachDon.getComponentCount();
			int currentIndex = -1;

			// Tìm vị trí panel hiện tại trong danh sách
			for (int i = 0; i < total; i++) {
				if (pnDanhSachDon.getComponent(i) == pnDon) {
					currentIndex = i;
					break;
				}
			}

			// Nếu không tìm thấy thì thôi
			if (currentIndex == -1)
				return;

			// Chỉ cho phép xoá panel cuối cùng (mới nhất)
			if (currentIndex != total - 1) {
				JOptionPane.showMessageDialog(pnDon,
						"Chỉ có thể xoá lô được thêm sau cùng (lô mới nhất)!\n" + "Vui lòng xoá theo thứ tự ngược lại.",
						"Ràng buộc thứ tự xoá", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Nếu là panel cuối → xoá bình thường
			pnDanhSachDon.remove(pnDon);
			pnDanhSachDon.revalidate();
			pnDanhSachDon.repaint();
			capNhatTongTien(); // Cập nhật tổng tiền khi xóa
		});
		// 🔹 NHẬP SỐ LƯỢNG BẰNG TAY RỒI ẤN ENTER
		txtSL.addActionListener(e -> {
		    int sl = parse(txtSL.getText());
		    int tonBanDau = (int) pnDon.getClientProperty("tonGoc");

		    if (sl < 1) sl = 1;
		    if (sl > tonBanDau) {
		        JOptionPane.showMessageDialog(pnDon,
		            "Số lượng vượt quá tồn kho (" + tonBanDau + "). Tự động điều chỉnh về mức tối đa.",
		            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
		        sl = tonBanDau;
		    }

		    txtSL.setText(String.valueOf(sl));
		    lblTong.setText(String.format("%,.0f đ", sl * sp.getGiaBan()));
		    lblTon.setText("Tồn: " + (tonBanDau - sl));
		    capNhatTongTien();
		});

		return pnDon;
		
	}

	private Component findByName(Container root, String name) {
		for (Component c : root.getComponents()) {
			if (name.equals(c.getName()))
				return c;
			if (c instanceof Container) {
				Component f = findByName((Container) c, name);
				if (f != null)
					return f;
			}
		}
		return null;
	}

	private void styleMiniButton(JButton btn) {
		btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btn.setFocusPainted(false);
		btn.setBackground(new Color(0xE0F2F1));
		btn.setBorder(new LineBorder(new Color(0x80CBC4), 1, true));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setOpaque(true);
		btn.setPreferredSize(new Dimension(40, 36));
	}

	private int parse(String s) {
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Tính tổng tiền hàng (chưa trừ KM) từ tất cả các panel sản phẩm.
	 */
	private void capNhatTongTien() {
		double tongHang = 0;

		for (Component comp : pnDanhSachDon.getComponents()) {
			if (comp instanceof JPanel pnDon) {
				try {
					// Lấy các thành phần cần thiết
					JTextField txtSL = (JTextField) findByName(pnDon, "txtSL");

					// Lấy SanPham từ lô (MaSP được lưu trong LoSanPham)
					String maLo = (String) pnDon.getClientProperty("maLo");
					LoSanPham lo = loDAO.layLoTheoMa(maLo);

					if (txtSL != null && lo != null && lo.getSanPham() != null) {
						int sl = parse(txtSL.getText());
						double giaBan = lo.getSanPham().getGiaBan();
						tongHang += sl * giaBan;
					}
				} catch (Exception e) {
					System.err.println("Lỗi khi tính tổng tiền: " + e.getMessage());
				}
			}
		}

		// Cập nhật các label trên giao diện
		if (lblTongHangValue != null) {
			lblTongHangValue.setText(String.format("%,.0f đ", tongHang));
		}
		if (lblTongHDValue != null) {
			lblTongHDValue.setText(String.format("TỔNG CỘNG: %,.0f Đ", tongHang));
		}

		// Cập nhật Tiền thừa (Logic phức tạp hơn, tạm thời 0)
		if (lblTienThuaValue != null) {
			// 💡 Cần tính Tiền khách đưa - Tổng hóa đơn (Sau khi trừ KM nếu có)
			// Tạm thời set 0
			lblTienThuaValue.setText("0 đ");
		}
		if (lblTienThuaValue != null && txtTienKhach != null) {
			capNhatTienThuaDonGian();
		}
	}

	private void capNhatTienThuaDonGian() {
	    try {
	        // ✅ 1. Lấy tổng hóa đơn (lọc ký tự số)
	        String rawTong = lblTongHDValue.getText().replaceAll("[^0-9]", "");
	        double tong = rawTong.isEmpty() ? 0 : Double.parseDouble(rawTong);

	        // ✅ 2. Lấy tiền khách đưa (và kiểm tra rỗng / hợp lệ)
	        String tienKhachStr = txtTienKhach.getText().trim();
	        if (tienKhachStr.isEmpty()) {
	            lblTienThuaValue.setForeground(Color.RED);
	            lblTienThuaValue.setText("Chưa nhập tiền khách");
	            return;
	        }

	        double tienKhach;
	        try {
	            tienKhach = Double.parseDouble(tienKhachStr);
	        } catch (NumberFormatException ex) {
	            lblTienThuaValue.setForeground(Color.RED);
	            lblTienThuaValue.setText("Sai định dạng số");
	            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho tiền khách!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
	            return;
	        }

	        // ✅ 3. Tính và hiển thị tiền thừa
	        double tienThua = tienKhach - tong;
	        if (tienThua < 0) {
	            lblTienThuaValue.setForeground(Color.RED);
	            lblTienThuaValue.setText("Còn thiếu " + String.format("%,.0f đ", -tienThua));
	        } else {
	            lblTienThuaValue.setForeground(new Color(0x00796B));
	            lblTienThuaValue.setText(String.format("%,.0f đ", tienThua));
	        }

	    } catch (Exception e) {
	        lblTienThuaValue.setForeground(Color.GRAY);
	        lblTienThuaValue.setText("0 đ");
	        System.err.println("❌ Lỗi khi tính tiền thừa: " + e.getMessage());
	    }
	}


	/** Panel bên phải: khách hàng & thanh toán */
	private JPanel buildRightPanel() {
		// ... (Code khởi tạo pnCotPhaiRight và thông tin Nhân viên/Thời gian) ...
		JPanel pnCotPhaiRight = new JPanel();
		pnCotPhaiRight.setPreferredSize(new Dimension(1920 - 383 - 1073, 1080));
		pnCotPhaiRight.setBackground(Color.WHITE);
		pnCotPhaiRight.setBorder(new EmptyBorder(20, 20, 20, 20)); // padding tổng thể
		pnCotPhaiRight.setLayout(new BoxLayout(pnCotPhaiRight, BoxLayout.Y_AXIS));

		// <<< KHỞI TẠO lblNhanVien >>>
		lblNhanVien = new JLabel();
		lblNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblNhanVien.setName("lblNhanVien");

		// ==== TÊN NHÂN VIÊN & THỜI GIAN ====
		JPanel pnNhanVien = new JPanel(new BorderLayout(5, 5));
		pnNhanVien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		pnNhanVien.setOpaque(false);

		// Lấy thông tin nhân viên hiện đang đăng nhập
		TaiKhoan tk = Session.getInstance().getTaiKhoanDangNhap();
		if (tk != null && tk.getNhanVien() != null) {
			NhanVien nv = tk.getNhanVien();
			lblNhanVien.setText("👤 " + nv.getTenNhanVien() + " (" + nv.getCaLam() + ")");
		} else {
			lblNhanVien.setText("👤 [Chưa đăng nhập]");
		}

		JLabel lblThoiGian = new JLabel("", SwingConstants.RIGHT); // ← set thời gian hiện tại
		lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblThoiGian.setName("lblThoiGian");

		pnNhanVien.add(lblNhanVien, BorderLayout.WEST);
		pnNhanVien.add(lblThoiGian, BorderLayout.EAST);
		pnCotPhaiRight.add(pnNhanVien);
		pnCotPhaiRight.add(Box.createVerticalStrut(10));

		// ===== ĐƯỜNG LINE NGAY DƯỚI =====
		JSeparator lineNV = new JSeparator();
		lineNV.setForeground(new Color(200, 200, 200));
		lineNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		pnCotPhaiRight.add(Box.createVerticalStrut(4));
		pnCotPhaiRight.add(lineNV);
		pnCotPhaiRight.add(Box.createVerticalStrut(10));

		// ===== Ô TÌM KHÁCH HÀNG =====
		JTextField txtTimKH = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKH, "🔍 Nhập số điện thoại khách hàng");
		txtTimKH.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtTimKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		txtTimKH.setBorder(new LineBorder(new Color(0x00C0E2), 2, true));
		pnCotPhaiRight.add(txtTimKH);
		pnCotPhaiRight.add(Box.createVerticalStrut(10));

		// --- KHỞI TẠO VÀ HIỂN THỊ TÊN KHÁCH HÀNG ---

		JPanel pnTenKH = new JPanel(new BorderLayout());
		pnTenKH.setOpaque(false);
		pnTenKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

		JLabel lblTenKHLeft = new JLabel("Tên khách hàng:");
		lblTenKHLeft.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		JLabel lblTenKHValue = new JLabel("Khách lẻ"); // Giá trị mặc định
		lblTenKHValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblTenKHValue.setForeground(new Color(0x00796B));

		pnTenKH.add(lblTenKHLeft, BorderLayout.WEST);
		pnTenKH.add(lblTenKHValue, BorderLayout.EAST);
		pnCotPhaiRight.add(pnTenKH);
		pnCotPhaiRight.add(Box.createVerticalStrut(15));

		// ===== SỰ KIỆN TÌM KHÁCH HÀNG (Cập nhật khachHangHienTai) =====
		txtTimKH.addActionListener(e -> {
			String soDT = txtTimKH.getText().trim();
			if (soDT.isEmpty() || soDT.length() != 10 || !soDT.matches("0[0-9]{9}")) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại 10 chữ số hợp lệ!");
				// 💡 Reset về khách lẻ nếu nhập sai
				khachHangHienTai = new KhachHang("KH-0001", "Khách vãng lai", true, "0900000000",
						LocalDate.now().minusYears(18));
				lblTenKHValue.setText("Khách vãng lai");
				lblTenKHValue.setForeground(new Color(0x00796B));
				return;
			}

			KhachHang khTimThay = null;
			// Thực hiện tìm kiếm trong DAO
			// (Bạn nên có hàm timTheoSDT() trong KhachHang_DAO để tối ưu)
			for (KhachHang kh : khachHangDAO.getAllKhachHang()) {
				if (kh.getSoDienThoai() != null && kh.getSoDienThoai().equals(soDT)) {
					khTimThay = kh;
					break;
				}
			}

			if (khTimThay != null) {
				khachHangHienTai = khTimThay; // 💡 Gán khách hàng tìm thấy
				lblTenKHValue.setText(khTimThay.getTenKhachHang());
				lblTenKHValue.setForeground(new Color(0x00796B));
			} else {
				khachHangHienTai = new KhachHang("KH-0001", "Khách vãng lai", true, "0900000000",
						LocalDate.now().minusYears(18)); // 💡 Reset về khách lẻ
				lblTenKHValue.setText("—");
				lblTenKHValue.setForeground(Color.GRAY);

				int confirm = JOptionPane.showConfirmDialog(this,
						"Không tìm thấy khách hàng có số: " + soDT + "\nBạn có muốn thêm khách hàng mới không?",
						"Không tìm thấy", JOptionPane.YES_NO_OPTION);

				if (confirm == JOptionPane.YES_OPTION) {
					JOptionPane.showMessageDialog(this, "👉 Tính năng thêm khách hàng mới sẽ được bổ sung sau.");
				}
			}
		});

		// --- KHU VỰC THÔNG TIN HÓA ĐƠN ---

		// ... (Code hiển thị Tổng tiền hàng, Giảm giá) ...
		JPanel pnTongHang = new JPanel(new BorderLayout());
		pnTongHang.setOpaque(false);
		pnTongHang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

		JLabel lblTongHangLeft = new JLabel("Tổng tiền hàng:");
		lblTongHangLeft.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		lblTongHangValue = new JLabel("0 đ");
		lblTongHangValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblTongHangValue.setHorizontalAlignment(SwingConstants.RIGHT);

		pnTongHang.add(lblTongHangLeft, BorderLayout.WEST);
		pnTongHang.add(lblTongHangValue, BorderLayout.EAST);
		pnCotPhaiRight.add(pnTongHang);

		pnCotPhaiRight.add(makeLabel("Giảm giá sản phẩm:", "0 đ")); // lblGiamSP
		pnCotPhaiRight.add(makeLabel("Giảm giá hóa đơn:", "0 đ")); // lblGiamHD

		JPanel pnMGG = new JPanel(new BorderLayout(5, 5));
		pnMGG.setOpaque(false);
		pnMGG.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		JLabel lblGiamPhanTram = new JLabel("—"); // Giá trị mặc định
		lblGiamPhanTram.setForeground(Color.RED);
		lblGiamPhanTram.setFont(new Font("Segoe UI", Font.BOLD, 13));
		pnMGG.add(lblGiamPhanTram, BorderLayout.WEST);

		pnCotPhaiRight.add(pnMGG);
		pnCotPhaiRight.add(Box.createVerticalStrut(10));

		// Tổng hóa đơn
		JPanel pnTongTien = new JPanel(new BorderLayout());
		pnTongTien.setOpaque(false);
		pnTongTien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		lblTongHDValue = new JLabel("TỔNG CỘNG: 0 Đ");
		lblTongHDValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblTongHDValue.setHorizontalAlignment(SwingConstants.RIGHT);
		pnTongTien.add(lblTongHDValue, BorderLayout.WEST);

		pnCotPhaiRight.add(pnTongTien);
		pnCotPhaiRight.add(Box.createVerticalStrut(10));

		// Ô nhập tiền khách đưa
		JPanel pnTienKhach = new JPanel(new BorderLayout(8, 0));
		pnTienKhach.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		pnTienKhach.setOpaque(false);

		JLabel lblTienKhach = new JLabel("Tiền khách đưa:");
		lblTienKhach.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		txtTienKhach = new JTextField(); // 💡 Gán vào thuộc tính để truy cập sau
		txtTienKhach.setFont(new Font("Segoe UI", Font.BOLD, 16));
		txtTienKhach.setHorizontalAlignment(SwingConstants.RIGHT);
		txtTienKhach.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0x00C0E2), 2, true),
				new EmptyBorder(5, 10, 5, 10)));
		txtTienKhach.setBackground(new Color(0xF0FAFA));
		txtTienKhach.setForeground(new Color(0x00796B));
		txtTienKhach.setName("txtTienKhach");
		txtTienKhach.addActionListener(e -> capNhatTienThuaDonGian());

		pnTienKhach.add(lblTienKhach, BorderLayout.WEST);
		pnTienKhach.add(txtTienKhach, BorderLayout.CENTER);
		pnCotPhaiRight.add(pnTienKhach);
		pnCotPhaiRight.add(Box.createVerticalStrut(10));

		// Label Tiền thừa
		JPanel pnTienThua = new JPanel(new BorderLayout());
		pnTienThua.setOpaque(false);
		pnTienThua.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

		JLabel lblTienThuaLeft = new JLabel("Tiền thừa:");
		lblTienThuaLeft.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		lblTienThuaValue = new JLabel("0 đ");
		lblTienThuaValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblTienThuaValue.setHorizontalAlignment(SwingConstants.RIGHT);

		pnTienThua.add(lblTienThuaLeft, BorderLayout.WEST);
		pnTienThua.add(lblTienThuaValue, BorderLayout.EAST);
		pnCotPhaiRight.add(pnTienThua);

		// ====== NÚT BÁN HÀNG ======
		pnCotPhaiRight.add(Box.createVerticalGlue());

		JButton btnBanHang = new PillButton("Bán hàng");
		btnBanHang.setFont(new Font("Segoe UI", Font.BOLD, 20));
		btnBanHang.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnBanHang.addActionListener(e -> xuLyBanHang()); // 💡 Gán sự kiện Bán hàng

		pnCotPhaiRight.add(btnBanHang);
		pnCotPhaiRight.add(Box.createVerticalStrut(10));

		capNhatTongTien();

		return pnCotPhaiRight;
	}

	private JPanel makeLabel(String left, String right) {
		// ... (Hàm makeLabel giữ nguyên) ...
		JPanel pn = new JPanel(new BorderLayout());
		pn.setOpaque(false);
		pn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

		JLabel l = new JLabel(left);
		l.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		JLabel r = new JLabel(right);
		r.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		r.setHorizontalAlignment(SwingConstants.RIGHT);

		pn.add(l, BorderLayout.WEST);
		pn.add(r, BorderLayout.EAST);

		return pn;
	}

	// 💡 HÀM XỬ LÝ SỰ KIỆN BÁN HÀNG
	private void xuLyBanHang() {
		// --- KIỂM TRA DANH SÁCH SẢN PHẨM ---
		if (pnDanhSachDon.getComponentCount() == 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào hóa đơn!", "Thiếu sản phẩm",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// --- KIỂM TRA TIỀN KHÁCH ---
		double tong = 0, tienKhach = 0;
		try {
			String raw = lblTongHDValue.getText().replaceAll("[^0-9]", "");
			if (!raw.isEmpty())
				tong = Double.parseDouble(raw);
			tienKhach = Double.parseDouble(txtTienKhach.getText().trim());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập tiền khách đưa hợp lệ!", "Thiếu dữ liệu",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (tienKhach < tong) {
			JOptionPane.showMessageDialog(this,
					"Khách hàng chưa đưa đủ tiền!\nCòn thiếu: " + String.format("%,.0f đ", (tong - tienKhach)),
					"Thiếu tiền", JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 1. KIỂM TRA DỮ LIỆU
		if (pnDanhSachDon.getComponentCount() == 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào hóa đơn!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 2. LẤY DỮ LIỆU CẦN THIẾT
		TaiKhoan tk = Session.getInstance().getTaiKhoanDangNhap();
		// Lấy thông tin nhân viên (dùng NV tạm nếu không có Session)
		NhanVien nv = tk != null && tk.getNhanVien() != null ? tk.getNhanVien()
				: new NhanVien("NV9999999999", "Nhân viên bán hàng", "SANG", true);

		// 3. TẠO VÀ LƯU HÓA ĐƠN
		String maHD = hoaDonDAO.taoMaHoaDon(); // Dùng DAO để tạo mã HD

		HoaDon hd = new HoaDon(maHD, khachHangHienTai.getMaKhachHang(), // 💡 Dùng Khách hàng đã chọn
				LocalDate.now(), nv, null, // KhuyenMai (chưa xử lý)
				false // thuocTheoDon (chưa xử lý)
		);

		// 4. DUYỆT CÁC PANEL ĐỂ TẠO CHI TIẾT (LƯU KÈM MA LÔ)
		List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();

		for (Component comp : pnDanhSachDon.getComponents()) {
			if (!(comp instanceof JPanel pnDon))
				continue;

			try {
				String maLo = (String) pnDon.getClientProperty("maLo");
				JTextField txtSL = (JTextField) findByName(pnDon, "txtSL");
				int sl = parse(txtSL.getText());

				LoSanPham lo = loDAO.layLoTheoMa(maLo);

				if (lo == null || lo.getSanPham() == null || sl <= 0)
					continue;

				// Tạo ChiTietHoaDon
				ChiTietHoaDon cthd = new ChiTietHoaDon(hd, lo, // 💡 Truyền LoSanPham đầy đủ
						sl, lo.getSanPham().getGiaBan(), null);

				danhSachChiTiet.add(cthd);

			} catch (Exception e) {
				System.err.println("Lỗi khi tạo ChiTietHoaDon: " + e.getMessage());
				JOptionPane.showMessageDialog(this, "Lỗi tạo chi tiết hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		hd.setChiTietHoaDonList(danhSachChiTiet);

		// 5. LƯU DỮ LIỆU VÀ CẬP NHẬT TỒN KHO
		if (hoaDonDAO.themHoaDon(hd)) { // Dùng hàm đã có Transaction

			// 5.1 CẬP NHẬT TỒN KHO
			if (capNhatTonKho(danhSachChiTiet)) {
				JOptionPane.showMessageDialog(this, "Bán hàng thành công! Mã HD: " + maHD, "Thành công",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this,
						"Bán hàng thành công nhưng CẬP NHẬT TỒN KHO THẤT BẠI. Cần kiểm tra lại tồn kho thủ công!",
						"Cảnh báo", JOptionPane.WARNING_MESSAGE);
			}

			// 5.2 Dọn dẹp giao diện
			pnDanhSachDon.removeAll();
			pnDanhSachDon.revalidate();
			pnDanhSachDon.repaint();
			capNhatTongTien();

		} else {
		    JOptionPane.showMessageDialog(this,
		        "Lưu hóa đơn thất bại! Vui lòng kiểm tra Log/Kết nối DB.",
		        "Lỗi", JOptionPane.ERROR_MESSAGE);
		    return; // ❌ Thoát, không reset
		}

		// ✅ Chỉ reset nếu thêm hóa đơn thành công
		resetFormSauBanHang();

	}

	/**
	 * Cập nhật số lượng tồn của các lô sản phẩm đã bán trong DB.
	 * 
	 * @param chiTietHoaDonList danh sách chi tiết hóa đơn
	 * @return true nếu tất cả lô được cập nhật thành công
	 */
	private boolean capNhatTonKho(List<ChiTietHoaDon> chiTietHoaDonList) {
		boolean allSuccess = true;
		for (ChiTietHoaDon cthd : chiTietHoaDonList) {
			try {
				LoSanPham loCanCapNhat = cthd.getLoSanPham();
				int slBan = (int) cthd.getSoLuong();

				// Tải lại đối tượng lô từ DB để đảm bảo dữ liệu mới nhất
				LoSanPham loHienTai = loDAO.layLoTheoMa(loCanCapNhat.getMaLo());

				if (loHienTai != null) {
					int tonMoi = loHienTai.getSoLuongTon() - slBan;

					if (tonMoi < 0) {
						System.err.println(
								"Lỗi: Số lượng tồn kho lô " + loHienTai.getMaLo() + " bị âm! (" + tonMoi + ")");
						allSuccess = false;
						continue;
					}

					loHienTai.setSoLuongTon(tonMoi);
					if (!loDAO.capNhatLoSanPham(loHienTai)) {
						allSuccess = false;
						System.err.println("Lỗi DB khi cập nhật lô: " + loHienTai.getMaLo());
					}
				} else {
					System.err
							.println("Lỗi: Không tìm thấy lô sản phẩm để cập nhật tồn kho: " + loCanCapNhat.getMaLo());
					allSuccess = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				allSuccess = false;
			}
		}
		return allSuccess;
	}

	/** 🧹 Reset toàn bộ form sau khi bán hàng thành công */
	private void resetFormSauBanHang() {
		// 1. Xóa danh sách sản phẩm
		pnDanhSachDon.removeAll();
		pnDanhSachDon.revalidate();
		pnDanhSachDon.repaint();

		// 2. Reset thông tin khách hàng
		khachHangHienTai = new KhachHang("KH-0001", "Khách vãng lai", true, "0900000000",
				LocalDate.now().minusYears(18));

		// Nếu có TextField tìm KH hoặc label hiển thị tên KH, reset nó (tìm bằng tên
		// nếu chưa có tham chiếu)
		Component compTimKH = findByName(this, "txtTimKH");
		if (compTimKH instanceof JTextField txtTimKH) {
			txtTimKH.setText("");
		}
		Component compTenKH = findByName(this, "lblTenKHValue");
		if (compTenKH instanceof JLabel lblTenKH) {
			lblTenKH.setText("Khách lẻ");
			lblTenKH.setForeground(new Color(0x00796B));
		}

		// 3. Reset tiền khách & tổng tiền
		if (txtTienKhach != null)
			txtTienKhach.setText("");
		if (lblTienThuaValue != null) {
			lblTienThuaValue.setText("0 đ");
			lblTienThuaValue.setForeground(Color.GRAY);
		}
		if (lblTongHangValue != null)
			lblTongHangValue.setText("0 đ");
		if (lblTongHDValue != null)
			lblTongHDValue.setText("TỔNG CỘNG: 0 Đ");
		
		// 4. Làm sạch dữ liệu khác nếu có
		capNhatTongTien(); // Đảm bảo sync lại giao diện
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame f = new JFrame("Bán Hàng - Lô & Tồn cố định");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(1280, 800);
			f.setLocationRelativeTo(null);
			f.setContentPane(new BanHang_GUI());
			f.setVisible(true);
		});
	}
}