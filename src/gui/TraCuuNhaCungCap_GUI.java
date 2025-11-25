/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Nov 19, 2025
 *
 * Mô tả: Giao diện tra cứu Nhà Cung Cấp và Lịch sử Nhập hàng.
 * (Form chuẩn theo TraCuuNhanVien_GUI)
 */
package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

// Import các component riêng của bạn
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.ChiTietPhieuNhap_DAO;
import dao.NhaCungCap_DAO;
import dao.PhieuNhap_DAO;
import dao.SanPham_DAO;
import entity.ChiTietPhieuNhap;
import entity.NhaCungCap;
import entity.PhieuNhap;
import entity.SanPham;

public class TraCuuNhaCungCap_GUI extends JPanel {

	private static final String PLACEHOLDER_TIM_NCC = "Tìm NCC theo mã, tên, sđt, email...";
	private JPanel pnHeader;
	private JPanel pnCenter;

	private JTable tblNhaCungCap;
	private DefaultTableModel modelNhaCungCap;

	private JTabbedPane tabChiTiet;

	// Tab 1: Lịch sử nhập hàng (Phiếu Nhập)
	private JTable tblLichSuNhap;
	private DefaultTableModel modelLichSuNhap;

	// Tab 2: Sản phẩm cung cấp (Optional: Xem NCC này bán món gì)
	private JTable tblSanPhamCungCap;
	private DefaultTableModel modelSanPhamCungCap;

	// Components lọc
	private JTextField txtTimKiem;
	private JComboBox<String> cbKhuVuc; // Thay cho Chức vụ
	private JComboBox<String> cbTieuChi; // Thay cho Ca làm
	private JComboBox<String> cbTrangThai;
	private final NhaCungCap_DAO nccDAO = new NhaCungCap_DAO();
	private PillButton btnMoi;
	private PillButton btnTim;

	// Danh sách 63 tỉnh thành VN
	private static final String[] TAT_CA_TINH_THANH = { "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn",
			"Bạc Liêu", "Bắc Ninh", "Bến Tre", "Bình Định", "Bình Dương", "Bình Phước", "Bình Thuận", "Cà Mau",
			"Cần Thơ", "Cao Bằng", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai",
			"Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên",
			"Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lâm Đồng", "Lạng Sơn", "Lào Cai", "Long An", "Nam Định",
			"Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi",
			"Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa",
			"Thừa Thiên Huế", "Tiền Giang", "TP.HCM", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái" };

	public TraCuuNhaCungCap_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		// 1. HEADER
		taoPhanHeader();
		add(pnHeader, BorderLayout.NORTH);

		// 2. CENTER
		taoPhanCenter();
		add(pnCenter, BorderLayout.CENTER);

		// 3. DATA
		loadDuLieuNhaCungCap();
		addEvents();
	}

	// ==============================================================================
	// PHẦN HEADER
	// ==============================================================================
	private void taoPhanHeader() {
		pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(1073, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));

		// --- Ô TÌM KIẾM TO ---
		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_NCC);
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		txtTimKiem.setBounds(25, 17, 400, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setBackground(Color.WHITE);
		txtTimKiem.setForeground(Color.GRAY);
		pnHeader.add(txtTimKiem);

		// --- BỘ LỌC ---
		int yFilter = 28;
		int hFilter = 38;

		// Lọc 1: Khu vực (Ví dụ: Hà Nội, HCM...)
		JLabel lblKhuVuc = new JLabel("Khu vực:");
		lblKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblKhuVuc.setBounds(450, yFilter, 70, 35);
		pnHeader.add(lblKhuVuc);

		// TẠO COMBOBOX KHU VỰC TỪ 63 TỈNH
		cbKhuVuc = new JComboBox<>();
		cbKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		cbKhuVuc.setBounds(520, yFilter, 150, hFilter);
		pnHeader.add(cbKhuVuc);

		loadDanhSachKhuVuc(); // 🔥 GỌI HÀM MỚI
		cbKhuVuc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		cbKhuVuc.setBounds(520, yFilter, 100, hFilter);
		pnHeader.add(cbKhuVuc);

		// Lọc 2: Tiêu chí sắp xếp
		JLabel lblSort = new JLabel("Sắp xếp:");
		lblSort.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblSort.setBounds(640, yFilter, 60, 35);
		pnHeader.add(lblSort);

		cbTieuChi = new JComboBox<>(new String[] { "Mới nhất", "Tên A-Z", "Nhập nhiều nhất" });
		cbTieuChi.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		cbTieuChi.setBounds(710, yFilter, 120, hFilter);
		pnHeader.add(cbTieuChi);

		// Lọc 3: Trạng thái
		JLabel lblTT = new JLabel("Trạng thái:");
		lblTT.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lblTT.setBounds(850, yFilter, 80, 35);
		pnHeader.add(lblTT);

		cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Đang hợp tác", "Ngừng hợp tác" });
		cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		cbTrangThai.setBounds(930, yFilter, 120, hFilter);
		pnHeader.add(cbTrangThai);

		// --- NÚT ---
		btnTim = new PillButton("Tìm kiếm");
		btnTim.setBounds(1080, 22, 120, 50);
		btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnHeader.add(btnTim);

		btnMoi = new PillButton("Làm mới");
		btnMoi.setBounds(1220, 22, 120, 50);
		btnMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnHeader.add(btnMoi);

	}

	/**
	 * 🔥 Load toàn bộ tỉnh thành → sau đó giữ lại các tỉnh xuất hiện trong DB NCC
	 */
	private void loadDanhSachKhuVuc() {
		// 1) Xóa trước
		cbKhuVuc.removeAllItems();

		// 2) LUÔN THÊM "Tất cả"
		cbKhuVuc.addItem("Tất cả");

		// 3) Lấy toàn bộ NCC từ DB
		java.util.List<NhaCungCap> dsNCC = nccDAO.layTatCaNhaCungCap();

		// 4) Set lưu các tỉnh có NCC
		Set<String> khuVucCoTrongDB = new HashSet<>();

		for (NhaCungCap n : dsNCC) {
			String diaChi = n.getDiaChi();
			if (diaChi == null)
				continue;

			// Lấy tỉnh từ địa chỉ: lấy phần cuối sau dấu phẩy
			String tinh = layTinhTuDiaChi(diaChi);

			if (tinh != null && !tinh.isBlank()) {
				khuVucCoTrongDB.add(tinh);
			}
		}

		// 5) Thêm các tỉnh tồn tại trong DB
		for (String tinh : TAT_CA_TINH_THANH) {
			if (khuVucCoTrongDB.contains(tinh)) {
				cbKhuVuc.addItem(tinh);
			}
		}
	}

	/** Lấy tỉnh thành từ địa chỉ NCC */
	private String layTinhTuDiaChi(String diaChi) {
		if (diaChi == null)
			return null;

		// Lấy phần sau dấu ","
		if (diaChi.contains(",")) {
			String last = diaChi.substring(diaChi.lastIndexOf(",") + 1).trim();
			return chuanHoaTinh(last);
		}

		// Nếu không có dấu phẩy → dùng full
		return chuanHoaTinh(diaChi.trim());
	}

	/** Chuẩn hóa để đồng nhất với danh sách 63 tỉnh */
	private String chuanHoaTinh(String input) {
		input = input.replace(".", "").trim().toLowerCase();

		for (String t : TAT_CA_TINH_THANH) {
			if (input.contains(t.replace(".", "").toLowerCase())) {
				return t;
			}
		}
		return input; // nếu không khớp → trả nguyên bản
	}

	// ==============================================================================
	// PHẦN CENTER
	// ==============================================================================
	private void taoPhanCenter() {
		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(400);
		splitPane.setResizeWeight(0.5);
		pnCenter.add(splitPane, BorderLayout.CENTER);

		// --- TOP: BẢNG NHÀ CUNG CẤP ---
		String[] colNCC = { "STT", "Mã NCC", "Tên Nhà Cung Cấp", "SĐT", "Email", "Địa chỉ", "Trạng thái" };
		modelNhaCungCap = new DefaultTableModel(colNCC, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblNhaCungCap = setupTable(modelNhaCungCap);

		// Custom width cho bảng NCC
		tblNhaCungCap.getColumnModel().getColumn(0).setPreferredWidth(50); // STT
		tblNhaCungCap.getColumnModel().getColumn(1).setPreferredWidth(150); // Mã
		tblNhaCungCap.getColumnModel().getColumn(2).setPreferredWidth(250); // Tên (Dài)
		tblNhaCungCap.getColumnModel().getColumn(5).setPreferredWidth(300); // Địa chỉ (Rất dài)

		// Render Căn lề & Màu sắc
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);

		// Căn giữa các cột ngắn
		tblNhaCungCap.getColumnModel().getColumn(0).setCellRenderer(center);
		tblNhaCungCap.getColumnModel().getColumn(1).setCellRenderer(center);
		tblNhaCungCap.getColumnModel().getColumn(3).setCellRenderer(center);

		// Render Trạng thái
		tblNhaCungCap.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				if ("Đang hợp tác".equals(value)) {
					lbl.setForeground(new Color(0x2E7D32)); // Xanh lá đậm
					lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
				} else {
					lbl.setForeground(Color.RED);
					lbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
				}
				return lbl;
			}
		});

		JScrollPane scrollNCC = new JScrollPane(tblNhaCungCap);
		scrollNCC.setBorder(createTitledBorder("Danh sách Nhà Cung Cấp"));
		splitPane.setTopComponent(scrollNCC);

		// --- BOTTOM: TABBED PANE (LỊCH SỬ & SP) ---
		tabChiTiet = new JTabbedPane();
		tabChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 16));

		// Tab 1: Lịch sử Nhập Hàng
//		tabChiTiet.addTab("Lịch sử nhập hàng", createTabLichSuNhap());
		tabChiTiet.addTab("Lịch sử nhập hàng", new JLabel("Đang cập nhật...", JLabel.CENTER));

		// Tab 2: Sản phẩm cung cấp
//		tabChiTiet.addTab("Sản phẩm cung cấp", createTabSanPham());
		tabChiTiet.addTab("Sản phẩm cung cấp", new JLabel("Đang cập nhật...", JLabel.CENTER));

		splitPane.setBottomComponent(tabChiTiet);
	}

	// Tạo Panel cho Tab Lịch Sử Nhập
	private JComponent createTabLichSuNhap() {
		String[] cols = { "STT", "Mã Phiếu Nhập", "Ngày nhập", "Nhân viên phụ trách", "Tổng tiền nhập", "Ghi chú" };
		modelLichSuNhap = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblLichSuNhap = setupTable(modelLichSuNhap);
		setupTableAlign(tblLichSuNhap); // Căn tiền sang phải
		return new JScrollPane(tblLichSuNhap);
	}

	// Tạo Panel cho Tab Sản Phẩm (Để biết NCC này bán cái gì)
	private JComponent createTabSanPham() {
		String[] cols = { "STT", "Mã Thuốc", "Tên Thuốc", "Đơn vị tính", "Giá nhập gần nhất", "Xuất xứ" };
		modelSanPhamCungCap = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblSanPhamCungCap = setupTable(modelSanPhamCungCap);
		setupTableAlign(tblSanPhamCungCap);
		return new JScrollPane(tblSanPhamCungCap);
	}

	// Setup chung cho table
	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		table.setRowHeight(28);
		table.setSelectionBackground(new Color(0xC8E6C9)); // Màu xanh nhạt khi chọn

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);
		return table;
	}

	// Setup căn lề (Tiền số bên phải, Text bên trái/giữa)
	private void setupTableAlign(JTable table) {
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		// Cột STT và Mã luôn giữa
		table.getColumnModel().getColumn(0).setCellRenderer(center);
		table.getColumnModel().getColumn(1).setCellRenderer(center);

		// Cột áp chót và cuối thường là Tiền -> Phải
		int lastCol = table.getColumnCount() - 1;
		table.getColumnModel().getColumn(lastCol - 1).setCellRenderer(right);
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), Color.DARK_GRAY);
	}

	// ==============================================================================
	// DỮ LIỆU & SỰ KIỆN
	// ==============================================================================

	private void addEvents() {

		// --- TÌM KIẾM ---
		txtTimKiem.addActionListener(e -> timKiem());
		btnTim.addActionListener(e -> timKiem());

		// --- BỘ LỌC ---
		cbKhuVuc.addActionListener(e -> timKiem());
		cbTieuChi.addActionListener(e -> timKiem());
		cbTrangThai.addActionListener(e -> timKiem());

		// --- LÀM MỚI ---
		btnMoi.addActionListener(e -> lamMoi());

		// --- CLICK CHỌN NCC ---
//		tblNhaCungCap.getSelectionModel().addListSelectionListener(e -> {
//			if (!e.getValueIsAdjusting()) {
//				int row = tblNhaCungCap.getSelectedRow();
//				if (row >= 0) {
//					String maNCC = tblNhaCungCap.getValueAt(row, 1).toString();
//					loadChiTietNCC(maNCC);
//				}
//			}
//		});
	}

	private void timKiem() {
		String keyword = txtTimKiem.getText().trim();
		if (keyword.equalsIgnoreCase(PLACEHOLDER_TIM_NCC)) {
			System.out.println("ok");
			keyword = "";
		}
		String khuVuc = cbKhuVuc.getSelectedItem().toString();
		String tieuChi = cbTieuChi.getSelectedItem().toString();
		String trangThai = cbTrangThai.getSelectedItem().toString();

		// Gọi DAO lấy dữ liệu thật
		java.util.List<NhaCungCap> list = nccDAO.timKiemNCC(keyword, khuVuc, trangThai, tieuChi);

		modelNhaCungCap.setRowCount(0);
		int stt = 1;

		for (NhaCungCap n : list) {
			modelNhaCungCap.addRow(new Object[] { stt++, n.getMaNhaCungCap(), n.getTenNhaCungCap(), n.getSoDienThoai(),
					n.getEmail(), n.getDiaChi(), n.isHoatDong() ? "Đang hợp tác" : "Ngừng hợp tác" });
		}

		// Reset bảng chi tiết
//		modelLichSuNhap.setRowCount(0);
//		modelSanPhamCungCap.setRowCount(0);
	}

	private void lamMoi() {
		txtTimKiem.setText("");
		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_NCC);
		cbKhuVuc.setSelectedIndex(0);
		cbTieuChi.setSelectedIndex(0);
		cbTrangThai.setSelectedIndex(0);

		modelNhaCungCap.setRowCount(0);
		loadDuLieuNhaCungCap(); // gọi lại danh sách ban đầu
		txtTimKiem.requestFocus();
//		modelLichSuNhap.setRowCount(0);
//		modelSanPhamCungCap.setRowCount(0);
	}

	private void loadDuLieuNhaCungCap() {
		modelNhaCungCap.setRowCount(0);

		java.util.List<NhaCungCap> list = nccDAO.layTatCaNhaCungCap();
		int stt = 1;

		for (NhaCungCap n : list) {
			modelNhaCungCap.addRow(new Object[] { stt++, n.getMaNhaCungCap(), n.getTenNhaCungCap(), n.getSoDienThoai(),
					n.getEmail(), n.getDiaChi(), n.isHoatDong() ? "Đang hợp tác" : "Ngừng hợp tác" });
		}
	}

//	private void loadChiTietNCC(String maNCC) {
//		modelLichSuNhap.setRowCount(0);
//		modelSanPhamCungCap.setRowCount(0);
//
//		// DAO cần dùng
//		PhieuNhap_DAO pnDAO = new PhieuNhap_DAO();
//		ChiTietPhieuNhap_DAO ctpnDAO = new ChiTietPhieuNhap_DAO();
//		SanPham_DAO spDAO = new SanPham_DAO();
//
//		// 1) LẤY DANH SÁCH PHIẾU NHẬP CỦA NCC
//		java.util.List<PhieuNhap> listPN = new ArrayList<>();
//
//		for (PhieuNhap pn : pnDAO.layDanhSachPhieuNhap()) {
//			if (pn.getNhaCungCap().getMaNhaCungCap().equals(maNCC)) {
//				listPN.add(pn);
//			}
//		}
//
//		int stt = 1;
//		for (PhieuNhap pn : listPN) {
//			modelLichSuNhap.addRow(new Object[] { stt++, pn.getMaPhieuNhap(), pn.getNgayNhap(),
//					pn.getNhanVien().getTenNhanVien(), String.format("%,.0f", pn.getTongTien()), "" // Ghi chú chưa có
//																									// cột trong DB
//			});
//		}
//
//		// 2) LẤY DANH SÁCH SẢN PHẨM NCC ĐÃ CUNG CẤP
//		Set<String> maSanPhamSet = new HashSet<>();
//
//		for (PhieuNhap pn : listPN) {
//			java.util.List<ChiTietPhieuNhap> dsCT = ctpnDAO.timKiemChiTietPhieuNhapBangMa(pn.getMaPhieuNhap());
//			for (ChiTietPhieuNhap ct : dsCT) {
//				String maSP = ct.getLoSanPham().getSanPham().getMaSanPham();
//				maSanPhamSet.add(maSP);
//			}
//		}
//
//		stt = 1;
//		for (String maSP : maSanPhamSet) {
//			SanPham sp = spDAO.laySanPhamTheoMa(maSP);
//			if (sp != null) {
//				modelSanPhamCungCap.addRow(new Object[] { stt++, sp.getMaSanPham(), sp.getTenSanPham(),
//						sp.getLoaiSanPham(), String.format("%,.0f", sp.getGiaNhap()), sp.getKeBanSanPham() });
//			}
//		}
//	}

}