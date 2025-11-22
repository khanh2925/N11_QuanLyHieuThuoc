package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
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

	// ===== VÙNG KHAI BÁO THÀNH PHẦN GIAO DIỆN =====
	private JPanel pnCenter;
	private JPanel pnHeader;
	private JPanel pnAction; // panel chứa các nút hành động phía dưới

	private JButton btnXuatFile;
	private PillButton btnLamMoi;
	private PillButton btnNhapKho;
	private PillButton btnHuyHang;

	private JTextField txtSearch;
	private JDateChooser dateTu;
	private JDateChooser dateDen;

	private DefaultTableModel modelPT;
	private JTable tblPT;
	private JScrollPane scrPT;

	private DefaultTableModel modelCTPT;
	private JTable tblCTPT;
	private JScrollPane scrCTPT;

	private JSplitPane splitPane;

	// ===== FORMAT & MÀU SẮC =====
	private final String txtSearchPlaceholderText = "Tìm kiếm theo mã phiếu, tên khách hàng hoặc SDT";
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DecimalFormat df = new DecimalFormat("#,##0.#'đ'");

	// màu sử dụng lại cho selection
	private final Color selectionTop = new Color(204, 232, 255);
	private final Color selectionBottom = new Color(255, 230, 240);

	// ===== DAO, SORTER =====
	private PhieuTra_DAO phieuTraDAO = new PhieuTra_DAO();
	private ChiTietPhieuTra_DAO chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();
	private TableRowSorter<DefaultTableModel> sorterPT;
	private TableRowSorter<DefaultTableModel> sorterCTPT;

	public QLTraHang_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 850));

		// =========== HEADER =========== //
		pnHeader = new JPanel(null);
		pnHeader.setPreferredSize(new Dimension(1537, 80));
		pnHeader.setBackground(new Color(245, 250, 252));
		add(pnHeader, BorderLayout.NORTH);

		txtSearch = TaoJtextNhanh.timKiem();
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtSearch.setBounds(10, 18, 380, 40);
		txtSearch.setBorder(new RoundedBorder(20));
		PlaceholderSupport.addPlaceholder(txtSearch, txtSearchPlaceholderText);

		JLabel lblTuNgay = new JLabel("Từ ngày:");
		lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblTuNgay.setBounds(410, 18, 80, 40);

		dateTu = new JDateChooser();
		dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dateTu.setDateFormatString("dd/MM/yyyy");
		dateTu.setBounds(470, 23, 130, 30);

		JLabel lblDenNgay = new JLabel("Đến:");
		lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblDenNgay.setBounds(620, 18, 50, 40);

		dateDen = new JDateChooser();
		dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dateDen.setDateFormatString("dd/MM/yyyy");
		dateDen.setBounds(660, 23, 130, 30);

		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnXuatFile.setBounds(820, 23, 120, 35);

		btnLamMoi = new PillButton("Làm mới");
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnLamMoi.setBounds(950, 23, 120, 35);

		pnHeader.add(txtSearch);
		pnHeader.add(lblTuNgay);
		pnHeader.add(dateTu);
		pnHeader.add(lblDenNgay);
		pnHeader.add(dateDen);
		pnHeader.add(btnXuatFile);
		pnHeader.add(btnLamMoi);

		// Lọc theo ngày khi chọn date
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

		// =========== CENTER =========== //
		pnCenter = new JPanel(new BorderLayout());
		add(pnCenter, BorderLayout.CENTER);

		initTable(); // khởi tạo bảng + splitpane + panel nút

		// ===== SỰ KIỆN TÌM KIẾM TEXT =====
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
					sorterPT.setRowFilter(null);
					resetChiTiet();
				}
			}
		});

		// ===== SỰ KIỆN NÚT =====
		btnNhapKho.addActionListener(e -> capNhatTrangThai(1));
		btnHuyHang.addActionListener(e -> capNhatTrangThai(2));
		btnLamMoi.addActionListener(e -> lamMoiDuLieu());
	}

	// Loại bỏ dấu của từ (phục vụ so sánh trạng thái)
	private String normalize(String s) {
		return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}

	/**
	 * Thiết lập font, căn lề, màu header cho table.
	 */
	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		table.getTableHeader().setOpaque(true);
		table.getTableHeader().setBackground(new Color(13, 139, 217)); // xanh giống ảnh
		table.getTableHeader().setForeground(Color.WHITE);

		table.setRowHeight(26);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setShowGrid(false);
		table.setFillsViewportHeight(true);

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
		// ====== MODEL & BẢNG PHIẾU TRẢ ======
		String[] phieuTraCols = { "Mã PT", "Khách hàng", "SĐT", "Người trả", "Ngày lập", "Trạng thái",
				"Tổng tiền hoàn" };

		modelPT = new DefaultTableModel(phieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblPT = new JTable(modelPT) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (!isRowSelected(row)) {
					// striping dòng
					if (row % 2 == 0) {
						c.setBackground(Color.WHITE);
					} else {
						c.setBackground(new Color(242, 248, 252));
					}
				} else {
					c.setBackground(selectionTop);
				}
				return c;
			}

			@Override
			public String getToolTipText(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				int col = columnAtPoint(e.getPoint());
				if (row < 0 || col < 0)
					return null;

				if (col == 1) { // cột khách hàng
					int modelRow = convertRowIndexToModel(row);
					String ten = getModel().getValueAt(modelRow, 1).toString();
					String sdt = getModel().getValueAt(modelRow, 2).toString();
					return ten + " – " + sdt;
				}

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

		// Ẩn cột SĐT trong bảng nhưng vẫn giữ cho logic filter
		TableColumn colSDT = tblPT.getColumnModel().getColumn(2);
		colSDT.setMinWidth(0);
		colSDT.setMaxWidth(0);
		colSDT.setPreferredWidth(0);

		formatTable(tblPT);

		sorterPT = new TableRowSorter<>(modelPT);
		tblPT.setRowSorter(sorterPT);

		scrPT = new JScrollPane(tblPT);
		scrPT.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(200, 210, 220)));

		// Panel tiêu đề + bảng PT (giống thanh "Danh sách phiếu trả hàng")
		JPanel pnTopTable = new JPanel(new BorderLayout());
		JLabel lblTopTitle = new JLabel("Danh sách phiếu trả hàng");
		lblTopTitle.setOpaque(true);
		lblTopTitle.setBackground(new Color(13, 139, 217));
		lblTopTitle.setForeground(Color.WHITE);
		lblTopTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblTopTitle.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));

		pnTopTable.add(lblTopTitle, BorderLayout.NORTH);
		pnTopTable.add(scrPT, BorderLayout.CENTER);

		// ====== MODEL & BẢNG CHI TIẾT PHIẾU TRẢ ======
		String[] cTPhieuTraCols = { "Mã hoá đơn", "Mã lô", "Tên SP", "Hạn dùng", "Số lượng", "Giá bán", "Đơn vị tính",
				"Khuyến mãi", "Lý do trả", "Thành tiền", "Trạng thái", "Mã DVT" };

		modelCTPT = new DefaultTableModel(cTPhieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblCTPT = new JTable(modelCTPT) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (!isRowSelected(row)) {
					if (row % 2 == 0) {
						c.setBackground(Color.WHITE);
					} else {
						c.setBackground(new Color(252, 246, 248));
					}
				} else {
					c.setBackground(selectionBottom);
				}
				return c;
			}

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

		TableColumn colMaDVT = tblCTPT.getColumnModel().getColumn(11);
		colMaDVT.setMinWidth(0);
		colMaDVT.setMaxWidth(0);
		colMaDVT.setPreferredWidth(0);

		formatTable(tblCTPT);

		sorterCTPT = new TableRowSorter<>(modelCTPT);
		tblCTPT.setRowSorter(sorterCTPT);

		scrCTPT = new JScrollPane(tblCTPT);
		scrCTPT.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, new Color(200, 210, 220)));

		JPanel pnBottomTable = new JPanel(new BorderLayout());
		JLabel lblBottomTitle = new JLabel("Chi tiết sản phẩm trả");
		lblBottomTitle.setOpaque(true);
		lblBottomTitle.setBackground(new Color(13, 139, 217));
		lblBottomTitle.setForeground(Color.WHITE);
		lblBottomTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
		lblBottomTitle.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));

		pnBottomTable.add(lblBottomTitle, BorderLayout.NORTH);
		pnBottomTable.add(scrCTPT, BorderLayout.CENTER);

		// ====== SPLITPANE TRÊN / DƯỚI ======
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopTable, pnBottomTable);
		splitPane.setResizeWeight(0.55); // 55% trên, 45% dưới
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(6);

		pnCenter.add(splitPane, BorderLayout.CENTER);

		// ====== PANEL NÚT DƯỚI BẢNG CHI TIẾT ======
		pnAction = new JPanel(new BorderLayout());
		pnAction.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		JPanel pnBtnRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		btnNhapKho = new PillButton("Nhập lại kho");
		btnNhapKho.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnNhapKho.setPreferredSize(new Dimension(150, 36));

		btnHuyHang = new PillButton("Hủy hàng");
		btnHuyHang.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btnHuyHang.setPreferredSize(new Dimension(120, 36));

		pnBtnRight.add(btnNhapKho);
		pnBtnRight.add(btnHuyHang);

		pnAction.add(pnBtnRight, BorderLayout.EAST);
		pnCenter.add(pnAction, BorderLayout.SOUTH);

		// ====== LOAD DATA BAN ĐẦU ======
		loadPhieuTraData();

		// ====== SỰ KIỆN CHỌN DÒNG TRONG BẢNG ======
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

		tblCTPT.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblCTPT.getSelectedRow();
				if (row < 0)
					return;

				int modelRow = tblCTPT.convertRowIndexToModel(row);

				// Lưu ý: giữ nguyên chỉ số cột 9 như code gốc để không đổi logic
				String trangThai = normalize(modelCTPT.getValueAt(modelRow, 9).toString());

				if (trangThai.contains("huy")) {
					btnNhapKho.setEnabled(false);
					btnHuyHang.setEnabled(false);
					return;
				}

				if (trangThai.contains("nhap")) {
					btnNhapKho.setEnabled(false);
					btnHuyHang.setEnabled(true);
					return;
				}

				btnNhapKho.setEnabled(true);
				btnHuyHang.setEnabled(true);
			}
		});
	}

	// ====== CÁC HÀM XỬ LÝ LOGIC – GIỮ NGUYÊN NỘI DUNG ======

	private void loadPhieuTraData() {
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

			modelPT.addRow(new Object[] { maPT, tenKH, sdt, nguoiTra, ngayLap, trangThai, tongTien });
		}

		btnNhapKho.setEnabled(false);
		btnHuyHang.setEnabled(false);

		modelCTPT.setRowCount(0);
	}

	private void loadChiTietPhieuTra(String maPhieuTra) {
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
					// ⭐ Ưu tiên DVT trong ChiTietPhieuTra, fallback về DVT trong CTHD
					ct.getDonViTinh() != null ? ct.getDonViTinh().getTenDonViTinh()
							: ct.getChiTietHoaDon().getDonViTinh().getTenDonViTinh(),
					ct.getChiTietHoaDon().getKhuyenMai() == null ? "Không có"
							: ct.getChiTietHoaDon().getKhuyenMai().getTenKM(),
					ct.getLyDoChiTiet(), df.format(ct.getThanhTienHoan()), ct.getTrangThaiText(),
					// ⭐ Cột 11: Mã DVT (dùng để xác định đúng dòng)
					ct.getDonViTinh() != null ? ct.getDonViTinh().getMaDonViTinh()
							: ct.getChiTietHoaDon().getDonViTinh().getMaDonViTinh() });
		}

		if (tatCaHuy) {
			btnNhapKho.setEnabled(false);
			btnHuyHang.setEnabled(false);
		} else {
			btnNhapKho.setEnabled(true);
			btnHuyHang.setEnabled(true);
		}

		PhieuTra pt = phieuTraDAO.timKiemPhieuTraBangMa(maPhieuTra);
		if (pt != null && pt.isDaDuyet()) {
			btnNhapKho.setEnabled(false);
			btnHuyHang.setEnabled(false);
		}
	}

	private void capNhatTrangThai(int trangThaiMoi) {

		int rowPT_View = tblPT.getSelectedRow();
		if (rowPT_View < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu trả!", "Chưa chọn phiếu",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		int rowPT_Model = tblPT.convertRowIndexToModel(rowPT_View);
		String maPhieuTra = modelPT.getValueAt(rowPT_Model, 0).toString();

		int rowCT_View = tblCTPT.getSelectedRow();
		if (rowCT_View < 0) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng chi tiết để cập nhật trạng thái!",
					"Chưa chọn chi tiết", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int rowCT_Model = tblCTPT.convertRowIndexToModel(rowCT_View);

		String maHoaDon = modelCTPT.getValueAt(rowCT_Model, 0).toString();
		String maLo = modelCTPT.getValueAt(rowCT_Model, 1).toString();
		String maDonViTinh = modelCTPT.getValueAt(rowCT_Model, 11).toString();
		int soLuongTra = Integer.parseInt(modelCTPT.getValueAt(rowCT_Model, 4).toString());

		List<ChiTietPhieuTra> dsCT = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);

		ChiTietPhieuTra ctSelected = null;
		for (ChiTietPhieuTra ct : dsCT) {
			String hd = ct.getChiTietHoaDon().getHoaDon().getMaHoaDon();
			String lo = ct.getChiTietHoaDon().getLoSanPham().getMaLo();
			String dvt = ct.getDonViTinh() != null ? ct.getDonViTinh().getMaDonViTinh()
					: (ct.getChiTietHoaDon().getDonViTinh() != null
							? ct.getChiTietHoaDon().getDonViTinh().getMaDonViTinh()
							: null);

			if (hd.equals(maHoaDon) && lo.equals(maLo) && maDonViTinh.equals(dvt)) {
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

		if (trangThaiHienTai == 2) {
			JOptionPane.showMessageDialog(this, "Chi tiết này đã ở trạng thái HỦY.\nKhông thể cập nhật lại!",
					"Không hợp lệ", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (trangThaiMoi == 2) {
			int confirm = JOptionPane.showConfirmDialog(this,
					"⚠ Bạn đang chọn HỦY HÀNG.\n\n" + "• Sau khi hủy, KHÔNG THỂ cập nhật lại.\n"
							+ "• Hệ thống sẽ tạo Phiếu Hủy (nếu có).\n\n" + "Bạn có muốn tiếp tục?",
					"Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (confirm != JOptionPane.YES_OPTION)
				return;
		}

		NhanVien nv = Session.getInstance().getTaiKhoanDangNhap().getNhanVien();
		String lyDoNhap = null;

		if (trangThaiMoi == 2) { // chỉ nhập khi hủy hàng

			// 🔥 Lấy lý do trả hàng ban đầu từ chi tiết đang chọn
			String lyDoMacDinh = ctSelected.getLyDoChiTiet();
			if (lyDoMacDinh == null || lyDoMacDinh.trim().isEmpty()) {
				lyDoMacDinh = "";
			}

			// 🔥 Set mặc định lý do vào input dialog để nhân viên sửa/xoá theo ý
			lyDoNhap = (String) JOptionPane.showInputDialog(this, "Nhập lý do hủy hàng:", "Lý do hủy",
					JOptionPane.PLAIN_MESSAGE, null, null, lyDoMacDinh);

			if (lyDoNhap == null)
				return; // user bấm cancel

			lyDoNhap = lyDoNhap.trim();
		}

		String result = phieuTraDAO.capNhatTrangThai_GiaoDich(maPhieuTra, maHoaDon, maLo, maDonViTinh, nv, trangThaiMoi,
				lyDoNhap);

		if (result.equals("ERR")) {
			JOptionPane.showMessageDialog(this, "Cập nhật thất bại!\nKhông có thay đổi nào được lưu.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		String maPhieuHuy = null;
		if (result.startsWith("OK|")) {
			maPhieuHuy = result.split("\\|")[1];
		}

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

		// reload & giữ selection
		loadPhieuTraData();

		int newPT_View = tblPT.convertRowIndexToView(rowPT_Model);
		tblPT.setRowSelectionInterval(newPT_View, newPT_View);
		loadChiTietPhieuTra(maPhieuTra);

		if (tblCTPT.getRowCount() > 0) {
			int newCT_View = tblCTPT.convertRowIndexToView(rowCT_Model);
			if (newCT_View >= 0)
				tblCTPT.setRowSelectionInterval(newCT_View, newCT_View);
		}

		if (tblCTPT.getRowCount() == 0
				|| (tblCTPT.getRowCount() == 1 && tblCTPT.getValueAt(0, 9).toString().toLowerCase().contains("hủy"))) {
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
		txtSearch.setText("");
		PlaceholderSupport.addPlaceholder(txtSearch, txtSearchPlaceholderText);

		if (sorterPT != null)
			sorterPT.setRowFilter(null);

		dateTu.setDate(null);
		dateDen.setDate(null);

		loadPhieuTraData();
		modelCTPT.setRowCount(0);

		btnNhapKho.setEnabled(false);
		btnHuyHang.setEnabled(false);
	}

	private void resetChiTiet() {
		modelCTPT.setRowCount(0);
		if (tblCTPT != null)
			tblCTPT.clearSelection();
		if (tblPT != null)
			tblPT.clearSelection();
		btnNhapKho.setEnabled(false);
		btnHuyHang.setEnabled(false);
	}

	// ====== MAIN TEST ======
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản lý trả hàng");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new QLTraHang_GUI());
			frame.setVisible(true);
		});
	}
}
