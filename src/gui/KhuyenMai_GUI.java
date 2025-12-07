package gui;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

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

/**
 * GUI Quản lý khuyến mãi - Giữ nguyên layout cũ - Dùng Entity + DAO thật
 */
@SuppressWarnings("serial")
public class KhuyenMai_GUI extends JPanel implements ActionListener {

	// UI Components
	private JPanel pnHeader, pnCenter;
	private JSplitPane splitPane;

	// Inputs
	private JTextField txtMaKM, txtTenKM, txtNgayBD, txtNgayKT, txtGiaTri, txtDieuKien, txtSoLuong;
	private JComboBox<String> cboLoaiKM, cboHinhThuc, cboTrangThai;

	// Buttons
	private PillButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem, btnChonSP, btnXoaSP;
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
		loadDataKhuyenMai();
		lamMoiForm(); // sinh mã mới ngay từ đầu
		if (!dsKhuyenMai.isEmpty()) {
			tblKhuyenMai.setRowSelectionInterval(0, 0);
			doToForm(0);
		}
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
		PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm khuyến mãi...");
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		txtTimKiem.setBounds(25, 17, 500, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		pnHeader.add(txtTimKiem);

		btnTimKiem = new PillButton("Tìm kiếm");
		btnTimKiem.setBounds(540, 22, 130, 50);
		btnTimKiem.setFont(FONT_BOLD);
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
		p.add(txtTenKM);

		// ===== HÀNG 2 =====
		yStart += hText + gap;
		p.add(createLabel("Ngày BĐ:", xStart, yStart));
		txtNgayBD = createTextField(xStart + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtNgayBD, "dd/MM/yyyy");
		p.add(txtNgayBD);

		p.add(createLabel("Ngày KT:", xCol2, yStart));
		txtNgayKT = createTextField(xCol2 + wLbl, yStart, wTxt);
		PlaceholderSupport.addPlaceholder(txtNgayKT, "dd/MM/yyyy");
		p.add(txtNgayKT);

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
		cboHinhThuc = new JComboBox<>(new String[] { HinhThucKM.GIAM_GIA_PHAN_TRAM.getMoTa(),
				HinhThucKM.GIAM_GIA_TIEN.getMoTa(), HinhThucKM.TANG_THEM.getMoTa() });
		cboHinhThuc.setBounds(xCol2 + wLbl, yStart, wTxt, hText);
		cboHinhThuc.setFont(FONT_TEXT);
		p.add(cboHinhThuc);

		// ===== HÀNG 4 =====
		yStart += hText + gap;
		p.add(createLabel("Giá trị:", xStart, yStart));
		txtGiaTri = createTextField(xStart + wLbl, yStart, wTxt);
		p.add(txtGiaTri);

		p.add(createLabel("Điều kiện:", xCol2, yStart));
		txtDieuKien = createTextField(xCol2 + wLbl, yStart, wTxt);
		p.add(txtDieuKien);

		// ===== HÀNG 5 =====
		yStart += hText + gap;
		p.add(createLabel("Số lượng:", xStart, yStart));
		txtSoLuong = createTextField(xStart + wLbl, yStart, wTxt);
		p.add(txtSoLuong);

		p.add(createLabel("Trạng thái:", xCol2, yStart));
		cboTrangThai = new JComboBox<>(new String[] { "Đang hoạt động", "Tạm ngưng", "Hết hạn" });
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

		btnThem = createPillButton("Tạo KM", 140, 45);
		btnThem.addActionListener(this); // ✅ THÊM
		gbc.gridy = 0;
		p.add(btnThem, gbc);

		btnSua = createPillButton("Cập nhật", 140, 45);
		btnSua.addActionListener(this); // ✅ THÊM
		gbc.gridy = 1;
		p.add(btnSua, gbc);

		btnXoa = createPillButton("Xóa", 140, 45);
		btnXoa.addActionListener(this); // ✅ THÊM
		gbc.gridy = 2;
		p.add(btnXoa, gbc);

		btnLamMoi = createPillButton("Làm mới", 140, 45);
		btnLamMoi.addActionListener(this); // ✅ THÊM
		gbc.gridy = 3;
		p.add(btnLamMoi, gbc);
	}

	// --- CÁC BẢNG ---
	private void taoBangDanhSach(JPanel p) {
		String[] cols = { "Mã", "Tên", "Hình thức", "Giá trị", "Bắt đầu", "Kết thúc", "Loại", "Trạng thái" };
		modelKhuyenMai = new DefaultTableModel(cols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblKhuyenMai = setupTable(modelKhuyenMai);

		// Event Click
		tblKhuyenMai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				doToForm(tblKhuyenMai.getSelectedRow());
			}
		});

		p.add(new JScrollPane(tblKhuyenMai), BorderLayout.CENTER);
	}

	private void taoBangSanPhamApDung(JPanel p) {
		// Toolbar thêm sản phẩm
		JPanel pnTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnTool.setBackground(Color.WHITE);

		btnChonSP = createPillButton("Chọn SP", 120, 35);
		btnChonSP.addActionListener(this); // ✅ THÊM

		btnXoaSP = createPillButton("Xóa SP", 120, 35);
		btnXoaSP.addActionListener(this); // ✅ THÊM

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

		// 3. XÓA
		else if (o.equals(btnXoa)) {
			xuLyXoa();
		}

		// 4. LÀM MỚI
		else if (o.equals(btnLamMoi)) {
			lamMoiForm();
		}

		// 5. CHỌN SẢN PHẨM
		else if (o.equals(btnChonSP)) {
			xuLyThemSanPhamApDung();
		}

		// 6. XÓA SP
		else if (o.equals(btnXoaSP)) {
			xuLyXoaSanPhamApDung();
		}
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

	private void xuLyXoa() {
		int row = tblKhuyenMai.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khuyến mãi để xóa!");
			return;
		}

		String maKM = (String) tblKhuyenMai.getValueAt(row, 0);
		int confirm = JOptionPane.showConfirmDialog(this,
				"Xóa khuyến mãi " + maKM + "?\nCác sản phẩm áp dụng sẽ bị xóa theo.", "Xác nhận",
				JOptionPane.YES_NO_OPTION);
		if (confirm != JOptionPane.YES_OPTION)
			return;

		// Xóa chi tiết SP trước
		ctkmDAO.xoaTatCaSanPhamCuaKM(maKM);
		// Sau đó xóa KM
		if (kmDAO.xoaKhuyenMai(maKM)) {
			JOptionPane.showMessageDialog(this, "Đã xóa khuyến mãi!");
			loadDataKhuyenMai();
			lamMoiForm();
		} else {
			JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

		for (KhuyenMai km : dsKhuyenMai) {
			modelKhuyenMai.addRow(new Object[] { km.getMaKM(), km.getTenKM(),
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

		String maKM = (String) tblKhuyenMai.getValueAt(row, 0);
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
		txtTenKM.setText(km.getTenKM());
		txtNgayBD.setText(dfDate.format(km.getNgayBatDau()));
		txtNgayKT.setText(dfDate.format(km.getNgayKetThuc()));
		txtGiaTri.setText(removeGrouping(dfNumber.format(km.getGiaTri())));
		txtDieuKien.setText(removeGrouping(dfNumber.format(km.getDieuKienApDungHoaDon())));
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

		for (KhuyenMai km : dsKhuyenMai) {
			if (kw.isEmpty() || km.getMaKM().toLowerCase().contains(kw) || km.getTenKM().toLowerCase().contains(kw)) {
				modelKhuyenMai.addRow(new Object[] { km.getMaKM(), km.getTenKM(),
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
		String strBD = txtNgayBD.getText().trim();
		String strKT = txtNgayKT.getText().trim();

		if (strBD.isEmpty()) {
			showErrorAndFocus(txtNgayBD, "Ngày bắt đầu không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
			return null;
		}
		if (strKT.isEmpty()) {
			showErrorAndFocus(txtNgayKT, "Ngày kết thúc không được bỏ trống!", JOptionPane.WARNING_MESSAGE);
			return null;
		}

		LocalDate ngayBD, ngayKT;
		try {
			ngayBD = LocalDate.parse(strBD, dfDate);
		} catch (Exception e) {
			showErrorAndFocus(txtNgayBD, "Ngày bắt đầu không đúng định dạng dd/MM/yyyy!", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		try {
			ngayKT = LocalDate.parse(strKT, dfDate);
		} catch (Exception e) {
			showErrorAndFocus(txtNgayKT, "Ngày kết thúc không đúng định dạng dd/MM/yyyy!", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		if (ngayKT.isBefore(ngayBD)) {
			showErrorAndFocus(txtNgayKT, "Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		if (isThemMoi && ngayBD.isBefore(LocalDate.now())) {
			showErrorAndFocus(txtNgayBD, "Ngày bắt đầu phải từ hôm nay trở đi!", JOptionPane.WARNING_MESSAGE);
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

		if (kmHoaDon && hinhThuc == HinhThucKM.TANG_THEM) {
			showErrorAndFocus((JTextField) cboHinhThuc.getEditor().getEditorComponent(),
					"Khuyến mãi hóa đơn không thể có hình thức 'TẶNG THÊM'!", JOptionPane.WARNING_MESSAGE);
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

		txtTenKM.setText("");
		txtNgayBD.setText("");
		txtNgayKT.setText("");
		txtGiaTri.setText("");
		txtDieuKien.setText("");
		txtSoLuong.setText("");
		cboLoaiKM.setSelectedIndex(0);
		cboHinhThuc.setSelectedIndex(0);
		cboTrangThai.setSelectedIndex(0);
		tblKhuyenMai.clearSelection();
		modelSanPhamApDung.setRowCount(0);
		txtTimKiem.requestFocus();
	}

	// ---------- HELPER ----------

	private KhuyenMai getKhuyenMaiDangChon() {
		int row = tblKhuyenMai.getSelectedRow();
		if (row < 0 || row >= dsKhuyenMai.size())
			return null;
		// Tìm theo mã trong dsKhuyenMai (do bảng có thể đang filter)
		String maKM = (String) tblKhuyenMai.getValueAt(row, 0);
		for (KhuyenMai km : dsKhuyenMai) {
			if (km.getMaKM().equals(maKM))
				return km;
		}
		return null;
	}

	private void chonDongTheoMa(String maKM) {
		for (int i = 0; i < modelKhuyenMai.getRowCount(); i++) {
			if (maKM.equals(modelKhuyenMai.getValueAt(i, 0))) {
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
		LocalDate today = LocalDate.now();
		if (km.isDangHoatDong())
			return "Đang hoạt động";
		if (today.isAfter(km.getNgayKetThuc()))
			return "Hết hạn";
		return "Tạm ngưng";
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

	private PillButton createPillButton(String text, int w, int h) {
		PillButton btn = new PillButton(text);
		btn.setFont(FONT_BOLD);
		btn.setPreferredSize(new Dimension(w, h));
		btn.addActionListener(this);
		return btn;
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
