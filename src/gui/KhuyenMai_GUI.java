package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import connectDB.connectDB;
import customcomponent.ClipTooltipRenderer;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.KhuyenMai_DAO;
import entity.KhuyenMai;

public class KhuyenMai_GUI extends JPanel implements ActionListener {

	// ===== KHAI BÁO THUỘC TÍNH =====
	private JPanel pnHeader;
	private PillButton btnThem;
	private PillButton btnCapNhat;
	private DefaultTableModel modelKM;
	private JTable tblKM;
	private JTextField txtTimKiem;
	
	private final Color blueMint = new Color(33, 150, 243);
	private final Color pinkPastel = new Color(255, 200, 220);
	private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DecimalFormat df = new DecimalFormat("#,###đ");

	private List<KhuyenMai> dsKhuyenMai;
	private KhuyenMai_DAO khuyenMaiDAO;
	private TableRowSorter<DefaultTableModel> sorter;

	// ===== CONSTRUCTOR =====
	public KhuyenMai_GUI() {
		khuyenMaiDAO = new KhuyenMai_DAO();
		dsKhuyenMai = new ArrayList<>();

		try {
			connectDB.getInstance().connect();
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu", "Error", JOptionPane.ERROR_MESSAGE);
		}

		initialize();
		addEvents();
	}

	// ===== KHỞI TẠO GIAO DIỆN =====
	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 850));

		// ----- HEADER -----
		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setBackground(new Color(0xE3F2F5));
		pnHeader.setLayout(null);
		add(pnHeader, BorderLayout.NORTH);

		btnThem = new PillButton("Thêm");
		btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnThem.setBounds(374, 25, 120, 40);
		pnHeader.add(btnThem);

		btnCapNhat = new PillButton("Cập nhật");
		btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnCapNhat.setBounds(549, 25, 120, 40);
		pnHeader.add(btnCapNhat);

		txtTimKiem = new JTextField("");
        PlaceholderSupport.addPlaceholder(txtTimKiem, "Tìm kiếm theo loại KM / tên KM...");
		txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		txtTimKiem.setBorder(new RoundedBorder(20));
		txtTimKiem.setBounds(10, 17, 336, 60);
		pnHeader.add(txtTimKiem);

		// ----- CENTER -----
		JPanel pnCenter = new JPanel(new BorderLayout());
		add(pnCenter, BorderLayout.CENTER);
		initTable();
		pnCenter.add(new JScrollPane(tblKM), BorderLayout.CENTER);
		
		loadDataFromDatabase();
	}

	// ===== XỬ LÝ SỰ KIỆN =====
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if (o.equals(btnThem)) {
			handleThemAction();
		} else if (o.equals(btnCapNhat)) {
			handleCapNhatAction();
		}
	}
	
	private void handleThemAction() {
		Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
		ThemKhuyenMai_Dialog themDialog = new ThemKhuyenMai_Dialog(parentFrame);
		themDialog.setVisible(true);

		KhuyenMai kmMoi = themDialog.getKhuyenMaiMoi();
		if (kmMoi != null) {
			if (khuyenMaiDAO.themKhuyenMai(kmMoi)) {
				loadDataFromDatabase();
				JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void handleCapNhatAction() {
		int viewRow = tblKM.getSelectedRow();
		if (viewRow == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
			return;
		}
		int modelRow = tblKM.convertRowIndexToModel(viewRow);
		KhuyenMai kmCanCapNhat = dsKhuyenMai.get(modelRow);

		Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
		CapNhatKhuyenMai_Dialog capNhatDialog = new CapNhatKhuyenMai_Dialog(parentFrame, kmCanCapNhat);
		capNhatDialog.setVisible(true);

		if (capNhatDialog.isUpdateSuccess()) {
			if (khuyenMaiDAO.capNhatKhuyenMai(kmCanCapNhat)) {
				dsKhuyenMai.set(modelRow, kmCanCapNhat);
				updateRowInTable(modelRow, kmCanCapNhat);
				JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				loadDataFromDatabase();
			}
		}
	}
	
	private void timKiem() {
	    String tuKhoa = txtTimKiem.getText().trim();
	    if (txtTimKiem.getForeground().equals(Color.GRAY)) {
	        sorter.setRowFilter(null);
	        return;
	    }
	    
	    if (tuKhoa.isEmpty()) {
	        sorter.setRowFilter(null);
	    } else {
	        try {
	            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + tuKhoa, 1, 6));
			} catch (java.util.regex.PatternSyntaxException e) {
				sorter.setRowFilter(null);
	        }
	    }
	}

	// ===== CÁC PHƯƠNG THỨC LIÊN QUAN ĐẾN BẢNG =====
	private void initTable() {
		String[] khuyenMaiCols = { "Mã khuyến mãi", "Tên khuyến mãi", "Hình thức", "Giá trị", "Ngày bắt đầu",
				"Ngày kết thúc", "Loại khuyến mãi", "Điều kiện áp dụng", "Trạng thái" };
		modelKM = new DefaultTableModel(khuyenMaiCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		
		sorter = new TableRowSorter<>(modelKM);
		tblKM = new JTable(modelKM);
		tblKM.setRowSorter(sorter);
		
		formatTable(tblKM);
		tblKM.setSelectionBackground(pinkPastel);
		tblKM.getTableHeader().setBackground(blueMint);
		tblKM.setRowHeight(35);

		for (int col = 0; col < tblKM.getColumnCount(); col++) {
			tblKM.getColumnModel().getColumn(col).setCellRenderer(new ClipTooltipRenderer());
		}
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
		table.getTableHeader().setBorder(null);
		table.getTableHeader().setForeground(Color.WHITE);
		table.setRowHeight(28);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		table.setSelectionBackground(new Color(180, 205, 230));
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		TableColumnModel m = table.getColumnModel();
		m.getColumn(0).setCellRenderer(centerRenderer);
		m.getColumn(3).setCellRenderer(rightRenderer);
		m.getColumn(4).setCellRenderer(centerRenderer);
		m.getColumn(5).setCellRenderer(centerRenderer);
		m.getColumn(8).setCellRenderer(centerRenderer);
	}

	private void loadDataFromDatabase() {
		dsKhuyenMai = khuyenMaiDAO.layTatCaKhuyenMai();
		modelKM.setRowCount(0); 
		for (KhuyenMai km : dsKhuyenMai) {
			addRowToTable(km);
		}
	}

	private void addRowToTable(KhuyenMai km) {
		String hinhThucHienThi, giaTriHienThi;
		switch (km.getHinhThuc()) {
		case GIAM_GIA_PHAN_TRAM:
			hinhThucHienThi = "Giảm giá phần trăm";
			giaTriHienThi = (int) km.getGiaTri() + "%";
			break;
		case GIAM_GIA_TIEN:
			hinhThucHienThi = "Giảm giá tiền";
			giaTriHienThi = df.format(km.getGiaTri());
			break;
		case TANG_THEM:
			hinhThucHienThi = "Tặng thêm";
			giaTriHienThi = "Mua " + km.getSoLuongToiThieu() + " tặng " + km.getSoLuongTangThem();
			break;
		default:
			hinhThucHienThi = "Không xác định";
			giaTriHienThi = "";
			break;
		}

		String loaiKm = km.isKhuyenMaiHoaDon() ? "Khuyến mãi hoá đơn" : "Khuyến mãi sản phẩm";
		String trangThai = km.isTrangThai() ? "Đang áp dụng" : "Hết hạn";
		
		String dieuKienHienThi = "Không áp dụng";
        if (km.isKhuyenMaiHoaDon() && km.getDieuKienApDungHoaDon() > 0) {
            dieuKienHienThi = "HĐ >= " + df.format(km.getDieuKienApDungHoaDon());
        }

		modelKM.addRow(new Object[] { km.getMaKM(), km.getTenKM(), hinhThucHienThi, giaTriHienThi,
				km.getNgayBatDau().format(fmt), km.getNgayKetThuc().format(fmt), loaiKm,
				dieuKienHienThi, trangThai });
	}

	private void updateRowInTable(int row, KhuyenMai km) {
		String hinhThucHienThi, giaTriHienThi;
		switch (km.getHinhThuc()) {
		case GIAM_GIA_PHAN_TRAM:
			hinhThucHienThi = "Giảm giá phần trăm";
			giaTriHienThi = (int) km.getGiaTri() + "%";
			break;
		case GIAM_GIA_TIEN:
			hinhThucHienThi = "Giảm giá tiền";
			giaTriHienThi = df.format(km.getGiaTri());
			break;
		case TANG_THEM:
			hinhThucHienThi = "Tặng thêm";
			giaTriHienThi = "Mua " + km.getSoLuongToiThieu() + " tặng " + km.getSoLuongTangThem();
			break;
		default:
			hinhThucHienThi = "Không xác định";
			giaTriHienThi = "";
			break;
		}

		String loaiKm = km.isKhuyenMaiHoaDon() ? "Khuyến mãi hoá đơn" : "Khuyến mãi sản phẩm";
		String trangThai = km.isTrangThai() ? "Đang áp dụng" : "Hết hạn";
		
		String dieuKienHienThi = "Không áp dụng";
        if (km.isKhuyenMaiHoaDon() && km.getDieuKienApDungHoaDon() > 0) {
            dieuKienHienThi = "HĐ >= " + df.format(km.getDieuKienApDungHoaDon());
        }

		modelKM.setValueAt(km.getTenKM(), row, 1);
		modelKM.setValueAt(hinhThucHienThi, row, 2);
		modelKM.setValueAt(giaTriHienThi, row, 3);
		modelKM.setValueAt(km.getNgayBatDau().format(fmt), row, 4);
		modelKM.setValueAt(km.getNgayKetThuc().format(fmt), row, 5);
		modelKM.setValueAt(loaiKm, row, 6);
		modelKM.setValueAt(dieuKienHienThi, row, 7);
		modelKM.setValueAt(trangThai, row, 8);
	}

	// ===== CÁC PHƯƠNG THỨC KHÁC =====
	private void addEvents() {
		btnThem.addActionListener(this);
		btnCapNhat.addActionListener(this);
		
		txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				timKiem();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				timKiem();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				timKiem();
			}
		});
	}
	
	// ===== CHẠY DEMO =====
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản lý Khuyến mãi");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new KhuyenMai_GUI());
			frame.setVisible(true);
		});
	}
}