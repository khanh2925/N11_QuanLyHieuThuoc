/**
 * @author Anh Khoi
 * @version 1.5
 */

package gui.quanly;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import com.toedter.calendar.JDateChooser;

import java.awt.event.HierarchyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

import database.connectDB;
import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.ChiTietPhieuHuy_DAO;
import dao.PhieuHuy_DAO;
import entity.ChiTietPhieuHuy;
import entity.PhieuHuy;
import gui.dialog.PhieuHuyPreviewDialog;

public class QL_HuyHang_GUI extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã phiếu hoặc tên NV (F1/Ctrl+F)";

	private JPanel pnPhieuHuy;
	private JPanel pnHeader;
	private JPanel pnCTPH;
	private JButton btnXuatFile;
	private JTextField txtSearch;
	private DefaultTableModel modelPH;
	private JTable tblPH;
	private JScrollPane scrCTPH;
	private DefaultTableModel modelCTPH;
	private JScrollPane scrPH;
	private JTable tblCTPH;
	private List<PhieuHuy> dsPhieuHuy;
	private List<ChiTietPhieuHuy> dsCTPhieuHuy;
	private PhieuHuy_DAO ph_dao;
	private ChiTietPhieuHuy_DAO ctph_dao;
	private PillButton btnTuChoi;
	private PillButton btnHuyHang;
	private JComboBox<String> cbTrangThai;
	private JDateChooser dateTuNgay;
	private JDateChooser dateDenNgay;
	private PillButton btnLamMoi;
	private PillButton btnTimKiem;
	private TableRowSorter<DefaultTableModel> sorter;
	private JPanel pnBtnCTPH;
	private JSplitPane pnCenter;

	// private static final String TEN_NHA_THUOC = "NHÀ THUỐC HÒA AN"; // đổi tên
	// theo nhà thuốc của bạn

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,###đ");

	public QL_HuyHang_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {

		try {
			connectDB.getInstance().connect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ph_dao = new PhieuHuy_DAO();
		ctph_dao = new ChiTietPhieuHuy_DAO();

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
		btnHuyHang.addActionListener(this);
		btnTuChoi.addActionListener(this);
		btnXuatFile.addActionListener(this);
		tblPH.addMouseListener(this);

		// Thiết lập phím tắt và focus
		thietLapPhimTat();
		addFocusOnShow();
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
				"<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã phiếu, tên NV và bộ lọc</html>");
		pnHeader.add(btnTimKiem);

		btnLamMoi = new PillButton("<html>" + "<center>" + "LÀM MỚI<br>"
				+ "<span style='font-size:10px; color:#888888;'>(F5)</span>" + "</center>" + "</html>");
		btnLamMoi.setBounds(1275, 22, 130, 50);
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
		pnHeader.add(btnLamMoi);

		btnXuatFile = new PillButton("<html>" + "<center>" + "XUẤT FILE<br>"
				+ "<span style='font-size:10px; color:#888888;'>(F8)</span>" + "</center>" + "</html>");
		btnXuatFile.setBounds(1415, 22, 180, 50);
		btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnXuatFile.setToolTipText("<html><b>Phím tắt:</b> F8<br>Xuất danh sách phiếu hủy ra file Excel</html>");
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
	 * Thiết lập phím tắt cho các component
	 */
	/**
	 * Thiết lập phím tắt cho màn hình Quản lý hủy hàng
	 */
	private void thietLapPhimTat() {
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
				loadDataTablePH();
				modelCTPH.setRowCount(0);
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

		// Ctrl+D: Duyệt hủy hàng
		inputMap.put(KeyStroke.getKeyStroke("control D"), "huyHang");
		actionMap.put("huyHang", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HuyHang();
			}
		});

		// Ctrl+R: Từ chối hủy
		inputMap.put(KeyStroke.getKeyStroke("control R"), "tuChoi");
		actionMap.put("tuChoi", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TuChoiHuy();
			}
		});
	}

	/**
	 * Focus vào ô tìm kiếm khi panel được hiển thị
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

	private void TaoPanelCenter() {
		TaoPanelPhieuHuy();
		TaoPanelCTPH();
		pnCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnPhieuHuy, pnCTPH);
		pnCenter.setDividerLocation(350);
		pnCenter.setResizeWeight(0.0);
		add(pnCenter, BorderLayout.CENTER);
	}

	private void TaoPanelPhieuHuy() {
		// ===== CENTER =====
		pnPhieuHuy = new JPanel(new BorderLayout());
		pnPhieuHuy.setLayout(new BorderLayout());
		pnPhieuHuy.add(scrPH);

	}

	private void TaoPanelCTPH() {

		pnCTPH = new JPanel(new BorderLayout());
		pnCTPH.setPreferredSize(new Dimension(600, 1080));

		TitledBorder tbCTPH = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				"Danh sách chi tiết phiếu hủy", TitledBorder.LEFT, TitledBorder.TOP,
				new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
		pnCTPH.setBorder(tbCTPH);

		// ==== PANEL CHỨA 2 BUTTON

		pnBtnCTPH = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

		btnHuyHang = new PillButton("<html>" + "<center>" + "HỦY HÀNG<br>"
				+ "<span style='font-size:10px; color:#888888;'>(Ctrl+D)</span>" + "</center>" + "</html>");
		btnHuyHang.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnHuyHang.setPreferredSize(new Dimension(150, 50));
		btnHuyHang.setToolTipText("<html><b>Phím tắt:</b> Ctrl+D<br>Duyệt hủy hàng cho chi tiết đang chọn</html>");
		btnHuyHang.setEnabled(false); // Mặc định disable

		btnTuChoi = new PillButton("<html>" + "<center>" + "TỪ CHỐI<br>"
				+ "<span style='font-size:10px; color:#888888;'>(Ctrl+R)</span>" + "</center>" + "</html>");
		btnTuChoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTuChoi.setPreferredSize(new Dimension(150, 50));
		btnTuChoi.setToolTipText("<html><b>Phím tắt:</b> Ctrl+R<br>Từ chối hủy hàng cho chi tiết đang chọn</html>");
		btnTuChoi.setEnabled(false); // Mặc định disable

		pnBtnCTPH.add(btnHuyHang);
		pnBtnCTPH.add(btnTuChoi);

		// Thêm panel nút lên trên, bảng CTPH ở giữa
		pnCTPH.add(pnBtnCTPH, BorderLayout.NORTH);
		pnCTPH.add(scrCTPH, BorderLayout.CENTER);
	}

	private void refreshFilters() {
		if (sorter == null)
			return;

		List<RowFilter<Object, Object>> filters = new ArrayList<>();

		// --- Lọc theo text: cột 1 (Mã PH) và 3 (Nhân viên)
		String text = txtSearch.getText().trim();
		// Kiểm tra placeholder: nếu text màu xám (placeholder) hoặc rỗng thì bỏ qua
		if (!text.isEmpty() && !txtSearch.getForeground().equals(Color.GRAY)) {
			// STT, Mã PH, Ngày lập, Nhân viên, Tổng tiền, Trạng thái
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1, 3));
		}

		// --- Lọc theo trạng thái ComboBox: cột 5
		String trangThai = (String) cbTrangThai.getSelectedItem();
		if (trangThai != null && !trangThai.equals("Tất cả")) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(trangThai), 5));
		}

		// --- Lọc theo ngày: cột 2 (Ngày lập)
		java.util.Date tuNgay = dateTuNgay.getDate();
		java.util.Date denNgay = dateDenNgay.getDate();

		if (tuNgay != null || denNgay != null) {
			filters.add(new RowFilter<Object, Object>() {
				@Override
				public boolean include(Entry<? extends Object, ? extends Object> entry) {
					try {
						String ngayStr = entry.getStringValue(2); // Cột Ngày lập (index 2)
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
		// Bảng phiếu huỷ
		String[] phieuHuyCols = { "STT", "Mã PH", "Ngày lập phiếu", "Nhân viên", "Tổng tiền", "Trạng thái" };
		modelPH = new DefaultTableModel(phieuHuyCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblPH = setupTable(modelPH);
		scrPH = new JScrollPane(tblPH);
		TitledBorder tbPH = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				"Danh sách phiếu hủy", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18),
				Color.DARK_GRAY);
		scrPH.setBorder(tbPH);

		// Khởi tạo sorter TRƯỚC khi load data để bộ lọc 30 ngày được áp dụng ngay
		sorter = new TableRowSorter<>(modelPH);
		tblPH.setRowSorter(sorter);

		loadDataTablePH();

		// Bảng chi tiết phiếu huỷ
		String[] cTPhieuCols = { "STT", "Mã lô", "Tên SP", "SL huỷ", "Lý do", "Đơn vị tính", "Thành tiền",
				"Trạng thái" };

		modelCTPH = new DefaultTableModel(cTPhieuCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPH = setupTable(modelCTPH);
		scrCTPH = new JScrollPane(tblCTPH);

		// bắt sự kiện chọn dòng phiếu hủy để tự nạp chi tiết
		tblPH.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblPH.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				loadTableCTPH();
				capNhatTrangThaiNut();
			}
		});

		// bắt sự kiện chọn dòng CTPH để cập nhật trạng thái nút
		tblCTPH.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblCTPH.getSelectionModel().addListSelectionListener(e -> {
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

	// đưa dữ liệu Phiếu Hủy lên bảng với 30 ngày mặc định
	private void loadDataTablePH() {
		dsPhieuHuy = new ArrayList<PhieuHuy>();
		modelPH.setRowCount(0);

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
			dsPhieuHuy = ph_dao.layTatCaPhieuHuy();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int stt = 1;
		for (PhieuHuy ph : dsPhieuHuy) {
			modelPH.addRow(new Object[] { stt++, ph.getMaPhieuHuy(), ph.getNgayLapPhieu().format(fmt),
					ph.getNhanVien().getTenNhanVien(), df.format(ph.getTongTien()), ph.getTrangThaiText() });
		}

		// Áp dụng bộ lọc ngày mặc định
		refreshFilters();
		capNhatTrangThaiNut();
	}

	// đưa dữ liệu CTPH lên bảng
	private void loadTableCTPH() {
		int viewRow = tblPH.getSelectedRow();

		if (viewRow == -1) {
			modelCTPH.setRowCount(0);
			capNhatTrangThaiNut();
			return;
		}

		// Convert view row to model row (quan trọng khi có sorter/filter)
		// Convert view row to model row (quan trọng khi có sorter/filter)
		int modelRow = tblPH.convertRowIndexToModel(viewRow);
		String maPH = modelPH.getValueAt(modelRow, 1).toString(); // Cột 1 là Mã PH

		dsCTPhieuHuy = new ArrayList<ChiTietPhieuHuy>();
		modelCTPH.setRowCount(0);

		try {
			dsCTPhieuHuy = ph_dao.layChiTietTheoMaPhieu(maPH);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int stt = 1;
		for (ChiTietPhieuHuy ctph : dsCTPhieuHuy) {
			// Xử lý trường hợp DonViTinh = null
			String tenDonViTinh = "N/A";
			if (ctph.getDonViTinh() != null) {
				tenDonViTinh = ctph.getDonViTinh().getTenDonViTinh();
			}

			modelCTPH.addRow(new Object[] { stt++, ctph.getLoSanPham().getMaLo(),
					ctph.getLoSanPham().getSanPham().getTenSanPham(), ctph.getSoLuongHuy(), ctph.getLyDoChiTiet(),
					tenDonViTinh, df.format(ctph.getThanhTien()), ctph.getTrangThaiText() });
		}

		capNhatTrangThaiNut();
	}

	/**
	 * Cấu hình renderer cho các cột trong bảng - Bảng phiếu hủy: Đã duyệt = xanh
	 * đậm, Chờ duyệt = đỏ nghiêng - Bảng chi tiết: Đã hủy hàng = xanh đậm, Đã từ
	 * chối hủy = đỏ đậm, Chờ duyệt = đỏ nghiêng
	 */
	private void configureTableRenderers() {
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		// Bảng phiếu hủy: căn giữa cho Mã PH, Ngày lập, Trạng thái; căn phải cho Tổng
		// tiền
		// Bảng phiếu hủy
		tblPH.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblPH.getColumnModel().getColumn(1).setCellRenderer(center); // Mã PH
		tblPH.getColumnModel().getColumn(2).setCellRenderer(center); // Ngày lập
		tblPH.getColumnModel().getColumn(4).setCellRenderer(right); // Tổng tiền

		// Cột trạng thái phiếu hủy (index 5)
		tblPH.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);

				String text = value == null ? "" : value.toString().trim();

				if (text.equalsIgnoreCase("Đã duyệt")) {
					lbl.setForeground(new Color(0x2E7D32)); // Xanh lá đậm
					lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
				} else if (text.equalsIgnoreCase("Chờ duyệt")) {
					lbl.setForeground(Color.RED);
					lbl.setFont(new Font("Segoe UI", Font.ITALIC, 15));
				} else {
					lbl.setForeground(Color.BLACK);
					lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
				}

				return lbl;
			}
		});

		// Bảng chi tiết
		tblCTPH.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblCTPH.getColumnModel().getColumn(1).setCellRenderer(center); // Mã lô
		tblCTPH.getColumnModel().getColumn(3).setCellRenderer(right); // SL hủy
		tblCTPH.getColumnModel().getColumn(6).setCellRenderer(right); // Thành tiền

		// Cột trạng thái chi tiết (index 7)
		tblCTPH.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);

				String text = value == null ? "" : value.toString().trim();

				if (text.equalsIgnoreCase("Đã hủy hàng")) {
					lbl.setForeground(new Color(0x2E7D32)); // Xanh lá đậm
					lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
				} else if (text.equalsIgnoreCase("Đã từ chối hủy")) {
					lbl.setForeground(Color.RED);
					lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
				} else if (text.equalsIgnoreCase("Chờ duyệt")) {
					lbl.setForeground(Color.RED);
					lbl.setFont(new Font("Segoe UI", Font.ITALIC, 15));
				} else {
					lbl.setForeground(Color.BLACK);
					lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
				}

				return lbl;
			}
		});
	}

	/**
	 * Cập nhật trạng thái hiển thị các nút dựa trên việc có chọn dòng hay không -
	 * Không chọn dòng CTPH: Disable nút Hủy Hàng và Từ Chối - Có chọn dòng CTPH (và
	 * PH): Enable nút Hủy Hàng và Từ Chối - Chi tiết đã xử lý (Đã hủy hàng / Đã từ
	 * chối hủy): Disable cả 2 nút
	 */
	private void capNhatTrangThaiNut() {
		// Null check để tránh NPE khi khởi tạo
		if (tblPH == null || tblCTPH == null || btnHuyHang == null || btnTuChoi == null) {
			return;
		}

		int rowPH = tblPH.getSelectedRow();
		int rowCTPH = tblCTPH.getSelectedRow();
		boolean coDongCTPHDuocChon = (rowCTPH != -1);
		boolean coDongPHDuocChon = (rowPH != -1);

		// Kiểm tra trạng thái chi tiết - chỉ enable nếu còn "Chờ duyệt"
		boolean chiTietChuaXuLy = false;
		if (coDongCTPHDuocChon && modelCTPH != null) {
			String trangThai = modelCTPH.getValueAt(rowCTPH, 7).toString().trim(); // Cột 7 - Trạng thái
			chiTietChuaXuLy = trangThai.equalsIgnoreCase("Chờ duyệt");
		}

		// Buttons only enabled when a CTPH row with status "Chờ duyệt" is selected
		btnHuyHang.setEnabled(coDongCTPHDuocChon && coDongPHDuocChon && chiTietChuaXuLy);
		btnTuChoi.setEnabled(coDongCTPHDuocChon && coDongPHDuocChon && chiTietChuaXuLy);
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
			// Xóa cache
			ph_dao.clearCache();
			loadDataTablePH();
			modelCTPH.setRowCount(0);
			txtSearch.requestFocus(); // Focus vào ô tìm kiếm sau khi làm mới
			return;
		}
		if (src == btnHuyHang) {
			HuyHang();
			return;
		}
		if (src == btnTuChoi) {
			TuChoiHuy();
			return;
		}
		if (src == btnXuatFile) {
			xuatExcel();
			return;
		}

	}

	// sự kiện từ chối hủy hàng
	private void TuChoiHuy() {

		int selectRowCT = tblCTPH.getSelectedRow();
		int selectRowPH = tblPH.getSelectedRow();
		if (selectRowCT == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn chi tiết phiếu hủy để cập nhật trạng thái!!");
			return;
		}
		// ✅ Đọc cột 7 (Trạng thái)
		String trangThai = modelCTPH.getValueAt(selectRowCT, 7).toString();
		if (trangThai.trim().equals("Đã từ chối")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu hủy này đã ở trạng thái từ chối hủy");
			return;
		}

		// đã hủy hàng thì không được cập nhật trạng thái
		if (trangThai.trim().equals("Đã hủy hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu hủy này đã hủy hàng, không được cập nhật trạng thái");
			return;
		}
		String maPH = modelPH.getValueAt(selectRowPH, 1).toString(); // Mã PH index 1
		String maLo = modelCTPH.getValueAt(selectRowCT, 1).toString(); // Mã lô index 1

		if (ctph_dao.capNhatTrangThaiChiTiet(maPH, maLo, 3)) {
			// ✅ Update cột 7 (Trạng thái)
			modelCTPH.setValueAt("Đã từ chối hủy", selectRowCT, 7);
			JOptionPane.showMessageDialog(null, "Đã từ chối hủy hàng!");

			capNhatTrangThaiPhieuSauKhiCapNhatCTPH(maPH);
		} else {
			JOptionPane.showMessageDialog(null, "Không thể từ chối hủy hàng");
		}

	}

	// sự kiện hủy hàng
	private void HuyHang() {

		int selectRowCT = tblCTPH.getSelectedRow();
		int selectRowPH = tblPH.getSelectedRow();
		if (selectRowCT == -1) {
			JOptionPane.showMessageDialog(null, "Vui lòng chọn chi tiết phiếu hủy để cập nhật trạng thái!!");
			return;
		}
		// ✅ Đọc cột 7 (Trạng thái)
		String trangThai = modelCTPH.getValueAt(selectRowCT, 7).toString();

		if (trangThai.trim().equals("Đã hủy hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu hủy đã ở trạng thái đã hủy!!");
			return;
		}
		// Không cho phép chuyển từ "Đã từ chối hủy" sang "Hủy hàng"
		if (trangThai.trim().equals("Đã từ chối hủy")) {
			JOptionPane.showMessageDialog(null,
					"Chi tiết phiếu hủy đã bị từ chối, không thể chuyển sang trạng thái hủy hàng!");
			return;
		}
		String maPH = modelPH.getValueAt(selectRowPH, 1).toString(); // Mã PH index 1
		String maLo = modelCTPH.getValueAt(selectRowCT, 1).toString(); // Mã lô index 1

		if (ctph_dao.capNhatTrangThaiChiTiet(maPH, maLo, 2)) {
			// ✅ Update cột 7 (Trạng thái)
			modelCTPH.setValueAt("Đã hủy hàng", selectRowCT, 7);
			JOptionPane.showMessageDialog(null, "Hủy hàng thành công!");

			capNhatTrangThaiPhieuSauKhiCapNhatCTPH(maPH);
		} else {
			JOptionPane.showMessageDialog(null, "Hủy hàng thất bại");
		}

	}

	/**
	 * 🔹 Sau khi cập nhật 1 chi tiết, tự động cập nhật trạng thái của Phiếu Hủy nếu
	 * đủ điều kiện. - Nếu TẤT CẢ chi tiết đều không còn trạng thái "Chờ duyệt" -
	 * Thì cập nhật Phiếu Hủy sang "Đã duyệt" - Và cập nhật lại bảng GUI đúng theo
	 * model
	 */
	private void capNhatTrangThaiPhieuSauKhiCapNhatCTPH(String maPhieuHuy) {

		boolean duDuLieuDeDuyet = ph_dao.checkTrangThai(maPhieuHuy);

		if (!duDuLieuDeDuyet) {
			return;
		}

		int rowView = tblPH.getSelectedRow();
		if (rowView == -1) {
			return;
		}

		int rowModel = tblPH.convertRowIndexToModel(rowView);

		boolean ok = ph_dao.capNhatTrangThaiPhieuHuy(maPhieuHuy);
		if (!ok) {
			JOptionPane.showMessageDialog(null, "Cập nhật trạng thái phiếu huỷ thất bại!");
			return;
		}

		modelPH.setValueAt("Đã duyệt", rowModel, 5); // Cột trạng thái index 5

	}

	// sự kiện xuất file
	// sự kiện xuất file
	// 🎯 Xuất EXCEL cho phiếu hủy
	private void xuatExcel() {
		if (modelPH.getRowCount() == 0) {
			JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
		fileChooser.setSelectedFile(new File(
				"DanhSachPhieuHuy_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx"));
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
			// ===== SHEET 1: DANH SÁCH PHIẾU HỦY =====
			Sheet sheetPH = workbook.createSheet("Danh sách phiếu hủy");

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
			Row headerRow = sheetPH.createRow(0);
			String[] headers = { "Mã PH", "Ngày lập phiếu", "Nhân viên", "Tổng tiền", "Trạng thái" };
			for (int i = 0; i < headers.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			// Điền dữ liệu từ bảng
			for (int row = 0; row < modelPH.getRowCount(); row++) {
				Row dataRow = sheetPH.createRow(row + 1);

				// Cột 0: Mã PH
				Cell cell0 = dataRow.createCell(0);
				cell0.setCellValue(modelPH.getValueAt(row, 0).toString());
				cell0.setCellStyle(dataStyle);

				// Cột 1: Ngày lập
				Cell cell1 = dataRow.createCell(1);
				cell1.setCellValue(modelPH.getValueAt(row, 1).toString());
				cell1.setCellStyle(dataStyle);

				// Cột 2: Nhân viên
				Cell cell2 = dataRow.createCell(2);
				cell2.setCellValue(modelPH.getValueAt(row, 2).toString());
				cell2.setCellStyle(dataStyle);

				// Cột 3: Tổng tiền
				Cell cell3 = dataRow.createCell(3);
				cell3.setCellValue(modelPH.getValueAt(row, 3).toString());
				cell3.setCellStyle(moneyStyle); // Format tiền

				// Cột 4: Trạng thái
				Cell cell4 = dataRow.createCell(4);
				cell4.setCellValue(modelPH.getValueAt(row, 4).toString());
				cell4.setCellStyle(dataStyle);
			}

			// Auto-size columns
			for (int i = 0; i < headers.length; i++) {
				sheetPH.autoSizeColumn(i);
			}

			// ===== SHEET 2: CHI TIẾT PHIẾU HỦY (nếu có dòng được chọn) =====
			if (modelCTPH.getRowCount() > 0) {
				Sheet sheetCTPH = workbook.createSheet("Chi tiết phiếu hủy");

				// Header chi tiết
				Row headerRowCT = sheetCTPH.createRow(0);
				String[] headersCT = { "Mã lô", "Tên SP", "SL hủy", "Lý do", "Đơn vị tính", "Thành tiền",
						"Trạng thái" };
				for (int i = 0; i < headersCT.length; i++) {
					Cell cell = headerRowCT.createCell(i);
					cell.setCellValue(headersCT[i]);
					cell.setCellStyle(headerStyle);
				}

				// Điền dữ liệu chi tiết
				for (int row = 0; row < modelCTPH.getRowCount(); row++) {
					Row dataRow = sheetCTPH.createRow(row + 1);
					for (int col = 0; col < modelCTPH.getColumnCount(); col++) {
						Cell cell = dataRow.createCell(col);
						Object value = modelCTPH.getValueAt(row, col);
						cell.setCellValue(value != null ? value.toString() : "");
						cell.setCellStyle(dataStyle);
					}
				}

				// Auto-size columns
				for (int i = 0; i < headersCT.length; i++) {
					sheetCTPH.autoSizeColumn(i);
				}
			}

			// Ghi file
			try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
				workbook.write(fos);
			}

			JOptionPane.showMessageDialog(this, "Xuất Excel thành công!\nFile: " + fileToSave.getAbsolutePath(),
					"Thành công", JOptionPane.INFORMATION_MESSAGE);

			// Mở file sau khi xuất
			if (java.awt.Desktop.isDesktopSupported()) {
				java.awt.Desktop.getDesktop().open(fileToSave);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel:\n" + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản lý phiếu hủy hàng");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new QL_HuyHang_GUI());
			frame.setVisible(true);
		});
	}

	/**
	 * Mở dialog xem phiếu hủy
	 */
	private void xemPhieuHuy(String maPH) {
		PhieuHuy ph = ph_dao.layTheoMa(maPH);
		if (ph != null) {
			List<ChiTietPhieuHuy> dsCT = ctph_dao.timKiemChiTietPhieuHuyBangMa(maPH);
			ph.setChiTietPhieuHuyList(dsCT);
			new PhieuHuyPreviewDialog(SwingUtilities.getWindowAncestor(this), ph).setVisible(true);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == tblPH && e.getClickCount() == 2) {
			int row = tblPH.getSelectedRow();
			if (row != -1) {
				int modelRow = tblPH.convertRowIndexToModel(row);
				String maPH = modelPH.getValueAt(modelRow, 1).toString(); // MaPH at index 1
				xemPhieuHuy(maPH);
			}
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
}
