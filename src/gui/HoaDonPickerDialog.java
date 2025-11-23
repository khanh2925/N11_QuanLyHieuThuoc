package gui;

import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.ChiTietHoaDon_DAO;
import entity.HoaDon;
import entity.KhachHang;
import entity.ChiTietHoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class HoaDonPickerDialog extends JDialog {
	private final JTable tblHoaDon;
	private final JTable tblChiTiet;
	private final DefaultTableModel modelHD;
	private final DefaultTableModel modelCT;

	private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
	private final ChiTietHoaDon_DAO cthdDAO = new ChiTietHoaDon_DAO();
	private final KhachHang_DAO khDAO = new KhachHang_DAO();

	private String selectedMaHD = null;

	public HoaDonPickerDialog(Window owner, String soDT) {
		super(owner, "", ModalityType.APPLICATION_MODAL);
		KhachHang currentKH = khDAO.timKhachHangTheoSoDienThoai(soDT);
		setTitle("Chọn hoá đơn của khách hàng: " + currentKH.getTenKhachHang());
		setSize(900, 520);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout(8, 8));

		// top tìm lại (readonly hiển thị)
		JPanel top = new JPanel(new BorderLayout());
		top.add(new JLabel("Số điện thoại: " + soDT), BorderLayout.WEST);
		add(top, BorderLayout.NORTH);

		// Bảng hóa đơn (trái)
		modelHD = new DefaultTableModel(
				new String[] { "Mã HĐ", "Ngày lập", "Nhân viên", "Khách hàng", "Tổng tiền", "Thanh toán" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblHoaDon = new JTable(modelHD) {
			@Override
			public String getToolTipText(MouseEvent e) {
				int r = rowAtPoint(e.getPoint()), c = columnAtPoint(e.getPoint());
				if (r < 0 || c < 0)
					return null;
				Object v = getValueAt(r, c);
				if (v == null)
					return null;
				TableCellRenderer rdz = getCellRenderer(r, c);
				Component comp = prepareRenderer(rdz, r, c);
				int pref = comp.getPreferredSize().width;
				int colW = getColumnModel().getColumn(c).getWidth();
				return (pref > colW - 4) ? v.toString() : null;
			}
		};
		JScrollPane spHD = new JScrollPane(tblHoaDon);

		// Bảng chi tiết (phải)
		modelCT = new DefaultTableModel(
				new String[] { "Mã lô", "Sản phẩm", "Số lượng", "Giá bán", "Khuyến mãi", "Thành tiền" }, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		tblChiTiet = new JTable(modelCT) {
			@Override
			public String getToolTipText(MouseEvent e) {
				int r = rowAtPoint(e.getPoint()), c = columnAtPoint(e.getPoint());
				if (r < 0 || c < 0)
					return null;
				Object v = getValueAt(r, c);
				if (v == null)
					return null;
				TableCellRenderer rdz = getCellRenderer(r, c);
				Component comp = prepareRenderer(rdz, r, c);
				int pref = comp.getPreferredSize().width;
				int colW = getColumnModel().getColumn(c).getWidth();
				return (pref > colW - 4) ? v.toString() : null;
			}
		};
		JScrollPane spCT = new JScrollPane(tblChiTiet);

		// Split ngang
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spHD, spCT);
		split.setResizeWeight(0.5);
		split.setContinuousLayout(true);
		split.setOneTouchExpandable(true);
		add(split, BorderLayout.CENTER);

		// Buttons
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnXacNhan = new JButton("Xác nhận");
		JButton btnDong = new JButton("Đóng");
		bottom.add(btnXacNhan);
		bottom.add(btnDong);
		add(bottom, BorderLayout.SOUTH);

		// Events
		tblHoaDon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int r = tblHoaDon.getSelectedRow();
				if (r >= 0) {
					selectedMaHD = (String) modelHD.getValueAt(r, 0);
					loadChiTiet(selectedMaHD);
				}
			}
		});

		btnXacNhan.addActionListener(e -> {
			if (selectedMaHD == null) {
				JOptionPane.showMessageDialog(HoaDonPickerDialog.this, "Vui lòng chọn 1 hoá đơn!");
				return;
			}
			dispose();
		});

		btnDong.addActionListener(e -> {
			selectedMaHD = null;
			dispose();
		});

		// Load danh sách hóa đơn theo SĐT
		loadHoaDonTheoSDT(soDT);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		// ⚡ Reset selectedMaHD khi người dùng bấm nút X (close window)
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				selectedMaHD = null;
			}
		});

	}

	private void loadHoaDonTheoSDT(String soDT) {
		modelHD.setRowCount(0);
		List<HoaDon> ds = hoaDonDAO.timHoaDonTheoSoDienThoai(soDT);
		for (HoaDon hd : ds) {
			modelHD.addRow(new Object[] { hd.getMaHoaDon(), hd.getNgayLap(),
					hd.getNhanVien() != null ? hd.getNhanVien().getTenNhanVien() : "",
					hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "", hd.getTongTien(),
					hd.getTongThanhToan() });
		}
		if (modelHD.getRowCount() > 0) {
			tblHoaDon.setRowSelectionInterval(0, 0);
			selectedMaHD = (String) modelHD.getValueAt(0, 0);
			loadChiTiet(selectedMaHD);
		} else {
			selectedMaHD = null;
			modelCT.setRowCount(0);
		}
	}

	private void loadChiTiet(String maHD) {
		modelCT.setRowCount(0);
		List<ChiTietHoaDon> ds = cthdDAO.layDanhSachChiTietTheoMaHD(maHD);
		for (ChiTietHoaDon ct : ds) {
			modelCT.addRow(new Object[] { ct.getLoSanPham() != null ? ct.getLoSanPham().getMaLo() : "",
					ct.getSanPham() != null ? ct.getSanPham().getTenSanPham()
							: (ct.getLoSanPham() != null && ct.getLoSanPham().getSanPham() != null
									? ct.getLoSanPham().getSanPham().getTenSanPham()
									: ""),
					(int) ct.getSoLuong(), ct.getGiaBan(),
					ct.getKhuyenMai() != null ? ct.getKhuyenMai().getTenKM() : "Không có", ct.getThanhTien() });
		}
	}

	/** Trả về mã hoá đơn đã chọn, hoặc null nếu người dùng đóng dialog */
	public String getSelectedMaHD() {
		return selectedMaHD;
	}
}
