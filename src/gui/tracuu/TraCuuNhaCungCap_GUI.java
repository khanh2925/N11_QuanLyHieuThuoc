package gui.tracuu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
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

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.NhaCungCap_DAO;
import dao.PhieuNhap_DAO;
import dao.SanPham_DAO;
import entity.NhaCungCap;
import entity.PhieuNhap;

/**
 * @author Thanh Kha
 * @version 1.0
 */
@SuppressWarnings("serial")
public class TraCuuNhaCungCap_GUI extends JPanel implements ActionListener {

	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã hoặc tên nhà cung cấp...";

	private JPanel pnHeader;
	private JPanel pnCenter;

	private JTextField txtTimKiem;

	private JComboBox<String> cbTrangThai;

	private PillButton btnTimKiem;
	private PillButton btnLamMoi;

	private JTable tblNhaCungCap;
	private DefaultTableModel modelNCC;

	private final DecimalFormat df = new DecimalFormat("#,### đ");
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final NhaCungCap_DAO nhaCungCapDAO;
	private final PhieuNhap_DAO phieuNhapDAO;
	private final SanPham_DAO sanPhamDAO;
	private List<NhaCungCap> allNhaCungCap = new ArrayList<>();

	private JTabbedPane tabChiTiet;
	// Thêm vào phần khai báo biến
	private JTable tblLichSuNhapHang;
	private DefaultTableModel modelLichSu;
	private JTable tblSanPhamCungCap;
	private DefaultTableModel modelSanPham;

	public TraCuuNhaCungCap_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		nhaCungCapDAO = new NhaCungCap_DAO();
		phieuNhapDAO = new PhieuNhap_DAO();
		sanPhamDAO = new SanPham_DAO();
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

	// =====================================================================================
	// HEADER – GIỐNG HỆT TraCuuDonTraHang (layout null từng px)
	// =====================================================================================
	private void taoHeader() {

		pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(1073, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));

		// ----- Ô TÌM KIẾM -----
		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtTimKiem.setBounds(25, 17, 480, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		pnHeader.add(txtTimKiem);

		addFilterLabel("Trạng thái:", 530, 28, 100, 35);
		cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Đang hoạt động", "Ngưng hợp tác" });
		setupComboBox(cbTrangThai, 630, 28, 180, 38);

		// ----- NÚT -----
		btnTimKiem = new PillButton("Tìm kiếm");
		btnTimKiem.setBounds(1100, 22, 130, 50);
		btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnHeader.add(btnTimKiem);

		btnLamMoi = new PillButton("Làm mới");
		btnLamMoi.setBounds(1235, 22, 130, 50);
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnHeader.add(btnLamMoi);
	}

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

		String[] colNCC = { "STT", "Mã NCC", "Tên nhà cung cấp", "Số điện thoại", "Địa chỉ", "Email", "Trạng thái" };

		modelNCC = new DefaultTableModel(colNCC, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblNhaCungCap = setupTable(modelNCC);

		JScrollPane scrollNCC = new JScrollPane(tblNhaCungCap);
		scrollNCC.setBorder(createTitledBorder("Danh sách nhà cung cấp"));

		splitPane.setTopComponent(scrollNCC);
		// --- BOTTOM: TABBED PANE ---
		tabChiTiet = new JTabbedPane();
		tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

		tabChiTiet.addTab("Lịch sử nhập hàng", createTabLichSuNhapHang());
		tabChiTiet.addTab("Sản phẩm cung cấp", createTabSanPhamCungCap());

		splitPane.setBottomComponent(tabChiTiet);

		pnCenter.add(splitPane, BorderLayout.CENTER);
	}

	private JComponent createTabLichSuNhapHang() {
		String[] colLichSu = { "STT", "Mã phiếu nhập", "Ngày nhập", "Tổng tiền", "Nhân viên nhập" };
		modelLichSu = new DefaultTableModel(colLichSu, 0) { // ✅ Gán vào biến instance
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblLichSuNhapHang = setupTable(modelLichSu); // ✅ Gán table

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		for (int i = 0; i < tblLichSuNhapHang.getColumnCount(); i++) {
			if (i == 3) { // Cột Tổng tiền
				tblLichSuNhapHang.getColumnModel().getColumn(i).setCellRenderer(right);
			} else {
				tblLichSuNhapHang.getColumnModel().getColumn(i).setCellRenderer(center);
			}
		}

		return new JScrollPane(tblLichSuNhapHang);
	}

	private JComponent createTabSanPhamCungCap() {
		String[] colSanPham = { "STT", "Mã SP", "Tên sản phẩm", "Loại", "Số lần nhập", "Tổng SL đã nhập" };
		modelSanPham = new DefaultTableModel(colSanPham, 0) { // ✅ Gán vào biến instance
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblSanPhamCungCap = setupTable(modelSanPham); // ✅ Gán table

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		tblSanPhamCungCap.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblSanPhamCungCap.getColumnModel().getColumn(1).setCellRenderer(center); // Mã SP
		tblSanPhamCungCap.getColumnModel().getColumn(3).setCellRenderer(center); // Loại
		tblSanPhamCungCap.getColumnModel().getColumn(4).setCellRenderer(center); // Số lần
		tblSanPhamCungCap.getColumnModel().getColumn(5).setCellRenderer(right); // Tổng SL

		return new JScrollPane(tblSanPhamCungCap);
	}

	// ==============================================================================
	// EVENTS
	// ==============================================================================
	private void addEvents() {
		btnTimKiem.addActionListener(this);
		btnLamMoi.addActionListener(this);
		txtTimKiem.addActionListener(this);

		// --- double click nhà cung cấp ---
		tblNhaCungCap.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = tblNhaCungCap.getSelectedRow();
					if (row != -1) {
						String ma = tblNhaCungCap.getValueAt(row, 1).toString();
						JOptionPane.showMessageDialog(TraCuuNhaCungCap_GUI.this,
								"Bạn vừa mở nhà cung cấp: " + ma + "\n(Có thể mở form chi tiết hoặc sửa NCC tại đây)");
					}
				}
			}
		});

		tblNhaCungCap.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				loadChiTietNhaCungCap();
			}
		});
	}

	// ==============================================================================
	// ACTION
	// ==============================================================================
	@Override
	public void actionPerformed(java.awt.event.ActionEvent e) {
		Object o = e.getSource();

		if (o == btnTimKiem || o == txtTimKiem) {
			xuLyTimKiem();
		} else if (o == btnLamMoi) {
			xuLyLamMoi();
		}
	}

	// ==============================================================================
	// TẢI DỮ LIỆU BAN ĐẦU
	// ==============================================================================
	private void initData() {
		loadComboTrangThai();
		taiDanhSachNhaCungCap();
		loadTableNhaCungCap(allNhaCungCap);
	}

	/** load danh sách NHÀ CUNG CẤP từ DB */
	private void taiDanhSachNhaCungCap() {
		allNhaCungCap = nhaCungCapDAO.layTatCaNhaCungCap();
	}

	// ==============================================================================
	// COMBOBOX
	// ==============================================================================
	private void loadComboTrangThai() {
		cbTrangThai.removeAllItems();
		cbTrangThai.addItem("Tất cả");
		cbTrangThai.addItem("Đang hoạt động");
		cbTrangThai.addItem("Ngưng hợp tác");
	}

	// ==============================================================================
	// TÌM KIẾM
	// ==============================================================================
	private void xuLyTimKiem() {
		String keyword = txtTimKiem.getText().trim();
		String tt = cbTrangThai.getSelectedItem().toString();

		List<NhaCungCap> ds = new ArrayList<>(allNhaCungCap);

		// --- keyword ---
		if (!keyword.isEmpty() && !keyword.equals(PLACEHOLDER_TIM_KIEM)) {
			String kw = keyword.toLowerCase();
			ds.removeIf(ncc -> !(ncc.getMaNhaCungCap().toLowerCase().contains(kw)
					|| ncc.getTenNhaCungCap().toLowerCase().contains(kw)));
		}

		// --- trạng thái (khớp với isHoatDong) ---
		if (!"Tất cả".equals(tt)) {
			if ("Đang hoạt động".equals(tt))
				ds.removeIf(ncc -> !ncc.isHoatDong());
			else
				ds.removeIf(ncc -> ncc.isHoatDong());
		}

		loadTableNhaCungCap(ds);
	}

	// ==============================================================================
	// LÀM MỚI
	// ==============================================================================
	private void xuLyLamMoi() {
		txtTimKiem.setText("");
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
		cbTrangThai.setSelectedIndex(0);

		taiDanhSachNhaCungCap();
		loadTableNhaCungCap(allNhaCungCap);
	}

	// ==============================================================================
	// LOAD BẢNG NHÀ CUNG CẤP
	// ==============================================================================
	private void loadTableNhaCungCap(List<NhaCungCap> ds) {
		modelNCC.setRowCount(0);
		int stt = 1;

		for (NhaCungCap ncc : ds) {
			modelNCC.addRow(new Object[] { stt++, ncc.getMaNhaCungCap(), ncc.getTenNhaCungCap(), ncc.getSoDienThoai(),
					ncc.getDiaChi(), ncc.getEmail(), ncc.isHoatDong() ? "Đang hoạt động" : "Ngưng hợp tác" });
		}
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
		header.setPreferredSize(new Dimension(100, 40));
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);

		// Tooltip auto
		table.setToolTipText("");
		table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				int row = table.rowAtPoint(e.getPoint());
				int col = table.columnAtPoint(e.getPoint());

				if (row > -1 && col > -1) {
					Object value = table.getValueAt(row, col);
					if (value != null) {
						Component comp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
						int cellWidth = table.getColumnModel().getColumn(col).getWidth();
						int textWidth = comp.getPreferredSize().width;

						table.setToolTipText(textWidth > cellWidth - 5 ? value.toString() : null);
					}
				}
			}
		});

		// Căn giữa 2 cột đầu
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(center);
		table.getColumnModel().getColumn(1).setCellRenderer(center);

		return table;
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
	}

	private void loadChiTietNhaCungCap() {
		int row = tblNhaCungCap.getSelectedRow();
		if (row < 0) {
			modelLichSu.setRowCount(0);
			modelSanPham.setRowCount(0);
			return;
		}

		String maNCC = tblNhaCungCap.getValueAt(row, 1).toString();

		// ✅ 1. Load lịch sử nhập hàng
		loadLichSuNhapHang(maNCC);

		// ✅ 2. Load sản phẩm cung cấp
		loadSanPhamCungCap(maNCC);
	}

	private void loadLichSuNhapHang(String maNCC) {
		modelLichSu.setRowCount(0);

		try {
			List<PhieuNhap> dsPhieuNhap = phieuNhapDAO.layPhieuNhapTheoNhaCungCap(maNCC);

			int stt = 1;
			for (PhieuNhap pn : dsPhieuNhap) {
				modelLichSu.addRow(new Object[] { stt++, pn.getMaPhieuNhap(), pn.getNgayNhap().format(dtf),
						df.format(pn.getTongTien()), pn.getNhanVien().getTenNhanVien() });
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Lỗi load lịch sử nhập: " + e.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadSanPhamCungCap(String maNCC) {
		modelSanPham.setRowCount(0);

		Map<String, Object[]> thongKe = sanPhamDAO.thongKeSanPhamTheoNCC(maNCC);

		int stt = 1;
		for (Map.Entry<String, Object[]> entry : thongKe.entrySet()) {
			String maSP = entry.getKey();
			Object[] data = entry.getValue();

			modelSanPham.addRow(new Object[] { stt++, maSP, data[0], // Tên SP
					data[1], // Loại
					data[2], // Số lần nhập
					data[3] // Tổng SL
			});
		}
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
			JFrame frame = new JFrame("Tra cứu nhà cung cấp");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1400, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new TraCuuNhaCungCap_GUI());
			frame.setVisible(true);
		});
	}
}
