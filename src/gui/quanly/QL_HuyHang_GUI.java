/**
 * @author Anh Khoi
 * @version 1.5
 */

package gui.quanly;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.RowFilter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.toedter.calendar.JDateChooser;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

import database.connectDB;
import component.button.PillButton;
import component.input.PlaceholderSupport;
import component.border.RoundedBorder;
import dao.ChiTietPhieuHuy_DAO;
import dao.PhieuHuy_DAO;
import entity.ChiTietPhieuHuy;
import entity.PhieuHuy;

public class QL_HuyHang_GUI extends JPanel implements ActionListener, DocumentListener {

	private static final long serialVersionUID = 1L;
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
	private JComboBox<String> cbTrangThai;
	private JDateChooser dateTuNgay;
	private JDateChooser dateDenNgay;
	private PillButton btnLamMoi;
	private TableRowSorter<DefaultTableModel> sorter;
	private JPanel pnBtnCTPH;
	private JSplitPane pnCenter;

	private static final String TEN_NHA_THUOC = "NHÀ THUỐC HÒA AN"; // đổi tên theo nhà thuốc của bạn

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,###đ");

	public QL_HuyHang_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {

		try {
			connectDB.getInstance().connect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ph_dao = new PhieuHuy_DAO();
		ctph_dao = new ChiTietPhieuHuy_DAO();

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		TaoHeader();
		initTable();// tạo bảng và load dữ liệu từ database lên bảng
		TaoPanelCenter();

		// Event listeners
		cbTrangThai.addActionListener(e -> refreshFilters());
		dateTuNgay.addPropertyChangeListener("date", e -> refreshFilters());
		dateDenNgay.addPropertyChangeListener("date", e -> refreshFilters());
		btnLamMoi.addActionListener(this);
		btnHuyHang.addActionListener(this);
		btnTuChoi.addActionListener(this);
		btnXuatFile.addActionListener(this);
		txtSearch.getDocument().addDocumentListener(this);

	}

	private void TaoHeader() {
		pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(0, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));
		add(pnHeader, BorderLayout.NORTH);

		// --- Ô TÌM KIẾM (Font 20) ---
		txtSearch = new JTextField();
		PlaceholderSupport.addPlaceholder(txtSearch, "Nhập mã phiếu hủy, mã sản phẩm, tên sản phẩm...");
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 20));
		txtSearch.setBounds(25, 17, 500, 60);
		txtSearch.setBorder(new RoundedBorder(20));
		txtSearch.setBackground(Color.WHITE);
		pnHeader.add(txtSearch);

		// --- BỘ LỌC (Font 18) ---
		// 1. Trạng thái ComboBox
		addFilterLabel("Trạng thái:", 530, 28, 90, 35);
		cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Đã duyệt", "Chờ duyệt" });
		cbTrangThai.setBounds(620, 28, 150, 38);
		cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		pnHeader.add(cbTrangThai);

		// 2. Từ ngày
		addFilterLabel("Từ ngày:", 790, 28, 80, 35);
		dateTuNgay = new JDateChooser();
		dateTuNgay.setDateFormatString("dd/MM/yyyy");
		dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateTuNgay.setBounds(870, 28, 180, 38);
		pnHeader.add(dateTuNgay);

		// 3. Đến ngày
		addFilterLabel("Đến:", 1070, 28, 50, 35);
		dateDenNgay = new JDateChooser();
		dateDenNgay.setDateFormatString("dd/MM/yyyy");
		dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateDenNgay.setBounds(1120, 28, 180, 38);
		pnHeader.add(dateDenNgay);

		// --- NÚT (Font 18) ---
		btnLamMoi = new PillButton("Làm mới");
		btnLamMoi.setBounds(1320, 22, 130, 50);
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnHeader.add(btnLamMoi);

		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setBounds(1465, 22, 130, 50);
		btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
		pnHeader.add(btnXuatFile);
	}

	// Helper tạo label (Font 18)
	private void addFilterLabel(String text, int x, int y, int w, int h) {
		JLabel lbl = new JLabel(text);
		lbl.setBounds(x, y, w, h);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		pnHeader.add(lbl);
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

		pnCTPH = new JPanel(new BorderLayout());
		pnCTPH.setPreferredSize(new Dimension(600, 1080));

		TitledBorder tbCTPH = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				"Danh sách chi tiết phiếu hủy", TitledBorder.LEFT, TitledBorder.TOP,
				new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY);
		pnCTPH.setBorder(tbCTPH);

		// ==== PANEL CHỨA 2 BUTTON

		pnBtnCTPH = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

		btnHuyHang = new PillButton("Hủy hàng");
		btnHuyHang.setFont(new Font("Segoe UI", Font.BOLD, 18));

		btnTuChoi = new PillButton("Từ chối");
		btnTuChoi.setFont(new Font("Segoe UI", Font.BOLD, 18));

		pnBtnCTPH.add(btnHuyHang);
		pnBtnCTPH.add(btnTuChoi);

		// Thêm panel nút lên trên, bảng CTPH ở giữa
		pnCTPH.add(pnBtnCTPH, BorderLayout.NORTH);
		pnCTPH.add(scrCTPH, BorderLayout.CENTER);
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

		// --- Lọc theo trạng thái ComboBox: cột 4
		String trangThai = (String) cbTrangThai.getSelectedItem();
		if (trangThai != null && !trangThai.equals("Tất cả")) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(trangThai), 4));
		}

		// --- Lọc theo ngày: cột 3 (Ngày lập)
		java.util.Date tuNgay = dateTuNgay.getDate();
		java.util.Date denNgay = dateDenNgay.getDate();

		if (tuNgay != null || denNgay != null) {
			filters.add(new RowFilter<Object, Object>() {
				@Override
				public boolean include(Entry<? extends Object, ? extends Object> entry) {
					try {
						String ngayStr = entry.getStringValue(1); // Cột Ngày lập
						LocalDate ngay = LocalDate.parse(ngayStr, fmt);

						LocalDate tu = tuNgay != null
								? tuNgay.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
								: null;
						LocalDate den = denNgay != null
								? denNgay.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
								: null;

						if (tu != null && den != null) {
							return !ngay.isBefore(tu) && !ngay.isAfter(den);
						} else if (tu != null) {
							return !ngay.isBefore(tu);
						} else if (den != null) {
							return !ngay.isAfter(den);
						}
						return true;
					} catch (Exception e) {
						return true;
					}
				}
			});
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
		TitledBorder tbPH = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
				"Danh sách phiếu hủy", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18),
				Color.DARK_GRAY);
		scrPH.setBorder(tbPH);
		loadDataTablePH();

		// Bảng chi tiết phiếu huỷ
		String[] cTPhieuCols = { "Mã lô", "Tên SP", "SL huỷ", "Lý do", "Đơn vị tính", "Thành tiền", "Trạng thái" };

		modelCTPH = new DefaultTableModel(cTPhieuCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPH = setupTable(modelCTPH);
		scrCTPH = new JScrollPane(tblCTPH);

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
		// Cột 5 (không phải cột 4) vì đã thêm cột "Đơn vị tính"
		tblCTPH.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font 16
		table.setRowHeight(35); // Cao 35
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setGridColor(new Color(230, 230, 230));

		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Header Font 16 Bold
		header.setOpaque(true);
		header.setBackground(new Color(33, 150, 243));
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(100, 40)); // Header Cao 40
		header.setReorderingAllowed(false);
		return table;
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 16));
		table.getTableHeader().setBorder(null);

		table.setRowHeight(28);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
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
			modelPH.addRow(new Object[] { ph.getMaPhieuHuy(), ph.getNgayLapPhieu().format(fmt),
					ph.getNhanVien().getTenNhanVien(), df.format(ph.getTongTien()), ph.getTrangThaiText() });
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
			// ✅ Xử lý trường hợp DonViTinh = null
			String tenDonViTinh = "N/A";
			if (ctph.getDonViTinh() != null) {
				tenDonViTinh = ctph.getDonViTinh().getTenDonViTinh();
			}

			modelCTPH.addRow(
					new Object[] { ctph.getLoSanPham().getMaLo(), ctph.getLoSanPham().getSanPham().getTenSanPham(),
							ctph.getSoLuongHuy(), ctph.getLyDoChiTiet(), tenDonViTinh, ctph.getThanhTien(), // ✅ Cột 4:
																											// Đơn vị
																											// tính
							ctph.getTrangThaiText() // ✅ Cột 5: Trạng thái
					});
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == btnLamMoi) {
			txtSearch.setText("");
			cbTrangThai.setSelectedIndex(0);
			dateTuNgay.setDate(null);
			dateDenNgay.setDate(null);
			loadDataTablePH();
			modelCTPH.setRowCount(0);
			return;
		}
		if (src == btnHuyHang) {
			HuyHang();
			return;
		}
		if (src == btnTuChoi) {
			TuChoiHuy();
			return;
		}
		if (src == btnXuatFile) {
			xuatPDFPhieuHuyDangChon();
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
		// ✅ Đọc cột 6 (Trạng thái)
		String trangThai = modelCTPH.getValueAt(selectRowCT, 6).toString();
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
			// ✅ Update cột 6 (Trạng thái)
			modelCTPH.setValueAt("Đã từ chối hủy", selectRowCT, 6);
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
		// ✅ Đọc cột 5 (Trạng thái), không phải cột 4 (Đơn vị tính)
		String trangThai = modelCTPH.getValueAt(selectRowCT, 6).toString();

		if (trangThai.trim().equals("Đã hủy hàng")) {
			JOptionPane.showMessageDialog(null, "Chi tiết phiếu hủy đã ở trạng thái đã hủy!!");
			return;
		}
		String maPH = modelPH.getValueAt(selectRowPH, 0).toString();
		String maLo = modelCTPH.getValueAt(selectRowCT, 0).toString();

		if (ctph_dao.capNhatTrangThaiChiTiet(maPH, maLo, 2)) {
			// ✅ Update cột 6 (Trạng thái)
			modelCTPH.setValueAt("Đã hủy hàng", selectRowCT, 6);
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

	// sự kiện xuất file
	private void xuatPDFPhieuHuyDangChon() {
		// 1. Kiểm tra đã chọn phiếu chưa
		int rowView = tblPH.getSelectedRow();
		if (rowView == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiếu hủy trước khi xuất file!");
			return;
		}

		int rowModel = tblPH.convertRowIndexToModel(rowView);

		// 2. Lấy thông tin phiếu hủy đang chọn
		String maPH = modelPH.getValueAt(rowModel, 0).toString(); // Mã PH
		String ngayLap = modelPH.getValueAt(rowModel, 1).toString(); // Ngày lập phiếu
		String nhanVien = modelPH.getValueAt(rowModel, 2).toString(); // Nhân viên
		String tongTien = modelPH.getValueAt(rowModel, 3).toString(); // Tổng tiền (đã format)
		String trangThai = modelPH.getValueAt(rowModel, 4).toString(); // Trạng thái

		// 3. Chọn nơi lưu file
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Lưu phiếu hủy PDF");
		chooser.setSelectedFile(new File("PhieuHuy_" + maPH + ".pdf"));

		int result = chooser.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = chooser.getSelectedFile();

		// 4. Tạo PDF
		Document doc = new Document();
		try {
			PdfWriter.getInstance(doc, new FileOutputStream(file));
			doc.open();
			
			
			String fontPath = "lib/times.ttf"; // đúng vị trí file bro đang để

			com.itextpdf.text.pdf.BaseFont bf =
			    com.itextpdf.text.pdf.BaseFont.createFont(
			        fontPath,
			        com.itextpdf.text.pdf.BaseFont.IDENTITY_H,
			        com.itextpdf.text.pdf.BaseFont.EMBEDDED
			    );
			
			// FONT cho PDF 
			com.itextpdf.text.Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

			com.itextpdf.text.Font fontSubTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

			com.itextpdf.text.Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 11);

			// 4.1 Tên nhà thuốc (trên cùng bên trái)
			Paragraph tenNT = new Paragraph(TEN_NHA_THUOC + "\n\n", fontSubTitle);
			tenNT.setAlignment(Element.ALIGN_LEFT);
			doc.add(tenNT);

			// 4.2 Tiêu đề phiếu
			Paragraph title = new Paragraph("PHIẾU HỦY HÀNG\n\n", fontTitle);
			title.setAlignment(Element.ALIGN_CENTER);
			doc.add(title);

			// 4.3 Thông tin chung của phiếu (bảng 2 cột)
			PdfPTable infoTable = new PdfPTable(2);
			infoTable.setWidthPercentage(100);
			infoTable.setSpacingBefore(5);
			infoTable.setSpacingAfter(10);

			addInfoRow(infoTable, "Mã phiếu hủy:", maPH, fontSubTitle, fontNormal);
			addInfoRow(infoTable, "Ngày lập:", ngayLap, fontSubTitle, fontNormal);
			addInfoRow(infoTable, "Nhân viên lập:", nhanVien, fontSubTitle, fontNormal);
			addInfoRow(infoTable, "Trạng thái:", trangThai, fontSubTitle, fontNormal);
			addInfoRow(infoTable, "Tổng tiền:", tongTien, fontSubTitle, fontNormal);

			doc.add(infoTable);

			// 4.4 Bảng chi tiết phiếu hủy (lấy từ tblCTPH)
			Paragraph ctTitle = new Paragraph("Chi tiết phiếu hủy\n\n", fontSubTitle);
			ctTitle.setAlignment(Element.ALIGN_LEFT);
			doc.add(ctTitle);

			PdfPTable detailTable = new PdfPTable(tblCTPH.getColumnCount());
			detailTable.setWidthPercentage(100);

			// Header chi tiết
			for (int c = 0; c < tblCTPH.getColumnCount(); c++) {
				PdfPCell cell = new PdfPCell(new Paragraph(tblCTPH.getColumnName(c), fontSubTitle));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				detailTable.addCell(cell);
			}

			// Dòng dữ liệu chi tiết
			for (int r = 0; r < tblCTPH.getRowCount(); r++) {
				for (int c = 0; c < tblCTPH.getColumnCount(); c++) {
					Object val = tblCTPH.getValueAt(r, c);
					PdfPCell cell = new PdfPCell(new Paragraph(val == null ? "" : val.toString(), fontNormal));
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					detailTable.addCell(cell);
				}
			}

			doc.add(detailTable);

			JOptionPane.showMessageDialog(this, "Xuất PDF phiếu hủy thành công!");

		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Xuất PDF thất bại!");
		} finally {
			doc.close();
		}
	}

	private void addInfoRow(PdfPTable table, String label, String value, com.itextpdf.text.Font labelFont,
			com.itextpdf.text.Font valueFont) {
		PdfPCell c1 = new PdfPCell(new Paragraph(label, labelFont));
		PdfPCell c2 = new PdfPCell(new Paragraph(value, valueFont));
		c1.setBorder(PdfPCell.NO_BORDER);
		c2.setBorder(PdfPCell.NO_BORDER);
		table.addCell(c1);
		table.addCell(c2);
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
}
