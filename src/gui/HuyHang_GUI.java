/**
 * @author Thanh Kha
 * @version 1.0
 * @since Oct 16, 2025
 *
 * Mô tả: Giao diện quản lý phiếu huỷ hàng
 */

package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.toedter.calendar.JDateChooser;
import customcomponent.*;
import entity.*;
import enums.DuongDung;

public class HuyHang_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private JPanel pnRight; // vùng cột phải
	private JButton btnThem;
	private JButton btnXuatFile;
	private JTextField txtSearch;
	private DefaultTableModel modelPH;
	private JTable tblPH;
	private JScrollPane scrCTPH;
	private DefaultTableModel modelCTPH;
	private JScrollPane scrPH;
	private JTable tblCTPH;

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,000.#đ");

	private Color blueMint = new Color(180, 220, 240);
	private Color pinkPastel = new Color(255, 200, 220);

	public HuyHang_GUI() {
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
		loadPhieuHuy();
	}

	private void initTable() {
		// Bảng phiếu huỷ
		String[] phieuHuyCols = { "Mã PH", "Ngày lập phiếu", "Nhân Viên", "Tổng dòng huỷ", "Trạng thái" };
		modelPH = new DefaultTableModel(phieuHuyCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblPH = new JTable(modelPH);
		scrPH = new JScrollPane(tblPH);
		pnCenter.add(scrPH);
		// Bảng chi tiết phiếu huỷ
		String[] cTPhieuCols = { "Mã lô", "Mã SP", "Tên SP", "SL huỷ", "Đơn vị tính", "Hạn sử dụng", "Lý do" };

		modelCTPH = new DefaultTableModel(cTPhieuCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPH = new JTable(modelCTPH);
		scrCTPH = new JScrollPane(tblCTPH);
		pnRight.add(scrCTPH);

		formatTable(tblPH);
		tblPH.setSelectionBackground(blueMint);
		tblPH.getTableHeader().setBackground(pinkPastel);
		formatTable(tblCTPH);
		tblCTPH.setSelectionBackground(pinkPastel);
		tblCTPH.getTableHeader().setBackground(blueMint);
	}

	// Nạp danh sách phiếu nhập (master)
	private void loadPhieuHuy() {
		// ====== DANH MỤC / SẢN PHẨM CƠ BẢN (tương tự LoadPhieuNhap) ======
		DonViTinh vien = new DonViTinh("DVT-001", "Viên", null);
		DuongDung uong = DuongDung.UONG;
		LoaiSanPham thuoc = new LoaiSanPham("LSP001", "Thuốc", null);

		SanPham sp1 = new SanPham("SP000001", "Paracetamol 500mg", thuoc, "VN-12345", "Paracetamol", "500mg",
				"DHG Pharma", "Việt Nam", vien, uong, 800, 1500, "paracetamol.jpg", "Hộp 10 vỉ x 10 viên", "A1", true);

		SanPham sp2 = new SanPham("SP000002", "Vitamin C 1000mg", thuoc, "VN-67890", "Ascorbic Acid", "1000mg",
				"Traphaco", "Việt Nam", vien, uong, 1200, 2500, "vitaminc.jpg", "Hộp 5 vỉ x 10 viên", "A2", true);

		// ====== LÔ SẢN PHẨM (theo constructor bạn đang dùng: 2 LocalDate) ======
		LoSanPham lo1 = new LoSanPham("LO000003", LocalDate.of(2025, 6, 1), LocalDate.of(2027, 6, 1), 80, sp1);
		LoSanPham lo2 = new LoSanPham("LO000004", LocalDate.of(2024, 12, 1), LocalDate.of(2026, 12, 1), 120, sp2);
		// Nếu LoSanPham của bạn chỉ có 1 LocalDate (hanSuDung), thay bằng:
		// LoSanPham lo1 = new LoSanPham("LO000003", LocalDate.of(2027, 6, 1), 80, sp1);
		// LoSanPham lo2 = new LoSanPham("LO000004", LocalDate.of(2026, 12, 1), 120,
		// sp2);

		// ====== TÀI KHOẢN & NHÂN VIÊN ======
		TaiKhoan tk1 = new TaiKhoan("TK000001", "admin", "Aa123456@");
		TaiKhoan tk2 = new TaiKhoan("TK000002", "user25100001", "Aa123456@");

		// Giữ đúng thứ tự tham số giống bên bạn (ma, ten, gioiTinh, ngaySinh, sdt,
		// diaChi, quanLy, taiKhoan, caLam, trangThai)
		NhanVien nv1 = new NhanVien("NV2025100001", "Lê Thanh Kha", true, LocalDate.of(1998, 5, 10), "0912345678",
				"TP.HCM", true, tk1, "SANG", true);
		NhanVien nv2 = new NhanVien("NV2025100002", "Chu Anh Khôi", true, LocalDate.of(1998, 5, 10), "0912345678",
				"TP.HCM", false, tk2, "SANG", true);

		// ====== PHIẾU HỦY ======
		PhieuHuy ph1 = new PhieuHuy("PH-20251018-0001", LocalDate.of(2025, 10, 18), nv1, true);
		PhieuHuy ph2 = new PhieuHuy("PH-20251019-0002", LocalDate.of(2025, 10, 19), nv2, false);

		List<PhieuHuy> phieuHuys = Arrays.asList(ph1, ph2);

		// ====== CHI TIẾT PHIẾU HỦY ======
		ChiTietPhieuHuy cth1 = new ChiTietPhieuHuy(ph1, 10, "Viên bị ẩm/mốc", lo1, lo1.getSanPham().getGiaNhap());
		ChiTietPhieuHuy cth2 = new ChiTietPhieuHuy(ph1, 5, "Vỏ vỉ rách", lo2, lo2.getSanPham().getGiaNhap());
		ChiTietPhieuHuy cth3 = new ChiTietPhieuHuy(ph2, 20, "Cận hạn sử dụng", lo1, lo1.getSanPham().getGiaNhap());

		List<ChiTietPhieuHuy> chiTietPhieuHuys = Arrays.asList(cth1, cth2, cth3);

		// ====== ĐỔ DỮ LIỆU LÊN BẢNG PHIẾU HỦY ======
		// Giả định bạn đã có:
		// - modelPH: DefaultTableModel cho bảng Phiếu Hủy
		// - fmt: DateTimeFormatter (ví dụ: DateTimeFormatter.ofPattern("dd/MM/yyyy"))
		modelPH.setRowCount(0);
		for (PhieuHuy ph : phieuHuys) {
			modelPH.addRow(new Object[] { ph.getMaPhieuHuy(), ph.getNgayLapPhieu().format(fmt),
					ph.getNhanVien().getTenNhanVien(), ph.isTrangThai() ? "Đã duyệt" : "Chờ duyệt" });
		}

		// ====== ĐỔ DỮ LIỆU LÊN BẢNG CHI TIẾT PHIẾU HỦY ======
		// Giả định bạn đã có modelCTPH: DefaultTableModel cho bảng chi tiết hủy
		modelCTPH.setRowCount(0);
		for (ChiTietPhieuHuy cth : chiTietPhieuHuys) {
			modelCTPH.addRow(new Object[] { cth.getPhieuHuy().getMaPhieuHuy(), cth.getLoSanPham().getMaLo(),
					cth.getLoSanPham().getSanPham().getTenSanPham(), cth.getSoLuongHuy(), cth.getLyDoChiTiet() });
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
			frame.setContentPane(new HuyHang_GUI());
			frame.setVisible(true);
		});
	}
}
