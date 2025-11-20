package gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
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
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import customcomponent.TaoJtextNhanh;
import dao.ChiTietHoaDon_DAO;
import dao.ChiTietPhieuTra_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.LoSanPham_DAO;
import dao.PhieuTra_DAO;
import dao.SanPham_DAO;
import entity.Session;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuTra;
import entity.HoaDon;
import entity.KhachHang;
import entity.LoSanPham;
import entity.PhieuTra;
import entity.TaiKhoan;
import entity.DonViTinh;
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
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TraHangNhanVien_GUI extends JPanel {

	private static final int MAX_RETURN_DAYS = 7;

	private JTextField txtTimHoaDon;
	private JTextField txtTimKH;

	private JPanel pnCotPhaiCenter;
	private JPanel pnDanhSachDon;
	private JLabel lblTienTra;
	private JLabel lblTenKhachHang;
	private JLabel lblNguoiBan;
	private JLabel lblMaHoaDon;
	private JLabel lblThoiGian;

	private double tongTien;
	private double tienTra = 0;

	private LocalDate today = LocalDate.now();
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
	private final ChiTietHoaDon_DAO cthdDAO = new ChiTietHoaDon_DAO();
	private final PhieuTra_DAO ptDAO = new PhieuTra_DAO();
	private final LoSanPham_DAO loDAO = new LoSanPham_DAO();
	private DefaultTableModel modelTraHang;
	private JTable tblTraHang;

	private JTextArea txtGhiChuGiamGia;

	public TraHangNhanVien_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		// ===== HEADER =====
		JPanel pnCotPhaiHead = new JPanel(null);
		pnCotPhaiHead.setPreferredSize(new Dimension(1073, 88));
		pnCotPhaiHead.setBackground(new Color(0xE3F2F5));
		add(pnCotPhaiHead, BorderLayout.NORTH);

		// Ô tìm kiếm
		txtTimHoaDon = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimHoaDon, "Tìm hoá đơn theo mã");
		txtTimHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtTimHoaDon.setBounds(25, 17, 420, 60);
		txtTimHoaDon.setBorder(new RoundedBorder(20));
		txtTimHoaDon.setBackground(Color.WHITE);
		txtTimHoaDon.setForeground(Color.GRAY);

		pnCotPhaiHead.add(txtTimHoaDon);

		// ===== CENTER (DANH SÁCH SẢN PHẨM) =====
		pnCotPhaiCenter = new JPanel();
		pnCotPhaiCenter.setPreferredSize(new Dimension(1073, 992));
		pnCotPhaiCenter.setBackground(Color.WHITE);
		pnCotPhaiCenter.setBorder(
				new CompoundBorder(new LineBorder(new Color(0x00C853), 3, true), new EmptyBorder(5, 5, 5, 5)));
		pnCotPhaiCenter.setLayout(new BorderLayout(0, 0));
		add(pnCotPhaiCenter, BorderLayout.CENTER);

		// Panel chứa danh sách đơn hàng
		pnDanhSachDon = new JPanel();
		pnDanhSachDon.setLayout(new BoxLayout(pnDanhSachDon, BoxLayout.Y_AXIS));
		pnDanhSachDon.setBackground(Color.WHITE);

		JScrollPane scrPnDanhSachDon = new JScrollPane(pnDanhSachDon);
		scrPnDanhSachDon.setBorder(null);
		scrPnDanhSachDon.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrPnDanhSachDon.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrPnDanhSachDon.getVerticalScrollBar().setOpaque(false);
		pnCotPhaiCenter.add(scrPnDanhSachDon);

		// ====== CỘT PHẢI ======
		JPanel pnCotPhaiRight = new JPanel();
		pnCotPhaiRight.setPreferredSize(new Dimension(1920 - 383 - 1073, 1080));
		pnCotPhaiRight.setBackground(Color.WHITE);
		pnCotPhaiRight.setBorder(new EmptyBorder(20, 20, 20, 20));
		pnCotPhaiRight.setLayout(new BoxLayout(pnCotPhaiRight, BoxLayout.Y_AXIS));
		add(pnCotPhaiRight, BorderLayout.EAST);

		// ==== Thông tin nhân viên & thời gian ====
		JPanel pnNhanVien = new JPanel(new BorderLayout(5, 5));
		pnNhanVien.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		pnNhanVien.setOpaque(false);

		JLabel lblNhanVien = new JLabel();
		lblNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 14));

		TaiKhoan tk = Session.getInstance().getTaiKhoanDangNhap();
		if (tk != null && tk.getNhanVien() != null) {
			lblNhanVien.setText(tk.getNhanVien().getTenNhanVien());
		} else {
			lblNhanVien.setText("Không xác định");
		}

		lblThoiGian = new JLabel(fmt.format(today), SwingConstants.RIGHT);
		lblThoiGian.setFont(new Font("Segoe UI", Font.PLAIN, 14));

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

		// Ô tìm khách hàng
		txtTimKH = TaoJtextNhanh.timKiem();
		txtTimKH = TaoJtextNhanh.timKiem();
		txtTimKH.setPreferredSize(new Dimension(0, 60));
		txtTimKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		txtTimKH.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		PlaceholderSupport.addPlaceholder(txtTimKH, "Tìm hoá đơn theo số điện thoại khách hàng");
		
		pnCotPhaiRight.add(txtTimKH);
		pnCotPhaiRight.add(Box.createVerticalStrut(15));

		lblMaHoaDon = new JLabel("");
		lblNguoiBan = new JLabel("");
		lblTenKhachHang = new JLabel("");
		lblTienTra = new JLabel("0 đ");

		// Thêm các dòng thông tin
		pnCotPhaiRight.add(makeLabel("Mã hoá đơn:", lblMaHoaDon));
		pnCotPhaiRight.add(makeLabel("Người bán:", lblNguoiBan));
		pnCotPhaiRight.add(makeLabel("Tên khách hàng:", lblTenKhachHang));
		pnCotPhaiRight.add(makeLabel("Tiền trả khách:", lblTienTra));

		JPanel pnMGG = new JPanel((LayoutManager) null);
		pnMGG.setOpaque(false);
		pnMGG.setMaximumSize(new Dimension(2147483647, 85));
		pnCotPhaiRight.add(pnMGG);
		pnMGG.setLayout(new BorderLayout(5, 5));

		txtGhiChuGiamGia = new JTextArea();
		txtGhiChuGiamGia.setFont(new Font("Segoe UI", Font.BOLD, 13));
		txtGhiChuGiamGia.setForeground(Color.RED);
		txtGhiChuGiamGia.setOpaque(false);
		txtGhiChuGiamGia.setEditable(false);
		txtGhiChuGiamGia.setFocusable(false);
		txtGhiChuGiamGia.setLineWrap(true);
		txtGhiChuGiamGia.setWrapStyleWord(true);
		txtGhiChuGiamGia.setAlignmentX(Component.LEFT_ALIGNMENT);
		txtGhiChuGiamGia.setBorder(null);
		txtGhiChuGiamGia.setMargin(new Insets(0, 0, 0, 0));
		txtGhiChuGiamGia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

		pnMGG.add(txtGhiChuGiamGia, BorderLayout.CENTER);
		pnCotPhaiRight.add(Box.createVerticalStrut(30));

		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		actionPanel.setBackground(null);
		PillButton btnTraHang = new PillButton("Trả hàng");
		btnTraHang.setFont(new Font("Segoe UI", Font.BOLD, 20));
		btnTraHang.setAlignmentX(Component.CENTER_ALIGNMENT);
		actionPanel.add(btnTraHang);

		JButton btnHuyBo = new JButton("Huỷ bỏ");

		actionPanel.add(btnHuyBo);

		pnCotPhaiRight.add(actionPanel);

		String[] colTraHang = { "Mã lô", "Tên sản phẩm", "Số lượng", "Giá bán", "Thành tiền", "Lý do" };
		modelTraHang = new DefaultTableModel(colTraHang, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// cho phép chỉnh lý do nếu muốn
				return column == 5;
			}
		};
		tblTraHang = new JTable(modelTraHang);
		JScrollPane scrollTraHang = new JScrollPane(tblTraHang);
		scrollTraHang.setPreferredSize(new Dimension(400, 150));

		txtTimHoaDon.addActionListener(e -> xyLyTimHD());
		txtTimKH.addActionListener(e -> xuLyTimHDTheoSDTKH());
		btnHuyBo.addActionListener(e -> resetForm());
		btnTraHang.addActionListener(this::xuLyTraHang);
	}

	private void hienThiChiTietHoaDon(String maHD) {
		HoaDon hoaDon = hoaDonDAO.timHoaDonTheoMa(maHD);
		if (hoaDon == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
			return;
		}

		if (hoaDon.getNgayLap() != null) {
			long daysSincePurchase = ChronoUnit.DAYS.between(hoaDon.getNgayLap(), LocalDate.now());
			if (daysSincePurchase > MAX_RETURN_DAYS) {
				JOptionPane.showMessageDialog(this,
						String.format("⚠️ Hoá đơn đã quá %d ngày kể từ ngày mua (%s). Không thể thực hiện trả hàng.",
								MAX_RETURN_DAYS, hoaDon.getNgayLap().format(fmt)),
						"Hết hạn trả hàng", JOptionPane.WARNING_MESSAGE);
			} else {
				List<ChiTietHoaDon> dsCT = cthdDAO.layDanhSachChiTietTheoMaHD(maHD);
				pnDanhSachDon.removeAll();

				for (ChiTietHoaDon ct : dsCT) {
					pnDanhSachDon.add(createPanelDongCTPT(ct, false)); // false = không cho tăng
				}

				pnDanhSachDon.revalidate();
				pnDanhSachDon.repaint();

				// === Cập nhật các label ===
				lblMaHoaDon.setText(hoaDon.getMaHoaDon());
				lblTenKhachHang.setText(hoaDon.getKhachHang().getTenKhachHang());
				lblNguoiBan.setText(hoaDon.getNhanVien().getTenNhanVien());
				capNhatTongTienTra();

				// === Hiển thị khuyến mãi hoá đơn nếu có ===
				if (hoaDon.getKhuyenMai() != null && hoaDon.getKhuyenMai().isKhuyenMaiHoaDon()) {
					String tenKM = hoaDon.getKhuyenMai().getTenKM();
					double giaTri = hoaDon.getKhuyenMai().getGiaTri();
					String hinhThuc = hoaDon.getKhuyenMai().getHinhThuc().toString();
					String moTaKM = "";

					if ("GIAM_GIA_PHAN_TRAM".equalsIgnoreCase(hinhThuc)) {
						moTaKM = String.format("Áp dụng khuyến mãi hóa đơn: %s - Giảm %.0f%% tổng hóa đơn", tenKM,
								giaTri);
					} else if ("GIAM_GIA_TIEN_MAT".equalsIgnoreCase(hinhThuc)) {
						moTaKM = String.format("Áp dụng khuyến mãi hóa đơn: %s - Giảm %, .0fđ", tenKM, giaTri);
					} else {
						moTaKM = String.format("Khuyến mãi hóa đơn: %s", tenKM);
					}

					txtGhiChuGiamGia.setText(moTaKM);
				} else {
					txtGhiChuGiamGia.setText("");
				}

			}
		}

	}

	private void xyLyTimHD() {
		String maHD = txtTimHoaDon.getText().trim();
		if (maHD.isEmpty()) {
			resetForm();
			return;
		}
		hienThiChiTietHoaDon(maHD);
	}

	private void xuLyTimHDTheoSDTKH() {
		String sdt = txtTimKH.getText().trim();
		if (sdt.isEmpty() || !sdt.matches("0\\d{9}")) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT 10 số (bắt đầu bằng 0).");
			return;
		}

		KhachHang_DAO khDAO = new KhachHang_DAO();
		KhachHang kh = khDAO.timKhachHangTheoSoDienThoai(sdt);
		if (kh == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng nào có số điện thoại: " + sdt,
					"Kết quả tìm kiếm", JOptionPane.WARNING_MESSAGE);
			return;
		}

		HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
		List<HoaDon> dsHD = hoaDonDAO.timHoaDonTheoSoDienThoai(sdt);
		if (dsHD == null || dsHD.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Khách hàng '" + kh.getTenKhachHang() + "' chưa có hoá đơn nào.",
					"Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// ✅ Nếu có hóa đơn, mở dialog
		HoaDonPickerDialog dlg = new HoaDonPickerDialog(SwingUtilities.getWindowAncestor(this), sdt);
		dlg.setVisible(true);

		String maHD = dlg.getSelectedMaHD();
		if (maHD != null) {
			hienThiChiTietHoaDon(maHD);
			txtTimHoaDon.setText(maHD);
		}
	}

	private void resetForm() {
		pnDanhSachDon.removeAll();
		pnDanhSachDon.revalidate();
		pnDanhSachDon.repaint();

		if (modelTraHang != null) {
			modelTraHang.setRowCount(0);
		}

		lblMaHoaDon.setText("");
		lblNguoiBan.setText("");
		lblTenKhachHang.setText("");
		lblTienTra.setText("0 đ");

		if (lblThoiGian != null) {
			lblThoiGian.setText(LocalDate.now().format(fmt));
		}

		txtTimHoaDon.setText("");
		txtTimHoaDon.setForeground(Color.GRAY);

		if (txtTimKH != null) {
			txtTimKH.setText("");
			txtTimKH.setForeground(Color.GRAY);
		}

		tienTra = 0;
		txtTimHoaDon.requestFocus();
	}

	private String formatSo(double x) {
		if (x == (long) x)
			return String.format("%d", (long) x);
		else
			return String.format("%.2f", x);
	}

	private void xuLyTraHang(ActionEvent e) {
		String maHD = txtTimHoaDon.getText().trim();

		if (maHD.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập hoặc chọn mã hoá đơn cần trả hàng!");
			return;
		}

		HoaDon hd = hoaDonDAO.timHoaDonTheoMa(maHD);
		if (hd == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy hoá đơn: " + maHD);
			return;
		}

		if (hd.getNgayLap() != null) {
			long daysSincePurchase = ChronoUnit.DAYS.between(hd.getNgayLap(), LocalDate.now());
			if (daysSincePurchase > MAX_RETURN_DAYS) {
				JOptionPane.showMessageDialog(this,
						String.format("Hoá đơn đã quá %d ngày, không thể thực hiện trả hàng.", MAX_RETURN_DAYS));
				return;
			}
		}

		if (modelTraHang.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Chưa có sản phẩm nào để trả hàng!");
			return;
		}

		Map<String, ChiTietHoaDon> chiTietTheoLo = new HashMap<>();
		for (int i = 0; i < modelTraHang.getRowCount(); i++) {
			Object maLoObj = modelTraHang.getValueAt(i, 0);
			String maLo = maLoObj != null ? maLoObj.toString().trim() : "";
			if (maLo.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Không xác định được lô ở dòng " + (i + 1) + ".");
				return;
			}

			LoSanPham lo = loDAO.timLoTheoMa(maLo);

			int soLuong;
			try {
				soLuong = Integer.parseInt(modelTraHang.getValueAt(i, 2).toString().trim());
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ ở dòng " + (i + 1) + ".");
				return;
			}

			if (soLuong <= 0) {
				JOptionPane.showMessageDialog(this, "Có sản phẩm có số lượng trả <= 0 ở dòng " + (i + 1) + ".");
				return;
			}

			ChiTietHoaDon cthd = cthdDAO.timKiemChiTietHoaDonBangMa(maHD, maLo);
			if (cthd == null) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy chi tiết hoá đơn cho lô " + lo.getHanSuDung());
				return;
			}

			if (soLuong > cthd.getSoLuong()) {
				JOptionPane.showMessageDialog(this,
						String.format("Số lượng trả (%d) vượt quá số lượng đã mua (%d) ở dòng %d.", soLuong,
								(int) cthd.getSoLuong(), i + 1));
				return;
			}

			// ===== Kiểm tra đã trả trùng chưa =====
			double daTra = ChiTietPhieuTra_DAO.tongSoLuongDaTra(maHD, maLo);
			double soLuongDaMua = cthd.getSoLuong();
			if (daTra > 0.0001) { // có thể sai số nhỏ do kiểu double
				double conLai = soLuongDaMua - daTra;
				if (conLai <= 0.0001) {
					JOptionPane.showMessageDialog(this,
							String.format("⚠️  Lô %s của hóa đơn này đã được trả đủ (%.0f/%s). Không thể trả thêm.",
									lo.getHanSuDung().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), daTra,
									formatSo(soLuongDaMua)),
							"Đã trả đủ", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (soLuong > conLai + 0.0001) {
					JOptionPane.showMessageDialog(this,
							String.format("Lô %s đã được trả %.2f, chỉ còn %.2f có thể trả.", maLo, daTra, conLai),
							"Số lượng không hợp lệ", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}

			chiTietTheoLo.put(maLo, cthd);
		}

		TaiKhoan taiKhoanDangNhap = Session.getInstance().getTaiKhoanDangNhap();
		if (taiKhoanDangNhap == null || taiKhoanDangNhap.getNhanVien() == null) {
			JOptionPane.showMessageDialog(this, "Không xác định được nhân viên lập phiếu trả!");
			return;
		}

		if (hd.getKhachHang() == null) {
			JOptionPane.showMessageDialog(this, "Hoá đơn chưa xác định khách hàng, không thể tạo phiếu trả.");
			return;
		}

		PhieuTra pt = new PhieuTra();
		pt.setMaPhieuTra(ptDAO.taoMaPhieuTra());
		pt.setKhachHang(hd.getKhachHang());
		pt.setNhanVien(taiKhoanDangNhap.getNhanVien());
		pt.setNgayLap(LocalDate.now());
		pt.setDaDuyet(false);

		List<ChiTietPhieuTra> dsCT = new ArrayList<>();
		for (int i = 0; i < modelTraHang.getRowCount(); i++) {
			String maLo = modelTraHang.getValueAt(i, 0).toString().trim();
			int soLuong = Integer.parseInt(modelTraHang.getValueAt(i, 2).toString().trim());
			ChiTietHoaDon cthd = chiTietTheoLo.get(maLo);

			ChiTietPhieuTra ct = new ChiTietPhieuTra();
			ct.setPhieuTra(pt);
			ct.setChiTietHoaDon(cthd);
			try {
				ct.setSoLuong(soLuong);
				Object lyDoObj = modelTraHang.getValueAt(i, 5);
				String lyDo = lyDoObj != null ? lyDoObj.toString().trim() : null;
				if (lyDo != null && lyDo.isEmpty()) {
					lyDo = null;
				}
				ct.setLyDoChiTiet(lyDo);
			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			ct.capNhatThanhTienHoan();
			ct.setTrangThai(0);
			dsCT.add(ct);
		}

		pt.setChiTietPhieuTraList(dsCT);
		pt.capNhatTongTienHoan();

		boolean ok = ptDAO.themPhieuTraVaChiTiet(pt, dsCT);
		if (!ok) {
			JOptionPane.showMessageDialog(this, "❌ Lưu phiếu trả hàng thất bại. Vui lòng thử lại!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// ⭐ Hiển thị phiếu trả cho nhân viên xem
		new PhieuTraPreviewDialog(SwingUtilities.getWindowAncestor(this), pt, dsCT).setVisible(true);

		resetForm();

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
				for (int i = 0; i < modelTraHang.getRowCount(); i++) {
					if (modelTraHang.getValueAt(i, 0).equals(maLo)) {
						modelTraHang.setValueAt(sl, i, 4); // cột 4: Số lượng
						break;
					}
				}
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
				lblTongTien.setText(String.format("%,.0fđ", sl * donGia));

				// --- Đồng bộ lại model (cập nhật cột Số lượng) ---
				String maLo = (String) pnDongCTPT.getClientProperty("maLo");
				for (int i = 0; i < modelTraHang.getRowCount(); i++) {
					if (modelTraHang.getValueAt(i, 0).equals(maLo)) {
						modelTraHang.setValueAt(sl, i, 4); // cột 4: Số lượng
						break;
					}
				}
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
			capNhatTongTienTra();

			// Xoá luôn dòng trong bảng dựa theo mã lô
			String maLo = (String) pnDongCTPT.getClientProperty("maLo");
			for (int i = 0; i < modelTraHang.getRowCount(); i++) {
				if (modelTraHang.getValueAt(i, 0).equals(maLo)) {
					modelTraHang.removeRow(i);
					break;
				}
			}
		});

		// ==== NHẬP SỐ LƯỢNG THỦ CÔNG ====
		txtSoLuong.addActionListener(e -> {
			try {
				int slMoi = Integer.parseInt(txtSoLuong.getText().trim());
				if (slMoi <= 0) {
					// Nếu nhập 0 hoặc nhỏ hơn thì xoá dòng
					pnDanhSachDon.remove(pnDongCTPT);
					pnDanhSachDon.revalidate();
					pnDanhSachDon.repaint();
					capNhatTongTienTra();
					return;
				}

				if (slMoi > soLuongBanDau) {
					slMoi = soLuongBanDau;
					txtSoLuong.setText(String.valueOf(soLuongBanDau));
				}

				// Cập nhật lại thành tiền cho sản phẩm này
				double thanhTienMoi = slMoi * donGia;

				// Cập nhật tổng tiền trả (trừ tiền cũ, cộng tiền mới)
				lblTongTien.setText(String.format("%,.0fđ", thanhTienMoi));

				// Cập nhật trạng thái nút tăng / giảm
				btnTang.setEnabled(slMoi < soLuongBanDau);
				btnTang.setBackground(slMoi < soLuongBanDau ? new Color(0xE0F2F1) : new Color(0xE0E0E0));
				btnTang.setCursor(slMoi < soLuongBanDau ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

				btnGiam.setEnabled(slMoi > 1);
				btnGiam.setBackground(slMoi > 1 ? new Color(0xE0F2F1) : new Color(0xE0E0E0));
				btnGiam.setCursor(slMoi > 1 ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

			} catch (NumberFormatException ex) {
				txtSoLuong.setText("1");
			}
			capNhatTongTienTra();
		});

		pnDongCTPT.setMaximumSize(new Dimension(1060, 150));
		pnDongCTPT.setMinimumSize(new Dimension(1040, 120));

		// === Lấy thông tin từ cthd để thêm vào bảng dữ liệu ===
		String maLo = cthd.getLoSanPham() != null ? cthd.getLoSanPham().getMaLo() : "";
		String tenSP = cthd.getSanPham() != null ? cthd.getSanPham().getTenSanPham()
				: (cthd.getLoSanPham() != null && cthd.getLoSanPham().getSanPham() != null
						? cthd.getLoSanPham().getSanPham().getTenSanPham()
						: "");
		int soLuong = (int) cthd.getSoLuong();
		double giaBan = cthd.getGiaBan();
		double thanhTien = giaBan * soLuong;

		// === Thêm dòng vào bảng ===
		modelTraHang.addRow(new Object[] { maLo, tenSP, soLuong, giaBan, thanhTien, "" });

		return pnDongCTPT;
	}

	private JPanel makeLabel(String left, JLabel rightLabel) {
		JPanel pn = new JPanel(new BorderLayout());
		pn.setOpaque(false);
		JLabel l = new JLabel(left);
		l.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		rightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		pn.add(l, BorderLayout.WEST);
		pn.add(rightLabel, BorderLayout.EAST);
		pn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		return pn;
	}

	private void capNhatTongTienTra() {
		double tong = 0;
		for (Component comp : pnDanhSachDon.getComponents()) {
			if (comp instanceof JPanel p) {
				for (Component c : p.getComponents()) {
					if (c instanceof JLabel lbl && "lblTongTien".equals(lbl.getName())) {
						String txt = lbl.getText();
						if (txt != null) {
							txt = txt.replace("vnđ", "").replace("đ", "").replace(".", "").replace(",", "")
									.replace(" ", "").trim();
							try {
								tong += Double.parseDouble(txt);
							} catch (NumberFormatException ignored) {
							}
						}
					}
				}
			}
		}
		tienTra = tong;
		lblTienTra.setText(String.format("%,.0f đ", tienTra));
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
