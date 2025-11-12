package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.toedter.calendar.JDateChooser;
import customcomponent.PillButton;
import customcomponent.PlaceholderSupport;
import customcomponent.RoundedBorder;
import dao.ChiTietPhieuTra_DAO;
import dao.PhieuTra_DAO;
import entity.ChiTietPhieuTra;
import entity.KhuyenMai;
import entity.PhieuTra;

public class QLTraHang_GUI extends JPanel {

	private JPanel pnCenter; // vùng trung tâm
	private JPanel pnHeader; // vùng đầu trang
//	private JPanel pnRight; // vùng cột phải
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
	private JDateChooser dateTu;
	private JDateChooser dateDen;

	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");	
	private final DecimalFormat df = new DecimalFormat("#,##0.#'đ'");

	private final Color blueMint = new Color(180, 220, 240);
	private final Color pinkPastel = new Color(255, 200, 220);

	private PhieuTra_DAO phieuTraDAO = new PhieuTra_DAO();
	private ChiTietPhieuTra_DAO chiTietPhieuTraDAO = new ChiTietPhieuTra_DAO();
	private JSplitPane splitPane;

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

		txtSearch = new JTextField();
		txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		txtSearch.setBounds(10, 17, 420, 60);
		txtSearch.setBorder(new RoundedBorder(20));
		txtSearch.setBackground(Color.WHITE);
		PlaceholderSupport.addPlaceholder(txtSearch, "Tìm theo mã/tên ...");

		btnXuatFile = new PillButton("Xuất file");
		btnXuatFile.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnXuatFile.setSize(120, 40);
		btnXuatFile.setLocation(946, 30);

		pnHeader.add(txtSearch);
		pnHeader.add(btnXuatFile);

		btnNhapKho = new PillButton("Nhập lại kho");
		btnNhapKho.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnNhapKho.setBounds(1090, 26, 150, 40);
		pnHeader.add(btnNhapKho);

		btnHuyHang = new PillButton("Hủy hàng");
		btnHuyHang.setFont(new Font("Segoe UI", Font.BOLD, 18));
		btnHuyHang.setBounds(1260, 26, 120, 40);
		pnHeader.add(btnHuyHang);
		
		JLabel lblTuNgay = new JLabel("Từ ngày:");
		lblTuNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblTuNgay.setBounds(478, 30, 90, 40);
		pnHeader.add(lblTuNgay);

		dateTu = new JDateChooser();
		dateTu.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateTu.setDateFormatString("dd/MM/yyyy");
		dateTu.setBounds(560, 35, 130, 30);
		pnHeader.add(dateTu);

		JLabel lblDenNgay = new JLabel("Đến:");
		lblDenNgay.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		lblDenNgay.setBounds(743, 26, 80, 40);
		pnHeader.add(lblDenNgay);

		dateDen = new JDateChooser();
		dateDen.setFont(new Font("Segoe UI", Font.PLAIN, 18));
		dateDen.setDateFormatString("dd/MM/yyyy");
		dateDen.setBounds(780, 35, 130, 30);
		pnHeader.add(dateDen);

		// ===== CENTER =====
		pnCenter = new JPanel(new BorderLayout());
		add(pnCenter, BorderLayout.CENTER);

		initTable();
		
	    btnNhapKho.addActionListener(e -> capNhatTrangThai(1));
	    btnHuyHang.addActionListener(e -> capNhatTrangThai(2));
	}

	private void initTable() {
		String[] phieuTraCols = { "Mã PT", "Khách hàng", "Người trả", "Ngày lập", "Trạng thái", "Tổng tiền hoàn" };
		modelPT = new DefaultTableModel(phieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblPT = new JTable(modelPT) {
		    @Override
		    public String getToolTipText(MouseEvent e) {
		        int row = rowAtPoint(e.getPoint());
		        int col = columnAtPoint(e.getPoint());
		        if (row < 0 || col < 0) return null;
		        Object v = getValueAt(row, col);
		        if (v == null) return null;

		        TableCellRenderer r = getCellRenderer(row, col);
		        Component comp = prepareRenderer(r, row, col);
		        int pref = comp.getPreferredSize().width;
		        int colW = getColumnModel().getColumn(col).getWidth();
		        // chỉ hiển thị tooltip khi bị cắt
		        return (pref > colW - 4) ? v.toString() : null;
		    }
		};
		scrPT = new JScrollPane(tblPT);

		String[] cTPhieuTraCols = { "Mã hoá đơn", "Mã lô", "Số lượng", "Giá bán", "Khuyến mãi", "Lý do trả", "Thành tiền", "Trạng thái" };
		modelCTPT = new DefaultTableModel(cTPhieuTraCols, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		tblCTPT = new JTable(modelCTPT) {
		    @Override
		    public String getToolTipText(MouseEvent e) {
		        int row = rowAtPoint(e.getPoint());
		        int col = columnAtPoint(e.getPoint());
		        if (row < 0 || col < 0) return null;
		        Object v = getValueAt(row, col);
		        if (v == null) return null;

		        TableCellRenderer r = getCellRenderer(row, col);
		        Component comp = prepareRenderer(r, row, col);
		        int pref = comp.getPreferredSize().width;
		        int colW = getColumnModel().getColumn(col).getWidth();
		        return (pref > colW - 4) ? v.toString() : null;
		    }
		};
		scrCTPT = new JScrollPane(tblCTPT);

		formatTable(tblPT);
		tblPT.setSelectionBackground(blueMint);
		tblPT.getTableHeader().setBackground(pinkPastel);

		formatTable(tblCTPT);
		tblCTPT.setSelectionBackground(pinkPastel);
		tblCTPT.getTableHeader().setBackground(blueMint);

		// === TẠO SPLITPANE NGANG ===
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrPT, scrCTPT);
		splitPane.setResizeWeight(0.4); // 40% - 60%
		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		
		
		pnCenter.add(splitPane);
		
		loadPhieuTraData();

		tblPT.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tblPT.getSelectedRow();
				if (row >= 0) {
					String maPT = modelPT.getValueAt(row, 0).toString();
					loadChiTietPhieuTra(maPT);
				}
			}
		});
	}
	
	private void capNhatTrangThai(int trangThai) {
	    int row = tblCTPT.getSelectedRow();
	    if (row == -1) {
	        JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng chi tiết để cập nhật!");
	        return;
	    }

	    String maPhieuTra = (String) tblPT.getValueAt(tblPT.getSelectedRow(), 0);
	    String maHoaDon = (String) tblCTPT.getValueAt(row, 0);
	    String maLo = (String) tblCTPT.getValueAt(row, 1);

	    boolean ok = chiTietPhieuTraDAO.capNhatTrangThaiChiTiet(maPhieuTra, maHoaDon, maLo, trangThai);
	    if (ok) {
	        JOptionPane.showMessageDialog(this, (trangThai == 1 ? "Đã nhập lại kho." : "Đã huỷ hàng."));
	        loadChiTietPhieuTra(maPhieuTra);
	    } else {
	        JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
	    }
	}

	
	private void loadPhieuTraData() {
		modelPT.setRowCount(0); // clear bảng
		List<PhieuTra> dsPhieuTra = phieuTraDAO.layTatCaPhieuTra();

		for (PhieuTra pt : dsPhieuTra) {
			String maPT = pt.getMaPhieuTra();
			String khachHang = pt.getKhachHang() != null ? pt.getKhachHang().getTenKhachHang() : "Không rõ";
			String nguoiTra = khachHang; // vì người trả là khách hàng
			String ngayLap = dtf.format(pt.getNgayLap());
			String trangThai = pt.isDaDuyet() ? "Đã xử lý" : "Chờ duyệt";
			String tongTien = df.format(pt.getTongTienHoan());

			// ✅ Thêm đúng theo thứ tự 6 cột của bạn
			modelPT.addRow(new Object[] { maPT, khachHang, nguoiTra, ngayLap, trangThai, tongTien });
		}
	}

	private void loadChiTietPhieuTra(String maPhieuTra) {
		modelCTPT.setRowCount(0);
		List<ChiTietPhieuTra> dsCT = chiTietPhieuTraDAO.timKiemChiTietBangMaPhieuTra(maPhieuTra);

		for (ChiTietPhieuTra ct : dsCT) {
			String maHD = ct.getChiTietHoaDon().getHoaDon().getMaHoaDon();
			String maLo = ct.getChiTietHoaDon().getLoSanPham().getMaLo();
			int soLuong = ct.getSoLuong();
			String giaBan = df.format(ct.getChiTietHoaDon().getGiaBan());

			KhuyenMai km = ct.getChiTietHoaDon().getKhuyenMai();
			String khuyenMai = "";
			if (km != null) {
				khuyenMai = ct.getChiTietHoaDon().getKhuyenMai().getTenKM();
			} else khuyenMai = "Không có";
			String lyDo = ct.getLyDoChiTiet();
			String thanhTien = df.format(ct.getThanhTienHoan());
			String trangThai = ct.getTrangThaiText();
			
			modelCTPT.addRow(new Object[] { maHD, maLo, soLuong, giaBan, khuyenMai, lyDo, thanhTien, trangThai });
		}
	}

	private void formatTable(JTable table) {
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
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
			else if (col.contains("ngày"))
				m.getColumn(i).setCellRenderer(centerRenderer);
			else
				m.getColumn(i).setCellRenderer(leftRenderer);
		}
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
