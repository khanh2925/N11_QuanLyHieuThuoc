package gui.nhanvien;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import component.border.*;
import component.input.*;
import component.label.*;
import component.button.*;
import dao.LoSanPham_DAO;
import dao.PhieuHuy_DAO;
import dao.QuyCachDongGoi_DAO;

import entity.ChiTietPhieuHuy;
import entity.ItemHuyHang;
import entity.LoSanPham;
import entity.NhanVien;
import entity.PhieuHuy;
import entity.QuyCachDongGoi;
import entity.SanPham;
import entity.Session;
import entity.TaiKhoan;
import enums.LoaiSanPham;
import gui.panel.HuyHangItemPanel;
import gui.dialog.DialogChonLo;

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

	private static final String PLACEHOLDER_TIM_LO = "Nhập mã lô (LO-xxxxxx),mã SP (SP-xxxxxx), tên SP";
	// ====== TÌM KIẾM / DANH SÁCH ======
	private JTextField txtTimLo; // tìm theo mã lô / sau này có thể mở dialog chọn lô
	private JPanel pnCotPhaiCenter;
	private JPanel pnDanhSachLo; // chứa các panel dòng huỷ

	// ====== TÓM TẮT BÊN PHẢI ======
	private JTextField lblTongDong;
	private JTextField lblTongSoLuong;
	private JTextField lblTongTien;
	private PillButton btnTaoPhieu;
	private PillButton btnHSD;
	private JButton btnHuyBo;

	// ====== MODEL TẠM LƯU DỮ LIỆU HUỶ ======
	// Mỗi dòng: MaLo, TenSP, HSD, SLTon, SLHuy, DonGiaNhap, ThanhTien, LyDo
	private DefaultTableModel modelHuy;
	private double tongTienHuy = 0;

	// ====== DAO ======
	private final LoSanPham_DAO loDAO = new LoSanPham_DAO();
	private final PhieuHuy_DAO phieuHuyDAO = new PhieuHuy_DAO();
	private final QuyCachDongGoi_DAO quyCachDAO = new QuyCachDongGoi_DAO();

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

		txtTimLo = TaoJtextNhanh.timKiem();
		txtTimLo.setBorder(new LineBorder(new Color(0x00C0E2), 3, true));
		txtTimLo.setBounds(25, 17, 480, 60);
		txtTimLo.setForeground(Color.GRAY);

		PlaceholderSupport.addPlaceholder(txtTimLo, PLACEHOLDER_TIM_LO);
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
		pnRight.setBorder(new EmptyBorder(25, 25, 25, 25));
		pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));
		add(pnRight, BorderLayout.EAST);

		// ==== Số dòng huỷ ====
		Box boxSoDong = Box.createHorizontalBox();
		boxSoDong.add(TaoLabelNhanh.tieuDe("Số dòng huỷ:"));
		lblTongDong = TaoJtextNhanh.hienThi("0 dòng", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
		lblTongDong.setMaximumSize(new Dimension(215, 40));
		lblTongDong.setPreferredSize(new Dimension(215, 40));
		lblTongDong.setFocusable(false);
		boxSoDong.add(lblTongDong);
		pnRight.add(boxSoDong);
		pnRight.add(Box.createVerticalStrut(10));

		// ==== Tổng SL huỷ ====
		Box boxTongSL = Box.createHorizontalBox();
		boxTongSL.add(TaoLabelNhanh.tieuDe("Tổng SL huỷ:"));
		lblTongSoLuong = TaoJtextNhanh.hienThi("0", new Font("Segoe UI", Font.BOLD, 20), Color.BLACK);
		lblTongSoLuong.setMaximumSize(new Dimension(215, 40));
		lblTongSoLuong.setPreferredSize(new Dimension(215, 40));
		lblTongSoLuong.setFocusable(false);
		boxTongSL.add(lblTongSoLuong);
		pnRight.add(boxTongSL);
		pnRight.add(Box.createVerticalStrut(10));

		// ==== Tổng giá trị huỷ ====
		Box boxTongTien = Box.createHorizontalBox();
		boxTongTien.add(TaoLabelNhanh.tieuDe("Tổng giá trị huỷ:"));
		lblTongTien = TaoJtextNhanh.hienThi("0 đ", new Font("Segoe UI", Font.BOLD, 20), new Color(0xD32F2F));
		lblTongTien.setMaximumSize(new Dimension(215, 40));
		lblTongTien.setPreferredSize(new Dimension(215, 40));
		lblTongTien.setFocusable(false);
		boxTongTien.add(lblTongTien);
		pnRight.add(boxTongTien);
		pnRight.add(Box.createVerticalStrut(20));

		// ==== Nút ====
		btnTaoPhieu = new PillButton("Tạo phiếu huỷ");
		btnTaoPhieu.setMaximumSize(new Dimension(135, 40));
		btnTaoPhieu.setPreferredSize(new Dimension(135, 40));

		btnHuyBo = new PillButton("Huỷ bỏ");
		btnHuyBo.setMaximumSize(new Dimension(115, 40));
		btnHuyBo.setPreferredSize(new Dimension(115, 40));

		JPanel pnBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
		pnBtn.setOpaque(false);
		pnBtn.add(btnTaoPhieu);
		pnBtn.add(btnHuyBo);

		pnRight.add(pnBtn);
		pnRight.add(Box.createVerticalStrut(15));

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
		txtTimLo.addActionListener(this);
		btnHSD.addActionListener(this);
		btnTaoPhieu.addActionListener(this);
		btnHuyBo.addActionListener(this);
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

		// Kiểm tra nếu chọn tất cả
		if (dialog.isSelectedAll()) {
			ArrayList<LoSanPham> danhSachLo = dialog.getDanhSachLoChon();
			if (danhSachLo != null && !danhSachLo.isEmpty()) {
				int soLoThem = 0;
				int soLoTrung = 0;

				for (LoSanPham lo : danhSachLo) {
					// Kiểm tra trùng
					boolean daTonTai = false;
					for (ItemHuyHang t : dsItem) {
						if (t.getMaLo().equals(lo.getMaLo())) {
							daTonTai = true;
							soLoTrung++;
							break;
						}
					}

					if (!daTonTai) {
						addDongHuyVoiLyDo(lo, lo.getSanPham().getGiaNhap(), "Gần hết hạn sử dụng");
						soLoThem++;
					}
				}

				String thongBao = String.format("✅ Đã thêm %d lô vào danh sách huỷ!", soLoThem);

				if (soLoTrung > 0) {
					thongBao += String.format("\n⚠️ Bỏ qua %d lô đã có trong danh sách.", soLoTrung);
				}

				JOptionPane.showMessageDialog(this, thongBao, "Thành công", JOptionPane.INFORMATION_MESSAGE);
			}
		} else {
			// Chọn 1 lô
			LoSanPham lo = dialog.getSelectedLo();
			if (lo != null) {
				// Nếu chọn từ dialog HSD, tự động gán lý do
				if ("HSD".equals(loaiTim)) {
					addDongHuyVoiLyDo(lo, lo.getSanPham().getGiaNhap(), "Gần hết hạn sử dụng");
				} else {
					addDongHuy(lo, lo.getSanPham().getGiaNhap());
				}
			}
		}
	}

	/** API cho các màn khác muốn đẩy lô vào danh sách huỷ với lý do tự động */
	private void addDongHuyVoiLyDo(LoSanPham lo, double giaNhap, String lyDo) {
		// kiểm tra trùng
		for (ItemHuyHang t : dsItem) {
			if (t.getMaLo().equals(lo.getMaLo())) {
				return; // Không hiển thị thông báo khi thêm nhiều
			}
		}

		// Lấy số lượng tồn theo đơn vị gốc
		int slTonGoc = lo.getSoLuongTon();

		ItemHuyHang it = new ItemHuyHang(lo.getMaLo(), lo.getSanPham().getTenSanPham(), slTonGoc, giaNhap);

		// ✅ Set số lượng huỷ = toàn bộ số lượng tồn (cho chức năng "Huỷ tất cả")
		it.setSoLuongHuy(slTonGoc);

		// Lấy và set quy cách gốc
		QuyCachDongGoi qcGoc = quyCachDAO.timQuyCachGocTheoSanPham(lo.getSanPham().getMaSanPham());
		if (qcGoc != null) {
			it.setQuyCachGoc(qcGoc);
			it.setQuyCachHienTai(qcGoc);
		}

		// Gán lý do tự động
		if (lyDo != null && !lyDo.isEmpty()) {
			it.setLyDo(lyDo);
		}

		dsItem.add(it);
		addPanelItem(it);

		// --- cập nhật model cũ ---
		modelHuy.addRow(
				new Object[] { it.getMaLo(), it.getTenSanPham(), lo.getHanSuDung().format(fmt), it.getSoLuongTon(),
						it.getSoLuongHuy(), it.getDonGiaNhap(), it.getThanhTien(), lyDo != null ? lyDo : "" });

		capNhatTongSoLuongVaTien();
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

		// Lấy số lượng tồn theo đơn vị gốc
		int slTonGoc = lo.getSoLuongTon();

		ItemHuyHang it = new ItemHuyHang(lo.getMaLo(), lo.getSanPham().getTenSanPham(), slTonGoc, giaNhap);

		// Lấy và set quy cách gốc
		QuyCachDongGoi qcGoc = quyCachDAO.timQuyCachGocTheoSanPham(lo.getSanPham().getMaSanPham());
		if (qcGoc != null) {
			it.setQuyCachGoc(qcGoc);
			it.setQuyCachHienTai(qcGoc);
		}

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

				// Lưu số lượng huỷ theo đơn vị đang hiển thị (UI)
				modelHuy.setValueAt(it.getSoLuongHuy(), i, 4);
				// Thành tiền đã tự động tính theo đơn vị gốc
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

	private void capNhatTongSoLuongVaTien() {
		int soDong = dsItem.size();
		int tongSLGoc = 0; // Tổng số lượng theo đơn vị gốc
		double tongTien = 0;

		// Tính từ dsItem thay vì model
		for (ItemHuyHang it : dsItem) {
			tongSLGoc += it.getSoLuongHuyTheoGoc();
			tongTien += it.getThanhTien();
		}

		tongTienHuy = tongTien;

		lblTongDong.setText(soDong + " dòng");
		lblTongSoLuong.setText(String.valueOf(tongSLGoc)); // Hiển thị tổng theo đơn vị gốc
		lblTongTien.setText(String.format("%,.0f đ", tongTienHuy));
	}

	private void resetForm() {
		pnDanhSachLo.removeAll();
		pnDanhSachLo.revalidate();
		pnDanhSachLo.repaint();

		modelHuy.setRowCount(0);
		dsItem.clear(); // Clear danh sách item

		lblTongDong.setText("0 dòng");
		lblTongSoLuong.setText("0");
		lblTongTien.setText("0 đ");

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

		// Chuẩn bị chi tiết từ dsItem (không dùng model)
		List<ChiTietPhieuHuy> dsCT = new ArrayList<>();
		int tongSLGoc = 0; // Tổng số lượng huỷ theo đơn vị gốc

		// Duyệt qua dsItem thay vì model
		for (ItemHuyHang it : dsItem) {
			// Lấy số lượng huỷ theo đơn vị gốc (đã quy đổi)
			int slHuyGoc = it.getSoLuongHuyTheoGoc();

			if (slHuyGoc <= 0) {
				JOptionPane.showMessageDialog(this, "Số lượng huỷ của lô " + it.getMaLo() + " phải > 0.",
						"Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (slHuyGoc > it.getSoLuongTon()) {
				JOptionPane.showMessageDialog(this,
						"Số lượng huỷ của lô " + it.getMaLo() + " vượt quá tồn (theo đơn vị gốc).",
						"Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
				return;
			}

			LoSanPham lo = loDAO.timLoTheoMa(it.getMaLo());
			if (lo == null) {
				JOptionPane.showMessageDialog(this, "Không tìm thấy lô trong CSDL: " + it.getMaLo(), "Lỗi dữ liệu",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			// Tạo chi tiết
			ChiTietPhieuHuy ct = new ChiTietPhieuHuy();
			ct.setLoSanPham(lo);
			ct.setSoLuongHuy(slHuyGoc); // Lưu theo đơn vị gốc
			ct.setDonGiaNhap(it.getDonGiaNhap());
			String lyDo = it.getLyDo();
			ct.setLyDoChiTiet(lyDo == null || lyDo.isEmpty() ? null : lyDo);

			// ✅ Set đơn vị tính (lấy DonViTinh từ QuyCachGoc)
			if (it.getQuyCachGoc() != null && it.getQuyCachGoc().getDonViTinh() != null) {
				ct.setDonViTinh(it.getQuyCachGoc().getDonViTinh());
			}

			ct.setTrangThai(ChiTietPhieuHuy.CHO_DUYET); // 1 = Chờ duyệt
			ct.capNhatThanhTien();

			dsCT.add(ct);
			tongSLGoc += slHuyGoc;
		}

		int confirm = JOptionPane.showConfirmDialog(this, String.format(
				"Xác nhận tạo phiếu huỷ?\n- Số dòng: %d\n- Tổng SL huỷ (đơn vị gốc): %d\n- Giá trị huỷ: %,.0f đ",
				dsCT.size(), tongSLGoc, tongTienHuy), "Xác nhận", JOptionPane.YES_NO_OPTION);

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
				String.format("✔ Tạo phiếu huỷ thành công!\nMã phiếu: %s\nTổng tiền huỷ: %,.0f đ", ph.getMaPhieuHuy(),
						ph.getTongTien()),
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

		if (src == btnHuyBo) {
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
