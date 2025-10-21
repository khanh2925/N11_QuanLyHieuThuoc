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

import com.toedter.calendar.JDateChooser;

import customcomponent.NhapHangItemRow;
import customcomponent.PillButton;


public class ThemPhieuNhap_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private JPanel pnRight; // vùng cột phải
	private JScrollPane scrNhapHangItems;

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

		String placeholder = "Tìm kiếm";
		JTextField txtSearch = new JTextField() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (placeholder == null || placeholder.length() == 0 || getText().length() > 0)
					return;
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getDisabledTextColor());

				FontMetrics fm = g2.getFontMetrics();
				int textY = getHeight() / 2 + fm.getAscent() / 2 - 2;
				g2.drawString(placeholder, getInsets().left, textY);
			}
		};
		txtSearch.setSize(340, 65);
		txtSearch.setLocation(10, 10);
		txtSearch.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		txtSearch.setFont(new Font("Segoe UI", Font.BOLD, 20));

		JTextField txtDateRange = new JTextField();
		txtDateRange.setSize(250, 65);
		txtDateRange.setLocation(360, 10);
		txtDateRange.setEditable(false);
		txtDateRange.setText("Chọn ngày");
		txtDateRange.setFont(new Font("Segoe UI", Font.BOLD, 15));
		txtDateRange.setHorizontalAlignment(SwingConstants.CENTER);
		txtDateRange.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFrame frame = new JFrame();
				frame.getContentPane().setLayout(new FlowLayout());

				JDateChooser startChooser = new JDateChooser();
				JDateChooser endChooser = new JDateChooser();
				JButton btnOk = new JButton("Chọn");

				frame.getContentPane().add(new JLabel("Từ ngày:"));
				frame.getContentPane().add(startChooser);
				frame.getContentPane().add(new JLabel("Đến ngày:"));
				frame.getContentPane().add(endChooser);
				frame.getContentPane().add(btnOk);

				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);

				btnOk.addActionListener(ev -> {
					Date start = startChooser.getDate();
					Date end = endChooser.getDate();
					if (start != null && end != null) {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						txtDateRange.setText(sdf.format(start) + " - " + sdf.format(end));
					}
					frame.dispose();
				});
			}
		});

		PillButton btnThem = new PillButton("Thêm");
		btnThem.setSize(100, 30);
		btnThem.setLocation(620, 25);
		PillButton btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setSize(100, 30);
		btnXuatFile.setLocation(730, 25);

		pnHeader.add(txtSearch);
		pnHeader.add(txtDateRange);
		pnHeader.add(btnThem);
		pnHeader.add(btnXuatFile);

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
		pnSearch.setBounds(0, 50, 300, 28);
		JTextField txtTimNCC = new JTextField();
		txtTimNCC.setBounds(0, 0, 300, 28);
		txtTimNCC.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		txtTimNCC.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createLineBorder(new Color(180,180,180), 1, true),
		        BorderFactory.createEmptyBorder(0, 28, 0, 5)
		));
		pnSearch.add(txtTimNCC, Integer.valueOf(0)); // lớp dưới cùng

		// Icon search (đè lên bên trái)
		JLabel iconSearch = new JLabel(new ImageIcon(getClass().getResource("/images/icon_tra_cuu.png")));
		iconSearch.setBounds(6, 4, 20, 20);
		pnSearch.add(iconSearch, Integer.valueOf(1)); // lớp cao hơn text
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
//		btnNhapPhieu.setForeground(Color.BLACK);
//		btnNhapPhieu.setFocusPainted(false);
		btnNhapPhieu.setBounds(20, 217, 263, 100);
//		btnNhapPhieu.setBackground(new Color(240, 240, 240));
//		btnNhapPhieu.setBorder(BorderFactory.createEmptyBorder());
		btnNhapPhieu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		// hiệu ứng gradient nhẹ
//		btnNhapPhieu.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
//		    @Override
//		    public void paint(Graphics g, JComponent c) {
//		        Graphics2D g2 = (Graphics2D) g.create();
//		        GradientPaint gp = new GradientPaint(0, 0, new Color(250, 250, 250),
//		                                             c.getWidth(), 0, new Color(245, 200, 200));
//		        g2.setPaint(gp);
//		        g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);
//		        g2.dispose();
//		        super.paint(g, c);
//		    }
//		});
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
			JFrame frame = new JFrame("Thêm phiếu nhập");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(1280, 800);
			frame.setLocationRelativeTo(null);
			frame.setContentPane(new ThemPhieuNhap_GUI());
			frame.setVisible(true);
		});
	}
}
