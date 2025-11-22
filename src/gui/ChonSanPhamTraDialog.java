package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import entity.ChiTietHoaDon;

public class ChonSanPhamTraDialog extends JDialog {

	private JTable table;
	private DefaultTableModel model;

	private List<ChiTietHoaDon> dsCTHD; // toàn bộ chi tiết hóa đơn
	private List<ChiTietHoaDon> dsChon = new ArrayList<>(); // trả về

	// Nhóm sản phẩm theo TÊN -> danh sách CTHD (nhiều DVT)
	private Map<String, List<ChiTietHoaDon>> mapTheoSanPham = new HashMap<>();

	public ChonSanPhamTraDialog(List<ChiTietHoaDon> dsCTHD) {
		this.dsCTHD = dsCTHD;

		setTitle("Chọn sản phẩm muốn trả");
		setModal(true);
		setSize(450, 420);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		initUI();
		loadData();
	}

	private void initUI() {
		// ===== TABLE =====
		model = new DefaultTableModel(new Object[] { "Chọn", "Sản phẩm" }, 0) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0)
					return Boolean.class;
				return String.class;
			}

			@Override
			public boolean isCellEditable(int r, int c) {
				return c == 0; // chỉ cho phép tick checkbox
			}
		};

		table = new JTable(model);
		table.setRowHeight(26);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

		// ===== SET LẠI SIZE CỘT CHECKBOX =====
		table.getColumnModel().getColumn(0).setMaxWidth(60);
		table.getColumnModel().getColumn(0).setMinWidth(60);
		table.getColumnModel().getColumn(0).setPreferredWidth(60);

		JScrollPane scr = new JScrollPane(table);
		scr.setPreferredSize(new Dimension(430, 300));
		add(scr, BorderLayout.CENTER);

		// ===== BOTTOM BUTTONS =====
		JPanel pnBottom = new JPanel();
		JButton btnChonAll = new JButton("Chọn tất cả");
		JButton btnOK = new JButton("Xác nhận");
		JButton btnCancel = new JButton("Hủy");

		pnBottom.add(btnChonAll);
		pnBottom.add(btnOK);
		pnBottom.add(btnCancel);

		add(pnBottom, BorderLayout.SOUTH);

		// ===== EVENTS =====
		btnChonAll.addActionListener(e -> {
			boolean allChecked = true;

			// Kiểm tra xem tất cả đã được chọn chưa
			for (int i = 0; i < model.getRowCount(); i++) {
				if (!(boolean) model.getValueAt(i, 0)) {
					allChecked = false;
					break;
				}
			}

			if (!allChecked) {
				// CHỌN TẤT CẢ
				for (int i = 0; i < model.getRowCount(); i++) {
					model.setValueAt(true, i, 0);
				}
				btnChonAll.setText("Bỏ chọn tất cả");
			} else {
				// BỎ CHỌN TẤT CẢ
				for (int i = 0; i < model.getRowCount(); i++) {
					model.setValueAt(false, i, 0);
				}
				btnChonAll.setText("Chọn tất cả");
			}
		});

		btnCancel.addActionListener(e -> {
			dsChon.clear();
			dispose();
		});

		btnOK.addActionListener(e -> xuLyChonSanPham());

		// ===== AUTO UPDATE NÚT CHỌN TẤT CẢ =====
		table.getModel().addTableModelListener(e -> {
			boolean allChecked = true;

			for (int i = 0; i < model.getRowCount(); i++) {
				if (!(boolean) model.getValueAt(i, 0)) {
					allChecked = false;
					break;
				}
			}

			// Cập nhật lại text nút
			if (allChecked) {
				btnChonAll.setText("Bỏ chọn tất cả");
			} else {
				btnChonAll.setText("Chọn tất cả");
			}
		});

	}

	/**
	 * Gom dữ liệu theo TÊN sản phẩm và hiển thị 1 dòng / 1 tên SP
	 */
	private void loadData() {
		model.setRowCount(0);
		mapTheoSanPham.clear();

		// Gom nhóm: tên -> danh sách CTHD (nhiều DVT)
		for (ChiTietHoaDon ct : dsCTHD) {
			String ten = ct.getLoSanPham().getSanPham().getTenSanPham();
			mapTheoSanPham.computeIfAbsent(ten, k -> new ArrayList<>()).add(ct);
		}

		// Bảng chỉ hiển thị 1 dòng / tên sản phẩm
		for (String tenSP : mapTheoSanPham.keySet()) {
			model.addRow(new Object[] { false, tenSP });
		}
	}

	/**
	 * Xác nhận lựa chọn → trả về tất cả CTHD của SP được tick.
	 */
	private void xuLyChonSanPham() {
		dsChon.clear();

		for (int i = 0; i < model.getRowCount(); i++) {
			boolean isChecked = (boolean) model.getValueAt(i, 0);
			if (isChecked) {
				String tenSP = model.getValueAt(i, 1).toString();
				List<ChiTietHoaDon> ds = mapTheoSanPham.get(tenSP);
				dsChon.addAll(ds);
			}
		}

		if (dsChon.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Bạn chưa chọn sản phẩm nào.");
			return;
		}

		dispose();
	}

	public List<ChiTietHoaDon> getDsSanPhamDuocChon() {
		return dsChon;
	}
}
