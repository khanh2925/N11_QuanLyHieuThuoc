package gui.quanly;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import com.toedter.calendar.JDateChooser;

import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.ChiTietKhuyenMaiSanPham_DAO;
import dao.KhuyenMai_DAO;
import dao.SanPham_DAO;
import entity.ChiTietKhuyenMaiSanPham;
import entity.KhuyenMai;
import entity.SanPham;
import enums.HinhThucKM;
import gui.dialog.DialogChonSanPhamApDung;

/**
 * GUI Quản lý khuyến mãi - Giữ nguyên layout cũ - Dùng Entity + DAO thật
 */
@SuppressWarnings("serial")
public class KhuyenMai_GUI extends JPanel implements ActionListener {

	// UI Components
	private JPanel pnHeader, pnCenter;
	private JSplitPane splitPane;

	// Inputs
	private JTextField txtMaKM, txtTenKM, txtGiaTri, txtDieuKien, txtSoLuong;
	private JDateChooser dateNgayBD, dateNgayKT;
	private JComboBox<String> cboLoaiKM, cboHinhThuc, cboTrangThai;

	// Buttons
	private PillButton btnThem, btnSua, btnLamMoi, btnTimKiem, btnChonSP, btnXoaSP;
	private JTextField txtTimKiem;

	// Tables
	private JTable tblKhuyenMai, tblSanPhamApDung;
	private DefaultTableModel modelKhuyenMai, modelSanPhamApDung;

	// DAO & DATA
	private KhuyenMai_DAO kmDAO = new KhuyenMai_DAO();
	private ChiTietKhuyenMaiSanPham_DAO ctkmDAO = new ChiTietKhuyenMaiSanPham_DAO();
	private SanPham_DAO spDAO = new SanPham_DAO();
	private List<KhuyenMai> dsKhuyenMai = new ArrayList<>();

	// Format
	private DecimalFormat dfNumber = new DecimalFormat("#,###");
	private DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
	private Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);

	public KhuyenMai_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
		setupKeyboardShortcuts(); // Thiết lập phím tắt
		addFocusOnShow();
		loadDataKhuyenMai();
		lamMoiForm(); // sinh mã mới ngay từ đầu
		capNhatTrangThaiNut(); // Cập nhật hiển thị nút theo chọn dòng
	}

	// ====================== BUILD UI ======================

	private void initialize() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		// Header
		taoPhanHeader();
		add(pnHeader, BorderLayout.NORTH);

		// Center (SplitPane)
		taoPhanCenter();
		add(pnCenter, BorderLayout.CENTER);
	}

	// ====================== HEADER ======================
	private void taoPhanHeader() {
		pnHeader = new JPanel(null);
		pnHeader.setPreferredSize(new Dimension(1073, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));

		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm KM theo mã, tên chương trình (F1 / Ctrl+F)");
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		txtTimKiem.setBounds(25, 17, 500, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
		txtTimKiem.addActionListener(e -> xuLyTimKiem());
		pnHeader.add(txtTimKiem);

		btnTimKiem = new PillButton("<html>" + "<center>" + "TÌM KIẾM<br>"
				+ "<span style='font-size:10px; color:#888888;'>(Enter)</span>" + "</center>" + "</html>");
		btnTimKiem.setBounds(540, 22, 130, 50);
		btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTimKiem.setToolTipText(
				"<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên khuyến mãi</html>");
		btnTimKiem.addActionListener(e -> xuLyTimKiem());
		pnHeader.add(btnTimKiem);
	}

	// ====================== CENTER (SPLIT) ======================
	private void taoPhanCenter() {
		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

		// --- TOP: FORM + BUTTONS ---
		JPanel pnTopWrapper = new JPanel(new BorderLayout());
		pnTopWrapper.setBackground(Color.WHITE);
		pnTopWrapper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				"Thông tin khuyến mãi", TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY));

		JPanel pnForm = new JPanel(null);
		pnForm.setBackground(Color.WHITE);
		taoFormNhapLieu(pnForm);
		pnTopWrapper.add(pnForm, BorderLayout.CENTER);

		JPanel pnButton = new JPanel();
		pnButton.setBackground(Color.WHITE);
		taoPanelNutBam(pnButton);
		pnTopWrapper.add(pnButton, BorderLayout.EAST);

		// --- BOTTOM: TABS ---
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(FONT_TEXT);

		// Tab 1: Danh sách KM
		JPanel pnTab1 = new JPanel(new BorderLayout());
		pnTab1.setBackground(Color.WHITE);
		taoBangDanhSach(pnTab1);
		tabbedPane.addTab("Danh sách khuyến mãi", pnTab1);

		// Tab 2: Sản phẩm áp dụng
		JPanel pnTab2 = new JPanel(new BorderLayout());
		pnTab2.setBackground(Color.WHITE);
		taoBangSanPhamApDung(pnTab2);
		tabbedPane.addTab("Sản phẩm áp dụng", pnTab2);

		// SplitPane
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, tabbedPane);
		splitPane.setDividerLocation(380);
		splitPane.setResizeWeight(0.0);

		pnCenter.add(splitPane, BorderLayout.CENTER);
	}

	// --- FORM NHẬP LIỆU ---
	private void taoFormNhapLieu(JPanel p) {
		int xStart = 50, yStart = 30;
		int hText = 35, wLbl = 120, wTxt = 320, gap = 25;

		int xCol2 = xStart + wLbl + wTxt + 120;

		// ===== HÀNG 1 =====
		p.add(createLabel("Mã KM:", xStart, yStart));
		txtMaKM = createTextField(xStart + wLbl, yStart, wTxt);
		txtMaKM.setEditable(false);
		p.add(txtMaKM);

		p.add(createLabel("Tên KM:", xCol2, yStart));
		txtTenKM = createTextField(xCol2 + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtTenKM, "Nhập tên khuyến mãi");
		p.add(txtTenKM);

		// ===== HÀNG 2 =====
		yStart += hText + gap;
		p.add(createLabel("Ngày BĐ:", xStart, yStart));
		dateNgayBD = new JDateChooser();
		dateNgayBD.setDateFormatString("dd/MM/yyyy");
		dateNgayBD.setFont(FONT_TEXT);
		dateNgayBD.setBounds(xStart + wLbl, yStart, wTxt, hText);
		dateNgayBD.setDate(new Date()); // Mặc định là ngày hôm nay
		p.add(dateNgayBD);

		p.add(createLabel("Ngày KT:", xCol2, yStart));
		dateNgayKT = new JDateChooser();
		dateNgayKT.setDateFormatString("dd/MM/yyyy");
		dateNgayKT.setFont(FONT_TEXT);
		dateNgayKT.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
		dateNgayKT.setDate(new Date()); // Mặc định là ngày hôm nay
		p.add(dateNgayKT);

		// ===== HÀNG 3 =====
		yStart += hText + gap;
		p.add(createLabel("Loại KM:", xStart, yStart));
		cboLoaiKM = new JComboBox<>(new String[] { "Theo hóa đơn", "Theo sản phẩm" });
		cboLoaiKM.setBounds(xStart + wLbl, yStart, wTxt, hText);
		cboLoaiKM.setFont(FONT_TEXT);
		cboLoaiKM.addActionListener(e -> {
			boolean isHoaDon = cboLoaiKM.getSelectedItem().equals("Theo hóa đơn");
			txtDieuKien.setEnabled(isHoaDon);
			if (!isHoaDon)
				txtDieuKien.setText("0");
		});

		p.add(cboLoaiKM);

		p.add(createLabel("Hình thức:", xCol2, yStart));
		// text hiển thị khớp với enum.toString()
		cboHinhThuc = new JComboBox<>(
				new String[] { HinhThucKM.GIAM_GIA_PHAN_TRAM.getMoTa(), HinhThucKM.GIAM_GIA_TIEN.getMoTa() });
		cboHinhThuc.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
		cboHinhThuc.setFont(FONT_TEXT);
		p.add(cboHinhThuc);

		// ===== HÀNG 4 =====
		yStart += hText + gap;
		p.add(createLabel("Giá trị:", xStart, yStart));
		txtGiaTri = createTextField(xStart + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtGiaTri, "Nhập giá trị");
		p.add(txtGiaTri);

		p.add(createLabel("Điều kiện:", xCol2, yStart));
		txtDieuKien = createTextField(xCol2 + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtDieuKien, "Nhập điều kiện");
		p.add(txtDieuKien);

		// ===== HÀNG 5 =====
		yStart += hText + gap;
		p.add(createLabel("Số lượng:", xStart, yStart));
		txtSoLuong = createTextField(xStart + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtSoLuong, "Nhập số lượng");
		p.add(txtSoLuong);

		p.add(createLabel("Trạng thái:", xCol2, yStart));
		cboTrangThai = new JComboBox<>(new String[] { "Đang hoạt động", "Ngưng hoạt động" });
		cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
		cboTrangThai.setFont(FONT_TEXT);
		p.add(cboTrangThai);
	}

	// --- PANEL NÚT ---
	private void taoPanelNutBam(JPanel p) {
		p.setPreferredSize(new Dimension(200, 0));
		p.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
		p.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.insets = new Insets(10, 0, 10, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Button dimensions
		int btnW = 140;
		int btnH = 45;

		btnThem = new PillButton("<html>" + "<center>" + "TẠO KM<br>"
				+ "<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" + "</center>" + "</html>");
		btnThem.setFont(FONT_BOLD);
		btnThem.setPreferredSize(new Dimension(btnW, btnH));
		btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Tạo khuyến mãi mới</html>");
		btnThem.addActionListener(this);
		gbc.gridy = 0;
		p.add(btnThem, gbc);

		btnSua = new PillButton("<html>" + "<center>" + "CẬP NHẬT<br>"
				+ "<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" + "</center>" + "</html>");
		btnSua.setFont(FONT_BOLD);
		btnSua.setPreferredSize(new Dimension(btnW, btnH));
		btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật khuyến mãi đang chọn</html>");
		btnSua.addActionListener(this);
		gbc.gridy = 1;
		p.add(btnSua, gbc);

		btnLamMoi = new PillButton("<html>" + "<center>" + "LÀM MỚI<br>"
				+ "<span style='font-size:10px; color:#888888;'>(F5)</span>" + "</center>" + "</html>");
		btnLamMoi.setFont(FONT_BOLD);
		btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
		btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới form nhập liệu</html>");
		btnLamMoi.addActionListener(this);
		gbc.gridy = 2;
		p.add(btnLamMoi, gbc);
	}

	// --- CÁC BẢNG ---
	private void taoBangDanhSach(JPanel p) {
		String[] cols = { "STT", "Mã KM", "Tên KM", "Hình thức", "Giá trị", "Bắt đầu", "Kết thúc", "Loại",
				"Trạng thái" };
		modelKhuyenMai = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblKhuyenMai = setupTable(modelKhuyenMai);

		// Renderer căn phải cho STT (cột 0)
		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

		tblKhuyenMai.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT căn giữa
		tblKhuyenMai.getColumnModel().getColumn(0).setPreferredWidth(50);

		// Renderer căn trái cho Tên KM, Hình thức (cột 2, 3)
		tblKhuyenMai.getColumnModel().getColumn(2).setCellRenderer(leftRenderer); // Tên KM
		tblKhuyenMai.getColumnModel().getColumn(3).setCellRenderer(leftRenderer); // Hình thức

		// Renderer căn giữa cho Mã KM, Loại (cột 1, 7)
		tblKhuyenMai.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Mã KM
		tblKhuyenMai.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Loại

		// Renderer căn phải cho Giá trị (cột 4)
		tblKhuyenMai.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

		// Renderer căn giữa cho Ngày (cột 5, 6)
		tblKhuyenMai.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Bắt đầu
		tblKhuyenMai.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Kết thúc

		// Renderer màu cho cột Trạng thái (cột 8) + căn giữa
		tblKhuyenMai.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa
				String status = String.valueOf(value);
				if ("Đang hoạt động".equals(status)) {
					lbl.setForeground(new Color(0x2E7D32)); // Xanh lá đậm
					lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
				} else {
					lbl.setForeground(Color.RED);
					lbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
				}
				return lbl;
			}
		});

		// Event Click
		tblKhuyenMai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				doToForm(tblKhuyenMai.getSelectedRow());
				capNhatTrangThaiNut();
			}
		});

		// Selection Listener for button visibility
		tblKhuyenMai.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				capNhatTrangThaiNut();
			}
		});

		p.add(new JScrollPane(tblKhuyenMai), BorderLayout.CENTER);
	}

	private void taoBangSanPhamApDung(JPanel p) {
		// Toolbar thêm sản phẩm
		// Toolbar thêm sản phẩm
		JPanel pnTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnTool.setBackground(Color.WHITE);

		btnChonSP = new PillButton("<html>" + "<center>" + "CHỌN SP<br>"
				+ "<span style='font-size:10px; color:#888888;'>(F7)</span>" + "</center>" + "</html>");
		btnChonSP.setFont(FONT_BOLD);
		btnChonSP.setPreferredSize(new Dimension(140, 45));
		btnChonSP.setToolTipText("<html><b>Phím tắt:</b> F7<br>Mở danh sách chọn sản phẩm áp dụng</html>");
		btnChonSP.addActionListener(this);

		btnXoaSP = new PillButton("<html>" + "<center>" + "XÓA SP<br>"
				+ "<span style='font-size:10px; color:#888888;'>(F8)</span>" + "</center>" + "</html>");
		btnXoaSP.setFont(FONT_BOLD);
		btnXoaSP.setPreferredSize(new Dimension(140, 45));
		btnXoaSP.setToolTipText("<html><b>Phím tắt:</b> F8<br>Xóa sản phẩm đã chọn khỏi khuyến mãi</html>");
		btnXoaSP.addActionListener(this);

		pnTool.add(btnChonSP);
		pnTool.add(btnXoaSP);
		p.add(pnTool, BorderLayout.NORTH);

		String[] cols = { "Mã SP", "Tên sản phẩm", "Đơn vị", "Giá gốc", "Giá KM" };
		modelSanPhamApDung = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblSanPhamApDung = setupTable(modelSanPhamApDung);

		// Selection Listener for btnXoaSP
		tblSanPhamApDung.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				capNhatTrangThaiNut();
			}
		});

		p.add(new JScrollPane(tblSanPhamApDung), BorderLayout.CENTER);
	}

	// ====================== LOGIC ======================
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		// 1. THÊM MỚI
		if (o.equals(btnThem)) {
			xuLyThem();
		}

		// 2. CẬP NHẬT
		else if (o.equals(btnSua)) {
			xuLyCapNhat();
		}

		// 3. LÀM MỚI
		else if (o.equals(btnLamMoi)) {
			lamMoiForm();
		}

		// 4. CHỌN SẢN PHẨM
		else if (o.equals(btnChonSP)) {
			xuLyThemSanPhamApDung();
		}

		// 5. XÓA SP
		else if (o.equals(btnXoaSP)) {
			xuLyXoaSanPhamApDung();
		}
	}

	/**
	 * Thiết lập phím tắt cho màn hình Quản lý Khuyến mãi
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
				lamMoiForm();
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

		// Ctrl+N: Thêm
		inputMap.put(KeyStroke.getKeyStroke("control N"), "themKM");
		actionMap.put("themKM", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuLyThem();
			}
		});

		// Ctrl+U: Cập nhật
		inputMap.put(KeyStroke.getKeyStroke("control U"), "capNhatKM");
		actionMap.put("capNhatKM", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuLyCapNhat();
			}
		});

		// F7: Chọn SP (Tab 2)
		inputMap.put(KeyStroke.getKeyStroke("F7"), "chonSP");
		actionMap.put("chonSP", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Chỉ hoạt động nếu đang ở tab 2 hoặc muốn switch qua?
				// Đơn giản là gọi hàm xử lý, hàm đó sẽ check logic
				if (splitPane.getBottomComponent() instanceof JTabbedPane) {
					JTabbedPane tabs = (JTabbedPane) splitPane.getBottomComponent();
					if (tabs.getSelectedIndex() == 1) { // Tab "Sản phẩm áp dụng"
						xuLyThemSanPhamApDung();
					}
				}
			}
		});

		// F8: Xóa SP (Tab 2)
		inputMap.put(KeyStroke.getKeyStroke("F8"), "xoaSP");
		actionMap.put("xoaSP", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (splitPane.getBottomComponent() instanceof JTabbedPane) {
					JTabbedPane tabs = (JTabbedPane) splitPane.getBottomComponent();
					if (tabs.getSelectedIndex() == 1) {
						xuLyXoaSanPhamApDung();
					}
				}
			}
		});
	}

	private void addFocusOnShow() {
		addHierarchyListener(e -> {
			if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
				SwingUtilities.invokeLater(() -> {
					txtTimKiem.requestFocusInWindow();
					txtTimKiem.selectAll();
				});
			}
		});
	}
	// ---------- CRUD KM ----------

	private void xuLyThem() {
		try {
			txtMaKM.setText(kmDAO.taoMaKhuyenMai());
			KhuyenMai km = getKhuyenMaiFromForm(true); // true = đang thêm mới
			if (kmDAO.themKhuyenMai(km)) {
				JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!");
				loadDataKhuyenMai();
				chonDongTheoMa(km.getMaKM());
				if (!km.isKhuyenMaiHoaDon()) {
					int chon = JOptionPane.showConfirmDialog(this, "Bạn có muốn chọn sản phẩm áp dụng ngay không?",
							"Chọn sản phẩm", JOptionPane.YES_NO_OPTION);
					if (chon == JOptionPane.YES_OPTION) {
						btnChonSP.doClick(); // mở dialog chọn SP
					}
				}
				lamMoiForm();
				txtTenKM.requestFocus();
			} else {
				JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void xuLyCapNhat() {
		int row = tblKhuyenMai.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khuyến mãi để cập nhật!");
			return;
		}

		try {
			KhuyenMai km = getKhuyenMaiFromForm(false); // false = cập nhật
			if (km == null)
				return;

			// Kiểm tra nếu muốn bật lại "Đang hoạt động"
			String trangThaiChon = (String) cboTrangThai.getSelectedItem();
			if ("Đang hoạt động".equals(trangThaiChon)) {
				LocalDate today = LocalDate.now();
				// Kiểm tra còn hạn không
				if (km.getNgayKetThuc().isBefore(today)) {
					JOptionPane.showMessageDialog(
							this, "Không thể kích hoạt lại!\nKhuyến mãi đã hết hạn ("
									+ dfDate.format(km.getNgayKetThuc()) + ")",
							"Không hợp lệ", JOptionPane.WARNING_MESSAGE);
					return;
				}
				// Kiểm tra còn số lượng
				if (km.getSoLuongKhuyenMai() <= 0) {
					JOptionPane.showMessageDialog(this, "Không thể kích hoạt lại!\nSố lượng khuyến mãi đã hết (= 0)",
							"Không hợp lệ", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}

			if (!km.isKhuyenMaiHoaDon()) {
				// đang là theo sản phẩm
				List<ChiTietKhuyenMaiSanPham> ds = ctkmDAO.layChiTietKhuyenMaiTheoMaCoJoin(km.getMaKM());
				if (!ds.isEmpty() && cboLoaiKM.getSelectedItem().equals("Theo hóa đơn")) {
					JOptionPane.showMessageDialog(this, "Không thể đổi thành KM hóa đơn vì đã có sản phẩm áp dụng.",
							"Không hợp lệ", JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			if (kmDAO.capNhatKhuyenMai(km)) {
				JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!");
				loadDataKhuyenMai();
				chonDongTheoMa(km.getMaKM());
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
		}
	}

	// ---------- SẢN PHẨM ÁP DỤNG ----------

	private void xuLyThemSanPhamApDung() {
		KhuyenMai km = getKhuyenMaiDangChon();
		if (km == null) {
			JOptionPane.showMessageDialog(this, "Hãy chọn 1 khuyến mãi trước.");
			tblKhuyenMai.requestFocus();
			return;
		}

		if (km.isKhuyenMaiHoaDon()) {
			JOptionPane.showMessageDialog(this, "KM hóa đơn không áp dụng sản phẩm.");
			return;
		}

		if (!km.isDangHoatDong()) {
			JOptionPane.showMessageDialog(this, "Chỉ KM đang hoạt động mới được thêm sản phẩm!");
			return;
		}

		// --- MỞ DIALOG CHỌN SẢN PHẨM ---
		JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
		DialogChonSanPhamApDung dlg = new DialogChonSanPhamApDung(parent);
		dlg.setVisible(true);

		// --- LẤY DANH SÁCH SẢN PHẨM ĐÃ CHỌN ---
		DefaultTableModel modelDaChon = dlg.getModelDaChon(); // bạn cần thêm hàm getModelDaChon() trong dialog

		if (modelDaChon.getRowCount() == 0) {
			return; // không chọn gì
		}

		// --- LƯU VÀO DB ---
		int them = 0;
		for (int i = 0; i < modelDaChon.getRowCount(); i++) {
			String maSP = modelDaChon.getValueAt(i, 0).toString();

			// kiểm tra tồn tại trong CTKM
			if (ctkmDAO.daTonTai(km.getMaKM(), maSP)) {
				continue; // bỏ qua nếu trùng
			}

			SanPham sp = spDAO.laySanPhamTheoMa(maSP);
			if (sp == null)
				continue; // dự phòng

			ChiTietKhuyenMaiSanPham ct = new ChiTietKhuyenMaiSanPham(sp, km);

			if (ctkmDAO.themChiTietKhuyenMaiSanPham(ct)) {
				them++;
			}
		}

		if (them > 0) {
			loadSanPhamApDung(km);
			JOptionPane.showMessageDialog(this, "Đã thêm " + them + " sản phẩm vào khuyến mãi!");
		} else {
			JOptionPane.showMessageDialog(this, "Không có sản phẩm nào được thêm.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	private void xuLyXoaSanPhamApDung() {
		KhuyenMai km = getKhuyenMaiDangChon();
		if (km == null) {
			JOptionPane.showMessageDialog(this, "Hãy chọn 1 khuyến mãi trước.");
			tblKhuyenMai.requestFocus();
			return;
		}

		if (km.isKhuyenMaiHoaDon()) {
			JOptionPane.showMessageDialog(this, "KM hóa đơn không có sản phẩm áp dụng.");
			return;
		}

		int row = tblSanPhamApDung.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Hãy chọn 1 sản phẩm để xóa!");
			tblSanPhamApDung.requestFocus();
			return;
		}

		String maSP = (String) tblSanPhamApDung.getValueAt(row, 0);

		int confirm = JOptionPane.showConfirmDialog(this, "Xóa sản phẩm '" + maSP + "' khỏi khuyến mãi?", "Xác nhận",
				JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		if (ctkmDAO.xoaChiTietKhuyenMaiSanPham(km.getMaKM(), maSP)) {
			loadSanPhamApDung(km);
		} else {
			JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	// ---------- LOAD DATA BẢNG ----------

	private void loadDataKhuyenMai() {
		dsKhuyenMai = kmDAO.layTatCaKhuyenMai();

		// ====== SORT ======
		dsKhuyenMai.sort((a, b) -> {
			// 1) So sánh theo ngày lập (ngày bắt đầu)
			int cmp = b.getNgayBatDau().compareTo(a.getNgayBatDau());
			if (cmp != 0)
				return cmp;

			// 2) Nếu trùng ngày, sort theo mã KM (giảm dần)
			return b.getMaKM().compareTo(a.getMaKM());
		});

		modelKhuyenMai.setRowCount(0);

		int stt = 1;
		for (KhuyenMai km : dsKhuyenMai) {
			modelKhuyenMai.addRow(new Object[] { stt++, km.getMaKM(), km.getTenKM(),
					km.getHinhThuc() != null ? km.getHinhThuc().getMoTa() : "", formatGiaTri(km),
					dfDate.format(km.getNgayBatDau()), dfDate.format(km.getNgayKetThuc()),
					km.isKhuyenMaiHoaDon() ? "Theo hóa đơn" : "Theo sản phẩm", tinhTrangThaiHienThi(km) });
		}
	}

	private void loadSanPhamApDung(KhuyenMai km) {
		modelSanPhamApDung.setRowCount(0);
		if (km == null)
			return;

		// KM hóa đơn: hiển thị 1 dòng mô tả
		if (km.isKhuyenMaiHoaDon()) {
			modelSanPhamApDung.addRow(new Object[] { "-", "Áp dụng cho toàn bộ hóa đơn", "-", "-", "-" });
			return;
		}

		List<ChiTietKhuyenMaiSanPham> dsCT = ctkmDAO.layChiTietKhuyenMaiTheoMaCoJoin(km.getMaKM());

		for (ChiTietKhuyenMaiSanPham ct : dsCT) {
			SanPham sp = ct.getSanPham();
			double giaGoc = sp.getGiaNhap();
			double giaKM = tinhGiaSauKhuyenMai(giaGoc, km);

			modelSanPhamApDung.addRow(new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), sp.getKeBanSanPham(),
					dfNumber.format(giaGoc), dfNumber.format(giaKM) });
		}
	}

	private void doToForm(int row) {
		if (row < 0 || row >= tblKhuyenMai.getRowCount())
			return;

		String maKM = (String) tblKhuyenMai.getValueAt(row, 1); // Cột 1 là Mã KM (cột 0 là STT)
		KhuyenMai km = null;

		// Tìm KM theo mã
		for (KhuyenMai k : dsKhuyenMai) {
			if (k.getMaKM().equals(maKM)) {
				km = k;
				break;
			}
		}

		if (km == null)
			return;

		txtMaKM.setText(km.getMaKM());
		txtTenKM.setForeground(Color.BLACK);
		txtTenKM.setText(km.getTenKM());

		// Chuyển LocalDate sang Date cho JDateChooser
		dateNgayBD.setDate(Date.from(km.getNgayBatDau().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		dateNgayKT.setDate(Date.from(km.getNgayKetThuc().atStartOfDay(ZoneId.systemDefault()).toInstant()));

		txtGiaTri.setForeground(Color.BLACK);
		txtGiaTri.setText(removeGrouping(dfNumber.format(km.getGiaTri())));

		txtDieuKien.setForeground(Color.BLACK);
		txtDieuKien.setText(removeGrouping(dfNumber.format(km.getDieuKienApDungHoaDon())));

		txtSoLuong.setForeground(Color.BLACK);
		txtSoLuong.setText(String.valueOf(km.getSoLuongKhuyenMai()));

		cboLoaiKM.setSelectedItem(km.isKhuyenMaiHoaDon() ? "Theo hóa đơn" : "Theo sản phẩm");
		if (km.getHinhThuc() != null)
			cboHinhThuc.setSelectedItem(km.getHinhThuc().getMoTa());
		cboTrangThai.setSelectedItem(tinhTrangThaiHienThi(km));

		loadSanPhamApDung(km);
	}

	private void xuLyTimKiem() {
		String kw = txtTimKiem.getText().trim().toLowerCase();
		modelKhuyenMai.setRowCount(0);

		int stt = 1;
		for (KhuyenMai km : dsKhuyenMai) {
			if (kw.isEmpty() || km.getMaKM().toLowerCase().contains(kw) || km.getTenKM().toLowerCase().contains(kw)) {
				modelKhuyenMai.addRow(new Object[] { stt++, km.getMaKM(), km.getTenKM(),
						km.getHinhThuc() != null ? km.getHinhThuc().getMoTa() : "", formatGiaTri(km),
						dfDate.format(km.getNgayBatDau()), dfDate.format(km.getNgayKetThuc()),
						km.isKhuyenMaiHoaDon() ? "Theo hóa đơn" : "Theo sản phẩm", tinhTrangThaiHienThi(km) });
			}
		}
	}

	// ---------- FORM <-> ENTITY ----------

	private KhuyenMai getKhuyenMaiFromForm(boolean isThemMoi) {

		// ===== 1. Mã khuyến mãi =====
		String maKM;
		if (isThemMoi) {
			// luôn sinh mã mới
			maKM = kmDAO.taoMaKhuyenMai();
			txtMaKM.setText(maKM);
		} else {
			maKM = txtMaKM.getText().trim();
		}

		// ===== 2. Tên khuyến mãi =====
		String ten = txtTenKM.getText().trim();
		if (ten.isEmpty()) {
			showErrorAndFocus(txtTenKM, "Tên khuyến mãi không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		// ===== 3. Ngày bắt đầu - kết thúc =====
		Date dateBD = dateNgayBD.getDate();
		Date dateKT = dateNgayKT.getDate();

		if (dateBD == null) {
			JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được bỏ trống!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			dateNgayBD.requestFocus();
			return null;
		}
		if (dateKT == null) {
			JOptionPane.showMessageDialog(this, "Ngày kết thúc không được bỏ trống!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			dateNgayKT.requestFocus();
			return null;
		}

		// Chuyển Date sang LocalDate
		LocalDate ngayBD = dateBD.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate ngayKT = dateKT.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (ngayKT.isBefore(ngayBD)) {
			JOptionPane.showMessageDialog(this, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			dateNgayKT.requestFocus();
			return null;
		}

		if (isThemMoi && ngayBD.isBefore(LocalDate.now())) {
			JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải từ hôm nay trở đi!", "Cảnh báo",
					JOptionPane.WARNING_MESSAGE);
			dateNgayBD.requestFocus();
			return null;
		}

		// ===== 4. Loại KM =====
		String loai = (String) cboLoaiKM.getSelectedItem();
		boolean kmHoaDon = loai.equals("Theo hóa đơn");

		// ===== 5. Hình thức =====
		String ht = (String) cboHinhThuc.getSelectedItem();
		HinhThucKM hinhThuc = mapHinhThucFromText(ht);

		// ===== 6. Giá trị =====
		double giaTri = parseDoubleField(txtGiaTri, "Giá trị");
		if (giaTri <= 0) {
			showErrorAndFocus(txtGiaTri, "Giá trị khuyến mãi phải lớn hơn 0!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		if (hinhThuc == HinhThucKM.GIAM_GIA_PHAN_TRAM && giaTri > 100) {
			showErrorAndFocus(txtGiaTri, "Giảm giá phần trăm không được vượt quá 100%!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		// ===== 7. Điều kiện =====
		double dieuKien = parseDoubleField(txtDieuKien, "Điều kiện");

		if (kmHoaDon) {
			if (dieuKien < 0) {
				showErrorAndFocus(txtDieuKien, "Điều kiện phải >= 0!", JOptionPane.WARNING_MESSAGE);
				return null;
			}
		} else {
			// theo sản phẩm thì điều kiện luôn = 0
			dieuKien = 0;
			txtDieuKien.setText("0");
		}

		// ===== 8. Số lượng =====
		int soLuong = (int) parseDoubleField(txtSoLuong, "Số lượng");
		if (soLuong <= 0) {
			showErrorAndFocus(txtSoLuong, "Số lượng phải lớn hơn 0!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		// ===== 9. Trạng thái =====
		boolean trangThai = true; // mặc định đang hoạt động khi thêm
		if (!isThemMoi) {
			trangThai = cboTrangThai.getSelectedItem().equals("Đang hoạt động");
		}

		// ===== TẠO OBJECT =====
		KhuyenMai km = new KhuyenMai();
		km.setMaKM(maKM);
		km.setTenKM(ten);
		km.setNgayBatDau(ngayBD);
		km.setNgayKetThuc(ngayKT);
		km.setKhuyenMaiHoaDon(kmHoaDon);
		km.setHinhThuc(hinhThuc);
		km.setGiaTri(giaTri);
		km.setDieuKienApDungHoaDon(dieuKien);
		km.setSoLuongKhuyenMai(soLuong);
		km.setTrangThai(trangThai);

		km.capNhatTrangThaiTuDong();

		return km;
	}

	private HinhThucKM mapHinhThucFromText(String text) {
		for (HinhThucKM ht : HinhThucKM.values()) {
			if (ht.getMoTa().equals(text))
				return ht;
		}
		// fallback
		return HinhThucKM.GIAM_GIA_TIEN;
	}

	private double parseDoubleField(JTextField txt, String fieldName) {
		String s = txt.getText().trim().replace(".", "").replace(",", "");
		if (s.isEmpty())
			return 0;
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(fieldName + " không hợp lệ.");
		}
	}

	private void lamMoiForm() {
		String newMa = kmDAO.taoMaKhuyenMai();
		txtMaKM.setText(newMa);
		txtMaKM.setEditable(false);

		// Reset text fields - set empty text, placeholder will show when focus is lost
		txtTenKM.setText("");
		txtTenKM.setForeground(Color.GRAY);
		PlaceholderSupport.addPlaceholder(txtTenKM, "Nhập tên khuyến mãi");
		txtGiaTri.setText("");
		txtGiaTri.setForeground(Color.GRAY);
		PlaceholderSupport.addPlaceholder(txtGiaTri, "Nhập giá trị");

		txtDieuKien.setText("");
		txtDieuKien.setForeground(Color.GRAY);
		PlaceholderSupport.addPlaceholder(txtDieuKien, "Nhập điều kiện");

		txtSoLuong.setText("");
		txtSoLuong.setForeground(Color.GRAY);
		PlaceholderSupport.addPlaceholder(txtSoLuong, "Nhập số lượng");

		// Set ngày mặc định là hôm nay
		dateNgayBD.setDate(new Date());
		dateNgayKT.setDate(new Date());

		cboLoaiKM.setSelectedIndex(0);
		cboHinhThuc.setSelectedIndex(0);
		cboTrangThai.setSelectedIndex(0);
		tblKhuyenMai.clearSelection();
		modelSanPhamApDung.setRowCount(0);
		loadDataKhuyenMai();
		capNhatTrangThaiNut(); // Cập nhật trạng thái nút
		txtTimKiem.requestFocus();
	}

	/**
	 * Cập nhật trạng thái hiển thị các nút dựa trên việc có chọn dòng hay không -
	 * Không chọn dòng: Hiện nút Thêm, ẩn Cập nhật - Có chọn dòng: Ẩn nút Thêm, hiện
	 * Cập nhật - btnChonSP: chỉ enable khi chọn KM loại "theo sản phẩm" - btnXoaSP:
	 * chỉ enable khi chọn KM loại "theo sản phẩm" VÀ có dòng được chọn trong bảng
	 * sản phẩm
	 */
	private void capNhatTrangThaiNut() {
		int row = tblKhuyenMai.getSelectedRow();
		boolean coDongDuocChon = (row != -1);

		// Nút Thêm: chỉ hiện khi KHÔNG chọn dòng nào
		btnThem.setEnabled(!coDongDuocChon);

		// Nút Cập nhật: chỉ hiện khi CÓ chọn dòng
		btnSua.setEnabled(coDongDuocChon);

		// Kiểm tra KM đang chọn có phải loại "theo sản phẩm" không
		boolean isTheoSanPham = false;
		if (coDongDuocChon) {
			KhuyenMai km = getKhuyenMaiDangChon();
			isTheoSanPham = km != null && !km.isKhuyenMaiHoaDon();
		}

		// Nút Chọn SP: chỉ enable khi chọn KM loại "theo sản phẩm"
		btnChonSP.setEnabled(isTheoSanPham);

		// Nút Xóa SP: chỉ enable khi chọn KM loại "theo sản phẩm" VÀ có dòng được chọn
		// trong bảng sản phẩm
		boolean coDongSPDuocChon = tblSanPhamApDung.getSelectedRow() != -1;
		btnXoaSP.setEnabled(isTheoSanPham && coDongSPDuocChon);
	}

	// ---------- HELPER ----------

	private KhuyenMai getKhuyenMaiDangChon() {
		int row = tblKhuyenMai.getSelectedRow();
		if (row < 0 || row >= dsKhuyenMai.size())
			return null;
		// Tìm theo mã trong dsKhuyenMai (do bảng có thể đang filter)
		String maKM = (String) tblKhuyenMai.getValueAt(row, 1); // Cột 1 là Mã KM (cột 0 là STT)
		for (KhuyenMai km : dsKhuyenMai) {
			if (km.getMaKM().equals(maKM))
				return km;
		}
		return null;
	}

	private void chonDongTheoMa(String maKM) {
		for (int i = 0; i < modelKhuyenMai.getRowCount(); i++) {
			if (maKM.equals(modelKhuyenMai.getValueAt(i, 1))) { // Cột 1 là Mã KM (cột 0 là STT)
				tblKhuyenMai.setRowSelectionInterval(i, i);
				doToForm(i);
				break;
			}
		}
	}

	private String formatGiaTri(KhuyenMai km) {
		if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
			return dfNumber.format(km.getGiaTri()) + " %";
		}
		return dfNumber.format(km.getGiaTri());
	}

	private String tinhTrangThaiHienThi(KhuyenMai km) {
		if (km.isDangHoatDong())
			return "Đang hoạt động";
		return "Ngưng hoạt động";
	}

	private double tinhGiaSauKhuyenMai(double giaGoc, KhuyenMai km) {
		if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_PHAN_TRAM) {
			return Math.max(0, giaGoc * (1 - km.getGiaTri() / 100.0));
		} else if (km.getHinhThuc() == HinhThucKM.GIAM_GIA_TIEN) {
			return Math.max(0, giaGoc - km.getGiaTri());
		}
		// Tặng thêm: giá không đổi
		return giaGoc;
	}

	private String removeGrouping(String s) {
		return s.replace(".", "").replace(",", "");
	}

	// --- HELPER UI ---
	private JLabel createLabel(String text, int x, int y) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(FONT_TEXT);
		lbl.setBounds(x, y, 100, 35);
		return lbl;
	}

	private JTextField createTextField(int x, int y, int w) {
		JTextField txt = new JTextField();
		txt.setFont(FONT_TEXT);
		txt.setBounds(x, y, w, 35);
		return txt;
	}

	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(FONT_TEXT);
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setFont(FONT_BOLD);
		table.getTableHeader().setBackground(new Color(33, 150, 243));
		table.getTableHeader().setForeground(Color.WHITE);
		return table;
	}

	private void showErrorAndFocus(JTextField txt, String message, int messageType) {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(this, message, "Thông báo", messageType);
			txt.requestFocus();
			txt.selectAll();
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản Lý Khuyến Mãi");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1500, 850);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new KhuyenMai_GUI());
			frame.setVisible(true);
		});
	}
}
