package gui.tracuu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã, tên hoặc SĐT... (F1 / Ctrl+F)";

	// HEADER
	private JPanel pnHeader;

	private JTextField txtTimKiem;
	private JComboBox<String> cbChucVu;
	private JComboBox<String> cbCaLam;
	private JComboBox<String> cbTrangThai;
	private PillButton btnTim;
	private PillButton btnLamMoi;
	private PillButton btnXuatExcel;

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
		setupKeyboardShortcuts(); // Thiết lập phím tắt
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
		txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
		pnHeader.add(txtTimKiem);

		addFilterLabel("Chức vụ:", 525, 28, 70, 35);
		cbChucVu = new JComboBox<>(new String[] { "Tất cả", "Quản lý", "Nhân viên" });
		setupCombo(cbChucVu, 605, 28, 105, 35);

		addFilterLabel("Ca làm:", 720, 28, 70, 35);
		cbCaLam = new JComboBox<>(new String[] { "Tất cả", "Sáng", "Chiều", "Tối" });
		setupCombo(cbCaLam, 790, 28, 105, 35);

		addFilterLabel("Trạng thái:", 905, 28, 90, 35);
		cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Đang làm", "Đã nghỉ" });
		setupCombo(cbTrangThai, 1005, 28, 105, 35);

		// Nút Tìm kiếm
		btnTim = new PillButton(
				"<html>" +
						"<center>" +
						"TÌM KIẾM<br>" +
						"<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
						"</center>" +
						"</html>");
		btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTim.setBounds(1135, 22, 130, 50);
		btnTim.setToolTipText(
				"<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên và bộ lọc</html>");
		pnHeader.add(btnTim);

		// Nút Làm mới
		btnLamMoi = new PillButton(
				"<html>" +
						"<center>" +
						"LÀM MỚI<br>" +
						"<span style='font-size:10px; color:#888888;'>(F5)</span>" +
						"</center>" +
						"</html>");
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnLamMoi.setBounds(1275, 22, 130, 50);
		btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
		pnHeader.add(btnLamMoi);

		// Nút Xuất Excel
		btnXuatExcel = new PillButton(
				"<html>" +
						"<center>" +
						"XUẤT EXCEL<br>" +
						"<span style='font-size:10px; color:#888888;'>(Ctrl+E)</span>" +
						"</center>" +
						"</html>");
		btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnXuatExcel.setBounds(1415, 22, 180, 50);
		btnXuatExcel.setToolTipText("<html><b>Phím tắt:</b> Ctrl+E<br>Xuất dữ liệu ra file Excel</html>");
		pnHeader.add(btnXuatExcel);
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
		
		// Căn lề các cột
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		
		// Căn giữa các cột: STT, Mã NV, Giới tính, Ngày sinh, SĐT, Chức vụ, Ca, Trạng thái
		tblNhanVien.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblNhanVien.getColumnModel().getColumn(1).setCellRenderer(center); // Mã NV
		// Cột 2 (Họ tên) để mặc định căn trái
		tblNhanVien.getColumnModel().getColumn(3).setCellRenderer(center); // Giới tính
		tblNhanVien.getColumnModel().getColumn(4).setCellRenderer(center); // Ngày sinh
		tblNhanVien.getColumnModel().getColumn(5).setCellRenderer(center); // SĐT
		tblNhanVien.getColumnModel().getColumn(6).setCellRenderer(center); // Chức vụ
		tblNhanVien.getColumnModel().getColumn(7).setCellRenderer(center); // Ca
		tblNhanVien.getColumnModel().getColumn(8).setCellRenderer(center); // Trạng thái
		
		// Render màu cho Trạng thái
		tblNhanVien.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
				if ("Đang làm".equals(value)) {
					lbl.setForeground(new Color(0x2E7D32)); // Xanh lá
				} else {
					lbl.setForeground(Color.RED); // Đỏ
				}
				return lbl;
			}
		});

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
		
		// Căn lề cho bảng bán hàng
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);
		
		tblLichSuBan.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblLichSuBan.getColumnModel().getColumn(1).setCellRenderer(center); // Mã hóa đơn
		tblLichSuBan.getColumnModel().getColumn(2).setCellRenderer(center); // Ngày lập
		// Cột 3 (Khách hàng) để mặc định
		tblLichSuBan.getColumnModel().getColumn(4).setCellRenderer(right); // Tổng tiền
		
		tabChiTiet.add("Lịch sử bán hàng", new JScrollPane(tblLichSuBan));
		
		modelTra = new DefaultTableModel(
				new String[] { "STT", "Mã phiếu trả", "Ngày lập", "Khách hàng", "Tổng tiền", "Trạng thái" }, 0);

		tblLichSuTra = setupTable(modelTra);
		
		// Căn lề cho bảng trả hàng
		tblLichSuTra.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblLichSuTra.getColumnModel().getColumn(1).setCellRenderer(center); // Mã phiếu trả
		tblLichSuTra.getColumnModel().getColumn(2).setCellRenderer(center); // Ngày lập
		// Cột 3 (Khách hàng) để mặc định
		tblLichSuTra.getColumnModel().getColumn(4).setCellRenderer(right); // Tổng tiền
		tblLichSuTra.getColumnModel().getColumn(5).setCellRenderer(center); // Trạng thái

		tabChiTiet.add("Lịch sử trả hàng", new JScrollPane(tblLichSuTra));

		modelHuy = new DefaultTableModel(
				new String[] { "STT", "Mã phiếu hủy", "Ngày lập", "Nhân viên lập", "Trạng thái", "Tổng tiền" }, 0);

		tblLichSuHuy = setupTable(modelHuy);
		
		// Căn lề cho bảng hủy hàng
		tblLichSuHuy.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblLichSuHuy.getColumnModel().getColumn(1).setCellRenderer(center); // Mã phiếu hủy
		tblLichSuHuy.getColumnModel().getColumn(2).setCellRenderer(center); // Ngày lập
		// Cột 3 (Nhân viên lập) để mặc định
		tblLichSuHuy.getColumnModel().getColumn(4).setCellRenderer(center); // Trạng thái
		tblLichSuHuy.getColumnModel().getColumn(5).setCellRenderer(right); // Tổng tiền

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
	// EVENT HANDLER
	// =====================================================================
	private void addEvents() {

		btnTim.addActionListener(e -> xuLyTimKiem());
		txtTimKiem.addActionListener(e -> xuLyTimKiem());

		// cbChucVu.addActionListener(e -> locTheoBoLoc());
		// cbCaLam.addActionListener(e -> locTheoBoLoc());
		// cbTrangThai.addActionListener(e -> locTheoBoLoc());

		btnLamMoi.addActionListener(e -> xuLyLamMoi());
		btnXuatExcel.addActionListener(e -> xuatExcel());
		// Khi chọn 1 nhân viên → load lịch sử
		tblNhanVien.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				taiLichSuBanHang();
				taiLichSuTraHang();
				taiLichSuHuyHang();
			}
		});

	}

	/**
	 * Thiết lập phím tắt cho màn hình Tra cứu Nhân viên
	 */
	private void setupKeyboardShortcuts() {
		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();

		// F1: Focus tìm kiếm
		inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
		actionMap.put("focusTimKiem", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				txtTimKiem.requestFocus();
				txtTimKiem.selectAll();
			}
		});

		// F5: Làm mới
		inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
		actionMap.put("lamMoi", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				xuLyLamMoi();
			}
		});

		// Ctrl+F: Focus tìm kiếm
		inputMap.put(KeyStroke.getKeyStroke("control F"), "timKiem");
		actionMap.put("timKiem", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				txtTimKiem.requestFocus();
				txtTimKiem.selectAll();
			}
		});

		// Ctrl+E: Xuất Excel
		inputMap.put(KeyStroke.getKeyStroke("control E"), "xuatExcel");
		actionMap.put("xuatExcel", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				xuatExcel();
			}
		});
	}

	private void xuLyLamMoi() {
		txtTimKiem.setText("");
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);

		cbChucVu.setSelectedIndex(0);
		cbCaLam.setSelectedIndex(0);
		cbTrangThai.setSelectedIndex(0);

		// Refresh cache và tải lại dữ liệu từ DB
		nvDAO.refreshCache();
		taiDanhSachNhanVien();
		loadTableNhanVien(danhSachGoc);
	}

	// =====================================================================
	// INIT DATA (giống tra cứu đơn trả hàng)
	// =====================================================================
	private void initData() {
		taiDanhSachNhanVien();
		loadTableNhanVien(danhSachGoc);
	}

	// =====================================================================
	// TẢI DỮ LIỆU NHÂN VIÊN
	// =====================================================================

	private void taiDanhSachNhanVien() {
		// convert sang ArrayList để tránh lỗi type mismatch
		danhSachGoc = new ArrayList<>(nvDAO.layTatCaNhanVien());
	}

	// =====================================================================
	// LOAD TABLE
	// =====================================================================
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

	// =====================================================================
	// XỬ LÝ TÌM KIẾM (giống đơn hàng - hybrid cache + DB)
	// =====================================================================
	private void xuLyTimKiem() {

		String keyword = txtTimKiem.getText().trim();
		String cv = cbChucVu.getSelectedItem().toString();
		String ca = cbCaLam.getSelectedItem().toString();
		String tt = cbTrangThai.getSelectedItem().toString();

		List<NhanVien> ketQua = new ArrayList<>();

		// LOGIC TỐI ƯU: Lai ghép 2 phương pháp (giống TraCuuDonHang_GUI)
		// 1. Nếu chỉ có bộ lọc → Filter trên cache (nhanh hơn)
		if (keyword.isEmpty() || keyword.equals(PLACEHOLDER_TIM_KIEM)) {
			// Đảm bảo cache đã được load
			if (danhSachGoc == null || danhSachGoc.isEmpty()) {
				taiDanhSachNhanVien();
			}
			ketQua = new ArrayList<>(danhSachGoc); // Clone từ cache
		}
		// 2. Nếu có keyword cụ thể → Tìm kiếm trong cache (tối ưu)
		else {
			String kw = keyword.toLowerCase();
			// Đảm bảo cache đã được load
			if (danhSachGoc == null || danhSachGoc.isEmpty()) {
				taiDanhSachNhanVien();
			}
			// Tìm kiếm lai ghép trên cache
			for (NhanVien nv : danhSachGoc) {
				if (nv.getMaNhanVien().toLowerCase().contains(kw)
						|| nv.getTenNhanVien().toLowerCase().contains(kw)
						|| (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(kw))) {
					ketQua.add(nv);
				}
			}
		}

		// --- Áp dụng bộ lọc: chức vụ ---
		if (!"Tất cả".equals(cv)) {
			ketQua.removeIf(
					nv -> (cv.equals("Quản lý") && !nv.isQuanLy()) || (cv.equals("Nhân viên") && nv.isQuanLy()));
		}

		// --- Áp dụng bộ lọc: ca làm ---
		if (!"Tất cả".equals(ca)) {
			ketQua.removeIf(nv -> !doiCaLam(nv.getCaLam()).equals(ca));
		}

		// --- Áp dụng bộ lọc: trạng thái ---
		if (!"Tất cả".equals(tt)) {
			boolean isWorking = tt.equals("Đang làm");
			ketQua.removeIf(nv -> nv.isTrangThai() != isWorking);
		}

		loadTableNhanVien(ketQua);

		// Nếu tìm kiếm cụ thể (có nhập text) mà không thấy thì báo
		if (ketQua.isEmpty() && !keyword.isEmpty() && !keyword.equals(PLACEHOLDER_TIM_KIEM)) {
			javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên nào!", "Thông báo",
					javax.swing.JOptionPane.INFORMATION_MESSAGE);
		}
	}

	//// =====================================================================
	//// LỌC THEO COMBOBOX (giống đơn trả hàng)
	//// =====================================================================
	// private void locTheoBoLoc() {
	//
	// String cv = cbChucVu.getSelectedItem().toString();
	// String ca = cbCaLam.getSelectedItem().toString();
	// String tt = cbTrangThai.getSelectedItem().toString();
	//
	// List<NhanVien> ds = new ArrayList<>(danhSachGoc);
	//
	// // --- chức vụ ---
	// if (!"Tất cả".equals(cv)) {
	// ds.removeIf(nv -> (cv.equals("Quản lý") && !nv.isQuanLy()) ||
	//// (cv.equals("Nhân viên") && nv.isQuanLy()));
	// }
	//
	// // --- ca ---
	// if (!"Tất cả".equals(ca)) {
	// ds.removeIf(nv -> !doiCaLam(nv.getCaLam()).equals(ca));
	// }
	//
	// // --- trạng thái ---
	// if (!"Tất cả".equals(tt)) {
	// boolean isWorking = tt.equals("Đang làm");
	// ds.removeIf(nv -> nv.isTrangThai() != isWorking);
	// }
	//
	// loadTableNhanVien(ds);
	// }

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

	/**
	 * Xuất dữ liệu ra file Excel
	 */
	private void xuatExcel() {
		if (modelNhanVien.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!",
					"Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
			fileChooser.setSelectedFile(new File("DanhSachNhanVien.xlsx"));
			fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (!file.getName().endsWith(".xlsx")) {
					file = new File(file.getAbsolutePath() + ".xlsx");
				}

				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet("Danh Sách Nhân Viên");

				// Header style
				CellStyle headerStyle = workbook.createCellStyle();
				XSSFFont headerFont = workbook.createFont();
				headerFont.setBold(true);
				headerStyle.setFont(headerFont);
				headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
				headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

				// Tiêu đề
				Row titleRow = sheet.createRow(0);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue("DANH SÁCH NHÂN VIÊN");

				CellStyle titleStyle = workbook.createCellStyle();
				XSSFFont titleFont = workbook.createFont();
				titleFont.setBold(true);
				titleFont.setFontHeightInPoints((short) 16);
				titleStyle.setFont(titleFont);
				titleCell.setCellStyle(titleStyle);

				// Thông tin ngày xuất
				Row periodRow = sheet.createRow(1);
				periodRow.createCell(0).setCellValue(
						"Ngày xuất: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

				// Header row
				Row headerRow = sheet.createRow(3);
				for (int i = 0; i < modelNhanVien.getColumnCount(); i++) {
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(modelNhanVien.getColumnName(i));
					cell.setCellStyle(headerStyle);
				}

				// Data rows
				for (int row = 0; row < modelNhanVien.getRowCount(); row++) {
					Row dataRow = sheet.createRow(row + 4);
					for (int col = 0; col < modelNhanVien.getColumnCount(); col++) {
						Object value = modelNhanVien.getValueAt(row, col);
						dataRow.createCell(col).setCellValue(value != null ? value.toString() : "");
					}
				}

				// Auto-size columns
				for (int i = 0; i < modelNhanVien.getColumnCount(); i++) {
					sheet.autoSizeColumn(i);
				}

				// Write file
				try (FileOutputStream fos = new FileOutputStream(file)) {
					workbook.write(fos);
				}
				workbook.close();

				JOptionPane.showMessageDialog(this,
						"Xuất Excel thành công!\nFile: " + file.getAbsolutePath(),
						"Thành công", JOptionPane.INFORMATION_MESSAGE);

				// Mở file
				Desktop.getDesktop().open(file);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi xuất Excel: " + e.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

}
