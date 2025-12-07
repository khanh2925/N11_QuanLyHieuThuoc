package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import component.input.PlaceholderSupport;
import dao.SanPham_DAO;
import entity.SanPham;
import enums.LoaiSanPham;

public class DialogChonSanPhamApDung extends JDialog {

	private static final String PLACEHOLDER_TIM_KIEM = "Tìm kiếm theo mã sản phẩm hoặc tên sản phẩm...";
	private JTextField txtNhapMa, txtTimKiem;
	private JTable tblSanPham, tblDaChon;
	private DefaultTableModel modelSanPham, modelDaChon;
	private SanPham_DAO spDAO = new SanPham_DAO();
	private JComboBox<String> cboLoai;
	private JButton btnThem;
	private JButton btnOK;
	private JButton btnHuy;

	public DialogChonSanPhamApDung(JFrame owner) {
		super(owner, "Chọn sản phẩm", true);
		setSize(900, 800);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout());

		initUI();
	}

	private void initUI() {

		JPanel pnTop = new JPanel(new GridLayout(3, 1, 5, 5));

		// ====================== HÀNG 1: nhập mã nhanh ======================
		JPanel pnNhapMa = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnNhapMa.add(new JLabel("Nhập mã SP:"));

		txtNhapMa = new JTextField(20);
//		PlaceholderSupport.addPlaceholder(txtNhapMa, "Nhập mã sản phẩm...");
		pnNhapMa.add(txtNhapMa);

		txtNhapMa.addActionListener(e -> xuLyNhapMa());
		pnTop.add(pnNhapMa);

		// ====================== HÀNG 2: thanh lọc + nút thêm ======================
		JPanel pnFilter = new JPanel(new FlowLayout(FlowLayout.LEFT));

		pnFilter.add(new JLabel("Loại SP:"));

		cboLoai = new JComboBox<>();
		cboLoai.addItem("Tất cả");

		for (LoaiSanPham loai : LoaiSanPham.values()) {
			cboLoai.addItem(loai.getTenLoai());
		}

		cboLoai.addActionListener(e -> loadSanPham());
		pnFilter.add(cboLoai);

		pnTop.add(pnFilter);

		// ====================== HÀNG 3: tìm kiếm ======================
		JPanel pnTim = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnTim.add(new JLabel("Tìm kiếm:"));
		txtTimKiem = new JTextField(25);
//		PlaceholderSupport.addPlaceholder(txtTimKiem, PLACEHOLDER_TIM_KIEM);
		pnTim.add(txtTimKiem);
		txtTimKiem.addActionListener(e -> loadSanPham());

		pnTop.add(pnTim);

		// GẮN TOP LÊN NORTH
		add(pnTop, BorderLayout.NORTH);

		// ====================== BẢNG SẢN PHẨM ======================
		String[] col1 = { "Mã", "Tên", "Đơn vị", "Giá" };
		modelSanPham = new DefaultTableModel(col1, 0);
		tblSanPham = new JTable(modelSanPham);

		// ====================== BẢNG ĐÃ CHỌN ======================
		String[] col2 = { "Mã", "Tên", "Đơn vị", "Giá" };
		modelDaChon = new DefaultTableModel(col2, 0);
		tblDaChon = new JTable(modelDaChon);

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tblSanPham),
				new JScrollPane(tblDaChon));
		sp.setDividerLocation(300);

		add(sp, BorderLayout.CENTER);

		// ====================== NÚT OK ======================
		JPanel pnAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		btnThem = new JButton("Thêm");
		btnThem.addActionListener(e -> xuLyAddTuBang());
		pnAction.add(btnThem);

		btnOK = new JButton("OK");
		btnOK.addActionListener(e -> dispose());
		pnAction.add(btnOK);

		btnHuy = new JButton("Hủy");
		btnHuy.addActionListener(e -> {
			modelDaChon.setRowCount(0); // xoá hết đã chọn (tuỳ bạn muốn)
			dispose();
		});
		pnAction.add(btnHuy);

		add(pnAction, BorderLayout.SOUTH);
		// load dữ liệu
		loadSanPham();
	}

	private void xuLyAddTuBang() {
		int[] rows = tblSanPham.getSelectedRows();
		if (rows.length == 0) {
			JOptionPane.showMessageDialog(this, "Hãy chọn ít nhất 1 sản phẩm!");
			return;
		}

		int them = 0;

		for (int r : rows) {
			String maSP = modelSanPham.getValueAt(r, 0).toString();

			// kiểm tra trùng
			boolean tonTai = false;
			for (int i = 0; i < modelDaChon.getRowCount(); i++) {
				if (modelDaChon.getValueAt(i, 0).equals(maSP)) {
					tonTai = true;
					break;
				}
			}
			if (tonTai)
				continue;

			// thêm xuống bảng đã chọn
			modelDaChon.addRow(new Object[] { modelSanPham.getValueAt(r, 0), modelSanPham.getValueAt(r, 1),
					modelSanPham.getValueAt(r, 2), modelSanPham.getValueAt(r, 3) });

			them++;
		}

		if (them == 0)
			JOptionPane.showMessageDialog(this, "Tất cả sản phẩm đã tồn tại trong danh sách!");
	}

	private void xuLyNhapMa() {
		String ma = txtNhapMa.getText().trim();
		if (ma.isEmpty())
			return;

		SanPham sp = spDAO.laySanPhamTheoMa(ma);
		if (sp == null) {
			JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!");
			txtNhapMa.selectAll();
			txtNhapMa.requestFocus();
			return;
		}

		// Kiểm tra trùng
		for (int i = 0; i < modelDaChon.getRowCount(); i++) {
			if (modelDaChon.getValueAt(i, 0).equals(ma)) {
				JOptionPane.showMessageDialog(this, "Sản phẩm đã được chọn!");
				txtNhapMa.setText("");
				txtNhapMa.requestFocus();
				return;
			}
		}

		// Thêm vào bảng đã chọn
		modelDaChon
				.addRow(new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), sp.getKeBanSanPham(), sp.getGiaNhap() });

		txtNhapMa.setText("");
		txtNhapMa.requestFocus();
	}

	private void loadSanPham() {
		String kw = txtTimKiem.getText().trim().toLowerCase();
		if (kw.equals(PLACEHOLDER_TIM_KIEM)) {
			kw = "";
		}

		String loaiChon = (String) cboLoai.getSelectedItem();

		modelSanPham.setRowCount(0);

		for (SanPham sp : spDAO.layTatCaSanPham()) {

			// ======= LỌC THEO LOẠI CHUẨN ENUM =======
			if (!"Tất cả".equals(loaiChon)) {
				if (!sp.getLoaiSanPham().getTenLoai().equals(loaiChon)) {
					continue;
				}
			}

			// ======= LỌC TỪ KHÓA =======
			if (kw.isEmpty() || sp.getMaSanPham().toLowerCase().contains(kw)
					|| sp.getTenSanPham().toLowerCase().contains(kw)) {

				modelSanPham.addRow(
						new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), sp.getKeBanSanPham(), sp.getGiaNhap() });
			}
		}
	}

	public DefaultTableModel getModelDaChon() {
		return modelDaChon;
	}

}