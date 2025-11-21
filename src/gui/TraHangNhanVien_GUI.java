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
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.LoSanPham_DAO;
import dao.PhieuTra_DAO;
import entity.Session;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuTra;
import entity.HoaDon;
import entity.ItemTraHang;
import entity.KhachHang;
import entity.PhieuTra;
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

//	private void capNhatTongTienTra() {
//		double tong = 0;
//		int colTT = modelTraHang.findColumn("Thành tiền");
//
//		for (int i = 0; i < modelTraHang.getRowCount(); i++) {
//			tong += Double.parseDouble(modelTraHang.getValueAt(i, colTT).toString());
//		}
//
//		tienTra = tong;
//		txtTienTra.setText(String.format("%,.0f đ", tienTra));
//	}
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

		List<ChiTietHoaDon> dsCT = cthdDAO.layDanhSachChiTietTheoMaHD(maHD);

		ChonSanPhamTraDialog dlg = new ChonSanPhamTraDialog(dsCT);
		dlg.setVisible(true);
		List<ChiTietHoaDon> dsChon = dlg.getDsSanPhamDuocChon();

		if (dsChon.isEmpty()) {
			resetForm();
			return;
		}

		pnDanhSachDon.removeAll();
		dsTraHang.clear();

		int stt = 1;

		for (ChiTietHoaDon ct : dsChon) {

			ItemTraHang item = new ItemTraHang(ct.getLoSanPham().getMaLo(),
					ct.getLoSanPham().getSanPham().getTenSanPham(), ct.getDonViTinh().getTenDonViTinh(), ct.getGiaBan(),
					(int) ct.getSoLuong(), ct);

			dsTraHang.add(item);

			String anh = ct.getLoSanPham().getSanPham().getHinhAnh();

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
			}, anh // ảnh sản phẩm
			);

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
			ChiTietPhieuTra ct = new ChiTietPhieuTra();
			ct.setChiTietHoaDon(it.getChiTietHoaDonGoc());
			ct.setSoLuong(it.getSoLuongTra());
//			ct.setThanhTienHoan(it.getThanhTien());
			ct.capNhatThanhTienHoan();
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
