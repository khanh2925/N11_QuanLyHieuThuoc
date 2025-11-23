/**
 * @author Thanh Kha
 * @version 1.1
 * @since Oct 27, 2025
 *
 */

package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.toedter.calendar.JDateChooser;

import connectDB.connectDB;
import customcomponent.*;
import dao.ChiTietPhieuHuy_DAO;
import dao.PhieuHuy_DAO;
import entity.ChiTietPhieuHuy;
import entity.PhieuHuy;

public class QL_HuyHang_GUI extends JPanel implements ActionListener, MouseListener, DocumentListener {

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
	private JCheckBox chckbxDaDuyet;
	private JCheckBox chckbxChoDuyet;
	private TableRowSorter<DefaultTableModel> sorter;
	private JPanel pnLoc, pnBtnCTPH;
	private JSplitPane pnCenter;

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,###đ");

	// Utils
	private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 22);
	private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
	private final Color COLOR_PRIMARY = new Color(33, 150, 243);

	public QL_HuyHang_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {

		// kết nối database
		try {
			connectDB.getInstance().connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// tao các dao
		ph_dao = new PhieuHuy_DAO();
		ctph_dao = new ChiTietPhieuHuy_DAO();

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		TaoHeader();
		initTable();// tạo bảng và load dữ liệu từ database lên bảng
		TaoPanelCenter();

		// Chỉ cho phép chọn 1 trong 2
		ActionListener filterTrangThaiListener = e -> {
			if (e.getSource() == chckbxDaDuyet && chckbxDaDuyet.isSelected()) {
				chckbxChoDuyet.setSelected(false);
			} else if (e.getSource() == chckbxChoDuyet && chckbxChoDuyet.isSelected()) {
				chckbxDaDuyet.setSelected(false);
			}
			refreshFilters();
		};

		chckbxDaDuyet.addActionListener(filterTrangThaiListener);
		chckbxChoDuyet.addActionListener(filterTrangThaiListener);
		btnHuyHang.addActionListener(this);
		btnTuChoi.addActionListener(this);
		btnXuatFile.addActionListener(this);
		tblCTPH.addMouseListener(this);
		tblPH.addMouseListener(this);
		txtSearch.getDocument().addDocumentListener(this);

	}

	private void TaoHeader() {
		pnHeader = new JPanel();
		pnHeader.setLayout(new BoxLayout(pnHeader, BoxLayout.X_AXIS));
		pnHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // padding 2 bên
		pnHeader.setBackground(Color.WHITE);
		add(pnHeader, BorderLayout.NORTH);

		// ====== Ô tìm kiếm ======
		txtSearch = new JTextField();
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		txtSearch.setPreferredSize(new Dimension(350, 40));
		txtSearch.setMaximumSize(new Dimension(350, 50));
		txtSearch.setBorder(new RoundedBorder(20));
		txtSearch.setBackground(Color.WHITE);
		PlaceholderSupport.addPlaceholder(txtSearch, "Tìm theo mã phiếu/ tên");

		// tạo panel lọc
		TaoPanelLoc();

		// ====== Các nút ======
		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 20));

		// ====== Thêm vào header theo thứ tự ======
		pnHeader.add(txtSearch);
		pnHeader.add(Box.createRigidArea(new Dimension(15, 0)));
		pnHeader.add(pnLoc);
		pnHeader.add(Box.createRigidArea(new Dimension(15, 0)));
		pnHeader.add(btnXuatFile);

		// co giãn khi resize cửa sổ
		pnHeader.add(Box.createHorizontalGlue());
	}

	private void TaoPanelLoc() {
		// ====== Panel lọc trạng thái ======
		pnLoc = new JPanel();
		pnLoc.setLayout(new BoxLayout(pnLoc, BoxLayout.X_AXIS));
		pnLoc.setBorder(new RoundedBorder(20));
		pnLoc.setBackground(new Color(240, 255, 255));
		// tăng chiều cao để không bị cắt "Chờ duyệt"
		pnLoc.setPreferredSize(new Dimension(250, 70));
		pnLoc.setMaximumSize(new Dimension(250, 70));
		pnLoc.setMinimumSize(new Dimension(250, 70));
		pnLoc.setAlignmentY(Component.CENTER_ALIGNMENT);

		// --- Label bên trái ---
		JLabel lblTrangThai = new JLabel("Trạng thái:");
		lblTrangThai.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblTrangThai.setAlignmentY(Component.TOP_ALIGNMENT);
		lblTrangThai.setBorder(BorderFactory.createEmptyBorder(4, 10, 0, 5));

		// --- Panel chứa 2 checkbox (dọc) ---
		JPanel pnCheckBox = new JPanel();
		pnCheckBox.setLayout(new BoxLayout(pnCheckBox, BoxLayout.Y_AXIS));
		pnCheckBox.setBackground(new Color(240, 255, 255));
		pnCheckBox.setAlignmentY(Component.TOP_ALIGNMENT);
		pnCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		chckbxDaDuyet = new JCheckBox("Đã duyệt");
		chckbxDaDuyet.setFont(new Font("Tahoma", Font.BOLD, 14));
		chckbxDaDuyet.setBackground(new Color(240, 255, 255));

		chckbxChoDuyet = new JCheckBox("Chờ duyệt");
		chckbxChoDuyet.setFont(new Font("Tahoma", Font.BOLD, 14));
		chckbxChoDuyet.setBackground(new Color(240, 255, 255));

		// Thêm khoảng cách dọc nhỏ giữa hai checkbox
		pnCheckBox.add(chckbxDaDuyet);
		pnCheckBox.add(Box.createVerticalStrut(4));
		pnCheckBox.add(chckbxChoDuyet);

		// --- Thêm vào panel lọc chính ---
		pnLoc.add(lblTrangThai);
		pnLoc.add(Box.createHorizontalStrut(6));
		pnLoc.add(pnCheckBox);
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
		// ===== RIGHT =====
		pnCTPH = new JPanel(new BorderLayout());
		pnCTPH.setPreferredSize(new Dimension(600, 1080));
		pnCTPH.setLayout(new BoxLayout(pnCTPH, BoxLayout.Y_AXIS));

		pnBtnCTPH = new JPanel();
		pnBtnCTPH.setLayout(new BoxLayout(pnBtnCTPH, BoxLayout.X_AXIS));
		pnBtnCTPH.setAlignmentX(Component.LEFT_ALIGNMENT);
		// thêm bảng CTPH
		pnCTPH.add(scrCTPH);
		pnCTPH.add(pnBtnCTPH);

		btnHuyHang = new PillButton("Hủy hàng");
		btnHuyHang.setFont(new Font("Segoe UI", Font.BOLD, 20));
		btnTuChoi = new PillButton("Từ chối");
		btnTuChoi.setFont(new Font("Segoe UI", Font.BOLD, 20));

		pnBtnCTPH.add(btnHuyHang);
		pnBtnCTPH.add(Box.createRigidArea(new Dimension(10, 0)));
		pnBtnCTPH.add(btnTuChoi);
	}

	private void refreshFilters() {
		if (sorter == null)
			return;

		List<RowFilter<Object, Object>> filters = new ArrayList<>();

		// --- Lọc theo text: cột 0 (Mã PH) và 2 (Nhân viên)
		String text = txtSearch.getText().trim();
		if (!text.isEmpty() && !txtSearch.getForeground().equals(Color.GRAY)) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 0, 2));
		}

		// --- Lọc theo trạng thái: cột 4 (chỉ 1 trong 2)
		if (chckbxDaDuyet.isSelected()) {
			filters.add(RowFilter.regexFilter("(?i)Đã duyệt", 4));
		} else if (chckbxChoDuyet.isSelected()) {
			filters.add(RowFilter.regexFilter("(?i)Chờ duyệt", 4));
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
		String[] phieuHuyCols = { "Mã PH", "Ngày lập phiếu", "Nhân viên", "Tổng tiền", "Trạng thái" };
		modelPH = new DefaultTableModel(phieuHuyCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblPH = setupTable(modelPH);
		scrPH = new JScrollPane(tblPH);
		TitledBorder tbPH = BorderFactory.createTitledBorder("Danh sách phiếu hủy");
		tbPH.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
		scrPH.setBorder(tbPH);
		loadDataTablePH();

		// Bảng chi tiết phiếu huỷ
		String[] cTPhieuCols = { "Mã lô", "Tên SP", "SL huỷ", "Lý do", "Trạng thái" };

		modelCTPH = new DefaultTableModel(cTPhieuCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPH = setupTable(modelCTPH);
		scrCTPH = new JScrollPane(tblCTPH);
		TitledBorder tbCTPH = BorderFactory.createTitledBorder("Danh sách chi tiết phiếu hủy");
		tbCTPH.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
		scrCTPH.setBorder(tbCTPH);

		// ===== Format chung (giữ nguyên style cũ của bạn) =====
		formatTable(tblPH);
		formatTable(tblCTPH);

		// ===================================================================
		// ---- 1) Trạng thái bảng PHIẾU HỦY: Đã duyệt = xanh, Chờ duyệt = đỏ ----
		tblPH.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);

				String text = value == null ? "" : value.toString().trim();

				if (text.equalsIgnoreCase("Đã duyệt")) {
					lbl.setForeground(new Color(0, 128, 0)); // Xanh lá
				} else if (text.equalsIgnoreCase("Chờ duyệt")) {
					lbl.setForeground(Color.RED);
				} else {
					lbl.setForeground(Color.BLACK);
				}

				// Không đụng tới background để vẫn giữ màu chọn dòng
				return lbl;
			}
		});

		// ---- 2) Trạng thái bảng CHI TIẾT: Đã hủy hàng = xanh, Đã từ chối hủy = đỏ
		// ----
		tblCTPH.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);

				String text = value == null ? "" : value.toString().trim();

				if (text.equalsIgnoreCase("Đã hủy hàng")) {
					lbl.setForeground(new Color(0, 128, 0)); // Xanh lá
				} else if (text.equalsIgnoreCase("Đã từ chối hủy")) {
					lbl.setForeground(Color.RED);
				} else { // Chờ duyệt, hoặc trạng thái khác
					lbl.setForeground(Color.BLACK);
				}

				return lbl;
			}
		});

		// bắt sự kiện chọn dòng để tự nạp chi tiết
		tblPH.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblPH.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting())
				loadTableCTPH();
		});

		// sắp xếp tăng giảm tự động khi click vào header
		sorter = new TableRowSorter<>(modelPH);
		tblPH.setRowSorter(sorter);
	}

	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		table.setRowHeight(35);
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setSelectionForeground(Color.BLACK);
		table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 22));
		table.getTableHeader().setBackground(COLOR_PRIMARY);
		table.getTableHeader().setForeground(Color.WHITE);
		return table;
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 22));
		table.getTableHeader().setBorder(null);

		table.setRowHeight(28);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 22));
		table.setSelectionBackground(new Color(180, 205, 230));
		table.setShowGrid(false);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);

		TableColumnModel m = table.getColumnModel();
		for (int i = 0; i < m.getColumnCount(); i++) {
			String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();

			if (col.contains("mã"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else if (col.contains("số lượng") || col.contains("sl"))
				m.getColumn(i).setCellRenderer(rightRenderer);
			else if (col.contains("giá") || col.contains("tiền"))
				m.getColumn(i).setCellRenderer(rightRenderer);
			else if (col.contains("ngày") || col.contains("hạn"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else
				m.getColumn(i).setCellRenderer(leftRenderer);
		}

		table.getTableHeader().setReorderingAllowed(false);
	}

	// đưa dữ liệu Phiếu Hủy lên bảng
	private void loadDataTablePH() {
		dsPhieuHuy = new ArrayList<PhieuHuy>();
		modelPH.setRowCount(0);

		try {
			dsPhieuHuy = ph_dao.layTatCaPhieuHuy();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (PhieuHuy ph : dsPhieuHuy) {
			modelPH.addRow(new Object[] { ph.getMaPhieuHuy(), ph.getNgayLapPhieu(), ph.getNhanVien().getTenNhanVien(),
					df.format(ph.getTongTien()), ph.getTrangThaiText() });
		}

	}

	// đưa dữ liệu CTPH lên bảng
	private void loadTableCTPH() {
		int selectRow = tblPH.getSelectedRow();

		if (selectRow == -1) {
			return;
		}

		String maPH = modelPH.getValueAt(selectRow, 0).toString();

		dsCTPhieuHuy = new ArrayList<ChiTietPhieuHuy>();
		modelCTPH.setRowCount(0);

		try {
			dsCTPhieuHuy = ph_dao.layChiTietTheoMaPhieu(maPH);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (ChiTietPhieuHuy ctph : dsCTPhieuHuy) {
			modelCTPH.addRow(
					new Object[] { ctph.getLoSanPham().getMaLo(), ctph.getLoSanPham().getSanPham().getTenSanPham(),
							ctph.getSoLuongHuy(), ctph.getLyDoChiTiet(), ctph.getTrangThaiText() });
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

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
		Object src = e.getSource();

		if (src == btnHuyHang) {
			HuyHang();
			return;
		}
		if (src == btnTuChoi) {
			TuChoiHuy();
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
		String trangThai = modelCTPH.getValueAt(selectRowCT, 4).toString();
		if (trangThai.trim().equals("Đã từ chối")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu hủy này đã ở trạng thái từ chối hủy");
			return;
		}

		// đã hủy hàng thì không được cập nhật trạng thái
		if (trangThai.trim().equals("Đã hủy hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu hủy này đã hủy hàng, không được cập nhật trạng thái");
			return;
		}
		String maPH = modelPH.getValueAt(selectRowPH, 0).toString();
		String maLo = modelCTPH.getValueAt(selectRowCT, 0).toString();

		if (ctph_dao.capNhatTrangThaiChiTiet(maPH, maLo, 3)) {
			modelCTPH.setValueAt("Đã từ chối hủy", selectRowCT, 4);
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
		String trangThai = modelCTPH.getValueAt(selectRowCT, 4).toString();

		if (trangThai.trim().equals("Đã hủy hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu hủy đã ở trạng thái đã hủy!!");
			return;
		}
		String maPH = modelPH.getValueAt(selectRowPH, 0).toString();
		String maLo = modelCTPH.getValueAt(selectRowCT, 0).toString();

		if (ctph_dao.capNhatTrangThaiChiTiet(maPH, maLo, 2)) {
			modelCTPH.setValueAt("Đã hủy hàng", selectRowCT, 4);
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

		modelPH.setValueAt("Đã duyệt", rowModel, 4);

	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		refreshFilters();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		refreshFilters();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		refreshFilters();
	}

}
