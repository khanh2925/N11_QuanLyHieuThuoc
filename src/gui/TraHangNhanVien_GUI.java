package gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import customcomponent.PillButton;
import customcomponent.TaoJtextNhanh;
import customcomponent.TaoLabelNhanh;
import dao.ChiTietHoaDon_DAO;
import dao.ChiTietPhieuTra_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.PhieuTra_DAO;
import dao.QuyCachDongGoi_DAO;
import entity.Session;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuTra;
import entity.HoaDon;
import entity.ItemTraHang;
import entity.KhachHang;
import entity.PhieuTra;
import entity.QuyCachDongGoi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;
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

	private double tienTra = 0;

	private LocalDate today = LocalDate.now();
	private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final HoaDon_DAO hoaDonDAO;
	private final ChiTietHoaDon_DAO cthdDAO;
	private final PhieuTra_DAO ptDAO;
	private final ChiTietPhieuTra_DAO ctptDAO;
	private final QuyCachDongGoi_DAO qcdgDAO;

	private JTextArea txtGhiChuGiamGia;

	private PillButton btnTraHang;

	private PillButton btnHuy;
	private List<ItemTraHang> dsTraHang = new ArrayList<>();

	public TraHangNhanVien_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();

		hoaDonDAO = new HoaDon_DAO();
		cthdDAO = new ChiTietHoaDon_DAO();
		ptDAO = new PhieuTra_DAO();
		ctptDAO = new ChiTietPhieuTra_DAO();
		qcdgDAO = new QuyCachDongGoi_DAO();
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
		txtGhiChuGiamGia.setMaximumSize(new Dimension(Integer.MAX_VALUE, txtGhiChuGiamGia.getPreferredSize().height));
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
		for (ItemTraHang it : dsTraHang) {
			tong += it.getThanhTien();
		}
		tienTra = tong;
		txtTienTra.setText(String.format("%,.0f đ", tienTra));
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

	private void capNhatSTT() {
		Component[] comps = pnDanhSachDon.getComponents();
		int stt = 1;
		for (Component c : comps) {
			if (c instanceof TraHangItemPanel panel) {
				panel.setSTT(stt++);
			}
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

		// Lấy toàn bộ CTHD theo hoá đơn
		List<ChiTietHoaDon> dsCT = cthdDAO.layDanhSachChiTietTheoMaHD(maHD);

		// Dialog chọn sản phẩm (theo tên như bạn đã làm)
		ChonSanPhamTraDialog dlg = new ChonSanPhamTraDialog(dsCT);
		dlg.setVisible(true);
		List<ChiTietHoaDon> dsChon = dlg.getDsSanPhamDuocChon();

		if (dsChon.isEmpty()) {
			resetForm();
			return;
		}

		// Gom nhóm CTHD theo LÔ (mỗi lô = 1 ItemTraHang, nhưng có thể có nhiều DVT)
		Map<String, List<ChiTietHoaDon>> mapTheoLo = new java.util.LinkedHashMap<>();
		for (ChiTietHoaDon ct : dsChon) {
			String maLo = ct.getLoSanPham().getMaLo();
			mapTheoLo.computeIfAbsent(maLo, k -> new ArrayList<>()).add(ct);
		}

		pnDanhSachDon.removeAll();
		dsTraHang.clear();

		int stt = 1;

		for (Map.Entry<String, List<ChiTietHoaDon>> entry : mapTheoLo.entrySet()) {
			List<ChiTietHoaDon> dsTheoLo = entry.getValue();
			if (dsTheoLo.isEmpty())
				continue;

			ChiTietHoaDon ctDau = dsTheoLo.get(0);
			String maLo = ctDau.getLoSanPham().getMaLo();

			var sp = ctDau.getLoSanPham().getSanPham();

			// Lấy tất cả quy cách (DVT) của sản phẩm
			List<QuyCachDongGoi> dsQuyCach = qcdgDAO.layDanhSachQuyCachTheoSanPham(sp.getMaSanPham());
			if (dsQuyCach == null || dsQuyCach.isEmpty()) {
				// fallback: không có quy cách → vẫn tạo item như cũ (1 DVT)
				ItemTraHang item = new ItemTraHang(maLo, sp.getTenSanPham(), ctDau.getDonViTinh().getTenDonViTinh(),
						ctDau.getGiaBan(), (int) ctDau.getSoLuong(), ctDau);

				dsTraHang.add(item);

				String anh = sp.getHinhAnh();
				TraHangItemPanel pnl = new TraHangItemPanel(item, stt++, new TraHangItemPanel.Listener() {
					@Override
					public void onUpdate(ItemTraHang i) {
						capNhatTongTienTra();
					}

					@Override
					public void onDelete(ItemTraHang i, TraHangItemPanel p) {
						dsTraHang.remove(i);
						pnDanhSachDon.remove(p);
						pnDanhSachDon.revalidate();
						pnDanhSachDon.repaint();
						capNhatTongTienTra();
						capNhatSTT();
					}
				}, anh);

				pnDanhSachDon.add(pnl);
				pnDanhSachDon.add(Box.createVerticalStrut(5));
				continue;
			}

			// Tính tổng số lượng đã mua quy về đơn vị gốc (viên)
			int tongMuaGoc = 0;
			for (ChiTietHoaDon ct : dsTheoLo) {
				QuyCachDongGoi qc = timQuyCachTheoDVT(dsQuyCach, ct.getDonViTinh().getMaDonViTinh());
				if (qc != null) {
					tongMuaGoc += (int) ct.getSoLuong() * qc.getHeSoQuyDoi();
				}
			}
			if (tongMuaGoc <= 0) {
				continue;
			}

			// Chọn quy cách mặc định: đơn vị có hệ số quy đổi lớn nhất nhưng <= SL mua
			QuyCachDongGoi qcMacDinh = chonQuyCachMacDinh(dsQuyCach, tongMuaGoc);

			// Tạo ItemTraHang theo quy đổi
			// Tạo ItemTraHang theo quy đổi (gom tất cả CTHD theo lô)
			ItemTraHang item = new ItemTraHang(maLo, sp.getTenSanPham(), dsTheoLo, // list ChiTietHoaDon của lô này
					dsQuyCach, qcMacDinh);

			dsTraHang.add(item);

			String anh = sp.getHinhAnh();

			TraHangItemPanel pnl = new TraHangItemPanel(item, stt++, new TraHangItemPanel.Listener() {
				@Override
				public void onUpdate(ItemTraHang i) {
					capNhatTongTienTra();
				}

				@Override
				public void onDelete(ItemTraHang i, TraHangItemPanel p) {
					dsTraHang.remove(i);
					pnDanhSachDon.remove(p);
					pnDanhSachDon.revalidate();
					pnDanhSachDon.repaint();
					capNhatTongTienTra();
					capNhatSTT();
				}
			}, anh);

			pnDanhSachDon.add(pnl);
			pnDanhSachDon.add(Box.createVerticalStrut(5));
		}

		pnDanhSachDon.revalidate();
		pnDanhSachDon.repaint();

		capNhatTongTienTra();

		txtMaHoaDon.setText(maHD);
		txtNguoiBan.setText(hd.getNhanVien().getTenNhanVien());
		txtTenKhachHang.setText(hd.getKhachHang().getTenKhachHang());

		capNhatTongTienTra();
		capNhatGhiChuKhuyenMai(dsChon);
	}

	private QuyCachDongGoi timQuyCachTheoDVT(List<QuyCachDongGoi> dsQuyCach, String maDonViTinh) {
		if (dsQuyCach == null || maDonViTinh == null)
			return null;
		for (QuyCachDongGoi qc : dsQuyCach) {
			if (qc.getDonViTinh().getMaDonViTinh().equals(maDonViTinh)) {
				return qc;
			}
		}
		return null;
	}

	/**
	 * Chọn quy cách mặc định: ưu tiên DVT có hệ số lớn nhất nhưng không vượt quá
	 * tổng số lượng đã mua (quy về gốc).
	 */
	private QuyCachDongGoi chonQuyCachMacDinh(List<QuyCachDongGoi> dsQuyCach, int tongMuaGoc) {
		if (dsQuyCach == null || dsQuyCach.isEmpty())
			return null;

		QuyCachDongGoi best = null;
		for (QuyCachDongGoi qc : dsQuyCach) {
			// chỉ chọn những DVT mà 1 đơn vị của nó không vượt quá SL đã mua
			if (qc.getHeSoQuyDoi() <= tongMuaGoc) {
				if (best == null || qc.getHeSoQuyDoi() > best.getHeSoQuyDoi()) {
					best = qc;
				}
			}
		}
		if (best != null)
			return best;

		// fallback: nếu tất cả đều > tongMuaGoc, lấy đơn vị gốc
		for (QuyCachDongGoi qc : dsQuyCach) {
			if (qc.isDonViGoc())
				return qc;
		}
		return dsQuyCach.get(0);
	}

	/**
	 * Kiểm tra số lượng trả hợp lệ dựa trên số lượng đã trả trước đó. Trả về: true
	 * = hợp lệ, false = vượt số lượng cho phép.
	 */
	private boolean kiemTraSoLuongHopLe(String maHD, ItemTraHang it) {

		String maLo = it.getMaLo();

		// Giả định: ctptDAO.tongSoLuongDaTra(maHD, maLo) đã quy về đơn vị gốc
		double daTraGoc = ctptDAO.tongSoLuongDaTra(maHD, maLo);
		int daMuaGoc = it.getSoLuongMuaGoc();
		double conLaiGoc = daMuaGoc - daTraGoc;

//		int slTraGoc = it.getSoLuongTraQuyVeGoc();
		int slTraGoc = it.getSoLuongTra() * it.getQuyCachDangChon().getHeSoQuyDoi();

		if (slTraGoc > conLaiGoc) {
			JOptionPane.showMessageDialog(this,
					"Lô " + maLo + " chỉ còn được trả tối đa: " + (int) conLaiGoc + " (quy về đơn vị gốc).",
					"Vượt số lượng còn lại", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		return true;
	}

	private void xuLyTraHang(ActionEvent e) {

		if (dsTraHang.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không có sản phẩm để trả!");
			return;
		}

		HoaDon hd = hoaDonDAO.timHoaDonTheoMa(txtMaHoaDon.getText());
		if (hd == null)
			return;

		String maPhieu = ptDAO.taoMaPhieuTra();

		PhieuTra pt = new PhieuTra();
		pt.setMaPhieuTra(maPhieu);
		pt.setKhachHang(hd.getKhachHang());
		pt.setNhanVien(Session.getInstance().getTaiKhoanDangNhap().getNhanVien());
		pt.setNgayLap(LocalDate.now());
		pt.setDaDuyet(false);

		List<ChiTietPhieuTra> dsCT = new ArrayList<>();

		for (ItemTraHang it : dsTraHang) {

			String maHD = txtMaHoaDon.getText().trim();
			String maLo = it.getMaLo();

			// 1) Lấy tổng đã trả trước đó (quy về gốc)
			double daTraGoc = ctptDAO.tongSoLuongDaTra(maHD, maLo);

			// 2) Tổng số lượng đã mua của sản phẩm (quy về gốc)
			int daMuaGoc = it.getSoLuongMuaGoc();

			// 3) Số lượng muốn trả lần này (quy về gốc)
			int slTraGoc = it.getSoLuongTra() * it.getQuyCachDangChon().getHeSoQuyDoi();

			// 4) Kiểm tra vượt số lượng cho phép
			if (daTraGoc + slTraGoc > daMuaGoc) {
				JOptionPane.showMessageDialog(this,
						"Sản phẩm: " + it.getTenSanPham() + "\n" + "Đã mua: " + daMuaGoc + " (gốc)\n"
								+ "Đã trả trước đó: " + (int) daTraGoc + " (gốc)\n" + "Muốn trả thêm: " + slTraGoc
								+ " (gốc)\n\n" + "❌ Tổng vượt mức cho phép!",
						"Vượt số lượng", JOptionPane.ERROR_MESSAGE);
				return;
			}

			// 5) Kiểm tra nếu đã trả hết mà vẫn trả tiếp
			if (daTraGoc >= daMuaGoc) {
				JOptionPane.showMessageDialog(this,
						"Sản phẩm " + it.getTenSanPham() + " đã được trả đủ.\nKhông thể trả thêm!", "Đã trả hết",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			// 6) Tạo chi tiết phiếu trả
			ChiTietPhieuTra ct = new ChiTietPhieuTra();

			// hóa đơn gốc để biết mã HĐ, lô, KM... (dùng cho log + đơn giá tham khảo)
			ct.setChiTietHoaDon(it.getChiTietHoaDonGoc());

			// số lượng trả theo đơn vị đang chọn
			ct.setSoLuong(it.getSoLuongTra());

			// đơn vị tính ĐANG CHỌN trong combobox (vỉ / hộp / viên)
			if (it.getQuyCachDangChon() != null) {
				ct.setDonViTinh(it.getQuyCachDangChon().getDonViTinh());
			}

			// tiền hoàn: dùng đúng đơn giá & số lượng mà UI đang hiển thị
			ct.setThanhTienHoan(it.getThanhTien());

			ct.setLyDoChiTiet(it.getLyDo());
			ct.setTrangThai(0);
			dsCT.add(ct);

		}

		pt.setChiTietPhieuTraList(dsCT);

		boolean ok = ptDAO.themPhieuTraVaChiTiet(pt, dsCT);
		if (!ok) {
			JOptionPane.showMessageDialog(this, "Không thể lưu phiếu trả!");
			return;
		}

		new PhieuTraPreviewDialog(SwingUtilities.getWindowAncestor(this), pt, dsCT).setVisible(true);
		resetForm();
	}

	private void resetForm() {
		pnDanhSachDon.removeAll();
		pnDanhSachDon.revalidate();
		pnDanhSachDon.repaint();

		dsTraHang.clear();

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
