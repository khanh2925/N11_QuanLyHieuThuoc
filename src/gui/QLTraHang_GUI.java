package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPPr;

import com.toedter.calendar.JDateChooser;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import customcomponent.TaoJtextNhanh;
import dao.ChiTietPhieuTra_DAO;
import dao.LoSanPham_DAO;
import dao.PhieuTra_DAO;
import entity.ChiTietPhieuTra;
import entity.KhuyenMai;
import entity.LoSanPham;
import entity.NhanVien;
import entity.PhieuTra;
import entity.Session;

public class QLTraHang_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private JButton btnXuatFile;
	private JTextField txtSearch;
	private DefaultTableModel modelPT;
	private JTable tblPT;
	private JScrollPane scrCTPT;
	private DefaultTableModel modelCTPT;
	private JScrollPane scrPT;
	private JTable tblCTPT;
	private PillButton btnHuyHang;
	private PillButton btnNhapKho;
	private JDateChooser dateTu;
	private JDateChooser dateDen;
	private JLabel lblTuNgay, lblDenNgay;

	private String txtSearchPlaceholderText = "Tìm kiếm theo mã phiếu, tên khách hàng hoặc SDT";

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DecimalFormat df = new DecimalFormat("#,##0.#'đ'");

	private final Color blueMint = new Color(180, 220, 240);
	private final Color pinkPastel = new Color(255, 200, 220);
	private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
	private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
	private final Color COLOR_PRIMARY = new Color(33, 150, 243);

	private PhieuTra_DAO phieuTraDAO = new PhieuTra_DAO();
	private ChiTietPhieuTra_DAO chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();
	private JSplitPane splitPane;
	private TableRowSorter<DefaultTableModel> sorterPT;
	private TableRowSorter<DefaultTableModel> sorterCTPT;
	private PillButton btnLamMoi;

	public QLTraHang_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		TaoHeader();
		TaoPnCenter();
		initTable();

		// Events
		txtSearch.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				filter();
			}

			public void removeUpdate(DocumentEvent e) {
				filter();
			}

			public void changedUpdate(DocumentEvent e) {
				filter();
			}

			private void filter() {
				resetChiTiet();
				String keyword = txtSearch.getText().trim();

				// Nếu text == placeholder → reset filter
				if (keyword.equals(txtSearchPlaceholderText) || keyword.isEmpty()) {
					sorterPT.setRowFilter(null);
					return;
				}

				try {
					sorterPT.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(keyword), 0, 1, 2));
				} catch (Exception ex) {
					sorterPT.setRowFilter(null);
				}
			}

		});

		// Fix rời focus bị rỗng bảng
		txtSearch.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String keyword = txtSearch.getText().trim();
				if (keyword.isEmpty()) {
					sorterPT.setRowFilter(null); // ⭐ FIX LỖI BỊ TRẮNG BẢNG
					resetChiTiet();
				}
			}
		});

		btnNhapKho.addActionListener(e -> capNhatTrangThai(1));
		btnHuyHang.addActionListener(e -> capNhatTrangThai(2));
		btnLamMoi.addActionListener(e -> lamMoiDuLieu());
	}

	private void TaoHeader() {
		// ===== HEADER =====
		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setLayout(null);
		add(pnHeader, BorderLayout.NORTH);

		txtSearch = TaoJtextNhanh.timKiem();
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtSearch.setBounds(10, 17, 420, 60);
		txtSearch.setBorder(new RoundedBorder(20));
		PlaceholderSupport.addPlaceholder(txtSearch, txtSearchPlaceholderText);

		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setFont(FONT_BOLD);
		btnXuatFile.setSize(120, 40);
		btnXuatFile.setLocation(946, 30);

		btnNhapKho = new PillButton("Nhập lại kho");
		btnNhapKho.setFont(FONT_BOLD);
		btnNhapKho.setBounds(1090, 26, 150, 40);

		btnHuyHang = new PillButton("Hủy hàng");
		btnHuyHang.setFont(FONT_BOLD);
		btnHuyHang.setBounds(1260, 26, 120, 40);

		btnLamMoi = new PillButton("Làm mới");
		btnLamMoi.setFont(FONT_BOLD);
		btnLamMoi.setBounds(1420, 26, 120, 40);
		pnHeader.add(btnLamMoi);

		TaoPanelLocTheoNgay();

		pnHeader.add(txtSearch);
		pnHeader.add(btnXuatFile);
		pnHeader.add(btnNhapKho);
		pnHeader.add(btnHuyHang);
		pnHeader.add(lblTuNgay);
		pnHeader.add(dateTu);
		pnHeader.add(lblDenNgay);
		pnHeader.add(dateDen);
	}

	private void TaoPanelLocTheoNgay() {
		lblTuNgay = new JLabel("Từ ngày:");
		lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblTuNgay.setBounds(478, 30, 90, 40);

		dateTu = new JDateChooser();
		dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateTu.setDateFormatString("dd/MM/yyyy");
		dateTu.setBounds(560, 35, 130, 30);

		lblDenNgay = new JLabel("Đến:");
		lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblDenNgay.setBounds(735, 30, 80, 40);

		dateDen = new JDateChooser();
		dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateDen.setDateFormatString("dd/MM/yyyy");
		dateDen.setBounds(781, 35, 130, 30);

		// Lọc theo ngày — mỗi khi chọn ngày
		dateTu.addPropertyChangeListener(evt -> {
			if ("date".equals(evt.getPropertyName())) {
				locPhieuTraTheoNgay();
			}
		});

		dateDen.addPropertyChangeListener(evt -> {
			if ("date".equals(evt.getPropertyName())) {
				locPhieuTraTheoNgay();
			}
		});
	}

	private void TaoPnCenter() {
		// ===== CENTER =====
		pnCenter = new JPanel(new BorderLayout());
		add(pnCenter, BorderLayout.CENTER);
	}

	// Loại bỏ dấu của từ
	private String normalize(String s) {
		return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		table.setRowHeight(28);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setSelectionBackground(new Color(180, 205, 230));
		table.setShowGrid(false);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);

		TableColumnModel m = table.getColumnModel();
		for (int i = 0; i < m.getColumnCount(); i++) {
			String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();
			if (col.contains("mã"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else if (col.contains("số lượng") || col.contains("sl"))
				m.getColumn(i).setCellRenderer(rightRenderer);
			else if (col.contains("giá") || col.contains("tiền"))
				m.getColumn(i).setCellRenderer(rightRenderer);
			else if (col.contains("ngày"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else
				m.getColumn(i).setCellRenderer(leftRenderer);
		}
		table.getTableHeader().setReorderingAllowed(false);
	}

	private void initTable() {
		String[] phieuTraCols = { "Mã PT", "Khách hàng", "SĐT", "Người trả", "Ngày lập", "Trạng thái",
				"Tổng tiền hoàn" };
		modelPT = new DefaultTableModel(phieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		// === BẢNG PHIẾU TRẢ ===
		tblPT = new JTable(modelPT) {
			@Override
			public String getToolTipText(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				if (row < 0 || col < 0)
					return null;

				// ⭐ Nếu là cột Khách hàng (index = 1)
				if (col == 1) {
					int modelRow = convertRowIndexToModel(row);

					String ten = getModel().getValueAt(modelRow, 1).toString();
					String sdt = getModel().getValueAt(modelRow, 2).toString(); // cột SĐT ẩn

					return ten + " – " + sdt; // ⭐ Tooltip KH + SĐT
				}

				// ⭐ Giữ nguyên tooltip mặc định cho các cột khác
				Object value = getValueAt(row, col);
				if (value == null)
					return null;

				TableCellRenderer renderer = getCellRenderer(row, col);
				Component comp = prepareRenderer(renderer, row, col);

				int pref = comp.getPreferredSize().width;
				int colW = getColumnModel().getColumn(col).getWidth();

				String text = value.toString();
				return (pref > colW - 6 || text.length() > 20) ? text : null;
			}
		};
		// Ẩn cột SĐT (index = 2)
		TableColumn colSDT = tblPT.getColumnModel().getColumn(2);
		colSDT.setMinWidth(0);
		colSDT.setMaxWidth(0);
		colSDT.setPreferredWidth(0);

		formatTable(tblPT);
		tblPT.setSelectionBackground(blueMint);
		tblPT.getTableHeader().setBackground(pinkPastel);

		// === SORTER ===
		sorterPT = new TableRowSorter<>(modelPT);
		tblPT.setRowSorter(sorterPT);
		scrPT = new JScrollPane(tblPT);

		// === BẢNG CHI TIẾT PHIẾU TRẢ ===
		String[] cTPhieuTraCols = { "Mã hoá đơn", "Mã lô", "Tên SP", "Hạn dùng", "Số lượng", "Giá bán", "Đơn vị tính",
				"Khuyến mãi", "Lý do trả", "Thành tiền", "Trạng thái" };

		modelCTPT = new DefaultTableModel(cTPhieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblCTPT = new JTable(modelCTPT) {
			@Override
			public String getToolTipText(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				if (row < 0 || col < 0)
					return null;
				Object value = getValueAt(row, col);
				if (value == null)
					return null;
				TableCellRenderer renderer = getCellRenderer(row, col);
				Component comp = prepareRenderer(renderer, row, col);
				int pref = comp.getPreferredSize().width;
				int colW = getColumnModel().getColumn(col).getWidth();
				String text = value.toString();
				return (pref > colW - 6 || text.length() > 20) ? text : null;
			}
		};
		formatTable(tblCTPT);
		tblCTPT.setSelectionBackground(pinkPastel);
		tblCTPT.getTableHeader().setBackground(blueMint);

		sorterCTPT = new TableRowSorter<>(modelCTPT);
		tblCTPT.setRowSorter(sorterCTPT);

		scrCTPT = new JScrollPane(tblCTPT);

		// === SPLIT PANE NGANG ===
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrPT, scrCTPT);
		splitPane.setDividerLocation(350);
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);

		pnCenter.add(splitPane);
		loadPhieuTraData();

		// === SỰ KIỆN CHỌN PHIẾU TRẢ ===
		tblPT.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblPT.getSelectedRow();
				if (row >= 0) {
					String maPT = modelPT.getValueAt(tblPT.convertRowIndexToModel(row), 0).toString();
					loadChiTietPhieuTra(maPT);

					// Reset nút khi đổi phiếu
					btnNhapKho.setEnabled(true);
					btnHuyHang.setEnabled(true);
				}
			}
		});

		// === SỰ KIỆN CHỌN DÒNG CHI TIẾT ===
		tblCTPT.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				int row = tblCTPT.getSelectedRow();
				if (row < 0)
					return;

				int modelRow = tblCTPT.convertRowIndexToModel(row);

				String trangThai = normalize(modelCTPT.getValueAt(modelRow, 9).toString());

				if (trangThai.contains("huy")) { // match mọi dạng: hủy, huỷ, huỹ, h u y . . .
					btnNhapKho.setEnabled(false);
					btnHuyHang.setEnabled(false);
					return;
				}

				if (trangThai.contains("nhap")) {
					btnNhapKho.setEnabled(false);
					btnHuyHang.setEnabled(true);
					return;
				}

				// Nếu là CHỜ → enable cả 2 nút
				btnNhapKho.setEnabled(true);
				btnHuyHang.setEnabled(true);
			}
		});
	}

	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(FONT_TEXT);
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setFont(FONT_BOLD);
		table.getTableHeader().setBackground(COLOR_PRIMARY);
		table.getTableHeader().setForeground(Color.WHITE);
		return table;
	}

	private void loadPhieuTraData() {

		// Xóa bảng
		modelPT.setRowCount(0);

		List<PhieuTra> dsPhieuTra = phieuTraDAO.layTatCaPhieuTra();

		for (PhieuTra pt : dsPhieuTra) {

			String maPT = pt.getMaPhieuTra();
			String tenKH = pt.getKhachHang() != null ? pt.getKhachHang().getTenKhachHang() : "Không rõ";
			String sdt = pt.getKhachHang() != null ? pt.getKhachHang().getSoDienThoai() : "";
			String nguoiTra = pt.getNhanVien().getTenNhanVien();
			String ngayLap = dtf.format(pt.getNgayLap());
			String trangThai = pt.isDaDuyet() ? "Đã xử lý" : "Chờ duyệt";
			String tongTien = df.format(pt.getTongTienHoan());

			// ĐÚNG thứ tự cột
			modelPT.addRow(new Object[] { maPT, tenKH, sdt, nguoiTra, ngayLap, trangThai, tongTien });
		}

		// Khi load danh sách, chưa chọn phiếu nào → khóa nút
		btnNhapKho.setEnabled(false);
		btnHuyHang.setEnabled(false);

		// Clear bảng chi tiết
		modelCTPT.setRowCount(0);
	}

	private void loadChiTietPhieuTra(String maPhieuTra) {

		// Xóa bảng CT
		modelCTPT.setRowCount(0);

		List<ChiTietPhieuTra> dsCT = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);

		if (dsCT.isEmpty()) {
			btnNhapKho.setEnabled(false);
			btnHuyHang.setEnabled(false);
			return;
		}

		boolean tatCaHuy = true;

		for (ChiTietPhieuTra ct : dsCT) {

			int tt = ct.getTrangThai();
			if (tt != 2)
				tatCaHuy = false;

			modelCTPT.addRow(new Object[] { ct.getChiTietHoaDon().getHoaDon().getMaHoaDon(),
					ct.getChiTietHoaDon().getLoSanPham().getMaLo(),
					ct.getChiTietHoaDon().getLoSanPham().getSanPham().getTenSanPham(),
					ct.getChiTietHoaDon().getLoSanPham().getHanSuDung()
							.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
					ct.getSoLuong(), df.format(ct.getChiTietHoaDon().getGiaBan()),
					ct.getChiTietHoaDon().getDonViTinh().getTenDonViTinh(),
					ct.getChiTietHoaDon().getKhuyenMai() == null ? "Không có"
							: ct.getChiTietHoaDon().getKhuyenMai().getTenKM(),
					ct.getLyDoChiTiet(), df.format(ct.getThanhTienHoan()), ct.getTrangThaiText() });
		}

		// Disable nút nếu tất cả chi tiết đều đã HUỶ
		if (tatCaHuy) {
			btnNhapKho.setEnabled(false);
			btnHuyHang.setEnabled(false);
		} else {
			btnNhapKho.setEnabled(true);
			btnHuyHang.setEnabled(true);
		}

		// Nếu phiếu đã duyệt → khóa nút
		PhieuTra pt = phieuTraDAO.timKiemPhieuTraBangMa(maPhieuTra);
		if (pt != null && pt.isDaDuyet()) {
			btnNhapKho.setEnabled(false);
			btnHuyHang.setEnabled(false);
		}
	}

	private void capNhatTrangThai(int trangThaiMoi) {

		// 1. KIỂM TRA PHIẾU TRẢ ĐƯỢC CHỌN
		int rowPT_View = tblPT.getSelectedRow();
		if (rowPT_View < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu trả!", "Chưa chọn phiếu",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Lấy index model của PT
		int rowPT_Model = tblPT.convertRowIndexToModel(rowPT_View);
		String maPhieuTra = modelPT.getValueAt(rowPT_Model, 0).toString();

		// 2. KIỂM TRA CHI TIẾT ĐƯỢC CHỌN
		int rowCT_View = tblCTPT.getSelectedRow();
		if (rowCT_View < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng chi tiết để cập nhật trạng thái!",
					"Chưa chọn chi tiết", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int rowCT_Model = tblCTPT.convertRowIndexToModel(rowCT_View);

		String maHoaDon = modelCTPT.getValueAt(rowCT_Model, 0).toString();
		String maLo = modelCTPT.getValueAt(rowCT_Model, 1).toString();
		int soLuongTra = Integer.parseInt(modelCTPT.getValueAt(rowCT_Model, 4).toString());

		// 3. LẤY TRẠNG THÁI HIỆN TẠI (CHUẨN – DÙNG ENTITY)
		List<ChiTietPhieuTra> dsCT = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);

		ChiTietPhieuTra ctSelected = null;
		for (ChiTietPhieuTra ct : dsCT) {
			String hd = ct.getChiTietHoaDon().getHoaDon().getMaHoaDon();
			String lo = ct.getChiTietHoaDon().getLoSanPham().getMaLo();
			if (hd.equals(maHoaDon) && lo.equals(maLo)) {
				ctSelected = ct;
				break;
			}
		}

		if (ctSelected == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy chi tiết tương ứng!", "Lỗi dữ liệu",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		int trangThaiHienTai = ctSelected.getTrangThai();

		// 4. CHẶN CẬP NHẬT DÒNG ĐÃ HỦY
		if (trangThaiHienTai == 2) {
			JOptionPane.showMessageDialog(this, "Chi tiết này đã ở trạng thái HỦY.\nKhông thể cập nhật lại!",
					"Không hợp lệ", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 5. XÁC NHẬN KHI HỦY
		if (trangThaiMoi == 2) {
			int confirm = JOptionPane.showConfirmDialog(this,
					"⚠ Bạn đang chọn HỦY HÀNG.\n\n" + "• Sau khi hủy, KHÔNG THỂ cập nhật lại.\n"
							+ "• Hệ thống sẽ tạo Phiếu Hủy (nếu có).\n\n" + "Bạn có muốn tiếp tục?",
					"Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (confirm != JOptionPane.YES_OPTION)
				return;
		}

		// 6. GỌI DAO CẬP NHẬT TRẠNG THÁI
		NhanVien nv = Session.getInstance().getTaiKhoanDangNhap().getNhanVien();
		String result = phieuTraDAO.capNhatTrangThai_GiaoDich(maPhieuTra, maHoaDon, maLo, nv, trangThaiMoi);

		if (result.equals("ERR")) {
			JOptionPane.showMessageDialog(this, "Cập nhật thất bại!\nKhông có thay đổi nào được lưu.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// 7. PHÂN TÁCH MÃ PHIẾU HỦY
		String maPhieuHuy = null;
		if (result.startsWith("OK|")) {
			maPhieuHuy = result.split("\\|")[1];
		}

		// 8. HIỂN THỊ THÔNG BÁO TƯƠNG ỨNG
		if (trangThaiMoi == 1) {

			LoSanPham_DAO loDAO = new LoSanPham_DAO();
			LoSanPham lo = loDAO.timLoTheoMa(maLo);

			String msg = """
					Nhập kho thành công!

					📦 Sản phẩm: %s
					➕ Tăng: +%d

					Tồn kho hiện tại: %d
					""".formatted(lo.getSanPham().getTenSanPham(), soLuongTra, lo.getSoLuongTon());

			JOptionPane.showMessageDialog(this, msg, "Nhập lại kho", JOptionPane.INFORMATION_MESSAGE);

		} else if (trangThaiMoi == 2) {

			if (maPhieuHuy != null) {
				JOptionPane.showMessageDialog(this, "Hủy hàng thành công!\nĐã tạo Phiếu Hủy: " + maPhieuHuy, "Đã hủy",
						JOptionPane.WARNING_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Hủy hàng thành công!", "Đã hủy", JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);
		}

		// 9. RELOAD VÀ GIỮ ĐÚNG SELECTION
		loadPhieuTraData();

		// Chọn lại phiếu trả
		int newPT_View = tblPT.convertRowIndexToView(rowPT_Model);
		tblPT.setRowSelectionInterval(newPT_View, newPT_View);

		loadChiTietPhieuTra(maPhieuTra);

		// Chọn lại dòng chi tiết
		if (tblCTPT.getRowCount() > 0) {
			int newCT_View = tblCTPT.convertRowIndexToView(rowCT_Model);
			if (newCT_View >= 0)
				tblCTPT.setRowSelectionInterval(newCT_View, newCT_View);
		}

		// Nếu chỉ còn 1 dòng trong chi tiết → cập nhật xong disable nút
		if (tblCTPT.getRowCount() == 0
				|| tblCTPT.getRowCount() == 1 && tblCTPT.getValueAt(0, 9).toString().toLowerCase().contains("hủy")) {
			btnNhapKho.setEnabled(false);
			btnHuyHang.setEnabled(false);
		}
	}

	private void locPhieuTraTheoNgay() {
		resetChiTiet();

		java.util.Date tu = dateTu.getDate();
		java.util.Date den = dateDen.getDate();

		sorterPT.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
			@Override
			public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {

				if (tu == null && den == null)
					return true;

				try {
					String ngayStr = entry.getStringValue(4); // cột ngày lập
					LocalDate ngay = LocalDate.parse(ngayStr, dtf);

					if (tu != null) {
						LocalDate ntu = new java.sql.Date(tu.getTime()).toLocalDate();
						if (ngay.isBefore(ntu))
							return false;
					}

					if (den != null) {
						LocalDate nden = new java.sql.Date(den.getTime()).toLocalDate();
						if (ngay.isAfter(nden))
							return false;
					}

					return true;

				} catch (Exception e) {
					return true;
				}
			}
		});
	}

	private void lamMoiDuLieu() {

		// 1. Xóa text tìm kiếm
		txtSearch.setText("");
		PlaceholderSupport.addPlaceholder(txtSearch, txtSearchPlaceholderText);

		// 2. Reset lọc sorter
		if (sorterPT != null)
			sorterPT.setRowFilter(null);

		// 3. Reset ngày lọc
		dateTu.setDate(null);
		dateDen.setDate(null);

		// 4. Load lại bảng phiếu trả
		loadPhieuTraData();

		// 5. Clear bảng chi tiết
		modelCTPT.setRowCount(0);

		// 6. Disable nút hành động
		btnNhapKho.setEnabled(false);
		btnHuyHang.setEnabled(false);
	}

	/** Reset bảng chi tiết và disable nút hành động */
	private void resetChiTiet() {
		modelCTPT.setRowCount(0);
		tblCTPT.clearSelection();
		tblPT.clearSelection();
		btnNhapKho.setEnabled(false);
		btnHuyHang.setEnabled(false);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Khung trống - clone base");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new QLTraHang_GUI());
			frame.setVisible(true);
		});
	}
}
