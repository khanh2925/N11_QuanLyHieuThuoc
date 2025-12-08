/**
 * @author Quốc Khánh cute
 * @version 1.0
 * @since Oct 19, 2025
 *
 * Mô tả: Giao diện tra cứu phiếu hủy hàng (3 loại: NV tạo, Hệ thống tạo, Duyệt trả hàng).
 */
package gui.tracuu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;

import component.border.RoundedBorder;
import component.button.*;
import component.input.*;
import dao.ChiTietPhieuHuy_DAO;
import dao.PhieuHuy_DAO;
import entity.ChiTietPhieuHuy;
import entity.PhieuHuy;

public class TraCuuPhieuHuy_GUI extends JPanel implements ActionListener, DocumentListener {

	private JPanel pnHeader;
	private JPanel pnCenter;

	// Bảng Phiếu Hủy (Trên)
	private JTable tblPhieuHuy;
	private DefaultTableModel modelPhieuHuy;

	// Bảng Chi Tiết Phiếu Hủy (Dưới)
	private JTable tblChiTiet;
	private DefaultTableModel modelChiTiet;

	// Các component lọc
	private JTextField txtTimKiem;
	private JDateChooser dateTuNgay;
	private JDateChooser dateDenNgay;
	private JComboBox<String> cbTrangThai;
	private PhieuHuy_DAO ph_dao;
	private ChiTietPhieuHuy_DAO ctph_dao;
	private List<PhieuHuy> dsPH;
	private List<ChiTietPhieuHuy> dsCTPH;
	private PillButton btnLamMoi;
	private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 16);
	private final Color COLOR_PRIMARY = new Color(33, 150, 243);
	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,###đ");
	private TableRowSorter<DefaultTableModel> sorterPhieuHuy;

	public TraCuuPhieuHuy_GUI() {
		setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		// 1. LAYOUT CHÍNH
		ph_dao = new PhieuHuy_DAO();
		ctph_dao = new ChiTietPhieuHuy_DAO();
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		// 2. HEADER
		taoPhanHeader();
		add(pnHeader, BorderLayout.NORTH);

		// 3. CENTER (2 Bảng)
		taoPhanCenter();
		add(pnCenter, BorderLayout.CENTER);

		// 4. DATA & EVENTS
		loadDuLieuPhieuHuy();
		loadDuLieuPhieuHuyTheoPH();

		btnLamMoi.addActionListener(this);
		txtTimKiem.getDocument().addDocumentListener(this);
		cbTrangThai.addActionListener(e -> applyFilters());
	}

	// ==============================================================================
	// PHẦN HEADER
	// ==============================================================================
	private void taoPhanHeader() {
		pnHeader = new JPanel();
		pnHeader.setLayout(null);
		pnHeader.setPreferredSize(new Dimension(1073, 94));
		pnHeader.setBackground(new Color(0xE3F2F5));

		// --- 1. Ô TÌM KIẾM TO (Bên trái) - KHỚP VỚI TRA CỨU ĐƠN HÀNG ---
		txtTimKiem = new JTextField();
		PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm theo mã phiếu, tên nhân viên");
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 20)); // Font 20
		txtTimKiem.setBounds(25, 17, 480, 60); // Width 480 giống TraCuuDonHang
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setBackground(Color.WHITE);
		pnHeader.add(txtTimKiem);

		// --- 2. BỘ LỌC (Ở giữa) - KHỚP VỊ TRÍ ---
		
		// Trạng thái (Vị trí giữa tìm kiếm và từ ngày)
		JLabel lblTT = new JLabel("Trạng thái:");
		lblTT.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Font 18
		lblTT.setBounds(530, 28, 90, 35); // x=530 giống TraCuuDonHang
		pnHeader.add(lblTT);

		cbTrangThai = new JComboBox<>(new String[] { "Tất cả", "Đã duyệt", "Chờ duyệt" });
		cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Font 18
		cbTrangThai.setBounds(625, 28, 135, 38); // Height 38
		pnHeader.add(cbTrangThai);

		// Từ ngày (Vị trí tiếp theo)
		JLabel lblTu = new JLabel("Từ ngày:");
		lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Font 18
		lblTu.setBounds(775, 28, 80, 35);
		pnHeader.add(lblTu);

		dateTuNgay = new JDateChooser();
		dateTuNgay.setDateFormatString("dd/MM/yyyy");
		dateTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Font 18
		dateTuNgay.setBounds(860, 28, 150, 38); // Width 150
		dateTuNgay.setDate(null);
		pnHeader.add(dateTuNgay);

		// Đến ngày
		JLabel lblDen = new JLabel("Đến:");
		lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Font 18
		lblDen.setBounds(1025, 28, 50, 35);
		pnHeader.add(lblDen);

		dateDenNgay = new JDateChooser();
		dateDenNgay.setDateFormatString("dd/MM/yyyy");
		dateDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Font 18
		dateDenNgay.setBounds(1080, 28, 150, 38); // Width 150
		pnHeader.add(dateDenNgay);
		
		dateTuNgay.getDateEditor().addPropertyChangeListener("date", evt -> {
		    validateAndApplyDateFilter();
		});

		dateDenNgay.getDateEditor().addPropertyChangeListener("date", evt -> {
		    validateAndApplyDateFilter();
		});

		// --- 3. CÁC NÚT CHỨC NĂNG (Bên phải) - KHỚP VỊ TRÍ ---
		btnLamMoi = new PillButton("Làm mới");
		btnLamMoi.setBounds(1265, 22, 130, 50); // Vị trí btnLamMoi của TraCuuDonHang
		btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Font 18
		pnHeader.add(btnLamMoi);

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

		// --- BẢNG 1: DANH SÁCH PHIẾU HỦY (TOP) ---
		// Thêm cột "Nguồn gốc" để phân biệt 3 loại
		String[] colPhieuHuy = { "STT", "Mã phiếu hủy", "Người lập / Hệ thống", "Ngày lập", "Tổng tiền", "Trạng thái" };
		modelPhieuHuy = new DefaultTableModel(colPhieuHuy, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblPhieuHuy = setupTable(modelPhieuHuy);
		sorterPhieuHuy = new TableRowSorter<>(modelPhieuHuy);// lọc bảng theo dữ liệu trên thanh tìm kiếm
		tblPhieuHuy.setRowSorter(sorterPhieuHuy);

		// Căn lề & Render màu sắc
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);

		tblPhieuHuy.getColumnModel().getColumn(0).setCellRenderer(center); // STT
		tblPhieuHuy.getColumnModel().getColumn(1).setCellRenderer(center); // Mã
		tblPhieuHuy.getColumnModel().getColumn(2).setCellRenderer(center); // Người lập
		tblPhieuHuy.getColumnModel().getColumn(3).setCellRenderer(center); // Ngày
		tblPhieuHuy.getColumnModel().getColumn(4).setCellRenderer(right); // Tiền

		// Render cột Trạng Thái (Màu sắc)
		tblPhieuHuy.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Font 16 đồng nhất
				String status = (String) value;
				if ("Đã duyệt".equals(status)) {
					lbl.setForeground(new Color(0x2E7D32)); // Xanh lá
				} else {
					lbl.setForeground(new Color(0xE65100)); // Cam
				}
				return lbl;
			}
		});

		tblPhieuHuy.getColumnModel().getColumn(1).setPreferredWidth(150);
		tblPhieuHuy.getColumnModel().getColumn(2).setPreferredWidth(200);
		tblPhieuHuy.getColumnModel().getColumn(4).setPreferredWidth(180);

		JScrollPane scrollPH = new JScrollPane(tblPhieuHuy);
		scrollPH.setBorder(createTitledBorder("Danh sách phiếu hủy hàng"));
		splitPane.setTopComponent(scrollPH);

		// --- BẢNG 2: CHI TIẾT PHIẾU HỦY (BOTTOM) ---
		String[] colChiTiet = { "STT", "Mã Lô", "Sản phẩm", "Lý do chi tiết", "Số lượng", "Giá vốn", "Thành tiền",
				"Trạng thái" };
		modelChiTiet = new DefaultTableModel(colChiTiet, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblChiTiet = setupTable(modelChiTiet);

		tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(center);// stt
		tblChiTiet.getColumnModel().getColumn(1).setCellRenderer(center);// mã lô
		tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(250); // Tên SP
		tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(200); // Lý do
		tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(right); // SL
		tblChiTiet.getColumnModel().getColumn(5).setCellRenderer(right); // Giá nhập
		tblChiTiet.getColumnModel().getColumn(6).setCellRenderer(right); // Thành tiền
		tblChiTiet.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				lbl.setHorizontalAlignment(SwingConstants.CENTER);
				lbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Font 16 đồng nhất
				String status = (String) value;
				if ("Đã hủy hàng".equals(status)) {
					lbl.setForeground(new Color(0x2E7D32)); // Xanh lá
				} else if ("Đã từ chối hủy".equals(status)) {
					lbl.setForeground(new Color(0xE65100)); // Cam
				} else if ("Chờ duyệt".equals(status)) {
					lbl.setForeground(Color.black);
				}
				return lbl;
			}
		});

		JScrollPane scrollChiTiet = new JScrollPane(tblChiTiet);
		scrollChiTiet.setBorder(createTitledBorder("Chi tiết sản phẩm hủy"));
		splitPane.setBottomComponent(scrollChiTiet);
	}

	private JTable setupTable(DefaultTableModel model) {
		JTable table = new JTable(model);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Font 16
		table.setRowHeight(35); // Cao 35
		table.setSelectionBackground(new Color(0xC8E6C9));
		table.setGridColor(new Color(230, 230, 230));
		
		JTableHeader header = table.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Header Font 16 Bold
		header.setBackground(COLOR_PRIMARY);
		header.setForeground(Color.WHITE);
		header.setPreferredSize(new Dimension(100, 40)); // Header Cao 40
		return table;
	}

	private TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(Color.LIGHT_GRAY), title,
			TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18), Color.DARK_GRAY // Font 18
		);
	}

	// ==================================================================
	// LỌC DỮ LIỆU TRÊN BẢNG PHIẾU HỦY (không query DB lại)
	// ==================================================================
	private void applyFilters() {
		if (sorterPhieuHuy == null)
			return;

		List<RowFilter<Object, Object>> filters = new ArrayList<>();

		// 1. Lọc theo text: cột 1 (Mã phiếu hủy) + cột 2 (Người lập / Hệ thống)
		String text = txtTimKiem.getText().trim();
		if (!text.isEmpty() && !txtTimKiem.getForeground().equals(Color.GRAY)) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(text), 1, 2));
		}

		// 2. Lọc theo trạng thái: cột 5 (Trạng thái)
		String trangThai = (String) cbTrangThai.getSelectedItem();
		if (trangThai != null && !"Tất cả".equalsIgnoreCase(trangThai.trim())) {
			filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(trangThai.trim()), 5));
		}

		// 3. Lọc theo khoảng ngày: cột 3 (Ngày lập)
		Date tu = dateTuNgay.getDate();
		Date den = dateDenNgay.getDate();

		if (tu != null || den != null) {

			RowFilter<Object, Object> dateFilter = new RowFilter<Object, Object>() {
				@Override
				public boolean include(Entry<?, ?> entry) {

					Object value = entry.getValue(3); // cột "Ngày lập"
					if (value == null)
						return false;

					LocalDate ngay;

					// Nếu model lưu LocalDate (đúng với loadDuLieuPhieuHuy hiện tại)
					if (value instanceof LocalDate) {
						ngay = (LocalDate) value;
					} else {
						// Nếu sau này bạn đổi sang String
						String s = value.toString().trim();
						LocalDate tmp = null;
						try {
							tmp = LocalDate.parse(s, fmt); // dd/MM/yyyy
						} catch (Exception ex1) {
							try {
								tmp = LocalDate.parse(s); // yyyy-MM-dd
							} catch (Exception ex2) {
								// Không parse được → không lọc theo ngày dòng này
								return true;
							}
						}
						ngay = tmp;
					}

					// Từ ngày
					if (tu != null) {
						LocalDate from = tu.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						if (ngay.isBefore(from))
							return false;
					}

					// Đến ngày
					if (den != null) {
						LocalDate to = den.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						if (ngay.isAfter(to))
							return false;
					}

					return true;
				}
			};

			filters.add(dateFilter);
		}

		// ÁP DỤNG LỌC
		if (filters.isEmpty()) {
			sorterPhieuHuy.setRowFilter(null); // hiện full
		} else {
			sorterPhieuHuy.setRowFilter(RowFilter.andFilter(filters)); // lọc chồng
		}
	}
	
	// ==================================================================
	// Kiểm tra logic ngày và gọi applyFilters() nếu hợp lệ
	// ==================================================================
	private void validateAndApplyDateFilter() {
	    Date tu = dateTuNgay.getDate();
	    Date den = dateDenNgay.getDate();

	    // Nếu đã chọn cả 2 ngày thì kiểm tra
	    if (tu != null && den != null) {
	        LocalDate from = tu.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        LocalDate to = den.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	        if (to.isBefore(from)) {
	            JOptionPane.showMessageDialog(
	                this,
	                "Ngày đến không được trước ngày từ!",
	                "Lỗi ngày",
	                JOptionPane.WARNING_MESSAGE
	            );

	            // Reset ngày đến, không áp dụng filter sai
	            dateDenNgay.setDate(null);
	            return;
	        }
	    }

	    // Nếu hợp lệ (hoặc chưa chọn đủ 2 ngày) thì vẫn áp dụng lọc bình thường
	    applyFilters();
	}

	

	// ==============================================================================
	// DỮ LIỆU & SỰ KIỆN
	// ==============================================================================

	private void loadDuLieuPhieuHuyTheoPH() {
		// Click phiếu hủy -> Load chi tiết
		tblPhieuHuy.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int row = tblPhieuHuy.getSelectedRow();
				if (row >= 0) {
					String maPH = tblPhieuHuy.getValueAt(row, 1).toString();
					loadChiTietPhieuHuy(maPH);
				}
			}
		});
	}

	private void loadDuLieuPhieuHuy() {
		dsPH = new ArrayList<PhieuHuy>();
		modelPhieuHuy.setRowCount(0);

		try {
			dsPH = ph_dao.layTatCaPhieuHuy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int stt = 1;
		for (PhieuHuy ph : dsPH) {
			modelPhieuHuy.addRow(new Object[] { stt++, ph.getMaPhieuHuy(), ph.getNhanVien().getTenNhanVien(),
					fmt.format(ph.getNgayLapPhieu()), df.format(ph.getTongTien()), ph.getTrangThaiText() });
		}
	}

	private void loadChiTietPhieuHuy(String maPH) {

		dsCTPH = new ArrayList<ChiTietPhieuHuy>();
		modelChiTiet.setRowCount(0);

		try {
			dsCTPH = ph_dao.layChiTietTheoMaPhieu(maPH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int stt = 1;
		for (ChiTietPhieuHuy ctph : dsCTPH) {
			modelChiTiet.addRow(new Object[] { stt++, ctph.getLoSanPham().getMaLo(),
					ctph.getLoSanPham().getSanPham().getTenSanPham(), ctph.getLyDoChiTiet(), ctph.getSoLuongHuy(),
					df.format(ctph.getDonGiaNhap()), df.format(ctph.getThanhTien()), ctph.getTrangThaiText() });
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
			}
			JFrame frame = new JFrame("Tra cứu phiếu hủy");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1450, 850);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new TraCuuPhieuHuy_GUI());
			frame.setVisible(true);
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == btnLamMoi) {
			LamMoi();
			return;
		}
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		applyFilters();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		applyFilters();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		applyFilters();
	}

	private void LamMoi() {
		txtTimKiem.setText("");
		cbTrangThai.setSelectedIndex(0);		
		dateTuNgay.setDate(null);
		dateDenNgay.setDate(null);

		if (sorterPhieuHuy != null) {
			sorterPhieuHuy.setRowFilter(null);
		}

		loadDuLieuPhieuHuy();
	}

}