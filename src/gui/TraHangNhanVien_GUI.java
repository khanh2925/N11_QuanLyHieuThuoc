package gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import customcomponent.PillButton;
import customcomponent.TaoJtextNhanh;
import customcomponent.TaoLabelNhanh;
import dao.ChiTietHoaDon_DAO;
import dao.ChiTietPhieuTra_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.LoSanPham_DAO;
import dao.PhieuTra_DAO;
import entity.Session;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuTra;
import entity.HoaDon;
import entity.KhachHang;
import entity.LoSanPham;
import entity.PhieuTra;
import entity.TaiKhoan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TraHangNhanVien_GUI extends JPanel implements ActionListener {

	private static final int MAX_RETURN_DAYS = 7;

	private static final String PLACEHOLDER_TIM_HOA_DON = "Tìm hoá đơn theo mã";
	private static final String PLACEHOLDER_TIM_KH = "Tìm hoá đơn theo số điện thoại khách hàng";

	private static final String REGEX_MA_HOA_DON = "^HD-\\d{8}-\\d{4}$";

	private JTextField txtTimHoaDon;
	private JTextField txtTimKH;

	private JPanel pnDanhSachDon;
	private JTextField txtTienTra;
	private JTextField txtTenKhachHang;
	private JTextField txtNguoiBan;
	private JTextField txtMaHoaDon;
	private JLabel lblThoiGian;

	private double tongTien;
	private double tienTra = 0;

	private LocalDate today = LocalDate.now();
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final HoaDon_DAO hoaDonDAO;
	private final ChiTietHoaDon_DAO cthdDAO;
	private final PhieuTra_DAO ptDAO;
	private final LoSanPham_DAO loDAO;

	private DefaultTableModel modelTraHang;
	private JTable tblTraHang;

	private JTextArea txtGhiChuGiamGia;

	private PillButton btnTraHang;

	private PillButton btnHuy;

	public TraHangNhanVien_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();

		hoaDonDAO = new HoaDon_DAO();
		cthdDAO = new ChiTietHoaDon_DAO();
		ptDAO = new PhieuTra_DAO();
		loDAO = new LoSanPham_DAO();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		add(createHeaderPanel(), BorderLayout.NORTH);
		add(createCenterPanel(), BorderLayout.CENTER);
		add(createRightPanel(), BorderLayout.EAST);
	}

	private JPanel createHeaderPanel() {
		JPanel pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setBackground(new Color(0xE3F2F5));

		txtTimHoaDon = TaoJtextNhanh.timKiem();
		txtTimHoaDon.setBorder(new LineBorder(new Color(0x00C0E2), 3, true));
		txtTimHoaDon.setBounds(25, 17, 480, 60);
		txtTimHoaDon.setText(PLACEHOLDER_TIM_HOA_DON);
		txtTimHoaDon.setForeground(Color.GRAY);

		txtTimHoaDon.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtTimHoaDon.getText().equals("Tìm hoá đơn theo mã")) {
					txtTimHoaDon.setText("");
					txtTimHoaDon.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (txtTimHoaDon.getText().trim().isEmpty()) {
					txtTimHoaDon.setText("Tìm hoá đơn theo mã");
					txtTimHoaDon.setForeground(Color.GRAY);
				}
			}
		});

		pnHeader.add(txtTimHoaDon);

		txtTimHoaDon.addActionListener(this);
		return pnHeader;
	}

	private JPanel createCenterPanel() {
		JPanel pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setPreferredSize(new Dimension(1087, 1080));

		pnCenter.setBorder(
				new CompoundBorder(new LineBorder(new Color(0x00C853), 3, true), new EmptyBorder(10, 10, 10, 10)));

		String[] col = { "Mã lô", "Tên sản phẩm", "Số lượng", "Giá bán", "Thành tiền", "Lý do", "Đơn vị tính" };
		modelTraHang = new DefaultTableModel(col, 0) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return col == 5; // chỉ cho sửa lý do
			}
		};
		tblTraHang = new JTable(modelTraHang);

		pnDanhSachDon = new JPanel();
		pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
		pnDanhSachDon.setBackground(Color.WHITE);

		JScrollPane scr = new JScrollPane(pnDanhSachDon);
		scr.setBorder(null);
		scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scr.getVerticalScrollBar().setUnitIncrement(16);

		pnCenter.add(scr, BorderLayout.CENTER);

		return pnCenter;
	}

	private JPanel createRightPanel() {
		JPanel pnRight = new JPanel();
		pnRight.setPreferredSize(new Dimension(1920 - 383 - 1073, 1080));
		pnRight.setBackground(Color.WHITE);
		pnRight.setBorder(new EmptyBorder(25, 25, 25, 25));
		pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));

		// ==== Tìm khách hàng ====
		Box boxTimKhachHang = Box.createHorizontalBox();
		txtTimKH = TaoJtextNhanh.nhapLieu(PLACEHOLDER_TIM_KH);
		txtTimKH.setMaximumSize(new Dimension(480, 50));
		txtTimKH.setPreferredSize(new Dimension(480, 50));
		txtTimKH.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtTimKH.getText().equals(PLACEHOLDER_TIM_KH)) {
					txtTimKH.setText("");
					txtTimKH.setForeground(Color.BLACK); // Đổi màu chữ khi nhập
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				String s = txtTimKH.getText().trim();

				if (s.isEmpty() || s.equals(PLACEHOLDER_TIM_KH)) {
					txtTimKH.setText(PLACEHOLDER_TIM_KH);
					txtTimKH.setForeground(Color.GRAY); // Màu placeholder (tùy thư viện bạn dùng)
					return;
				}
			}
		});

		boxTimKhachHang.add(txtTimKH);
		pnRight.add(boxTimKhachHang);
		pnRight.add(Box.createVerticalStrut(10));

		Box boxMaHoaDon = Box.createHorizontalBox();
		boxMaHoaDon.add(TaoLabelNhanh.tieuDe("Mã hoá đơn:"));
		txtMaHoaDon = TaoJtextNhanh.hienThi("", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
		txtMaHoaDon.setMaximumSize(new Dimension(215, 40));
		txtMaHoaDon.setPreferredSize(new Dimension(215, 40));
		txtMaHoaDon.setFocusable(false);
		boxMaHoaDon.add(txtMaHoaDon);
		pnRight.add(boxMaHoaDon);
		pnRight.add(Box.createVerticalStrut(10));

		Box boxNguoiBan = Box.createHorizontalBox();
		boxNguoiBan.add(TaoLabelNhanh.tieuDe("Người bán:"));
		txtNguoiBan = TaoJtextNhanh.hienThi("", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
		txtNguoiBan.setMaximumSize(new Dimension(215, 40));
		txtNguoiBan.setPreferredSize(new Dimension(215, 40));
		txtNguoiBan.setFocusable(false);
		boxNguoiBan.add(txtNguoiBan);
		pnRight.add(boxNguoiBan);
		pnRight.add(Box.createVerticalStrut(10));

		Box boxTenKhach = Box.createHorizontalBox();
		boxTenKhach.add(TaoLabelNhanh.tieuDe("Tên khách hàng:"));
		txtTenKhachHang = TaoJtextNhanh.hienThi("", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
		txtTenKhachHang.setMaximumSize(new Dimension(215, 40));
		txtTenKhachHang.setPreferredSize(new Dimension(215, 40));
		txtTenKhachHang.setFocusable(false);
		boxTenKhach.add(txtTenKhachHang);
		pnRight.add(boxTenKhach);
		pnRight.add(Box.createVerticalStrut(10));

		Box boxTienTra = Box.createHorizontalBox();
		boxTienTra.add(TaoLabelNhanh.tieuDe("Tiền trả khách:"));
		txtTienTra = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), new Color(0xD32F2F));
		txtTienTra.setMaximumSize(new Dimension(215, 40));
		txtTienTra.setPreferredSize(new Dimension(215, 40));
		txtTienTra.setFocusable(false);
		boxTienTra.add(txtTienTra);
		pnRight.add(boxTienTra);
		pnRight.add(Box.createVerticalStrut(10));

		// ==== Ghi chú KM ====
		txtGhiChuGiamGia = new JTextArea();
		txtGhiChuGiamGia.setOpaque(false);
		txtGhiChuGiamGia.setEditable(false);
		txtGhiChuGiamGia.setFont(new Font("Segoe UI", Font.ITALIC, 13));
		txtGhiChuGiamGia.setForeground(Color.RED);
		txtGhiChuGiamGia.setLineWrap(true);
		txtGhiChuGiamGia.setWrapStyleWord(true);
		txtGhiChuGiamGia.setVisible(false);
		txtGhiChuGiamGia.setMaximumSize(new Dimension(
		        Integer.MAX_VALUE,
		        txtGhiChuGiamGia.getPreferredSize().height
		));
		pnRight.add(txtGhiChuGiamGia);
		pnRight.add(Box.createVerticalStrut(20));

		// ==== Nút ====
		btnTraHang = new PillButton("Trả hàng");
		btnTraHang.setMaximumSize(new Dimension(300, 70));
		btnTraHang.setMaximumSize(new Dimension(115, 40));
		btnTraHang.setPreferredSize(new Dimension(115, 40));

		btnHuy = new PillButton("Huỷ bỏ");
		btnHuy.setMaximumSize(new Dimension(300, 70));
		btnHuy.setMaximumSize(new Dimension(115, 40));
		btnHuy.setPreferredSize(new Dimension(115, 40));

		JPanel pnBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		pnBtn.setOpaque(false);
		pnBtn.add(btnTraHang);
		pnBtn.add(btnHuy);

		pnRight.add(pnBtn);
		pnRight.add(Box.createVerticalStrut(15));

		txtTimKH.addActionListener(this);
		btnTraHang.addActionListener(this);
		btnHuy.addActionListener(this);
		return pnRight;
	}

	private void xyLyTimHD() {
		String maHD = txtTimHoaDon.getText().trim();

		if (!maHD.matches(REGEX_MA_HOA_DON)) {
			JOptionPane.showMessageDialog(this, "❌ Mã hoá đơn không đúng định dạng!\n\n"
					+ "Định dạng hợp lệ: HD-YYYYMMDD-XXXX\n" + "Ví dụ: HD-20250210-0001", "Sai định dạng mã hóa đơn",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (maHD.isEmpty()) {
			resetForm();
			return;
		}
		hienThiChiTietHoaDon(maHD);
	}

	private void xuLyTimHDTheoSDTKH() {
		String sdt = txtTimKH.getText().trim();
		if (sdt.isEmpty() || !sdt.matches("0\\d{9}")) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT hợp lệ (10 số).");
			return;
		}

		KhachHang kh = new KhachHang_DAO().timKhachHangTheoSoDienThoai(sdt);
		if (kh == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng!");
			return;
		}

		List<HoaDon> ds = hoaDonDAO.timHoaDonTheoSoDienThoai(sdt);
		if (ds.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Khách hàng chưa có hóa đơn.");
			return;
		}

		HoaDonPickerDialog dlg = new HoaDonPickerDialog(SwingUtilities.getWindowAncestor(this), sdt);
		dlg.setVisible(true);

		if (dlg.getSelectedMaHD() != null) {
			hienThiChiTietHoaDon(dlg.getSelectedMaHD());
			txtTimHoaDon.setText(dlg.getSelectedMaHD());
		}
	}

	private void capNhatTongTienTra() {
		double tong = 0;
		int colTT = modelTraHang.findColumn("Thành tiền");

		for (int i = 0; i < modelTraHang.getRowCount(); i++) {
			tong += Double.parseDouble(modelTraHang.getValueAt(i, colTT).toString());
		}

		tienTra = tong;
		txtTienTra.setText(String.format("%,.0f đ", tienTra));
	}

	private void capNhatModel(String maLo, int soLuong, double donGia) {
		int colSL = modelTraHang.findColumn("Số lượng");
		int colTT = modelTraHang.findColumn("Thành tiền");

		for (int i = 0; i < modelTraHang.getRowCount(); i++) {
			if (modelTraHang.getValueAt(i, 0).equals(maLo)) {
				modelTraHang.setValueAt(soLuong, i, colSL);
				modelTraHang.setValueAt(soLuong * donGia, i, colTT);
				break;
			}
		}
	}

	private JPanel createPanelDongCTPT(ChiTietHoaDon cthd, boolean allowIncrease) {
		JPanel pnDongCTPT = new JPanel();
		pnDongCTPT.setPreferredSize(new Dimension(1040, 120));
		pnDongCTPT.setLayout(null);
		pnDongCTPT.setBackground(Color.WHITE);
		pnDongCTPT.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

		int centerY = 120 / 2; // để canh giữa theo chiều cao

		// ==== ẢNH SẢN PHẨM ====
		JLabel lblHinhAnh = new JLabel("Ảnh", SwingConstants.CENTER);
		lblHinhAnh.setBorder(new LineBorder(Color.LIGHT_GRAY));
		lblHinhAnh.setBounds(27, centerY - 30, 100, 100);

		String strAnhSP = cthd.getSanPham().getHinhAnh();
		if (strAnhSP != null) {
			URL url = getClass().getResource(strAnhSP);
			if (url != null) {
				ImageIcon icon = new ImageIcon(url);
				Image scaled = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
				lblHinhAnh.setIcon(new ImageIcon(scaled));
				lblHinhAnh.setText("");
			}
		}
		pnDongCTPT.add(lblHinhAnh);

		// ==== TÊN THUỐC ====
		String strTenThuoc = cthd.getLoSanPham().getSanPham().getTenSanPham();
		String hienThiTen = strTenThuoc;

		// Nếu tên thuốc dài hơn 20 ký tự thì rút gọn và thêm "..."
		if (strTenThuoc.length() > 20) {
			hienThiTen = strTenThuoc.substring(0, 20) + "...";
		}

		JLabel lblTenThuoc = new JLabel(hienThiTen);
		lblTenThuoc.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblTenThuoc.setBounds(168, centerY - 30, 320, 34);
		lblTenThuoc.setToolTipText(strTenThuoc); // Tooltip hiển thị tên đầy đủ
		lblTenThuoc.setName("lblTenThuoc");

		pnDongCTPT.add(lblTenThuoc);

		// ==== ĐƠN VỊ TÍNH ====
		String dvt = cthd.getDonViTinh().getTenDonViTinh();
		JLabel lblDonViTinh = new JLabel(dvt);
		lblDonViTinh.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblDonViTinh.setBounds(400, centerY - 28, 120, 30);
		pnDongCTPT.add(lblDonViTinh);

		// ==== LÔ THUỐC ====
		LocalDate hsdLoThuoc = cthd.getLoSanPham().getHanSuDung();
		JLabel lblLoThuoc = new JLabel("Lô: " + hsdLoThuoc.format(fmt));
		lblLoThuoc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblLoThuoc.setForeground(new Color(80, 80, 80));
		lblLoThuoc.setBounds(168, centerY + 12, 320, 25);
		pnDongCTPT.add(lblLoThuoc);

		// ==== PANEL TĂNG GIẢM ====
		JPanel pnTangGiam = new JPanel(new BorderLayout(5, 0));
		pnTangGiam.setBounds(500, centerY, 137, 36);
		pnTangGiam.setBackground(new Color(0xF8FAFB));
		pnTangGiam.setBorder(new LineBorder(new Color(0xB0BEC5), 2, true));
		pnDongCTPT.add(pnTangGiam);

		JButton btnGiam = new JButton("−");
		btnGiam.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnGiam.setFocusPainted(false);
		btnGiam.setBackground(new Color(0xE0F2F1));
		btnGiam.setBorder(new LineBorder(new Color(0x80CBC4), 1, true));
		btnGiam.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnGiam.setOpaque(true);
		btnGiam.setPreferredSize(new Dimension(40, 36));
		pnTangGiam.add(btnGiam, BorderLayout.WEST);

		JTextField txtSoLuong = new JTextField();
		txtSoLuong.setText((int) cthd.getSoLuong() + "");
		txtSoLuong.setHorizontalAlignment(SwingConstants.CENTER);
		txtSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 16));
		txtSoLuong.setBorder(null);
		txtSoLuong.setBackground(Color.WHITE);
		txtSoLuong.setName("txtSoLuong");
		pnTangGiam.add(txtSoLuong, BorderLayout.CENTER);

		JButton btnTang = new JButton("+");
		btnTang.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTang.setFocusPainted(false);
		btnTang.setBackground(new Color(0xE0F2F1));
		btnTang.setBorder(new LineBorder(new Color(0x80CBC4), 1, true));
		btnTang.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnTang.setOpaque(true);
		btnTang.setPreferredSize(new Dimension(40, 36));
		pnTangGiam.add(btnTang, BorderLayout.EAST);

		// ==== ĐƠN GIÁ ====
		double donGia = cthd.getGiaBan();
		JLabel lblDonGia = new JLabel(String.format("%,.0f vnđ", donGia));
		lblDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblDonGia.setBounds(700, centerY, 120, 29);
		pnDongCTPT.add(lblDonGia);

		// ==== GIẢM GIÁ ====
		String strGiamGia = "";
		if (cthd.getKhuyenMai() != null) {
			strGiamGia = cthd.getKhuyenMai().getTenKM();
		}
		JLabel lblGiamGiaSanPham = new JLabel(strGiamGia);
		lblGiamGiaSanPham.setFont(new Font("Segoe UI", Font.ITALIC, 13));
		lblGiamGiaSanPham.setForeground(new Color(220, 0, 0));
		lblGiamGiaSanPham.setBounds(168, centerY + 46, 260, 22);
		if (strGiamGia != null && !strGiamGia.isEmpty()) {
			lblGiamGiaSanPham.setToolTipText(strGiamGia);
		}
		pnDongCTPT.add(lblGiamGiaSanPham);

		// ==== TỔNG TIỀN ====
		tongTien = cthd.getThanhTien();
		JLabel lblTongTien = new JLabel(String.format("%,.0f vnđ", tongTien));
		lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTongTien.setBounds(850, centerY, 120, 29);
		lblTongTien.setName("lblTongTien");
		pnDongCTPT.add(lblTongTien);

		// ⬇️ GẮN "KHOÁ ĐỊNH DANH" CHO PANEL DÒNG
		pnDongCTPT.putClientProperty("maLo", cthd.getLoSanPham().getMaLo());
		pnDongCTPT.putClientProperty("maDVT", cthd.getDonViTinh().getMaDonViTinh());
		pnDongCTPT.putClientProperty("donGia", donGia);

		// ==== NÚT XÓA ====
		JButton btnXoa = new JButton();
		btnXoa.setBounds(980, centerY, 35, 35);
		URL binUrl = getClass().getResource("/images/bin.png");
		if (binUrl != null) {
			ImageIcon iconBin = new ImageIcon(binUrl);
			Image img = iconBin.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			btnXoa.setIcon(new ImageIcon(img));
		}
		btnXoa.setBorderPainted(false);
		btnXoa.setContentAreaFilled(false);
		btnXoa.setFocusPainted(false);
		btnXoa.setOpaque(false);
		btnXoa.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pnDongCTPT.add(btnXoa);

		// ==== LÝ DO TRẢ HÀNG ====
		JTextField txtLyDo = new JTextField("Nhập lý do trả hàng");
		txtLyDo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtLyDo.setForeground(Color.DARK_GRAY);
		txtLyDo.setBounds(700, 100, 220, 30);
		pnDongCTPT.add(txtLyDo);

		// 🔹 Khi người dùng nhập trực tiếp (ô nhỏ)
		txtLyDo.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (txtLyDo.getText().equals("Nhập lý do trả hàng")) {
					txtLyDo.setText("");
					txtLyDo.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				String lyDo = txtLyDo.getText().trim();
				String maLo = (String) pnDongCTPT.getClientProperty("maLo");
				for (int i = 0; i < modelTraHang.getRowCount(); i++) {
					if (modelTraHang.getValueAt(i, 0).equals(maLo)) {
						modelTraHang.setValueAt(lyDo, i, 5);
						break;
					}
				}
				if (txtLyDo.getText().isEmpty()) {
					txtLyDo.setText("Nhập lý do trả hàng");
					txtLyDo.setForeground(Color.GRAY);
				}
			}
		});

		// ================= CƠ CHẾ NÚT TĂNG / GIẢM =================
		int soLuongBanDau = (int) cthd.getSoLuong();

		// Khi load hóa đơn, disable nút tăng (chưa được phép tăng lại)
		btnTang.setEnabled(false);
		btnTang.setBackground(new Color(0xE0E0E0));
		btnTang.setCursor(Cursor.getDefaultCursor());

		// Nếu số lượng = 1 thì disable nút giảm
		if (soLuongBanDau <= 1) {
			btnGiam.setEnabled(false);
			btnGiam.setBackground(new Color(0xE0E0E0));
			btnGiam.setCursor(Cursor.getDefaultCursor());
		}

		// Nút giảm
		btnGiam.addActionListener(e -> {
			int sl = Integer.parseInt(txtSoLuong.getText());
			if (sl > 1) {
				sl--;
				txtSoLuong.setText(String.valueOf(sl));
				lblTongTien.setText(String.format("%,.0f đ", sl * donGia));

				// --- Đồng bộ lại model (cập nhật cột Số lượng) ---
				String maLo = (String) pnDongCTPT.getClientProperty("maLo");
				capNhatModel(maLo, sl, donGia);

				// Cho phép tăng trở lại
				btnTang.setEnabled(true);
				btnTang.setBackground(new Color(0xE0F2F1));
				btnTang.setCursor(new Cursor(Cursor.HAND_CURSOR));

				// Nếu sau khi giảm = 1 thì disable nút giảm
				if (sl == 1) {
					btnGiam.setEnabled(false);
					btnGiam.setBackground(new Color(0xE0E0E0));
					btnGiam.setCursor(Cursor.getDefaultCursor());
				}
			}
			capNhatTongTienTra();
		});

		// Nút tăng
		btnTang.addActionListener(e -> {
			int sl = Integer.parseInt(txtSoLuong.getText());
			if (sl < soLuongBanDau) {
				sl++;
				txtSoLuong.setText(String.valueOf(sl));
				lblTongTien.setText(String.format("%,.0f đ", sl * donGia));

				// --- Đồng bộ lại model (cập nhật cột Số lượng) ---
				String maLo = (String) pnDongCTPT.getClientProperty("maLo");
				capNhatModel(maLo, sl, donGia);

				// Khi tăng lại > 1 thì bật lại nút giảm
				btnGiam.setEnabled(true);
				btnGiam.setBackground(new Color(0xE0F2F1));
				btnGiam.setCursor(new Cursor(Cursor.HAND_CURSOR));

				// Nếu đạt tới giới hạn thì disable nút tăng
				if (sl == soLuongBanDau) {
					btnTang.setEnabled(false);
					btnTang.setBackground(new Color(0xE0E0E0));
					btnTang.setCursor(Cursor.getDefaultCursor());
				}
			}
			capNhatTongTienTra();
		});

		btnXoa.addActionListener(e -> {
			pnDanhSachDon.remove(pnDongCTPT);
			pnDanhSachDon.revalidate();
			pnDanhSachDon.repaint();

			// Xoá luôn dòng trong bảng dựa theo mã lô
			String maLo = (String) pnDongCTPT.getClientProperty("maLo");
			for (int i = 0; i < modelTraHang.getRowCount(); i++) {
				if (modelTraHang.getValueAt(i, 0).equals(maLo)) {
					modelTraHang.removeRow(i);
					break;
				}
			}

			capNhatTongTienTra();
		});

		// ==== NHẬP SỐ LƯỢNG THỦ CÔNG ====
		txtSoLuong.addActionListener(e -> {
			try {
				int slMoi = Integer.parseInt(txtSoLuong.getText().trim());

				// Nếu <=0 thì xóa dòng
				if (slMoi <= 0) {
					pnDanhSachDon.remove(pnDongCTPT);
					pnDanhSachDon.revalidate();
					pnDanhSachDon.repaint();

					// Xóa trong bảng
					String maLo = (String) pnDongCTPT.getClientProperty("maLo");
					for (int i = 0; i < modelTraHang.getRowCount(); i++) {
						if (modelTraHang.getValueAt(i, 0).equals(maLo)) {
							modelTraHang.removeRow(i);
							break;
						}
					}
					capNhatTongTienTra();
					return;
				}

				// Giới hạn số lượng không vượt quá số lượng mua ban đầu
				if (slMoi > soLuongBanDau) {
					slMoi = soLuongBanDau;
					txtSoLuong.setText(String.valueOf(soLuongBanDau));
				}

				// Cập nhật lại tổng tiền dòng này
				double thanhTienMoi = slMoi * donGia;
				lblTongTien.setText(String.format("%,.0f đ", thanhTienMoi));

				// === Đồng bộ bảng modelTraHang ===
				String maLo = (String) pnDongCTPT.getClientProperty("maLo");
				capNhatModel(maLo, slMoi, donGia);

				// === Cập nhật nút tăng / giảm ===
				btnTang.setEnabled(slMoi < soLuongBanDau);
				btnTang.setBackground(slMoi < soLuongBanDau ? new Color(0xE0F2F1) : new Color(0xE0E0E0));
				btnTang.setCursor(slMoi < soLuongBanDau ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

				btnGiam.setEnabled(slMoi > 1);
				btnGiam.setBackground(slMoi > 1 ? new Color(0xE0F2F1) : new Color(0xE0E0E0));
				btnGiam.setCursor(slMoi > 1 ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ. Vui lòng nhập số!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}

			capNhatTongTienTra();
		});

		pnDongCTPT.setMaximumSize(new Dimension(1060, 150));
		pnDongCTPT.setMinimumSize(new Dimension(1040, 120));

		return pnDongCTPT;
	}

	private void capNhatGhiChuKhuyenMai(List<ChiTietHoaDon> dsChon) {
		if (txtGhiChuGiamGia == null)
			return;

		StringBuilder sb = new StringBuilder();
		Map<String, String> dsKM = new HashMap<>();

		for (ChiTietHoaDon ct : dsChon) {
			if (ct.getKhuyenMai() != null) {
				String tenKM = ct.getKhuyenMai().getTenKM();

				// tránh trùng khuyến mãi theo mã
				dsKM.put(ct.getKhuyenMai().getMaKM(), "• " + tenKM);
			}
		}

		for (String km : dsKM.values()) {
			sb.append(km).append("\n");
		}

		if (sb.length() == 0) {
		    txtGhiChuGiamGia.setText("");
		    txtGhiChuGiamGia.setVisible(false);
		} else {
		    txtGhiChuGiamGia.setText(sb.toString());
		    txtGhiChuGiamGia.setVisible(true);
		}

	}

	private void hienThiChiTietHoaDon(String maHD) {
		HoaDon hd = hoaDonDAO.timHoaDonTheoMa(maHD);
		if (hd == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
			return;
		}

		long days = ChronoUnit.DAYS.between(hd.getNgayLap(), LocalDate.now());
		if (days > MAX_RETURN_DAYS) {
			JOptionPane.showMessageDialog(this, "Hoá đơn đã quá " + MAX_RETURN_DAYS + " ngày - không thể trả!");
			return;
		}

		List<ChiTietHoaDon> dsCT = cthdDAO.layDanhSachChiTietTheoMaHD(maHD);

		ChonSanPhamTraDialog dlg = new ChonSanPhamTraDialog(dsCT);
		dlg.setVisible(true);
		List<ChiTietHoaDon> dsChon = dlg.getDsSanPhamDuocChon();

		if (dsChon.isEmpty()) {
			resetForm();
			return;
		}

		pnDanhSachDon.removeAll();
		for (ChiTietHoaDon ct : dsChon) {
			pnDanhSachDon.add(createPanelDongCTPT(ct, true));
		}
		pnDanhSachDon.revalidate();
		pnDanhSachDon.repaint();

		modelTraHang.setRowCount(0);

		for (ChiTietHoaDon ct : dsChon) {
			String maLo = ct.getLoSanPham().getMaLo();
			String tenSP = ct.getLoSanPham().getSanPham().getTenSanPham();
			int sl = (int) ct.getSoLuong();
			double donGia = ct.getGiaBan();
			String maDVT = ct.getDonViTinh().getMaDonViTinh();

			modelTraHang.addRow(new Object[] { 
			    maLo, tenSP, sl, donGia, sl * donGia, "", maDVT
			});
		}

		txtMaHoaDon.setText(maHD);
		txtNguoiBan.setText(hd.getNhanVien().getTenNhanVien());
		txtTenKhachHang.setText(hd.getKhachHang().getTenKhachHang());

		capNhatTongTienTra();
		capNhatGhiChuKhuyenMai(dsChon);
	}

	private JTextField timTxtSoLuong(JPanel panel) {
		for (Component comp : panel.getComponents()) {
			if (comp instanceof JPanel childPanel) {
				JTextField rs = timTxtSoLuong(childPanel);
				if (rs != null)
					return rs;
			}
			if (comp instanceof JTextField txt) {
				if ("txtSoLuong".equals(txt.getName())) {
					return txt;
				}
			}
		}
		return null;
	}

	private String timTenSanPham(JPanel panel) {
		for (Component comp : panel.getComponents()) {
			if (comp instanceof JPanel childPanel) {
				String rs = timTenSanPham(childPanel);
				if (rs != null)
					return rs;
			}
			if (comp instanceof JLabel lbl) {
				if ("lblTenThuoc".equals(lbl.getName())) {
					return lbl.getText();
				}
			}
		}
		return null;
	}

	private void xuLyTraHang(ActionEvent e) {
		String maHD = txtMaHoaDon.getText().trim();
		if (maHD.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Chưa chọn hoá đơn!");
			txtMaHoaDon.requestFocus();
			return;
		}

		HoaDon hd = hoaDonDAO.timHoaDonTheoMa(maHD);

		if (!maHD.matches(REGEX_MA_HOA_DON)) {
			JOptionPane.showMessageDialog(this, "❌ Mã hoá đơn không đúng định dạng!\n\n"
					+ "Định dạng hợp lệ: HD-YYYYMMDD-XXXX\n" + "Ví dụ: HD-20250210-0001", "Sai định dạng mã hóa đơn",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (pnDanhSachDon.getComponentCount() == 0) {
			JOptionPane.showMessageDialog(this, "Không có sản phẩm nào được chọn!");
			return;
		}

		modelTraHang.setRowCount(0);

		for (Component comp : pnDanhSachDon.getComponents()) {
			if (!(comp instanceof JPanel))
				continue;

			JPanel p = (JPanel) comp;
			String maLo = (String) p.getClientProperty("maLo");
			JTextField txtSL = timTxtSoLuong(p);
			int sl = Integer.parseInt(txtSL.getText());

			double donGia = (double) p.getClientProperty("donGia");
			String tenSP = timTenSanPham(p);

			String maDVT = (String) p.getClientProperty("maDVT");

			modelTraHang.addRow(new Object[] { 
			    maLo, tenSP, sl, donGia, sl * donGia, "", maDVT
			});
		}

		// VALIDATOR: kiểm tra trả trùng, kiểm tra vượt quá số lượng mua
		Map<String, ChiTietHoaDon> map = new HashMap<>();
		for (int i = 0; i < modelTraHang.getRowCount(); i++) {
			String maLo = modelTraHang.getValueAt(i, 0).toString();
			String maDVT = modelTraHang.getValueAt(i, 6).toString();
			
			LoSanPham lo = loDAO.timLoTheoMa(maLo);
			int sl = Integer.parseInt(modelTraHang.getValueAt(i, 2).toString());

			ChiTietHoaDon cthd = cthdDAO.timKiemChiTietHoaDonBangMa(maHD, maLo, maDVT);
			
			double daTra = ChiTietPhieuTra_DAO.tongSoLuongDaTra(maHD, maLo);
			double conLai = cthd.getSoLuong() - daTra;

			if (sl > conLai) {
				JOptionPane.showMessageDialog(this, "Lô " + maLo + " - " + lo.getHanSuDung() + " chỉ còn được trả tối đa: " + conLai);
				return;
			}
			String key = maLo + "_" + maDVT;
			map.put(key, cthd);
		}

		// TẠO PHIẾU TRẢ
		TaiKhoan tk = Session.getInstance().getTaiKhoanDangNhap();

		PhieuTra pt = new PhieuTra();
		pt.setMaPhieuTra(ptDAO.taoMaPhieuTra());
		pt.setKhachHang(hd.getKhachHang());
		pt.setNhanVien(tk.getNhanVien());
		pt.setNgayLap(LocalDate.now());
		pt.setDaDuyet(false);

		List<ChiTietPhieuTra> dsCT = new ArrayList<>();

		for (int i = 0; i < modelTraHang.getRowCount(); i++) {
			String maLo = modelTraHang.getValueAt(i, 0).toString();
			String maDVT = modelTraHang.getValueAt(i, 6).toString(); 
			
			int sl = Integer.parseInt(modelTraHang.getValueAt(i, 2).toString());

			ChiTietPhieuTra ct = new ChiTietPhieuTra();
			ct.setPhieuTra(pt);
			String key = maLo + "_" + maDVT;
			ChiTietHoaDon cthd = map.get(key);
			ct.setChiTietHoaDon(cthd);
			ct.setSoLuong(sl);

			String lyDo = modelTraHang.getValueAt(i, 5).toString().trim();
			ct.setLyDoChiTiet(lyDo.isBlank() ? null : lyDo);

			ct.setTrangThai(0);
			ct.capNhatThanhTienHoan();
			dsCT.add(ct);
		}

		pt.setChiTietPhieuTraList(dsCT);
		pt.capNhatTongTienHoan();

		if (!ptDAO.themPhieuTraVaChiTiet(pt, dsCT)) {
			JOptionPane.showMessageDialog(this, "Lưu phiếu trả thất bại!");
			return;
		}

		new PhieuTraPreviewDialog(SwingUtilities.getWindowAncestor(this), pt, dsCT).setVisible(true);
		resetForm();
	}

	private void resetForm() {
		pnDanhSachDon.removeAll();
		pnDanhSachDon.revalidate();
		pnDanhSachDon.repaint();

		modelTraHang.setRowCount(0);

		txtMaHoaDon.setText("");
		txtNguoiBan.setText("");
		txtTenKhachHang.setText("");
		txtTienTra.setText("0 đ");

		txtTimHoaDon.setText(PLACEHOLDER_TIM_HOA_DON);
		txtTimKH.setText(PLACEHOLDER_TIM_KH);
		
		txtGhiChuGiamGia.setText("");
		
		txtTimHoaDon.requestFocus();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		// --- TÌM HOÁ ĐƠN THEO MÃ ---
		if (o == txtTimHoaDon) {
			xyLyTimHD();
			return;
		}

		// --- TÌM HOÁ ĐƠN THEO SĐT ---
		if (o == txtTimKH) {
			xuLyTimHDTheoSDTKH();
			return;
		}

		// --- TRẢ HÀNG ---
		if (o == btnTraHang) {
			xuLyTraHang(e);
			return;
		}

		// --- HUỶ ---
		if (o == btnHuy) {
			resetForm();
			return;
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Trả hàng - Data Fake");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new TraHangNhanVien_GUI());
			frame.setVisible(true);
		});
	}
}
