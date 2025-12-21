/**
 * @author Thanh Kha  
 * @version 2.0 - Rewritten to match QL_HuyHang_GUI structure 100%
 */

package gui.quanly;

import database.connectDB;
import component.button.PillButton;
import component.input.PlaceholderSupport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.HierarchyEvent;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import com.toedter.calendar.JDateChooser;

import component.border.RoundedBorder;
import dao.ChiTietPhieuTra_DAO;
import dao.PhieuTra_DAO;
import entity.ChiTietPhieuTra;
import entity.LoSanPham;
import entity.NhanVien;
import entity.PhieuTra;
import entity.Session;
import dao.LoSanPham_DAO;
import dao.QuyCachDongGoi_DAO;
import entity.QuyCachDongGoi;
import gui.dialog.PhieuTraPreviewDialog;

public class QLTraHang_GUI extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã phiếu, tên KH hoặc SĐT... (F1 / Ctrl+F)";

	private JPanel pnPhieuTra;
	private JPanel pnHeader;
	private JPanel pnCTPT;

	private JTextField txtSearch;
	private DefaultTableModel modelPT;
	private JTable tblPT;
	private JScrollPane scrCTPT;
	private DefaultTableModel modelCTPT;
	private JScrollPane scrPT;
	private JTable tblCTPT;
	private List<PhieuTra> dsPhieuTra;
	private List<ChiTietPhieuTra> dsCTPhieuTra;
	private PhieuTra_DAO pt_dao;
	private ChiTietPhieuTra_DAO ctpt_dao;
	private PillButton btnHuyHang;
	private PillButton btnNhapKho;
	private JComboBox<String> cbTrangThai;
	private JDateChooser dateTuNgay;
	private JDateChooser dateDenNgay;
	private PillButton btnLamMoi;
	private PillButton btnTimKiem;
	private TableRowSorter<DefaultTableModel> sorter;
	private JPanel pnBtnCTPT;
	private JSplitPane pnCenter;
	// Font & Color
	private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
	private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
	private final Color COLOR_PRIMARY = new Color(33, 150, 243);

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,###đ");

	public QLTraHang_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {

		try {
			connectDB.getInstance().connect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		pt_dao = new PhieuTra_DAO();
		ctpt_dao = new ChiTietPhieuTra_DAO();

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		TaoHeader();
		initTable();// tạo bảng và load dữ liệu từ database lên bảng
		TaoPanelCenter();
		configureTableRenderers();
		// Event listeners
		txtSearch.addActionListener(e -> refreshFilters()); // Nhấn Enter để tìm kiếm
		cbTrangThai.addActionListener(e -> refreshFilters());
		dateTuNgay.addPropertyChangeListener("date", e -> refreshFilters());
		dateDenNgay.addPropertyChangeListener("date", e -> refreshFilters());
		btnTimKiem.addActionListener(this);
		btnLamMoi.addActionListener(this);
		btnNhapKho.addActionListener(this);
		btnHuyHang.addActionListener(this);
		tblCTPT.addMouseListener(this);
		tblPT.addMouseListener(this);

		setupKeyboardShortcuts(); // Thiết lập phím tắt
		addFocusOnShow(); // Focus vào ô tìm kiếm khi panel được hiển thị
	}

	private void TaoHeader() {
		pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(0, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));
		add(pnHeader, BorderLayout.NORTH);

		// --- 1. Ô TÌM KIẾM (Font 20) ---
		txtSearch = new JTextField();
		PlaceholderSupport.addPlaceholder(txtSearch, PLACEHOLDER_TIM_KIEM);
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtSearch.setBounds(25, 17, 480, 60);
		txtSearch.setBorder(new RoundedBorder(20));
		txtSearch.setBackground(Color.WHITE);
		txtSearch.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
		pnHeader.add(txtSearch);

		// --- 2. BỘ LỌC NGÀY ---
		// Từ ngày
		addFilterLabel("Từ:", 525, 28, 35, 35);
		dateTuNgay = new JDateChooser();
		dateTuNgay.setDateFormatString("dd/MM/yyyy");
		dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dateTuNgay.setBounds(560, 28, 140, 38);
		pnHeader.add(dateTuNgay);

		// Đến ngày
		addFilterLabel("Đến:", 710, 28, 40, 35);
		dateDenNgay = new JDateChooser();
		dateDenNgay.setDateFormatString("dd/MM/yyyy");
		dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dateDenNgay.setBounds(750, 28, 140, 38);
		pnHeader.add(dateDenNgay);

		// --- 3. TRẠNG THÁI ---
		addFilterLabel("Trạng thái:", 905, 28, 85, 35);
		cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Đã duyệt", "Chờ duyệt" });
		cbTrangThai.setBounds(990, 28, 130, 38);
		cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		pnHeader.add(cbTrangThai);

		// --- 4. CÁC NÚT CHỨC NĂNG ---
		btnTimKiem = new PillButton("<html>" + "<center>" + "TÌM KIẾM<br>"
				+ "<span style='font-size:10px; color:#888888;'>(Enter)</span>" + "</center>" + "</html>");
		btnTimKiem.setBounds(1135, 22, 130, 50);
		btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTimKiem.setToolTipText(
				"<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã phiếu, SĐT và bộ lọc</html>");
		pnHeader.add(btnTimKiem);

		btnLamMoi = new PillButton("<html>" + "<center>" + "LÀM MỚI<br>"
				+ "<span style='font-size:10px; color:#888888;'>(F5)</span>" + "</center>" + "</html>");
		btnLamMoi.setBounds(1275, 22, 130, 50);
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
		pnHeader.add(btnLamMoi);

	}

	// Helper tạo label (Font 16)
	private void addFilterLabel(String text, int x, int y, int w, int h) {
		JLabel lbl = new JLabel(text);
		lbl.setBounds(x, y, w, h);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		pnHeader.add(lbl);
	}

	/**
	 * Thiết lập phím tắt cho màn hình Quản lý trả hàng
	 */
	private void setupKeyboardShortcuts() {
		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();

		// F1: Focus tìm kiếm
		inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
		actionMap.put("focusTimKiem", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtSearch.requestFocus();
				txtSearch.selectAll();
			}
		});

		// F5: Làm mới
		inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
		actionMap.put("lamMoi", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtSearch.setText("");
				PlaceholderSupport.addPlaceholder(txtSearch, PLACEHOLDER_TIM_KIEM);
				cbTrangThai.setSelectedIndex(0);
				dateTuNgay.setDate(null);
				dateDenNgay.setDate(null);
				loadDataTablePT();
				modelCTPT.setRowCount(0);
			}
		});

		// Ctrl+F: Focus tìm kiếm
		inputMap.put(KeyStroke.getKeyStroke("control F"), "timKiem");
		actionMap.put("timKiem", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtSearch.requestFocus();
				txtSearch.selectAll();
			}
		});

		// Ctrl+K: Nhập kho
		inputMap.put(KeyStroke.getKeyStroke("control K"), "nhapKho");
		actionMap.put("nhapKho", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NhapKho();
			}
		});

		// Ctrl+H: Hủy hàng
		inputMap.put(KeyStroke.getKeyStroke("control H"), "huyHang");
		actionMap.put("huyHang", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HuyHang();
			}
		});
	}

	// Helper method để loại bỏ dấu tiếng Việt
	private String removeDiacritics(String text) {
		if (text == null)
			return "";
		String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
		return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}

	private void TaoPanelCenter() {
		TaoPanelPhieuTra();
		TaoPanelCTPT();
		pnCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnPhieuTra, pnCTPT);
		pnCenter.setDividerLocation(350);
		pnCenter.setResizeWeight(0.0);
		add(pnCenter, BorderLayout.CENTER);
	}

	private void TaoPanelPhieuTra() {
		// ===== CENTER =====
		pnPhieuTra = new JPanel(new BorderLayout());
		pnPhieuTra.setLayout(new BorderLayout());
		pnPhieuTra.add(scrPT);

	}

	private void TaoPanelCTPT() {

		pnCTPT = new JPanel(new BorderLayout());
		pnCTPT.setPreferredSize(new Dimension(600, 1080));

		TitledBorder tbCTPT = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				"Danh sách chi tiết phiếu trả", TitledBorder.LEFT, TitledBorder.TOP,
				new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
		pnCTPT.setBorder(tbCTPT);

		// ==== PANEL CHỨA 2 BUTTON

		pnBtnCTPT = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

		btnNhapKho = new PillButton("<html>" + "<center>" + "NHẬP KHO<br>"
				+ "<span style='font-size:10px; color:#888888;'>(Ctrl+K)</span>" + "</center>" + "</html>");
		btnNhapKho.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnNhapKho.setPreferredSize(new Dimension(150, 50)); // Kích thước nút lớn hơn
		btnNhapKho.setToolTipText(
				"<html><b>Phím tắt:</b> Ctrl+K<br>Nhập hàng trả về kho sau khi kiểm tra chất lượng</html>");
		btnNhapKho.setEnabled(false); // Mặc định disable

		btnHuyHang = new PillButton("<html>" + "<center>" + "HỦY HÀNG<br>"
				+ "<span style='font-size:10px; color:#888888;'>(Ctrl+H)</span>" + "</center>" + "</html>");
		btnHuyHang.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnHuyHang.setPreferredSize(new Dimension(150, 50)); // Kích thước nút lớn hơn
		btnHuyHang.setToolTipText("<html><b>Phím tắt:</b> Ctrl+H<br>Hủy hàng trả không đạt chất lượng</html>");
		btnHuyHang.setEnabled(false); // Mặc định disable

		pnBtnCTPT.add(btnNhapKho);
		pnBtnCTPT.add(btnHuyHang);

		// Thêm panel nút lên trên, bảng CTPT ở giữa
		pnCTPT.add(pnBtnCTPT, BorderLayout.NORTH);
		pnCTPT.add(scrCTPT, BorderLayout.CENTER);
	}

	private void refreshFilters() {
		if (sorter == null)
			return;

		List<RowFilter<Object, Object>> filters = new ArrayList<>();

		// --- Lọc theo text: cột 1 (Mã PT), cột 2 (Khách hàng), cột 3 (SĐT), cột 5
		// (Người trả) - đã dịch do thêm cột STT
		String text = txtSearch.getText().trim();
		if (!text.isEmpty() && !txtSearch.getForeground().equals(Color.GRAY)) {
			String searchTextNoSign = removeDiacritics(text);

			// Custom RowFilter hỗ trợ tìm kiếm tiếng Việt không dấu
			filters.add(new RowFilter<Object, Object>() {
				@Override
				public boolean include(Entry<? extends Object, ? extends Object> entry) {
					// Kiểm tra các cột: 1 (Mã PT), 2 (Khách hàng), 3 (SĐT), 5 (Người trả)
					int[] colsToCheck = { 1, 2, 3, 5 };
					for (int col : colsToCheck) {
						String value = entry.getStringValue(col);
						if (value != null) {
							String valueNoSign = removeDiacritics(value);
							if (valueNoSign.contains(searchTextNoSign)) {
								return true;
							}
						}
					}
					return false;
				}
			});
		}

		// --- Lọc theo trạng thái ComboBox: cột 6 (đã dịch do thêm cột STT)
		String trangThai = (String) cbTrangThai.getSelectedItem();
		if (trangThai != null && !trangThai.equals("Tất cả")) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(trangThai), 6));
		}

		// --- Lọc theo ngày: cột 4 (Ngày lập - đã dịch do thêm cột STT)
		java.util.Date tuNgay = dateTuNgay.getDate();
		java.util.Date denNgay = dateDenNgay.getDate();

		if (tuNgay != null || denNgay != null) {
			filters.add(new RowFilter<Object, Object>() {
				@Override
				public boolean include(Entry<? extends Object, ? extends Object> entry) {
					try {
						String ngayStr = entry.getStringValue(4); // Cột Ngày lập (đã dịch do thêm STT)
						LocalDate ngay = LocalDate.parse(ngayStr, fmt);

						LocalDate tu = tuNgay != null
								? tuNgay.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
								: null;
						LocalDate den = denNgay != null
								? denNgay.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
								: null;

						if (tu != null && den != null) {
							return !ngay.isBefore(tu) && !ngay.isAfter(den);
						} else if (tu != null) {
							return !ngay.isBefore(tu);
						} else if (den != null) {
							return !ngay.isAfter(den);
						}
						return true;
					} catch (Exception e) {
						return true;
					}
				}
			});
		}

		// --- Áp filter
		if (filters.isEmpty()) {
			sorter.setRowFilter(null);
		} else {
			sorter.setRowFilter(RowFilter.andFilter(filters));
		}
	}

	private void initTable() {
		// Bảng phiếu trả - Thêm cột STT và SĐT ẩn để tìm kiếm
		String[] phieuTraCols = { "STT", "Mã PT", "Khách hàng", "SĐT", "Ngày lập", "Người trả", "Trạng thái",
				"Tổng tiền hoàn" };
		modelPT = new DefaultTableModel(phieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblPT = setupTable(modelPT);
		scrPT = new JScrollPane(tblPT);
		TitledBorder tbPT = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				"Danh sách phiếu trả", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18),
				Color.DARK_GRAY);
		scrPT.setBorder(tbPT);

		// Cột STT căn giữa và width nhỏ

		// Khởi tạo sorter TRƯỚC khi load data để bộ lọc 30 ngày được áp dụng ngay
		sorter = new TableRowSorter<>(modelPT);
		tblPT.setRowSorter(sorter);

		loadDataTablePT();

		// Bảng chi tiết phiếu trả - Thêm cột STT
		String[] cTPhieuCols = { "STT", "Mã hóa đơn", "Mã lô", "Tên SP", "Hạn dùng", "SL trả", "Lý do", "Đơn vị tính",
				"Trạng thái" };

		modelCTPT = new DefaultTableModel(cTPhieuCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPT = setupTable(modelCTPT);
		scrCTPT = new JScrollPane(tblCTPT);

		tblPT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblPT.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				loadTableCTPT();
				capNhatTrangThaiNut();
			}
		});

		// bắt sự kiện chọn dòng CTPT để cập nhật trạng thái nút
		tblCTPT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblCTPT.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				capNhatTrangThaiNut();
			}
		});
	}

	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		table.setRowHeight(35);
		table.setGridColor(new Color(230, 230, 230));
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setSelectionForeground(Color.BLACK);

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(100, 40));
		return table;
	}

	private void configureTableRenderers() {
		tblPT.getColumnModel().getColumn(0).setPreferredWidth(50);
		// 3 la sdt an di
		tblPT.getColumnModel().getColumn(3).setMinWidth(0);
		tblPT.getColumnModel().getColumn(3).setMaxWidth(0);
		tblPT.getColumnModel().getColumn(3).setPreferredWidth(0);

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		tblPT.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblPT.getColumnModel().getColumn(1).setCellRenderer(center); // Mã PT
		tblPT.getColumnModel().getColumn(4).setCellRenderer(center); // Ngày lập
		tblPT.getColumnModel().getColumn(7).setCellRenderer(right); // Tổng tiền

		tblCTPT.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblCTPT.getColumnModel().getColumn(1).setCellRenderer(center); // Mã hóa đơn
		tblCTPT.getColumnModel().getColumn(4).setCellRenderer(center); // Hạn dùng
		tblCTPT.getColumnModel().getColumn(5).setCellRenderer(right); // SL trả

		// Cột trạng thái phiếu trả - căn giữa + màu sắc
		tblPT.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa
				if ("Đã duyệt".equals(value)) {
					lbl.setForeground(new Color(0x2E7D32));
					lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
				} else {
					lbl.setForeground(Color.RED);
					lbl.setFont(new Font("Segoe UI", Font.ITALIC, 15));
				}
				return lbl;
			}
		});
		// Cột trạng thái chi tiết - căn giữa + màu sắc
		tblCTPT.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa
				if ("Nhập lại hàng".equals(value)) {
					lbl.setForeground(new Color(0x2E7D32));
					lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
				} else if ("Huỷ hàng".equals(value)) {
					lbl.setForeground(Color.RED);
					lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
				} else {
					lbl.setForeground(Color.RED);
					lbl.setFont(new Font("Segoe UI", Font.ITALIC, 15));
				}
				return lbl;
			}
		});
	}

	// đưa dữ liệu Phiếu Trả lên bảng với 30 ngày mặc định
	private void loadDataTablePT() {
		dsPhieuTra = new ArrayList<PhieuTra>();
		modelPT.setRowCount(0);

		// --- CHỌN NGÀY MẶC ĐỊNH ---
		Calendar cal = Calendar.getInstance();

		// Đến ngày: Hôm nay
		java.util.Date now = cal.getTime();
		dateDenNgay.setDate(now);

		// Từ ngày: 30 ngày trước
		cal.add(Calendar.DAY_OF_MONTH, -30);
		java.util.Date d30 = cal.getTime();
		dateTuNgay.setDate(d30);

		try {
			dsPhieuTra = pt_dao.layTatCaPhieuTra();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int stt = 1;
		for (PhieuTra pt : dsPhieuTra) {
			String khachHang = pt.getKhachHang() != null ? pt.getKhachHang().getTenKhachHang() : "N/A";
			String sdt = pt.getKhachHang() != null ? pt.getKhachHang().getSoDienThoai() : ""; // Thêm SĐT (ẩn)
			String nhanVien = pt.getNhanVien() != null ? pt.getNhanVien().getTenNhanVien() : "N/A";

			modelPT.addRow(new Object[] { stt++, pt.getMaPhieuTra(), khachHang, sdt, // Cột SĐT (ẩn)
					pt.getNgayLap().format(fmt), nhanVien, pt.getTrangThaiText(), df.format(pt.getTongTienHoan()) });
		}

		// Áp dụng bộ lọc ngày mặc định
		refreshFilters();
		capNhatTrangThaiNut();
	}

	// đưa dữ liệu CTPT lên bảng với STT
	private void loadTableCTPT() {
		int selectRow = tblPT.getSelectedRow();

		if (selectRow == -1) {
			modelCTPT.setRowCount(0);
			capNhatTrangThaiNut();
			return;
		}

		// Lấy mã PT từ cột 1 (do thêm STT)
		String maPT = modelPT.getValueAt(selectRow, 1).toString();

		dsCTPhieuTra = new ArrayList<ChiTietPhieuTra>();
		modelCTPT.setRowCount(0);

		try {
			dsCTPhieuTra = ctpt_dao.timKiemChiTietBangMaPhieuTra(maPT);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int stt = 1;
		for (ChiTietPhieuTra ctpt : dsCTPhieuTra) {
			// ✅ Xử lý trường hợp DonViTinh = null
			String tenDonViTinh = "N/A";
			if (ctpt.getDonViTinh() != null) {
				tenDonViTinh = ctpt.getDonViTinh().getTenDonViTinh();
			}

			// Lấy thông tin từ ChiTietHoaDon
			String maHD = ctpt.getChiTietHoaDon() != null && ctpt.getChiTietHoaDon().getHoaDon() != null
					? ctpt.getChiTietHoaDon().getHoaDon().getMaHoaDon()
					: "N/A";
			String maLo = ctpt.getChiTietHoaDon() != null && ctpt.getChiTietHoaDon().getLoSanPham() != null
					? ctpt.getChiTietHoaDon().getLoSanPham().getMaLo()
					: "N/A";
			String tenSP = ctpt.getChiTietHoaDon() != null && ctpt.getChiTietHoaDon().getLoSanPham() != null
					&& ctpt.getChiTietHoaDon().getLoSanPham().getSanPham() != null
							? ctpt.getChiTietHoaDon().getLoSanPham().getSanPham().getTenSanPham()
							: "N/A";
			String hanDung = ctpt.getChiTietHoaDon() != null && ctpt.getChiTietHoaDon().getLoSanPham() != null
					&& ctpt.getChiTietHoaDon().getLoSanPham().getHanSuDung() != null
							? ctpt.getChiTietHoaDon().getLoSanPham().getHanSuDung().format(fmt)
							: "N/A";

			modelCTPT.addRow(new Object[] { stt++, maHD, maLo, tenSP, hanDung, ctpt.getSoLuong(), ctpt.getLyDoChiTiet(),
					tenDonViTinh, ctpt.getTrangThaiText() // Cột 8: Trạng thái
			});
		}

		capNhatTrangThaiNut();
	}

	/**
	 * Cập nhật trạng thái hiển thị các nút dựa trên việc có chọn dòng hay không -
	 * Không chọn dòng CTPT: Disable nút Nhập Kho và Hủy Hàng - Có chọn dòng CTPT
	 * (và PT): Enable nút Nhập Kho và Hủy Hàng - Chi tiết đã xử lý (Nhập lại hàng /
	 * Huỷ hàng): Disable cả 2 nút
	 */
	private void capNhatTrangThaiNut() {
		// Null check để tránh NPE khi khởi tạo
		if (tblPT == null || tblCTPT == null || btnNhapKho == null || btnHuyHang == null) {
			return;
		}

		int rowPT = tblPT.getSelectedRow();
		int rowCTPT = tblCTPT.getSelectedRow();
		boolean coDongCTPTDuocChon = (rowCTPT != -1);
		boolean coDongPTDuocChon = (rowPT != -1);

		// Kiểm tra trạng thái chi tiết - chỉ enable nếu còn "Chờ duyệt"
		boolean chiTietChuaXuLy = false;
		if (coDongCTPTDuocChon && modelCTPT != null) {
			String trangThai = modelCTPT.getValueAt(rowCTPT, 8).toString().trim(); // Cột 8 - Trạng thái
			chiTietChuaXuLy = trangThai.equalsIgnoreCase("Chờ duyệt");
		}

		// Buttons only enabled when a CTPT row with status "Chờ duyệt" is selected
		btnNhapKho.setEnabled(coDongCTPTDuocChon && coDongPTDuocChon && chiTietChuaXuLy);
		btnHuyHang.setEnabled(coDongCTPTDuocChon && coDongPTDuocChon && chiTietChuaXuLy);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản lý phiếu trả hàng");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new QLTraHang_GUI());
			frame.setVisible(true);
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Double click phiếu trả -> Xem phiếu trả (giống TraCuuDonTraHang)
		if (e.getSource() == tblPT && e.getClickCount() == 2) {
			int row = tblPT.getSelectedRow();
			if (row != -1) {
				String maPT = tblPT.getValueAt(row, 1).toString(); // Cột 1: Mã PT
				xemPhieuTra(maPT);
			}
		}
	}

	/**
	 * Mở dialog xem phiếu trả
	 */
	private void xemPhieuTra(String maPT) {
		PhieuTra pt = pt_dao.timKiemPhieuTraBangMa(maPT);
		if (pt != null) {
			List<ChiTietPhieuTra> dsCT = ctpt_dao.timKiemChiTietBangMaPhieuTra(maPT);
			new PhieuTraPreviewDialog(SwingUtilities.getWindowAncestor(this), pt, dsCT).setVisible(true);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == btnTimKiem) {
			refreshFilters();
			return;
		}
		if (src == btnLamMoi) {
			txtSearch.setText("");
			PlaceholderSupport.addPlaceholder(txtSearch, PLACEHOLDER_TIM_KIEM);
			cbTrangThai.setSelectedIndex(0);
			dateTuNgay.setDate(null);
			dateDenNgay.setDate(null);
			loadDataTablePT();
			modelCTPT.setRowCount(0);
			txtSearch.requestFocus(); // Focus vào ô tìm kiếm sau khi làm mới
			return;
		}
		if (src == btnNhapKho) {
			NhapKho();
			return;
		}
		if (src == btnHuyHang) {
			HuyHang();
			return;
		}

	}

	// sự kiện hủy hàng
	private void HuyHang() {

		int selectRowCT = tblCTPT.getSelectedRow();
		int selectRowPT = tblPT.getSelectedRow();
		if (selectRowCT == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn chi tiết phiếu trả để cập nhật trạng thái!!");
			return;
		}
		// ✅ Đọc cột 8 (Trạng thái - đã dịch do thêm STT)
		String trangThai = modelCTPT.getValueAt(selectRowCT, 8).toString();
		if (trangThai.trim().equalsIgnoreCase("Huỷ hàng") || trangThai.trim().equalsIgnoreCase("Hủy hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu trả này đã ở trạng thái hủy hàng");
			return;
		}

		// đã nhập kho thì không được hủy
		if (trangThai.trim().equalsIgnoreCase("Nhập lại hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu trả này đã nhập kho, không được hủy hàng");
			return;
		}

		if (selectRowPT == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn phiếu trả tương ứng!");
			return;
		}

		// Lấy mã từ cột 1 (do thêm STT)
		String maPT = modelPT.getValueAt(selectRowPT, 1).toString();
		String maHD = modelCTPT.getValueAt(selectRowCT, 1).toString();
		String maLo = modelCTPT.getValueAt(selectRowCT, 2).toString();
		String maDVT = "";
		NhanVien nv = Session.getInstance().getTaiKhoanDangNhap().getNhanVien();

		// Tìm mã DVT từ dsCTPhieuTra
		for (ChiTietPhieuTra ct : dsCTPhieuTra) {
			if (ct.getChiTietHoaDon().getHoaDon().getMaHoaDon().equals(maHD)
					&& ct.getChiTietHoaDon().getLoSanPham().getMaLo().equals(maLo)) {
				maDVT = ct.getDonViTinh() != null ? ct.getDonViTinh().getMaDonViTinh() : "";
				break;
			}
		}

		// ✅ Gọi DAO đúng: trangThaiMoi = 2 (Huỷ hàng) - sẽ tự tạo/nhóm phiếu huỷ
		String kq = pt_dao.capNhatTrangThai_GiaoDich(maPT, maHD, maLo, maDVT, nv, 2);

		if (kq != null && kq.startsWith("OK")) {
			// ✅ Cập nhật lại GUI - cột 8 (do thêm STT)
			modelCTPT.setValueAt("Huỷ hàng", selectRowCT, 8);

			// Hiển thị thông báo có mã phiếu huỷ nếu được tạo
			String tenSP = modelCTPT.getValueAt(selectRowCT, 3).toString(); // Cột 3: Tên SP
			if (kq.contains("|")) {
				String maPhieuHuy = kq.split("\\|")[1];
				String thongBao = String.format(
						"Huỷ hàng thành công!\n\n" +
								"Sản phẩm: %s\n" +
								"Lô: %s\n" +
								"Phiếu huỷ: %s\n\n" +
								"✅ Phiếu huỷ và chi tiết đã được duyệt tự động.",
						tenSP, maLo, maPhieuHuy);
				JOptionPane.showMessageDialog(this, thongBao, "Thành công", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Huỷ hàng thành công!\n\n✅ Phiếu huỷ đã được duyệt tự động.",
						"Thành công", JOptionPane.INFORMATION_MESSAGE);
			}

			// Cập nhật trạng thái phiếu nếu cần
			capNhatTrangThaiPhieuSauKhiCapNhatCTPT(maPT);
		} else {
			JOptionPane.showMessageDialog(this, "Huỷ hàng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}

	}

	// sự kiện nhập lại kho
	private void NhapKho() {

		int selectRowCT = tblCTPT.getSelectedRow();
		int selectRowPT = tblPT.getSelectedRow();
		if (selectRowCT == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn chi tiết phiếu trả để cập nhật trạng thái!!");
			return;
		}

		// ✅ Đọc cột 8 (Trạng thái - đã dịch do thêm STT)
		String trangThai = modelCTPT.getValueAt(selectRowCT, 8).toString();

		if (trangThai.trim().equalsIgnoreCase("Nhập lại hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu trả đã ở trạng thái đã nhập kho!!");
			return;
		}

		// đã hủy thì không được nhập lại
		if (trangThai.trim().equalsIgnoreCase("Huỷ hàng") || trangThai.trim().equalsIgnoreCase("Hủy hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu trả này đã bị hủy, không thể nhập lại kho");
			return;
		}

		if (selectRowPT == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn phiếu trả tương ứng!");
			return;
		}

		// Lấy mã từ cột 1 (do thêm STT)
		String maPT = modelPT.getValueAt(selectRowPT, 1).toString();
		String maHD = modelCTPT.getValueAt(selectRowCT, 1).toString();
		String maLo = modelCTPT.getValueAt(selectRowCT, 2).toString();
		String maDVT = "";
		NhanVien nv = Session.getInstance().getTaiKhoanDangNhap().getNhanVien();

		// 🔍 Tìm mã DVT từ dsCTPhieuTra
		for (ChiTietPhieuTra ct : dsCTPhieuTra) {
			if (ct.getChiTietHoaDon().getHoaDon().getMaHoaDon().equals(maHD)
					&& ct.getChiTietHoaDon().getLoSanPham().getMaLo().equals(maLo)) {
				maDVT = (ct.getDonViTinh() != null) ? ct.getDonViTinh().getMaDonViTinh() : "";
				break;
			}
		}

		// ✅ Lấy thông tin lô sản phẩm TRƯỚC khi cập nhật để hiển thị tồn kho
		LoSanPham_DAO loSanPham_dao = new LoSanPham_DAO();
		LoSanPham loSanPham = loSanPham_dao.timLoTheoMa(maLo);

		int tonKhoTruoc = 0;
		String tenDonViGoc = "đơn vị";
		String tenSP = modelCTPT.getValueAt(selectRowCT, 3).toString(); // Cột 3: Tên SP
		int soLuongTra = 0;

		// Lấy thông tin từ lô sản phẩm
		if (loSanPham != null) {
			tonKhoTruoc = loSanPham.getSoLuongTon();

			// 🔍 Tìm đơn vị gốc của sản phẩm
			QuyCachDongGoi_DAO qcDAO = new QuyCachDongGoi_DAO();
			QuyCachDongGoi qcGoc = qcDAO.timQuyCachGocTheoSanPham(loSanPham.getSanPham().getMaSanPham());
			if (qcGoc != null && qcGoc.getDonViTinh() != null) {
				tenDonViGoc = qcGoc.getDonViTinh().getTenDonViTinh();
			}
		}

		// Lấy số lượng từ chi tiết phiếu trả và quy đổi về đơn vị gốc
		int heSoQuyDoi = 1;
		for (ChiTietPhieuTra ct : dsCTPhieuTra) {
			if (ct.getChiTietHoaDon().getHoaDon().getMaHoaDon().equals(maHD)
					&& ct.getChiTietHoaDon().getLoSanPham().getMaLo().equals(maLo)) {
				soLuongTra = ct.getSoLuong();

				// 🔍 Lấy hệ số quy đổi từ đơn vị tính của chi tiết phiếu trả
				if (ct.getDonViTinh() != null && loSanPham != null) {
					QuyCachDongGoi_DAO qcDAO = new QuyCachDongGoi_DAO();
					QuyCachDongGoi qc = qcDAO.timQuyCachTheoSanPhamVaDonVi(
							loSanPham.getSanPham().getMaSanPham(),
							ct.getDonViTinh().getMaDonViTinh());
					if (qc != null) {
						heSoQuyDoi = qc.getHeSoQuyDoi();
					}
				}
				break;
			}
		}

		// Quy đổi số lượng về đơn vị gốc
		int soLuongTraGoc = soLuongTra * heSoQuyDoi;

		// Gọi DAO: 1 = Nhập lại kho
		String kq = pt_dao.capNhatTrangThai_GiaoDich(maPT, maHD, maLo, maDVT, nv, 1);

		if (kq != null && kq.startsWith("OK")) {
			// ✅ Cập nhật lại GUI - cột 8 (do thêm STT)
			modelCTPT.setValueAt("Nhập lại hàng", selectRowCT, 8);

			// ✅ Hiển thị thông báo chi tiết về số lượng tăng với tồn kho (theo đơn vị gốc)
			int tonKhoSau = tonKhoTruoc + soLuongTraGoc;

			String thongBao = String.format(
					"Nhập kho thành công!\n\n" +
							"Sản phẩm: %s\n" +
							"Lô: %s\n" +
							"Tồn kho: %d + %d = %d (%s)",
					tenSP, maLo, tonKhoTruoc, soLuongTraGoc, tonKhoSau, tenDonViGoc);

			JOptionPane.showMessageDialog(this, thongBao, "Thành công", JOptionPane.INFORMATION_MESSAGE);

			// Cập nhật trạng thái phiếu nếu cần
			capNhatTrangThaiPhieuSauKhiCapNhatCTPT(maPT);

		} else {
			JOptionPane.showMessageDialog(this, "Nhập lại kho thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 🔹 Sau khi cập nhật 1 chi tiết, tự động cập nhật trạng thái của Phiếu Trả nếu
	 * đủ điều kiện. - Nếu TẤT CẢ chi tiết đều không còn trạng thái "Chờ duyệt" -
	 * Thì cập nhật Phiếu Trả sang "Đã duyệt" - Và cập nhật lại bảng GUI đúng theo
	 * model
	 */
	private void capNhatTrangThaiPhieuSauKhiCapNhatCTPT(String maPhieuTra) {

		// Kiểm tra xem tất cả chi tiết đã được xử lý chưa - cột 8 (do thêm STT)
		boolean tatCaDaXuLy = true;
		for (int i = 0; i < modelCTPT.getRowCount(); i++) {
			String trangThai = modelCTPT.getValueAt(i, 8).toString().trim();
			if (trangThai.equalsIgnoreCase("Chờ duyệt")) {
				tatCaDaXuLy = false;
				break;
			}
		}

		if (!tatCaDaXuLy) {
			return;
		}

		int rowView = tblPT.getSelectedRow();
		if (rowView == -1) {
			return;
		}

		int rowModel = tblPT.convertRowIndexToModel(rowView);

		// Cập nhật trạng thái phiếu trả trong DB
		boolean ok = capNhatTrangThaiPhieuTra(maPhieuTra, true);
		if (!ok) {
			JOptionPane.showMessageDialog(null, "Cập nhật trạng thái phiếu trả thất bại!");
			return;
		}

		// Cột 6 (Trạng thái - do thêm STT)
		modelPT.setValueAt("Đã duyệt", rowModel, 6);
		JOptionPane.showMessageDialog(null, "Phiếu trả đã được duyệt tự động!");

	}

	/**
	 * Cập nhật trạng thái phiếu trả trong database
	 */
	private boolean capNhatTrangThaiPhieuTra(String maPT, boolean daDuyet) {
		try {
			String sql = "UPDATE PhieuTra SET DaDuyet = ? WHERE MaPhieuTra = ?";
			java.sql.Connection con = connectDB.getConnection();
			java.sql.PreparedStatement ps = con.prepareStatement(sql);
			ps.setBoolean(1, daDuyet);
			ps.setString(2, maPT);
			int result = ps.executeUpdate();
			ps.close();
			return result > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Auto focus vào ô tìm kiếm khi panel được hiển thị (giống KhuyenMai_GUI)
	 */
	private void addFocusOnShow() {
		addHierarchyListener(e -> {
			if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
				SwingUtilities.invokeLater(() -> {
					txtSearch.requestFocusInWindow();
					txtSearch.selectAll();
				});
			}
		});
	}

}