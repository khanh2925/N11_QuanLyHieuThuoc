package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import customcomponent.ClipTooltipRenderer;
import customcomponent.PillButton;
import customcomponent.RoundedBorder;

public class KhuyenMai_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private PillButton btnThem;
	private PillButton btnCapNhat;
	private DefaultTableModel modelKM;
	private JTable tblKM;
	private JScrollPane scrKM;
	private Color blueMint = new Color(33, 150, 243);
	private Color pinkPastel = new Color(255, 200, 220);

	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,###đ");
	private JTextField textField;

	public KhuyenMai_GUI() {
		this.setPreferredSize(new Dimension(1537, 850));
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(1537, 1168));

		// ===== HEADER =====
		pnHeader = new JPanel();
		pnHeader.setPreferredSize(new Dimension(1073, 88));
		pnHeader.setBackground(new Color(0xE3F2F5));
		add(pnHeader, BorderLayout.NORTH);

		btnThem = new PillButton("Thêm");
		btnThem.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnThem.setLocation(374, 25);
		btnThem.setSize(120, 40);

		btnCapNhat = new PillButton("Cập nhật");
		btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnCapNhat.setLocation(549, 25);
		btnCapNhat.setSize(120, 40);

		pnHeader.setLayout(null);
		pnHeader.add(btnThem);
		pnHeader.add(btnCapNhat);

		textField = new JTextField("");
		textField.setForeground(Color.GRAY);
		textField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		textField.setBorder(new RoundedBorder(20));
		textField.setBounds(10, 17, 336, 60);
		pnHeader.add(textField);

		// ===== CENTER =====
		pnCenter = new JPanel();
		pnCenter.setBackground(new Color(255, 128, 192));
		add(pnCenter, BorderLayout.CENTER);
		initTable();
		LoadKhuyenMai(); // nạp data fake
	}

	private void initTable() {
		// Bảng khuyến mãi
		String[] khuyenMaiCols = { "Mã khuyến mãi", "Tên khuyến mãi", "Hình thức", "Giá trị", "Ngày bắt đầu",
				"Ngày kết thúc", "Loại khuyến mãi", "Điều kiện áp dụng", "Trạng thái" };
		modelKM = new DefaultTableModel(khuyenMaiCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		pnCenter.setLayout(new BorderLayout(0, 0));
		tblKM = new JTable(modelKM);
		scrKM = new JScrollPane(tblKM);
		pnCenter.add(scrKM);
		formatTable(tblKM);
		tblKM.setSelectionBackground(pinkPastel);
		tblKM.getTableHeader().setBackground(blueMint);

		// Render ảnh trên table (không dùng ảnh nhưng giữ renderer để không lỗi)
		tblKM.setRowHeight(55);
		tblKM.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public void setValue(Object value) {
				if (value instanceof ImageIcon) {
					setIcon((ImageIcon) value);
					setText("");
				} else {
					setIcon(null);
					setText(value == null ? "" : value.toString());
				}
			}
		});

		// Tooltip cho các cột còn lại
		for (int col = 1; col < tblKM.getColumnCount(); col++) {
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
			else if (col.contains("ngày") || col.contains("ảnh")) {
				m.getColumn(i).setCellRenderer(centerRenderer);
			} else
				m.getColumn(i).setCellRenderer(leftRenderer);
		}

		table.getTableHeader().setReorderingAllowed(false);
	}

	/** Nạp DATA FAKE (không đụng tới entity/enum) */
	public void LoadKhuyenMai() {
		// hinhThuc: "GIAM_GIA_PHAN_TRAM" | "GIAM_GIA_TIEN" | "TANG_THEM"
		// loaiKm: "Khuyến mãi hoá đơn" | "Khuyến mãi sản phẩm"
		Object[][] data = {
				{ "KM-20251022-0001", "Giảm 10% toàn bộ hóa đơn", "GIAM_GIA_PHAN_TRAM", 10.0, LocalDate.of(2025, 10, 22),
						LocalDate.of(2025, 11, 30), true, "Áp dụng mọi đơn hàng", true },
				{ "KM-20251101-0002", "Giảm 5% cho đơn từ 300.000đ", "GIAM_GIA_PHAN_TRAM", 5.0,
						LocalDate.of(2025, 11, 1), LocalDate.of(2025, 11, 30), true, "Đơn hàng >= 300000đ", true },
				{ "KM-20251001-0003", "Giảm 20.000đ cho đơn từ 200.000đ", "GIAM_GIA_TIEN", 20000.0,
						LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 31), true, "Đơn hàng >= 200000đ", false },
				{ "KM-20251201-0004", "Giảm 15% nhóm Vitamin", "GIAM_GIA_PHAN_TRAM", 15.0, LocalDate.of(2025, 12, 1),
						LocalDate.of(2025, 12, 15), false, "Chỉ áp dụng nhóm Vitamin/TP chức năng", true },
				{ "KM-20251025-0005", "Mua 2 tặng 1 – Paracetamol 500mg", "TANG_THEM", "2->1",
						LocalDate.of(2025, 10, 25), LocalDate.of(2025, 11, 25), false,
						"Áp dụng riêng Paracetamol 500mg", true },
				{ "KM-20251105-0006", "Mua 5 tặng 2 – Salonpas", "TANG_THEM", "5->2", LocalDate.of(2025, 11, 5),
						LocalDate.of(2025, 11, 20), false, "Áp dụng khi mua hộp Salonpas", true },
				{ "KM-20251020-0007", "Giảm 5.000đ – Nước muối 0.9%", "GIAM_GIA_TIEN", 5000.0,
						LocalDate.of(2025, 10, 20), LocalDate.of(2025, 11, 10), false,
						"Nước muối 0.9% chai 500ml", true },
				{ "KM-20250901-0008", "Giảm 7% cho nhóm giảm đau", "GIAM_GIA_PHAN_TRAM", 7.0, LocalDate.of(2025, 9, 1),
						LocalDate.of(2025, 9, 30), false, "Hết hạn", false },
				{ "KM-20251210-0009", "Giảm 30.000đ đơn từ 500.000đ", "GIAM_GIA_TIEN", 30000.0,
						LocalDate.of(2025, 12, 10), LocalDate.of(2026, 1, 10), true, "Đơn hàng >= 500000đ", true } };

		modelKM.setRowCount(0);
		for (Object[] r : data) {
			String hinhThucStr = r[2].toString();
			Object rawGiaTri = r[3];

			String hinhThucHienThi;
			String giaTriHienThi;

			if ("GIAM_GIA_PHAN_TRAM".equals(hinhThucStr)) {
				hinhThucHienThi = "Giảm giá phần trăm";
				giaTriHienThi = ((Number) rawGiaTri).intValue() + "%";
			} else if ("GIAM_GIA_TIEN".equals(hinhThucStr)) {
				hinhThucHienThi = "Giảm giá tiền";
				giaTriHienThi = df.format(((Number) rawGiaTri).doubleValue());
			} else { // TANG_THEM
				hinhThucHienThi = "Tặng thêm";
				giaTriHienThi = rawGiaTri.toString(); // ví dụ "2->1", "5->2"
			}

			String loaiKm = ((Boolean) r[6]) ? "Khuyến mãi hoá đơn" : "Khuyến mãi sản phẩm";
			String trangThai = ((Boolean) r[8]) ? "Đang áp dụng" : "Hết hạn";

			modelKM.addRow(new Object[] { r[0], // mã
					r[1], // tên
					hinhThucHienThi, // hình thức hiển thị
					giaTriHienThi, // giá trị hiển thị
					((LocalDate) r[4]).format(fmt), // ngày bắt đầu
					((LocalDate) r[5]).format(fmt), // ngày kết thúc
					loaiKm, // loại khuyến mãi
					r[7], // điều kiện áp dụng
					trangThai // trạng thái
			});
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Khuyến mãi - Data Fake");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new KhuyenMai_GUI());
			frame.setVisible(true);
		});
	}
}
