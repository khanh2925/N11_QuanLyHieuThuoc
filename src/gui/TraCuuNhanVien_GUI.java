package gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.toedter.calendar.JDateChooser;

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.LichSuNhanVien_DAO;
import dao.NhanVien_DAO;
import entity.NhanVien;
import net.miginfocom.swing.MigLayout;

public class TraCuuNhanVien_GUI extends JPanel {

	// DAO
	private final NhanVien_DAO nvDAO = new NhanVien_DAO();
	private final LichSuNhanVien_DAO lichSuDAO = new LichSuNhanVien_DAO();
	private ArrayList<NhanVien> danhSachGoc = new ArrayList<>();

	// HEADER
	private JPanel pnHeader;
	private JPanel pnAdvancedWrap;
	private JPanel pnAdvancedFilter;
	private JButton btnToggleAdvanced;

	private JTextField txtTimKiem;
	private JComboBox<String> cbChucVu;
	private JComboBox<String> cbCaLam;
	private JComboBox<String> cbTrangThai;

	private JDateChooser dateTuNgay;
	private JDateChooser dateDenNgay;

	private PillButton btnTim;
	private PillButton btnMoi;

	// CENTER
	private JPanel pnCenter;
	private JTable tblNhanVien;
	private DefaultTableModel modelNhanVien;

	private JTabbedPane tabChiTiet;
	private JTable tblLichSuBan;
//	private JTable tblLichSuTra;
//	private JTable tblLichSuHuy;

	private DefaultTableModel modelBan;
//	private DefaultTableModel modelTra;
//	private DefaultTableModel modelHuy;

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DecimalFormat df = new DecimalFormat("#,##0.#'đ'");

	public TraCuuNhanVien_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		taoHeader();
		add(pnHeader, BorderLayout.NORTH);

		taoCenter();
		add(pnCenter, BorderLayout.CENTER);

		loadDuLieuNhanVien();
		addEvents();
		SwingUtilities.invokeLater(() -> applyFilter());
	}

	// ================================================================
	// HEADER – SEARCH + ADV FILTER (MigLayout)
	// ================================================================
	private void taoHeader() {

		pnHeader = new JPanel(new MigLayout("ins 10, gapx 10, gapy 6, fillx", "[grow]"));
		pnHeader.setBackground(new Color(0xE3F2F5));
		pnHeader.putClientProperty("migLayout.hidemode", 3);

		// ---------- HÀNG 1 ----------
		JPanel row1 = new JPanel(new MigLayout("ins 0, gapx 10, fillx", "[grow][][grow][][grow][]"));
		row1.setOpaque(false);

		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã, tên, SĐT...");
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		txtTimKiem.setBorder(new RoundedBorder(18));

		dateTuNgay = new JDateChooser();
		dateTuNgay.setDateFormatString("dd/MM/yyyy");
		dateTuNgay.setBorder(new RoundedBorder(18));

		dateDenNgay = new JDateChooser();
		dateDenNgay.setDateFormatString("dd/MM/yyyy");
		dateDenNgay.setBorder(new RoundedBorder(18));

		btnToggleAdvanced = new JButton("Bộ lọc nâng cao \u25BC");

		row1.add(txtTimKiem, "growx");
		row1.add(new JLabel("Từ:"));
		row1.add(dateTuNgay, "growx");
		row1.add(new JLabel("Đến:"));
		row1.add(dateDenNgay, "growx");
		row1.add(btnToggleAdvanced);

		pnHeader.add(row1, "growx, wrap");

		// ---------- HÀNG 2–3 (ADV FILTER) ----------
		pnAdvancedFilter = new JPanel(
				new MigLayout("ins 0, gapx 10, gapy 4, fillx, wrap 6", "[][grow][][grow][][grow]"));
		pnAdvancedFilter.setOpaque(false);

		cbChucVu = new JComboBox<>(new String[] { "Tất cả", "Quản lý", "Nhân viên" });
		cbChucVu.setBorder(new RoundedBorder(18));
		cbCaLam = new JComboBox<>(new String[] { "Tất cả", "Sáng", "Chiều", "Tối" });
		cbCaLam.setBorder(new RoundedBorder(18));
		cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Đang làm", "Đã nghỉ" });
		cbTrangThai.setBorder(new RoundedBorder(18));

		btnTim = new PillButton("Tìm kiếm");
		btnMoi = new PillButton("Làm mới");

		pnAdvancedFilter.add(new JLabel("Chức vụ:"));
		pnAdvancedFilter.add(cbChucVu, "growx");
		pnAdvancedFilter.add(new JLabel("Ca làm:"));
		pnAdvancedFilter.add(cbCaLam, "growx");
		pnAdvancedFilter.add(new JLabel("Trạng thái:"));
		pnAdvancedFilter.add(cbTrangThai, "growx");

		pnAdvancedFilter.add(btnTim);
		pnAdvancedFilter.add(btnMoi);

		pnAdvancedWrap = new JPanel(new MigLayout("ins 0, gap 0, hidemode 3", "[grow]"));
		pnAdvancedWrap.setOpaque(false);
		pnAdvancedWrap.add(pnAdvancedFilter, "growx");

		pnAdvancedWrap.setVisible(false);
		pnAdvancedFilter.setVisible(false);

		pnHeader.add(pnAdvancedWrap, "growx, wrap");
	}

	// ================================================================
	// CENTER – MASTER (table NV) + DETAIL (3 tabs)
	// ================================================================
	private void taoCenter() {

		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
		pnCenter.setBackground(Color.WHITE);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(380);
		split.setResizeWeight(0.5);
		pnCenter.add(split, BorderLayout.CENTER);

		// --- BẢNG NHÂN VIÊN ---
		String[] colNV = { "STT", "Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Chức vụ", "Ca", "Trạng thái" };
		modelNhanVien = new DefaultTableModel(colNV, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblNhanVien = setupTable(modelNhanVien);

		JScrollPane scrollNV = new JScrollPane(tblNhanVien);
		scrollNV.setBorder(createBorder("Danh sách nhân viên"));
		split.setTopComponent(scrollNV);

		// --- TABBED PANE ---
		tabChiTiet = new JTabbedPane();
		tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

		split.setBottomComponent(tabChiTiet);

		taoTabsChiTiet();
	}

	private void taoTabsChiTiet() {

		// TAB 1 – BÁN HÀNG
		modelBan = new DefaultTableModel(new String[] { "STT", "Mã hóa đơn", "Ngày lập", "Khách hàng", "Tổng tiền" },
				0);
		tblLichSuBan = setupTable(modelBan);

		tabChiTiet.add("Lịch sử bán hàng", new JScrollPane(tblLichSuBan));

//		// TAB 2 – TRẢ HÀNG
//		modelTra = new DefaultTableModel(new String[] { "STT", "Mã phiếu trả", "Ngày lập", "Khách hàng", "Tiền hoàn" },
//				0);
//		tblLichSuTra = setupTable(modelTra);
//		tabChiTiet.add("Lịch sử duyệt trả", new JScrollPane(tblLichSuTra));
		tabChiTiet.add("Lịch sử duyệt trả", new JLabel("Đang cập nhật...", JLabel.CENTER));
//
//		// TAB 3 – HỦY HÀNG
//		modelHuy = new DefaultTableModel(new String[] { "STT", "Mã phiếu hủy", "Ngày lập", "Tổng giá trị" },
//				0);
//		tblLichSuHuy = setupTable(modelHuy);
//		tabChiTiet.add("Lịch sử hủy hàng", new JScrollPane(tblLichSuHuy));
		tabChiTiet.add("Lịch sử hủy hàng", new JLabel("Đang cập nhật...", JLabel.CENTER));
	}

	// ================================================================
	// DATA LOADING
	// ================================================================
	private void loadDuLieuNhanVien() {
		danhSachGoc = nvDAO.layTatCaNhanVien();
	}

	// ================================================================
	// FILTERING
	// ================================================================
	private void applyFilter() {

		if (danhSachGoc == null || danhSachGoc.isEmpty()) {
			modelNhanVien.setRowCount(0);
			return;
		}

		// FIX PLACEHOLDER
		String placeholder = "Tìm theo mã, tên, SĐT...";
		String raw = txtTimKiem.getText().trim();
		String text = raw.equals(placeholder) ? "" : raw.toLowerCase();

		String cv = cbChucVu.getSelectedItem() + "";
		String ca = cbCaLam.getSelectedItem() + "";
		String tt = cbTrangThai.getSelectedItem() + "";

		modelNhanVien.setRowCount(0);
		int stt = 1;

		for (NhanVien nv : danhSachGoc) {

			// TEXT FILTER
			if (!text.isEmpty()) {
				String combined = (nv.getMaNhanVien() + nv.getTenNhanVien() + nv.getSoDienThoai()).toLowerCase();
				if (!combined.contains(text))
					continue;
			}

			// CHỨC VỤ
			String chucVuNv = nv.isQuanLy() ? "Quản lý" : "Nhân viên";
			if (!cv.equals("Tất cả") && !cv.equals(chucVuNv))
				continue;

			// CA
			String caNv = nv.getCaLam() == 1 ? "Sáng" : nv.getCaLam() == 2 ? "Chiều" : "Tối";
			if (!ca.equals("Tất cả") && !ca.equals(caNv))
				continue;

			// TRẠNG THÁI
			String ttNv = nv.isTrangThai() ? "Đang làm" : "Đã nghỉ";
			if (!tt.equals("Tất cả") && !tt.equals(ttNv))
				continue;

			modelNhanVien.addRow(new Object[] { stt++, nv.getMaNhanVien(), nv.getTenNhanVien(),
					nv.isGioiTinh() ? "Nam" : "Nữ", nv.getNgaySinh() != null ? dtf.format(nv.getNgaySinh()) : "",
					nv.getSoDienThoai(), chucVuNv, caNv, ttNv });
		}

		if (tblNhanVien.getRowCount() > 0)
			tblNhanVien.setRowSelectionInterval(0, 0);
	}

	// Trả về LocalDate hoặc null từ JDateChooser
	private LocalDate getDateOrNull(JDateChooser chooser) {
		if (chooser.getDate() == null)
			return null;
		return chooser.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
	}

	private void loadChiTietTheoNV() {
		int row = tblNhanVien.getSelectedRow();
		if (row < 0)
			return;

		String maNV = tblNhanVien.getValueAt(row, 1).toString();

		// Reset 3 bảng
		modelBan.setRowCount(0);
//		modelTra.setRowCount(0);
//		modelHuy.setRowCount(0);

		// ================================================================
		// LOAD CHI TIẾT TỪ DAO THẬT
		// ================================================================
		int stt = 1;

		// ---------------------------------------------
		// 1) LỊCH SỬ BÁN HÀNG
		// ---------------------------------------------
		var dsBan = lichSuDAO.layLichSuBanTheoNhanVien(maNV, getDateOrNull(dateTuNgay), getDateOrNull(dateDenNgay));
		stt = 1;
		for (var hd : dsBan) {
			modelBan.addRow(
					new Object[] { stt++, hd.getMaHoaDon(), hd.getNgayLap() != null ? dtf.format(hd.getNgayLap()) : "",
							hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "",
						df.format(hd.getTongThanhToan()) });
		}

	}

	// ================================================================
	// EVENTS
	// ================================================================
	private void addEvents() {

		btnToggleAdvanced.addActionListener(e -> {
			boolean show = !pnAdvancedWrap.isVisible();
			pnAdvancedWrap.setVisible(show);
			pnAdvancedFilter.setVisible(show);
			btnToggleAdvanced.setText(show ? "Bộ lọc nâng cao \u25B2" : "Bộ lọc nâng cao \u25BC");
			pnHeader.revalidate();
			pnHeader.repaint();
		});

		txtTimKiem.addActionListener(e -> applyFilter());
		dateTuNgay.getDateEditor().addPropertyChangeListener(e -> applyFilter());
		dateDenNgay.getDateEditor().addPropertyChangeListener(e -> applyFilter());

		cbChucVu.addActionListener(e -> applyFilter());
		cbCaLam.addActionListener(e -> applyFilter());
		cbTrangThai.addActionListener(e -> applyFilter());

		btnTim.addActionListener(e -> applyFilter());
		btnMoi.addActionListener(e -> lamMoi());

		// Chọn NV -> load 3 bảng
		tblNhanVien.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				loadChiTietTheoNV();
			}
		});
	}

	private void lamMoi() {
		txtTimKiem.setText("");
		cbChucVu.setSelectedIndex(0);
		cbCaLam.setSelectedIndex(0);
		cbTrangThai.setSelectedIndex(0);
		dateTuNgay.setDate(null);
		dateDenNgay.setDate(null);

		applyFilter();
	}

	// ================================================================
	// UTILS
	// ================================================================
	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		table.setRowHeight(28);

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 14));
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);

		return table;
	}

	private TitledBorder createBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 15), Color.DARK_GRAY);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
			}
			JFrame f = new JFrame("Tra cứu nhân viên");
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(1400, 800);
			f.setLocationRelativeTo(null);
			f.setContentPane(new TraCuuNhanVien_GUI());
			f.setVisible(true);
		});
	}
}
