package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.*;
import javax.swing.table.*;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.LichSuNhanVien_DAO;
import dao.NhanVien_DAO;
import entity.HoaDon;
import entity.NhanVien;
import entity.PhieuHuy;
import entity.PhieuTra;

@SuppressWarnings("serial")
public class TraCuuNhanVien_GUI extends JPanel {

	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã, tên hoặc SĐT...";

	// HEADER
	private JPanel pnHeader;

	private JTextField txtTimKiem;
	private JComboBox<String> cbChucVu;
	private JComboBox<String> cbCaLam;
	private JComboBox<String> cbTrangThai;
	private PillButton btnTim;
	private PillButton btnLamMoi;

	// CENTER
	private JPanel pnCenter;
	private JTable tblNhanVien;
	private DefaultTableModel modelNhanVien;

	private JTabbedPane tabChiTiet;
	private JTable tblLichSuBan;
	private DefaultTableModel modelBan;

	private final NhanVien_DAO nvDAO;
	private final LichSuNhanVien_DAO lichSuDAO;

	private List<NhanVien> danhSachGoc = new ArrayList<>();

	private DefaultTableModel modelTra;

	private JTable tblLichSuTra;

	private DefaultTableModel modelHuy;

	private JTable tblLichSuHuy;
	private final DecimalFormat df = new DecimalFormat("#,### đ");
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public TraCuuNhanVien_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		nvDAO = new NhanVien_DAO();
		lichSuDAO = new LichSuNhanVien_DAO();
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
		initData();
	}

	// ================================================================
	// HEADER – GIỐNG TRA CỨU ĐƠN TRẢ HÀNG
	// ================================================================
	private void taoHeader() {

		pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(1073, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));

		// Ô tìm kiếm
		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setBounds(25, 17, 480, 60);
		pnHeader.add(txtTimKiem);

		addFilterLabel("Chức vụ:", 540, 28, 100, 35);
		cbChucVu = new JComboBox<>(new String[] { "Tất cả", "Quản lý", "Nhân viên" });
		setupCombo(cbChucVu, 630, 28, 160, 38);

		addFilterLabel("Ca làm:", 800, 28, 100, 35);
		cbCaLam = new JComboBox<>(new String[] { "Tất cả", "Sáng", "Chiều", "Tối" });
		setupCombo(cbCaLam, 880, 28, 160, 38);

		addFilterLabel("Trạng thái:", 1050, 28, 120, 35);
		cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Đang làm", "Đã nghỉ" });
		setupCombo(cbTrangThai, 1140, 28, 160, 38);

		// Nút Tìm kiếm
		btnTim = new PillButton("Tìm kiếm");
		btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTim.setBounds(1310, 22, 130, 50);
		pnHeader.add(btnTim);

		// Nút Làm mới
		btnLamMoi = new PillButton("Làm mới");
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnLamMoi.setBounds(1450, 22, 130, 50);
		pnHeader.add(btnLamMoi);
	}

	private void addFilterLabel(String text, int x, int y, int w, int h) {
		JLabel lbl = new JLabel(text);
		lbl.setBounds(x, y, w, h);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		pnHeader.add(lbl);
	}

	private void setupCombo(JComboBox<?> cb, int x, int y, int w, int h) {
		cb.setBounds(x, y, w, h);
		cb.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		pnHeader.add(cb);
	}

	// ================================================================
	// CENTER
	// ================================================================
	private void taoCenter() {
		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(400);
		split.setResizeWeight(0.5);

		// Bảng Nhân viên
		String[] colNV = { "STT", "Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Chức vụ", "Ca", "Trạng thái" };
		modelNhanVien = new DefaultTableModel(colNV, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblNhanVien = setupTable(modelNhanVien);

		JScrollPane scrollNV = new JScrollPane(tblNhanVien);
		scrollNV.setBorder(createTitledBorder("Danh sách nhân viên"));
		split.setTopComponent(scrollNV);

		// Tab chi tiết giữ nguyên
		tabChiTiet = new JTabbedPane();
		tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		split.setBottomComponent(tabChiTiet);

		taoTabsChiTiet();
		pnCenter.add(split, BorderLayout.CENTER);
	}

	private void taoTabsChiTiet() {

		modelBan = new DefaultTableModel(new String[] { "STT", "Mã hóa đơn", "Ngày lập", "Khách hàng", "Tổng tiền" },
				0);

		tblLichSuBan = setupTable(modelBan);
		tabChiTiet.add("Lịch sử bán hàng", new JScrollPane(tblLichSuBan));
		modelTra = new DefaultTableModel(
				new String[] { "STT", "Mã phiếu trả", "Ngày lập", "Khách hàng", "Tổng tiền", "Trạng thái" }, 0);

		tblLichSuTra = setupTable(modelTra);

		tabChiTiet.add("Lịch sử trả hàng", new JScrollPane(tblLichSuTra));

		modelHuy = new DefaultTableModel(
				new String[] { "STT", "Mã phiếu hủy", "Ngày lập", "Nhân viên lập", "Trạng thái", "Tổng tiền" }, 0);

		tblLichSuHuy = setupTable(modelHuy);

		tabChiTiet.add("Lịch sử hủy hàng", new JScrollPane(tblLichSuHuy));
	}

	// ================================================================
	// TABLE STYLE – GIỐNG ĐƠN TRẢ HÀNG 100%
	// ================================================================
	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);

		table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		table.setRowHeight(35);
		table.setGridColor(new Color(230, 230, 230));
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setSelectionForeground(Color.BLACK);

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.setPreferredSize(new Dimension(100, 40));
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);

		// Tooltip auto
		table.setToolTipText("");
		table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseMoved(java.awt.event.MouseEvent e) {
				int r = table.rowAtPoint(e.getPoint());
				int c = table.columnAtPoint(e.getPoint());
				if (r > -1 && c > -1) {
					Object v = table.getValueAt(r, c);
					if (v != null) {
						Component comp = table.prepareRenderer(table.getCellRenderer(r, c), r, c);
						int cellW = table.getColumnModel().getColumn(c).getWidth();
						int textW = comp.getPreferredSize().width;
						table.setToolTipText(textW > cellW - 5 ? v.toString() : null);
					}
				}
			}
		});

		return table;
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
	}

	// =====================================================================
//  EVENT HANDLER
//=====================================================================
	private void addEvents() {

		btnTim.addActionListener(e -> xuLyTimKiem());
		txtTimKiem.addActionListener(e -> xuLyTimKiem());

//		cbChucVu.addActionListener(e -> locTheoBoLoc());
//		cbCaLam.addActionListener(e -> locTheoBoLoc());
//		cbTrangThai.addActionListener(e -> locTheoBoLoc());

		btnLamMoi.addActionListener(e -> xuLyLamMoi());
		// Khi chọn 1 nhân viên → load lịch sử
		tblNhanVien.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				taiLichSuBanHang();
				taiLichSuTraHang();
				taiLichSuHuyHang();
			}
		});

	}

	private void xuLyLamMoi() {
		txtTimKiem.setText("");
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);

		cbChucVu.setSelectedIndex(0);
		cbCaLam.setSelectedIndex(0);
		cbTrangThai.setSelectedIndex(0);

		taiDanhSachNhanVien();
		loadTableNhanVien(danhSachGoc);
	}

	// =====================================================================
//  INIT DATA (giống tra cứu đơn trả hàng)
//=====================================================================
	private void initData() {
		taiDanhSachNhanVien();
		loadTableNhanVien(danhSachGoc);
	}

//=====================================================================
//  TẢI DỮ LIỆU NHÂN VIÊN
//=====================================================================

	private void taiDanhSachNhanVien() {
		// convert sang ArrayList để tránh lỗi type mismatch
		danhSachGoc = new ArrayList<>(nvDAO.layTatCaNhanVien());
	}

//=====================================================================
//  LOAD TABLE
//=====================================================================
	private void loadTableNhanVien(List<NhanVien> ds) {
		modelNhanVien.setRowCount(0);
		int stt = 1;

		for (NhanVien nv : ds) {
			modelNhanVien.addRow(new Object[] { stt++, nv.getMaNhanVien(), nv.getTenNhanVien(),
					nv.isGioiTinh() ? "Nam" : "Nữ", nv.getNgaySinh() != null ? nv.getNgaySinh().format(dtf) : "",
					nv.getSoDienThoai(), nv.isQuanLy() ? "Quản lý" : "Nhân viên", doiCaLam(nv.getCaLam()),
					nv.isTrangThai() ? "Đang làm" : "Đã nghỉ" });
		}
	}

//=====================================================================
//  XỬ LÝ TÌM KIẾM (giống đơn trả hàng)
//=====================================================================
	private void xuLyTimKiem() {

		String keyword = txtTimKiem.getText().trim();
		String cv = cbChucVu.getSelectedItem().toString();
		String ca = cbCaLam.getSelectedItem().toString();
		String tt = cbTrangThai.getSelectedItem().toString();

		List<NhanVien> ds = new ArrayList<>(danhSachGoc);

		// --- keyword ---
		if (!keyword.isEmpty() && !keyword.equals(PLACEHOLDER_TIM_KIEM)) {
			String kw = keyword.toLowerCase();
			ds.removeIf(nv -> !(nv.getMaNhanVien().toLowerCase().contains(kw)
					|| nv.getTenNhanVien().toLowerCase().contains(kw) || nv.getSoDienThoai().contains(kw)));
		}

		// --- chức vụ ---
		if (!"Tất cả".equals(cv)) {
			ds.removeIf(nv -> (cv.equals("Quản lý") && !nv.isQuanLy()) || (cv.equals("Nhân viên") && nv.isQuanLy()));
		}

		// --- ca làm ---
		if (!"Tất cả".equals(ca)) {
			ds.removeIf(nv -> !doiCaLam(nv.getCaLam()).equals(ca));
		}

		// --- trạng thái ---
		if (!"Tất cả".equals(tt)) {
			boolean isWorking = tt.equals("Đang làm");
			ds.removeIf(nv -> nv.isTrangThai() != isWorking);
		}

		loadTableNhanVien(ds);
	}

////=====================================================================
////  LỌC THEO COMBOBOX (giống đơn trả hàng)
////=====================================================================
//	private void locTheoBoLoc() {
//
//		String cv = cbChucVu.getSelectedItem().toString();
//		String ca = cbCaLam.getSelectedItem().toString();
//		String tt = cbTrangThai.getSelectedItem().toString();
//
//		List<NhanVien> ds = new ArrayList<>(danhSachGoc);
//
//		// --- chức vụ ---
//		if (!"Tất cả".equals(cv)) {
//			ds.removeIf(nv -> (cv.equals("Quản lý") && !nv.isQuanLy()) || (cv.equals("Nhân viên") && nv.isQuanLy()));
//		}
//
//		// --- ca ---
//		if (!"Tất cả".equals(ca)) {
//			ds.removeIf(nv -> !doiCaLam(nv.getCaLam()).equals(ca));
//		}
//
//		// --- trạng thái ---
//		if (!"Tất cả".equals(tt)) {
//			boolean isWorking = tt.equals("Đang làm");
//			ds.removeIf(nv -> nv.isTrangThai() != isWorking);
//		}
//
//		loadTableNhanVien(ds);
//	}

	private String doiCaLam(int ca) {
		return switch (ca) {
		case 1 -> "Sáng";
		case 2 -> "Chiều";
		case 3 -> "Tối";
		default -> "Không rõ";
		};
	}

	private String layMaNhanVienDangChon() {
		int row = tblNhanVien.getSelectedRow();
		if (row == -1)
			return null;

		return tblNhanVien.getValueAt(row, 1).toString(); // cột 1 = Mã NV
	}

	private void taiLichSuBanHang() {
		String maNV = layMaNhanVienDangChon();
		if (maNV == null)
			return;

		List<HoaDon> ds = lichSuDAO.layLichSuBanTheoNhanVien(maNV, null, null);

		modelBan.setRowCount(0);
		int stt = 1;

		for (HoaDon hd : ds) {
			modelBan.addRow(
					new Object[] { stt++, hd.getMaHoaDon(), hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "",
							hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "",
							df.format(hd.getTongThanhToan()) });
		}
	}

	private void taiLichSuTraHang() {
		int row = tblNhanVien.getSelectedRow();
		if (row < 0)
			return;

		String maNV = tblNhanVien.getValueAt(row, 1).toString();

		List<PhieuTra> ds = lichSuDAO.layLichSuTraTheoNhanVien(maNV, null, null);

		modelTra.setRowCount(0);
		int stt = 1;

		for (PhieuTra pt : ds) {
			modelTra.addRow(new Object[] { stt++, pt.getMaPhieuTra(),
					pt.getNgayLap() != null ? pt.getNgayLap().format(dtf) : "",
					pt.getKhachHang() != null ? pt.getKhachHang().getTenKhachHang() : "",
					df.format(pt.getTongTienHoan()), pt.isDaDuyet() ? "Đã duyệt" : "Chờ duyệt" });
		}
	}

	private void taiLichSuHuyHang() {
		int row = tblNhanVien.getSelectedRow();
		if (row < 0)
			return;

		String maNV = tblNhanVien.getValueAt(row, 1).toString();

		List<PhieuHuy> ds = lichSuDAO.layLichSuHuyTheoNhanVien(maNV, null, null);

		modelHuy.setRowCount(0);
		int stt = 1;

		for (PhieuHuy ph : ds) {
			modelHuy.addRow(new Object[] { stt++, ph.getMaPhieuHuy(),
					ph.getNgayLapPhieu() != null ? ph.getNgayLapPhieu().format(dtf) : "",
					ph.getNhanVien() != null ? ph.getNhanVien().getTenNhanVien() : "", ph.getTrangThaiText(),
					df.format(ph.getTongTien()) });
		}
	}

}
