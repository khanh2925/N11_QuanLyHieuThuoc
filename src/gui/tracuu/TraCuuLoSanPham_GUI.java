/**

 */
package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.InputMap;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

import component.border.RoundedBorder;
import component.button.PillButton;
import component.input.PlaceholderSupport;
import dao.LoSanPham_DAO;
import dao.SanPham_DAO;
import entity.LoSanPham;
import entity.SanPham;

@SuppressWarnings("serial")
public class TraCuuLoSanPham_GUI extends JPanel implements ActionListener, MouseListener, DocumentListener {

	private JPanel pnHeader;
	private JPanel pnCenter;
	private JTable tblLo;
	private DefaultTableModel modelLo;
	private JTable tblSanPham;
	private DefaultTableModel modelSanPham;

	// Header components (UI only)
	private JTextField txtTimKiem;
	private JCheckBox chkHetHan;
	private JComboBox<String> cbTonKho;

	private final LoSanPham_DAO loDao = new LoSanPham_DAO();
	private final SanPham_DAO spDao = new SanPham_DAO();

	private List<LoSanPham> allLo = new ArrayList<>();
	private List<LoSanPham> loHetHan = new ArrayList<>();
	private List<LoSanPham> dsDangHienThi = new ArrayList<>();
	private TableRowSorter<DefaultTableModel> sorterLo;

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DecimalFormat df = new DecimalFormat("#,###đ");

	private PillButton btnTim, btnLamMoi;

	public TraCuuLoSanPham_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
		setupKeyboardShortcuts();
		addEvents();
		initData();
	}

	// ==============================================================================
	// KHỞI TẠO LAYOUT
	// ==============================================================================
	private void initialize() {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		taoPhanHeader();
		add(pnHeader, BorderLayout.NORTH);

		taoPhanCenter();
		add(pnCenter, BorderLayout.CENTER);
	}

	// ==============================================================================
	// HEADER
	// ==============================================================================
	private void taoPhanHeader() {
		pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(1073, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));

		// 1) Ô tìm kiếm
		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã lô, mã SP (F1 / Ctrl+F)");
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtTimKiem.setBounds(25, 17, 480, 60);
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setBackground(Color.WHITE);
		txtTimKiem.setToolTipText("<html><b>Phím tắt:</b> F1 hoặc Ctrl+F<br>Nhấn Enter để tìm kiếm</html>");
		pnHeader.add(txtTimKiem);

		// 2) Bộ lọc HSD
		JLabel lblHSD = new JLabel("HSD:");
		lblHSD.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblHSD.setBounds(542, 28, 60, 35);
		pnHeader.add(lblHSD);

		chkHetHan = new JCheckBox("Hết hạn");
		chkHetHan.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		chkHetHan.setBackground(new Color(0xE3F2F5));
		chkHetHan.setBounds(608, 30, 100, 30);

		pnHeader.add(chkHetHan);

		// 3) Tồn kho
		JLabel lblTon = new JLabel("Tồn:");
		lblTon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblTon.setBounds(728, 28, 55, 35);
		pnHeader.add(lblTon);

		cbTonKho = new JComboBox<>(new String[] { "Tất cả", "Còn tồn", "Hết hàng" });
		cbTonKho.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		cbTonKho.setBounds(793, 28, 170, 38);
		pnHeader.add(cbTonKho);

		// 4) button
		btnTim = new PillButton(
				"<html>" +
					"<center>" +
						"TÌM KIẾM<br>" +
						"<span style='font-size:10px; color:#888888;'>(Enter)</span>" +
					"</center>" +
				"</html>"
			);
		btnTim.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnTim.setBounds(1009, 22, 130, 50);
		btnTim.setToolTipText("<html><b>Phím tắt:</b> Enter (khi ở ô tìm kiếm)<br>Tìm kiếm theo mã lô, mã sản phẩm và bộ lọc</html>");
		pnHeader.add(btnTim);

		btnLamMoi = new PillButton(
				"<html>" +
					"<center>" +
						"LÀM MỚI<br>" +
						"<span style='font-size:10px; color:#888888;'>(F5)</span>" +
					"</center>" +
				"</html>"
			);
		btnLamMoi.setBounds(1164, 22, 130, 50);
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnLamMoi.setToolTipText("<html><b>Phím tắt:</b> F5<br>Làm mới toàn bộ dữ liệu và xóa bộ lọc</html>");
		pnHeader.add(btnLamMoi);
	}

	// ==============================================================================
	// CENTER
	// ==============================================================================
	private void taoPhanCenter() {
		pnCenter = new JPanel(new BorderLayout());
		pnCenter.setBackground(Color.WHITE);
		pnCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
		createTable();
	}

	// ==============================================================================
	// TẠO 2 BẢNG
	// ==============================================================================
	private void createTable() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(400);
		splitPane.setResizeWeight(0.5);
		pnCenter.add(splitPane, BorderLayout.CENTER);

		// --- BẢNG 1 (TOP): DANH SÁCH LÔ ---
		String[] colLo = { "STT", "Mã lô", "Mã SP", "Hạn sử dụng", "Số lượng tồn" };
		modelLo = new DefaultTableModel(colLo, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblLo = setupTable(modelLo);
		sorterLo = new TableRowSorter<>(modelLo);
		tblLo.setRowSorter(sorterLo);

		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		tblLo.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblLo.getColumnModel().getColumn(1).setCellRenderer(center); // Mã lô
		tblLo.getColumnModel().getColumn(2).setCellRenderer(center); // Mã SP
		tblLo.getColumnModel().getColumn(3).setCellRenderer(center); // HSD

		// Render tình trạng màu sắc
		tblLo.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
				String status = value == null ? "" : value.toString();

				if ("0".equalsIgnoreCase(status)) {
					lbl.setForeground(Color.RED);
				} else {
					lbl.setForeground(new Color(0x2E7D32));
				}
				return lbl;
			}
		});

		tblLo.getColumnModel().getColumn(1).setPreferredWidth(160);
		tblLo.getColumnModel().getColumn(3).setPreferredWidth(250);

		JScrollPane scrollLo = new JScrollPane(tblLo);
		scrollLo.setBorder(createTitledBorder("Danh sách lô sản phẩm"));
		splitPane.setTopComponent(scrollLo);

		// --- BẢNG 2 (BOTTOM): THÔNG TIN SẢN PHẨM ---
		String[] colSP = { "Mã SP", "Tên sản phẩm", "Loại", "Số ĐK", "Đường dùng", "Giá bán", "Kệ bán", "Trạng thái" };
		modelSanPham = new DefaultTableModel(colSP, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblSanPham = setupTable(modelSanPham);

		tblSanPham.getColumnModel().getColumn(0).setCellRenderer(center);
		tblSanPham.getColumnModel().getColumn(2).setCellRenderer(center);
		tblSanPham.getColumnModel().getColumn(3).setCellRenderer(center);
		tblSanPham.getColumnModel().getColumn(4).setCellRenderer(center);
		tblSanPham.getColumnModel().getColumn(5).setCellRenderer(right);
		tblSanPham.getColumnModel().getColumn(6).setCellRenderer(center);

		tblSanPham.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
				String status = value == null ? "" : value.toString();
				if ("Đang bán".equalsIgnoreCase(status)) {
					lbl.setForeground(new Color(0x2E7D32));
				} else {
					lbl.setForeground(Color.RED);
				}
				return lbl;
			}
		});

		JScrollPane scrollSP = new JScrollPane(tblSanPham);
		scrollSP.setBorder(createTitledBorder("Thông tin sản phẩm"));
		splitPane.setBottomComponent(scrollSP);
	}

	// ==============================================================================
	// STYLE TABLE
	// ==============================================================================
	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setGridColor(new Color(230, 230, 230));

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 16));
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(100, 40));
		return table;
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
				TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
	}

	// ==============================================================================
	// INIT DATA
	// ==============================================================================
	private void initData() {
	    txtTimKiem.setText("");
	    cbTonKho.setSelectedIndex(0);
	    chkHetHan.setSelected(false);
	    modelSanPham.setRowCount(0);

	    allLo = loDao.layTatCaLoSanPham();
	    if (allLo == null) allLo = new ArrayList<>();

	    dsDangHienThi = new ArrayList<>(allLo);

	    hienThiDanhSachLo(dsDangHienThi);
	}


	private void loadDuLieuLo() {
	    allLo = loDao.layTatCaLoSanPham();
	    if (allLo == null) allLo = new ArrayList<>();

	    dsDangHienThi = new ArrayList<>(allLo);
	    apDungTimKiemTuDong();
	}

	private void loadSanPhamCuaLoDangChon(String maSP) {
		modelSanPham.setRowCount(0);

		SanPham sp = spDao.laySanPhamTheoMa(maSP);
		if (sp == null)
			return;

		String loai = (sp.getLoaiSanPham() != null) ? sp.getLoaiSanPham().getTenLoai() : "";

		modelSanPham.addRow(new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), loai, sp.getSoDangKy(),
				sp.getDuongDung(), df.format(sp.getGiaBan()), sp.getKeBanSanPham(),
				sp.isHoatDong() ? "Đang bán" : "Ngừng kinh doanh" });
	}

	// sk
	private void addEvents() {
		tblLo.addMouseListener(this);
		btnLamMoi.addActionListener(this);
		btnTim.addActionListener(this);
		chkHetHan.addActionListener(this);
		cbTonKho.addActionListener(this);
		txtTimKiem.getDocument().addDocumentListener(this);
	}

	/**
	 * Thiết lập phím tắt cho màn hình Tra cứu Lô sản phẩm
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
				lamMoi();
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

		// Enter: Tìm kiếm (từ bất kỳ đâu trong panel)
		inputMap.put(KeyStroke.getKeyStroke("ENTER"), "timKiemEnter");
		actionMap.put("timKiemEnter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				locTheoTonKhoKhiNhanTim();
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() != tblLo)
			return;

		int row = tblLo.getSelectedRow();
		if (row == -1)
			return;

		String maSP = tblLo.getValueAt(row, 2).toString();
		loadSanPhamCuaLoDangChon(maSP);
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
	    Object o = e.getSource();
	    if (o == chkHetHan) {
	        if (chkHetHan.isSelected()) {
	            loadDuLieuLoHetHan();
	        } else {
	            loadDuLieuLo();
	        }
	        modelSanPham.setRowCount(0);
	        return;
	    }

	    if (o == btnTim) {
	        locTheoTonKhoKhiNhanTim();
	        return;
	    }

	    if (o == btnLamMoi) {
	        lamMoi();
	        return;
	    }
	}

	private void lamMoi() {
	    txtTimKiem.setText("");
	    cbTonKho.setSelectedIndex(0);
	    chkHetHan.setSelected(false);

	    loadDuLieuLo();
	    modelSanPham.setRowCount(0);
	}


	private void loadDuLieuLoHetHan() {
	    loHetHan = loDao.layDanhSachLoSPToiHanSuDung();
	    if (loHetHan == null) loHetHan = new ArrayList<>();

	    dsDangHienThi = new ArrayList<>(loHetHan);

	    apDungTimKiemTuDong();
	}
	
	private void locTheoTonKhoKhiNhanTim() {
	    String option = cbTonKho.getSelectedItem() == null ? "Tất cả" : cbTonKho.getSelectedItem().toString();

	    String key = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim().toLowerCase();

	    List<LoSanPham> base = new ArrayList<>();
	    for (LoSanPham lo : dsDangHienThi) {
	        String maLo = (lo.getMaLo() == null) ? "" : lo.getMaLo().toLowerCase();
	        String maSP = (lo.getSanPham() == null || lo.getSanPham().getMaSanPham() == null)
	                ? "" : lo.getSanPham().getMaSanPham().toLowerCase();

	        if (key.isEmpty() || maLo.contains(key) || maSP.contains(key)) {
	            base.add(lo);
	        }
	    }

	    if ("Tất cả".equalsIgnoreCase(option)) {
	        hienThiDanhSachLo(base);
	        return;
	    }

	    List<LoSanPham> kq = new ArrayList<>();
	    for (LoSanPham lo : base) {
	        int ton = lo.getSoLuongTon();

	        if ("Còn tồn".equalsIgnoreCase(option)) {
	            // giữ row có tồn > 0
	            if (ton > 0) kq.add(lo);
	        } else if ("Hết hàng".equalsIgnoreCase(option)) {
	            // giữ row có tồn == 0
	            if (ton == 0) kq.add(lo);
	        }
	    }

	    hienThiDanhSachLo(kq);
	}

	private void hienThiDanhSachLo(List<LoSanPham> ds) {
	    modelLo.setRowCount(0);
	    if (ds == null) ds = new ArrayList<>();

	    int stt = 1;
	    for (LoSanPham lo : ds) {
	        modelLo.addRow(new Object[]{
	            stt++,
	            lo.getMaLo(),
	            lo.getSanPham().getMaSanPham(),
	            lo.getHanSuDung().format(dtf),
	            lo.getSoLuongTon()
	        });
	    }
	}

	// ✅ Auto lọc theo txtTimKiem (mã lô / mã sp) dựa trên dsDangHienThi (list trung gian)
	private void apDungTimKiemTuDong() {

	    String key = txtTimKiem.getText() == null ? "" : txtTimKiem.getText().trim();


	 if (key.equalsIgnoreCase("Tìm theo mã lô, mã SP")) {
	     key = "";
	 }

	 key = key.toLowerCase();

	    List<LoSanPham> ketQua = new ArrayList<>();
	    for (LoSanPham lo : dsDangHienThi) {
	        String maLo = (lo.getMaLo() == null) ? "" : lo.getMaLo().toLowerCase();
	        String maSP = (lo.getSanPham() == null || lo.getSanPham().getMaSanPham() == null)
	                ? "" : lo.getSanPham().getMaSanPham().toLowerCase();

	        if (maLo.contains(key) || maSP.contains(key)) {
	            ketQua.add(lo);
	        }
	    }

	    hienThiDanhSachLo(ketQua);
	}
	

	@Override
	public void insertUpdate(DocumentEvent e) {
	    apDungTimKiemTuDong();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	    apDungTimKiemTuDong();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	    apDungTimKiemTuDong();
	}


}
