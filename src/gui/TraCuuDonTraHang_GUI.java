package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.ChiTietPhieuTra_DAO;
import dao.PhieuTra_DAO;
import entity.ChiTietHoaDon;
import entity.ChiTietPhieuTra;
import entity.KhachHang;
import entity.LoSanPham;
import entity.NhanVien;
import entity.PhieuTra;
import entity.SanPham;
import net.miginfocom.swing.MigLayout;

public class TraCuuDonTraHang_GUI extends JPanel {

	private static final String PLACEHOLDER_LY_DO_TRA = "Lý do trả...";
	private static final String PLACEHOLDER_MA_LO = "Mã lô...";
	private static final String PLACEHOLDER_TEN_SAN_PHAM = "Tên sản phẩm...";
	private static final String PLACEHOLDER_MA_HOA_DON = "Nhập mã hóa đơn...";
	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã phiếu, tên KH hoặc SĐT...";
	// DAO
	private final PhieuTra_DAO phieuTraDAO = new PhieuTra_DAO();
	private final ChiTietPhieuTra_DAO chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();

	// DATA
	private List<PhieuTra> allPhieuTra = new ArrayList<>();

	// HEADER - quick filter
	private JPanel pnHeader;
	private JPanel pnQuickFilter;
	private JPanel pnAdvancedFilter;

	private JTextField txtTimKiem;
	private JDateChooser dateTuNgay;
	private JDateChooser dateDenNgay;

	// Advanced filter controls
	private JComboBox<ComboItem> cbKhachHang;
	private JComboBox<ComboItem> cbNhanVien;
	private JTextField txtMaHoaDon;
	private JTextField txtSanPham;
	private JTextField txtMaLo;
	private JTextField txtLyDo;
	private JComboBox<String> cbHuongXuLy;

	private PillButton btnTimKiem;
	private PillButton btnLamMoi;
	private JButton btnToggleAdvanced;

	// TABLES
	private JPanel pnCenter;
	private JTable tblPhieuTra;
	private DefaultTableModel modelPhieuTra;

	private JTable tblChiTiet;
	private DefaultTableModel modelChiTiet;

	// FORMATTERS
	private final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
	private JPanel pnAdvancedWrap;

	public TraCuuDonTraHang_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		taoHeader();
		add(pnHeader, BorderLayout.NORTH);

		taoCenter();
		add(pnCenter, BorderLayout.CENTER);

		addEvents();
		reloadDataFromDAO(); // ✔ load data 1 lần
		applyFilter(); // ✔ lọc sau khi UI đã có đủ controls
	}

	// =====================================================================================
	// HEADER - QUICK + ADVANCED FILTER (MigLayout)
	// =====================================================================================
	private void taoHeader() {

		pnHeader = new JPanel(new MigLayout("ins 10, gapx 10, gapy 6, fillx", "[grow]"));
		pnHeader.setBackground(new Color(0xE3F2F5));
		pnHeader.putClientProperty("migLayout.hidemode", 3);

		// ======================= HÀNG 1 (SEARCH + NGÀY) ===========================

		JPanel rowSearch = new JPanel(new MigLayout("ins 0, gapx 10, gapy 0, fillx", "[grow][][grow][][grow][]"));
		rowSearch.setOpaque(false);

		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtTimKiem.setBorder(new RoundedBorder(18));

		dateTuNgay = new JDateChooser();
		dateTuNgay.setDateFormatString("dd/MM/yyyy");
		dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		dateTuNgay.setBorder(new RoundedBorder(18));

		dateDenNgay = new JDateChooser();
		dateDenNgay.setDateFormatString("dd/MM/yyyy");
		dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		dateDenNgay.setBorder(new RoundedBorder(18));

		btnToggleAdvanced = new JButton("Bộ lọc nâng cao \u25BC");

		rowSearch.add(txtTimKiem, "growx");
		rowSearch.add(new JLabel("Từ:"));
		rowSearch.add(dateTuNgay, "growx");
		rowSearch.add(new JLabel("Đến:"));
		rowSearch.add(dateDenNgay, "growx");
		rowSearch.add(btnToggleAdvanced);

		pnHeader.add(rowSearch, "growx, wrap");

		// ======================= HÀNG 2–3 (LỌC NÂNG CAO) ===========================

		pnAdvancedFilter = new JPanel(
				new MigLayout("ins 0, gapx 10, gapy 4, fillx, wrap 8", "[][grow][][grow][][grow][][grow]"));
		pnAdvancedFilter.setOpaque(false);
		pnAdvancedFilter.putClientProperty("migLayout.hidemode", 3);

		// Declare controls
		cbKhachHang = new JComboBox<>();
		cbKhachHang.setBorder(new RoundedBorder(18));
		cbNhanVien = new JComboBox<>();
		cbNhanVien.setBorder(new RoundedBorder(18));
		cbHuongXuLy = new JComboBox<>(new String[] { "Tất cả", "Chờ duyệt", "Nhập lại hàng", "Hủy hàng" });

		txtMaHoaDon = new JTextField();
		txtMaHoaDon.setBorder(new RoundedBorder(18));
		PlaceholderSupport.addPlaceholder(txtMaHoaDon, PLACEHOLDER_MA_HOA_DON);

		txtSanPham = new JTextField();
		txtSanPham.setBorder(new RoundedBorder(18));
		PlaceholderSupport.addPlaceholder(txtSanPham, PLACEHOLDER_TEN_SAN_PHAM);

		txtMaLo = new JTextField();
		txtMaLo.setBorder(new RoundedBorder(18));
		PlaceholderSupport.addPlaceholder(txtMaLo, PLACEHOLDER_MA_LO);

		txtLyDo = new JTextField();
		txtLyDo.setBorder(new RoundedBorder(18));
		PlaceholderSupport.addPlaceholder(txtLyDo, PLACEHOLDER_LY_DO_TRA);

		btnTimKiem = new PillButton("Tìm kiếm");
		btnLamMoi = new PillButton("Làm mới");

		// ======================= HÀNG 2 ===========================
		pnAdvancedFilter.add(new JLabel("Khách hàng:"));
		pnAdvancedFilter.add(cbKhachHang, "growx");

		pnAdvancedFilter.add(new JLabel("Nhân viên:"));
		pnAdvancedFilter.add(cbNhanVien, "growx");

		pnAdvancedFilter.add(new JLabel("Hướng xử lý:"));
		pnAdvancedFilter.add(cbHuongXuLy, "growx");

		pnAdvancedFilter.add(new JLabel("Mã hóa đơn:"));
		pnAdvancedFilter.add(txtMaHoaDon, "growx");

		// ======================= HÀNG 3 ===========================
		pnAdvancedFilter.add(new JLabel("Sản phẩm:"));
		pnAdvancedFilter.add(txtSanPham, "growx");

		pnAdvancedFilter.add(new JLabel("Mã lô:"));
		pnAdvancedFilter.add(txtMaLo, "growx");

		pnAdvancedFilter.add(new JLabel("Lý do trả:"));
		pnAdvancedFilter.add(txtLyDo, "growx");

		pnAdvancedFilter.add(btnTimKiem, "");
		pnAdvancedFilter.add(btnLamMoi, "");

		pnAdvancedWrap = new JPanel(new MigLayout("ins 0, gap 0, hidemode 3", "[grow]"));
		pnAdvancedWrap.setOpaque(false);
		pnAdvancedWrap.add(pnAdvancedFilter, "growx");
		pnAdvancedWrap.setVisible(false); // ẩn luôn cả wrap

		pnHeader.add(pnAdvancedWrap, "growx, wrap");
		pnAdvancedFilter.setVisible(false);
	}

	// =====================================================================================
	// CENTER - TABLES (JSplitPane)
	// =====================================================================================
	private void taoCenter() {
		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(new EmptyBorder(5, 10, 10, 10));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(350);
		splitPane.setResizeWeight(0.5);
		pnCenter.add(splitPane, BorderLayout.CENTER);

		// Top table: Danh sách phiếu trả
		String[] colPhieuTra = { "STT", "Mã phiếu trả", "Khách hàng", "Nhân viên", "Ngày lập", "Tổng tiền hoàn",
				"Trạng thái" };
		modelPhieuTra = new DefaultTableModel(colPhieuTra, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblPhieuTra = setupTable(modelPhieuTra);

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		tblPhieuTra.getColumnModel().getColumn(0).setCellRenderer(center);
		tblPhieuTra.getColumnModel().getColumn(1).setCellRenderer(center);
		tblPhieuTra.getColumnModel().getColumn(4).setCellRenderer(center);
		tblPhieuTra.getColumnModel().getColumn(5).setCellRenderer(right);

		tblPhieuTra.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
				if ("Đã duyệt".equals(value)) {
					lbl.setForeground(new Color(0x2E7D32));
				} else {
					lbl.setForeground(new Color(0xE65100));
				}
				return lbl;
			}
		});

		JScrollPane scrollPT = new JScrollPane(tblPhieuTra);
		scrollPT.setBorder(createTitledBorder("Danh sách phiếu trả hàng"));
		splitPane.setTopComponent(scrollPT);

		// Bottom table: Chi tiết
		String[] colChiTiet = { "STT", "Sản phẩm", "Lý do trả", "Số lượng", "Tiền hoàn", "Hướng xử lý" };
		modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblChiTiet = setupTable(modelChiTiet);

		tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center);
		tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(center);
		tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(right);
		tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(center);

		JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
		scrollChiTiet.setBorder(createTitledBorder("Chi tiết sản phẩm trả"));
		splitPane.setBottomComponent(scrollChiTiet);
	}

	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		table.setRowHeight(28);
		table.setSelectionBackground(new Color(0xC8E6C9));
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 14));
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);
		return table;
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 15), Color.DARK_GRAY);
	}

	// =====================================================================================
	// DATA LOADING & FILTERING
	// =====================================================================================

	private void reloadDataFromDAO() {
		allPhieuTra.clear();
		allPhieuTra.addAll(phieuTraDAO.layTatCaPhieuTra());

		// setup combos based on data
		fillComboKhachHangNhanVien();

		// default dates: từ ngày nhỏ nhất đến ngày hiện tại + 1
		if (!allPhieuTra.isEmpty()) {
			LocalDate minDate = allPhieuTra.stream().map(PhieuTra::getNgayLap).min(LocalDate::compareTo)
					.orElse(LocalDate.now());
			dateTuNgay.setDate(java.util.Date.from(minDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
		dateDenNgay.setDate(
				java.util.Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
	}

	private void fillComboKhachHangNhanVien() {
		cbKhachHang.removeAllItems();
		cbNhanVien.removeAllItems();
		cbKhachHang.addItem(new ComboItem(null, "Tất cả khách hàng"));
		cbNhanVien.addItem(new ComboItem(null, "Tất cả nhân viên"));

		Set<String> seenKH = new LinkedHashSet<>();
		Set<String> seenNV = new LinkedHashSet<>();

		for (PhieuTra pt : allPhieuTra) {
			KhachHang kh = pt.getKhachHang();
			if (kh != null && seenKH.add(kh.getMaKhachHang())) {
				cbKhachHang.addItem(
						new ComboItem(kh.getMaKhachHang(), kh.getMaKhachHang() + " - " + kh.getTenKhachHang()));
			}

			NhanVien nv = pt.getNhanVien();
			if (nv != null && seenNV.add(nv.getMaNhanVien())) {
				cbNhanVien.addItem(new ComboItem(nv.getMaNhanVien(), nv.getMaNhanVien() + " - " + nv.getTenNhanVien()));
			}
		}
	}

	private void applyFilter() {

		modelPhieuTra.setRowCount(0);

		// Lấy dữ liệu từ Quick Filter
		String text = getRealText(txtTimKiem, PLACEHOLDER_TIM_KIEM, true, false);

		LocalDate tuNgay = getDateOrNull(dateTuNgay);
		LocalDate denNgay = getDateOrNull(dateDenNgay);

		boolean advancedOn = pnAdvancedFilter.isVisible();

		// Lấy Advanced Filter
		ComboItem itemKH = (ComboItem) cbKhachHang.getSelectedItem();
		String maKH = itemKH != null ? itemKH.key : null;

		ComboItem itemNV = (ComboItem) cbNhanVien.getSelectedItem();
		String maNV = itemNV != null ? itemNV.key : null;

		String maHD = getRealText(txtMaHoaDon, PLACEHOLDER_MA_HOA_DON, false, true);
		String tenSP = getRealText(txtSanPham, PLACEHOLDER_TEN_SAN_PHAM, true, false);
		String maLo = getRealText(txtMaLo, PLACEHOLDER_MA_LO, false, true);
		String lyDo = getRealText(txtLyDo, PLACEHOLDER_LY_DO_TRA, true, false);

		String huongXuLy = (String) cbHuongXuLy.getSelectedItem();

		int stt = 1;

		for (PhieuTra pt : allPhieuTra) {

			// QUICK FILTER
			if (!matchQuickFilter(pt, text, tuNgay, denNgay))
				continue;

			// ADVANCED FILTER
			if (advancedOn) {
				if (!matchAdvancedFilter(pt, maKH, maNV, maHD, tenSP, maLo, lyDo, huongXuLy))
					continue;
			}

			// Add row
			modelPhieuTra.addRow(new Object[] { stt++, pt.getMaPhieuTra(),
					pt.getKhachHang() != null ? pt.getKhachHang().getTenKhachHang() : "",
					pt.getNhanVien() != null ? pt.getNhanVien().getTenNhanVien() : "", df.format(pt.getNgayLap()),
					nf.format(pt.getTongTienHoan()), pt.isDaDuyet() ? "Đã duyệt" : "Chờ duyệt" });
		}

		// Auto load chi tiết
		if (modelPhieuTra.getRowCount() > 0) {
			tblPhieuTra.setRowSelectionInterval(0, 0);
			loadChiTietForSelected();
		} else {
			modelChiTiet.setRowCount(0);
		}
	}

	private boolean matchQuickFilter(PhieuTra pt, String text, LocalDate tuNgay, LocalDate denNgay) {

		// ========================= TEXT SEARCH ==========================
		if (!text.isEmpty()) {
			String t = text.toLowerCase();

			// header info
			String sMaPT = safeLower(pt.getMaPhieuTra());
			String sTenKH = pt.getKhachHang() != null ? safeLower(pt.getKhachHang().getTenKhachHang()) : "";
			String sSDT = pt.getKhachHang() != null && pt.getKhachHang().getSoDienThoai() != null
					? safeLower(pt.getKhachHang().getSoDienThoai())
					: "";

			boolean matchHeader = sMaPT.contains(t) || sTenKH.contains(t) || sSDT.contains(t);

			// Nếu header không khớp → kiểm tra chi tiết
			if (!matchHeader) {
				List<ChiTietPhieuTra> ds = pt.getChiTietPhieuTraList();
				if (ds != null) {
					for (ChiTietPhieuTra ct : ds) {

						if (ct == null)
							continue;

						ChiTietHoaDon cthd = ct.getChiTietHoaDon();
						LoSanPham lo = cthd != null ? cthd.getLoSanPham() : null;
						SanPham sp = lo != null ? lo.getSanPham() : null;

						String sMaHD = cthd != null && cthd.getHoaDon() != null
								? safeLower(cthd.getHoaDon().getMaHoaDon())
								: "";
						String sTenSP = sp != null ? safeLower(sp.getTenSanPham()) : "";
						String sMaLo = lo != null ? safeLower(lo.getMaLo()) : "";
						String sLyDo = safeLower(ct.getLyDoChiTiet());

						if (sMaHD.contains(t) || sTenSP.contains(t) || sMaLo.contains(t) || sLyDo.contains(t)) {
							matchHeader = true;
							break;
						}
					}
				}
			}

			if (!matchHeader)
				return false;
		}

		// ========================= DATE FILTER ==========================
		LocalDate ngayLap = pt.getNgayLap();

		if (tuNgay != null && ngayLap.isBefore(tuNgay))
			return false;

		if (denNgay != null && ngayLap.isAfter(denNgay))
			return false;

		return true;
	}

	private boolean matchAdvancedFilter(PhieuTra pt, String maKH, String maNV, String maHD, String tenSP, String maLo,
			String lyDo, String huongXuLy) {

		if (maKH != null) {
			if (pt.getKhachHang() == null || !maKH.equals(pt.getKhachHang().getMaKhachHang()))
				return false;
		}

		if (maNV != null) {
			if (pt.getNhanVien() == null || !maNV.equals(pt.getNhanVien().getMaNhanVien()))
				return false;
		}

		boolean needDetailFilter = notEmpty(maHD) || notEmpty(tenSP) || notEmpty(maLo) || notEmpty(lyDo)
				|| (huongXuLy != null && !"Tất cả".equals(huongXuLy));

		if (!needDetailFilter) {
			return true;
		}

		List<ChiTietPhieuTra> dsCT = pt.getChiTietPhieuTraList();
		if (dsCT == null || dsCT.isEmpty()) {
			return false;
		}

		for (ChiTietPhieuTra ct : dsCT) {
			if (ct == null)
				continue;

			ChiTietHoaDon cthd = ct.getChiTietHoaDon();
			LoSanPham lo = cthd != null ? cthd.getLoSanPham() : null;
			SanPham sp = lo != null ? lo.getSanPham() : null;

			String maHDct = cthd != null && cthd.getHoaDon() != null ? safeUpper(cthd.getHoaDon().getMaHoaDon()) : "";
			String tenSPct = sp != null ? safeLower(sp.getTenSanPham()) : "";
			String maLoct = lo != null ? safeUpper(lo.getMaLo()) : "";
			String lyDoct = safeLower(ct.getLyDoChiTiet());
			String huongct = ct.getTrangThaiText(); // "Chờ duyệt" / "Nhập lại hàng" / "Huỷ hàng"

			if (notEmpty(maHD) && !maHDct.contains(maHD)) {
				continue;
			}
			if (notEmpty(tenSP) && !tenSPct.contains(tenSP)) {
				continue;
			}
			if (notEmpty(maLo) && !maLoct.contains(maLo)) {
				continue;
			}
			if (notEmpty(lyDo) && !lyDoct.contains(lyDo)) {
				continue;
			}
			if (huongXuLy != null && !"Tất cả".equals(huongXuLy) && !huongXuLy.equals(huongct)) {
				continue;
			}

			// Nếu đến đây nghĩa là chi tiết này thỏa mọi điều kiện
			return true;
		}

		return false;
	}

	private void loadChiTietForSelected() {
		int row = tblPhieuTra.getSelectedRow();
		if (row < 0) {
			modelChiTiet.setRowCount(0);
			return;
		}
		String maPT = (String) tblPhieuTra.getValueAt(row, 1);
		modelChiTiet.setRowCount(0);

		List<ChiTietPhieuTra> dsCT = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPT);

		int stt = 1;
		for (ChiTietPhieuTra ct : dsCT) {
			if (ct == null)
				continue;

			ChiTietHoaDon cthd = ct.getChiTietHoaDon();
			LoSanPham lo = cthd != null ? cthd.getLoSanPham() : null;
			SanPham sp = lo != null ? lo.getSanPham() : null;

			String tenSP = sp != null ? sp.getTenSanPham() : "";
			String lyDo = ct.getLyDoChiTiet();
			String soLuongText = String.valueOf(ct.getSoLuong());
			try {
				if (ct.getDonViTinh() != null && ct.getDonViTinh().getTenDonViTinh() != null) {
					soLuongText = ct.getSoLuong() + " " + ct.getDonViTinh().getTenDonViTinh();
				}
			} catch (Exception ignored) {
			}

			String tienHoanText = nf.format(ct.getThanhTienHoan());
			String huong = ct.getTrangThaiText();

			modelChiTiet.addRow(new Object[] { stt++, tenSP, lyDo, soLuongText, tienHoanText, huong });
		}
	}

	// =====================================================================================
	// EVENTS
	// =====================================================================================
	private void addEvents() {

		// Toggle bật tắt advanced filter
		btnToggleAdvanced.addActionListener(e -> {
			boolean show = !pnAdvancedWrap.isVisible();
			pnAdvancedWrap.setVisible(show);
			pnAdvancedFilter.setVisible(show);
			btnToggleAdvanced.setText(show ? "Bộ lọc nâng cao \u25B2" : "Bộ lọc nâng cao \u25BC");
			pnHeader.revalidate();
			pnHeader.repaint();
		});

		// Enter trong ô tìm kiếm
		txtTimKiem.addActionListener(e -> applyFilter());
		txtMaHoaDon.addActionListener(e -> applyFilter());
		txtSanPham.addActionListener(e -> applyFilter());
		txtMaLo.addActionListener(e -> applyFilter());
		txtLyDo.addActionListener(e -> applyFilter());

		// Auto lọc khi đổi ngày
		dateTuNgay.getDateEditor().addPropertyChangeListener(e -> applyFilter());
		dateDenNgay.getDateEditor().addPropertyChangeListener(e -> applyFilter());

		// Tìm kiếm
		btnTimKiem.addActionListener(e -> applyFilter());

		// Làm mới
		btnLamMoi.addActionListener(e -> lamMoiDuLieu());

		// click chọn phiếu
		tblPhieuTra.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				loadChiTietForSelected();
			}
		});
	}

	private void lamMoiDuLieu() {

		// Reset placeholder
		resetPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
		resetPlaceholder(txtMaHoaDon, PLACEHOLDER_MA_HOA_DON);
		resetPlaceholder(txtSanPham, PLACEHOLDER_TEN_SAN_PHAM);
		resetPlaceholder(txtMaLo, PLACEHOLDER_MA_LO);
		resetPlaceholder(txtLyDo, PLACEHOLDER_LY_DO_TRA);

		// Reset combobox
		cbKhachHang.setSelectedIndex(0);
		cbNhanVien.setSelectedIndex(0);
		cbHuongXuLy.setSelectedIndex(0);

		// Reset ngày
		if (!allPhieuTra.isEmpty()) {
			LocalDate minDate = allPhieuTra.stream().map(PhieuTra::getNgayLap).min(LocalDate::compareTo)
					.orElse(LocalDate.now());

			dateTuNgay.setDate(java.util.Date.from(minDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}

		dateDenNgay.setDate(
				java.util.Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));

		// Trong hàm applyFilter() có load data
		applyFilter();
	}

	// =====================================================================================
	// HELPERS
	// =====================================================================================
	private LocalDate getDateOrNull(JDateChooser chooser) {
		java.util.Date d = chooser.getDate();
		if (d == null)
			return null;
		return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private String safeLower(String s) {
		return s == null ? "" : s.toLowerCase().trim();
	}

	private String safeUpper(String s) {
		return s == null ? "" : s.toUpperCase().trim();
	}

	private boolean notEmpty(String s) {
		return s != null && !s.trim().isEmpty();
	}

	// Combo item for combos KH / NV
	private static class ComboItem {
		final String key;
		final String label;

		ComboItem(String key, String label) {
			this.key = key;
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	// Kiểm tra xem text trong ô có phải placeholder hay không
	private boolean isPlaceholder(JTextField txt, String placeholder) {
		return txt.getForeground().equals(Color.GRAY) && txt.getText().equals(placeholder);
	}

	// Reset placeholder đúng chuẩn (dùng khi bấm Làm mới)
	private void resetPlaceholder(JTextField txt, String placeholder) {
		txt.setForeground(Color.GRAY);
		txt.setText(placeholder);
	}

	// Lấy text thực sự (nếu là placeholder thì trả về rỗng)
	private String getRealText(JTextField txt, String placeholder, boolean toLower, boolean toUpper) {
		if (isPlaceholder(txt, placeholder))
			return "";
		String s = txt.getText().trim();
		if (toLower)
			return s.toLowerCase();
		if (toUpper)
			return s.toUpperCase();
		return s;
	}

	// =====================================================================================
	// TEST MAIN
	// =====================================================================================
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}
			JFrame frame = new JFrame("Tra cứu đơn trả hàng");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1400, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new TraCuuDonTraHang_GUI());
			frame.setVisible(true);
		});
	}
}
