/**
 * @author Quốc Khánh
 * @version 3.0
 * @since Oct 16, 2025
 *
 * Mô tả: Khung giao diện trống - giữ lại bố cục chính để clone trang khác.
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
import dao.*;
import entity.NhanVien;
import entity.PhieuNhap;

public class QLTraHang_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private JPanel pnRight; // vùng cột phải
	private JButton btnThem;
	private JButton btnXuatFile;
	private JTextField txtSearch;
	private DefaultTableModel modelPT;
	private JTable tblPT;
	private JScrollPane scrCTPT;
	private DefaultTableModel modelCTPT;
	private JScrollPane scrPT;
	private JTable tblCTPT;
	private PillButton btnHuyHang;
	private PillButton btnNhapKho;


	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,000.#đ");

	private Color blueMint = new Color(180, 220, 240);
	private Color pinkPastel = new Color(255, 200, 220);

	public QLTraHang_GUI() {
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

		String placeholder = "Tìm kiếm";
		JTextField txtSearch = new JTextField() {
		    @Override
		    protected void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) return;
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
//		txtSearch.setText("Tìm phiếu nhập theo sđt, NCC, tên...");

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

		btnThem = new PillButton("Thêm");
		btnThem.setSize(100, 30);
		btnThem.setLocation(620, 25);
		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setSize(100, 30);
		btnXuatFile.setLocation(736, 25);

		pnHeader.add(txtSearch);
		pnHeader.add(txtDateRange);
		pnHeader.add(btnThem);
		pnHeader.add(btnXuatFile);
		
		btnNhapKho = new PillButton("Nhập lại kho");
		btnNhapKho.setBounds(857, 25, 130, 30);
		pnHeader.add(btnNhapKho);
		
		btnHuyHang = new PillButton("Hủy hàng");
		btnHuyHang.setBounds(1010, 25, 100, 30);
		pnHeader.add(btnHuyHang);

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
	}

	private void initTable() {
		// Bảng phiếu nhập
		String[] phieuTraCols = { "Mã PT", "Người bán", "Người trả", "Mã HD","Thời gian","Khách hàng", "Tổng tiền","Trạng thái" };
		modelPT = new DefaultTableModel(phieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblPT = new JTable(modelPT);
		scrPT = new JScrollPane(tblPT);
		pnCenter.add(scrPT);
		// Bảng chi tiết phiếu nhập
		String[] cTPhieuTraCols = { "Ngày lập phiếu", "Tên hàng", "Số lượng", "Thành tiền", "Lý do trả", "Trạng thái" };

		modelCTPT = new DefaultTableModel(cTPhieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPT = new JTable(modelCTPT);
		scrCTPT = new JScrollPane(tblCTPT);
		pnRight.add(scrCTPT);

		formatTable(tblPT);
		tblPT.setSelectionBackground(blueMint);
		tblPT.getTableHeader().setBackground(pinkPastel);
		formatTable(tblCTPT);
		tblCTPT.setSelectionBackground(pinkPastel);
		tblCTPT.getTableHeader().setBackground(blueMint);
		
		modelPT.addRow(new Object[]{"PT000001", "Chu Anh Khôi","Chu Anh Khôi", "HD-20251003-0001", "2025-10-15", "Công ty Minh Tâm", "1.250.000", "Đã xử lý"});
		modelPT.addRow(new Object[]{"PT000002", "Chu Anh Khôi","Chu Anh Khôi", "HD-20251003-0002", "2025-10-15", "Nguyễn Thị Hoa",   "820.000",   "Chờ duyệt"});
		modelPT.addRow(new Object[]{"PT000003", "Chu Anh Khôi","Chu Anh Khôi", "HD-20251003-0003", "2025-10-16", "Phạm Anh Khoa",    "560.000",   "Đã xử lý"});

		
		
		modelCTPT.addRow(new Object[]{"2025-10-16", "Thuốc ho", 2, "300.000", "Sai thuốc", "Đã xử lý"});
		modelCTPT.addRow(new Object[]{"2025-10-16", "Băng cá nhân",  1, "10.000", "Thiếu 1 băng", "Đã xử lý"});
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
			frame.setContentPane(new QLTraHang_GUI());
			frame.setVisible(true);
		});
	}
}
