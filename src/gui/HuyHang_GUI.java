/**
 * @author Thanh Kha
 * @version 1.1
 * @since Oct 27, 2025
 *
 * Mô tả: Giao diện quản lý phiếu huỷ hàng (data tự sinh, không dùng entity)
 */

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

import com.toedter.calendar.JDateChooser;

import customcomponent.*;

public class HuyHang_GUI extends JPanel {

	private JPanel pnCenter;
	private JPanel pnHeader;
	private JPanel pnRight;
	private JButton btnXuatFile;
	private JTextField txtSearch;
	private DefaultTableModel modelPH;
	private JTable tblPH;
	private JScrollPane scrCTPH;
	private DefaultTableModel modelCTPH;
	private JScrollPane scrPH;
	private JTable tblCTPH;

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,###đ");

	private Color blueMint = new Color(180, 220, 240);
	private Color pinkPastel = new Color(255, 200, 220);
	private JDateChooser dateTu;
	private JDateChooser dateDen;

	public HuyHang_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setLayout(null);
		add(pnHeader, BorderLayout.NORTH);

		txtSearch = new JTextField();
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		txtSearch.setBounds(10, 17, 420, 60);
		txtSearch.setBorder(new RoundedBorder(20));
		txtSearch.setBackground(Color.WHITE);

		PlaceholderSupport.addPlaceholder(txtSearch, "Tìm theo mã/tên ...");

		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnXuatFile.setSize(120, 40);
		btnXuatFile.setLocation(957, 30);

		pnHeader.add(txtSearch);
		pnHeader.add(btnXuatFile);

		JLabel lblTuNgay = new JLabel("Từ ngày:");
		lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblTuNgay.setBounds(468, 30, 78, 40);
		pnHeader.add(lblTuNgay);

		dateTu = new JDateChooser();
		dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateTu.setDateFormatString("dd/MM/yyyy");
		dateTu.setBounds(550, 30, 130, 35);
		pnHeader.add(dateTu);

		JLabel lblDenNgay = new JLabel("Đến:");
		lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblDenNgay.setBounds(700, 33, 50, 25);
		pnHeader.add(lblDenNgay);

		dateDen = new JDateChooser();
		dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateDen.setDateFormatString("dd/MM/yyyy");
		dateDen.setBounds(750, 30, 130, 35);
		pnHeader.add(dateDen);

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
		loadFakeData();
	}

	private void initTable() {
		// Bảng phiếu huỷ
		String[] phieuHuyCols = { "Mã PH", "Ngày lập phiếu", "Nhân viên", "Tổng dòng huỷ", "Trạng thái" };
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
		String[] cTPhieuCols = { "Mã PH", "Mã lô", "Mã SP", "Tên SP", "SL huỷ", "Đơn vị tính", "Hạn sử dụng", "Lý do" };

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

	// Fake data không liên quan entity
	private void loadFakeData() {
		// ====== PHIẾU HỦY ======
		Object[][] phieuHuyData = {
				{ "PH-0001", LocalDate.of(2025, 10, 20), "Nguyễn Văn A", 2, "Đã duyệt" },
				{ "PH-0002", LocalDate.of(2025, 10, 22), "Trần Thị B", 1, "Chờ duyệt" },
				{ "PH-0003", LocalDate.of(2025, 10, 23), "Lê Quốc C", 3, "Đã duyệt" } };

		modelPH.setRowCount(0);
		for (Object[] row : phieuHuyData) {
			modelPH.addRow(new Object[] { row[0], ((LocalDate) row[1]).format(fmt), row[2], row[3], row[4] });
		}

		// ====== CHI TIẾT PHIẾU HỦY ======
		Object[][] ctPhieuHuyData = {
				{ "PH-0001", "LO-001", "SP-001", "Paracetamol 500mg", 10, "Viên", "06/2026",
						"Viên bị ẩm, mốc" },
				{ "PH-0001", "LO-002", "SP-002", "Vitamin C 1000mg", 5, "Viên", "12/2026",
						"Vỏ bị rách" },
				{ "PH-0002", "LO-003", "SP-003", "Efferalgan 500mg", 20, "Viên", "03/2026",
						"Cận hạn sử dụng" },
				{ "PH-0003", "LO-004", "SP-004", "Bông y tế", 50, "Gói", "09/2025", "Bị ướt" },
				{ "PH-0003", "LO-005", "SP-005", "Cồn 70 độ", 30, "Chai", "10/2026", "Vỡ nắp chai" } };

		modelCTPH.setRowCount(0);
		for (Object[] row : ctPhieuHuyData) {
			modelCTPH.addRow(row);
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quản lý phiếu hủy hàng - Data Fake");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new HuyHang_GUI());
			frame.setVisible(true);
		});
	}
}
