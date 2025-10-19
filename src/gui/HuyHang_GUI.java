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
import java.util.Date;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import com.toedter.calendar.JDateChooser;
import customcomponent.*;
import dao.*;
import dao.ChiTietPhieuHuy_DAO.CTPHView;
import dao.PhieuHuy_DAO.PhieuHuyView;
import entity.NhanVien;
import entity.PhieuHuy;
import entity.PhieuNhap;

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
	private DefaultTableModel modelCTPh;
	private JScrollPane scrPH;
	private JTable tblCTPH;

	private final PhieuHuy_DAO phDAO = new PhieuHuy_DAO();
	private final ChiTietPhieuHuy_DAO ctphDAO = new ChiTietPhieuHuy_DAO();
	private NhanVien_DAO nvDAO = new NhanVien_DAO();

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
		btnXuatFile.setLocation(730, 25);

		pnHeader.add(txtSearch);
		pnHeader.add(txtDateRange);
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
		initMasterDetail();
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

		modelCTPh = new DefaultTableModel(cTPhieuCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPH = new JTable(modelCTPh);
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
		try {
			modelPH.setRowCount(0);
			List<PhieuHuyView> list = phDAO.findAll();

			for (PhieuHuyView x : list) {
				String ngay = (x.getNgayLap() != null) ? sdf.format(java.sql.Date.valueOf(x.getNgayLap())) : "";
				String tenNV = x.getTenNhanVien();
				int soDong = x.getSoDongChiTiet();
				boolean trangThai = x.getTrangThai();
				modelPH.addRow(new Object[] { x.getMaPhieuHuy(), ngay, tenNV, soDong, trangThai});
			}

			// Chọn dòng đầu và load chi tiết
			if (modelPH.getRowCount() > 0) {
				tblPH.setRowSelectionInterval(0, 0);
				String maPN = String.valueOf(modelPH.getValueAt(0, 0));
				loadChiTiet(maPN);
			} else {
				modelCTPh.setRowCount(0);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			javax.swing.JOptionPane.showMessageDialog(this, "Lỗi tải Phiếu nhập: " + ex.getMessage());
		}
	}

	private void loadChiTiet(String maPhieuHuy) {
		try {
			modelCTPh.setRowCount(0);
			// dùng view-model có TenSanPham
			List<CTPHView> list = ctphDAO.findByMaPhieu(maPhieuHuy);
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

			for (CTPHView v : list) {
				modelCTPh.addRow(new Object[] { v.getMaLo(), v.getMaSanPham(), v.getTenSanPham(), v.getSoLuongHuy(),
						v.getTenDonViTinh(), v.getHanSuDung(), v.getLyDo()});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			javax.swing.JOptionPane.showMessageDialog(this, "Lỗi tải Chi tiết: " + ex.getMessage());
		}
	}

	private void initMasterDetail() {
		tblPH.getSelectionModel().addListSelectionListener(e -> {
			if (e.getValueIsAdjusting())
				return; // tránh bắn 2 lần
			int viewRow = tblPH.getSelectedRow();
			if (viewRow < 0)
				return;
			int modelRow = tblPH.convertRowIndexToModel(viewRow);
			String maPH = String.valueOf(modelPH.getValueAt(modelRow, 0));
			loadChiTiet(maPH);
		});
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
