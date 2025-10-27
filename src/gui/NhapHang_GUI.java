
package gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import customcomponent.*;
import entity.*;
import enums.DuongDung;

public class NhapHang_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private JPanel pnRight; // vùng cột phải
	private JButton btnThem;
	private JButton btnXuatFile;
	private DefaultTableModel modelPN;
	private JTable tblPN;
	private JScrollPane scrCTPN;
	private DefaultTableModel modelCTPN;
	private JScrollPane scrPN;
	private JTable tblCTPN;

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,000.#đ");

	private Color blueMint = new Color(180, 220, 240);
	private Color pinkPastel = new Color(255, 200, 220);
	private JTextField txtSearch;

	public NhapHang_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		// ===== HEADER =====
		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setLayout(null);
		add(pnHeader, BorderLayout.NORTH);

		txtSearch = new JTextField("");
		PlaceholderSupport.addPlaceholder(txtSearch, "Tìm kiếm theo tên / số điện thoại");
		txtSearch.setForeground(Color.GRAY);
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtSearch.setBounds(20, 27, 250, 44);
		txtSearch.setBorder(new RoundedBorder(20));

		// ===== BỘ LỌC THEO NGÀY =====
		JLabel lblTuNgay = new JLabel("Từ ngày:");
		lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblTuNgay.setBounds(306, 36, 60, 25);

		com.toedter.calendar.JDateChooser dateTu = new com.toedter.calendar.JDateChooser();
		dateTu.setDateFormatString("dd/MM/yyyy");
		dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		dateTu.setBounds(366, 36, 130, 25);
		dateTu.setDate(new java.util.Date()); // mặc định là hôm nay

		JLabel lblDenNgay = new JLabel("Đến:");
		lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblDenNgay.setBounds(511, 36, 40, 25);

		com.toedter.calendar.JDateChooser dateDen = new com.toedter.calendar.JDateChooser();
		dateDen.setDateFormatString("dd/MM/yyyy");
		dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		dateDen.setBounds(551, 36, 130, 25);

		// set mặc định là ngày mai
		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.add(java.util.Calendar.DATE, 1);
		dateDen.setDate(cal.getTime());

		btnThem = new PillButton("Thêm");
		btnThem.setSize(100, 30);
		btnThem.setLocation(703, 34);

		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setSize(100, 30);
		btnXuatFile.setLocation(813, 34);

		pnHeader.add(txtSearch);
		pnHeader.add(lblTuNgay);
		pnHeader.add(dateTu);
		pnHeader.add(lblDenNgay);
		pnHeader.add(dateDen);
		pnHeader.add(btnThem);
		pnHeader.add(btnXuatFile);

		// ===== CENTER =====
		pnCenter = new JPanel();
		pnCenter.setLayout(new BorderLayout());
		add(pnCenter, BorderLayout.CENTER);

		// ===== RIGHT =====
		pnRight = new JPanel();
		pnRight.setPreferredSize(new Dimension(600, 1080));
		pnRight.setBackground(new Color(0, 128, 255));
		pnRight.setLayout(new BoxLayout(pnRight, BoxLayout.Y_AXIS));
		add(pnRight, BorderLayout.EAST);

		initTable();
		LoadPhieuNhap();
	}

	private void initTable() {
		// Bảng phiếu nhập
		String[] phieuNhapCols = { "Mã PN", "Ngày lập phiếu", "Nhân Viên", "NCC", "Tổng tiền" };
		modelPN = new DefaultTableModel(phieuNhapCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblPN = new JTable(modelPN);
		scrPN = new JScrollPane(tblPN);
		pnCenter.add(scrPN);
		// Bảng chi tiết phiếu nhập
		String[] cTPhieuCols = { "Mã lô", "Mã SP", "Tên SP", "SL nhập", "Đơn giá", "Thành tiền" };

		modelCTPN = new DefaultTableModel(cTPhieuCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPN = new JTable(modelCTPN);
		scrCTPN = new JScrollPane(tblCTPN);
		pnRight.add(scrCTPN);

		formatTable(tblPN);
		tblPN.setSelectionBackground(blueMint);
		tblPN.getTableHeader().setBackground(pinkPastel);
		formatTable(tblCTPN);
		tblCTPN.setSelectionBackground(pinkPastel);
		tblCTPN.getTableHeader().setBackground(blueMint);
	}

	// Nạp danh sách phiếu nhập (master)
	public void LoadPhieuNhap() {
		DonViTinh vien = new DonViTinh("DVT-001", "Viên", null);
		DonViTinh chai = new DonViTinh("DVT-002", "Chai", null);
		DuongDung uong = DuongDung.UONG;
		LoaiSanPham thuoc = new LoaiSanPham("LSP001", "Thuốc", null);

		// ====== SẢN PHẨM ======
		SanPham sp1 = new SanPham("SP000001", "Paracetamol 500mg", thuoc, "VN-12345", "Paracetamol", "500mg",
				"DHG Pharma", "Việt Nam", vien, uong, 800, 1500, "paracetamol.jpg", "Hộp 10 vỉ x 10 viên", "A1", true);

		SanPham sp2 = new SanPham("SP000002", "Vitamin C 1000mg", thuoc, "VN-67890", "Ascorbic Acid", "1000mg",
				"Traphaco", "Việt Nam", vien, uong, 1200, 2500, "vitaminc.jpg", "Hộp 5 vỉ x 10 viên", "A2", true);

		// ====== LÔ SẢN PHẨM ======
		LoSanPham lo1 = new LoSanPham("LO000001", LocalDate.of(2025, 1, 1), LocalDate.of(2027, 1, 1), 100, sp1);
		LoSanPham lo2 = new LoSanPham("LO000002", LocalDate.of(2024, 1, 1), LocalDate.of(2026, 1, 1), 200, sp2);

		// ====== TÀI KHOẢN & NHÂN VIÊN ======
		TaiKhoan tk1 = new TaiKhoan("TK000001", "admin", "Aa123456@");
		TaiKhoan tk2 = new TaiKhoan("TK000002", "user25100001", "Aa123456@");
		NhanVien nv1 = new NhanVien("NV2025100001", "Lê Thanh Kha", true, LocalDate.of(1998, 5, 10), "0912345678",
				"TP.HCM", true, tk1, "SANG", true);
		NhanVien nv2 = new NhanVien("NV2025100002", "Chu Anh Khôi", true, LocalDate.of(1998, 5, 10), "0912345678",
				"TP.HCM", false, tk2, "SANG", true);

		// ====== NHÀ CUNG CẤP ======
		NhaCungCap ncc1 = new NhaCungCap("NCC-001", "Công ty Dược Hậu Giang", "0283822334", "Cần Thơ");
		NhaCungCap ncc2 = new NhaCungCap("NCC-002", "Traphaco Pharma", "0243933838", "Hà Nội");

		// ====== PHIẾU NHẬP ======
		PhieuNhap pn1 = new PhieuNhap("PN001", LocalDate.of(2025, 10, 18), ncc1, nv1, (double) 0);
		PhieuNhap pn2 = new PhieuNhap("PN002", LocalDate.of(2025, 10, 19), ncc2, nv1, 0);

		List<PhieuNhap> phieuNhaps = (List) Arrays.asList(pn1, pn2);

		// ====== CHI TIẾT PHIẾU NHẬP ======
		ChiTietPhieuNhap ct1 = new ChiTietPhieuNhap(pn1, lo1, 50, 800);
		ChiTietPhieuNhap ct2 = new ChiTietPhieuNhap(pn1, lo2, 30, 1200);
		ChiTietPhieuNhap ct3 = new ChiTietPhieuNhap(pn2, lo1, 20, 850);

		List<ChiTietPhieuNhap> chiTietPhieuNhaps = (List) Arrays.asList(ct1, ct2, ct3);

		// ====== TÍNH TỔNG TIỀN ======
		for (PhieuNhap pn : phieuNhaps) {
			double tong = chiTietPhieuNhaps.stream().filter(ct -> ct.getPhieuNhap() == pn)
					.mapToDouble(ct -> ct.getThanhTien()).sum();
			pn.setTongTien(tong);
		}

		for (PhieuNhap pn : phieuNhaps) {
			modelPN.addRow(
					new Object[] { pn.getMaPhieuNhap(), pn.getNgayNhap().format(fmt), pn.getNhanVien().getTenNhanVien(),
							pn.getNhaCungCap().getTenNhaCungCap(), df.format(pn.getTongTien()) });
		}

		for (ChiTietPhieuNhap ctpn : chiTietPhieuNhaps) {
			modelCTPN.addRow(new Object[] { ctpn.getLoSanPham().getMaLo(),
					ctpn.getLoSanPham().getSanPham().getMaSanPham(), ctpn.getLoSanPham().getSanPham().getTenSanPham(),
					ctpn.getSoLuongNhap(), df.format(ctpn.getDonGiaNhap()), df.format(ctpn.getThanhTien()) });
		}
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
		table.getTableHeader().setBorder(null);

		table.setRowHeight(28);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		table.setSelectionBackground(new Color(180, 205, 230));
		table.setShowGrid(false);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);

		// Đặt renderer cho từng cột theo tên header
		TableColumnModel m = table.getColumnModel();
		for (int i = 0; i < m.getColumnCount(); i++) {
			String col = m.getColumn(i).getHeaderValue().toString().toLowerCase();

			if (col.contains("mã"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else if (col.contains("số lượng") || col.contains("sl"))
				m.getColumn(i).setCellRenderer(rightRenderer);
			else if (col.contains("giá") || col.contains("tiền"))
				m.getColumn(i).setCellRenderer(rightRenderer);
			else if (col.contains("ngày"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else
				m.getColumn(i).setCellRenderer(leftRenderer);
		}

		// Không cho reorder header
		table.getTableHeader().setReorderingAllowed(false);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Khung trống - clone base");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new NhapHang_GUI());
			frame.setVisible(true);
		});
	}
}
