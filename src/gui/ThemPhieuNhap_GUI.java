/**
 * @author Thanh Kha
 * @version 1.0
 * @since Oct 16, 2025
 *
 * Mô tả: Giao diện thêm phiếu nhập
 */

package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.toedter.calendar.JDateChooser;
import customcomponent.*;
import entity.NhanVien;
import entity.PhieuNhap;

public class ThemPhieuNhap_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private JPanel pnRight; // vùng cột phải
	private JScrollPane scrNhapHangItems;
	private JTextField txtSearch;
	private PillButton btnImport;
	private PillButton btnXuatFile;
	private JTextField txtSearchNCC;

	public ThemPhieuNhap_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
		loadTestItemRows();
		initFilterPanel();
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
		dateTu.setDate(new java.util.Date());

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

		btnImport = new PillButton("Nhập từ file");
		btnImport.setSize(141, 30);
		btnImport.setLocation(703, 34);

		pnHeader.add(txtSearch);
		pnHeader.add(lblTuNgay);
		pnHeader.add(dateTu);
		pnHeader.add(lblDenNgay);
		pnHeader.add(dateDen);
		pnHeader.add(btnImport);

		// ===== CENTER =====
		pnCenter = new JPanel();
		pnCenter.setLayout(new FlowLayout());
		scrNhapHangItems = new JScrollPane(pnCenter);
		add(scrNhapHangItems, BorderLayout.CENTER);

		// ===== RIGHT =====
		pnRight = new JPanel();
		pnRight.setPreferredSize(new Dimension(300, 1080));
		pnRight.setBackground(new Color(255, 255, 255));
		add(pnRight, BorderLayout.EAST);

	}

	// ====================== DỮ LIỆU MẪU TEST ======================
	private java.util.List<NhapHangItemRow.RowModel> getTestRows() {
		java.util.List<NhapHangItemRow.RowModel> list = new java.util.ArrayList<>();
		list.add(new NhapHangItemRow.RowModel("Paracetamol", "500 mg", "Hộp", "LO0001",
				java.time.LocalDate.of(2025, 12, 10), 20, 100_000));
		list.add(new NhapHangItemRow.RowModel("Ibuprofen", "200 mg", "Vỉ", "LO0002",
				java.time.LocalDate.of(2025, 11, 5), 30, 80_000));
		list.add(new NhapHangItemRow.RowModel("Amoxicillin", "500 mg", "Hộp", "LO0003",
				java.time.LocalDate.of(2026, 1, 15), 15, 120_000));
		list.add(new NhapHangItemRow.RowModel("Cefalexin", "250 mg", "Vỉ", "LO0004",
				java.time.LocalDate.of(2025, 10, 20), 25, 95_000));
		list.add(new NhapHangItemRow.RowModel("Vitamin C", "1000 mg", "Hộp", "LO0005",
				java.time.LocalDate.of(2025, 9, 30), 50, 60_000));
		list.add(new NhapHangItemRow.RowModel("Panadol Extra", "500 mg", "Hộp", "LO0006",
				java.time.LocalDate.of(2026, 2, 12), 40, 110_000));
		list.add(new NhapHangItemRow.RowModel("Efferalgan", "500 mg", "Hộp", "LO0007",
				java.time.LocalDate.of(2025, 12, 25), 35, 105_000));
		list.add(new NhapHangItemRow.RowModel("Clorpheniramin", "4 mg", "Vỉ", "LO0008",
				java.time.LocalDate.of(2025, 8, 18), 60, 30_000));
		list.add(new NhapHangItemRow.RowModel("Azithromycin", "250 mg", "Hộp", "LO0009",
				java.time.LocalDate.of(2026, 4, 2), 18, 150_000));
		list.add(new NhapHangItemRow.RowModel("Loratadin", "10 mg", "Vỉ", "LO0010",
				java.time.LocalDate.of(2025, 11, 28), 45, 55_000));
		return list;
	}

	// ====================== HIỂN THỊ ITEM ROW ======================
	private void loadTestItemRows() {
		pnCenter.removeAll();
		pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.Y_AXIS));
		java.util.List<NhapHangItemRow.RowModel> testRows = getTestRows();

		for (NhapHangItemRow.RowModel m : testRows) {
			NhapHangItemRow row = new NhapHangItemRow(m);

			// thêm event xoá
			row.getBtnTrash().addActionListener(e -> {
				pnCenter.remove(row);
				pnCenter.revalidate();
				pnCenter.repaint();
			});

			pnCenter.add(row);
			row.setPreferredSize(new Dimension(1000, 100));
			pnCenter.add(Box.createVerticalStrut(6));
		}

		pnCenter.revalidate();
		pnCenter.repaint();
	}

	private void initFilterPanel() {
		pnRight.setLayout(null);
		// ==== Nhãn tên nhân viên + ngày giờ ====
		JLabel lblNhanVien = new JLabel("Phạm Quốc Khánh");
		lblNhanVien.setFont(new Font("Dialog", Font.PLAIN, 16));
		lblNhanVien.setBounds(17, 14, 143, 18);
		pnRight.add(lblNhanVien);

		JLabel lblNgay = new JLabel("08/10/2025 11:45", SwingConstants.RIGHT);
		lblNgay.setFont(new Font("Dialog", Font.PLAIN, 16));
		lblNgay.setBounds(158, 15, 125, 16);
		pnRight.add(lblNgay);

		// ==== Ô tìm kiếm nhà cung cấp ====
		JLayeredPane pnSearch = new JLayeredPane();
		pnSearch.setBounds(0, 50, 300, 40);
		txtSearchNCC = new JTextField("");
		PlaceholderSupport.addPlaceholder(txtSearchNCC, "Tìm kiếm nhà cung cấp");
		txtSearchNCC.setForeground(Color.GRAY);
		txtSearchNCC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		txtSearchNCC.setBounds(0, 0, 300, 40);
		txtSearchNCC.setBorder(new RoundedBorder(20));
		pnSearch.add(txtSearchNCC, Integer.valueOf(0));
		pnRight.add(pnSearch);

		// ==== Nhà cung cấp ====
		JLabel lblNCC = new JLabel("Nhà cung cấp:");
		lblNCC.setFont(new Font("Dialog", Font.PLAIN, 16));
		lblNCC.setBounds(10, 105, 113, 16);
		pnRight.add(lblNCC);

		JLabel lblNCCValue = new JLabel("Công ty Pharmedic");
		lblNCCValue.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNCCValue.setFont(new Font("Dialog", Font.PLAIN, 16));
		lblNCCValue.setBounds(140, 105, 143, 16);
		pnRight.add(lblNCCValue);

		// ==== Tổng tiền ====
		JLabel lblTong = new JLabel("Tổng tiền hàng:");
		lblTong.setFont(new Font("Dialog", Font.PLAIN, 16));
		lblTong.setBounds(10, 158, 113, 20);
		pnRight.add(lblTong);

		JLabel lblTongValue = new JLabel("3,200,000 vnd");
		lblTongValue.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTongValue.setFont(new Font("Dialog", Font.PLAIN, 16));
		lblTongValue.setBounds(170, 158, 113, 20);
		pnRight.add(lblTongValue);

		// ==== Nút nhập phiếu ====
		PillButton btnNhapPhieu = new PillButton("Nhập phiếu");
		btnNhapPhieu.setFont(new Font("Segoe UI", Font.BOLD, 25));
		btnNhapPhieu.setBounds(20, 217, 263, 100);
		btnNhapPhieu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		pnRight.add(btnNhapPhieu);

		// ==== Quay lại ====
		JLabel lblQuayLai = new JLabel("Quay lại");
		lblQuayLai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		lblQuayLai.setForeground(Color.RED);
		lblQuayLai.setBounds(222, 316, 61, 38);
		lblQuayLai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		pnRight.add(lblQuayLai);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Khung trống - clone base");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new ThemPhieuNhap_GUI());
			frame.setVisible(true);
		});
	}
}
