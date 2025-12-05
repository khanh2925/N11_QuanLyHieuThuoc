package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.ChiTietPhieuTra_DAO;
import dao.PhieuTra_DAO;
import entity.ChiTietPhieuTra;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuTra;

/**
 * @author Thanh Kha
 * @version 1.9
 */
@SuppressWarnings("serial")
public class TraCuuDonTraHang_GUI extends JPanel implements ActionListener {

	private static final String PLACEHOLDER_TIM_KIEM = "Tìm theo mã phiếu, tên KH hoặc SĐT...";
	// DAO
	private final PhieuTra_DAO phieuTraDAO;
	private final ChiTietPhieuTra_DAO chiTietPhieuTraDAO;

	// DATA
	private List<PhieuTra> allPhieuTra = new ArrayList<>();

	private JPanel pnHeader;

	private JTextField txtTimKiem;

	private JComboBox<String> cbKhachHang;
	private JComboBox<String> cbNhanVien;
	private JComboBox<String> cbTrangThai;

	private PillButton btnTimKiem;
	private PillButton btnLamMoi;

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

		// --- Ô TÌM KIẾM (Font 20) ---
		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtTimKiem.setBounds(25, 17, 480, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		pnHeader.add(txtTimKiem);

//        dateTuNgay = new JDateChooser();
//		dateTuNgay.setDateFormatString("dd/MM/yyyy");
//		dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//
//		dateDenNgay = new JDateChooser();
//		dateDenNgay.setDateFormatString("dd/MM/yyyy");
//		dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));

		addFilterLabel("Khách hàng:", 530, 28, 100, 35);
		cbKhachHang = new JComboBox<>();
		setupComboBox(cbKhachHang, 630, 28, 180, 38);

		addFilterLabel("Nhân viên:", 820, 28, 100, 35);
		cbNhanVien = new JComboBox<>();
		setupComboBox(cbNhanVien, 910, 28, 180, 38);

		addFilterLabel("Trạng thái:", 1100, 28, 100, 35);
		cbTrangThai = new JComboBox<>();
		setupComboBox(cbTrangThai, 1190, 28, 180, 38);

		// --- NÚT (Font 18) ---
		btnTimKiem = new PillButton("Tìm kiếm");
		btnTimKiem.setBounds(1380, 22, 130, 50);
		btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnHeader.add(btnTimKiem);

		btnLamMoi = new PillButton("Làm mới");
		btnLamMoi.setBounds(1515, 22, 130, 50);
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnHeader.add(btnLamMoi);
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
		}
	}

	// ==============================================================================
	// TẢI DỮ LIỆU BAN ĐẦU
	// ==============================================================================
	/** gọi ở constructor (giống TraCuuSanPham): load combobox + load bảng */
	private void initData() {
		loadComboKhachHang();
		loadComboNhanVien();
		loadComboTrangThai();
		taiDanhSachPhieuTra();
		loadTablePhieuTra(allPhieuTra);
	}

	/** load danh sách PHIẾU TRẢ từ DB */
	private void taiDanhSachPhieuTra() {
		allPhieuTra = phieuTraDAO.layTatCaPhieuTra();
	}

	// ==============================================================================
	// COMBOBOX
	// ==============================================================================
	private void loadComboKhachHang() {
		cbKhachHang.removeAllItems();
		cbKhachHang.addItem("Tất cả");

		List<KhachHang> ds = new dao.KhachHang_DAO().layTatCaKhachHang();
		for (KhachHang kh : ds) {
			cbKhachHang.addItem(kh.getTenKhachHang());
		}
	}

	private void loadComboNhanVien() {
		cbNhanVien.removeAllItems();
		cbNhanVien.addItem("Tất cả");

		List<NhanVien> ds = new dao.NhanVien_DAO().layTatCaNhanVien();
		for (NhanVien nv : ds) {
			cbNhanVien.addItem(nv.getTenNhanVien());
		}
	}

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
		String khach = cbKhachHang.getSelectedItem().toString();
		String nv = cbNhanVien.getSelectedItem().toString();
		String tt = cbTrangThai.getSelectedItem().toString();

		List<PhieuTra> ds = new ArrayList<>(allPhieuTra);

		// --- keyword ---
		if (!keyword.isEmpty() && !keyword.equals(PLACEHOLDER_TIM_KIEM)) {
			String kw = keyword.toLowerCase();
			ds.removeIf(pt -> !(pt.getMaPhieuTra().toLowerCase().contains(kw)
					|| pt.getKhachHang().getTenKhachHang().toLowerCase().contains(kw)
					|| pt.getKhachHang().getSoDienThoai().contains(kw)));
		}

		// --- khách hàng ---
		if (!"Tất cả".equals(khach)) {
			ds.removeIf(pt -> !pt.getKhachHang().getTenKhachHang().equals(khach));
		}

		// --- nhân viên ---
		if (!"Tất cả".equals(nv)) {
			ds.removeIf(pt -> !pt.getNhanVien().getTenNhanVien().equals(nv));
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
		cbKhachHang.setSelectedIndex(0);
		cbNhanVien.setSelectedIndex(0);
		cbTrangThai.setSelectedIndex(0);

		taiDanhSachPhieuTra();
		loadTablePhieuTra(allPhieuTra);
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
