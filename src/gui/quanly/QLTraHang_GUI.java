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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.toedter.calendar.JDateChooser;

import component.border.RoundedBorder;
import dao.ChiTietPhieuTra_DAO;
import dao.PhieuTra_DAO;
import entity.ChiTietPhieuTra;
import entity.NhanVien;
import entity.PhieuTra;
import entity.Session;

public class QLTraHang_GUI extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã phiếu, tên KH hoặc SĐT... (F1 / Ctrl+F)";

	private JPanel pnPhieuTra;
	private JPanel pnHeader;
	private JPanel pnCTPT;
	private PillButton btnXuatFile;
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

		// Event listeners
		txtSearch.addActionListener(e -> refreshFilters()); // Nhấn Enter để tìm kiếm
		cbTrangThai.addActionListener(e -> refreshFilters());
		dateTuNgay.addPropertyChangeListener("date", e -> refreshFilters());
		dateDenNgay.addPropertyChangeListener("date", e -> refreshFilters());
		btnTimKiem.addActionListener(this);
		btnLamMoi.addActionListener(this);
		btnNhapKho.addActionListener(this);
		btnHuyHang.addActionListener(this);
		btnXuatFile.addActionListener(this);
		tblCTPT.addMouseListener(this);
		tblPT.addMouseListener(this);

		setupKeyboardShortcuts(); // Thiết lập phím tắt

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
		btnTimKiem = new PillButton(
				"<html>" +
						"<center>" +
						"TÌM KIẾM<br>" +
						"<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
						"</center>" +
						"</html>");
		btnTimKiem.setBounds(1135, 22, 130, 50);
		btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTimKiem.setToolTipText(
				"<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã phiếu, SĐT và bộ lọc</html>");
		pnHeader.add(btnTimKiem);

		btnLamMoi = new PillButton(
				"<html>" +
						"<center>" +
						"LÀM MỚI<br>" +
						"<span style='font-size:10px; color:#888888;'>(F5)</span>" +
						"</center>" +
						"</html>");
		btnLamMoi.setBounds(1275, 22, 130, 50);
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
		pnHeader.add(btnLamMoi);

		btnXuatFile = new PillButton(
				"<html>" +
						"<center>" +
						"XUẤT FILE<br>" +
						"<span style='font-size:10px; color:#888888;'>(F8)</span>" +
						"</center>" +
						"</html>");
		btnXuatFile.setBounds(1415, 22, 180, 50);
		btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnXuatFile.setToolTipText("<html><b>Phím tắt:</b> F8<br>Xuất danh sách phiếu trả ra file Excel</html>");
		pnHeader.add(btnXuatFile);
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

		// F8: Xuất file Excel
		inputMap.put(KeyStroke.getKeyStroke("F8"), "xuatFile");
		actionMap.put("xuatFile", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuatExcel();
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

		btnNhapKho = new PillButton("Nhập lại kho");
		btnNhapKho.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnNhapKho
				.setToolTipText("<html><b>Nhập lại kho</b><br>Nhập hàng trả về kho sau khi kiểm tra chất lượng</html>");

		btnHuyHang = new PillButton("Hủy hàng");
		btnHuyHang.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnHuyHang.setToolTipText("<html><b>Hủy hàng</b><br>Hủy hàng trả không đạt chất lượng</html>");

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

		// --- Lọc theo text: cột 0 (Mã PT), cột 1 (Khách hàng), cột 2 (SĐT), cột 4
		// (Người trả)
		String text = txtSearch.getText().trim();
		if (!text.isEmpty() && !txtSearch.getForeground().equals(Color.GRAY)) {
			String searchTextNoSign = removeDiacritics(text);

			// Custom RowFilter hỗ trợ tìm kiếm tiếng Việt không dấu
			filters.add(new RowFilter<Object, Object>() {
				@Override
				public boolean include(Entry<? extends Object, ? extends Object> entry) {
					// Kiểm tra các cột: 0 (Mã PT), 1 (Khách hàng), 2 (SĐT), 4 (Người trả)
					int[] colsToCheck = { 0, 1, 2, 4 };
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

		// --- Lọc theo trạng thái ComboBox: cột 5 (đã dịch do thêm cột SĐT)
		String trangThai = (String) cbTrangThai.getSelectedItem();
		if (trangThai != null && !trangThai.equals("Tất cả")) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(trangThai), 5));
		}

		// --- Lọc theo ngày: cột 3 (Ngày lập - đã dịch do thêm cột SĐT)
		java.util.Date tuNgay = dateTuNgay.getDate();
		java.util.Date denNgay = dateDenNgay.getDate();

		if (tuNgay != null || denNgay != null) {
			filters.add(new RowFilter<Object, Object>() {
				@Override
				public boolean include(Entry<? extends Object, ? extends Object> entry) {
					try {
						String ngayStr = entry.getStringValue(3); // Cột Ngày lập (đã dịch)
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
		// Bảng phiếu trả - Thêm cột SĐT ẩn để tìm kiếm
		String[] phieuTraCols = { "Mã PT", "Khách hàng", "SĐT", "Ngày lập", "Người trả", "Trạng thái",
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

		// Ẩn cột SĐT (cột 2)
		tblPT.getColumnModel().getColumn(2).setMinWidth(0);
		tblPT.getColumnModel().getColumn(2).setMaxWidth(0);
		tblPT.getColumnModel().getColumn(2).setPreferredWidth(0);

		loadDataTablePT();

		// Bảng chi tiết phiếu trả
		String[] cTPhieuCols = { "Mã hóa đơn", "Mã lô", "Tên SP", "Hạn dùng", "SL trả", "Lý do", "Đơn vị tính",
				"Trạng thái" };

		modelCTPT = new DefaultTableModel(cTPhieuCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPT = setupTable(modelCTPT);
		scrCTPT = new JScrollPane(tblCTPT);

		// ===== Format chung (giữ nguyên style cũ của bạn) =====
		formatTable(tblPT);
		formatTable(tblCTPT);

		// ===================================================================
		// ---- 1) Trạng thái bảng PHIẾU TRẢ: Đã duyệt = xanh, Chờ duyệt = đỏ ----
		// Cột 5 (Trạng thái - đã dịch do thêm cột SĐT)
		tblPT.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);

				String text = value == null ? "" : value.toString().trim();

				if (text.equalsIgnoreCase("Đã duyệt")) {
					lbl.setForeground(new Color(0, 128, 0)); // Xanh lá
				} else if (text.equalsIgnoreCase("Chờ duyệt") || text.equalsIgnoreCase("Đang chờ duyệt")) {
					lbl.setForeground(Color.RED);
				} else {
					lbl.setForeground(Color.BLACK);
				}

				// Không đụng tới background để vẫn giữ màu chọn dòng
				return lbl;
			}
		});

		// ---- 2) Trạng thái bảng CHI TIẾT: Nhập lại hàng = xanh, Hủy hàng = đỏ ----
		// Cột 7 (Trạng thái)
		tblCTPT.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);

				String text = value == null ? "" : value.toString().trim();

				if (text.equalsIgnoreCase("Nhập lại hàng")) {
					lbl.setForeground(new Color(0, 128, 0)); // Xanh lá
				} else if (text.equalsIgnoreCase("Huỷ hàng") || text.equalsIgnoreCase("Hủy hàng")) {
					lbl.setForeground(Color.RED);
				} else { // Chờ duyệt, hoặc trạng thái khác
					lbl.setForeground(Color.BLACK);
				}

				return lbl;
			}
		});

		// bắt sự kiện chọn dòng để tự nạp chi tiết
		tblPT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblPT.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				loadTableCTPT();
		});

		// sắp xếp tăng giảm tự động khi click vào header
		sorter = new TableRowSorter<>(modelPT);
		tblPT.setRowSorter(sorter);
	}

	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font 16
		table.setRowHeight(35); // Cao 35
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setGridColor(new Color(230, 230, 230));

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);
		return table;
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 16));
		table.getTableHeader().setBorder(null);

		table.setRowHeight(28);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
			else if (col.contains("ngày") || col.contains("hạn"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else
				m.getColumn(i).setCellRenderer(leftRenderer);
		}

		table.getTableHeader().setReorderingAllowed(false);
	}

	// đưa dữ liệu Phiếu Trả lên bảng
	private void loadDataTablePT() {
		dsPhieuTra = new ArrayList<PhieuTra>();
		modelPT.setRowCount(0);

		try {
			dsPhieuTra = pt_dao.layTatCaPhieuTra();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (PhieuTra pt : dsPhieuTra) {
			String khachHang = pt.getKhachHang() != null ? pt.getKhachHang().getTenKhachHang() : "N/A";
			String sdt = pt.getKhachHang() != null ? pt.getKhachHang().getSoDienThoai() : ""; // Thêm SĐT (ẩn)
			String nhanVien = pt.getNhanVien() != null ? pt.getNhanVien().getTenNhanVien() : "N/A";

			modelPT.addRow(new Object[] { pt.getMaPhieuTra(), khachHang, sdt, // Cột SĐT (ẩn)
					pt.getNgayLap().format(fmt), nhanVien, pt.getTrangThaiText(), df.format(pt.getTongTienHoan()) });
		}

	}

	// đưa dữ liệu CTPT lên bảng
	private void loadTableCTPT() {
		int selectRow = tblPT.getSelectedRow();

		if (selectRow == -1) {
			return;
		}

		String maPT = modelPT.getValueAt(selectRow, 0).toString();

		dsCTPhieuTra = new ArrayList<ChiTietPhieuTra>();
		modelCTPT.setRowCount(0);

		try {
			dsCTPhieuTra = ctpt_dao.timKiemChiTietBangMaPhieuTra(maPT);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

			modelCTPT.addRow(
					new Object[] { maHD, maLo, tenSP, hanDung, ctpt.getSoLuong(), ctpt.getLyDoChiTiet(), tenDonViTinh, // Cột
																														// 6:
																														// Đơn
																														// vị
																														// tính
							ctpt.getTrangThaiText() // Cột 7: Trạng thái
					});
		}

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
		// TODO Auto-generated method stub

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
		if (src == btnXuatFile) {
			xuatExcel();
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
		// ✅ Đọc cột 7 (Trạng thái)
		String trangThai = modelCTPT.getValueAt(selectRowCT, 7).toString();
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

		String maPT = modelPT.getValueAt(selectRowPT, 0).toString();
		String maHD = modelCTPT.getValueAt(selectRowCT, 0).toString();
		String maLo = modelCTPT.getValueAt(selectRowCT, 1).toString();
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
			// ✅ Cập nhật lại GUI
			modelCTPT.setValueAt("Huỷ hàng", selectRowCT, 7);

			// Hiển thị thông báo có mã phiếu huỷ nếu được tạo
			if (kq.contains("|")) {
				String maPhieuHuy = kq.split("\\|")[1];
				JOptionPane.showMessageDialog(null, "Hủy hàng thành công!\nĐã thêm vào phiếu huỷ: " + maPhieuHuy);
			} else {
				JOptionPane.showMessageDialog(null, "Hủy hàng thành công");
			}

			// Cập nhật trạng thái phiếu nếu cần
			capNhatTrangThaiPhieuSauKhiCapNhatCTPT(maPT);
		} else {
			JOptionPane.showMessageDialog(null, "Hủy hàng thất bại");
		}

	}

	// sự kiện nhập lại kho
	// sự kiện nhập lại kho
	private void NhapKho() {

		int selectRowCT = tblCTPT.getSelectedRow();
		int selectRowPT = tblPT.getSelectedRow();
		if (selectRowCT == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn chi tiết phiếu trả để cập nhật trạng thái!!");
			return;
		}

		// ✅ Đọc cột 7 (Trạng thái)
		String trangThai = modelCTPT.getValueAt(selectRowCT, 7).toString();

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

		String maPT = modelPT.getValueAt(selectRowPT, 0).toString();
		String maHD = modelCTPT.getValueAt(selectRowCT, 0).toString();
		String maLo = modelCTPT.getValueAt(selectRowCT, 1).toString();
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

		// Gọi DAO: 1 = Nhập lại kho
		String kq = pt_dao.capNhatTrangThai_GiaoDich(maPT, maHD, maLo, maDVT, nv, 1);

		if (kq != null && kq.startsWith("OK")) {
			// ✅ Cập nhật lại GUI
			modelCTPT.setValueAt("Nhập lại hàng", selectRowCT, 7);
			JOptionPane.showMessageDialog(null, "Nhập lại kho thành công");

			// Cập nhật trạng thái phiếu nếu cần
			capNhatTrangThaiPhieuSauKhiCapNhatCTPT(maPT);

		} else {
			JOptionPane.showMessageDialog(null, "Nhập lại kho thất bại");
		}
	}

	/**
	 * 🔹 Sau khi cập nhật 1 chi tiết, tự động cập nhật trạng thái của Phiếu Trả nếu
	 * đủ điều kiện. - Nếu TẤT CẢ chi tiết đều không còn trạng thái "Chờ duyệt" -
	 * Thì cập nhật Phiếu Trả sang "Đã duyệt" - Và cập nhật lại bảng GUI đúng theo
	 * model
	 */
	private void capNhatTrangThaiPhieuSauKhiCapNhatCTPT(String maPhieuTra) {

		// Kiểm tra xem tất cả chi tiết đã được xử lý chưa
		boolean tatCaDaXuLy = true;
		for (int i = 0; i < modelCTPT.getRowCount(); i++) {
			String trangThai = modelCTPT.getValueAt(i, 7).toString().trim();
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

		modelPT.setValueAt("Đã duyệt", rowModel, 5); // Cột 5 (Trạng thái - đã dịch do thêm cột SĐT)
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
	 * Xuất danh sách phiếu trả ra file Excel
	 */
	private void xuatExcel() {
		if (modelPT.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
		fileChooser.setSelectedFile(new File(
				"DanhSachPhieuTra_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx"));
		fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

		int userSelection = fileChooser.showSaveDialog(this);
		if (userSelection != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File fileToSave = fileChooser.getSelectedFile();
		if (!fileToSave.getName().endsWith(".xlsx")) {
			fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
		}

		try (Workbook workbook = new XSSFWorkbook()) {
			// ===== SHEET 1: DANH SÁCH PHIẾU TRẢ =====
			Sheet sheetPT = workbook.createSheet("Danh sách phiếu trả");

			// Style cho tiêu đề
			CellStyle headerStyle = workbook.createCellStyle();
			org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerFont.setColor(IndexedColors.WHITE.getIndex());
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			// Style cho dữ liệu
			CellStyle dataStyle = workbook.createCellStyle();
			dataStyle.setBorderBottom(BorderStyle.THIN);
			dataStyle.setBorderTop(BorderStyle.THIN);
			dataStyle.setBorderLeft(BorderStyle.THIN);
			dataStyle.setBorderRight(BorderStyle.THIN);

			// Style cho số tiền
			CellStyle moneyStyle = workbook.createCellStyle();
			moneyStyle.cloneStyleFrom(dataStyle);
			moneyStyle.setAlignment(HorizontalAlignment.RIGHT);

			// Tạo header
			Row headerRow = sheetPT.createRow(0);
			String[] headers = { "Mã PT", "Khách hàng", "Ngày lập", "Người trả", "Trạng thái", "Tổng tiền hoàn" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			// Điền dữ liệu từ bảng (bỏ cột SĐT ẩn)
			for (int row = 0; row < modelPT.getRowCount(); row++) {
				Row dataRow = sheetPT.createRow(row + 1);

				// Cột 0: Mã PT
				Cell cell0 = dataRow.createCell(0);
				cell0.setCellValue(modelPT.getValueAt(row, 0).toString());
				cell0.setCellStyle(dataStyle);

				// Cột 1: Khách hàng
				Cell cell1 = dataRow.createCell(1);
				cell1.setCellValue(modelPT.getValueAt(row, 1).toString());
				cell1.setCellStyle(dataStyle);

				// Cột 2: Ngày lập (bỏ qua cột 2 - SĐT ẩn, lấy cột 3)
				Cell cell2 = dataRow.createCell(2);
				cell2.setCellValue(modelPT.getValueAt(row, 3).toString());
				cell2.setCellStyle(dataStyle);

				// Cột 3: Người trả (cột 4)
				Cell cell3 = dataRow.createCell(3);
				cell3.setCellValue(modelPT.getValueAt(row, 4).toString());
				cell3.setCellStyle(dataStyle);

				// Cột 4: Trạng thái (cột 5)
				Cell cell4 = dataRow.createCell(4);
				cell4.setCellValue(modelPT.getValueAt(row, 5).toString());
				cell4.setCellStyle(dataStyle);

				// Cột 5: Tổng tiền hoàn (cột 6)
				Cell cell5 = dataRow.createCell(5);
				cell5.setCellValue(modelPT.getValueAt(row, 6).toString());
				cell5.setCellStyle(moneyStyle);
			}

			// Auto-size columns
			for (int i = 0; i < headers.length; i++) {
				sheetPT.autoSizeColumn(i);
			}

			// ===== SHEET 2: CHI TIẾT PHIẾU TRẢ (nếu có dòng được chọn) =====
			if (modelCTPT.getRowCount() > 0) {
				Sheet sheetCTPT = workbook.createSheet("Chi tiết phiếu trả");

				// Header chi tiết
				Row headerRowCT = sheetCTPT.createRow(0);
				String[] headersCT = { "Mã hóa đơn", "Mã lô", "Tên SP", "Hạn dùng", "SL trả", "Lý do", "Đơn vị tính",
						"Trạng thái" };
				for (int i = 0; i < headersCT.length; i++) {
					Cell cell = headerRowCT.createCell(i);
					cell.setCellValue(headersCT[i]);
					cell.setCellStyle(headerStyle);
				}

				// Điền dữ liệu chi tiết
				for (int row = 0; row < modelCTPT.getRowCount(); row++) {
					Row dataRow = sheetCTPT.createRow(row + 1);
					for (int col = 0; col < modelCTPT.getColumnCount(); col++) {
						Cell cell = dataRow.createCell(col);
						Object value = modelCTPT.getValueAt(row, col);
						cell.setCellValue(value != null ? value.toString() : "");
						cell.setCellStyle(dataStyle);
					}
				}

				// Auto-size columns
				for (int i = 0; i < headersCT.length; i++) {
					sheetCTPT.autoSizeColumn(i);
				}
			}

			// Ghi file
			try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
				workbook.write(fos);
			}

			JOptionPane.showMessageDialog(this,
					"Xuất Excel thành công!\nFile: " + fileToSave.getAbsolutePath(),
					"Thành công", JOptionPane.INFORMATION_MESSAGE);

			// Mở file sau khi xuất
			if (java.awt.Desktop.isDesktopSupported()) {
				java.awt.Desktop.getDesktop().open(fileToSave);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Lỗi khi xuất file Excel:\n" + e.getMessage(),
					"Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

}