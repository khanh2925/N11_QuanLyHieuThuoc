package gui.quanly;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;

import javax.swing.*;
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
import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

@SuppressWarnings("serial")
public class NhanVien_QL_GUI extends JPanel implements ActionListener {

	// --- COMPONENTS UI ---
	private JPanel pnHeader, pnCenter;
	private JSplitPane splitPane;

	// Form nhập liệu
	private JTextField txtMaNV, txtTenNV, txtSDT, txtDiaChi, txtNgaySinh;
	private JComboBox<String> cboGioiTinh, cboChucVu, cboCaLam, cboTrangThai;
	private JLabel lblHinhAnh;
	private JButton btnChonAnh;
	// private String currentImagePath = "/resources/images/icon_anh_nv_null.png";

	// Buttons
	private PillButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;

	// Search & Table
	private JTextField txtTimKiem;
	private JTable tblNhanVien;
	private DefaultTableModel modelNhanVien;

	// DAO & DATA THẬT
	private NhanVien_DAO nvDAO = new NhanVien_DAO();
	private TaiKhoan_DAO tkDAO = new TaiKhoan_DAO();
	private List<NhanVien> dsNhanVien = new ArrayList<>();

	// Utils
	private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 16);
	private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
	private final Color COLOR_PRIMARY = new Color(33, 150, 243);
	private final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public NhanVien_QL_GUI() {
		setPreferredSize(new Dimension(1537, 850));

		// KHỞI TẠO GIAO DIỆN
		initialize();

		// LOAD DỮ LIỆU THẬT
		loadDataNhanVien();

		// TẠO SẴN MÃ MỚI CHO FORM
		lamMoiForm();

		// Nếu có dữ liệu thì chọn dòng đầu
		if (!dsNhanVien.isEmpty()) {
			tblNhanVien.setRowSelectionInterval(0, 0);
			doToForm(0);
		}
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		// Header
		taoPhanHeader();
		add(pnHeader, BorderLayout.NORTH);

		// Center (SplitPane)
		taoPhanCenter();
		add(pnCenter, BorderLayout.CENTER);

		setupKeyboardShortcuts(); // Thiết lập phím tắt
	}

	// =====================================================================
	// PHẦN HEADER
	// =====================================================================
	private void taoPhanHeader() {
		pnHeader = new JPanel(null);
		pnHeader.setPreferredSize(new Dimension(1073, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));

		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo mã, tên, số điện thoại... (F1 / Ctrl+F)");
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		txtTimKiem.setBounds(25, 17, 520, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setBackground(Color.WHITE);
		txtTimKiem.setForeground(Color.GRAY);
		txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
		txtTimKiem.addActionListener(e -> xuLyTimKiem());

		pnHeader.add(txtTimKiem);

		btnTimKiem = new PillButton(
				"<html>" +
						"<center>" +
						"TÌM KIẾM<br>" +
						"<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
						"</center>" +
						"</html>");
		btnTimKiem.setBounds(560, 22, 130, 50);
		btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTimKiem.setToolTipText(
				"<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã, tên và bộ lọc</html>");
		btnTimKiem.addActionListener(e -> xuLyTimKiem());
		pnHeader.add(btnTimKiem);

		// Nút Xuất Excel
		PillButton btnXuatExcel = new PillButton(
				"<html>" +
						"<center>" +
						"XUẤT EXCEL<br>" +
						"<span style='font-size:10px; color:#888888;'>(Ctrl+E)</span>" +
						"</center>" +
						"</html>");
		btnXuatExcel.setBounds(705, 22, 150, 50);
		btnXuatExcel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnXuatExcel.setToolTipText("<html><b>Phím tắt:</b> Ctrl+E<br>Xuất dữ liệu ra file Excel</html>");
		btnXuatExcel.addActionListener(e -> xuatExcel());
		pnHeader.add(btnXuatExcel);
	}

	// =====================================================================
	// PHẦN CENTER (SPLIT PANE)
	// =====================================================================
	private void taoPhanCenter() {
		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));

		// --- A. PHẦN TRÊN: FORM + NÚT ---
		JPanel pnTopWrapper = new JPanel(new BorderLayout());
		pnTopWrapper.setBackground(Color.WHITE);
		pnTopWrapper.setBorder(createTitledBorder("Thông tin nhân viên"));

		// 1. Form Nhập Liệu (Center)
		JPanel pnForm = new JPanel(null);
		pnForm.setBackground(Color.WHITE);
		taoFormNhapLieu(pnForm);
		pnTopWrapper.add(pnForm, BorderLayout.CENTER);

		// 2. Panel Nút (East)
		JPanel pnButton = new JPanel();
		pnButton.setBackground(Color.WHITE);
		taoPanelNutBam(pnButton);
		pnTopWrapper.add(pnButton, BorderLayout.EAST);

		// --- B. PHẦN DƯỚI: BẢNG ---
		JPanel pnTable = new JPanel(new BorderLayout());
		pnTable.setBackground(Color.WHITE);
		taoBangDanhSach(pnTable);

		// --- C. SPLIT PANE ---
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnTopWrapper, pnTable);
		splitPane.setDividerLocation(380);
		splitPane.setResizeWeight(0.0);

		pnCenter.add(splitPane, BorderLayout.CENTER);
	}

	private void taoFormNhapLieu(JPanel p) {

		int xStart = 50, yStart = 30;
		int hText = 35, wLbl = 110, wTxt = 300, gap = 25;

		int xCol2 = xStart + wLbl + wTxt + 120;

		// ===== HÀNG 1 =====
		p.add(createLabel("Mã NV:", xStart, yStart));
		txtMaNV = createTextField(xStart + wLbl, yStart, wTxt);
		txtMaNV.setEditable(false);
		p.add(txtMaNV);

		p.add(createLabel("Trạng thái:", xCol2, yStart));
		cboTrangThai = new JComboBox<>(new String[] { "Đang làm", "Đã nghỉ" });
		cboTrangThai.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
		cboTrangThai.setFont(FONT_TEXT);
		p.add(cboTrangThai);

		// ===== HÀNG 2 =====
		yStart += hText + gap;

		p.add(createLabel("Họ tên:", xStart, yStart));
		txtTenNV = createTextField(xStart + wLbl, yStart, wTxt);
		p.add(txtTenNV);

		p.add(createLabel("Ngày sinh:", xCol2, yStart));
		txtNgaySinh = createTextField(xCol2 + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtNgaySinh, "dd/MM/yyyy");
		p.add(txtNgaySinh);

		// ===== HÀNG 3 =====
		yStart += hText + gap;

		p.add(createLabel("Giới tính:", xStart, yStart));
		cboGioiTinh = new JComboBox<>(new String[] { "Nam", "Nữ" });
		cboGioiTinh.setBounds(xStart + wLbl, yStart, wTxt, hText);
		cboGioiTinh.setFont(FONT_TEXT);
		p.add(cboGioiTinh);

		p.add(createLabel("SĐT:", xCol2, yStart));
		txtSDT = createTextField(xCol2 + wLbl, yStart, wTxt);
		p.add(txtSDT);

		// ===== HÀNG 4 =====
		yStart += hText + gap;

		p.add(createLabel("Chức vụ:", xStart, yStart));
		cboChucVu = new JComboBox<>(new String[] { "Nhân viên", "Quản lý" });
		cboChucVu.setBounds(xStart + wLbl, yStart, wTxt, hText);
		cboChucVu.setFont(FONT_TEXT);
		p.add(cboChucVu);

		p.add(createLabel("Ca làm:", xCol2, yStart));
		// Entity quy ước: 1=Sáng, 2=Chiều, 3=Tối
		cboCaLam = new JComboBox<>(new String[] { "Sáng", "Chiều", "Tối" });
		cboCaLam.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
		cboCaLam.setFont(FONT_TEXT);
		p.add(cboCaLam);

		// ===== HÀNG 5 =====
		yStart += hText + gap;

		p.add(createLabel("Địa chỉ:", xStart, yStart));
		txtDiaChi = createTextField(xStart + wLbl, yStart, wTxt);
		p.add(txtDiaChi);

	}

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

		btnThem = new PillButton(
				"<html>" +
						"<center>" +
						"THÊM NV<br>" +
						"<span style='font-size:10px; color:#888888;'>(Ctrl+N)</span>" +
						"</center>" +
						"</html>");
		btnThem.setFont(FONT_BOLD);
		btnThem.setPreferredSize(new Dimension(btnW, btnH));
		btnThem.setToolTipText("<html><b>Phím tắt:</b> Ctrl+N<br>Thêm nhân viên mới</html>");
		btnThem.addActionListener(this);
		gbc.gridy = 0;
		p.add(btnThem, gbc);

		btnSua = new PillButton(
				"<html>" +
						"<center>" +
						"CẬP NHẬT<br>" +
						"<span style='font-size:10px; color:#888888;'>(Ctrl+U)</span>" +
						"</center>" +
						"</html>");
		btnSua.setFont(FONT_BOLD);
		btnSua.setPreferredSize(new Dimension(btnW, btnH));
		btnSua.setToolTipText("<html><b>Phím tắt:</b> Ctrl+U<br>Cập nhật thông tin nhân viên đang chọn</html>");
		btnSua.addActionListener(this);
		gbc.gridy = 1;
		p.add(btnSua, gbc);

		btnXoa = new PillButton(
				"<html>" +
						"<center>" +
						"THÔI VIỆC<br>" +
						"<span style='font-size:10px; color:#888888;'>(Ctrl+D)</span>" +
						"</center>" +
						"</html>");
		btnXoa.setFont(FONT_BOLD);
		btnXoa.setPreferredSize(new Dimension(btnW, btnH));
		btnXoa.setToolTipText(
				"<html><b>Phím tắt:</b> Ctrl+D<br>Đánh dấu nhân viên đã nghỉ (Không xóa khỏi hệ thống)</html>");
		btnXoa.addActionListener(this);
		gbc.gridy = 2;
		p.add(btnXoa, gbc);

		btnLamMoi = new PillButton(
				"<html>" +
						"<center>" +
						"LÀM MỚI<br>" +
						"<span style='font-size:10px; color:#888888;'>(F5)</span>" +
						"</center>" +
						"</html>");
		btnLamMoi.setFont(FONT_BOLD);
		btnLamMoi.setPreferredSize(new Dimension(btnW, btnH));
		btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới form nhập liệu</html>");
		btnLamMoi.addActionListener(this);
		gbc.gridy = 3;
		p.add(btnLamMoi, gbc);
	}

	private void taoBangDanhSach(JPanel p) {
		String[] cols = { "Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Chức vụ", "Ca làm", "Trạng thái" };
		modelNhanVien = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblNhanVien = setupTable(modelNhanVien);

		// Render màu trạng thái
		tblNhanVien.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				if ("Đang làm".equals(value))
					lbl.setForeground(new Color(0, 128, 0));
				else
					lbl.setForeground(Color.RED);
				return lbl;
			}
		});

		tblNhanVien.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				doToForm(tblNhanVien.getSelectedRow());
			}
		});

		JScrollPane scr = new JScrollPane(tblNhanVien);
		scr.setBorder(createTitledBorder("Danh sách nhân viên"));
		p.add(scr, BorderLayout.CENTER);
	}

	// =====================================================================
	// LOGIC CHÍNH
	// =====================================================================

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		// 1. THÊM
		if (o.equals(btnThem)) {
			xuLyThem();
			System.out.println("Đã thêm nhân viên");
		}
		// 2. SỬA
		else if (o.equals(btnSua)) {
			xuLyCapNhat();
		}
		// 3. "XÓA" = CHO NGHỈ (SOFT DELETE)
		else if (o.equals(btnXoa)) {
			xuLyChoNghi();
		}
		// 4. LÀM MỚI
		else if (o.equals(btnLamMoi)) {
			lamMoiForm();
		}
		// 5. CHỌN ẢNH (nếu bạn muốn dùng)
		else if (o.equals(btnChonAnh)) {
			chonAnh();
		}
	}

	// ---------- CRUD ----------

	private void xuLyThem() {
		try {
			NhanVien nv = getNhanVienFromForm(true);
			if (nv == null)
				return;

			if (nvDAO.themNhanVien(nv)) {
				// Tạo tài khoản tự động
				String maTK = tkDAO.taoMaTaiKhoanTuDong();
				String tenDangNhap = nv.getMaNhanVien(); // Tên đăng nhập = Mã nhân viên
				String matKhau = "123456aA@"; // Mật khẩu mặc định

				TaiKhoan tk = new TaiKhoan(maTK, tenDangNhap, matKhau, nv);

				if (tkDAO.themTaiKhoan(tk)) {
					JOptionPane.showMessageDialog(this,
							"Thêm nhân viên thành công!\n\n" + "Thông tin tài khoản:\n" + "Tên đăng nhập: "
									+ tenDangNhap + "\n" + "Mật khẩu: " + matKhau,
							"Thành công", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(this,
							"Thêm nhân viên thành công!\n" + "Nhưng tạo tài khoản thất bại. Vui lòng tạo thủ công.",
							"Cảnh báo", JOptionPane.WARNING_MESSAGE);
				}

				loadDataNhanVien();
				chonDongTheoMa(nv.getMaNhanVien());
				lamMoiForm();
			} else {
				JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IllegalArgumentException ex) {
		}
	}

	private void xuLyCapNhat() {
		int row = tblNhanVien.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân viên để cập nhật!");
			return;
		}

		try {
			NhanVien nv = getNhanVienFromForm(false);
			if (nv == null)
				return;

			if (nvDAO.capNhatNhanVien(nv)) {
				JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
				loadDataNhanVien();
				chonDongTheoMa(nv.getMaNhanVien());
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IllegalArgumentException ex) {
		}
	}

	// KHÔNG XOÁ RECORD -> CHUYỂN SANG "ĐÃ NGHỈ"
	private void xuLyChoNghi() {
		int row = tblNhanVien.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 nhân viên để cho nghỉ!");
			return;
		}

		String maNV = (String) tblNhanVien.getValueAt(row, 0);

		int opt = JOptionPane.showConfirmDialog(this,
				"Đánh dấu nhân viên " + maNV + " là 'Đã nghỉ'? (Không xóa khỏi hệ thống)", "Xác nhận",
				JOptionPane.YES_NO_OPTION);
		if (opt != JOptionPane.YES_OPTION)
			return;

		if (nvDAO.capNhatTrangThai(maNV, false)) {
			JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái nhân viên.");
			loadDataNhanVien();
			chonDongTheoMa(maNV);
		} else {
			JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
	}

	// =====================================================================
	// DATA BẢNG
	// =====================================================================

	private void loadDataNhanVien() {
		dsNhanVien = nvDAO.layTatCaNhanVien();
		modelNhanVien.setRowCount(0);

		for (NhanVien nv : dsNhanVien) {
			modelNhanVien.addRow(new Object[] { nv.getMaNhanVien(), nv.getTenNhanVien(), nv.isGioiTinh() ? "Nam" : "Nữ",
					formatNgay(nv.getNgaySinh()), nv.getSoDienThoai(), nv.isQuanLy() ? "Quản lý" : "Nhân viên",
					nv.getTenCaLam(), nv.isTrangThai() ? "Đang làm" : "Đã nghỉ" });
		}
	}

	private void doToForm(int row) {
		if (row < 0)
			return;
		String maNV = (String) tblNhanVien.getValueAt(row, 0);
		NhanVien nv = timNhanVienTrongDanhSach(maNV);
		if (nv == null)
			return;

		txtMaNV.setText(nv.getMaNhanVien());
		txtTenNV.setText(nv.getTenNhanVien());
		cboGioiTinh.setSelectedItem(nv.isGioiTinh() ? "Nam" : "Nữ");
		txtNgaySinh.setText(formatNgay(nv.getNgaySinh()));
		txtNgaySinh.setForeground(Color.BLACK);
		txtSDT.setText(nv.getSoDienThoai());
		txtDiaChi.setText(nv.getDiaChi());
		cboChucVu.setSelectedItem(nv.isQuanLy() ? "Quản lý" : "Nhân viên");

		int ca = nv.getCaLam();
		switch (ca) {
			case 1 -> cboCaLam.setSelectedItem("Sáng");
			case 2 -> cboCaLam.setSelectedItem("Chiều");
			case 3 -> cboCaLam.setSelectedItem("Tối");
			default -> cboCaLam.setSelectedIndex(0);
		}

		cboTrangThai.setSelectedItem(nv.isTrangThai() ? "Đang làm" : "Đã nghỉ");
	}

	private NhanVien timNhanVienTrongDanhSach(String maNV) {
		for (NhanVien nv : dsNhanVien) {
			if (nv.getMaNhanVien().equals(maNV))
				return nv;
		}
		return null;
	}

	private void chonDongTheoMa(String maNV) {
		for (int i = 0; i < modelNhanVien.getRowCount(); i++) {
			if (maNV.equals(modelNhanVien.getValueAt(i, 0))) {
				tblNhanVien.setRowSelectionInterval(i, i);
				tblNhanVien.scrollRectToVisible(tblNhanVien.getCellRect(i, 0, true));
				doToForm(i);
				break;
			}
		}
	}

	private void xuLyTimKiem() {
		String kw = txtTimKiem.getText().trim();
		if (kw.isEmpty()) {
			loadDataNhanVien();
			return;
		}

		List<NhanVien> ketQua = nvDAO.timNhanVien(kw);
		modelNhanVien.setRowCount(0);
		for (NhanVien nv : ketQua) {
			modelNhanVien.addRow(new Object[] { nv.getMaNhanVien(), nv.getTenNhanVien(), nv.isGioiTinh() ? "Nam" : "Nữ",
					formatNgay(nv.getNgaySinh()), nv.getSoDienThoai(), nv.isQuanLy() ? "Quản lý" : "Nhân viên",
					nv.getTenCaLam(), nv.isTrangThai() ? "Đang làm" : "Đã nghỉ" });
		}
	}

	/**
	 * Thiết lập phím tắt cho màn hình Quản lý Nhân viên
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

		// Ctrl+E: Xuất Excel
		inputMap.put(KeyStroke.getKeyStroke("control E"), "xuatExcel");
		actionMap.put("xuatExcel", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuatExcel();
			}
		});

		// Ctrl+N: Thêm
		inputMap.put(KeyStroke.getKeyStroke("control N"), "themNV");
		actionMap.put("themNV", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuLyThem();
			}
		});

		// Ctrl+U: Cập nhật
		inputMap.put(KeyStroke.getKeyStroke("control U"), "capNhatNV");
		actionMap.put("capNhatNV", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuLyCapNhat();
			}
		});

		// Ctrl+D: Thôi việc
		inputMap.put(KeyStroke.getKeyStroke("control D"), "choNghiNV");
		actionMap.put("choNghiNV", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuLyChoNghi();
			}
		});
	}

	// =====================================================================
	// FORM <-> ENTITY
	// =====================================================================

	private NhanVien getNhanVienFromForm(boolean isThemMoi) {
		// 1. Mã NV
		String maNV;
		if (isThemMoi) {
			maNV = nvDAO.taoMaNhanVienTuDong();
			txtMaNV.setText(maNV);
		} else {
			maNV = txtMaNV.getText().trim();
			if (maNV.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Mã nhân viên không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}

		// 2. Tên NV
		String ten = txtTenNV.getText().trim();
		if (ten.isEmpty()) {
			showErrorAndFocus(txtTenNV, "Tên nhân viên không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		// 3. Ngày sinh
		String strNgaySinh = txtNgaySinh.getText().trim();
		if (strNgaySinh.isEmpty()) {
			showErrorAndFocus(txtNgaySinh, "Ngày sinh không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		LocalDate ngaySinh;
		try {
			ngaySinh = LocalDate.parse(strNgaySinh, dfDate);
		} catch (DateTimeParseException e) {
			showErrorAndFocus(txtNgaySinh, "Ngày sinh không đúng định dạng dd/MM/yyyy!", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		int age = Period.between(ngaySinh, LocalDate.now()).getYears();
		if (age < 18) {
			showErrorAndFocus(txtNgaySinh, "Nhân viên phải đủ 18 tuổi!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		// 4. Giới tính
		boolean gioiTinh = "Nam".equals(cboGioiTinh.getSelectedItem());

		// 5. SĐT
		String sdt = txtSDT.getText().trim();
		if (sdt.isEmpty()) {
			showErrorAndFocus(txtSDT, "Số điện thoại không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		if (!sdt.matches("^0\\d{9}$")) {
			showErrorAndFocus(txtSDT, "Số điện thoại phải gồm 10 số và bắt đầu bằng 0!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		// 6. Địa chỉ
		String diaChi = txtDiaChi.getText().trim();
		if (diaChi.isEmpty()) {
			showErrorAndFocus(txtDiaChi, "Địa chỉ không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		// 7. Chức vụ
		boolean quanLy = "Quản lý".equals(cboChucVu.getSelectedItem());

		// 8. Ca làm
		String caText = (String) cboCaLam.getSelectedItem();
		int caLam = switch (caText) {
			case "Sáng" -> 1;
			case "Chiều" -> 2;
			case "Tối" -> 3;
			default -> 1;
		};

		// 9. Trạng thái
		boolean trangThai = "Đang làm".equals(cboTrangThai.getSelectedItem());

		return new NhanVien(maNV, ten, gioiTinh, ngaySinh, sdt, diaChi, quanLy, caLam, trangThai);
	}

	// =====================================================================
	// HELPER
	// =====================================================================

	private String formatNgay(LocalDate d) {
		return d != null ? dfDate.format(d) : "";
	}

	private void lamMoiForm() {
		String newMa = nvDAO.taoMaNhanVienTuDong();
		txtMaNV.setText(newMa);
		txtMaNV.setEditable(false);

		txtTenNV.setText("");
		txtNgaySinh.setText("");
		txtSDT.setText("");
		txtDiaChi.setText("");
		cboGioiTinh.setSelectedIndex(0);
		cboChucVu.setSelectedIndex(0);
		cboCaLam.setSelectedIndex(0);
		cboTrangThai.setSelectedIndex(0);
		txtTenNV.requestFocus();
		tblNhanVien.clearSelection();
	}

	// --- Helpers UI & Ảnh ---
	private void chonAnh() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Image", "jpg", "png"));
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			setHinhAnhLocal(file.getAbsolutePath());
		}
	}

	private void setHinhAnh(String name) {
		try {
			URL url = getClass().getResource("/resources/images/" + name);
			if (url == null)
				url = getClass().getResource("/resources/images/icon_anh_nv_null.png");
			lblHinhAnh = new JLabel();
			lblHinhAnh.setIcon(
					new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
		} catch (Exception e) {
			if (lblHinhAnh != null)
				lblHinhAnh.setText("Ảnh lỗi");
		}
	}

	private void setHinhAnhLocal(String path) {
		if (lblHinhAnh == null)
			return;
		lblHinhAnh
				.setIcon(new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
		lblHinhAnh.setText("");
	}

	private void showErrorAndFocus(JTextField txt, String message, int messageType) {
		SwingUtilities.invokeLater(() -> {
			JOptionPane.showMessageDialog(this, message, "Thông báo", messageType);
			txt.requestFocus();
			txt.selectAll();
		});
		throw new IllegalArgumentException(message);
	}

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
		table.getTableHeader().setBackground(COLOR_PRIMARY);
		table.getTableHeader().setForeground(Color.WHITE);
		return table;
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
				TitledBorder.LEFT, TitledBorder.TOP, FONT_BOLD, Color.DARK_GRAY);
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
			fileChooser.setSelectedFile(new File("QuanLyNhanVien.xlsx"));
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

	// MAIN TEST
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản Lý Nhân Viên");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1500, 850);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new NhanVien_QL_GUI());
			frame.setVisible(true);
		});
	}
}
