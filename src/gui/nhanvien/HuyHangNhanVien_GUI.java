package gui.nhanvien;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;

import dao.LoSanPham_DAO;
import dao.PhieuHuy_DAO;

import entity.ChiTietPhieuHuy;
import entity.ItemHuyHang;
import entity.LoSanPham;
import entity.NhanVien;
import entity.PhieuHuy;
import entity.SanPham;
import entity.Session;
import entity.TaiKhoan;
import enums.LoaiSanPham;
import gui.dialog.DialogChonLo;
import gui.panel.HuyHangItemPanel;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Giao diện NHÂN VIÊN lập PHIẾU HUỶ HÀNG - NV chọn lô hỏng / không bán được -
 * Lập phiếu huỷ trạng thái CHỜ DUYỆT - Không trừ tồn kho tại đây (trừ tồn khi
 * QL duyệt chi tiết)
 */
public class HuyHangNhanVien_GUI extends JPanel implements ActionListener {

	// ====== TÌM KIẾM / DANH SÁCH ======
	private JTextField txtTimLo; // tìm theo mã lô / sau này có thể mở dialog chọn lô
	private JPanel pnCotPhaiCenter;
	private JPanel pnDanhSachLo; // chứa các panel dòng huỷ

	// ====== TÓM TẮT BÊN PHẢI ======
	private JLabel lblThoiGian;
	private JLabel lblTongDong;
	private JLabel lblTongSoLuong;
	private JLabel lblTongTien;
	private JTextArea txtGhiChuChung;
	private PillButton btnTaoPhieu;
	private PillButton btnHSD;
	private JButton btnLamMoi;

	// ====== MODEL TẠM LƯU DỮ LIỆU HUỶ ======
	// Mỗi dòng: MaLo, TenSP, HSD, SLTon, SLHuy, DonGiaNhap, ThanhTien, LyDo
	private DefaultTableModel modelHuy;
	private double tongTienHuy = 0;

	// ====== DAO ======
	private final LoSanPham_DAO loDAO = new LoSanPham_DAO();
	private final PhieuHuy_DAO phieuHuyDAO = new PhieuHuy_DAO();

	// ====== NGÀY ======
	private final LocalDate today = LocalDate.now();
	private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	// ====== MODEL MỚI DÙNG ITEMHUYHANG ======
	private final List<ItemHuyHang> dsItem = new ArrayList<>();

	public HuyHangNhanVien_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		// ===== HEADER =====
		JPanel pnHeader = new JPanel(null);
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setBackground(new Color(0xE3F2F5));
		add(pnHeader, BorderLayout.NORTH);

		txtTimLo = new JTextField();
		txtTimLo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtTimLo.setBounds(25, 17, 510, 60);
		txtTimLo.setBorder(new RoundedBorder(20));
		txtTimLo.setBackground(Color.WHITE);
		txtTimLo.setForeground(Color.GRAY);
		PlaceholderSupport.addPlaceholder(txtTimLo, "Nhập mã lô (LO-xxxxxx),mã SP (SP-xxxxxx), tên SP");
		pnHeader.add(txtTimLo);

		btnHSD = new PillButton("HUỶ THEO HSD");
		pnHeader.add(btnHSD);
		btnHSD.setBounds(545, 28, 154, 40);

		// ===== CENTER (DANH SÁCH LÔ HUỶ) =====
		pnCotPhaiCenter = new JPanel();
		pnCotPhaiCenter.setPreferredSize(new Dimension(1073, 992));
		pnCotPhaiCenter.setBackground(Color.WHITE);
		pnCotPhaiCenter.setBorder(
				new CompoundBorder(new LineBorder(new Color(0xD32F2F), 3, true), new EmptyBorder(5, 5, 5, 5)));
		pnCotPhaiCenter.setLayout(new BorderLayout(0, 0));
		add(pnCotPhaiCenter, BorderLayout.CENTER);

		pnDanhSachLo = new JPanel();
		pnDanhSachLo.setLayout(new BoxLayout(pnDanhSachLo, BoxLayout.Y_AXIS));
		pnDanhSachLo.setBackground(Color.WHITE);

		JScrollPane scrPnDanhSachLo = new JScrollPane(pnDanhSachLo);
		scrPnDanhSachLo.setBorder(null);
		scrPnDanhSachLo.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrPnDanhSachLo.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrPnDanhSachLo.getVerticalScrollBar().setOpaque(false);
		pnCotPhaiCenter.add(scrPnDanhSachLo, BorderLayout.CENTER);

		// ====== CỘT PHẢI (THÔNG TIN PHIẾU HUỶ) ======
		JPanel pnRight = new JPanel();
		pnRight.setPreferredSize(new Dimension(1920 - 383 - 1073, 1080));
		pnRight.setBackground(Color.WHITE);
		pnRight.setBorder(new EmptyBorder(20, 20, 20, 20));
		pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));
		add(pnRight, BorderLayout.EAST);

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

		pnRight.add(pnNhanVien);
		pnRight.add(Box.createVerticalStrut(10));

		// ===== ĐƯỜNG KẺ =====
		JSeparator lineNV = new JSeparator();
		lineNV.setForeground(new Color(200, 200, 200));
		lineNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		pnRight.add(Box.createVerticalStrut(4));
		pnRight.add(lineNV);
		pnRight.add(Box.createVerticalStrut(10));

		// ===== TÓM TẮT =====
		lblTongDong = new JLabel("0 dòng");
		lblTongSoLuong = new JLabel("0");
		lblTongTien = new JLabel("0 đ");

		pnRight.add(makeInfoRow("Số dòng huỷ:", lblTongDong));
		pnRight.add(makeInfoRow("Tổng SL huỷ:", lblTongSoLuong));
		pnRight.add(makeInfoRow("Tổng giá trị huỷ:", lblTongTien));

		pnRight.add(Box.createVerticalStrut(10));

		// ===== GHI CHÚ CHUNG =====
		JPanel pnGhiChu = new JPanel(new BorderLayout(5, 5));
		pnGhiChu.setOpaque(false);
		pnGhiChu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

		JLabel lblGhiChu = new JLabel("Ghi chú chung:");
		lblGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		pnGhiChu.add(lblGhiChu, BorderLayout.NORTH);

		txtGhiChuChung = new JTextArea();
		txtGhiChuChung.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		txtGhiChuChung.setLineWrap(true);
		txtGhiChuChung.setWrapStyleWord(true);
		txtGhiChuChung.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0xCCCCCC), 1, true),
				new EmptyBorder(5, 8, 5, 8)));

		JScrollPane scrGhiChu = new JScrollPane(txtGhiChuChung);
		scrGhiChu.setBorder(null);
		scrGhiChu.setPreferredSize(new Dimension(100, 80));
		pnGhiChu.add(scrGhiChu, BorderLayout.CENTER);

		pnRight.add(pnGhiChu);
		pnRight.add(Box.createVerticalStrut(20));

		// ===== NÚT HÀNH ĐỘNG =====
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		actionPanel.setBackground(null);

		btnTaoPhieu = new PillButton("Tạo phiếu huỷ");
		btnTaoPhieu.setFont(new Font("Segoe UI", Font.BOLD, 20));
		btnTaoPhieu.setAlignmentX(Component.CENTER_ALIGNMENT);
		actionPanel.add(btnTaoPhieu);

		btnLamMoi = new JButton("Làm mới");
		actionPanel.add(btnLamMoi);

		pnRight.add(actionPanel);

		// ===== MODEL TẠM =====
		String[] cols = { "Mã lô", "Tên sản phẩm", "HSD", "SL tồn", "SL huỷ", "Đơn giá nhập", "Thành tiền", "Lý do" };
		modelHuy = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// Cho phép chỉnh lý do nếu muốn
				return column == 7;
			}
		};

		// ===== SỰ KIỆN =====
		btnLamMoi.addActionListener(this);
		txtTimLo.addActionListener(this);
		btnTaoPhieu.addActionListener(this);
		btnHSD.addActionListener(this);
	}

	// ===========================================
	// =========== LOGIC TÌM LÔ & THÊM ===========
	// ===========================================

	/**
	 * Xử lý ô tìm kiếm mới: - Nếu nhập LO-xxxxxx → tải lô trực tiếp - Nếu nhập mã
	 * SP / tên SP → mở dialog chọn lô - Nếu nhập hạn dùng → mở dialog và lọc theo
	 * HSD
	 */
	private void xuLyTimKiem() {

		String input = txtTimLo.getText().trim();
		if (input.isEmpty())
			return;

		// 1) Nếu nhập MÃ LÔ → load trực tiếp, bỏ qua dialog
		if (input.matches("^LO-\\d{6}$")) {

			LoSanPham lo = loDAO.timLoTheoMa(input);
			if (lo == null) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy lô " + input, "Không tồn tại",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			double giaNhap = lo.getSanPham().getGiaNhap();
			addDongHuy(lo, giaNhap);

			txtTimLo.setText("");
			return;
		}

		// 2) Nếu là mã sản phẩm → mở dialog chọn lô theo mã SP
		if (input.matches("^SP-\\w+$") || input.matches("^[A-Za-z0-9_-]+$")) {

			MoDialogChonLo(input, "MASP");
			txtTimLo.setText("");
			return;
		}

		// 3) Còn lại xem như tên sản phẩm → mở dialog theo tên
		MoDialogChonLo(input, "TENSP");
		txtTimLo.setText("");
	}

	/**
	 * Mở dialog chọn lô theo loại tìm kiếm: - TENSP: tên sản phẩm - MASP: mã sản
	 * phẩm - HSD: hạn sử dụng
	 *
	 * Kết quả trả về LoSanPham → addDongHuy()
	 */
	private void MoDialogChonLo(String keyword, String loaiTim) {
		DialogChonLo dialog = new DialogChonLo(keyword, loaiTim);
		dialog.setVisible(true);

		LoSanPham lo = dialog.getSelectedLo();
		if (lo != null) {
			addDongHuy(lo, lo.getSanPham().getGiaNhap());
		}
	}

	/** API cho các màn khác muốn đẩy lô vào danh sách huỷ */
	public void addDongHuy(LoSanPham lo, double giaNhap) {

		// kiểm tra trùng
		for (ItemHuyHang t : dsItem) {
			if (t.getMaLo().equals(lo.getMaLo())) {
				JOptionPane.showMessageDialog(this, "Lô đã có trong danh sách huỷ!", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		ItemHuyHang it = new ItemHuyHang(lo.getMaLo(), lo.getSanPham().getTenSanPham(), lo.getSoLuongTon(), giaNhap);

		dsItem.add(it);
		addPanelItem(it);

		// --- cập nhật model cũ ---
		modelHuy.addRow(new Object[] { it.getMaLo(), it.getTenSanPham(), lo.getHanSuDung().format(fmt),
				it.getSoLuongTon(), it.getSoLuongHuy(), it.getDonGiaNhap(), it.getThanhTien(), "" });

		capNhatTongSoLuongVaTien();
	}

	private void capNhatModel(ItemHuyHang it) {
		for (int i = 0; i < modelHuy.getRowCount(); i++) {
			if (modelHuy.getValueAt(i, 0).equals(it.getMaLo())) {

				modelHuy.setValueAt(it.getSoLuongHuy(), i, 4);
				modelHuy.setValueAt(it.getThanhTien(), i, 6);

				String lyDo = it.getLyDo();
				modelHuy.setValueAt(lyDo == null ? "" : lyDo, i, 7);
				return;
			}
		}
	}

	private void capNhatModelXoa(String maLo) {
		for (int i = 0; i < modelHuy.getRowCount(); i++) {
			if (modelHuy.getValueAt(i, 0).equals(maLo)) {
				modelHuy.removeRow(i);
				return;
			}
		}
	}

	private void capNhatSTT() {
		int stt = 1;
		for (Component c : pnDanhSachLo.getComponents()) {
			if (c instanceof HuyHangItemPanel hp) {
				hp.setSTT(stt++);
			}
		}
	}

	private void addPanelItem(ItemHuyHang it) {
		int stt = pnDanhSachLo.getComponentCount() + 1;

		HuyHangItemPanel panel = new HuyHangItemPanel(it, stt, new HuyHangItemPanel.Listener() {

			@Override
			public void onUpdate(ItemHuyHang item) {
				capNhatModel(item);
				capNhatTongSoLuongVaTien();
			}

			@Override
			public void onDelete(ItemHuyHang item, HuyHangItemPanel panel) {

				dsItem.remove(item);
				pnDanhSachLo.remove(panel);

				capNhatSTT();
				capNhatModelXoa(item.getMaLo());
				capNhatTongSoLuongVaTien();

				pnDanhSachLo.revalidate();
				pnDanhSachLo.repaint();
			}

		}, "thuoc_default.png");

		pnDanhSachLo.add(panel);
		pnDanhSachLo.revalidate();
		pnDanhSachLo.repaint();
	}

	private void capNhatSLTrongModel(String maLo, int slHuy, double thanhTien) {
		for (int i = 0; i < modelHuy.getRowCount(); i++) {
			if (modelHuy.getValueAt(i, 0).equals(maLo)) {
				modelHuy.setValueAt(slHuy, i, 4); // SL huỷ
				modelHuy.setValueAt(thanhTien, i, 6); // Thành tiền
				break;
			}
		}
	}

	private void capNhatLyDoTrongModel(String maLo, String lyDo) {
		for (int i = 0; i < modelHuy.getRowCount(); i++) {
			if (modelHuy.getValueAt(i, 0).equals(maLo)) {
				modelHuy.setValueAt(lyDo, i, 7);
				break;
			}
		}
	}

	// ===========================================
	// ============= HỖ TRỢ & TÍNH TỔNG ==========
	// ===========================================

	private int parseIntSafe(String text, int defaultVal) {
		try {
			return Integer.parseInt(text.trim());
		} catch (Exception e) {
			return defaultVal;
		}
	}

	private JPanel makeInfoRow(String left, JLabel rightLabel) {
		JPanel pn = new JPanel(new BorderLayout());
		pn.setOpaque(false);
		JLabel l = new JLabel(left);
		l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		rightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		pn.add(l, BorderLayout.WEST);
		pn.add(rightLabel, BorderLayout.EAST);
		pn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
		return pn;
	}

	private void capNhatTongSoLuongVaTien() {
		int soDong = modelHuy.getRowCount();
		int tongSL = 0;
		double tongTien = 0;

		for (int i = 0; i < modelHuy.getRowCount(); i++) {
			int sl = parseIntSafe(String.valueOf(modelHuy.getValueAt(i, 4)), 0);
			double tien = 0;
			Object tienObj = modelHuy.getValueAt(i, 6);
			if (tienObj instanceof Number n) {
				tien = n.doubleValue();
			} else {
				try {
					tien = Double.parseDouble(tienObj.toString());
				} catch (Exception ignored) {
				}
			}
			tongSL += sl;
			tongTien += tien;
		}

		tongTienHuy = tongTien;

		lblTongDong.setText(soDong + " dòng");
		lblTongSoLuong.setText(String.valueOf(tongSL));
		lblTongTien.setText(String.format("%,.0f đ", tongTienHuy));
	}

	private void resetForm() {
		pnDanhSachLo.removeAll();
		pnDanhSachLo.revalidate();
		pnDanhSachLo.repaint();

		modelHuy.setRowCount(0);

		lblTongDong.setText("0 dòng");
		lblTongSoLuong.setText("0");
		lblTongTien.setText("0 đ");

		txtGhiChuChung.setText("");

		if (lblThoiGian != null) {
			lblThoiGian.setText(LocalDate.now().format(fmt));
		}

		txtTimLo.setText("");
		txtTimLo.setForeground(Color.GRAY);

		tongTienHuy = 0;
		txtTimLo.requestFocus();
	}

	// ===========================================
	// =========== TẠO PHIẾU HUỶ (GỌI DAO) ======
	// ===========================================

	private void xuLyTaoPhieuHuy() {
		if (modelHuy.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Chưa có sản phẩm nào trong danh sách huỷ!", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Lấy nhân viên đang đăng nhập
		TaiKhoan tk = Session.getInstance().getTaiKhoanDangNhap();
		if (tk == null || tk.getNhanVien() == null) {
			JOptionPane.showMessageDialog(this, "Không xác định được nhân viên lập phiếu huỷ.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		NhanVien nv = tk.getNhanVien();

		// Chuẩn bị chi tiết từ model
		List<ChiTietPhieuHuy> dsCT = new ArrayList<>();
		int tongSL = 0;

		for (int i = 0; i < modelHuy.getRowCount(); i++) {
			String maLo = modelHuy.getValueAt(i, 0).toString();
			int slTon = parseIntSafe(String.valueOf(modelHuy.getValueAt(i, 3)), 0);
			int slHuy = parseIntSafe(String.valueOf(modelHuy.getValueAt(i, 4)), 0);
			double donGiaNhap = Double.parseDouble(modelHuy.getValueAt(i, 5).toString());
			String lyDo = modelHuy.getValueAt(i, 7) != null ? modelHuy.getValueAt(i, 7).toString().trim() : null;

			if (slHuy <= 0) {
				JOptionPane.showMessageDialog(this, "Số lượng huỷ ở dòng " + (i + 1) + " phải > 0.",
						"Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (slHuy > slTon) {
				JOptionPane.showMessageDialog(this, "Số lượng huỷ ở dòng " + (i + 1) + " vượt quá tồn.",
						"Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
				return;
			}

			LoSanPham lo = loDAO.timLoTheoMa(maLo);
			if (lo == null) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy lô trong CSDL: " + maLo, "Lỗi dữ liệu",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Tạo chi tiết
			ChiTietPhieuHuy ct = new ChiTietPhieuHuy();
			// phieuHuy sẽ gán sau
			ct.setLoSanPham(lo);
			ct.setSoLuongHuy(slHuy);
			ct.setDonGiaNhap(donGiaNhap);
			ct.setLyDoChiTiet(lyDo == null || lyDo.isEmpty() ? null : lyDo);
			ct.setTrangThai(ChiTietPhieuHuy.CHO_DUYET); // 1 = Chờ duyệt
			ct.capNhatThanhTien();

			dsCT.add(ct);
			tongSL += slHuy;
		}

		int confirm = JOptionPane.showConfirmDialog(this,
				String.format(
						"Xác nhận tạo phiếu huỷ?\n- Số dòng: %d\n- Tổng SL huỷ: %d\n- Giá trị huỷ (ước tính): %,.0f đ",
						modelHuy.getRowCount(), tongSL, tongTienHuy),
				"Xác nhận", JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		// Tạo phiếu huỷ
		PhieuHuy ph = new PhieuHuy();
		ph.setMaPhieuHuy(phieuHuyDAO.taoMaPhieuHuy());
		ph.setNgayLapPhieu(LocalDate.now());
		ph.setNhanVien(nv);
		ph.setTrangThai(false); // false = chờ duyệt

		// Gán phieuHuy cho từng chi tiết
		for (ChiTietPhieuHuy ct : dsCT) {
			ct.setPhieuHuy(ph);
		}
		ph.setChiTietPhieuHuyList(dsCT); // entity tự tính tổng tiền

		// GỌI DAO – đã có TRANSACTION header + chi tiết
		boolean ok = phieuHuyDAO.themPhieuHuy(ph);

		if (!ok) {
			JOptionPane.showMessageDialog(this, "❌ Lưu phiếu huỷ thất bại. Vui lòng thử lại!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JOptionPane.showMessageDialog(this,
				String.format("✔ Tạo phiếu huỷ thành công!\nMã phiếu: %s\nTổng tiền huỷ (ước tính): %,.0f đ",
						ph.getMaPhieuHuy(), ph.getTongTien()),
				"Thành công", JOptionPane.INFORMATION_MESSAGE);

		resetForm();
	}

	// ===========================================
	// ================ TEST MAIN ================
	// ===========================================

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Nhân viên - Huỷ hàng");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new HuyHangNhanVien_GUI());
			frame.setVisible(true);
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == txtTimLo) {
			xuLyTimKiem();
			return;
		}

		if (src == btnLamMoi) {
			resetForm();
			return;
		}

		if (src == btnTaoPhieu) {
			xuLyTaoPhieuHuy();
			return;
		}

		if (src == btnHSD) {
			// Mở dialog chọn lô theo HSD (gần hết hạn)
			MoDialogChonLo("", "HSD"); // keyword giờ không dùng nữa trong HSD
			return;
		}
	}

}
