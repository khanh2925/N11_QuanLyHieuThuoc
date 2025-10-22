/**
 * @author Quốc Khánh
 * @version 3.0
 * @since Oct 16, 2025
 *
 * Mô tả: Khung giao diện trống - giữ lại bố cục chính để clone trang khác.
 */

package gui;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.toedter.calendar.JDateChooser;
import customcomponent.*;
import entity.NhanVien;
import entity.PhieuNhap;

public class DonHang_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
	private JPanel pnRight; // vùng cột phải
	private JButton btnXuatFile;
	private JTextField txtSearch;
	private DefaultTableModel modelHD;
	private JTable tblHD;
	private JScrollPane scrCTHD;
	private DefaultTableModel modelCTHD;
	private JScrollPane scrHD;
	private JTable tblCTHD;
	private JDateChooser dateTu;
	private JDateChooser dateDen;


	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	DecimalFormat df = new DecimalFormat("#,000.#đ");

	private Color blueMint = new Color(180, 220, 240);
	private Color pinkPastel = new Color(255, 200, 220);

	public DonHang_GUI() {
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

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSearch.setBounds(10, 10, 342, 68);
        txtSearch.setBorder(new LineBorder(new Color(0x00C0E2), 2,true));
        txtSearch.setBackground(Color.WHITE);


        PlaceholderSupport.addPlaceholder(txtSearch, "Tìm theo mã/tên ...");

		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setSize(100, 30);
		btnXuatFile.setLocation(801, 30);

		pnHeader.add(txtSearch);
		pnHeader.add(btnXuatFile);
		
		JLabel lblTuNgay = new JLabel("Từ ngày:");
		lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblTuNgay.setBounds(360, 30, 60, 25);
		pnHeader.add(lblTuNgay);
		
		dateTu = new JDateChooser();
		dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		dateTu.setDateFormatString("dd/MM/yyyy");
		dateTu.setBounds(427, 30, 130, 25);
		pnHeader.add(dateTu);
		
		JLabel lblDenNgay = new JLabel("Đến:");
		lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblDenNgay.setBounds(588, 30, 40, 25);
		pnHeader.add(lblDenNgay);
		
		dateDen = new JDateChooser();
		dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		dateDen.setDateFormatString("dd/MM/yyyy");
		dateDen.setBounds(635, 30, 130, 25);
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
	}

	private void initTable() {
		// Bảng Danh sach hoa don
		String[] HDCols = { "Mã HD", "Khách hàng", "Ngày lập","Tổng tiền", "Nhân Viên","Khuyến mãi","Thuốc theo đơn"};
		modelHD = new DefaultTableModel(HDCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblHD = new JTable(modelHD);
		scrHD = new JScrollPane(tblHD);
		pnCenter.add(scrHD);
		// Bảng chi tiết hóa đơn
		String[] CTHDCols = { "Sản phẩm", "Số lượng", "Khuyến mãi", "Thành tiền",  };

		modelCTHD = new DefaultTableModel(CTHDCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTHD = new JTable(modelCTHD);
		scrCTHD = new JScrollPane(tblCTHD);
		pnRight.add(scrCTHD);

		formatTable(tblHD);
		tblHD.setSelectionBackground(blueMint);
		tblHD.getTableHeader().setBackground(pinkPastel);
		formatTable(tblCTHD);
		tblCTHD.setSelectionBackground(pinkPastel);
		tblCTHD.getTableHeader().setBackground(blueMint);
		
		modelHD.addRow(new Object[]{"HD-20251003-0001", "Chu Anh Khôi", "2025-10-15","1.250.000",  "",  "Theo đơn"});
		modelHD.addRow(new Object[]{"HD-20251003-0002", "Chu Anh Khôi", "2025-10-15","820.000",    "",  "Theo đơn"});
		modelHD.addRow(new Object[]{"HD-20251003-0003", "Chu Anh Khôi", "2025-10-16","560.000",    "",  "Không theo đơn"});

		
		
		modelCTHD.addRow(new Object[]{"Thuốc ho", 2, "", "300.000", });
		modelCTHD.addRow(new Object[]{ "Băng cá nhân",  1, "" ,"10.000" });
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
			frame.setContentPane(new DonHang_GUI());
			frame.setVisible(true);
		});
	}
}
