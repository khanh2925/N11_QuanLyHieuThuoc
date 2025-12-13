package gui.tracuu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.ChiTietPhieuTra_DAO;
import dao.PhieuTra_DAO;
import entity.ChiTietPhieuTra;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTra;
import gui.dialog.PhieuTraPreviewDialog;

/**
 * @author Thanh Kha
 * @version 1.9
 */
@SuppressWarnings("serial")
public class TraCuuDonTraHang_GUI extends JPanel implements ActionListener {

	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã phiếu, tên KH hoặc SĐT... (F1 / Ctrl+F)";
	// DAO
	private final PhieuTra_DAO phieuTraDAO;
	private final ChiTietPhieuTra_DAO chiTietPhieuTraDAO;

	// DATA
	private List<PhieuTra> allPhieuTra = new ArrayList<>();

	private JPanel pnHeader;

	private JTextField txtTimKiem;

	private JDateChooser dateTuNgay;
	private JDateChooser dateDenNgay;
	private JComboBox<String> cbTrangThai;

	private PillButton btnTimKiem;
	private PillButton btnLamMoi;
	private PillButton btnXemPhieuTra;

	private JPanel pnCenter;
	private JTable tblPhieuTra;
	private DefaultTableModel modelPhieuTra;

	private JTable tblChiTiet;
	private DefaultTableModel modelChiTiet;

	// FORMATTERS
	private final DecimalFormat df = new DecimalFormat("#,### đ");
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public TraCuuDonTraHang_GUI() {
		setPreferredSize(new Dimension(1537, 850));

		phieuTraDAO = new PhieuTra_DAO();
		chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();
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

	// =====================================================================================
	// HEADER - QUICK + ADVANCED FILTER (MigLayout)
	// =====================================================================================
	private void taoHeader() {

		pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(1073, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));

		// --- 1. Ô TÌM KIẾM TO (Bên trái) ---
		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtTimKiem.setBounds(25, 17, 480, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setBackground(Color.WHITE);
		txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
		pnHeader.add(txtTimKiem);

		// --- 2. BỘ LỌC NGÀY (Ở giữa) ---

		// Từ ngày
		JLabel lblTu = new JLabel("Từ:");
		lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblTu.setBounds(525, 28, 35, 35);
		pnHeader.add(lblTu);

		dateTuNgay = new JDateChooser();
		dateTuNgay.setDateFormatString("dd/MM/yyyy");
		dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dateTuNgay.setBounds(560, 28, 140, 38);
		pnHeader.add(dateTuNgay);

		// Đến ngày
		JLabel lblDen = new JLabel("Đến:");
		lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblDen.setBounds(710, 28, 40, 35);
		pnHeader.add(lblDen);

		dateDenNgay = new JDateChooser();
		dateDenNgay.setDateFormatString("dd/MM/yyyy");
		dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		dateDenNgay.setBounds(750, 28, 140, 38);
		pnHeader.add(dateDenNgay);

		// --- 3. TRẠNG THÁI ---
		addFilterLabel("Trạng thái:", 905, 28, 85, 35);
		cbTrangThai = new JComboBox<>();
		setupComboBox(cbTrangThai, 990, 28, 130, 38);

		// --- 4. CÁC NÚT CHỨC NĂNG (Bên phải) ---
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

		btnXemPhieuTra = new PillButton(
				"<html>" +
						"<center>" +
						"XEM PHIẾU TRẢ<br>" +
						"<span style='font-size:10px; color:#888888;'>(F3)</span>" +
						"</center>" +
						"</html>");
		btnXemPhieuTra.setBounds(1415, 22, 180, 50);
		btnXemPhieuTra.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnXemPhieuTra.setToolTipText("<html><b>Phím tắt:</b> F3<br>Xem chi tiết phiếu trả đang chọn</html>");
		pnHeader.add(btnXemPhieuTra);
	}

	// Helper tạo label và combobox (Font 18)
	private void addFilterLabel(String text, int x, int y, int w, int h) {
		JLabel lbl = new JLabel(text);
		lbl.setBounds(x, y, w, h);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		pnHeader.add(lbl);
	}

	private void setupComboBox(JComboBox<?> cb, int x, int y, int w, int h) {
		cb.setBounds(x, y, w, h);
		cb.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		pnHeader.add(cb);
	}

	// =====================================================================================
	// CENTER
	// =====================================================================================
	private void taoCenter() {
		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(new EmptyBorder(5, 10, 10, 10));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(400);
		splitPane.setResizeWeight(0.5);

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

		configureTableRenderers();

		JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
		scrollChiTiet.setBorder(createTitledBorder("Chi tiết sản phẩm trả"));
		splitPane.setBottomComponent(scrollChiTiet);
		pnCenter.add(splitPane, BorderLayout.CENTER);
	}

	private void configureTableRenderers() {
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

		tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center);
		tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(center);
		tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(right);
		tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(center);
	}

	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);

		// ====== FONT & STYLE ĐỒNG BỘ VỚI TraCuuSanPham_GUI ======
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // font lớn hơn
		table.setRowHeight(35); // cao hơn
		table.setGridColor(new Color(230, 230, 230)); // giống SP
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setSelectionForeground(Color.BLACK);

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 16)); // header to hơn
		header.setPreferredSize(new Dimension(100, 40)); // tăng chiều cao
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);

		// ========= TOOLTIP TỰ ĐỘNG =========
		table.setToolTipText("");
		table.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());

				if (row > -1 && col > -1) {
					Object value = table.getValueAt(row, col);
					if (value != null) {
						Component comp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);

						int cellWidth = table.getColumnModel().getColumn(col).getWidth();
						int textWidth = comp.getPreferredSize().width;

						if (textWidth > cellWidth - 5) {
							table.setToolTipText(value.toString());
						} else {
							table.setToolTipText(null);
						}
					} else {
						table.setToolTipText(null);
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

	// ==============================================================================
	// EVENTS
	// ==============================================================================
	private void addEvents() {
		btnTimKiem.addActionListener(this);
		btnLamMoi.addActionListener(this);
		btnXemPhieuTra.addActionListener(this);
		txtTimKiem.addActionListener(this);

		// --- chọn 1 phiếu → load chi tiết ---
		tblPhieuTra.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				loadChiTietTuDongChon();
			}
		});

		// --- double click phiếu trả ---
		tblPhieuTra.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = tblPhieuTra.getSelectedRow();
					if (row != -1) {
						String ma = tblPhieuTra.getValueAt(row, 1).toString();
						JOptionPane.showMessageDialog(TraCuuDonTraHang_GUI.this,
								"Bạn vừa mở phiếu trả: " + ma + "\n(Có thể mở form chi tiết hoặc sửa phiếu tại đây)");
					}
				}
			}
		});
	}

	/**
	 * Thiết lập phím tắt cho màn hình Tra cứu Đơn trả hàng
	 */
	private void setupKeyboardShortcuts() {
		InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();

		// F1: Focus tìm kiếm
		inputMap.put(KeyStroke.getKeyStroke("F1"), "focusTimKiem");
		actionMap.put("focusTimKiem", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtTimKiem.requestFocus();
				txtTimKiem.selectAll();
			}
		});

		// F5: Làm mới
		inputMap.put(KeyStroke.getKeyStroke("F5"), "lamMoi");
		actionMap.put("lamMoi", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuLyLamMoi();
			}
		});

		// Ctrl+F: Focus tìm kiếm
		inputMap.put(KeyStroke.getKeyStroke("control F"), "timKiem");
		actionMap.put("timKiem", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtTimKiem.requestFocus();
				txtTimKiem.selectAll();
			}
		});

		// F3: Xem phiếu trả
		inputMap.put(KeyStroke.getKeyStroke("F3"), "xemPhieuTra");
		actionMap.put("xemPhieuTra", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuLyXemPhieuTra();
			}
		});
	}

	// ==============================================================================
	// ACTION
	// ==============================================================================
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		if (o == btnTimKiem || o == txtTimKiem) {
			xuLyTimKiem();
		} else if (o == btnLamMoi) {
			xuLyLamMoi();
		} else if (o == btnXemPhieuTra) {
			xuLyXemPhieuTra();
		}
	}

	/**
	 * Xử lý xem phiếu trả đang chọn
	 */
	private void xuLyXemPhieuTra() {
		int row = tblPhieuTra.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this,
					"Vui lòng chọn phiếu trả cần xem!",
					"Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String maPT = tblPhieuTra.getValueAt(row, 1).toString();
		xemPhieuTra(maPT);
	}

	/**
	 * Mở dialog xem phiếu trả
	 */
	private void xemPhieuTra(String maPT) {
		PhieuTra pt = phieuTraDAO.timKiemPhieuTraBangMa(maPT);
		if (pt != null) {
			List<ChiTietPhieuTra> dsCT = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPT);
			new PhieuTraPreviewDialog(SwingUtilities.getWindowAncestor(this), pt, dsCT).setVisible(true);
		}
	}

	// ==============================================================================
	// TẢI DỮ LIỆU BAN ĐẦU
	// ==============================================================================
	/** gọi ở constructor (giống TraCuuSanPham): load combobox + load bảng */
	private void initData() {
		loadComboTrangThai();
		xuLyLamMoi(); // Load tất cả với ngày mặc định từ cũ nhất đến nay
	}

	/** load danh sách PHIẾU TRẢ từ DB */
	private void taiDanhSachPhieuTra() {
		allPhieuTra = phieuTraDAO.layTatCaPhieuTra();
	}

	// ==============================================================================
	// COMBOBOX
	// ==============================================================================
	private void loadComboTrangThai() {
		cbTrangThai.removeAllItems();
		cbTrangThai.addItem("Tất cả");
		cbTrangThai.addItem("Chờ duyệt");
		cbTrangThai.addItem("Đã duyệt");
	}

	// ==============================================================================
	// TÌM KIẾM
	// ==============================================================================
	private void xuLyTimKiem() {
		String keyword = txtTimKiem.getText().trim();
		if (keyword.contains("Tìm theo"))
			keyword = "";

		String tt = cbTrangThai.getSelectedItem().toString();

		List<PhieuTra> ds = new ArrayList<>(allPhieuTra);

		// --- keyword ---
		if (!keyword.isEmpty()) {
			String kw = keyword.toLowerCase();
			ds.removeIf(pt -> !(pt.getMaPhieuTra().toLowerCase().contains(kw)
					|| pt.getKhachHang().getTenKhachHang().toLowerCase().contains(kw)
					|| pt.getKhachHang().getSoDienThoai().contains(kw)));
		}

		// --- Lọc theo Ngày (Java Filter) ---
		Date dTu = dateTuNgay.getDate();
		Date dDen = dateDenNgay.getDate();

		if (dTu != null || dDen != null) {
			LocalDate fromDate = (dTu != null) ? dTu.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					: LocalDate.MIN;
			LocalDate toDate = (dDen != null) ? dDen.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
					: LocalDate.MAX;

			ds.removeIf(pt -> {
				LocalDate ngayLap = pt.getNgayLap();
				// So sánh ngày: fromDate <= ngayLap <= toDate
				return !((ngayLap.isEqual(fromDate) || ngayLap.isAfter(fromDate)) &&
						(ngayLap.isEqual(toDate) || ngayLap.isBefore(toDate)));
			});
		}

		// --- trạng thái ---
		if (!"Tất cả".equals(tt)) {
			if ("Đã duyệt".equals(tt))
				ds.removeIf(pt -> !pt.isDaDuyet());
			else
				ds.removeIf(pt -> pt.isDaDuyet());
		}

		loadTablePhieuTra(ds);
	}

	// ==============================================================================
	// LÀM MỚI
	// ==============================================================================
	private void xuLyLamMoi() {
		txtTimKiem.setText("");
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);

		// --- CHỌN NGÀY MẶC ĐỊNH ---
		taiDanhSachPhieuTra();

		// Đến ngày: Hôm nay
		Date now = new Date();
		dateDenNgay.setDate(now);

		// Từ ngày: Ngày cũ nhất của phiếu trả hàng (nếu có)
		if (!allPhieuTra.isEmpty()) {
			LocalDate oldestDate = allPhieuTra.stream()
					.map(PhieuTra::getNgayLap)
					.min(LocalDate::compareTo)
					.orElse(LocalDate.now());
			Date fromDate = Date.from(oldestDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			dateTuNgay.setDate(fromDate);
		} else {
			// Nếu không có phiếu trả nào, đặt từ ngày là hôm nay
			dateTuNgay.setDate(now);
		}

		cbTrangThai.setSelectedIndex(0);

		loadTablePhieuTra(allPhieuTra); // Hiển thị tất cả
		modelChiTiet.setRowCount(0);
	}

	// ==============================================================================
	// LOAD BẢNG PHIẾU TRẢ
	// ==============================================================================
	private void loadTablePhieuTra(List<PhieuTra> ds) {
		modelPhieuTra.setRowCount(0);
		int stt = 1;

		for (PhieuTra pt : ds) {
			modelPhieuTra.addRow(new Object[] { stt++, pt.getMaPhieuTra(), pt.getKhachHang().getTenKhachHang(),
					pt.getNhanVien().getTenNhanVien(), pt.getNgayLap().format(dtf), df.format(pt.getTongTienHoan()),
					pt.isDaDuyet() ? "Đã duyệt" : "Chờ duyệt" });
		}
	}

	// ==============================================================================
	// LOAD BẢNG CHI TIẾT
	// ==============================================================================
	private void loadChiTietTuDongChon() {
		int row = tblPhieuTra.getSelectedRow();
		if (row < 0)
			return;

		String maPT = tblPhieuTra.getValueAt(row, 1).toString();

		List<ChiTietPhieuTra> ds = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPT);

		modelChiTiet.setRowCount(0);
		int stt = 1;

		for (ChiTietPhieuTra ct : ds) {

			String tenSP = ct.getChiTietHoaDon().getLoSanPham().getSanPham().getTenSanPham();

			modelChiTiet.addRow(new Object[] { stt++, tenSP, ct.getLyDoChiTiet(), ct.getSoLuong(),
					df.format(ct.getThanhTienHoan()), trangThaiCTText(ct.getTrangThai()) });
		}
	}

	private String trangThaiCTText(int t) {
		return switch (t) {
			case 0 -> "Chờ duyệt";
			case 1 -> "Nhập kho";
			case 2 -> "Hủy";
			case 3 -> "Chuyển NCC";
			default -> "Không xác định";
		};
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
